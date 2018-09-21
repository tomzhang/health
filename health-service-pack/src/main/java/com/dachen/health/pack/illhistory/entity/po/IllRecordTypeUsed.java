package com.dachen.health.pack.illhistory.entity.po;

import com.dachen.health.pack.illhistory.entity.vo.RecordTypeVo;
import org.mongodb.morphia.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * 病程类型
 * Created by fuyongde on 2016/12/5.
 */
@Entity(value = "t_ill_record_type_userd", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "parent"), @Field(value = "name") }) })
public class IllRecordTypeUsed {

    @Id
    private String id;
    /**医生的Id**/
    private Integer doctorId;
    private List<RecordTypeVo> recordTypeVos;
    /**创建时间**/
    private Long createTime;
    /**更新时间**/
    private Long updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public List<RecordTypeVo> getRecordTypeVos() {
        return recordTypeVos;
    }

    public void setRecordTypeVos(List<RecordTypeVo> recordTypeVos) {
        this.recordTypeVos = recordTypeVos;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
