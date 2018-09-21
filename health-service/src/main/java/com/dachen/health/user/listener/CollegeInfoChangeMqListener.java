package com.dachen.health.user.listener;

import com.alibaba.fastjson.JSON;
import com.dachen.health.base.entity.po.CollegesPO;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.user.entity.param.LearningExperienceParam;
import com.dachen.mq.ExchangeType;
import com.dachen.mq.consume.annotation.MqConsumeMapping;
import com.dachen.mq.consume.listener.AbstractMqConsumerListener;
import com.dachen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Author: xuhuanjie
 * Date: 2018-09-14
 * Time: 17:34
 * Description:
 */
@Component
@MqConsumeMapping(exchangeType = ExchangeType.FANOUT, exchangeName = "COLLEGE_INFO_CHANGE", queueName = "COLLEGE_INFO_CHANGE_QUEUE")
public class CollegeInfoChangeMqListener extends AbstractMqConsumerListener {

    @Autowired
    private UserManager userManager;
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CollegeInfoChangeMqListener.class);

    @Override
    public void handleMessage(String jsonMessage) {
        LOGGER.info("院校信息更新接收MQ,param:{}", jsonMessage);
        CollegesPO collegesPO = JSON.parseObject(jsonMessage, CollegesPO.class);
        if (Objects.nonNull(collegesPO)) {
            String id = collegesPO.getId();
            String collegeName = collegesPO.getCollegeName();
            if (StringUtil.isNoneBlank(id) && StringUtil.isNoneBlank(collegeName)) {
                LearningExperienceParam learningExperienceParam = new LearningExperienceParam();
                learningExperienceParam.setCollegeId(id);
                learningExperienceParam.setCollegeName(collegeName);
                userManager.updateLearningExperienceMul(learningExperienceParam);
            }
        }
    }
}
