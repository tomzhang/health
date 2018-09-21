package com.dachen.health.group.group.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

public class GroupSearchParam extends PageVO {
	
	private String searchType;//gId:根据集团ID;sId:科室ID;kId:关键字搜索

    /* 病种Id */
    private String diseaseId;
    
    private String groupId;

    /* 关键字 */
    private String keyword;

    private String docGroupId; // 医生集团ID

    private String specialistId; // 医生科室ID

    private List<Integer> docIds;
    
    private String deptName;
    
    // 专长
    private List<String> specialtyIds;
    
    // 地区code，如深圳市为440300
    private Integer areaCode;
    
    /**
     * 医生id
     */
    private Integer doctorId;
    
    /**
     * 是否过滤掉不允许申请加入的集团记录(不传默认为true)
     */
    private boolean memberApply = true;
   
   
    /**
     * 是否默认排序
     */
    private Integer sort=0;
    
    /**
     * 用户当今纬度
     */
    private String lat;
    /**
     * 用户当前经度
     */
    private String lng;
    
    

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

    public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDocGroupId() {
        return docGroupId;
    }

    public void setDocGroupId(String docGroupId) {
        this.docGroupId = docGroupId;
    }

    public String getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(String specialistId) {
        this.specialistId = specialistId;
    }

    public List<Integer> getDocIds() {
        return docIds;
    }

    public void setDocIds(List<Integer> docIds) {
        this.docIds = docIds;
    }

    public List<String> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(List<String> specialtyIds) {
        this.specialtyIds = specialtyIds;
    }

	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public boolean isMemberApply() {
		return memberApply;
	}

	public void setMemberApply(boolean memberApply) {
		this.memberApply = memberApply;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}
