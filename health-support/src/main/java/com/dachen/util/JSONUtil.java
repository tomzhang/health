package com.dachen.util;

import java.util.List;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;

public class JSONUtil {

    private static final SerializeConfig serializeConfig;
    static {
        serializeConfig = new SerializeConfig();
        serializeConfig.put(ObjectId.class, new ObjectIdSerializer());
    }

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, serializeConfig);
    }

    public static <T> T parseObject(Class<T> type, String jsonStr) {
        return JSON.parseObject(jsonStr, type);
    }
    
    public static <T> List<T> parseList(Class<T> type, String jsonStr) {
        return JSON.parseArray(jsonStr, type);
    }

    public static <T> T parseObject(TypeReference<T> type, String jsonStr) {
        return JSON.parseObject(jsonStr, type);
    }

}
