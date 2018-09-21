package com.dachen.util.regex;  

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;


/**
 * ProjectName： sys-framework<br>
 * ClassName： RegexUtils<br>
 * Description： 正则表达式校验工具类<br>
 * @author yujl
 * @crateTime 2014年12月26日
 * @version 1.0.0
 */
public class RegexUtil {
	/**
	 * 
	 * </p>验证单个表单元素</p>
	 * @param regexInfo	验证信息 regexEnum：验证枚举 field：验证的表单元素描述 value：表单元素值
	 * @return
	 * @author yujl
	 * @date 2014年12月26日
	 */
	public static Result check(RegexInfo regexInfo){
		Result result = new Result(false);
		
		if (regexInfo == null || regexInfo.getRegexEnum() == null) {
			result.setPrompt("验证信息或验证规则不存在，验证失败！");
			return result;
		}
		
		if (regexInfo.getValue() == null) {
			result.setPrompt(MessageFormat.format(regexInfo.getRegexEnum().getPrompt(), regexInfo.getField()));
			return result;
		}
		
		result.setIsSuccess(Pattern.matches(regexInfo.getRegexEnum().getRegex(), regexInfo.getValue()));
		
		if (!result.getIsSuccess()) {
			result.setPrompt(MessageFormat.format(regexInfo.getRegexEnum().getPrompt(), regexInfo.getField()));
		}
		
		return result;
	}
	
	/**
	 * 
	 * </p>验证多个表单元素，如果验证失败则返回第一个验证失败的提示信息</p>
	 * @param infos
	 * @return
	 * @author yujl
	 * @date 2014年12月26日
	 */
	public static Result check(List<RegexInfo> infos){
		Result result = new Result(false);
		
		if (CollectionUtils.isEmpty(infos)) {
			result.setPrompt("没有需要验证的字段！");
			return result;
		}
		
		for (int i = 0; i < infos.size(); i++) {
			result = check(infos.get(i));
			
			if (!result.getIsSuccess()) {
				return result;
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * </p>验证多个表单元素，并返回所有验证失败的表单元素提示信息
	 *   如果全部验证成功，则返回结果集为空</p>
	 * @param infos
	 * @return
	 * @author yujl
	 * @date 2014年12月29日
	 */
	public static List<Result> checkAll(List<RegexInfo> infos){
		List<Result> list = new ArrayList<Result>();
		
		if (CollectionUtils.isEmpty(infos)) {
			return list;
		}
		
		Result result = null;
		
		for (int i = 0; i < infos.size(); i++) {
			result = check(infos.get(i));
			
			if (!result.getIsSuccess()) {
				list.add(result);
			}
		}
		
		return list;
	}
	
	
	public static void main(String[] args) {
		
		List<RegexInfo> list = new ArrayList<RegexInfo>();
		RegexInfo info1 = new RegexInfo(RegexEnum.手机号码,"手机号码", "11161805942");
//		RegexInfo info2 = new RegexInfo(RegexEnum.借款金额,"借款金额", "10000000");
		list.add(info1);
//		list.add(info2);
		
		Result result = check(list);
		
		System.out.println(result.getIsSuccess() + ":" + result.getPrompt());
		
	}
}