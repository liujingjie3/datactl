package com.zjlab.dataservice.common.util.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HdfsUtil {

    private static String defaultHdfsUrl;
    private static Configuration conf;

    public static void setConf(Configuration conf){
        HdfsUtil.conf = conf;
    }
    public static void setDefaultHdfsUrl(String defaultHdfsUrl){
        HdfsUtil.defaultHdfsUrl = defaultHdfsUrl;
    }

    /**
     * 获取HDFS文件系统
     * @return org.apache.hadoop.fs.FileSystem
     */
    private static FileSystem getFileSystem() throws IOException {
        return FileSystem.get(conf);
    }

    /**
     * 创建HDFS目录
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir
     * @return boolean 是否创建成功
     */
    public static boolean mkdir(String path){
        //如果目录已经存在，则直接返回
        if(checkExists(path)){
            return true;
        }else{
            FileSystem fileSystem = null;

            try {
                fileSystem = getFileSystem();

                //最终的HDFS文件目录
                String hdfsPath = generateHdfsPath(path);
                //创建目录
                return fileSystem.mkdirs(new Path(hdfsPath));
            } catch (IOException e) {
                log.error(MessageFormat.format("创建HDFS目录失败，path:{0}",path),e);
                return false;
            }finally {
                close(fileSystem);
            }
        }
    }

    /**
     * 上传文件至HDFS
     * @author adminstrator
     * @since 1.0.0
     * @param delSrc 是否删除本地文件
     * @param overwrite 是否覆盖HDFS上面的文件
     * @param srcFile 本地文件路径，比如：D:/test.txt
     * @param dstPath HDFS的相对目录路径，比如：/testDir
     */
    public static void uploadFileToHdfs(boolean delSrc, boolean overwrite, String srcFile, String dstPath){
        //源文件路径
        Path localSrcPath = new Path(srcFile);
        //目标文件路径
        Path hdfsDstPath = new Path(generateHdfsPath(dstPath));

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(delSrc,overwrite,localSrcPath,hdfsDstPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("上传文件至HDFS失败，srcFile:{0},dstPath:{1}",srcFile,dstPath),e);
            throw new JeecgBootException("上传文件至HDFS失败");
        }finally {
            close(fileSystem);
        }
    }

    /**
     * 通过文件流的方式上传文件到HDFS
     * @param file 文件输入流
     * @param dstPath HDFS文件相对路径
     * @return  hdfs文件的16进制MD5校验码
     * @throws IOException
     */
    public static String uploadFileToHdfs(MultipartFile file, String dstPath) throws IOException {
        long start = System.currentTimeMillis();
        Path hdfsDstPath = new Path(generateHdfsPath(dstPath));
        InputStream in = file.getInputStream();
        FileSystem fileSystem = getFileSystem();

        FSDataOutputStream out;
        try {
            if (!fileSystem.exists(hdfsDstPath)){
                log.info("hdfsPath not existed. create path : {}", hdfsDstPath);
                out = fileSystem.create(hdfsDstPath);
            }else {
                log.info("hdfsPath existed. path : {}", hdfsDstPath);
                out = fileSystem.append(hdfsDstPath);
                long offset = fileSystem.getFileStatus(hdfsDstPath).getLen();
                log.info("hdfs file offset : {}", offset);
                in.skip(offset);
            }
            IOUtils.copy(in, out);
        }catch (IOException e){
            log.error("上传文件至HDFS失败,dstPath:{}", dstPath, e);
            throw new JeecgBootException("上传文件至HDFS失败");
        }finally {
            close(fileSystem);
        }
        FSDataInputStream open = fileSystem.open(hdfsDstPath);
        String md5Hex = DigestUtils.md5Hex(open);
        long end = System.currentTimeMillis();
        log.info("time cost : {}", end - start);
        return md5Hex;
    }

    public static String getFileCheck(String dstPath) throws IOException {
        Path hdfsDstPath = new Path(generateHdfsPath(dstPath));
        FileSystem fileSystem = getFileSystem();
        try {
            FileChecksum fileChecksum = fileSystem.getFileChecksum(hdfsDstPath);
            FSDataInputStream open = fileSystem.open(hdfsDstPath);
            String md5Hex = DigestUtils.md5Hex(open);
            log.info("hdfs md5Hex : {}", md5Hex);
            log.info("fileCheckSum : {}", fileChecksum.toString());
            return fileChecksum.toString();
//            return fileChecksum.getBytes();
        }catch (IOException e){
            log.error("查询HDFS文件校验码失败,dstPath:{}", dstPath, e);
            throw new JeecgBootException("查询HDFS文件校验码失败");
        }finally {
            close(fileSystem);
        }
    }

    /**
     * 判断文件或者目录是否在HDFS上面存在
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir、/testDir/a.txt
     * @return boolean
     */
    public static boolean checkExists(String path){
        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();

            //最终的HDFS文件目录
            String hdfsPath = generateHdfsPath(path);

            //创建目录
            return fileSystem.exists(new Path(hdfsPath));
        } catch (IOException e) {
            log.error(MessageFormat.format("'判断文件或者目录是否在HDFS上面存在'失败，path:{0}",path),e);
            return false;
        }finally {
            close(fileSystem);
        }
    }

    /**
     * 获取HDFS上面的某个路径下面的所有文件或目录（不包含子目录）信息
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<JSONObject> listFiles(String path, PathFilter pathFilter){
        //返回数据
        List<JSONObject> result = new ArrayList<>();

        //如果目录已经存在，则继续操作
        if(checkExists(path)){
            FileSystem fileSystem = null;

            try {
                fileSystem = getFileSystem();

                //最终的HDFS文件目录
                String hdfsPath = generateHdfsPath(path);

                FileStatus[] statuses;
                //根据Path过滤器查询
                if(pathFilter != null){
                    statuses = fileSystem.listStatus(new Path(hdfsPath),pathFilter);
                }else{
                    statuses = fileSystem.listStatus(new Path(hdfsPath));
                }

                if(statuses != null){
                    for(FileStatus status : statuses){
                        //每个文件的属性
//                        Map<String,Object> fileMap = new HashMap<>(2);
                        JSONObject json = new JSONObject();
                        json.put("path",status.getPath().toString());
                        json.put("isDir",status.isDirectory());

                        result.add(json);
                    }
                }
            } catch (IOException e) {
                log.error(MessageFormat.format("获取HDFS上面的某个路径下面的所有文件失败，path:{0}",path),e);
            }finally {
                close(fileSystem);
            }
        }

        return result;
    }


    /**
     * 从HDFS下载文件至本地
     * @author adminstrator
     * @since 1.0.0
     * @param srcFile HDFS的相对目录路径，比如：/testDir/a.txt
     * @param dstFile 下载之后本地文件路径（如果本地文件目录不存在，则会自动创建），比如：D:/test.txt
     */
    public static void downloadFileFromHdfs(String srcFile, String dstFile){
        //HDFS文件路径
        Path hdfsSrcPath = new Path(generateHdfsPath(srcFile));
        //下载之后本地文件路径
        Path localDstPath = new Path(dstFile);

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            fileSystem.copyToLocalFile(hdfsSrcPath,localDstPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("从HDFS下载文件至本地失败，srcFile:{0},dstFile:{1}",srcFile,dstFile),e);
        }finally {
            close(fileSystem);
        }
    }

    public static void downloadStreamFromHdfs(String srcFile, String dstFile, HttpServletResponse response){
        //HDFS文件路径
        Path hdfsSrcPath = new Path(generateHdfsPath(srcFile));
        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            FSDataInputStream in = fileSystem.open(hdfsSrcPath);

            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment; filename=" + "test.txt");
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
            log.info("generate file : {}", dstFile);

        } catch (IOException e) {
            log.error(MessageFormat.format("从HDFS下载文件至本地失败，srcFile:{0},dstFile:{1}",srcFile,dstFile),e);
        }finally {
            close(fileSystem);
        }
    }

    /**
     * 打开HDFS上面的文件并返回 InputStream
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/c.txt
     * @return FSDataInputStream
     */
    public static FSDataInputStream open(String path){
        //HDFS文件路径
        Path hdfsPath = new Path(generateHdfsPath(path));

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();

            return fileSystem.open(hdfsPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}",path),e);
        }

        return null;
    }

    /**
     * 打开HDFS上面的文件并返回byte数组，方便Web端下载文件
     * <p>new ResponseEntity<byte[]>(byte数组, headers, HttpStatus.CREATED);</p>
     * <p>或者：new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(templateFile), headers, HttpStatus.CREATED);</p>
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/b.txt
     * @return FSDataInputStream
     */
    public static byte[] openWithBytes(String path){
        //HDFS文件路径
        Path hdfsPath = new Path(generateHdfsPath(path));

        FileSystem fileSystem = null;
        FSDataInputStream inputStream = null;
        try {
            fileSystem = getFileSystem();
            inputStream = fileSystem.open(hdfsPath);

            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}",path),e);
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return null;
    }

    /**
     * 打开HDFS上面的文件并返回String字符串
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/b.txt
     * @return FSDataInputStream
     */
    public static String openWithString(String path){
        //HDFS文件路径
        Path hdfsPath = new Path(generateHdfsPath(path));

        FileSystem fileSystem = null;
        FSDataInputStream inputStream = null;
        try {
            fileSystem = getFileSystem();
            inputStream = fileSystem.open(hdfsPath);

            return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        } catch (IOException e) {
            log.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}",path),e);
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return null;
    }

    /**
     * 打开HDFS上面的文件并转换为Java对象（需要HDFS上门的文件内容为JSON字符串）
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/c.txt
     * @return FSDataInputStream
     */
    public static <T extends Object> T openWithObject(String path, Class<T> clazz){
        //1、获得文件的json字符串
        String jsonStr = openWithString(path);

        //2、使用com.alibaba.fastjson.JSON将json字符串转化为Java对象并返回
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 重命名
     * @author adminstrator
     * @since 1.0.0
     * @param srcFile 重命名之前的HDFS的相对目录路径，比如：/testDir/b.txt
     * @param dstFile 重命名之后的HDFS的相对目录路径，比如：/testDir/b_new.txt
     */
    public static boolean rename(String srcFile, String dstFile) {
        //HDFS文件路径
        Path srcFilePath = new Path(generateHdfsPath(srcFile));
        //下载之后本地文件路径
        Path dstFilePath = new Path(dstFile);

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();

            return fileSystem.rename(srcFilePath,dstFilePath);
        } catch (IOException e) {
            log.error(MessageFormat.format("重命名失败，srcFile:{0},dstFile:{1}",srcFile,dstFile),e);
        }finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 删除HDFS文件或目录
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/c.txt
     * @return boolean
     */
    public static boolean delete(String path) {
        //HDFS文件路径
        Path hdfsPath = new Path(generateHdfsPath(path));

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();

            return fileSystem.delete(hdfsPath,true);
        } catch (IOException e) {
            log.error(MessageFormat.format("删除HDFS文件或目录失败，path:{0}",path),e);
        }finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 获取某个文件在HDFS集群的位置
     * @author adminstrator
     * @since 1.0.0
     * @param path HDFS的相对目录路径，比如：/testDir/a.txt
     * @return org.apache.hadoop.fs.BlockLocation[]
     */
    public static BlockLocation[] getFileBlockLocations(String path) {
        //HDFS文件路径
        Path hdfsPath = new Path(generateHdfsPath(path));

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            FileStatus fileStatus = fileSystem.getFileStatus(hdfsPath);

            return fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        } catch (IOException e) {
            log.error(MessageFormat.format("获取某个文件在HDFS集群的位置失败，path:{0}",path),e);
        }finally {
            close(fileSystem);
        }

        return null;
    }


    /**
     * 将相对路径转化为HDFS文件路径
     * @author adminstrator
     * @since 1.0.0
     * @param dstPath 相对路径，比如：/data
     * @return java.lang.String
     */
    private static String generateHdfsPath(String dstPath){
        String hdfsPath = defaultHdfsUrl;
        if(dstPath.startsWith("/")){
            hdfsPath += dstPath;
        }else{
            hdfsPath = hdfsPath + "/" + dstPath;
        }

        return hdfsPath;
    }

    /**
     * close方法
     */
    private static void close(FileSystem fileSystem){
        if(fileSystem != null){
            try {
                fileSystem.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
