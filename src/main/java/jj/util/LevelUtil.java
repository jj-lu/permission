package jj.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 部门层级工具类
 */
public class LevelUtil {

    //分隔符
    public final static String SEPRATOR = ".";

    //根层级
    public final static String ROOT = "0";

    /**
     * 层级计算规则
     * @param parentLevel
     * @param parentId
     * @return
     */
    public static String calculateLevel(String parentLevel,int parentId){
        if (StringUtils.isBlank(parentLevel)){
            return ROOT;
        }else {
            return StringUtils.join(parentLevel,SEPRATOR,parentId);
        }
    }
}
