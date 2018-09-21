package com.dachen.health.commons.constants;

import java.util.HashMap;
import java.util.Map;

public class CareEnum {

    public enum CarePlanStatus {
        enabled(1, "已启用"),
        disabled(0, "已禁用"),
        deleted(-1, "已删除");
        private int index;
        private String title;

        CarePlanStatus(int index, String title) {
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

    /** 标记关怀项在流程中的状态：（默认0：初始状态、1：已发送、2：已提交答卷（或已上传）、3：医生已查看、4：已失效 */
    public enum CareItemStatus {
        init(0, "初始状态"),
        sent(1, "已发送"),
        submitted(2, "已提交答卷或已上传"),
        doctorViewed(3, "医生已查看"),
        invalid(4, "已失效");
        private int index;
        private String title;

        CareItemStatus(int index, String title) {
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
	
	
	public enum CareItemBqjhStatus {
		一级警告(1, "warn"), 
		提示(2, "info"),
		二级警告(3, "warn3");
        private int index;
        private String title;

        private CareItemBqjhStatus(int index, String title) {
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
	
	public enum OrderCareItemStatus {
		正常(1, "正常"), 
		已回答(2, "已回答"),
		已删除(3, "已删除"),
		已结束(4, "已结束");
        private int index;
        private String title;

        private OrderCareItemStatus(int index, String title) {
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
	
	public enum SendCareIssueType {
		随访(1, "随访"), 
		关怀(2, "关怀");
        private int index;
        private String title;

        private SendCareIssueType(int index, String title) {
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
	
	public enum SendCareIssueStatus {
		未完成(1, "未完成"), 
		已完成(2, "已完成");
        private int index;
        private String title;

        private SendCareIssueStatus(int index, String title) {
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
	
	public enum CareIssueType {
		填空提(1, "c_BHSFK"), 
		选择题(2, "c_BHSFX"),
		问答题(3,"c_BHSFW");
		
        private int index;

        private String title;

        private CareIssueType(int index, String title) {
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
	
	public enum CareQueryType {
		病情跟踪(1, "c_BQGZ"), 
		日程提醒(2, "c_RCJH"),
		生活量表(3,"c_SHLB"),
		用药(4,"c_YYGH"),
		调查表(5,"c_BHSFB"),
		患教资料(6,"c_HJZL"),
		文本(7,"c_TEXT"),
		追加问题(8,"c_ZJWT");
		
        private int index;

        private String title;

        private CareQueryType(int index, String title) {
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
	
	
	public enum CareTemplateType {
		关怀计划模板(1, "关怀计划"), 
		随访模板(2, "随访模板");

		private int index;

        private String title;

        private CareTemplateType(int index, String title) {
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
	
	
	public static String getCareQueryType(Integer type) {
		if(type==CareQueryType.日程提醒.getIndex()) {
			return CareQueryType.日程提醒.getTitle();
		}else if(type==CareQueryType.生活量表.getIndex()) {
			return CareQueryType.生活量表.getTitle();
		}else if(type==CareQueryType.病情跟踪.getIndex()) {
			return CareQueryType.病情跟踪.getTitle();
		}else if(type==CareQueryType.调查表.getIndex()) {
			return CareQueryType.调查表.getTitle();
		}else {
			return CareQueryType.用药.getTitle();
		}
	}
	
	public static String getCareQueryTypeName(Integer type) {
		if(type==CareQueryType.日程提醒.getIndex()) {
			return CareQueryType.日程提醒.name();
		}else if(type==CareQueryType.生活量表.getIndex()) {
			return CareQueryType.生活量表.name();
		}else if(type==CareQueryType.病情跟踪.getIndex()) {
			return CareQueryType.病情跟踪.name();
		}else if(type==CareQueryType.调查表.getIndex()) {
			return CareQueryType.调查表.name();
		}else if(type==CareQueryType.患教资料.getIndex()){
			return CareQueryType.患教资料.name();
		}else if(type==CareQueryType.文本.getIndex()){
			return CareQueryType.文本.name();
		}else {
			return CareQueryType.用药.name();
		}
	}
	
	
	public static Integer getCareIssueType(String type) {
		if(type.equals(CareIssueType.填空提.getTitle())) {
			return CareIssueType.填空提.getIndex();
		}else if(type.equals(CareIssueType.选择题.getTitle())) {
			return CareIssueType.选择题.getIndex();
		}else if(type.equals(CareIssueType.问答题.getTitle())) {
			return CareIssueType.问答题.getIndex();
		}else {
			return 0;
		}
	}
	
	public enum DocumentItemFromType {
		DOCUMENT_SCIENCE(0, "健康科普"),
		KNOWLEDGE(1, "就医知识");
		
		private DocumentItemFromType(Integer index, String title) {
			this.index = index;
			this.title = title;
		}
		
		private Integer index;
		private String title;
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		private static final Map<Integer, DocumentItemFromType> mapping;
		static {
			DocumentItemFromType[] types = DocumentItemFromType.values();
			mapping = new HashMap<Integer, DocumentItemFromType>(types.length);
			for (DocumentItemFromType type:types) {
				mapping.put(type.getIndex(), type);
			}
		}
		
		public static DocumentItemFromType eval(Integer index) {
			return mapping.get(index);
		}
	}
	
	public enum QuestionType {
		Single_Choice(1, "单选题"),
		Fill_in_the_blank(2, "填空题"),
		Question_Answer(3, "问答题"),
		Multiple_Choice(4, "多选题");
		
		private QuestionType(Integer index, String title) {
			this.index = index;
			this.title = title;
		}
		
		private Integer index;
		private String title;
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		private static final Map<Integer, QuestionType> mapping;
		static {
			QuestionType[] types = QuestionType.values();
			mapping = new HashMap<Integer, QuestionType>(types.length);
			for (QuestionType type:types) {
				mapping.put(type.getIndex(), type);
			}
		}
		
		public static QuestionType eval(Integer index) {
			return mapping.get(index);
		}
	}
	
	/** 1: calling求助中、2：处理完成、3：cancel患者取消 */
	public enum HelpRecordStatus {
		CALLING(1, "求助中"),
		FINISHED(2, "处理完成"),
		PATIENT_CANCEL(3, "患者取消");
		
		private Integer index;
		private String title;
		
		private HelpRecordStatus(Integer index, String title) {
			this.index = index;
			this.title = title;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
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
