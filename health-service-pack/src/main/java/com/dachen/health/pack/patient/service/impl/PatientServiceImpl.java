package com.dachen.health.pack.patient.service.impl;

import java.util.*;

import javax.annotation.Resource;

import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.stat.dao.IAssessStatDao;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.patient.mapper.PatientDiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.model.PatientDisease;
import com.dachen.health.pack.patient.model.PatientDiseaseParam;
import com.dachen.health.pack.patient.model.PatientExample;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * 
 * ProjectName： health-service-pack<br>
 * ClassName： PatientServiceImpl<br>
 * Description： <br>
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@Service
public class PatientServiceImpl extends BaseServiceImpl<Patient, Integer> implements IPatientService {
	
	@Resource
	PatientMapper mapper;
	
	@Resource
	OrderMapper orderMapper;
	
	@Resource
	CheckInMapper checkInMapper;
	
	@Resource
	UserRepository userRepository;
	
	@Autowired
    IBaseUserDao baseUserDao;
	
	@Autowired
	IAssessStatDao assessStatDao;
	
	@Autowired
	PatientDiseaseMapper patientDiseaseMapper;

	private final static Object obj = new Object();
	
	@Override
	public int save(Patient intance) {
		intance.setTopPath(PropertiesUtil.removeUrlPrefix(intance.getTopPath()));
		/**
		 * 获取当前用户的所有患者名称
		 */
		String name = intance.getUserName();
		if(StringUtils.isBlank(name)){
			throw new ServiceException("患者姓名不能为空");
		} 
		synchronized (obj) {
			List<String> list = mapper.getAllPatientNames(intance.getUserId());	
			if(list != null && list.contains(name.trim())){
				throw new ServiceException("该患者已添加");
			}
			intance.setUserName(name.trim());
			int ret = mapper.insert(intance);
			return ret;
		}
	}
   
	
	
	@Override
	public int update(Patient intance) throws HttpApiException {
		intance.setTopPath(intance.getTopPath());
		int pk = mapper.updateByPrimaryKeySelective(intance);
		if (pk > 0 && SysConstants.ONESELF.equals(intance.getRelation())) {
			if (intance.getUserId() == null) {
				throw new ServiceException("参数有误：用户ID为空！");
			}
			UserExample example = new UserExample();
			example.setName(intance.getUserName());
			example.setSex(intance.getSex().intValue());
			example.setBirthday(intance.getBirthday());
			example.setAge(intance.getAge());
			example.setArea(intance.getArea());

			userRepository.updateUser(intance.getUserId(), example);
			userRepository.setHeaderPicName(intance.getUserId(), intance.getTopPath());
		}
		return pk;
	}

	@Override
	public int deleteByPK(Integer pk) {
		return mapper.deleteByPrimaryKey(pk);
	}
	
	public boolean existsBizData(Integer pk) {
		return orderMapper.countByPatientId(pk) + checkInMapper.countByPatientId(pk) > 0;
	}

	@Override
	public Patient findByPk(Integer pk) {
		return mapper.selectByPrimaryKey(pk);
	}
	
	@Override
	public List<Patient> findByIds(List<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		return mapper.findByIds(ids);
	}

	@Override
	public List<Patient> findByCreateUser(int createUserId) {
		PatientExample example=new PatientExample();
		example.createCriteria().andUserIdEqualTo(createUserId);
		List<Patient> patients = mapper.selectByExample(example);
		return patients;
	}
	
	public Patient save(Integer userId) {
		User user = userRepository.getUser(userId);
		return save(user);
	}
	
	@Override
	public Patient save(User user) {
		Patient patient = findOne(user.getUserId(), SysConstants.ONESELF);
		if (patient == null) {
			patient = new Patient();
		}
		patient.setUserName(user.getName());
		patient.setSex(user.getSex() == null ? null : user.getSex().shortValue());
		patient.setBirthday(user.getBirthday() == null ? null : user.getBirthday());
		patient.setRelation(SysConstants.ONESELF);
		patient.setArea(user.getArea());
		patient.setUserId(user.getUserId());
		patient.setTelephone(user.getTelephone());
		patient.setTopPath(user.getHeadPicFileName());
		if (patient.getId() == null) {
			mapper.insert(patient);
		} else {
			mapper.updateByPrimaryKeySelective(patient);
		}
		return patient;
	}
	
	public void updateTopPath(Integer userId, String topPath) {
		Patient patient = findOne(userId, SysConstants.ONESELF);
		patient.setTopPath(topPath);
		mapper.updateByPrimaryKeySelective(patient);
	}
	
	@Override
	public Patient findOne(Integer createUserId, String relation) {
		List<Patient> patients = findByCreateUser(createUserId);
		for (Patient patient : patients) {
			if (relation.equals(patient.getRelation())) {
				patient.setTopPath(PropertiesUtil.addUrlPrefix(patient.getTopPath()));
				return patient;
			} 
		}
		return null;
	}

	@Override
	public List<Integer> getPatientIdsByUserId(int userId) {
		List<Integer> list = mapper.getPatientIdsByUserId(userId);
		if(list == null || list.size() < 1)list = null;
		return list;
	}



	@Override
	public List<Patient> getPatientsByFuzzyName(String name) {
		PatientExample example = new PatientExample();
		example.createCriteria().andUserNameLike("%"+name+"%");
		return mapper.selectByExample(example);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.patient.service.IPatientService#getPatientNameByPatientId(java.lang.Integer)
	 */
	@Override
	public String getPatientNameByPatientId(Integer id){
		//参数校验
		if(id == null || id == 0){
			throw new ServiceException("患者id不能为空");
		}
		Patient patient = mapper.selectByPrimaryKey(id);
		if(patient == null){
			return null;
		}else{
			return patient.getUserName();
		}
		
	}



	@Override
	public Map<String,Object> checkIdCard(int createUserId,String idcard,Integer id) {
		//boolean flag=userRepository.checkIdCard(idcard,idtype, UserEnum.UserType.patient.getIndex());
		if(idcard==null){
			throw new ServiceException("证件号码不能为空");
		}
		PatientExample example=new PatientExample();
		if(id!=null&&id!=0){
			example.createCriteria().andUserIdEqualTo(createUserId).andIdcardEqualTo(idcard).andIdNotEqualTo(id);
		}else{
			example.createCriteria().andUserIdEqualTo(createUserId).andIdcardEqualTo(idcard);
		}
		
		boolean falg=false;
		List<Patient> patients = mapper.selectByExample(example);
		if(patients!=null&&patients.size()>0){
			falg=true;
		}
		Map<String,Object> map=Maps.newHashMap();
		map.put("is_repeat", falg);
		map.put("idcard", idcard);
		return map;
	}



	@Override
	public List<?> getPatientsByTelephone(String telephone) {
		User u = userRepository.getUserByTelAndType(telephone,UserType.patient.getIndex());
		if(u != null){
			Integer userId = u.getUserId();
			List<Patient> patients = findByCreateUser(userId);
			return patients;
		}
		return null;
	}

	@Override
	public PageVO statPatient(StatParam param) {
		if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        String[] statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex()};
        if (!param.isShowOnJob()){
        	statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex(), 
                    GroupEnum.GroupDoctorStatus.离职.getIndex(), GroupEnum.GroupDoctorStatus.踢出.getIndex()};
        }
        param.setStatuses(statuses);
        if (param.getDoctorId() == null) {
            // 按集团查询
            return statPatientByGroup(param);
        } else {
            // 按医生查询
            return statPatientByDoctor(param);
        }
	}

	private PageVO statPatientByDoctor(StatParam param) {
		//1、先根据医生id和集团id，在t_patient_disease表中查找患者id
    	PatientDiseaseParam patientDiseaseParam = new PatientDiseaseParam();
    	patientDiseaseParam.setGroupId(param.getGroupId());
    	patientDiseaseParam.setDoctorId(param.getDoctorId());
		List<Integer> patientIdList = patientDiseaseMapper.findPatientIdByDoctorAndGroup(patientDiseaseParam);
		//1.1、再过滤掉为激活的患者id
		//再过滤掉患者id中的未激活患者
    	patientIdList = baseUserDao.filterInactivePatientIds(patientIdList);
		//2、组装查询条件，去u_doctor_patient中查询
    	DBObject query = new BasicDBObject();
        query.put("userId", param.getDoctorId());
        query.put("toUserId", new BasicDBObject("$in",patientIdList));
        
        
        //时间查询条件
        if(param.getStartTime()!=null || param.getEndTime()!=null){
            DBObject matchTime = new BasicDBObject();
            if(param.getStartTime()!=null){
                matchTime.put("$gte", param.getStartTime());
            }
            if(param.getEndTime()!=null){
                matchTime.put("$lte", param.getEndTime());
            }
            query.put("createTime", matchTime);
        }
        
        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("toUserId", 1);
        project.put("createTime", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("createTime", -1);
        
        DBCollection collection = assessStatDao.getDBCollection("u_doctor_patient");
        
        List<StatVO> list = new ArrayList<StatVO>();
        List<Integer> userIds = new ArrayList<Integer>();
        
        DBCursor cursor = collection.find(query,project).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "toUserId"));
            vo.setTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "createTime"), null));

            userIds.add(vo.getId());

            list.add(vo);
        }
        
        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:list){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setTelephone(user.getTelephone());
                        stat.setSex(user.getSex());
                        stat.setAge(user.getAge());
                        stat.setAgeStr(user.getAgeStr());
                        stat.setHeadPicFileName(user.getHeadPicFileName());
                        stat.setStatus(user.getStatus());// 状态
                        UserStatus userStatus = UserEnum.UserStatus.getEnum(user.getStatus());
						stat.setStatusName(userStatus != null ? userStatus.getTitle() : ""); // 状态名称
                        break;
                    }
                }
            }
            
        }
        
        //构造分页
        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(collection.count(query));
        
        return page;
    	
	}

	private PageVO statPatientByGroup(StatParam param) {
		//1、先查找集团所有的医生id
        List<Integer> doctorIds = baseUserDao.getDoctorIdByGroup(param.getGroupId(), param.getStatuses(),param.getKeyword());
        //1.1、查询所有医生相关的患者id
    	List<Integer> patientIdList = baseUserDao.getUserPatientIdByDoctorIds(doctorIds);
    	//再过滤掉患者id中的未激活患者
    	patientIdList = baseUserDao.filterInactivePatientIds(patientIdList);
    	
    	PatientDiseaseParam patientDiseaseParam = new PatientDiseaseParam();
    	patientDiseaseParam.setGroupId(param.getGroupId());
    	patientDiseaseParam.setDoctorIds(doctorIds);
    	patientDiseaseParam.setUserIds(patientIdList);
    	
        //2、在MySQL库t_patient_disease表中查询对应的结果集
    	List<Map<String, Object>> patientDiseases = patientDiseaseMapper.findByParam(patientDiseaseParam);
    	List<StatVO> list = Lists.newArrayList();
    	if (patientDiseases != null && patientDiseases.size() > 0) {
    		patientDiseases.forEach((result) -> {
    			StatVO statVO = new StatVO();
    			Integer doctorId = (Integer) result.get("doctor_id");
    			Long count = (Long) result.get("count");
    			statVO.setId(doctorId);
    			statVO.setValue(count.intValue());
    			list.add(statVO);
    		});
		}
    	
    	 //手动构造分页
        this.buildPage(doctorIds, list);
        List<Integer> userIds = new ArrayList<Integer>();//需返回的数据的id
        List<StatVO> newList = new ArrayList<StatVO>();//需返回的数据
        
        int start = param.getStart();
        int end = param.getPageSize()+param.getStart();
        if(start<=end){
            if(end>list.size()){
                end = list.size();
            }
            for(int i = start;i < end;i++){
                newList.add(list.get(i));
                userIds.add(list.get(i).getId());
            }
        }

        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:newList){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setHospital(user.getHospital());
                        stat.setDepartments(user.getDepartments());
                        stat.setTitle(user.getTitle());
                        stat.setAge(user.getAge());
                        stat.setAgeStr(user.getAgeStr());
                        stat.setSex(user.getSex());
                        stat.setHeadPicFileName(user.getHeadPicFileName());
                        break;
                    }
                }
            }
            
        }
        
        //构造分页
        PageVO page = new PageVO();
        page.setPageData(newList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.valueOf(doctorIds.size())); 
        
        return page;
	}
		
	/**
     * </p>手动构造分页</p>
     * @param doctorIds 所有医生id
     * @param list 待构造的分页
     * @author fanp
     * @date 2015年9月23日
     */
    private void buildPage(List<Integer> doctorIds,List<StatVO> list){
        List<Integer> listIds = new ArrayList<Integer>();
        List<StatVO> remainList = new ArrayList<StatVO>();
        for(StatVO vo : list){
            listIds.add(vo.getId());
        }
        //查找出list中不存在的id
        for(Integer ids:doctorIds){
            int place = this.binSearch(listIds, ids);
            if(place == -1){
                StatVO vo = new StatVO();
                vo.setId(ids);
                vo.setValue(0);
                remainList.add(vo);
                
            }
        }
        list.addAll(remainList);
    }
    
    // 二分法查找id所在位置，list经过排序
    private int binSearch(List<Integer> listIds, int ids) {
        Integer[] sortIds = listIds.toArray(new Integer[]{});
        Arrays.sort(sortIds);
        
        int mid = sortIds.length / 2;

        int start = 0;
        int end = sortIds.length - 1;
        while (start <= end) {
            mid = (end - start) / 2 + start;
            Integer id = sortIds[mid];
            if (ids == id) {
                return mid;
            } else if (ids <= id) {
                end = mid - 1;
            } else if (ids >= id) {
                start = mid + 1;
            }
        }
        return -1;
    }
}
