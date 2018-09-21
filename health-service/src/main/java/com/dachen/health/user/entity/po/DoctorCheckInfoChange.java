package com.dachen.health.user.entity.po;

import com.dachen.health.commons.constants.DoctorInfoChangeEnum.InfoStatus;
import com.dachen.health.commons.constants.DoctorInfoChangeEnum.VerifyResult;
import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-04-25
 * Time: 17:20
 * Description:
 */
@Entity(value = "doctor_reset_check_info", noClassnameStored = true)
public class DoctorCheckInfoChange {

    @Id
    private String id;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headPicFileName;

    @ApiModelProperty(value = "证书图片")
    private List<String> checkImage;

    @ApiModelProperty(value = "医院")
    private String hospital;

    @ApiModelProperty(value = "医院Id")
    private String hospitalId;

    @ApiModelProperty(value = "科室")
    private String departments;

    @ApiModelProperty(value = "科室Id")
    private String deptId;

    @ApiModelProperty(value = "科室电话")
    private String deptPhone;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "审核意见")
    private String remark;

    /**
     * @see InfoStatus
     */
    @ApiModelProperty(value = "认证信息状态(1：未处理；2：已处理)")
    private Integer infoStatus;

    /**
     * @see VerifyResult
     */
    @ApiModelProperty(value = "处理结果(1：驳回；2：同意)")
    private Integer verifyResult;

    @ApiModelProperty(value = "处理时间")
    private Long verifyTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;


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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeptPhone() { return deptPhone; }

    public void setDeptPhone(String deptPhone) { this.deptPhone = deptPhone; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(Integer infoStatus) {
        this.infoStatus = infoStatus;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getResult() {
        return verifyResult;
    }

    public void setResult(Integer result) {
        this.verifyResult = result;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public Integer getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Integer verifyResult) {
        this.verifyResult = verifyResult;
    }

    public Long getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Long verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public List<String> getCheckImage() {
        return checkImage;
    }

    public void setCheckImage(List<String> checkImage) {
        this.checkImage = checkImage;
    }
}
