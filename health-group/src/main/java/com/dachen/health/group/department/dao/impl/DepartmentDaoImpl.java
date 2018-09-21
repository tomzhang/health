package com.dachen.health.group.department.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Repository
public class DepartmentDaoImpl extends NoSqlRepository implements IDepartmentDao {

	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#add(com.dachen.health.group.department.entity.po.Department)
	 */
	@Override
	public boolean add(Department department) {
		//参数校验
		if(department == null){
			throw new ServiceException("参数不能为空");
		}
		if(StringUtils.isEmpty(department.getGroupId())){
			throw new ServiceException("集团id不能为空");
		}
		if(StringUtils.isEmpty(department.getName())){
			throw new ServiceException("部门名称不能为空");
		}
		
		//持久化
		try {
			dsForRW.insert(department);
		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}

//	@Override
//	public boolean update(Department department) {
//		try {
//			DBObject query = new BasicDBObject();
//			DBObject update = new BasicDBObject();
//			
//			if(!StringUtil.isEmpty(department.getName())) {
//				update.put("name", department.getName());
//			}
//			
//			if(!StringUtil.isEmpty(department.getDescription())) {
//				update.put("description", department.getDescription());
//			}
//			
//			if(!StringUtil.isEmpty(department.getParentId())) {
//				update.put("parentId", department.getParentId());
//			}
//			
//			query.put("_id", new ObjectId(department.getId()));
//			dsForRW.getDB().getCollection("c_department").update(query, new BasicDBObject("$set",update));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#updateById(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void updateById(String id,String name,String description,String parentId,Integer operationUserId){
		//参数校验
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("参数id不能为空");
		}

		DBObject query = new BasicDBObject();
		try {
			query.put("_id", new ObjectId(id));
		} catch (Exception e) {
			throw new ServiceException("参数id格式不正确");
		}
		
		
		//更新部分属性
		DBObject update = new BasicDBObject();
		//name不允许设置为空
		if(!StringUtil.isEmpty(name)) {
			update.put("name", name);
		}
		//parentId不允许设置为空
		if(!StringUtil.isEmpty(parentId)) {
			update.put("parentId", parentId);
		}
		//描述可以设置为空
		update.put("description", description);
		
		//设置最后更新时间，最后更新人
		if(operationUserId != null && operationUserId != 0){
			update.put("updator", operationUserId);
		}
		update.put("updatorDate", System.currentTimeMillis());

		//持久化
		dsForRW.getDB().getCollection("c_department").update(query, new BasicDBObject("$set",update));
	}
	

	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#delete(java.lang.String[])
	 */
	@Override
	public void delete(String[] ids) {
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		try {
			for (String str : ids) {
				values.add(new ObjectId(str));
			}
		} catch (Exception e) {
			throw new ServiceException("参数id格式不正确");
		}

		in.put("$in", values);
		
		dsForRW.getDB().getCollection("c_department").remove(new BasicDBObject("_id", in));
	}

//	@Override
//	public List<DepartmentVO> search(DepartmentParam department) {
//		DBObject query = new BasicDBObject();
//		
//		if(!StringUtil.isEmpty(department.getParentId())) {
//			query.put("parentId", department.getParentId());
//		} else {
//			query.put("parentId", "0");
//		}
//		
//		if(!StringUtil.isEmpty(department.getGroupId())) {
//			query.put("groupId", department.getGroupId());
//		}
//		
//		if(!StringUtil.isEmpty(department.getName())) {
//			query.put("name", department.getName());
//		}
//		if(!StringUtil.isEmpty(department.getDescription())) {
//			query.put("description", department.getDescription());
//		}
//		
//		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(query);
//		List<DepartmentVO> dList = new ArrayList<DepartmentVO>();
//		while (cursor.hasNext()) {
//			DBObject obj = cursor.next();
//			DepartmentVO tt = new DepartmentVO();
//			tt.setId(MongodbUtil.getString(obj, "_id"));
//			tt.setGroupId(MongodbUtil.getString(obj, "groupId"));
//			tt.setParentId(MongodbUtil.getString(obj, "parentId"));
//			tt.setName(MongodbUtil.getString(obj, "name"));
//			tt.setDescription(MongodbUtil.getString(obj, "description"));
//			tt.setCreator(MongodbUtil.getInteger(obj, "creator"));
//			tt.setCreatorDate(MongodbUtil.getLong(obj, "creatorDate"));
//			tt.setUpdator(MongodbUtil.getInteger(obj, "updator"));
//			tt.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
//			tt.setSubList(this.getDepartmentListById(tt));
//			dList.add(tt);
//		}
//		
//		return dList;
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#searchDepartment(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<DepartmentVO> searchDepartment(String groupId,String parentId,String name,String description){
		DBObject query = new BasicDBObject();
		//初始化查询条件
		if(!StringUtil.isEmpty(parentId)) {
			query.put("parentId", parentId);
		} else {
			query.put("parentId", "0");
		}
		if(!StringUtil.isEmpty(groupId)) {
			query.put("groupId", groupId);
		}
		if(!StringUtil.isEmpty(name)) {
			query.put("name", name);
		}
		if(!StringUtil.isEmpty(description)) {
			query.put("description", description);
		}
		
		//查询列表数据
		List<DepartmentVO> dList = dsForRW.createQuery(DepartmentVO.class, query).asList();
		//查询子部门信息
		if(dList != null && dList.size() >0){
			for(DepartmentVO departmentVO : dList){
				if(departmentVO != null){
					departmentVO.setSubList(this.getDepartmentListById(departmentVO));
				}
			}
		}
		
		return dList;
	}

	/**
     * </p>根据id查找</p>
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
//    public DepartmentVO getOne(ObjectId id){
//        return dsForRW.createQuery("c_department",DepartmentVO.class).field("id").equal(id).get();
//    }
    


	@Override
	public List<Department> findListByIds(Collection<String> ids) {
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String str : ids) {
			values.add(new ObjectId(str));
		}
		in.put("$in", values);
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("_id", in));
		List<Department> dList = new ArrayList<Department>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Department dvo = new Department();
			dvo.setId(obj.get("_id").toString());
			dvo.setName(obj.get("name").toString());
			dvo.setDescription(obj.get("description") == null ? "" : obj.get("description").toString());
			dvo.setParentId(obj.get("parentId") == null ? "" : obj.get("parentId").toString());
			dvo.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			dvo.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			dvo.setCreator(Integer.valueOf(obj.get("creator").toString()));
			dvo.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			dList.add(dvo);
			
		}
		return dList;
	}
	
//	public int findCountByGroupIds(String... ids) {
//		BasicDBList idsList = new BasicDBList();
//		for (String id : ids) {
//			idsList.add(id);
//		}
//		return dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("groupId", new BasicDBObject("$in", idsList))).count();
//
//	}

	@Override
	public List<Department> findSubjectByIds(String[] ids) {
		BasicDBList idList = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String id : ids) {
			idList.add(id);
		}
		in.put("$in", idList);
		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("parentId", in));
		List<Department> dList = new ArrayList<Department>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Department dvo = new Department();
			dvo.setId(obj.get("_id").toString());
			dvo.setName(obj.get("name").toString());
			dvo.setDescription(obj.get("description") == null ? "" : obj.get("description").toString());
			dvo.setParentId(obj.get("parentId") == null ? "" : obj.get("parentId").toString());
			dvo.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			dvo.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			dvo.setCreator(Integer.valueOf(obj.get("creator").toString()));
			dvo.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			dList.add(dvo);
		}
		return dList;
	}

	@Override
	public List<DepartmentDoctorVO> findDoctorByIds(String[] ids) {
		BasicDBList idList = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String id : ids) {
			idList.add(id);
		}
		in.put("$in", idList);
		DBCursor cursor = dsForRW.getDB().getCollection("c_department_doctor").find(new BasicDBObject("departmentId", in));
		List<DepartmentDoctorVO> ddList = new ArrayList<DepartmentDoctorVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			
			DepartmentDoctorVO dd = new DepartmentDoctorVO();
			dd.setId(obj.get("_id").toString());
			dd.setDepartmentId(obj.get("departmentId").toString());
			dd.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
			dd.setCreator(Integer.valueOf(obj.get("creator").toString()));
			dd.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			dd.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			dd.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			ddList.add(dd);
			
		}
		return ddList;
	}

	@Override
	public List<DepartmentVO> findListById(String groupId,String parentId) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		if(parentId!=null) {
			query.put("parentId", parentId);
		}
		DBCursor cursor = dsForRW.getDB().getCollection("c_department").find(query);
		List<DepartmentVO> departmentList = new ArrayList<DepartmentVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			DepartmentVO dvo = new DepartmentVO();
			dvo.setId(obj.get("_id").toString());
			dvo.setName(obj.get("name").toString());
			dvo.setDescription(obj.get("description") == null ? "" : obj.get("description").toString());
			dvo.setParentId(obj.get("parentId") == null ? "" : obj.get("parentId").toString());
			dvo.setUpdator(Integer.valueOf(obj.get("updator").toString()));
			dvo.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
			dvo.setCreator(Integer.valueOf(obj.get("creator").toString()));
			dvo.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			dvo.setSubList(this.getDepartmentListById(dvo));
			departmentList.add(dvo);
		}
		return departmentList;
	}
	
	/**
	 * 递归查询科室下面的子科室
	 * @param department
	 * @return
	 */
	private List<DepartmentVO> getDepartmentListById(DepartmentVO department) {
		DBCursor desor = dsForRW.getDB().getCollection("c_department").find(new BasicDBObject("parentId", department.getId()));
		List<DepartmentVO> subList = new ArrayList<DepartmentVO>();
		while (desor.hasNext()) {
			DBObject deobj = desor.next();
			DepartmentVO depart = new DepartmentVO();
			depart.setId(deobj.get("_id").toString());
			depart.setName(deobj.get("name").toString());
			depart.setDescription(deobj.get("description") == null ? "" : deobj.get("description").toString());
			depart.setParentId(deobj.get("parentId").toString());
			depart.setUpdator(Integer.valueOf(deobj.get("updator").toString()));
			depart.setUpdatorDate(Long.valueOf(deobj.get("updatorDate").toString()));
			depart.setCreator(Integer.valueOf(deobj.get("creator").toString()));
			depart.setCreatorDate(Long.valueOf(deobj.get("creatorDate").toString()));
			
			if(!StringUtil.isEmpty(deobj.get("parentId").toString())) {
				depart.setSubList(this.getDepartmentListById(depart));
			}
			subList.add(depart);
		}
		
		return subList;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#getDepartmentById(java.lang.String)
	 */
	@Override
	public Department getDepartmentById(String departmentId){
		//参数校验
    	if(StringUtils.isEmpty(departmentId)){
			throw new ServiceException("组织Id不能为空");
    	}
		Query<Department> query = dsForRW.createQuery(Department.class);
		try {
			query.filter("_id", new ObjectId(departmentId));
		} catch (Exception e) {
			throw new ServiceException("组织Id格式不正确");
		}
		
		return query.get();    	
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.dao.IDepartmentDao#getDepartmentByName(java.lang.String, java.lang.String)
	 */
	@Override
	public Department getDepartmentByName(String name,String groupId){
		//参数校验
    	if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团Id不能为空");
    	}
    	if(StringUtils.isEmpty(name)){
			throw new ServiceException("组织名称不能为空");
    	}
		Query<Department> query = dsForRW.createQuery(Department.class);
		query.filter("groupId", groupId);
		query.filter("name", name);
		return query.get();   
    	
	}
}
