package jj.service;

import jj.dao.SysAclMapper;
import jj.dao.SysRoleUserMapper;
import jj.model.SysAcl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 核心权限
 */
@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    /**
     * 获取当前用户的权限点
     * @return
     */
    public List<SysAcl> getCurrentUserAclList(){
        //判断是否超级管理员
        if (isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        //
        return null;
    }

    /**
     * 获取角色的权限点
     * @param roleId
     * @return
     */
    public List<SysAcl> getRoleAclList(int roleId){
        return null;
    }

    /**
     * 根据用户id查权限点
     * @param userId
     * @return
     */
    public List<SysAcl> getUserAclList(int userId){
        return null;
    }


    /**
     * 是否超级管理员
     * @return
     */
    public boolean isSuperAdmin(){
        return true;
    }
}
