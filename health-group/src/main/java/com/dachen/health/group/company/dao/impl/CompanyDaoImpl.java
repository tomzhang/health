package com.dachen.health.group.company.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.company.dao.ICompanyDao;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.entity.vo.CompanyVO;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * company 相关dao接口实现类
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
@Repository
public class CompanyDaoImpl extends NoSqlRepository implements ICompanyDao {

	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyDao#save(com.dachen.health.group.company.entity.po.Company)
	 */
	@Override
	public Company add(Company company) {
		//参数校验
		if(company == null){
			throw new ServiceException("参数不能为空");
		}
		String id = dsForRW.insert(company).getId().toString();
		return dsForRW.createQuery(Company.class).field("_id").equal(new ObjectId(id)).get();
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyDao#update(com.dachen.health.group.company.entity.po.Company)
	 */
	@Override
	public Company update(Company company) {
		//参数校验
		if(company == null){
			throw new ServiceException("参数不能为空");
		}
		if(StringUtils.isEmpty(company.getId()) ){
			throw new ServiceException("参数id不能为空");
		}
		
		DBObject query = new BasicDBObject();
		DBObject update = new BasicDBObject();
		
		if(!StringUtil.isEmpty(company.getBankAccount())) {
			update.put("bankAccount", company.getBankAccount());
		}
		
		if(!StringUtil.isEmpty(company.getOpenBank())) {
			update.put("openBank", company.getOpenBank());
		}
		
		if(!StringUtil.isEmpty(company.getBankNumber())) {
			update.put("bankNumber", company.getBankNumber());
		}
		
		if(!StringUtil.isEmpty(company.getCorporation())) {
			update.put("corporation", company.getCorporation());
		}
		
		if(!StringUtil.isEmpty(company.getLicense())) {
			update.put("license", company.getLicense());
		}
		
		if(!StringUtil.isEmpty(company.getName())) {
			update.put("name", company.getName());
		}
		
		if(!StringUtil.isEmpty(company.getStatus())) {
			update.put("status", company.getStatus());
		}
		
		if(!StringUtil.isEmpty(company.getCheckRemarks())) {
			update.put("checkRemarks", company.getCheckRemarks());
		}
		
		query.put("_id", new ObjectId(company.getId()));
		//更新
		dsForRW.getDB().getCollection("c_company").update(query, new BasicDBObject("$set",update));
		
		return dsForRW.createQuery(Company.class).field("_id").equal(new ObjectId(company.getId())).get();
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyDao#delete(java.lang.Integer[])
	 */
	@Override
	@Deprecated
	public boolean delete(Integer[] ids) {
		//参数校验
		if(ids == null){
			throw new ServiceException("参数不能为空");
		}
		
		dsForRW.delete(Company.class, ids);
		return true;
	}

	/***
	 * 获取所有集团列表
	 */
	@Deprecated //业务有问题，暂时废弃
	public PageVO search(CompanyParam company) {
		DBObject query = new BasicDBObject();
		
		if(!StringUtil.isEmpty(company.getBankAccount())) {
			query.put("bankAccount", company.getBankAccount());
		}
		
		if(!StringUtil.isEmpty(company.getOpenBank())) {
			query.put("openBank", company.getOpenBank());
		}
		
		if(!StringUtil.isEmpty(company.getBankNumber())) {
			query.put("bankNumber", company.getBankNumber());
		}
		
		if(!StringUtil.isEmpty(company.getCorporation())) {
			query.put("corporation", company.getCorporation());
		}
		
		if(!StringUtil.isEmpty(company.getLicense())) {
			query.put("license", company.getLicense());
		}
		
		if(!StringUtil.isEmpty(company.getName())) {
			query.put("name", company.getName());
		}
		
		if(!StringUtil.isEmpty(company.getDescription())) {
			query.put("description", company.getDescription());
		}
		
		if(!StringUtil.isEmpty(company.getStatus())) {
			query.put("status", company.getStatus());
		}
		
		if(null != company.getCreator()) {
			query.put("creator", company.getCreator());
		}
		
		if(!StringUtil.isEmpty(company.getId())) {
			query.put("_id", new ObjectId(company.getId()));
		}
		
		
		DBObject sortField = new BasicDBObject();
		sortField.put("applyDate", -1);
		DBCollection collection = dsForRW.getDB().getCollection("c_company");
		DBCursor cursor = collection.find(query).sort(sortField).skip(company.getStart()).limit(company.getPageSize());
		List<CompanyVO> comList = new ArrayList<CompanyVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			
			CompanyVO com = new CompanyVO();
			com.setId(MongodbUtil.getString(obj, "_id"));
			com.setName(MongodbUtil.getString(obj, "name"));
			com.setDescription(MongodbUtil.getString(obj, "description"));
			com.setCorporation(MongodbUtil.getString(obj, "corporation"));
			com.setCheckRemarks(MongodbUtil.getString(obj, "checkRemarks"));
			com.setLicense(MongodbUtil.getString(obj, "license"));
			com.setStatus(MongodbUtil.getString(obj, "status"));
			com.setCreator(MongodbUtil.getInteger(obj, "creator"));
			com.setCreatorDate(MongodbUtil.getLong(obj, "creatorDate"));
			com.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			com.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
			
			comList.add(com);
		}
		
		PageVO page = new PageVO();
        page.setPageData(comList);
        page.setPageIndex(company.getPageIndex());
        page.setPageSize(company.getPageSize());
        page.setTotal(collection.count(query));
        
        return page;
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyDao#getById(java.lang.String)
	 */
	@Override
	public Company getById(String id) {
		//参数校验
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("参数不能为空");
		}
		ObjectId objectId = null;
		try {
			objectId = new ObjectId(id);
		} catch (Exception e) {
			throw new ServiceException("参数id不是正确的ObjectId格式");
		}
		
		return dsForRW.createQuery(Company.class).field("_id").equal(objectId).get();
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyDao#getByOrgCode(java.lang.String)
	 */
	@Override
	public Company getByOrgCode(String orgCode) {
		//参数校验
		if(StringUtils.isEmpty(orgCode)){
			throw new ServiceException("参数不能为空");
		}
		
		return dsForRW.createQuery(Company.class).field("orgCode").equal(orgCode).get();
	}

}
