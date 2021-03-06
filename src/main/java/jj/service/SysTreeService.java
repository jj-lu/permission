package jj.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jj.dao.SysAclMapper;
import jj.dao.SysAclModuleMapper;
import jj.dao.SysDeptMapper;
import jj.dto.AclDto;
import jj.dto.AclModuleLevelDto;
import jj.dto.DeptLevelDto;
import jj.model.SysAcl;
import jj.model.SysAclModule;
import jj.model.SysDept;
import jj.util.LevelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> userAclTree(int userId){
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl acl : userAclList){
            AclDto dto = AclDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);

    }

    public Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleLevelDtoComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclDto> aclComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){
        if (CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }

        Multimap<String,AclModuleLevelDto> levelDtoMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList){
            levelDtoMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        Collections.sort(rootList,aclModuleLevelDtoComparator);
        transformAclModuleTree(rootList,LevelUtil.ROOT,levelDtoMap);
        //rootList.forEach(aclModuleLevelDto -> log.info(aclModuleLevelDto.toString()));
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap multimap){
        for (int i = 0;i<dtoList.size();i++){
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level,dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) multimap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)){
                Collections.sort(tempList,aclModuleLevelDtoComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList,nextLevel,multimap);
            }
        }
    }


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

    /**
     * 权限树
     * @param roleId
     * @return
     */
    public List<AclModuleLevelDto> roleTree(int roleId){
        //当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        //当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);

        userAclList.forEach(acl -> log.info("用户分配："+acl));
        roleAclList.forEach(acl -> log.info("角色分配："+acl));

        //使用流遍历，构造set
        Set<Integer> userAclIdList = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdList = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        //系统权限点全集
        List<SysAcl> allAclList = sysAclMapper.getAll();
        //HashSet<SysAcl> aclset = new HashSet<>(allAclList);
//        //将当前用户已分配的权限点和角色分配的权限点做并集
//        Set<SysAcl> aclset = new HashSet<>(userAclList);
//        aclset.addAll(roleAclList);

        //遍历全集，赋值是否被选中和是否有权限
        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl acl : allAclList){
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdList.contains(dto.getId())){
                dto.setHasAcl(true);
            }
            if (roleAclIdList.contains(dto.getId())){
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }

        return aclListToTree(aclDtoList);
    }

    /**
     * 转换成权限树
     * @param aclDtoList
     * @return
     */
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if (CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }

        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();

        //每个模块的权限点集合
        ArrayListMultimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for (AclDto acl : aclDtoList){
            if (acl.getStatus() == 1 ){
                moduleIdAclMap.put(acl.getAclModuleId(),acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelList,moduleIdAclMap);
        return aclModuleLevelList;
    }

    /**
     * 排序权限树
     * @param aclModuleLevelList
     * @param moduleIdAclMap
     */
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList,ArrayListMultimap<Integer, AclDto> moduleIdAclMap){
        if (CollectionUtils.isEmpty(aclModuleLevelList)){
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelList){
            List<AclDto> aclDtoList = moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)){
                Collections.sort(aclDtoList,aclComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
        }
    }


}
