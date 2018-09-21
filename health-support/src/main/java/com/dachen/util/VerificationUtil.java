package com.dachen.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: xuhuanjie
 * Date: 2018-01-08
 * Time: 18:06
 * Description:
 */
public class VerificationUtil {
    /**
     * 验证日期格式是否正确
     * @author wangqiao
     * @date 2016年5月30日
     * @param str
     * @param dateFormat
     * @return
     */
    public static boolean isValidDate(String str , String dateFormat) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；"yyyy/MM/dd HH:mm"
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 校验是否是手机号码
     *
     * @author wangqiao
     * @date 2016年2月29日
     * @param str
     * @return
     */
    public static boolean checkMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile(CheckRegexEnum.手机号码.getRegex()); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 校验是否是固话
     *
     * @author xuhuanjie
     * @date 2018年1月8日
     * @param str
     * @return
     */
    public static boolean checkTelephone(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile(CheckRegexEnum.固话.getRegex()); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 通用规则校验
     *
     * @author wangqiao
     * @date 2016年2月29日
     * @param str
     *            被校验的字符串
     * @param checkRegex
     *            校验规则正则表达式（可以从枚举中读取 @CheckRegexEnum ）
     * @return
     */
    public static boolean checkCommon(String str, String checkRegex) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile(checkRegex); //
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 参数校验 正则表达式
     *
     * @author wangqiao
     * @date 2016年2月29日
     */
    public enum CheckRegexEnum {
        整数("^-?[1-9]\\d*$", "{0}必须为整数。"),
        正整数("^[1-9]\\d*$", "{0}必须为正整数。"),
        负整数("^-[1-9]\\d*$", "{0}必须为负整数。"),
        数字("^([+-]?)\\d*\\.?\\d+$", "{0}必须为数字。"),
        正数("^[1-9]\\d*|0$", "{0}必须为正整数或0。"),
        负数("^-[1-9]\\d*|0$", "{0}必须为负整数或0。"),
        浮点数("^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$", "{0}必须为浮点数。"),
        正浮点数("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$", "{0}必须为正浮点数。"),
        负浮点数("^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$", "{0}必须为负浮点数。"),
        非负浮点数("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$", "{0}必须为正浮点数或0。"),
        非正浮点数("^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$", "{0}必须为负浮点数或0。"),
        ACSII字符("^[\\x00-\\xFF]+$", "{0}必须为ACSII字符。"),
        中文字符("^[\\u4e00-\\u9fa5]+$", "{0}必须为中文字符。"),
        颜色("^[a-fA-F0-9]{6}$", "{0}不是有效地颜色值。"),
        日期("^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$", "{0}格式应为yyyy-MM-dd。"),
        邮箱("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", "{0}格式不正确。"),
        // 手机号码("^(13[0-9]|14[57]|15[0-9]|17[0678]|18[0-9])[0-9]{8}$", "请输入11位手机号码。"),
        手机号码("^1\\d{10}", "请输入11位手机号码。"),
        固话("^0(\\d{2}|\\d{3})-?\\d{7,8}", "请输入区号+电话号码"),
        字母("^[A-Za-z]+$", "{0}必须为字母。"),
        小写字母("^[a-z]+$", "{0}必须为小写字母。"),
        大写字母("^[A-Z]+$", "{0}必须为大写字母。"),
        图片("(.*)\\.((?i)jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", "{0}不是图片。"),
        压缩文件("(.*)\\.(rar|zip|7zip|tgz|7z|tar.gz)$", "{0}不是有效的压缩文件。"),
        邮编("^\\d{5}$", "邮政编码格式有误。"),
        URL("^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$", "{0}不是有效地URL"),
        身份证号码("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}[\\dxX]$", "{0}格式有误。"),
        金额("^(?!0+(?:\\.0+)?$)(?:[1-9]\\d*|0)(?:\\.\\d{1,2})?$", "{0}格式应为#、#.#或#.##"),
        银行卡号("^\\d{16,19}$", "{0}格式不正确。"),
        非空("^\\S+$", "{0}不能为空。"),
        字符("^[\\S]{1,20}$", "{0}必须为1~20位字符。");

        private CheckRegexEnum() {
        }

        private CheckRegexEnum(String regex, String prompt) {
            this.regex = regex;
            this.prompt = prompt;
        }

        /*
         * 正则表达式
         */
        private String regex;

        /*
         * 错误信息描述
         */
        private String prompt;

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

    }

    public static void main(String[] args) {
        String a = "13725518367";
        boolean b = CheckUtils.checkMobile(a);
        System.out.println(b);
    }

}
