package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.pack.illhistory.entity.po.RecordNormal;
import com.dachen.util.BeanUtil;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class RecordNormalVo {

    /**recordTypeId**/
    private String recordType;
    /**名称**/
    private String recordName;
    /**时间**/
    private Long time;
    /**图片信息**/
    private List<String> pics;
    /**病程信息**/
    private String info;
    /**是否允许患者查看**/
    private Boolean isSecret;
    /**添加人**/
    private Integer creater;
    /**添加人姓名**/
    private String createrName;
    /**添加人类型1患者，3医生**/
    private Integer createrType;

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Integer getCreaterType() {
        return createrType;
    }

    public void setCreaterType(Integer createrType) {
        this.createrType = createrType;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Integer getCreater() {
        return creater;
    }

    public void setCreater(Integer creater) {
        this.creater = creater;
    }

    public Boolean getSecret() {
        return isSecret;
    }

    public void setSecret(Boolean secret) {
        isSecret = secret;
    }

    public static RecordNormalVo fromRecordNormal(RecordNormal recordNormal) {
        return BeanUtil.copy(recordNormal, RecordNormalVo.class);
    }
}
