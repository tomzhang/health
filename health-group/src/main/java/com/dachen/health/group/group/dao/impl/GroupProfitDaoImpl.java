package com.dachen.health.group.group.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupProfitDao;
import com.dachen.health.group.group.entity.param.GroupProfitParam;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.health.group.group.entity.po.GroupProfitConfig;
import com.dachen.health.group.group.entity.vo.GroupProfitVO;
import com.dachen.util.MongodbUtil;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * ProjectName： health-group<br>
 * ClassName： GroupProfitDaoImpl<br>
 * Description：集团抽成关系dao实现类 <br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
@Repository
public class GroupProfitDaoImpl extends NoSqlRepository implements IGroupProfitDao {

	@Autowired
    protected IGroupDoctorDao groupDoctorDao;

	@Autowired
    protected IBaseUserDao baseUserDao;

	@Resource
    protected IDepartmentDao departmentDao;

	/**
	 * </p>
	 * 集团创建者邀请的人的树
	 * </p>
	 * 
	 * @param groupId
	 * @return
	 * @author fanp
	 * @date 2015年8月28日
	 */
	public List<ExtTreeNode> getGroupProfit(String groupId) {

		List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
		List<Integer> docIds = new ArrayList<Integer>();

		BasicDBList userIds = new BasicDBList();

		// 查找子节点
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);

		DBObject project = new BasicDBObject();
		project.put("_id", 1);
		project.put("parentId", 1);
		project.put("doctorId", 1);
		project.put("config", 1);
		project.put("groupProfit", 1);
		project.put("parentProfit", 1);

		DBCursor cursor = dsForRW.getDB().getCollection("c_group_profit").find(query, project);
		// DBCursor cursor =
		// dsForRW.getDB().getCollection("c_group_profit").find(query);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			ExtTreeNode node = new ExtTreeNode();

			// doctorId=0的排除掉
			if (obj.get("doctorId") == null || Integer.parseInt(obj.get("doctorId").toString()) == 0) {
				continue;
			}
//			BaseUserVO user = baseUserDao.getUser(Integer.parseInt(obj.get("doctorId").toString()));
//			if (user != null && user.getStatus() != null && UserEnum.UserStatus.normal.getIndex()==user.getStatus().intValue()){// 审核通过的医生
				node.setId(obj.get("doctorId").toString());
				node.setParentId(obj.get("parentId").toString());
				// 读取抽成比例关系
				Map<String, Object> attr = readProfitToMap(obj);

				node.setAttributes(attr);

				nodeList.add(node);
				docIds.add(Integer.parseInt(node.getId()));
				userIds.add(Integer.valueOf(obj.get("doctorId").toString()));
//			}
		}

		List<BaseUserVO> users = baseUserDao.getByIds(docIds.toArray(new Integer[] {}),null/*UserEnum.UserStatus.normal.getIndex()*/);// 审核通过的用户
		
		
		for (BaseUserVO user : users) {
			// 设置头像
			for (ExtTreeNode node : nodeList) {
				if (Integer.parseInt(node.getId()) == user.getUserId().intValue()) {
					node.setName(user.getName());
					node.getAttributes().put("headPicFileName", user.getHeadPicFileName());
					node.getAttributes().put("hospital", user.getHospital());
					node.getAttributes().put("departments", user.getDepartments());
					node.getAttributes().put("title", user.getTitle());
					break;
				}
			}
		}

		// 新增两个
		// @apiSuccess {String} attributes.contactWay 联系方式
		// @apiSuccess {String} attributes.departmentFullName 组织全称
		// 从c_group_doctor里找的contactWay
		List<GroupDoctor> groupDoctors = groupDoctorDao.findGroupDoctorsByGroupId(groupId);
		if (groupDoctors != null && groupDoctors.isEmpty() == false) {
			for (GroupDoctor groupDoctor : groupDoctors) {
				for (ExtTreeNode node : nodeList) {
					if (Integer.parseInt(node.getId()) == groupDoctor.getDoctorId().intValue()) {
						node.getAttributes().put("contactWay", groupDoctor.getContactWay());
						break;
					}
				}
			}
		}

		Map<String, String> fullNameMap = getFullNameMap(groupId, userIds);
		for (ExtTreeNode node : nodeList) {
			node.getAttributes().put("departmentFullName", fullNameMap.get(node.getId()));
		}

		return ExtTreeUtil.buildTree(nodeList);
	}

	/**
	 * 读取db查询结果中的 抽成比例 放到map中
	 * 
	 * @param obj
	 * @author wangqiao
	 * @date 2015年12月28日
	 */
	private Map<String, Object> readProfitToMap(DBObject obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 兼容老数据
		// map.put("parentProfit", MongodbUtil.getInteger(obj, "parentProfit"));
		// map.put("groupProfit", MongodbUtil.getInteger(obj, "groupProfit"));
		// 读取抽成比例
		if (obj != null && obj.get("config") != null) {
			DBObject config = (DBObject) obj.get("config");
			map.put("textParentProfit", MongodbUtil.getInteger(config, "textParentProfit"));
			map.put("textGroupProfit", MongodbUtil.getInteger(config, "textGroupProfit"));

			map.put("phoneParentProfit", MongodbUtil.getInteger(config, "phoneParentProfit"));
			map.put("phoneGroupProfit", MongodbUtil.getInteger(config, "phoneGroupProfit"));

			map.put("carePlanParentProfit", MongodbUtil.getInteger(config, "carePlanParentProfit"));
			map.put("carePlanGroupProfit", MongodbUtil.getInteger(config, "carePlanGroupProfit"));

			map.put("clinicParentProfit", MongodbUtil.getInteger(config, "clinicParentProfit"));
			map.put("clinicGroupProfit", MongodbUtil.getInteger(config, "clinicGroupProfit"));
			//添加会诊
			map.put("consultationGroupProfit", MongodbUtil.getInteger(config, "consultationGroupProfit"));
			map.put("consultationParentProfit", MongodbUtil.getInteger(config, "consultationParentProfit"));
			// 名医面对面
			map.put("appointmentGroupProfit", MongodbUtil.getInteger(config, "appointmentGroupProfit"));
			map.put("appointmentParentProfit", MongodbUtil.getInteger(config, "appointmentParentProfit"));
			// 收费项
			map.put("chargeItemGroupProfit", MongodbUtil.getInteger(config, "chargeItemGroupProfit"));
			map.put("chargeItemParentProfit", MongodbUtil.getInteger(config, "chargeItemParentProfit"));
		}
		return map;
	}

	/**
	 * 存储userId：departmentFullName键值对
	 * 
	 * @param groupId
	 * @param userIds
	 * @return
	 */
	private Map<String, String> getFullNameMap(String groupId, List<?> userIds) {
		Map<String, String> resultMap = new HashMap<String, String>();

		List<DepartmentVO> departmentList = departmentDao.findListById(groupId, null);

		DBObject queryDepart = new BasicDBObject();
		queryDepart.put("doctorId", new BasicDBObject("$in", userIds));
		queryDepart.put("groupId", groupId);

		DBCursor depDocCursor = dsForRW.getDB().getCollection("c_department_doctor").find(queryDepart);

		while (depDocCursor.hasNext()) {
			DBObject obj = depDocCursor.next();

			String departmentFullName = findFullNameById(departmentList, obj.get("departmentId").toString());
			if (obj.get("doctorId") != null) {
				resultMap.put(obj.get("doctorId").toString(), departmentFullName);
			}
		}
		return resultMap;
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupProfitDao#getChildrenCount(java.
	 * lang.String, java.util.List)
	 */
	@Override
	public Map<Integer, Integer> getChildrenCount(String groupId, List<?> userIds) {
		Map<Integer, Integer> childrenCount = new HashMap<Integer, Integer>();

		DBObject query = new BasicDBObject();
		query.put("parentId", new BasicDBObject("$in", userIds));
		query.put("groupId", groupId);

		DBObject orderBy = new BasicDBObject();
		orderBy.put("parentId", 1);

		DBCursor cursor = dsForRW.getDB().getCollection("c_group_profit").find(query).sort(orderBy);
		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		List<String> userIdList = null;
		for (; cursor.hasNext();) {
			DBObject obj = cursor.next();
			
			/***add　by  liwei  *****/
			Object doctorObj = obj.get("doctorId");
			if (doctorObj == null || Integer.parseInt(doctorObj.toString()) == 0) {
				continue;
			}
			if (!tempMap.containsKey(obj.get("parentId").toString())) {
				userIdList = new ArrayList<String>();
				userIdList.add(doctorObj.toString());
				tempMap.put(obj.get("parentId").toString(), userIdList);
			} else {
				userIdList.add(doctorObj.toString());
			}
		}
		for (Iterator<String> it = tempMap.keySet().iterator(); it.hasNext();) {
			String userId = it.next();
			childrenCount.put(Integer.valueOf(userId), tempMap.get(userId).size());
		}
		return childrenCount;
	}

	/**
	 * </p>
	 * 查找下级抽成关系
	 * </p>
	 * 
	 * @return
	 * @author fanp
	 * @date 2015年9月6日
	 */
	// public PageVO getGroupProfit(GroupProfitParam param){
	// DBObject query = new BasicDBObject();
	// query.put("groupId", param.getGroupId());
	// query.put("parentId", param.getParentId());
	//
	// DBObject project = new BasicDBObject();
	// project.put("_id", 1);
	// project.put("groupProfit", 1);
	// project.put("parentProfit", 1);
	// project.put("doctorId", 1);
	// project.put("config", 1);
	//
	// DBCollection collection =
	// dsForRW.getDB().getCollection("c_group_profit");
	// DBCursor cursor =
	// collection.find(query,project).skip(param.getStart()).limit(param.getPageSize());
	//
	// List<Map<String, Object>> list = getGroupProfitList(param.getGroupId(),
	// cursor);
	//
	// PageVO page = new PageVO();
	// page.setPageData(list);
	// page.setPageIndex(param.getPageIndex());
	// page.setPageSize(param.getPageSize());
	// page.setTotal(collection.count(query));
	//
	// return page;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#
	 * getGroupProfitByParentId(java.lang.String, java.lang.Integer,
	 * java.lang.Integer, java.lang.Integer)
	 */
	public List<Map<String, Object>> getGroupProfitByParentId(String groupId, Integer parentId, Integer pageIndex,
			Integer pageSize) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("parentId", parentId);

		DBObject project = new BasicDBObject();
		project.put("_id", 1);
		project.put("doctorId", 1);
		project.put("config", 1);

		DBCollection collection = dsForRW.getDB().getCollection("c_group_profit");
		DBCursor cursor = collection.find(query, project).skip(pageIndex * pageSize).limit(pageSize);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			// 读取抽成比例
			Map<String, Object> map = readProfitToMap(obj);
			map.put("id", MongodbUtil.getInteger(obj, "doctorId"));
			list.add(map);
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#
	 * countGroupProfitByParentId(java.lang.String, java.lang.Integer)
	 */
	public long countGroupProfitByParentId(String groupId, Integer parentId) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("parentId", parentId);

		DBObject project = new BasicDBObject();
		project.put("_id", 1);
		project.put("doctorId", 1);
		project.put("config", 1);

		DBCollection collection = dsForRW.getDB().getCollection("c_group_profit");

		return collection.count(query);
	}

	/**
	 * 获取收益抽成集合
	 * 
	 * @return
	 */
	// private List<Map<String, Object>> getGroupProfitList(String groupId ,
	// DBCursor cursor) {
	// List<Integer> ids = new ArrayList<Integer>();
	// List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	// while (cursor.hasNext()) {
	// DBObject obj = cursor.next();
	// //读取抽成比例
	// Map<String,Object> map = readProfitToMap(obj);
	// map.put("id", MongodbUtil.getInteger(obj, "doctorId"));
	// list.add(map);
	// ids.add(MongodbUtil.getInteger(obj, "doctorId"));
	// }
	//
	// //设置用户名，头像，医生号，职称，联系方式，备注
	// if (ids.size() > 0) {
	// List<BaseUserVO> userList = baseUserDao.getByIds(ids.toArray(new
	// Integer[] {}));
	// List<GroupDoctorVO> gdoctorList = groupDoctorDao.getByIds(groupId, ids);
	// Map<String, String> fullNameMap = getFullNameMap(groupId, ids);
	// Map<Integer, Integer> childrenCountMap = getChildrenCount(groupId, ids);
	//
	// for (BaseUserVO user : userList) {
	// for (Map<String, Object> map : list) {
	// if (user.getUserId().equals(map.get("id"))) {
	// map.put("name", user.getName());
	// map.put("headPicFileName", user.getHeadPicFileName());
	// map.put("doctorNum", user.getDoctorNum());
	// map.put("hospital", user.getHospital());
	// map.put("departments", user.getDepartments());
	// map.put("title", user.getTitle());
	//
	// break;
	// }
	// }
	// }
	//
	// for (GroupDoctorVO user : gdoctorList) {
	// for (Map<String, Object> map : list) {
	// if (user.getDoctorId().equals(map.get("id"))) {
	// map.put("contactWay", user.getContactWay());
	// map.put("remarks", user.getRemarks());
	//
	// break;
	// }
	// }
	// }
	//
	// for (Map<String, Object> map : list) {
	// map.put("departmentFullName", fullNameMap.get(map.get("id").toString()));
	// map.put("childrenCount",
	// childrenCountMap.get(Integer.valueOf(map.get("id").toString())) == null ?
	// 0
	// : childrenCountMap.get(Integer.valueOf(map.get("id").toString())));
	// }
	//
	// }
	//
	// Collections.sort(list, new SortChineseName());
	// return list;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupProfitDao#searchByKeyword(java.
	 * lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Map<String, Object>> searchByKeyword(String searchKey, String groupId, Integer pageIndex,
			Integer pageSize) {
		DBObject query = getDBQuerySearchByKeyword(searchKey,groupId);
		
		DBObject project = new BasicDBObject();
		project.put("_id", 1);
		project.put("config", 1);
		project.put("doctorId", 1);
		
		DBCollection collection = dsForRW.getDB().getCollection("c_group_profit");
		DBCursor profitCursor = collection.find(query, project).skip(pageIndex * pageSize).limit(pageSize);
		


		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (profitCursor.hasNext()) {
			DBObject obj = profitCursor.next();
			// 读取抽成比例
			Map<String, Object> map = readProfitToMap(obj);
			map.put("id", MongodbUtil.getInteger(obj, "doctorId"));
			list.add(map);
		}

		return list;
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#countSearchByKeyword(java.lang.String, java.lang.String)
	 */
	@Override
	public long countSearchByKeyword(String searchKey, String groupId) {
		DBObject query = getDBQuerySearchByKeyword(searchKey,groupId);
		DBCollection collection = dsForRW.getDB().getCollection("c_group_profit");
		
		
		return collection.count(query);
	}

	/**
	 * 查询搜索的 DB Query
	 * @param searchKey
	 * @param groupId
	 * @return
	 * @author wangqiao
	 * @date 2016年2月19日
	 */
	private DBObject getDBQuerySearchByKeyword(String searchKey, String groupId) {
		DBObject query = new BasicDBObject();
		BasicDBList dbList = new BasicDBList();
		DBObject project = new BasicDBObject();
		// 关键字存在时，需要匹配医生名字把医生列表过滤出来
		if (searchKey != null && !StringUtils.isEmpty(searchKey)) {
			// 查询user信息 关键字匹配 医生名字
			Pattern pattern = Pattern.compile("^.*" + searchKey + ".*$", Pattern.CASE_INSENSITIVE);
			query.put("name", pattern);
			query.put("userType", UserType.doctor.getIndex());

			project.put("_id", 1);
			DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, project);//FIXME 直接查询user信息不够内聚
			for (; cursor.hasNext();) {
				DBObject obj = cursor.next();
				dbList.add(obj.get("_id"));
			}
		}

		// 在医生结果中 查询 抽成信息
		query = new BasicDBObject();
		query.put("groupId", groupId);
		// 关键字存在时，需要匹配医生列表
		if (searchKey != null && !StringUtils.isEmpty(searchKey)) {
			query.put("doctorId", new BasicDBObject("$in", dbList));
		}

		return query;
	}

	/**
	 * 根据key（名字）搜索所有符合的数据
	 * 
	 * @param param
	 * @param key
	 * @return
	 */
	// public PageVO searchByKeyword(GroupProfitParam param) {
	//
	// Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$",
	// Pattern.CASE_INSENSITIVE);
	// DBObject query = new BasicDBObject();
	// query.put("name", pattern);
	// query.put("userType", UserType.doctor.getIndex());
	// DBObject project = new BasicDBObject();
	// project.put("_id", 1);
	// DBCursor cursor = dsForRW.getDB().getCollection("user").find(query,
	// project);
	//
	// BasicDBList dbList = new BasicDBList();
	// for (;cursor.hasNext();) {
	// DBObject obj = cursor.next();
	// dbList.add(obj.get("_id"));
	// }
	//
	// query = new BasicDBObject();
	// query.put("groupId", param.getGroupId());
	// query.put("doctorId", new BasicDBObject("$in", dbList));
	//
	// project = new BasicDBObject();
	// project.put("_id", 1);
	// project.put("groupProfit", 1);
	// project.put("parentProfit", 1);
	// project.put("config", 1);
	// project.put("doctorId", 1);
	//
	// DBCollection collection =
	// dsForRW.getDB().getCollection("c_group_profit");
	// cursor = collection.find(query,
	// project).skip(param.getStart()).limit(param.getPageSize());
	//
	// List<Map<String, Object>> list = getGroupProfitList(param.getGroupId(),
	// cursor);
	//
	// PageVO page = new PageVO();
	// page.setPageData(list);
	// page.setPageIndex(param.getPageIndex());
	// page.setPageSize(param.getPageSize());
	// page.setTotal(collection.count(query));
	//
	// return page;
	// }

	/**
	 * </p>
	 * 通过id查找抽成关系
	 * </p>
	 * 
	 * @return
	 * @author fanp
	 * @date 2015年9月2日
	 */
	public GroupProfitVO getById(Integer doctorId, String groupId) {
		return dsForRW.createQuery("c_group_profit", GroupProfitVO.class).field("doctorId").equal(doctorId)
				.field("groupId").equal(groupId).get();
	}

	// public GroupProfit getByDoctorIdAndGroupId(Integer doctorId,String
	// groupId){
	// return dsForRW.createQuery("c_group_profit",
	// GroupProfit.class).field("doctorId").equal(doctorId).field("groupId").equal(groupId).get();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupProfitDao#add(com.dachen.health.
	 * group.group.entity.po.GroupProfit) update by wangqiao
	 */
	public void add(GroupProfit po) {
		// 数据合规校验
		if (po.getDoctorId() == null || po.getDoctorId() == 0) {
			throw new ServiceException("医生id为空");
		}
		if (po.getGroupId() == null || StringUtils.isEmpty(po.getGroupId())) {
			throw new ServiceException("集团id为空");
		}

		DBObject query = new BasicDBObject();
		query.put("doctorId", po.getDoctorId());
		query.put("groupId", po.getGroupId());
		long count = dsForRW.getDB().getCollection("c_group_profit").count(query);
		if (count > 0) {
			// 删除多余的 垃圾数据 TODO 打印日志，提醒出现垃圾数据
			dsForRW.getDB().getCollection("c_group_profit").remove(query);
		}

		dsForRW.insert("c_group_profit", po);
	}

	/**
	 * </p>
	 * 添加抽成关系
	 * </p>
	 * 
	 * @param po
	 * @author fanp
	 * @date 2015年9月2日
	 */
	// public void add(GroupProfit po) {
	// //判断是否存在
	// DBObject query = new BasicDBObject();
	// query.put("_id", po.getId());
	// query.put("groupId", po.getGroupId());
	// query.put("parentId", po.getParentId());
	// long count =
	// dsForRW.getDB().getCollection("c_group_profit").count(query);
	// if(count>0){
	// //已存在
	// return;
	// }
	//
	// List<GroupProfit> list = new ArrayList<GroupProfit>();
	// po.setUpdator(po.getId());
	// po.setUpdatorDate(System.currentTimeMillis());
	//
	// // 查找邀请关系树路径
	// GroupDoctor param = new GroupDoctor();
	// param.setDoctorId(po.getParentId());
	// param.setGroupId(po.getGroupId());
	//
	// boolean insertParent = false;// 判断抽成关系父节点是否插入
	// Group group = groupDao.getById(po.getGroupId());
	// GroupDoctor doctor = groupDoctorDao.getById(param);
	// if (doctor == null) {
	// //邀请人不在医生集团
	// if (po.getParentId() == 0) {
	// // 自己邀请自己时
	//// po.setGroupProfit(group.getConfig() != null ?
	// group.getConfig().getGroupProfit() : 0);
	// if(group.getConfig() ==null){
	// po.setGroupProfit(0);
	// }else{
	// po.setGroupProfit(group.getConfig().getGroupProfit());
	// }
	//
	// po.setParentProfit(0);
	// po.setTreePath("/" + po.getId() + "/");
	// } else {
	// // 别人邀请自己，但别人不在医生集团
	// po.setTreePath("/" + po.getParentId() + "/" + po.getId() + "/");
	// insertParent = true;
	// }
	// } else {
	// //邀请人在医生集团，查找邀请人treePath
	// GroupProfitVO vo = this.getById(po.getParentId(),po.getGroupId());
	//
	// if(vo!=null){
	// po.setTreePath(vo.getTreePath() + po.getId() + "/");
	// }else{
	// //父节点不存在，补全父节点
	// //TODO 后期递归补全
	// GroupProfit fpo = new GroupProfit();
	// fpo.setGroupId(po.getGroupId());
	// fpo.setId(po.getParentId());
	// fpo.setParentId(0);
	// fpo.setGroupProfit(group.getConfig() != null ?
	// group.getConfig().getGroupProfit() : 0);
	// fpo.setParentProfit(0);
	// fpo.setUpdator(po.getParentId());
	// fpo.setUpdatorDate(po.getUpdatorDate());
	// fpo.setTreePath("/"+fpo.getId() + "/");
	// list.add(fpo);
	//
	// po.setTreePath(fpo.getTreePath() + po.getId() + "/");
	// }
	//
	// }
	// list.add(po);
	//
	// if (insertParent) {
	// // 插入父节点
	// GroupProfit fpo = new GroupProfit();
	// fpo.setGroupId(po.getGroupId());
	// fpo.setId(po.getParentId());
	// fpo.setParentId(0);
	// fpo.setGroupProfit(group.getConfig() != null ?
	// group.getConfig().getGroupProfit() : 0);
	// fpo.setParentProfit(0);
	// fpo.setUpdator(po.getParentId());
	// fpo.setUpdatorDate(po.getUpdatorDate());
	// fpo.setTreePath("/"+fpo.getId() + "/");
	// list.add(fpo);
	// }
	//
	// for(GroupProfit groupProfit:list){
	// dsForRW.save("c_group_profit",groupProfit);
	// }
	// }

	/**
	 * </p>
	 * 修改抽成关系
	 * </p>
	 * 
	 * @author fanp
	 * @date 2015年9月2日
	 */
	public void updateParentId(Integer doctorId, Integer parentId, String groupId) {
		GroupProfitVO selfVO = this.getById(doctorId, groupId);// 当前节点

		// 当前节点不存在
		if (selfVO == null) {
			return;
		}

		if (parentId == null) {
			parentId = 0;
		}

		// 相同的parentId 没有修改
		if (selfVO.getParentId().equals(parentId)) {
			// 未移动节点
			return;
		}

		Integer newParent = 0;
		String newTreePath = "";
		String oldTreePath = selfVO.getTreePath();

		// dsForRW.getDB().getCollection("c_group_profit").update(query, new
		// BasicDBObject("$set",update));

		// 修改后的parentId为0，或自己
		if (selfVO.getDoctorId().equals(parentId) || parentId == 0) {
			newParent = 0;
			newTreePath = "/" + doctorId + "/";
		} else {
			// 查询父节点的treePath
			GroupProfitVO parentVO = this.getById(parentId, groupId);
			if (parentVO == null) {
				// 父节点不存在
				newParent = 0;
				newTreePath = "/" + doctorId + "/";
			} else {
				newParent = parentId;
				newTreePath = parentVO.getTreePath() + doctorId + "/";
			}
		}

		// 更新 parentId和treePath
		DBObject query = new BasicDBObject();
		query.put("doctorId", doctorId);
		query.put("groupId", groupId);
		BasicDBObject update = new BasicDBObject();
		update.put("parentId", newParent);
		update.put("treePath", newTreePath);
		dsForRW.getDB().getCollection("c_group_profit").update(query, new BasicDBObject("$set", update));

		// 查找子节点
		List<GroupProfit> list = dsForRW.createQuery(GroupProfit.class).field("groupId").equal(groupId)
				.field("parentId").equal(doctorId).asList();

		if (list != null && list.size() > 0) {
			for (GroupProfit vo : list) {
				String updateTreePath = vo.getTreePath().replace(oldTreePath, newTreePath);
				DBObject q = new BasicDBObject();
				q.put("doctorId", vo.getDoctorId());
				q.put("groupId", vo.getGroupId());
				q.put("parentId", doctorId);
				DBObject u = new BasicDBObject();
				u.put("treePath", updateTreePath);
				dsForRW.getDB().getCollection("c_group_profit").update(q, new BasicDBObject("$set", u));
			}
		}
	}

	/**
	 * </p>
	 * 删除抽成关系
	 * </p>
	 * 
	 * @author fanp
	 * @date 2015年9月2日
	 */
	public void delete(Integer doctorId, String groupId) {
		GroupProfitVO selfVO = this.getById(doctorId, groupId);// 当前节点
		if (selfVO == null) {
			return;
		}

		// 查找子节点
		List<GroupProfit> childrenList;
		// 当doctorId=0时，不需要查找子节点
		if (doctorId == 0) {
			childrenList = new ArrayList<GroupProfit>();
		} else {
			childrenList = dsForRW.createQuery(GroupProfit.class).field("groupId").equal(groupId).field("parentId")
					.equal(doctorId).asList();
		}

		updateChildProfitByDelete(selfVO,childrenList);

		// 删除当前节点
		DBObject query = new BasicDBObject();
		query.put("doctorId", doctorId);
		query.put("groupId", groupId);
		dsForRW.getDB().getCollection("c_group_profit").remove(query);
	}
	
	/**
	 * 删除profit时，需要将下级profit的parentid进行更新
	 * @author wangqiao
	 * @date 2016年3月23日
	 * @param profitVO
	 * @param childrenList
	 */
	private void  updateChildProfitByDelete(GroupProfitVO profitVO, List<GroupProfit> childrenList){
		//参数校验
		if (profitVO == null) {
			return;
		}
		if(childrenList == null){
			return;
		}
		boolean isRoot = false;// 当前节点是否为根节点
		if (profitVO.getParentId() == null || profitVO.getParentId() == 0) {
			isRoot = true;
		}
		
		if (childrenList.size() > 0) {
			for (GroupProfit vo : childrenList) {
				String treePath = "";
				if (isRoot) {
					// 删除节点为根节点, 将所有子节点设置为根节点
					if (vo.getParentId().equals(profitVO.getDoctorId())) {
						vo.setParentId(0);
					}
					// 清理子节点的tree中 父tree部分
					treePath = vo.getTreePath().replace(profitVO.getTreePath(), "/");
				} else {
					// 删除节点不为根节点，则将删除节点的父节点设置为所有子节点的父节点
					if (vo.getParentId().equals(profitVO.getDoctorId())) {
						vo.setParentId(profitVO.getParentId());
					}
					// 清理子节点的tree中， 父节点的doctorId部分
					treePath = vo.getTreePath().replace("/" + profitVO.getDoctorId() + "/", "/");
				}
				vo.setTreePath(treePath);
				//更新当前的树形结构（2016-9-1 傅永德）
				this.updateParentId(vo.getDoctorId(), null, vo.getGroupId());
			}
			// 修改树路径（下面这个代码不要再用了，容易出现Mongo中记录不会更新，却出现id相同，id为字符串格式的BUG  2016-9-1）
			// dsForRW.save(childrenList);
		}
		
	}

	public boolean delete(String... groupIds) {
		BasicDBObject in = new BasicDBObject();
		BasicDBList values = new BasicDBList();
		for (String groupId : groupIds) {
			values.add(groupId);
		}
		in.put("$in", values);

		WriteResult wr = dsForRW.getDB().getCollection("c_group_profit").remove(new BasicDBObject("groupId", in));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#deleteById(java.lang.String)
	 */
	@Override
	public void deleteById(String groupProfitId){
		//参数校验
		if(StringUtils.isEmpty(groupProfitId)){
			throw new ServiceException("id不能为空");
		}
		
		GroupProfitVO profitVO = dsForRW.createQuery("c_group_profit", GroupProfitVO.class).field("_id").equal(new ObjectId(groupProfitId)).get();
		if(profitVO == null){
			return;
		}
		
		// 查找子节点
		List<GroupProfit> childrenList;
		// 当doctorId=0时，不需要查找子节点
		if (profitVO.getDoctorId() == null || profitVO.getDoctorId() == 0 ) {
			childrenList = new ArrayList<GroupProfit>();
		} else {
			childrenList = dsForRW.createQuery(GroupProfit.class).field("groupId").equal(profitVO.getGroupId()).field("parentId")
					.equal(profitVO.getDoctorId()).asList();
		}
		//更新子节点的 parentId
		updateChildProfitByDelete(profitVO,childrenList);
		
		// 删除当前节点
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(groupProfitId));
		dsForRW.getDB().getCollection("c_group_profit").remove(query);
		
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#deleteByGroupId(java.lang.String)
	 */
	@Override
	public void deleteByGroupId(String groupId){
		//参数校验
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团id不能为空");
		}
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		dsForRW.getDB().getCollection("c_group_profit").remove(query);
	}

	/**
	 * </p>
	 * 修改抽成比例
	 * </p>
	 * 
	 * @param param
	 * @author fanp
	 * @date 2015年9月6日
	 */
	public void updateProfit(GroupProfitParam param) {
		GroupProfitVO selfVO = this.getById(param.getDoctorId(), param.getGroupId());// 当前节点

		DBObject query = new BasicDBObject();
		query.put("doctorId", param.getDoctorId());
		query.put("groupId", param.getGroupId());

		BasicDBObject update = new BasicDBObject();
		GroupProfitConfig config = new GroupProfitConfig();
		GroupProfit addProfit = new GroupProfit();

		// 设置更新人
		if (param.getUpdator() != null && param.getUpdator() != 0) {
			update.put("updator", param.getUpdator());
			addProfit.setUpdator(param.getUpdator());
		}
		// 设置更新时间
		Long now = System.currentTimeMillis();
		update.put("updatorDate", now);
		addProfit.setUpdatorDate(now);

		// 设置分成比例
		if (param.getGroupProfit() != null && param.getGroupProfit() >= 0) {
			update.put("groupProfit", param.getGroupProfit());
		}
		if (param.getParentProfit() != null && param.getParentProfit() >= 0) {
			update.put("parentProfit", param.getParentProfit());
		}
		// add by wangqiao
		if (param.getTextParentProfit() != null && param.getTextParentProfit() >= 0) {
			update.put("config.textParentProfit", param.getTextParentProfit());
			config.setTextParentProfit(param.getTextParentProfit());
		}
		if (param.getTextGroupProfit() != null && param.getTextGroupProfit() >= 0) {
			update.put("config.textGroupProfit", param.getTextGroupProfit());
			config.setTextGroupProfit(param.getTextGroupProfit());
		}

		if (param.getPhoneParentProfit() != null && param.getPhoneParentProfit() >= 0) {
			update.put("config.phoneParentProfit", param.getPhoneParentProfit());
			config.setPhoneParentProfit(param.getPhoneParentProfit());
		}
		if (param.getPhoneGroupProfit() != null && param.getPhoneGroupProfit() >= 0) {
			update.put("config.phoneGroupProfit", param.getPhoneGroupProfit());
			config.setPhoneGroupProfit(param.getPhoneGroupProfit());
		}

		if (param.getCarePlanParentProfit() != null && param.getCarePlanParentProfit() >= 0) {
			update.put("config.carePlanParentProfit", param.getCarePlanParentProfit());
			config.setCarePlanParentProfit(param.getCarePlanParentProfit());
		}
		if (param.getCarePlanGroupProfit() != null && param.getCarePlanGroupProfit() >= 0) {
			update.put("config.carePlanGroupProfit", param.getCarePlanGroupProfit());
			config.setCarePlanGroupProfit(param.getCarePlanGroupProfit());
		}

		if (param.getClinicParentProfit() != null && param.getClinicParentProfit() >= 0) {
			update.put("config.clinicParentProfit", param.getClinicParentProfit());
			config.setClinicParentProfit(param.getClinicParentProfit());
		}
		if (param.getClinicGroupProfit() != null && param.getClinicGroupProfit() >= 0) {
			update.put("config.clinicGroupProfit", param.getClinicGroupProfit());
			config.setClinicGroupProfit(param.getClinicGroupProfit());
		}
		//添加会诊抽成比例
		if (param.getConsultationGroupProfit() != null && param.getConsultationGroupProfit() >= 0) {
			update.put("config.consultationGroupProfit", param.getConsultationGroupProfit());
			config.setConsultationGroupProfit(param.getConsultationGroupProfit());
		}
		if (param.getConsultationParentProfit() != null && param.getConsultationParentProfit() >= 0) {
			update.put("config.consultationParentProfit", param.getConsultationParentProfit());
			config.setConsultationParentProfit(param.getConsultationParentProfit());
		}
		if(param.getChargeItemGroupProfit()!=null && param.getChargeItemGroupProfit() >=0){
			update.put("config.chargeItemGroupProfit", param.getChargeItemGroupProfit());
			config.setChargeItemGroupProfit(param.getChargeItemGroupProfit());
		}
		if(param.getChargeItemParentProfit()!=null && param.getChargeItemParentProfit() >= 0){
			update.put("config.chargeItemParentProfit", param.getChargeItemParentProfit());
			config.setChargeItemParentProfit(param.getChargeItemParentProfit());
		}

		// 名医面对面
		if(param.getAppointmentGroupProfit()!=null && param.getAppointmentGroupProfit() >=0){
			update.put("config.appointmentGroupProfit", param.getAppointmentGroupProfit());
			config.setAppointmentGroupProfit(param.getAppointmentGroupProfit());
		}
		if(param.getAppointmentParentProfit()!=null && param.getAppointmentParentProfit() >= 0){
			update.put("config.appointmentParentProfit", param.getAppointmentParentProfit());
			config.setAppointmentParentProfit(param.getAppointmentParentProfit());
		}
		
		if (selfVO == null) {
			// 新增抽成关系
			if (param.getDoctorId() == null || param.getDoctorId() == 0) {
				return;
			}
			if (param.getGroupId() == null || StringUtils.isEmpty(param.getGroupId())) {
				return;
			}

			addProfit.setDoctorId(param.getDoctorId());
			addProfit.setGroupId(param.getGroupId());
			// 先设置为0，新增后再更新parentId
			addProfit.setParentId(0);
			addProfit.setTreePath("/" + param.getDoctorId() + "/");

			addProfit.setConfig(config);
			dsForRW.insert("c_group_profit", addProfit);
			updateParentId(param.getDoctorId(), param.getParentId(), param.getGroupId());
		} else {
			// 更新 抽成数值
			dsForRW.getDB().getCollection("c_group_profit").update(query, new BasicDBObject("$set", update));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dachen.health.group.group.dao.IGroupProfitDao#getGroupProfitById(java
	 * .lang.Integer, java.lang.String)
	 */
	@Override
	public GroupProfit getGroupProfitById(Integer doctorId, String groupId) {
		return dsForRW.createQuery("c_group_profit", GroupProfit.class).field("doctorId").equal(doctorId)
				.field("groupId").equal(groupId).get();

	}
	
	   /* (non-Javadoc)
	 * @see com.dachen.health.group.group.dao.IGroupProfitDao#getGroupProfitByGroupId(java.lang.String)
	 */
	@Override
	public List<GroupProfit> getGroupProfitByGroupId(String groupId){
		   //参数校验
		   if(StringUtils.isEmpty(groupId)){
			   throw new ServiceException("集团id为空");
		   }
		   
			return dsForRW.createQuery("c_group_profit", GroupProfit.class).field("groupId").equal(groupId).asList();
		   
	   }

	@Override
	public void updateAppointment(String groupId, Integer doctorId, Integer appointmentGroupProfit,
			Integer appointmentParentProfit) {
		Query<GroupProfit> q = dsForRW.createQuery("c_group_profit", GroupProfit.class)
				.field("doctorId").equal(doctorId)
				.field("groupId").equal(groupId);
		UpdateOperations<GroupProfit> ops = dsForRW.createUpdateOperations(GroupProfit.class);
		ops.set("config.appointmentGroupProfit", appointmentGroupProfit);
		ops.set("config.appointmentParentProfit", appointmentParentProfit);
		dsForRW.update(q, ops);
	}

}
