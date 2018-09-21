package com.dachen.health.commons.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.permission.entity.po.Role;
import com.dachen.health.user.entity.po.*;
import com.dachen.util.DateUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity(value = "user")
public class User {

    @Id
    private Integer userId;// 用户Id

    @NotSaved
    private String openId;

    @JSONField(serialize = false)
    private String username;// 用户名
    /**
     *
     */
    @JSONField(serialize = false)
    private String password;

    @Indexed(value = IndexDirection.ASC)
    private Integer userType;// 用户类型

    @Indexed(value = IndexDirection.ASC)
    private String telephone;

    private String name;// 姓名

    private Long birthday;// 生日

    private Integer sex;// 性别1男，2女 3 保密

    @Indexed(value = IndexDirection.ASC)
    private Integer active;// 最后出现时间

    @Indexed(value = IndexDirection.GEO2D)
    private Loc loc;// 地理位置

    private String description;// 签名、说说、备注
    @Indexed(value = IndexDirection.DESC)
    private Long createTime;
    private Long modifyTime;

    /* 区域 */
    private String area;

    private String remarks;

    /**
     * 医生审核结果
     *
     * @see UserEnum.UserStatus
     */
    @Indexed(value = IndexDirection.ASC)
    private Integer status;

    /**
     * 医生加入医院状态
     *
     * @author wangqiao
     * @date 2016年3月26日
     */
    private Integer hospitalStatus;

    //医生提价认证的时间
    private Long submitTime;

    // ********************引用字段********************
    private @NotSaved
    LoginLog loginLog;
    private @NotSaved
    UserSettings settings;
    /* 集团及集团科室列表 */
    private @NotSaved
    List<Map<String, Object>> groupList;

    /**
     * 医院以及医院科室列表
     *
     * @author wangqiao
     * @date 2016年3月26日
     */
    private @NotSaved
    List<Map<String, Object>> hospitalList;

    /**
     * 科室列表
     **/
    private @NotSaved
    List<Map<String, Object>> deptList;

    private @NotSaved
    Map<String, Object> platform;

    /*集团中医生联系方式和备注*/
    private @NotSaved
    Integer groupSame; //同一集团
    private @NotSaved
    String groupContact;
    private @NotSaved
    String groupRemark;

    private @NotSaved
    String is3A;//医院是否三甲

    /**
     * 登录的医生集团id（多集团改造添加）
     * add by wangqiao
     */
    private @NotSaved
    String loginGroupId;

    @Embedded
    private Nurse nurse;// add by liwei 
    @Embedded
    private Doctor doctor;
    @Embedded
    private DoctorGuider doctorGuider;//add by cqy

    @Embedded
    private Assistant assistant;

    @Embedded
    private UserConfig userConfig;

    private @NotSaved
    String[] tags;
    /**
     * 年龄
     */
    private int age;
    /**
     * 头像名称
     */
    private String headPicFileName;

    /**
     * 云通讯
     * {@link VOIP}
     */
    @Embedded
    private VOIP voip;
    //医生是否接受提醒
    @Embedded
    private Integer receiveRemind;

    /**
     * 是否需要重置密码（true表示是，false表示否）
     **/
    private Boolean needResetPassword;
    /**
     * 用户的来源信息
     **/
    @Embedded
    private UserSource source;

    @Embedded
    private WeChatUserInfo weInfo;

    private @NotSaved
    String recommendId;

    // ********************引用字段********************

    //社区权限:0-普通管理员，1-社区管理员
    private Integer communityRole;

    /**
     * 农牧项目相关的参数
     **/
    @Embedded
    private Farm farm;

    //微信公众号注册登陆后更名 1为可更名
    private Integer rename;

    /**
     * 最后一次登录的时间
     **/
    private Long lastLoginTime;

    /**
     * 用户邮箱
     **/
    private String email;

    /**
     * 是否赠送100学币 第一次认证通过赠送  1:已赠送
     * @return
     */
    private Integer giveCoin;

    /**
     * 用户级别 0到期 1游客 2临时用户 3认证用户
     * @return
     */
    private Integer userLevel;

    /**
     * 用户级别有效期
     */
    private Long limitedPeriodTime;

    /**
     * 角色Ids
     */
    private List<String> roleIds;

    /**
     * 角色列表
     */
    @NotSaved
    private List<Role> roleList;

    /**
     * 区别于用户审核
     * 挂起或禁用用户（0表示正常状态，1表示挂起状态，4表示暂时禁用状态）
     */
    private Integer suspend = 0;//默认值

    private SuspendInfo suspendInfo;

    /* 参加工作时间 */
    private Long workTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public SuspendInfo getSuspendInfo() {
        return suspendInfo;
    }

    public void setSuspendInfo(SuspendInfo suspendInfo) {
        this.suspendInfo = suspendInfo;
    }

    public Integer getSuspend() { return suspend; }

    public void setSuspend(Integer suspend) { this.suspend = suspend; }

    public Integer getGiveCoin() {
        return giveCoin;
    }

    public void setGiveCoin(Integer giveCoin) {
        this.giveCoin = giveCoin;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Boolean getNeedResetPassword() {
        return needResetPassword;
    }

    public void setNeedResetPassword(Boolean needResetPassword) {
        this.needResetPassword = needResetPassword;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public Boolean getNeedReSetPassword() {
        return needResetPassword;
    }

    public void setNeedReSetPassword(Boolean needReSetPassword) {
        this.needResetPassword = needReSetPassword;
    }

    public UserSource getSource() {
        return source;
    }

    public void setSource(UserSource source) {
        this.source = source;
    }

    public WeChatUserInfo getWeInfo() {
        return weInfo;
    }

    public void setWeInfo(WeChatUserInfo weInfo) {
        this.weInfo = weInfo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LoginLog getLoginLog() {
        return loginLog;
    }

    public void setLoginLog(LoginLog loginLog) {
        this.loginLog = loginLog;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public List<Map<String, Object>> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Map<String, Object>> groupList) {
        this.groupList = groupList;
    }

    public Map<String, Object> getPlatform() {
        return platform;
    }

    public void setPlatform(Map<String, Object> platform) {
        this.platform = platform;
    }

    public Integer getGroupSame() {
        return groupSame;
    }

    public void setGroupSame(Integer groupSame) {
        this.groupSame = groupSame;
    }

    public String getGroupContact() {
        return groupContact;
    }

    public void setGroupContact(String groupContact) {
        this.groupContact = groupContact;
    }

    public String getGroupRemark() {
        return groupRemark;
    }

    public void setGroupRemark(String groupRemark) {
        this.groupRemark = groupRemark;
    }

    public String getIs3A() {
        return is3A;
    }

    public void setIs3A(String is3a) {
        is3A = is3a;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public boolean ifDoctor() {
        if (this.userType == UserEnum.UserType.doctor.getIndex()) {
            return true;
        }
        return false;
    }

    public DoctorGuider getDoctorGuider() {
        return doctorGuider;
    }

    public void setDoctorGuider(DoctorGuider doctorGuider) {
        this.doctorGuider = doctorGuider;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }


    public static class Count {
        private int att;
        private int fans;
        private int friends;

        public int getAtt() {
            return att;
        }

        public int getFans() {
            return fans;
        }

        public int getFriends() {
            return friends;
        }

        public void setAtt(int att) {
            this.att = att;
        }

        public void setFans(int fans) {
            this.fans = fans;
        }

        public void setFriends(int friends) {
            this.friends = friends;
        }
    }

    public static class ThridPartyAccount {

        private long createTime;
        private long modifyTime;
        private int status;// 状态（0：解绑；1：绑定）
        private String tpAccount;// 账号
        private String tpName;// 帐号所属平台名字或代码
        private String tpUserId;// 账号唯一标识

        public long getCreateTime() {
            return createTime;
        }

        public long getModifyTime() {
            return modifyTime;
        }

        public int getStatus() {
            return status;
        }

        public String getTpAccount() {
            return tpAccount;
        }

        public String getTpName() {
            return tpName;
        }

        public String getTpUserId() {
            return tpUserId;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setModifyTime(long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setTpAccount(String tpAccount) {
            this.tpAccount = tpAccount;
        }

        public void setTpName(String tpName) {
            this.tpName = tpName;
        }

        public void setTpUserId(String tpUserId) {
            this.tpUserId = tpUserId;
        }

    }

    public static class LoginLog {
        private int isFirstLogin;
        private long loginTime;
        private String apiVersion;
        private String osVersion;
        private String model;
        private String serial;
        private double latitude;
        private double longitude;
        private String location;
        private String address;
        private long offlineTime;

        public static DBObject init(UserExample example, boolean isFirst) {
            DBObject jo = new BasicDBObject();
            jo.put("isFirstLogin", isFirst ? 1 : 0);
            jo.put("loginTime", DateUtil.currentTimeSeconds());
            jo.put("apiVersion", example.getApiVersion());
            jo.put("osVersion", example.getOsVersion());
            jo.put("model", example.getModel());
            jo.put("serial", example.getSerial());
            jo.put("latitude", example.getLatitude());
            jo.put("longitude", example.getLongitude());
            jo.put("location", example.getLocation());
            jo.put("address", example.getAddress());
            jo.put("offlineTime", 0);

            return jo;
        }

        public int getIsFirstLogin() {
            return isFirstLogin;
        }

        public void setIsFirstLogin(int isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        public long getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(long loginTime) {
            this.loginTime = loginTime;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public long getOfflineTime() {
            return offlineTime;
        }

        public void setOfflineTime(long offlineTime) {
            this.offlineTime = offlineTime;
        }

    }

    public static class UserSettings {
        /**
         * 允许关注
         */
        private Integer allowAtt;
        /**
         * 允许打招呼
         */
        private Integer allowGreet;
        /**
         * 好友添加验证
         */
        private Integer friendsVerify;

        /**
         * 是否接收通知：1正常接收，2不接收
         */
        private Integer ispushflag;

        /**
         * 需要医助1 需要 ，2不需要
         */
        private Integer needAssistant;

        /**
         * 需要医助1 是 ，2否
         */
        private Integer patientVerify;

        /**
         * 需要医助1 是 ，2否
         */
        private Integer doctorVerify;

        /**
         * 显示消息详情1是，2否
         */
        private Integer dispMsgDetail;

        public Integer getAllowAtt() {
            return allowAtt;
        }

        public Integer getAllowGreet() {
            return allowGreet;
        }

        public Integer getFriendsVerify() {
            return friendsVerify;
        }

        public void setAllowAtt(Integer allowAtt) {
            this.allowAtt = allowAtt;
        }

        public void setAllowGreet(Integer allowGreet) {
            this.allowGreet = allowGreet;
        }

        public void setFriendsVerify(int friendsVerify) {
            this.friendsVerify = friendsVerify;
        }

        public Integer getIspushflag() {
            return ispushflag;
        }

        public void setIspushflag(Integer ispushflag) {
            this.ispushflag = ispushflag;
        }

        public Integer getNeedAssistant() {
            return needAssistant;
        }

        public void setNeedAssistant(Integer needAssistant) {
            this.needAssistant = needAssistant;
        }


        public Integer getPatientVerify() {
            return patientVerify;
        }

        public void setPatientVerify(Integer patientVerify) {
            this.patientVerify = patientVerify;
        }

        public Integer getDoctorVerify() {
            return doctorVerify;
        }

        public void setDoctorVerify(Integer doctorVerify) {
            this.doctorVerify = doctorVerify;
        }

        public Integer getDispMsgDetail() {
            return dispMsgDetail;
        }

        public void setDispMsgDetail(Integer dispMsgDetail) {
            this.dispMsgDetail = dispMsgDetail;
        }

        public static DBObject getDefault() {
            // UserSettings settings = new UserSettings();
            // settings.setAllowAtt(1);// 允许关注
            // settings.setAllowGreet(1);// 允许打招呼
            // settings.setFriendsVerify(1);// 加好友需验证
            DBObject dbObj = new BasicDBObject();
            dbObj.put("allowAtt", 1);// 允许关注
            dbObj.put("allowGreet", 1);// 允许打招呼
            dbObj.put("friendsVerify", 1);// 加好友不需要验证(改为默认需要验证)
            dbObj.put("ispushflag", 1);//接收通知
            dbObj.put("needAssistant", 1);//需要医助
            dbObj.put("patientVerify", 1);//患者好友验证，1：需要验证，2：不需要验证；
            dbObj.put("doctorVerify", 1);//医生好友验证，1：需要验证，2：不需要验证；
            dbObj.put("dispMsgDetail", 1);//显示消息详情
            return dbObj;
        }

        public static UserSettings getDefaultSetting() {
            UserSettings settings = new UserSettings();
            settings.setAllowAtt(1);// 允许关注
            settings.setAllowGreet(1);// 允许打招呼
            settings.setFriendsVerify(1);// 加好友需验证
            settings.setIspushflag(1);
            settings.setNeedAssistant(1);
            settings.setPatientVerify(1);
            settings.setDoctorVerify(1);
            settings.setDispMsgDetail(1);
            return settings;
        }

        /**
         * </p>校验指定参数值</p>
         *
         * @param i
         * @return
         * @author limiaomiao
         * @date 2015年7月11日
         */
        public boolean verify(Integer i) {
            if (i != null) {
                if (i != 1 && i != 2) {
                    return false;
                }
            }
            return true;
        }

        /**
         * </p>参数校验</p>
         *
         * @return
         * @author limiaomiao
         * @date 2015年7月10日
         */
        public int verifys() {
            int count = 0;
            Field[] fileds = getClass().getDeclaredFields();
            for (Field field : fileds) {
                try {
                    Integer value = (Integer) field.get(this);
                    if (!verify(value)) {
                        throw new ServiceException("" + field.getName() + " 's value " + value + " is not correct");
                    } else {
                        count++;
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return count;
        }


        public static void main(String[] args) {
            UserSettings setting = new UserSettings();
            setting.setAllowAtt(0);

            int count = setting.verifys();
            System.out.println(count);
        }
    }

    /**
     * 坐标
     *
     * @author luorc@www.youjob.co
     */
    public static class Loc {
        public Loc() {
            super();
        }

        public Loc(double lng, double lat) {
            super();
            this.lng = lng;
            this.lat = lat;
        }

        private double lng;// longitude
        private double lat;// latitude

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

    }

    public int getAge() {

        return age;

    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHeadPicFileName() {
        return UserHelper.buildHeaderPicPath(headPicFileName, userId, sex, userType);
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public VOIP getVoip() {
        return voip;
    }

    public void setVoip(VOIP voip) {
        this.voip = voip;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Nurse getNurse() {
        return nurse;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    public String getLoginGroupId() {
        return loginGroupId;
    }

    public void setLoginGroupId(String loginGroupId) {
        this.loginGroupId = loginGroupId;
    }

    public Integer getReceiveRemind() {
        return receiveRemind;
    }

    public void setReceiveRemind(Integer receiveRemind) {
        this.receiveRemind = receiveRemind;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getHospitalStatus() {
        return hospitalStatus;
    }

    public void setHospitalStatus(Integer hospitalStatus) {
        this.hospitalStatus = hospitalStatus;
    }

    public List<Map<String, Object>> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<Map<String, Object>> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<Map<String, Object>> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<Map<String, Object>> deptList) {
        this.deptList = deptList;
    }

    public String getAgeStr() {
        if (birthday != null) {
            int ages = DateUtil.calcAge(birthday);
            if (ages == 0 || ages == -1) {
                return DateUtil.calcMonth(birthday) <= 0 ? "1个月" : DateUtil.calcMonth(birthday) + "个月";
            }
            return ages + "岁";
        } else {
            return null;
        }
    }

    public String getRecommendId() {
        return recommendId;
    }

    public void setRecommendId(String recommendId) {
        this.recommendId = recommendId;
    }


    public Integer getCommunityRole() {
        return communityRole;
    }

    public void setCommunityRole(Integer communityRole) {
        this.communityRole = communityRole;
    }

    public Integer getRename() {
        return rename;
    }

    public void setRename(Integer rename) {
        this.rename = rename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserLevel() {
    	if(Objects.nonNull(getLimitedPeriodTime())&&getLimitedPeriodTime()<System.currentTimeMillis()) {
			return UserEnum.UserLevel.Expire.getIndex();
		}
    	return userLevel;
	}
   
	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

    public Integer getBaseUserLevel() {
        return userLevel;
    }
    
    public Long getLimitedPeriodTime() {
		return limitedPeriodTime;
	}

	public void setLimitedPeriodTime(Long limitedPeriodTime) {
		this.limitedPeriodTime = limitedPeriodTime;
	}

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

}
