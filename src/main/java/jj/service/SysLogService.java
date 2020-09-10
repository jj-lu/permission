package jj.service;

import jj.beans.LogType;
import jj.common.RequestHolder;
import jj.dao.SysLogMapper;
import jj.model.*;
import jj.util.IpUtil;
import jj.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
public class SysLogService {

    @Resource
    private SysLogMapper sysLogMapper;

    public void saveDeptLog(SysDept before,SysDept after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveUserLog(SysUser before,SysUser after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveAclModule(SysAclModule before,SysAclModule after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveAclLog(SysAcl before,SysAcl after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveRoleAcl(int roleId, List<SysRoleAcl> before,List<SysRoleAcl> after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(roleId);
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveRoleUser(int roleId,List<SysRoleUser> before,List<SysRoleUser> after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(roleId);
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

}
