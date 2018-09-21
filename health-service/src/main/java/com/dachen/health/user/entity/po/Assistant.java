package com.dachen.health.user.entity.po;

/**
 * ProjectName： health-service<br>
 * ClassName： Assistant<br>
 * Description： 医助信息<br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
public class Assistant {

    /* 所属公司 */
    private String company;

    /* 区域 */
    private String area;

    /* 部门 */
    private String department;

    /* 岗位 */
    private String position;
    
    private String number;

    /* 是否在职 */
    private Integer onduty;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getOnduty() {
        return onduty;
    }

    public void setOnduty(Integer onduty) {
        this.onduty = onduty;
    }

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
