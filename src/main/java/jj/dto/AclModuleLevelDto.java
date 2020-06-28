package jj.dto;

import com.google.common.collect.Lists;
import jj.model.SysAclModule;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleLevelDtoList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule aclModule){
        AclModuleLevelDto aclModuleLevelDto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule,aclModuleLevelDto);
        return  aclModuleLevelDto;
    }
}
