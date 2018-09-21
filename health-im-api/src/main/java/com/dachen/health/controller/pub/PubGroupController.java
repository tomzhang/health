package com.dachen.health.controller.pub;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.service.PubCustomerService;
import com.dachen.pub.service.PubGroupService;
import com.dachen.pub.util.PubUtils;

@RestController
@RequestMapping("/pub")
public class PubGroupController {
	@Autowired
	private PubGroupService pubGroupService;
	
	@Autowired
	private PubAccountService pubAccountService;
	
	@Autowired
	private PubCustomerService customerService;
	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IGroupService groupService;
	
//	@Autowired
//	private PubGroupDAO pubGroupDAO;
	
	@Autowired
	private UserManager userManager;  
	
//	@Autowired
//	private PubAccountDAO pubAccountDAO;
	
	
	/**
	 * 初始化所有的集团动态和患者之声公共号 
	 * @return
	 */
	/*@RequestMapping("/initGroupPub")
	public JSONMessage initPubAccount() {
		List<Group>groupList = groupDao.getAllGroup();
		for(Group gp:groupList)
		{
			PubPO pubPo = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_DOCTOR.getValue());
			if(pubPo == null)
			{
//				pubPo = groupService.createPubFromGroup(gp,"集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue());
				pubPo = pubGroupService.createPubForGroupCreate("集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue(),gp.getCreator()
						,gp.getId(),gp.getName(),gp.getIntroduction(),gp.getLogoUrl());
				
				List<String>userIdList = pubGroupDAO.getDoctorIdByGroup(gp.getId());
				subscribe(pubPo,userIdList,gp.getCreator());
			}
			PubPO pubPo1 = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_PATIENT.getValue());
			if(pubPo1 == null)
			{
//				pubPo1 = groupService.createPubFromGroup(gp,"患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue());
				pubPo1 =pubGroupService.createPubForGroupCreate("患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue(),gp.getCreator()
						,gp.getId(),gp.getName(),gp.getIntroduction(),gp.getLogoUrl());
				
				List<String>userIdList = pubGroupDAO.getUserPatientIdByGroup(gp.getId(),null);
				subscribe(pubPo1,userIdList,gp.getCreator());
			}
			System.out.println("创建集团公共号："+gp.getName());
		}
		return JSONMessage.success();
	} */
	/*@RequestMapping("/editGroupPub")
	public JSONMessage editGroupPub() {
		List<Group>groupList = groupDao.getAllGroup();
		for(Group gp:groupList)
		{
			PubPO pubPo = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_DOCTOR.getValue());
			if(pubPo != null)
			{
				PubParam pubParam = new PubParam();
				pubParam.setPid(pubPo.getPid());
				pubParam.setNickName(gp.getName());
				pubParam.setName("集团动态_"+gp.getName());
				pubAccountService.savePub(pubParam);
			}
			PubPO pubPo1 = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_PATIENT.getValue());
			if(pubPo1 != null)
			{
				PubParam pubParam = new PubParam();
				pubParam.setPid(pubPo1.getPid());
				pubParam.setNickName(gp.getName());
				pubParam.setName("患者之声_"+gp.getName());
				pubAccountService.savePub(pubParam);
			}
		}
		return JSONMessage.success();
	} */
	/**
     * @api {get} /pub/getDoctorPub 获取医生公共号信息
     * @apiVersion 1.0.0
     * @apiName getDoctorPub
     * @apiGroup 公共号
     * @apiDescription 根据医生Id获取医生公共号信息
     * @apiParam {String}   access_token           token
     * @apiSuccess {Integer}   doctorId    	医生用户ID
     * @apiSuccess {Object}    PubPO        会话组信息，详见http://dachen.picp.net:8091/apidoc/#api-公共号-get
     * @apiAuthor chengwei
     * @date 2015年11月3日
     */
	@RequestMapping("/getDocPubInfo")
	public JSONMessage getDoctorPub(@RequestParam Integer doctorId) throws HttpApiException {
		String pid = PubUtils.PUB_DOC_PREFIX+doctorId;
		PubPO pubPo = pubAccountService.getPub(pid);
		if(pubPo == null)
		{
			User user = userManager.getUser(doctorId);
			String note = null;
			if(user!=null && user.getDoctor()!=null )
			{
				note = user.getDoctor().getIntroduction();
			}
			pubPo = pubGroupService.createDoctorPub(doctorId,note,null);
			
//			List<String>userIdList =  pubGroupDAO.getUserPatientIdByDoctorId(doctorId);
//			subscribe(pubPo,userIdList,doctorId);
		}
		return JSONMessage.success(null,pubPo);
	}
	
	/*@RequestMapping("/initDoctorPub")
	public JSONMessage initDoctorPub() {
		
		List<String>doctorList = pubGroupDAO.getAllUser(3,null);
		for(String doctorId:doctorList)
		{
			String pid = PubUtils.PUB_DOC_PREFIX+doctorId;
			PubPO pubPo = pubAccountService.getPub(pid);
			if(pubPo == null)
			{
				int userId = Integer.valueOf(doctorId);
				User user = userManager.getUser(userId);
				String note = null;
				if(user!=null && user.getDoctor()!=null )
				{
					note = user.getDoctor().getIntroduction();
				}
				pubPo = pubGroupService.createDoctorPub(userId,note,null);
				
				List<String>userIdList =  pubGroupDAO.getUserPatientIdByDoctorId(userId);
				subscribe(pubPo,userIdList,userId);
				System.out.println("创建医生公共号："+user.getName());
			}
		}
		return JSONMessage.success();
	}*/
	
	/*private void subscribe(PubPO pubPo,List<String>userIdList,Integer creator)
	{
		Set<String>userIdSet = new HashSet<>();
		userIdSet.addAll(userIdList);
		pubAccountService.addUserToPub(pubPo.getPid(), userIdSet,UserRoleEnum.SUBSCRIBE);
	}*/

	
	/*@RequestMapping("/initDoctorPub2")
	public JSONMessage initDoctorPub2() {
		
		List<String>doctorList = pubGroupDAO.getAllUser(3,null);
		for(String doctorId:doctorList)
		{
			String pid = PubUtils.PUB_DOC_PREFIX+doctorId;
			PubPO pubPo = pubAccountService.getPub(pid);
			if(pubPo!=null)
			{
				pubAccountService.deletePub(pid);
			}
			int userId = Integer.valueOf(doctorId);
			User user = userManager.getUser(userId);
			String note = null;
			if(user!=null && user.getDoctor()!=null )
			{
				note = user.getDoctor().getIntroduction();
			}
			pubPo = pubGroupService.createDoctorPub(userId,note,null);
			
			List<String>userIdList =  pubGroupDAO.getUserPatientIdByDoctorId(userId);
			subscribe(pubPo,userIdList,userId);
			System.out.println("创建医生公共号："+user.getName());
		}
		return JSONMessage.success();
	}*/
	
	/*@RequestMapping("/initGroupPub2")
	public JSONMessage initPubAccount2() {
		List<Group>groupList = groupDao.getAllGroup();
		for(Group gp:groupList)
		{
			PubPO pubPo = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_PATIENT.getValue());
			if(pubPo != null)
			{
				pubAccountService.deletePub(pubPo.getPid());
			}
			{
//				pubPo = groupService.createPubFromGroup(gp,"患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue());
				pubPo = pubGroupService.createPubForGroupCreate("患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue(),gp.getCreator()
						,gp.getId(),gp.getName(),gp.getIntroduction(),gp.getLogoUrl());
				
				List<String>userIdList = pubGroupDAO.getUserPatientIdByGroup(gp.getId(),null);
				subscribe(pubPo,userIdList,gp.getCreator());
			}
			//集团公告
			PubPO pubPo1 = pubGroupService.getPubByMid(gp.getId(),PubTypeEnum.PUB_GROUP_DOCTOR.getValue());
			if(pubPo1 != null)
			{
				pubAccountService.deletePub(pubPo1.getPid());
			}
			{
//				pubPo1 = groupService.createPubFromGroup(gp,"集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue());
				pubPo1 = pubGroupService.createPubForGroupCreate("集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue(),gp.getCreator()
						,gp.getId(),gp.getName(),gp.getIntroduction(),gp.getLogoUrl());
				List<String>userIdList = pubGroupDAO.getDoctorIdByGroup(gp.getId());
				subscribe(pubPo1,userIdList,gp.getCreator());
			}
			System.out.println("创建集团公共号："+gp.getName());
		}
		return JSONMessage.success();
	} */
	
	/*@RequestMapping("/updatePubMsgSource")
	public JSONMessage updatePubMsgSource() {
//		pubAccountDAO.updateMsg();
		return JSONMessage.success();
	}*/
	
}
