package jj.service;

import jj.dao.SysAclModuleMapper;
import jj.model.SysAclModule;
import jj.param.AclModuleParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public void save(AclModuleParam aclModuleParam){

    }

    public void update(AclModuleParam aclModuleParam){

    }

    public void updateWithChild(SysAclModule before,SysAclModule after){

    }

    private boolean checkExist(){
        return false;
    }


}
