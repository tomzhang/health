package com.dachen.health.activity.invite.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.entity.CircleInvite;
import com.dachen.health.activity.invite.form.CircleInviteForm;
import com.dachen.health.activity.invite.service.ActivityService;
import com.dachen.health.activity.invite.service.CircleInviteReportService;
import com.dachen.health.activity.invite.service.CircleInviteService;
import com.dachen.health.activity.invite.vo.MobileInviteVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Model(value = CircleInvite.class)
@Service
public class CircleInviteServiceImpl extends BaseServiceImpl implements CircleInviteService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //活动邀请注册医生页面放在七牛服务器上的地址
    private static final String QINIU_ACTIVITY_INVITE_REGISTER_URL = "https://activity.mediportal.com.cn/regitst/doctorRegister/registrationActivity.html";

    @Autowired
    protected UserManager userManager;
    @Autowired
    protected IGroupDao groupDao;
    @Resource
    protected IBaseDataService baseDataService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;
    @Autowired
    private CircleInviteReportService circleInviteReportService;

    private String getDoctorInviteNote(User doctor, String shortUrl) {
        //我是{0}，我诚挚的邀请你加入医生圈，新用户注册认证后即可获得100学分，点击{1}赶快加入吧！
        return baseDataService.toContent("1099", doctor.getName(), shortUrl)+"【医生圈】";
    }

    private String getGroupInviteNote(User doctor, Group group, String shortUrl) {
        //我是{0}，我诚挚的邀请你加入{1}，赶紧点击{2}加入吧！
        return baseDataService.toContent("1100", doctor.getName(), group.getName(), shortUrl)+"【医生圈】";
    }

    private String getDoctorInviteShortUrl(Integer doctorId, String inviteActivityId,
        String registerActivityId, String way, Integer subsystem) throws HttpApiException {
        String tag = "getDoctorInviteShortUrl";
        StringBuilder sb = new StringBuilder();
//        sb.append(this.getConfigValue("invite.url"));
//        sb.append(this.getConfigValue("invite.joinDoctorCircle"));
        sb.append(this.getConfigValue("activity.invite.qiniu.url"));

        sb.append("?");
        sb.append("inviterId=");//邀请人Id
        sb.append(doctorId);
        sb.append("&inviteActivityId=");//邀请活动Id
        sb.append(inviteActivityId);
        sb.append("&registerActivityId=");//注册活动Id
        sb.append(registerActivityId);
        sb.append("&subsystem=");//来源子系统
        sb.append(subsystem);
        sb.append("&way=");//邀请方式
        sb.append(way);
        sb.append("&ts=");//时间戳
        sb.append(System.currentTimeMillis());

        String url = sb.toString();
        logger.debug("{}. url={}", tag, url);

        return url;
    }


    private String getActivityDoctorInviteShortUrl(Integer doctorId, String inviteActivityId,
                                           String registerActivityId, String way, Integer subsystem) throws HttpApiException {
        String tag = "getActivityDoctorInviteShortUrl";
        StringBuilder sb = new StringBuilder();
//        sb.append(this.getConfigValue("invite.url"));
//        sb.append(this.getConfigValue("invite.joinDoctorCircle"));
        sb.append(this.getConfigValue("wwh.invite.qiniu.url"));

        sb.append("?");
        sb.append("inviterId=");//邀请人Id
        sb.append(doctorId);
        sb.append("&inviteActivityId=");//邀请活动Id
        sb.append(inviteActivityId);
        sb.append("&registerActivityId=");//注册活动Id
        sb.append(registerActivityId);
        sb.append("&subsystem=");//来源子系统
        sb.append(subsystem);
        sb.append("&way=");//邀请方式
        sb.append(way);
        sb.append("&ts=");//时间戳
        sb.append(System.currentTimeMillis());

        String url = sb.toString();
        logger.debug("{}. url={}", tag, url);

        return url;
    }

    private String getConfigValue(String key) {
        return PropertiesUtil.getContextProperty(key);
    }

    @Override
    public MobileInviteVO createInviteShortUrl(Integer doctorId, String inviteActivityId,
        String registerActivityId, String way, Integer subsystem) throws HttpApiException {
        User doctor = userManager.getUser(doctorId);

        String longUrl = this.getDoctorInviteShortUrl(doctorId, inviteActivityId, registerActivityId, way, subsystem);
        String shortUrl = shortUrlComponent.generateShortUrl(longUrl);
        String note = this.getDoctorInviteNote(doctor, shortUrl);

        return new MobileInviteVO(note, shortUrl, longUrl);
    }

    @Override
    public MobileInviteVO createInviteShortUrl(Integer doctorId, String groupId, String way,
        Integer subsystem) throws HttpApiException {
        User doctor = userManager.getUser(doctorId);
        String longUrl = this.getDeptInviteShortUrl(doctorId, groupId, way, subsystem);
        String shortUrl = shortUrlComponent.generateShortUrl(longUrl);
        Group group = groupDao.getById(groupId);
        if (null == group) {
            throw new ServiceException("圈子不存在:" + groupId);
        }
        String note = this.getGroupInviteNote(doctor, group, shortUrl);

        return new MobileInviteVO(note, shortUrl, longUrl);
    }

    @Override
    public MobileInviteVO createActivityDoctorShortUrl(Integer doctorId, String inviteActivityId,
                                                       String registerActivityId, String way, Integer subsystem) throws HttpApiException {
        User doctor = userManager.getUser(doctorId);

        String longUrl = this.getActivityDoctorInviteShortUrl(doctorId, inviteActivityId, registerActivityId, way, subsystem);
        String shortUrl = shortUrlComponent.generateShortUrl(longUrl);
        String note = "";

        return new MobileInviteVO(note, shortUrl, longUrl);
    }


    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    private String getDeptInviteShortUrl(Integer doctorId, String groupId, String way, Integer subsystem) throws HttpApiException {
        String tag = "getDeptInviteShortUrl";
        StringBuilder sb = new StringBuilder();
        sb.append(this.getConfigValue("invite.url"));
        sb.append(this.getConfigValue("invite.joinDept"));//加入科室H5页面地址

        sb.append("?");
        sb.append("userId=");//邀请人Id
        sb.append(doctorId);
        sb.append("&groupId=");//集团Id
        sb.append(groupId);
        sb.append("&subsystem=");//来源子系统
        sb.append(subsystem);
        sb.append("&way=");//邀请方式
        sb.append(way);

        String url = sb.toString();
        logger.debug("{}. url={}", tag, url);

        return url;
    }

    @Override
    public void save(CircleInviteForm form) {
        CircleInvite circleInvite = form.toCircleInvite();

        //根据子系统设置邀请人姓名
        Integer subsystem = form.getSubsystem();
        if (Source.drugOrg.getIndex() == subsystem){
            List<Integer> userIds = new ArrayList<>();
            userIds.add(form.getUserId());
            try {
                List<CSimpleUser> simpleUsers = drugOrgApiClientProxy.getSimpleUser(userIds);
                if (!CollectionUtils.isEmpty(simpleUsers)) {
                    CSimpleUser simpleUser = simpleUsers.get(0);

                    circleInvite.setUserName(simpleUser.getName());
                }
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (Source.doctorCircle.getIndex() == subsystem){
            User user = userManager.getUser(form.getUserId());
            if (user != null){
                circleInvite.setUserName(user.getName());
            }
        }

        //邀请活动
        Activity activity = activityService.findById(form.getInviteActivityId());
        if (!Objects.isNull(activity)){
            circleInvite.setActivityName(activity.getName());
        }

        Long curTime = System.currentTimeMillis();
        circleInvite.setCreateTime(curTime);
        circleInvite.setUpdateTime(curTime);

        //保存邀请数据
        this.saveEntity(circleInvite);
        //更新邀请报表数据
        circleInviteReportService.updateReport(form.getUserId(), form.getInviteActivityId(), form.getSubsystem(), form.getWay());
    }

    @Override
    public void save(Integer userId, String activityId, String way, Integer subsystem) {
        CircleInviteForm form = new CircleInviteForm();
        form.setUserId(userId);
        form.setInviteActivityId(activityId);
        form.setWay(way);
        form.setSubsystem(subsystem);
        save(form);
    }

}
