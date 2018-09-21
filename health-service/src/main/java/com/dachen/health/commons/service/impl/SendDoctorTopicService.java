package com.dachen.health.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.micro.buryingPoint.AsyncBuryingPointService;
import com.dachen.health.commons.constants.DoctorTopicEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.entity.vo.DoctorTopicVO;
import com.dachen.util.BeanUtil;
import com.dachen.util.ReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-02-08
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class SendDoctorTopicService {

    private static Logger LOGGER = LoggerFactory.getLogger(SendDoctorTopicService.class);

    @Autowired
    private AsyncBuryingPointService asyncBuryingPointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReqUtil reqUtil;

    @Async
    public void sendRegisterTopicMes(User user) {
        if (Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())) {
            LOGGER.info("Kafka消息队列：Doctor register; userId:{} ", user.getUserId());
            DoctorTopicVO doctorTopicVO = BeanUtil.copy(user.getDoctor(), DoctorTopicVO.class);
            if (Objects.isNull(doctorTopicVO)) {
                doctorTopicVO = new DoctorTopicVO();
                LOGGER.error("Kafka消息队列出错：Bean doctorTopicVO after copy is NULL;userId:{}", user.getUserId());
            }
            try {
                doctorTopicVO.setDsType(DoctorTopicEnum.DoctorDataSourceEnum.Register.getSourceName());
                if (Objects.nonNull(user.getUserId())) {
                    doctorTopicVO.setUserId(Objects.toString(user.getUserId()));
                }
                if (Objects.nonNull(user.getBirthday())) {
                    doctorTopicVO.setBirthday(Objects.toString(user.getBirthday()));
                }
                if (Objects.nonNull(user.getDoctor().getProvinceId())) {
                    doctorTopicVO.setProvinceId(Objects.toString(user.getDoctor().getProvinceId()));
                }
                if (Objects.nonNull(user.getDoctor().getCityId())) {
                    doctorTopicVO.setCityId(Objects.toString(user.getDoctor().getCityId()));
                }
                if (Objects.nonNull(user.getDoctor().getCountryId())) {
                    doctorTopicVO.setAreaId(Objects.toString(user.getDoctor().getCountryId()));
                }
                if (Objects.nonNull(user.getStatus())) {
                    doctorTopicVO.setStatus(Objects.toString(user.getStatus()));
                }
                if (Objects.nonNull(user.getUserLevel())) {
                    doctorTopicVO.setUserLevel(Objects.toString(user.getUserLevel()));
                }
                if (Objects.nonNull(user.getSource().getInviterId())) {
                    doctorTopicVO.setInviterId(Objects.toString(user.getSource().getInviterId()));
                }
                if (Objects.nonNull(user.getSex())) {
                    doctorTopicVO.setSex(Objects.toString(user.getSex()));
                }
                if (Objects.nonNull(user.getSource().getSourceType())) {
                    doctorTopicVO.setRegfrom(Objects.toString(user.getSource().getSourceType()));
                }
                if (Objects.nonNull(user.getCreateTime())) {
                    doctorTopicVO.setCreateTime(Objects.toString(user.getCreateTime()));
                }
                doctorTopicVO.setUsername(user.getUsername());
                doctorTopicVO.setTelephone(user.getTelephone());
                doctorTopicVO.setEmail(user.getEmail());
                doctorTopicVO.setDesc(user.getDescription());
                doctorTopicVO.setModel(ReqUtil.instance.getHeaderInfo().getDeviceType());
                doctorTopicVO.setLastUploadTime(String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Kafka消息队列：Send doctor register message; topic name:{}, json:{}", DoctorTopicEnum.DoctorTopicNameEnum.Register.getTopicName(), user.getUserId(), JSON.toJSON(doctorTopicVO));
                asyncBuryingPointService.sendBuryingPoint(DoctorTopicEnum.DoctorTopicNameEnum.Register.getTopicName(), JSON.toJSONString(doctorTopicVO));
            } catch (Exception ex) {
                LOGGER.error("Kafka消息队列出错：Sending doctor register message,userId:{}, ex:{}", user.getUserId(), ex);
            }
        }

    }

    @Async
    public void sendLoginTopicMes(User user, String version, String loginType) {
        if (Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())) {
            /* 重新取下user */
            user = userRepository.getUser(user.getUserId());
            LOGGER.info("Kafka消息队列：Doctor login; userId: {} ", user.getUserId());
            DoctorTopicVO doctorTopicVO = BeanUtil.copy(user.getDoctor(), DoctorTopicVO.class);
            if (Objects.isNull(doctorTopicVO)) {
                doctorTopicVO = new DoctorTopicVO();
                LOGGER.error("Kafka消息队列出错：Bean doctorTopicVO after copy is NULL;userId:{}", user.getUserId());
            }
            try {
                doctorTopicVO.setLoginType(loginType);
                doctorTopicVO.setDsType(DoctorTopicEnum.DoctorDataSourceEnum.Login.getSourceName());
                if (Objects.nonNull(user.getUserId())) {
                    doctorTopicVO.setUserId(Objects.toString(user.getUserId()));
                }
                doctorTopicVO.setLoginTime(Objects.toString(user.getLastLoginTime()));
                LOGGER.info("------------version---:{}", version);
                if (Objects.nonNull(version)) {
                    doctorTopicVO.setApiVersion(Objects.toString(version));
                }
                if (Objects.nonNull(user.getLoginLog().getOsVersion())) {
                    doctorTopicVO.setOsVersion(Objects.toString(user.getLoginLog().getOsVersion()));
                }
                if (Objects.nonNull(user.getLoginLog().getModel())) {
                    doctorTopicVO.setModel(Objects.toString(user.getLoginLog().getModel()));
                }
                if (Objects.nonNull(user.getLoginLog().getSerial())) {
                    doctorTopicVO.setSerial(Objects.toString(user.getLoginLog().getSerial()));
                }
                doctorTopicVO.setLatitude(Objects.toString(user.getLoginLog().getLatitude()));
                doctorTopicVO.setLongitude(Objects.toString(user.getLoginLog().getLongitude()));
                if (Objects.nonNull(user.getDoctor().getProvinceId())) {
                    doctorTopicVO.setProvinceId(Objects.toString(user.getDoctor().getProvinceId()));
                }
                if (Objects.nonNull(user.getDoctor().getCityId())) {
                    doctorTopicVO.setCityId(Objects.toString(user.getDoctor().getCityId()));
                }
                if (Objects.nonNull(user.getDoctor().getCountryId())) {
                    doctorTopicVO.setAreaId(Objects.toString(user.getDoctor().getCountryId()));
                }
                if (Objects.nonNull(user.getLoginLog().getLocation())) {
                    doctorTopicVO.setLocation(Objects.toString(user.getLoginLog().getLocation()));
                }
                if (Objects.nonNull(user.getLoginLog().getAddress())) {
                    doctorTopicVO.setAddress(Objects.toString(user.getLoginLog().getAddress()));
                }
                if (Objects.nonNull(user.getStatus())) {
                    doctorTopicVO.setStatus(Objects.toString(user.getStatus()));
                }
                doctorTopicVO.setLastUploadTime(String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Kafka消息队列：Send doctor login message; topic name: {}, json:{}", DoctorTopicEnum.DoctorTopicNameEnum.Login.getTopicName(), user.getUserId(), JSON.toJSON(doctorTopicVO));
                asyncBuryingPointService.sendBuryingPoint(DoctorTopicEnum.DoctorTopicNameEnum.Login.getTopicName(), JSON.toJSONString(doctorTopicVO));
            } catch (Exception ex) {
                LOGGER.error("Kafka消息队列出错：Sending doctor login message,userId:{}, ex:{}", user.getUserId(), ex);
            }
        }

    }

    @Async
    public void sendCertifyTopicMes(User user) {
        if (Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())) {
            LOGGER.info("Kafka消息队列：Doctor certify; userId: {} ", user.getUserId());
            DoctorTopicVO doctorTopicVO = BeanUtil.copy(user.getDoctor(), DoctorTopicVO.class);
            if (Objects.isNull(doctorTopicVO)) {
                doctorTopicVO = new DoctorTopicVO();
                LOGGER.error("Kafka消息队列出错：Bean doctorTopicVO after copy is NULL;userId:{}", user.getUserId());
            }
            try {
                doctorTopicVO.setDsType(DoctorTopicEnum.DoctorDataSourceEnum.Certify.getSourceName());
                if (Objects.nonNull(user.getUserId())) {
                    doctorTopicVO.setUserId(Objects.toString(user.getUserId()));
                }
                if (Objects.nonNull(user.getStatus())) {
                    doctorTopicVO.setStatus(Objects.toString(user.getStatus()));
                }
                if (Objects.nonNull(user.getSubmitTime())) {
                    doctorTopicVO.setCertifyTime(String.valueOf(user.getSubmitTime()));
                }
                doctorTopicVO.setLastUploadTime(String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Kafka消息队列：Send certify login message; topic name: {}, json:{}", DoctorTopicEnum.DoctorTopicNameEnum.Certify.getTopicName(), user.getUserId(), JSON.toJSON(doctorTopicVO));
                asyncBuryingPointService.sendBuryingPoint(DoctorTopicEnum.DoctorTopicNameEnum.Certify.getTopicName(), JSON.toJSONString(doctorTopicVO));
            } catch (Exception ex) {
                LOGGER.error("Kafka消息队列出错：Sending doctor certify message,userId:{}, ex:{}", user.getUserId(), ex);
            }
        }

    }

    @Async
    public void sendCheckTopicMes(User user) {
        if (Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())) {
            LOGGER.info("Kafka消息队列：Doctor check; userId: {} ", user.getUserId());
            DoctorTopicVO doctorTopicVO = BeanUtil.copy(user.getDoctor(), DoctorTopicVO.class);
            if (Objects.isNull(doctorTopicVO)) {
                doctorTopicVO = new DoctorTopicVO();
                LOGGER.error("Kafka消息队列出错：Bean doctorTopicVO after copy is NULL;userId:{}", user.getUserId());
            }
            try {
                doctorTopicVO.setDsType(DoctorTopicEnum.DoctorDataSourceEnum.Check.getSourceName());
                if (Objects.nonNull(user.getUserId())) {
                    doctorTopicVO.setUserId(Objects.toString(user.getUserId()));
                }
                if (Objects.nonNull(user.getStatus())) {
                    doctorTopicVO.setStatus(Objects.toString(user.getStatus()));
                }
                if (Objects.nonNull(user.getSubmitTime())) {
                    doctorTopicVO.setCertifyTime(String.valueOf(user.getSubmitTime()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getRemark()))) {
                    doctorTopicVO.setRemark(Objects.toString(user.getDoctor().getCheck().getRemark()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getCheckerId()))) {
                    doctorTopicVO.setCheckerId(Objects.toString(user.getDoctor().getCheck().getCheckerId()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getCheckTime()))) {
                    doctorTopicVO.setCheckTime(Objects.toString(user.getDoctor().getCheck().getCheckTime()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getChecker()))) {
                    doctorTopicVO.setChecker(Objects.toString(user.getDoctor().getCheck().getChecker()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getLicenseNum()))) {
                    doctorTopicVO.setLicenseNum(Objects.toString(user.getDoctor().getCheck().getLicenseNum()));
                }
                if (Objects.nonNull(Objects.toString(user.getDoctor().getCheck().getLicenseExpire()))) {
                    doctorTopicVO.setLicenseExpire(Objects.toString(user.getDoctor().getCheck().getLicenseExpire()));
                }
                doctorTopicVO.setLastUploadTime(String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Kafka消息队列：Send check check message; topic name: {}, json:{}", DoctorTopicEnum.DoctorTopicNameEnum.Check.getTopicName(), user.getUserId(), JSON.toJSON(doctorTopicVO));
                asyncBuryingPointService.sendBuryingPoint(DoctorTopicEnum.DoctorTopicNameEnum.Check.getTopicName(), JSON.toJSONString(doctorTopicVO));
            } catch (Exception ex) {
                LOGGER.error("Kafka消息队列出错：Sending doctor check message,userId:{}, ex:{}", user.getUserId(), ex);
            }
        }

    }

    @Async
    public void sendChangeInfoTopicMes(User user) {
        if (Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())) {
            LOGGER.info("Kafka消息队列：Doctor register; userId: {} ", user.getUserId());
            DoctorTopicVO doctorTopicVO = BeanUtil.copy(user.getDoctor(), DoctorTopicVO.class);
            if (Objects.isNull(doctorTopicVO)) {
                doctorTopicVO = new DoctorTopicVO();
                LOGGER.error("Kafka消息队列出错：Bean doctorTopicVO after copy is NULL;userId:{}", user.getUserId());
            }
            try {
                doctorTopicVO.setDsType(DoctorTopicEnum.DoctorDataSourceEnum.ChangeInfo.getSourceName());
                if (Objects.nonNull(user.getUserId())) {
                    doctorTopicVO.setUserId(Objects.toString(user.getUserId()));
                }
                if (Objects.nonNull(user.getBirthday())) {
                    doctorTopicVO.setBirthday(Objects.toString(user.getBirthday()));
                }
                if (Objects.nonNull(user.getDoctor().getProvinceId())) {
                    doctorTopicVO.setProvinceId(Objects.toString(user.getDoctor().getProvinceId()));
                }
                if (Objects.nonNull(user.getDoctor().getCityId())) {
                    doctorTopicVO.setCityId(Objects.toString(user.getDoctor().getCityId()));
                }
                if (Objects.nonNull(user.getDoctor().getCountryId())) {
                    doctorTopicVO.setAreaId(Objects.toString(user.getDoctor().getCountryId()));
                }
                if (Objects.nonNull(user.getStatus())) {
                    doctorTopicVO.setStatus(Objects.toString(user.getStatus()));
                }
                if (Objects.nonNull(user.getUserLevel())) {
                    doctorTopicVO.setUserLevel(Objects.toString(user.getUserLevel()));
                }
                if (Objects.nonNull(user.getSource().getInviterId())) {
                    doctorTopicVO.setInviterId(Objects.toString(user.getSource().getInviterId()));
                }
                if (Objects.nonNull(user.getSex())) {
                    doctorTopicVO.setSex(Objects.toString(user.getSex()));
                }
                if (Objects.nonNull(user.getSource().getSourceType())) {
                    doctorTopicVO.setRegfrom(Objects.toString(user.getSource().getSourceType()));
                }
                if (Objects.nonNull(user.getCreateTime())) {
                    doctorTopicVO.setCreateTime(Objects.toString(user.getCreateTime()));
                }
                doctorTopicVO.setUsername(user.getUsername());
                doctorTopicVO.setTelephone(user.getTelephone());
                doctorTopicVO.setEmail(user.getEmail());
                doctorTopicVO.setDesc(user.getDescription());
                doctorTopicVO.setModel(ReqUtil.instance.getHeaderInfo().getDeviceType());
                doctorTopicVO.setLastUploadTime(String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Kafka消息队列：Send doctor changeInfo message; topic name: {}, json:{}", DoctorTopicEnum.DoctorTopicNameEnum.ChangeInfo.getTopicName(), user.getUserId(), JSON.toJSON(doctorTopicVO));
                asyncBuryingPointService.sendBuryingPoint(DoctorTopicEnum.DoctorTopicNameEnum.ChangeInfo.getTopicName(), JSON.toJSONString(doctorTopicVO));
            } catch (Exception ex) {
                LOGGER.error("Kafka消息队列出错：Sending doctor changeInfo message,userId:{}, ex:{}", user.getUserId(), ex);
            }
        }
    }

}
