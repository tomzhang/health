package com.dachen.health.pack.stat.entity.vo;

import java.util.Set;

/**
 * ProjectName： health-group<br>
 * ClassName： PatientStatVO<br>
 * Description：患者统计VO <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class PatientStatVO implements java.io.Serializable {

    private static final long serialVersionUID = -4954653798336508529L;

    /* 患者id */
    private Integer id;

    /* 患者姓名 */
    private String name;
    
    private Integer orderId;

    private Integer sex;

    private Long birthday;

    private Integer age;
    
    private String ageStr;

    private String telephone;

    /* 关系 */
    private String relation;

    /* 头像（这个已经无效了） */
    private String topPath;
    
    /* 头像 */
    private String headPicFileName;

    /*病种*/
    private String diseaseTypeName;
    
    private Set<String> diseaseTypeNames;
    
    private Long createTime;
    
    private String area;
    
    public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Set<String> getDiseaseTypeNames() {
		return diseaseTypeNames;
	}

	public void setDiseaseTypeNames(Set<String> diseaseTypeNames) {
		this.diseaseTypeNames = diseaseTypeNames;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getTopPath() {
        return topPath;
    }

    public void setTopPath(String topPath) {
        this.topPath = topPath;
    }

    public String getDiseaseTypeName() {
        return diseaseTypeName;
    }

    public void setDiseaseTypeName(String diseaseTypeName) {
        this.diseaseTypeName = diseaseTypeName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

}
