package com.dachen.health.pack.illhistory.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.util.ReqUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.IllHistoryEnum;
import com.dachen.health.pack.illhistory.dao.IllHistoryRecordDao;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;

import java.util.List;

/**
 * Created by wangl on 2016/12/5.
 */
@Repository
public class IllHistoryRecordImpl extends NoSqlRepository implements IllHistoryRecordDao {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public IllHistoryRecord insert(IllHistoryRecord hr) {
        Long now = System.currentTimeMillis();
        hr.setCreateTime(now);
        hr.setUpdateTime(now);
        dsForRW.insert(hr);
        return hr;
    }

    @Override
    public IllHistoryRecord insertWithOutSetTime(IllHistoryRecord illHistoryRecord) {
        dsForRW.insert(illHistoryRecord);
        return illHistoryRecord;
    }

    @Override
    public IllHistoryRecord findByOrderIdAndType(Integer orderId, Integer type) {
        if (null == orderId || null == type) {
            return new IllHistoryRecord();
        }

        if (type == IllHistoryEnum.IllHistoryRecordType.care.getIndex()) {
            return dsForRW.createQuery(IllHistoryRecord.class).filter("recordCare.orderId", orderId).get();
        } else if (type == IllHistoryEnum.IllHistoryRecordType.consultation.getIndex()) {
            return dsForRW.createQuery(IllHistoryRecord.class).filter("recordConsultiation.orderId", orderId).get();
        } else if (type == IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex()) {
            return dsForRW.createQuery(IllHistoryRecord.class).filter("recordCheckIn.orderId", orderId).get();
        } else {
            return dsForRW.createQuery(IllHistoryRecord.class).filter("recordOrder.orderId", orderId).get();
        }

    }

    @Override
    public void updateFromOrder(Integer orderId, String drugCase, List<String> drugGoodsIds, List<String> drugPicUrls, String hopeHelp, List<String> picUrls) {
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("订单为空");
        }
        Integer packType = order.getPackType();
        if (packType != null) {
            if (packType.intValue() == PackEnum.PackType.phone.getIndex()
                    || packType.intValue() == PackEnum.PackType.message.getIndex()
                    || packType.intValue() == PackEnum.PackType.integral.getIndex()
                    || packType.intValue() == PackEnum.PackType.online.getIndex()
                    ) {
                //图文、电话、积分、门诊
                Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("recordOrder.orderId").equal(orderId);
                UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
                if (StringUtils.isNotEmpty(drugCase)) {
                    ops.set("recordOrder.drugCase", drugCase);
                }
                if (drugGoodsIds != null && drugGoodsIds.size() > 0) {
                    ops.set("recordOrder.drugGoodsIds", drugGoodsIds);
                }
                if (drugPicUrls != null && drugPicUrls.size() > 0) {
                    ops.set("recordOrder.drugPicUrls", drugPicUrls);
                }
                if (StringUtils.isNotEmpty(hopeHelp)) {
                    ops.set("recordOrder.hopeHelp", hopeHelp);
                }
                if (picUrls != null && picUrls.size() > 0) {
                    ops.set("recordOrder.pics", picUrls);
                }
                ops.set("updateTime", System.currentTimeMillis());
                dsForRW.update(query, ops);

            } else if (packType.intValue() == PackEnum.PackType.careTemplate.getIndex()) {
                //健康关怀
                Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("recordCare.orderId").equal(orderId);
                UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
                if (StringUtils.isNotEmpty(drugCase)) {
                    ops.set("recordCare.drugCase", drugCase);
                }
                if (drugGoodsIds != null && drugGoodsIds.size() > 0) {
                    ops.set("recordCare.drugGoodsIds", drugGoodsIds);
                }
                if (drugPicUrls != null && drugPicUrls.size() > 0) {
                    ops.set("recordCare.drugPicUrls", drugPicUrls);
                }
                if (StringUtils.isNotEmpty(hopeHelp)) {
                    ops.set("recordCare.hopeHelp", hopeHelp);
                }
                if (picUrls != null && picUrls.size() > 0) {
                    ops.set("recordCare.pics", picUrls);
                }
                ops.set("updateTime", System.currentTimeMillis());
                dsForRW.update(query, ops);

            }
        }
    }

    @Override
    public void updateRecordPayOrSessionGroup(Integer orderId, Boolean isPay, String msgGroupId) {
        // 判断isPay和msgGroupId是否为空，不为空则更新
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("订单为空");
        }
        Integer packType = order.getPackType();
        if (packType != null) {
            if (packType.intValue() == PackEnum.PackType.phone.getIndex()
                    || packType.intValue() == PackEnum.PackType.message.getIndex()
                    || packType.intValue() == PackEnum.PackType.integral.getIndex()
                    || packType.intValue() == PackEnum.PackType.online.getIndex()
                    ) {
                //图文、电话、积分、门诊
                Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("recordOrder.orderId").equal(orderId);
                UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
                if (isPay != null) {
                    ops.set("recordOrder.isPay", isPay);
                }
                if (StringUtils.isNotEmpty(msgGroupId)) {
                    ops.set("recordOrder.messageGroupId", msgGroupId);
                }
                dsForRW.update(query, ops);

            } else if (packType.intValue() == PackEnum.PackType.careTemplate.getIndex()) {
                //健康关怀
                Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("recordCare.orderId").equal(orderId);
                UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
                if (isPay != null) {
                    ops.set("recordCare.isPay", isPay);
                }
                if (StringUtils.isNotEmpty(msgGroupId)) {
                    ops.set("recordCare.messageGroupId", msgGroupId);
                }
                dsForRW.update(query, ops);
            } else if (packType.intValue() == PackEnum.PackType.consultation.getIndex()) {
                //远程会诊
                Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("recordConsultiation.orderId").equal(orderId);
                UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
                if (isPay != null) {
                    ops.set("recordConsultiation.isPay", isPay);
                }
                if (StringUtils.isNotEmpty(msgGroupId)) {
                    ops.set("recordConsultiation.messageGroupId", msgGroupId);
                }
                dsForRW.update(query, ops);
            }
        }

    }

    @Override
    public List<IllHistoryRecord> findByIllHistoryInfoIdForDoctor(String illHistoryInfoId, Integer doctorId, Integer pageIndex, Integer pageSize) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class);
        query.field("illHistoryInfoId").equal(illHistoryInfoId);
        query.and(
                query.criteria("recordOrder.isPay").notEqual(false),
                query.criteria("recordCare.isPay").notEqual(false),
                query.criteria("recordConsultiation.isPay").notEqual(false)
        );
        query.or(
                query.criteria("secret").notEqual(true),
        		query.criteria("creater").equal(doctorId),
                query.criteria("forDoctorId").equal(doctorId)
        );
        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;

        query.offset(skip);
        query.limit(pageSize);
        query.order("-updateTime");

        return query.asList();
    }

    @Override
    public Long findByIllHistoryInfoIdForDoctorCount(String illHistoryInfoId, Integer doctorId) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class);
        query.field("illHistoryInfoId").equal(illHistoryInfoId);
        query.and(
                query.criteria("recordOrder.isPay").notEqual(false),
                query.criteria("recordCare.isPay").notEqual(false),
                query.criteria("recordConsultiation.isPay").notEqual(false)
        );
        query.or(
                query.criteria("secret").notEqual(true),
                query.criteria("creater").equal(doctorId),
                query.criteria("forDoctorId").equal(doctorId)
        );
        query.order("-updateTime");
        return query.countAll();
    }

    @Override
    public List<IllHistoryRecord> findByIllHistoryInfoIdForPatient(String illHistoryInfoId, Integer pageIndex, Integer pageSize) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class);
        query.field("illHistoryInfoId").equal(illHistoryInfoId);
        query.and(
                query.criteria("recordOrder.isPay").notEqual(false),
                query.criteria("recordCare.isPay").notEqual(false),
                query.criteria("recordConsultiation.isPay").notEqual(false),
                query.criteria("secret").notEqual(true)
        );

        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;

        query.offset(skip);
        query.limit(pageSize);
        query.order("-updateTime");

        return query.asList();
    }

    @Override
    public Long findByIllHistoryInfoIdForPatient(String illHistoryInfoId) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class);
        query.field("illHistoryInfoId").equal(illHistoryInfoId);
        query.and(
                query.criteria("recordOrder.isPay").notEqual(false),
                query.criteria("recordCare.isPay").notEqual(false),
                query.criteria("recordConsultiation.isPay").notEqual(false),
                query.criteria("secret").notEqual(true)
        );
        query.order("-updateTime");
        return query.countAll();
    }

	@Override
	public IllHistoryRecord findLastRecordByIllhistoryInfoId(String illHistoryInfoId) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class);
        query.field("illHistoryInfoId").equal(illHistoryInfoId).order("-createTime");
        query.or(query.criteria("type").equal(IllHistoryEnum.IllHistoryRecordType.order.getIndex()),
        		query.criteria("type").equal(IllHistoryEnum.IllHistoryRecordType.care.getIndex())
        );
        
		return query.get();
	}

    @Override
    public void updateIllHistoryInfoId(String illHistoryRecordId, String illHistoryInfoId) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
        ops.set("illHistoryInfoId", illHistoryInfoId);
        dsForRW.update(query, ops);
    }

    @Override
    public void fixCheckIn(String illHistoryRecordId, IllHistoryRecord illHistoryRecord) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
        ops.set("recordCheckIn", illHistoryRecord.getRecordCheckIn());
        dsForRW.update(query, ops);
    }

    @Override
    public void removeDirtyCheckInData() {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("type").equal(4).field("recordCheckIn").doesNotExist();
        dsForRW.delete(query);
    }

    @Override
    public void removeDirtyCareData() {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("type").equal(3).field("recordCare").doesNotExist();
        dsForRW.delete(query);
    }

    @Override
    public void fixCareGroupDoctors(String illHistoryRecordId, List<Integer> groupDoctors) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
        if (groupDoctors != null && groupDoctors.size() > 0) {
            ops.set("recordCare.groupDoctors", groupDoctors);
        } else {
            ops.unset("recordCare.groupDoctors");
        }
        dsForRW.update(query, ops);
    }

    @Override
    public void removeDirtyOrderStatus(String illHistoryRecordId) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        dsForRW.delete(query);
    }

    @Override
    public void fixRecordCareTreatCase(String illHistoryRecordId, String treatCase) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
        if (StringUtils.isNotBlank(treatCase)) {
            ops.set("recordCare.treatCase", treatCase);
        } else {
            ops.unset("recordCare.treatCase");
        }
        dsForRW.update(query, ops);
    }

    @Override
    public void fixRecordOrderTreatCase(String illHistoryRecordId, String treatCase) {
        Query<IllHistoryRecord> query = dsForRW.createQuery(IllHistoryRecord.class).field("_id").equal(new ObjectId(illHistoryRecordId));
        UpdateOperations<IllHistoryRecord> ops = dsForRW.createUpdateOperations(IllHistoryRecord.class);
        if (StringUtils.isNotBlank(treatCase)) {
            ops.set("recordOrder.treatCase", treatCase);
        } else {
            ops.unset("recordOrder.treatCase");
        }
        dsForRW.update(query, ops);
    }
}
