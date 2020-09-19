package jj.service;

import com.google.common.base.Preconditions;
import jj.beans.LogType;
import jj.beans.PageQuery;
import jj.beans.PageResult;
import jj.common.RequestHolder;
import jj.dao.*;
import jj.dto.SearchLogDto;
import jj.exception.ParamException;
import jj.model.*;
import jj.param.SearchLogParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import jj.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysLogService {

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysRoleUserService sysRoleUserService;

    @Resource
    private SysRoleAclService sysRoleAclService;

    public void recover(int id){
        SysLogWithBLOBs sysLog = sysLogMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysLog,"待还原的记录不存在");
        switch (sysLog.getType()){
            case LogType.TYPE_DEPT:
                SysDept beforeDept = sysDeptMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforeDept,"待还原的部门已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysDept afterDept = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysDept>() {
                });
                afterDept.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterDept.setOperatorIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
                afterDept.setOperatorTime(new Date());
                sysDeptMapper.updateByPrimaryKeySelective(afterDept);
                saveDeptLog(beforeDept,afterDept);
                break;
            case LogType.TYPE_USER:
                SysUser beforeUser = sysUserMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforeUser,"带还原的用户已经不存在了");
                if (StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysUser afterUser = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysUser>() {
                });
                afterUser.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
                afterUser.setOperateTime(new Date());
                sysUserMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforeUser,afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:
                SysAclModule beforeAclModule = sysAclModuleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforeAclModule,"带还原的用户已经不存在了");
                if (StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysAclModule afterAclModule = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAclModule>() {
                });
                afterAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAclModule.setOperatorIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
                afterAclModule.setOperatorTime(new Date());
                sysAclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforeAclModule,afterAclModule);
                break;
            case LogType.TYPE_ACL:
                SysAcl beforeAcl = sysAclMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforeAcl,"带还原的权限已经不存在了");
                if (StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysAcl afterAcl = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAcl>() {
                });
                afterAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
                afterAcl.setOperateTime(new Date());
                sysAclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforeAcl,afterAcl);
                break;
            case LogType.TYPE__ROLE:
                SysRole beforeRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforeRole,"带还原的角色已经不存在了");
                if (StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysRole afterRole = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysRole>() {
                });
                afterRole.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterRole.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
                afterRole.setOperateTime(new Date());
                sysRoleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforeRole,afterRole);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole aclRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(aclRole,"角色已经不存在了");
                sysRoleAclService.changeRoleAcls(sysLog.getTargetId(),JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole userRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(userRole,"角色已经不存在了");
                sysRoleUserService.changeRoleUsers(sysLog.getTargetId(),JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
                default:;
        }
    }

    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam searchLogParam,PageQuery pageQuery){
        BeanValidator.check(pageQuery);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(searchLogParam.getType());
        if (StringUtils.isNotBlank(searchLogParam.getBeforeSeg())){
            dto.setBeforeSeg("%"+searchLogParam.getBeforeSeg()+"%");
        }
        if (StringUtils.isNotBlank(searchLogParam.getAfterSeg())){
            dto.setAfterSeg("%"+searchLogParam.getAfterSeg()+"%");
        }
        if (StringUtils.isNotBlank(searchLogParam.getOperator())){
            dto.setOperator(searchLogParam.getOperator());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNotBlank(searchLogParam.getFromTime())){
                simpleDateFormat.parse(searchLogParam.getFromTime());
            }
            if (StringUtils.isNotBlank(searchLogParam.getAfterSeg())){
                simpleDateFormat.parse(searchLogParam.getAfterSeg());
            }
        } catch (ParseException e) {
            throw new ParamException("传入的日期格式有问题，正确格式未：yyyy-MM-dd HH:mm:ss");
        }
        int count = sysLogMapper.countBySearchDto(dto);
        if (count > 0){
            List<SysLogWithBLOBs> logList = sysLogMapper.getPageListBySearchDto(dto,pageQuery);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(logList).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();

    }

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

    public void saveAclModuleLog(SysAclModule before,SysAclModule after){
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
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }



    public void saveRoleLog(SysRole before,SysRole after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

}
