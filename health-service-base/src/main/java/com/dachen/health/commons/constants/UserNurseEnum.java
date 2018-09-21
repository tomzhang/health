package com.dachen.health.commons.constants;

/**
 * ProjectName： health-service<br>
 * ClassName： UserEnum<br>
 * Description： 会员枚举类<br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
public class UserNurseEnum {

 

    
    
    /**
     * ProjectName： health-service<br>
     * ClassName： RelationStatus<br>
     * Description：关系状态 <br>
     * 
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum RelationStatus {
        normal(1, "正常"), 
        deleted(2, "删除");

        private int index;

        private String title;

        private RelationStatus(int index, String title) {
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
     * ProjectName： health-service<br>
     * ClassName： RelationType<br>
     * Description：关系类型 <br>
     * 
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum RelationType {
        doctorPatient("医患关系"),
        patientFriend("患者好友关系"),
        doctorFriend("医生好友关系"),
        doctorAssistant("医生医助关系");

        private String title;

        private RelationType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
    
    /**
     * ProjectName： health-service<br>
     * ClassName： TagType<br>
     * Description： 标签分类<br>
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum TagType {
        doctorPatient(1, "医患标签"),
        patientFriend(2, "患者好友标签"),
        doctorFriend(3, "医生好友标签");

        private int index;

        private String title;

        private TagType(int index, String title) {
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
