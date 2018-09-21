package com.dachen.health.group.group.entity.param;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.po.GroupConfig;
import com.dachen.health.group.group.entity.vo.HospitalInfo;

/**
 * 
 * @author pijingwei
 * @date 2015/8/7
 * 医院集团
 */
public class GroupParam extends PageVO {
	
	private String id;
	
	/**
	 * 公司Id--所属公司
	 */
	private String companyId;
	
	/**
	 * 集团名称
	 */
	private String  name;
	
	/**
	 * 集团介绍
	 */
	private String introduction;
	
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
	 * 集团设置
	 */
	@Embedded
	private GroupConfig config;
	
	/**
     * 医院id
     */
    private String hospitalId;
    
    /**
     * 医院id列表
     */
    private List<String> hospitalIds;
   
    /**
     * 医院列表（坐标名称）
     */
    private List<HospitalInfo> hospitalInfo;
    
	public List<HospitalInfo> getHospitalInfo() {
		return hospitalInfo;
	}

	public void setHospitalInfo(List<HospitalInfo> hospitalInfo) {
		this.hospitalInfo = hospitalInfo;
	}

	/**
     * 数据类型type（hospital，group）
     * @return
     */
    private String type;
    //搜索关键字
    private String keyWord;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public GroupConfig getConfig() {
		return config;
	}

	public void setConfig(GroupConfig config) {
		this.config = config;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<String> getHospitalIds() {
		return hospitalIds;
	}

	public void setHospitalIds(List<String> hospitalIds) {
		this.hospitalIds = hospitalIds;
	}
	
}
