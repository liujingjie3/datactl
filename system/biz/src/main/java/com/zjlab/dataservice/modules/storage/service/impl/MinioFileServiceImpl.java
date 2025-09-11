package com.zjlab.dataservice.modules.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.common.util.PageUtil;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import com.zjlab.dataservice.modules.storage.mapper.FileMapper;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadZipDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectListRequestDto;
import com.zjlab.dataservice.modules.storage.model.po.FilePo;
import com.zjlab.dataservice.modules.storage.model.vo.BucketVo;
import com.zjlab.dataservice.modules.storage.model.vo.ObjectVo;
import com.zjlab.dataservice.modules.storage.service.ObjectService;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: OSS云存储实现类
 * @author: jeecg-boot
 */
@Slf4j
@Service("minioFileService")
public class MinioFileServiceImpl extends ServiceImpl<FileMapper, FilePo> implements ObjectService {

	@Override
	public List<BucketVo> getListBuckets() throws Exception {
		List<Bucket> buckets = MinioUtil.getAllBuckets();
		return buckets.stream().map(
				bucket -> {
					BucketVo bucketVo = new BucketVo();
					bucketVo.setName(bucket.name());
					bucketVo.setCreateTime(Date.from(bucket.creationDate().toInstant()));
					return bucketVo;
				}
		).collect(Collectors.toList());
	}

	@Override
	public String getPreSignedObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
		if (expires == null){
			return MinioUtil.getPresignedObjectUrl(bucketName, objectName);
		}else {
			return MinioUtil.getPresignedObjectUrl(bucketName, objectName, expires);
		}
	}

	@Override
	public PageResult<ObjectVo> getListObject(ObjectListRequestDto requestDto) throws Exception {
		int pageIndex = Optional.ofNullable(requestDto.getPageNo()).orElse(1);
		int pageSize = Optional.ofNullable(requestDto.getPageSize()).orElse(10);
		String orderByField = Optional.ofNullable(requestDto.getOrderByField()).orElse("lastModifiedTime");
		String orderByType = Optional.ofNullable(requestDto.getOrderByType()).orElse("desc");
		//兼容输入端开头带斜杠的情况
		String prefix = requestDto.getPrefix().replaceAll("^/+", "");
		List<Item> objects = MinioUtil.getAllObjectsByPrefix(requestDto.getBucketName(), prefix, requestDto.getRecursive());
		List<ObjectVo> collect = objects.stream().map(
				object -> {
					ObjectVo objectVo = new ObjectVo();
					objectVo.setName(object.objectName());
					objectVo.setLastModifiedTime(Date.from(object.lastModified().toInstant()));
					objectVo.setSize(object.size());
					objectVo.setIsDir(object.isDir());
					objectVo.setMd5(object.etag().replace("\"", ""));
					return objectVo;
				}
		).collect(Collectors.toList());

		List<ObjectVo> sortedCollect;
		if (StringUtils.equals(orderByType, "desc")){
			sortedCollect = collect.stream().sorted(Comparator.comparing(ObjectVo::getLastModifiedTime).reversed())
					.collect(Collectors.toList());
		}else {
			sortedCollect = collect.stream().sorted(Comparator.comparing(ObjectVo::getLastModifiedTime))
					.collect(Collectors.toList());
		}

		PageResult<ObjectVo> result = new PageResult<>();
		result.setPageNo(pageIndex);
		result.setPageSize(pageSize);
		result.setTotal(sortedCollect.size());
		result.setData(PageUtil.getPageData(sortedCollect, pageIndex, pageSize));
		return result;
	}

	@Override
	public ObjectVo getObjectInfo(String bucketName, String objectName) throws Exception{
		//兼容输入端开头带斜杠的情况
		objectName = objectName.replaceAll("^/+", "");
		StatObjectResponse objectResponse = MinioUtil.getFileStatusInfo(bucketName, objectName);
		return new ObjectVo().setName(objectResponse.object())
				.setSize(objectResponse.size())
				.setIsDir(false)
				.setLastModifiedTime(Date.from(objectResponse.lastModified().toInstant()))
				.setMd5(objectResponse.etag());
	}

	@Override
	public void upload(MultipartFile[] files, String bucketName, String folder) throws Exception {
		for (MultipartFile file : files){
			//本地文件信息
			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			String md5 = DigestUtils.md5Hex(file.getInputStream());
			//上传文件
			String objectName = folder + "/" + fileName;
			MinioUtil.uploadFile(bucketName, file, objectName, contentType);
			ObjectVo objectInfo = getObjectInfo(bucketName, objectName);
			//文件完整性校验
			if (!StringUtils.equals(md5, objectInfo.getMd5())){
				throw new JeecgBootException("上传文件的md5值不同");
			}
		}
	}

	@Override
        public String uploadReturnObjectName(MultipartFile file, String bucketName, String folder) throws Exception {
		// 原文件名
		String originalName = file.getOriginalFilename();
		String contentType = file.getContentType();
		String md5 = DigestUtils.md5Hex(file.getInputStream());

		// 生成新文件名（保留原扩展名）
		String ext = "";
		if (originalName != null && originalName.contains(".")) {
			ext = originalName.substring(originalName.lastIndexOf("."));
		}
		String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

		// 构造对象路径
		String objectName = folder + "/" + newFileName;

		// 上传文件
		MinioUtil.uploadFile(bucketName, file, objectName, contentType);

		// 获取上传后的文件信息
		ObjectVo objectInfo = getObjectInfo(bucketName, objectName);

		// 文件完整性校验
		if (!StringUtils.equals(md5, objectInfo.getMd5())) {
			throw new JeecgBootException("上传文件的md5值不同");
		}

		// 返回新文件名
                return objectName;
        }

        @Override
        public String uploadReturnObjectNameWithOriginal(MultipartFile file, String bucketName, String folder) throws Exception {
                // 原文件名
                String originalName = file.getOriginalFilename();
                String contentType = file.getContentType();
                String md5 = DigestUtils.md5Hex(file.getInputStream());

                String newFileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalName;

                String objectName = folder + "/" + newFileName;

                MinioUtil.uploadFile(bucketName, file, objectName, contentType);

                ObjectVo objectInfo = getObjectInfo(bucketName, objectName);

                if (!StringUtils.equals(md5, objectInfo.getMd5())) {
                        throw new JeecgBootException("上传文件的md5值不同");
                }

                return objectName;
        }


	@Override
	public void download(ObjectDownloadDto downloadDto, HttpServletResponse response) throws Exception {
		String path = downloadDto.getPath();
		String fileName = downloadDto.getFileName();
		if (fileName == null){
			fileName = path.substring(path.lastIndexOf('/') + 1);
		}
		InputStream objectStream = MinioUtil.getObject(downloadDto.getBucketName(), path);
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		ServletOutputStream out = response.getOutputStream();
		IOUtils.copy(objectStream, out);
	}

	@Override
	public void downloadZip(ObjectDownloadZipDto downloadZipDto, HttpServletResponse response) throws Exception {
		String fileName = Optional.ofNullable(downloadZipDto.getFileName()).orElse("download.zip");
		List<String> pathList = downloadZipDto.getPathList();
		String bucketName = downloadZipDto.getBucketName();
		//转换为zip压缩包
		convertZip(bucketName, pathList, fileName, response);
	}

	private void convertZip(String bucketName, List<String> pathList, String fileName, HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		//获取对象文件流
		ServletOutputStream outputStream = response.getOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		for (String path : pathList){
			InputStream objectStream = MinioUtil.getObject(bucketName, path);
			fileName = path.substring(path.lastIndexOf('/') + 1);
			//创建ZIP条目，使用原始文件名
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);

			// 读取文件流并写入ZIP输出流
			byte[] buffer = new byte[4096];
			int length;
			while ((length = objectStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, length);
			}
			// 完成当前ZIP条目的写入
			zipOutputStream.closeEntry();
		}
		zipOutputStream.close();
	}
	@Override
	public void downloadZipFromTxt(MultipartFile file, HttpServletResponse response) throws Exception {
		//从txt获取path列表
		List<String> pathList = convertFileContentToListStr(file);
		//文件名为同名txt的zip文件名
		String originalFilename = file.getOriginalFilename();
		String fileName = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + ".zip";
		//转换为zip压缩包
		convertZip("dspp", pathList, fileName, response);
	}

	private List<String> convertFileContentToListStr(MultipartFile file) throws IOException {
		InputStream inputStream = file.getInputStream();
		List<String> pathList = new ArrayList<>();
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		line = reader.readLine();	//读取第一行
		while (line != null){
			pathList.add(line);
			line = reader.readLine();	//读取下一行
		}
		reader.close();
		inputStream.close();
		return pathList;
	}

	@Override
	public void mkdir(String bucketName, String path) {
		try {
			MinioUtil.createDir(bucketName, path);
		}catch (Exception e){
			throw new JeecgBootException("创建目录失败");
		}
	}

	@Override
	public void rename(String srcFile, String dstFile) {

	}

	@Override
	public void delete(String bucketName, String objectName) {
		try {
			MinioUtil.removeFile(bucketName, objectName);
		}catch (Exception e){
			throw new JeecgBootException("删除对象失败", e);
		}
	}


}
