package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.pack.illhistory.entity.po.RecordCheckItem;
import com.dachen.util.BeanUtil;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class RecordCheckItemVo {

    /**检查项的id**/
    private String checkItemId;
    /**创建人**/
    private Integer creater;

    /**检查项的时间**/
    private Long time;

    /**创建人姓名**/
    private String createrName;

    /**创建人类型1患者，3医生**/
    private Integer createrType;

    /**图片列表**/
    private List<String> pics;
    /**备注信息**/
    private String info;

    /**检查项的id**/
    private String checkupId;

    /**患者是否允许查看**/
    private Boolean isSecret;

    public String getCheckupId() {
        return checkupId;
    }

    public void setCheckupId(String checkupId) {
        this.checkupId = checkupId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCheckItemId() {
        return checkItemId;
    }

    public void setCheckItemId(String checkItemId) {
        this.checkItemId = checkItemId;
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

    public static RecordCheckItemVo fromRecordCheckItem(RecordCheckItem recordCheckItem) {
        return BeanUtil.copy(recordCheckItem, RecordCheckItemVo.class);
    }

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

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
