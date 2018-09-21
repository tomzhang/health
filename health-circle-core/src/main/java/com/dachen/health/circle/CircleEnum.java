package com.dachen.health.circle;

import java.util.HashMap;
import java.util.Map;

public class CircleEnum {
    public enum GroupTrendStatusEnum {
        Prepared(0, "待审核"),
        Passed(2, "已通过"),
        Deleted(9, "已删除");

        private Integer id;
        private String title;

        GroupTrendStatusEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, GroupTrendStatusEnum> mapping;
        static {
            GroupTrendStatusEnum[] types = GroupTrendStatusEnum.values();
            mapping = new HashMap<>(types.length);
            for (GroupTrendStatusEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static GroupTrendStatusEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }
    }

    public enum TrendCommentStatusEnum {
        Prepared(0, "待审核"),
        Passed(2, "已通过"),
        Deleted(9, "已删除");

        private Integer id;
        private String title;

        TrendCommentStatusEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, TrendCommentStatusEnum> mapping;
        static {
            TrendCommentStatusEnum[] types = TrendCommentStatusEnum.values();
            mapping = new HashMap<>(types.length);
            for (TrendCommentStatusEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static TrendCommentStatusEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }
    }

    public enum TrendCommentTypeEnum {
        Comment(1, "评论"),
        ReplyComment(2, "回复评论"),
        ReplyReply(3, "回复回复");

        private Integer id;
        private String title;

        TrendCommentTypeEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, TrendCommentTypeEnum> mapping;
        static {
            TrendCommentTypeEnum[] types = TrendCommentTypeEnum.values();
            mapping = new HashMap<>(types.length);
            for (TrendCommentTypeEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static TrendCommentTypeEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }
    }

    public enum GroupUnionApplyStatus {
        handling(1, "处理中"),
        accepted(2, "已同意"),
        refused(3, "已拒绝"),
        closed(8, "已关闭"),
        deleted(9, "已删除");

        private Integer index;
        private String title;

        GroupUnionApplyStatus(Integer index, String title) {
            this.index = index;
            this.title = title;
        }

        private static final Map<Integer, GroupUnionApplyStatus> mapping;

        static {
            GroupUnionApplyStatus[] types = GroupUnionApplyStatus.values();
            mapping = new HashMap<>(types.length);
            for (GroupUnionApplyStatus type : types) {
                mapping.put(type.getIndex(), type);
            }
        }

        public static GroupUnionApplyStatus eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public enum GroupUnionMemberFromTypeEnum {
        groupUnion(0, "医联体创建"),
        apply(1, "科室申请"),
        invite(2, "医联体邀请");

        private Integer index;
        private String title;

        GroupUnionMemberFromTypeEnum(Integer index, String title) {
            this.index = index;
            this.title = title;
        }

        private static final Map<Integer, GroupUnionMemberFromTypeEnum> mapping;

        static {
            GroupUnionMemberFromTypeEnum[] types = GroupUnionMemberFromTypeEnum.values();
            mapping = new HashMap<>(types.length);
            for (GroupUnionMemberFromTypeEnum type : types) {
                mapping.put(type.getIndex(), type);
            }
        }

        public static GroupUnionMemberFromTypeEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
