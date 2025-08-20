package com.zjlab.dataservice.config;

import com.zjlab.dataservice.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Swagger 调试时的 Mock 登录过滤器，允许通过请求头注入用户信息。
 */
@Profile({"dev","swagger"})
@Component
public class MockLoginFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private SecurityManager securityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityUtils.getSubject() != null && SecurityUtils.getSubject().getPrincipal() != null) {
            chain.doFilter(req, res);
            return;
        }
        String uid = req.getHeader("X-Mock-UserId");
        if (uid != null && securityManager != null) {
            LoginUser mock = new LoginUser();
            mock.setId(uid);
            mock.setUsername(uid);
            mock.setRealname(uid);
            Subject subject = new Subject.Builder(securityManager).buildSubject();
            PrincipalCollection pc = new SimplePrincipalCollection(mock, "mockRealm");
            subject.runAs(pc);
            ThreadContext.bind(subject);
        }
        chain.doFilter(req, res);
    }
}
