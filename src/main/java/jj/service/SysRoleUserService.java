package jj.service;

import com.google.common.collect.Lists;
import jj.dao.SysRoleUserMapper;
import jj.dao.SysUserMapper;
import jj.model.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 拥有该角色的用户
     * @param roleId
     * @return
     */
    public List<SysUser> getListByRoleId(int roleId){
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }
}
