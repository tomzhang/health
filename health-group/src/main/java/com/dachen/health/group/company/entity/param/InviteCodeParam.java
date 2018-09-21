package com.dachen.health.group.company.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * 
 * @author pijingwei
 * @date 2015/8/10
 */
@Deprecated //暂时废弃，没有这块业务
public class InviteCodeParam extends PageVO {

	/**
	 * id
	 */
	private String id;
	
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
	 * 生成时间（验证码有效开始时间）
	 */
	private String generateDate;
	
	/**
	 * 发送对象（发送手机）
	 */
	private String mobile;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getGenerateDate() {
		return generateDate;
	}

	public void setGenerateDate(String generateDate) {
		this.generateDate = generateDate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
