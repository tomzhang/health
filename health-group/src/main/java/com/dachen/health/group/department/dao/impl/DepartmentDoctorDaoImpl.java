package com.dachen.health.group.department.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.dao.ICommonDao;
import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.entity.vo.DeptByDoctorVO;
import com.dachen.health.group.department.entity.vo.OnlineDocByGorupVO;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.GroupDoctorInfoVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.InviteRelation;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Repository
public class DepartmentDoctorDaoImpl extends NoSqlRepository implements IDepartmentDoctorDao {

	@Autowired
    protected IGroupDoctorDao groupDoctorDao;
	
	@Autowired
    protected IDepartmentDao deparDao;
	
	@Override
	public boolean save(DepartmentDoctor ddoc) {
		dsForRW.insert(ddoc);
		return true;
	}
	
	public boolean save(List<DepartmentDoctor> ddoc) {
		dsForRW.save(ddoc);
		return true;
	}

	@Override
	public boolean update(DepartmentDoctor ddoc) {
		/***
		 * 暂不实现
		 */
		return false;
	}

	@Override
	public boolean delete(String... ids) {
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String str : ids) {
			values.add(new ObjectId(str));
		}
		in.put("$in", values);
		
		dsForRW.getDB().getCollection("c_department_doctor").remove(new BasicDBObject("_id", in));
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PageVO search(DepartmentDoctorParam param) {
		List<DepartmentDoctor> allddList = 
				dsForRW.createQuery(DepartmentDoctor.class)
				.field("departmentId").equal(param.getDepartmentId())
				.field("groupId").equal(param.getGroupId())
				.asList();
		BasicDBList docIds = new BasicDBList();
		for (DepartmentDoctor dd : allddList) {
			docIds.add(dd.getDoctorId());
		}
		
		DBObject query = new BasicDBObject();
		query.put("doctorId", new BasicDBObject("$in", docIds));
		query.put("status", "C");
		query.put("groupId", param.getGroupId());
		DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		List<DepartmentDoctorVO> ddvoList = new ArrayList<DepartmentDoctorVO>();
		BasicDBList userIdList = new BasicDBList();
		while (docsor.hasNext()) {
			DBObject obj = docsor.next();
			userIdList.add( (Integer) obj.get("doctorId"));
			
			DepartmentDoctorVO ddoc = new DepartmentDoctorVO();
			ddoc.setGroupDoctorId(obj.get("_id").toString());
			ddoc.setContactWay(obj.get("contactWay") == null ? "" : obj.get("contactWay").toString());
			ddoc.setRemarks(obj.get("remarks") == null ? "" : obj.get("remarks").toString());
			ddoc.setDoctorId( (Integer) obj.get("doctorId"));
			ddoc.setGroupId(obj.get("groupId").toString());
			InviteRelation invite = groupDoctorDao.getInviteInfo(ddoc.getDoctorId(), ddoc.getGroupId());
			if(null != invite) {
				List<?> list = groupDoctorDao.findInviteListById(ddoc.getDoctorId(), ddoc.getGroupId()).getPageData();
				invite.setMyInvite(list == null ? new ArrayList<InviteRelation>() : (List<InviteRelation>) list);
				ddoc.setInviterId(invite.getInviterId());
				ddoc.setInviterName(invite.getName());
			}
			ddoc.setInvite(invite);
			ddvoList.add(ddoc);
		}
		
		DBObject queryUser = new BasicDBObject();
		queryUser.put("_id", new BasicDBObject("$in", userIdList));
		if(null != param.getKeyword()) {
			BasicDBList keyword = new BasicDBList();
			Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
			keyword.add(new BasicDBObject("name", pattern));
			keyword.add(new BasicDBObject("doctor.title", pattern));
			queryUser.put(QueryOperators.OR, keyword);
		}
		
		DBCursor uCursor = dsForRW.getDB().getCollection("user").find(queryUser).sort(new BasicDBObject("modifyTime", -1)).skip(param.getStart()).limit(param.getPageSize());
		
		Query<User> q = dsForRW.createQuery(User.class);
		q.field("_id").in(userIdList);
		if(null != param.getKeyword()) {
			q.or(
					q.criteria("name").contains(param.getKeyword()),
					q.criteria("doctor.title").contains(param.getKeyword())
				);
		}
		Long count = q.countAll();
		
		List<DoctorBasicInfo> doctorList = new ArrayList<DoctorBasicInfo>();
		BasicDBList userIds = new BasicDBList();
		while (uCursor.hasNext()) {
			DBObject obj = uCursor.next();
			DoctorBasicInfo doctor = new DoctorBasicInfo();
//			obj.get("name");
			doctor.setDoctorId(Integer.valueOf(obj.get("_id").toString()));
			doctor.setName(MongodbUtil.getString(obj, "name"));
			doctor.setTelephone(obj.get("telephone").toString());
			doctor.setUserType(Integer.valueOf(obj.get("userType").toString()));
			doctor.setSex(MongodbUtil.getInteger(obj, "sex"));
			if(obj.get("headPicFileName") == null) {
				doctor.setHeadPicFileName("");
				doctor.setHeadPicFilePath("");
			} else {
				doctor.setHeadPicFileName(obj.get("headPicFileName").toString());
				doctor.setHeadPicFilePath(UserHelper.buildHeaderPicPath(doctor.getHeadPicFileName(), doctor.getDoctorId()));
			}
			
			/** 审核状态  add by tanyf 20160627 */
			Integer applyStatus = MongodbUtil.getInteger(obj, "status");
			doctor.setStatus(applyStatus);
			/** end */
			
			DBObject doc = (BasicDBObject) obj.get("doctor");
			if(null != doc) {
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
		
		DBObject queryDepart = new BasicDBObject();
		queryDepart.put("doctorId", new BasicDBObject("$in", userIds));
		queryDepart.put("departmentId", param.getDepartmentId());
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(queryDepart);
		List<DepartmentDoctorVO> ddList = new ArrayList<DepartmentDoctorVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			
			DepartmentDoctorVO dd = new DepartmentDoctorVO();
			dd.setId(obj.get("_id").toString());
			dd.setGroupId(obj.get("groupId") == null ? "" : obj.get("groupId").toString());
			dd.setDepartmentId(obj.get("departmentId").toString());
			dd.setDoctorId(obj.get("doctorId") == null ? null : Integer.valueOf(obj.get("doctorId").toString()));
			dd.setCreatorDate(obj.get("creatorDate") == null ? null : Long.valueOf(obj.get("creatorDate").toString()));
			dd.setUpdator(obj.get("updator") == null ? null : Integer.valueOf(obj.get("updator").toString()));
			dd.setUpdatorDate(obj.get("updatorDate") == null ? null : Long.valueOf(obj.get("updatorDate").toString()));
			for (DoctorBasicInfo doctor : doctorList) {
				if(doctor.getDoctorId().equals(Integer.valueOf(obj.get("doctorId").toString()))) {
					dd.setDoctor(doctor);
					dd.setApplyStatus(doctor.getStatus());
					UserStatus userStatus = UserEnum.UserStatus.getEnum(doctor.getStatus());
					dd.setApplyStatusName(userStatus.getTitle());
					break;
				}
			}
			for (DepartmentDoctorVO ddvo : ddvoList) {
				if(ddvo.getDoctorId().equals(dd.getDoctorId())) {
					dd.setGroupDoctorId(ddvo.getGroupDoctorId());
					dd.setContactWay(ddvo.getContactWay());
					dd.setRemarks(ddvo.getRemarks());
					dd.setInvite(ddvo.getInvite());
					dd.setInviterId(ddvo.getInviterId());
					dd.setInviterName(ddvo.getInviterName());
					break;
				}
			}
			ddList.add(dd);
		}
		
		PageVO page = new PageVO();
        page.setPageData(ddList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.valueOf(uCursor.count()));
        return page;
	}

	/**
     * </p>查找科室下的医生</p>
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public List<Integer> getDepartmentDoctorId(String groupId,List<String> departmentIds,String[] statuses){
        //查询科室下的医生
        List<DepartmentDoctor> allddList = dsForRW.createQuery(DepartmentDoctor.class)
                								  .field("departmentId").in(departmentIds)
                								  .field("groupId").equal(groupId).asList();
        BasicDBList docIds = new BasicDBList();
        if (!CollectionUtils.isEmpty(allddList)) {
			for (DepartmentDoctor dd : allddList) {
				docIds.add(dd.getDoctorId());
			}
		}
        List<Integer> userIds = Lists.newArrayList();
        
        //查询医生集团医生
        DBObject query = new BasicDBObject();
        query.put("doctorId", new BasicDBObject("$in", docIds));
        query.put("groupId", groupId);
        query.put("status", new BasicDBObject("$in",statuses));
        
        
        DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
        while (docsor.hasNext()) {
            DBObject obj = docsor.next();
            
            userIds.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        
        return userIds;
    }
    
    /**
     * </p>查找未分配的医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月23日
     */
    public List<Integer> getUndistributedDoctorId(String groupId){
        
        DBCursor dersor = dsForRW.getDB().getCollection("c_department_doctor").find(new BasicDBObject("groupId", groupId));
        BasicDBList doctorIds = new BasicDBList();
        while (dersor.hasNext()) {
            DBObject obj = dersor.next();
            doctorIds.add(Integer.valueOf(obj.get("doctorId").toString()));
        }
        BasicDBObject query = new BasicDBObject();
        query.put("groupId", groupId);
        query.put("doctorId", new BasicDBObject("$nin", doctorIds));
        query.put("status", "C");
        
        DBCursor docsor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
        List<Integer> docIds = new ArrayList<Integer>();
        while (docsor.hasNext()) {
            DBObject obj = docsor.next();
            docIds.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        return docIds;
    }

    public List<DepartmentDoctor> getDepartmentDoctorByGroupIdAndDoctorId(String groupId, int doctorId) {

    	BasicDBObject query = new BasicDBObject();
    	query.put("groupId", groupId);
    	query.put("doctorId", doctorId);

    	List<DepartmentDoctor> list = dsForRW.createQuery(DepartmentDoctor.class, query).asList();
    	return list;
    }

	@Override
	public void deleteByAlsoId(DepartmentDoctor ddoc) {
		DBObject delete = new BasicDBObject();
		delete.put("departmentId", ddoc.getDepartmentId());
		delete.put("doctorId", ddoc.getDoctorId());
		dsForRW.getDB().getCollection("c_department_doctor").remove(delete);
	}

	@Override
	public void deleteDoctorIdByDepartmentId(Integer... doctorIds) {
		BasicDBList docIdList = new BasicDBList();
		for (Integer docId : doctorIds) {
			docIdList.add(docId);
		}
		BasicDBObject docIdIn = new BasicDBObject();
		docIdIn.put("$in", docIdList);
		dsForRW.getDB().getCollection("c_department_doctor").remove(new BasicDBObject("doctorId", docIdIn));
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDoctorDao#deleteByDoctorIdAndGroupId(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void deleteByDoctorIdAndGroupId(Integer[] doctorIds,String groupId){
		BasicDBList docIdList = new BasicDBList();
		for (Integer docId : doctorIds) {
			docIdList.add(docId);
		}
		BasicDBObject docIdIn = new BasicDBObject();
		docIdIn.put("$in", docIdList);
		
		BasicDBObject query = new BasicDBObject();
    	query.put("groupId", groupId);
    	query.put("doctorId", docIdIn);
    	dsForRW.getDB().getCollection("c_department_doctor").remove(query);
	}

	@Override
	public int getCountDepartmentDoctorByDoctorIds(List<Integer> docList) {
		BasicDBList idList = new BasicDBList();
		for (Integer id : docList) {
			idList.add(id);
		}
		return dsForRW.getDB().getCollection("c_department_doctor").find(new BasicDBObject("doctorId", new BasicDBObject("$in", idList))).count();
	}

	@Override
	public void deleteAllCorrelation(String groupId, Integer doctorId) {
		DBObject delete = new BasicDBObject();
		delete.put("groupId", groupId);
		delete.put("doctorId", doctorId);
		dsForRW.getDB().getCollection("c_department_doctor").remove(delete);
	}

	
	/**
     * </p>通过组织架构查找医生</p>
     * @param departmentId
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    public List<Integer> getDoctorIdsByDepartment(String departmentId){
        DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(
                new BasicDBObject("departmentId",departmentId),
                new BasicDBObject("doctorId",1));
        
        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        
        return list;
    }
	
    /**
     * </p>查找属于该组织架构的医生</p>
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    public Set<Integer> getDoctorIdsByDepartment(List<String> departmentIds,List<Integer> doctorIds){
        BasicDBObject query = new BasicDBObject();
        query.put("departmentId", new BasicDBObject("$in",departmentIds));
        query.put("doctorId", new BasicDBObject("$in",doctorIds));
        
        DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(query,new BasicDBObject("doctorId",1));
        
        Set<Integer> list = new HashSet<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        return list;
    }

	@Override
	public int getCountByGroupIds(String[] ids) {
		BasicDBList idList = new BasicDBList();
		for (String id : ids) {
			idList.add(id);
		}
		
		return (int) dsForRW.getDB().getCollection("c_group_user").count(new BasicDBObject("groupId", new BasicDBObject("$in", idList)));
	}

	/**
	 * 在线且问诊数多的排前面
	 * 
	 * @return
	 */
	private List<GroupDoctorInfoVO> sortOnlineInquiryNum(List<GroupDoctorInfoVO> groupDoctorInfoVOList) {
		
//		List<GroupDoctorInfoVO> groupDoctorInfoVOList = groupDoctorDao.findUsersBydocId(onlineUserIds, param);

		if(groupDoctorInfoVOList != null && groupDoctorInfoVOList.size() > 0) {
			// TODO 在这里排序
			// 问诊人数从高到低排序
			Collections.sort(groupDoctorInfoVOList, new Comparator<GroupDoctorInfoVO>() {
				public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
					if (arg1.getInquiryNum() == null) {
						arg1.setInquiryNum(new Integer(0));
					}

					if (arg0.getInquiryNum() == null) {
						arg0.setInquiryNum(new Integer(0));
					}

					return arg1.getInquiryNum().compareTo(arg0.getInquiryNum());
				}
			});
		}

		return groupDoctorInfoVOList;
	}

	/**
	 * 不在线按职称高低排序
	 * 
	 * @return
	 */
	private List<GroupDoctorInfoVO> sortOfflineTitleRank(List<GroupDoctorInfoVO> groupDoctorInfoVOList) {
		
//		List<GroupDoctorInfoVO> groupDoctorInfoVOList = groupDoctorDao.findUsersBydocId(offlineUserIds, param);
		
		if(groupDoctorInfoVOList != null && groupDoctorInfoVOList.size()>0) {
			// TODO 在这里排序
			// 医生的职称等级从高到低排序(即从1到100)
			Collections.sort(groupDoctorInfoVOList, new Comparator<GroupDoctorInfoVO>() {
				public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
					if (arg1.titleRank == null) {
						arg1.titleRank = "24";
					}

					if (arg0.titleRank == null) {
						arg0.titleRank = "24";
					}

					Integer _arg1 = Integer.valueOf(arg1.titleRank);
					Integer _arg0 = Integer.valueOf(arg0.titleRank);

					return _arg0.compareTo(_arg1);
				}
			});
		}
		
		return groupDoctorInfoVOList;
	}
	
	@Override
	public PageVO findDocGroupDoctorInfo(GroupSearchParam param) {
		
		DBObject query = new BasicDBObject();
		PageVO pageVO = new PageVO();
		List<Integer> docIds = new ArrayList<Integer>();
		query.put("status", "C");
		
		List<GroupDoctorInfoVO> groupDoctorInfoVOList = new ArrayList<GroupDoctorInfoVO>();
		
		boolean specialistId = false;
		
		if(StringUtil.isNotBlank(param.getDocGroupId())) {
			query.put("groupId", param.getDocGroupId());
			
			List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();

			List<Integer> all_doctorUserrIds = new ArrayList<Integer>();
			// 在线doctorUserId
			List<Integer> online_doctorUserrIds = new ArrayList<Integer>();

			// 不在线doctorUserId
			List<Integer> offline_doctorUserIds = new ArrayList<Integer>();

			// add list
			for (GroupDoctor groupDoctor : groupDoctorList) {
				String onLineState = null;
				onLineState = groupDoctor.getOnLineState();
				all_doctorUserrIds.add(groupDoctor.getDoctorId());
				if (onLineState != null && onLineState.equalsIgnoreCase("1")) {
					// 在线
					online_doctorUserrIds.add(Integer.valueOf(groupDoctor.getDoctorId()));
				}else{
					// 不在线
					offline_doctorUserIds.add(Integer.valueOf(groupDoctor.getDoctorId()));
				}
			}
			//查询医生相关信息，过滤掉user status!=1的 异常数据
			List<GroupDoctorInfoVO> groupDoctorInfoVOList_all = groupDoctorDao.findUsersBydocId(all_doctorUserrIds, param);
			List<GroupDoctorInfoVO> groupDoctorInfoVOList_online = new ArrayList<GroupDoctorInfoVO>();
			List<GroupDoctorInfoVO> groupDoctorInfoVOList_offline = new ArrayList<GroupDoctorInfoVO>();
			// set onLineState
			for(GroupDoctorInfoVO __a : groupDoctorInfoVOList_all){
				if(online_doctorUserrIds.contains(__a.getDoctorId())){
					__a.setOnLineState("1");
					groupDoctorInfoVOList_online.add(__a);
				}else{
					__a.setOnLineState("2");
					groupDoctorInfoVOList_offline.add(__a);
				}
				
			}
			
			// sort
			groupDoctorInfoVOList_online = sortOnlineInquiryNum(groupDoctorInfoVOList_online);
			groupDoctorInfoVOList_offline = sortOfflineTitleRank(groupDoctorInfoVOList_offline);

			// add all
			groupDoctorInfoVOList.addAll(groupDoctorInfoVOList_online);
			groupDoctorInfoVOList.addAll(groupDoctorInfoVOList_offline);
			
			
		}else if(StringUtil.isNotBlank(param.getSpecialistId())) {
			// 传了 - 医生科室ID
			
			specialistId = true;
			
			query.put("departmentId", param.getSpecialistId());
			DBCollection collection = dsForRW.getDB().getCollection("c_department_doctor");
			DBCursor docsor = collection.find(query);
			while (docsor.hasNext()) {
	            DBObject obj = docsor.next();
	            docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
	        }
			pageVO.setTotal(collection.count(query));
		}
		
		if(groupDoctorInfoVOList.size() > 0 || docIds.size() > 0) {
			if (specialistId) {
				groupDoctorInfoVOList = groupDoctorDao.findUsersBydocId(docIds,param);	
			}
			pageVO.setPageData(groupDoctorInfoVOList);
			pageVO.setTotal(param.getTotal());
		}else {
			pageVO.setTotal(0l);
		}
		
        pageVO.setPageIndex(param.getPageIndex());
        pageVO.setPageSize(param.getPageSize());
		return pageVO;
	}
    
	/**
	 * 这个方法被两个接口调用
	 * http://192.168.3.7:8091/groupSearch/findDoctorOnlineByGroupAndDept
	 * http://192.168.3.7:8091/groupSearch/findDoctorOnlineByGroup
	 */
	public PageVO findDocGroupOnlineDoctorInfo(GroupSearchParam param) {
		
		// 修改排序规则：在线且问诊数多的拍前，不在线按职称高低排序
		
		DBObject query = new BasicDBObject();
		PageVO pageVO = new PageVO();
		List<Integer> docIds = new ArrayList<Integer>();
		query.put("groupId", param.getDocGroupId());
//		query.put("onLineState", "1"); // //在线状态1，在线，2离线
		query.put("status", "C");
		DBObject order_stor =  new BasicDBObject();
//		order_stor.put("onLineTime", -1); // 排序用sort,相当于order by,升序(从小到大)用1表示，降序用-1
		
		if(StringUtil.isNotBlank(param.getDeptName())) {
			// 医生科室名称不为空时，调用这个http://192.168.3.7:8091/groupSearch/findDoctorOnlineByGroupAndDept
			// 要返回的结果
			List<GroupDoctorInfoVO> gorupDocInfoList_result = new ArrayList<GroupDoctorInfoVO>();
			// 在线doctorUserId
			List<Integer> online_doctorUserrIds = new ArrayList<Integer>();
			// 不在线doctorUserId
			List<Integer> offline_doctorUserIds = new ArrayList<Integer>();
			// 先查在线的
//			query.put("onLineState", "1"); // //在线状态1，在线，2离线
			query.put("deptName", param.getDeptName());
			List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();

			//			while (docsor.hasNext()) {
			//	            DBObject obj = docsor.next();
			for (GroupDoctor groupDoctor : groupDoctorList) {
				String onLineState = null;
				onLineState = groupDoctor.getOnLineState();
				//	            if (obj.get("onLineState") != null) {
				//	            	onLineState = obj.get("onLineState").toString();
				//	            }
				if (onLineState != null && onLineState.equalsIgnoreCase("1")) {
					// 在线
					online_doctorUserrIds.add(Integer.valueOf(groupDoctor.getDoctorId()));	
				}else{
					// 不在线
					offline_doctorUserIds.add(Integer.valueOf(groupDoctor.getDoctorId()));
				}
			}

			List<GroupDoctorInfoVO> gorupDocInfoList_online = groupDoctorDao.findUsersBydocId(online_doctorUserrIds, param);
			if(gorupDocInfoList_online != null && gorupDocInfoList_online.size() > 0) {
				// TODO 在这里排序
		        // 问诊人数从高到低排序
		        Collections.sort(gorupDocInfoList_online, new Comparator<GroupDoctorInfoVO>() {
		        	public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
		        		if (arg1.getInquiryNum() == null) {
		        			arg1.setInquiryNum(new Integer(0));
		        		}
		        		
		        		if (arg0.getInquiryNum() == null) {
		        			arg0.setInquiryNum(new Integer(0));
		        		}
		        		
		        		return arg1.getInquiryNum().compareTo(arg0.getInquiryNum());
		        	}
		        });

		        // set onLineState
		        for (GroupDoctorInfoVO __a : gorupDocInfoList_online) {
		        	__a.setOnLineState("1");
		        }

		        // add all
		        gorupDocInfoList_result.addAll(gorupDocInfoList_online);
			}

			List<GroupDoctorInfoVO> gorupDocInfoList_offline = groupDoctorDao.findUsersBydocId(offline_doctorUserIds, param);
			if(gorupDocInfoList_offline != null && gorupDocInfoList_offline.size()>0) {
				// TODO 在这里排序
				// 医生的职称等级从高到低排序(即从1到100)
				Collections.sort(gorupDocInfoList_offline, new Comparator<GroupDoctorInfoVO>() {
					public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
						if (arg1.titleRank == null) {
							arg1.titleRank = "0";
						}

						if (arg0.titleRank == null) {
							arg0.titleRank = "0";
						}

						Integer _arg1 = Integer.valueOf(arg1.titleRank);
						Integer _arg0 = Integer.valueOf(arg0.titleRank);

						return _arg0.compareTo(_arg1);
					}
				});

				// set onLineState
				for (GroupDoctorInfoVO __a : gorupDocInfoList_offline) {
					__a.setOnLineState("2");
				}

				// add all
				gorupDocInfoList_result.addAll(gorupDocInfoList_offline);
				
			}
			
			pageVO.setPageData(gorupDocInfoList_result);
		
		}else {
			
			// 医生科室名称为空时，调用这个http://192.168.3.7:8091/groupSearch/findDoctorOnlineByGroup
			// 根据科室分组获取在线医生
//			query.put("onLineState", "1"); // //在线状态1，在线，2离线
			query.put("deptName", new BasicDBObject("$exists", true)); // 存在
			
			// 按科室将doctorUserId分组
			List<DeptByDoctorVO> deptByDoctorVOList = new ArrayList<DeptByDoctorVO>();
			
			// 这是要返回的
			List<OnlineDocByGorupVO> onlineList_return = new ArrayList<OnlineDocByGorupVO>();
			
			List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();
			
			for (GroupDoctor groupDoctor : groupDoctorList) {
				String deptName = groupDoctor.getDeptName();
				if (deptName != null && deptName.isEmpty() == false) {
					boolean isFind = false;
					for (DeptByDoctorVO deptByDoctorVO : deptByDoctorVOList) {
						// 比较deptName
						if (deptByDoctorVO.getDeptName() != null && deptName.equalsIgnoreCase(deptByDoctorVO.getDeptName())) {
							// 相等
							isFind = true;
							// add groupDoctor
							deptByDoctorVO.getGroupDoctorList().add(groupDoctor);
							break;
						}
					}
					if(isFind == false) {
						// 找不着，则new -> add
						DeptByDoctorVO deptByDoctorVO = new DeptByDoctorVO();
						deptByDoctorVO.setDeptName(deptName);
						// add groupDoctor
						deptByDoctorVO.getGroupDoctorList().add(groupDoctor);
						deptByDoctorVOList.add(deptByDoctorVO);
					}
				}
			}
			// 将List<DeptByDoctorVO>转成List<OnlineDocByGorupVO>
			//deptByDoctorVO:医生集团的某个科室和科室里的医生
			for(DeptByDoctorVO deptByDoctorVO : deptByDoctorVOList) {
				List<Integer> doctorUserIds = new ArrayList<Integer>();
				Map<Integer,String> onLineStateMap=new HashMap<Integer,String> ();
				for (GroupDoctor _g : deptByDoctorVO.getGroupDoctorList()) {
					doctorUserIds.add(_g.getDoctorId());
					onLineStateMap.put(_g.getDoctorId(), _g.getOnLineState());
				}
				List<GroupDoctorInfoVO> groupDoctorInfoVOList = groupDoctorDao.findUsersAndStateBydocId(doctorUserIds, onLineStateMap);
				OnlineDocByGorupVO onlineDocByGorupVO = new OnlineDocByGorupVO();
				onlineDocByGorupVO.setDeptName(deptByDoctorVO.getDeptName());
				onlineDocByGorupVO.setGroupDoctorInfoLists(groupDoctorInfoVOList);
				onlineDocByGorupVO.setDocIds(null);
				onlineDocByGorupVO.setParamMap(null);
				onlineList_return.add(onlineDocByGorupVO);
			}
			// TODO 还有一项：集团科室排序：按该科室全部医生的就诊总人数排序
			// List<OnlineDocByGorupVO> onlineList_return;
			// 排序，问诊人数多的排在前面。
			Collections.sort(onlineList_return, new Comparator<OnlineDocByGorupVO>() {
				public int compare(OnlineDocByGorupVO arg0, OnlineDocByGorupVO arg1) {
					Integer a_InquiryNum = new Integer(0);
					Integer b_InquiryNum = new Integer(0);
					for (GroupDoctorInfoVO _a : arg0.getGroupDoctorInfoLists()) {
						int number = 0;
						if (_a.getInquiryNum() != null) {
							number = _a.getInquiryNum();
						}
						a_InquiryNum += number;
					}
					for (GroupDoctorInfoVO _b : arg1.getGroupDoctorInfoLists()) {
						int number = 0;
						if (_b.getInquiryNum() != null) {
							number = _b.getInquiryNum();
						}
						b_InquiryNum += number;
					}
					
					return b_InquiryNum.compareTo(a_InquiryNum);
				}
			});
			
			// 排序
			// 修改排序规则：在线且问诊数多的拍前，不在线按职称高低排序
			for(OnlineDocByGorupVO onlineDocByGorupVO : onlineList_return) {
				
				// 分组：在线+不在线
				List<GroupDoctorInfoVO> GroupDoctorInfoVO_online = new ArrayList<GroupDoctorInfoVO>();
				List<GroupDoctorInfoVO> GroupDoctorInfoVO_offline = new ArrayList<GroupDoctorInfoVO>();
				
				for (GroupDoctorInfoVO __g : onlineDocByGorupVO.getGroupDoctorInfoLists()) {
		            if (__g.getOnLineState() != null && __g.getOnLineState().equalsIgnoreCase("1")) {
		            	// 在线
		            	GroupDoctorInfoVO_online.add(__g);
		            }else{
		            	// 不在线
		            	GroupDoctorInfoVO_offline.add(__g);
		            }
				}
				onlineDocByGorupVO.getGroupDoctorInfoLists().clear();
				// TODO 在这里排序
		        // 在线 -> 问诊人数从高到低排序
		        Collections.sort(GroupDoctorInfoVO_online, new Comparator<GroupDoctorInfoVO>() {
		        	public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
		        		if (arg1.getInquiryNum() == null) {
		        			arg1.setInquiryNum(new Integer(0));
		        		}
		        		if (arg0.getInquiryNum() == null) {
		        			arg0.setInquiryNum(new Integer(0));
		        		}
		        		return arg1.getInquiryNum().compareTo(arg0.getInquiryNum());
		        	}
		        });
		        
		        onlineDocByGorupVO.getGroupDoctorInfoLists().addAll(GroupDoctorInfoVO_online);
		        // TODO 在这里排序
		        // 不在线 -> 医生的职称等级从高到低排序(即从1到100)
		        Collections.sort(GroupDoctorInfoVO_offline, new Comparator<GroupDoctorInfoVO>() {
		        	public int compare(GroupDoctorInfoVO arg0, GroupDoctorInfoVO arg1) {
		        		if (arg1.titleRank == null) {
		        			arg1.titleRank = "0";
		        		}

		        		if (arg0.titleRank == null) {
		        			arg0.titleRank = "0";
		        		}

		        		Integer _arg1 = Integer.valueOf(arg1.titleRank);
		        		Integer _arg0 = Integer.valueOf(arg0.titleRank);

		        		return _arg0.compareTo(_arg1);
		        	}
		        });
		        onlineDocByGorupVO.getGroupDoctorInfoLists().addAll(GroupDoctorInfoVO_offline);

			}
			
			if(onlineList_return.size()>0) {
				pageVO.setPageData(onlineList_return);
			}
			pageVO.setTotal(0l);
		}
		
		pageVO.setTotal(param.getTotal());
        pageVO.setPageIndex(param.getPageIndex());
        pageVO.setPageSize(param.getPageSize());
		return pageVO;
	}
	
	
	public List<OnlineDocByGorupVO> searchGroupDocInfo(List<OnlineDocByGorupVO> onlineList) {
		
		for(OnlineDocByGorupVO vo : onlineList) {
			List<GroupDoctorInfoVO> gorupDocts = groupDoctorDao.findUsersAndStateBydocId(vo.getDocIds(),null);
			for(GroupDoctorInfoVO docInfo :gorupDocts ) {
				docInfo.setIsFree(vo.getParamMap().get(docInfo.getDoctorId()));
			}
			vo.setGroupDoctorInfoLists(gorupDocts);
			vo.setDocIds(null);
			vo.setParamMap(null);
		}
		return onlineList;
	}
	
	@Override
	public DepartmentDoctor updateDepartment(DepartmentDoctorParam param) {
		UpdateOperations<DepartmentDoctor> ops = dsForRW.createUpdateOperations(DepartmentDoctor.class);
		Query<DepartmentDoctor> query = dsForRW.createQuery(DepartmentDoctor.class).field("groupId").equal(param.getGroupId())
				.field("doctorId").equal(param.getDoctorId());
		if (query.get() == null) {
			ops.set("creator", param.getDoctorId());
			ops.set("creatorDate", System.currentTimeMillis());
			ops.set("updator", param.getDoctorId());
			ops.set("updatorDate", System.currentTimeMillis());
		}
		ops.set("departmentId", param.getDepartmentId());
		DepartmentDoctor departDoc = dsForRW.findAndModify(query, ops, false, true);
		return departDoc;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDoctorDao#getDepartmentFullName(java.lang.String, java.util.List)
	 */
	@Override
	public Map<Integer, String> getDepartmentFullName(String groupId,List<Integer> doctorIdList){
    	Map<Integer, String> resultMap = new HashMap<Integer, String> ();
    	
    	List<DepartmentVO> departmentList = deparDao.findListById(groupId, null);
    	
    	DBObject queryDepart = new BasicDBObject();
		queryDepart.put("doctorId", new BasicDBObject("$in", doctorIdList));
		queryDepart.put("groupId", groupId);
		
		DBCursor depDocCursor = dsForRW.getDB().getCollection("c_department_doctor").find(queryDepart);
		
		
		while (depDocCursor.hasNext()) {
			DBObject obj = depDocCursor.next();
			
			String departmentFullName = findFullNameById(departmentList, obj.get("departmentId").toString());
			if (obj.get("doctorId") != null) {
				resultMap.put(MongodbUtil.getInteger(obj, "doctorId"), departmentFullName);
				
			}
			
		}
		return resultMap;
	}
	
	/**
	 * 读取 组织全路径
	 * @param departList
	 * @param id
	 * @return
	 *@author wangqiao
	 *@date 2016年2月18日
	 */
	private String findFullNameById(List<DepartmentVO> departList, String id) {
		String fullName = null;
		for (DepartmentVO depart : departList) {
			if (depart.getId().equals(id)) {
				// 如果父节点不为“0”，这说明要继续查找
				if (!depart.getParentId().equals("0")) {
					fullName = findFullNameById(departList, depart.getParentId()) + "/" + depart.getName();
				} else {
					fullName = depart.getName();
				}
				break;
			}

		}
		return fullName;
	}
	
}
