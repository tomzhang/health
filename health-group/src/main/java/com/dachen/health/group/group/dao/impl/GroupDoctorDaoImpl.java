package com.dachen.health.group.group.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.common.helper.BeanUtils;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupCertStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentMobileVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.param.GroupDoctorApplyParam;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.param.GroupsParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.*;
import com.dachen.util.BusinessUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Repository
public class GroupDoctorDaoImpl extends NoSqlRepository implements IGroupDoctorDao {

	@Autowired
    protected  UserRepository userRepository;

	@Resource
    protected  IDepartmentDao departmentDao;

	@Autowired
    protected  DiseaseTypeRepository diseaseTypeRepository;

	@Autowired
    protected IGroupDao groupDao;

	@Autowired
    protected IBaseDataDao baseDataDao;

	@Override
	public GroupDoctor save(GroupDoctor gdoc) {

		// 查找邀请关系树路径
		if (gdoc.getReferenceId() == 0) {
			gdoc.setTreePath("/" + gdoc.getDoctorId() + "/");
		} else {
			GroupDoctor param = new GroupDoctor();
			param.setDoctorId(gdoc.getReferenceId());
			param.setGroupId(gdoc.getGroupId());

			GroupDoctor doctor = this.getById(param);
			if (doctor == null) {
				gdoc.setTreePath("/" + gdoc.getReferenceId() + "/" + gdoc.getDoctorId() + "/");
				gdoc.setReferenceId(0);// 邀请人不在医生集团，则为根节点
			} else {
				gdoc.setTreePath(doctor.getTreePath() + "/" + gdoc.getDoctorId() + "/");
			}
		}
		// 设置默认值班价格 100分
		if (gdoc.getOutpatientPrice() == null || gdoc.getOutpatientPrice() == 0) {
			gdoc.setOutpatientPrice(100);
		}
		User user = userRepository.getUser(gdoc.getDoctorId());
		if (Objects.nonNull(user) && Objects.nonNull(user.getDoctor()) && Objects.nonNull(user.getDoctor().getDepartments())) {
			String deptName = user.getDoctor().getDepartments();
			// 从医生身上找到科室来冗余到集团医生表上
			gdoc.setDeptName(deptName);
		}
		Object id = dsForRW.insert(gdoc).getId();
		gdoc.setId(id.toString());

		return gdoc;
	}

	@Override
	public GroupDoctor update(GroupDoctor gdoc) {

		DBObject update = new BasicDBObject();
		if (!StringUtil.isEmpty(gdoc.getStatus())) {
			update.put("status", gdoc.getStatus());
		}

		update.put("contactWay", gdoc.getContactWay());

		update.put("remarks", gdoc.getRemarks());

		if (null != gdoc.getReferenceId()) {
			update.put("referenceId", gdoc.getReferenceId());
		}
		if (StringUtil.isNotBlank(gdoc.getTreePath())) {
			update.put("treePath", gdoc.getTreePath());
		}
		if (StringUtil.isNotBlank(gdoc.getOnLineState())) {
			update.put("onLineState", gdoc.getOnLineState());
		}
		if (gdoc.getDutyDuration() != null) {
			update.put("dutyDuration", gdoc.getDutyDuration());
		}
		if (null != gdoc.getTaskDuration()) {
			update.put("taskDuration", gdoc.getTaskDuration());
		}
		if (null != gdoc.getOutpatientPrice()) {
			update.put("outpatientPrice", gdoc.getOutpatientPrice());
		}
		if (StringUtil.isNotBlank(gdoc.getTroubleFree())) {
			update.put("troubleFree", gdoc.getTroubleFree());
		}
		if (gdoc.getOnLineTime() != null) {
			update.put("onLineTime", gdoc.getOnLineTime());
		}

		if (gdoc.getOffLineTime() != null) {
			update.put("offLineTime", gdoc.getOffLineTime());
		}

		if (gdoc.getApplyMsg() != null) {
			update.put("applyMsg", gdoc.getApplyMsg());
		}

		update.put("updator", gdoc.getUpdator());
		update.put("updatorDate", new Date().getTime());
		update.put("isMain", gdoc.isMain());

		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(gdoc.getId()));
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update));
		return gdoc;
	}

	@Override
	public boolean updateStatus(GroupDoctor gdoc) {

		DBObject update = new BasicDBObject();
		if (!StringUtil.isEmpty(gdoc.getStatus())) {
			update.put("status", gdoc.getStatus());
		}

		if (!StringUtil.isEmpty(gdoc.getTroubleFree())) {
			update.put("troubleFree", gdoc.getTroubleFree());
		}
		if (!StringUtil.isEmpty(gdoc.getOnLineState())) {
			update.put("onLineState", gdoc.getOnLineState());
		}
		update.put("taskDuration", 0);

		DBObject query = new BasicDBObject();
		query.put("groupId", gdoc.getGroupId());
		query.put("doctorId", gdoc.getDoctorId());
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update));
		return true;
	}

	public boolean updateTaskDuration(String groupId) {
		DBObject update = new BasicDBObject();
		update.put("taskDuration", 0);

		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update), false, true);
		return true;
	}

	@Override
	public boolean updateOutpatientPrice(String groupId, Integer outpatientPrice) {

		DBObject update = new BasicDBObject();
		update.put("outpatientPrice", outpatientPrice);

		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		dsForRW.getDB().getCollection("c_group_doctor").updateMulti(query, new BasicDBObject("$set", update));
		return true;
	}

	@Override
	public boolean delete(String... ids) {
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String id : ids) {
			values.add(new ObjectId(id));
		}
		in.put("$in", values);

		dsForRW.getDB().getCollection("c_group_doctor").remove(new BasicDBObject("_id", in));
		return true;
	}

	@Override
	public PageVO search(GroupDoctorParam param) {

		DBObject query = new BasicDBObject();

		if (!StringUtil.isEmpty(param.getGroupId())) {
			query.put("groupId", param.getGroupId());
		}

		if (!StringUtil.isEmpty(param.getStatus())) {
			query.put("status", param.getStatus());
		}

		if (!StringUtil.isEmpty(param.getOnLineState())) { 
			query.put("onLineState", param.getOnLineState());
		}

		if (param.getStartTime() != null || param.getEndTime() != null) {
			DBObject timeQuery = new BasicDBObject();
			if (param.getStartTime() != null) {
				timeQuery.put("$gte", param.getStartTime());
			}
			if (param.getEndTime() != null) {
				timeQuery.put("$lt", param.getEndTime());
			}
			query.put("creatorDate", timeQuery);
		}
		
		//根据集团id，获取到该集团所有的doctorId
		List<GroupDoctor> groupDoctors = findDoctorsByGroupIdAllStatus(param.getGroupId());
		List<Integer> doctorIds = Lists.newArrayList();
		if (groupDoctors != null && groupDoctors.size() > 0) {
			for (GroupDoctor groupDoctor : groupDoctors) {
				doctorIds.add(groupDoctor.getDoctorId());
			}
		}
		
		if (StringUtils.isNotEmpty(param.getKeyword())) {
			
			// 根据集团医生id列表和关键字查询出符合条件的医生
			DBObject queryUser = new BasicDBObject();
			
			BasicDBList userIdList = new BasicDBList();
			userIdList.addAll(doctorIds);
			
			queryUser.put("_id", new BasicDBObject("$in", userIdList));
			if (null != param.getKeyword()) {
				BasicDBList keyword = new BasicDBList();
				Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
				keyword.add(new BasicDBObject("name", pattern));
				keyword.add(new BasicDBObject("telephone", pattern));
				keyword.add(new BasicDBObject("doctor.title", pattern));
				queryUser.put(QueryOperators.OR, keyword);
			}
			DBCursor uCursor = dsForRW.getDB().getCollection("user").find(queryUser);
			BasicDBList userIdsIn = new BasicDBList();
			while (uCursor.hasNext()) {
				DBObject object = uCursor.next();
				userIdsIn.add(MongodbUtil.getInteger(object, "_id"));
			}
			
			DBObject doctorIdQuery = new BasicDBObject();
			doctorIdQuery.put("$in", userIdsIn);
			query.put("doctorId", doctorIdQuery);
			
		}
		
		
		if(StringUtils.isNotEmpty(param.getDeptId())) {
			BasicDBList userIdsIn = new BasicDBList();
			
			if(param.getDeptId().equalsIgnoreCase("Undefined")) {
				
				List<User> users = userRepository.getDoctorsByIds(doctorIds);
				
				for (User user : users) {
					if (user.getDoctor() == null || StringUtils.isEmpty(user.getDoctor().getDeptId())) {
						userIdsIn.add(user.getUserId());
					}
				}
				
			}else {
				//2、根据deptId在b_hospitaldept表中查询所有子节点
				com.dachen.health.base.entity.vo.DepartmentVO departmentVO = baseDataDao.getDepartmentById(param.getDeptId());
				List<String> deptIds = null;
				if (departmentVO.getIsLeaf() == 1) {
					deptIds = Lists.newArrayList();
					deptIds.add(param.getDeptId());
				} else {
					//不是叶子节点，则递归获取其叶子节点
					List<com.dachen.health.base.entity.vo.DepartmentVO> departmentVOs = baseDataDao.getAllDepartments();
					Set<String> tempDetpIds = Sets.newHashSet(); 
					parseDepartmentIds(departmentVOs, param.getDeptId(), tempDetpIds);
					deptIds = new ArrayList<>(tempDetpIds);
				}
				//3、根据doctorId和第2步获取的deptId查询User表，获取用户的id
				List<Integer> usersid = userRepository.findByIdsAndDeptIds(doctorIds, deptIds);
				//4、将第3部获取的id组装成query的一个in条件
				if (usersid != null && usersid.size() > 0) {
					for (Integer id : usersid) {
						userIdsIn.add(id);
					}
				}
			}
			DBObject doctorIdQuery = new BasicDBObject();
			doctorIdQuery.put("$in", userIdsIn);
			query.put("doctorId", doctorIdQuery);
		}
		
		//区域未分配
		if (param.getAreaId()!=null && param.getAreaId()[0].equalsIgnoreCase("Undefined")) {
			List<Integer> usersid = userRepository.findNoLoctionDoctorsByIds(doctorIds);
			BasicDBList userIdsIn = new BasicDBList();
			if (usersid != null && usersid.size() > 0) {
				for (Integer id : usersid) {
					userIdsIn.add(id);
				}
			}
			DBObject doctorIdQuery = new BasicDBObject();
			doctorIdQuery.put("$in", userIdsIn);
			query.put("doctorId", doctorIdQuery);
			param.setAreaId(null);
		}
		
		//将区域ID拆分成省市区三级
		if (param.getAreaId()!=null && param.getAreaId().length > 0) {
			
			String range = "";
			String locId = "";

			
			if (param.getAreaId().length == 1) {
				locId = param.getAreaId()[0];
				range = "province";
			} else if (param.getAreaId().length == 2) {
				param.setProvinceId(param.getAreaId()[0]);
				locId = param.getAreaId()[1];
				range = "city";
			} else if (param.getAreaId().length == 3) {
				locId = param.getAreaId()[2];
				range = "country";
			}
			
			//5、根据第1步获取的doctorId和省份id查询User表，获取用户的id
			List<Integer> usersid = userRepository.findByIdsAndProvinceId(doctorIds, locId, range);
			
			//6、将第5步获取的id组装成query的一个in条件
			BasicDBList userIdsIn = new BasicDBList();
			if (usersid != null && usersid.size() > 0) {
				for (Integer id : usersid) {
					userIdsIn.add(id);
				}
			}
			DBObject doctorIdQuery = new BasicDBObject();
			doctorIdQuery.put("$in", userIdsIn);
			query.put("doctorId", doctorIdQuery);
			
		}
		
		if (StringUtils.isNotEmpty(param.getPackId())) {
			
			List<Integer> docIds = param.getHasTypeIds();
			
			BasicDBList userIdsIn = new BasicDBList();
			if (docIds != null && docIds.size() > 0) {
				for (Integer id : docIds) {
					userIdsIn.add(id);
				}
			}
			DBObject doctorIdQuery = new BasicDBObject();
			doctorIdQuery.put("$in", userIdsIn);
			query.put("doctorId", doctorIdQuery);
		}
		
		DBObject sorter = null;
		if (param.getSorter() != null && !param.getSorter().isEmpty()) {
			sorter = new BasicDBObject();
			sorter.putAll(param.getSorter());
		} else {
			sorter = new BasicDBObject("updatorDate", -1);
		}
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor")
				.find(query).sort(sorter).skip(param.getStart())
				.limit(param.getPageSize());
		List<GroupDoctorVO> gList = new ArrayList<GroupDoctorVO>();
		BasicDBList idList = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(MongodbUtil.getString(obj, "_id"));
			gd.setGroupDoctorId(MongodbUtil.getString(obj, "_id"));
			gd.setGroupId(MongodbUtil.getString(obj, "groupId"));
			gd.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			gd.setReferenceId(MongodbUtil.getInteger(obj, "referenceId"));
			gd.setStatus(MongodbUtil.getString(obj, "status"));
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get(
					"recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get(
					"contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks")
					.toString());
			if (null != obj.get("creator")) {
				gd.setCreator(MongodbUtil.getInteger(obj, "creator"));
			}
			if (null != obj.get("updator")) {
				gd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			}
			if (null != obj.get("updatorDate")) {
				gd.setUpdatorDate(Long.valueOf(obj.get("updatorDate")
						.toString()));
			}
			if (null != obj.get("creatorDate")) {
				gd.setCreatorDate(Long.valueOf(obj.get("creatorDate")
						.toString()));
			}
			gList.add(gd);
			idList.add(obj.get("doctorId"));
		}
		
		gList = this.getUserBydoctorIds(gList, idList);

		PageVO page = new PageVO();
		page.setPageData(gList);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(dsForRW.getDB().getCollection("c_group_doctor")
				.count(query));

		return page;

	}
	
	public void parseDepartmentIds(List<com.dachen.health.base.entity.vo.DepartmentVO> allDepts, String deptId, Set<String> deptIds) {
		deptIds.add(deptId);
		if (allDepts != null && allDepts.size() > 0) {
			for(com.dachen.health.base.entity.vo.DepartmentVO departmentVO : allDepts) {
				if (StringUtils.equals(deptId, departmentVO.getParentId())) {
					if (departmentVO.getIsLeaf() == 0) {
						parseDepartmentIds(allDepts, departmentVO.getId(), deptIds);
					} else {
						deptIds.add(departmentVO.getId());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageVO searchDoctorByKeyWord(DepartmentDoctorParam param) {

		// 先根据groupId查询该集团医生
		DBObject query = new BasicDBObject();
		query.put("groupId", param.getGroupId());
		// query.put("status", "C");要求搜索所有状态的的
		if(param.getDoctorStatus()!=null){
			query.put("status", param.getDoctorStatus());
		}
		if (StringUtils.isNotBlank(param.getConsultationPackId())) {
			List<Integer> notInIds = this.getNotInCurrentPackDoctorIds(param.getGroupId(),
					param.getConsultationPackId());
			if (notInIds != null && notInIds.size() > 0) {
				query.put("doctorId", new BasicDBObject("$nin", notInIds));
			}
		}
		DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor").find(query).sort(new BasicDBObject("updatorDate", -1));
		List<DepartmentDoctorVO> ddvoList = new ArrayList<DepartmentDoctorVO>();

		BasicDBList userIdList = new BasicDBList();
		while (docsor.hasNext()) {
			DBObject obj = docsor.next();
			userIdList.add((Integer) obj.get("doctorId"));

			DepartmentDoctorVO ddoc = new DepartmentDoctorVO();
			ddoc.setGroupDoctorId(obj.get("_id").toString());
			ddoc.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			ddoc.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			ddoc.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			ddoc.setGroupId(MongodbUtil.getString(obj, "groupId"));
			ddoc.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
			InviteRelation invite = getInviteInfo(ddoc.getDoctorId(), ddoc.getGroupId());
			if (null != invite) {
				List<?> list = findInviteListById(ddoc.getDoctorId(), ddoc.getGroupId()).getPageData();
				invite.setMyInvite(list == null ? new ArrayList<InviteRelation>() : (List<InviteRelation>) list);

				ddoc.setInviterId(invite.getInviterId());
				ddoc.setInviterName(invite.getName());
			}
			ddoc.setInvite(invite);
			ddvoList.add(ddoc);
		}

		/**
		 * 排除当前会诊包的医生
		 */

		// 根据集团医生id列表和关键字查询出符合条件的医生
		DBObject queryUser = new BasicDBObject();
		queryUser.put("_id", new BasicDBObject("$in", userIdList));
		if (null != param.getKeyword()) {
			BasicDBList keyword = new BasicDBList();
			Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
			keyword.add(new BasicDBObject("name", pattern));
			keyword.add(new BasicDBObject("telephone", pattern));
			keyword.add(new BasicDBObject("doctor.title", pattern));
			queryUser.put(QueryOperators.OR, keyword);
		}
		if(param.getDoctorStatus()!=null){
			queryUser.put("status", UserEnum.UserStatus.normal.getIndex());
		}

		if (StringUtils.isNotBlank(param.getConsultationPackId())) {
			// 过滤出正常用户
			// 针对会诊包过滤掉非主任头衔的医生
			queryUser.put("status", UserEnum.UserStatus.normal.getIndex());
			queryUser.put("doctor.titleRank", new BasicDBObject("$in", new Integer[] { 1, 2 }));
		}

		DBCursor uCursor = dsForRW.getDB().getCollection("user").find(queryUser)
				.sort(new BasicDBObject("modifyTime", -1)).skip(param.getStart()).limit(param.getPageSize());
		List<DoctorBasicInfo> doctorList = new ArrayList<DoctorBasicInfo>();
		BasicDBList userIds = new BasicDBList();
		while (uCursor.hasNext()) {
			DBObject obj = uCursor.next();
			DoctorBasicInfo doctor = new DoctorBasicInfo();
			// obj.get("name");
			doctor.setDoctorId(MongodbUtil.getInteger(obj, "_id"));
			doctor.setName(MongodbUtil.getString(obj, "name"));
			doctor.setTelephone(MongodbUtil.getString(obj, "telephone"));
			doctor.setUserType(MongodbUtil.getInteger(obj, "userType"));
			doctor.setStatus(MongodbUtil.getInteger(obj, "status"));
			doctor.setSex(MongodbUtil.getInteger(obj, "sex"));
			if(MongodbUtil.getLong(obj, "modifyTime")!=null){//兼容BUG修复
				doctor.setModifyTime(MongodbUtil.getLong(obj, "modifyTime"));
			}
			if (obj.get("headPicFileName") == null) {
				doctor.setHeadPicFileName("");
				doctor.setHeadPicFilePath("");
			} else {
				doctor.setHeadPicFileName(obj.get("headPicFileName").toString());
				doctor.setHeadPicFilePath(
						UserHelper.buildHeaderPicPath(doctor.getHeadPicFileName(), doctor.getDoctorId()));
			}

			DBObject doc = (BasicDBObject) obj.get("doctor");
			if (null != doc) {
				doctor.setDoctorNum(MongodbUtil.getString(doc, "doctorNum"));
				doctor.setIntroduction(MongodbUtil.getString(doc, "introduction"));
				doctor.setPosition(MongodbUtil.getString(doc, "title"));
				doctor.setSkill(MongodbUtil.getString(doc, "skill"));
				doctor.setHospital(MongodbUtil.getString(doc, "hospital"));
				doctor.setDepartments(MongodbUtil.getString(doc, "departments"));
			}
			userIds.add(Integer.valueOf(obj.get("_id").toString()));
			doctorList.add(doctor);
		}

		List<DepartmentVO> departmentList = departmentDao.findListById(param.getGroupId(), null);

		// 根据查询出来的userid列表和集团id查询这些医生的科室情况
		DBObject queryDepart = new BasicDBObject();
		queryDepart.put("doctorId", new BasicDBObject("$in", userIds));
		queryDepart.put("groupId", param.getGroupId());

		DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(queryDepart);
		List<DepartmentDoctorVO> ddListDept = new ArrayList<DepartmentDoctorVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();

			Integer doctorId = obj.get("doctorId") == null ? null : Integer.valueOf(obj.get("doctorId").toString());
			/**
			 * 针对会诊包过滤掉非主任头衔的医生
			 */
			if (StringUtils.isNotBlank(param.getConsultationPackId())) {
				String rank = userRepository.getDoctorRankById(doctorId);
				if (!StringUtils.equals("1", rank) && !StringUtils.equals("2", rank)) {
					continue;
				}
			}

			DepartmentDoctorVO dd = new DepartmentDoctorVO();
			dd.setId(obj.get("_id").toString());
			dd.setGroupId(obj.get("groupId") == null ? "" : obj.get("groupId").toString());
			dd.setDepartmentId(MongodbUtil.getString(obj, "departmentId"));
			dd.setDepartmentFullName(GroupUtil.findFullNameById(departmentList,
					MongodbUtil.getString(obj, "departmentId")));
			dd.setDoctorId(doctorId);
			dd.setCreatorDate(obj.get("creatorDate") == null ? null : Long.valueOf(obj.get("creatorDate").toString()));
			dd.setUpdator(obj.get("updator") == null ? null : Integer.valueOf(obj.get("updator").toString()));
			dd.setUpdatorDate(obj.get("updatorDate") == null ? null : Long.valueOf(obj.get("updatorDate").toString()));
			for (DoctorBasicInfo doctor : doctorList) {
				if (doctor.getDoctorId().equals(MongodbUtil.getInteger(obj, "doctorId"))) {
					dd.setDoctor(doctor);
					Integer applyStatus = doctor.getStatus();
					dd.setApplyStatus(applyStatus);
					dd.setApplyStatusName(applyStatus != null
							? UserEnum.UserStatus.getEnum(applyStatus.intValue()).getTitle() : null);
					continue;
				}
			}
			for (DepartmentDoctorVO ddvo : ddvoList) {
				if (ddvo.getDoctorId().equals(dd.getDoctorId())) {
					dd.setGroupDoctorId(ddvo.getGroupDoctorId());
					dd.setContactWay(ddvo.getContactWay());
					dd.setRemarks(ddvo.getRemarks());
					dd.setUpdatorDate(ddvo.getUpdatorDate());
					dd.setInvite(ddvo.getInvite());
					InviteRelation invite = ddvo.getInvite();
					dd.setInvite(invite);
					dd.setInviterId(invite != null ? invite.getInviterId() : null);
					dd.setInviterName(invite != null ? invite.getName() : null);
					continue;
				}
			}
			ddListDept.add(dd);
		}

		List<DepartmentDoctorVO> ddList = new ArrayList<DepartmentDoctorVO>();
		ddList.addAll(ddListDept);
		// 循环查询出来的最终医生对象，如果没有在科室ddListDept中，则加进去
		for (DoctorBasicInfo doctor : doctorList) {
			// 在有科室的医生列表中搜索
			boolean isFind = false;
			for (DepartmentDoctorVO ddDept : ddListDept) {
				if (doctor.getDoctorId().equals(ddDept.getDoctorId())) {
					isFind = true;
					break;
				}
			}
			// 如果搜索不到，说明这个医生未分配科室
			if (!isFind) {
				DepartmentDoctorVO dd = new DepartmentDoctorVO();
				dd.setGroupId(param.getGroupId());
				dd.setDoctorId(doctor.getDoctorId());
				dd.setDoctor(doctor);
				for (DepartmentDoctorVO ddvo : ddvoList) {
					if (ddvo.getDoctorId().equals(dd.getDoctorId())) {
						dd.setGroupDoctorId(ddvo.getGroupDoctorId());
						dd.setContactWay(ddvo.getContactWay());
						dd.setUpdatorDate(ddvo.getUpdatorDate());
						dd.setRemarks(ddvo.getRemarks());
						InviteRelation invite = ddvo.getInvite();
						dd.setInvite(invite);
						continue;
					}
				}
				if (dd.getUpdatorDate() == null) {
					dd.setUpdatorDate(doctor.getModifyTime());
				}
				Integer applyStatus = doctor.getStatus();
				dd.setApplyStatus(applyStatus);
				dd.setApplyStatusName(
						applyStatus != null ? UserEnum.UserStatus.getEnum(applyStatus.intValue()).getTitle() : null);
				InviteRelation invite = dd.getInvite();
				dd.setInviterId(invite != null ? invite.getInviterId() : null);
				dd.setInviterName(invite != null ? invite.getName() : null);
				ddList.add(dd);
			}
		}

		PageVO page = new PageVO();
		page.setPageData(ddList);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(Long.valueOf(dsForRW.getDB().getCollection("user").count(queryUser)));
		return page;
	}

	@Override
	public GroupDoctor getById(GroupDoctor gdoc) {
		return getById(gdoc, GroupDoctorStatus.正在使用.getIndex());
	}

	@Override
	public GroupDoctor getById(GroupDoctor gdoc, String status) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);

		if (null != gdoc.getDoctorId()) {
			query.field("doctorId").equal(gdoc.getDoctorId());
		}

		if (!StringUtil.isEmpty(gdoc.getGroupId())) {
			query.field("groupId").equal(gdoc.getGroupId());
		}

		if (!StringUtil.isEmpty(gdoc.getId())) {
			query.field("_id").equal(new ObjectId(gdoc.getId()));
		}

		if (StringUtil.isNotBlank(status)) {
			query.field("status").equal(status);
		}
		return query.get();
	}

	@Override
	public List<GroupVO> findAllCompleteStatusByDoctorId(GroupDoctorParam param) {
		DBObject query = new BasicDBObject();
		query.put("doctorId", param.getDoctorId());
		query.put("status", "C");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		BasicDBList idList = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			idList.add(new ObjectId(MongodbUtil.getString(obj, "groupId")));
		}
		/* 获取所有集团Id 再根据Id获取所有集团信息列表 */
		DBObject in = new BasicDBObject();
		in.put("$in", idList);
		DBCursor uCursor = dsForRW.getDB().getCollection("c_group").find(new BasicDBObject("_id", in));
		List<GroupVO> gList = new ArrayList<GroupVO>();
		while (uCursor.hasNext()) {
			DBObject uobj = uCursor.next();
			GroupVO g = new GroupVO();
			g.setCompanyId(uobj.get("companyId") != null ? uobj.get("companyId").toString() : null);
			g.setId(uobj.get("_id").toString());
			g.setName(MongodbUtil.getString(uobj, "name"));
			g.setIntroduction(uobj.get("introduction") == null ? "" : uobj.get("introduction").toString());
			if (null != uobj.get("creator")) {
				g.setCreator(Integer.valueOf(uobj.get("creator").toString()));
			}
			if (null != uobj.get("updator")) {
				g.setUpdator(Integer.valueOf(uobj.get("updator").toString()));
			}
			if (null != uobj.get("updatorDate")) {
				g.setUpdatorDate(Long.valueOf(uobj.get("updatorDate").toString()));
			}
			if (null != uobj.get("creatorDate")) {
				g.setCreatorDate(Long.valueOf(uobj.get("creatorDate").toString()));
			}
			g.setCertStatus(uobj.get("certStatus") == null ? GroupCertStatus.noCert.getIndex()
					: uobj.get("certStatus").toString());
			g.setDepartmentList(this.getAllDepartmentListById(g));

			gList.add(g);

		}

		return gList;
	}

	@Override
	public List<GroupVO> findAllCompleteStatusByCompanyId(String companyId) {
		DBCursor uCursor = dsForRW.getDB().getCollection("c_group").find(new BasicDBObject("companyId", companyId));
		List<GroupVO> gList = new ArrayList<GroupVO>();
		while (uCursor.hasNext()) {
			DBObject uobj = uCursor.next();
			GroupVO g = new GroupVO();
			g.setCompanyId(MongodbUtil.getString(uobj, "companyId"));
			g.setId(MongodbUtil.getString(uobj, "_id"));
			g.setName(MongodbUtil.getString(uobj, "name"));
			g.setIntroduction(uobj.get("introduction") == null ? "" : uobj.get("introduction").toString());
			if (null != uobj.get("creator")) {
				g.setCreator(Integer.valueOf(uobj.get("creator").toString()));
			}
			if (null != uobj.get("updator")) {
				g.setUpdator(Integer.valueOf(uobj.get("updator").toString()));
			}
			if (null != uobj.get("updatorDate")) {
				g.setUpdatorDate(Long.valueOf(uobj.get("updatorDate").toString()));
			}
			if (null != uobj.get("creatorDate")) {
				g.setCreatorDate(Long.valueOf(uobj.get("creatorDate").toString()));
			}
			g.setDepartmentList(this.getAllDepartmentListById(g));
			gList.add(g);
		}
		return gList;
	}

	@Override
	public List<GroupVO> findAllCompleteStatusByGroupId(String groupId) {
		DBCursor uCursor = dsForRW.getDB().getCollection("c_group")
				.find(new BasicDBObject("_id", new ObjectId(groupId)));
		List<GroupVO> gList = new ArrayList<GroupVO>();
		while (uCursor.hasNext()) {
			DBObject uobj = uCursor.next();
			GroupVO g = new GroupVO();
			g.setCompanyId(MongodbUtil.getString(uobj, "companyId"));
			g.setId(MongodbUtil.getString(uobj, "_id"));
			g.setName(MongodbUtil.getString(uobj, "name"));
			g.setIntroduction(uobj.get("introduction") == null ? "" : uobj.get("introduction").toString());
			if (null != uobj.get("creator")) {
				g.setCreator(Integer.valueOf(uobj.get("creator").toString()));
			}
			if (null != uobj.get("updator")) {
				g.setUpdator(Integer.valueOf(uobj.get("updator").toString()));
			}
			if (null != uobj.get("updatorDate")) {
				g.setUpdatorDate(Long.valueOf(uobj.get("updatorDate").toString()));
			}
			if (null != uobj.get("creatorDate")) {
				g.setCreatorDate(Long.valueOf(uobj.get("creatorDate").toString()));
			}
			g.setDepartmentList(this.getAllDepartmentListById(g));

			gList.add(g);
		}
		return gList;
	}

	@Override
	public List<Department> getDepartmentById(List<GroupVO> gList, Integer doctorId) {
		BasicDBList idList = new BasicDBList();
		for (GroupVO g : gList) {
			idList.add(g.getId());
		}

		/** 获取集团下所有的科室 */
		DBObject groupIn = new BasicDBObject();
		groupIn.put("$in", idList);
		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("groupId", groupIn));

		/** 记录所有科室的Id */
		BasicDBList dIdList = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject cc = cursor.next();
			dIdList.add(cc.get("_id").toString());
		}

		DBObject departIn = new BasicDBObject();
		departIn.put("$in", dIdList);
		DBObject query = new BasicDBObject();
		query.put("departmentId", departIn);
		query.put("doctorId", doctorId);
		/** 查询满足当前科室Id 和 医生的科室 */
		DBCursor ddsor = dsForRW.getDB().getCollection("c_department_doctor").find(query);

		/** 再记录满足条件的 departemntId 并再次查询department列表 **/
		BasicDBList newdIdList = new BasicDBList();
		while (ddsor.hasNext()) {
			DBObject dd = ddsor.next();
			newdIdList.add(new ObjectId(MongodbUtil.getString(dd, "departmentId")));
		}
		DBObject newIn = new BasicDBObject();
		newIn.put("$in", newdIdList);
		DBCursor nnsor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("_id", newIn));
		List<Department> dList = new ArrayList<Department>();
		while (nnsor.hasNext()) {
			DBObject nn = nnsor.next();
			Department dvo = new Department();
			dvo.setId(MongodbUtil.getString(nn, "_id"));
			dvo.setName(MongodbUtil.getString(nn, "name"));
			dvo.setDescription(nn.get("description") == null ? "" : nn.get("description").toString());
			dvo.setParentId(MongodbUtil.getString(nn, "parentId"));
			dvo.setUpdator(MongodbUtil.getInteger(nn, "updator"));
			dvo.setUpdatorDate((MongodbUtil.getLong(nn, "updatorDate")));
			dvo.setCreator(MongodbUtil.getInteger(nn, "creator"));
			dvo.setCreatorDate((MongodbUtil.getLong(nn, "creatorDate")));
			dList.add(dvo);
		}

		return dList;
	}

	public List<GroupDoctorVO> findListByDepartmentIds(List<DepartmentVO> dList, String groupId) {
		BasicDBList departIdList = new BasicDBList();
		for (DepartmentVO depar : dList) {
			departIdList.add(depar.getId());
		}
		/** 获取科室下所有的医生 */
		DBObject departIn = new BasicDBObject();
		departIn.put("$in", departIdList);

		DBCursor ddsor = dsForRW.getDB().getCollection("c_department_doctor")
				.find(new BasicDBObject("departmentId", departIn));
		BasicDBList doctorIdList = new BasicDBList();
		while (ddsor.hasNext()) {
			DBObject obj = ddsor.next();
			doctorIdList.add((Integer) obj.get("doctorId"));
		}
		DBObject doctorIn = new BasicDBObject();
		doctorIn.put("$in", doctorIdList);

		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("doctorId", doctorIn);
		query.put("status", "C");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		List<GroupDoctorVO> gList = new ArrayList<GroupDoctorVO>();
		BasicDBList idList = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(obj.get("_id").toString());
			gd.setGroupId(MongodbUtil.getString(obj, "groupId"));
			gd.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			gd.setReferenceId(MongodbUtil.getInteger(obj, "referenceId"));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());

			gd.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			gd.setUpdatorDate((MongodbUtil.getLong(obj, "updatorDate")));
			gd.setCreator(MongodbUtil.getInteger(obj, "creator"));
			gd.setCreatorDate((MongodbUtil.getLong(obj, "creatorDate")));
			gList.add(gd);
			idList.add(obj.get("doctorId"));
		}

		return this.getUserBydoctorIds(gList, idList);
	}

	private List<GroupDoctorVO> getUserBydoctorIdsWithStatus(List<GroupDoctorVO> gdList, BasicDBList idList,
			String status) {
		List<GroupDoctorVO> result = new ArrayList<GroupDoctorVO>();
		DBObject in = new BasicDBObject();
		in.put("$in", idList);
		DBObject query = new BasicDBObject();
		query.put("_id", in);
		if (StringUtil.isNotBlank(status)) {
			query.put("status", Integer.parseInt(status));
		}
		DBCursor uCursor = dsForRW.getDB().getCollection("user").find(query);
		List<Integer> inviterIds = Lists.newArrayList();
		while (uCursor.hasNext()) {
			DBObject uobj = uCursor.next();
			Integer doctorId = (Integer) uobj.get("_id");
			for (GroupDoctorVO ddoc : gdList) {
				/** 对比Id相同的setter医生属性 */
				if (ddoc.getDoctorId().equals(doctorId)) {
					String uStatus = MongodbUtil.getString(uobj, "status");
					if (StringUtil.isNotBlank(status) && !status.equals(uStatus)) {
						continue;
					}
					/** 审核状态 add by tanyf 20160621 */
					Integer applyStatus = MongodbUtil.getInteger(uobj, "status");
					ddoc.setApplyStatus(applyStatus);
					UserStatus userStatus = UserEnum.UserStatus.getEnum(applyStatus);
					ddoc.setApplyStatusName(userStatus == null ? "" : userStatus.getTitle()); /** end */

					DoctorBasicInfo doctor = new DoctorBasicInfo();

					String doctorName = MongodbUtil.getString(uobj, "name");
					String telephone = uobj.get("telephone").toString();

					doctor.setName(StringUtils.isEmpty(doctorName) ? telephone : doctorName);
					doctor.setDoctorId(Integer.valueOf(uobj.get("_id").toString()));
					doctor.setTelephone(telephone);
					doctor.setUserType(Integer.valueOf(uobj.get("userType").toString()));
					doctor.setSex(MongodbUtil.getInteger(uobj, "sex"));
					if (uobj.get("headPicFileName") == null) {
						doctor.setHeadPicFileName("");
						doctor.setHeadPicFilePath("");
					} else {
						doctor.setHeadPicFileName(uobj.get("headPicFileName").toString());
						doctor.setHeadPicFilePath(UserHelper.buildHeaderPicPath(doctor.getHeadPicFileName(), doctorId));
					}
					DBObject doc = (BasicDBObject) uobj.get("doctor");
					if (null != doc) {
						doctor.setDoctorNum(MongodbUtil.getString(doc, "doctorNum"));
						doctor.setIntroduction(MongodbUtil.getString(doc, "introduction"));
						doctor.setPosition(MongodbUtil.getString(doc, "title"));
						doctor.setSkill(MongodbUtil.getString(doc, "skill"));
						doctor.setHospital(MongodbUtil.getString(doc, "hospital"));
						doctor.setDepartments(MongodbUtil.getString(doc, "departments"));

						Integer role = MongodbUtil.getInteger(doc, "role");
						if (null == role || role.intValue() == 0) {
							role = UserEnum.DoctorRole.doctor.getIndex();
						}
						doctor.setRole(role);
					}
					ddoc.setDoctor(doctor);

					// 邀请人
					ddoc.setInviterId(ddoc.getReferenceId());
					inviterIds.add(ddoc.getReferenceId());
					result.add(ddoc);
					break;
				}
			}

		}

		// 查询邀请人的信息
		if (inviterIds != null && inviterIds.size() > 0) {
			BasicDBList userIds = new BasicDBList();
			for (Integer id : inviterIds) {
				userIds.add(id);
			}

			BasicDBObject inUserObj = new BasicDBObject();
			inUserObj.put("$in", userIds);
			DBObject inviterQuery = new BasicDBObject();
			inviterQuery.put("_id", inUserObj);
			DBCursor inviterCursor = dsForRW.getDB().getCollection("user").find(inviterQuery);
			while (inviterCursor.hasNext()) {
				DBObject obj = inviterCursor.next();
				String name = obj.get("name").toString();
				String id = obj.get("_id").toString();
				if (result != null && result.size() > 0) {
					for (GroupDoctorVO doc : result) {
						Integer tempInviterId = doc.getInviterId();
						if (tempInviterId != null && StringUtils.equals(id, String.valueOf(tempInviterId))) {
							doc.setInviterName(name);
						}
					}
				}
			}
		}

		if (StringUtil.isNotBlank(status)) {
			return result;
		}
		return gdList;
	}

	/**
	 * 根据医生Id列表获取用户信息
	 * 
	 * @param gdList
	 * @param idList
	 * @return
	 */
	private List<GroupDoctorVO> getUserBydoctorIds(List<GroupDoctorVO> gdList, BasicDBList idList) {
		return getUserBydoctorIdsWithStatus(gdList, idList, null);
	}

	/**
	 * 获取 我的邀请关系
	 * 
	 * @param gdList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<GroupDoctorVO> setterDoctorRelationByIds(List<GroupDoctorVO> gdList) {
		BasicDBList refIds = new BasicDBList();
		for (GroupDoctorVO gd : gdList) {
			refIds.add(gd.getReferenceId());
		}

		BasicDBObject refIn = new BasicDBObject();
		refIn.put("$in", refIds);
		DBCursor cursor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", refIn));
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Integer doctorId = (Integer) obj.get("_id");
			for (GroupDoctorVO ddoc : gdList) {
				if (ddoc.getReferenceId().equals(doctorId)) {
					InviteRelation invite = new InviteRelation();
					invite.setInviterId(doctorId);
					invite.setInviteeId(ddoc.getDoctorId());
					invite.setInviteDate(ddoc.getCreatorDate());
					invite.setName(MongodbUtil.getString(obj, "name"));
					if (obj.get("headPicFileName") == null) {
						invite.setHeadPicFileName("");
						invite.setHeadPicFilePath("");
					} else {
						invite.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
						invite.setHeadPicFilePath(UserHelper.buildHeaderPicPath(invite.getHeadPicFileName(), doctorId));
					}
					invite.setInviteMsg(obj.get("name").toString() + "在"
							+ DateUtil.formatDate2Str(new Date(ddoc.getCreatorDate())) + "要请了我加入");
					List<?> list = findInviteListById(ddoc.getDoctorId(), ddoc.getGroupId()).getPageData();
					invite.setMyInvite(list == null ? new ArrayList<InviteRelation>() : (List<InviteRelation>) list);
					ddoc.setRelation(invite);
					break;
				}
			}
		}
		return gdList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DepartmentMobileVO> getAllDepartmentListById(GroupVO groupVO) {
		DBObject depart = new BasicDBObject();
		depart.put("groupId", groupVO.getId());
		depart.put("parentId", "0");
		DBCursor desor = dsForRW.getDB().getCollection("c_department").find(depart);
		List<DepartmentMobileVO> departList = new ArrayList<DepartmentMobileVO>();
		while (desor.hasNext()) {
			DBObject deobj = desor.next();
			DepartmentMobileVO dvo = new DepartmentMobileVO();
			dvo.setId(deobj.get("_id").toString());
			dvo.setName(deobj.get("name").toString());
			dvo.setDescription(deobj.get("description") == null ? "" : deobj.get("description").toString());
			dvo.setParentId(deobj.get("parentId").toString());
			dvo.setGroupId(deobj.get("groupId").toString());
			dvo.setUpdator(MongodbUtil.getInteger(deobj, "updator"));
			dvo.setUpdatorDate((MongodbUtil.getLong(deobj, "updatorDate")));
			dvo.setCreator(MongodbUtil.getInteger(deobj, "creator"));
			dvo.setCreatorDate((MongodbUtil.getLong(deobj, "creatorDate")));
			dvo.setSubList(this.getDepartmentListById(dvo));
			dvo.setDoctorList(this.getGroupDoctorListById(dvo));
			departList.add(dvo);
		}
		/* 获取集团未分配的医生列表 */
		DepartmentMobileVO dmvo = new DepartmentMobileVO();
		dmvo.setName("未分配");
		GroupDoctorParam param = new GroupDoctorParam();
		param.setGroupId(groupVO.getId());
		param.setPageSize(Integer.MAX_VALUE);
		List<?> list = this.findUndistributedListById(param).getPageData();
		if (null != list && 0 != list.size()) {
			List<GroupDoctorVO> gdocList = (List<GroupDoctorVO>) list;
			dmvo.setDoctorList(gdocList);
		} else {
			dmvo.setDoctorList(new ArrayList<GroupDoctorVO>());
		}
		/* 添加未分配的 */
		departList.add(dmvo);
		return departList;
	}

	public List<DepartmentMobileVO> getMyDepartmentListById(List<GroupVO> gList, Integer doctorId) {
		BasicDBList gIds = new BasicDBList();
		for (GroupVO g : gList) {
			gIds.add(g.getId());
		}

		BasicDBObject groupIn = new BasicDBObject();
		groupIn.put("$in", gIds);
		DBCursor deasor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("groupId", groupIn));
		BasicDBList deIds = new BasicDBList();
		while (deasor.hasNext()) {
			DBObject obj = deasor.next();
			deIds.add(obj.get("_id").toString());
		}

		DBObject query = new BasicDBObject();
		query.put("departmentId", new BasicDBObject("$in", deIds));
		query.put("doctorId", doctorId);

		DBCursor sursor = dsForRW.getDB().getCollection("c_department_doctor").find(query);
		BasicDBList departIds = new BasicDBList();
		while (sursor.hasNext()) {
			DBObject obj = sursor.next();
			departIds.add(new ObjectId(MongodbUtil.getString(obj, "departmentId")));
		}
		BasicDBObject departin = new BasicDBObject();
		departin.put("$in", departIds);
		DBCursor desor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("_id", departin));
		List<DepartmentMobileVO> departList = new ArrayList<DepartmentMobileVO>();
		while (desor.hasNext()) {
			DBObject deobj = desor.next();
			DepartmentMobileVO dvo = new DepartmentMobileVO();
			dvo.setId(deobj.get("_id").toString());
			dvo.setName(deobj.get("name").toString());
			dvo.setDescription(deobj.get("description") == null ? "" : deobj.get("description").toString());
			dvo.setParentId(deobj.get("parentId").toString());

			dvo.setGroupId(deobj.get("groupId").toString());

			dvo.setUpdator(MongodbUtil.getInteger(deobj, "updator"));
			dvo.setUpdatorDate((MongodbUtil.getLong(deobj, "updatorDate")));
			dvo.setCreator(MongodbUtil.getInteger(deobj, "creator"));
			dvo.setCreatorDate((MongodbUtil.getLong(deobj, "creatorDate")));

			// dvo.setSubList(this.getDepartmentListById(dvo));
			// dvo.setDoctorList(this.getGroupDoctorListById(dvo));
			departList.add(dvo);
		}

		return departList;
	}

	/**
	 * 递归查询departemnt
	 * 
	 * @param department
	 * @return
	 */
	private List<DepartmentMobileVO> getDepartmentListById(DepartmentMobileVO department) {
		DBCursor desor = dsForRW.getDB().getCollection("c_department")
				.find(new BasicDBObject("parentId", department.getId()));
		List<DepartmentMobileVO> subList = new ArrayList<DepartmentMobileVO>();
		while (desor.hasNext()) {
			DBObject deobj = desor.next();
			DepartmentMobileVO depart = new DepartmentMobileVO();
			depart.setId(deobj.get("_id").toString());
			depart.setName(deobj.get("name").toString());
			depart.setDescription(deobj.get("description") == null ? "" : deobj.get("description").toString());
			depart.setParentId(deobj.get("parentId").toString());
			depart.setGroupId(deobj.get("groupId").toString());

			depart.setUpdator(MongodbUtil.getInteger(deobj, "updator"));
			depart.setUpdatorDate((MongodbUtil.getLong(deobj, "updatorDate")));
			depart.setCreator(MongodbUtil.getInteger(deobj, "creator"));
			depart.setCreatorDate((MongodbUtil.getLong(deobj, "creatorDate")));

			if (!StringUtil.isEmpty(deobj.get("parentId").toString())) {
				depart.setSubList(this.getDepartmentListById(depart));
			}
			depart.setDoctorList(this.getGroupDoctorListById(depart));
			subList.add(depart);
		}

		return subList;
	}

	/**
	 * 查询科室下的医生列表
	 * 
	 * @param department
	 * @return
	 */
	private List<GroupDoctorVO> getGroupDoctorListById(DepartmentMobileVO department) {
		DBCursor desor = dsForRW.getDB().getCollection("c_department_doctor").find(new BasicDBObject("departmentId", department.getId()));
		BasicDBList doctorIds = new BasicDBList();
		while (desor.hasNext()) {
			DBObject obj = desor.next();
			doctorIds.add(Integer.valueOf(obj.get("doctorId").toString()));
		}
		DBObject query = new BasicDBObject();
		query.put("doctorId", new BasicDBObject("$in", doctorIds));
		query.put("groupId", department.getGroupId());
		query.put("status", "C");

		DBCursor ussor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();

		while (ussor.hasNext()) {
			DBObject obj = ussor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(obj.get("_id").toString());
			gd.setDepartmentId(department.getId());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());

			gd.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			gd.setUpdatorDate((MongodbUtil.getLong(obj, "updatorDate")));
			gd.setCreator(MongodbUtil.getInteger(obj, "creator"));
			gd.setCreatorDate((MongodbUtil.getLong(obj, "creatorDate")));

			gdList.add(gd);

		}

		gdList = this.getUserBydoctorIds(gdList, doctorIds);
		return this.setterDoctorRelationByIds(gdList);
	}

	@Override
	public PageVO findUndistributedListById(GroupDoctorParam param) {
		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("groupId", param.getGroupId()));
		BasicDBList departIds = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			departIds.add(obj.get("_id").toString());
		}
		BasicDBObject departIn = new BasicDBObject();
		departIn.put("$in", departIds);

		DBCursor dersor = dsForRW.getDB().getCollection("c_department_doctor").find(new BasicDBObject("departmentId", departIn));
		BasicDBList doctorIds = new BasicDBList();
		while (dersor.hasNext()) {
			DBObject obj = dersor.next();
			doctorIds.add(Integer.valueOf(obj.get("doctorId").toString()));
		}
		/**
		 * 排除会诊包中的医生
		 */
//		if (StringUtils.isNotBlank(param.getConsultationPackId())) {
//			List<Integer> removeIds = this.getNotInCurrentPackDoctorIds(param.getGroupId(),
//					param.getConsultationPackId());
//			if (removeIds != null && removeIds.size() > 0) {
//				for (Integer id : removeIds) {
//					doctorIds.add(id);
//				}
//			}
//		}

		BasicDBObject doctorIn = new BasicDBObject();
		doctorIn.put("$nin", doctorIds);
		BasicDBObject query = new BasicDBObject();
		query.put("groupId", param.getGroupId());
		query.put("doctorId", doctorIn);
		query.put("status", "C");
		DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor")
								.find(query)
								.sort(new BasicDBObject("updatorDate", -1))
								.skip(param.getStart())
								.limit(param.getPageSize());
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		BasicDBList docIds = new BasicDBList();
		while (docsor.hasNext()) {
			DBObject obj = docsor.next();
			GroupDoctorVO gd = new GroupDoctorVO();
			Integer doctorId = Integer.valueOf(obj.get("doctorId").toString());
			/**
			 * 针对会诊包过滤掉非主任头衔的医生
			 */
			if (StringUtils.isNotBlank(param.getConsultationPackId())) {
				String rank = userRepository.getDoctorRankById(doctorId);
				if (!StringUtils.equals("1", rank) && !StringUtils.equals("2", rank)) {
					continue;
				}
			}

			gd.setId(obj.get("_id").toString());
			gd.setGroupDoctorId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(doctorId);
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId") == null ? "0" : obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			if (null != obj.get("creator")) {
				gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			}
			if (null != obj.get("updator")) {
				gd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			}
			if (null != obj.get("updatorDate")) {
				gd.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			}
			if (null != obj.get("creatorDate")) {
				gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			}
			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
			gdList.add(gd);
		}

		// gdList = this.getUserBydoctorIds(gdList, docIds);
		gdList = this.getUserBydoctorIdsWithStatus(gdList, docIds, param.getStatus());
		gdList = this.setterDoctorRelationByIds(gdList);
		PageVO page = new PageVO();
		page.setPageData(gdList);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(dsForRW.getDB().getCollection("c_group_doctor").count(query));

		return page;
	}

	@Override
	public List<GroupDoctorVO> findGroupDoctorListByGroupId(String groupId) {
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(new BasicDBObject("groupId", groupId));

		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		BasicDBList docIds = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			if (null != obj.get("creator")) {
				gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			}
			if (null != obj.get("updator")) {
				gd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			}
			if (null != obj.get("updatorDate")) {
				gd.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			}
			if (null != obj.get("creatorDate")) {
				gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			}

			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
			gdList.add(gd);
		}
		gdList = this.getUserBydoctorIds(gdList, docIds);
		gdList = this.setterDoctorRelationByIds(gdList);
		return gdList;
	}

	@Override
	public int getCountByGroupIds(String... ids) {
		BasicDBList idList = new BasicDBList();
		for (String id : ids) {
			idList.add(id);
		}
		return dsForRW.getDB().getCollection("c_group_doctor")
				.find(new BasicDBObject("groupId", new BasicDBObject("$in", idList))).count();
	}

	@Override
	public List<GroupDoctorVO> findListByIds(String[] ids) {
		BasicDBList idList = new BasicDBList();
		for (String id : ids) {
			idList.add(new ObjectId(id));
		}
		DBCursor corsor = dsForRW.getDB().getCollection("c_group_doctor")
				.find(new BasicDBObject("_id", new BasicDBObject("$in", idList)));
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		BasicDBList docIds = new BasicDBList();
		while (corsor.hasNext()) {
			DBObject obj = corsor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			if (null != obj.get("creator")) {
				gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			}
			if (null != obj.get("updator")) {
				gd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			}
			if (null != obj.get("updatorDate")) {
				gd.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			}
			if (null != obj.get("creatorDate")) {
				gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			}

			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
			gdList.add(gd);
		}
		return gdList;
	}

	/**
	 * </p>
	 * 根据doctorId获取所有数据
	 * </p>
	 * 
	 * @param groupId
	 * @param doctorIds
	 * @return
	 * @author fanp
	 * @date 2015年9月6日
	 */
	public List<GroupDoctorVO> getByIds(String groupId, List<Integer> doctorIds) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("doctorId", new BasicDBObject("$in", doctorIds));
		// 必须查询在集团中正常状态的人员信息 add by wangqiao 2016-02-18
		query.put("status", GroupDoctorStatus.正在使用.getIndex());

		DBObject project = new BasicDBObject();
		project.put("doctorId", 1);
		project.put("contactWay", 1);
		project.put("remarks", 1);
		project.put("onLineState", 1);
		project.put("taskDuration", 1);
		project.put("troubleFree", 1);
		project.put("outpatientPrice", 1);
		project.put("onLineTime", 1);
		project.put("dutyDuration", 1);
		project.put("offLineTime", 1);

		DBCursor corsor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);
		List<GroupDoctorVO> list = new ArrayList<GroupDoctorVO>();

		while (corsor.hasNext()) {
			DBObject obj = corsor.next();

			GroupDoctorVO vo = new GroupDoctorVO();
			vo.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			vo.setContactWay(MongodbUtil.getString(obj, "contactWay"));
			vo.setRemarks(MongodbUtil.getString(obj, "remarks"));
			vo.setOnLineState(MongodbUtil.getString(obj, "onLineState"));
			vo.setTroubleFree(MongodbUtil.getString(obj, "troubleFree"));
			vo.setOutpatientPrice(MongodbUtil.getInteger(obj, "outpatientPrice"));
			vo.setOnLineTime(MongodbUtil.getLong(obj, "onLineTime"));
			vo.setOffLineTime(MongodbUtil.getLong(obj, "offLineTime"));
			// Long dutyDuration=MongodbUtil.getInteger(obj,
			// "dutyDuration")!=null?
			// Long.parseLong(""+MongodbUtil.getInteger(obj,
			// "dutyDuration")):null;
			vo.setDutyDuration(MongodbUtil.getLong(obj, "dutyDuration"));
			vo.setTaskDuration(MongodbUtil.getLong(obj, "taskDuration"));

			list.add(vo);
		}
		return list;
	}

	/**
	 * </p>
	 * 通过状态查找集团医生
	 * </p>
	 * 
	 * @param groupId
	 * @param statuses
	 * @return
	 * @author fanp
	 * @date 2015年9月23日
	 */
	public List<Integer> getDoctorByStatus(String groupId, String[] statuses) {
		List<Integer> userIds = new ArrayList<Integer>();

		// 查询医生集团医生
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("status", new BasicDBObject("$in", statuses));

		DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		while (docsor.hasNext()) {
			DBObject obj = docsor.next();
			userIds.add(MongodbUtil.getInteger(obj, "doctorId"));
		}

		return userIds;
	}

	// 获取集团预约医生信息
	public PageVO findDoctorByGroup(GroupSearchParam param) {
		List<GroupDoctorInfoVO> groupDoc = new ArrayList<GroupDoctorInfoVO>();
		if (param.getDocIds() != null) {
			groupDoc = findUsersBydocId(param.getDocIds(), param);
		}
		PageVO pageVO = new PageVO();
		pageVO.setPageData(groupDoc);
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		pageVO.setTotal(param.getTotal());
		return pageVO;
	}

	public List<GroupDoctorInfoVO> findUsersBydocId(List<Integer> docIds, PageVO pageVo) {

		List<GroupDoctorInfoVO> groupDoc = new ArrayList<GroupDoctorInfoVO>();
		List<User> users = userRepository.findUsers(docIds, pageVo);
		Map<String, String> diseaseMap = getDiseaseTypeMap(users);
		for (User user : users) {
			GroupDoctorInfoVO dto = new GroupDoctorInfoVO();
			dto.setDoctorId(user.getUserId());
			dto.setDoctorName(user.getName());
			dto.setDoctorPath(user.getHeadPicFileName());
			if (user.getDoctor() != null) {
				dto.setSkill(user.getDoctor().getSkill());
				dto.setSpecialist(getDocspecias(user, diseaseMap));// 专长
				dto.setPosition(user.getDoctor().getTitle());
				dto.setInquiryNum(user.getDoctor().getCureNum());
				dto.setDepartments(user.getDoctor().getDepartments());
				// 职称排行
				dto.titleRank = user.getDoctor().getTitleRank();
				dto.setRole(user.getDoctor().getRole());// 增加角色
			}
			groupDoc.add(dto);
		}

		return groupDoc;
	}

	public List<GroupDoctorInfoVO> findUsersAndStateBydocId(List<Integer> docIds, Map<Integer, String> onLineStateMap) {
		List<GroupDoctorInfoVO> groupDoc = new ArrayList<GroupDoctorInfoVO>();
		List<User> users = userRepository.findUsers(docIds);
		Map<String, String> diseaseMap = getDiseaseTypeMap(users);
		for (User user : users) {
			// User user_ = userManagerImpl.getUser(user.getUserId());
			GroupDoctorInfoVO dto = new GroupDoctorInfoVO();
			dto.setDoctorId(user.getUserId());
			dto.setDoctorName(user.getName());
			dto.setDoctorPath(user.getHeadPicFileName());
			if (user.getDoctor() != null) {
				dto.setSkill(user.getDoctor().getSkill());
				dto.setSpecialist(getDocspecias(user, diseaseMap));// 专长
				dto.setPosition(user.getDoctor().getTitle());
				dto.setInquiryNum(user.getDoctor().getCureNum());
				dto.setDepartments(user.getDoctor().getDepartments());
				dto.setOnLineState(getOnLineState(user.getUserId(), onLineStateMap));
				// 职称排行
				dto.titleRank = user.getDoctor().getTitleRank();

				dto.setRole(user.getDoctor().getRole());// 增加角色
			}
			groupDoc.add(dto);
		}

		return groupDoc;
	}

	/**
	 * 1：在线；2：离线
	 * 
	 * @param userId
	 * @param onLineStateMap
	 * @return
	 */
	private String getOnLineState(Integer userId, Map<Integer, String> onLineStateMap) {
		String onLineState = "2";
		if (onLineStateMap != null) {
			onLineState = onLineStateMap.get(userId);
			if (!"1".equals(onLineState)) {
				onLineState = "2";
			}
		}
		return onLineState;
	}

	private String getDocspecias(User user, Map<String, String> diseaseMap) {
		String speciasName = "";
		if (user.getDoctor().getExpertise() != null) {
			List<String> diesIds = user.getDoctor().getExpertise();
			if (diesIds.size() > 0) {
				for (String disease : diesIds) {
					if (!StringUtil.isEmpty(diseaseMap.get(disease))) {
						speciasName += diseaseMap.get(disease) + ",";
					}

				}
				if (diesIds.size() > 0) {
					if (!StringUtil.isEmpty(speciasName)) {
						speciasName = speciasName.substring(0, speciasName.length() - 1);
					}

				}
			}
		}
		return speciasName;
	}

	/**
	 * 获取专长map
	 * 
	 * @param users
	 * @return
	 */
	private Map<String, String> getDiseaseTypeMap(List<User> users) {
		Map<String, String> diseaseMap = new HashMap<String, String>();
		List<String> diesIds = new ArrayList<String>();
		for (User user : users) {
			if (user.getDoctor() != null) {
				List<String> aDiesIds = user.getDoctor().getExpertise();
				if (aDiesIds != null) {
					for (String diseaseId : aDiesIds) {
						if (!diesIds.contains(diseaseId)) {
							diesIds.add(diseaseId);
						}
					}
				}
			}

		}
		if (diesIds.size() > 0) {
			List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(diesIds);
			for (DiseaseType diseaseType : diseaseTypes) {
				diseaseMap.put(diseaseType.getId(), diseaseType.getName());
			}
		}
		return diseaseMap;
	}

	// 根据医生集团ID或科室ID分页查询医生信息
	public PageVO findProDoctorByGroupId(GroupSearchParam param) {

		DBObject query = new BasicDBObject();
		if (StringUtil.isNotBlank(param.getDocGroupId())) {
			query.put("groupId", param.getDocGroupId());
		} else {
			return null;
		}
		List<Integer> docIds = new ArrayList<Integer>();
		query.put("status", "C");
		DBCollection collection = dsForRW.getDB().getCollection("c_group_doctor");
		DBCursor docsor = collection.find(
				query)/*
						 * .skip(param.getStart()).limit( param.getPageSize())
						 */;
		while (docsor.hasNext()) {
			DBObject obj = docsor.next();
			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
		}
		PageVO pageVO = new PageVO();
		pageVO.setPageData(findUsersBydocId(docIds, param));
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		pageVO.setTotal(collection.count(query));
		return pageVO;
	}

	@Override
	public List<GroupDoctor> findGroupDoctorsByGroupId(String groupId) {

		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("groupId", groupId);
		query.filter("status", "C");
		return query.asList();

	}

	@Override
	public GroupDoctor findOneByUserId(Integer userId, String status) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", userId);
		query.filter("isMain", true);
		if (status != null) {
			query.filter("status", status);
		}
		return query.get();
	}

	public GroupDoctor findOneByUserIdAndGroupId(Integer userId, String groupId, String status) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", userId);
		query.filter("groupId", groupId);
		if (StringUtils.isNotEmpty(status)) {
			query.filter("status", status);
		}
		return query.get();
	}

	public void updateMainGroup(Integer doctorId, String groupId) {
		// 设置所有集团为非 主集团
		DBObject update = new BasicDBObject();
		update.put("isMain", false);
		DBObject query = new BasicDBObject();
		query.put("doctorId", doctorId);
		dsForRW.getDB().getCollection("c_group_doctor").updateMulti(query, new BasicDBObject("$set", update));
		// 设置主集团
		update = new BasicDBObject();
		update.put("isMain", true);
		query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("doctorId", doctorId);
		query.put("status", "C");
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update));
	}

	//查询集团列表
	public List<GroupDoctor> getByDoctorIds(List<Integer> doctorIds, String type) {
		return dsForRW.createQuery("c_group_doctor", GroupDoctor.class).filter("doctorId in", doctorIds).filter("status",GroupDoctorStatus.正在使用.getIndex())
				.filter("type", type).asList();
	}
	
	public List<GroupDoctor> getByDoctorId(Integer doctorId) {
		return getByDoctorId(doctorId, GroupType.group.getIndex(), GroupDoctor.class);
	}

	public List<GroupDoctor> getByDoctorId(Integer doctorId, String type) {
		return getByDoctorId(doctorId, type, GroupDoctor.class);
	}

	public <T> List<T> getByDoctorId(Integer doctorId, String type, Class<T> clazz) {
		// 先按主集团排序，再按加入时间排序（越早加入的排在前面）
		if (StringUtil.isEmpty(type)) {
			return dsForRW.createQuery("c_group_doctor", clazz).field("doctorId").equal(doctorId).field("status")
					.equal(GroupDoctorStatus.正在使用.getIndex()).order("-isMain,creatorDate").asList();
		} else {
			return dsForRW.createQuery("c_group_doctor", clazz).field("doctorId").equal(doctorId).field("status")
					.equal(GroupDoctorStatus.正在使用.getIndex()).field("type").equal(type).order("-isMain,creatorDate")
					.asList();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object listGroupDoctorGroupByDept(GroupDoctorParam param) {

		DBObject matchFields = new BasicDBObject();
		DBObject sorterFields = new BasicDBObject();

		matchFields.put("groupId", param.getGroupId());

		sorterFields.put("onLineTime", -1);

		DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(matchFields);

		List<Integer> doctorIds = new ArrayList<Integer>();

		List<Map> deptDoctors = new ArrayList<Map>();

		// deptDoctor:
		Set<String> departmentIds = new HashSet<String>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Object doctorId = obj.get("doctorId");

			doctorIds.add((Integer) doctorId);
			deptDoctors.add(obj.toMap());
			departmentIds.add((String) obj.get("departmentId"));
		}

		List<Department> departments = departmentDao.findListByIds(departmentIds);
		Map<String, Department> departmentMap = new HashMap<String, Department>();
		for (Department department : departments) {
			departmentMap.put(department.getId(), department);
		}

		List<GroupDoctorVO> data = getByIds(param.getGroupId(), doctorIds);
		List<User> doctors = userRepository.findUsers(doctorIds);
		Map<Integer, User> doctorMap = new HashMap<Integer, User>();
		for (User user : doctors) {
			doctorMap.put(user.getUserId(), user);
		}
		Map<Integer, GroupDoctorVO> groupDoctors = new HashMap<Integer, GroupDoctorVO>();
		for (GroupDoctorVO groupDoctorVO : data) {
			groupDoctors.put(groupDoctorVO.getDoctorId(), groupDoctorVO);
		}

		Map<Department, List<Map>> deptGroupData = new HashMap<Department, List<Map>>();

		for (Map map : deptDoctors) {
			Integer doctorId = (Integer) map.get("doctorId");
			GroupDoctorVO vo = groupDoctors.get(doctorId);
			map.put("groupDoctor", vo);
			if (vo != null) {
				map.put("isFree", !BusinessUtil.getDoctorIsFee(vo.getDutyDuration(), vo.getTaskDuration(),
						vo.getOutpatientPrice()));
			}

			List<Map> groupDeptdoctors = deptGroupData.get(departmentMap.get(map.get("departmentId")));
			if (groupDeptdoctors == null) {
				groupDeptdoctors = new ArrayList<Map>();
				deptGroupData.put((Department) departmentMap.get(map.get("departmentId")), groupDeptdoctors);

			}

			User doctorUser = (User) doctorMap.get(doctorId);
			if (doctorUser != null) {
				// Doctor doctor=doctorUser.getDoctor();
				// if(doctor!=null){
				map.put("user", doctorUser);
				// }
			}
			if (groupDeptdoctors.size() < 8) {
				if (vo != null && "1".equals(vo.getOnLineState())) {// 只加在线的的
					groupDeptdoctors.add(map);
				}
			}

		}
		List<Map> ret_data = new ArrayList<Map>();

		for (Entry<Department, List<Map>> e : deptGroupData.entrySet()) {
			Collections.sort(e.getValue(), new Sorter());
			Department key = e.getKey();
			Map x = BeanUtils.toMap(key);
			x.put("doctorList", e.getValue());
			ret_data.add(x);
		}

		return ret_data;
	}

	class Sorter implements Comparator {

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(Object o1, Object o2) {
			// 定义排序规则
			// System.out.println(o1);
			if (o1 != null && o2 != null) {
				Map o1m = (Map) o1;
				Map o2m = (Map) o2;
				GroupDoctorVO gdv1 = (GroupDoctorVO) o1m.get("groupDoctor");
				GroupDoctorVO gdv2 = (GroupDoctorVO) o2m.get("groupDoctor");
				if (gdv1 != null && gdv2 != null) {
					Long o1onLineTime = gdv1.getOnLineTime();
					Long o2onLineTime = gdv1.getOnLineTime();
					if (o1onLineTime != null && o1onLineTime != null) {
						if (o1onLineTime > o2onLineTime) {
							return 1;
						}
					}
				}
			}
			return 0;
		}

	}

	@Override
	public void clearDuration() {
		UpdateOperations<GroupDoctor> ops = dsForRW.createUpdateOperations(GroupDoctor.class);
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		ops.set("dutyDuration", 0);
		dsForRW.update(query, ops);
	}

	@Override
	public List<GroupDoctorVO> getHasSetPriceGroupDoctorListByGroupId(String groupId) {
		DBObject query = new BasicDBObject("groupId", groupId);
		query.put("taskDuration", new BasicDBObject("$gt", 0));
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);

		List<GroupDoctorVO> gdList = cursor2List(cursor);
		return gdList;
	}

	private List<GroupDoctorVO> cursor2List(DBCursor cursor) {
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		BasicDBList docIds = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();

			gd.setId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setRecordMsg(obj.get("recordMsg") == null ? "" : obj.get("recordMsg").toString());
			gd.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			gd.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			if (null != obj.get("creator")) {
				gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			}
			if (null != obj.get("updator")) {
				gd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			}
			if (null != obj.get("updatorDate")) {
				gd.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			}
			if (null != obj.get("creatorDate")) {
				gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			}
			gd.setOutpatientPrice(MongodbUtil.getInteger(obj, "outpatientPrice"));
			gd.setOnLineState(MongodbUtil.getString(obj, "onLineState"));
			gd.setDutyDuration(MongodbUtil.getLong(obj, "dutyDuration"));
			gd.setTaskDuration(MongodbUtil.getLong(obj, "taskDuration"));
			gd.setOnLineTime(MongodbUtil.getLong(obj, "onLineTime"));
			gd.setOffLineTime(MongodbUtil.getLong(obj, "offLineTime"));

			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
			gdList.add(gd);
		}
		gdList = this.getUserBydoctorIds(gdList, docIds);
		gdList = this.setterDoctorRelationByIds(gdList);
		return gdList;
	}

	@Override
	public GroupDoctor getByDoctorIdAndStatus(GroupDoctor gdoc) {

		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		if (null != gdoc.getDoctorId()) {
			query.field("doctorId").equal(gdoc.getDoctorId());
		}

		if (null != gdoc.getStatus()) {
			query.field("status").equal(gdoc.getStatus());
		}
		return query.get();
	}

	@Override
	public boolean updateContactWay(GroupDoctorParam param) {
		UpdateOperations<GroupDoctor> ops = dsForRW.createUpdateOperations(GroupDoctor.class);
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class).field("groupId").equal(param.getGroupId())
				.field("doctorId").equal(param.getDoctorId());
		ops.set("contactWay", param.getContactWay());
		dsForRW.update(query, ops);
		return true;
	}

	@Override
	public List<GroupDoctor> getGroupDoctor(GroupDoctorParam param) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class).retrievedFields(true, "doctorId", "groupId");
		if (StringUtil.isNotBlank(param.getOnLineState())) {
			q.field("onLineState").equal(param.getOnLineState());
		}
		if (StringUtil.isNotBlank(param.getStatus())) {
			q.field("status").equal(param.getStatus());
		}
		if (StringUtil.isNotBlank(param.getGroupId())) {
			q.field("groupId").equal(param.getGroupId());
		}
		return q.asList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#findGroupDoctor(java
	 * .lang.Integer, java.lang.String, java.lang.String)
	 */
	public List<GroupDoctor> findGroupDoctor(Integer doctorId, String groupId, String status) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		if (StringUtil.isNotBlank(groupId)) {
			query.filter("groupId", groupId);
		}
		if (doctorId != null) {
			query.filter("doctorId", doctorId);
		}
		if (status != null && !StringUtils.isEmpty(status.trim())) {
			query.filter("status", status);
		}

		return query.asList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#findMainGroupByDoctorId
	 * (java.lang.Integer)
	 */
	public List<GroupDoctor> findMainGroupByDoctorId(Integer doctorId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", doctorId);
		query.filter("status", GroupDoctorStatus.正在使用.getIndex());
		query.filter("isMain", true);

		return query.asList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupDoctorDao#
	 * findFirstJoinGroupByDoctorId(java.lang.Integer)
	 */
	@Deprecated /* 使用 findFirstJoinGroupIdByDoctorId */
	public GroupDoctor findFirstJoinGroupByDoctorId(Integer doctorId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", doctorId);
		query.filter("status", GroupDoctorStatus.正在使用.getIndex());
		// 按创建时间排序
		query.order("creatorDate");
		return query.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupDoctorDao#getById(java.lang.
	 * String )
	 */
	public GroupDoctor getById(String id) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		if (!StringUtil.isEmpty(id)) {
			query.field("_id").equal(new ObjectId(id));
		}
		return query.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#getDoctorApplyByGroupId
	 * (java.lang.String, java.lang.String)
	 */
	public List<GroupDoctorApplyVO> getDoctorApplyByGroupId(GroupDoctorApplyParam param, String status) {
		List<GroupDoctorApplyVO> list = new ArrayList<GroupDoctorApplyVO>();
		// HashMap<Integer,GroupDoctorApplyVO> map = new
		// HashMap<Integer,GroupDoctorApplyVO>();
		// List<Integer> doctorIds = new ArrayList<Integer>();

		// 查询集团下的加入申请
		DBObject query = new BasicDBObject();
		query.put("groupId", param.getGroupId());
		query.put("status", status);

		// 按时间排序
		DBObject sort = new BasicDBObject();
		sort.put("updatorDate", -1);

		// 查询 加入申请信息
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query).sort(sort).skip(param.getStart())
				.limit(param.getPageSize());
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorApplyVO vo = new GroupDoctorApplyVO();
			vo.setId(MongodbUtil.getString(obj, "_id"));
			vo.setGroupId(MongodbUtil.getString(obj, "groupId"));
			vo.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			vo.setApplyMsg(MongodbUtil.getString(obj, "applyMsg"));
			vo.setApplyDate(MongodbUtil.getLong(obj, "updatorDate"));

			// 查询医生信息
			DBObject doctorQuery = new BasicDBObject();
			doctorQuery.put("_id", vo.getDoctorId());
			DBObject doctorObj = dsForRW.getDB().getCollection("user").findOne(doctorQuery);
			if (doctorObj != null) {
				vo.setName(MongodbUtil.getString(doctorObj, "name"));
				vo.setHeadPicFileName(MongodbUtil.getString(doctorObj, "headPicFileName"));
				vo.setStatus(MongodbUtil.getInteger(doctorObj, "status"));

				DBObject doctorConfigObj = (DBObject) doctorObj.get("doctor");
				if (doctorConfigObj != null) {
					vo.setTitle(MongodbUtil.getString(doctorConfigObj, "title"));
					vo.setDepartments(MongodbUtil.getString(doctorConfigObj, "departments"));
					vo.setHospital(MongodbUtil.getString(doctorConfigObj, "hospital"));
				}
				// vo.setTitle(MongodbUtil.getString(doctorObj,
				// "doctor.title"));
				// vo.setDepartments(MongodbUtil.getString(doctorObj,
				// "doctor.departments"));
				// vo.setHospital(MongodbUtil.getString(doctorObj,
				// "doctor.hospital"));
			}

			list.add(vo);
		}
		return list;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#getByDoctorIdAndGroupId(java.lang.Integer, java.lang.String)
	 */
	public GroupDoctor getByDoctorIdAndGroupId(Integer doctorId, String groupId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		if (!StringUtil.isEmpty(groupId)) {
			query.field("groupId").equal(groupId);
		}
		if (doctorId != null && doctorId != 0) {
			query.field("doctorId").equal(doctorId);
		}

		return query.get();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#findGroupById(java.lang.String)
	 */
	public GroupVO findGroupById(String groupId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(groupId));

		DBCursor uCursor = dsForRW.getDB().getCollection("c_group").find(query);
		List<GroupVO> gList = new ArrayList<GroupVO>();
		while (uCursor.hasNext()) {
			DBObject uobj = uCursor.next();
			GroupVO g = new GroupVO();
			g.setCompanyId(uobj.get("companyId") != null ? uobj.get("companyId").toString() : null);
			if (null != uobj.get("_id")) {
				g.setId(uobj.get("_id").toString());
			}
			if (null != uobj.get("logoUrl")) {
				g.setLogoUrl(uobj.get("logoUrl").toString());
			}
			if (null != uobj.get("name")) {
				g.setName(uobj.get("name").toString());
			}

			g.setIntroduction(uobj.get("introduction") == null ? "" : uobj.get("introduction").toString());
			if (null != uobj.get("creator")) {
				g.setCreator(Integer.valueOf(uobj.get("creator").toString()));
			}
			if (null != uobj.get("updator")) {
				g.setUpdator(Integer.valueOf(uobj.get("updator").toString()));
			}
			if (null != uobj.get("updatorDate")) {
				g.setUpdatorDate(Long.valueOf(uobj.get("updatorDate").toString()));
			}
			if (null != uobj.get("creatorDate")) {
				g.setCreatorDate(Long.valueOf(uobj.get("creatorDate").toString()));
			}
			g.setCertStatus(uobj.get("certStatus") == null ? GroupCertStatus.noCert.getIndex()
					: uobj.get("certStatus").toString());
			g.setDepartmentList(this.getAllDepartmentListById(g));

			g.setSkip(uobj.get("skip") == null ? "N" : uobj.get("skip").toString());
			gList.add(g);

		}
		if (gList != null && gList.size() > 0) {
			return gList.get(0);
		}
		return null;

	}

	@Override
	public GroupUser findGroupDoctorByGroupIdAndDoctorId(String groupId, Integer inviteUserId) {
		return dsForRW.createQuery(GroupUser.class).field("objectId").equal(groupId).field("doctorId")
				.equal(inviteUserId).field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex()).get();
	}

	@Override
	public GroupUser findByRootAndDoctorId(Integer applyUserId) {
		List<GroupUser> list = dsForRW.createQuery(GroupUser.class).field("doctorId").equal(applyUserId)
				.field("rootAdmin").equal("root").field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex())
				.asList();
		if (list != null && list.size() > 0) {
			for (GroupUser gu : list) {
				String id = gu.getObjectId();
				Group g = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(id)).field("type")
						.equal("group").get();
				if (g != null) {
					return gu;
				}
			}
		}
		return null;
	}

	@Override
	public void updateGroupUser(GroupUser groupUser) {
		UpdateOperations<GroupUser> ops = dsForRW.createUpdateOperations(GroupUser.class);
		Query<GroupUser> query = dsForRW.createQuery(GroupUser.class).field("objectId").equal(groupUser.getObjectId())
				.field("doctorId").equal(groupUser.getDoctorId()).field("status")
				.equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
		if (groupUser.getType() != null) {
			ops.set("type", groupUser.getType());
		}
		if (StringUtils.isNotBlank(groupUser.getStatus())) {
			ops.set("status", groupUser.getStatus());
		}
		if (groupUser.getUpdator() != null) {
			ops.set("updator", groupUser.getUpdator());
		}
		if (groupUser.getUpdatorDate() != null) {
			ops.set("updatorDate", groupUser.getUpdatorDate());
		}
		if (StringUtils.isNotBlank(groupUser.getRootAdmin())) {
			ops.set("rootAdmin", groupUser.getRootAdmin());
		}
		dsForRW.updateFirst(query, ops);
	}

	// @Override
	// public GroupUser findGroupUser(Integer confirmUserId, String groupId,
	// String status) {
	// return dsForRW.createQuery(GroupUser.class)
	// .field("objectId").equal(groupId)
	// .field("doctorId").equal(confirmUserId)
	// .field("status").equal(status).get();
	// }

	@Override
	public List<Integer> findAllDoctorIdsInGroupIds(List<String> groupIds) {
		List<Integer> list = new ArrayList<Integer>();
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").in(groupIds);
		q.field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		q.retrievedFields(true, "doctorId");
		List<GroupDoctor> data = q.asList();
		if (data != null && data.size() > 0) {
			for (GroupDoctor groupDoctor : data) {
				list.add(groupDoctor.getDoctorId());
			}
		}
		return list;
	}

	// @Override
	// public List<GroupUser> getGroupUserByGroupId(String groupId) {
	//
	// return
	// dsForRW.createQuery(GroupUser.class).field("objectId").equal(groupId).field("status").equal("C").asList();
	// }

	@Override
	public List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		if (StringUtil.isNotEmpty(consultationPackId)) {
			DBObject noInId = new BasicDBObject("$ne", new ObjectId(consultationPackId));
			query.put("_id", noInId);
		}
		query.put("groupId", groupId);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("doctorIds", 1);
		DBCursor cursor = dbCollection.find(query, projection);
		Set<Integer> set = new HashSet<Integer>();
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			Object obj = dbObj.get("doctorIds");
			if (obj != null) {
				set.addAll((List<Integer>) obj);
			}
		}
		return new ArrayList<Integer>(set);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupDoctorDao#getGroupHospitalDoctorByDoctorId(java.lang.Integer)
	 */
	@Override
	public GroupDoctor getGroupHospitalDoctorByDoctorId(Integer doctorId) {
		// 参数校验
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("医生Id不能为空");
		}

		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", doctorId);
		query.filter("status", GroupDoctorStatus.正在使用.getIndex());
		query.filter("type", GroupType.hospital.getIndex());
		// 按创建时间排序
		query.order("creatorDate");

		return query.get();
	}

	@Override
	public List<GroupDoctor> findDoctorsByGroupId(String groupId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").equal(groupId);
		String[] status = { GroupEnum.GroupDoctorStatus.正在使用.getIndex() };
		q.field("status").in(Arrays.asList(status));
		return q.asList();
	}
	
	@Override
	public List<GroupDoctor> findDoctorsByGroupIdAllStatus(String groupId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").equal(groupId);
		return q.asList();
	}

	@Override
	public long findDoctorsCountByGroupId(String groupId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").equal(groupId);
		String[] status = { GroupEnum.GroupDoctorStatus.正在使用.getIndex() };
		q.field("status").in(Arrays.asList(status));
		return q.countAll();
	}

	/**
	 * 集团下成员列表
	 * 
	 * @param groupId
	 *            集团ID
	 * @return List<GroupDoctor>
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	@Override
	public List<GroupDoctor> findDoctorsListByGroupId(String groupId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").equal(groupId);
		String[] status = { GroupEnum.GroupDoctorStatus.正在使用.getIndex() };
		q.field("status").in(Arrays.asList(status));
		return q.asList();
	}

	@Override
	public GroupUser findGroupRootAdmin(String groupId) {
		GroupUser gu = dsForRW.createQuery(GroupUser.class).field("objectId").equal(groupId).field("rootAdmin")
				.equal("root").field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex()).get();
		return gu;
	}

	@Override
	public void removeByObjectId(String objectId) {
		Query<GroupUser> uq = dsForRW.createQuery(GroupUser.class).filter("_id", new ObjectId(objectId));
		dsForRW.delete(uq);
	}

	@Override
	public void delectGroupDoctor(Integer doctorId, String groupId, String type) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class).filter("doctorId", doctorId)
				.filter("groupId", groupId).filter("type", type);
		dsForRW.delete(q);
	}

	@Override
	public void updateCreateDate(String groupId, Integer doctorId) {
		UpdateOperations<GroupDoctor> ops = dsForRW.createUpdateOperations(GroupDoctor.class);
		ops.set("creatorDate", System.currentTimeMillis());
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("groupId").equal(groupId);
		q.field("doctorId").equal(doctorId);
		dsForRW.update(q, ops);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#findInviteListById(java
	 * .lang.Integer, java.lang.String)
	 */
	@Override
	public PageVO findInviteListById(Integer doctorId, String groupId) {
		DBObject query = new BasicDBObject();
		query.put("referenceId", doctorId);
		query.put("groupId", groupId);
		// query.put("status", "C");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		BasicDBList docIds = new BasicDBList();
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();
			gd.setId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			gd.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			gd.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
			gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));

			gdList.add(gd);
			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
		}
		BasicDBObject insIn = new BasicDBObject();
		insIn.put("$in", docIds);
		DBCursor usesor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", insIn));

		Map<Integer, Integer> inviteCountMap = getInviteCount(docIds, groupId);

		List<InviteRelation> inviteList = new ArrayList<InviteRelation>();
		while (usesor.hasNext()) {
			DBObject obj = usesor.next();
			Integer userId = (Integer) obj.get("_id");
			for (GroupDoctorVO doc : gdList) {
				if (doc.getDoctorId().equals(userId)) {
					InviteRelation invite = new InviteRelation();
					invite.setInviterId(doctorId);
					invite.setInviteeId(userId);
					invite.setName(MongodbUtil.getString(obj, "name"));

					invite.setHeadPicFilePath(
							UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), userId));

					invite.setInviteDate(doc.getCreatorDate());
					invite.setInviteMsg("我在 " + DateUtil.formatDate2Str(new Date(doc.getCreatorDate())) + " 邀请了 "
							+ MongodbUtil.getString(obj, "name") + " 加入");

					invite.setInviteCount(inviteCountMap.get(userId) == null ? 0 : inviteCountMap.get(userId));

					inviteList.add(invite);
				}
			}
		}

		PageVO page = new PageVO();
		page.setPageData(inviteList);
		page.setPageIndex(1);
		page.setPageSize(1);
		page.setTotal(Long.valueOf(inviteList.size()));
		return page;
	}

	@Override
	public PageVO findInviteListByIdWithNormalStatus(Integer doctorId, String groupId) {
		DBObject query = new BasicDBObject();
		query.put("referenceId", doctorId);
		query.put("groupId", groupId);
		query.put("status", "C");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		BasicDBList docIds = new BasicDBList();
		List<GroupDoctorVO> gdList = new ArrayList<GroupDoctorVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorVO gd = new GroupDoctorVO();
			gd.setId(obj.get("_id").toString());
			gd.setGroupId(obj.get("groupId").toString());
			gd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			gd.setReferenceId(Integer.valueOf(obj.get("referenceId").toString()));
			gd.setStatus(obj.get("status").toString());
			gd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			gd.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			gd.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
			gd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));

			gdList.add(gd);
			docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
		}
		BasicDBObject insIn = new BasicDBObject();
		insIn.put("$in", docIds);
		DBCursor usesor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", insIn));

		Map<Integer, Integer> inviteCountMap = getInviteCount(docIds, groupId);

		List<InviteRelation> inviteList = new ArrayList<InviteRelation>();
		while (usesor.hasNext()) {
			DBObject obj = usesor.next();
			Integer userId = (Integer) obj.get("_id");
			for (GroupDoctorVO doc : gdList) {
				if (doc.getDoctorId().equals(userId)) {
					InviteRelation invite = new InviteRelation();
					invite.setInviterId(doctorId);
					invite.setInviteeId(userId);
					invite.setName(MongodbUtil.getString(obj, "name"));

					invite.setHeadPicFilePath(
							UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), userId));

					invite.setInviteDate(doc.getCreatorDate());
					invite.setInviteMsg("我在 " + DateUtil.formatDate2Str(new Date(doc.getCreatorDate())) + " 邀请了 "
							+ MongodbUtil.getString(obj, "name") + " 加入");

					invite.setInviteCount(inviteCountMap.get(userId) == null ? 0 : inviteCountMap.get(userId));

					inviteList.add(invite);
				}
			}
		}

		PageVO page = new PageVO();
		page.setPageData(inviteList);
		page.setPageIndex(1);
		page.setPageSize(1);
		page.setTotal(Long.valueOf(inviteList.size()));
		return page;
	}

	/**
	 * 获取每个被邀请人邀请的总人数
	 * 
	 * @author 谢平
	 */
	private Map<Integer, Integer> getInviteCount(BasicDBList docIds, String groupId) {
		Map<Integer, Integer> inviteCountMap = new HashMap<Integer, Integer>();

		DBObject query = new BasicDBObject();
		query.put("referenceId", new BasicDBObject("$in", docIds));
		query.put("groupId", groupId);
		query.put("status", "C");

		DBObject fields = new BasicDBObject();
		fields.put("referenceId", 1);
		fields.put("doctorId", 1);

		DBObject orderBy = new BasicDBObject();
		orderBy.put("referenceId", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, fields).sort(orderBy);
		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		List<String> doctorIds = null;
		for (; cursor.hasNext();) {
			DBObject obj = cursor.next();
			if (!tempMap.containsKey(obj.get("referenceId").toString())) {
				doctorIds = new ArrayList<String>();
				doctorIds.add(obj.get("doctorId").toString());
				tempMap.put(obj.get("referenceId").toString(), doctorIds);
			} else {
				doctorIds.add(obj.get("doctorId").toString());
			}
		}
		for (Iterator<String> it = tempMap.keySet().iterator(); it.hasNext();) {
			String referenceId = it.next();
			inviteCountMap.put(Integer.valueOf(referenceId), tempMap.get(referenceId).size());
		}
		return inviteCountMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#getInviteInfo(java.lang
	 * .Integer, java.lang.String)
	 */
	@Override
	public InviteRelation getInviteInfo(Integer doctorId, String groupId) {
		InviteRelation invite = null;
		GroupDoctor groupDoctor = dsForRW.createQuery(GroupDoctor.class).field("doctorId").equal(doctorId)
				.field("groupId").equal(groupId).get();
		if (null != groupDoctor) {
			User user = dsForRW.createQuery(User.class).field("_id").equal(groupDoctor.getReferenceId()).get();
			invite = new InviteRelation();
			invite.setInviterId(groupDoctor.getReferenceId());
			invite.setInviteeId(doctorId);
			if (null != user) {
				invite.setName(user.getName());// 邀请人姓名
				if (null == user.getName() || "".equals(user.getName())) {
					invite.setInviteMsg(user.getTelephone() + " 在 "
							+ DateUtil.formatDate2Str(new Date(groupDoctor.getCreatorDate())) + "邀请我加入");
				} else {
					invite.setInviteMsg(user.getName() + " 在 "
							+ DateUtil.formatDate2Str(new Date(groupDoctor.getCreatorDate())) + "邀请我加入");
				}
			}
		}

		return invite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupDoctorDao#getMyInviteRelaions(
	 * java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<ExtTreeNode> getMyInviteRelaions(Integer doctorId, String groupId) {
		List<ExtTreeNode> gdList = new ArrayList<ExtTreeNode>();
		List<Integer> docIds = new ArrayList<Integer>();

		// 判断集团创建者在不在医生集团里面
		GroupDoctor gdoc = new GroupDoctor();
		gdoc.setGroupId(groupId);
		gdoc.setDoctorId(doctorId);

		GroupDoctor gd = getById(gdoc);
		if (gd == null) {
			// 构造虚拟根节点
			ExtTreeNode node = new ExtTreeNode();
			node.setId(doctorId.toString());
			node.setParentId("0");
			gdList.add(node);
		}

		// 查找我邀请的列表
		DBObject query = new BasicDBObject();
		query.put("treePath", Pattern.compile("^/" + doctorId, Pattern.CASE_INSENSITIVE));
		query.put("groupId", groupId);

		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			ExtTreeNode node = new ExtTreeNode();

			node.setId(obj.get("doctorId").toString());
			node.setParentId(obj.get("referenceId").toString());

			Map<String, Object> attr = new HashMap<String, Object>();
			attr.put("inviteDate", obj.get("creatorDate"));

			node.setAttributes(attr);

			gdList.add(node);
			docIds.add(Integer.parseInt(node.getId()));

		}

		DBObject insIn = new BasicDBObject();
		insIn.put("$in", docIds);
		DBCursor usesor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", insIn));
		while (usesor.hasNext()) {
			DBObject obj = usesor.next();
			String headPicFileName = UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"),
					MongodbUtil.getInteger(obj, "_id"));

			// 设置头像
			for (ExtTreeNode node : gdList) {
				if (Integer.parseInt(node.getId()) == MongodbUtil.getInteger(obj, "_id")) {
					node.setName(MongodbUtil.getString(obj, "name"));
					node.getAttributes().put("headPicFileName", headPicFileName);
					break;
				}
			}
		}

		gdList = ExtTreeUtil.buildTree(gdList);
		// 删除虚拟根节点
		if (gd == null && gdList != null) {
			gdList = gdList.get(0).getChildren();
		}
		return gdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupDoctorDao#
	 * getGroupDoctorCountByGroupIdsAndType(java.util.List, java.lang.String)
	 */
	@Override
	public int getGroupDoctorCountByGroupIdsAndType(List<String> groupIdList, String type) {
		// 参数校验
		if (groupIdList == null || groupIdList.size() == 0) {
			return 0;
		}
		// 查询
		DBObject query = new BasicDBObject();
		query.put("groupId", new BasicDBObject("$in", groupIdList));
		query.put("type", type);
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		return cursor.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupDoctorDao#
	 * updateGroupDoctorTypeByGroupIdsAndType(java.util.List, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void updateGroupDoctorTypeByGroupIdsAndType(List<String> groupIdList, String type, String newType) {
		// 参数校验
		if (groupIdList == null || groupIdList.size() == 0) {
			return;
		}
		if (newType == null) {
			throw new ServiceException("参数 newType不能为空");
		}
		if (!newType.equals(GroupType.group.getIndex()) && !newType.equals(GroupType.hospital.getIndex())) {
			throw new ServiceException("参数 newType不正确");
		}
		// 更新
		DBObject query = new BasicDBObject();
		query.put("groupId", new BasicDBObject("$in", groupIdList));
		query.put("type", type);

		DBObject update = new BasicDBObject();
		update.put("type", newType);

		// 批量更新
		dsForRW.getDB().getCollection("c_group_doctor").updateMulti(query, new BasicDBObject("$set", update));

	}

	@Override
	public List<User> getDoctorByDiseaseId(GroupSearchParam param) {
		Query<User> query = dsForRW.createQuery(User.class);
		query.filter("userType", UserEnum.UserType.doctor.getIndex()).filter("status", 1);
		if (StringUtil.isNotBlank(param.getDiseaseId())) {
			Pattern pattern = Pattern.compile("^" + param.getDiseaseId() + ".*$");
			query.filter("doctor.expertise", pattern);
		}
		query.offset(param.getStart()).limit(param.getPageSize());
		query.order("-doctor.titleRank").order("-doctor.cureNum");
		return query.asList();
	}

	@Override
	public List<GroupDoctor> getOnlineDoctorsByGroup(String groupId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("groupId", groupId).filter("status", "C").filter("onLineState", "1").order("-onLineTime");
		return query.asList();
	}

	@Override
	public List<String> findAllGroupIdByDoctorId(Integer doctorId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("doctorId").equal(doctorId);
		q.field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		q.retrievedFields(true, "groupId");
		List<GroupDoctor> gds = q.asList();
		List<String> groupIds = new ArrayList<String>();
		if (gds != null && gds.size() > 0) {
			for (GroupDoctor gd : gds) {
				groupIds.add(gd.getGroupId());
			}
		}
		return groupIds;
	}

	@Override
	public List<Integer> filterByGroupId(List<Integer> doctorList, String groupId) {
		Query<GroupDoctor> q = dsForRW.createQuery(GroupDoctor.class);
		q.field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		q.field("doctorId").in(doctorList);
		q.field("groupId").equal(groupId);

		q.retrievedFields(true, "doctorId");
		List<GroupDoctor> gds = q.asList();
		List<Integer> doctorIds = new ArrayList<Integer>();
		if (gds != null && gds.size() > 0) {
			for (GroupDoctor gd : gds) {
				doctorIds.add(gd.getDoctorId());
			}
		}
		return doctorIds;
	}

	@Override
	public String findFirstJoinGroupIdByDoctorId(Integer doctorId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.filter("doctorId", doctorId);
		query.filter("status", GroupDoctorStatus.正在使用.getIndex());
		// 按创建时间排序
		query.order("creatorDate");
		List<GroupDoctor> groupDoctorList = query.asList();

		if (!CollectionUtils.isEmpty(groupDoctorList)) {
			// 过滤屏蔽的集团 add by tanyf 20160606
			List<String> groupIds = Lists.transform(groupDoctorList, new Function<GroupDoctor, String>() {
				@Override
				public String apply(GroupDoctor input) {
					return input.getGroupId();
				}
			});
			GroupsParam param = new GroupsParam();
			param.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
			param.setGroupIds(groupIds);
			//只查询集团，不查询医院
			param.setType("group");
			List<Group> groupList = groupDao.getGroupList(param);// 查询集团
			if (!CollectionUtils.isEmpty(groupList)) {
				List<String> groupNormalIds = Lists.transform(groupList, new Function<Group, String>() {
					@Override
					public String apply(Group input) {
						return input.getId();
					}
				});
				return CollectionUtils.findFirstMatch(groupNormalIds, groupIds);
			}
		}
		return null;
	}

	/**
	 * 更新集团医生
	 * 
	 * @author tan.yf
	 * @date 2016年6月6日
	 */
	public void updateGroupDoctor(GroupDoctor gdoc) {
		DBObject update = new BasicDBObject();
		if (!StringUtil.isEmpty(gdoc.getStatus())) {
			update.put("status", gdoc.getStatus());
		}
		if (!StringUtil.isEmpty(gdoc.getContactWay())) {
			update.put("contactWay", gdoc.getContactWay());
		}
		if (!StringUtil.isEmpty(gdoc.getRemarks())) {
			update.put("remarks", gdoc.getRemarks());
		}

		if (null != gdoc.getReferenceId()) {
			update.put("referenceId", gdoc.getReferenceId());
		}
		if (StringUtil.isNotBlank(gdoc.getTreePath())) {
			update.put("treePath", gdoc.getTreePath());
		}
		if (StringUtil.isNotBlank(gdoc.getOnLineState())) {
			update.put("onLineState", gdoc.getOnLineState());
		}
		if (gdoc.getDutyDuration() != null) {
			update.put("dutyDuration", gdoc.getDutyDuration());
		}
		if (null != gdoc.getTaskDuration()) {
			update.put("taskDuration", gdoc.getTaskDuration());
		}
		// if (null != gdoc.getOutpatientPrice()) {
		// update.put("outpatientPrice", gdoc.getOutpatientPrice());
		// }
		if (StringUtil.isNotBlank(gdoc.getTroubleFree())) {
			update.put("troubleFree", gdoc.getTroubleFree());
		}
		if (gdoc.getOnLineTime() != null) {
			update.put("onLineTime", gdoc.getOnLineTime());
		}

		if (gdoc.getOffLineTime() != null) {
			update.put("offLineTime", gdoc.getOffLineTime());
		}

		if (gdoc.getApplyMsg() != null) {
			update.put("applyMsg", gdoc.getApplyMsg());
		}
		if (null != gdoc.getUpdator()) {
			update.put("updator", gdoc.getUpdator());
		}
		update.put("updatorDate", new Date().getTime());
		if (null != gdoc.isMain()) {
			update.put("isMain", gdoc.isMain());
		}

		DBObject query = new BasicDBObject();
		if (StringUtils.isNotBlank(gdoc.getGroupId())) {
			query.put("groupId", gdoc.getGroupId());
		}
		if (StringUtils.isNotBlank(gdoc.getId())) {
			query.put("_id", new ObjectId(gdoc.getId()));
		}
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update));
	}
	
	@Override
	public List<Integer> getDoctorIdByGroupId(String groupId) {
		
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		List<Integer> doctorIds = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			if (MongodbUtil.getInteger(obj, "doctorId") != null) {				
				doctorIds.add(MongodbUtil.getInteger(obj, "doctorId"));
			}
		}
		
		return doctorIds;
	}

	@Override
	public List<String> getGroupNameByDoctorId(Integer doctorId) {
		List<String> result = Lists.newArrayList();
		List<GroupDoctor> groupDoctors = dsForRW.createQuery(GroupDoctor.class).filter("doctorId", doctorId).asList();
		if (groupDoctors != null && groupDoctors.size() > 0) {
			for(GroupDoctor groupDoctor : groupDoctors) {
				Group group = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(groupDoctor.getGroupId())).get();
				if (group!=null) {
					result.add(group.getName());
				}
			}
		}
		return result;
	}

	@Override
	public Map<Integer, List<String>> getGroupNameByDoctorIds(List<Integer> doctorIds) {
		
		Map<Integer, List<String>> result = Maps.newHashMap();
		BasicDBList groupIds = new BasicDBList();
		
		Query<GroupDoctor> gQuery = dsForRW.createQuery(GroupDoctor.class).filter("doctorId in", doctorIds);
		gQuery.filter("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		
		List<GroupDoctor> groupDoctors = gQuery.asList();
		
		if (groupDoctors != null && groupDoctors.size() > 0) {
			for(GroupDoctor groupDoctor : groupDoctors) {
				groupIds.add(new ObjectId(groupDoctor.getGroupId()));
			}
		}
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("$in", groupIds);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", dbObject);
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query);
		
		Map<String, String> groupMap = Maps.newHashMap();
		
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String id = MongodbUtil.getString(obj, "_id");
			String name = MongodbUtil.getString(obj, "name");
			groupMap.put(id, name);
		}
		
		for(Integer doctorId : doctorIds) {
			List<String> groupNames = Lists.newArrayList();
			for(GroupDoctor groupDoctor: groupDoctors) {
				if (doctorId.equals(groupDoctor.getDoctorId())) {
					groupNames.add(groupMap.get(groupDoctor.getGroupId()));
				}
			}
			result.put(doctorId, groupNames);
		}
		
		return result;
	}
	public List<String> findGroupMainList(String groupId){
		Query<GroupDoctor> q=dsForRW.createQuery(GroupDoctor.class);
		q.filter("groupId", groupId);
		//q.filter("isMain", true);
		q.filter("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());
		q.retrievedFields(true, "doctorId");
		List<GroupDoctor> list=q.asList();
		List<String> doctorIds=new ArrayList<>();
		for(GroupDoctor group:list){
			doctorIds.add(group.getDoctorId().toString());
		}
		return doctorIds;
	}
	
	@Override
	public List<String> getActiveGroupIdListByDoctor(Integer doctorId) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class)
				.field("doctorId").equal(doctorId)
				.filter("status", GroupDoctorStatus.正在使用.getIndex())
				.retrievedFields(true, "groupId");
		
		List<String> groupIdList = Lists.newArrayList();
		MorphiaIterator<GroupDoctor, GroupDoctor> iter = query.fetch();
		while (iter.hasNext()) {
			GroupDoctor gd = iter.next();
			groupIdList.add(gd.getGroupId());
		}
		return groupIdList;
	}

    @Override
    public List<GroupDoctor> getActiveListByDoctor(Integer doctorId) {
        Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class)
                .field("doctorId").equal(doctorId)
                .filter("status", GroupDoctorStatus.正在使用.getIndex());

        List<GroupDoctor> list = query.asList();

        return list;
    }

	@Override
	public GroupDoctor findByDoctorIdAndType(Integer doctorId, String type) {
		Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class);
		query.field("doctorId").equal(doctorId);
		query.field("type").equal(type);
		query.field("status").equal(GroupDoctorStatus.正在使用.getIndex());
		return query.get();
	}


}
