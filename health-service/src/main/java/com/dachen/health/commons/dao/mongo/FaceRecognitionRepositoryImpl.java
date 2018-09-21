package com.dachen.health.commons.dao.mongo;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.FaceRecognitionRepository;
import com.dachen.health.commons.vo.FaceRecognition;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

/**
 * Author: xuhuanjie
 * Date: 2018-01-05
 * Time: 10:28
 * Description:
 */
@Repository
public class FaceRecognitionRepositoryImpl extends NoSqlRepository implements FaceRecognitionRepository {

    public FaceRecognition getFaceRecRecord(Integer userId) {
        Query<FaceRecognition> query = dsForRW.createQuery(FaceRecognition.class).field("userId").equal(userId);

        return query.get();
    }

    public void addFaceRecRecord(FaceRecognition faceRecognition) {
        dsForRW.insert(faceRecognition);
    }

    public void updateFaceRecRecord(Integer userId, String faceImage, Integer passed) {
        Query<FaceRecognition> q = dsForRW.createQuery(FaceRecognition.class).field("userId").equal(userId);
        UpdateOperations<FaceRecognition> ops = dsForRW.createUpdateOperations(FaceRecognition.class);
        ops.set("verifyCount", q.get().getVerifyCount() + 1);
        ops.set("passed", passed);
        ops.set("faceImage", faceImage);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

}
