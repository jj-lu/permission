package jj.service;

import com.google.common.base.Preconditions;
import jj.common.RequestHolder;
import jj.dao.SysAclModuleMapper;
import jj.dao.SysDeptMapper;
import jj.exception.ParamException;
import jj.model.SysAclModule;
import jj.model.SysDept;
import jj.param.AclModuleParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import jj.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 新增权限模块
     * @param aclModuleParam
     */
    public void save(AclModuleParam aclModuleParam){
        BeanValidator.check(aclModuleParam);
        if(checkExist(aclModuleParam.getParentId(),aclModuleParam.getName(),aclModuleParam.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder().name(aclModuleParam.getName()).parentId(aclModuleParam.getParentId())
                .seq(aclModuleParam.getSeq()).status(aclModuleParam.getStatus()).remark(aclModuleParam.getRemark()).build();
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperatorTime(new Date());

        sysAclModuleMapper.insertSelective(aclModule);
    }

    /**
     *
     * @param aclModuleParam
     */
    public void update(AclModuleParam aclModuleParam){
        BeanValidator.check(aclModuleParam);
        if (checkExist(aclModuleParam.getParentId(),aclModuleParam.getName(),aclModuleParam.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(aclModuleParam.getId());
        Preconditions.checkNotNull(before,"待更新权限模块不存在");

        SysAclModule after = SysAclModule.builder().id(aclModuleParam.getId()).name(aclModuleParam.getName()).parentId(aclModuleParam.getParentId())
                .seq(aclModuleParam.getSeq()).status(aclModuleParam.getStatus()).remark(aclModuleParam.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(aclModuleParam.getParentId()),aclModuleParam.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperatorTime(new Date());

        updateWithChild(before,after);

    }

    /**
     * 更新子模块
     * @param before
     * @param after
     */
    @Transactional
    public void updateWithChild(SysAclModule before,SysAclModule after){
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if (!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(aclModuleList)){
                for (SysAclModule aclModule : aclModuleList){
                    String level = aclModule.getLevel();
                    if (level.indexOf(oldLevelPrefix) == 0){
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId,String aclMOduleName,Integer deptId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,aclMOduleName,deptId) > 0;
    }

    private String getLevel(Integer deptId){
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }
}
