package jj.dao;

import jj.beans.PageQuery;
import jj.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    //查询用户名或邮箱
    SysUser findByKeyword(@Param("keyword") String keyword);

    //查询邮箱
    int countByMail(@Param("mail") String mail,@Param("id") Integer id);

    //查询电话
    int countByTelePone(@Param("telephone") String telephone,@Param("id") Integer id);

    //根据部门Id查询用工总数
    int countByDeptId(@Param("deptId") Integer deptId);

    //根据部门id查询用户
    List<SysUser> getPageByDeptId(@Param("deptId") int deptId, @Param("page")PageQuery page);

    //根据用户ids查询用户
    List<SysUser> getByIdList(@Param("idList") List<Integer> idList);

    //查询所有用户
    List<SysUser> getAll();

}