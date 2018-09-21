package com.dachen.health.commons.constants;

public class IncomeEnum {

    public enum IncomeStatus {
        paied(1, "已结算"), 
        unpaied(2, "未结算");

        private int index;

        private String title;

        private IncomeStatus(int index, String title) {
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
