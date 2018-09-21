package com.dachen.health.commons.vo;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/23 15:53 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class UserConstant {

    public static final String DEFAULT_DOCTOR_NAME = "未命名";

    /**
     * 用户人身验证
     *
     * @author xuhuanjie
     * @date 2018年1月9日
     */
    public enum FaceRecognitionStatus {

        fail(0, "验证失败"),
        pass(1, "通过验证"),
        never(2, "未验证");
        private int index;

        private String title;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        private FaceRecognitionStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }
    }

    public enum SuspendStatus {

        normal(0, "正常"),
        suspend(1, "挂起"),
        tempForbid(4, "暂时禁用");

        private int index;

        private String title;

        private SuspendStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public static UserConstant.SuspendStatus getEnum(int index) {
            UserConstant.SuspendStatus e = null;
            for (UserConstant.SuspendStatus e1 : UserConstant.SuspendStatus.values()) {
                if (e1.index == index) {
                    e = e1;
                    break;
                }
            }
            return e;
        }
    }

}
