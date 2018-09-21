package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.line.stat.dao.IUserHospitalDao;
import com.dachen.line.stat.entity.vo.UserHospital;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class UserHospitalDaoImpl extends NoSqlRepository implements
		IUserHospitalDao {

	@Override
	public List<UserHospital> getUserHospitalList(String column, Object sourceId) {
		List<UserHospital> result = new ArrayList<UserHospital>();
		Query<UserHospital> uq = dsForRW.createQuery(UserHospital.class)
				.filter(column, sourceId);// 查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public boolean checkGetUserHospitalList(String hospitalId, String sourceId) {
		boolean result = false;
		Query<UserHospital> uq = dsForRW.createQuery(UserHospital.class)
				.filter("hospitalId", sourceId).filter("sourceId", sourceId);// 查询搜有的数据
		List<UserHospital> list = uq.asList();
		if (null != list && list.size() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * 查询制定字段条件下的下面的医院
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<ObjectId> getUserHospitalStringList(String column,
			Object sourceId) {

		List<ObjectId> sourceIds = new ArrayList<ObjectId>();
		List<UserHospital> ids = getUserHospitalList(column, sourceId);
		if (ConfigUtil.checkCollectionIsEmpty(ids)) {
			for (UserHospital user : ids) {
				sourceIds.add(new ObjectId(user.getSourceId()));
			}
		}
		return sourceIds;
	}

	@Override
	public void insertBatchUserHospital(List<UserHospital> hospital) {
		if (ConfigUtil.checkCollectionIsEmpty(hospital)) {
			for (UserHospital result : hospital) {
				insertUserHospital(result);
			}
		}
	}

	@Override
	public void insertUserHospital(UserHospital hospital) {

		if (!checkGetUserHospitalList(hospital.getHospitalId(),
				hospital.getSourceId())) {
			dsForRW.insert(hospital);

		}
	}

	@Override
	public List<Map<String, Object>> getCertificatedHospitalList(Integer status) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		DBObject ref = new BasicDBObject();
		if (null != status)
			ref.put("status", status);
		ref.put("userType", UserEnum.UserType.nurse.getIndex());

		DBObject fields = new BasicDBObject();
		fields.put("nurse", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("user")
				.find(ref, fields);

		Map<String, Object> mapSet = new HashMap<String, Object>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			DBObject nurse = (BasicDBObject) obj.get("nurse");
			if (nurse != null) {
				String hospital = MongodbUtil.getString(nurse, "hospital");
				String hospitalId = MongodbUtil.getString(nurse, "hospitalId");
				if (StringUtils.isNotEmpty(hospital)) {
					mapSet.put(hospital, hospitalId);
				}
			}
		}
		if (mapSet.size() > 0) {
			setMap(list, mapSet);
		}

		return list;
	}

	private void setMap(List<Map<String, Object>> list,
			Map<String, Object> mapSet) {
		if (null != mapSet && mapSet.size() > 0) {
			Iterator<Map.Entry<String, Object>> s = mapSet.entrySet()
					.iterator();
			while (s.hasNext()) {
				Entry<String, Object> entry = s.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("hospital", entry.getKey());
				map.put("hospitalId", entry.getValue());
				list.add(map);
			}
		}
	}
}
