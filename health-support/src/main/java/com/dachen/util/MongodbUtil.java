package com.dachen.util;

import com.mongodb.DBObject;

/**
 * ProjectName： health-service<br>
 * ClassName： MongodbUtil<br>
 * Description： 获取mongodb返回数据工具类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月8日
 * @version 1.0.0
 */
public class MongodbUtil {

    public static String getString(DBObject obj, String key) {
        Object val = obj.get(key);
        return val == null ? "" : val.toString();
    }

    public static Integer getInteger(DBObject obj, String key) {
        Object val = obj.get(key);
        if(val!=null){
        	return Integer.parseInt(val.toString());
        }
        return null;
    }

    public static Long getLong(DBObject obj, String key) {
        Object val = obj.get(key);
        if(val!=null){
         	return Long.parseLong(val.toString());
        }
        return null;
    }
    
    public static Double getDouble(DBObject obj, String key) {
        Object val = obj.get(key);
        return val == null ? null : (Double) val;
    }
    
    public static Boolean getBoolean(DBObject obj, String key) {
        Object val = obj.get(key);
        return val == null ? null : (Boolean) val;
    }
    
}
