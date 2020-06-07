package jj.service;

import jj.dao.SysDeptMapper;
import jj.exception.ParamException;
import jj.model.SysDept;
import jj.param.DeptParam;
import jj.util.BeanValidator;
import jj.util.LevelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 保存部门信息
     * @param param
     */
    public void save(DeptParam param){
        //校验部门信息的完整性
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
        sysDept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        sysDept.setOperator("system");
        sysDept.setOperatorIp("127.0.0.1");
        sysDept.setOperatorTime(new Date());
        sysDeptMapper.insertSelective(sysDept);
    }

    /**
     * 判断是否存在相同的部门信息
     * @param parentId
     * @param deptName
     * @param deptId
     * @return
     */
    private boolean checkExist(Integer parentId,String deptName,Integer deptId){
        return true;
    }

    /**
     * 获取部门的等级
     * @param deptid
     * @return
     */
    private String getLevel(Integer deptid){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptid);
        if (dept == null){
            return null;
        }
        return dept.getLevel();
    }
}
