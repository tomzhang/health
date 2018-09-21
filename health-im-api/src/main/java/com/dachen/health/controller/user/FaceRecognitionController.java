package com.dachen.health.controller.user;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.impl.FaceRecognitionService;
import com.dachen.health.commons.vo.FaceRecognition;
import com.dachen.health.commons.vo.UserConstant;
import com.dachen.util.ReqUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Author: xuhuanjie
 * Date: 2018-01-04
 * Time: 20:09
 * Description:
 */
@RestController
@RequestMapping("/faceRec")
public class FaceRecognitionController {

    @Resource
    private FaceRecognitionService faceRecognitionService;

    /**
     * @api {[get,post]} /faceRec/passFaceRec
     * @apiVersion 1.0.0
     * @apiName /faceRec/passFaceRec
     * @apiGroup 用户
     * @apiDescription 通过活体脸部检测
     * @apiParam {String}    access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiParam {String}	faceImage	    照片URL
     * @apiSuccess {String} resultCode 返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月4日
     */
    @RequestMapping("/passFaceRec")
    public JSONMessage passFaceRecognition(@RequestParam Integer userId, @RequestParam String faceImage) {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }
        if (Objects.isNull(faceRecognitionService.getFaceRecRecord(userId))) {
            faceRecognitionService.addFaceRecRecord(userId, faceImage, UserConstant.FaceRecognitionStatus.pass.getIndex());
        } else {
            faceRecognitionService.updateFaceRecRecord(userId, faceImage, UserConstant.FaceRecognitionStatus.pass.getIndex());
        }
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /faceRec/failFaceRec
     * @apiVersion 1.0.0
     * @apiName /faceRec/failFaceRec
     * @apiGroup 用户
     * @apiDescription 活体脸部检测失败
     * @apiParam {String}    access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiParam {String}	faceImage	    照片URL
     * @apiSuccess {String} resultCode 返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月4日
     */
    @RequestMapping("/failFaceRec")
    public JSONMessage failFaceRecognition(@RequestParam Integer userId, @RequestParam String faceImage) {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }
        FaceRecognition faceRecognition = faceRecognitionService.getFaceRecRecord(userId);
        if (Objects.isNull(faceRecognition)) {
            faceRecognitionService.addFaceRecRecord(userId, faceImage, UserConstant.FaceRecognitionStatus.fail.getIndex());
        } else if (Objects.equals(faceRecognition.getPassed(), 0)) {
            faceRecognitionService.updateFaceRecRecord(userId, faceImage, UserConstant.FaceRecognitionStatus.fail.getIndex());
        }
        return JSONMessage.success();
    }

}
