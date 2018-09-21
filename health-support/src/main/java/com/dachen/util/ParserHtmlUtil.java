package com.dachen.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtmlUtil {

	public static void main(String[] args) {
		String str = "<p style='border: 0px; margin-top: 0px; margin-bottom: 0px; padding-top: 26px; font-size: 14px; color: rgb(0, 0, 0); font-family: 宋体; line-height: 26px; white-space: normal;'><span style='border: 0px; margin: 0px; padding: 0px;'>健步走是世界上最好的运动，对糖尿病患者而言是最简单、最有效、成本最低的有氧运动，同样有利于降血糖。健步走和平时的散步不同，首先要达到一定强度，而且姿势特别重要。</span></p><p style='border: 0px; margin-top: 0px; margin-bottom: 0px; padding-top: 26px; font-size: 14px; color: rgb(0, 0, 0); font-family: 宋体; line-height: 26px; white-space: normal;'>　　<span style='border: 0px; margin: 0px; padding: 0px;'><strong style='border: 0px; margin: 0px; padding: 0px;'>健步走要分三步走</strong></span></p><p style='border: 0px; margin-top: 0px; margin-bottom: 0px; padding-top: 26px; font-size: 14px; color: rgb(0, 0, 0); font-family: 宋体; line-height: 26px; white-space: normal;'>";
		String result = delHTMLTag(str);
		System.out.println(result);
		str ="asdf&ensp;+&emsp;+&nbsp;weqfr&ewwettlt;+&gt;+&amp;+&quot;+&copy;+&reg;+&times;+&divide;\n";
		System.err.println(delTransferredCode(str));
	}
	
    public static String delHTMLTag(String htmlStr){ 
         String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
         String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
         String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
         
         Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
         Matcher m_script=p_script.matcher(htmlStr); 
         htmlStr=m_script.replaceAll(""); //过滤script标签 
         
         Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
         Matcher m_style=p_style.matcher(htmlStr); 
         htmlStr=m_style.replaceAll(""); //过滤style标签 
         
         Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
         Matcher m_html=p_html.matcher(htmlStr); 
         htmlStr=m_html.replaceAll(""); //过滤html标签 

        return htmlStr.trim(); //返回文本字符串 
     } 
    
    public static String delTransferredCode(String htmlStr){
//    	String[] codes = {"&ensp;","&emsp;","&nbsp;","&lt;","&gt;","&amp;","&quot;","&copy;","&reg;","&times;","&divide;"};
    	 String regEx_code = "&([a-zA-Z]){2,6};";
    	 Pattern p_script=Pattern.compile(regEx_code,Pattern.CASE_INSENSITIVE); 
         Matcher m_script=p_script.matcher(htmlStr); 
         htmlStr=m_script.replaceAll(""); 
//         htmlStr = htmlStr.replaceAll("\n", "<br>");

    	return htmlStr.trim();
    }
    
    /**
     * 将正文中的\n修复为<br>
     * @param htmlStr
     * @return
     */
    public static String changeLineTag(String htmlStr) {
    	htmlStr = htmlStr.replaceAll("\n", "<br>");
    	return htmlStr.trim();
    }

}
