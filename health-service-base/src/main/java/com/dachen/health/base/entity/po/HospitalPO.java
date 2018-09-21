package com.dachen.health.base.entity.po;

import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 医疗机构基本信息
 * 
 * @author Cwei
 */
@Entity(value = "b_hospital", noClassnameStored = true)
@Indexes({@Index(fields = {@Field(value = "name")})})
public class HospitalPO {

    // 医院编号
    @Id
    private String id;

    /**
     * 结构名称
     */
    private String name;

    /**
     * 医院地址
     */
    private String address;

    // /**
    // * 省
    // */
    private int province;
    //
    // /**
    // * 市
    // */
    private int city;
    //
    // /**
    // * 县
    // */
    private int country;

    // @Reference
    // private Area area;

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

    /**
     * 正常、冻结(DataStatusEnum)
     */
    private Integer status;

    /**
     * 医院介绍
     */
    private String description;

    /**
     * 医院门户地址
     */
    private String portal;

    /**
     * 医疗机构类型-->HospitalTypeEnum
     */
    private Integer hospitalType;

    private Long lastUpdatorTime;
    private Long createTime;
    private double lng;// longitude
    private double lat;// latitude

    /**
     * 经纬度
     */
    private Loc loc;


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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public Integer getHospitalType() {
        return hospitalType;
    }

    public void setHospitalType(Integer hospitalType) {
        this.hospitalType = hospitalType;
    }
    
    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 坐标:经度，纬度
     */
    public static class Loc {
        public Loc() {
            super();
        }

        public Loc(double lng, double lat) {
            super();
            this.lng = lng;
            this.lat = lat;
        }

        private double lng;// longitude
        private double lat;// latitude

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public Long getLastUpdatorTime() {
        return lastUpdatorTime;
    }

    public void setLastUpdatorTime(Long lastUpdatorTime) {
        this.lastUpdatorTime = lastUpdatorTime;
    }

}
