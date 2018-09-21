package com.dachen.health.pack.stat.entity.vo;

/**
 * ProjectName： health-group<br>
 * ClassName： PackStatVO<br>
 * Description：统计VO <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class PackStatVO implements java.io.Serializable {

    private static final long serialVersionUID = -2788501403116057451L;

    /* 医生id */
    private Integer id;

    /* 医生姓名 */
    private String name;

    private String hospital;

    private String departments;

    private String title;

    /* 统计数量 */
    private Integer amount;

    private String telephone;

    /* 时间 */
    private Long time;

    private Integer sex;

    private Integer age;

    private String headPicFileName;

    /* 金额，单位元 */
    private Long money;

    /* 订单号 */
    private String orderNo;

    /* 订单类型 */
    private Integer packType;

    /* 订单名称 */
    private String packName;

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

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getPackType() {
        return packType;
    }

    public void setPackType(Integer packType) {
        this.packType = packType;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

}
