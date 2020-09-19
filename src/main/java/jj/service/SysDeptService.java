package jj.service;

import com.google.common.base.Preconditions;
import jj.common.RequestHolder;
import jj.dao.SysDeptMapper;
import jj.dao.SysUserMapper;
import jj.exception.ParamException;
import jj.model.SysDept;
import jj.param.DeptParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import jj.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource SysLogService sysLogService;

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
        sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysDept.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysDept.setOperatorTime(new Date());
        sysDeptMapper.insertSelective(sysDept);
        sysLogService.saveDeptLog(null,sysDept);
    }

    /**
     * 更新部门信息
     * @param param
     */
    public void update(DeptParam param){
        //检验部门信息的完整性
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }

        //获取待更新的部门
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        //检验待更新部门是否为空
        Preconditions.checkNotNull(before,"带更新的部门不存在");
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperatorTime(new Date());

        updateWithChild(before,after);
        sysLogService.saveDeptLog(before,after);
    }

    /**
     * 更新部门与旗下子部门的信息（调用事务，全部成功或全部失败）
     * @param before
     * @param after
     */
    @Transactional
    void updateWithChild(SysDept before, SysDept after){
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();

        //更新需要修改的子部门层级信息
        if (!after.getLevel().equals(before.getLevel())){
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(deptList)){
                for (SysDept dept : deptList){
                    String level = dept.getLevel();
                    //更新前部门层级为子部门层级的前缀
                    if (level.indexOf(before.getLevel())==0){
                        //截取更新前部门层级后缀，与更新后部门层级拼接
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }

        //更新当前部门的信息
        sysDeptMapper.updateByPrimaryKey(after);


    }

    /**
     * 判断是否存在相同的部门信息
     * @param parentId
     * @param deptName
     * @param deptId
     * @return
     */
    private boolean checkExist(Integer parentId,String deptName,Integer deptId){
        int i = sysDeptMapper.countByNameAndParentId(parentId, deptName, deptId);
        return i > 0;
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

    /**
     * 删除部门
     * @param deptId
     */
    public void delete(int deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);

        //判断部门是否存在
        Preconditions.checkNotNull(dept,"待删除的部门不存在，无法删除");

        //判断是否有子部门
        if (sysDeptMapper.countByParentId(deptId) > 0){
            throw new ParamException("当前部门下有子部门，无法删除");
        }

        //判断部门下是否有员工
        if (sysUserMapper.countByDeptId(deptId) > 0){
            throw new ParamException("当前部门下有用户，无法删除");
        }

        sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}
