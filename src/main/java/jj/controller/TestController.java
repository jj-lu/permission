package jj.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    //private final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        log.info("test permission");
        return "hello,permission";
    }
}
