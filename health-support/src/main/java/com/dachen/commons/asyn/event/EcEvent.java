package com.dachen.commons.asyn.event;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class EcEvent {

    private EventType type;
    private Map<String, Object> params;

    private EcEvent(EventType type) {
        this.type = type;
    }

    public static EcEvent build(EventType type) {
        return new EcEvent(type);
    }

    public static EcEvent parserJSONString(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String type = jsonObject.getString("type");
        EcEvent event = new EcEvent(EventType.valueOf(type));
        JSONObject params = jsonObject.getJSONObject("params");
        if (params != null) {
            event.params = new HashMap<String, Object>();
            event.params.putAll(params);
        }

        return event;
    }

    public EventType getType() {
        return type;
    }

    public EcEvent param(String key, Object val) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put(key, val);
        return this;
    }

    public <T> T param(String key) {
        if (params == null)
            return null;
        else
            return (T) params.get(key);
    }

    public void copyParam(EcEvent event2) {
        if (event2 == null || event2.params == null)
            return;
        if (params == null)
            params = new HashMap<String, Object>();
        params.putAll(event2.params);
    }

    public String toJSONString() {
        Map<String, Object> data = new HashMap<String, Object>();
        param("createTime", System.currentTimeMillis());
//        param("flag", RandomUtils.nextInt());
        data.put("type", type);
        data.put("params", params);
        return JSON.toJSONString(data);
    }
}
