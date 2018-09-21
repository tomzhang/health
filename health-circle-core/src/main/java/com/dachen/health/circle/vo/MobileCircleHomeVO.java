package com.dachen.health.circle.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 医生圈首页菜单
 * Created By lim
 * Date: 2017/9/7
 * Time: 11:05
 */
public class MobileCircleHomeVO {


    @ApiModelProperty(value = "关注数")
    private Long followersNumber;
    @ApiModelProperty(value = "粉丝数")
    private Long fansNumber;
    @ApiModelProperty(value = "收藏数")
    private Long collectNumber;

    public Long getFollowersNumber() {
        return followersNumber;
    }

    public void setFollowersNumber(Long followersNumber) {
        this.followersNumber = followersNumber;
    }

    public Long getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(Long fansNumber) {
        this.fansNumber = fansNumber;
    }

    public Long getCollectNumber() {
        return collectNumber;
    }

    public void setCollectNumber(Long collectNumber) {
        this.collectNumber = collectNumber;
    }
}
