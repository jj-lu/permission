package jj.service;

import com.google.common.base.Preconditions;
import jj.beans.PageQuery;
import jj.beans.PageResult;
import jj.common.RequestHolder;
import jj.dao.SysAclMapper;
import jj.exception.ParamException;
import jj.model.SysAcl;
import jj.param.AclParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysLogService sysLogService;

    public void save(AclParam param){
        BeanValidator.check(param);
        log.info(param.toString());
        if (checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限下面存在相同名称的权限点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).remark(param.getRemark()).seq(param.getSeq()).build();
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateTime(new Date());
        acl.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(acl);
        sysLogService.saveAclLog(null,acl);
    }

    public void update(AclParam param){
        BeanValidator.check(param);
        if (checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限下面存在相同名称的权限点");
        }

        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");

        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).remark(param.getRemark()).seq(param.getSeq()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getUserIP(RequestHolder.getCurrentRequest()));

        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveAclLog(before,after);
    }

    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page){
        BeanValidator.check(page);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if (count > 0){
            List<SysAcl> aclList = sysAclMapper.getPageByAclModuleId(aclModuleId, page);
            log.info("开始：");
            aclList.forEach(acl -> log.info("权限点数据："+acl));
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }



    public boolean checkExist(int aclModuleId,String name,Integer id){
        return sysAclMapper.countByNameAndAclModuleId(name,aclModuleId,id) > 0;
    }

    public String generateCode(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + "_" + (int)(Math.random() * 100);
    }
}
