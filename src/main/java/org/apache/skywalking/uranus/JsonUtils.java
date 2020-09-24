package org.apache.skywalking.uranus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtils {

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T parseObject(String json, Class<T> objClass) {
        return JSON.parseObject(json, objClass);
    }

    public static <T> List<T> parseArray(String json, Class<T> objClass) {
        return JSON.parseArray(json, objClass);
    }

    public static JSONObject parseObject(String json) {
        return JSONObject.parseObject(json);
    }

    public static JSONArray parseArray(String json) {
        return JSONObject.parseArray(json);
    }
}
