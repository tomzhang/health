package com.dachen.health.group.company.dao.impl;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.company.dao.InviteCodeDao;
import com.dachen.health.group.company.entity.param.InviteCodeParam;
import com.dachen.health.group.company.entity.po.InviteCode;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author pijingwei
 * @date 2015/8/20
 */
@Repository
@Deprecated //暂时废弃，没有这块业务
public class InviteCodeDaoImpl extends NoSqlRepository implements InviteCodeDao {

	@Override
	public InviteCode save(InviteCode code) {
		Object id = dsForRW.insert(code).getId();
		return dsForRW.createQuery(InviteCode.class).field("_id").equal(new ObjectId(id.toString())).get();
	}

	public InviteCode update(InviteCode code) {
		DBObject query = new BasicDBObject();
		DBObject update = new BasicDBObject();
		
		query.put("_id", new ObjectId(code.getId()));
		update.put("status", "Y");
		update.put("useDate", new Date().getTime());
		dsForRW.getDB().getCollection("c_invite_code").update(query, new BasicDBObject("$set",update));
		/* 更新之后返回邀请码信息 */
		return this.findByCode(code.getCode());
	}
	
	public InviteCode findByCode(String code) {
		return dsForRW.createQuery(InviteCode.class).field("code").equal(code).get();
	}

	public PageVO search(InviteCodeParam param) {
		
		return null;
	}
	
	public void delete(InviteCode code) {
		DBObject delete = new BasicDBObject();
		if(!StringUtil.isEmpty(code.getCompanyId())) {
			delete.put("companyId", code.getCompanyId());
		}
		if(!StringUtil.isEmpty(code.getTelephone())) {
			delete.put("telephone", code.getTelephone());
		}
		if(null != code.getDoctorId()) {
			delete.put("doctorId", code.getDoctorId());
		}
		dsForRW.getDB().getCollection("c_invite_code").remove(delete);
	}

}
