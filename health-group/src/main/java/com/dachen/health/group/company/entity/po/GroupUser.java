package com.dachen.health.group.company.entity.po;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * 
 * @author pijingwei
 * @date 2015/8/12
 */
@Entity(value="c_group_user", noClassnameStored = true)
public class GroupUser implements Serializable {

	private static final long serialVersionUID = 7645671919574125481L;

	/**
	 * Id
	 */
	@Id
	private String id;
	
	/**
	 * 用户Id
	 */
	private Integer doctorId;
	
	/**
	 * 集团Id或公司Id
	 */
	private String objectId;
	
	/**
	 * 账户类型    1：公司用户   2：集团用户
	 */
	private Integer type;
	
	/**
	 * 状态	I：邀请待通过，C：正常使用， S：已离职，N：拒绝邀请
	 */
	private String status;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建时间
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新时间
	 */
	private Long updatorDate;
	
	/**
	 * root=超级管理员，admin=普通管理员
	 */
	private String rootAdmin;
	
	/**
	 * 姓名
	 */
	private @NotSaved String name;
	/**
	 * 头像
	 */
	private @NotSaved String headPicFileName;

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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public String getRootAdmin() {
		return rootAdmin;
	}

	public void setRootAdmin(String rootAdmin) {
		this.rootAdmin = rootAdmin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

}
