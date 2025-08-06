package com.zjlab.dataservice.modules.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadZipDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectListRequestDto;
import com.zjlab.dataservice.modules.storage.model.po.FilePo;
import com.zjlab.dataservice.modules.storage.model.vo.BucketVo;
import com.zjlab.dataservice.modules.storage.model.vo.ObjectVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ObjectService extends IService<FilePo> {

    void upload(MultipartFile[] files, String bucketName, String folder) throws Exception;

    void download(ObjectDownloadDto downloadDto, HttpServletResponse response) throws Exception;
    void downloadZip(ObjectDownloadZipDto downloadZipDto, HttpServletResponse response) throws Exception;

    void downloadZipFromTxt(MultipartFile file, HttpServletResponse response) throws Exception;

    void mkdir(String bucketName, String path);

    void rename(String srcFile, String dstFile);

    List<BucketVo> getListBuckets() throws Exception;

    String getPreSignedObjectUrl(String bucketName, String objectName, Integer expires) throws Exception;

    PageResult<ObjectVo> getListObject(ObjectListRequestDto requestDto) throws Exception;

    ObjectVo getObjectInfo(String bucketName, String objectName) throws Exception;

    void delete(String bucketName, String objectName) throws Exception;
}
