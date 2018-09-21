package com.dachen.health.pack.patient.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dachen.health.pack.patient.mapper.CallResultMapper;
import com.dachen.health.pack.patient.model.CallResult;
import com.dachen.health.pack.patient.model.CallResultExample;
import com.dachen.health.pack.patient.service.ICallResultService;

/**
 * 
 * @author vincent
 *
 */
@Service
public class CallResultServiceImpl extends BaseServiceImpl<CallResult, Integer>
		implements ICallResultService {

	@Resource
	CallResultMapper mapper;

	@Override
	public int save(CallResult intance) {
		CallResultExample example=new CallResultExample();
		example.createCriteria().andCallidEqualTo(intance.getCallid());
		int count=mapper.countByExample(example);
		if(count==0){
			return mapper.insert(intance);
		}else{
			return 0;
		}
		
	}

	@Override
	public int update(CallResult intance) {
		return mapper.updateByPrimaryKey(intance);
	}

	@Override
	public int deleteByPK(Integer pk) {
		return mapper.deleteByPrimaryKey(pk);
	}

	@Override
	public CallResult findByPk(Integer pk) {
		return mapper.selectByPrimaryKey(pk);
	}
	
	@Override
	public List<CallResult> findByCallid(String callId) {
		CallResultExample example=new CallResultExample();
		example.createCriteria().andCallidEqualTo(callId);
		List<CallResult> datas=mapper.selectByExample(example);
		return datas;
	}

	@Override
	public List<CallResult> get8HourCallResultList() {
		//查询当前会议已经结束的订单（判断标准stoptime不为空） b
		List<CallResult> list= mapper.selectByTime();
		return list;
	}

	@Override
	public List<CallResult> getAllCallResultByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		return mapper.getAllCallResultByOrderId(orderId);
	}
}
