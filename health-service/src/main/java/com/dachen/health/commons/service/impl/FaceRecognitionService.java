package com.dachen.health.commons.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.FaceRecognitionRepository;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.FaceRecognition;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserConstant;
import com.dachen.health.user.entity.po.Change;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.util.ReqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Author: xuhuanjie
 * Date: 2018-01-05
 * Time: 10:15
 * Description:
 */
@Service
public class FaceRecognitionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLogRespository userLogRespository;

    @Autowired
    FaceRecognitionRepository faceRecognitionRepository;

    public FaceRecognition getFaceRecRecord(Integer userId) {
        return faceRecognitionRepository.getFaceRecRecord(userId);
    }

    public void addFaceRecRecord(Integer userId, String faceImage, Integer passed) {
        User user = userRepository.getUser(userId);
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在");
        }
        FaceRecognition faceRecognition = new FaceRecognition(userId, passed);
        faceRecognition.setFaceImage(faceImage);
        faceRecognition.setVerifyCount(1);
        faceRecognition.setCreateTime(System.currentTimeMillis());
        faceRecognition.setModifyTime(System.currentTimeMillis());
        // 操作记录
        addOrUpdateOperaRec(userId, passed);
        faceRecognitionRepository.addFaceRecRecord(faceRecognition);
    }

    public void updateFaceRecRecord(Integer userId, String faceImage, Integer passed) {
        User user = userRepository.getUser(userId);
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在");
        }
        // 操作记录
        addOrUpdateOperaRec(userId, passed);
        faceRecognitionRepository.updateFaceRecRecord(userId, faceImage, passed);
    }

    private void addOrUpdateOperaRec(Integer userId, Integer passed) {
        OperationRecord operationRecord = new OperationRecord();
        Integer id = ReqUtil.instance.getUserId();
        if (Objects.isNull(id) || id.intValue() == 0) {
            operationRecord.setCreator(userId);
        } else {
            operationRecord.setCreator(id);
        }
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setObjectId(userId + "");
        operationRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
        FaceRecognition faceRecognition = getFaceRecRecord(userId);
        String oldPassed = faceRecognition == null ? null : faceRecognition.getPassed().toString();
        String newPassed = passed.toString();
        operationRecord.setChange(new Change(UserLogEnum.infoType.faceRecognition.getType(), "FaceRecognition", oldPassed, newPassed));
        userLogRespository.addOperationRecord(operationRecord);
    }

    /**
     * 返回人身验证状态
     * @param userId
     * @return
     * 0: 人身检测失败
     * 1: 人身检测成功
     * 2: 未人身检测
     */
    public Integer passFaceRec(Integer userId) {
        FaceRecognition faceRecognition = getFaceRecRecord(userId);
        if (Objects.isNull(faceRecognition)) {
            return UserConstant.FaceRecognitionStatus.never.getIndex();
        } else {
            return faceRecognition.getPassed();
        }
    }
}
