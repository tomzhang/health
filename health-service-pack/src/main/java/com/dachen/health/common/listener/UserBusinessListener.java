package com.dachen.health.common.listener;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsDoctor;
import com.dachen.elasticsearch.model.EsGroup;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.pub.service.PubCustomerService;

@Component("UserBusinessListener")
@EcEventListener
public class UserBusinessListener {
	
	private static final Logger log = LoggerFactory.getLogger(UserBusinessListener.class);
	
	@Autowired
	private PubCustomerService pubCustomerService;
	
	@Autowired
	private IPatientService patientService;
	
	@Resource
	private IInvitePatientService invitePatientService;
	
	@Resource
	private IOrderService orderService;
	
	@Resource
	private UserRepository userRepository;
	
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	
	@EcEventMapping(type = {EventType.UserRegisterSuccess})
    public void fireRegisterSuccess(EcEvent event) throws Exception {
		Integer userId = event.param("userId");
		User user = userRepository.getUser(userId);
		try {
			if (user.getStatus() != UserEnum.UserStatus.inactive.getIndex()) {
				pubCustomerService.welcome(userId);
			}
		} catch (Exception e) {
			log.error("注册成功发送欢迎卡片失败。用户ID："+userId, e);
		}
	}
	
	@EcEventMapping(type = {EventType.User1InfoUpdated})
    public void firePatientInfoModify(EcEvent event) throws Exception {
		Integer userId = event.param("userId");
		patientService.save(userId);
	}
	
	@EcEventMapping(type = {EventType.UserActivateSuccess})
    public void fireActivateSuccess(EcEvent event) throws Exception {
		Integer userId = event.param("userId");
		
		try {
			invitePatientService.sendNotice(userId);
		} catch (Exception e) {
			log.error("激活成功发送通知失败。用户ID："+userId, e);
		}
		
		try {
			orderService.updateOrderByUserId(userId, OrderEnum.OrderActivateStatus.noActivate.getIndex());
		} catch (Exception e) {
			log.error("激活成功更新订单状态OrderActivateStatus失败。用户ID："+userId, e);
		}
	}
	
	
	@EcEventMapping(type = {EventType.DoctorInfoUpdateForEs})
	public void fireDoctorSkillSuccess(EcEvent event) throws Exception {
		Integer userId = event.param("userId");
		String name = event.param("name");
		String skill = event.param("skill");
		List<EsDiseaseType> expertise = event.param("expertise");
		String departments = event.param("departments");
		EsDoctor doctor = new EsDoctor(String.valueOf(userId));
        doctor.setDepartments(departments);
        doctor.setName(name);
        doctor.setSkill(skill);
        doctor.setExpertise(expertise);
		try {
			List<GroupDoctor> groupList = groupDoctorDao.getByDoctorId(userId);
			for(GroupDoctor group:groupList){
				doctor.addGroupId(group.getGroupId());
			}
			ElasticSearchFactory.getInstance().updateDocument(doctor);
		} catch (Exception e) {
			log.error("推送到ES服务器消息失败："+userId, e);
		}
	}
	
	@EcEventMapping(type = {EventType.InsertDoctorInfoToEs})
	public void insertDoctorInfoToEs(EcEvent event) throws Exception {
		EsDoctor doctor = new EsDoctor(event.param("userId").toString());
		doctor.setDepartments(event.param("departments"));
		doctor.setName(event.param("name"));
		doctor.setSkill(event.param("skill"));
		doctor.setExpertise(event.param("expertise"));
		try {
			List<GroupDoctor> groupList = groupDoctorDao.getByDoctorId(event.param("userId"));
			for(GroupDoctor group:groupList){
				doctor.addGroupId(group.getGroupId());
			}
			ElasticSearchFactory.getInstance().insertDocument(doctor);
		} catch (Exception e) {
			log.error("推送到ES服务器消息失败：", e);
		}
	}
	@EcEventMapping(type = {EventType.GroupInfoInsertForEs})
	public void groupInfoInsert(EcEvent event) throws Exception {
		EsGroup group = new EsGroup(event.param("bizid"));
		group.setName(event.param("name"));
		group.setExpertise(event.param("diseaselist"));
		ElasticSearchFactory.getInstance().insertDocument(group);
	}
	
	@EcEventMapping(type = {EventType.GroupInfoUpdateForEs})
	public void groupInfoUpdate(EcEvent event) throws Exception {
		EsGroup group = new EsGroup(event.param("bizid"));
		group.setName(event.param("name"));
		group.setExpertise(event.param("diseaselist"));
		ElasticSearchFactory.getInstance().updateDocument(group);
	}
}
