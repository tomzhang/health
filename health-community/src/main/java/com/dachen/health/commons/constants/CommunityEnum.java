package com.dachen.health.commons.constants;

public class CommunityEnum {
	
	public enum CommunityType {
		医生交流社区("DDC", "医生交流社区");

		private String index;

		private String title;

		private CommunityType(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
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
	 * 帖子状态
	 * @Description 
	 * @title CommunityState
	 * @author liminng
	 * @data 2016年7月26日
	 */
	public enum TopicState {
		正常("0", "正常"),
		草稿("2","草稿"),
		已删除("1", "已删除");

		private String index;

		private String title;

		private TopicState(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
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
	 * 帖子类型
	 * @Description 
	 * @title CommunityType
	 * @author liminng
	 * @data 2016年7月26日
	 */
	public enum TopicType {
		普通("0", "普通"),
		卡片("1", "卡片");

		private String index;

		private String title;

		private TopicType(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum ReplyState {
		正常("0", "普通"),
		已删除("1", "卡片");

		private String index;

		private String title;

		private ReplyState(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
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
	 * 是否有未读消息
	 * @Description 
	 * @title CommunityUpdate
	 * @author liminng
	 * @data 2016年8月3日
	 */
	public enum CommunityMessage {
		没未读("0", "没有我未读消息"),
		有未读("1", "有未读消息");

		private String index;

		private String title;

		private CommunityMessage(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
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
	 * 圈子时候正常
	 * @Description 
	 * @title CommunityUpdate
	 * @author liminng
	 * @data 2016年8月3日
	 */
	public enum CommunityCircleState {
		正常("0", "没有我未读消息"),
		已删除("1", "有未读消息");

		private String index;

		private String title;

		private CommunityCircleState(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
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
