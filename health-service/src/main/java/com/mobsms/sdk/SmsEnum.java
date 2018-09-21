package com.mobsms.sdk;

public class SmsEnum {
    public enum Signature{
        xuanguan("0","【玄关健康】", 0),
        kangzhe("1","【康哲公司】", 1),
        IBD("2","【IBD中心】", 2),
        bodejialian("3","【博德嘉联】",3),
        kangzhenongye("4","【康哲农业】", 4),
        yaoqiquan("5","【药企圈】", 5),
        yishengquan("6","【医生圈】", 6),
        yishengquan7("7","【医生圈】", 7);


        private String index;
        private String value;
        private int ext;
        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public int getExt() {
            return ext;
        }

        public void setExt(int ext) {
            this.ext = ext;
        }

        Signature(String index, String val, int ext){
            this.index = index;
            this.value = val;
            this.ext = ext;
        }
    }

    public static String getSignVal(String index){
        for(Signature s :Signature.values()){
            if(s.getIndex().equalsIgnoreCase(index)){
                return s.getValue();
            }
        }
        return "";
    }
}
