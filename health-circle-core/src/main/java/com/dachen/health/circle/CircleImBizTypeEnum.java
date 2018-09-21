package com.dachen.health.circle;

import java.util.HashMap;
import java.util.Map;

public enum CircleImBizTypeEnum {
    GroupApply(1000, "医生申请加入科室"),
    GroupInvite(1001, "科室邀请医生成员"),
    GroupUnionApply(1010, "科室申请加入医联体"),
    GroupUnionInvite(1011, "医联体邀请科室成员"),
    GroupTrend(1100, "科室动态"),
    GroupTrendComment(1012, "科室动态评论"),
    GroupTrendCommentReply(1013, "科室动态评论回复"),
    GroupTrendCredit(1014, "科室动态打赏"),
    GroupDismiss(1015, "科室或者圈子解散"),
    GroupRemove(1016, "科室或者圈子移除"),
    GroupQuit(1017, "科室或者圈子退出"),
    GroupResult(1018, "申请加入科室结果");


    private Integer id;
    private String title;

    CircleImBizTypeEnum(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    private static final Map<Integer, CircleImBizTypeEnum> mapping;
    static {
        CircleImBizTypeEnum[] types = CircleImBizTypeEnum.values();
        mapping = new HashMap<>(types.length);
        for (CircleImBizTypeEnum type:types) {
            mapping.put(type.getId(), type);
        }
    }

    public static CircleImBizTypeEnum eval(Integer index) {
        return mapping.get(index);
    }

    public Integer getId() {
        return id;
    }

}
