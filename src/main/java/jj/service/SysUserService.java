package jj.service;

import com.google.common.base.Preconditions;
import jj.beans.PageQuery;
import jj.beans.PageResult;
import jj.common.RequestHolder;
import jj.dao.SysUserMapper;
import jj.exception.ParamException;
import jj.model.SysUser;
import jj.param.UserParam;
import jj.util.BeanValidator;
import jj.util.IpUtil;
import jj.util.MD5Util;
import jj.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 新增用户
     * @param param
     */
    public void save(UserParam param){
        //验证信息
        BeanValidator.check(param);
        if (checkEmailExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        if(checkTelephoneExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }

        //生成密码
        String password = PasswordUtil.randomPassword();
        password = "12345678";
        String encryptedPassword = MD5Util.encrypt(password);

        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();

        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        log.info("IP:"+IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        //sendEmail


        sysUserMapper.insertSelective(user);
    }

    /**
     * 后台更新用户信息
     * @param param
     */
    public void update(UserParam param){
        BeanValidator.check(param);
        if (checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        if (checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();

        after.setOperator(RequestHolder.getCurrentUser().getUsername());

        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    /**
     * 验证是否存在相同的邮箱
     * @param mail
     * @param userId
     * @return
     */
    public boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId) > 0;
    }

    /**
     * 验证是否相同的电话
     * @param tele
     * @param userId
     * @return
     */
    public boolean checkTelephoneExist(String tele,Integer userId){
        return sysUserMapper.countByTelePone(tele,userId) > 0;
    }

    /**
     * 查询用户名或邮箱是否存在
     * @param keyword
     * @return
     */
    public SysUser findByKeyword(String keyword){
        return sysUserMapper.findByKeyword(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        if (count > 0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }
}
