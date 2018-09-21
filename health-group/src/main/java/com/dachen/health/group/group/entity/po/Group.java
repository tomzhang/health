package com.dachen.health.group.group.entity.po;

import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.util.JSONUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author pijingwei
 * @date 2015/8/7 医院集团
 */
@Entity(value = "c_group", noClassnameStored = true)
public class Group {

    /**
     * 集团Id
     */
    @Id
    private String id;

    /**
     * 公司Id--所属公司
     */
    private String companyId;

    /**
     * 加V审核时间
     */
    private Long processVTime;

    /**
     * 集团名称
     */
    private String name;

    /**
     * 集团介绍
     */
    private String introduction;

    /**
     * 创建人
     */
    private Integer creator;

    /**
     * 创建人名字
     */
    private String creatorName;

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

    /* 就诊人数 */
    private Integer cureNum;

    /* 权重 */
    private Integer weight;

    /*集团设置专长 diseaseId 和  diseaseName,*/
    @Indexed(value = IndexDirection.ASC)
    private List<String> diseaselist;
    /*设置预约专家ID*/
    private List<Integer> expertIds;

    //值班价格
    private Integer outpatientPrice;

    /**
     * 公司认证材料
     */
    @Embedded
    private GroupCertification groupCert;

    /**
     * 公司认证状态，对应web后台-设置-公司认证
     *
     * @see GroupEnum.GroupCertStatus
     */
    private String certStatus;

    /**
     * 公司是否屏蔽
     */
    private String skip;

    /**
     * active=已激活，inactive=未激活
     */
    private String active;

    /**
     * 集团申请状态
     * A=待审核，P=审核通过，NP=审核不通过
     *
     * @author wangqiao
     * @date 2016年3月4日
     */
    private String applyStatus;

    /**
     * 集团logo
     */
    private String logoUrl;

    /**
     * 提供给客户端的返回值
     */
    private
    @NotSaved
    Map<String, Object> groupUser;

    private
    @NotSaved
    String userStatus;

    /**
     * 医院id
     */
    private String hospitalId;

    /**
     * 科室id
     */
    private String deptId;

    /**
     * 数据类型type（hospital，group）
     *
     * @return
     */
    private String type;

    /**
     * 首页简介对应文档ID
     */
    private String documentId;

    /**
     * 加入集团标准
     */
    private String standard;

    /**
     * 集团会话组id
     **/
    private String gid;

    /**
     * 医院名称（冗余）
     */
    private String hospitalName;
    /**
     * 科室名称（冗余）
     */
    private String deptName;
    /**
     * 子科室名称
     */
    private String childName;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public List<Integer> getExpertIds() {
        return expertIds;
    }

    public void setExpertIds(List<Integer> expertIds) {
        this.expertIds = expertIds;
    }

    public List<String> getDiseaselist() {
        return diseaselist;
    }

    public void setDiseaselist(List<String> diseaselist) {
        this.diseaselist = diseaselist;
    }

    public GroupConfig getConfig() {
        return config;
    }

    public void setConfig(GroupConfig config) {
        this.config = config;
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

    public Integer getCureNum() {
        return cureNum;
    }

    public void setCureNum(Integer cureNum) {
        this.cureNum = cureNum;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public GroupCertification getGroupCert() {
        return groupCert;
    }

    public void setGroupCert(GroupCertification certInfo) {
        this.groupCert = certInfo;
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(String cartStatus) {
        this.certStatus = cartStatus;
    }

    public Integer getOutpatientPrice() {
        return outpatientPrice;
    }

    public void setOutpatientPrice(Integer outpatientPrice) {
        this.outpatientPrice = outpatientPrice;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Map<String, Object> getGroupUser() {
        return groupUser;
    }

    public void setGroupUser(Map<String, Object> groupUser) {
        this.groupUser = groupUser;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Long getProcessVTime() {
        return processVTime;
    }

    public void setProcessVTime(Long processVTime) {
        this.processVTime = processVTime;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
