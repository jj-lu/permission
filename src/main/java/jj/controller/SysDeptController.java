package jj.controller;


import jj.common.JsonData;
import jj.dto.DeptLevelDto;
import jj.param.DeptParam;
import jj.service.SysDeptService;
import jj.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysTreeService sysTreeService;

    /**
     * 跳转到部门页面
     * @return
     */
    @RequestMapping("/dept.page")
    public ModelAndView page(){
        return new ModelAndView("dept");
    }

    /**
     * 存储部门数据
     * @param deptParam
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam deptParam){
        sysDeptService.save(deptParam);
        return JsonData.success();
    }

    /**
     * 获取部门树结构
     * @return
     */
    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<DeptLevelDto> dtoList = sysTreeService.deptTree();
        return JsonData.success(dtoList);
    }

    /**
     * 更新部门
     * @param deptParam
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(DeptParam deptParam){
        sysDeptService.update(deptParam);
        return JsonData.success();
    }

    @RequestMapping("delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id") int id){
        sysDeptService.delete(id);
        return JsonData.success();
    }
}
