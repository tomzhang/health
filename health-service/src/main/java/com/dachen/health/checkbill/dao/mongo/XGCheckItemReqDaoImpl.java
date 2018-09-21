package com.dachen.health.checkbill.dao.mongo;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.checkbill.dao.XGCheckItemReqDao;
import com.dachen.health.checkbill.entity.po.XGCheckItemReq;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Created by fuyongde on 2017/1/12.
 */
@Repository
public class XGCheckItemReqDaoImpl extends NoSqlRepository implements XGCheckItemReqDao {
    @Override
    public void save(XGCheckItemReq xgCheckItemReq) {
        dsForRW.save(xgCheckItemReq);
    }

    @Override
    public XGCheckItemReq findByCheckItemId(String checkItemId) {
        return dsForRW.createQuery(XGCheckItemReq.class).field("checkItemId").equal(checkItemId).get();
    }

    @Override
    public XGCheckItemReq findById(String id) {
        return dsForRW.createQuery(XGCheckItemReq.class).field("_id").equal(new ObjectId(id)).get();
    }

    @Override
    public void update(XGCheckItemReq xgCheckItemReq) {
        Query<XGCheckItemReq> query = dsForRW.createQuery(XGCheckItemReq.class).field("_id").equal(new ObjectId(xgCheckItemReq.getId()));
        UpdateOperations<XGCheckItemReq> ops = dsForRW.createUpdateOperations(XGCheckItemReq.class);

        if (!Objects.isNull(xgCheckItemReq.getSuccess())) {
            ops.set("success", xgCheckItemReq.getSuccess());
        }
        if (!Objects.isNull(xgCheckItemReq.getResponseMessage())) {
            ops.set("responseMessage", xgCheckItemReq.getResponseMessage());
        }

        ops.set("updateTime", System.currentTimeMillis());
        dsForRW.update(query, ops);
    }

    @Override
    public List<XGCheckItemReq> findNoSuccess() {
        return dsForRW.createQuery(XGCheckItemReq.class).field("success").equal(false).asList();
    }

}
