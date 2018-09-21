package com.dachen.line.stat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class RelativeDateFormat {

	private static final long ONE_HOUR = 3600000L;
	private static final String ONE_MINUTE_AGO = "分钟前";
	private static final String ONE_HOUR_AGO = "小时前";
	private static final String ONE_DAY_AGO = "天前";

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = format.parse("2016-1-3 20:11:00");
		// System.out.println(format(date));

		System.out.println(date.getTime());
		System.out.println(formatOrderTime(date.getTime()));
		// System.out.println(format.format(new Date(1450867535000l)));
		// System.out.println(format.format(new Date(1450867535000l)));
	}

	public static String format(long time) {
		long delta = new Date().getTime() - time;
		if (delta < 1L * ONE_HOUR) {
			long minutes = toMinutes(delta);
			return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
		}
		if (delta < 24L * ONE_HOUR) {
			long days = toHours(delta);
			return (days <= 0 ? 1 : days) + ONE_HOUR_AGO;
		} else {
			long days = toDays(delta);
			return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
		}

	}

	public static String formatOrderTime(long time) {
		String result = null;
		long delta = new Date().getTime() - time;
		if (delta < 1L * ONE_HOUR) {
			long minutes = toMinutes(delta);
			result = (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
			return result;
		}
		if (delta < 24L * ONE_HOUR) {
			long days = toHours(delta);
			result = (days <= 0 ? 1 : days) + ONE_HOUR_AGO;
			return result;
		} else {
			result = DateUtils.formatDate2Str(time,
					DateUtils.FORMAT_PATTERN_MM_DD_HH_MM);
			return result;
		}
	}

	private static long toSeconds(long date) {
		return date / 1000L;
	}

	private static long toMinutes(long date) {
		return toSeconds(date) / 60L;
	}

	private static long toHours(long date) {
		return toMinutes(date) / 60L;
	}

	private static long toDays(long date) {
		return toHours(date) / 24L;
	}

	private static long toMonths(long date) {
		return toDays(date) / 30L;
	}

}