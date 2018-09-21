package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.pack.illhistory.entity.po.DrugInfo;
import com.dachen.health.pack.illhistory.entity.po.RecordDrug;
import com.dachen.util.BeanUtil;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class RecordDrugVo {

    private String drugCase;
    private List<String> pics;
    private List<DrugInfo> drugInfos;

    /**用药时间**/
    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDrugCase() {
        return drugCase;
    }

    public void setDrugCase(String drugCase) {
        this.drugCase = drugCase;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public List<DrugInfo> getDrugInfos() {
        return drugInfos;
    }

    public void setDrugInfos(List<DrugInfo> drugInfos) {
        this.drugInfos = drugInfos;
    }

    public static RecordDrugVo fromRecordDrug(RecordDrug recordDrug) {
        return BeanUtil.copy(recordDrug, RecordDrugVo.class);
    }
}
