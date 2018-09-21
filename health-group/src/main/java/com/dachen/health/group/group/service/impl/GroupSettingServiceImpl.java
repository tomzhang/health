package com.dachen.health.group.group.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.exception.ServiceException;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IOperateRecordDao;
import com.dachen.health.group.group.entity.param.GroupSettingParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupSettingService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.system.dao.mongo.DoctorCheckDaoImpl;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 医生集团设置
 * @author xie pei
 * @date 2015/9/28
 */
@Service
public class GroupSettingServiceImpl implements IGroupSettingService {

	@Autowired
    protected IGroupDao groupDao;
	
	@Autowired
    protected IGroupDoctorDao gdocDao;
	
	@Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;
	
	@Autowired
    protected IBusinessServiceMsg businessService;

	@Autowired
    protected IOperateRecordDao recordDao;
	
	public void setSpecialty(GroupSettingParam param) {
		if(StringUtil.isBlank(param.getDocGroupId())) {
			throw new ServiceException("医生集团ID不能为空!");
		}
		
		if(param.getSpecialtyIds()==null) {
			param.setSpecialtyIds(new ArrayList<String>());
			//throw new ServiceException("专长ID不能为空!");
		}
		
		//List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(param.getSpecialtyIds());
		Group group = new Group();
		/*List<String> objs = new ArrayList<String>();
		if(diseaseTypes!=null && diseaseTypes.size()>0) {
			for(DiseaseType diseaseType:diseaseTypes) {
				objs.add(diseaseType.getId());
			}
		}*/

		//保存修改记录
		Group find = groupDao.getById(param.getDocGroupId());
		if (null != find) {

			List<String> diseases = find.getDiseaselist();
			String oldDiseases = JSON.toJSONString(diseases);
			String newDiseases = JSON.toJSONString(param.getSpecialtyIds());

			if (!oldDiseases.equals(newDiseases)) {

				String content = "集团擅长：由【" + getDiseaseNameByIds(diseases) + "】改为【" + getDiseaseNameByIds(param.getSpecialtyIds()) + "】";
				OperationRecord record = new OperationRecord();
				record.setContent(content);
				record.setCreator(ReqUtil.instance.getUserId());
				record.setCreateTime(System.currentTimeMillis());
				record.setObjectId(param.getDocGroupId());
				recordDao.save(record);
			}
		}

		group.setDiseaselist(param.getSpecialtyIds());
		group.setId(param.getDocGroupId());
		group=groupDao.update(group);
		//设置之后更新es
		EcEvent event = EcEvent.build(EventType.GroupInfoUpdateForEs)
				.param("bizid",group.getId())
				.param("name", group.getName())
				.param("diseaselist",getEsDiseaseTypeList(group.getDiseaselist()));
		EventProducer.fireEvent(event);
	}

	private List<EsDiseaseType> getEsDiseaseTypeList(List<String> diseaseIds){
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

	public void setWeights(GroupSettingParam param) {
		if(StringUtil.isBlank(param.getDocGroupId())) {
			throw new ServiceException("医生集团ID不能为空!");
		}
		if(param.getWeight()==null) {
			throw new ServiceException("权重数不能为空 !");
		}
		Group group = new Group();
		group.setWeight(param.getWeight());
		group.setId(param.getDocGroupId());
		groupDao.update(group);
	}

	public void setResExpert(GroupSettingParam param) {
		
		if(StringUtil.isBlank(param.getDocGroupId())) {
			throw new ServiceException("医生集团ID不能为空!");
		}
		
		if(param.getExpertIds()==null) {
			//throw new ServiceException("设置预约专家不能为空!");
			param.setExpertIds(new ArrayList<Integer>());
		}
		
		Group group = new Group();
		group.setExpertIds(param.getExpertIds());
		group.setId(param.getDocGroupId());
		groupDao.update(group);
	}
	
	public void setMsgDisturb(GroupSettingParam param) {
		
		if(StringUtil.isBlank(param.getDocGroupId())) {
			throw new ServiceException("医生集团ID不能为空!");
		}
		
		if(param.getDoctorId()==null || param.getDoctorId()==0) {
			throw new ServiceException("集团医生ID不能为空!");
		}
		
		if(StringUtil.isBlank(param.getIsOpenMsg()) ||(!"1".equals(param.getIsOpenMsg()) && !"2".equals(param.getIsOpenMsg()))) {
			throw new ServiceException("请设置正确的免打扰状态！");
		}
		
		GroupDoctor groupDoc = new GroupDoctor();
		groupDoc.setDoctorId(param.getDoctorId());
		groupDoc.setGroupId(param.getDocGroupId());
		groupDoc.setTroubleFree(param.getIsOpenMsg());
		gdocDao.updateStatus(groupDoc);
	}
	
	/* 
	 * 更新值班时间段
	 */
	@Override
	public boolean updateDutyTime(String groupId, String dutyStartTime, String dutyEndTime) {

		// TODO 要判断dutyStartTime和dutyEndTime是否符合??:??这种格式。
		// dutyEndTime的时间要大于dutyStartTime

		if (StringUtils.isEmpty(groupId)) {
			return false;
		}

		if (StringUtils.isEmpty(dutyStartTime)) {
			return false;
		}

		if (StringUtils.isEmpty(dutyEndTime)) {
			return false;
		}
		
		Group group = groupDao.getById(groupId);
		if (group.getConfig() != null && group.getConfig().getDutyStartTime() != null
				&& group.getConfig().getDutyEndTime() != null
				&& group.getConfig().getDutyStartTime().equals(dutyStartTime)
				&& group.getConfig().getDutyEndTime().equals(dutyEndTime)) {
			return true;
		}

		// 操作数据库
		boolean writeResult = groupDao.updateDutyTime(groupId, dutyStartTime, dutyEndTime);
		
		businessService.groupDutyTimeChangeNotify(groupId);
		
		return writeResult;
	}


	private String getDiseaseNameByIds(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if (!CollectionUtils.isEmpty(list)) {
			List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(list);
			for (int i = 0; i < diseaseTypes.size(); i++) {
				sb.append(diseaseTypes.get(i).getName());
				if (diseaseTypes.size() != (i+1)) {
					sb.append("、 ");
				}
			}
		}else {
			return "空";
		}

		return sb.toString();
	}

}
