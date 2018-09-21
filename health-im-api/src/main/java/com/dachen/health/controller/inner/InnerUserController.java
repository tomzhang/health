package com.dachen.health.controller.inner;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.user.UserSessionService;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.manager.ISMSManager;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/inner_api/user")
public class InnerUserController {

	private Logger logger = LoggerFactory.getLogger(InnerUserController.class);

	@Resource
	private ISMSManager smsManager;

	@Autowired
	private UserSessionService userSessionService;

	@Autowired
	private UserManager userManager;

	@Autowired
	private IPatientService patientService;

	/**
	 * @api {get} /inner_api/user/getToken 根据token获取userId
	 * @apiVersion 1.0.0
	 * @apiName getToken
	 * @apiGroup 内部api
	 * @apiDescription 根据token获取userId
	 *
	 * @apiParam {String} token token
	 * 
	 * @apiSuccess {Integer} userId 用户id，如果为0，则说明是非法token
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping("/getToken")
	public JSONMessage getToken(String token) {
		return JSONMessage.success(null, ReqUtil.instance.getUserId(token));
	}

	/**
	 * @api {get} /inner_api/user/getSimpleUser 获取用户的简单信息
	 * @apiVersion 1.0.0
	 * @apiName getSimpleUser
	 * @apiGroup 内部api
	 * @apiDescription 获取用户的简单信息
	 *
	 * @apiParam {String[]} userId 用户id集合
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping(value = "/getSimpleUser")
	public JSONMessage getSimpleUser(@RequestParam List<Integer> userId) {
		return JSONMessage.success(userSessionService.getSimpleUserInfo(userId));
	}

    @RequestMapping(value = "/getSimpleUsers")
    public JSONMessage getSimpleUser(@RequestParam Integer[] userId) {
        if (Objects.isNull(userId) || userId.length == 0) {
            throw new ServiceException("用户id不能为空");
        }
        List<Integer> userIds = Arrays.asList(userId);
        return JSONMessage.success(userSessionService.getSimpleUserInfo(userIds));
    }
	
	
	/**
	 * @api {get} /inner_api/user/getDetailUser 获取用户的详细信息
	 * @apiVersion 1.0.0
	 * @apiName getDetailUser
	 * @apiGroup 内部api
	 * @apiDescription 获取用户的详细信息
	 *
	 * @apiParam {String[]} userId 用户id集合
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping(value = "/getDetailUser")
	public JSONMessage getDetailUser(@RequestParam List<Integer> userId) {
		return JSONMessage.success(userSessionService.getDetailUserInfo(userId));
	}
	
	/**
	 * @api {get} /inner_api/user/getModifiedUser 获取用户增量接口
	 * @apiVersion 1.0.0
	 * @apiName getModifiedUser
	 * @apiGroup 内部api
	 * @apiDescription 获取用户增量接口，获取在一个时间段内有改变的用户数据
	 *
	 * @apiParam {String[]} userId 用户id集合
	 * @apiParam {Long} startTime 用户id集合
	 *
	 * @apiAuthor 罗超
	 * @date 2017年3月14日9:38:52
	 */
	@RequestMapping(value = "/getModifiedUser")
	public JSONMessage getModifiedUser(@RequestParam List<Integer> userId, Long startTime) {
		return JSONMessage.success(userSessionService.getModifiedUser(userId, startTime));
	}

	/**
	 * @api {get} /inner_api/user/getUserById 获取用户的简单信息
	 * @apiVersion 1.0.0
	 * @apiName getUserById
	 * @apiGroup 内部api
	 * @apiDescription 获取用户的简单信息
	 *
	 * @apiParam {Integer} userId 用户id
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping(value = "/getUserById")
	public JSONMessage getUserById(Integer userId) {
		return JSONMessage.success(null, userSessionService.getUserSession(userId));
	}

	@RequestMapping(value = "/getUsersByIds")
    public JSONMessage getUsersByIds(Integer[] userId) {
        List<Integer> ids = new ArrayList<>();
        if (Objects.nonNull(userId) && userId.length > 0) {
            ids = Arrays.asList(userId);
        }
        return JSONMessage.success(userSessionService.getUserSessionByIds(ids));
    }

	@RequestMapping(value = "/getUsersFromDB")
    public JSONMessage getUsersFromDB(@RequestBody List<String> userIds) {
	    List<Integer> ids = userIds.stream().map(Integer::parseInt).collect(Collectors.toList());
		return JSONMessage.success(userManager.getUsers(ids));
	}

	/**
	 * @api {get} /inner_api/user/getWeChatUserInfoById 获取用户微信相关的信息
	 * @apiVersion 1.0.0
	 * @apiName /inner_api/user/getWeChatUserInfoById
	 * @apiGroup 内部api
	 * @apiDescription 获取用户微信相关的信息
	 *
	 * @apiParam {Integer} userId 用户id
	 *
	 * @apiSuccess {Map} data 发送结果
	 *
	 * @apiAuthor 钟良
	 * @date 2017年5月17日
	 */
	@RequestMapping(value = "/getWeChatUserInfoById")
	public JSONMessage getWeChatUserInfoById(Integer userId) {
		return JSONMessage.success(null, userManager.getWeChatUserInfoById(userId));
	}

	/**
	 * @api {get} /inner_api/user/getUserByTel 通过手机号码和用户类型获取用户信息
	 * @apiVersion 1.0.0
	 * @apiName getUserByTel
	 * @apiGroup 内部api
	 * @apiDescription 通过手机号码和用户类型获取用户信息
	 *
	 * @apiParam {String} telephone 手机号码
	 * @apiParam {Integer} userType 用户类型
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping(value = "/getUserByTel")
	public JSONMessage getUserByTel(String telephone, Integer userType) {
		return JSONMessage.success(userManager.getUser(telephone, userType));
	}
	
	/**
	 * @api {get} /inner_api/user/isRegister 判断用户手机号码是否已注册
	 * @apiVersion 1.0.0
	 * @apiName isRegister
	 * @apiGroup 内部api
	 * @apiDescription 判断用户手机号码是否已注册
	 *
	 * @apiParam {String} telephone 手机号码
	 * @apiParam {Integer} userType 用户类型
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年10月22日
	 */
	@RequestMapping(value = "/isRegister")
	public JSONMessage isRegister(String telephone, Integer userType) {
		return JSONMessage.success(userManager.isRegister(telephone, userType));
	}

	/**
	 * 
	 * @api {[get,post]} inner_api/user/sendSMS 发送短信
	 * @apiVersion 1.0.0
	 * @apiName 发送短信
	 * @apiGroup 内部api
	 * @apiDescription 发送短信的接口，内容可以自己定制
	 * @apiParam {String} phone 电话
	 * @apiParam {String} content 短信内容
	 * 
	 * @apiSuccess {Map} data 发送结果
	 * @apiAuthor 傅永德
	 * @date 2015年11月13日
	 */
	@RequestMapping(value = "/sendSMS")
	public JSONMessage sendSMS(String phone, String content) {
		logger.info("向手机号码：{} 发送短信 {}", phone, content);
		boolean f = smsManager.sendSMS(phone, content);
		if (!f) {
			return JSONMessage.failure("短信发送失败");
		}
		return JSONMessage.success("短信发送成功");
	}
	
	/**
	 * 
	 * @api {[get,post]} inner_api/user/sendVoiceCode 发送短信
	 * @apiVersion 1.0.0
	 * @apiName 发送短信
	 * @apiGroup 内部api
	 * @apiDescription 发送短信的接口，内容可以自己定制
	 * @apiParam {String} phone 电话
	 * @apiParam {String} content 短信内容
	 * 
	 * @apiSuccess {Map} data 发送结果
	 * @apiAuthor 傅永德
	 * @date 2015年11月13日
	 */
	@RequestMapping(value = "/sendVoiceCode")
	public JSONMessage sendVoiceCode(String telephone, String verifyCode) {
		logger.info("向手机号码：{} 发送语音验证码 {}", telephone, verifyCode);
		boolean f = smsManager.sendVoiceCode(telephone, verifyCode);
		if (!f) {
			return JSONMessage.failure("语音验证码发送失败");
		}
		return JSONMessage.success("语音验证码发送成功");
	}

	/**
	 * 
	 * @api {[get,post]} /inner_api/user/getAllUserIdByUserType
	 *      根据userType获取用户Id
	 * @apiVersion 1.0.0
	 * @apiName getAllUserIdByUserType
	 * @apiGroup 内部api
	 * @apiDescription 根据userType获取所有的1237状态的用户Id（用于公共号推送广播消息）
	 * @apiParam {Integer} userType 用户类型
	 * @apiParam {Integer} pageIndex 页码数
	 * @apiParam {Integer} pageSize 页记录数
	 * 
	 * @apiSuccess {String[]} data 用户Id集合
	 * @apiSuccess {String} resultCode 返回码
	 * @apiAuthor 钟良
	 * @date 2016年8月10日
	 */
	@RequestMapping(value = "/getAllUserIdByUserType")
	public JSONMessage getAllUserIdByUserType(@RequestParam(required = true) Integer userType, @RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "20")Integer pageSize) {
		return JSONMessage.success(userManager.getAllUserIdByUserType(userType, pageIndex, pageSize));
	}

	/**
	 * 
	 * @api {[get,post]} /user/login 登录
	 * @apiVersion 1.0.1
	 * @apiName login
	 * @apiGroup 内部api
	 * @apiDescription 用户登录（原参数不变，这里列出的只是新加的参数）
	 * @apiParam {String} telephone 手机号码
	 * @apiParam {Integer} userType 用户类型
	 * @apiParam {String} serial 客户端设备号,用于推送
	 * @apiParam {String} model 客户端类型，值为ios或者android
	 * @apiParam {String} loginGroupId 想登录的医生集团id（为空则默认登录主集团）
	 * @apiParam {Integer} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团；
	 *           6：导医；8：护士；9：店主；10：企业用户；100：游客；
	 * 
	 * @apiSuccess {Map} User 返回参数
	 * @apiSuccess {Integer} User.doctor.cureNum 患者人数
	 * @apiSuccess {Integer} User.doctor.role 角色 1 医生 2 护士 针对医生登录role才起作用
	 * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
	 * @apiSuccess {String} User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
	 * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
	 * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
	 * @apiSuccess {String} User.groupDoctor.onLineState 在线状态1，在线，2离线
	 * @apiSuccess {String} User.loginGroupId 目前登录的医生集团id
	 * @apiSuccess {String} User.groupList[0].skip 集团的屏蔽状态（N表示正常，S表示该集团被屏蔽）
	 * @apiSuccess {Integer} User.bdjlGroupStatus
	 *             （0表示未加入，1表示已加入且为主集团，2表示已加入非主集团，3表示集团邀请用户用户加入）
	 * @apiSuccess {String} User.headPicFileName 头像
	 * @apiSuccess {String} User.name 姓名
	 * @apiSuccess {String} User.telephone 电话
	 * @apiSuccess {Integer} User.communityRole 社区角色权限:0-普通管理员，1-社区管理员
	 * @apiSuccess {Integer} User.status
	 *             1:正常。2：待审核。3：审核未通过。4：暂时禁用。5：永久禁用。6：未激活。7：未认证。8：离职。9：注销
	 * 
	 * @apiSuccess {Integer} resultCode 1:Success； 1040101：帐号不存在；
	 *             1040102：用户名或密码无效； 1040103：请重置密码
	 * @apiAuthor 傅永德
	 * @date 2015年12月26日
	 */
	@RequestMapping(value = "/login")
	public JSONMessage login(@ModelAttribute UserExample example) {
		Map<String, Object> data = userManager.login(example);

		if (data.get("user") != null) {
			User user = (User) data.get("user");

			if (user.getUserType() == UserType.patient.getIndex()) {
				Patient p = patientService.findOne(user.getUserId(), SysConstants.ONESELF);
				if (p != null) {
					data.put("patientId", p.getId());
				}
			}
		}
		return JSONMessage.success(null, data);
	}
	
	/**
	 * @api {[get,post]} inner_api/user/register 注册
	 * @apiVersion 1.0.0
	 * @apiName register
	 * @apiGroup 内部api
	 * @apiDescription 用户注册
	 * @apiParam {String} access_token token
	 * @apiParam {String} telephone 电话
	 * @apiParam {String} password 密码
	 * @apiParam {String} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团；
	 * 6：导医；8：护士；9：店主；10：企业用户；100：游客；
	 * @apiParam {String} name	   姓名
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年7月30日
	 */
	@RequestMapping("/register")
	public JSONMessage register(UserExample example) {
		if ((example.getUserType() == null) || (example.getUserType() == UserType.customerService.getIndex())) {
			throw new ServiceException("用户类型错误，请确认用户类型非空、非客服！");
		}
		setUserSource(example);
		Map<String, Object> result = userManager.registerIMUser(example);
		
		return JSONMessage.success(null, result);
	}
	
	private void setUserSource(UserExample example) {
		UserSource us = new UserSource();
		us.setSourceType(UserEnum.Source.app.getIndex());
		us.setTerminal(UserEnum.Terminal.xg.getIndex());
		example.setUserSource(us);
	}
	
	/**
	 * @api {[get,post]} inner_api/user/getUserIdsByName 通过name和UserType查询用户id列表
	 * @apiVersion 1.0.0
	 * @apiName /user/getUserIdsByName
	 * @apiGroup 内部api
	 * @apiDescription 根据name和UserType查询用户id列表
	 * 
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} userType 用户类型
	 * @apiParam {String} name 姓名
	 * 
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {Integer[]} userIdList 用户id列表
	 * 
	 * @apiAuthor 钟良
	 * @date 2016年10月26日
	 */
	@RequestMapping(value = "getUserIdsByName")
	public JSONMessage getUserIdsByName(String name, Integer userType) {
		return JSONMessage.success(userManager.getUserIdsByName(name, userType));
	}
	
	/**
	 * @api {[get,post]} inner_api/user/searchDoctor 搜索医生
	 * @apiVersion 1.0.0
	 * @apiName searchDoctor
	 * @apiGroup 内部api
	 * @apiDescription 搜索医生
	 * 
	 * @apiParam {String} access_token 凭证
	 * @apiParam {List} hospitalIdList 医院Id集团
	 * @apiParam {String} keyword 搜索关键字
	 * @apiParam {String} departments 科室
	 * 
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年7月25日
	 */
	@RequestMapping("/searchDoctor")
	public JSONMessage searchDoctor(String[] hospitalIdList ,String keyword ,String departments) {
		return JSONMessage.success(userManager.searchDoctor(hospitalIdList, keyword, departments));
	}

	/**
	 * @api {[get,post]} inner_api/user/searchDoctorByKeyword 搜索医生
	 * @apiVersion 1.0.0
	 * @apiName inner_api/user/searchDoctorByKeyword
	 * @apiGroup 内部api
	 * @apiDescription 搜索医生
	 *
	 * @apiParam {String} keyword 搜索关键字
	 * @apiParam {String} departments 科室
	 *
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
	 *
	 * @apiAuthor 钟良
	 * @date 2017年5月18日
	 */
	@RequestMapping("/searchDoctorByKeyword")
	public JSONMessage searchDoctorByKeyword(String keyword, String departments) {
		return JSONMessage.success(userManager.searchDoctorByKeyword(keyword, departments));
	}

	/**
	 * @api {[get,post]} inner_api/user/getDoctorByTelOrNum 通过手机号获取医生信息
	 * @apiVersion 1.0.0
	 * @apiName inner_api/user/getDoctorByTelOrNum
	 * @apiGroup 内部api
	 * @apiDescription 通过手机号获取医生信息
	 *
	 * @apiParam {String} telephone 手机号
	 *
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
	 *
	 * @apiAuthor 钟良
	 * @date 2017年5月18日
	 */
	@RequestMapping("/getDoctorByTelOrNum")
	public JSONMessage getDoctorByTelOrNum(@RequestParam String telephone) {
		return JSONMessage.success(userManager.getDoctorByTelOrNumNoStatus(telephone));
	}

	/**
	 * @api {[get,post]} inner_api/user/invitePUser 药店圏添加邀请患
	 * @apiVersion 1.0.0
	 * @apiName invitePUser
	 * @apiGroup 内部api
	 * @apiDescription 药店圏添加邀请患
	 * 
	 * @apiParam {String} drugStoreId 药店ID
	 * @apiParam {List} storeName 药店名字
	 * @apiParam {String} userName 患者用户姓名
	 * @apiParam {String} telephone 患者用户手机号
	 * 
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {com.dachen.health.commons.vo.User} data 患者用户
	 * 
	 * @apiAuthor zhy
	 * @date 2017年2月13日
	 */
	@RequestMapping("/invitePUser")
	public JSONMessage invitePUser(String drugStoreId,String storeName, String userName, String telephone) throws HttpApiException {
		return JSONMessage.success(userManager.invitePatientFromDrugStore(drugStoreId, storeName, userName, telephone));
	}


	/**
	 * @api {[get,post]} inner_api/user/updateBaseInfo 医生圈H5邀请 时候修改user信息
	 * @apiVersion 1.0.0
	 * @apiName updateBaseInfo
	 * @apiGroup 内部api
	 * @apiDescription 医生圈H5邀请 时候修改user信息
	 *
	 * @apiParam {Integer} userId 用户ID
	 * @apiParam {String} hospitalName 医院名称
	 * @apiParam {String} hospitalId 医院id
	 * @apiParam {String} deptName 科室名称
	 * @apiParam {String} deptId  科室id
	 * @apiParam {String} title 职称
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date 2017年9月30日15:43:42
	 */
	@RequestMapping("/updateBaseInfo")
	public JSONMessage updateBaseInfo(String userId,String hospitalName,String hospitalId,String deptName,String deptId,String title){
		if (StringUtil.isEmpty(userId)
				&& StringUtil.isEmpty(hospitalName)
//				&& StringUtil.isEmpty(hospitalId)
				&& StringUtil.isEmpty(deptName)
				&& StringUtil.isEmpty(deptId)
				&& StringUtil.isEmpty(title)) {
			return JSONMessage.failure("参数有误");
		}
		UserExample userExample=new UserExample();
		Doctor doctor =new Doctor();

		doctor.setHospital(hospitalName);
		if(StringUtil.isNotBlank(hospitalId)) {
            doctor.setHospitalId(hospitalId);
        }
		doctor.setDepartments(deptName);
		doctor.setDeptId(deptId);
		doctor.setTitle(title);
		userExample.setDoctor(doctor);
		try {
			userManager.updateUser(Integer.parseInt(userId),userExample);
		} catch (HttpApiException e) {
			e.getMessage();
		}

		userManager.userInfoChangeNotify(Integer.valueOf(userId));
		return JSONMessage.success();
	}

	/**
	 * @api {[get,post]} inner_api/user/updateUserName 医生圈H5邀请 时候修改user信息
	 * @apiVersion 1.0.0
	 * @apiName updateUserName
	 * @apiGroup 内部api
	 * @apiDescription 医生圈H5邀请 时候修改user信息
	 *
	 * @apiParam {String} name 姓名
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date 2017年10月11日15:52:25
	 */
	@RequestMapping("/updateUserName")
	public JSONMessage updateBaseInfo(String userId,String name){
		if (StringUtil.isEmpty(userId)
				&& StringUtil.isEmpty(name)) {
			return JSONMessage.failure("参数有误");
		}
		UserExample userExample=new UserExample();

		userExample.setName(name);
		try {
			userManager.updateUser(Integer.parseInt(userId),userExample);
		} catch (HttpApiException e) {
			e.getMessage();
		}
		userManager.userInfoChangeNotify(Integer.valueOf(userId));
		return JSONMessage.success();
	}

	/**
	 * @api {[get,post]} inner_api/user/getRegistryUserCount 注册用户 time为空查平台所有注册用户数 time不为空查time以后注册的用户数
	 * @apiVersion 1.0.0
	 * @apiName getRegistryUserCount
	 * @apiGroup 内部api
	 * @apiDescription 注册用户 time为空查平台所有注册用户数 time不为空查time以后注册的用户数
	 *
	 * @apiParam {Long} time 时间
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date  2017年11月29日15:34:39
	 */
	@RequestMapping("/getRegistryUserCount")
	public JSONMessage getRegistryUserCount(Long time){
		return JSONMessage.success(userManager.getRegistryUserCount(time));
	}

	/**
	 * @api {[get,post]} inner_api/user/getActivityUserCount 今日活跃用户
	 * @apiVersion 1.0.0
	 * @apiName 活跃用户
	 * @apiGroup 内部api
	 * @apiDescription 活跃用户
	 *
	 * @apiParam {Long} time 时间
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date  2017年11月29日15:34:39
	 */
	@RequestMapping("/getActivityUserCount")
	public JSONMessage getActivityUserCount(Long time){
		return JSONMessage.success(userManager.getActivityUserCount(time));
	}

	/**
	 * @api {[get,post]} inner_api/user/getCertifyUserCount 认证通过用户 time为空查平台所有认证用户数 time不为空查time以后认证的用户数
	 * @apiVersion 1.0.0
	 * @apiName getCertifyUserCount
	 * @apiGroup 内部api
	 * @apiDescription 认证通过用户 time为空查平台所有认证用户数 time不为空查time以后认证的用户数
	 *
	 * @apiParam {Long} time 时间
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date  2017年11月29日15:34:39
	 */
	@RequestMapping("/getCertifyUserCount")
	public JSONMessage getCertifyUserCount(Long time){
		return JSONMessage.success(userManager.getCertifyUserCount(time));
	}

	/**
	 * @api {[get,post]} inner_api/user/getUserPositionCount 查询一级城市的个数
	 * @apiVersion 1.0.0
	 * @apiName getUserPositionCount
	 * @apiGroup 内部api
	 * @apiDescription 查询一级城市的个数
	 *
	 * @apiParam {Long} time 时间
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date  2017年11月29日15:34:39
	 */
	@RequestMapping("/getUserPositionCount")
	public JSONMessage getUserPositionCount(String city){
		return JSONMessage.success(userManager.getUserPositionCount(city));
	}
	/**
	 * @api {[get,post]} inner_api/user/getSubmitUserCount 查询提交认证数
	 * @apiVersion 1.0.0
	 * @apiName getSubmitUserCount
	 * @apiGroup 内部api
	 * @apiDescription 查询提交认证数
	 *
	 * @apiParam {Long} time 时间
	 *
	 * @apiSuccess {String} resultCode  返回码
	 *
	 * @apiAuthor lim
	 * @date  2017年12月22日11:10:43
	 */
	@RequestMapping("/getSubmitUserCount")
	public JSONMessage getSubmitUserCount(Long time){
		return JSONMessage.success(userManager.getSubmitUserCount(time));
	}


	/**
	 * 根据用户ID获取用户所在科室ID
	 * @param userId
	 * @return
	 */
	@RequestMapping("/getUserDeptId")
	public JSONMessage getUserDeptId(@RequestParam Integer userId) {
		User user = userManager.getUser(userId);
		Map<String,String> deptId = new HashMap<>(1);
		if (user != null && user.getDoctor() != null && user.getDoctor().getDeptId() != null) {
			deptId.put("deptId",user.getDoctor().getDeptId());
		}
		return JSONMessage.success(deptId);
	}

	/**
	 * 根据用户ID列表批量获取用户所在科室ID
	 * @param userIds
	 * @return
	 */
	@RequestMapping("/getUserDeptIds")
	public JSONMessage getUserDeptIds(@RequestParam List<Integer> userIds) {
		List<User> users = userManager.getUsers(userIds);
		Map<Integer,String> deptIds = new HashMap<>(users.size());
		users.forEach(user -> {
			if (user != null && user.getDoctor() != null && user.getDoctor().getDeptId() != null) {
				deptIds.put(user.getUserId(),user.getDoctor().getDeptId());
			}
		});

		return JSONMessage.success(deptIds);
	}

}
