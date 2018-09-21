package com.dachen.commons.taglibs;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SamplesUtils {

	public static String format(Long milliseconds, String pattern) {
		Date date = new Date(milliseconds);
		return new SimpleDateFormat(pattern).format(date);
	}

}
