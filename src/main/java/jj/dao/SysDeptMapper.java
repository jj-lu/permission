package jj.dao;

import jj.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    //获取全部部门列表
    List<SysDept> getAllDept();

    //获取部门下子部门的列表
    List<SysDept> getChildDeptListByLevel(@Param("level")String level);

    //批量更新部门的level
    void batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList);

    //是否存在同部门下同名的部门
    int countByNameAndParentId(@Param("parentId") Integer parentId,@Param("name") String name,@Param("id") Integer id);
}