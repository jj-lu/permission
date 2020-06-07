package jj.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * 类与Json的相互转换
 */
@Slf4j
public class JsonMapper {

    //核心类
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //config
        //排除为空的字段
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    //核心转换方法
    //对象转换为字符串
    public static <T> String obj2String(T src){
        //
        if (src == null){
            return null;
        }

        try{
            //如果是String类型直接强转，其他类型调用方法进行转换
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        }catch (Exception e){
            log.warn("parse object to String exception:"+e);
            return null;
        }
    }

    //字符串转化成对象
    public static <T> T string2Obj(String src, TypeReference<T> tTypeReference){
        //判断非空
        if (src == null || tTypeReference == null){
            return null;
        }
        //进行转换
        try{
            return (T) (tTypeReference.getType().equals(String.class) ? src : objectMapper.readValue(src,tTypeReference));
        }catch (Exception e){
            log.warn("parse String to Object exception,String:{},TypeReference<T>:{},error:{}",src,tTypeReference.getType(),e);
            return null;
        }
    }
}
