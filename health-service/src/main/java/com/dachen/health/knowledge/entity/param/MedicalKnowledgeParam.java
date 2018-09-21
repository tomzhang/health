package com.dachen.health.knowledge.entity.param;

import com.dachen.health.knowledge.entity.po.MedicalKnowledge;

public class MedicalKnowledgeParam extends MedicalKnowledge{

	private String groupId;
	private Integer doctorId;
	private String keywords;
	private String authorIoc;//图标
	private String codeSrc;//二维码图
	private String desc;//医生或者集团简介
	private String docTitle;//医生标题
	private String docDept;//医生科室
	private String hospital;//医生所属医院
	private String sCategoryId;//原来的分类ID
	private String categoryId;//新的分类
	
	private String fileName;//生成文件名
	private String templateFile;//模板文件
	private String authorName;//作者名称
	private String authorType;//作者类型
	
	public String getAuthorType() {
		return authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * 返回的页码
	 */
	protected int pageIndex = 0;
	
	/**
	 * 每页数据大小
	 */
	protected int pageSize = 15;
	
	protected int start;
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start = (pageIndex * pageSize);
	}


	public String getAuthorIoc() {
		return authorIoc;
	}

	public void setAuthorIoc(String authorIoc) {
		this.authorIoc = authorIoc;
	}

	public String getCodeSrc() {
		return codeSrc;
	}

	public void setCodeSrc(String codeSrc) {
		this.codeSrc = codeSrc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getDocDept() {
		return docDept;
	}

	public void setDocDept(String docDept) {
		this.docDept = docDept;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getsCategoryId() {
		return sCategoryId;
	}

	public void setsCategoryId(String sCategoryId) {
		this.sCategoryId = sCategoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}
	
}
