package jj.controller;

import jj.beans.PageQuery;
import jj.common.JsonData;
import jj.param.AclParam;
import jj.service.SysAclService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    private SysAclService sysAclService;

    @RequestMapping("/save")
    @ResponseBody
    public JsonData saveAcl(AclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonData updateAcl(AclParam param){
        sysAclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData list(@Param("aclModuleId") Integer aclModuleId, PageQuery page){
        return JsonData.success(sysAclService.getPageByAclModuleId(aclModuleId,page));
    }
}
