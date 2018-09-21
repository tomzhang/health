package com.dachen.lbs.vo;

public class NearbyJob extends BasePoi {
	private int companyId;// 公司Id
	private String companyName;// 公司名称
	private String description;// 公司简介
	private int diploma;// 学历
	private int jobId;// 职位Id
	private String jobName;// 职位名称
	private int salary;// 薪水
	private int workExp;// 工作经验
	private String workLocation;// 工作地点

	public int getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getDescription() {
		return description;
	}

	public int getDiploma() {
		return diploma;
	}

	public int getJobId() {
		return jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public int getSalary() {
		return salary;
	}

	public int getWorkExp() {
		return workExp;
	}

	public String getWorkLocation() {
		return workLocation;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiploma(int diploma) {
		this.diploma = diploma;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public void setWorkExp(int workExp) {
		this.workExp = workExp;
	}

	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}

}
