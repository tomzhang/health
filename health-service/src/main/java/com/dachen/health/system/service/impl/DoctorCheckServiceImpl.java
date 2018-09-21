package com.dachen.health.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsDoctor;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.entity.vo.TitleVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.UserOperationLogService;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserConstant;
import com.dachen.health.commons.vo.UserLog;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.system.dao.IDoctorCheckDao;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.param.DoctorNameParam;
import com.dachen.health.system.entity.param.FindDoctorByAuthStatusParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;
import com.dachen.health.system.service.IDoctorCheckService;
import com.dachen.health.user.entity.param.LearningExperienceParam;
import com.dachen.health.user.entity.po.Change;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.pub.service.PubCustomerService;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorCheckServiceImpl<br>
 * Description： 医生审核service实现类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
@Service  @Lazy(true)
public class DoctorCheckServiceImpl implements IDoctorCheckService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected IDoctorCheckDao doctorCheckDao;
    
    @Autowired
    protected IBaseDataDao baseDataDao;
    
    @Autowired
    protected IBaseDataService baseDataService;
    
    @Resource
    protected MobSmsSdk mobSmsSdk;
    
    @Autowired
    protected ICommonGroupDoctorService commonGroupDoctorService;
    
    @Autowired
    protected PubCustomerService pubCustomerService;
    
    @Autowired
    protected PubGroupService pubGroupService;
    
    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;
    
    @Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;
    
    @Autowired
	private UserManager userManager;
    
    @Autowired
    protected UserLogRespository userLogRespository;
    
    @Resource(name = "jedisTemplate")
    protected JedisTemplate jedisTemplate;
    
    @Autowired
    protected IServiceCategoryService serviceCategoryService;

	@Autowired
	private Auth2Helper auth2Helper;

	@Autowired
	private OperationLogMqProducer operationLogMqProducer;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserOperationLogService userOperationLogService;

	@Autowired
	private DoctorCheckHandler doctorCheckHandler;

    /**
     * </p>获取医生信息列表,查询条件为姓名、状态、注册时间</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    @Override
	public PageVO getDoctors(DoctorCheckParam param) {
        // 验证参数,时间和状态
		if ((param.getStatus() == null)
				|| ((param.getStatus() != null) && ((param.getStatus() != UserEnum.UserStatus.normal.getIndex())
						|| (param.getStatus() != UserEnum.UserStatus.fail.getIndex())
						|| (param.getStatus() != UserEnum.UserStatus.uncheck.getIndex())
						//|| (param.getStatus() != UserEnum.UserStatus.tempForbid.getIndex()) // 禁用状态
						|| (param.getStatus() != UserEnum.UserStatus.Unautherized.getIndex())))) {
        } else {
            throw new ServiceException("审核状态参数有误");
        }

        if ((param.getEndTime() != null) && (param.getStartTime() != null) && (param.getStartTime() > param.getEndTime())) {
            throw new ServiceException("开始时间不能大于结束时间");
        }

        return doctorCheckDao.getDoctors(param);
    }
	
	/**
     * </p>获取医生信息列表,查询条件为姓名、状态、注册时间</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    @Override
    public PageVO getDoctorsByName(DoctorNameParam param) {
        return doctorCheckDao.getDoctorsByName(param);
    }

    @Override
    public PageVO findDoctorByAuthStatus(FindDoctorByAuthStatusParam param) {

    	Query<User> q = doctorCheckDao.findDoctorByAuthStatus(param);
    	
    	List<User> list = q.offset(param.getStart()).limit(param.getPageSize()).order("createTime").asList();
    	
    	return new PageVO(list, q.countAll(), param.getPageIndex(), param.getPageSize());
    	
    }
    
    
	/**
     * </p>获取医生详细信息</p>
     * 
     * @param id
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    @Override
	public DoctorCheckVO getDoctor(Integer id) {
        if (id == null) {
            throw new ServiceException("医生id有误");
        }
		DoctorCheckVO doctor = doctorCheckDao.getDoctor(id);
		/* 已通过，未通过，未认证，已封号，已挂起，带上学习经历 */
		if (Objects.equals(doctor.getStatus(), UserEnum.UserStatus.normal.getIndex()) || Objects.equals(doctor.getStatus(), UserEnum.UserStatus.fail.getIndex()) || Objects.equals(doctor.getStatus(), UserEnum.UserStatus.Unautherized.getIndex()) || Objects.equals(doctor.getSuspend(), UserConstant.SuspendStatus.suspend.getIndex()) || Objects.equals(doctor.getSuspend(), UserConstant.SuspendStatus.tempForbid.getIndex())) {
			doctor.setLearningExpList(userRepository.getLearningExperience(id));
		}
        return doctor;
    }

    /**
     * </p>审核医生通过</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    @Override
	public void checked(DoctorCheckParam param) throws HttpApiException {
    	User user = userManager.getUser(param.getUserId());
    	int userType = user.getUserType();
    	param.setUserType(userType);
    	if(UserEnum.UserType.doctor.getIndex() == userType){
    		 checkDoctor(user, param);
    	}else if(UserEnum.UserType.nurse.getIndex() == userType){
    		checkNurse(param);
    	}
    }

	private void checkNurse(DoctorCheckParam param) throws HttpApiException {
		if (param.getUserId() == null) {
	        throw new ServiceException("护士id为空");
	    }
	    if (StringUtil.isBlank(param.getHospital())) {
	        throw new ServiceException("医疗机构为空");
	    }
	    if (StringUtil.isBlank(param.getHospitalId())) {
	        throw new ServiceException("医疗机构Id为空");
	    }
	    if (StringUtil.isBlank(param.getDepartments())) {
	        throw new ServiceException("科室为空");
	    }
	    if (StringUtil.isBlank(param.getTitle())) {
	        throw new ServiceException("职称为空");
	    }
	    // 判断是否在审核
	    if (checking(param.getUserId())) {
	        throw new ServiceException("该护士正在被审核");
	    }
	    
	    // 判断审核状态
	    Integer status = doctorCheckDao.getStatus(param.getUserId());
	    if ((status != null) && ((status == UserEnum.UserStatus.uncheck.getIndex()) 
            || (status == UserEnum.UserStatus.fail.getIndex())
            || (status == UserEnum.UserStatus.Unautherized.getIndex()))) {
	        //判断医院，科室，职称是否正确
	        HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
	        if((hvo == null) || !StringUtil.equals(hvo.getName(), param.getHospital())){
	            throw new ServiceException("医院有误，请重新选择");
	        }
	        
	        DeptVO dvo = baseDataDao.getDept(param.getDepartments());
	        if(dvo == null){
	            throw new ServiceException("科室有误，请重新选择");
	        }
	        
	        TitleVO tvo = baseDataDao.getTitle(param.getTitle());
	        if(tvo == null){
	            throw new ServiceException("职称有误，请重新选择");
	        }
	        
	        //审核通过
	        doctorCheckDao.checkedNurse(param);
	        User user = userManager.getUser(param.getUserId());
	        
	        String[]params=new String[]{param.getNurseShortLinkUrl()};
	        
	        if ((param.getSendSMS()!=null) && param.getSendSMS()) {
	        	final String content = baseDataService.toContent("0053", params);
	        	mobSmsSdk.send(user.getTelephone(), content);				
			}
	        
//	        String tpl = user.getName()+"您好，恭喜您，您提交的认证信息已通过审核！现在登录V小护马上接单吧【"+param.getNurseShortLinkUrl()+"】";
//	        mobSmsSdk.send(user.getTelephone(), tpl);
	        
	        pubCustomerService.welcome(user.getUserId());
	        //设置职业区域
	        
	        RedisUtil.del(Constants.CacheKeyPre.NurseCheck + "_" + param.getUserId());
	    } else {
	        throw new ServiceException("该护士已审核通过无法再次审核");
	    }
	}

	private void checkDoctor(User user, DoctorCheckParam param) throws HttpApiException {
    	// 防止客服快速连续点击，导致记录数据错误
		if (user != null && UserEnum.UserStatus.normal.getIndex() == user.getStatus()) {
	        throw new ServiceException("该医生已审核通过");
	    }
	    if (StringUtil.isBlank(param.getHospital())) {
	        throw new ServiceException("医疗机构为空");
	    }
	    if (StringUtil.isBlank(param.getHospitalId())) {
	        throw new ServiceException("医疗机构Id为空");
	    }
	    if (StringUtil.isBlank(param.getDepartments())) {
	        throw new ServiceException("科室为空");
	    }
	    if (StringUtil.isBlank(param.getDeptId())) {
	        throw new ServiceException("科室Id为空");
	    }
	    if (StringUtil.isBlank(param.getTitle())) {
	        throw new ServiceException("职称为空");
	    }
	    if ((param.getAssistantId() == null) || (param.getAssistantId() == 0)) {
	        throw new ServiceException("医生助手为空");
	    }
	    if (null==param.getRole()) {
	    	param.setRole(UserEnum.DoctorRole.doctor.getIndex());
//	        throw new ServiceException("角色不能够为空！");
	    }
		if (Objects.isNull(param.getRemark())) {
			throw new ServiceException("审核意见不能为空");
		}
		/*if (StringUtils.isBlank(param.getDeptPhone())) {
			throw new ServiceException("科室电话为空");
		}*/
	    // 判断是否在审核
	    if (checking(param.getUserId())) {
	        throw new ServiceException("该医生正在被审核");
	    }
	    // 判断审核状态
	    Integer status = doctorCheckDao.getStatus(param.getUserId());
	    if ((status != null) && ((status == UserEnum.UserStatus.uncheck.getIndex()) 
            || (status == UserEnum.UserStatus.fail.getIndex())
            || (status == UserEnum.UserStatus.Unautherized.getIndex()))) {
	        //判断医院，科室，职称是否正确
	        HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
	        if((hvo == null) || !StringUtil.equals(hvo.getName(), param.getHospital())){
	            throw new ServiceException("医院有误，请重新选择");
	        }
	        
	        DeptVO dvo = baseDataDao.getDeptById(param.getDeptId());
	        if((dvo == null) || !StringUtil.equals(dvo.getName(), param.getDepartments())){
	            throw new ServiceException("科室有误，请重新选择");
	        }
	        
	        TitleVO tvo = baseDataDao.getTitle(param.getTitle());
	        if(tvo == null){
	            throw new ServiceException("职称有误，请重新选择");
	        }
			/*if (!VerificationUtil.checkTelephone(param.getDeptPhone())) {
				throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
			}*/
			param.setUserLevel(UserEnum.UserLevel.AuthenticatedUser.getIndex());
			param.setLimitedPeriodTime(UserEnum.FOREVER_LIMITED_PERIOD);
	        
	        //审核通过
	        doctorCheckDao.checked(param);

	        //异步记录操作日志
			userOperationLogService.logOperationRecord(user, param);

			//重新获取一次修改后的用户信息
			user = userManager.getUser(user.getUserId());
			//更新医生集团医生属性
			commonGroupDoctorService.updateGroupDoctor(user.getUserId(), user.getDoctor().getDepartments());


	        //删除锁KEY
	        RedisUtil.del(Constants.CacheKeyPre.DoctorCheck + "_" + param.getUserId());
	        //发送审核指令
			businessServiceMsg.sendEventForDoctor(user.getUserId(), user.getUserId(),
					UserEnum.UserStatus.normal.getIndex(),
					user.getName(),
					UserEnum.UserLevel.AuthenticatedUser.getIndex(),
					UserEnum.FOREVER_LIMITED_PERIOD);
			logger.info("审核通过发送指令...userId={}; doctorName={};", user.getUserId(), user.getName());

	        //一切操作完成之后将数据推向ES服务器
			EcEvent event = EcEvent.build(EventType.InsertDoctorInfoToEs)
					.param("departments", user.getDoctor().getDepartments())
					.param("name", user.getName())
					.param("skill", user.getDoctor().getSkill())
					.param("userId", user.getUserId())
					.param("expertise",getEsDiseaseTypeList(user.getDoctor().getExpertise()));
			EventProducer.fireEvent(event);

			//审核通过，发送通知
			doctorCheckHandler.checkedNotify(user, param);

			/**
			 * 发送通知清除缓存
			 */
			if (StringUtil.isNotBlank(param.getHeadPicFileName())) {
				userRepository.updateUserSessionCache(user);
			}
	    } else {
	        throw new ServiceException("该医生已审核通过无法再次审核");
	    }
	}
	
	private List<EsDiseaseType>getEsDiseaseTypeList(List<String> diseaseIds){
        if((diseaseIds==null) || (diseaseIds.size()==0)){
            return null;
        }
        List<DiseaseType> diseases = diseaseTypeRepository.findByIds(diseaseIds);
        //医生集团擅长病种
        List<EsDiseaseType>diseaseTypeList=new ArrayList<EsDiseaseType>(diseaseIds.size());
        for(DiseaseType type:diseases){
        	diseaseTypeList.add(new EsDiseaseType(type.getName(),type.getAlias(),type.getRemark()));//TODO remark
        }
        return diseaseTypeList;
	}
	
	
    /**
     * </p>审核医生为通过</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    @Override
	public void fail(DoctorCheckParam param) {
        if (param.getUserId() == null) {
            throw new ServiceException("医生id有误");
        }
        if (StringUtil.isBlank(param.getRemark())) {
            throw new ServiceException("请填写审核未通过意见");
        }
        
        // 判断是否在审核
        if (checking(param.getUserId())) {
            throw new ServiceException("该医生正在被审核");
        }

        User user = userManager.getUser(param.getUserId());
        int userType = user.getUserType();
        if(UserEnum.UserType.doctor.getIndex() == userType){
        	failDoctor(param);
        }else if(UserEnum.UserType.nurse.getIndex() == userType){
        	failNurse(param);
        }
    }

	private void failNurse(DoctorCheckParam param) {
		// 判断审核状态
        Integer status = doctorCheckDao.getStatus(param.getUserId());
        if ((status != null) && ((status == UserEnum.UserStatus.uncheck.getIndex()) 
                || (status == UserEnum.UserStatus.fail.getIndex()))) {
            doctorCheckDao.failNurse(param);
            User user = userManager.getUser(param.getUserId());
//            String tpl="{0}您好，很遗憾，您提交的职业认证信息因为{1}原因，没有通过审核，请登录V小护重新提交资料！";
//			tpl = MessageFormat.format(tpl, new Object[] { user.getName(), param.getRemark() });
//            mobSmsSdk.send(user.getTelephone(), tpl);
            
            String[]params=new String[]{user.getName(), param.getRemark()};
            if ((param.getSendSMS()!=null) && param.getSendSMS()) {
            	final String content = baseDataService.toContent("0057", params);
            	mobSmsSdk.send(user.getTelephone(), content);				
			}

            RedisUtil.del(Constants.CacheKeyPre.NurseCheck + "_" + param.getUserId());
        } else {
            throw new ServiceException("该护士已审核通过无法再次审核");
        }
	}

	private void failDoctor(DoctorCheckParam param) {
		// 判断审核状态
        Integer status = doctorCheckDao.getStatus(param.getUserId());
        if ((status != null) && ((status == UserEnum.UserStatus.uncheck.getIndex()) 
                || (status == UserEnum.UserStatus.fail.getIndex()))) {
            doctorCheckDao.fail(param);
            
	        //操作记录
	        OperationRecord operationRecord = new OperationRecord();
	        operationRecord.setCreateTime(System.currentTimeMillis());
	        operationRecord.setCreator(param.getCheckerId());
	        operationRecord.setObjectId(param.getUserId()+"");
	        operationRecord.setObjectType(UserLogEnum.OperateType.check.getOperate());
	        operationRecord.setContent("医生未通过审核，审核意见：" + param.getRemark());
	        operationRecord.setChange(new Change(UserLogEnum.OperateType.check.getOperate(),"check",null,"医生未通过审核。"));
	        userLogRespository.addOperationRecord(operationRecord);
            
            User user = userManager.getUser(param.getUserId());
			
			String telephone = user.getTelephone();
			String doc = BaseConstants.XG_YSQ_APP;
			if ((param.getSendSMS() != null) && param.getSendSMS()) {
				final String content = baseDataService.toContent("0056", user.getName(), param.getRemark(),doc);
				mobSmsSdk.send(telephone, content, doc);
			}
            RedisUtil.del(Constants.CacheKeyPre.DoctorCheck + "_" + param.getUserId());
            //发送审核指令
	        businessServiceMsg.sendEventForDoctor(user.getUserId(), user.getUserId(), UserEnum.UserStatus.fail.getIndex(), user.getName(),user.getUserLevel(),user.getLimitedPeriodTime());

        } else {
            throw new ServiceException("该医生已审核通过无法再次审核");
        }
	}

    /**
     * </p>当前医生审核状态</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    @Override
	public boolean checking(Integer paramUserId) {
        // 判断是否有可否正在审核，如有审核返回给后面的客服，审核默认时间为10分钟
        String doctorStr = RedisUtil.get(Constants.CacheKeyPre.DoctorCheck + "_" + paramUserId);
        String nurseStr = RedisUtil.get(Constants.CacheKeyPre.NurseCheck + "_" + paramUserId);
        int userId = ReqUtil.instance.getUserId();
        
        if (StringUtil.isNotBlank(doctorStr)) {
            // 正在审核
        	int doctorId = Integer.parseInt(doctorStr);
            if (userId != doctorId) {
                // 不是当前用户审核
                return true;
            }
        }
        if(StringUtil.isNotBlank(nurseStr) ){
        	// 正在审核
        	int nurseId = Integer.parseInt(nurseStr);
        	if(userId != nurseId){
        		//不是当前用户审核
        		return true;	
        	}
        }
        
        UserSession userSession = ReqUtil.instance.getUser();
        int userType = 3;
        if (userSession != null) {
        	userType = userSession.getUserType();			
		}
        
        if(UserEnum.UserType.nurse.getIndex() == userType){
        	RedisUtil.set(Constants.CacheKeyPre.NurseCheck + "_" + paramUserId, String.valueOf(userId), Constants.Expire.MINUTE1);
        }else if (UserEnum.UserType.doctor.getIndex() == userType){
        	RedisUtil.set(Constants.CacheKeyPre.DoctorCheck + "_" + paramUserId, String.valueOf(userId), Constants.Expire.MINUTE1);	
        }
        return false;
    }

	@Override
	@Transactional
	public void uncheck(Integer doctorId, Integer checkerId) {
		//先查询到该用户，判断用户状态是否能进行反审核		
		User doctor = userManager.getUser(doctorId);
		User checker = userManager.getUser(checkerId);
		if (doctor == null) {
			throw new ServiceException("医生未找到");
		}
		Integer oldUserLevel = doctor.getUserLevel();
		Long oldLimitedPeriodTime = doctor.getLimitedPeriodTime();
		
		if ((doctor.getStatus() == null) || ((doctor.getStatus() != null) && (doctor.getStatus() != UserEnum.UserStatus.normal.getIndex()))) {
			throw new ServiceException("当前医生状态不允许反审核");
		}
		
		doctor.setStatus(UserEnum.UserStatus.uncheck.getIndex());
		//反审核修改用户状态发指令
		Map<String,String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType",OperationLogTypeDesc.DOCOTORLEVELCHANGE);
		if (Objects.equals(doctor.getUserType(), UserType.doctor.getIndex())) {
			doctor.setUserLevel(UserEnum.UserLevel.TemporaryUser.getIndex());
			opeartorLog.put("content",
					String.format("(%1$s)被反审核身份由(%2$s)变为(%3$s),有效期由(%4$s)变为(%5$s)", doctor.getTelephone(),
							UserEnum.UserLevel.getName(oldUserLevel),
							UserEnum.UserLevel.getName(UserEnum.UserLevel.TemporaryUser.getIndex()),
							DateUtil.formatDate2Str(oldLimitedPeriodTime, null),
							DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
		}
		
        //操作记录
        OperationRecord operationRecord = new OperationRecord();
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setCreator(checkerId);
        operationRecord.setObjectId(doctorId+"");
        operationRecord.setObjectType(UserLogEnum.OperateType.uncheck.getOperate());
        operationRecord.setContent("对医生进行了反审核。");
        operationRecord.setChange(new Change(UserLogEnum.OperateType.uncheck.getOperate(),"uncheck",null,"对医生进行了反审核。"));
        userLogRespository.addOperationRecord(operationRecord);
        
		userManager.uncheck(doctor, checkerId, checker.getName());
		
		//和产品确认反审核需要发送指令
		if (Objects.equals(doctor.getUserType(), UserType.doctor.getIndex())) {
			businessServiceMsg.sendEventForDoctor(doctor.getUserId(), doctor.getUserId(),
					UserEnum.UserStatus.uncheck.getIndex(), doctor.getName(),
					UserEnum.UserLevel.TemporaryUser.getIndex(),
					UserEnum.FOREVER_LIMITED_PERIOD);
			if (!CollectionUtils.isEmpty(opeartorLog) && Objects.equals(opeartorLog.size(), 2)) {
				operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(), opeartorLog.get("operationType"),
						opeartorLog.get("content"));
			}
		} else {
			businessServiceMsg.sendEventForDoctor(doctor.getUserId(), doctor.getUserId(),
					UserEnum.UserStatus.uncheck.getIndex(), doctor.getName());
		}

		//将redis中该用户的token删除
		UserUtil.clearUserTokens(doctorId);

		//将全文索引里面该医生的信息删除
		EsDoctor esDoctor = new EsDoctor(String.valueOf(doctor.getUserId()));
		try{
			ElasticSearchFactory.getInstance().deleteDocument(esDoctor);
		}catch(Exception e){
			logger.error("将全文索引里面该医生的信息删除异常.userId{}",doctor.getUserId());
		}

		//失效该医生的账号token
		auth2Helper.invalidToken(doctorId);

		//同时将操作记录到另外一张表
		UserLog userLog = new UserLog();
		userLog.setUserId(doctorId);
		userLog.setOperaterId(checkerId);
		userLog.setOperateType(UserLogEnum.OperateType.uncheck.getIndex());
		
		userLogRespository.addUserLog(userLog);
	}



	/**
     * </p>编辑医生资料(修改医生提交的资料)</p>
     * @param param
     * @author tanyf
     * @date 2016年5月31日
     */
	@Override
	public void edit(DoctorCheckParam param){
		int userId = param.getUserId();
		User user = userManager.getUser(userId);
    	int userType = user.getUserType();
    	param.setUserType(userType);
    	if(UserEnum.UserType.doctor.getIndex() == userType){
    		editDoctor(param);
    	}else if(UserEnum.UserType.nurse.getIndex() == userType){
    		editNurse(param);
    	}
    	
    	// 用户信息修改通知
		if (StringUtils.isNotBlank(param.getName()) || StringUtils.isNotBlank(param.getHeadPicFileName())) {
			if (StringUtils.isNotBlank(param.getName())) {
				user.setName(param.getName());
			}
			if (StringUtils.isNotBlank(param.getHeadPicFileName())) {
				user.setHeadPicFileName(param.getHeadPicFileName());
			}
			userManager.updateUserSessionCache(user);
		}
    	// 强制退出
    	Integer forceQuitApp = param.getForceQuitApp();
		if((forceQuitApp!=null) && (forceQuitApp.intValue() == 1)){
			//删除当前用户的所有token
			auth2Helper.invalidToken(userId);
		}
	}

	private void editDoctor(DoctorCheckParam param) {
		if (param.getUserId() == null) {
	        throw new ServiceException("医生id有误");
	    }
	    if (StringUtil.isBlank(param.getHospital())) {
	        throw new ServiceException("医疗机构为空");
	    }
	    if (StringUtil.isBlank(param.getHospitalId())) {
	        throw new ServiceException("医疗机构Id为空");
	    }
	    if (StringUtil.isBlank(param.getDepartments())) {
	        throw new ServiceException("科室为空");
	    }
	    if (StringUtil.isBlank(param.getDeptId())) {
	        throw new ServiceException("科室Id为空");
	    }
	    if (StringUtil.isBlank(param.getTitle())) {
	        throw new ServiceException("职称为空");
	    }
	    if ((param.getAssistantId() == null) || (param.getAssistantId() == 0)) {
	        throw new ServiceException("医生助手为空");
	    }
	    if (null==param.getRole()) {
	    	param.setRole(UserEnum.DoctorRole.doctor.getIndex());
//	        throw new ServiceException("角色不能够为空！");
	    }
		/*if (StringUtils.isBlank(param.getDeptPhone())) {
			throw new ServiceException("科室电话为空");
		}*/
		// 校验学习经历
		List<LearningExperienceParam> learningExpList = JSON.parseArray(param.getLearningExp(), LearningExperienceParam.class);
		if (!CollectionUtils.isEmpty(learningExpList)) {
			if (learningExpList.size() >= 10) {
				throw new ServiceException("不能超过10个学习经历");
			}
			checkLearningExp(learningExpList);
		}
	    // 判断是否在审核|编辑
	    if (checking(param.getUserId())) {
	        throw new ServiceException("该医生正在被编辑");
	    }

	    // 判断审核状态
	    Integer status = doctorCheckDao.getStatus(param.getUserId());
	    if (((status != null) && ( 
	    		(status == UserEnum.UserStatus.normal.getIndex())
	            || (status == UserEnum.UserStatus.fail.getIndex())))
	            || (status == UserEnum.UserStatus.Unautherized.getIndex())) {
	        //判断医院，科室，职称是否正确
	        HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
	        if((hvo == null) || !StringUtil.equals(hvo.getName(), param.getHospital())){
	            throw new ServiceException("医院有误，请重新选择");
	        }
	        
	        DeptVO dvo = baseDataDao.getDept(param.getDepartments());
	        if(dvo == null){
	            throw new ServiceException("科室有误，请重新选择");
	        }
	        
	        TitleVO tvo = baseDataDao.getTitle(param.getTitle());
	        if(tvo == null){
	            throw new ServiceException("职称有误，请重新选择");
	        }

			/*if (!VerificationUtil.checkTelephone(param.getDeptPhone())) {
				throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
			}*/

			User user = userManager.getUser(param.getUserId());
	        //编辑资料
	        doctorCheckDao.edit(param);
			/* 增加学习经历 先把原来的删除 */
			if (!CollectionUtils.isEmpty(learningExpList)) {
				userRepository.delLearningExpByUserId(param.getUserId());
				for (LearningExperienceParam learningExp : learningExpList) {
					userRepository.addLearningExperience(learningExp, param.getUserId());
				}
			}
			//记录医生信息改变
	        userOperationLogService.logDoctorChange(user, param);

	        commonGroupDoctorService.updateGroupDoctor(user.getUserId(), user.getDoctor().getDepartments());
	       
	        //设置职业区域
	        RedisUtil.del(Constants.CacheKeyPre.DoctorCheck + "_" + param.getUserId());

	        //一切操作完成之后将数据推向ES服务器
			EcEvent event = EcEvent.build(EventType.InsertDoctorInfoToEs)
					.param("departments", user.getDoctor().getDepartments())
					.param("name", user.getName())
					.param("skill", user.getDoctor().getSkill())
					.param("userId", user.getUserId())
					.param("expertise",getEsDiseaseTypeList(user.getDoctor().getExpertise()));
			EventProducer.fireEvent(event);
			
	    } else {
	    	throw new ServiceException("该医生状态错误");
	    }
	}

	private void checkLearningExp(List<LearningExperienceParam> learningExpList) {
		for (LearningExperienceParam param : learningExpList) {
			if (StringUtil.isBlank(param.getCollegeName())) {
				throw new ServiceException("毕业院校不能为空");
			}
			if (StringUtil.isBlank(param.getDepartments())) {
				throw new ServiceException("院系不能为空");
			}
			if (StringUtil.isBlank(param.getQualifications())) {
				throw new ServiceException("学历不能为空");
			}
			if (Objects.isNull(param.getStartTime())) {
				throw new ServiceException("入学时间不能为空");
			}
		}
	}

	private void editNurse(DoctorCheckParam param) {
		if (param.getUserId() == null) {
	        throw new ServiceException("护士id为空");
	    }
	    if (StringUtil.isBlank(param.getHospital())) {
	        throw new ServiceException("医疗机构为空");
	    }
	    if (StringUtil.isBlank(param.getHospitalId())) {
	        throw new ServiceException("医疗机构Id为空");
	    }
	    if (StringUtil.isBlank(param.getDepartments())) {
	        throw new ServiceException("科室为空");
	    }
	    if (StringUtil.isBlank(param.getTitle())) {
	        throw new ServiceException("职称为空");
	    }
	    // 判断是否在审核
	    if (checking(param.getUserId())) {
	        throw new ServiceException("该护士正在被编辑");
	    }
	    
	    // 判断审核状态
	    Integer status = doctorCheckDao.getStatus(param.getUserId());
	    if (((status != null) && ( 
	    		(status == UserEnum.UserStatus.normal.getIndex())
	            || (status == UserEnum.UserStatus.fail.getIndex())))
	            || (status == UserEnum.UserStatus.Unautherized.getIndex())) {
	        //判断医院，科室，职称是否正确
	        HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
	        if((hvo == null) || !StringUtil.equals(hvo.getName(), param.getHospital())){
	            throw new ServiceException("医院有误，请重新选择");
	        }
	        
	        DeptVO dvo = baseDataDao.getDept(param.getDepartments());
	        if(dvo == null){
	            throw new ServiceException("科室有误，请重新选择");
	        }
	        
	        TitleVO tvo = baseDataDao.getTitle(param.getTitle());
	        if(tvo == null){
	            throw new ServiceException("职称有误，请重新选择");
	        }
	        
	        //编辑
	        doctorCheckDao.editNurse(param);
	        
	        RedisUtil.del(Constants.CacheKeyPre.NurseCheck + "_" + param.getUserId());
	    } else {
	        throw new ServiceException("该护士状态错误");
	    }
	}

	@Override
	public PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
		return userManager.getByKeyword(keyword, pageIndex, pageSize);
	}

	@Override
	public void refreshIllegalUser(String[] tels, String pwd,Long time) {
		if(Objects.isNull(tels)){
			throw new ServiceException("非法电话号码不能为空");
		}
		if(tels.length==0){
			throw new ServiceException("非法电话号码不能为空");
		}
		if(!Objects.equals(pwd, "dachen$888888")){
			throw new ServiceException("刷新口令不正确");
		}
		if(Objects.isNull(time)){
			throw new ServiceException("过期时间不能为空");
		}
		if(time>System.currentTimeMillis()){
			throw new ServiceException("过期时间大于当前时间");
		}
		List<String> telList = new ArrayList<>();
		for(String tel : tels){
			if(Objects.nonNull(tel)&&!Objects.equals(tel,"")){
				telList.add(tel);
			}
		}
		List<User> illegalUsers = userRepository.findByTelephone(telList);
		
		if(!CollectionUtils.isEmpty(illegalUsers)){
			logger.info("非法用户刷新电话集合tels.{}",telList.toString());
			for(User user : illegalUsers){
				if(Objects.nonNull(user)&&Objects.equals(user.getUserType(), UserEnum.UserType.doctor.getIndex())){
					userRepository.updateUserLevel(user.getUserId(), UserEnum.UserLevel.Tourist.getIndex(), time);
					businessServiceMsg.sendEventForDoctor(user.getUserId(), user.getUserId(), user.getStatus(), user.getName(),UserEnum.UserLevel.Expire.getIndex(),time);
					userManager.userInfoChangeNotify(user.getUserId()); // 同步状态至circle
			        logger.info("非法用户刷新审核通过发送指令telephone.{} userId.{} status.{} doctorName.{} userLevel.{} limitedPeriodTime.{}",user.getTelephone(),user.getUserId(),user.getStatus(),user.getName(),UserEnum.UserLevel.Tourist.getIndex(),time);
				}
			}
		}
	}

	@Async
	public void updateDoctorHospital(HospitalVO oldHospital, HospitalPO newHospital) {
		boolean isUpdate = false;
		HospitalVO param = new HospitalVO();
		param.setId(newHospital.getId());
		if (!oldHospital.getName().equals(newHospital.getName())) {//医院名称修改
			param.setName(newHospital.getName());
			isUpdate = true;
		}
		if (!oldHospital.getProvince().equals(newHospital.getProvince())
			|| !oldHospital.getCity().equals(newHospital.getCity())
			|| !oldHospital.getCountry().equals(newHospital.getCountry())) {//医院地址修改
			param.setProvince(newHospital.getProvince());
			param.setProvinceName(baseDataService.getAreaNameByCode(newHospital.getProvince()));
			param.setCity(newHospital.getCity());
			param.setCityName(baseDataService.getAreaNameByCode(newHospital.getCity()));
			param.setCountry(newHospital.getCountry());
			param.setCountryName(baseDataService.getAreaNameByCode(newHospital.getCountry()));
			isUpdate = true;
		}
		if (!isUpdate) {
			return;
		}
		List<User> users = userRepository.findByHospitalAndDept(param.getId(), null, null);
		for (User user : users) {
			boolean updateSuccess = userRepository.updateDoctorHospital(user.getUserId(), param);
			if (updateSuccess && !oldHospital.getName().equals(newHospital.getName())) {
				userManager.userInfoChangeNotify(user.getUserId());
			}
		}
	}

	@Override
	public void suspend(Integer userId, Integer adminUserId) {
		User user = userRepository.getUser(userId);
		if (user == null) {
			throw new ServiceException("当前用户不存在");
		}
		if (UserEnum.UserStatus.uncheck.getIndex() != user.getStatus()) {
			throw new ServiceException("只有待审核账号才可以挂起");
		}
		if (UserType.doctor.getIndex() != user.getUserType()) {
			throw new ServiceException("只有医生账号才可以挂起");
		}
		userRepository.updateSuspend(userId, UserConstant.SuspendStatus.suspend.getIndex());
		//操作记录
		OperationRecord operationRecord = new OperationRecord();
		operationRecord.setCreateTime(System.currentTimeMillis());
		operationRecord.setCreator(adminUserId);
		operationRecord.setObjectId(userId+"");
		operationRecord.setObjectType(UserLogEnum.OperateType.suspend.getOperate());
		operationRecord.setContent("挂起");
		operationRecord.setChange(new Change(UserLogEnum.OperateType.suspend.getOperate(),"suspend",null,"挂起"));
		userLogRespository.addOperationRecord(operationRecord);
	}

	@Override
	public void removeSuspend(Integer userId, Integer adminUserId) {
		User user = userRepository.getUser(userId);
		if (user == null) {
			throw new ServiceException("当前用户不存在");
		}
		userRepository.updateSuspend(userId, UserConstant.SuspendStatus.normal.getIndex());
		//操作记录
		OperationRecord operationRecord = new OperationRecord();
		operationRecord.setCreateTime(System.currentTimeMillis());
		operationRecord.setCreator(adminUserId);
		operationRecord.setObjectId(userId+"");
		operationRecord.setObjectType(UserLogEnum.OperateType.removeSuspend.getOperate());
		operationRecord.setContent("解除挂起");
		operationRecord.setChange(new Change(UserLogEnum.OperateType.removeSuspend.getOperate(),"removeSuspend",null,"解除挂起"));
		userLogRespository.addOperationRecord(operationRecord);
	}
}
