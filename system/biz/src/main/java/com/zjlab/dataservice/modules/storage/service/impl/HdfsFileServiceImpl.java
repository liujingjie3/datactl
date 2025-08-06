package com.zjlab.dataservice.modules.storage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.common.util.storage.HdfsUtil;
import com.zjlab.dataservice.modules.storage.mapper.FileMapper;
import com.zjlab.dataservice.modules.storage.model.dto.FileListRequestDto;
import com.zjlab.dataservice.modules.storage.model.po.FilePo;
import com.zjlab.dataservice.modules.storage.model.vo.FileVo;
import com.zjlab.dataservice.modules.storage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("hdfsFileService")
public class HdfsFileServiceImpl extends ServiceImpl<FileMapper, FilePo> implements FileService {
    @Override
    public void upload(boolean delSrc, boolean overwrite, String srcFile, String dstPath) {
        HdfsUtil.uploadFileToHdfs(delSrc, overwrite, srcFile, dstPath);
    }

    @Override
    public void upload(MultipartFile file, String dstPath) throws Exception {
        String localMd5Hex = DigestUtils.md5Hex(file.getInputStream());
        String filename = file.getOriginalFilename();
        String hdfsMd5Hex = HdfsUtil.uploadFileToHdfs(file, dstPath + "/" + filename);
        if (!StringUtils.equals(localMd5Hex, hdfsMd5Hex)){
            log.error("md5 not equal. localMd5Hex:{}, hdfsMd5Hex:{}", localMd5Hex, hdfsMd5Hex);
            throw new JeecgBootException("文件校验码不匹配");
        }
    }

    @Override
    public void download(String srcFile, String dstFile, HttpServletResponse response) {
//        HdfsUtil.downloadFileFromHdfs(srcFile, dstFile);
        HdfsUtil.downloadStreamFromHdfs(srcFile, dstFile, response);
    }

    @Override
    public void mkdir(String path) {
        boolean mkdir = HdfsUtil.mkdir(path);
        if (!mkdir){
            throw new JeecgBootException("创建目录失败");
        }
    }

    @Override
    public void rename(String srcFile, String dstFile) {
        boolean rename = HdfsUtil.rename(srcFile, dstFile);
        if (!rename){
            throw new JeecgBootException("重命名失败");
        }
    }

    @Override
    public void delete(String path) {
        boolean delete = HdfsUtil.delete(path);
        if (!delete){
            throw new JeecgBootException("删除失败");
        }
    }

    @Override
    public PageResult<FileVo> listFiles(FileListRequestDto requestDto) {
        List<JSONObject> list = HdfsUtil.listFiles(requestDto.getPath(), null);
        List<FileVo> collect = list.stream().map(jsonObject -> {
            FileVo fileVo = new FileVo();
            fileVo.setName(jsonObject.getString("name"))
                    .setIsDir(jsonObject.getBoolean("isDir"));
            return fileVo;
        }).collect(Collectors.toList());

        PageResult<FileVo> result = new PageResult<>();
        return result;
    }

}
