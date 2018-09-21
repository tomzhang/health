package com.dachen.health.pack.order.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.order.dao.IAssistantSessionRelationDao;
import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;
import com.dachen.health.pack.order.service.IAssistantSessionRelationService;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.im.server.enums.RelationTypeEnum;
import com.dachen.util.BusinessUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qinyuan.chen
 * Date:2017/1/4
 * Time:19:53
 */
@Service
public class AssistantSessionRelationServiceImpl implements IAssistantSessionRelationService{

    @Autowired
    private IAssistantSessionRelationDao assistantSessionRelationDao;

    @Autowired
    private IMsgService imsgService;
    
    @Autowired
    private UserRepository userMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public AssistantSessionRelation add(AssistantSessionRelation param,Integer type) {//助患会话，医助会话
    	AssistantSessionRelation asr = verifyParam(param,type);
    	List<AssistantSessionRelation> list = assistantSessionRelationDao.queryByConditions(asr);
    	if(list.isEmpty()){//需要创建会话
    		String gType = getGtype(type);
    		String groupId = "";
			groupId = createSession(asr,gType,true);
			asr.setMsgGroupId(groupId);
			return assistantSessionRelationDao.add(asr);
		}
    	return list.get(0);
    }
    private String getGtype(Integer type){
    	String gType = null;
    	if(type == 1 || type == 3){
			gType = RelationTypeEnum.DOC_ASSISTANT_NOORDER.getValue();
		}else if(type == 2 || type == 4){
			gType = RelationTypeEnum.PATIENT_ASST.getValue();
		}else {
			throw new ServiceException("类型不正确");
		}
    	return gType;
    }
    public String  createSession(AssistantSessionRelation asr,String gtype,boolean isCreateNew){
	     CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
         createGroupParam.setCreateNew(isCreateNew);
         createGroupParam.setSendRemind(false);
         createGroupParam.setType(GroupTypeEnum.MULTI.getValue()); //1:单人，2：多人
         createGroupParam.setGtype(gtype);//
         
    	 String  FromUserId;
    	 Map<String,Object> groupParam = new HashMap<String,Object>();
    	 groupParam.put("doctorId", asr.getDoctorId());
         if(gtype == RelationTypeEnum.DOC_ASSISTANT_NOORDER.getValue() ){
             //医助会话组
             FromUserId = String.valueOf(asr.getDoctorId());
         }else if(gtype == RelationTypeEnum.PATIENT_ASST.getValue() ){
        	 groupParam.put("doctorName", asr.getDoctorName());
        	 Patient patient = patientMapper.selectByPrimaryKey(asr.getPatientId());
        	 if(patient != null){
        		 groupParam.put("patientId", patient.getId());
        		 groupParam.put("patientName", patient.getUserName());
                 groupParam.put("patientAge", patient.getAgeStr());
                 groupParam.put("patientSex", patient.getSex() != null ? BusinessUtil.getSexName(Integer.valueOf(patient.getSex())):"");
                 groupParam.put("patientArea", patient.getArea());
        	 }
        	 FromUserId = String.valueOf(asr.getUserId());
         }else {
        	 throw new ServiceException("暂不支持该种类型会话创建");
         }
         createGroupParam.setParam(groupParam);
         createGroupParam.setFromUserId(FromUserId);
         createGroupParam.setToUserId(String.valueOf(asr.getAssistantId()));
         createGroupParam.setGname(null);
         GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);
         return groupInfo.getGid();
    }

    @Override
    public void update(AssistantSessionRelation asr) {
        assistantSessionRelationDao.update(asr);
    }

    private AssistantSessionRelation verifyParam(AssistantSessionRelation asr,Integer type){
    	if(asr == null || type == null){
    		throw new ServiceException("参数不能为空");
    	}
		if(type == 1){//医生
			if(asr.getDoctorId() == null){
				throw new ServiceException("医生Id不能为空");
			}
			User user = userMapper.getUser(asr.getDoctorId());
			if(user == null || user.getDoctor() == null){
				throw new ServiceException("医生未找到");
			}
			asr.setAssistantId(user.getDoctor().getAssistantId());
			asr.setType(1);
		}else if(type == 2){//患者助手
			if(asr.getDoctorId() == null || asr.getUserId() == null || asr.getPatientId() == null){
				throw new ServiceException("医生Id或者患者用户ID不能为空");
			}
			User user = userMapper.getUser(asr.getDoctorId());
			if(user == null || user.getDoctor() == null){
				throw new ServiceException("医生未找到");
			}
			asr.setAssistantId(user.getDoctor().getAssistantId());
			asr.setDoctorName(user.getName()); 
			asr.setType(2);
		}else if(type == 3){//助手医生
			if(asr.getAssistantId() == null || asr.getDoctorId() == null){
				throw new ServiceException("医生Id或者助手ID不能为空");
			}
			asr.setType(1);
		}else if(type == 4){//助手患者
			if(asr.getAssistantId() == null || asr.getUserId() == null || asr.getPatientId() == null || asr.getDoctorId() == null){
				throw new ServiceException("患者用户Id或者助手ID不能为空");
			}
			User user = userMapper.getUser(asr.getDoctorId());
			if(user == null || user.getDoctor() == null){
				throw new ServiceException("医生未找到");
			}
			asr.setDoctorName(user.getName()); 
			asr.setType(2);
		}else{ 
			throw new ServiceException("会话类型不明确");
		}
		return asr;
    }
}
