package com.dachen.health.base.utils;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中文排序
 * @author 谢平
 *
 * @param <T>
 */
public class SortByChina<T> implements Comparator<T> {
	
	private static Logger log = LoggerFactory.getLogger(SortByChina.class);

	private String field;
	
	public SortByChina(String field) {
		this.field = field;
	}
	
	public int compare(T t1, T t2) {
		Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
		try {
			Field f = t1.getClass().getDeclaredField(field);
			f.setAccessible(true);
			Object o1 = f.get(t1);
			
			f = t2.getClass().getDeclaredField(field);
			f.setAccessible(true);
			Object o2 = f.get(t2);
			
			return cmp.compare(o1, o2);
			
		} catch (NoSuchFieldException e) {
			log.error("字段名错误！"+field, e);
		} catch (Exception e) {
			log.error("中文排序错误！Class: "+t1.getClass().getName(), e);
		}
		return 0;
	};
}
