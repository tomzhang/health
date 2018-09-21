package com.dachen.health.activity.invite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/24 14:51 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class InviteEnum {
	/**发短信*/
    public static final String SEND_SMS_URL = "http://SMSSERVICE/sms/sendAllByExt";
    public static  final String INVITIE_REGISTER_NOTICE_CONTENT="感谢您注册【医生圈】，平台已经接受您的申请，请下载【医生圈】加入更多专家大咖的圈子。下载地址【%s】";
    public enum ActivityTypeEnum {
        invite(1, "邀请活动"),
        register(2, "注册活动");

        private Integer id;
        private String title;

        ActivityTypeEnum(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<Integer, ActivityTypeEnum> mapping;
        static {
            ActivityTypeEnum[] types = ActivityTypeEnum.values();
            mapping = new HashMap<>(types.length);
            for (ActivityTypeEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static ActivityTypeEnum eval(Integer index) {
            return mapping.get(index);
        }

        public Integer getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public enum QrCodeTypeEnum {
        Dept("dept", "科室"),
        ActivityDoctor("activityDoctor", "活动医生"),
        Doctor("doctor", "医生");

        private String id;
        private String title;

        QrCodeTypeEnum(String id, String title) {
            this.id = id;
            this.title = title;
        }

        private static final Map<String, QrCodeTypeEnum> mapping;
        static {
            QrCodeTypeEnum[] types = QrCodeTypeEnum.values();
            mapping = new HashMap<>(types.length);
            for (QrCodeTypeEnum type:types) {
                mapping.put(type.getId(), type);
            }
        }

        public static QrCodeTypeEnum eval(String index) {
            return mapping.get(index);
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}
