package jj.service;

import com.google.common.base.Preconditions;
import jj.common.RequestHolder;
import jj.dao.SysRoleMapper;
import jj.exception.ParamException;
import jj.model.SysRole;
import jj.param.RoleParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

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
    }

    public List<SysRole> getAll(){
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id) > 0;
    }
}
