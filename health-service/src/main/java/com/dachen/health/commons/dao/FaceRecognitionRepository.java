package com.dachen.health.commons.dao;

import com.dachen.health.commons.vo.FaceRecognition;

/**
 * Author: xuhuanjie
 * Date: 2018-01-05
 * Time: 11:00
 * Description:
 */
public interface FaceRecognitionRepository {

    FaceRecognition getFaceRecRecord(Integer userId);

    void addFaceRecRecord(FaceRecognition faceRecognition);

    void updateFaceRecRecord(Integer userId, String faceImage, Integer passed);
}
