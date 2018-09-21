package com.dachen.health.wx.model;

public class TemplateParam {
	
	private final String template_id;
	private final String url;
	private final String first;
	private final String keyword1;
	private final String keyword2;
	private final String keyword3;
	private final String keyword4;
	private final String keyword5;
	private final String remark;
	
	private TemplateParam(Builder builder) {
		this.template_id = builder.template_id;
		this.url = builder.url;
		this.first = builder.first;
		this.keyword1 = builder.keyword1;
		this.keyword2 = builder.keyword2;
		this.keyword3 = builder.keyword3;
		this.keyword4 = builder.keyword4;
		this.keyword5 = builder.keyword5;
		this.remark = builder.remark;
	}
	
	public static class Builder {
		private String template_id;
		private String url;
		private String first;
		private String keyword1;
		private String keyword2;
		private String keyword3;
		private String keyword4;
		private String keyword5;
		private String remark;
		public Builder template_id(String template_id) {
			this.template_id = template_id;
			return this;
		}
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		public Builder first(String first) {
			this.first = first;
			return this;
		}
		public Builder keyword1(String keyword1) {
			this.keyword1 = keyword1;
			return this;
		}
		public Builder keyword2(String keyword2) {
			this.keyword2 = keyword2;
			return this;
		}
		public Builder keyword3(String keyword3) {
			this.keyword3 = keyword3;
			return this;
		}
		public Builder keyword4(String keyword4) {
			this.keyword4 = keyword4;
			return this;
		}
		public Builder keyword5(String keyword5) {
			this.keyword5 = keyword5;
			return this;
		}
		public Builder remark(String remark) {
			this.remark = remark;
			return this;
		}
		public TemplateParam build() {
			return new TemplateParam(this);
		}
	}
	
}
