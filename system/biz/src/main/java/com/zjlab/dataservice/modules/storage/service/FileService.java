package com.zjlab.dataservice.modules.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.storage.model.dto.FileListRequestDto;
import com.zjlab.dataservice.modules.storage.model.po.FilePo;
import com.zjlab.dataservice.modules.storage.model.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService extends IService<FilePo> {

    void upload(boolean delSrc, boolean overwrite, String srcFile, String dstPath);

    void upload(MultipartFile file, String path) throws Exception;

    void download(String srcFile, String dstFile, HttpServletResponse response);

    void mkdir(String path);

    void rename(String srcFile, String dstFile);

    PageResult<FileVo> listFiles(FileListRequestDto requestDto) throws JSchException, SftpException, NoSuchFieldException;

	void delete(String objectName) throws Exception;

}
