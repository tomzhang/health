package com.dachen.health.commons.constants;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-02-08
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class DoctorTopicEnum {

    public enum DoctorTopicNameEnum {
        /**
         * 医生注册
         */
        Register(0, "pre_doctor_register_R2P4"),
        /**
         * 医生登录
         */
        Login(1, "pre_doctor_login_R2P4"),
        /**
         * 医生认证
         */
        Certify(2, "pre_doctor_certify_R2P4"),
        /**
         * 医生审核
         */
        Check(3, "pre_doctor_check_R2P4"),
        /**
         * 医生修改信息
         */
        ChangeInfo(4, "pre_doctor_change_R2P4");

        private Integer index;

        private String topicName;

        DoctorTopicNameEnum(Integer index, String topicName) {
            this.index = index;
            this.topicName = topicName;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }
    }

    public enum DoctorDataSourceEnum {

        /**
         * 医生注册
         */
        Register(0, "doctorRegister"),
        /**
         * 医生登录
         */
        Login(1, "doctorLogin"),
        /**
         * 医生认证
         */
        Certify(2, "doctorCertify"),
        /**
         * 医生审核
         */
        Check(3, "doctorCheck"),
        /**
         * 医生修改信息
         */
        ChangeInfo(4, "doctorChange");

        private Integer index;

        private String sourceName;

        DoctorDataSourceEnum(Integer index, String sourceName) {
            this.index = index;
            this.sourceName = sourceName;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
    }

    public enum DoctorLoginTypeEnum {

        LoginAuto("0", "免密登录"),

        LoginByPassWord("1", "手动登录"),

        LoginOut("2", "注销");

        private String index;

        private String loginType;

        DoctorLoginTypeEnum(String index, String loginType) {
            this.index = index;
            this.loginType = loginType;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getLoginType() {
            return loginType;
        }

        public void setLoginType(String loginType) {
            this.loginType = loginType;
        }
    }

}
