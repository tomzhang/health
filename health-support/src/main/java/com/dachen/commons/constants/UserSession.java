package com.dachen.commons.constants;

import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-service<br>
 * ClassName： UserEnum<br>
 * Description： 会员枚举类<br>
 *
 * @author fanp
 * @version 1.0.0
 * @crateTime 2015年6月29日
 */
public class UserSession {

    /* 会员id */
    private Integer userId;

    /* 姓名 */
    private String name;

    /* 会员类型 */
    private Integer userType;

    private String telephone;

    private Integer sex;

    /* 医生简要信息     */
    private SimpleDoctorInfo doctor;

    /**
     * 用户头像名称
     */
    private String headPicFileName;

    /* 医助号 */
    private String assistantNum;

    /**
     * 用户的端（1表示玄关，2表示博德嘉联）
     **/
    private String terminal;

    private Long birthday;
    private Integer status;
    private Integer suspend;
    private Long createTime;
    private Long lastLoginTime;

    private Integer userLevel;

    public Integer getSuspend() {
        return suspend;
    }

    public void setSuspend(Integer suspend) {
        this.suspend = suspend;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getAssistantNum() {
        return assistantNum;
    }

    public void setAssistantNum(String assistantNum) {
        this.assistantNum = assistantNum;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public SimpleDoctorInfo getDoctor() {
        return doctor;
    }

    public void setDoctor(SimpleDoctorInfo doctor) {
        this.doctor = doctor;
    }

    public String getHeadImgPath() {
        if (StringUtil.isEmpty(headPicFileName)) {
            //TODO 改成七牛默认地址
            //return PropertiesUtil.addUrlPrefix("/avatar/" + PropertiesUtil.getContextProperty("defalut.headerfile"));//默认图片
            return QiniuUtil.DEFAULT_AVATAR();
        } else {
            if (headPicFileName.contains("http://")) {
                return headPicFileName;
            } else if (headPicFileName.contains("/")) {
                return PropertiesUtil.addUrlPrefix(headPicFileName);
            } else {
                return PropertiesUtil.addUrlPrefix("/avatar/o/" + (userId % 10000) + "/" + headPicFileName);
            }
        }
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHeadImgPath(String headPicFileName, Integer userId, Integer sex, Integer userType) {

        if (StringUtil.isEmpty(headPicFileName)) {
            // 改成七牛默认地址
            if ((null != userType) && (userType.intValue() == 3)) {
                if (null != sex) {
                    return (2 == sex) ? QiniuUtil.DEFAULT_AVATAR_FEMALE() : QiniuUtil.DEFAULT_AVATAR_MEN();
                } else {
                    return QiniuUtil.DEFAULT_AVATAR_MEN();
                }
            }
            return QiniuUtil.DEFAULT_AVATAR();
        } else {
            if (headPicFileName.contains("http://")) {
                return headPicFileName;
            } else if (headPicFileName.contains("/")) {
                return PropertiesUtil.addUrlPrefix(headPicFileName);
            } else {
                return PropertiesUtil.addUrlPrefix("/avatar/o/" + (userId % 10000) + "/" + headPicFileName);
            }
        }
    }

}
