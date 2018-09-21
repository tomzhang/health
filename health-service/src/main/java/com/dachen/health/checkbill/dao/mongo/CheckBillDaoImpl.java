package com.dachen.health.checkbill.dao.mongo;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.checkbill.dao.CheckBillDao;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.po.CheckupItem;
import com.dachen.health.checkbill.entity.vo.CheckItemCount;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.entity.vo.CheckbillSearchParam;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository(value = "checkBillDao")
public class CheckBillDaoImpl extends NoSqlRepository implements CheckBillDao{

	@Override
	public CheckBill insertCheckBill(CheckBill checkBill) {
		String id = dsForRW.insert(checkBill).getId().toString();
		return dsForRW.createQuery(CheckBill.class).field("_id").equal(new ObjectId(id)).get();
	}

	
	private List<String> setImageUrlToPath(List<String> imageUrls) {
		List<String> imagePaths = new ArrayList<String>();
		if(imageUrls != null && imageUrls.size() > 0){
			for (String url : imageUrls) {
				String path = PropertiesUtil.removeUrlPrefix(url);
				imagePaths.add(path);
			}
		}
		return imagePaths;
	}
	
	
	@Override
	public CheckItem updateCheckItem(CheckItem checkItem) {
		UpdateOperations<CheckItem> ops = dsForRW.createUpdateOperations(CheckItem.class);
		Query<CheckItem> query = dsForRW.createQuery(CheckItem.class).field("_id").equal(new ObjectId(checkItem.getId()));
		List<String> images = checkItem.getImageList();
		Long updateTime = System.currentTimeMillis();
		if(images != null ){
			ops.set("imageList", setImageUrlToPath(images));
			ops.set("updateTime",updateTime);
		}
		if(checkItem.getResults() != null){
			ops.set("results", checkItem.getResults());
			ops.set("updateTime",updateTime);
		}
		if(checkItem.getVisitingTime() != null){
			ops.set("visitingTime", checkItem.getVisitingTime());
			ops.set("updateTime",updateTime);
		}
		dsForRW.updateFirst(query, ops);
		return query.get();
	}

	@Override
	public CheckItem getCheckItemById(String checkItemId) {
		return dsForRW.createQuery(CheckItem.class).field("_id").equal(new ObjectId(checkItemId)).get();
	}

	@Override
	public CheckItem addCheckItem(CheckItem checkItem) {
		String id = dsForRW.insert(checkItem).getId().toString();
		return dsForRW.createQuery(CheckItem.class).field("_id").equal(new ObjectId(id)).get();
	}
	
	@Override
	public List<String> addCheckupItemBatch(List<CheckupItem> list) {
		List<String> idList = new ArrayList<String>(list.size());
		Long currTime = System.currentTimeMillis();
		for (CheckupItem ci:list) {
			ci.setCreateTime(currTime);
			String id = this.dsForRW.insert(ci).getId().toString();
			idList.add(id);
		}
		return idList;
	}

	@Override
	public int batchAddCheckItem(List<CheckItem> items) {
		if(items != null){
			for (CheckItem checkItem : items) {
				dsForRW.insert(checkItem);
			}
		}
		return 1;
	}
	
	@Override
	public PageVO getCheckItemListByFromId(CheckItemSearchParam scp) {
		PageVO pageVo = new PageVO();
		Query<CheckItem> query = 
				dsForRW.createQuery(CheckItem.class).field("fromId").equal(scp.getFromId());
		pageVo.setTotal(query.countAll());
		List<CheckItem> data= query.offset(scp.getPageIndex()*scp.getPageSize()).limit(scp.getPageSize()).order("-createTime").asList();
		pageVo.setPageData(data);
		return pageVo;
	}

	@Override
	public PageVO getCheckBillPageVo(CheckbillSearchParam csp) {
		PageVO pageVo = new PageVO();
		//List<Integer> list = new ArrayList<Integer>();list.add(922);
		Query<CheckBill> query = 
				dsForRW.createQuery(CheckBill.class).field("patientId").in(csp.getPatientIds()).order("patientId");
		pageVo.setTotal(query.countAll());
		List<CheckBill> data= query.offset(csp.getPageIndex()*csp.getPageSize()).order("-createTime").limit(csp.getPageSize()).asList();
		pageVo.setPageData(data);
		return pageVo;
	}

	@Override
	public CheckBill getCheckBillDetail(String checkbillId) {
		return dsForRW.createQuery(CheckBill.class).field("_id").equal(new ObjectId(checkbillId)).get();
	}

	@Override
	public CheckBill updateCheckbill(CheckBill checkBill) {
		UpdateOperations<CheckBill> ops = dsForRW.createUpdateOperations(CheckBill.class);
		Query<CheckBill> query = dsForRW.createQuery(CheckBill.class).field("_id").equal(new ObjectId(checkBill.getId()));
		CheckBill cb = query.get();
		if(cb == null){
			throw new ServiceException("根据Id={"+checkBill.getId()+"}获取不到检查单记录");
		}
		Integer paramStatus = checkBill.getCheckBillStatus();
		long updateTime = System.currentTimeMillis();
		if(paramStatus != null){
			ops.set("checkBillStatus", paramStatus);
			ops.set("updateTime",updateTime);
			/*if(paramStatus.equals(new Integer(2)) || paramStatus == 2){
				long time = System.currentTimeMillis();
				DBObject q = new BasicDBObject();
				q.put("fromId", checkBill.getId());
				DBObject u = new BasicDBObject();
				u.put("$set", new BasicDBObject("visitingTime",time));
				dsForRW.getDB().getCollection("t_check_item").update(q, u, false, true);
			}*/
		}
		List<String> items = checkBill.getCheckItemIds();
		if(items != null && items.size() > 0){
			ops.set("checkItemIds", items);
			ops.set("updateTime",updateTime);
		}
		
		dsForRW.updateFirst(query, ops);
		return query.get();
	}
	
	@Override
	public boolean updateCareItemId(String checkBillId, String careItemId) {
		Query<CheckBill> query = dsForRW.createQuery(CheckBill.class)
				.field("_id").equal(new ObjectId(checkBillId));
		
		UpdateOperations<CheckBill> ops = dsForRW.createUpdateOperations(CheckBill.class);
		ops.set("careItemId",careItemId);
		
		UpdateResults ur = dsForRW.updateFirst(query, ops);
		return ur.getUpdatedExisting();
	}

	@Override
	public List<String> getCheckbillIds(int patientId) {
		DBObject query = new BasicDBObject();
		query.put("patientId", patientId);
		DBObject projection = new BasicDBObject();
		projection.put("_id", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("t_check_bill").find(query, projection);
		List<String> list = new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getString(obj, "_id"));
        }
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<CheckItemCount> getCheckItemCount(List<String> checkBillIds) {
		List<DBObject> pipeline = new ArrayList<DBObject>();
		DBObject matchFields = new BasicDBObject();
		matchFields.put("fromId", new BasicDBObject("$in",checkBillIds));
		matchFields.put("visitingTime", new BasicDBObject("$exists",true));
		matchFields.put("imageList", new BasicDBObject("$exists",true));
		matchFields.put("results", new BasicDBObject("$exists",true));
		DBObject match = new BasicDBObject();
		match.put("$match", matchFields);
		
		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("checkUpId", 1);
		projectFields.put("itemName", 1);
		BasicDBObject project = new BasicDBObject();
		project.put("$project", projectFields);
		
		
		DBObject _group = new BasicDBObject("checkUpId", "$checkUpId");
		_group.put("itemName", "$itemName");
		
		DBObject groupFields = new BasicDBObject();
		groupFields.put("_id", _group);
		groupFields.put("count", new BasicDBObject("$sum", 1));
		
		DBObject group = new BasicDBObject("$group", groupFields);
		
		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count",-1));
		
		pipeline.add(match);
		pipeline.add(project);
		pipeline.add(group);
		pipeline.add(sort);
		AggregationOutput output = null;
		try{
			output = dsForRW.getDB().getCollection("t_check_item").aggregate(pipeline);
		}catch(Exception e){
			throw new ServiceException("系统内部异常");
		}
		List<CheckItemCount> list = new ArrayList<CheckItemCount>();
		if(output != null){
			Iterator<DBObject> it = output.results().iterator();
			while (it.hasNext()) {
		         DBObject obj = it.next();
		         Map mapObj = (Map) obj.get("_id");
		         String checkUpId = (String) mapObj.get("checkUpId");
		         String itemName = (String) mapObj.get("itemName");
		         long  count = MongodbUtil.getLong(obj, "count");
		         CheckItemCount item = new CheckItemCount();
		         item.setCheckUpId(checkUpId);
		         item.setCount(count);
		         item.setItemName(itemName);
		         list.add(item);
		    }
		}
		
		return list;
	}

	@Override
	public List<CheckItem> getCheckItemByClassify(List<String> checkBillIds, String checkUpId) {
		return  dsForRW.createQuery(CheckItem.class).field("fromId").in(checkBillIds)
				.field("checkUpId").equal(checkUpId).order("-visitingTime").asList();
	}

	@Override
	public boolean existsCheckItem(String checkUpId, String fromId) {
		CheckItem checkItem = dsForRW.createQuery(CheckItem.class).
					field("checkUpId").equal(checkUpId).
					field("fromId").equal(fromId).
					get();
		return checkItem != null;
	}

	@Override
	public void updateCheckItemByFromIdAndCheckUpId(CheckItem checkItem) {
		UpdateOperations<CheckItem> ops = dsForRW.createUpdateOperations(CheckItem.class);
		Query<CheckItem> query = dsForRW.createQuery(CheckItem.class).
			field("checkUpId").equal(checkItem.getCheckUpId()).
			field("fromId").equal(checkItem.getFromId());
		List<String> images = checkItem.getImageList();
		Long updateTime = System.currentTimeMillis();
		if(images != null){
			ops.set("imageList", setImageUrlToPath(images));
			ops.set("updateTime",updateTime);
		}
		if(checkItem.getResults() != null){
			ops.set("results", checkItem.getResults());
			ops.set("updateTime",updateTime);
		}
		if(checkItem.getVisitingTime() != null){
			ops.set("visitingTime", checkItem.getVisitingTime());
			ops.set("updateTime",updateTime);
		}
		dsForRW.updateFirst(query, ops);
	}
	
	@Override
	public CheckItem getCheckItemByFromIdAndCheckupId(String fromId, String checkupId) {
		return dsForRW.createQuery(CheckItem.class).filter("fromId", fromId).filter("checkUpId", checkupId).get();
	}
	
	@Override
	public List<CheckItem> getCheckItemsByFromId(String fromId) {
		return dsForRW.createQuery(CheckItem.class).filter("fromId", fromId).asList();
	}
	
	@Override
	public List<CheckItem> getCheckItemsByFromIds(List<String> fromIds) {
		Query<CheckItem> query = dsForRW.createQuery(CheckItem.class);
        Criteria[] idParams = new Criteria[fromIds.size()];
        for (int i = 0; i < fromIds.size(); i++) {
            String fromId = fromIds.get(i);
            idParams[i] = query.criteria("fromId").equal(fromId);
        }
        query.or(idParams);
		return query.asList();
	}

	@Override
	public List<CheckBill> getCheckBillList(Set<Integer> orderIds, Integer patientId) {
		return dsForRW.createQuery(CheckBill.class).field("orderId").in(orderIds).
				field("patientId").equal(patientId).field("checkBillStatus").equal(4).
				order("-createTime").asList();
	}

	@Override
	public List<CheckItem> getItemList(String checkBillId) {
		return dsForRW.createQuery(CheckItem.class).field("fromId").equal(checkBillId).
				order("-visitingTime").asList();
	}


	@Override
	public CheckBill getCheckBillByOrderId(Integer orderId) {
		return dsForRW.createQuery(CheckBill.class).field("orderId").equal(orderId).get();
	}


	@Override
	public void removeCheckItemByCheckBillId(String checkbillId) {
		Query<CheckItem> q = dsForRW.createQuery(CheckItem.class);
		q.field("fromId").equal(checkbillId);
		dsForRW.delete(q);
	}

	@Override
	public List<CheckItem> findByIds(List<String> ids) {
		List<ObjectId> paramIds = Lists.newArrayList();
		ids.forEach(id->{
			paramIds.add(new ObjectId(id));
		});
		return dsForRW.createQuery(CheckItem.class).field("_id").in(paramIds).asList();
	}


	@Override
	public CheckBill getCheckBillById(String checkBillId) {
		return dsForRW.createQuery(CheckBill.class).field("_id").equal(new ObjectId(checkBillId)).get();
	}

}
