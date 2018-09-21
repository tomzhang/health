package com.dachen.health.file.constant;

import java.util.Objects;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-01
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class UserFileEnum {

    /**
     * 文件 分类
     */
    public enum UserFileType {

        all(0, "全部"),
        document(1, "文档"),
        picture(2, "图片"),
        video(3, "视频"),
        music(4, "音乐"),
        other(5, "其它");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileType(Integer value, String title) {
            this.value = value;
            this.title = title;
        }

        public static UserFileType getEnum(Integer index) {
            UserFileType e = null;
            for (UserFileType e1 : UserFileType.values())
                if (e1.value == index) {
                    e = e1;
                    break;
                }
            return e;
        }
    }

    /**
     * 文件查询 方式
     */
    public enum UserFileMode {

        upload(0, "我上传的文件"),
        receive(1, "我接收的文件"),
        drug(2, "品种文件");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileMode(Integer value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    /**
     * 文件的bucket类型  公有，私有
     */
    public enum UserFileBucketType {
        pub(0, "公有"),
        pri(1, "私有");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileBucketType(Integer value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    /**
     * 文件的状态
     */
    public enum UserFileStatus {
        common(0, "正常"),
        delete(1, "已删除");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setIndex(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileStatus(Integer value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    public enum BizType {
        LIVE(0, "圈子");

        private Integer index;
        private String desc;

        BizType(Integer index, String desc) {
            this.index = index;
            this.desc = desc;
        }

        public static BizType getEnum(Integer value) {
            for (BizType e : BizType.values()) {
                if (Objects.equals(e.getIndex(), value)) {
                    return e;
                }
            }
            return null;
        }


        public Integer getIndex() {
            return index;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 文件的状态
     */
    public enum UserFileSortAttr {

        name(0, "name"),
        size(1, "size"),
        date(2, "createTime");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setIndex(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileSortAttr(Integer value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    /**
     * 文件的状态
     */
    public enum UserFileSourceType {
        upload(0, "上传"),
        receive(1, "接收");

        private Integer value;
        private String title;

        public Integer getValue() {
            return value;
        }

        public void setIndex(Integer value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        UserFileSourceType(Integer value, String title) {
            this.value = value;
            this.title = title;
        }
    }

}
