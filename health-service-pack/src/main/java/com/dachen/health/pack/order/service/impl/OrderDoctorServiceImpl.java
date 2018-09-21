package com.dachen.health.pack.order.service.impl;

import com.dachen.health.base.helper.TemplateHelper;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.po.OrderDoctorExample;
import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDoctorServiceImpl implements IOrderDoctorService{

	@Autowired
    protected OrderDoctorMapper orderDoctorMapper;
	@Resource
    protected MobSmsSdk modSmsSdk;

    @Resource
    protected UserRepository userRepository;
	
	
	public void sendMessageToAll(Integer orderId,Integer userId,String messageContent,String toLinkApp) {
		if(StringUtil.isNotBlank(toLinkApp)) {
			setMessge(orderId,userId,messageContent,toLinkApp);
			
		}else {
			sendMessageToAll(orderId,userId,messageContent);
		}
		
	}
	
	public void setMessge(Integer orderId,Integer userId,String messageContent,String toLinkApp){
		List<OrderDoctor> orderDoctors = findOrderDoctors(orderId);
		User user = userRepository.getUser(userId);
		for(OrderDoctor orderDoctor : orderDoctors) {
			User userDoc = userRepository.getUser(orderDoctor.getDoctorId());
			TemplateHelper helper = new TemplateHelper(null, userDoc.getName(),user.getName());
			String messge =  helper.formatText(messageContent);
			modSmsSdk.send(userDoc.getTelephone(), messge+toLinkApp);
		}
	}
	
	public void sendMessageToAll(Integer orderId,Integer userId,String messageContent) {
		
		List<OrderDoctor> orderDoctors = findOrderDoctors(orderId);
		User user = userRepository.getUser(userId);
		for(OrderDoctor orderDoctor : orderDoctors) {
			User userDoc = userRepository.getUser(orderDoctor.getDoctorId());
			TemplateHelper helper = new TemplateHelper(null, userDoc.getName(),user.getName());
			String messge =  helper.formatText(messageContent);
			modSmsSdk.send(userDoc.getTelephone(), messge);
		}
		
	}

	public List<OrderDoctor> findOrderDoctors(Integer orderId) {
		OrderDoctorExample orderDoctorExample = new OrderDoctorExample();
		orderDoctorExample.createCriteria().andOrderIdEqualTo(orderId);
		return orderDoctorMapper.selectByExample(orderDoctorExample);
	}
	public int deleteByIdList(List<Integer> list){
		OrderDoctorExample orderDoctorExample = new OrderDoctorExample();
		orderDoctorExample.createCriteria().andDoctorIdIn(list);
		return orderDoctorMapper.deleteByExample(orderDoctorExample);
	}
	
	public int updateSelective(OrderDoctor record){
		return orderDoctorMapper.updateByPrimaryKeySelective(record);
	}
	public int add(OrderDoctor record){
		return orderDoctorMapper.insert(record);
	}

	
	@Override
	public List<DoctoreRatioVO> getDoctorRatiosByOrder(Integer orderId, Integer mainDoctorId) {
		List<OrderDoctor> orderDoctors = this.findOrderDoctors(orderId);
		if (CollectionUtils.isEmpty(orderDoctors)) {
			return null;
		}
		
		List<DoctoreRatioVO> list = new ArrayList<DoctoreRatioVO>(orderDoctors.size());
		for (OrderDoctor orderDoctor : orderDoctors) {
			DoctoreRatioVO doctoreRatioVO = new DoctoreRatioVO();
			doctoreRatioVO.setRatioNum(orderDoctor.getSplitRatio());
			doctoreRatioVO.setReceiveRemind(orderDoctor.getReceiveRemind());
			doctoreRatioVO.setUserId(orderDoctor.getDoctorId());
			if (mainDoctorId == orderDoctor.getDoctorId().intValue()) {
				doctoreRatioVO.setGroupType(1); // 主医生
			}
			User user = userRepository.getUser(orderDoctor.getDoctorId());
			if (user != null) {
				doctoreRatioVO.setDoctoreName(user.getName());
				doctoreRatioVO.setDoctorePic(user.getHeadPicFileName());
			}
			
			if (1 == doctoreRatioVO.getUserId()) {
                list.add(0, doctoreRatioVO);    // 主医生排在第1位
            } else {
                list.add(doctoreRatioVO);
            }
		}
		return list;
	}
}
