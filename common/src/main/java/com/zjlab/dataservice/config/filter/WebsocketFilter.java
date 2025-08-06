package com.zjlab.dataservice.config.filter;

import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.CommonAPI;
import org.jeecg.common.util.RedisUtil;
import com.zjlab.dataservice.common.util.SpringContextUtils;
import com.zjlab.dataservice.common.util.TokenUtils;
import com.zjlab.dataservice.common.util.oConvertUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * websocket 前端将token放到子协议里传入 与后端建立连接时需要用到http协议，此处用于校验token的有效性
 * @Author taoYan
 * @Date 2022/4/21 17:01
 **/
@Slf4j
public class WebsocketFilter implements Filter {

    private static final String TOKEN_KEY = "Sec-WebSocket-Protocol";
    private static final String USER_KEY = "X-Login-UserId";

    private static CommonAPI commonApi;

    private static RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (commonApi == null) {
            commonApi = SpringContextUtils.getBean(CommonAPI.class);
        }
        if (redisUtil == null) {
            redisUtil = SpringContextUtils.getBean(RedisUtil.class);
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = request.getHeader(TOKEN_KEY);
        String userId = request.getHeader(USER_KEY);

        log.debug("Websocket连接 Token安全校验，Path = {}，token:{}", request.getRequestURI(), token);

        try {
            TokenUtils.verifyToken(token, commonApi, redisUtil);
        } catch (Exception exception) {
            //log.error("Websocket连接 Token安全校验失败，IP:{}, Token:{}, Path = {}，异常：{}", oConvertUtils.getIpAddrByRequest(request), token, request.getRequestURI(), exception.getMessage());
            log.debug("Websocket连接 Token安全校验失败，IP:{}, Token:{}, Path = {}，异常：{}", oConvertUtils.getIpAddrByRequest(request), token, request.getRequestURI(), exception.getMessage());
            return;
        }

        if (userId != null){
            UserThreadLocal.setUserId(userId);
        }
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setHeader(TOKEN_KEY, token);
        filterChain.doFilter(servletRequest, servletResponse);

        UserThreadLocal.remove();
    }

}
