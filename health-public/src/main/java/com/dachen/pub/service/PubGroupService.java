package com.dachen.pub.service;

import com.dachen.pub.model.po.PubPO;
import com.dachen.sdk.exception.HttpApiException;

public interface PubGroupService {

    /**
     * 562f1fb04203f3672bae390c
     * 用户加入集团的时候，自动关注集团对应的公共号
     *
     * @param mid 集团Id
     */
    void addSubUser(String mid, String userId, int userType) throws HttpApiException;

    /**
     * 患者关注医生公共号
     */
    void addSubUser(Integer doctorId, Integer userId) throws HttpApiException;

    /**
     * 医生关注科室公众号
     * @param doctorId
     * @throws HttpApiException
     */
    void addSubUser(Integer doctorId, String groupId) throws HttpApiException;

    /**
     * 当医生退出医生集团的时候，自动取消关注的集团公共号
     */
    void delSubUser(String mid, String userId, int userType) throws HttpApiException;

    void addSubUser(Integer doctorId, String groupId, boolean sendMsg) throws HttpApiException;

    /**
     * 患者取消关注医生公共号
     */
    void delSubUser(Integer doctorId, Integer userId) throws HttpApiException;

    void delSubUser(Integer doctorId, String groupId) throws HttpApiException;

    /**
     * 医生身份审核通过之后创建默认公共号
     */
    PubPO createDoctorPub(Integer doctorId, String note, Integer role) throws HttpApiException;

    /**
     * 创建医生集团时，开通两个公共号
     * rtype=RelationTypeEnum.DOCTOR 集团新闻
     * rtype=RelationTypeEnum.PATIENT 患者之声
     *
     * @author wangqiao
     * @date 2016年3月7日
     */
    PubPO createPubForGroupCreate(String pubName, String rtype, Integer creator, String groupId, String groupName,
        String groupIntroduction, String photourl) throws HttpApiException;

    /**
     * 创建科室时，创建科室动态的公众号
     */
    PubPO createPubForDept(String pubName, Integer creator, String groupId, String groupName,
        String groupIntroduction, String photourl) throws HttpApiException;

    void updatePubForDept(String groupId, String groupName, String photourl) throws HttpApiException;
}
