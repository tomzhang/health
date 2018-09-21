package com.dachen.health.pack.consult.entity.vo;

public class ConsultationEnum {

	 public enum FriendApply {
	        cApplyu(1, "会诊医生添加主诊医生"), 
	        uApplyc(2, "主诊医生添加会诊医生");
		 
	        private int index;

	        private String title;

	        private FriendApply(int index, String title) {
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

	        public static  FriendApply  getEnum(int index)
	        {
	        	FriendApply e=null;
	            for(FriendApply e1:FriendApply.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	    }
	 
	 
	 public enum ApplyStatus {
	     
		 	applying(1, "已申请"), 
	        agree(2, "已同意"),
			ignore(3, "已忽略");
		 
	        private int index;

	        private String title;

	        private ApplyStatus(int index, String title) {
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

	        public static  ApplyStatus  getEnum(int index)
	        {
	        	ApplyStatus e=null;
	            for(ApplyStatus e1:ApplyStatus.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	    }
	 
	 
	 public enum DoctorRole {
	     
		 	specialist(1, "专家"), 
		 	assistant(2, "助手");
		 
	        private int index;

	        private String title;

	        private DoctorRole(int index, String title) {
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

	        public static  DoctorRole  getEnum(int index)
	        {
	        	DoctorRole e=null;
	            for(DoctorRole e1:DoctorRole.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	    }
	 
	 public enum DoctorFriendType {

		 	applying(1, "已申请"), 
		 	beapplied(2, "已被申请"),
		    norelation(3, "无会诊好友关系"),
		 	collect(4, "已收藏"),
			befriend(5, "已经是朋友");
		 
	        private int index;

	        private String title;

	        private DoctorFriendType(int index, String title) {
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

	        public static  DoctorFriendType  getEnum(int index)
	        {
	        	DoctorFriendType e=null;
	            for(DoctorFriendType e1:DoctorFriendType.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	    }
	 
	 
	 public enum CollectOperate {
	     
		 	collect(1, "收藏"), 
		 	cancel(2, "取消");
		 
	        private int index;

	        private String title;

	        private CollectOperate(int index, String title) {
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

	        public static  CollectOperate  getEnum(int index)
	        {
	        	CollectOperate e=null;
	            for(CollectOperate e1:CollectOperate.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	   }
	 
	 
	 public enum IllCaseTreatType {
		 	 text(1, "图文咨询"), 
		 	 phone(2, "电话咨询"),
			 consultation(3, "会诊咨询"),
			 outPatient(4, "门诊咨询"),
			 care(5, "健康关怀"),
			 appointment(6, "预约名医");
		 
	        private int index;

	        private String title;

	        private IllCaseTreatType(int index, String title) {
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

	        public static  CollectOperate  getEnum(int index)
	        {
	        	CollectOperate e=null;
	            for(CollectOperate e1:CollectOperate.values())
	                if(e1.index==index){
	                    e=e1;
	                    break;
	                }
	            return e;	
	        }
	   }
	 
}
