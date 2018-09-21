package com.dachen.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class DateUtil {

    public static final SimpleDateFormat FORMAT_YYYY_MM;

    public static final SimpleDateFormat FORMAT_YYYY_MM_DD;

    public static final SimpleDateFormat FORMAT_YYYY_MM_DD_HH_MM_SS;
    
    public static final SimpleDateFormat  FORMAT_YYYY_MM_DD_HH_MM;
    
    public static final SimpleDateFormat  FORMAT_HH_MM;
    
   public static final SimpleDateFormat  FORMAT_MM_DD_HH_MM;
    
    public static final SimpleDateFormat  FORMAT_YYYY;

    public static final Pattern PATTERN_YYYY_MM;

    public static final Pattern PATTERN_YYYY_MM_DD;

    public static final Pattern PATTERN_YYYY_MM_DD_HH_MM_SS;
    
    public static final Long daymillSeconds = 24 * 60 * 60 * 1000l;
    
    public static final Long minute30millSeconds =  30 * 60 * 1000l;
    
    public static final Long weekmillSeconds =  7 * 24 * 60 * 60 * 1000l;
    
    public static final Long halfmonthmillSeconds = 15 * 24 * 60 * 60 * 1000l;
    
    public static final Long week12millSeconds =  12 * 7 * 24 * 60 * 60 * 1000l;

    static {
        FORMAT_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FORMAT_YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
        FORMAT_YYYY_MM = new SimpleDateFormat("yyyy-MM");
        FORMAT_HH_MM = new SimpleDateFormat("HH:mm");
        FORMAT_MM_DD_HH_MM=new SimpleDateFormat("MM-dd HH:mm");
        FORMAT_YYYY=new SimpleDateFormat("yyyy");
        PATTERN_YYYY_MM_DD_HH_MM_SS = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}");
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
    
    public static String formatDate2Str3(Date date) {
        return formatDate2Str(date, FORMAT_YYYY_MM_DD);
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
    public static String formatDate2Str(Long milliseconds) {
    	return formatDate2Str(milliseconds, FORMAT_YYYY_MM_DD_HH_MM);
    }
    
    public static String formatDate2Str(Long milliseconds, SimpleDateFormat format) {
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
    
    public static String getDateByLong(Long times) {
    	
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(times);
    	
    	return formatDate2Str(c.getTime(),null);
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
     * @param d1
     * @param d2
     * @return d1.yyyy-d2.yyyy
     * @author limiaomiao
     * @date 2015年7月8日
     */
    public static int yearDiff(Date d1,Date d2){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
    	int y1=Integer.parseInt(sdf.format(d1));
    	int y2=Integer.parseInt(sdf.format(d2));
    	return y1-y2;
    	
    }
    
    /**
     * </p>获取一周第一天</p>
     * @param date
     * @return
     * @author fanp
     * @date 2015年8月12日
     */
    public static Integer getFirstOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        if(date!=null){
            cal.setTime(date);
        }
        
        int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;
        cal.add(Calendar.DATE, -daysSinceMonday);
        
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.YEAR));
        sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1,2));
        sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH),2));
        return Integer.parseInt(sb.toString());
    }
    
    /**
     * </p>获取一周最后一天</p>
     * @param date
     * @return
     * @author fanp
     * @date 2015年8月12日
     */
    public static Integer getLastOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        if(date!=null){
            cal.setTime(date);
        }
        
        int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;

        cal.add(Calendar.DATE, 6-daysSinceMonday);
        
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.YEAR));
        sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1,2));
        sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH),2));
        return Integer.parseInt(sb.toString());
    }
    
    /**
     * </p>获取一周所有天日期"YYYYMMDD"数字表示</p>
     * @param date
     * @return
     * @author fanp
     * @date 2015年8月12日
     */
    public static Integer[] getWeekDays(Date date){
        Calendar cal = Calendar.getInstance();
        if(date!=null){
            cal.setTime(date);
        }
        int daysSinceMonday = (7 + cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7;
        Integer[] days = new Integer[7];
        
        
        for(int i=0;i<7;i++){
            if(i==0){
                daysSinceMonday = -daysSinceMonday;
            }else{
                daysSinceMonday = 1;
            }
            
            cal.add(Calendar.DATE, daysSinceMonday);
            
            StringBuilder sb = new StringBuilder();
            sb.append(cal.get(Calendar.YEAR));
            sb.append(StringUtil.format(cal.get(Calendar.MONTH) + 1,2));
            sb.append(StringUtil.format(cal.get(Calendar.DAY_OF_MONTH),2));
            
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
        if(date!=null){
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
        if(date!=null){
            cal.setTime(date);
        }
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    
    public static Long getDayBegin(long millis) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(millis);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * </p>数字类型日期转换成Date</p>
     * @param date yyyyMMdd yyyyMM yyyy
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    public static Date intToDate(Integer date){
        if(date == null){
            return null;
        }
        if(String.valueOf(date).length()==8){
            Calendar cal = Calendar.getInstance();
            cal.set(date/10000, (date%10000)/100-1, date%100,0,0,0);
            return cal.getTime();
        }
        if(String.valueOf(date).length()==6){
            Calendar cal = Calendar.getInstance();
            cal.set(date/100, date%100-1, 1,0,0,0);
            return cal.getTime();     
        }
        if(String.valueOf(date).length()==4){
            Calendar cal = Calendar.getInstance();
            cal.set(date, 0, 1,0,0,0);
            return cal.getTime();
        }
        return null;
    }
    
    public static String getMonthFrastDay(Date startDate) {
    	Calendar c = Calendar.getInstance(); 
		c.setTime(startDate);
		return c.getTimeInMillis()+"";
    }
    
    public static Date getLassDay() {
    	Calendar c = Calendar.getInstance(); 
    	c.setTime(new Date());
    	c.set(Calendar.DATE,-1);
    	return c.getTime();
    }
    
    public static String getMonthLastDay(Date endDate) {
    	Calendar c = Calendar.getInstance(); 
		c.setTime(endDate);
		return c.getTimeInMillis()+c.getTimeInMillis()+(24*60*1000*60)+"";
    }
    
    /**
	 * 年龄计算
     * @author 			李淼淼
     * @date 2015年8月17日
     */
    public static int calcAge(Long birthday) {
    	if (birthday == null) {
			return 0;
		}
//		return calcAge(birthday, Calendar.YEAR);
    	
    	return countMonths(birthday)/12;
	}
   
    /**
	 * 月份计算
     * @author 			李淼淼
     * @date 2015年8月17日
     */
    public static int calcMonth(Long birthday) {
		if (birthday == null) {
			return 0;
		}
		return countMonths(birthday);
	}

    public static int calcAge(Long birthday, int type) {
    	Calendar cal=Calendar.getInstance();
		int now=cal.get(type);
		cal.setTime(new Date(birthday));
		int bir=cal.get(type);
		return now-bir;
    }
    
    
    public static Date getCurrentMonthStart(){
    	Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		return 	cal.getTime();
    }
    
    /**
     * 计算年龄
     * @param date1
     * @return
     * add by  liwei  20160217
     * 
     */
    public static int countMonths(long date1){
    	
    	long date2 = new Date().getTime();
//        System.out.println(date2);
        Calendar c1=Calendar.getInstance();
        Calendar c2=Calendar.getInstance();
        c1.setTimeInMillis(date1);
        c2.setTimeInMillis(date2);
        int year =c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR);
        //开始日期若小月结束日期
        if(year<0){
            year=-year;
            return year*12+c1.get(Calendar.MONTH)-c2.get(Calendar.MONTH);
        }
        int monthNumber = year*12+c2.get(Calendar.MONTH)-c1.get(Calendar.MONTH);
       
        return monthNumber;
    }
    public static void main(String[] args) throws ParseException {

//    	
//		Date d1=new Date();
//		Date d2=new Date();
//		System.out.println(yearDiff(d1, d2));
//		
//		Calendar c = Calendar.getInstance(); 
//		//c.setTime(null);
//		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2015-09-22");
//		
//		Date d =  getLastDayOfMonth(date);
//		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(d));
		/*int dayForWeek = 0;
		if(c.get(Calendar.DAY_OF_WEEK) == 1){
		dayForWeek = 7;
		}else{
		dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		
		System.out.println(dayForWeek);
		
		System.out.println(c.getTimeInMillis()+(24*60*1000*60));
		//Date date = new Date(1442852640000L);
		//System.out.println(c.ge.getWeekYear());
		System.out.println(DateUtil.getWeekDays(date)[0]);
		System.out.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));*/
    	//System.out.println(toCommunityTime(1471745470000L));
	}
    
    public static int daysBetween(String smdate,String bdate) throws ParseException{
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();  
        cal.setTime(sdf.parse(smdate));  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(sdf.parse(bdate));  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);
          
       return Integer.parseInt(String.valueOf(between_days));   
    }
    /**
     * sfzq格式：0 Day，2 day；0 Week
     * @param sfzq
     * @return
     */
    public static int getDayNumBySfzq(String sfzq)
    {
    	int dayNum=0;
    	if(sfzq.split(" ")[1].equals("Day"))
    	{
    		dayNum=Integer.valueOf(sfzq.split(" ")[0]);
    	}
    	else if(sfzq.split(" ")[1].equals("Week"))
    	{
    		dayNum=Integer.valueOf(sfzq.split(" ")[0])*7;
    	}
    	else if(sfzq.split(" ")[1].equals("Month"))
    	{
    		dayNum=Integer.valueOf(sfzq.split(" ")[0])*30;
    	}
    	else if(sfzq.split(" ")[1].equals("Year"))
    	{
    		dayNum=Integer.valueOf(sfzq.split(" ")[0])*365;
    	}
    	return dayNum;
    }
    
    public static int getTimeNumByCfys(String cfys)
    {	
    	if(StringUtil.isBlank(cfys)) {
    		return 0;
    	}
    	
    	int timeNum=0;
    	if(cfys.split(" ")[1].equals("Second"))
    	{
    		timeNum=Integer.valueOf(cfys.split(" ")[0]);
    	}else if(cfys.split(" ")[1].equals("Minute")) {
    		timeNum=Integer.valueOf(cfys.split(" ")[0])*60;
    	}
    	return timeNum;
    }
    
    
    public static Date getAddDate(Date date,Integer addNum)
    {
    	 Calendar cal = Calendar.getInstance();
    	 cal.setTime(date);
    	 cal.add(java.util.Calendar.DAY_OF_MONTH, addNum);  
    	 return cal.getTime();
    }

	public static String getMinuteTimeByLong(long timestamp) {
		Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(timestamp);
    	return formatDate2Str(c.getTime(),FORMAT_HH_MM);
	}
	
	 public static String getMinuteDateByLong(Long times) {
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(times);
    	return formatDate2Str2(c.getTime());
     }
	 
	 public static int getDayOfLongTime(Long time){
		 Calendar cal = Calendar.getInstance();
    	 cal.setTime(new Date(time));
    	 return cal.get(Calendar.DAY_OF_MONTH);
	 }
	 
	 public static Long parseStringToTimestamp(String dateStr){
		 long dataPoint = 0;
		 try {
			 if(StringUtils.isNotBlank(dateStr)){
				 dataPoint = FORMAT_YYYY_MM_DD_HH_MM.parse(dateStr).getTime();
			 }
		 } catch (ParseException e) {
		 }
		 return dataPoint;
	 }
	 
	 public static Long parseTimeStringToToDayTime(String timeString){
		 try{
			 long t = FORMAT_HH_MM.parse(timeString).getTime();
			 	  t += getDayBegin(System.currentTimeMillis());
			 	  t += 28800000; // 因为 1970-1-1 0:0:0 的时间戳是-28800000
			 return t;
		 }catch(Exception e){
		 }
		 return null;
	 }
	 
	 
		/**
		 * 将时间戳 转换为 yyyy-MM-dd 格式的字符串
		 * @author wangqiao
		 * @date 2016年6月4日
		 * @param timeMillis
		 * @return
		 */
		public static  String formatDate(Long timeMillis){
			return FORMAT_YYYY_MM_DD.format(timeMillis);
		}
		
		/**
		 * 计算 两个日期的天数 差距
		 * @author wangqiao
		 * @date 2016年6月4日
		 * @param startDate
		 * @param endDate
		 * @return
		 * @throws ParseException 
		 */
		public static long getDayDifference(Long startDate , Long endDate) {
			try {
				Date  startDateFormat=FORMAT_YYYY_MM_DD.parse( formatDate(startDate));
				Date  endDateFormat=FORMAT_YYYY_MM_DD.parse( formatDate(endDate));
				long day=(endDateFormat.getTime()-startDateFormat.getTime())/(24*60*60*1000);
				return day;
			} catch (ParseException e) {
				return -1l;
			}
		}

		public static Map<String,Long> getDayStartAndEndTime(Long date) {
			Map<String,Long> map = new HashMap<String,Long>();
			try{
				long start = FORMAT_YYYY_MM_DD.parse( formatDate(date)).getTime();
				long end = start + 86400000;
				map.put("start", start);
				map.put("end", end);
			}catch (ParseException e) {
			}
			return map;
		}
		
		/**
		 * 根据毫秒数返回对应的中文日期
		 * @param time
		 * @param dayOrWeek(day-返回天、week-返回周)
		 * @return
		 */
		public static String getDayStrByTime(Long time,String dayOrWeek){
			String currentTime = DateUtil.formatDate2Str(time, DateUtil.FORMAT_YYYY_MM_DD_HH_MM);
			int currentIdx = currentTime.indexOf(' ');
			if("day".equals(dayOrWeek)){
				return currentTime.substring(0, currentIdx);
			}else if("week".equals(dayOrWeek)){
				return DateUtil.toWeek(time);
			}else{
				return currentTime.substring(currentIdx + 1);
			}
		}
		
		/**
	     * 根据时间获取星期几，"星期一","星期二"
	     * @param times
	     * @return
	     */
	    public static String toWeek(long times){
			Date date = new Date(times);
			String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
				w = 0;
			return weekDays[w];
	    }
	    
	    /**
	     * 医生社区时间转换
	     * @author liming
	     */
	    public static String toCommunityTime(long time){
	    	//先比较是否是当年
	    	if(!formatDate2Str(System.currentTimeMillis(), FORMAT_YYYY).equals(formatDate2Str(time, FORMAT_YYYY))){
	    		return formatDate2Str(time,FORMAT_YYYY_MM_DD_HH_MM);
	    	}
	    	if(formatDate2Str(System.currentTimeMillis(), FORMAT_YYYY_MM_DD).equals(formatDate2Str(time, FORMAT_YYYY_MM_DD))){
	    		return formatDate2Str(time,FORMAT_HH_MM);
	    	}else if(formatDate2Str(System.currentTimeMillis(), FORMAT_YYYY_MM_DD).equals(formatDate2Str(time+24*60*60*1000, FORMAT_YYYY_MM_DD))){
	    		return "昨天 "+formatDate2Str(time,FORMAT_HH_MM);
	    	}else{
	    		return formatDate2Str(time,FORMAT_MM_DD_HH_MM);
	    	}

	    }
	   
}
