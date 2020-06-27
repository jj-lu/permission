package jj.filter;

import jj.common.RequestHolder;
import jj.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器
 */
@Slf4j
public class LoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        SysUser sysuser = (SysUser) request.getSession().getAttribute("user");

        //sysuser为空，用户没有登录，返回登录页面
        if (sysuser == null){
            String path = "/signin.jsp";
            response.sendRedirect(path);
            return;
        }

        //用户已登陆,使用ThreadLocal线程
        RequestHolder.add(sysuser);
        RequestHolder.add(request);
        filterChain.doFilter(servletRequest,servletResponse);
        return;
    }

    @Override
    public void destroy() {

    }
}
