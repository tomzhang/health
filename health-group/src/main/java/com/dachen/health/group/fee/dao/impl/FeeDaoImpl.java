package com.dachen.health.group.fee.dao.impl;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.fee.dao.IFeeDao;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;

@Repository
public class FeeDaoImpl extends NoSqlRepository implements IFeeDao{

    /**
     * </p>获取收费设置</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public FeeVO get(String groupId){
        return dsForRW.createQuery("c_group_fee",FeeVO.class).retrievedFields(false,"groupId").field("groupId").equal(groupId).get();
    }

    /**
     * </p>收费设置</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public void save(FeeParam param){
//        DBObject query = new BasicDBObject();
        String groupId = param.getGroupId();
//		query.put("groupId", groupId);
		Query<FeeVO> query = 
				dsForRW.createQuery(FeeVO.class)
				.field("groupId").equal(groupId);
		UpdateOperations<FeeVO> ops = dsForRW.createUpdateOperations(FeeVO.class);
//        DBObject update = new BasicDBObject();
        ops.set("groupId", groupId);
        
        if(param.getClinicMax()!=null){
            ops.set("clinicMax", param.getClinicMax());
        }
        if(param.getClinicMin()!=null){
            ops.set("clinicMin", param.getClinicMin());
        }
        if(param.getPhoneMax()!=null){
            ops.set("phoneMax", param.getPhoneMax());
        }
        if(param.getPhoneMin()!=null){
            ops.set("phoneMin", param.getPhoneMin());
        }
        if(param.getTextMax()!=null){
            ops.set("textMax", param.getTextMax());
        }
        if(param.getTextMax()!=null){
            ops.set("textMin", param.getTextMin());
        }
        
        if(param.getCarePlanMax()!=null){
            ops.set("carePlanMax", param.getCarePlanMax());
        }
        if(param.getCarePlanMin()!=null){
            ops.set("carePlanMin", param.getCarePlanMin());
        }
        
        if(param.getAppointmentMin()!=null){
            ops.set("appointmentMin", param.getAppointmentMin());
        }
        if(param.getAppointmentMax()!=null){
            ops.set("appointmentMax", param.getAppointmentMax());
        }
        if(param.getAppointmentDefault()!=null){
        	ops.set("appointmentDefault", param.getAppointmentDefault());
        }
        dsForRW.update(query, ops, true);
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.fee.dao.IFeeDao#getByGroupIds(java.util.List)
     */
    public List<FeeVO> getByGroupIds(List<String> groupIds ){
    	
//    	DBObject query = new BasicDBObject();
//    	query.put("d", doctorId);
    	return dsForRW.createQuery("c_group_fee",FeeVO.class).field("groupId").in(groupIds).asList();
    	
    }
    
}
