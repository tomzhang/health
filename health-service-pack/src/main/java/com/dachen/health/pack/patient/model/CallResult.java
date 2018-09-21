package com.dachen.health.pack.patient.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 回拨结果
 * @author 李淼淼
 * @version 1.0 2015-08-20
 */
@XmlRootElement(name="request")
public class CallResult {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 值为：callhangup
     */
    private String event;

    /**
     * 呼叫的唯一标识
     */
    private String callid;

    /**
     * 开发者账号id
     */
    private String accountid;

    /**
     * 应用id
     */
    private String appid;

    /**
     * 群聊id (仅语音群聊场景)
     */
    private String confid;

    /**
     * 0：直拨，1：免费，2：回拨
     */
    private Byte calltype;

    /**
     * 主叫号码类型，0：Client账号，1：普通电话
     */
    private Byte callertype;

    /**
     * 主叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    private String caller;

    /**
     * 被叫号码类型，0：Client账号，1：普通电话
     */
    private Byte calledtype;

    /**
     * 被叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    private String called;

    /**
     * 开始通话时间。时间格式如：2014-06-16 16:47:28
     */
    private String starttime;

    /**
     * 结束通话时间。时间格式如：2014-06-16 17:31:14
     */
    private String stoptime;

    /**
     * 主叫通话时长(s)
     */
    private Integer length;
    /**
     * 被叫通话时长(s)
     */
    private Integer lengthA;

    /**
     * 通话录音完整下载地址，默认为空。
     */
    private String recordurl;

    /**
     * 用户自定义数据字符串，最大长度128字节
     */
    private String userData;

    /**
     * 挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。
     */
    private String reason;

    /**
     * 挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。
     */
    private String subreason;

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
     * 获取值为：callhangup
     *
     * @return event - 值为：callhangup
     */
    public String getEvent() {
        return event;
    }

    /**
     * 设置值为：callhangup
     *
     * @param event 值为：callhangup
     */
    public void setEvent(String event) {
        this.event = event == null ? null : event.trim();
    }

    /**
     * 获取呼叫的唯一标识
     *
     * @return callid - 呼叫的唯一标识
     */
    public String getCallid() {
        return callid;
    }

    /**
     * 设置呼叫的唯一标识
     *
     * @param callid 呼叫的唯一标识
     */
    public void setCallid(String callid) {
        this.callid = callid == null ? null : callid.trim();
    }

    /**
     * 获取开发者账号id
     *
     * @return accountid - 开发者账号id
     */
    public String getAccountid() {
        return accountid;
    }

    /**
     * 设置开发者账号id
     *
     * @param accountid 开发者账号id
     */
    public void setAccountid(String accountid) {
        this.accountid = accountid == null ? null : accountid.trim();
    }

    /**
     * 获取应用id
     *
     * @return appid - 应用id
     */
    public String getAppid() {
        return appid;
    }

    /**
     * 设置应用id
     *
     * @param appid 应用id
     */
    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    /**
     * 获取群聊id (仅语音群聊场景)
     *
     * @return confid - 群聊id (仅语音群聊场景)
     */
    public String getConfid() {
        return confid;
    }

    /**
     * 设置群聊id (仅语音群聊场景)
     *
     * @param confid 群聊id (仅语音群聊场景)
     */
    public void setConfid(String confid) {
        this.confid = confid == null ? null : confid.trim();
    }

    /**
     * 获取0：直拨，1：免费，2：回拨
     *
     * @return calltype - 0：直拨，1：免费，2：回拨
     */
    public Byte getCalltype() {
        return calltype;
    }

    /**
     * 设置0：直拨，1：免费，2：回拨
     *
     * @param calltype 0：直拨，1：免费，2：回拨
     */
    public void setCalltype(Byte calltype) {
        this.calltype = calltype;
    }

    /**
     * 获取主叫号码类型，0：Client账号，1：普通电话
     *
     * @return callertype - 主叫号码类型，0：Client账号，1：普通电话
     */
    public Byte getCallertype() {
        return callertype;
    }

    /**
     * 设置主叫号码类型，0：Client账号，1：普通电话
     *
     * @param callertype 主叫号码类型，0：Client账号，1：普通电话
     */
    public void setCallertype(Byte callertype) {
        this.callertype = callertype;
    }

    /**
     * 获取主叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     *
     * @return caller - 主叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    public String getCaller() {
        return caller;
    }

    /**
     * 设置主叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     *
     * @param caller 主叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    public void setCaller(String caller) {
        this.caller = caller == null ? null : caller.trim();
    }

    /**
     * 获取被叫号码类型，0：Client账号，1：普通电话
     *
     * @return calledtype - 被叫号码类型，0：Client账号，1：普通电话
     */
    public Byte getCalledtype() {
        return calledtype;
    }

    /**
     * 设置被叫号码类型，0：Client账号，1：普通电话
     *
     * @param calledtype 被叫号码类型，0：Client账号，1：普通电话
     */
    public void setCalledtype(Byte calledtype) {
        this.calledtype = calledtype;
    }

    /**
     * 获取被叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     *
     * @return called - 被叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    public String getCalled() {
        return called;
    }

    /**
     * 设置被叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     *
     * @param called 被叫号码 
            普通电话：18612345678 
            Client号码：60000000000017
     */
    public void setCalled(String called) {
        this.called = called == null ? null : called.trim();
    }

    /**
     * 获取开始通话时间。时间格式如：2014-06-16 16:47:28
     *
     * @return starttime - 开始通话时间。时间格式如：2014-06-16 16:47:28
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * 设置开始通话时间。时间格式如：2014-06-16 16:47:28
     *
     * @param starttime 开始通话时间。时间格式如：2014-06-16 16:47:28
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime == null ? null : starttime.trim();
    }

    /**
     * 获取结束通话时间。时间格式如：2014-06-16 17:31:14
     *
     * @return stoptime - 结束通话时间。时间格式如：2014-06-16 17:31:14
     */
    public String getStoptime() {
        return stoptime;
    }

    /**
     * 设置结束通话时间。时间格式如：2014-06-16 17:31:14
     *
     * @param stoptime 结束通话时间。时间格式如：2014-06-16 17:31:14
     */
    public void setStoptime(String stoptime) {
        this.stoptime = stoptime == null ? null : stoptime.trim();
    }

    /**
     * 获取通话时长(s)
     *
     * @return length - 通话时长(s)
     */
    public Integer getLength() {
        return length;
    }

    /**
     * 设置通话时长(s)
     *
     * @param length 通话时长(s)
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLengthA() {
		return lengthA;
	}

	public void setLengthA(Integer lengthA) {
		this.lengthA = lengthA;
	}

	/**
     * 获取通话录音完整下载地址，默认为空。
     *
     * @return recordurl - 通话录音完整下载地址，默认为空。
     */
    public String getRecordurl() {
        return recordurl;
    }

    /**
     * 设置通话录音完整下载地址，默认为空。
     *
     * @param recordurl 通话录音完整下载地址，默认为空。
     */
    public void setRecordurl(String recordurl) {
        this.recordurl = recordurl == null ? null : recordurl.trim();
    }

    /**
     * 获取用户自定义数据字符串，最大长度128字节
     *
     * @return user_data - 用户自定义数据字符串，最大长度128字节
     */
    public String getUserData() {
        return userData;
    }

    /**
     * 设置用户自定义数据字符串，最大长度128字节
     *
     * @param userData 用户自定义数据字符串，最大长度128字节
     */
    public void setUserData(String userData) {
        this.userData = userData == null ? null : userData.trim();
    }

    /**
     * 获取挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。
     *
     * @return reason - 挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。
     *
     * @param reason 挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。
     */
    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    /**
     * 获取挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。
     *
     * @return subreason - 挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。
     */
    public String getSubreason() {
        return subreason;
    }

    /**
     * 设置挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。
     *
     * @param subreason 挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。
     */
    public void setSubreason(String subreason) {
        this.subreason = subreason == null ? null : subreason.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CallResult other = (CallResult) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEvent() == null ? other.getEvent() == null : this.getEvent().equals(other.getEvent()))
            && (this.getCallid() == null ? other.getCallid() == null : this.getCallid().equals(other.getCallid()))
            && (this.getAccountid() == null ? other.getAccountid() == null : this.getAccountid().equals(other.getAccountid()))
            && (this.getAppid() == null ? other.getAppid() == null : this.getAppid().equals(other.getAppid()))
            && (this.getConfid() == null ? other.getConfid() == null : this.getConfid().equals(other.getConfid()))
            && (this.getCalltype() == null ? other.getCalltype() == null : this.getCalltype().equals(other.getCalltype()))
            && (this.getCallertype() == null ? other.getCallertype() == null : this.getCallertype().equals(other.getCallertype()))
            && (this.getCaller() == null ? other.getCaller() == null : this.getCaller().equals(other.getCaller()))
            && (this.getCalledtype() == null ? other.getCalledtype() == null : this.getCalledtype().equals(other.getCalledtype()))
            && (this.getCalled() == null ? other.getCalled() == null : this.getCalled().equals(other.getCalled()))
            && (this.getStarttime() == null ? other.getStarttime() == null : this.getStarttime().equals(other.getStarttime()))
            && (this.getStoptime() == null ? other.getStoptime() == null : this.getStoptime().equals(other.getStoptime()))
            && (this.getLength() == null ? other.getLength() == null : this.getLength().equals(other.getLength()))
            && (this.getRecordurl() == null ? other.getRecordurl() == null : this.getRecordurl().equals(other.getRecordurl()))
            && (this.getUserData() == null ? other.getUserData() == null : this.getUserData().equals(other.getUserData()))
            && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
            && (this.getSubreason() == null ? other.getSubreason() == null : this.getSubreason().equals(other.getSubreason()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEvent() == null) ? 0 : getEvent().hashCode());
        result = prime * result + ((getCallid() == null) ? 0 : getCallid().hashCode());
        result = prime * result + ((getAccountid() == null) ? 0 : getAccountid().hashCode());
        result = prime * result + ((getAppid() == null) ? 0 : getAppid().hashCode());
        result = prime * result + ((getConfid() == null) ? 0 : getConfid().hashCode());
        result = prime * result + ((getCalltype() == null) ? 0 : getCalltype().hashCode());
        result = prime * result + ((getCallertype() == null) ? 0 : getCallertype().hashCode());
        result = prime * result + ((getCaller() == null) ? 0 : getCaller().hashCode());
        result = prime * result + ((getCalledtype() == null) ? 0 : getCalledtype().hashCode());
        result = prime * result + ((getCalled() == null) ? 0 : getCalled().hashCode());
        result = prime * result + ((getStarttime() == null) ? 0 : getStarttime().hashCode());
        result = prime * result + ((getStoptime() == null) ? 0 : getStoptime().hashCode());
        result = prime * result + ((getLength() == null) ? 0 : getLength().hashCode());
        result = prime * result + ((getRecordurl() == null) ? 0 : getRecordurl().hashCode());
        result = prime * result + ((getUserData() == null) ? 0 : getUserData().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        result = prime * result + ((getSubreason() == null) ? 0 : getSubreason().hashCode());
        return result;
    }
}