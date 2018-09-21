package com.dachen.health.pack.pack.entity.po;

/**
 * @author 谢佩
 * @version 1.0 2015-12-10
 */
public class PackDrug {
    /**
     * 主键
     */
    private Integer id;

    private Integer packId;

    private String drugId;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return pack_id
     */
    public Integer getPackId() {
        return packId;
    }

    /**
     * @param packId
     */
    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    /**
     * @return drug_id
     */
    public String getDrugId() {
        return drugId;
    }

    /**
     * @param drugId
     */
    public void setDrugId(String drugId) {
        this.drugId = drugId == null ? null : drugId.trim();
    }
}