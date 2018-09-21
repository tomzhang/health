package com.dachen.health.pack.invite.service.impl;

import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.commons.constants.OrderEnum.CheckInStatus;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.SMSUtil;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.po.CheckIn;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.service.ICheckInService;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.CheckUtils;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvitePatientServiceImpl implements IInvitePatientService {
	
	@Autowired
	private UserRepository userDao;
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private ICheckInService checkIn;
	
	@Autowired
	private IdxRepository idxRepository;
	
	@Autowired
	private IPatientService patientService;
	
	@Autowired
	private FriendsManager friendsManager;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;
	
	@Autowired
    private CheckInMapper checkInMapper;
	
	@Autowired
    private IBusinessServiceMsg businessMsgService; 
	
	@Autowired
	private IBaseDataService baseDataService;
	
	@Autowired
	private IGroupDoctorDao groupdoctorDao;

	@Autowired
	private IServiceCategoryService serviceCategoryService;
	
	@Autowired
	IGroupService groupService;
	
	@Override
	public Map<String, Object> invitePatient(Integer doctorId, String... telephones) {
		Map<String, Object> maps = new HashMap<String, Object>();
		for (String tel : telephones) {
			//校验电话号码格式
			if(!CheckUtils.checkMobile(tel)){
				//校验失败
				maps.put(tel, "手机号码格式不正确");
				continue;
			}
			
			//医生用户信息
			User docUser = userDao.getUser(doctorId);
			//患者用户信息
			User patUser = userDao.getUser(tel, UserType.patient.getIndex());
			Patient patient = null;
			if (patUser != null) {
				if (friendsManager.friends(docUser.getUserId(), patUser.getUserId(), DoctorPatient.class)) {
					maps.put(tel, retMsg(patUser, "已是好友"));
					continue;
				}
				patient = patientService.findOne(patUser.getUserId(), SysConstants.ONESELF);
				maps.put(tel, retMsg(patUser, "已注册非好友"));
			} else {
				patUser = userManager.createInactiveUser(tel, null);
				sendInviteNoteAsync(docUser.getName(), patUser);
				maps.put(tel, retMsg(patUser, "新添加用户"));
			}
			if (patient == null) {
				patient = patientService.save(patUser);
			}
			try {
				Integer checkInId = checkIn.add(initParam_checkIn(docUser, patUser, patient, tel));
				checkIn.updateStatus(initParam_confirm(docUser, checkInId));
			} catch (Exception e) {
				e.printStackTrace();
				maps.put(tel, retMsg(patUser, "添加失败"));
			}
		}
		return maps;
	}
	
	private CheckInParam initParam_checkIn(User docUser, User patUser, Patient patient, String telephone) {
		CheckInParam param = new CheckInParam();
		param.setDoctorId(docUser.getUserId());
		param.setUserId(patUser.getUserId());
		param.setPatientId(patient.getId());
		param.setPhone(telephone);
		param.setCheckInFrom(CheckInParam.CheckInFrom.App.getIndex());
		return param;
	}
	
	private CheckInParam initParam_confirm(User user, Integer checkInId) {
		CheckInParam param = new CheckInParam();
		param.setDoctorId(user.getUserId());
		param.setCheckInId(checkInId);
		param.setStatus(CheckInStatus.confirm.getIndex());
		return param;
	}

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private void sendInviteNoteAsync(String doctorName, User user) {
		this.asyncTaskPool.getPool().submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendInviteNote(doctorName, user);
				} catch (HttpApiException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private void sendInviteNote(String doctorName, User user) throws HttpApiException {
		String url = null;
		String doc = null;
		if (mobSmsSdk.isBDJL(user.getTelephone())) {
			doc = BaseConstants.BD_DOC_APP;	
			url = BaseConstants.BD_DOWN_PAT();
		} else {
			doc = BaseConstants.XG_PLATFORM;
			url = BaseConstants.XG_DOWN_PAT();
		}
		final String content = baseDataService.toContent("0060", doctorName, doc, shortUrlComponent.generateShortUrl(url));
		mobSmsSdk.send(user.getTelephone(), content);
	}

	private Map<String, Object> retMsg(User user, String msg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user.getUserId());
		map.put("msg", msg);
		return map;
	}
	
	@Override
	public void sendNotice(Integer userId) throws HttpApiException {
		sendNotice(userDao.getUser(userId));
	}
	@Override
	public void sendNotice(User user) throws HttpApiException {
		List<CheckIn> list = checkInMapper.getByUserId(user.getUserId());
        for (CheckIn checkIn : list) {
        	List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        	ImgTextMsg textMsg = new ImgTextMsg();
        	textMsg.setStyle(7);
        	textMsg.setTitle("系统通知");
        	textMsg.setTime(System.currentTimeMillis());
        	textMsg.setContent("您邀请的患者"+user.getName()+"已注册玄关健康并与您绑定好友关系，请到患者通讯录中查看");
        	mpt.add(textMsg);
        	businessMsgService.sendTextMsg(checkIn.getDoctorId()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
        }
	}
	
	private void sendSms(Integer userId, Integer toUserId, Integer smsType, String text) throws HttpApiException {
		if (smsType == SMSUtil.SMSType.Text.getIndex() && StringUtil.isBlank(text)) {
			throw new ServiceException("请传入文本消息内容");
		}
		User docUser = userDao.getUser(userId);
		User patUser = userDao.getUser(toUserId);
		String note = SMSUtil.getNote(smsType);
		if (smsType == SMSUtil.SMSType.Text.getIndex()) {
			note = note + ":" + text;
		}
		String url = null;
		String paltFrom = null;
		if(mobSmsSdk.isBDJL(patUser)){
			url = PropertiesUtil.getContextProperty("invite.url")
					+ PropertiesUtil.getContextProperty("invite.download.bdjl") + "?userType=1";
			paltFrom = BaseConstants.BD_DOC_APP;
		}else{
			url = PropertiesUtil.getContextProperty("invite.url")
					+ PropertiesUtil.getContextProperty("invite.download") + "?userType=1";
			paltFrom = BaseConstants.XG_PLATFORM;
		}
		
		/*note = MessageFormat.format(SMSUtil.TEMPALTE,
				new Object[] { docUser.getName(), note, ShortUrlGenerator.getGenerateUrl(url) });*/
		///mobSmsSdk.send(patUser.getTelephone(), note);
		final String msg = baseDataService.toContent("1009", docUser.getName(), note, paltFrom,shortUrlComponent.generateShortUrl(url));
		mobSmsSdk.send(patUser.getTelephone(), msg);
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Autowired
	protected AsyncTaskPool asyncTaskPool;

	@Override
	public void sendSmsAsync(Integer userId, Integer toUserId, Integer smsType, String text) {
		asyncTaskPool.getPool().submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendSms(userId, toUserId, smsType, text);
				} catch (HttpApiException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public Map<String, Object> addOnePatient(Integer doctorId, String  tel) {
		Map<String, Object> maps = new HashMap<String, Object>();
			//校验电话号码格式
			if(!CheckUtils.checkMobile(tel)){
				throw new ServiceException("手机号码格式不正确");
			}
			//医生用户信息
			User docUser = userDao.getUser(doctorId);
			//患者用户信息
			User patUser = userDao.getUser(tel, UserType.patient.getIndex());
			Patient patient = null;
			if (patUser != null) {
				if (friendsManager.friends(docUser.getUserId(), patUser.getUserId(), DoctorPatient.class)) {
					throw new ServiceException("已是好友");
				}
				patient = patientService.findOne(patUser.getUserId(), SysConstants.ONESELF);
			} else {
				//医生邀请患者（2016-6-8傅永德）
				UserSource userSource = new UserSource();
				userSource.setSourceType(UserEnum.Source.doctorInvite.getIndex());
				userSource.setInviterId(doctorId);
				
				//设置用户端的来源（2016-6-12傅永德）
				UserSource tempUserSource = docUser.getSource();
				if (null != tempUserSource && null != tempUserSource.getTerminal()) {
					userSource.setTerminal(tempUserSource.getTerminal());
				} else {
					ServiceCategoryVO serviceCategoryVO = serviceCategoryService.getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
					String bdjlId = serviceCategoryVO.getGroupId();
					
					//获取医生的主集团信息
					GroupDoctor groupDoctor = groupdoctorDao.getByDoctorIdAndGroupId(doctorId, bdjlId);
					if (groupDoctor != null && groupDoctor.isMain()) {
						userSource.setTerminal(UserEnum.Terminal.bdjl.getIndex());
					} else {
						userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
					}
				}
				
				patUser = userManager.createInactiveUser(tel, userSource);
				sendInviteNoteAsync(docUser.getName(), patUser);
			}
			try {
				if (patient == null) {
					patient = patientService.save(patUser);
				}
				Integer checkInId = checkIn.add(initParam_checkIn(docUser, patUser, patient, tel));
				checkIn.updateStatus(initParam_confirm(docUser, checkInId));
				maps.put("userId", patUser.getUserId());
				maps.put("patientId", patient.getId());
			} catch (Exception e) {
				throw new ServiceException("添加失败");
			}
		return maps;
	}

	@Override
	public Map<String,Object> addPatientByGuide(Patient patient) throws HttpApiException {
		String tel = patient.getTelephone();
		User pUser = userDao.getUser(tel, UserType.patient.getIndex());
		boolean addSelf = false;
		if(pUser == null){
			//添加未激活用户
			UserSource userSource = new UserSource();
			userSource.setSourceType(Source.guideInvite.getIndex());
			userSource.setInviterId(ReqUtil.instance.getUserId());
			
			if(ReqUtil.instance.isBDJL()){
				userSource.setTerminal(UserEnum.Terminal.bdjl.getIndex());
			}else{
				userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
			}
			pUser = userManager.createInactiveUser(tel, userSource);
			addSelf = true;
		}else{
			if(SysConstants.ONESELF.equals(patient.getRelation().trim())){
				throw new ServiceException("该用户的当前患者已经存在");
			}
		}
		if(!SysConstants.ONESELF.equals(patient.getRelation().trim()) && addSelf){
			//生成默认的本人患者
			patientService.save(pUser);
		}
		patient.setUserId(pUser.getUserId());
		patientService.save(patient);
		Map<String,Object> map = new HashMap<>();
		map.put("patient", patient);
		map.put("noRegister", addSelf);
		return map;
	}
}
