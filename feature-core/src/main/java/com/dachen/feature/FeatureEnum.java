package com.dachen.feature;


import java.util.HashMap;
import java.util.Map;

public class FeatureEnum {

    public enum FeatureKindEnum {
        Advertisement(1, "广告");

        private Integer id;
        private String title;

        FeatureKindEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, FeatureKindEnum> mapping;
        static {
            FeatureKindEnum[] types = FeatureKindEnum.values();
            mapping = new HashMap<>(types.length);
            for (FeatureKindEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static FeatureKindEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public enum FeatureObjectKindIdEnum {
        HttpUrl(1, "跳转链接"),
        RichText(2, "富文本"),
        RecArticle(3, "推荐文章"),
        OtherType(4,"跳转其他业务");


        private Integer id;
        private String title;

        FeatureObjectKindIdEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, FeatureObjectKindIdEnum> mapping;
        static {
            FeatureObjectKindIdEnum[] types = FeatureObjectKindIdEnum.values();
            mapping = new HashMap<>(types.length);
            for (FeatureObjectKindIdEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static FeatureObjectKindIdEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public enum FeatureStatusEnum {
        Prepared(0, "就绪"),
        Published(2, "已发布"),
        Deleted(9, "已删除");

        private Integer id;
        private String title;

        FeatureStatusEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
