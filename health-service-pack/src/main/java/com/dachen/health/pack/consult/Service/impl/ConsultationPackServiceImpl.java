package com.dachen.health.pack.consult.Service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackStatus;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.consult.Service.ConsultationPackService;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackListVO;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackPageVo;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackParams;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.vo.UserInfoVO;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.BeanUtil;
import com.dachen.util.StringUtil;
import com.tencent.common.Util;

@Service
public class ConsultationPackServiceImpl implements ConsultationPackService{

	@Autowired
	ConsultationPackDao consultationPackDao;
	
	@Autowired
	UserManager userManagerImpl;
	
	@Autowired
    IBusinessServiceMsg businessMsgServiceImpl; 
	
	@Autowired
	IGroupDao groupDaoImpl;
	
	@Autowired
    IPackService packService;
	
	@Autowired
	IGroupService groupService;
	
	@Autowired
	OrderMapper orderMapper;
	
	
	
	@Override
	public PageVO getConsultPackList(ConsultationPackParams consultationPackParams) {
		if(StringUtil.isNullOrEmpty(consultationPackParams.getGroupId()))
			throw new ServiceException("查询参数groupId为空");
		PageVO pageVo = new PageVO();
		long total = consultationPackDao.getTotal(consultationPackParams);
		List<GroupConsultationPack> list = consultationPackDao.getConsultPackList(consultationPackParams);
		pageVo.setTotal(total);
		pageVo.setPageData(list);
		return pageVo;
	}
	public PageVO getConsulPackList(ConsultationPackParams params,List<String> consultationIds){
		long total = consultationPackDao.getTotal(params, null,true);
		List<GroupConsultationPack> list = new ArrayList<GroupConsultationPack>();
		int pageIndex = params.getPageIndex();
		int pageSize = params.getPageSize();
		if(consultationIds !=null && consultationIds.size() > 0){
			List<ObjectId> ids = new ArrayList<ObjectId>();
			for(String id : consultationIds){
				ids.add(new ObjectId(id));
			}
			list.addAll(consultationPackDao.getConsultPackList(params, ids,pageIndex*pageSize,false));
			if(list.isEmpty()){
				int count = (pageIndex+1) * pageSize;
				int hasCount = Integer.parseInt(consultationPackDao.getTotal(params,ids,true)+"");
				int nowPageIndex = (count-hasCount)/pageSize;
				if( (count-hasCount)%pageSize != 0){
					nowPageIndex++;
				}
				params.setPageIndex(nowPageIndex);
				int baseNum = hasCount%pageSize;
				baseNum = pageSize - baseNum;
				int skipNum = getSkipNum(pageIndex, pageSize, baseNum);
				list.addAll(consultationPackDao.getConsultPackList(params, ids,skipNum,true));
			}else if(list.size() < pageSize) {
				params.setPageIndex(0);
				params.setPageSize(pageSize-list.size());
				list.addAll(consultationPackDao.getConsultPackList(params, ids,0,true));
				params.setPageSize(pageSize);
			}
		}else{
			list.addAll(consultationPackDao.getConsultPackList(params, null,pageIndex*pageSize,true));
		}
		return new PageVO(list, total, params.getPageIndex(), params.getPageSize());
	}
	private int getSkipNum(int pageIndex,int pageSize,int baseNum){
		if(pageIndex == 0){
			return 0;
		}
		return (pageIndex-1)*pageSize +baseNum;
	}
	@Override
	public ConsultationPackPageVo getConsultPackDetail(String consultationPackId) {
		GroupConsultationPack gcp = consultationPackDao.getConsultPackDetail(consultationPackId); 
		List<UserInfoVO> doctorInfoList = Util.isNullOrEmpty(gcp.getDoctorIds()) ? null :
			userManagerImpl.getHeaderByUserIds(new ArrayList<Integer>(gcp.getDoctorIds()));
		ConsultationPackPageVo pageVo = BeanUtil.copy(gcp, ConsultationPackPageVo.class);
		if(doctorInfoList != null){
			Map<String,Integer> map = gcp.getDoctorPercents();
			if(map == null){
				throw new ServiceException("该会诊数据有问题");
			}
			for(UserInfoVO vo : doctorInfoList){
				vo.splitPercent= map.get(vo.userId+"");
			}
		}
		User u = userManagerImpl.getUser(pageVo.getConsultationDoctor());
		if(u != null){
			pageVo.setConsultationDoctorName(u.getName());
		}
		pageVo.setDoctorInfoList(doctorInfoList);
		return pageVo;
	}

	@Override
	public void updateConsultPack(ConsultationPackParams param) throws HttpApiException {
		if(StringUtil.isNullOrEmpty(param.getId())){
			throw new ServiceException("会诊包id为空");
		}
		
		GroupConsultationPack gcp = consultationPackDao.getConsultPackDetail(param.getId());
		if(gcp == null){
			return ;
		}
		
		GroupConsultationPack groupConsultationPack = verifyParams(param,false);
		groupConsultationPack.setId(gcp.getId());
		
		consultationPackDao.updateConsultPack(groupConsultationPack);
		
	}

	private void deleteDoctorPack(Integer doctorId) {
	    Object obj = consultationPackDao.findOneOkPack(doctorId);
	    if(obj == null){
	    	packService.deleteConsultationPackByDoctorId(doctorId);
	    }
	}
	
	private void addDoctorPack(Integer doctorId, String groupId) throws HttpApiException {
		//判断该医生的会诊包是否存在
		Pack pack = packService.getDoctorPackDBData(doctorId,PackEnum.PackType.consultation.getIndex());
		if(pack != null){
			if(pack.getStatus() == null || pack.getStatus().intValue() != PackEnum.PackStatus.open.getIndex()){
				long price = Long.valueOf(consultationPackDao.getMinFeeByDoctorId(doctorId));
				packService.updateConsultationPackPrice(doctorId,price);
			}
		}else{
			Pack doctorPack = new Pack();
			doctorPack.setName(PackEnum.PackType.consultation.getTitle());
			//doctorPack.setDescription(PackEnum.PackType.consultation.getTitle());
			//获取当前医生在所有集团的最低范围价格
			doctorPack.setPrice(Long.valueOf(consultationPackDao.getMinFeeByDoctorId(doctorId)));
			doctorPack.setPackType(PackEnum.PackType.consultation.getIndex());
			//doctorPack.setGroupId(groupId);
			doctorPack.setDoctorId(doctorId);
			packService.addPack(doctorPack);
		}
	}
	private void systemNotify(Integer doctorId,Integer packId,String groupId) throws HttpApiException {
		UserSession us = userManagerImpl.getUserById(doctorId);
		Group group = groupDaoImpl.getById(groupId);
		if(packId == null){
			Pack pack = packService.getDoctorPackDBData(doctorId, PackEnum.PackType.consultation.getIndex());
			if(pack == null){
				throw new ServiceException("获取不到当前医生的会诊套餐");
			}
			packId = pack.getId();
		}
		
		String groupName = "";
		String name = "";
		if(us != null){
			name = us.getName();
		}
		if(group != null){
			groupName = group.getName();
		}
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg textMsg = new ImgTextMsg();
		textMsg.setStyle(7);
		textMsg.setTitle("开通会诊套餐通知");
		textMsg.setTime(System.currentTimeMillis());
		textMsg.setContent(name+"医生，您已加入"+groupName+"会诊服务。点击进入查看更多详情");
		Map<String, Object> param = new HashMap<String, Object>();
		textMsg.setFooter("立即查看");
		param.put("bizType", 17);
		param.put("bizId", packId);
		textMsg.setParam(param);
		mpt.add(textMsg);
		businessMsgServiceImpl.sendTextMsg(doctorId+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
	}
	
	private void systemNotify(Integer doctorId,String groupId) throws HttpApiException {
		systemNotify(doctorId,null,groupId);
	}
	private Set<Integer> getDoctorIds(Map<String,Integer> map){
		Set<Integer> set = new HashSet<Integer>();
		Set<String> keys = map.keySet();
		for(String s : keys){
			set.add(Integer.parseInt(s));
		}
		return set;
	}
	private GroupConsultationPack verifyParams(ConsultationPackParams param,boolean isAdd){
		if(StringUtil.isNullOrEmpty(param.getGroupId())){
			throw new ServiceException("集团Id为空");
		}
		String groupId = param.getGroupId();
		if(!StringUtils.equals(groupId, Constants.Id.PLATFORM_ID)){
			//除去平台
			Group g = groupDaoImpl.getById(groupId);
			if(g == null || g.getConfig() == null || !g.getConfig().getOpenConsultation()){
				throw new ServiceException("该集团不存在，或者没有开通会诊服务");
			}
		}
		Map<String,Integer> map = param.getDoctorPercents();
		if(map.isEmpty()){
			throw new ServiceException("分成比例不能为空 ");
		}else{
			if(param.getConsultationDoctorPercent() == null ){
				throw new ServiceException("主会诊医生分成不能为空");
			}
			int sum = param.getConsultationDoctorPercent();
			Set<String> temp = map.keySet();
			for(String s : temp){
				sum +=map.get(s);
			}
			if(sum != 100){
				throw new ServiceException("分成比例总和不对");
			}
		}
		Set<Integer> doctorIds = getDoctorIds(map);
		if(isAdd){
			if(param.getConsultationDoctor() == null || doctorIds.isEmpty()){
				throw new ServiceException("主会诊医师及参与医生不能为空");
			}
		}
		
		if(StringUtil.isEmpty(param.getConsultationPackTitle()) || StringUtil.isEmpty(param.getConsultationPackDesc()) ){
			throw new ServiceException("会诊标题及描述不能为空");
		}
		
		if(param.getConsultationPrice() == null ||param.getConsultationPrice() <= 0){
			throw new ServiceException("价格不能小于0");
		}
		param.setId(null);
		GroupConsultationPack pack = BeanUtil.copy(param, GroupConsultationPack.class);
		pack.setDoctorIds(doctorIds);
		pack.setCreateTime(System.currentTimeMillis());
		return pack;
	}
	
	private void verifyInGroup(GroupConsultationPack param){
		List<Integer> docIds = groupService.getDoctorIdsByGroupId(param.getGroupId());
		if(!docIds.contains(param.getConsultationDoctor())){
			throw new ServiceException("已解除的医生不能加到会诊包内 ");
		}
		if(param.getDoctorIds()  != null){
			for(int doc : param.getDoctorIds()){
				if(!docIds.contains(doc)){
					throw new ServiceException("已解除的医生不能加到会诊包内 ");
				}
			}
		}
		
	}
	@Override
	public GroupConsultationPack addConsultPack(ConsultationPackParams param) throws HttpApiException {
		GroupConsultationPack groupConsultationPack = verifyParams(param,true);
		verifyInGroup(groupConsultationPack);
		GroupConsultationPack obj = consultationPackDao.addConsultPack(groupConsultationPack);
//		List<Pack> list =packService.findByDoctorIdAndPackType(obj.getConsultationDoctor(), PackType.consultation.getIndex());
//		if (list == null || list.isEmpty()){
			//需要向套餐表里添加主医生的套餐
			Pack pack = new Pack();
			pack.setName(PackType.consultation.getTitle());
			pack.setPackType(PackType.consultation.getIndex());
			pack.setDescription(obj.getConsultationPackDesc());
			pack.setPrice(Long.valueOf(obj.getConsultationPrice()));
			pack.setDoctorId(obj.getConsultationDoctor());
			pack.setGroupId(obj.getGroupId());
			pack.setStatus(PackStatus.open.getIndex());
			pack.setConsultationId(obj.getId());
			packService.addPack(pack);
			systemNotify(obj.getConsultationDoctor(),pack.getId(),obj.getGroupId());
//		}
		return obj;
	}
	
	@Override
	public List<UserInfoVO> getDoctorList(String consultationPackId) {
		List<Integer> doctorIds = consultationPackDao.getDoctorIds(consultationPackId);
		return Util.isNullOrEmpty(doctorIds) ? null :
			userManagerImpl.getHeaderByUserIds(doctorIds);
	}

	@Override
	public List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId) {
		return consultationPackDao.getNotInCurrentPackDoctorIds(groupId,consultationPackId);
	}

	@Override
	public int deleteConsultPack(String consultationPackId) {
		if(StringUtil.isEmpty(consultationPackId)){
			throw new ServiceException("会诊套餐ID不能为空");
		}
		GroupConsultationPack pack = consultationPackDao.getById(consultationPackId);
		if(pack == null){
			return 0;
		}else{
//			List<GroupConsultationPack> list = consultationPackDao.getConsultationPackList(pack.getConsultationDoctor());
//			if(list == null || list.isEmpty()){
				packService.deletePackByConsultationId(consultationPackId);
//			}
			consultationPackDao.deleteConsultPack(consultationPackId);
			return 1;
		}
	}

	@Override
	public void openService(String groupId) {
		groupDaoImpl.openService(groupId);
	}

	@Override
	public Map<String, Integer> getConsultationPrice(int doctorId) {
		int min = consultationPackDao.getMinFeeByDoctorId(doctorId);
		int max = consultationPackDao.getMaxFeeByDoctorId(doctorId);
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("consultationMin", min);
		map.put("consultationMax", max);
		return map;
	}

	@Override
	public List<Map<String, Object>> getGroupListByDoctorId(Integer doctorId,Integer orderId) {
		List<String> groupIds = consultationPackDao.getGroupIdsByDoctorId(doctorId);
		List<Map<String, Object>> obj = null;
		if(!Util.isNullOrEmpty(groupIds)){
			obj = groupService.getConsultationGroupByDoctorId(doctorId,groupIds);
		}
		if(orderId != null && (obj == null || obj.size() < 1)){
			Order o = orderMapper.getOne(orderId);
			if(o != null && StringUtil.isNotBlank(o.getGroupId())){
				obj = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				Group group = groupService.getGroupById(o.getGroupId());
				map.put("groupId", group.getId());
				map.put("groupName", group.getName());
				map.put("isMain", false);
				map.put("createTime", 0l);
				obj.add(map);
			}
		}
		if(obj != null && obj.size() > 1){
			Collections.sort(obj, new Comparator<Map<String,Object>>() {

				@Override
				public int compare(Map<String,Object> m1 , Map<String,Object> m2) {
					int m1Flag = (Boolean)m1.get("isMain") ? 1 : 0;
					Long m1Time = (Long)m1.get("createTime");
					
					int m2Flag = (Boolean)m2.get("isMain") ? 1 : 0;
					Long m2Time = (Long)m2.get("createTime");
					
					int flagInterval = m2Flag - m1Flag;
					Long timeInterval = m1Time - m2Time;
					return flagInterval == 0 ? timeInterval.intValue() : flagInterval;
				}
				
			});
		}
		return obj;
	}

	
	/**
	 * map.put("groupId", group.getId());
				map.put("groupName", group.getName());
				map.put("isMain", gd.isMain());
				map.put("createTime", gd.getCreatorDate());
	 */
	@Override
	public String getConsultationOrderGroupByDoctorId(Integer doctorId) {
		List<Map<String, Object>> list = getGroupListByDoctorId(doctorId,null);
		if(!Util.isNullOrEmpty(list)){
			Long maxTime = null;
			String groupId = null;
			for (Map<String, Object> map : list) {
				Long time = (Long) map.get("createTime");
				String gid = map.get("groupId")+"";
				Boolean isMain = (Boolean) map.get("isMain");
				if(isMain){
					return gid;
				}
				if(maxTime == null){
					maxTime = time;
					groupId = gid;
				}else{
					if(maxTime < time){
						maxTime = time;
						groupId = gid;
					}
				}
			}
			return groupId;
		}
		return null;
	}

	@Override
	public Map<String,String> getConsultationPackByGroupIdAndDoctorId(String groupId, Integer doctorId,Long price) {
		GroupConsultationPack gp = consultationPackDao.getConsultationPackByGroupIdAndDoctorId(groupId,doctorId);
		if(gp == null){
			return null;
		}
		long dot = 0;
		price = price == null ? 0l : price;
		if(price != null){
			dot = price % 100;
		}
		price -= dot;
		Integer conPer = gp.getConsultationDoctorPercent();
		Integer uniPre = gp.getUnionDoctorPercent();
		long uniPrice = (price * uniPre) / 100;
		long conPrice = ((price * conPer) / 100) + dot;
		Map<String,String> map = new HashMap<String,String>();
		map.put("uniPrice", uniPrice+"");
		map.put("conPrice", conPrice+"");
		map.put("conPercent", conPer+"");
		map.put("uniPrecent", uniPre+"");
		return map;
	}

	@Override
	public Integer getOpenConsultation(String groupId) {
		Group g = groupDaoImpl.getById(groupId);
		if(g != null && g.getConfig() != null && g.getConfig().getOpenConsultation()){
			return 1;
		}
		return 2;
	}
	
	/*
	 * 查询 当前医生 作为主诊医生会诊包列表
	 */
	public PageVO getMyConsultationPackList(ConsultationPackParams consultationPackParams){
		int pageIndex = consultationPackParams.getPageIndex();
		int pageSize = consultationPackParams.getPageSize();
		long total = consultationPackDao.getTotal(consultationPackParams,null,false);
		List<GroupConsultationPack>  list = consultationPackDao.getConsultPackList(consultationPackParams, null, pageIndex* pageSize, false);
		List<ConsultationPackListVO> data = convertVO(list);
		 for(ConsultationPackListVO vo : data){
    		 setDocotrInfo(vo);
    	 }
		 return new PageVO(data, total, pageIndex, pageSize);
	}
	
	 public PageVO getConsultationPackList(ConsultationPackParams consultationPackParams){
	    	/*
	    	 * 查询 当前医生 作为参与医生会诊包列表
	    	 * 
	    	 * 1，首先根据当前医查出对应的会诊包列表
	    	 * 2，查出当前医生所有发起人会诊次数集合(医生ID，集团Id),因为排序时根据会诊次数排序
	    	 * 3，根据步骤2中查出的会诊ID优先查出会诊包，然后实际情况分页，分页查出会诊主医生及参与医生属性（名字，头像，职称，医院，科室）集合
	    	 * 4，分别给VO设置对应的属性
	    	 * 5，排序（按已发起会诊次数排序，已发起会诊次数相同按标题首字母排序）
	    	 */
		 //根据医生id,集团ID（可以为空），查出所有发起的会诊及次列表。
		 List<Map<String,Object>> timeList = orderMapper.getConsultationTimesByDocIdAndGroupId(consultationPackParams.getDoctorId(), null);
		 //根据医生Id，会诊包ID去会诊包表分页查询
    	 PageVO page = getConsulPackList(consultationPackParams, getMapValueListByKey(timeList, "consultationId"));
    	 List<GroupConsultationPack> list = (List<GroupConsultationPack>)page.getPageData();
    	 List<ConsultationPackListVO> data = convertVO(list);
    	 Map<String,String> groupCache = new HashMap<String,String>();
    	 for(ConsultationPackListVO vo : data){
    		 if(groupCache.get(vo.getGroupId()) == null){
    			 Group group = groupDaoImpl.getById(vo.getGroupId());
        		 if(group != null){
        			 vo.setGroupName(group.getName());
        		 }
        		 groupCache.put(vo.getGroupId(), vo.getGroupName());
    		 }else{
    			 vo.setGroupName(groupCache.get(vo.getGroupId())); 
    		 }
    		 vo.setConsultationTimes(getConsultTimes(timeList,vo.getId()));
    		 setDocotrInfo(vo);
    	 }
    	 sortByTimes(data);
    	 page.setPageData(data);
    	 return page;
    }
	private void sortByTimes(List<ConsultationPackListVO> data){
		Collections.sort(data, new Comparator<ConsultationPackListVO>(){  
            public int compare(ConsultationPackListVO o1, ConsultationPackListVO o2) {  
                if(o1.getConsultationTimes() > o2.getConsultationTimes()){  
                    return -1;  
                }  
                if(o1.getConsultationTimes() == o2.getConsultationTimes()){  
                    return 0;  
                }  
                return 1;  
            }  
        });   
	}
	private void setDocotrInfo(ConsultationPackListVO vo){
		 User user = userManagerImpl.getUser(vo.getConsultationDoctor());
		 if(user != null && user.getDoctor() != null){
			 Doctor doc = user.getDoctor();
			 vo.setDoctorId(vo.getConsultationDoctor());
			 vo.setDoctorDept(doc.getDepartments());
			 vo.setDoctorHostpital(doc.getHospital());
			 vo.setDoctorName(user.getName());
			 vo.setDoctorTitle(doc.getTitle());
			 vo.setDoctorPic(user.getHeadPicFileName());
		 }
		 List<User> userList = userManagerImpl.getDoctorsByIds(new ArrayList<>(vo.getDoctorIds()));
		 List<Map<String,String>> doctorList = new ArrayList<Map<String,String>>();
		 for(User u :userList){
			 Map<String,String> map = new HashMap<String,String>();
			 map.put("id", u.getUserId()+"");
			 map.put("pic", u.getHeadPicFileName());
			 map.put("name", u.getName());
			 doctorList.add(map);
		 }
		 vo.setDoctorList(doctorList);
	}
	private List<String> getMapValueListByKey(List<Map<String,Object>> list,String key){
		List<String> result = null;
		if(list != null && list.size() >0){
			result = new ArrayList<String>();
			for(Map<String,Object> map : list){
				result.add(map.get(key).toString());
			}
		}
		return result;
	}
	private int getConsultTimes(List<Map<String,Object>> list,String consultationId){
		int times = 0; 
		for(Map<String,Object> map : list){
			String id = map.get("consultationId").toString();
			if(id.equals(consultationId)){
				return Integer.parseInt(map.get("times").toString());
			}
		}
		return times;
	}
	private  List<ConsultationPackListVO> convertVO(List<GroupConsultationPack> list){
		return BeanUtil.copyList(list, ConsultationPackListVO.class);
	}

	@Override
	public Map<String, String> getConsultationPackById(String consultationPackId, Long price) {
		GroupConsultationPack gp = consultationPackDao.getById(consultationPackId);
		if(gp == null){
			return null;
		}
		long dot = 0;
		price = price == null ? 0l : price;
		if(price != null){
			dot = price % 100;
		}
		price -= dot;
		Integer conPer = gp.getConsultationDoctorPercent();
		Integer uniPre = gp.getUnionDoctorPercent();
		long uniPrice = (price * uniPre) / 100;
		long conPrice = ((price * conPer) / 100) + dot;
		Map<String,String> map = new HashMap<String,String>();
		map.put("uniPrice", uniPrice+"");
		map.put("conPrice", conPrice+"");
		map.put("conPercent", conPer+"");
		map.put("uniPrecent", uniPre+"");
		return map;
	}

	@Override
	public PageVO getPlatformSelectedDoctors(String groupId, String consultationPackId ,String keyWord, Integer pageIndex, Integer pageSize) {
		List<Integer> notInDoctorIds = consultationPackDao.getNotInCurrentPackDoctorIds(groupId, consultationPackId);
		return userManagerImpl.getPlatformSelectedDoctors(notInDoctorIds,keyWord,pageIndex,pageSize);
	}

	@Override
	public void syncData() throws HttpApiException {
		Set<Integer> conDoctorIds = consultationPackDao.getAllConsultationDoctorIds();
		Set<Integer> packDoctorIds = packService.getAllConsultationDoctorIds();
		
		Set<Integer> needAddTemp = new HashSet<Integer>();
			needAddTemp.addAll(conDoctorIds);
			needAddTemp.addAll(packDoctorIds);
	    Set<Integer> needRemoveTemp = new HashSet<Integer>();
			needRemoveTemp.addAll(conDoctorIds);
			needRemoveTemp.addAll(packDoctorIds);
		
		needAddTemp.removeAll(packDoctorIds);
		
		needRemoveTemp.removeAll(conDoctorIds);
		
		if(!Util.isNullOrEmpty(needAddTemp)){
			for (Integer doctorId : needAddTemp) {
				addDoctorPack(doctorId, null);
			}
		}
		
		if(!Util.isNullOrEmpty(needRemoveTemp)){
			for (Integer doctorId : needRemoveTemp) {
				deleteDoctorPack(doctorId);
			}
		}
	}
}
