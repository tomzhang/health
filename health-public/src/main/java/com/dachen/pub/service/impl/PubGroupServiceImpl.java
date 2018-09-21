package com.dachen.pub.service.impl;

import com.dachen.commons.constants.UserSession;
import com.dachen.pub.dao.PubGroupDAO;
import com.dachen.pub.model.PubTypeEnum;
import com.dachen.pub.model.param.PubMsgParam;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubGroupService;
import com.dachen.pub.util.PubUtils;
import com.dachen.pub.util.PubaccHelper;
import com.dachen.pub.util.UserRoleEnum;
import com.dachen.pub.util.WelcomeMessageHelper;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PubGroupServiceImpl implements PubGroupService {

    @Autowired
    private PubGroupDAO pubGroupDAO;

    @Autowired
    private PubaccHelper pubaccHelper;

    /**
     * 562f1fb04203f3672bae390c
     * 用户加入集团的时候，自动关注集团对应的公共号
     *
     * @param mid 集团Id
     */
    public void addSubUser(String mid, String userId, int userType) throws HttpApiException {
        String pid = PubUtils.getPubIdByMid(userType, mid);
        if (pid == null) {
            return;
        }
        boolean sendMsg = false;
        //医生加入集团发送欢迎消息
        if (userType == 3 && !pubGroupDAO.isCreator(mid, userId)) {
            sendMsg = true;
        }
        pubaccHelper.subscribe(pid, userId, UserRoleEnum.SUBSCRIBE, sendMsg);
    }

    /**
     * 患者关注医生公共号
     */
    public void addSubUser(Integer doctorId, Integer userId) throws HttpApiException {
        String pid = PubUtils.PUB_DOC_PREFIX + doctorId;
        pubaccHelper.subscribe(pid, String.valueOf(userId), UserRoleEnum.SUBSCRIBE, false);
    }

    @Override
    public void addSubUser(Integer doctorId, String groupId) throws HttpApiException {
        String pid = PubUtils.PUB_DEPT + groupId;
        pubaccHelper.subscribe(pid, String.valueOf(doctorId), UserRoleEnum.SUBSCRIBE, false);
    }

    @Override
    public void addSubUser(Integer doctorId, String groupId, boolean sendMsg) throws HttpApiException {
        String pid = PubUtils.PUB_DEPT + groupId;
        pubaccHelper.subscribe(pid, String.valueOf(doctorId), UserRoleEnum.SUBSCRIBE, sendMsg);
    }

    /**
     * 患者取消关注医生公共号
     */
    public void delSubUser(Integer doctorId, Integer userId) throws HttpApiException {
        String pid = PubUtils.PUB_DOC_PREFIX + doctorId;
        pubaccHelper.unsubscribe(pid, String.valueOf(userId), UserRoleEnum.SUBSCRIBE);
    }

    @Override
    public void delSubUser(Integer doctorId, String groupId) throws HttpApiException {
        String pid = PubUtils.PUB_DEPT + groupId;
        pubaccHelper.unsubscribe(pid, String.valueOf(doctorId), UserRoleEnum.SUBSCRIBE);
    }

    /**
     * 当医生退出医生集团的时候，自动取消关注的集团公共号
     */
    public void delSubUser(String mid, String userId, int userType) throws HttpApiException {
        String pid = PubUtils.getPubIdByMid(userType, mid);
        pubaccHelper.unsubscribe(pid, String.valueOf(userId), UserRoleEnum.SUBSCRIBE);
    }

    public PubPO createDoctorPub(Integer doctorId, String note, Integer role) throws HttpApiException {
        UserSession user = ReqUtil.instance.getUser(doctorId);
        if (user == null) {
            return null;
        }
        String userId = String.valueOf(doctorId);
        String photoUrl = user.getHeadImgPath();
        PubParam pubParam = new PubParam();
        pubParam.setPid(PubUtils.PUB_DOC_PREFIX + doctorId);
        pubParam.setName("医生公众号_" + user.getName());//
        pubParam.setNickName(user.getName());
        pubParam.setNote(note);
        pubParam.setRtype(PubTypeEnum.PUB_DOCTOR.getValue());
        pubParam.setPhotourl(photoUrl);
//		pubParam.setCreator(userId);
        pubParam.setDefault(true);

        Map<Integer, List<String>> pubUsers = new HashMap<Integer, List<String>>();
        List<String> speakers = new ArrayList<String>();
        speakers.add(userId);
        pubUsers.put(UserRoleEnum.SPEAKER.getValue(), speakers);
        pubParam.setPubUsers(pubUsers);

        PubPO pub = pubaccHelper.createPub(pubParam);
        //无法引入DoctorRole，暂时用数字代替
        if (role != null && role == 1) {

            List<String> to = new ArrayList<String>(1);
            to.add(userId);

            PubMsgParam pubMsg = new PubMsgParam();
            pubMsg.setModel(2);//单图文消息
            pubMsg.setPubId(pub.getPid());//公共号Id
            pubMsg.setSendType(1);//广播给指定用户
            pubMsg.setToAll(false);//不发送给所有人
            pubMsg.setTo(to);//指定人
            pubMsg.setToNews(false);//不往健康动态公共号转发
            pubMsg.setMpt(WelcomeMessageHelper.getEduGuiderWelcomeMsg());
            pubMsg.setSource("system");
            pubaccHelper.sendMsgToPub(pubMsg);
        }
        return pub;
    }

    @Override
    public PubPO createPubForGroupCreate(String pubName, String rtype, Integer creator, String groupId, String groupName,
        String groupIntroduction, String photourl) throws HttpApiException {

        PubParam pubParam = new PubParam();
        pubParam.setMid(groupId);
        /**
         * rtype=RelationTypeEnum.DOCTOR 集团新闻
         * rtype=RelationTypeEnum.PATIENT 患者之声
         */
        pubParam.setName(pubName + "_" + groupName);
        pubParam.setNickName(groupName);
        pubParam.setNote(groupIntroduction);
        pubParam.setRtype(rtype);
        pubParam.setPhotourl(photourl);
        pubParam.setDefault(true);
        pubParam.setCreator(String.valueOf(creator));
        if (PubTypeEnum.PUB_GROUP_DOCTOR.getValue().equals(rtype)) {
            pubParam.setPid(PubUtils.PUB_GROUP_NEWS + groupId);
        } else {
            pubParam.setPid(PubUtils.PUB_PATIENT_VOICE + groupId);
        }

        PubPO pub = pubaccHelper.createPub(pubParam);
        return pub;
    }

    @Override
    public PubPO createPubForDept(String pubName, Integer creator, String groupId, String groupName,
        String groupIntroduction, String photourl) throws HttpApiException {
        PubParam pubParam = new PubParam();

        pubParam.setFolder("dept");
        pubParam.setMid(groupId);
        pubParam.setName(pubName + "_" + groupName);
        pubParam.setNickName(groupName);
        pubParam.setNote(groupIntroduction);
        pubParam.setRtype("pub_dept");
        pubParam.setPhotourl(photourl);
        pubParam.setDefault(true);
        pubParam.setCreator(String.valueOf(creator));
        pubParam.setPid(PubUtils.PUB_DEPT + groupId);

        PubPO pub = pubaccHelper.createPub(pubParam);

        List<String> to = new ArrayList<String>(1);
        to.add(creator.toString());
        return pub;
    }

    @Override
    public void updatePubForDept(String groupId, String groupName, String photourl) throws HttpApiException {
        PubParam pubParam = new PubParam();
        pubParam.setNickName(groupName);
        pubParam.setPhotourl(photourl);
        pubParam.setPid(PubUtils.PUB_DEPT + groupId);
        pubaccHelper.savePub(pubParam);
    }
}
