package jj.service;

import com.google.common.collect.Lists;
import jj.common.RequestHolder;
import jj.dao.SysAclMapper;
import jj.dao.SysRoleAclMapper;
import jj.dao.SysRoleUserMapper;
import jj.model.SysAcl;
import org.apache.commons.collections.CollectionUtils;
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

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 获取当前用户的权限点
     * @return
     */
    public List<SysAcl> getCurrentUserAclList(){

        int userId = RequestHolder.getCurrentUser().getId();
        List<SysAcl> sysAclList = getUserAclList(userId);
        return sysAclList;
    }

    /**
     * 获取角色的权限点
     * @param roleId
     * @return
     */
    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);

    }

    /**
     * 根据用户id查权限点
     * @param userId
     * @return
     */
    public List<SysAcl> getUserAclList(int userId){
        //判断是否超级管理员
        if (isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if (CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }

        return sysAclMapper.getByIdList(userAclIdList);
    }


    /**
     * 是否超级管理员
     * @return
     */
    public boolean isSuperAdmin(){
        return true;
    }
}
