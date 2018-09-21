package com.dachen.health.commons.constants;

/**
 * Author: dell
 * Date: 2018-04-25
 * Time: 18:20
 * Description:
 */
public class DoctorInfoChangeEnum {

    public enum InfoStatus {
        uncheck(1, "待查看"),
        check(2, "已查看");

        private int index;

        private String title;

        private InfoStatus(int index, String title) {
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

        public static InfoStatus getEnum(int index) {
            InfoStatus e = null;
            for (InfoStatus e1 : InfoStatus.values()) {
                if (e1.index == index) {
                    e = e1;
                    break;
                }
            }
            return e;
        }
    }

    public enum VerifyResult {
        disagree(1, "驳回"),
        agree(2, "同意");

        private int index;

        private String title;

        private VerifyResult(int index, String title) {
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

        public static VerifyResult getEnum(int index) {
            VerifyResult e = null;
            for (VerifyResult e1 : VerifyResult.values()) {
                if (e1.index == index) {
                    e = e1;
                    break;
                }
            }
            return e;
        }
    }
}
