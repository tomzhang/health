package com.dachen.health.commons.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * User: xuhuanjie
 * Date: 2018-01-04
 * Time: 15:09
 * Description: 用户在平台认证前要进行活体人脸检测
 */
@Entity(value = "user_face_recognition", noClassnameStored = true)
public class FaceRecognition {

    @Id
    private String id;

    @Indexed(unique = true)
    private Integer userId;
    /**
     * 0代表没通过；1代表通过
     */
    private Integer passed;
    /**
     * 用户的验证次数
     */
    private Integer verifyCount;

    private String faceImage;

    private Long createTime;

    private Long modifyTime;

    public FaceRecognition() {

    }

    public FaceRecognition(Integer userId, Integer passed) {
        this.userId = userId;
        this.passed = passed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPassed() {
        return passed;
    }

    public void setPassed(Integer passed) {
        this.passed = passed;
    }

    public Integer getVerifyCount() {
        return verifyCount;
    }

    public void setVerifyCount(Integer verifyCount) {
        this.verifyCount = verifyCount;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }
}
