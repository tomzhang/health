package com.dachen.health.pack.guide.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CHelpRecord;
import com.dachen.careplan.api.entity.CWarningRecord;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.guide.entity.vo.HelpVO;
import com.dachen.sdk.exception.HttpApiException;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.guide.entity.po.ConsultOrderOtherPO;

@Repository
public class ConsultOrderOtherDaoImpl extends NoSqlRepository implements IConsultOrderOtherDao {

	@Override
	public String receiveCareOrder(ConsultOrderOtherPO po) {
		String id = null;
		try {
			Key<ConsultOrderOtherPO> key = dsForRW.insert(po);
			if (null != key) {
				id = key.getId().toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public boolean checkCareOrder(HelpVO vo) {
		ConsultOrderOtherPO po = dsForRW.createQuery(ConsultOrderOtherPO.class).filter("fromId", vo.getFromId())
				.filter("state", 0).get();
		return po == null ? true : false;
	}

	@Override
	public boolean checkByOrderId(Integer orderId) {
		ConsultOrderOtherPO po = dsForRW.createQuery(ConsultOrderOtherPO.class).filter("orderId", orderId)
				.filter("state", 0).get();
		return po == null ? true : false;
	}

	@Override
	public List<ConsultOrderOtherPO> getHandleCareOrder(Integer userId) {
		Query<ConsultOrderOtherPO> q = dsForRW.createQuery(ConsultOrderOtherPO.class).filter("guideId", userId)
				.filter("state", 0);
		return q.asList();
	}

	@Autowired
	protected CarePlanApiClientProxy carePlanApiClientProxy;

	@Override
	public CHelpRecord updateHelpInfoByOrderId(String id) throws HttpApiException {
		ConsultOrderOtherPO orderPo = this.updateGuideOrder(id);
		if(orderPo==null){
			throw new ServiceException("找不到订单！！！");
		}
		CHelpRecord helpRecord = carePlanApiClientProxy.updateHelpRecordStatusFinished(orderPo.getFromId());
		return helpRecord;
	}

	@Override
	public CWarningRecord updateWarningInfoByOrderId(String id) throws HttpApiException {
		ConsultOrderOtherPO orderPo = this.updateGuideOrder(id);
		if(orderPo==null){
			throw new ServiceException("找不到订单！！！");
		}

		CWarningRecord warningRecord = carePlanApiClientProxy.updateWarningRecordStatusFinished(orderPo.getFromId());
		return warningRecord;
	}

	/**
	 * 更新导医接单表的状态为1
	 * @param id
	 */
	public ConsultOrderOtherPO updateGuideOrder(String id){
		try {
			Query<ConsultOrderOtherPO> q = dsForRW.createQuery(ConsultOrderOtherPO.class).filter("_id", new ObjectId(id));
			Map<String,Object> updateValue = new HashMap<String,Object>();
			updateValue.put("state", 1);
			UpdateOperations<ConsultOrderOtherPO> ops = dsForRW.createUpdateOperations(ConsultOrderOtherPO.class);
			for( Map.Entry<String, Object> eachObj:updateValue.entrySet()){
				ops.set(eachObj.getKey(), eachObj.getValue());
			}
			ConsultOrderOtherPO info = dsForRW.findAndModify(q, ops);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
