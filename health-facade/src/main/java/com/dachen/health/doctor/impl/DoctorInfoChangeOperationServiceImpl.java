package com.dachen.health.doctor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.commons.vo.User;
import com.dachen.health.doctor.IDoctorInfoChangeOperationService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.order.dao.IAssistantSessionRelationDao;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.util.StringUtil;
@Service
public class DoctorInfoChangeOperationServiceImpl implements IDoctorInfoChangeOperationService{
	
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private IAssistantSessionRelationDao assistantSessionRelationDao;
	@Override
	public void closeAndDeleteOldSession()
	{
		//先关闭再删除
		AssistantSessionRelation asr = new AssistantSessionRelation();
		asr.setType(1);
		List<AssistantSessionRelation> list = assistantSessionRelationDao.queryByConditions(asr);
		List<String> ids = new ArrayList<String>();
		for(AssistantSessionRelation a :list){
			UpdateGroupRequestMessage imMsg = new UpdateGroupRequestMessage();
			imMsg.setGid(a.getMsgGroupId());
			imMsg.setFromUserId(a.getDoctorId()+"");
			imMsg.setAct(9);
			MsgHelper.updateGroup(imMsg);
			ids.add(a.getId());
		}
		asr.setType(2);
		list = assistantSessionRelationDao.queryByConditions(asr);
		for(AssistantSessionRelation a :list){
			UpdateGroupRequestMessage imMsg = new UpdateGroupRequestMessage();
			imMsg.setGid(a.getMsgGroupId());
			imMsg.setFromUserId(a.getDoctorId()+"");
			imMsg.setAct(9);
			MsgHelper.updateGroup(imMsg);
			ids.add(a.getId());
		}
		assistantSessionRelationDao.deleteByIds(ids);//删除
	}

	@Override
	public void assistantChanged(User user, DoctorCheckParam param) {
		/*
		 * 医生助手修改 医生与助手会话里关系表里数据替换，IM会话组里 组员改变
		 * 医生助手修改 助手与患者会话里关系表里数据替换，IM会话组里 组员改变
		 * 
		 * 医生助手修改 同时替换之前助手负责的订单由新的助手负责即需要修改订单助手ID
		*/
		AssistantSessionRelation asr = new AssistantSessionRelation();
		asr.setDoctorId(user.getUserId());
		asr.setAssistantId(user.getDoctor().getAssistantId());
		asr.setType(1);
		List<AssistantSessionRelation> list = assistantSessionRelationDao.queryByConditions(asr);
		for(AssistantSessionRelation a :list){
			replaceTeamer(a.getMsgGroupId(), a.getDoctorId()+"", a.getAssistantId()+"", param.getAssistantId()+"");
			a.setAssistantId(param.getAssistantId());
			assistantSessionRelationDao.update(a);
			
		}
		asr.setDoctorId(user.getUserId());
		asr.setAssistantId(user.getDoctor().getAssistantId());
		asr.setType(2);
		list = assistantSessionRelationDao.queryByConditions(asr);
		for(AssistantSessionRelation a :list){
			replaceTeamer(a.getMsgGroupId(), a.getUserId()+"", a.getAssistantId()+"", param.getAssistantId()+"");
			a.setAssistantId(param.getAssistantId());
			assistantSessionRelationDao.update(a);
		}
		
		int oldAssistantId = user.getDoctor().getAssistantId()==null?0:user.getDoctor().getAssistantId();
		if(param.getAssistantId()!=null && param.getAssistantId().intValue() != oldAssistantId)
		{
			OrderParam oParam = new OrderParam();
			oParam.setDoctorId(user.getUserId());
			oParam.setAssistantId(user.getDoctor().getAssistantId());
			orderMapper.updateOrderDocAssitant(oParam, param.getAssistantId());
		}
		
	}
	private void replaceTeamer(String msgGroupId,String fromUserId,String oldTeamer,String newTeamer){
		if(StringUtil.isEmpty(msgGroupId)){
			return;
		}
		UpdateGroupRequestMessage imMsg = new UpdateGroupRequestMessage();
		imMsg.setGid(msgGroupId);
		imMsg.setFromUserId(fromUserId);
		if(StringUtil.isNotEmpty(oldTeamer)){
			imMsg.setAct(2);
	        List<String> docIds = Arrays.asList(oldTeamer);
	        imMsg.setToUserId(OrderSession.appendStringUserId(docIds));
			MsgHelper.updateGroup(imMsg);
		}
		if(StringUtil.isNotEmpty(newTeamer)){
			imMsg.setAct(1);
			List<String> docIds = Arrays.asList(newTeamer);
	        imMsg.setToUserId(OrderSession.appendStringUserId(docIds));
			MsgHelper.updateGroup(imMsg);
		}
	}
}
