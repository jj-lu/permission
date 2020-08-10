package jj.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jj.common.RequestHolder;
import jj.dao.SysRoleUserMapper;
import jj.dao.SysUserMapper;
import jj.model.SysRoleUser;
import jj.model.SysUser;
import jj.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    public void changeRoleUsers(int roleId,List<Integer> userIdList){
        List<Integer> originAclIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);

        //判断更新前与更新后是否相同
        if (originAclIdList.size() == userIdList.size()){
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> userIdSet = Sets.newHashSet(userIdList);
            originAclIdSet.removeAll(userIdSet);
            if (originAclIdSet.isEmpty()){
                return;
            }
        }
        updateRoleUsers(roleId,userIdList);

    }


    @Transactional
    public void updateRoleUsers(int roleId,List<Integer> userIdList){
        sysRoleUserMapper.deleteByRoleId(roleId);

        if (CollectionUtils.isEmpty(userIdList)){
            return;
        }

        List<SysRoleUser> roleUserList = Lists.newArrayList();
        for (Integer userId : userIdList){
            SysRoleUser roleUser = SysRoleUser.builder().roleId(roleId).userId(userId).operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operator(RequestHolder.getCurrentUser().getUsername()).operateTime(new Date()).build();
            roleUserList.add(roleUser);
        }

        sysRoleUserMapper.batchInsert(roleUserList);
    }
}
