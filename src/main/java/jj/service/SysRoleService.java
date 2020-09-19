package jj.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import jj.common.RequestHolder;
import jj.dao.SysRoleAclMapper;
import jj.dao.SysRoleMapper;
import jj.dao.SysRoleUserMapper;
import jj.dao.SysUserMapper;
import jj.exception.ParamException;
import jj.model.SysRole;
import jj.model.SysUser;
import jj.param.RoleParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLogService sysLogService;

    public void save(RoleParam param){
        BeanValidator.check(param);
         if (checkExist(param.getName(),param.getId())){
             throw new ParamException("角色名称已存在");
         }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
         role.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
         role.setOperateTime(new Date());
         role.setOperator(RequestHolder.getCurrentUser().getUsername());
         sysRoleMapper.insertSelective(role);
         sysLogService.saveRoleLog(null,role);
    }

    public void update(RoleParam param){
        BeanValidator.check(param);
        if (checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的角色不存在");

        SysRole after = SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysRoleMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveRoleLog(before,after);
    }

    public List<SysRole> getAll(){
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id) > 0;
    }

    public List<SysRole> getRoleListByUserId(int userId){
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        List<SysRole> roleList = sysRoleMapper.getByIdList(roleIdList);
        return roleList;
    }

    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        List<SysRole> roleList = sysRoleMapper.getByIdList(roleIdList);
        return roleList;
    }

    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if (CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(role -> role.getId()).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }
}
