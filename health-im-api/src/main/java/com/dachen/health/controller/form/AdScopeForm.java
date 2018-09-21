package com.dachen.health.controller.form;

import com.dachen.health.circle.form.AreaInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 广告文章范围
 * Created By lim
 * Date: 2017/6/9
 * Time: 10:46
 */
public class AdScopeForm {

    /**
     * 选择筛选范围类型
     *  1：全平台
     *  2：按条件筛选可领取范围
     *  3: 按组织筛选可领取范围
     */
    private String type;
    /**
     * 省级 市级id数组
     */
    private String areaJson;
    /**
     * 医院级别名称数组
     */
    private String[] levels;
    /**
     * 科室名称数组
     */
    private String[] deptIds;
    /**
     * 医生职称名称
     */
    private String[] titles;

    /**
     * 圈子id
     */
    private String[] groupIds;

    /**
     * 医联体id
     */
    private String[] unionIds;

    /**
     * 是否筛选已认证状态用户
     */
    private Boolean userCheck;

    public AdScopeForm() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAreaJson() {
        return areaJson;
    }

    public void setAreaJson(String areaJson) {
        this.areaJson = areaJson;
    }

    public String[] getLevels() {
        return levels;
    }

    public void setLevels(String[] levels) {
        this.levels = levels;
    }

    public String[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String[] deptIds) {
        this.deptIds = deptIds;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public String[] getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String[] groupIds) {
        this.groupIds = groupIds;
    }

    public String[] getUnionIds() {
        return unionIds;
    }

    public void setUnionIds(String[] unionIds) {
        this.unionIds = unionIds;
    }

    public Boolean getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(Boolean userCheck) {
        this.userCheck = userCheck;
    }
}
