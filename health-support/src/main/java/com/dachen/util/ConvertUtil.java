package com.dachen.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ConvertUtil
 * @Description:简单的类型转换
 * @author: qinyuan.chen
 * @date: 2016年9月9日 下午4:32:41
 *
 */
public class ConvertUtil {
	
	public static <T> T mapToObject(Map<String, Object> map, Class<T> targetClazz) {
		T targetBean = null;
		if (map == null)
			return null;
		try {
			targetBean = targetClazz.newInstance();
			Field[] beanFields = targetClazz.getDeclaredFields();
			for (Field field : beanFields) {
				field.setAccessible(true);
				String fieldName = field.getName();
				Class<?> fieldType = field.getType();
				String fieldValue = map.get(fieldName) == null ? null : map.get(fieldName).toString();
				if (fieldValue != null) {
					try {
						if (String.class.equals(fieldType)) {
							field.set(targetBean, fieldValue);
						} else if (byte.class.equals(fieldType)) {
							field.setByte(targetBean, Byte.parseByte(fieldValue));
						} else if (Byte.class.equals(fieldType)) {
							field.set(targetBean, Byte.valueOf(fieldValue));
						} else if (boolean.class.equals(fieldType)) {
							field.setBoolean(targetBean, Boolean.parseBoolean(fieldValue));
						} else if (Boolean.class.equals(fieldType)) {
							field.set(targetBean, Boolean.valueOf(fieldValue));
						} else if (short.class.equals(fieldType)) {
							field.setShort(targetBean, Short.parseShort(fieldValue));
						} else if (Short.class.equals(fieldType)) {
							field.set(targetBean, Short.valueOf(fieldValue));
						} else if (int.class.equals(fieldType)) {
							field.setInt(targetBean, Integer.parseInt(fieldValue));
						} else if (Integer.class.equals(fieldType)) {
							field.set(targetBean, Integer.valueOf(fieldValue));
						} else if (long.class.equals(fieldType)) {
							field.setLong(targetBean, Long.parseLong(fieldValue));
						} else if (Long.class.equals(fieldType)) {
							field.set(targetBean, Long.valueOf(fieldValue));
						} else if (float.class.equals(fieldType)) {
							field.setFloat(targetBean, Float.parseFloat(fieldValue));
						} else if (Float.class.equals(fieldType)) {
							field.set(targetBean, Float.valueOf(fieldValue));
						} else if (double.class.equals(fieldType)) {
							field.setDouble(targetBean, Double.parseDouble(fieldValue));
						} else if (Double.class.equals(fieldType)) {
							field.set(targetBean, Double.valueOf(fieldValue));
						} else if (Date.class.equals(fieldType)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							field.set(targetBean, sdf.parse(fieldValue));
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetBean;
	}

	public static <T> T mapToObject2(Map<String, Object> map, Class<T> beanClass) {
		if (map == null)
			return null;
		T t = null;
		try {
			t = beanClass.newInstance();
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				field.setAccessible(true);
				field.set(t, map.get(field.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static Map<String, Object> objectToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] declaredFields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : declaredFields) {
				field.setAccessible(true);
				if(field.get(obj)!=null){
					map.put(field.getName(), field.get(obj));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static <T> T objectToObject(Object sourceObject, Class<T> targetClazz) {
		T targetBean = null;
		if (null == sourceObject) {
			return null;
		}
		Map<String, Object> map = objectToMap(sourceObject);
		targetBean=mapToObject(map, targetClazz);
		return targetBean;
	}
	
	public static <T> T objectToObject2(Object sourceObject, Class<T> targetClazz) {
		T targetBean = null;
		if (null == sourceObject) {
			return null;
		}
		Map<String, Object> map = objectToMap(sourceObject);
		targetBean=mapToObject2(map, targetClazz);
		return targetBean;
	}
}
