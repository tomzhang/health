package com.dachen.health.pack.illhistory.entity.po;

import com.dachen.commons.JSONMessage;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 药品信息（病程中只需要这几个字段，真正的药那里会有一堆字段）
 * Created by fuyongde on 2016/12/14.
 */
public class DrugInfo {
    /**药品id**/
    private String drugId;
    /**药品数量**/
    private Integer drugCount;
    /**药品名称**/
    private String drugName;
    /**规格**/
    private String specification;
    /**包装规格**/
    private String packSpecification;
    /**药品图片链接**/
    private String drugImageUrl;
    /**生产厂家**/
    private String manufacturer;

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public Integer getDrugCount() {
        return drugCount;
    }

    public void setDrugCount(Integer drugCount) {
        this.drugCount = drugCount;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getPackSpecification() {
        return packSpecification;
    }

    public void setPackSpecification(String packSpecification) {
        this.packSpecification = packSpecification;
    }

    public String getDrugImageUrl() {
        return drugImageUrl;
    }

    public void setDrugImageUrl(String drugImageUrl) {
        this.drugImageUrl = drugImageUrl;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public static void main(String[] args) {
        List<DrugInfo> drugInfoList = Lists.newArrayList();
        DrugInfo drugInfo = new DrugInfo();
        drugInfo.setDrugName("云南白药");
        drugInfo.setDrugImageUrl("http://xxx.xxx.com/ynby.jpg");
        drugInfo.setManufacturer("云南制药厂");
        drugInfo.setSpecification("10g");
        drugInfo.setPackSpecification("1瓶");

        DrugInfo drugInfo2 = new DrugInfo();
        drugInfo2.setDrugName("云南黑药");
        drugInfo2.setDrugImageUrl("http://xxx.xxx.com/ynhy.jpg");
        drugInfo2.setManufacturer("云南制药厂");
        drugInfo2.setSpecification("10g");
        drugInfo2.setPackSpecification("1瓶");

        drugInfoList.add(drugInfo);
        drugInfoList.add(drugInfo2);
        System.out.println(JSONMessage.toJSON(drugInfoList));
    }
}
