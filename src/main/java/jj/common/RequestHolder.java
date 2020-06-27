package jj.common;

import jj.model.SysUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截
 * ThreadLocal：线程保存内容分开，防止并发请求登录，预防高并发
 * 可以在过滤器中使用，保存每次请求的用户
 */
public class RequestHolder {

    //登录的用户对象
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    //request对象
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    /**
     * HttpInterceptor中可以调用，当方法结束时使用
     */
    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}
