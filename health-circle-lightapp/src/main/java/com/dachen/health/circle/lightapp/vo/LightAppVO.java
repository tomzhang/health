package com.dachen.health.circle.lightapp.vo;

/**
 * @author sharp
 * @desc
 * @date:2017/6/1216:13 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class LightAppVO {

    private String appId;

    private String lightAppId;
    private String lightAppName;
    private String lightAppDesc;
    private String lightAppPic;
    private String newPic;
    private String bgPic;
    /** WEB协议 */
    private String webProtocol;
    /** IOS端协议 */
    private String iosProtocol;
    /** Android端协议 */
    private String androidProtocol;

    private String minVersion;
    private Integer sort;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLightAppId() {
        return lightAppId;
    }

    public void setLightAppId(String lightAppId) {
        this.lightAppId = lightAppId;
    }

    public String getLightAppName() {
        return lightAppName;
    }

    public void setLightAppName(String lightAppName) {
        this.lightAppName = lightAppName;
    }

    public String getLightAppDesc() {
        return lightAppDesc;
    }

    public void setLightAppDesc(String lightAppDesc) {
        this.lightAppDesc = lightAppDesc;
    }

    public String getLightAppPic() {
        return lightAppPic;
    }

    public void setLightAppPic(String lightAppPic) {
        this.lightAppPic = lightAppPic;
    }

    public String getNewPic() {
        return newPic;
    }

    public void setNewPic(String newPic) {
        this.newPic = newPic;
    }

    public String getBgPic() {
        return bgPic;
    }

    public void setBgPic(String bgPic) {
        this.bgPic = bgPic;
    }

    public String getWebProtocol() {
        return webProtocol;
    }

    public void setWebProtocol(String webProtocol) {
        this.webProtocol = webProtocol;
    }

    public String getIosProtocol() {
        return iosProtocol;
    }

    public void setIosProtocol(String iosProtocol) {
        this.iosProtocol = iosProtocol;
    }

    public String getAndroidProtocol() {
        return androidProtocol;
    }

    public void setAndroidProtocol(String androidProtocol) {
        this.androidProtocol = androidProtocol;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
