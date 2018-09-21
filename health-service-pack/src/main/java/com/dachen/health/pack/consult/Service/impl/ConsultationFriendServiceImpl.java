package com.dachen.health.pack.consult.Service.impl;

import com.dachen.common.PinYinUtil;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.consult.dao.ConsultationFriendDao;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.ConsultationApplyFriend;
import com.dachen.health.pack.consult.entity.po.ConsultationFriend;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.consult.entity.vo.*;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.tencent.common.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ConsultationFriendServiceImpl implements ConsultationFriendService{

	@Autowired
	UserManager userManager;
	
	@Autowired
    IGroupDoctorDao gdocDao;
	
	@Autowired
	IGroupService groupService;
	
	@Autowired
	ConsultationFriendDao consultationFriendDaoImpl;
	
	@Autowired
	ConsultationPackDao consultationPackDaoImpl;
	
	@Autowired
	ElectronicIllCaseDao electronicIllCaseDaoImpl;
	
	@Autowired
	IPackService packService;
	
    @Resource
    OrderDoctorMapper orderDoctorMapper;
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Autowired
	IOrderDoctorService orderDoctorService;
	
	@Autowired
    IBaseDataService baseDataService;
	
	@Autowired
	MobSmsSdk mobSmsSdk;
	
	@Autowired
	IMsgService msgService;
	
	@Autowired
    IBusinessServiceMsg businessMsgServiceImpl; 
	
	@Autowired
	IPatientService patientService;
	
	@Autowired
	IBaseDataDao baseDataDao;
	
    @Autowired
    private OrderMapper orderMapper;
	
	@Override
	public DoctorDetail searchAssistantDoctors(String number) {
		User user = userManager.getDoctorByTelOrNum(number);
		if(user == null){
			return null;
		}
		if(user != null && user.getUserId().intValue() == ReqUtil.instance.getUserId()){
			return null;
		}
		return userToDoctorDetail(user,ConsultationEnum.DoctorRole.specialist.getIndex());
	}
	
	private int getFriendRelation(Integer doctorId,Integer currentDoctorRole) {
		int currDoctorId = ReqUtil.instance.getUserId();
		if(ConsultationEnum.DoctorRole.assistant.getIndex() == currentDoctorRole.intValue()){
			 ConsultationApplyFriend cafcu = consultationFriendDaoImpl.getApplyFriendByDoctorId(doctorId,currDoctorId);
			 if(cafcu != null){
			 	int applyType = cafcu.getApplyType();
		    	int status = cafcu.getStatus();
		    	if(applyType == ConsultationEnum.FriendApply.cApplyu.getIndex()){
		    		if(status == ConsultationEnum.ApplyStatus.applying.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.beapplied.getIndex();
		    		}else if(status == ConsultationEnum.ApplyStatus.agree.getIndex()){
		    			return consultationFriendDaoImpl.isCollect(currDoctorId,doctorId,currentDoctorRole) ? 
		    					ConsultationEnum.DoctorFriendType.collect.getIndex() :
				    			ConsultationEnum.DoctorFriendType.befriend.getIndex();
		    		}else if(status == ConsultationEnum.ApplyStatus.ignore.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.norelation.getIndex();
		    		}
		    	}else if(applyType == ConsultationEnum.FriendApply.uApplyc.getIndex()){
		    		if(status == ConsultationEnum.ApplyStatus.applying.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.applying.getIndex();
		    		}else if(status == ConsultationEnum.ApplyStatus.agree.getIndex()){
		    			return consultationFriendDaoImpl.isCollect(currDoctorId,doctorId,currentDoctorRole) ? 
		    					ConsultationEnum.DoctorFriendType.collect.getIndex() :
				    			ConsultationEnum.DoctorFriendType.befriend.getIndex();
		    		}else if(status == ConsultationEnum.ApplyStatus.ignore.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.norelation.getIndex();
		    		}
		    	}else{
		    		throw new ServiceException("用户关系不明确");
		    	}
		    }
		}else if(ConsultationEnum.DoctorRole.specialist.getIndex() == currentDoctorRole.intValue()){
			ConsultationApplyFriend cafuc =	consultationFriendDaoImpl.getApplyFriendByDoctorId(currDoctorId,doctorId);
			if(cafuc != null){
		    	int applyType = cafuc.getApplyType();
		    	int status = cafuc.getStatus();
		    	if(applyType == ConsultationEnum.FriendApply.cApplyu.getIndex()){
		    		if(status == ConsultationEnum.ApplyStatus.applying.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.applying.getIndex();
		    		} else if(status == ConsultationEnum.ApplyStatus.agree.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.befriend.getIndex();
		    		}else{ 
		    			return ConsultationEnum.DoctorFriendType.norelation.getIndex();
		    		}
		    	}else if(applyType == ConsultationEnum.FriendApply.uApplyc.getIndex()){
		    		if(status == ConsultationEnum.ApplyStatus.applying.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.beapplied.getIndex();
		    		}else if(status == ConsultationEnum.ApplyStatus.agree.getIndex()){
		    			return ConsultationEnum.DoctorFriendType.befriend.getIndex();
		    		}else {
		    			return ConsultationEnum.DoctorFriendType.norelation.getIndex();
		    		}
		    	}else{
		    		throw new ServiceException("用户关系不明确");
		    	}
		    }
		}
	    return ConsultationEnum.DoctorFriendType.norelation.getIndex();
	}


	@Override
	public PageVO searchConsultationDoctors(Integer areaCode, String name, String deptId,Integer pageIndex,Integer pageSize) {
		Set<Integer> beSearchIds = getBeSearchConsultationDoctorIds();
		if(Util.isNullOrEmpty(beSearchIds)){
			return null;
		}
		/**
		 * 已经添加了好友关系的就不能再搜索
		 */
		int doctorId = ReqUtil.instance.getUserId();
		ConsultationFriend cf = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(doctorId,2);
		if(cf != null){
			Set<Integer> friendIds = cf.getDoctorIdFriendIds();
			if(!Util.isNullOrEmpty(friendIds)){
				beSearchIds.removeAll(friendIds);
			}
			if(Util.isNullOrEmpty(beSearchIds)){
				return null;
			}
		}
		List<String> hospitalIds = new ArrayList<String>();
		if(areaCode != null){
			//通过地区code获取此地区所有的医院
			List<HospitalPO> hpos = baseDataDao.getHospitals(areaCode, false);
			for (HospitalPO hpo : hpos) {
				hospitalIds.add(hpo.getId());
			}
			if(Util.isNullOrEmpty(hospitalIds)){
				return null;
			}
		}
		
		PageVO pageVo = userManager.searchConsultationDoctors(beSearchIds,hospitalIds,name,deptId,pageIndex,pageSize);
		List<?> list = pageVo.getPageData();
		List<ConsultationFriendsVo> doctorItems = new ArrayList<ConsultationFriendsVo>();
		if(!Util.isNullOrEmpty(list)){
			for (Object obj : list) {
				User user = (User) obj;
				ConsultationFriendsVo item = userToConsultationFriendsVo(user,ConsultationEnum.DoctorRole.assistant.getIndex());
				doctorItems.add(item);
			}
		}
		pageVo.setPageData(doctorItems);
		return pageVo;
	}

	private Set<Integer> getBeSearchConsultationDoctorIds() {
		Set<Integer> doctorIds = consultationPackDaoImpl.getAllConsultationDoctorIds();
		if(Util.isNullOrEmpty(doctorIds)){
			return null;
		}
		Set<Integer> beSearchIds = packService.getAllBeSearchDoctorIds(new ArrayList<Integer>(doctorIds));
		if(Util.isNullOrEmpty(beSearchIds)){
			return null;
		}
		beSearchIds.remove(Integer.valueOf(ReqUtil.instance.getUserId()));
		return beSearchIds;
	}

	@Override
	public DoctorDetail doctorDetail(Integer doctorId,Integer currentDoctorRole) {
		User user = userManager.getUser(doctorId);
		return userToDoctorDetail(user,currentDoctorRole);
	}

	private DoctorDetail userToDoctorDetail(User user,Integer currentDoctorRole) {
		Integer doctorId = user.getUserId();
		Doctor d = user.getDoctor();
		if(d == null){
			return null;
		}
		DoctorDetail item = new DoctorDetail();
		String groupName = "";
		if(user != null){
			 List<GroupDoctor> groups = gdocDao.findMainGroupByDoctorId(user.getUserId());
			 if(!Util.isNullOrEmpty(groups)){
				 String groupId = groups.get(0).getGroupId();
				 Group group = groupService.getGroupById(groupId);
				 groupName = group.getName();
			 }
		}
		item.setDoctorId(user.getUserId());
		item.setApplyStatus(getFriendRelation(doctorId,currentDoctorRole));
		item.setDoctorGroupName(groupName);
		item.setHeadPicFileName(user.getHeadPicFileName());
		item.setHospital(d.getHospital());
		item.setIntroduction(d.getIntroduction());
		item.setName(user.getName());
		item.setDepartments(d.getDepartments());
		item.setTitle(d.getTitle());
		item.setDoctorNum(d.getDoctorNum());
		
		StringBuilder skill = new StringBuilder();
		List<String> expIds = d.getExpertise();
		if(!Util.isNullOrEmpty(expIds)){
			List<DiseaseTypeVO> retData = baseDataService.getDiseaseType(expIds);
			if(!Util.isNullOrEmpty(retData)){
				for (DiseaseTypeVO diseaseTypeVO : retData) {
					skill.append(diseaseTypeVO.getName()).append(",");
				}
				skill.deleteCharAt(skill.length() - 1);
			}
		}
		item.setSkill(skill.toString());
		int currDocId = ReqUtil.instance.getUserId();
		ConsultationApplyFriend caf = consultationFriendDaoImpl.getApplyFriendByDoctorId(currDocId, doctorId);
		if(caf != null){
			item.setApplyMessage(caf.getApplyMessage());
		}else{
			caf = consultationFriendDaoImpl.getApplyFriendByDoctorId(doctorId, currDocId);
			if(caf != null){
				item.setApplyMessage(caf.getApplyMessage());
			}
		}
		Pack pack = packService.getDoctorPackByType(doctorId, PackEnum.PackType.consultation.getIndex());
		if(pack != null){
			item.setConsultationPrice(pack.getPrice().intValue());
			item.setConsultationRequired(pack.getDescription());
		}
		return item;
	}

	@Override
	public void applyFriends(ConsultationApplyFriend consultationApplyFriend) {
		Integer consultationDoctorId = consultationApplyFriend.getConsultationDoctorId();
		Integer unionDoctorId = consultationApplyFriend.getUnionDoctorId();
		Integer applyType = consultationApplyFriend.getApplyType();
		if(consultationDoctorId == null || unionDoctorId == null || applyType == null){
			throw new ServiceException("必要参数为空");
		}
		ConsultationApplyFriend dbObj = consultationFriendDaoImpl.getApplyFriendByDoctorId(consultationDoctorId, unionDoctorId);
		if(dbObj == null){
			consultationApplyFriend.setCreateTime(System.currentTimeMillis());
			consultationApplyFriend.setStatus(ConsultationEnum.ApplyStatus.applying.getIndex());
			consultationFriendDaoImpl.insertApplyFriend(consultationApplyFriend);
		}
		
		/**
		 * 发送申请指令
		 */
		int toUserId = 0;
		if(applyType.intValue() == ConsultationEnum.FriendApply.cApplyu.getIndex()){
			toUserId = unionDoctorId;
		}else if(applyType.intValue() == ConsultationEnum.FriendApply.uApplyc.getIndex()){
			toUserId = consultationDoctorId;
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.CONSULTATION_FRIEND_APPLY.getValue());
		eventVO.setUserId(toUserId+"");
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	@Override
	public List<CategoryFriendsPageVo> getFriendsByRoleType(Integer doctorId, Integer roleType,Integer pageEnterType){
		List<CategoryFriendsPageVo> list = new ArrayList<CategoryFriendsPageVo>();
		ConsultationFriend cf = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(doctorId,roleType);
		if(cf == null){
			return list;
		}
		Set<Integer> friendIds = cf.getDoctorIdFriendIds();
		if(ConsultationEnum.DoctorRole.specialist.getIndex() == roleType.intValue()){
			//大医生获取小医生列表
			if(Util.isNullOrEmpty(friendIds)){
				return list;
			}
			List<User> users = userManager.getDoctorsByIds(new ArrayList<Integer>(friendIds));
			Map<String,List<ConsultationFriendsVo>> map = new HashMap<String,List<ConsultationFriendsVo>>();
			if(!Util.isNullOrEmpty(users)){
				for (User user : users) {
					String name = user.getName();
					String firstLetter = PinYinUtil.getWordsFirstLetter(name);
					List<ConsultationFriendsVo> dataList = map.get(firstLetter);
					if(dataList == null){
						dataList = new ArrayList<ConsultationFriendsVo>();
						map.put(firstLetter, dataList);
					}
					ConsultationFriendsVo cfvo = userToConsultationFriendsVo(user,roleType);
					dataList.add(cfvo);
				}
				Iterator<String> keyIte = map.keySet().iterator();
				
				while(keyIte.hasNext()){
					String firstLetter = keyIte.next();
					List<ConsultationFriendsVo> doctors = map.get(firstLetter);
					CategoryFriendsPageVo obj = new CategoryFriendsPageVo();
					obj.setFirstLetter(firstLetter);
					obj.setDoctors(doctors);
					list.add(obj);
				}
				
				//按照首字母排序
				Collections.sort(list,new Comparator<CategoryFriendsPageVo>() {
					@Override
					public int compare(CategoryFriendsPageVo o1, CategoryFriendsPageVo o2) {
						return o1.getFirstLetter().charAt(0) - o2.getFirstLetter().charAt(0) ;  
					}
				});
			}
		}else if(ConsultationEnum.DoctorRole.assistant.getIndex() == roleType.intValue()){
			//小医生获取大医生列表
			Set<Integer> specialDocIds = cf.getSpecialFriendIds();
			Map<String,List<ConsultationFriendsVo>> map = new HashMap<String,List<ConsultationFriendsVo>>();
			if(!Util.isNullOrEmpty(specialDocIds)){
				List<User> specialUsers = userManager.getDoctorsByIds(new ArrayList<Integer>(specialDocIds));
				List<ConsultationFriendsVo> specialList = new ArrayList<ConsultationFriendsVo>();
				for (User user : specialUsers) {
					ConsultationFriendsVo cfvo = userToConsultationFriendsVo(user,roleType);
					Integer price = cfvo.getConsultationPrice();
					//如果是发起会诊选择会诊医生页面   并且 价格为空就不添加
					if(pageEnterType != null 
							&& pageEnterType.intValue() == 2 
							&& (price == null || price.intValue() == 0)){
					}else{
						specialList.add(cfvo);
					}
				}
				if(!Util.isNullOrEmpty(specialList)){
					CategoryFriendsPageVo sVo = new CategoryFriendsPageVo();
					sVo.setDeptId("special");
					sVo.setDeptName("收藏专家");
					sVo.setDoctors(specialList);
					list.add(sVo);
				}
			}
			List<User> users = userManager.getDoctorsByIds(new ArrayList<Integer>(friendIds));
			if(!Util.isNullOrEmpty(users)){
				for (User user : users) {
					Doctor d = user.getDoctor();
					if(d != null){
						String deptId = d.getDeptId();
						String deptName = d.getDepartments();
						List<ConsultationFriendsVo> dataList = map.get(deptId+","+deptName);
						if(dataList == null){
							dataList = new ArrayList<ConsultationFriendsVo>();
							map.put(deptId+","+deptName, dataList);
						}
						ConsultationFriendsVo cfvo = userToConsultationFriendsVo(user,roleType);
						Integer price = cfvo.getConsultationPrice();
						//如果是发起会诊选择会诊医生页面   并且 价格为空就不添加
						if(pageEnterType != null 
								&& pageEnterType.intValue() == 2 
								&& (price == null || price.intValue() == 0)){
						}else{
							dataList.add(cfvo);
						}
					}
				}
				Iterator<String> keyIte = map.keySet().iterator();
				while(keyIte.hasNext()){
					String keyString = keyIte.next();
					String[] arr = keyString.split(","); 
					List<ConsultationFriendsVo> doctors = map.get(keyString);
					if(!Util.isNullOrEmpty(doctors)){
						CategoryFriendsPageVo obj = new CategoryFriendsPageVo();
						obj.setDeptId(arr[0]);
						obj.setDeptName(arr[1]);
						obj.setDoctors(doctors);
						list.add(obj);
					}
				}
			}
		}
		return list;
	}

	private ConsultationFriendsVo userToConsultationFriendsVo(User user, Integer roleType) {
		ConsultationFriendsVo item = new ConsultationFriendsVo();
		item.setUserId(user.getUserId());
		item.setName(user.getName());
		item.setHeadPicFilleName(user.getHeadPicFileName());
		item.setHospital(user.getDoctor().getHospital());
		item.setTitle(user.getDoctor().getTitle());
		item.setApplyType(getFriendRelation(user.getUserId(),roleType));
		if(ConsultationEnum.DoctorRole.assistant.getIndex() == roleType.intValue()){
			Pack pack = packService.getDoctorPackByType(user.getUserId(), PackEnum.PackType.consultation.getIndex());
			if(pack != null){
				item.setConsultationPrice(pack.getPrice().intValue());
			}
		}
		return item;
	}

	@Override
	public void processFriendsApply(String consultationApplyFriendId, Integer status) {
		ConsultationApplyFriend dbObj = consultationFriendDaoImpl.getApplyFriendById(consultationApplyFriendId);
		if(dbObj == null){
			throw new ServiceException("参数错误");
		}
		int dbstatus = dbObj.getStatus();
		if(dbstatus == status.intValue()){
			throw new ServiceException("参数逻辑错误");
		}
		Integer consultationDoctorId = dbObj.getConsultationDoctorId();
		Integer unionDoctorId = dbObj.getUnionDoctorId();
		Integer applyType = dbObj.getApplyType();
		if(ConsultationEnum.ApplyStatus.agree.getIndex() == status.intValue()){
			//相互添加好友
			ConsultationFriend df_c = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(consultationDoctorId,ConsultationEnum.DoctorRole.specialist.getIndex());
			if(df_c == null){
				ConsultationFriend cf = new ConsultationFriend();
				cf.setDoctorId(consultationDoctorId);
				cf.setFriendType(ConsultationEnum.DoctorRole.specialist.getIndex());
				cf.getDoctorIdFriendIds().add(unionDoctorId);
				consultationFriendDaoImpl.insertDoctorFriend(cf);
			}else{
				consultationFriendDaoImpl.addFriends(consultationDoctorId,unionDoctorId,ConsultationEnum.DoctorRole.specialist.getIndex());
			}
			
			ConsultationFriend df_u = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(unionDoctorId,ConsultationEnum.DoctorRole.assistant.getIndex());
			if(df_u == null){
				ConsultationFriend cf = new ConsultationFriend();
				cf.setDoctorId(unionDoctorId);
				cf.setFriendType(ConsultationEnum.DoctorRole.assistant.getIndex());
				cf.getDoctorIdFriendIds().add(consultationDoctorId);
				consultationFriendDaoImpl.insertDoctorFriend(cf);
			}else{
				consultationFriendDaoImpl.addFriends(unionDoctorId,consultationDoctorId,ConsultationEnum.DoctorRole.assistant.getIndex());
			}
			consultationFriendDaoImpl.updateFriendsApply(consultationApplyFriendId,status);
		}else if(ConsultationEnum.ApplyStatus.ignore.getIndex() == status.intValue()){
			consultationFriendDaoImpl.removeFriendsApply(consultationApplyFriendId);
		}
		//发送好友变化指令
		int toUserId = 0;
		if(applyType.intValue() == ConsultationEnum.FriendApply.cApplyu.getIndex()){
			toUserId = unionDoctorId;
		}else if(applyType.intValue() == ConsultationEnum.FriendApply.uApplyc.getIndex()){
			toUserId = consultationDoctorId;
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.CONSULTATION_FRIEND_APPLY.getValue());
		eventVO.setUserId(toUserId+"");
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	@Override
	public boolean isSpecialist(Integer doctorId) {
		return consultationPackDaoImpl.existsConsultationPack(doctorId);
	}

	@Override
	public PageVO getPatientIllcaseList(Integer doctorId,Integer pageIndex,Integer pageSize){
		PageVO pageVo = new PageVO();
		pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
		pageSize = pageSize == null ? pageVo.getPageSize() : pageIndex;
		long count = electronicIllCaseDaoImpl.getPatientIllcaseListTotal(doctorId);
		List<IllCaseInfo> list = electronicIllCaseDaoImpl.getPatientIllcaseList(doctorId,pageIndex,pageSize);
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		if(!Util.isNullOrEmpty(list)){
			for (IllCaseInfo illCaseInfo : list) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("userId", illCaseInfo.getUserId());
				map.put("patientId", illCaseInfo.getPatientId());
				Patient patient = patientService.findByPk(illCaseInfo.getPatientId());
				if(patient != null){
					map.put("patientName", patient.getUserName());
				}
				dataList.add(map);
			}
		}
		pageVo.setTotal(count);
		pageVo.setPageData(dataList);
		return pageVo;
	}

	@Override
	public Integer getConsultationDoctorNum(Integer doctorId) {
		return consultationFriendDaoImpl.getFriendCount(doctorId,ConsultationEnum.DoctorRole.assistant.getIndex());
	}

	@Override
	public Integer getUnionDoctorNum(Integer doctorId) {
		return consultationFriendDaoImpl.getFriendCount(doctorId,ConsultationEnum.DoctorRole.specialist.getIndex());
	}

	@Override
	public Integer getDoctorApplyNum(Integer doctorId, Integer applyType) {
		return consultationFriendDaoImpl.getFriendApplyCount(doctorId,applyType);
	}

	@Override
	public PageVO getApplyFriendByRoleType(Integer doctorId, Integer roleType, Integer pageIndex, Integer pageSize) {
		PageVO pageVo = new PageVO();
		List<DoctorApplyItemVo> dataList = new ArrayList<DoctorApplyItemVo>();
		pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
		pageSize = pageSize == null ? pageVo.getPageSize() : pageIndex;
		long count = consultationFriendDaoImpl.getApplyFriendCountByRoleType(doctorId,roleType);
		List<ConsultationApplyFriend> list = consultationFriendDaoImpl.getApplyFriendByRoleType(doctorId,roleType,pageIndex,pageSize);
		pageVo.setTotal(count);
		if(!Util.isNullOrEmpty(list)){
			for (ConsultationApplyFriend caf : list) {
				DoctorApplyItemVo daivo = new DoctorApplyItemVo();
				daivo.setConsultationApplyFriendId(caf.getId());
				daivo.setApplyTime(caf.getCreateTime());
				daivo.setApplyType(ConsultationEnum.DoctorFriendType.beapplied.getIndex());
				daivo.setApplyMessage(caf.getApplyMessage());
				User user = null;
				if(roleType.intValue() == 1){
					user = userManager.getUser(caf.getUnionDoctorId());
				}else if(roleType.intValue() == 2){
					user = userManager.getUser(caf.getConsultationDoctorId());
				}
				Doctor doctor = user.getDoctor();
				if(doctor != null){
					daivo.setDepartments(doctor.getDepartments());
					daivo.setHeadPicFilleName(user.getHeadPicFileName());
					daivo.setTitle(doctor.getTitle());
					daivo.setName(user.getName());
					daivo.setHospital(doctor.getHospital());
					daivo.setDoctorId(user.getUserId());
				}
				dataList.add(daivo);
			}
			pageVo.setPageData(dataList);
		}
		return pageVo;
	}

	@Override
	public void collectOperate(Integer unionDoctorId, Integer consultationDoctorId, Integer operateIndex) {
		ConsultationFriend cf = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(unionDoctorId,ConsultationEnum.DoctorRole.assistant.getIndex());
		if(cf == null){
			throw new ServiceException("请先添加该医生为好友");
		}
		Set<Integer> friendIds = cf.getDoctorIdFriendIds();
		Set<Integer> specialIds = cf.getSpecialFriendIds();
		if(ConsultationEnum.CollectOperate.collect.getIndex() == operateIndex.intValue()){
			if(!friendIds.contains(consultationDoctorId)){
				throw new ServiceException("请先添加该医生为好友");
			}
			if(specialIds.contains(consultationDoctorId)){
				throw new ServiceException("请不要重复收藏该医生");
			}
			consultationFriendDaoImpl.collectFriend(unionDoctorId,consultationDoctorId);
		}else if(ConsultationEnum.CollectOperate.cancel.getIndex() == operateIndex.intValue()){
			if(!specialIds.contains(consultationDoctorId)){
				throw new ServiceException("请先收藏该医生");
			}
			consultationFriendDaoImpl.cancelCollectFriend(unionDoctorId,consultationDoctorId);
		}
	}

	@Override
	public List<OrderUserInfoVo> getConsultationMember(Integer orderId) {
		List<OrderUserInfoVo> list = new ArrayList<OrderUserInfoVo>();
		Order order = orderService.getOne(orderId);
		if(order == null){
			return null;
		}
		int userId = order.getUserId();
		User patientUser = userManager.getUser(userId);
		Patient patient = patientService.findByPk(order.getPatientId());
		OrderUserInfoVo vo = new OrderUserInfoVo();
		vo.setRoleType(UserType.patient.getIndex());
		vo.setAgeStr(patient.getAgeStr());
		vo.setUserId(patient.getUserId());
		vo.setHeadPicFileName(patientUser.getHeadPicFileName());
		vo.setName(patient.getUserName());
		vo.setPatientId(patient.getId());
		Integer sex = null;
		if(patient.getSex() != null){
			sex = Integer.valueOf(patient.getSex());
		}
		vo.setSex(sex);
		//添加患者
		list.add(vo);
		int assistantId = order.getDoctorId();
		List<OrderDoctor> docs = orderDoctorService.findOrderDoctors(orderId);
		if(!Util.isNullOrEmpty(docs)){
			for (OrderDoctor od : docs) {
				int specialistId = od.getDoctorId();
				if(specialistId != assistantId){
					OrderUserInfoVo specialistDoctor = userToConsultMember(specialistId);
					//添加专家
					list.add(specialistDoctor);
				}
			}
		}
		
		OrderUserInfoVo assistantDoctor = userToConsultMember(assistantId);
		//添加助手
		list.add(assistantDoctor);
		return list;
	}

	private OrderUserInfoVo userToConsultMember(int specialistId) {
		User u = userManager.getUser(specialistId);
		if(u == null || u.getDoctor() == null){
			return null;
		}
		OrderUserInfoVo item = new OrderUserInfoVo();
		Integer doctorId = u.getUserId();
		Doctor d = u.getDoctor();
		String groupName = "";
		 List<GroupDoctor> groups = gdocDao.findMainGroupByDoctorId(doctorId);
		 if(!Util.isNullOrEmpty(groups)){
			 String groupId = groups.get(0).getGroupId();
			 Group group = groupService.getGroupById(groupId);
			 groupName = group.getName();
		 }else{
//			 GroupDoctor gd = gdocDao.findFirstJoinGroupByDoctorId(doctorId);
			 String gd = gdocDao.findFirstJoinGroupIdByDoctorId(doctorId);// add by tanyf 过滤 屏蔽的集团 20160606
			 if(gd != null){
				 groupName = groupService.getGroupById(gd).getName();
			 }
		 }
		item.setUserId(doctorId);
		item.setDoctorGroupName(groupName);
		item.setHeadPicFileName(u.getHeadPicFileName());
		item.setHospital(d.getHospital());
		item.setIntroduction(d.getIntroduction());
		item.setName(u.getName());
		item.setDepartments(d.getDepartments());
		item.setTitle(d.getTitle());
		item.setRoleType(u.getUserType());
		item.setCureNum(d.getCureNum());
		List<String> expIds = d.getExpertise();
		StringBuilder sb = new StringBuilder();
		if(!Util.isNullOrEmpty(expIds)){
			List<DiseaseTypeVO> retData = baseDataService.getDiseaseType(expIds);
			if(!Util.isNullOrEmpty(retData)){
				for (DiseaseTypeVO diseaseTypeVO : retData) {
					sb.append(diseaseTypeVO.getName()).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		item.setSkill(sb.toString());
		return item;
	}

	@Override
	public void notifySpecialist(Integer orderId) throws HttpApiException {
		Order order = orderService.getOne(orderId);
		if(order == null){
			return ;
		}
		OrderSession session = orderSessionService.findOneByOrderId(orderId);
		String msgGroupId = "";
		if(session != null){
			msgGroupId = session.getMsgGroupId();
		}
		int assistantDoctorId = order.getDoctorId();
		List<OrderDoctor> docs = orderDoctorService.findOrderDoctors(orderId);
		if(!Util.isNullOrEmpty(docs)){
			for (OrderDoctor od : docs) {
				int docId = od.getDoctorId();
				if(assistantDoctorId != docId){
					User specialistDoctor = userManager.getUser(docId);
					User assistantDoctor = userManager.getUser(assistantDoctorId);
//					String template = "尊敬的{0}医生，您的医联体医生{1}与您发起视频会诊，请您接收并回复患者咨询。点击网址 {2} 立即回复";
//					mobSmsSdk.send(specialistDoctor.getTelephone(),
//									MessageFormat.format(template, 
//											specialistDoctor.getName(),
//											assistantDoctor.getName(),
//											PackConstants.greneartenURL("1",msgGroupId, 1))
//							);
					
					String telephone = specialistDoctor.getTelephone();
					String generateUrl = null;
					// add by tanyf 20160613
					generateUrl = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1",msgGroupId, UserEnum.UserType.doctor.getIndex()));

					String[] params = new String[]{specialistDoctor.getName(),
										 assistantDoctor.getName(),
										 generateUrl
									    };
					final String content = baseDataService.toContent("0058", params);
					mobSmsSdk.send(telephone, content);

				}
			}
		}
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Override
	public void sendDirective(Integer orderId) throws HttpApiException {
		//INVITE_JOIN_ROOM
		Order order = orderService.getOne(orderId);
		if(order == null){
			throw new ServiceException("根据orderId找不到对应的订单");
		}
		List<OrderDoctor> ods = orderDoctorService.findOrderDoctors(orderId);
		if(Util.isNullOrEmpty(ods)){
			throw new ServiceException("根据orderId找不到会诊医生的记录");
		}
		OrderSession session = orderSessionService.findOneByOrderId(orderId);
		if(session == null){
			throw new ServiceException("根据orderId找不到对应的会话");
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.INVITE_JOIN_ROOM.getValue());
		eventVO.setUserId(ReqUtil.instance.getUserId()+"");
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("gid", session.getMsgGroupId());
		param.put("roomId", order.getOrderNo());
		param.put("type", EventEnum.INVITE_JOIN_ROOM.getValue());
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
		
		int specialistDoctorId = 0;
		for (OrderDoctor od : ods) {
			if(od.getDoctorId().intValue() != order.getDoctorId().intValue()){
				specialistDoctorId = od.getDoctorId();
			}
		}
		User specialist = userManager.getUser(specialistDoctorId);
		User assistant = userManager.getUser(order.getDoctorId());
		
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg textMsg = new ImgTextMsg();
		textMsg.setStyle(7);
		textMsg.setTitle("电话视频会议通知");
		textMsg.setTime(System.currentTimeMillis());
		textMsg.setContent(specialist.getName()+"医生 您好，"+assistant.getName()+" 医生邀请您加入电话视频会议  立即加入");
		Map<String, Object> param1 = new HashMap<String, Object>();
		textMsg.setFooter("立即加入");
		param1.put("bizType", 21);
		param1.put("bizId", session.getMsgGroupId());
		textMsg.setParam(param1);
		mpt.add(textMsg);
		businessMsgServiceImpl.sendTextMsg(specialistDoctorId+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
	}

	
	@Override
	public PageVO searchConsultationDoctorsByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
		Set<Integer> beSearchIds = getBeSearchConsultationDoctorIds();
		if(Util.isNullOrEmpty(beSearchIds)){
			return null;
		}
		
		/**
		 * 已经添加了好友关系的就不能再搜索
		 */
		int doctorId = ReqUtil.instance.getUserId();
		ConsultationFriend cf = consultationFriendDaoImpl.getDoctorFriendByDoctorIdAndRoleType(doctorId,2);
		if(cf != null){
			Set<Integer> friendIds = cf.getDoctorIdFriendIds();
			if(!Util.isNullOrEmpty(friendIds)){
				beSearchIds.removeAll(friendIds);
			}
			if(Util.isNullOrEmpty(beSearchIds)){
				return null;
			}
		}
		
		PageVO pageVo = userManager.searchConsultationDoctorsByKeyword(beSearchIds,keyword,pageIndex,pageSize);
		List<?> list = pageVo.getPageData();
		List<ConsultationFriendsVo> doctorItems = new ArrayList<ConsultationFriendsVo>();
		if(!Util.isNullOrEmpty(list)){
			for (Object obj : list) {
				User user = (User) obj;
				ConsultationFriendsVo item = userToConsultationFriendsVo(user,ConsultationEnum.DoctorRole.assistant.getIndex());
				doctorItems.add(item);
			}
		}
		pageVo.setPageData(doctorItems);
		return pageVo;
	}
	
	
	@Override
	public PageVO getFriendList(Integer pageIndex, Integer pageSize) {
		PageVO pageVo = new PageVO();
		Integer doctorId = ReqUtil.instance.getUserId();
		List<Integer> friendIds = consultationFriendDaoImpl.findAllDoctorFriendIdByDoctorId(doctorId);
		List<Integer> groupDoctorIds = null;
		List<String> groupIds = new ArrayList<String>();
		List<Integer> allIds = new ArrayList<Integer>();
		List<GroupDoctor> gds = gdocDao.findGroupDoctor(doctorId, null, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		if(gds != null && gds.size() > 0){
			for (GroupDoctor gd : gds) {
				groupIds.add(gd.getGroupId());
			}
		}
		if(groupIds.size() > 0){
			groupDoctorIds = gdocDao.findAllDoctorIdsInGroupIds(groupIds);
		}
		if(friendIds != null){
			allIds.addAll(friendIds);
		}
		if(groupDoctorIds != null){
			allIds.addAll(groupDoctorIds);
		}
		if(allIds.size() < 1){
			return pageVo;
		}
		//排除自己
		Set<Integer> distinctDoctorIds = new HashSet<Integer>(allIds);
		distinctDoctorIds.remove(doctorId);
		List<Integer> params = new ArrayList<Integer>(distinctDoctorIds);
		List<Integer> packFriendIds = packService.getConsultationDoctorId(params);
		if(packFriendIds != null && packFriendIds.size() > 0){
			List<User> userList = userManager.findDoctorsInIds(packFriendIds,0,Integer.MAX_VALUE);
			long count = userManager.findDoctorsInIdsCount(packFriendIds);
			
			pageIndex = pageIndex == null ? 0 : pageIndex;
			pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
			int start = pageIndex * pageSize;
			int end = start + pageSize;
			end = Math.min(end, userList.size());
			if(start >= end){
				return pageVo;
			}
			if(userList != null && userList.size() > 0){
				List<ConsultationFriendsVo> list = new ArrayList<ConsultationFriendsVo>();
				//缓存用户数据
				Map<Integer,User> cacheMap = new HashMap<Integer,User>();
				for (User u : userList) {
					ConsultationFriendsVo vo = new ConsultationFriendsVo();
					vo.setConsultationCount(getConsultationCount(u.getUserId()));
					cacheMap.put(u.getUserId(), u);
					vo.setUserId(u.getUserId());
					list.add(vo);
				}
				Collections.sort(list,new Comparator<ConsultationFriendsVo>() {

					@Override
					public int compare(ConsultationFriendsVo o1, ConsultationFriendsVo o2) {
						return  o2.getConsultationCount() - o1.getConsultationCount();
					}
				});
				List<ConsultationFriendsVo> pageList = new ArrayList<ConsultationFriendsVo>();
				for(int i = start ; i < end ; i++){
					ConsultationFriendsVo vo = list.get(i);
					User u = cacheMap.get(vo.getUserId());
					vo.setName(u.getName());
					vo.setHeadPicFilleName(u.getHeadPicFileName());
					Doctor d = u.getDoctor();
					if(d != null){
						vo.setDepartments(d.getDepartments());
						vo.setTitle(d.getTitle());
						vo.setHospital(d.getHospital());
					}
					Pack pack = packService.getDoctorPackByType(u.getUserId(), PackEnum.PackType.consultation.getIndex());
					if(pack != null){
						vo.setConsultationPrice(pack.getPrice().intValue());
						vo.setConsultationRequired(pack.getDescription());
					}
					vo.setGroupName(getMainGroupName(u.getUserId()));
					pageList.add(vo);
				}
				pageVo.setPageData(pageList);
			}
			pageVo.setTotal(count);
		}
		return pageVo;
	}

	@Override
	public String getMainGroupName(Integer userId) {
		List<GroupDoctor> groups = gdocDao.findMainGroupByDoctorId(userId);
		if(!Util.isNullOrEmpty(groups)){
			 String groupId = groups.get(0).getGroupId();
			 Group group = groupService.getGroupById(groupId);
			 if(group != null){
				 /**
				  * 添加该判断是因为3.7库中c_group 数据表莫名其妙的少了一部分数据导致 好多group找不到
				  */
				 return group.getName();
			 }
		}
		return null;
	}

	public Integer getConsultationCount(Integer doctorId){
		  Map<String,Object> orderMapParam = new HashMap<String,Object>();
		  orderMapParam.put("doctorId", ReqUtil.instance.getUserId());
		  List<Integer> statusList = new ArrayList<Integer>();
		  statusList.add(OrderEnum.OrderStatus.待支付.getIndex());
		  statusList.add(OrderEnum.OrderStatus.已支付.getIndex());
		  statusList.add(OrderEnum.OrderStatus.已完成.getIndex());
		  statusList.add(OrderEnum.OrderStatus.进行中.getIndex());
		  statusList.add(OrderEnum.OrderStatus.预约成功.getIndex());
		  orderMapParam.put("statusList", statusList);
		  List<Integer> currentDoctorOrderIds = orderMapper.findOrderIdByDoctorId(orderMapParam);
		  if(currentDoctorOrderIds != null && currentDoctorOrderIds.size() > 0){
			  Map<String,Object> paramMap = new HashMap<String,Object>();
			  paramMap.put("orderIds", currentDoctorOrderIds);
			  paramMap.put("doctorId", doctorId);
			  List<Integer> orderIds = orderDoctorMapper.findOrderIdByRelationDoctor(paramMap);
			  if(orderIds != null && orderIds.size() > 0){
				  return new HashSet<Integer>(orderIds).size();
			  }
		  }
		  return 0;
	}
	
	@Override
	public PageVO searchDoctors(Integer areaCode, String name, String deptId, Integer pageIndex, Integer pageSize) {
		Integer doctorId = ReqUtil.instance.getUserId();
		List<Integer> friendIds = consultationFriendDaoImpl.findAllDoctorFriendIdByDoctorId(doctorId);
		List<Integer> packDoctorIds = packService.getConsultationDoctorIdNotInIds(friendIds);
		Integer currentUserId = ReqUtil.instance.getUserId();
		packDoctorIds.remove(currentUserId);
		if(packDoctorIds != null && packDoctorIds.size() > 0){
			List<String> hospitalIds = new ArrayList<String>();
			if(areaCode != null){
				//通过地区code获取此地区所有的医院
				List<HospitalPO> hpos = baseDataDao.getHospitals(areaCode, false);
				for (HospitalPO hpo : hpos) {
					hospitalIds.add(hpo.getId());
				}
				if(Util.isNullOrEmpty(hospitalIds)){
					return null;
				}
			}
			PageVO pageVo = userManager.searchConsultationDoctors(new HashSet<Integer>(packDoctorIds),hospitalIds,name,deptId,pageIndex,pageSize);
			List<?> users = pageVo.getPageData();
			List<ConsultationFriendsVo> doctorItems = new ArrayList<ConsultationFriendsVo>();
			if(!Util.isNullOrEmpty(users)){
				for (Object obj : users) {
					User user = (User) obj;
					ConsultationFriendsVo item = new ConsultationFriendsVo();
					item.setUserId(user.getUserId());
					item.setName(user.getName());
					item.setHeadPicFilleName(user.getHeadPicFileName());
					Doctor d = user.getDoctor();
					if(d != null){
						item.setDepartments(d.getDepartments());
						item.setTitle(d.getTitle());
						item.setHospital(d.getHospital());
					}
					Pack pack = packService.getDoctorPackByType(user.getUserId(), PackEnum.PackType.consultation.getIndex());
					if(pack != null){
						item.setConsultationPrice(pack.getPrice().intValue());
						item.setConsultationRequired(pack.getDescription());
					}
					  List<GroupDoctor> groups = gdocDao.findMainGroupByDoctorId(user.getUserId());
					  if(!Util.isNullOrEmpty(groups)){
						 String groupId = groups.get(0).getGroupId();
						 Group group = groupService.getGroupById(groupId);
						 item.setGroupName(group.getName());
					  }
					doctorItems.add(item);
				}
			}
			pageVo.setPageData(doctorItems);
			return pageVo;
		}
		return new PageVO();
	}

	@Override
	public Long getFriendsNum(int userId) {
		Integer doctorId = ReqUtil.instance.getUserId();
		List<Integer> friendIds = consultationFriendDaoImpl.findAllDoctorFriendIdByDoctorId(doctorId);
		List<Integer> groupDoctorIds = null;
		List<String> groupIds = new ArrayList<String>();
		List<Integer> allIds = new ArrayList<Integer>();
		List<GroupDoctor> gds = gdocDao.findGroupDoctor(doctorId, null, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		if(gds != null && gds.size() > 0){
			for (GroupDoctor gd : gds) {
				groupIds.add(gd.getGroupId());
			}
		}
		if(groupIds.size() > 0){
			groupDoctorIds = gdocDao.findAllDoctorIdsInGroupIds(groupIds);
		}
		if(friendIds != null){
			allIds.addAll(friendIds);
		}
		if(groupDoctorIds != null){
			allIds.addAll(groupDoctorIds);
		}
		//排除自己
		Set<Integer> distinctDoctorIds = new HashSet<Integer>(allIds);
		distinctDoctorIds.remove(doctorId);
		List<Integer> params = new ArrayList<Integer>(distinctDoctorIds);
		List<Integer> packFriendIds = packService.getConsultationDoctorId(params);
		return userManager.findDoctorsInIdsCount(packFriendIds);
	}
	

}
