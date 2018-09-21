package com.dachen.line.stat.util;

import java.util.Collection;
import java.util.Random;

/**
 * 获取配置
 * 
 * @author weilit
 *
 */
public class ConfigUtil {

	/**
	 * 获取随机数目
	 * 
	 * @param range
	 * @return
	 */
	public static int getRandomNumber(int range) {
		int result = 0;
		Random random = new Random();
		result = random.nextInt(range);
		return result;
	}

	/**
	 * 随机获取数组中的数值
	 * 
	 * @param random
	 * @param args
	 * @return
	 */
	public static String getRandomArrayItem(String[] args) {
		String result = null;
		if (null != args && args.length > 0) {
			int resultRandom = getRandomNumber(args.length);
			if (resultRandom >= 0 && resultRandom <= args.length) {
				result = args[resultRandom];
			} else {
				result = args[args.length - 1];
			}
		}
		return result;
	}

	public static boolean checkCollectionIsEmpty(
			Collection<? extends Object> collections) {
		if (null != collections && collections.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		System.err.println(getRandomNumber(12));
	}
}
