package com.dachen.health.commons.vo;

public class CircleEnum {
    
    public enum CircleType{
        CIRCLE(1, "圈子"),
        DEPT(2, "科室");
        
        private Integer index;
        private String title;
        CircleType(Integer index, String title){
            this.index = index;
            this.title = title;
        }
        public Integer getIndex() {
            return index;
        }
        public String getTitle() {
            return title;
        }
    }
}
