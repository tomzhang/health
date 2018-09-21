package com.dachen.health.commons.dao.mongo;

import com.dachen.health.base.entity.po.QrScanParamPo;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.IQrCodeDao;
import com.dachen.health.commons.entity.QrCode;
import com.mongodb.WriteResult;

@Repository
public class QrCodeDaoImpl extends NoSqlRepository implements IQrCodeDao {

	public QrCode save(QrCode qrcode) {
		String id = dsForRW.save(qrcode).getId().toString();
		return dsForRW.createQuery(QrCode.class).filter("_id", new ObjectId(id)).get();
	}
	
	public QrCode get(String content) {
		return dsForRW.createQuery(QrCode.class).filter("content", content).get();
	}
	
	public boolean delete() {
		Query<QrCode> q = dsForRW.createQuery(QrCode.class);
		WriteResult wr = dsForRW.delete(q);
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	public boolean update(String id, String imageKey) {
		Query<QrCode> q = dsForRW.createQuery(QrCode.class).filter("_id", new ObjectId(id));
		UpdateOperations<QrCode> ops = dsForRW.createUpdateOperations(QrCode.class);
		ops.set("imageKey", imageKey);
		UpdateResults result = dsForRW.update(q, ops);
		if (result != null && result.getUpdatedExisting())
			return true;
		return false;
	}

	@Override
	public String insert(QrScanParamPo param) {
		dsForRW.insert(param);
		return param.id;
	}

	@Override
	public QrScanParamPo getQrScanParam(String id) {
		Query<QrScanParamPo> q = dsForRW.createQuery(QrScanParamPo.class).filter("_id", new ObjectId(id));
		return q.get();
	}

	@Override
    public boolean delete(QrCode q) {
        dsForRW.delete(q);
        return true;
    }
}
