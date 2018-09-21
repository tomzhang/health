package com.dachen.health.pack.stat.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.commons.constants.GroupEnum.GroupCertStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.GroupEnum.OnLineState;
import com.dachen.health.commons.constants.HospitalLevelEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackStatus;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.group.dao.*;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.vo.OfflineVO;
import com.dachen.health.pack.evaluate.service.IEvaluationService;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.po.PackParam2;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.stat.entity.vo.DoctorStatVO;
import com.dachen.health.pack.stat.service.IDoctorStatService;
import com.dachen.health.recommend.dao.IDoctorRecommendDao;
import com.dachen.health.user.dao.IRelationDao;
import com.dachen.util.MapDistance;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.print.Doc;
import java.util.*;

@Service
public class DoctorStatServiceImpl implements IDoctorStatService {
	private static Logger logger = LoggerFactory.getLogger(DoctorStatServiceImpl.class);

	@Autowired
	private IGroupDao groupDao;

	@Autowired
    private IGroupDoctorDao gdocDao;

	@Autowired
    private IGroupDoctorService gdocService;

	@Autowired
    private IPlatformDoctorDao pdocDao;

	@Autowired
	private IBaseDataDao baseDataDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PackMapper packMapper;

	@Autowired
	private IPackService packService;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private IGroupSearchDao groupSearchDao;

	@Autowired
	private FriendsManager friendsManager;

	@Autowired
	private IEvaluationService evaluationService;

	@Autowired
    private IRelationDao relationDao;

	@Autowired
	private IOfflineDao offlineDao;

	@Autowired
	private IGroupUserDao groupUserDao;

	@Autowired
	private IDoctorRecommendDao doctorRecommendDao;
	@Autowired
	private IGroupService groupService;


	private static Map<String, String> lngMap = Maps.newHashMap();
	private static Map<String, String> latMap = Maps.newHashMap();

	@Override
	public PageVO findOnDutyToday(GroupSearchParam param) {
		//通过地区code获取此地区所有的医院
		List<HospitalPO> hpos = baseDataDao.getHospitals(param.getAreaCode(), false);
		if (param.getAreaCode() != null && hpos.isEmpty()) {
			return new PageVO();
		}
		//取得所有医院的Id
		List<String> hospitalIds = new ArrayList<String>();
		for (HospitalPO hpo : hpos) {
			hospitalIds.add(hpo.getId());
		}

		//获取集团所有的在线医生Id
		List<Integer> doctorIds = new ArrayList<Integer>();
		if(StringUtil.isNotBlank(param.getDocGroupId())){
			doctorIds.addAll(getOnlineDoctors(param.getDocGroupId()));
		}else{
			doctorIds.addAll(getOnlineDoctors());
		}

		if (doctorIds.isEmpty()) {
			return new PageVO();
		}
		//通过集团医生Id、医院Id、科室名获取结果Query
		Query<User> q = userRepository.getDoctorQuery(doctorIds, hospitalIds, param.getSpecialistId());

		List<DoctorStatVO> vos = getDoctorStats(param, q);

		for (DoctorStatVO vo : vos) {
			OutpatientVO outpatient = gdocService.getOutpatientInfo(vo.getDoctorId());
			vo.setPrice(outpatient.getIsFree() ? "免费" : (outpatient.getPrice()/100)+"元");
		}
		return new PageVO(vos, q.countAll(), param.getPageIndex(), param.getPageSize());
	}



	@Override
	public PageVO findAllDoctor(GroupSearchParam param) {
		Query<User> q = userRepository.getDoctorQuery(null, null, null);
		List<DoctorStatVO> vos = getDoctorStats(param, q);

		for (DoctorStatVO vo : vos) {
			Map<String, Integer> params = new HashMap<String, Integer>();
			params.put("doctorId", vo.getDoctorId());
			params.put("packType", PackType.phone.getIndex());
			Pack pack = packMapper.queryByDoctorIdAndType(params);
			vo.setPrice("免费");
			if (pack != null && pack.getPrice() > 0) {
				vo.setPrice((pack.getPrice()/100) + "元/" + (pack.getTimeLimit() == null ? "15" : pack.getTimeLimit()) +"分钟");
			}
		}
		return new PageVO(vos, q.countAll(), param.getPageIndex(), param.getPageSize());
	}

	@Override
	public PageVO findOrderDoctor(GroupSearchParam param) {
		//通过地区code获取此地区所有的三甲医院
		List<HospitalPO> hpos = baseDataDao.getHospitals(param.getAreaCode(), true);
		if (param.getAreaCode() != null && hpos.isEmpty()) {
			return new PageVO();
		}
		//取得所有医院的Id
		List<String> hospitalIds = new ArrayList<String>();
		for (HospitalPO hpo : hpos) {
			hospitalIds.add(hpo.getId());
		}

		//获取所有的开通电话咨询和图文咨询的医生Id
		//Set<Integer> set = new HashSet<>();
		List<Integer> doctorIds = Lists.newArrayList();

		doctorIds.addAll(getOpenDoctorsByType(PackType.phone.getIndex()));
		doctorIds.addAll(getOpenDoctorsByType(PackType.message.getIndex()));

		//set.addAll(phones);
		//List<Integer> messages = getOpenDoctorsByType(PackType.message.getIndex());
		//set.addAll(messages);

		//doctorIds.addAll(set);

		//删除上线的医生Id
		doctorIds.removeAll(getOnlineDoctors());

		if (doctorIds.isEmpty()) {
			return new PageVO();
		}
		//通过集团医生Id、医院Id、科室名获取结果Query
		Query<User> q = userRepository.getDoctorQuery(doctorIds, hospitalIds, param.getSpecialistId());

		List<DoctorStatVO> vos = getDoctorStats(param, q);

		for (DoctorStatVO vo : vos) {
			Map<String, Integer> params = new HashMap<String, Integer>();
			params.put("doctorId", vo.getDoctorId());
			params.put("packType", PackType.phone.getIndex());
			Pack pack = packMapper.queryByDoctorIdAndType(params);
			vo.setPrice("免费");
			if (pack != null && pack.getPrice() > 0) {
				vo.setPrice((pack.getPrice()/100) + "元/" + (pack.getTimeLimit() == null ? "15" : pack.getTimeLimit()) +"分钟");
			}
		}

		setMinPrice(vos);

		return new PageVO(vos, q.countAll(), param.getPageIndex(), param.getPageSize());
	}

	public PageVO findOnlineConsultDoctor(GroupSearchParam param) {
		List<Integer> doctorIds = getMessageOrPhonePackDoctor(param.getDocGroupId());

		if (doctorIds.isEmpty()) {
			return new PageVO();
		}
		//通过集团医生Id、医院Id、科室名获取结果Query
		Query<User> q = userRepository.getDoctorQuery(doctorIds, null, param.getSpecialistId(), param.getAreaCode());
		List<DoctorStatVO> vos = getDoctorStats(param, q);
		for (DoctorStatVO vo : vos) {
			Pack pack = new Pack();
			pack.setDoctorId(vo.getDoctorId());
			pack.setStatus(PackStatus.open.getIndex());

			List<Pack> packList = packMapper.query(pack);
			setPackStatusAndPrice(packList, vo);
		}
		return new PageVO(vos, q.countAll(), param.getPageIndex(), param.getPageSize());
	}

	private List<Integer> getMessageOrPhonePackDoctor(String docGroupId) {
		List<Integer> doctorIds = new ArrayList<Integer>();
		doctorIds.addAll(getOpenDoctorsByType(PackType.message.getIndex()));
		doctorIds.addAll(getOpenDoctorsByType(PackType.phone.getIndex()));

		if (StringUtil.isNotBlank(docGroupId)) {//过滤博德嘉联集团的医生
			List<GroupDoctor> gdocs = gdocDao.findGroupDoctorsByGroupId(docGroupId);
			List<Integer> bdjlDoctors = new ArrayList<Integer>();
			for (GroupDoctor gdoc : gdocs) {
				bdjlDoctors.add(gdoc.getDoctorId());
			}
			for (Iterator<Integer> it = doctorIds.iterator(); it.hasNext();) {
				if (!bdjlDoctors.contains(it.next())) {
					it.remove();
				}
			}
		}
		return doctorIds;
	}

	@Override
	public PageVO findAppointmentDoctor(GroupSearchParam param) {

		List<Integer> doctorIds = null;

		if (StringUtils.isBlank(param.getGroupId())) {
			String groupId = groupDao.getAppointmentGroupId();
			if (StringUtils.isBlank(groupId)) {
				return new PageVO();
			}
			param.setGroupId(groupId);
		}

		//获取集团下面所有的医院
		List<HospitalInfo> hospitals=new ArrayList<>();
		hospitals=groupDao.getById(param.getGroupId()).getConfig().getHospitalInfo();
		for(HospitalInfo hos:hospitals){
			if(hos.getLat()!=null&&hos.getLng()!=null&&param.getLat()!=null&&param.getLng()!=null){
				String distance=MapDistance.getDistance(param.getLng(), param.getLat(), hos.getLng(), hos.getLat());
				hos.setDistance(distance);
			}
		}

		List<String> gIds = new ArrayList<String>();
		gIds.add(param.getGroupId());
		//获取集团所有医生
		doctorIds = gdocDao.findAllDoctorIdsInGroupIds(gIds);
		//获取开通了预约套餐的所有医生
		List<Integer> dIds = packMapper.getAppointmentDoctorIds(doctorIds);

		if (dIds == null || dIds.isEmpty()) {
			return new PageVO();
		}

		boolean isContains = false;
		if (StringUtil.isEmpty(param.getSpecialistId()) && param.getDoctorId() != null) {
			if (dIds.contains(param.getDoctorId())) {
				dIds.remove(param.getDoctorId());
				isContains = true;
			}
		}
		//通过集团医生Id、医院Id、科室名获取结果Query
		Query<User> q = userRepository.getDoctorQuery(dIds, null, param.getSpecialistId());


		List<DoctorStatVO> vos;



		//List<DoctorStatVO> vos = getDoctorStats(param, q);
		//List<DoctorStatVO> vos=q.asList();
		if(param.getSort() != null && param.getSort() == 1){
			 vos=convert(q.asList());
			 vos = distanceSort(vos);
			 if(param.getPageIndex()*param.getPageSize()>vos.size()){
				 //vos=null;
				 vos=new ArrayList<>();
			 }else {
				 if((param.getPageIndex()+1)*param.getPageSize()<vos.size()){
					vos=vos.subList(param.getPageIndex()*param.getPageSize(), (param.getPageIndex()+1)*param.getPageSize()) ;
				 }else{
					 vos.subList(param.getPageIndex()*param.getPageSize(), vos.size());
				 }


			 }

		}else{
			vos=getDoctorStats(param, q);

		}

		for (DoctorStatVO vo : vos) {

			setPriceAndOffLine(param,vo,hospitals);
		}



		List<DoctorPatient> list = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : list) {
			myDoctorIds.add(dp.getToUserId());
		}
		//推荐的名医放置第一个位置
		if (param.getDoctorId() != null && param.getDoctorId() != 0 && param.getPageIndex() == 0) {
			DoctorStatVO recommendDoctor = null;
			if (isContains) {
				List<DoctorStatVO> recommendDoctorList = convert(Arrays.asList(userRepository.getUser(param.getDoctorId())));
				if (recommendDoctorList != null && !recommendDoctorList.isEmpty()) {
					recommendDoctor = recommendDoctorList.get(0);
				}
			}
			if (recommendDoctor != null) {
				if(myDoctorIds.contains(recommendDoctor.getDoctorId())){
					recommendDoctor.setMyDoctor("1");
				}
//				List obj=offlineDao.getAllhospital(recommendDoctor.getDoctorId(), 0,param.getLat(),param.getLng(),hospitals);
//				recommendDoctor.setOffline(obj);
				setPriceAndOffLine(param,recommendDoctor,hospitals);
				vos.add(0, recommendDoctor);

			}
		}

		//vos=vos.subList(fromIndex, toIndex)


		return new PageVO(vos, q.countAll(), param.getPageIndex(), param.getPageSize());
	}

	private void setPriceAndOffLine(GroupSearchParam param,DoctorStatVO vo,List<HospitalInfo> hospitals){
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("doctorId", vo.getDoctorId());
		params.put("packType", PackType.appointment.getIndex());
		Pack pack = packMapper.queryByDoctorIdAndType(params);
		//判断是否获取到当前的位置
		if(param.getLat()==null||param.getLat()==null){
			throw new ServiceException("请获取用户当前的位置");
		}
		//加入会诊医院
		List obj=offlineDao.getAllhospital(vo.getDoctorId(), 0,param.getLat(),param.getLng(),hospitals);
		vo.setOffline(obj);

		if (pack != null && pack.getPrice() > 0) {
			vo.setPrice((pack.getPrice()/100) + "元");
		}
	}

	/**
	 * 排序要求：是按照每个医生第一条医院的距离排序。如果有的医生第一条医院没有坐标，则该医生排在最后
	 * @param list
	 * @return List<DoctorStatVO>
	 * @author 李明
	 */
	private List<DoctorStatVO> distanceSort(List<DoctorStatVO> list){

		 Collections.sort(list, new Comparator<DoctorStatVO>(){

	            public int compare(DoctorStatVO o1, DoctorStatVO o2) {
	            	if(o1.getOffline()==null||o1.getOffline().size()<1||((OfflineVO)o1.getOffline().get(0)).getDistance()==null){
	            		return 1;
	            	}
	            	if(o2.getOffline()==null||o2.getOffline().size()<1||((OfflineVO)o2.getOffline().get(0)).getDistance()==null){
	            		return -1;
	            	}

            		OfflineVO v1=(OfflineVO) o1.getOffline().get(0);
            		OfflineVO v2=(OfflineVO) o2.getOffline().get(0);

        			if(Double.parseDouble(v1.getDistance())>  Double.parseDouble(v2.getDistance())){
            			return 1;
            		}else if(Double.parseDouble(v1.getDistance())== Double.parseDouble(v2.getDistance())){
            			return 0;
            		}else{
            			return -1;
            		}


	            }
	        });


		return list;
	}


    /**
     *
     * @param param
     * @param q
     * @return
     */
	private List<DoctorStatVO> getDoctorStats(GroupSearchParam param, Query<User> q) {
		List<User> users = q.asList();

		Collections.sort(users, new SortByMyDoctor());

		List<DoctorStatVO> statList = convert(users);

        List<DoctorStatVO> resultList = Lists.newArrayList();
        if (statList != null) {
            //从list中的第几个元素开始取
            int start = param.getPageIndex() < 0 ? 0 : (param.getPageIndex() * param.getPageSize());
            //取到第几个元素
            int end = start + param.getPageSize() - 1;
            for (int i = start; i <= end; i++) {
                if (start > (statList.size()-1)) {
                    //当起始位大于all最大的一个元素，则什么都不做
                } else {
                    if (end > ((statList.size()-1))) {
                        //若要取得结束的元素位置，大于all的最大长度,则取值取到all的最大元素即可
                        if (i <= (statList.size()-1)) {
                            resultList.add(statList.get(i));
                        } else {
                            //当i大于all的最大长度时，则什么都不做
                        }
                    } else {
                        resultList.add(statList.get(i));
                    }
                }
            }
        }
        return resultList;
    }

	public class SortByMyDoctor implements Comparator<User> {

		private List<Integer> myDoctorIds = new ArrayList<Integer>();
		public SortByMyDoctor() {
			Integer currUserId = ReqUtil.instance.getUserId();
			List<DoctorPatient> doctorPatients = friendsManager.getFriends(currUserId, DoctorPatient.class);
			for (DoctorPatient dp : doctorPatients) {
				myDoctorIds.add(dp.getToUserId());
			}
		}
		@Override
		public int compare(User o1, User o2) {
			if (myDoctorIds.contains(o1.getUserId()) && !myDoctorIds.contains(o2.getUserId())) {
				//标志“我的医生”
				o1.setRemarks("MY_DOCTOR");
				return -1;
			} else if (!myDoctorIds.contains(o1.getUserId()) && myDoctorIds.contains(o2.getUserId())) {
				//标志“我的医生”
				o2.setRemarks("MY_DOCTOR");
				return 1;
			} else if (myDoctorIds.contains(o1.getUserId()) && myDoctorIds.contains(o2.getUserId())) {
				o1.setRemarks("MY_DOCTOR");
				o2.setRemarks("MY_DOCTOR");
				return 0;
			}
			return 0;
		}
	}


    /**
     *
     * @param users
     * @return
     */
	public List<DoctorStatVO> convert(List<User> users) {
		List<DoctorStatVO> list = new ArrayList<DoctorStatVO>();

		for (User user : users) {
			DoctorStatVO vo = new DoctorStatVO();
			vo.setDoctorId(user.getUserId());
			vo.setDoctorName(user.getName());
			List<GroupDoctor> gdocs = gdocDao.getByDoctorId(user.getUserId());
			boolean groupCert = false;
			for (GroupDoctor gdoc : gdocs) {
				if (GroupCertStatus.passed.getIndex().equals(groupDao.getById(gdoc.getGroupId()).getCertStatus())) {
					groupCert = true;
					break;
				}
			}
			vo.setGroupCert(groupCert ? "1" : "0");
			vo.setDoctorPath(user.getHeadPicFileName());
			vo.setDoctorTitle(user.getDoctor().getTitle());
			vo.setDoctorDept(user.getDoctor().getDepartments());
			vo.setHospital(user.getDoctor().getHospital());
			vo.setHospitalId(user.getDoctor().getHospitalId());
			HospitalVO hospital = baseDataDao.getHospital(vo.getHospitalId());
			if (hospital!= null && hospital.getLevel() != null && hospital.getLevel().equals(HospitalLevelEnum.Three3.getAlias())) {
				vo.setIs3A("1");
			} else {
				vo.setIs3A("0");
			}

			//经纬度
			if(hospital != null){
				vo.setHospital(hospital.getName());
				vo.setLng(hospital.getLng());
				vo.setLat(hospital.getLat());
			}

			vo.setDoctorCureNum(user.getDoctor().getCureNum());
			vo.setRole(user.getDoctor().getRole());
			String goodRate = evaluationService.getGoodRate(user.getUserId());
			vo.setGoodRate("暂无评价".equals(goodRate) ? "暂无" : goodRate);

			List<String> diseaseIds = user.getDoctor().getExpertise();
			if (diseaseIds != null && !diseaseIds.isEmpty()) {
				if(StringUtil.isEmpty(user.getDoctor().getSkill())) {
					//skill为空的话 只单单返回expertise
					vo.setDoctorSkill(groupSearchDao.getDisease(diseaseIds));
				} else {
					//expertise、skill都不为空则一起返回
					if(StringUtil.isNotEmpty(groupSearchDao.getDisease(diseaseIds))){
						vo.setDoctorSkill(groupSearchDao.getDisease(diseaseIds)+"、"+user.getDoctor().getSkill());
					} else {
						vo.setDoctorSkill(user.getDoctor().getSkill());
					}
				}
			} else {
				vo.setDoctorSkill(user.getDoctor().getSkill());
			}
			if (user.getRemarks() != null && "MY_DOCTOR".equals(user.getRemarks())) {
				vo.setMyDoctor("1");
			} else {
				vo.setMyDoctor("0");
			}
			list.add(vo);
		}
		return list;
	}

	/**
	 * 获取所有的下级地区code
	 * @param code
	 * @return
	 */
	public List<Integer> getAllAreas(Integer code) {
		List<Integer> codes = new ArrayList<Integer>();
		addSubArea(codes, code);
		return codes;
	}
	public void addSubArea(List<Integer> codes, Integer code) {
		if (code == null || code == 0)
			return;

		codes.add(code);
		List<AreaVO> areas = baseDataDao.getAreas(code);
		if (areas == null || areas.isEmpty()) {
			return;
		}
		for (AreaVO area : areas) {
			addSubArea(codes, area.getCode());
		}
	}

	/**
	 * 获取所有的下级科室
	 * @param deptId
	 * @return
	 */
	public List<String> getAllDepts(String deptId) {
		List<String> deptNames = new ArrayList<String>();
		addSubDept(deptNames, deptId);
		return deptNames;
	}
	public void addSubDept(List<String> deptNames, String deptId) {
		if (StringUtil.isEmpty(deptId))
			return;

		deptNames.add(deptId);
		List<DeptVO> depts = baseDataDao.getDepts(deptId, null);
		if (depts == null || depts.isEmpty()) {
			return;
		}
		for (DeptVO dept : depts) {
			addSubDept(deptNames, dept.getName());
		}
	}

	/**
	 * 查询开通了电话咨询/图文咨询的医生
	 */
	public List<Integer> getOpenDoctorsByType(Integer packType) {
		Pack pack = new Pack();
		pack.setStatus(PackStatus.open.getIndex());
		pack.setPackType(packType);
		List<Pack> packs = packMapper.query(pack);
		List<Integer> doctorIds = Lists.newArrayList();
		if (Objects.nonNull(packs) && packs.size() > 0) {
            for (Pack p : packs) {
                doctorIds.add(p.getDoctorId());
            }
        }
		return doctorIds;
	}

	/**
	 * 获取上线的集团医生
	 * @return
	 */
	public List<Integer> getOnlineDoctors() {
		return getOnlineDoctors(null);
	}
	public List<Integer> getOnlineDoctors(String groupId){
		List<Integer> doctorIds = new ArrayList<Integer>();
		GroupDoctorParam param = new GroupDoctorParam();
		param.setOnLineState(OnLineState.onLine.getIndex());
		param.setStatus(GroupDoctorStatus.正在使用.getIndex());
		if(StringUtil.isNotBlank(groupId)){
			param.setGroupId(groupId);
		}
		List<GroupDoctor> gdocs = gdocDao.getGroupDoctor(param);
		for (GroupDoctor gdoc : gdocs) {
			doctorIds.add(gdoc.getDoctorId());
		}

		PlatformDoctor param2 = new PlatformDoctor();
		param2.setOnLineState(OnLineState.onLine.getIndex());
		List<PlatformDoctor> pdocs = pdocDao.getPlatformDoctor(param2);
		for (PlatformDoctor pdoc : pdocs) {
			gdocs = gdocDao.getByDoctorId(pdoc.getDoctorId(), GroupType.group.getIndex());
			//需要排除屏蔽集团和未审核通过的集团
			if (gdocs == null || gdocs.isEmpty()) {//没有加入集团，才计入平台
				doctorIds.add(pdoc.getDoctorId());
			}else{
				boolean flag=true;
				for(GroupDoctor groupDoctor:gdocs){
					if(StringUtil.isNotEmpty(groupDoctor.getGroupId())){
						if(groupService.isNormalGroup(groupDoctor.getGroupId())){
							flag=false;
						}
					}
				}
				if(flag){
					doctorIds.add(pdoc.getDoctorId());
				}

			}




		}
		return doctorIds;
	}


	public List<GroupSearchVO> findDoctorByDisease(String diseaseId) {
        return groupSearchDao.findDoctor(diseaseId, null, getOpenDoctorsByType(PackType.phone.getIndex()));
    }

    public List<GroupSearchVO> findDoctorByDept(String deptId) {
        return groupSearchDao.findDoctor(null, deptId, getOpenDoctorsByType(PackType.phone.getIndex()));
    }

    @Override
	public Map<String, Object> getDoctorPatient(RelationType relationType, Integer userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取医生基本信息（名称，专长，职称，医院，认证，三甲，就诊过等相关信息）
		List<User> list = relationDao.getRelation(relationType, userId);

		Collections.sort(list, new SortByOrderCreateTime());

		List<DoctorStatVO> statList = convert(list);
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		List<Integer> OnlineList = getOnlineDoctors();
		for (DoctorStatVO vo : statList) {
			Pack param = new Pack();
			param.setDoctorId(vo.getDoctorId());;
			param.setStatus(PackEnum.PackStatus.open.getIndex());			
			List<Pack> packList = packMapper.queryForPrice(param);
			setPackStatusAndPrice(packList, vo);
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
		map.put("users", statList);
		return map;
	}

    class SortByOrderCreateTime implements Comparator<User> {

    	Map<Integer, Long> map = new HashMap<Integer, Long>();
    	public SortByOrderCreateTime() {
    		List<OrderVO> orders = orderMapper.getLastEveryDoctor(ReqUtil.instance.getUserId());
    		for (OrderVO order : orders) {
    			map.put(order.getDoctorId(), order.getCreateTime());
    		}
    	}

		@Override
		public int compare(User o1, User o2) {
			if (map.get(o1.getUserId()) != null && map.get(o2.getUserId()) != null) {
				return map.get(o2.getUserId()).compareTo(map.get(o1.getUserId()));
			} else if (map.get(o1.getUserId()) != null) {
				return -1;
			} else if (map.get(o2.getUserId()) != null) {
				return 1;
			}
			return 0;
		}

    }

	/**
	 * 设置最低价格以及开通情况
	 *
	 * @param packList
	 * @param vo
	 */
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

	/**
	 * 设置最低价格以及开通情况
	 *
	 * @param packList
	 * @param vo
	 */
	private void setPackStatusAndMinPrice(List<Pack> packList, DoctorStatVO vo) {
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
		vo.setMinPrice(String.valueOf(price));
	}

    @Override
	public Object searchDoctorByKeyWord(GroupSearchParam param) {
		Integer groupOwnerId = null;
		List<User> list = new ArrayList<User>();

		// 获取好友列表
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();

		//集团所有者
		GroupUser groupUser = groupUserDao.findGroupRootAdmin(param.getDocGroupId());
		if (groupUser != null) {
			groupOwnerId = groupUser.getDoctorId();
		}

		if (param.getSearchType().equals("kId")) {
			if (StringUtil.isBlank(param.getKeyword())) {
				return null;
			}
			param.setDiseaseId(null);
			list = groupSearchDao.searchDocByKeywordFomEsServer(param);
		} else if (param.getSearchType().equals("gId")) {
			if (StringUtil.isEmpty(param.getDocGroupId())) {
				return null;
			}
			// 先判断是否为第一页，若为第一页，则先查出该集团的所有者，并将其放在列表元素中的第一个，若不为第一页，则正常查找（需要排除集团所有者的id）
			// 1、先去group_user表获取集团所有者（不将集团所有者排在第一位了）
//			if (groupOwnerId != null && param.getPageIndex() == 0) {
//				User groupOwner = userRepository.getUser(groupOwnerId);
//				if (groupOwner != null) {
//					list.add(groupOwner);
//				}
//			}
			// 2、再去user表进行查询
			PageVO vo = null;
			if (param.getSort().intValue() == 0) {
				vo = groupSearchDao.searchDocByGIdOrSIdWithoutOwnerId(param, null, myDoctorIds);
			} else if(param.getSort().intValue() == 1 && StringUtils.isNotEmpty(param.getLat()) && StringUtils.isNotEmpty(param.getLng())) {
				//离我最近
				vo = groupSearchDao.searchDocByGIdOrSIdWithoutOwnerIdAll(param, null, myDoctorIds);
			} else {
				//这种情况暂时也按照综合排序处理
				vo = groupSearchDao.searchDocByGIdOrSIdWithoutOwnerId(param, null, myDoctorIds);
			}
			list.addAll(vo.getPageData() != null ?(List<User>) vo.getPageData():new ArrayList<User>());
		} else if (param.getSearchType().equals("sId")) {
			if (StringUtil.isEmpty(param.getDocGroupId()) || StringUtil.isEmpty(param.getDeptName())) {
				return null;
			}
			PageVO vo = groupSearchDao.searchDocByGIdOrSId(param);
			list = (List<User>) vo.getPageData();
		}

		if (list == null || list.size() == 0)
			return null;

		List<DoctorStatVO> statList = convert(list);
		// 获取医院的经纬度来计算距离
		if (StringUtils.isNotEmpty(param.getLat()) && StringUtils.isNotEmpty(param.getLng())) {
			List<String> hospitalIds = Lists.newArrayList();
			if (statList != null && statList.size() > 0) {
				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						hospitalIds.add(doctorStatVO.getHospitalId());
					}
				}
				Map<String, String> distanseMap = Maps.newHashMap();
				if (hospitalIds != null && hospitalIds.size() > 0) {
					List<HospitalVO> hospitalVOs = baseDataDao.getHospitals(hospitalIds);
					if (hospitalVOs != null && hospitalVOs.size() > 0) {
						for (HospitalVO hospitalVO : hospitalVOs) {
							if(StringUtil.isNotEmpty( hospitalVO.getLat())&&StringUtil.isNotEmpty( hospitalVO.getLng()))
							{
							String distanse = MapDistance.getDistance(param.getLng(), param.getLat(), hospitalVO.getLng(), hospitalVO.getLat());
							distanseMap.put(hospitalVO.getId(), distanse);
							}
						}
					}
				}

				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						doctorStatVO.setDistance(distanseMap.get(doctorStatVO.getHospitalId()));
					}
				}
			}
		}
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, groupOwnerId);

		if (param.getSort().intValue() == 1) {
			//离我最近排序
			Collections.sort(statList, (a, b) -> {
				if (StringUtils.isEmpty(a.getDistance()) || StringUtils.isEmpty(b.getDistance())) {
					if(!StringUtils.isEmpty(a.getDistance()) && StringUtils.isEmpty(b.getDistance())) {
						return -1;
					} else if(StringUtils.isEmpty(a.getDistance()) && !StringUtils.isEmpty(b.getDistance())) {
						return 1;
					} else {
						return 0;
					}

				} else {
					Double d1 = Double.valueOf(a.getDistance());
					Double d2 = Double.valueOf(b.getDistance());
					return d1.compareTo(d2);
				}

			});

			List<DoctorStatVO> resultList = Lists.newArrayList();
			if (statList != null) {
				//从list中的第几个元素开始取
				int start = param.getPageIndex() < 0 ? 0 : (param.getPageIndex() * param.getPageSize());
				//取到第几个元素
				int end = start + param.getPageSize() - 1;
				for (int i = start; i <= end; i++) {
					if (start > (statList.size()-1)) {
						//当起始位大于all最大的一个元素，则什么都不做
					} else {
						if (end > ((statList.size()-1))) {
							//若要取得结束的元素位置，大于all的最大长度,则取值取到all的最大元素即可
							if (i <= (statList.size()-1)) {
								resultList.add(statList.get(i));
							} else {
								//当i大于all的最大长度时，则什么都不做
							}
						} else {
							resultList.add(statList.get(i));
						}
					}
				}
			}
			return resultList;
		}

		return statList;
	}

	@Override
	public List<DoctorStatVO> searchDoctorByDiseaseId(GroupSearchParam param) {
		//为空时查全部
//		if (StringUtil.isEmpty(param.getDiseaseId())) {
//			throw new ServiceException("病种ID不能为空");
//		}
		List<User> list = gdocDao.getDoctorByDiseaseId(param);
		// 获取好友列表
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}
		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		List<DoctorStatVO> statList = convert(list);

		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
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
		return statList;
	}

	@Override
	public PageVO getGroupOnlineDoctors(String groupId, Integer countryId, Integer provinceId, Integer cityId, String deptId, Integer pageIndex, Integer pageSize) {
		//1、先去c_group_doctor表中获取该集团状态为在线的医生的id
		List<GroupDoctor> groupDoctors = gdocDao.getOnlineDoctorsByGroup(groupId);
		if (groupDoctors != null && groupDoctors.size() > 0) {
			List<Integer> groupDoctorIds = Lists.newArrayList();
			for (GroupDoctor groupDoctor : groupDoctors) {
				groupDoctorIds.add(groupDoctor.getDoctorId());
			}

			//2、再去User表查询资料
			PageVO pageVO = userRepository.findByAreaAndDeptWithInIds(groupDoctorIds, countryId, provinceId, cityId, deptId, pageIndex, pageSize);
			List<User> users = (List<User>) pageVO.getPageData();
			List<User> usersOrder = Lists.newArrayList();
			//fixBug see XGSF-9693 最新上线的医生排最前面
			if (users != null && users.size() > 0) {
				for(Integer id : groupDoctorIds) {
					for (User user : users) {
						if (id.equals(user.getUserId())) {
							usersOrder.add(user);
						}
					}
				}
			}

			//3、  获取好友列表
			List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
			List<Integer> myDoctorIds = new ArrayList<Integer>();
			for (DoctorPatient dp : dpList) {
				myDoctorIds.add(dp.getToUserId());
			}
			// 获取在线医生列表
			List<Integer> OnlineList = getOnlineDoctors();
			List<DoctorStatVO> statList = convert(usersOrder);

			// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
			for (DoctorStatVO vo : statList) {

				if (myDoctorIds.contains(vo.getDoctorId())) {
					vo.setMyDoctor("1");
				} else {
					vo.setMyDoctor("0");
				}

				// 获取在线门诊的价格（这里不取图文、电话套餐的价格做对比）
				if (OnlineList.contains(vo.getDoctorId())) {
					vo.setClinicOpen("1");
					OutpatientVO op = gdocService.getOutpatientInfo(vo.getDoctorId());
					vo.setPrice(op.getIsFree() ? "0" : String.valueOf(op.getPrice()));
				}
				if (vo.getPrice() == null) {
					vo.setPrice("0");
				}
			}
			PageVO statPageVo = new PageVO();
			statPageVo.setPageIndex(pageVO.getPageIndex());
			statPageVo.setPageSize(pageVO.getPageSize());
			statPageVo.setPageData(statList);
			statPageVo.setTotal(pageVO.getTotal());

			return statPageVo;

		} else {
			PageVO statPageVo = new PageVO();
			statPageVo.setPageIndex(pageIndex);
			statPageVo.setPageSize(pageSize);
			statPageVo.setPageData(null);
			statPageVo.setTotal(0l);
			return statPageVo;
		}
	}

	@Override
	public PageVO findDoctorsByGID(GroupSearchParam param) {
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("集团ID不能为空");
		}
		//获取地区医院ID
		List<HospitalPO> hpos = baseDataDao.getHospitalsCodeNull(param.getAreaCode(), false);
		if (param.getAreaCode() != null && hpos.isEmpty()) {
			return new PageVO();
		}

		//取得所有医院的Id
		List<String> hospitalIds = new ArrayList<String>();
		for (HospitalPO hpo : hpos) {
			hospitalIds.add(hpo.getId());
		}

		//获取集团的所有医生ID
		List<Integer> allDocs = gdocService.getDocIdsByGroupID(param.getGroupId(), "C");
		//获取我在集团增就诊过的医生ID
		List<DoctorPatient> list = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : list) {
			myDoctorIds.add(dp.getToUserId());
		}
		List<Integer> friends = getMyFriendsList(list,allDocs,true);

		//通过医院Id、科室名获取曾就诊过的医生列表
		Query<User> q = userRepository.getDoctorQuery(friends, hospitalIds, param.getSpecialistId());
		List<User> myList = q.offset(param.getStart()).limit(param.getPageSize()).asList();
		setMyDoc(myList);

		//获取我未在集团增就诊过的医生ID
		if(myList.size() < param.getPageSize()){
			List<Integer> noFriends = getMyFriendsList(list,allDocs,false);
			int skip = 0;

			q = userRepository.getDoctorQuery(friends, hospitalIds, param.getSpecialistId());
			int all = Integer.parseInt(q.countAll()+"");
			int myTotalPage = all / param.getPageSize() ;//总页数
			int lastSize = all%param.getPageSize();//最后一页size
			int limit = param.getPageSize() - myList.size();

			if(param.getPageIndex() ==  myTotalPage){
				skip = 0;
			}else if(param.getPageIndex() > myTotalPage){
				int skipPage = param.getPageIndex() - myTotalPage -1;
				skipPage = skipPage < 0 ? 0:skipPage;
				skip = param.getPageSize() - lastSize  + param.getPageSize()*(skipPage);
			}

			q = userRepository.getDoctorQuery(noFriends, hospitalIds, param.getSpecialistId());
			myList.addAll(q.offset(skip).limit(limit).asList());
		}

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		GroupUser groupUser = groupUserDao.findGroupRootAdmin(param.getDocGroupId());
		Integer groupOwnerId = null;
		if (groupUser != null) {
			groupOwnerId = groupUser.getDoctorId();
		}

		List<DoctorStatVO> myDocStat = convert(myList);
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds,myDocStat,OnlineList,groupOwnerId);
		return new PageVO(myDocStat, userRepository.getDoctorQuery(allDocs, hospitalIds, param.getSpecialistId()).countAll(), param.getPageIndex(), param.getPageSize());
	}
	private void setMyDoc(List<User> list){
		for(User u :list){
			u.setRemarks("MY_DOCTOR");
		}
	}

	private List<Integer> getMyFriendsList(List<DoctorPatient> list,List<Integer> all,boolean isFrends){
		List<Integer> myDoctorIds = new ArrayList<Integer>();
        List<Integer> friendList = new ArrayList<Integer>();
    	for (DoctorPatient dp : list) {
    		friendList.add(dp.getToUserId());
		}
        if(isFrends){
        	for (Integer uid  : friendList) {
    			if(all.contains(uid)){
    				myDoctorIds.add(uid);
    			}
    		}
        }else{
        	for(Integer uid :all){
        		if(!friendList.contains(uid)){
        			myDoctorIds.add(uid);
        		}
        	}
        }
	return myDoctorIds;
	}

	@Override
	public List<DeptVO> getAllDoctDepts(String groupId, String type) {
		//根据type类型获取所有对应的doctor --------- > 获取doctor对应的所有deptId ------ > 组装成结构化数据返回
		List<Integer> uList = new ArrayList<Integer>();
		if(StringUtil.isEmpty(type)){
			type = "all";
		}
		if(type.equals("appoint")){
			//获取所有预约医生
			List<Integer> allDocs = gdocService.getDocIdsByGroupID(groupId, "C");
			//获取开通了预约套餐的所有医生
			List<Integer> dIds = packMapper.getAppointmentDoctorIds(allDocs);
			uList.addAll(dIds);
		}else if(type.equals("online")){
			//获取集团所有在线医生
			 uList = getMessageOrPhonePackDoctor(groupId);

//			List<GroupDoctor> list = gdocDao.getOnlineDoctorsByGroup(groupId);
//			uList = Lists.transform(list, new Function<GroupDoctor, Integer>() {
//				@Override
//				public Integer apply(GroupDoctor input) {
//					return input.getDoctorId();
//				}
//			});

		}else {
			//获取集团所有医生
			List<GroupDoctor> list = gdocDao.findDoctorsByGroupId(groupId);
			uList = Lists.transform(list, new Function<GroupDoctor, Integer>() {
				@Override
				public Integer apply(GroupDoctor input) {
					return input.getDoctorId();
				}
			});
		}
		if(uList.size() == 0){
			return new ArrayList<DeptVO>();
		}
		List<User> docList = userRepository.getDoctorQuery(uList, null, null).asList();
		List<String> deptList = transerDeptList(docList);
		List<DeptVO> voList = baseDataDao.getDeptByIds(deptList);
		return format(voList);
	}

	private List<DeptVO> format(List<DeptVO> list){
		List<DeptVO> voList = new ArrayList<DeptVO>();
		Map<String, DeptVO> map = new HashMap<String, DeptVO>();
		if (!CollectionUtils.isEmpty(list)) {
			for (DeptVO vp : list) {
				if (vp.getParentId().equals("A")) {
					if (vp.getChildren() == null) {
						vp.setChildren(new ArrayList<DeptVO>());
					}
					map.put(vp.getId(), vp);
					voList.add(vp);
				} else {
					if (map.containsKey(vp.getParentId())) {
						map.get(vp.getParentId()).getChildren().add(vp);
					} else {
						DeptVO dp = baseDataDao.getDeptById(vp.getParentId());
						if (dp != null) {
							List<DeptVO> children = new ArrayList<DeptVO>();
							children.add(vp);
							dp.setChildren(children);
							map.put(dp.getId(), dp);
							voList.add(dp);
						}
					}
				}
			}
		}
		return voList;
	}

	/**
	 * 获取对应的科室ID列表
	 * @param docList
	 * @return
	 */
	private List<String> transerDeptList(List<User> docList ){
		return Lists.transform(docList, new Function<User, String>() {
			@Override
			public String apply(User u) {
				if(u.getDoctor() == null || StringUtil.isEmpty(u.getDoctor().getDeptId())){
					return "";
				}
				return u.getDoctor().getDeptId();
			}
		});
	}

	private void setPrice(List<Integer> myDoctorIds,List<DoctorStatVO> statList,List<Integer> OnlineList,Integer groupOwnerId){
		for (DoctorStatVO vo : statList) {
			Pack pack = new Pack();
			pack.setDoctorId(vo.getDoctorId());
			pack.setStatus(PackEnum.PackStatus.open.getIndex());
			List<Pack> packList = packMapper.queryForPrice(pack);
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

	private void setMinPrice(List<DoctorStatVO> statList) {
		for (DoctorStatVO vo : statList) {
			Pack pack = new Pack();
			pack.setDoctorId(vo.getDoctorId());
			pack.setStatus(PackEnum.PackStatus.open.getIndex());
			List<Pack> packList = packMapper.queryForPrice(pack);
			setPackStatusAndMinPrice(packList, vo);
			if (Long.valueOf(vo.getMinPrice()) == Long.MAX_VALUE) {
				vo.setMinPrice("0");
			}
		}
	}

	public static void main(String[] args) {
//		int a = 3;
//		System.err.println(a/10);
//		System.err.println(a%10);
		List<String> list=new ArrayList<String>();
		for(int i=1;i<100;i++){
			list.add(i+"");
		}
		System.out.println("前："+list.size());
		list=list.subList(10, 20);
		System.out.println("后："+list.size());
		for(String str:list){
			System.out.println(str);
		}
	}

	@Override
	public PageVO getDoctorsByPackType(String groupId, Integer packType, Integer pageIndex, Integer pageSize) {
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("集团ID为空");
		}
		if (packType == null) {
			throw new ServiceException("服务类型为空");
		}
		List<DoctorStatVO> doctorStatVOs = Lists.newArrayList();

		//1、判断packType类型，为1、2的则先查出所有的集团医生的id
		List<Integer> allGroupDoctorIds = gdocDao.getDoctorIdByGroupId(groupId);

		if (allGroupDoctorIds != null && allGroupDoctorIds.size() > 0) {
			//2、再去MySQL库中查询p_pack表获取到医生的Id
			PackParam2 packParam = new PackParam2();
			packParam.setType(packType);
			packParam.setDoctorIds(allGroupDoctorIds);
			packParam.setGroupId(groupId);
			//3、获取开启服务的医生的id
			List<Integer> openServerGroupDoctorIds = Lists.newArrayList();

			if (packType.equals(1) || packType.equals(2) || packType.equals(3)) {
				openServerGroupDoctorIds = packMapper.getAdviseDoctorIds(packParam);
			}
			/*else if(packType.equals(3)) {
				//筛选健康关怀价格大于0的医生
				openServerGroupDoctorIds = packMapper.getNoFreeAdviseDoctorIds(packParam);
						//packMapper.getAdviseDoctorIdsWithGroup(packParam);
			}
			else {
				//其他的服务套餐暂时不支持（2016-07-26）
			}*/

			if (openServerGroupDoctorIds != null && openServerGroupDoctorIds.size() > 0) {
				//4、查询最终需要的DoctorStatVO对象
				//4.1、先根据id查询User表
				List<User> users = userRepository.findUsersByIds(openServerGroupDoctorIds);
				//4.2、调用convert方法
				doctorStatVOs = convert(users);

				//4.3、  获取好友列表
				List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
				List<Integer> myDoctorIds = new ArrayList<Integer>();
				for (DoctorPatient dp : dpList) {
					myDoctorIds.add(dp.getToUserId());
				}

				// 获取在线医生列表
				List<Integer> OnlineList = getOnlineDoctors();
				//集团所有者(由于该接口不传groupId，所以不返回用户是否为集团所有者)
				Integer groupOwnerId = null;
				//集团所有者
				GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);
				if (groupUser != null) {
					groupOwnerId = groupUser.getDoctorId();
				}
				// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
				setPrice(myDoctorIds, doctorStatVOs, OnlineList, groupOwnerId);

				//4.4、 拼装价格字段
				List<Pack> packs = null;
				if (packType.equals(1) || packType.equals(2) || packType.equals(3)) {
					packs = packMapper.getAdviseByDoctorIds(openServerGroupDoctorIds);
				}
				/*else if (packType.equals(3)) {
					//只获取价格大于0的健康关怀
					packs = packMapper.getNoFreeAdviseByDoctorIds(openServerGroupDoctorIds);

					//健康关怀目前不区分集团(2016-8-9)
//					PackParam2 packParam2 = new PackParam2();
//					packParam2.setGroupId(groupId);
//					packParam2.setDoctorIds(openServerGroupDoctorIds);
//					packs = packMapper.getAdviseByDoctorIdsWithGroup(packParam2);
				}
				else {
					//其他的服务类型暂时不做处理
				}*/
				for (DoctorStatVO vo : doctorStatVOs) {
					if (myDoctorIds.contains(vo.getDoctorId())) {
						vo.setMyDoctor("1");
					} else {
						vo.setMyDoctor("0");
					}
					//packType来获取价格
					String price = null;
					Integer timeLimet = null;
					if (packs != null && packs.size() > 0) {
						List<Long> prices = Lists.newArrayList();
						for (Pack pack : packs) {
							if (pack.getDoctorId().equals(vo.getDoctorId()) && pack.getPackType().equals(packType)) {
								if (pack.getPackType().equals(PackEnum.PackType.message.getIndex()) || pack.getPackType().equals(PackEnum.PackType.phone.getIndex())) {
									price = String.valueOf(pack.getPrice());
									timeLimet = pack.getTimeLimit();
									vo.setReplyCount(pack.getReplyCount());
								} else {
									prices.add(pack.getPrice());
								}
							}
						}
						if (packType.equals(PackEnum.PackType.careTemplate.getIndex())) {
							if (prices!= null && prices.size() > 0) {
								price = String.valueOf(Collections.min(prices));
							} else {
								price = "0";
							}
						}
					} else {
						price = "0";
					}
					vo.setTimeLimit(timeLimet);
					vo.setPrice(price);
				}

				//4.5、根据MyDoctor字段对doctorStatVOs进行一次排序，将就诊过的排到最前面
				Collections.sort(doctorStatVOs, (a, b) -> b.getMyDoctor().compareTo(a.getMyDoctor()));
			}
		}

		//5、内存分页
		PageVO pageVO = new PageVO();
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);

		if (doctorStatVOs != null && doctorStatVOs.size() > 0) {
			List<DoctorStatVO> result = Lists.newArrayList();
			//从list中的第几个元素开始取
			int start = pageIndex < 0 ? 0 : (pageIndex * pageSize);
			//取到第几个元素
			int end = start + pageSize - 1;
			for (int i = start; i <= end; i++) {
				if (start > (doctorStatVOs.size()-1)) {
					//当起始位大于all最大的一个元素，则什么都不做
				} else {
					if (end > ((doctorStatVOs.size()-1))) {
						//若要取得结束的元素位置，大于all的最大长度,则取值取到all的最大元素即可
						if (i <= (doctorStatVOs.size()-1)) {
							result.add(doctorStatVOs.get(i));
						} else {
							//当i大于all的最大长度时，则什么都不做
						}
					} else {
						result.add(doctorStatVOs.get(i));
					}
				}
			}
			pageVO.setPageData(result);
		} else {
			pageVO.setPageData(doctorStatVOs);
		}
		pageVO.setTotal(Long.valueOf(doctorStatVOs.size()));

		return pageVO;
	}



	@Override
	public PageVO getRecommendDoctorByGroupId(String groupId, Integer pageIndex, Integer pageSize) {
		//1、去t_doctor_recommend分页查询到医生的id
		List<Integer> doctorIds = doctorRecommendDao.getDoctorIds(groupId, pageIndex, pageSize);
		Long count = doctorRecommendDao.getDoctorIdsCount(groupId);

		//2、根据doctorId查询user表
		List<User> users = userRepository.findUsersWithOutStatus(doctorIds);
		List<User> userSort = Lists.newArrayList();
		//2.1、在根据doctorIds的顺序，对User进行排序
		if (users != null && users.size() > 0) {
			for(Integer doctorId : doctorIds){
				for(User user : users) {
					if (doctorId.equals(user.getUserId())) {
						userSort.add(user);
					}
				}
			}
		}

		//3、将User转化为DoctorStatVo
		List<DoctorStatVO> result = convert(userSort);

		// 获取好友列表
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		//集团所有者(由于该接口不传groupId，所以不返回用户是否为集团所有者)
		Integer groupOwnerId = null;
		//集团所有者
		GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);
		if (groupUser != null) {
			groupOwnerId = groupUser.getDoctorId();
		}
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, result, OnlineList, groupOwnerId);

		PageVO pageVO = new PageVO();
		pageVO.setPageData(result);
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		pageVO.setTotal(count);
		return pageVO;
	}

	@Override
	public PageVO getDoctorsByDiseaseId(String diseaseId, String lat, String lng, Integer pageIndex, Integer pageSize) {
		//1、去user表查询userid
		PageVO pageVO = userRepository.getDoctorsByDiseaseId(diseaseId, pageIndex, pageSize);
		List<User> users = (List<User>) pageVO.getPageData();
		List<DoctorStatVO> statList = convert(users);
		if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)) {
			List<String> hospitalIds = Lists.newArrayList();
			if (statList != null && statList.size() > 0) {
				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						hospitalIds.add(doctorStatVO.getHospitalId());
					}
				}
				Map<String, String> distanseMap = Maps.newHashMap();
				if (hospitalIds != null && hospitalIds.size() > 0) {
					List<HospitalVO> hospitalVOs = baseDataDao.getHospitals(hospitalIds);
					if (hospitalVOs != null && hospitalVOs.size() > 0) {
						for (HospitalVO hospitalVO : hospitalVOs) {
							if (hospitalVO != null && hospitalVO.getLat()!= null && hospitalVO.getLng()!= null) {
								String distanse = MapDistance.getDistance(lng, lat, hospitalVO.getLng(), hospitalVO.getLat());
								distanseMap.put(hospitalVO.getId(), distanse);
							}
						}
					}
				}

				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						doctorStatVO.setDistance(distanseMap.get(doctorStatVO.getHospitalId()));
					}
				}
			}
		}

		// 获取好友列表
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		//集团所有者(由于该接口不传groupId，所以不返回用户是否为集团所有者)
		Integer groupOwnerId = null;
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, groupOwnerId);
		pageVO.setPageData(statList);
		return pageVO;
	}

	@Override
	public PageVO getDoctorsByDiseaseIds(List<String> diseaseId, String lat, String lng, Integer pageIndex, Integer pageSize) {

		PageVO pageVO= userRepository.getDoctorsByDiseaseIds(diseaseId,pageIndex, pageSize);

		List<User> users = (List<User>) pageVO.getPageData();
		List<DoctorStatVO> statList = convert(users);
		if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)) {
			List<String> hospitalIds = Lists.newArrayList();
			if (statList != null && statList.size() > 0) {
				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						hospitalIds.add(doctorStatVO.getHospitalId());
					}
				}
				Map<String, String> distanseMap = Maps.newHashMap();
				if (hospitalIds != null && hospitalIds.size() > 0) {
					List<HospitalVO> hospitalVOs = baseDataDao.getHospitals(hospitalIds);
					if (hospitalVOs != null && hospitalVOs.size() > 0) {
						for (HospitalVO hospitalVO : hospitalVOs) {
							if (hospitalVO != null && hospitalVO.getLat()!= null && hospitalVO.getLng()!= null) {
								String distanse = MapDistance.getDistance(lng, lat, hospitalVO.getLng(), hospitalVO.getLat());
								distanseMap.put(hospitalVO.getId(), distanse);
							}
						}
					}
				}

				for(DoctorStatVO doctorStatVO : statList) {
					if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
						doctorStatVO.setDistance(distanseMap.get(doctorStatVO.getHospitalId()));
					}
				}
			}
		}

		// 获取好友列表
		List<DoctorPatient> dpList = friendsManager.getFriends(ReqUtil.instance.getUserId(), DoctorPatient.class);
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		for (DoctorPatient dp : dpList) {
			myDoctorIds.add(dp.getToUserId());
		}

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		//集团所有者(由于该接口不传groupId，所以不返回用户是否为集团所有者)
		Integer groupOwnerId = null;
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, groupOwnerId);
		pageVO.setPageData(statList);
		return pageVO;
	}

	@Override
	public List<DoctorStatVO> findDoctorByLocation(String lng, String lat) {
		List<User> list = new ArrayList<User>();

		if(StringUtil.isEmpty(lng)){
			throw new ServiceException("经度不能为空");
		}
		if(StringUtil.isEmpty(lat)){
			throw new ServiceException("纬度不能为空");
		}

		double _lng = 0l;//经度
		double _lat = 0l;//纬度
		try{
			_lng = Double.parseDouble(lng);
			_lat = Double.parseDouble(lat);
		}catch(NumberFormatException e){
			logger.info(e.getMessage(), e);
			throw new ServiceException("传入参数格式错误");
		}
		List<HospitalVO> voList = baseDataDao.getHospitalsByLocation(null, false, _lng, _lat);

		if(CollectionUtils.isEmpty(voList)){
			return null;
		}

		//取得所有医院的Id
		List<String> _hospitalIds = new ArrayList<String>();
		Map<String, String> lngMap = Maps.newHashMap();
		Map<String, String> latMap = Maps.newHashMap();
		for (HospitalVO vo : voList) {
			_hospitalIds.add(vo.getId());
			lngMap.put(vo.getId(), vo.getLng());
			latMap.put(vo.getId(), vo.getLat());
		}

		Query<User> q = userRepository.getDoctorQuery(null, _hospitalIds, null).order("-doctor.serviceStatus,doctor.titleRank,-doctor.cureNum");

		list = q.asList();

		List<Integer> myDoctorIds = new ArrayList<Integer>();

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();

		if (list == null || list.size() == 0)
			return null;

		List<DoctorStatVO> statList = convert(list);
		for(DoctorStatVO doctorStatVO : statList) {
			if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
				doctorStatVO.setLng(lngMap.get(doctorStatVO.getHospitalId()));
				doctorStatVO.setLat(latMap.get(doctorStatVO.getHospitalId()));

				if(StringUtil.isNotEmpty(lng) && StringUtil.isNotEmpty(lat)){
					double dis = MapDistance.Distance(_lng, _lat, Double.parseDouble(doctorStatVO.getLng()), Double.parseDouble(doctorStatVO.getLat()));
					doctorStatVO.setDistance(dis + "");
				}
			}
		}
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, null);

		//离我最近排序
		Collections.sort(statList, (a, b) -> {
			if (StringUtils.isEmpty(a.getDistance()) || StringUtils.isEmpty(b.getDistance())) {
				if(!StringUtils.isEmpty(a.getDistance()) && StringUtils.isEmpty(b.getDistance()))
				{
					return -1;
				}
				if(StringUtils.isEmpty(a.getDistance()) && !StringUtils.isEmpty(b.getDistance()))
				{
					return 1;
				}
				else
				{
					return 0;
				}

			} else {
				Double d1 = Double.valueOf(a.getDistance());
				Double d2 = Double.valueOf(b.getDistance());
				return d1.compareTo(d2);
			}

		});

		return statList;
	}



	@Override
	public List<DoctorStatVO> findDoctorByHospitalId(String hospitalId, String lng, String lat) {
		List<User> list = new ArrayList<User>();

		if(StringUtil.isEmpty(hospitalId)){
			throw new ServiceException("医院不能为空");
		}

		double _lng = 0l;//经度
		double _lat = 0l;//纬度
		try{
			_lng = Double.parseDouble(lng);
			_lat = Double.parseDouble(lat);
		}catch(NumberFormatException e){
			logger.info(e.getMessage(), e);
			throw new ServiceException("传入参数格式错误");
		}
		HospitalVO vo = baseDataDao.getHospital(hospitalId);

		if(vo == null){
			return null;
		}

		//取得所有医院的Id
		List<String> _hospitalIds = new ArrayList<String>();
		Map<String, String> lngMap = Maps.newHashMap();
		Map<String, String> latMap = Maps.newHashMap();
		if(vo != null){
			_hospitalIds.add(vo.getId());
			lngMap.put(vo.getId(), vo.getLng());
			latMap.put(vo.getId(), vo.getLat());
		}

		Query<User> q = userRepository.getDoctorQuery(null, _hospitalIds, null).order("-doctor.cureNum,doctor.titleRank");

		list = q.asList();

		List<Integer> myDoctorIds = new ArrayList<Integer>();

		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();

		if (list == null || list.size() == 0)
			return null;

		List<DoctorStatVO> statList = convert(list);
		for(DoctorStatVO doctorStatVO : statList) {
			if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
				doctorStatVO.setLng(lngMap.get(doctorStatVO.getHospitalId()));
				doctorStatVO.setLat(latMap.get(doctorStatVO.getHospitalId()));

				if(StringUtil.isNotEmpty(lng) && StringUtil.isNotEmpty(lat)){
					double dis = com.dachen.health.commons.utils.MapDistance.Distance(_lng, _lat, Double.parseDouble(doctorStatVO.getLng()), Double.parseDouble(doctorStatVO.getLat()));
					doctorStatVO.setDistance(dis + "");
				}
			}
		}
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, null);

		//离我最近排序
		Collections.sort(statList, (a, b) -> {
			if (StringUtils.isEmpty(a.getDistance()) || StringUtils.isEmpty(b.getDistance())) {
				if(!StringUtils.isEmpty(a.getDistance()) && StringUtils.isEmpty(b.getDistance()))
				{
					return -1;
				}
				if(StringUtils.isEmpty(a.getDistance()) && !StringUtils.isEmpty(b.getDistance()))
				{
					return 1;
				}
				else
				{
					return 0;
				}

			} else {
				Double d1 = Double.valueOf(a.getDistance());
				Double d2 = Double.valueOf(b.getDistance());
				return d1.compareTo(d2);
			}

		});

		return statList;
	}

	@Override
	public List<DoctorStatVO> findDoctorByCondition(String city, String deptId, String lng, String lat) {
		List<User> doctorList = new ArrayList<User>();

		if(StringUtil.isEmpty(city)){
			throw new ServiceException("城市不能为空");
		}

		Integer _code = null;
		double _lng = 0l;//经度
		double _lat = 0l;//纬度
		try{
			_code = Integer.parseInt(city);
			if(StringUtil.isNotEmpty(lng)){
				_lng = Double.parseDouble(lng);
			}
			if(StringUtil.isNotEmpty(lat)){
				_lat = Double.parseDouble(lat);
			}
		}catch(NumberFormatException e){
			logger.info(e.getMessage(), e);
			throw new ServiceException("传入参数格式错误");
		}

		//所有医院列表，因为这里有可能是按照全国+科室去搜索医生。所以这里需要缓存所有医院的地理位置
		if(this.lngMap.isEmpty() && this.latMap.isEmpty()){
			List<HospitalVO> allHospitals = baseDataDao.getHospitalInfos(null, false);

			for (HospitalVO vo : allHospitals) {
				this.lngMap.put(vo.getId(), vo.getLng());
				this.latMap.put(vo.getId(), vo.getLat());
			}
		}

		//按照条件搜索出来的医院列表
		List<HospitalVO> hospitalList = baseDataDao.getHospitalInfos(_code, false);

		boolean isCurCondition = true;
		/**
		 * 按城市+科室去搜索，搜索不到则按照科室去搜索
		 */
		if(CollectionUtils.isEmpty(hospitalList)){
			isCurCondition = false;
		}

		//取得所有医院的Id
		List<String> _hospitalIds = new ArrayList<String>();
		for (HospitalVO vo : hospitalList) {
			_hospitalIds.add(vo.getId());
		}

		/**
		 * 按城市+科室去搜索，搜索不到则按照科室去搜索
		 */
		Query<User> q = userRepository.getDoctorQuery(null, _hospitalIds, deptId);
		doctorList = q.asList();
		if(CollectionUtils.isEmpty(doctorList)){
			q = userRepository.getDoctorQuery(null, null, deptId);
			doctorList = q.asList();

			isCurCondition = false;
		}

		if (doctorList == null || doctorList.size() == 0)
			return null;

		List<DoctorStatVO> statList = convert(doctorList);
		
		List<DoctorStatVO> delList = new ArrayList<>();
		for(DoctorStatVO doctorStatVO : statList) {
			doctorStatVO.setCurCondition(isCurCondition);
			if (StringUtils.isNotEmpty(doctorStatVO.getHospitalId())) {
				doctorStatVO.setLng(this.lngMap.get(doctorStatVO.getHospitalId()));
				doctorStatVO.setLat(this.latMap.get(doctorStatVO.getHospitalId()));

				//去掉没有经纬度的医生
				if(StringUtil.isEmpty(doctorStatVO.getLng()) && StringUtil.isEmpty(doctorStatVO.getLat())){
					delList.add(doctorStatVO);
				}
				
				if(StringUtil.isNotEmpty(lng) && StringUtil.isNotEmpty(lat)&&doctorStatVO.getLat()!=null&&doctorStatVO.getLng()!=null){
					double dis = com.dachen.health.commons.utils.MapDistance.Distance(_lng, _lat, Double.parseDouble(doctorStatVO.getLng()), Double.parseDouble(doctorStatVO.getLat()));
					doctorStatVO.setDistance(dis + "");
				}
			}
		}
		
		if(!CollectionUtils.isEmpty(delList)){
			statList.removeAll(delList);
		}
		
		List<Integer> myDoctorIds = new ArrayList<Integer>();
		// 获取在线医生列表
		List<Integer> OnlineList = getOnlineDoctors();
		// 获取（会诊，门诊，图文，电话，关怀计划）各套餐开通情况以及最低价格
		setPrice(myDoctorIds, statList, OnlineList, null);

		//离我最近排序
		Collections.sort(statList, (a, b) -> {
			if (StringUtils.isEmpty(a.getDistance()) || StringUtils.isEmpty(b.getDistance())) {
				if(!StringUtils.isEmpty(a.getDistance()) && StringUtils.isEmpty(b.getDistance()))
				{
					return -1;
				}
				if(StringUtils.isEmpty(a.getDistance()) && !StringUtils.isEmpty(b.getDistance()))
				{
					return 1;
				}
				else
				{
					return 0;
				}

			} else {
				Double d1 = Double.valueOf(a.getDistance());
				Double d2 = Double.valueOf(b.getDistance());
				return d1.compareTo(d2); 
			}

		});

		return statList;
	}

	@Override
	public List<DoctorStatVO> findDoctorByGoodsGroupIds(List<Integer> doctorIds) {
		Query<User> q = userRepository.getDoctorQuery(doctorIds, null, null);

		List<User> users=q.asList();
		Collections.sort(users, new SortByMyDoctor());
		List<DoctorStatVO> vos=convert(users);
		for (DoctorStatVO vo : vos) {
			Map<String, Integer> params = new HashMap<String, Integer>();
			params.put("doctorId", vo.getDoctorId());
			params.put("packType", PackType.phone.getIndex());
			Pack pack = packMapper.queryByDoctorIdAndType(params);
			vo.setPrice("免费");
			if (pack != null && pack.getPrice() > 0) {
				vo.setPrice((pack.getPrice()/100) + "元/" + (pack.getTimeLimit() == null ? "15" : pack.getTimeLimit()) +"分钟");
			}
		}

		return vos;
	}


}
