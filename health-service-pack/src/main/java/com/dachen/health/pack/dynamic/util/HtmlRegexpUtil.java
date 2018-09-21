package com.dachen.health.pack.dynamic.util;
import java.util.regex.Matcher;   
import java.util.regex.Pattern;   

import com.dachen.util.StringUtils;
  
/**  
 * <p>  
 * Title: HTML相关的正则表达式工具类  
 * </p>  
 * <p>  
 * Description: 包括过滤HTML标记，转换HTML标记，替换特定HTML标记  
 * </p>  
 * <p>  
 * Copyright: Copyright (c) 2006  
 * </p>  
 *   
 * @author liwei  
 * @version 1.0  
 * @createtime 2006-10-16  
 */  
  
public class HtmlRegexpUtil {   
    private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签   
  
    private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>"; // 找出IMG标签   
  
    private final static String regxpForImaTagSrcAttrib = "src=\"([^\"]+)\""; // 找出IMG标签的SRC属性   
    
    public static void main(String[] args) {
    	HtmlRegexpUtil  s = new HtmlRegexpUtil();
		String str ="<p><strong>测试标</strong>签测试标签测试标签测试标签<img src=\"http://resource.dev.file.dachentech.com.cn/o_1ap7ntvmg19dr1as91f8jd4t156ll\" _src=\"http://resource.dev.file.dachentech.com.cn/o_1ap7ntvmg19dr1as91f8jd4t156ll\"/></p>";
		System.out.println(s.filterHtml(str) );
		
		System.out.println("fiterHtmlTag="+fiterHtmlTag(str, "img"));
		
		System.out.println(getImgSrcValue(fiterHtmlTag(str, "img")));
		
		System.out.println(filterImgTagExists(str, "img"));
		
//		System.out.println(checkTagExists(str, "img"));
	}
    /**  
     *   
     * 基本功能：替换标记以正常显示  
     * <p>  
     *   
     * @param input  
     * @return String  
     */  
    private String replaceTag(String input) {   
        if (!hasSpecialChars(input)) {   
            return input;   
        }   
        StringBuffer filtered = new StringBuffer(input.length());   
        char c;   
        for (int i = 0; i <= input.length() - 1; i++) {   
            c = input.charAt(i);   
            switch (c) {   
            case '<':   
                filtered.append("&lt;");   
                break;   
            case '>':   
                filtered.append("&gt;");   
                break;   
            case '"':   
                filtered.append("&quot;");   
                break;   
            case '&':   
                filtered.append("&amp;");   
                break;   
            default:   
                filtered.append(c);   
            }   
  
        }   
        return (filtered.toString());   
    }   
  
    /**  
     *   
     * 基本功能：判断标记是否存在  
     * <p>  
     *   
     * @param input  
     * @return boolean  
     */  
    private boolean hasSpecialChars(String input) {   
        boolean flag = false;   
        if ((input != null) && (input.length() > 0)) {   
            char c;   
            for (int i = 0; i <= input.length() - 1; i++) {   
                c = input.charAt(i);   
                switch (c) {   
                case '>':   
                    flag = true;   
                    break;   
                case '<':   
                    flag = true;   
                    break;   
                case '"':   
                    flag = true;   
                    break;   
                case '&':   
                    flag = true;   
                    break;   
                }   
            }   
        }   
        return flag;   
    }   
  
    /**  
     *   
     * 基本功能：过滤所有以"<"开头以">"结尾的标签  
     * <p>  
     *   
     * @param str  
     * @return String  
     */  
    public static String filterHtml(String str) {   
        Pattern pattern = Pattern.compile(regxpForHtml);   
        Matcher matcher = pattern.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result1 = matcher.find();   
        while (result1) {   
            matcher.appendReplacement(sb, "");   
            result1 = matcher.find();   
        }   
        matcher.appendTail(sb);   
        return sb.toString();   
    }   
  
    /**  
     *   
     * 基本功能：过滤指定标签  
     * <p>  
     *   
     * @param str  
     * @param tag  
     *            指定标签  
     * @return String  
     */  
    public static String fiterHtmlTag(String str, String tag) {   
        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";   
        Pattern pattern = Pattern.compile(regxp);   
        Matcher matcher = pattern.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result = matcher.find();   
        while (result) {   
            matcher.appendReplacement(sb, "");   
            result = matcher.find();   
        }   
        matcher.appendTail(sb);   
        return sb.toString();   
    }
    
    /**
     * 判断是否存在某个标签
     * @param str
     * @param tag
     * @return
     */
    public static boolean checkTagExists(String str, String tag)
    {
    	   String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";   
           Pattern pattern = Pattern.compile(regxp);   
           Matcher matcher = pattern.matcher(str);   
           boolean result = matcher.find();  
           while(result)
           {	  
        	   System.out.println(matcher.group());
        	   result = matcher.find();
           }
           return result;
    }
    
    public static String getImgSrcValue(String str)
    {
    	String srcValue ="";
           Pattern pattern = Pattern.compile(regxpForImaTagSrcAttrib);   
           Matcher matcher = pattern.matcher(str);   
           boolean result = matcher.find();  
           while(result)
           {	  
        	   srcValue =matcher.group();
        	   break;
           }
           return srcValue;
    }
    /**
     * 讲字符串中的图片标签用 src替换
     * @param str
     * @param tag
     * @return
     */
    public static String filterImgTagExists(String str, String tag)
    {
    	 String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";   
         Pattern pattern = Pattern.compile(regxp);   
         StringBuffer sb = new StringBuffer();   

         Matcher matcher = pattern.matcher(str);   
         boolean result = matcher.find();  
         while(result)
         {	  
           String image =matcher.group();
           if(StringUtils.isNotEmpty(image))
           {	   
        	   String imgSrc= getImgSrcValue(image);
        	   if(StringUtils.isNotEmpty(imgSrc))
        	   {		   
                   matcher.appendReplacement(sb, imgSrc);   
        	   }
           }
      	   result = matcher.find();
         }
         matcher.appendTail(sb);   
         return sb.toString();
    }
    
    
  
    /**  
     *   
     * 基本功能：替换指定的标签  
     * <p>  
     *   
     * @param str  
     * @param beforeTag  
     *            要替换的标签  
     * @param tagAttrib  
     *            要替换的标签属性值  
     * @param startTag  
     *            新标签开始标记  
     * @param endTag  
     *            新标签结束标记  
     * @return String  
     * @如：替换img标签的src属性值为[img]属性值[/img]  
     */  
    public static String replaceHtmlTag(String str, String beforeTag,   
            String tagAttrib, String startTag, String endTag) {   
        String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";   
        String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";   
        Pattern patternForTag = Pattern.compile(regxpForTag);   
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);   
        Matcher matcherForTag = patternForTag.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result = matcherForTag.find();   
        while (result) {   
            StringBuffer sbreplace = new StringBuffer();   
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag   
                    .group(1));   
            if (matcherForAttrib.find()) {   
                matcherForAttrib.appendReplacement(sbreplace, startTag   
                        + matcherForAttrib.group(1) + endTag);   
            }   
            matcherForTag.appendReplacement(sb, sbreplace.toString());   
            result = matcherForTag.find();   
        }   
        matcherForTag.appendTail(sb);   
        return sb.toString();   
    }   
}