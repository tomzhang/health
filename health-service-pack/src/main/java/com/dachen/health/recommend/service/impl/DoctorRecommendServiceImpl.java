package com.dachen.health.recommend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.commons.vo.User;
import com.dachen.health.document.dao.IDocumentDao;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupUserDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.stat.entity.vo.DoctorStatVO;
import com.dachen.health.pack.stat.service.IDoctorStatService;
import com.dachen.health.recommand.dao.IDiseaseLaberDao;
import com.dachen.health.recommend.constant.DoctorRecommendEnum;
import com.dachen.health.recommend.dao.IDoctorRecommendDao;
import com.dachen.health.recommend.entity.param.DoctorRecommendParam;
import com.dachen.health.recommend.entity.po.DoctorRecommend;
import com.dachen.health.recommend.entity.vo.DoctorRecommendVO;
import com.dachen.health.recommend.service.IDoctorRecommendService;
import com.dachen.util.BeanUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class DoctorRecommendServiceImpl implements IDoctorRecommendService{
	
	@Autowired
	private IDoctorRecommendDao doctorRecommendDao;
	
	@Autowired
	private IDocumentDao documentDao;
	
	@Autowired
	private UserRepository UserRepository;
	
	@Autowired
    private IGroupDoctorService gdocService;
	
	@Autowired
	private PackMapper packMapper;
	
	@Autowired
	private IPackService packService;
	
	@Autowired
	private FriendsManager friendsManager;
	
	@Autowired
	private IGroupUserDao groupUserDao;
	
	@Autowired
	private IDoctorStatService doctorService;
	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	
	@Autowired
	private IDiseaseLaberDao laberDao;
	
	@Override
	public DoctorRecommendVO createDoctorRecommend(DoctorRecommendParam param) {
		if(StringUtil.isEmpty(param.getGroupId()) || param.getDoctorId() == null ){
			throw new ServiceException("集团Id或者医生为空");
		}
		
		String cgId = doctorRecommendDao.getGroupDoctorId(param.getGroupId(), param.getDoctorId()+"");
		if(StringUtil.isEmpty(cgId)){
			throw new ServiceException("传入医生ID不正确");
		}
		
		// 先根据groupId和doctorId来判断集团是否推荐过该医生
		DoctorRecommend doctorRecommend = doctorRecommendDao.getByGroupAndGroupDocId(param.getGroupId(), cgId);
		if (doctorRecommend != null) {
			throw new ServiceException("已经推荐过当前医生");
		}

		Integer weight = doctorRecommendDao.getWeightByGroupId(param.getGroupId(), true) + 1;
		if(StringUtil.isEmpty(param.getIsRecommend())){
			param.setIsRecommend(DoctorRecommendEnum.IsRecommendStatus.recommend.getIndex() +"");
		}
		param.setGroupDocId(cgId);
		param.setWeight(weight);
		param.setIsShow(DoctorRecommendEnum.ShowStatus.hide.getIndex()+"");
		return doctorRecommendDao.addDoctorRecommend(param);
	}
	

	@Override
	public void createRecommendDoctorForPlatform(DoctorRecommendParam param) {
		if(StringUtil.isEmpty(param.getGroupId()) || param.getDoctorIds().length < 1 ){
			throw new ServiceException("集团Id或者医生为空");
		}
		
		//查找所有已存在医生ID
		List<Integer> ids = Lists.newArrayList();
		List<DoctorRecommendVO> allDoctor = doctorRecommendDao.getAllPlatformDoctors();
		for(DoctorRecommendVO doctor : allDoctor) {
			ids.add(doctor.getDoctorId());
		}
		
		for (int i = 0; i < param.getDoctorIds().length; i++) {
			if (ids.contains(param.getDoctorIds()[i])) {
				throw new ServiceException("医生已存在！");
			}
		}
		
		for (int i = 0; i < param.getDoctorIds().length; i++) {
			Integer doctorId = Integer.valueOf(param.getDoctorIds()[i]);
		
			param.setDoctorId(doctorId);
			
			Integer weight = doctorRecommendDao.getWeightByGroupId(param.getGroupId(), true) + 1;
			if(StringUtil.isEmpty(param.getIsRecommend())){
				param.setIsRecommend(DoctorRecommendEnum.IsRecommendStatus.recommend.getIndex() +"");
			}
			param.setWeight(weight);
			param.setIsShow(DoctorRecommendEnum.ShowStatus.hide.getIndex()+"");
			doctorRecommendDao.addDoctorRecommend(param);
		}
	}

	@Override
	public Map<String, Object> delDoctorRecommend(String id) {
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		DoctorRecommendVO vo = doctorRecommendDao.getDoctorRecommendById(id);
		if(vo == null){
			map.put("result", false);
			map.put("msg", "未找到ID对应的对象");
			return map;
		}
		boolean result = doctorRecommendDao.delDoctorRecommendById(id);
		map.put("result", result); 
		if(result){
			//称除对应的文档
			documentDao.delDocumentByGDID(vo.getId());
			map.put("msg","删除成功"); 
		}else{
			map.put("msg","未知原因移除失败"); 
		}
		return map;
	}

	@Override
	public PageVO getRecommendDoctorList(DoctorRecommendParam param) {
		if(StringUtil.isEmpty(param.getGroupId()) || param.getIsApp() == null){
			throw new ServiceException("groupId及isApp 不能为空");
		}
		
		//平台推荐医生
		if ("platform".equals(param.getGroupId())) {
			return getRecommendDoctorListForPlatform(param);
		}
		
		//根据groupId 去c_group_doctor表里攻取对应的ID列表，以及医生ID列表
		Map<Integer, String> map = doctorRecommendDao.getGroupDoctorInfosByGroupId(param.getGroupId());
		List<String> sList = new ArrayList<String>();
		for(Integer key : map.keySet()){
			sList.add(map.get(key));
		}
		//根据ID列表去recommentDoctor表里获取对应相关Query
		Query<DoctorRecommendVO> query = doctorRecommendDao.getDoctorRecommendQuery(sList,param.getIsApp());
		List<DoctorRecommendVO> drList = query.offset(param.getStart()).limit(param.getPageSize()).order("isRecommend,-weight,createTime").asList();
		List<Integer> doctorIds = new ArrayList<Integer>();
		for(DoctorRecommendVO vo : drList){
			doctorIds.add(getDocIdByDoRecommendId(map,vo.getGroupDocId()));
		}
		List<User> uList = UserRepository.getDoctorQuery(doctorIds,null,null).asList();
		if(!param.getIsApp()){
			return new PageVO(convert(drList, uList, map), query.countAll(), param.getPageIndex(), param.getPageSize());
		}
		
		List<DoctorStatVO> statList = doctorService.convert(uList);
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}
		//集团所有者
		GroupUser groupUser = groupUserDao.findGroupRootAdmin(param.getGroupId());
		Integer groupOwnerId = null;
		if (groupUser != null) {
			groupOwnerId = groupUser.getDoctorId();
		}
		setPrice(myDoctorIds, statList, doctorService.getOnlineDoctors(), groupOwnerId);
		return new PageVO(sort(drList,statList,map), query.countAll(), param.getPageIndex(), param.getPageSize());
	}
	
	public PageVO getRecommendDoctorListForPlatform(DoctorRecommendParam param) {
		
		if (param.getSource() == null || !param.getSource().equals("web")) {
			return getDoctorListForApp(param);
		}
		
		List<Integer> doctorIds = Lists.newArrayList();
		Map<Integer, String> map = Maps.newHashMap();
		List<String> documentIds = Lists.newArrayList();
		
		Query<DoctorRecommendVO> query = doctorRecommendDao.getDoctorRecommendInPlatform().order("isRecommend,-weight,createTime");
		List<DoctorRecommendVO> doctorRecommendVOs = query.offset(param.getStart()).limit(param.getPageSize()).asList();
		
		if (doctorRecommendVOs == null || doctorRecommendVOs.size() <= 0) {
			return new PageVO(null,new Long(0),param.getPageIndex(),param.getPageSize());
		}
		
		for(DoctorRecommendVO vo : doctorRecommendVOs){
			map.put(vo.getDoctorId(),vo.getDoctorId().toString());
			documentIds.add(vo.getId());
			doctorIds.add(vo.getDoctorId());
		}
		
		//获取文档Url
		List<Document> documents = documentDao.getDocumentsByRecommendIds(documentIds);
		Map<String, String> documentMap = Maps.newHashMap();
		for(Document document : documents) {
			if (document.getIsShow() == 1) {
				documentMap.put(document.getRecommendDoctId(), document.getUrl());
			}
		}
		
		Query<User> uQuery = UserRepository.findUserQuery(doctorIds, null);
		uQuery.filter("userType", UserType.doctor.getIndex());
		uQuery.filter("status", UserStatus.normal.getIndex());
		
		List<User> users = uQuery.asList();
		
		//组装数据
		List<DoctorRecommendVO> result = convert(doctorRecommendVOs, users, map);
		
		Map<Integer, List<String>> nameMaps = groupDoctorDao.getGroupNameByDoctorIds(doctorIds);
		
		for(DoctorRecommendVO vo : result) {
			vo.setGroups(nameMaps.get(vo.getDoctorId()));
			vo.setSelect(true);
		}
		
		return new PageVO(result, query.countAll(), param.getPageIndex(), param.getPageSize());
		
	}
	
	public PageVO getDoctorListForApp(DoctorRecommendParam param) {
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		
		List<Integer> doctorIds = Lists.newArrayList();
		Map<Integer, String> map = Maps.newHashMap();
		List<String> documentIds = Lists.newArrayList();
		Map<Integer, DoctorRecommendVO> voMap = Maps.newHashMap();
		
		Query<DoctorRecommendVO> query = doctorRecommendDao.getDoctorRecommendInPlatform().filter("isRecommend", "1").order("-weight,createTime");
		List<DoctorRecommendVO> doctorRecommendVOs = query.asList();
		
		if (doctorRecommendVOs == null || doctorRecommendVOs.size() <= 0) {
			return page;
		}
		
		for(DoctorRecommendVO vo : doctorRecommendVOs){
			map.put(vo.getDoctorId(),vo.getDoctorId().toString());
			documentIds.add(vo.getId());
			doctorIds.add(vo.getDoctorId());
		}
		
		//获取文档Url
		List<Document> documents = documentDao.getDocumentsByRecommendIds(documentIds);
		Map<String, String> documentMap = Maps.newHashMap();
		for(Document document : documents) {
			if (document.getIsShow() == 1) {
				documentMap.put(document.getRecommendDoctId(), document.getUrl());
			}
		}
		
		Query<User> uQuery = UserRepository.findUserQuery(doctorIds, null);
		uQuery.filter("userType", UserType.doctor.getIndex());
		uQuery.filter("status", UserStatus.normal.getIndex());
		
		List<User> users = uQuery.asList();
		
		//组装数据
		List<DoctorRecommendVO> vos = convert(doctorRecommendVOs, users, map);
		
		Map<Integer, List<String>> nameMaps = groupDoctorDao.getGroupNameByDoctorIds(doctorIds);
		
		for(DoctorRecommendVO vo : vos) {
			vo.setGroups(nameMaps.get(vo.getDoctorId()));
			vo.setSelect(true);
		}
		
		for(DoctorRecommendVO vo : vos) {
			DoctorRecommendVO doctor = new DoctorRecommendVO();
			doctor.setDoctorId(vo.getDoctorId());
			doctor.setName(vo.getName());
			doctor.setHeadPicFileName(vo.getHeadPicFileName());
			doctor.setDepartments(vo.getDepartments());
			doctor.setSelect(true);
			doctor.setDocumentUrl(documentMap.get(vo.getId()));
			doctor.setWeight(vo.getWeight());
			
			voMap.put(doctor.getDoctorId(), doctor);
			
		}
		
		//根据用户关注的疾病列表进行重新排序
		Integer userId = ReqUtil.instance.getUserId();
		List<UserDiseaseLaber> labers = laberDao.findByUserId(userId);
		
		if (!CollectionUtils.isEmpty(labers)) {
			for(User user : users) {
				if (null != user.getDoctor()) {
					List<String> expertises = user.getDoctor().getExpertise();
					if (!CollectionUtils.isEmpty(expertises)) {
						for(String expertise : expertises) {
							for(UserDiseaseLaber laber : labers) {
								if (expertise.equals(laber.getParentId()) || expertise.equals(laber.getDiseaseId())) {
									DoctorRecommendVO doctor = voMap.get(user.getUserId());
									if (null != doctor && doctor.getWeight() != null && laber.getWeight() != null) {
										//如果医生的擅长跟患者的关注疾病匹配，大幅加大医生的权重
										doctor.setWeight(doctor.getWeight() + laber.getWeight() * 100000);
									}
								}
							}
						}
					}
				}
			}
		}
		
		List<DoctorRecommendVO> result = Lists.newArrayList();
		
		for(Map.Entry<Integer, DoctorRecommendVO> entry : voMap.entrySet()) {
			result.add(entry.getValue());
		}
		
		if (CollectionUtils.isEmpty(result)) {
			return page;
		}
		
		//根据权重排序
		
		Collections.sort(result, new SortByMyDoctor());
/*		Collections.sort(result, new Comparator<DoctorRecommendVO>() {
			
			List<Integer> myDoctorIds = new ArrayList<Integer>();
			
			@Override
			public int compare(DoctorRecommendVO type1, DoctorRecommendVO type2) {
				return type2.getWeight().compareTo(type1.getWeight());
			}
		});*/
		
		//内存分页
		if (result.size() < page.getStart()) {
			result = Lists.newArrayList();
		} else {
			if ((page.getPageIndex() + 1) * page.getPageSize() < result.size()) {
				result = result.subList(page.getStart(), (page.getPageIndex() + 1) * page.getPageSize());
			} else {
				result = result.subList(page.getStart(), result.size());
			}
		}
		
		page.setPageData(result);
		page.setTotal(query.countAll());
		return page;
	}
	
	public class SortByMyDoctor implements Comparator<DoctorRecommendVO> {

		private List<Integer> myDoctorIds = new ArrayList<Integer>();
		public SortByMyDoctor() {
			Integer currUserId = ReqUtil.instance.getUserId();
			for (DoctorPatient dp : friendsManager.getFriends(currUserId, DoctorPatient.class)) {
				myDoctorIds.add(dp.getToUserId());
			}
		}
		
		@Override
		public int compare(DoctorRecommendVO o1, DoctorRecommendVO o2) {
			if (myDoctorIds.contains(o1.getDoctorId()) && !myDoctorIds.contains(o2.getDoctorId())) {
				return -1;
			} else if (!myDoctorIds.contains(o1.getDoctorId()) && myDoctorIds.contains(o2.getDoctorId())) {
				return 1;
			} 
			return o2.getWeight().compareTo(o1.getWeight());
		}
		
	}
	
	@Override
	public Map<String, Object> setRecommend(DoctorRecommendParam param) {
		if(StringUtil.isEmpty(param.getId()) || StringUtil.isEmpty(param.getIsRecommend())){
			throw new ServiceException("id及isRecommend不能为空");
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		DoctorRecommendVO vo = doctorRecommendDao.getDoctorRecommendById(param.getId());
		if(vo == null){
			map.put("result", false);
			map.put("msg", "未找到ID对应的对象");
			return map;
		}
		
/*		if(param.getIsRecommend().equals(String.valueOf(DoctorRecommendEnum.IsRecommendStatus.recommend.getIndex()))) {
			String groupId = vo.getGroupId();
			//判断推荐的人数
			List<DoctorRecommendVO> list = doctorRecommendDao.getWeightList(groupId);
			if (list != null && list.size() >= DoctorRecommend.LIMIT_RECOMMEND) {
				throw new ServiceException("推荐医生数量超过限制");
			}
		}*/
		
		if(!param.getIsRecommend().equals(String.valueOf(DoctorRecommendEnum.IsRecommendStatus.recommend.getIndex()))){
			param.setWeight(0);
		}else{
			Integer weight = doctorRecommendDao.getWeightByGroupId(vo.getGroupId(), true) + 1;
			param.setWeight(weight);
		}
		boolean result = doctorRecommendDao.updateDoctorRecommend(param);
		map.put("result", result);
		return map;
	}
	
	@Override
	public Map<String, Object> upWeight(String id) {
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id 不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		DoctorRecommendVO vo = doctorRecommendDao.getDoctorRecommendById(id);
		if(vo == null){
			map.put("result", false);
			map.put("msg", "没有找到对应的推荐名医");
			return map;
		}
		List<DoctorRecommendVO> list = doctorRecommendDao.getWeightList(vo.getGroupId());
		for(int i=0;i<list.size();i++){
			
			if(list.get(i).getId().equals(vo.getId())){
				if(i == 0){
					map.put("result", false);
					map.put("msg", "已经在第一个，无法上移");
					return map;
				}else{
					DoctorRecommendVO v = list.get(i-1);
					int w = v.getWeight();
					v.setWeight(vo.getWeight());
					vo.setWeight(w);
					doctorRecommendDao.updateDoctorRecommend(v);
					doctorRecommendDao.updateDoctorRecommend(vo);
					map.put("result", true);
					map.put("msg", "上移成功");
					return map;
				}
			}
		}
		return map;
	}
	
	
	
	@Override
	public DocumentVO getRecommendDoc(String recommendId) {
		if(StringUtil.isEmpty(recommendId)){
			throw new ServiceException("recommendId 不能为空");
		}
		Document doc = documentDao.getDocumentByRecommendId(recommendId);
		if(doc == null ){
			return null;
		}
		return BeanUtil.copy(doc, DocumentVO.class);
	}

	private Integer getDocIdByDoRecommendId(Map<Integer,String> map ,String recommendId){
		for(Entry<Integer,String> entry:map.entrySet()){
			if(entry.getValue().equals(recommendId)){
				return entry.getKey();
			}
		}
		return null;
	}

	private List<DoctorRecommendVO> convert(List<DoctorRecommendVO> dList,List<User> uList,Map<Integer,String> map){
		List<DoctorRecommendVO> list = new ArrayList<DoctorRecommendVO>();
		for(DoctorRecommendVO dc:dList){
			Integer docId;
			if (StringUtils.isNotEmpty(dc.getGroupDocId())) {
				docId = getDocIdByDoRecommendId(map, dc.getGroupDocId());
			}else {
				docId = dc.getDoctorId();
			}
			
			if(docId == null){
				continue;
			}
			for(User u : uList){
				if(u.getUserId().intValue() == docId.intValue()){
					dc.setHeadPicFileName(u.getHeadPicFileName());
					dc.setName(u.getName());
					dc.setTitle(u.getDoctor().getTitle());
					dc.setDepartments(u.getDoctor().getDepartments());
					dc.setDoctorId(u.getUserId());
					Document doc = documentDao.getDocumentByRecommendId(dc.getId());
					if(doc !=null){
					}
					list.add(dc);
				}
			}
		}
		return list;
	}
	
	public List<DoctorStatVO> sort(List<DoctorRecommendVO> dList, List<DoctorStatVO> statList,Map<Integer,String> map){
		List<DoctorStatVO> list = new ArrayList<DoctorStatVO>();
		for(DoctorRecommendVO dr :dList){
			Integer docId = getDocIdByDoRecommendId(map, dr.getGroupDocId());
			if(docId == null){
				continue;
			}
			for(DoctorStatVO vo :statList){
				vo.setRecommendId(dr.getId());
				if(vo.getDoctorId().intValue()  == docId.intValue()){
					vo.setIsShow(dr.getIsShow());
					Document doc = documentDao.getDocumentByRecommendId(dr.getId());
					if(doc !=null){
						vo.setUrl(doc.getUrl());
						vo.setIsShow(doc.getIsShow()+"");
					}else {
						vo.setIsShow("2");
					}
					list.add(vo);
				}
			}
		}
		return list;
	}
	private void setPrice(List<Integer> myDoctorIds,List<DoctorStatVO> statList,List<Integer> OnlineList,Integer groupOwnerId){
		for (DoctorStatVO vo : statList) {
			Pack pack = new Pack();
			pack.setDoctorId(vo.getDoctorId());

			List<Pack> packList = packMapper.query(pack);
			setPackStatusAndPrice(packList, vo);
			
			if (myDoctorIds.contains(vo.getDoctorId())) {
				vo.setMyDoctor("1");
			} else {
				vo.setMyDoctor("0");
			}
			
			if (vo.getDoctorId()!=null && groupOwnerId!=null && vo.getDoctorId().equals(groupOwnerId)) {
				vo.setGroupOwner(true);
			} else {
				vo.setGroupOwner(false);
			}

			// 在线门诊
			if (OnlineList.contains(vo.getDoctorId())) {
				vo.setClinicOpen("1");
				OutpatientVO op = gdocService.getOutpatientInfo(vo.getDoctorId());
				if (op.getPrice() < Long.valueOf(vo.getPrice())) {
					vo.setPrice(String.valueOf(op.getPrice()));
				}
			}
			if (Long.valueOf(vo.getPrice()) == Long.MAX_VALUE) {
				vo.setPrice("0");
			}
		}
	}
	private void setPackStatusAndPrice(List<Pack> packList, DoctorStatVO vo) {
		Long price = Long.MAX_VALUE;
		String isOpenCare = "0";
		for (Pack pack : packList) {
			String isOpen = "0";

			switch (pack.getPackType().intValue()) {
			case 1:// 图文
				if (pack.getStatus() == PackEnum.PackStatus.open.getIndex()) {
					isOpen = "1";
					if (pack.getPrice() < price) {
						price = pack.getPrice();
					}
				}
				vo.setTextOpen(isOpen);
				break;
			case 2:// 电话
				if (pack.getStatus() == PackEnum.PackStatus.open.getIndex()) {
					isOpen = "1";
					if (pack.getPrice() < price) {
						price = pack.getPrice();
					}
				}
				vo.setPhoneOpen(isOpen);
				break;
			case 3:// 关怀计划
				if (pack.getStatus() == PackEnum.PackStatus.open.getIndex()) {
					//排除价格为0的（即随访）和非启用的，与PackServiceImpl.queryPack保持一致
					if (!packService.isValidCareTemplateForPatient(pack)) {
						continue;
					}
					isOpenCare = "1";
					if (pack.getPrice() < price) {
						price = pack.getPrice();
					}
				}
				vo.setCareOpen(isOpenCare);
				break;
			case 8:// 会诊
				if (pack.getStatus() == PackEnum.PackStatus.open.getIndex()) {
					isOpen = "1";
					if (pack.getPrice() < price) {
						price = pack.getPrice();
					}
				}
				vo.setConsultationOpen(isOpen);
				break;
			case 9:
				if(pack.getStatus() == PackEnum.PackStatus.open.getIndex()){
					isOpen = "1";
					if (pack.getPrice() < price) {
						price = pack.getPrice();
					}
				}
				vo.setAppointmentOpen(isOpen);
				break;
			default:
				break;
			}
		}
		vo.setPrice(String.valueOf(price));
	}

	@Override
	public PageVO getDoctorsByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
		PageVO pageVO = new PageVO();
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		List<DoctorRecommendVO> result = Lists.newArrayList();
	
		List<Integer> doctorIds = Lists.newLinkedList();
		
		//获取已在推荐列表里的医生ID
		List<Integer> existIds = Lists.newArrayList();
		List<DoctorRecommendVO> existDoctor = doctorRecommendDao.getAllPlatformDoctors();
		for(DoctorRecommendVO vo : existDoctor) {
			existIds.add(vo.getDoctorId());
		}
		
		//精确查找医生
		List<User> completeMatch = doctorRecommendDao.getUserByName(keyword);
		List<Integer> completeMatchIds = Lists.newArrayList(); 
		if (completeMatch!=null && completeMatch.size()> 0) {
			for(User user : completeMatch) {
				completeMatchIds.add(user.getUserId());
			}
		}
		
		//根据关键字模糊查找医生
		doctorIds.addAll(doctorRecommendDao.searchDoctorIdsByName(keyword));
		
		//根据关键字模糊查找集团下医生
		List<Group> groups = groupDao.searchGroupsByName(keyword, null, null);
		if (groups != null && groups.size() > 0) {
			for(Group group : groups) {
				List<GroupDoctor> groupDoctors = groupDoctorDao.findDoctorsByGroupId(group.getId());
				for(GroupDoctor gd : groupDoctors) {
					if (!doctorIds.contains(gd.getDoctorId())) {
						doctorIds.add(gd.getDoctorId());
					}
				}
			}
		}
		
		//对查找出来的结果进行排序，完全匹配的排前列
		List<Integer> resultIds = Lists.newArrayList();
		if (completeMatchIds != null && completeMatchIds.size() > 0) {
			resultIds.addAll(0, completeMatchIds);
			if (doctorIds != null && doctorIds.size() > 0) {
				for(Integer id : doctorIds){
					if (!completeMatchIds.contains(id)) {
						resultIds.add(id);
					}
				}
			}
		}else {
			resultIds.addAll(doctorIds);
		}
		
		Query<User> query = UserRepository.findUserQuery(resultIds, null);
		query.filter("userType", UserType.doctor.getIndex());
		query.filter("status", UserStatus.normal.getIndex());
		long count = query.countAll();
/*		query.offset(pageIndex*pageSize);
		query.limit(pageSize);*/
		List<User> queryUsers = query.asList();
		
		//对查询出来的对象集进行排序,再分页
		List<User> resultUsers = Lists.newArrayList();
		if (queryUsers!= null && queryUsers.size()> 0) {
			for(Integer id : resultIds) {
				for(User user : queryUsers) {
					if (user.getUserId().equals(id)) {
						resultUsers.add(user);
					} 
				}
			}
		}
		
		List<User> users = Lists.newArrayList();
		for(int i = (pageIndex*pageSize); i<((pageIndex*pageSize+pageSize)>count?count:(pageIndex*pageSize+pageSize)); i++){
			users.add(resultUsers.get(i));
		}
		
		Map<Integer, List<String>> nameMaps = groupDoctorDao.getGroupNameByDoctorIds(resultIds);
		
		if (users != null && users.size() > 0) {
			for(User user : users) {

				DoctorRecommendVO vo = new DoctorRecommendVO();
				if (user.getHeadPicFileName()!=null) {
					vo.setHeadPicFileName(user.getHeadPicFileName());
				}
				if (user.getDoctor()!=null && user.getDoctor().getDepartments()!=null) {
					vo.setDepartments(user.getDoctor().getDepartments());
				}
				if (user.getDoctor()!=null && user.getDoctor().getTitle()!= null) {
					vo.setTitle(user.getDoctor().getTitle());
				}
				vo.setName(user.getName());
				vo.setGroups(nameMaps.get(user.getUserId()));
				vo.setDoctorId(user.getUserId());
				if (existIds.contains(user.getUserId())) {
					vo.setSelect(true);
				}
				result.add(vo);
			}
		}

		pageVO.setPageData(result);
		pageVO.setTotal(count);
		
		return pageVO;
	}


	@Override
	public List<Integer> getDoctorIdsByGroup(String groupId) {
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("集团id为空");
		}
		return doctorRecommendDao.getDoctorIdsByGroupId(groupId);
	}

}