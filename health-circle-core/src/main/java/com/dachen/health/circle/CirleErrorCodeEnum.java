package com.dachen.health.circle;

import java.util.HashMap;
import java.util.Map;

/**
 * 医生圈异常错误码
 * Created By lim
 * Date: 2017/6/27
 * Time: 14:02
 */
public enum CirleErrorCodeEnum {
    GroupTrendCommentNoExistent(86000200, "动态评论内容已删除"),
    GroupTrendNoExistent(86000100, "动态不存在"),
    GroupNoExistent(86000000, "科室解散");


    private Integer id;
    private String title;

    CirleErrorCodeEnum(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    private static final Map<Integer, CirleErrorCodeEnum> mapping;
    static {
        CirleErrorCodeEnum[] types = CirleErrorCodeEnum.values();
        mapping = new HashMap<>(types.length);
        for (CirleErrorCodeEnum type:types) {
            mapping.put(type.getId(), type);
        }
    }

    public static CirleErrorCodeEnum eval(Integer index) {
        return mapping.get(index);
    }

    public Integer getId() {
        return id;
    }
}
