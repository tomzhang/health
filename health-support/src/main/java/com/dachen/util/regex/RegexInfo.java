package com.dachen.util.regex;

/**
 * 
 * ProjectName： sys-framework<br>
 * ClassName： RegexInfo<br>
 * Description： 验证信息封装<br>
 * 
 * @author yujl
 * @crateTime 2014年12月29日
 * @version 1.0.0
 */
public class RegexInfo {
    // 正则枚举
    private RegexEnum regexEnum;

    // 字段描述
    private String field;

    // 字段值
    private String value;

    public RegexInfo() {
    }

    public RegexInfo(RegexEnum regexEnum, String field, String value) {
        this.regexEnum = regexEnum;
        this.field = field;
        this.value = value;
    }

    public RegexEnum getRegexEnum() {
        return regexEnum;
    }

    public void setRegexEnum(RegexEnum regexEnum) {
        this.regexEnum = regexEnum;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
