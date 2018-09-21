package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.comon.constant.NurseServiceSetEnum;
import com.dachen.line.stat.comon.constant.NurseServiceTimeSetEnum;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.IUserServiceTimeDao;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.NurseDutyTime;
import com.dachen.line.stat.entity.vo.UserLineService;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class UserServiceTimeDaoImpl extends NoSqlRepository implements
		IUserServiceTimeDao {
	@Autowired
	private ILineServiceProductDao lineServiceProductDao;

	public List<UserLineService> getUserLineService(Integer userId) {

		List<UserLineService> result = null;
		if (null == result || result.size() == 0) {
			List<LineServiceProduct> productList = lineServiceProductDao
					.getSystemLineServiceProduct();
			result = new ArrayList<UserLineService>();
			if (null != productList && productList.size() > 0) {
				for (LineServiceProduct product : productList) {
					UserLineService user = new UserLineService();
					user.setUserId(userId);
					user.setStatus(NurseServiceSetEnum.un_set.getIndex());
					user.setProductId(product.getId());
					result.add(user);
				}
			}
			if (result.size() > 0) {
				insertUserLineServiceList(result);
			}
		}

		return result;
	}

	/**
	 * 修改状态
	 */

	public void updateUserLineService(Integer userId, String serviceId,
			Integer status) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(serviceId));
		query.put("userId", userId);

		BasicDBObject update = new BasicDBObject();

		if (null != status) {// idCard
			update.put("status", status);
		}

		if (!update.isEmpty()) {
			dsForRW.getDB().getCollection("v_user_line_service")
					.update(query, new BasicDBObject("$set", update));
		}
	}

	/**
	 * 批量插入数据
	 */

	public void insertUserLineServiceList(List<UserLineService> userList) {
		try {
			List<BasicDBObject> documentList = new ArrayList<BasicDBObject>();
			if (null != userList && userList.size() > 0) {
				for (UserLineService param : userList) {
					BasicDBObject jo = new BasicDBObject();
					jo.put("userId", param.getUserId());// 索引
					jo.put("productId", param.getProductId());
					jo.put("status", NurseServiceSetEnum.un_set.getIndex());
					documentList.add(jo);
				}
			}
			if (documentList.size() > 0) {
				dsForRW.getDB().getCollection("v_user_line_service")
						.insert(documentList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<NurseDutyTime> getUserServiceTimeList(Integer userId) {

		// 手动构造的大于等于16天的护士服务时间
		List<NurseDutyTime> structureUserTimes = getNurseInSixteenDayList(userId);

		List<NurseDutyTime> result = getUserServiceTime(userId);

		if (null != result && result.size() > 0) {
			for (NurseDutyTime dbTime : result) {
				if (null != dbTime) {
					for (NurseDutyTime struts : structureUserTimes) {
						if (null != struts) {
							if (dbTime.getTime().equals(struts.getTime())) {
								struts.setStatus(dbTime.getStatus());
							}
						}
					}
				}
			}
		}
		return structureUserTimes;
	}
    
	/**
	 * 查询
	 * @param userId
	 * @return
	 */
	public List<NurseDutyTime> getUserServiceTime(Integer userId) {

		String today = DateUtil.formatDate2Str(DateUtil.FORMAT_YYYY_MM_DD);
		Query<NurseDutyTime> uq = dsForRW.createQuery(NurseDutyTime.class)
				.filter("userId", userId).filter("time >", today);// 查询搜有的数据
		List<NurseDutyTime> result = uq.asList();
		return result;
	}

	/**
	 * 插入用户服务时间
	 */
	@Override
	public void insertUserServiceTime(NurseDutyTime nurseDutyTime) {
		DBObject insert = new BasicDBObject();
		insert.put("userId", nurseDutyTime.getUserId());
		insert.put("time", nurseDutyTime.getTime());
		insert.put("status", nurseDutyTime.getStatus());
		dsForRW.getDB().getCollection("v_nurse_dutytime").insert(insert);
	}

	/**
	 * 删除用户服务时间
	 */
	@Override
	public void deleteUserServiceTime(Integer userId, String time) {

		DBObject query = new BasicDBObject();
		query.put("userId", userId);
		query.put("time", time);
		dsForRW.getDB().getCollection("v_nurse_dutytime").remove(query);
	}

	/**
	 * 获取护士未来多少天的服务时间列表
	 * 
	 * @return
	 */
	private List<NurseDutyTime> getNurseInSixteenDayList(Integer userId) {
		int days = getNurseConfigTime();

		List<NurseDutyTime> timeList = new ArrayList<NurseDutyTime>();
		for (int i = 1; i <= days; i++) {
			NurseDutyTime duty = new NurseDutyTime();
			duty.setStatus(NurseServiceTimeSetEnum.NO.getIndex());
			duty.setTime(DateUtils.formatDate2Str(
					DateUtils.getAfterDaysDate(i), DateUtils.FORMAT_YYYY_MM_DD));
			duty.setUserId(userId);
			timeList.add(duty);
		}
		return timeList;
	}

	private int getNurseConfigTime() {
		int days = 16;
		try {
			String time = PropertiesUtil.getContextProperty("nurse.duty.time");
			if (StringUtils.isNotEmpty(time)) {
				days = Integer.parseInt(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	@Override
	public void updateUserServiceTime(Integer userId, String time,
			Integer status) {

		if (null != status
				&& status.intValue() == NurseServiceTimeSetEnum.YES.getIndex()) {
			NurseDutyTime timeDb = new NurseDutyTime();
			timeDb.setUserId(userId);
			timeDb.setStatus(status);
			timeDb.setTime(time);
			insertUserServiceTime(timeDb);
		} else {
			deleteUserServiceTime(userId, time);
		}
	}

	public NurseDutyTime getDutyTime(Integer userId, String time) {
		NurseDutyTime timeDb = null;
		DBObject query = new BasicDBObject();
		query.put("userId", userId);
		query.put("time", time);
		DBObject obj = dsForRW.getDB().getCollection("v_nurse_dutytime")
				.findOne(query);
		if (obj != null) {
			timeDb = new NurseDutyTime();
			timeDb.setUserId(MongodbUtil.getInteger(obj, "userId"));
			timeDb.setStatus(MongodbUtil.getInteger(obj, "status"));
			timeDb.setTime(MongodbUtil.getString(obj, "time"));
		}

		return timeDb;
	}
}
