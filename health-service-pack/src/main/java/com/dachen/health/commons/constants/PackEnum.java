package com.dachen.health.commons.constants;

public class PackEnum {
    /**
     * 套餐类型
     */
    public enum PackType {
    	checkin(0, "患者报到"),
        message(1, "图文咨询"),
        phone(2, "电话咨询"),
    	careTemplate(3, "健康关怀计划"),
    	followTemplate(4, "随访表"),
    	throghtDoctor(5, "就医直通车"),
    	throghtSpecialist(6, "专家直通车"),
    	throghtCheck(7, "检查直通车"),
    	consultation(8,"远程会诊"),
    	appointment(9,"名医面对面"),
    	feeBill(10, "收费单"),
    	online(11, "在线门诊"),
    	integral(12, "积分问诊");
    	
        private int index;

        private String title;

        private PackType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public static String getTitle(int index) {
            for (PackType ms : PackType.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
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
     * ProjectName： health-service-pack<br>
     * ClassName： PackStatus<br>
     * Description：套餐状态 <br>
     * @author fanp
     * @createTime 2015年8月11日
     * @version 1.0.0
     */
    public enum PackStatus {
        open(1, "开通"), 
        close(2, "关闭");

        private int index;

        private String title;

        private PackStatus(int index, String title) {
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
    
    public static String getPackType(Integer type){
    	
    	if(type==PackType.message.getIndex()) {
			return PackType.message.getTitle();
		}else if(type==PackType.careTemplate.getIndex()) {
			return PackType.careTemplate.getTitle();
		}else if(type==PackType.phone.getIndex()) {
			return PackType.phone.getTitle();
		}else if(type==PackType.followTemplate.getIndex()) {
			return PackType.followTemplate.getTitle();
		}else {
			return "";
		}
    }
    
    /**
     * 过滤部分不显示的套餐
     * @author Administrator
     *
     */
    public enum PackTypeName {

        message(1, "图文咨询"), 
        phone(2, "电话咨询"),
    	careTemplate(3, "健康关怀"),
    	consultation(8,"远程会诊"),
    	appointment(9,"名医面对面"),
    	online(11, "在线门诊"),
    	integral(12, "积分问诊");
    	
        private int index;

        private String title;

        private PackTypeName(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public static String getTitle(int index) {
            for (PackTypeName ms : PackTypeName.values()) {
                if (ms.getIndex() == index) {
                    return ms.title;
                }
            }
            return null;
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
