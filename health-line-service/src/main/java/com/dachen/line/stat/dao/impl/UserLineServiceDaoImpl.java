package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.comon.constant.NurseServiceSetEnum;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.IUserLineServiceDao;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.UserLineService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class UserLineServiceDaoImpl extends NoSqlRepository implements
		IUserLineServiceDao {
	@Autowired
	private ILineServiceProductDao lineServiceProductDao;

	@Override
	public List<UserLineService> getUserLineService(Integer userId) {

		List<UserLineService> result = getUserLineServiceById(userId);
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
				result = getUserLineServiceById(userId);
			}
		}

		return result;
	}

	/**
	 * 修改状态
	 */
	@Override
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
	 * 获取制定用户的设置列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserLineService> getUserLineServiceById(Integer userId) {
		List<UserLineService> result = new ArrayList<UserLineService>();
		Query<UserLineService> uq = dsForRW.createQuery(UserLineService.class)
				.field("userId").equal(userId);// 查询搜有的数据
		result = uq.asList();
		if (null != result && result.size() > 0) {
			for (UserLineService user : result) {
				user.setLineServiceProduct(lineServiceProductDao
						.getSystemLineServiceBean(user.getProductId()));
			}
		}

		return result;
	}

	/**
	 * 批量插入数据
	 */
	@Override
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
	public void insertUserLineService(UserLineService user) {

		if (null != user) {
			List<UserLineService> userList = new ArrayList<UserLineService>();
			userList.add(user);
			insertUserLineServiceList(userList);
		}

	}
}
