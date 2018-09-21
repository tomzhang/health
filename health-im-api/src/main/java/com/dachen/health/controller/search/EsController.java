package com.dachen.health.controller.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.handler.param.BoolSearchParam;
import com.dachen.elasticsearch.handler.param.SearchParam;
import com.dachen.elasticsearch.model.BaseInfo;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsDoctor;
import com.dachen.elasticsearch.model.EsGroup;
import com.dachen.elasticsearch.util.TypeDefine.Constants;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;

@RestController
@RequestMapping("/es")
public class EsController {
	@Autowired
	private DiseaseTypeRepository diseaseTypeRepository;
	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	
	@Autowired
	private UserManager userManager;
	
	@RequestMapping("/search")
	public JSONMessage search(String key,String type,Integer from,Integer size) {
		String[]types=null;
		if(StringUtils.isBlank(type)){
			types = new String[]{Constants.TYPE_DOCTOR,Constants.TYPE_GROUP};
		}else{
			types = new String[]{type};
		}
		
		SearchParam searchParam = 
				new SearchParam.Builder(Constants.INDEX_HEALTH)
								.searchKey(key)
								.type(types)
								.from(from==null?0:from)
								.size(size==null?100:size)
								.build();
		Object obj = ElasticSearchFactory.getInstance().searchAndReturnDocument(searchParam);
		return JSONMessage.success(obj);
	}
	
	@RequestMapping("/boolSearch")
	public JSONMessage boolSearch(String key,String groupId,Integer from,Integer size) {
		BoolSearchParam searchParam = 
				new BoolSearchParam.Builder(Constants.INDEX_HEALTH)
								.type(Constants.TYPE_DOCTOR)
								.addFilter("groupId", groupId)
								.searchKey(key)
								.from(from==null?0:from)
								.size(size==null?100:size)
								.build();
		Object obj = ElasticSearchFactory.getInstance().boolSearchAndReturnDocument(searchParam);
		return JSONMessage.success(obj);
	}
	@RequestMapping("/rebuild")
	public JSONMessage rebuild(String type) {
		List<? extends BaseInfo> initDataList = null;
//		if(StringUtils.equals(type, Constants.TYPE_GROUP)){
//			initDataList = getInitGroup();
//		}else if(StringUtils.equals(type, Constants.TYPE_DOCTOR)){
//			initDataList = getInitDoctor();
//		}else{
//			
//		}
		ElasticSearchFactory.getInstance().rebuildIndex(Constants.INDEX_HEALTH);
		
		initDataList = getInitGroup();
		if(initDataList.size()>0){
			ElasticSearchFactory.getInstance().batchSaveDocument(initDataList);
		}
		
		initDataList = getInitDoctor();
		if(initDataList.size()>0){
			ElasticSearchFactory.getInstance().batchSaveDocument(initDataList);
		}
		
		initDataList = getInitDiseaseType();
		if(initDataList.size()>0){
			ElasticSearchFactory.getInstance().batchSaveDocument(initDataList);
		}
		return JSONMessage.success("");
	}
	
	@RequestMapping("/initByType")
	public JSONMessage initByType(String type) {
		List<? extends BaseInfo> initDataList = null;
		if(StringUtils.equals(type, Constants.TYPE_GROUP)){
			initDataList = getInitGroup();
		}else if(StringUtils.equals(type, Constants.TYPE_DOCTOR)){
			initDataList = getInitDoctor();
		}else if(StringUtils.equals(type, Constants.TYPE_DISEASE)){
			initDataList = getInitDiseaseType();
		}
		if(initDataList.size()>0){
			ElasticSearchFactory.getInstance().batchSaveDocument(initDataList);
		}
		return JSONMessage.success("");
	}
	@RequestMapping("/resave")
	public JSONMessage resave(String type,String bizId) {
		if(StringUtils.isBlank(type) || StringUtils.isBlank(bizId)){
			return JSONMessage.failure("参数错误");
		}
		if(StringUtils.equals(type, Constants.TYPE_GROUP)){
			ElasticSearchFactory.getInstance().deleteDocument(new EsGroup(bizId));
			
			Group group = groupDao.getById(bizId);
			EsGroup esGroup = new EsGroup(bizId);
			if(group!=null){
				esGroup.setName(group.getName());
				esGroup.setExpertise(getEsDiseaseTypeList(group.getDiseaselist()));
				ElasticSearchFactory.getInstance().insertDocument(esGroup);
			}
			return JSONMessage.success(esGroup);
		}else if(StringUtils.equals(type, Constants.TYPE_DOCTOR)){
			ElasticSearchFactory.getInstance().deleteDocument(new EsDoctor(bizId));
			
			User user = userManager.getUser(Integer.valueOf(bizId));
			EsDoctor doctor = new EsDoctor(bizId);
			if(user!=null && user.getDoctor()!=null){
				doctor.setDepartments(user.getDoctor().getDepartments());
				doctor.setName(user.getName());
				doctor.setSkill(user.getDoctor().getSkill());
				doctor.setStatus(user.getStatus());
				doctor.setExpertise(getEsDiseaseTypeList(user.getDoctor().getExpertise()));
				List<GroupDoctor> groupList = groupDoctorDao.getByDoctorId(user.getUserId());
				for(GroupDoctor group:groupList){
					doctor.addGroupId(group.getGroupId());
				}
				ElasticSearchFactory.getInstance().insertDocument(doctor);
			}
			return JSONMessage.success(doctor);
		}
		return JSONMessage.success("");
	}
	
	private List<? extends BaseInfo> getInitDoctor() {
		List<User>doctorList= userManager.getNormalUser(3);
		if(doctorList.size()==0){
			return null;
		}
		List<EsDoctor>initDataList = new ArrayList<EsDoctor>(doctorList.size());
		EsDoctor doctor = null;
		for(User user:doctorList){
			doctor = new EsDoctor(String.valueOf(user.getUserId()));
			doctor.setDepartments(user.getDoctor().getDepartments());
			doctor.setName(user.getName());
			doctor.setSkill(user.getDoctor().getSkill());
			doctor.setStatus(user.getStatus());
			doctor.setExpertise(getEsDiseaseTypeList(user.getDoctor().getExpertise()));
			List<GroupDoctor> groupList = groupDoctorDao.getByDoctorId(user.getUserId());
			for(GroupDoctor group:groupList){
				doctor.addGroupId(group.getGroupId());
			}
			initDataList.add(doctor);
		}
		return initDataList;
	}
	
	private List<? extends BaseInfo> getInitGroup() {
		//目前按照产品的要求只同步集团数据 暂时不管医院的（BUG号：XGSF-5089）
		List<Group>groupList = groupDao.getAllGroupForEs();
		List<EsGroup>initDataList = new ArrayList<EsGroup>(groupList.size());
		
		EsGroup esGroup = null;
		for(Group group:groupList){
			esGroup = new EsGroup(group.getId());
			esGroup.setName(group.getName());
			esGroup.setExpertise(getEsDiseaseTypeList(group.getDiseaselist()));
			
			initDataList.add(esGroup);
		}
		return initDataList;
	}
	
	private List<EsDiseaseType>getEsDiseaseTypeList(List<String> diseaseIds)
	{
        if(diseaseIds==null || diseaseIds.size()==0){
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
	
	private List<? extends BaseInfo>getInitDiseaseType(){
		List<DiseaseType> diseases = diseaseTypeRepository.createQuery().asList();
		 List<EsDiseaseType>diseaseTypeList=new ArrayList<EsDiseaseType>(diseases.size());
		 for(DiseaseType type:diseases){
			 if (StringUtils.isNotEmpty(type.getRemark()) && !StringUtils.equals("/", type.getRemark())) {
				 EsDiseaseType esTyp = new EsDiseaseType(type.getName(),type.getAlias(),type.getRemark());
				 esTyp.setBizId(type.getId());
	        	 diseaseTypeList.add(esTyp);//TODO remark
			}
        }
        return diseaseTypeList;
	}
}
