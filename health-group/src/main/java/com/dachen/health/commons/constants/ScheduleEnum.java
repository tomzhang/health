package com.dachen.health.commons.constants;

/**
 * ProjectName： health-group<br>
 * ClassName： ScheduleEnum<br>
 * Description： 排班模块枚举类<br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public class ScheduleEnum {

    /**
     * ProjectName： health-group<br>
     * ClassName： ClinicType<br>
     * Description： 门诊类型<br>
     * @author fanp
     * @createTime 2015年8月11日
     * @version 1.0.0
     */
    public enum ClinicType {
        wechat(1, "普通"), 
        alipay(2, "专家"), 
        balances(3, "特需");

        private int index;

        private String title;

        public static String getTitle(int index) {
            for (ClinicType ms : ClinicType.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
        }

        public static Integer getIndex(String title) {
            for (ClinicType ms : ClinicType.values()) {
                if (ms.title.equals(title)) {
                    return ms.index;
                }
            }
            return null;
        }
        
        private ClinicType(int index, String title) {
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
    
    /**
     * ProjectName： health-group<br>
     * ClassName： clinicType<br>
     * Description： 一天时间段<br>
     * @author fanp
     * @createTime 2015年8月11日
     * @version 1.0.0
     */
    public enum Period {
        forenoon(1, "上午"), 
        afternoon(2, "下午"), 
        evening(3, "晚上");

        private int index;

        private String title;

        public static String getTitle(int index) {
            for (Period ms : Period.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
        }

        public static Integer getIndex(String title) {
            for (Period ms : Period.values()) {
                if (ms.title.equals(title)) {
                    return ms.index;
                }
            }
            return null;
        }
        
        private Period(int index, String title) {
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

    public enum OfflineStatus {
        待预约(1, "待预约"),
        已预约(2, "已预约"),
        已开始(3, "已开始"),
        已完成(4, "已完成");

        private int index;

        private String title;

        public static String getTitle(int index) {
            for (Period ms : Period.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
        }

        public static Integer getIndex(String title) {
            for (Period ms : Period.values()) {
                if (ms.title.equals(title)) {
                    return ms.index;
                }
            }
            return null;
        }
        
        private OfflineStatus(int index, String title) {
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

    public enum OfflineDateFrom {
    	医生添加(1, "医生添加"),
        导医添加(2, "导医添加");

        private int index;

        private String title;

        public static String getTitle(int index) {
            for (Period ms : Period.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
        }

        public static Integer getIndex(String title) {
            for (Period ms : Period.values()) {
                if (ms.title.equals(title)) {
                    return ms.index;
                }
            }
            return null;
        }
        
        private OfflineDateFrom(int index, String title) {
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
