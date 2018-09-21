package com.dachen.health.pack.illhistory.entity.po;

import java.util.List;

/**
 * 用药情况
 * Created by fuyongde on 2016/12/14.
 */
public class RecordDrug {

    public static final String DRUG_IM_IMG_URL = "http://7xpy06.com1.z0.glb.clouddn.com/yao_icon_xxhdpi.png";

    private String drugCase;
    private List<String> pics;
    private List<DrugInfo> drugInfos;
    /**用药时间**/
    private Long time;

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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
