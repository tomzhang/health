package com.dachen.health.group.group.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * 
 * @author longjh
 *      date:2017/08/18
 */
public class HospitalBaseParam extends PageVO {
    
    private String id;
    private String name;
    private String level;
    private Integer province;
    private Integer city;
    private Integer country;
    private Integer status;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public Integer getProvince() {
        return province;
    }
    public void setProvince(Integer province) {
        this.province = province;
    }
    public Integer getCity() {
        return city;
    }
    public void setCity(Integer city) {
        this.city = city;
    }
    public Integer getCountry() {
        return country;
    }
    public void setCountry(Integer country) {
        this.country = country;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "HospitalBaseParam [id=" + id + ", name=" + name + ", level=" + level + ", province="
                + province + ", city=" + city + ", country=" + country + ", status=" + status + "]";
    }

}
