package jj.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jj.dao.SysDeptMapper;
import jj.dto.DeptLevelDto;
import jj.model.SysDept;
import jj.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    public Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    /**
     * 将部门列表转换成树结构
     * @return
     */
    public List<DeptLevelDto> deptTree(){
        //获取所有部门
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        //将部门类转换成自定义类带有部门列表的部门类
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList){
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }

        //将列表转换为树的方法
        return deptListToTree(dtoList);
    }

    /**
     * 生成部门树的方法，先找出根节点，再递归生成树(处理第一层再进行递归)
     * @param deptLevelList
     * @return
     */
    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList){
        //判断传入参数是否为空
        if(CollectionUtils.isEmpty(deptLevelList)){
            return Lists.newArrayList();
        }

        //Map<String,List> 相当于 level -> [dept1,dept2,...]
        Multimap<String,DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();

        for (DeptLevelDto dto : deptLevelList){
            levelDeptMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        //按照seq从小到大排序
        Collections.sort(rootList, deptLevelDtoComparator);

        //递归生成树
        transformDeptTree(deptLevelList,LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }

    /**
     * 递归生成树
     * @param deptLevelList
     * @param level
     * @param levelDtoMap
     */
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> levelDtoMap){
         for (int i = 0;i<deptLevelList.size();i++){
             //遍历该层的每个元素
             DeptLevelDto deptLevelDto = deptLevelList.get(i);
             //处理当前层级的数据
             String nextLevel = LevelUtil.calculateLevel(level,deptLevelDto.getId());
             //处理下一层
             List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDtoMap.get(nextLevel);
             if (CollectionUtils.isNotEmpty(tempDeptList)){
                 //排序
                 Collections.sort(tempDeptList,deptLevelDtoComparator);
                 //设置下一层部门
                 deptLevelDto.setDeptList(tempDeptList);
                 //进入到下一层处理
                 transformDeptTree(tempDeptList,nextLevel,levelDtoMap);
             }
         }
    }

}
