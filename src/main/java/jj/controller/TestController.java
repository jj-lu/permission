package jj.controller;

import jj.common.ApplicationContextHelper;
import jj.common.JsonData;
import jj.dao.SysAclModuleMapper;
import jj.exception.PermissionException;
import jj.model.SysAclModule;
import jj.param.TestVo;
import jj.util.BeanValidator;
import jj.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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

    @RequestMapping("testJson.json")
    @ResponseBody
    public JsonData testJsonData(){
        log.info("test JsonData");
        throw new PermissionException("test Exception");
        //return JsonData.success("test,JsonData");
    }

    @RequestMapping("validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
        try{
            //测试校验
            Map<String,String> map = BeanValidator.validateObject(vo);
            //判断map是否为空
            if (map != null & map.entrySet().size() > 0){
                //遍历map
                for (Map.Entry<String,String> entry : map.entrySet()){
                    log.info("{}->{}",entry.getKey(),entry.getValue());
                }
            }
        }catch (Exception e){

        }

        return JsonData.success("test validate");
    }

    @RequestMapping("testValidate.json")
    @ResponseBody
    public JsonData testValidate(TestVo testVo){
        log.info("test,testValidate");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule aclModule = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(aclModule));
        BeanValidator.check(testVo);
        return JsonData.success(testVo);
    }
}
