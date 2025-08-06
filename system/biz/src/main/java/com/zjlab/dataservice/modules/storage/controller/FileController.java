package com.zjlab.dataservice.modules.storage.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.storage.enums.FileServiceEnum;
import com.zjlab.dataservice.modules.storage.factory.FileFactory;
import com.zjlab.dataservice.modules.storage.model.dto.FileDownloadDto;
import com.zjlab.dataservice.modules.storage.model.dto.FileListRequestDto;
import com.zjlab.dataservice.modules.storage.model.dto.FileUploadDto;
import com.zjlab.dataservice.modules.storage.model.vo.FileVo;
import com.zjlab.dataservice.modules.storage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 文件存储，用于存放大文件原始影像
 * @Author: lishaohua
 * @Date:   2024-3-5
 * @Version: V1.0
 */

@Slf4j
@RestController
@RequestMapping("sys/file")
public class FileController {

//    /**
//     * 以url的方式实现本地上传文件到hdfs
//     * @return
//     */
//    @PostMapping("uploadLocal")
//    public Result upload(@RequestBody FileUploadDto uploadDto){
//        Result result = new Result();
//        hdfsFileService.upload(uploadDto.isDelSrc(), uploadDto.isOverWrite(), uploadDto.getSrcFile(), uploadDto.getDstPath());
//        result.success("上传成功！");
//        return result;
//    }

    /**
     * 以文件流的方式上传文件
     * @param file  待上传的文件
     * @param uploadDto 上传请求参数
     * @return
     * @throws IOException
     */
    @PostMapping("upload")
    public Result upload(@RequestParam("file") MultipartFile file, FileUploadDto uploadDto) throws Exception {
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(uploadDto.getType()));
        fileService.upload(file, uploadDto.getDstPath());
        return Result.OK("上传成功！");
    }

    /**
     * 普通下载--流方式
     * @return
     */
    @PostMapping("download")
    public void download(@RequestBody FileDownloadDto downloadDto, HttpServletResponse response){
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(downloadDto.getType()));
        fileService.download(downloadDto.getSrcFile(), downloadDto.getDstPath(), response);
    }

    /**
     * 创建目录
     * @param path
     * @return
     */
    @PostMapping("mkdir")
    public Result mkdir(@RequestParam("path") String path, @RequestParam("type") String type){
        Result result = new Result();
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(type));
        fileService.mkdir(path);
        result.success("创建目录成功");
        return result;
    }

    /**
     * 重命名 文件 或 目录
     * @param srcFile
     * @param dstFile
     * @return
     */
    @PostMapping("rename")
    public Result rename(@RequestParam("srcFile") String srcFile, @RequestParam("dstFile")String dstFile,
                                                                  @RequestParam("type") String type){
        Result result = new Result();
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(type));
        fileService.rename(srcFile, dstFile);
        result.success("重命名成功");
        return result;
    }

    /**
     * 删除 文件 或 目录
     * @return
     */
    @DeleteMapping("del")
    public Result delete(@RequestParam("path") String path, @RequestParam("type") String type) throws Exception {
        Result result = new Result();
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(type));
        fileService.delete(path);
        result.success("删除成功");
        return result;
    }

    /**
     * 查询文件目录列表
     * @param requestDto 查询参数，带分页
     * @return
     */
    @GetMapping("list")
    public Result<PageResult<FileVo>> listFiles(FileListRequestDto requestDto) throws Exception{
        FileService fileService = FileFactory.getFileService(FileServiceEnum.getEnumByType(requestDto.getType()));
        PageResult<FileVo> fileVoPageResult = fileService.listFiles(requestDto);
        return Result.OK(fileVoPageResult);
    }

}
