package com.dachen.util;

import java.text.NumberFormat;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public final class StringUtil extends StringUtils{

	private static final char[] charArray = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static String getExt(String filename) {
		return filename.substring(filename.lastIndexOf('.'));
	}

	public static boolean isEmpty(String s) {
		return isNullOrEmpty(s);
	}

	public static boolean isNullOrEmpty(String s) {
		return null == s || 0 == s.trim().length();
	}

	public static String randomCode() {
		return "" + (new Random().nextInt(899999) + 100000);
	}
	public static String random4Code() {
		return "" + (new Random().nextInt(8999) + 1000);
	}

	public static String randomPassword() {
		return randomString(6);
	}

	public static String randomString(int length) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			int index = new Random().nextInt(36);
			sb.append(charArray[index]);
		}

		return sb.toString();
	}

	public static String randomUUID() {
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString().replace("-", "");

		return uuidStr;
	}

	public static String getFormatName(String fileName) {
		int index = fileName.lastIndexOf('.');

		return -1 == index ? "jpg" : fileName.substring(index + 1);
	}

	public static String getFormatName(MultipartFile file) {
		return getFormatName(file.getName());
	}

	public static String randomFileName(MultipartFile file) {
		return randomUUID() + "." + getFormatName(file.getName());
	}

	public static String format(long number,int length){
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(length);
		nf.setMinimumIntegerDigits(length);
		nf.setGroupingUsed(false);
		return nf.format(number);
	}
	
	public static String returnSex(short sex) {
		switch (sex) {
		case 1:
			return "男";
		case 2:
			return "女";
		case 3: 
			return "保密";	
		default:
			return " ";
		}
	}
	
	public static String returnPackTitle(Integer packType,Long packPrice) {
		
//		if(packPrice!=null && packPrice ==0) {
//			return "免费咨询";
//		}
		
		switch (packType) {
		case 0:
			return "免费咨询";
		case 1:
			return "图文咨询";
		case 2: 
			return "电话咨询";	
		case 9: 
			return "名医面对面";
		default:
			return "未知";
		}
	}
	
	public static String returnPackName(Integer packType) {
		switch (packType) {
		case 0: 
			return "患者报到";
		case 1:
			return "图文套餐";
		case 2: 
			return "电话套餐";	
		default:
			return "未知";
		}
	}
	
	public static String returnSheduleName(Integer itemType) {
		switch (itemType) {
		case 7:
			return "随访计划日程";
		case 2: 
			return "关怀计划日程";	
		default:
			return "其他";
		}
	}
	
	public static String returnPayTypeName(Integer payType) {
		switch (payType) {
		case 1:
			return "微信支付";
		case 2: 
			return "支付宝支付";	
		default:
			return "未知";
		}
	}
	public static String atKey(String telephone, Integer userType) {
        return String.format("uk_%1$s_%2$d", telephone, userType);
    }
	
	public static String atValue(String orderRecharegeNo, String refundRrice,String refundReason) {
        return String.format("%1$s^%2$s^%3$s", orderRecharegeNo, refundRrice,refundReason);
	}
	
	public static boolean isMobiPhoneNum(String telNum){
		String regex = "^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(telNum);
        return m.matches();
	}
	
	public static void main(String... strings) {
		System.out.println(format(33,4));
		System.out.println(returnPackTitle(1,Long.valueOf(0)));
		System.out.println(atKey("18938856957",2));
		System.out.println(atValue("18938856957","11","111"));
	}
}
