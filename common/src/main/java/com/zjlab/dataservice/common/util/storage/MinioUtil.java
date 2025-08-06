package com.zjlab.dataservice.common.util.storage;

import com.zjlab.dataservice.common.constant.SymbolConstant;
import com.zjlab.dataservice.common.util.CommonUtils;
import com.zjlab.dataservice.common.util.filter.FileTypeFilter;
import com.zjlab.dataservice.common.util.filter.StrAttackFilter;
import com.zjlab.dataservice.common.util.oConvertUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * minio文件上传工具类
 * @author: jeecg-boot
 */
@Slf4j
@Data
public class MinioUtil {
    private static String endpoint;
    private static String accessKey;
    private static String secretKey;
    private static String bucketName;

    public static void setEndpoint(String endpoint) {
        MinioUtil.endpoint = endpoint;
    }

    public static void setAccessKey(String accessKey) {
        MinioUtil.accessKey = accessKey;
    }

    public static void setSecretKey(String secretKey) {
        MinioUtil.secretKey = secretKey;
    }

    public static void setBucketName(String bucketName) {
        MinioUtil.bucketName = bucketName;
    }

    public static String getMinioUrl() {
        return endpoint;
    }

    private static MinioClient minioClient = null;

    /**
     * 初始化客户端
     * @param endpoint
     * @param accessKey
     * @param secretKey
     */
    public static void initMinio(String endpoint, String accessKey, String secretKey) {
        if (minioClient == null) {
            try {
                minioClient = MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
            } catch (Exception e) {
                log.error("create minioClient failed");
                e.printStackTrace();
            }
        }
    }

    /*************************************  Bucket Operation  *************************************/

    /**
     *  判断Bucket是否存在，true：存在，false：不存在
     * @return
     * @throws Exception
     */
    public static boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获得Bucket的策略
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static String getBucketPolicy(String bucketName) throws Exception {
        String bucketPolicy = minioClient
                .getBucketPolicy(
                        GetBucketPolicyArgs
                                .builder()
                                .bucket(bucketName)
                                .build()
                );
        return bucketPolicy;
    }


    /**
     * 获得所有Bucket列表
     * @return
     * @throws Exception
     */
    public static List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取其相关信息
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static Optional<Bucket> getBucket(String bucketName) throws Exception {
        return getAllBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除Bucket，true：删除成功； false：删除失败，文件或已不存在
     * @param bucketName
     * @throws Exception
     */
    public static void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }


    /*************************************  File Operation  *************************************/

    /**
     * 判断文件是否存在
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @return
     */
    public static boolean isObjectExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 判断文件夹是否存在
     * @param bucketName 存储桶
     * @param objectName 文件夹名称
     * @return
     */
    public static boolean isFolderExist(String bucketName, String objectName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 根据文件前缀查询文件
     * @param bucketName 存储桶
     * @param prefix 前缀
     * @param recursive 是否使用递归查询
     * @return MinioItem 列表
     * @throws Exception
     */
    public static List<Item> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws Exception {
        List<Item> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
        if (objectsIterator != null) {
            for (Result<Item> o : objectsIterator) {
                Item item = o.get();
                list.add(item);
            }
        }

        return list;
    }

    /**
     * 获取文件流
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @return 二进制流
     */
    public static InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 断点下载
     * @param bucketName 存储桶
     * @param objectName 文件名称
     * @param offset 起始字节的位置
     * @param length 要读取的长度
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName, long offset, long length)throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build());
    }

    /**
     * 获取路径下文件列表
     * @param bucketName 存储桶
     * @param prefix 文件名称
     * @param recursive 是否递归查找，false：模拟文件夹结构查找
     * @return 二进制流
     */
    public static Iterable<Result<Item>> listObjects(String bucketName, String prefix,
                                                     boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build());
    }

    /**
     * 使用MultipartFile进行文件上传
     * @param bucketName 存储桶
     * @param file 文件名
     * @param objectName 对象名
     * @param contentType 类型
     * @return
     * @throws Exception
     */
    public static ObjectWriteResponse uploadFile(String bucketName, MultipartFile file,
                                                 String objectName, String contentType) throws Exception {
        InputStream inputStream = file.getInputStream();
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType(contentType)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * 上传本地文件
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @param fileName 本地文件路径
     */
    public static ObjectWriteResponse uploadFile(String bucketName, String objectName,
                                                 String fileName) throws Exception {
        return minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
    }

    /**
     * 通过流上传文件
     *
     * @param bucketName 存储桶
     * @param objectName 文件对象
     * @param inputStream 文件流
     */
    public static ObjectWriteResponse uploadFile(String bucketName, String objectName, InputStream inputStream) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType("application/octet-stream")
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * 创建文件夹或目录
     * @param bucketName 存储桶
     * @param objectName 目录路径
     */
    public static ObjectWriteResponse createDir(String bucketName, String objectName) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    /**
     * 获取文件信息, 如果抛出异常则说明文件不存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件名称
     */
    public static StatObjectResponse getFileStatusInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 拷贝文件
     *
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @param srcBucketName 目标存储桶
     * @param srcObjectName 目标文件名
     */
    public static ObjectWriteResponse copyFile(String bucketName, String objectName,
                                               String srcBucketName, String srcObjectName) throws Exception {
        return minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                        .bucket(srcBucketName)
                        .object(srcObjectName)
                        .build());
    }

    /**
     * 删除文件
     * @param bucketName 存储桶
     * @param objectName 文件名称
     */
    public static void removeFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 批量删除文件
     * @param bucketName 存储桶
     * @param keys 需要删除的文件列表
     * @return
     */
    public static void removeFiles(String bucketName, List<String> keys) {
        List<DeleteObject> objects = new LinkedList<>();
        keys.forEach(s -> {
            objects.add(new DeleteObject(s));
            try {
                removeFile(bucketName, s);
            } catch (Exception e) {
                log.error("批量删除失败！error:{}",e);
            }
        });
    }

    /**
     * 获取文件外链
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @param expires 过期时间 <=7 秒 （外链有效时间（单位：秒））
     * @return url
     * @throws Exception
     */
    public static String getPresignedObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .expiry(expires)    //单位 秒
                .method(Method.GET).build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 获得文件外链
     * @param bucketName
     * @param objectName
     * @return url
     * @throws Exception
     */
    public static String getPresignedObjectUrl(String bucketName, String objectName) throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET).build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 将URLDecoder编码转成UTF8
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getUtf8ByURLDecoder(String str) throws UnsupportedEncodingException {
        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(url, "UTF-8");
    }


    /*******************************************************************************************************/
    /**
     * 上传文件
     * @param file
     * @return
     */
    public static String upload(MultipartFile file, String bizPath, String customBucket) throws Exception {
        String fileUrl = "";
        //update-begin-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
        bizPath = StrAttackFilter.filter(bizPath);
        //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击

        //update-begin-author:liusq date:20210809 for: 过滤上传文件类型
        FileTypeFilter.fileTypeFilter(file);
        //update-end-author:liusq date:20210809 for: 过滤上传文件类型

        String newBucket = bucketName;
        if(oConvertUtils.isNotEmpty(customBucket)){
            newBucket = customBucket;
        }
        try {
            initMinio(endpoint, accessKey,secretKey);
            // 检查存储桶是否已经存在
            if(minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                log.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());
                log.info("create a new bucket.");
            }
            InputStream stream = file.getInputStream();
            // 获取文件名
            String orgName = file.getOriginalFilename();
            if("".equals(orgName)){
                orgName=file.getName();
            }
            orgName = CommonUtils.getFileName(orgName);
            String objectName = bizPath+"/"
                                +( orgName.indexOf(".")==-1
                                   ?orgName + "_" + System.currentTimeMillis()
                                   :orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."))
                                 );

            // 使用putObject上传一个本地文件到存储桶中。
            if(objectName.startsWith(SymbolConstant.SINGLE_SLASH)){
                objectName = objectName.substring(1);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
                    .bucket(newBucket)
                    .contentType("application/octet-stream")
                    .stream(stream,stream.available(),-1).build();
            minioClient.putObject(objectArgs);
            stream.close();
            fileUrl = endpoint+newBucket+"/"+objectName;
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return fileUrl;
    }

    /**
     * 文件上传
     * @param file
     * @param bizPath
     * @return
     */
    public static String upload(MultipartFile file, String bizPath) throws Exception {
        return upload(file,bizPath,null);
    }

    /**
     * 获取文件流
     * @param bucketName
     * @param objectName
     * @return
     */
    public static InputStream getMinioFile(String bucketName,String objectName){
        InputStream inputStream = null;
        try {
            initMinio(endpoint, accessKey,secretKey);
            GetObjectArgs objectArgs = GetObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            inputStream = minioClient.getObject(objectArgs);
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    /**
     * 删除文件
     * @param bucketName
     * @param objectName
     * @throws Exception
     */
    public static void removeObject(String bucketName, String objectName) {
        try {
            initMinio(endpoint, accessKey,secretKey);
            RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            minioClient.removeObject(objectArgs);
        }catch (Exception e){
            log.info("文件删除失败" + e.getMessage());
        }
    }

    /**
     * 获取文件外链
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String getObjectUrl(String bucketName, String objectName, Integer expires) {
        initMinio(endpoint, accessKey,secretKey);
        try{
            //update-begin---author:liusq  Date:20220121  for：获取文件外链报错提示method不能为空，导致文件下载和预览失败----
            GetPresignedObjectUrlArgs objectArgs = GetPresignedObjectUrlArgs.builder().object(objectName)
                    .bucket(bucketName)
                    .expiry(expires).method(Method.GET).build();
            //update-begin---author:liusq  Date:20220121  for：获取文件外链报错提示method不能为空，导致文件下载和预览失败----
            String url = minioClient.getPresignedObjectUrl(objectArgs);
            return URLDecoder.decode(url,"UTF-8");
        }catch (Exception e){
            log.info("文件路径获取失败" + e.getMessage());
        }
        return null;
    }


    /**
     * 上传文件到minio
     * @param stream
     * @param relativePath
     * @return
     */
    public static String upload(InputStream stream,String relativePath) throws Exception {
        initMinio(endpoint, accessKey,secretKey);
        if(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Bucket already exists.");
        } else {
            // 创建一个名为ota的存储桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        }
        PutObjectArgs objectArgs = PutObjectArgs.builder().object(relativePath)
                .bucket(bucketName)
                .contentType("application/octet-stream")
                .stream(stream,stream.available(),-1).build();
        minioClient.putObject(objectArgs);
        stream.close();
        return endpoint+bucketName+"/"+relativePath;
    }



}
