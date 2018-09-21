package com.dachen.health.controller.inner;

import java.util.Objects;

import javax.annotation.Resource;

import com.dachen.health.circle.service.User2Service;
import com.dachen.health.user.service.IRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.commons.constants.HospitalLevelEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("inner_api/doctor")
public class InnerDoctorController {
	 	
	    @Autowired
	    private IBaseUserService baseUserService;
	    
	    @Resource
	    private IGroupDoctorDao groupDoctorDao;
	    
	    @Resource
	    private IPlatformDoctorService pdocService;
	    
	    @Resource
	    private IGroupDoctorService groupDoctorService;
	    
	    @Resource
	    private UserManager userManager;
	    
		@Autowired
		IPackService packService;
	    
	    @Resource
	    private IInvitePatientService invitePatient;
	    
	    @Resource
	    private IGroupService groupService;
	    
	    @Resource
	    private IGroupFacadeService groupFacadeService;
	    
	    @Autowired
		private IBaseDataDao baseDataDao;
	    
	    @Resource
	    private IQrCodeService qrcodeService;
	    
	    @Autowired
	    private IDoctorService doctorService;

		@Autowired
		private IRelationService relationService;

		@Autowired
		private User2Service user2ServiceImpl;
	
	/**
     * @api {post} inner_api/doctor/basicInfo 获取医生基本信息
     * @apiVersion 1.0.0
     * @apiName basicInfo
     * @apiGroup 内部api
     * @apiDescription 获取医生基本信息
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {Integer}    doctorId            医生id（为空，则查询当前登录人）
     * @apiParam   {String}     groupId             集团id（可为空）
     * 
     * @apiSuccess {String}     userId              医生id
     * @apiSuccess {String}     name                姓名
     * @apiSuccess {String}     doctorNum           医生号
     * @apiSuccess {Integer}    isConsultationMember  1:是，0：否 
	 * @apiSuccess {Integer}    consultationPrice       会诊价格
	 * @apiSuccess {String}     consultationRequired    会诊要求
     * @apiSuccess {String}     headPicFileName     头像
     * @apiSuccess {String}     hospital            医院
     * @apiSuccess {String}     departments         科室
     * @apiSuccess {String}     title               职称
     * @apiSuccess {String}     groupId           	集团Id
     * @apiSuccess {String}     groupName           集团名称
     * @apiSuccess {String}     contactWay          集团联系方式
     * @apiSuccess {String}     departmentId        组织Id
     * @apiSuccess {String}     departmentName      组织名称
     * @apiSuccess {Integer}    settings.doctorVerify	好友添加验证
     * @apiSuccess {String}     certStatus      	P已认证，其它非认证
     * @apiSuccess {String}     is3A      			"1":三甲；"0"：非三甲
     *
     * @apiAuthor  范鹏
     * @date 2015年9月11日
     */
    @RequestMapping("/basicInfo")
    public JSONMessage basicInfo(Integer doctorId, String groupId) {
        if(doctorId == null){
            doctorId = ReqUtil.instance.getUserId();
        }
        BaseUserVO vo = baseUserService.getUser(doctorId);
		if (vo != null) {
			
			HospitalVO hospital = baseDataDao.getHospital(vo.getHospitalId());
			if (hospital!= null && hospital.getLevel() != null && hospital.getLevel().equals(HospitalLevelEnum.Three3.getAlias())) {
				vo.setIs3A("1");
			} else {
				vo.setIs3A("0");
			}
			
			BaseUserVO tempvo = baseUserService.getGroupById(vo.getUserId(), groupId);
			if (tempvo != null) {
				// 过滤 屏蔽的集团 add by tanyf 20160616
				Group group = groupService.getGroupById(tempvo.getGroupId());
				if (!GroupSkipStatus.skip.getIndex().equals(group.getSkip())){
					vo.setGroupId(tempvo.getGroupId());
					vo.setGroupName(tempvo.getGroupName());
					vo.setContactWay(tempvo.getContactWay());
					vo.setCertStatus(tempvo.getCertStatus());
				}
			}
			tempvo = baseUserService.getDepartment(vo.getUserId(), vo.getGroupId());
			if (tempvo != null) {
				vo.setDepartmentId(tempvo.getDepartmentId());
				vo.setDepartmentName(tempvo.getDepartmentName());
			}
			if(UserEnum.UserType.doctor.getIndex() == vo.getUserType()){
	    		Pack pack = packService.getDoctorPackByType(doctorId, PackEnum.PackType.consultation.getIndex());
				if(pack != null){
					vo.setConsultationPrice(pack.getPrice().intValue());
					vo.setConsultationRequired(pack.getDescription());
					vo.setIsConsultationMember(1);
				}else{
					vo.setIsConsultationMember(0);
				}
	    	}
			
			vo.setDoctorPageURL(UserUtil.DOCTOR_PAGE_URL() + doctorId);
			vo.setDoctorQrCodeURL(qrcodeService.generateUserQr(doctorId+"", "3"));
		}
        
        return JSONMessage.success(null,vo);
    }
    
    
    /**
     * 
     * @api 			{[get,post]} 					/inner_api/doctor/getExpertise					专长获取
     * @apiVersion 		1.0.0
     * @apiName 		getExpertise	
     * @apiGroup 		专长
     * @apiDescription 	专长获取
     * @apiParam   {Integer}    doctorId            医生id（为空，则查询当前登录人）
     * @apiParam  		{String}    					access_token         		 凭证
     * 
 	 * @apiSuccess 		{int} 							id 		病种id 
	 * @apiSuccess 		{int} 							name 						病种名称
     * @apiAuthor 		李淼淼
     * @author 			李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/getExpertise")
    public JSONMessage getExpertise(Integer doctorId){
    	Object data= null;
    	if(Objects.nonNull(doctorId)){
    		data=doctorService.getExpertise(doctorId);
        }
    	return JSONMessage.success(null,data);
    }

	/**
	 * @api {get} /inner_api/doctor/getAllFriends 医生获取好友
	 * @apiVersion 1.0.0
	 * @apiName getFriends
	 * @apiGroup 医生
	 * @apiDescription 医生获取好友
	 *
	 * @apiParam  {Integer}    userId              医生userId
	 *
	 * @apiSuccess {String}   userId              用户id
	 * @apiSuccess {String}   name                用户姓名
	 * @apiSuccess {String}   telephone           手机
	 * @apiSuccess {Number}   sex                 性别
	 * @apiSuccess {String}   headPicFileName     头像
	 * @apiSuccess {String}   hospital            医院
	 * @apiSuccess {String}   departments         科室
	 * @apiSuccess {String}   title               职称
	 * @apiAuthor  limin  copy method /getFriends
	 * @date 2017年7月26日15:57:01
	 */
	@RequestMapping("/getAllFriends/{userId}")
	public JSONMessage getAllFriends(@PathVariable Integer userId) {
		return JSONMessage.success(null, relationService.getRelation(UserEnum.RelationType.doctorFriend, userId));
	}

	/**
	 * @api {get} /inner_api/doctor/getAllFriends 医生获取好友id列表
	 * @apiVersion 1.0.0
	 * @apiName getFriends
	 * @apiGroup 医生
	 * @apiDescription 医生获取好友
	 *
	 * @apiParam  {Integer}    userId              医生userId
	 *
	 * @apiSuccess {String}   userId              用户id
	 * @apiAuthor  limin  copy method /getFriends
	 * @date 2017年7月26日15:57:01
	 */
	@RequestMapping("/getAllFriendUserIds/{userId}")
	public JSONMessage getAllFriendUserIds(@PathVariable Integer userId) {
		return JSONMessage.success(null, relationService.getRelationUserIds(UserEnum.RelationType.doctorFriend, userId));
	}
	/**
	 * @api {get} /inner_api/doctor/getSameHospitalIdAndDeptIdUserList/{userId} 获取该医生同一个医院同一个科室的医生
	 * @apiVersion 1.0.0
	 * @apiName getSameHospitalIdAndDeptIdUserList/{userId}
	 * @apiGroup 医生
	 * @apiDescription  获取该医生同一个医院同一个科室的医生
	 *
	 * @apiParam  {Integer}    userId              医生id
	 *
	 * @apiSuccess {Integer}  userId              用户id
	 * @apiSuccess {String}   name                用户姓名
	 * @apiSuccess {String}   headPicFileName     头像
	 * @apiSuccess {String}   hospital            医院
	 * @apiSuccess {String}   departments         科室
	 * @apiSuccess {String}   title               职称
	 * @apiSuccess {String}   introduction        介绍
	 * @apiAuthor  limin
	 * @date 2017年7月26日15:57:01
	 */
	@RequestMapping("/getSameHospitalIdAndDeptIdUserList/{userId}")
	public JSONMessage getSameHospitalIdAndDeptIdUserList(@PathVariable Integer userId){
		return JSONMessage.success(null,user2ServiceImpl.findBaseList(userId));
	}
	/**
	 * @api {get} /inner_api/doctor/getDoctorStatusByTelephone/{telephone} 根据手机号码获取状态
	 * @apiVersion 1.0.0
	 * @apiName getDoctorStatusByTelephone/{telephone}
	 * @apiGroup 医生
	 * @apiDescription  根据手机号码获取状态
	 *
	 * @apiParam  {String}    telephone              手机号码
	 *
	 * @apiSuccess {int}   data                     1 正常，2不正常，3未注册
	 * @apiAuthor  limin
	 * @date 2017年8月2日16:21:41
	 */
	@RequestMapping("/getDoctorStatusByTelephone/{telephone}")
	public JSONMessage getDoctorStatusByTelephone(@PathVariable String telephone){
		return JSONMessage.success(null,userManager.getDoctorStatusByTelephone(telephone));
	}
	
	
	
	/**
	 * @api {get} /upgradeUserLevel/{userId} 升级医生用户级别
	 * @apiVersion 1.0.0
	 * @apiName /upgradeUserLevel/{userId}
	 * @apiGroup 医生
	 * @apiDescription  升级医生用户级别
	 * @apiParam  {Integer}    userId              userId
	 * @apiAuthor  wangjin
	 * @date 2017年9月30日
	 */
	@RequestMapping("/upgradeUserLevel/{userId}")
	public JSONMessage upgradeUserLevel(@PathVariable Integer userId){
		return JSONMessage.success(userManager.upgradeUserLevel(userId,"邀请加入医生圈子"));
	}
	
	/**
	 * @api {get} /sendSmsToDownload/{userId}/{content}/{type} 发送短信唤起下载链接
	 * @apiVersion 1.0.0
	 * @apiName /sendSmsToDownload/{userId}/{content}/{type}
	 * @apiGroup 医生
	 * @apiDescription  升级医生用户级别
	 * @apiParam  {Integer}    userId              userId
	 * @apiParam  {String}    content          内容
	 * @apiParam  {Integer}    type           操作类型
	 * @apiAuthor  wangjin
	 * @date 2017年10月17日
	 */
	@RequestMapping("/sendSmsToDownload/{userId}/{content}/{type}")
	public JSONMessage sendSmsToDownload(@PathVariable Integer userId,@PathVariable String content,@PathVariable Integer type){
		userManager.sendSmsToDownload(userId,content,type);
		return JSONMessage.success();
	}
	
	/**
	 * @api {get} /sendSmsToDownloadByTels/{tels}/{content}/{type} 发送短信唤起下载链接
	 * @apiVersion 1.0.0
	 * @apiName /sendSmsToDownloadByTels/{tels}/{content}/{type}
	 * @apiGroup 医生
	 * @apiDescription  升级医生用户级别
	 * @apiParam  {Integer}    userId              userId
	 * @apiParam  {String}    content          内容
	 * @apiParam  {Integer}    type           操作类型
	 * @apiAuthor  wangjin
	 * @date 2017年11月8日
	 */
	@RequestMapping("/sendSmsToDownloadByTels/{tels}/{content}/{type}")
	public JSONMessage sendSmsToDownloadByTels(@PathVariable String tels,@PathVariable String content,@PathVariable String type){
		userManager.sendSmsToDownloadByTels(tels,content,type);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /department/topn/{topn} 按医生认证数统计前十五名的科室
	 * @apiVersion 1.0.0
	 * @apiName /department/topn/{topn}
	 * @apiGroup 医生
	 * @apiDescription  按医生认证数统计前十五名的科室
	 * @apiParam  {Integer}    topn              15
	 * @apiAuthor  wangjin
	 * @date 2017年11月8日
	 */
	@RequestMapping("/department/topn/{topn}")
	public JSONMessage departmentTopn(@PathVariable(value="topn") Integer topn){
		return JSONMessage.success(userManager.departmentTopn(topn));
	}
	
	/**
	 * @api {get} /department/topnNew/{topn} 按医生认证数统计前十五名的科室
	 * @apiVersion 1.0.0
	 * @apiName /department/topnNew/{topn}
	 * @apiGroup 医生
	 * @apiDescription  按医生认证数统计前十五名的科室
	 * @apiParam  {Integer}    topn              15
	 * @apiAuthor  wangjin
	 * @date 2017年11月8日
	 */
	@RequestMapping("/department/topnNew/{topn}")
	public JSONMessage departmentTopnNew(@PathVariable(value="topn") Integer topn){
		return JSONMessage.success(userManager.departmentTopnNew(topn));
	}
	
	/**
     * @api {post} /inner_api/doctor/filterFriends 返回给定列表中与userID是好友的用户id
     * @apiVersion 1.0.0
     * @apiName getFriends
     * @apiGroup 医生
     * @apiDescription 医生获取好友
     *
     * @apiParam  {Integer}    userId              医生userId
     *
     * @apiSuccess {String}   userId              用户id列表
     * @apiAuthor  longjh  
     * @date 2018年7月26日15:57:01
     */
    @RequestMapping("/filterFriends/{userId}")
    public JSONMessage filterFriends(@PathVariable Integer userId, @RequestBody Integer[] filterUserIds) {
        return JSONMessage.success(null, relationService.filterFriends(UserEnum.RelationType.doctorFriend, userId, filterUserIds));
    }
}
