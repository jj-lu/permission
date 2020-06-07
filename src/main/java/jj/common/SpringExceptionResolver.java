package jj.common;

import jj.exception.ParamException;
import jj.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 定义全局抛出异常时处理
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        //获取请求的url
        String url = httpServletRequest.getRequestURL().toString();
        ModelAndView modelAndView;
        String defaultMessage = "System error";

        // 判断请求是jason请求还是页面请求  自定义.json为json请求, .page为页面请求
        // json请求的处理
        if(url.endsWith(".json")){
            //异常为自定义异常的处理方法
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData result = JsonData.fail(e.getMessage());
                modelAndView = new ModelAndView("jsonView",result.toMap());
            }else {
                //日志记录未知异常
                log.error("unknow json exception,url:"+url,e);
                //异常为非自定义异常的处理方法
                JsonData result = JsonData.fail(defaultMessage);
                modelAndView = new ModelAndView("jsonView",result.toMap());
            }
            //页面请求的处理
        }else if (url.endsWith(".page")){
            //日志记录未知异常
            log.error("unknow page exception,url:"+url,e);
            JsonData result = JsonData.fail(defaultMessage);
            modelAndView = new ModelAndView("exception",result.toMap());
        }else {
            //日志记录未知异常
            log.error("unknow exception,url:"+url,e);
            JsonData result = JsonData.fail(defaultMessage);
            modelAndView = new ModelAndView("jsonView",result.toMap());
        }
        return modelAndView;
    }
}
