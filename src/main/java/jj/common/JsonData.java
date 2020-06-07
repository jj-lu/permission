package jj.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 规范化返回数据
 */
@Getter
@Setter
public class JsonData {
    //是否成功
    private boolean ret;
    //提示信息
    private String msg;
    //返回的数据
    private Object data;

    public JsonData(Boolean ret){
        this.ret = ret;
    }

    public static JsonData success(String msg,Object data){
        JsonData jsonData = new JsonData(true);
        jsonData.setData(data);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static JsonData success(Object data){
        JsonData jsonData = new JsonData(true);
        jsonData.setData(data);
        return jsonData;
    }

    public static JsonData success(){
        return new JsonData(true);
    }

    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static JsonData fail(){
        return new JsonData(false);
    }

    /**
     * 转化为Map返回
     * @return
     */
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("ret",ret);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }
}
