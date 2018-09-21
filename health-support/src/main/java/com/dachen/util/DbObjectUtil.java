package com.dachen.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject; 

public class DbObjectUtil {

	/** 
	   * 把实体bean对象转换成DBObject 
	   * @param bean 
	   * @return 
	   * @throws IllegalArgumentException 
	   * @throws IllegalAccessException 
	   */  
	  public static <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException,  
	      IllegalAccessException {  
	    if (bean == null) {  
	      return null;  
	    }  
	    DBObject dbObject = new BasicDBObject();  
	    // 获取对象对应类中的所有属性域  
	    Field[] fields = bean.getClass().getDeclaredFields();  
	    for (Field field : fields) {  
	      // 获取属性名  
	      String varName = field.getName();  
	      // 修改访问控制权限  
	      boolean accessFlag = field.isAccessible();  
	      if (!accessFlag) {  
	        field.setAccessible(true);  
	      }  
	      Object param = field.get(bean);  
	      if (param == null) {  
	        continue;  
	      } else if (param instanceof Integer) {//判断变量的类型  
	        int value = ((Integer) param).intValue();  
	        dbObject.put(varName, value);  
	      } else if (param instanceof String) {  
	        String value = (String) param;  
	        dbObject.put(varName, value);  
	      } else if (param instanceof Double) {  
	        double value = ((Double) param).doubleValue();  
	        dbObject.put(varName, value);  
	      } else if (param instanceof Float) {  
	        float value = ((Float) param).floatValue();  
	        dbObject.put(varName, value);  
	      } else if (param instanceof Long) {  
	        long value = ((Long) param).longValue();  
	        dbObject.put(varName, value);  
	      } else if (param instanceof Boolean) {  
	        boolean value = ((Boolean) param).booleanValue();  
	        dbObject.put(varName, value);  
	      } else if (param instanceof Date) {  
	        Date value = (Date) param;  
	        dbObject.put(varName, value);  
	      } else if (param instanceof List) {  
    	    List value = (List) param;
    	    if (value != null && value.size() > 0) {
    	    	BasicDBList list = new BasicDBList();
				for (Object object : value) {
					DBObject o = bean2DBObject(object);
					list.add(o);
				}
				dbObject.put(varName, list);  
			}
	      } 

	      // 恢复访问控制权限  
	      field.setAccessible(accessFlag);  
	    }  
	    return dbObject;  
	  }  
}
