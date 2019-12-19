package com.jimujia.pos.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * fastJson转换工具类
 */
public class FastJsonUtil {

    /**
     * 转换JSON字符串为对象
     * @param jsonData
     * @param clazz
     * @return
     */
    public static Object jsonToObject(String jsonData, Class<?> clazz) {
        return JSONObject.parseObject(jsonData, clazz);
    }

    /**
     * 对象转String字符串
     */
    public static String toJSONString(Object object) throws JSONException{
        String json = "";
        json = JSONObject.toJSONString(object);
        return json;
    }



    /**
     * fastjson 对象转Map
     */
    public static Map<String,String> object2Map(Object object) throws JSONException{
        String jsonStr = toJSONString(object);
        Map<String,String> map = JSONObject.parseObject(jsonStr,Map.class);
        return map;
    }

    /**
     * fastjson Map转对象
     */
    public static Object map2Object(Map<String,String> map,Class<?> clazz) throws JSONException{
        String jsonStr = toJSONString(map);
        return JSONObject.parseObject(jsonStr,clazz);

    }



}
