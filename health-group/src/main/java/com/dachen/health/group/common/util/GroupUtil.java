package com.dachen.health.group.common.util;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.dachen.health.group.department.entity.vo.DepartmentVO;
import org.jsoup.helper.StringUtil;

import com.dachen.commons.constants.Constants;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.util.PropertiesUtil;

public class GroupUtil {

    //平台ID
    public static final String PLATFORM_ID = Constants.Id.PLATFORM_ID;
    //平台Name
    public static final String PLATFORM_NAME = "系统平台";

    public static final String GROUP_DUTY_START_TIME = "08:00";

    public static final String GROUP_DUTY_END_TIME = "23:00";

    //	public static final String GROUP_PAGE_URL = UserUtil.GROUP_PAGE_URL;
    public static final String GROUP_PAGE_URL() {
        return UserUtil.GROUP_PAGE_URL();
    }

    public static String getInviteMemberImage() {
        return PropertiesUtil.getHeaderPrefix() + "/" + "default/" + PropertiesUtil.getContextProperty("msg.inviteMember");
    }

    public static String getInviteAdminImage() {
        return PropertiesUtil.getHeaderPrefix() + "/" + "default/" + PropertiesUtil.getContextProperty("msg.inviteAdmin");
    }

    public static String getApproveMemberImage() {
        return PropertiesUtil.getHeaderPrefix() + "/" + "default/" + PropertiesUtil.getContextProperty("msg.approveMenber");
    }

    public static String getPingImage() {
        return PropertiesUtil.getHeaderPrefix() + "/" + "default/" + PropertiesUtil.getContextProperty("msg.ping");
    }


    /**
     * 集团医生是否可值班
     *
     * @param dutyStartTime
     * @param dutyEndTime
     * @return
     */
    public static boolean isCanOnDuty(String dutyStartTime, String dutyEndTime) {
        if (StringUtil.isBlank(dutyStartTime) && StringUtil.isBlank(dutyEndTime))
            return true;
        int now = getCurrentTime();
        int startTime = toTime(dutyStartTime);
        int endTime = toTime(dutyEndTime);
        if (startTime <= now && now <= endTime) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前时分
     *
     * @return
     */
    public static int getCurrentTime() {
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        //c.get(Calendar.HOUR_OF_DAY); // 24小时制
        //小时：
        int hh = now.get(Calendar.HOUR_OF_DAY);
        // 分钟：
        int mm = now.get(Calendar.MINUTE);
        // 转成分钟
        return hh * 60 + mm;
    }

    /**
     * 集团值班时间转换
     *
     * @param dutyTime
     * @return
     */
    public static int toTime(String dutyTime) {
        String[] dutyTimes = dutyTime.split(":");
        int hh = Integer.valueOf(dutyTimes[0]);
        int mm = Integer.valueOf(dutyTimes[1]);
        return hh * 60 + mm;
    }


    public static String findFullNameById(List<DepartmentVO> departList, String id) {
        if (null == departList || 0 == departList.size()) {
            return null;
        }
        String fullName = null;
        for (DepartmentVO depart : departList) {
            if (depart.getId().equals(id)) {
                //如果父节点不为“0”，这说明要继续查找
                if (!depart.getParentId().equals("0")) {
                    fullName = findFullNameById(departList, depart.getParentId()) + "/" + depart.getName();
                } else {
                    fullName = depart.getName();
                }
                break;
            }

        }
        return fullName;
    }
}
