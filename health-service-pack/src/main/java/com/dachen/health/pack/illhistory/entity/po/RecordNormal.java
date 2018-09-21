package com.dachen.health.pack.illhistory.entity.po;

import java.util.List;

/**
 * 正常的手动添加的病程
 * Created by fuyongde on 2016/12/13.
 */
public class RecordNormal {
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

}
