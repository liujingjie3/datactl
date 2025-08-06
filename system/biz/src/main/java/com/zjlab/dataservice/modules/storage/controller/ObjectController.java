package com.zjlab.dataservice.modules.storage.controller;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.storage.enums.ObjectServiceEnum;
import com.zjlab.dataservice.modules.storage.factory.ObjectFactory;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadZipDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectListRequestDto;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectUploadDto;
import com.zjlab.dataservice.modules.storage.model.vo.BucketVo;
import com.zjlab.dataservice.modules.storage.model.vo.ObjectVo;
import com.zjlab.dataservice.modules.storage.service.ObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: minio 对象存储，用于存放缩略图等图片
 * @Author: lishaohua
 * @Date:   2024-3-5
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("sys/object")
public class ObjectController {


    @PostMapping("upload")
    public Result upload(@RequestParam("files") MultipartFile[] files,
                         @RequestParam("objectUploadDto") String uploadDto) throws Exception {
        ObjectUploadDto objectUploadDto = JSONObject.parseObject(uploadDto, ObjectUploadDto.class);
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(objectUploadDto.getType()));
        objectService.upload(files, objectUploadDto.getBucketName(), objectUploadDto.getPath());
        return Result.OK("上传成功！");
    }

    /**
     * 普通下载--流方式
     * @return
     */
    @PostMapping("download")
    public void download(@RequestBody ObjectDownloadDto downloadDto, HttpServletResponse response) throws Exception {
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(downloadDto.getType()));
        objectService.download(downloadDto, response);
    }

    /**
     * 下载minio对象list，以压缩包格式返回
     * @param downloadZipDto
     * @param response
     * @throws Exception
     */
    @PostMapping("downloadZip")
    public void downloadZip(@RequestBody ObjectDownloadZipDto downloadZipDto, HttpServletResponse response) throws Exception {
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(downloadZipDto.getType()));
        objectService.downloadZip(downloadZipDto, response);
    }

    @PostMapping("downloadTxtZip")
    public void downloadTxtZip(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType("minio"));
        objectService.downloadZipFromTxt(file, response);
    }

    /**
     * 创建目录
     * @param path
     * @return
     */
    @PostMapping("mkdir")
    public Result mkdir(@RequestParam(value = "type", required = false, defaultValue = "minio") String type,
                        @RequestParam("bucketName") String bucketName, @RequestParam("path") String path){
        Result result = new Result();
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(type));
        objectService.mkdir(bucketName, path);
        result.success("创建目录成功");
        return result;
    }

    /**
     * 重命名 文件 或 目录
     * @param srcObject
     * @param dstObject
     * @return
     */
    @PostMapping("rename")
    public Result rename(@RequestParam("srcObject") String srcObject, @RequestParam("dstObject")String dstObject,
                         @RequestParam(value = "type",required = false,defaultValue = "minio") String type){
        Result result = new Result();
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(type));
        objectService.rename(srcObject, dstObject);
        result.success("重命名成功");
        return result;
    }

    /**
     * 删除 文件 或 目录
     * @return
     */
    @DeleteMapping("del")
    public Result delete(@RequestParam(value = "type", required = false, defaultValue = "minio") String type,
                         @RequestParam("bucketName") String bucketName, @RequestParam("path") String path) throws Exception {
        Result result = new Result();
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(type));
        objectService.delete(bucketName, path);
        result.success("删除成功");
        return result;
    }

    /**
     * 获取桶列表
     * @return
     * @throws Exception
     */
    @GetMapping("bucket/list")
    public Result<List<BucketVo>> getBucketList() throws Exception {
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType("minio"));
        List<BucketVo> bucketList = objectService.getListBuckets();
        return Result.ok(bucketList);
    }

    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    @GetMapping("preSignedUrl")
    public Result<String> getPreSignedObjectUrl(@RequestParam("bucketName") String bucketName,
                                                @RequestParam("objectName") String objectName,
                                                @RequestParam(value = "expires", required = false) Integer expires) throws Exception {
        Result<String> result = new Result<>();
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType("minio"));
        String preSignedUrl = objectService.getPreSignedObjectUrl(bucketName, objectName, expires);
        result.success("生成预签名url成功");
        result.setResult(preSignedUrl);
        return result;
    }

    @GetMapping("list")
    public Result<PageResult<ObjectVo>> getListObject(ObjectListRequestDto requestDto) throws Exception {
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType(requestDto.getType()));
        PageResult<ObjectVo> listObjects = objectService.getListObject(requestDto);
        return Result.OK(listObjects);
    }

    @GetMapping("info")
    public Result<ObjectVo> getObjectInfo(@RequestParam("bucketName") String bucketName,
                                          @RequestParam("objectName") String objectName) throws Exception{
        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType("minio"));
        ObjectVo objectInfo = objectService.getObjectInfo(bucketName, objectName);
        return Result.OK(objectInfo);
    }

//    @DeleteMapping("del")
//    public Result deleteObj(@RequestParam(name = "objectName") String objectName) throws Exception {
//        ObjectService objectService = ObjectFactory.getObjectService(ObjectServiceEnum.getEnumByType("minio"));
//        objectService.delete(objectName);
//        return Result.OK();
//    }
}
