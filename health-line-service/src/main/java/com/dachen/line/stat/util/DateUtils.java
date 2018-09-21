package com.dachen.line.stat.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import com.dachen.util.StringUtil;

public final class DateUtils {

	public static final SimpleDateFormat FORMAT_YYYY_MM;

	public static final SimpleDateFormat FORMAT_YYYY_MM_DD;

	public static final SimpleDateFormat FORMAT_YYYY_MM_DD_HH_MM_SS;

	public static final SimpleDateFormat FORMAT_YYYY_MM_DD_HH_MM;
	public static final SimpleDateFormat FORMAT_MM_DD;
	public static final SimpleDateFormat FORMAT_PATTERN_MM_DD_HH_MM;
	public static final Pattern PATTERN_YYYY_MM;

	public static final Pattern PATTERN_YYYY_MM_DD;

	public static final Pattern PATTERN_YYYY_MM_DD_HH_MM_SS;
	static {
		FORMAT_PATTERN_MM_DD_HH_MM = new SimpleDateFormat("MM-dd HH:mm");
		FORMAT_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FORMAT_YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
		FORMAT_YYYY_MM = new SimpleDateFormat("yyyy-MM");
		FORMAT_MM_DD = new SimpleDateFormat("MM-dd");
		PATTERN_YYYY_MM_DD_HH_MM_SS = Pattern
				.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}");
		PATTERN_YYYY_MM_DD = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
		PATTERN_YYYY_MM = Pattern.compile("[0-9]{4}-[0-9]{1,2}");
	}

	/**
	 * 转换当前时间为默认格式
	 * 
	 * @author fanp
	 * @return
	 */
	public static String formatDate2Str() {
		return formatDate2Str(new Date());
	}

	/**
	 * 转换指定时间为默认格式
	 * 
	 * @author fanp
	 * @param date
	 * @return
	 */
	public static String formatDate2Str(Date date) {
		return formatDate2Str(date, FORMAT_YYYY_MM_DD_HH_MM_SS);
	}

	/**
	 * 转换指定时间为默认格式
	 * 
	 * @author fanp
	 * @param date
	 * @return
	 */
	public static String formatDate2Str2(Date date) {
		return formatDate2Str(date, FORMAT_YYYY_MM_DD_HH_MM);
	}

	/**
	 * 转换当前日期为指定格式
	 * 
	 * @author fanp
	 * @param date
	 * @return
	 */
	public static String formatDate2Str(SimpleDateFormat format) {
		return formatDate2Str(new Date(), format);
	}

	/**
	 * 转换当前日期为指定格式
	 * 
	 * @author fanp
	 * @param date
	 * @return
	 */
	public static String formatDate2Str(Long milliseconds,
			SimpleDateFormat format) {
		if (milliseconds == null) {
			return null;
		}

		return formatDate2Str(new Date(milliseconds), format);
	}

	/**
	 * 转换指定时间为指定格式
	 * 
	 * @author fanp
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate2Str(Date date, SimpleDateFormat format) {
		if (date == null) {
			return null;
		}

		if (format == null) {
			format = FORMAT_YYYY_MM_DD_HH_MM_SS;
		}
		return format.format(date);
	}

	public static long currentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	public static String getFullString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getYMDString() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	public static String getYMString() {
		return new SimpleDateFormat("yyyyMM").format(new Date());
	}

	public static Date toDate(String strDate) {
		strDate = strDate.replaceAll("/", "-");
		try {
			if (PATTERN_YYYY_MM_DD_HH_MM_SS.matcher(strDate).find())
				return FORMAT_YYYY_MM_DD_HH_MM_SS.parse(strDate);
			else if (PATTERN_YYYY_MM_DD.matcher(strDate).find())
				return FORMAT_YYYY_MM_DD.parse(strDate);
			else if (PATTERN_YYYY_MM.matcher(strDate).find())
				return FORMAT_YYYY_MM.parse(strDate);
			else
				throw new RuntimeException("未知的日期格式化字符串");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long toTimestamp(String strDate) {
		return toDate(strDate).getTime();
	}

	public static long toSeconds(String strDate) {
		return toTimestamp(strDate) / 1000;
	}

	public static long s2s(String s) {
		s = StringUtil.trim(s);
		if ("至今".equals(s)) {
			return 0;
		}
		return toSeconds(s);
	}

	/**
	 * 
	 * </p>计算年差</p>
	 * 
	 * @param d1
	 * @param d2
	 * @return d1.yyyy-d2.yyyy
	 * @author limiaomiao
	 * @date 2015年7月8日
	 */
	public static int yearDiff(Date d1, Date d2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int y1 = Integer.parseInt(sdf.format(d1));
		int y2 = Integer.parseInt(sdf.format(d2));
		return y1 - y2;

	}

	/**
	 * </p>获取一周第一天</p>
	 * 
	 * @param date
	 * @return
	 * @author fanp
	 * @date 2015年8月12日
	 */
	public static Integer getFirstOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}

		int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;
		cal.add(Calendar.DATE, -daysSinceMonday);

		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1, 2));
		sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH), 2));
		return Integer.parseInt(sb.toString());
	}

	/**
	 * </p>获取一周最后一天</p>
	 * 
	 * @param date
	 * @return
	 * @author fanp
	 * @date 2015年8月12日
	 */
	public static Integer getLastOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}

		int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;

		cal.add(Calendar.DATE, 6 - daysSinceMonday);

		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1, 2));
		sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH), 2));
		return Integer.parseInt(sb.toString());
	}

	/**
	 * </p>获取一周第一天</p>
	 * 
	 * @param date
	 * @return
	 * @author fanp
	 * @date 2015年8月12日
	 */
	public static Integer[] getWeekDays(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;
		Integer[] days = new Integer[7];

		for (int i = 0; i < 7; i++) {
			if (i == 0) {
				daysSinceMonday = -daysSinceMonday;
			} else {
				daysSinceMonday = 1;
			}

			cal.add(Calendar.DATE, daysSinceMonday);

			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR));
			sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1, 2));
			sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH), 2));

			days[i] = Integer.parseInt(sb.toString());
		}

		return days;
	}

	/**
	 * 获得月份的第一天。
	 * 
	 * @return 月份的第一天。
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.DATE, 1);
		return cal.getTime();
	}

	/**
	 * 获得月份的最后第一天。
	 * 
	 * @return 月份的最后第一天。
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * </p>数字类型日期转换成Date</p>
	 * 
	 * @param date
	 *            yyyyMMdd yyyyMM yyyy
	 * @return
	 * @author fanp
	 * @date 2015年8月19日
	 */
	public static Date intToDate(Integer date) {
		if (date == null) {
			return null;
		}
		if (String.valueOf(date).length() == 8) {
			Calendar cal = Calendar.getInstance();
			cal.set(date / 10000, (date % 10000) / 100 - 1, date % 100, 0, 0, 0);
			return cal.getTime();
		}
		if (String.valueOf(date).length() == 6) {
			Calendar cal = Calendar.getInstance();
			cal.set(date / 100, date % 100 - 1, 1, 0, 0, 0);
			return cal.getTime();
		}
		if (String.valueOf(date).length() == 4) {
			Calendar cal = Calendar.getInstance();
			cal.set(date, 0, 1, 0, 0, 0);
			return cal.getTime();
		}
		return null;
	}

	public static String getMonthFrastDay(Date startDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		return c.getTimeInMillis() + "";
	}

	public static Date getLassDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DATE, -1);
		return c.getTime();
	}

	public static String getMonthLastDay(Date endDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		return c.getTimeInMillis() + c.getTimeInMillis()
				+ (24 * 60 * 1000 * 60) + "";
	}

	/**
	 * 
	 * 年龄计算
	 * 
	 * @author 李淼淼
	 * @date 2015年8月17日
	 */
	public static int calcAge(Long birthday) {
		if (birthday != null) {
			Calendar cal = Calendar.getInstance();
			int now = cal.get(Calendar.YEAR);
			cal.setTime(new Date(birthday));
			int bir = cal.get(Calendar.YEAR);
			return now - bir;
		} else {
			return 0;
		}
	}

	public static Date getCurrentMonthStart() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static int daysBetween(String smdate, String bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * sfzq格式：0 Day，2 day；0 Week
	 * 
	 * @param sfzq
	 * @return
	 */
	public static int getDayNumBySfzq(String sfzq) {
		int dayNum = 0;
		if (sfzq.split(" ")[1].equals("Day")) {
			dayNum = Integer.valueOf(sfzq.split(" ")[0]);
		}
		return dayNum;
	}

	public static Date getAddDate(Date date, Integer addNum) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(java.util.Calendar.DAY_OF_MONTH, addNum);
		return cal.getTime();
	}

	/**
	 * 获取指定多少天的日期
	 * 
	 * @param days
	 *            0 当天 1 明天
	 */
	public static Date getAfterDaysDate(int days) {
		Date result = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.after(new Date());
		result = calendar.getTime();

		return result;
	}

	/**
	 * 获取指定多少天的日期
	 * 
	 * @param days
	 *            0 当天 1 明天
	 */
	public static Date getAfterDaysDate(int days, Date time) {
		Date result = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.after(time);
		result = calendar.getTime();

		return result;
	}

	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 获取当前时间+或者-小时数之后得到返回的秒数
	 * 
	 * @param hour
	 * @return
	 */
	public static Long getSecond(int hour) {
		Date date = new Date();
		Calendar dar = Calendar.getInstance();
		dar.setTime(date);
		dar.add(java.util.Calendar.HOUR_OF_DAY, hour);
		Date dates = dar.getTime();
		return dates.getTime() / 1000;
	}

	/**
	 * 获取当前时间+或者-小时数之后得到返回的秒数
	 * 
	 * @param hour
	 * @return
	 */
	public static Long getSecond(int hour, int min) {
		Date date = new Date();
		Calendar dar = Calendar.getInstance();
		dar.setTime(date);
		if (hour != 0) {
			dar.add(java.util.Calendar.HOUR_OF_DAY, hour);
		} else if (min != 0) {
			dar.add(java.util.Calendar.MINUTE, min);
		}
		Date dates = dar.getTime();
		System.out.println(dates.getTime() / 1000);
		return dates.getTime() / 1000;
	}

	/**
	 * 获取指定时间前一天的秒数
	 * 
	 * @param hour
	 * @return
	 * @throws ParseException
	 */
	public static Long getAfterSecond(String seconds, String t)
			throws ParseException {
		Date dBefore = new Date(); // 当前时间
		Date date = new Date();
		date.setTime(Long.parseLong(seconds));
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		dBefore = calendar.getTime(); // 得到前一天的时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
		String defaultStartDate = sdf.format(dBefore) + " " + t; // 格式化前一天
		return sdf1.parse(defaultStartDate).getTime();
	}

	/**
	 * 根据传入的时间 获取指定天数之后的时间（求二者之间的秒数）
	 * 
	 * @param seconds
	 * @return
	 * @throws ParseException
	 */
	public static Long getNextDaySecond(Long seconds, int d)
			throws ParseException {
		Date dBefore = new Date(); // 当前时间
		Date date = new Date();
		date.setTime(seconds * 1000);
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, d); // 设置为前一天
		dBefore = calendar.getTime(); // 得到前一天的时间
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
		String defaultStartDate = sdf1.format(dBefore); // 格式化前一天
		System.out.println(sdf1.parse(defaultStartDate).getTime());
		return sdf1.parse(defaultStartDate).getTime();
	}

	/**
	 * 根据秒数获取日期
	 * 
	 * @param seconds
	 * @return
	 */
	public static String getDateFromSecond(String seconds) {
		if (seconds == null)
			return null;
		else {
			Date date = new Date();
			date.setTime(Long.parseLong(seconds) * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println(sdf.format(date));
			return sdf.format(date);
		}
	}

	/**
	 * 根据传入的字符窜日期获取相应的毫秒数
	 * 
	 * @param dstr
	 * @return
	 * @throws ParseException
	 */
	public static String getSeconds2(String dstr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 小写的mm表示的是分钟
		java.util.Date date = sdf.parse(dstr);
		String s = String.valueOf(date.getTime());
		return s;
	}

	public static void main(String[] args) {
		String srt = "2015-12-30 12:20";
		Date date = DateUtils.toDate(srt);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		System.out.println(sdf.format(date));
	}
}
