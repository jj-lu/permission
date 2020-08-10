package jj.controller;

import com.google.common.collect.Maps;
import jj.beans.PageQuery;
import jj.common.JsonData;
import jj.model.SysRole;
import jj.param.AclParam;
import jj.service.SysAclService;
import jj.service.SysRoleService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    private SysAclService sysAclService;

    @Resource
    private SysRoleService sysRoleService;


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

    @RequestMapping("acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("aclId") int aclId){
        Map<String,Object> map = Maps.newHashMap();
        //根据用色获取用户
        List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);

        map.put("user",sysRoleService.getUserListByRoleList(roleList));
        map.put("roles",roleList);
        return JsonData.success(map);
    }
}
