package jj.dao;

import jj.beans.PageQuery;
import jj.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int countByAclModuleId(@Param("aclModuleId") Integer aclModuleId);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId") Integer aclModuleId,@Param("page") PageQuery page);

    int countByNameAndAclModuleId(@Param("name") String name,@Param("aclModuleId") Integer aclModuleId,@Param("id") Integer id);

    List<SysAcl> getAll();

    List<SysAcl> getByIdList(@Param("ids") List<Integer> ids);

    List<SysAcl> getByUrl(@Param("url") String url);
}