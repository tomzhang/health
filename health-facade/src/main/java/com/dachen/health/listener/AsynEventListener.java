package com.dachen.health.listener;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;
import com.dachen.commons.asyn.event.listener.DefaultListener;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.pub.dao.PubGroupDAO;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@EcEventListener
@Component("AsynEventListener")
public class AsynEventListener {
    private static final Logger logger = LoggerFactory.getLogger(DefaultListener.class);

    @Autowired
    private PubGroupDAO pubGroupDAO;

    @EcEventMapping(type = {EventType.DOCTOR_NEW_DYNAMIC})
    public void doctorNewDynamic(EcEvent event) throws Exception {
        String doctorId = event.param("doctorId");
        List<Integer> userIds = event.param("userIds");

        List<String> patientList;

        if (CollectionUtils.isEmpty(userIds) || userIds.contains(0)) {
            patientList = pubGroupDAO.getUserPatientIdByDoctorId(Integer.valueOf(doctorId));
            if (patientList == null || patientList.size() == 0) {
                return;
            }
        } else {
            patientList = Lists.newArrayList();
            for (Integer userId : userIds) {
                patientList.add(String.valueOf(userId));
            }
        }

        logger.debug("sendEvent doctor_new_dynamic {}", doctorId);
        sendEvent2Im(EventEnum.NEW_DYNAMIC, patientList);
    }

    @EcEventMapping(type = {EventType.GROUP_NEW_DYNAMIC})
    public void groupNewDynamic(EcEvent event) throws Exception {
        String groupId = event.param("groupId");
        List<Integer> userIds = event.param("userIds");

        List<String> patientList;

        if (CollectionUtils.isEmpty(userIds) || userIds.contains(0)) {
            patientList = pubGroupDAO.getUserPatientIdByGroup(groupId, "pub_voice");
            if (patientList == null || patientList.size() == 0) {
                return;
            }
        } else {
            patientList = Lists.newArrayList();
            for (Integer userId : userIds) {
                patientList.add(String.valueOf(userId));
            }
        }

        logger.debug("sendEvent group_new_dynamic {}", groupId);
        sendEvent2Im(EventEnum.NEW_DYNAMIC, patientList);
    }

    private void sendEvent2Im(EventEnum eventEnum, List<String> userList) {
        if (userList == null || userList.size() == 0) {
            return;
        }
        int size = userList.size();
        int pageSize = 500;
        if (size <= pageSize) {
            sendEvent2ImPageSize(eventEnum, userList);
        } else {
            //分批发送
            int lastPageCount = size % pageSize;
            int count = size / pageSize;
            if (lastPageCount > 0) {
                count++;
            }

            List<String> subList = null;
            int toIndex = 0;
            for (int i = 0; i < count; i++) {
                toIndex = (i + 1) * 500;
                if (toIndex > size) {
                    toIndex = size;
                }
                subList = userList.subList(i * 500, toIndex);
                sendEvent2ImPageSize(eventEnum, subList);
            }
        }
    }

    private void sendEvent2ImPageSize(EventEnum eventEnum, List<String> userList) {
        StringBuffer userIds = new StringBuffer();
        for (String uid : userList) {
            if (userIds.length() > 0) {
                userIds.append("|");
            }
            userIds.append(uid);
        }
        EventVO eventVO = new EventVO();
        eventVO.setEventType(eventEnum.getValue());
        eventVO.setUserId(userIds.toString());
        MsgHelper.sendEvent(eventVO);
    }
}
