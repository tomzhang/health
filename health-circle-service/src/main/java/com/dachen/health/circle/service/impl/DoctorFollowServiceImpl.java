package com.dachen.health.circle.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.circle.entity.DoctorFollow;
import com.dachen.health.circle.service.DoctorFollowService;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.service.impl.mq.DoctorFollowMqVo;
import com.dachen.health.circle.vo.MobileDoctorFollowVO;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.Json;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生的关注
 * Created By lim
 * Date: 2017/7/10
 * Time: 11:11
 */
@Model(DoctorFollow.class)
@Service
public class DoctorFollowServiceImpl extends BaseServiceImpl implements DoctorFollowService {
    @Autowired
    private User2Service user2Service;
    @Override
    public DoctorFollow addFollow(Integer doctorId, Integer userId) {
        if(doctorId==userId){
            throw new ServiceException("不能关注自己");
        }
        user2Service.findAndCheckDoctor(doctorId);
        DoctorFollow doctorFollow = new DoctorFollow();
        doctorFollow.setCreateTime(System.currentTimeMillis());
        doctorFollow.setDoctorId(doctorId);
        doctorFollow.setUserId(userId);
        DoctorFollow df = this.saveEntityAndFind(doctorFollow);
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DoctorFollowMqVo doctorFollowMqVo=new DoctorFollowMqVo();
                    doctorFollowMqVo.setUserId(userId);
                    doctorFollowMqVo.setToUserId(doctorId);
                    doctorFollowMqVo.setType(1);
                    BasicProducer.fanoutMessage("DOCTOR_FOLLOW_EXCHANGE", JSON.toJSONString(doctorFollowMqVo));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        return df;
    }

    @Override
    public int removeFollow(Integer doctorId, Integer userId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        query.field("doctorId").equal(doctorId);
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DoctorFollowMqVo doctorFollowMqVo=new DoctorFollowMqVo();
                    doctorFollowMqVo.setUserId(userId);
                    doctorFollowMqVo.setToUserId(doctorId);
                    doctorFollowMqVo.setType(2);
                    BasicProducer.fanoutMessage("DOCTOR_FOLLOW_EXCHANGE", JSON.toJSONString(doctorFollowMqVo));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        return this.deleteByQuery(query);
    }

    @Override
    public Pagination<MobileDoctorFollowVO> getDoctorFollowPage(Integer userId,Integer doctorId, Integer pageSize, Integer pageIndex) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("userId").equal(doctorId);
        query.offset(pageIndex * pageSize).limit(pageSize);
        query.order("-createTime");
        long total = query.countAll();
        List<DoctorFollow> doctorFollows = query.asList();
        List<MobileDoctorFollowVO> mobileDoctorFollowVOs = convertFollowVO(doctorFollows);
        if(SdkUtils.isNotEmpty(mobileDoctorFollowVOs)) {
            for (MobileDoctorFollowVO mobileDoctorFollowVO : mobileDoctorFollowVOs) {
                Integer id = mobileDoctorFollowVO.getDoctorId();
                boolean whetherFollow = whetherFollow(userId, id);
                mobileDoctorFollowVO.setIfFollower(whetherFollow);
            }
        }
        Pagination<MobileDoctorFollowVO> pagination = new Pagination<>(mobileDoctorFollowVOs, total, pageIndex, pageSize);
        return pagination;
    }

    @Override
    public Pagination<MobileDoctorFollowVO> getDoctorFansPage(Integer userId,Integer doctorId,  Integer pageSize, Integer pageIndex) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("doctorId").equal(doctorId);
        query.offset(pageIndex * pageSize).limit(pageSize);
        query.order("-createTime");
        long total = query.countAll();
        List<DoctorFollow> doctorFollows = query.asList();
        List<MobileDoctorFollowVO> mobileDoctorFollowVOs = convertFanVO(doctorFollows);
        if(SdkUtils.isNotEmpty(mobileDoctorFollowVOs)) {
            for (MobileDoctorFollowVO mobileDoctorFollowVO : mobileDoctorFollowVOs) {
                Integer id = mobileDoctorFollowVO.getDoctorId();
                boolean whetherFollow = whetherFollow(userId, id);
                mobileDoctorFollowVO.setIfFollower(whetherFollow);
            }
        }
        Pagination<MobileDoctorFollowVO> pagination = new Pagination<>(mobileDoctorFollowVOs, total, pageIndex, pageSize);
        return pagination;
    }
    @Override
    public List<Integer> getDoctorFansList(Integer userId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("doctorId").equal(userId);
        query.order("-createTime");
        List<DoctorFollow> doctorFollows = query.asList();
        if(SdkUtils.isEmpty(doctorFollows)){
            return null;
        }
        List<Integer> userIds = doctorFollows.stream().map(o -> o.getUserId()).collect(Collectors.toList());
        return userIds;
    }

    @Override
    public List<Integer> getDoctorFollowList(Integer userId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        query.order("-createTime");
        List<DoctorFollow> doctorFollows = query.asList();
        if(SdkUtils.isEmpty(doctorFollows)){
            return null;
        }
        List<Integer> userIds = doctorFollows.stream().map(o -> o.getDoctorId()).collect(Collectors.toList());
        return userIds;
    }

    @Override
    public Long countFollowByUserId(Integer userId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        return query.countAll();
    }

    @Override
    public Long countFanByUserId(Integer userId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("doctorId").equal(userId);
        return query.countAll();
    }

    @Override
    public boolean whetherFollow(Integer userId,Integer followUserId) {
        Query<DoctorFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        query.field("doctorId").equal(followUserId);
        DoctorFollow doctorFollow = query.get();
        if(doctorFollow!=null){
            return true;
        }
        return false;
    }

    private List<MobileDoctorFollowVO> convertFollowVO(List<DoctorFollow> doctorFollows){
        if (SdkUtils.isEmpty(doctorFollows)){
            return null;
        }
        List<MobileDoctorFollowVO> mobileDoctorFollowVOs=new ArrayList<>(doctorFollows.size());
        for (DoctorFollow follow:doctorFollows){
            User user=user2Service.findAndCheckDoctor(follow.getDoctorId());
            MobileDoctorFollowVO mobileDoctorFollowVO = new MobileDoctorFollowVO(user);
            mobileDoctorFollowVOs.add(mobileDoctorFollowVO);
        }
        return mobileDoctorFollowVOs;
    }

    private List<MobileDoctorFollowVO> convertFanVO(List<DoctorFollow> doctorFollows){
        if (SdkUtils.isEmpty(doctorFollows)){
            return null;
        }
        List<MobileDoctorFollowVO> mobileDoctorFollowVOs=new ArrayList<>(doctorFollows.size());
        for (DoctorFollow follow:doctorFollows){
            User user=user2Service.findAndCheckDoctor(follow.getUserId());
            MobileDoctorFollowVO mobileDoctorFollowVO = new MobileDoctorFollowVO(user);
            mobileDoctorFollowVOs.add(mobileDoctorFollowVO);
        }
        return mobileDoctorFollowVOs;
    }
}
