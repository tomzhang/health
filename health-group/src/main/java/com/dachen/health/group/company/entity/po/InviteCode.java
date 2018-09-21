package com.dachen.health.group.company.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author pijingwei
 * @date 2015/8/10
 */
@Entity(value="c_invite_code", noClassnameStored = true)
@Deprecated //暂时废弃，没有这块业务
public class InviteCode {

	/**
	 * id
	 */
	@Id
	private String id;
	
	/**
	 * 医生Id
	 */
	private Integer doctorId;
	
	/**
	 * 公司Id
	 */
	private String companyId;
	
	/**
	 * 邀请码
	 */
	private String code;
	
	/**
	 * 验证码状态（N：未使用，Y：已使用）
	 */
	private String status;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 生成时间（验证码有效开始时间）
	 */
	private Long generateDate;
	
	/**
	 * 使用日期
	 */
	private Long useDate;
	
	/**
	 * 发送对象（发送手机）
	 */
	private String telephone;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getGenerateDate() {
		return generateDate;
	}

	public void setGenerateDate(Long generateDate) {
		this.generateDate = generateDate;
	}

	public Long getUseDate() {
		return useDate;
	}

	public void setUseDate(Long useDate) {
		this.useDate = useDate;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
}
