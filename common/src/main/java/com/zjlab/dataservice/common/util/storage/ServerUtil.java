package com.zjlab.dataservice.common.util.storage;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.*;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Slf4j
public class ServerUtil {

    private static String host;
    private static int port;
    private static String username;
    private static String password;

    public static void setHost(String host){
        ServerUtil.host = host;
    }
    public static void setPort(int port){
        ServerUtil.port = port;
    }
    public static void setUsername(String username){
        ServerUtil.username = username;
    }
    public static void setPassword(String password){
        ServerUtil.password = password;
    }

    /**
     * 获取服务器SSH连接:提供外部参数
     */
    public static Session connectServer(String host, int port, String username, String password) throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no"); // 跳过主机密钥检查
        session.connect();
        return session;
    }

    public static Session connectServer() throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no"); // 跳过主机密钥检查
        session.connect();
        return session;
    }

    public static ChannelSftp getSftpChannel(Session session) throws JSchException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }

    public static ChannelExec getExecChannel(Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        return (ChannelExec) channel;
    }

    public static List<JSONObject> getDirectory(String directory) throws JSchException, SftpException {
        Session session = connectServer();
        ChannelSftp sftpChannel = getSftpChannel(session);
        Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(directory);
        List<JSONObject> list = new ArrayList<>();
        for (ChannelSftp.LsEntry entry : entries){
            JSONObject json = new JSONObject();
            json.put("name", entry.getFilename());
            json.put("isDir", entry.getAttrs().isDir());
            json.put("size", entry.getAttrs().getSize());
            json.put("mtime", entry.getAttrs().getMTime());
            json.put("permissions", entry.getAttrs().getPermissions());
            list.add(json);
        }
        sftpChannel.disconnect();
        session.disconnect();

        return list;
    }

    /**
     * 使用ssh执行命令  -- 执行命令失败时无反馈
     * @param cmd
     * @throws JSchException
     * @throws IOException
     */
    public static String execCmd(String cmd) throws JSchException, IOException {
        Session session = connectServer();
        ChannelExec execChannel = getExecChannel(session);
        execChannel.setCommand(cmd);
        InputStream in = execChannel.getInputStream();
        InputStream err = execChannel.getErrStream();
        execChannel.connect();

        StringBuilder stringBuilder = new StringBuilder();
        //获取命令输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        //获取错误流输出
        BufferedReader errReader = new BufferedReader(new InputStreamReader(err));
        String errLine;
        while ((errLine = errReader.readLine()) != null) {
            log.error("exec cmd : {}, result : {}", cmd, errLine);
            throw new BaseException(ResultCode.CMD_EXECUTE_FAILED);
        }
        execChannel.disconnect();
        session.disconnect();
        log.info("execute cmd success. result : {}", stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * 向服务器指定位置上传文件
     * @param file  待上传文件
     * @param remoteFile  服务器路径
     */
    public static void upload(MultipartFile file, String remoteFile) throws JSchException, IOException, SftpException {
        Session session = connectServer();
        ChannelSftp sftpChannel = getSftpChannel(session);

        InputStream inputStream = file.getInputStream();
        try {
            SftpATTRS lstat = sftpChannel.lstat(remoteFile);
            long offset = lstat.getSize();
            log.info("file existed. remoteFile : {}", remoteFile);
            inputStream.skip(offset);
            sftpChannel.put(inputStream, remoteFile, ChannelSftp.APPEND);
        }catch (SftpException e){
            log.info("file not exist. remoteFile : {}", remoteFile);
            sftpChannel.put(inputStream, remoteFile);
        }
        sftpChannel.disconnect();
        session.disconnect();
    }

    /**
     * 文件下载接口，将源路径的的文件下载为stream流，放入response中
     * @param srcPath   服务器源文件地址
     * @param response  接口返回响应
     */
    public static void download(String srcPath, HttpServletResponse response){

    }

    /**
     * 判断远程文件是否存在
     * @param remoteFile    远程文件路径
     * @return   文件是否存在
     * @throws JSchException    ssh连接异常
     */
    public static boolean fileExist(String remoteFile) throws JSchException {
        Session session = connectServer();
        ChannelSftp sftpChannel = getSftpChannel(session);
        try {
            sftpChannel.lstat(remoteFile);
            return true;
        } catch (SftpException e) {
            return false;
        }finally {
            sftpChannel.disconnect();
            session.disconnect();
        }
    }

    /**
     * 获取文件远程机器文件大小
     * @param remoteFile  远程文件路径
     * @return  文件大小
     * @throws JSchException    ssh连接异常
     * @throws SftpException    获取文件状态信息异常
     */
    public static long getRemoteFileSize(String remoteFile) throws JSchException, SftpException {
        Session session = connectServer();
        ChannelSftp sftpChannel = getSftpChannel(session);
        SftpATTRS lstat = sftpChannel.lstat(remoteFile);
        long size = lstat.getSize();
        sftpChannel.disconnect();
        session.disconnect();
        return size;
    }

    /**
     * 使用ProcessBuilder执行Linux命令
     * 必须在java的运行环境(本机)上执行cmd命令
     * @param cmd 需要执行的Linux命令
     * @return Linux命令执行的结果
     */
    public static String execute(String cmd) {
        log.info("execute cmd : {}", cmd);
        BufferedReader bufferedReader = null;
        int exitValue = -1;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("/bin/sh", "-c", cmd);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            bufferedReader = null;

            exitValue = process.waitFor();
            if (exitValue == 0) {
                log.info("execute cmd success， result ：{}",stringBuilder);
            } else {
                log.error("execute cmd failed. exitValue={}, result ：{}", exitValue, stringBuilder);
                throw new BaseException(ResultCode.CMD_EXECUTE_FAILED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return stringBuilder.toString();
    }
}
