package jj.util;

import com.google.common.collect.Maps;

import javax.validation.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 定义全局校验工厂
 */
public class BeanValidator {
    //创建Validator工厂
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static <T> Map<String,String> validate(T t,Class... groups){
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Class[]>> validateResult = validator.validate(groups);
        //没有验证错误
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        }else {
            //验证出现错误
            LinkedHashMap<String,String> errors = Maps.newLinkedHashMap();
            validateResult.forEach(constraintViolation -> errors.put(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage()));
            return errors;
        }
    }


}
