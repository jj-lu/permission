package jj.controller;

import jj.model.SysUser;
import jj.service.SysUserService;
import jj.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 登录方法
     * @param request
     * @param response
     */
    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        SysUser sysUser = sysUserService.findByKeyword(username);
        //登录错误信息
        String errorMsg = "";
        //跳转地址
        String ret = request.getParameter("ret");

        if (StringUtils.isBlank(username)){
            //用户名为空
            errorMsg = "用户名不可以为空";
        }else if (StringUtils.isBlank(password)){
            //密码为空
            errorMsg = "密码不可以为空";
        }else if (sysUser == null){
            //系统不存在该用户
            errorMsg = "查询不到指定的用户";
        }else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            //密码不正确
            errorMsg = "用户名名或密码错误";
        }else if (sysUser.getStatus() != 1){
            //用户状态不可用
            errorMsg = "用户已被冻结，请联系管理员";
        }else {
            //登录成功
            request.getSession().setAttribute("user",sysUser);
            if (StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
            }else {
                //重定向
                response.sendRedirect("/admin/index.page");
            }
        }

        /**
         * response.sendRedirect()和request.getRequestDispatcher(path).forward(request,response)的区别？
         *
         * 重定向时，是服务器向游览器重新发送了一个response命令,让游览器再次向url2发送请求，以获取url2的资源;
         * 跳转到指定url地址后，上个页面的请求会结束，request对象会消亡，数据会消亡;
         * 网址会改变;
         * 传参数需要在url后加参数;
         * 可以定位到任意的网址;
         *
         * 而请求转发时，类似于是服务器自己向自己发了一个跳转，然后将结果直接给浏览器（服务器内部跳转url然后将结果发给浏览器），这也是为什么浏览器会不改变url地址。
         * 内部跳转，request对象一直存在
         * 以浏览器角度来看，他只是发送一个request然后收到一个response,所以url不变
         * 传参数可以操作request对象方法setAttribute(“name”,value)
         * 只能定位到服务器资源
         */

        request.setAttribute("error",errorMsg);
        request.setAttribute("username",username);
        if (StringUtils.isNotBlank(ret)){
            request.setAttribute("ret",ret);
        }
        String path = "signin.jsp";
        //请求转发
        request.getRequestDispatcher(path).forward(request,response);
    }
}
