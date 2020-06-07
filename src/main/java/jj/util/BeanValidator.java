package jj.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jj.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.*;
import java.util.*;


/**
 * 定义全局校验工厂
 */
public class BeanValidator {
    //创建Validator工厂
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    /**
     * 单个类的校验方法（错误字段，错误信息）
     * @param t
     * @param groups
     * @param <T>
     * @return
     */
    public static <T> Map<String,String> validate(T t,Class... groups){
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> validateResult = validator.validate(t, groups);
        //没有验证错误
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        }else {
            //验证出现错误
            LinkedHashMap<String,String> errors = Maps.newLinkedHashMap();
            //将错误放进Map中
            validateResult.forEach(constraintViolation -> errors.put(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage()));
            //返回错误信息
            return errors;
        }
    }


    public static Map<String,String> validateList(Collection<?> collection){
        //判断集合是否为空,为空抛异常
        Preconditions.checkNotNull(collection);
        Iterator<?> iterator = collection.iterator();
        Map errors;

        do {
            if (!iterator.hasNext()){
                return Collections.emptyMap();
            }
            //单个校验
            Object object = iterator.next();
            errors = validate(object,new Class[0]);
        }while (errors.isEmpty());

        return errors;
    }

    /**
     * 包装validate和validatelist方法
     * @param first
     * @param objects
     * @return
     */
    public static Map<String,String> validateObject(Object first,Object... objects){
        //判断objects是否为空
        if(objects != null && objects.length > 0){
            return validateList(Lists.asList(first,objects));
        }else {
            return validate(first);
        }
    }


    /**
     * 检验后抛出异常，给规范化返回接口接收，返回致前端
     * @param param
     * @throws ParamException
     */
    public static void check(Object param) throws ParamException{
        Map<String,String> map = BeanValidator.validateObject(param);
        //使用map工具判断map是否为空
        if(MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }
    }


}
