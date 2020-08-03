package testaddAcl;


import jj.dao.SysAclModuleMapper;
import jj.model.SysAclModule;
import jj.util.LevelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml","classpath:mybatis-config.xml"})
public class save {


    @Resource
    private SysAclModuleMapper sysAclModule;

    @Test
    public void save(){

        System.out.println(LevelUtil.calculateLevel(getLevel(6),6));
    }

    private String getLevel(Integer deptId){
        SysAclModule sysDept = sysAclModule.selectByPrimaryKey(deptId);
        if (sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }
}
