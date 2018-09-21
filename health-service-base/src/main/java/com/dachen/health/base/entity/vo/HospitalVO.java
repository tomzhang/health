package com.dachen.health.base.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.Property;

import com.dachen.health.base.entity.po.HospitalPO.Loc;

/**
 * ProjectName： health-service<br>
 * ClassName： Hospital<br>
 * Description：医院基础数据 <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class HospitalVO implements java.io.Serializable{

    private static final long serialVersionUID = 2876948555047192730L;

    @Property("_id")
    private String id;

    /* 名称 */
    private String name;
    /**
     * 医院状态
     */
    private Integer status;

    /* 地区编码 */
    private Integer province;

    /* 地区编码 */
    private Integer city;

    /* 地区编码 */
    private Integer country;
    
    /* 地区编码名称 */
    private String provinceName;

    /* 地区编码 名称*/
    private String cityName;

    /* 地区编码名称 */
    private String countryName;
    
    /**
	 * 医院等级--->HospitalLevelEnum(?)
	 */
	private String level;

    @ApiModelProperty(value = "医院性质")
    private String nature;

    @ApiModelProperty(value = "医院类型")
    private String type;

    @ApiModelProperty(value = "医院属性")
    private String property;

    private String lat;//医院经度
	
	private String lng;//医院维度
	
	private String dis;//距离
	
	private String address;//地址
	
	private Long lastUpdatorTime;

    private Long createTime;

    private Loc loc;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

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

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
    
	public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDis() {
		return dis;
	}

	public void setDis(String dis) {
		this.dis = dis;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }
	

}
