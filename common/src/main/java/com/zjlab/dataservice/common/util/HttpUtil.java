package com.zjlab.dataservice.common.util;

import com.zjlab.dataservice.common.exception.JeecgBootException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;

public class HttpUtil {

    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static HttpClient httpClient= HttpClients.createDefault();


    /**
     * 普通HTTP POST请求接口
     *
     * @param postData 参数,格式：userid=&account=工号
     * @param postUrl  请求地址
     * @return
     */
    public static String post(String postUrl, String postData) {

        HttpResponse httpResponse= null;
        try {
            HttpPost httpPost=new HttpPost(postUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Connection", "Keep-Alive");
            httpPost.setEntity(new StringEntity(postData, "utf-8"));

            httpResponse=httpClient.execute(httpPost);
            log.info("http请求返回:"+httpResponse.getStatusLine());

            if(httpResponse.getStatusLine().getStatusCode()<200
                    ||httpResponse.getStatusLine().getStatusCode()>=300) {
                if(httpResponse.getEntity()!=null) {
                    log.error("http返回"+EntityUtils.toString(httpResponse.getEntity()));
                }
                throw new JeecgBootException("http请求失败"+postUrl);
            }
            if(httpResponse.getEntity()!=null) {
                return EntityUtils.toString(httpResponse.getEntity());
            }

            return StringUtils.EMPTY;
        } catch (Exception e) {
            log.error("http请求失败"+postUrl, e);
            throw new JeecgBootException("http请求失败"+postUrl, e);
        } finally {
            if(httpResponse!=null&&httpResponse instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse)httpResponse).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String get(String url, String param) {

        HttpResponse httpResponse= null;
        try {
            if(!StringUtils.isBlank(param)) {
                url=url+"?"+param;
            }
            HttpGet httpGet=new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Connection", "Keep-Alive");

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(10000)
                    .setSocketTimeout(20000)
                    .setConnectionRequestTimeout(10000)
                    .build();
            httpGet.setConfig(config);

            httpResponse=httpClient.execute(httpGet);
            log.info("http请求返回:"+httpResponse.getStatusLine());

            if(httpResponse.getStatusLine().getStatusCode()<200
                    ||httpResponse.getStatusLine().getStatusCode()>=300) {
                throw new JeecgBootException("httpGet请求失败"+url);
            }
            if(httpResponse.getEntity()!=null) {
                return EntityUtils.toString(httpResponse.getEntity());
            }

            return StringUtils.EMPTY;
        } catch (Exception e) {
            log.error("httpGet请求失败"+url, e);
            throw new JeecgBootException("httpGet请求失败"+url, e);
        } finally {
            if(httpResponse instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse)httpResponse).close();
                } catch (IOException e) {
//                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 获取GET http接口的状态码
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return statusCode   HttpStatus枚举类中的值
     */
    public static Integer getHttpStatusCode(String url, String param){
        HttpResponse httpResponse= null;
        try {
            if(!StringUtils.isBlank(param)) {
                url=url+"?"+param;
            }
            HttpGet httpGet=new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Connection", "Keep-Alive");

            httpResponse=httpClient.execute(httpGet);
            return httpResponse.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error("获取httpGet请求状态码失败"+url, e);
            throw new JeecgBootException("获取httpGet请求状态码失败"+url, e);
        }finally {
            if(httpResponse instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse)httpResponse).close();
                } catch (IOException e) {
//                    throw new RuntimeException(e);
                }
            }
        }
    }



}
