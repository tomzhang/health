package com.dachen.health.permission.enums;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 18:27 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class PermissionEnum {

    public enum RoleStatus {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");

        private int index;
        private String title;

        RoleStatus(int index, String title) {
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
    }
}
