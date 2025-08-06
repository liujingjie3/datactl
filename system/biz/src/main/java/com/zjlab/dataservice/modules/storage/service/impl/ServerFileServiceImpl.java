package com.zjlab.dataservice.modules.storage.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.common.util.PageUtil;
import com.zjlab.dataservice.common.util.storage.ServerUtil;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("serverFileService")
public class ServerFileServiceImpl extends ServiceImpl<FileMapper, FilePo> implements FileService {
    @Override
    public void upload(boolean delSrc, boolean overwrite, String srcFile, String dstPath) {

    }

    @Override
    public void upload(MultipartFile file, String path) throws Exception {
        String localMd5Hex = DigestUtils.md5Hex(file.getInputStream());
        String remoteFile = path + "/" + file.getOriginalFilename();
        ServerUtil.upload(file, remoteFile);
        String cmd = "md5sum " + remoteFile;
        String result = ServerUtil.execCmd(cmd);
        //example: 87d2940af9d523a2757f8d49d4225148  start-3.5.1.jar
        String remoteMd5Hex = result.split(" ")[0];
        if (!StringUtils.equals(localMd5Hex, remoteMd5Hex)){
            log.error("md5 not equal. localMd5Hex:{}, hdfsMd5Hex:{}", localMd5Hex, remoteMd5Hex);
            throw new JeecgBootException("文件校验码不匹配");
        }
    }

    @Override
    public void download(String srcFile, String dstFile, HttpServletResponse response) {

    }

    @Override
    public void mkdir(String path) {
        String cmd = "mkdir -p " + path;
        try {
            ServerUtil.execCmd(cmd);
        }catch (Exception e){
            throw new JeecgBootException("创建目录失败");
        }
    }

    @Override
    public void rename(String srcFile, String dstFile) {

    }

    @Override
    public PageResult<FileVo> listFiles(FileListRequestDto requestDto) throws JSchException, SftpException, NoSuchFieldException {
        int pageIndex = Optional.ofNullable(requestDto.getPageNo()).orElse(1);
        int pageSize = Optional.ofNullable(requestDto.getPageSize()).orElse(10);
        String orderByField = Optional.ofNullable(requestDto.getOrderByField()).orElse("lastModifiedTime");
        String orderByType = Optional.ofNullable(requestDto.getOrderByType()).orElse("desc");

        List<JSONObject> list = ServerUtil.getDirectory(requestDto.getPath());
        List<FileVo> collect = list.stream().map(jsonObject -> {
            FileVo fileVo = new FileVo();
            fileVo.setName(jsonObject.getString("name"))
                    .setIsDir(jsonObject.getBoolean("isDir"))
                    .setSize(jsonObject.getLong("size"))
                    .setPath(requestDto.getPath())
                    .setLastModifiedTime(jsonObject.getLong("mtime"))
                    .setPermissions(jsonObject.getString("permissions"));
            return fileVo;
        }).collect(Collectors.toList());

        List<FileVo> orderedCollect = orderForList(collect, orderByType, orderByField);

        PageResult<FileVo> result = new PageResult<>();
        result.setPageNo(pageIndex);
        result.setPageSize(pageSize);
        result.setTotal(orderedCollect.size());
        result.setData(PageUtil.getPageData(orderedCollect, pageIndex, pageSize));
        return result;
    }

    /**
     * list对对象中的字段进行排序
     * @param list 待排序的列表
     * @param orderType 排序{0,无序，1,ASC升序;2,DESC降序}
     * @param orderByName 实体排序的字段
     * */
    public static  <T> List<T> orderForList(List<T> list, String orderType, String orderByName) {
        if(CollUtil.isEmpty(list) || list.size() <= 1){
            return list;
        }
        //首字母转大写
        String newStr = orderByName.substring(0, 1).toUpperCase() + orderByName.replaceFirst("\\w", "");
        String methodStr = "get" + newStr;
        Field declaredField;
        boolean stringType = true ;
        // 排序类型的获取
        try {
            declaredField = list.get(0).getClass().getDeclaredField(orderByName);
            Class<?> type = declaredField.getType();
            if(type.equals(Integer.class) || type.equals(Float.class) || type.equals(Double.class) ||
                    type.equals(BigDecimal.class) || type.equals(Long.class)){
                stringType = false;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        final boolean flag = stringType ;

        Collections.sort(list, (obj1, obj2) -> {
            int retVal = 0;
            try {
                Method method1 = ((T) obj1).getClass().getMethod(methodStr, null);
                Method method2 = ((T) obj2).getClass().getMethod(methodStr, null);
                // 倒序
                if (Objects.equals(orderType, "desc")) {
                    // 是否按字符串比较
                    if(flag) {
                        // 字符串按 中英文 排序方式
                        Collator chinaSort = Collator.getInstance(Locale.CHINA);
                        retVal = chinaSort.compare(method2.invoke(obj2, null).toString(),method1.invoke(obj1, null).toString());
                    }else {
                        retVal = Double.valueOf(method2.invoke(obj2, null).toString()).compareTo(Double.valueOf(method1.invoke(obj1, null).toString()));
                    }
                }
                // 正序
                if (Objects.equals(orderType, "asc")) {
                    if(flag) {
                        // 字符串按 中英文 排序方式
                        Collator chinaSort = Collator.getInstance(Locale.CHINA);
                        retVal = chinaSort.compare(method1.invoke(obj1, null).toString(),method2.invoke(obj2, null).toString());
                    }else {
                        retVal = Double.valueOf(method1.invoke(obj1, null).toString()).compareTo(Double.valueOf(method2.invoke(obj2, null).toString()));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return retVal;
        });
        return list;
    }

    @Override
    public void delete(String objectName) throws Exception {

    }
}
