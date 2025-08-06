package com.zjlab.dataservice.common.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(urlPatterns = "/*")
@Slf4j
@Component
public class HttpFilter implements Filter {

    List<String> whiteList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                                                            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        log.info("filter url : {}", requestURI);
        String userId = request.getHeader("X-Login-UserId");
        String userName = request.getHeader("X-Login-LoginName");
        String token = request.getHeader("X-Token");
        if (userId != null){
            UserThreadLocal.setUserId(userId);
            TokenThreadLocal.setToken(token);
        }

        filterChain.doFilter(servletRequest, servletResponse);
        log.info("will remove threadlocal authorization");
        UserThreadLocal.remove();
        TokenThreadLocal.remove();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
