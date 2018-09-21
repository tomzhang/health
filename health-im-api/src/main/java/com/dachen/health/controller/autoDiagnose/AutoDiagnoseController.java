package com.dachen.health.controller.autoDiagnose;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.auto.service.AutoDiagnoseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by liming on 2016/11/7.
 */
@RestController
@RequestMapping("autoDiagnose")
public class AutoDiagnoseController {
    @Autowired
    private AutoDiagnoseService auto;
    /**
     * @api {[get,post]} /autoDiagnose/getBodyPartsList 获取身体部位和症状表
     * @apiVersion 1.0.0
     * @apiName getBodyList
     * @apiGroup 自我诊断
     * @apiDescription 获取身体部位和症状表
     *
     * @apiParam {String} sex  性别 1-男 2-女
     *
     * @apiSuccess	{String}		PartsCode	身体部位编码
     * @apiSuccess	{Long}			PartsName	身体部位名称
     * @apiSuccess  {String}        bodyCode   部位code
     * @apiSuccess  {String}        code       病症code
     * @apiSuccess  {String}        sex        0-共有属性1-男2-女
     * @apiSuccess  {String}        name       病症名称
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  李明
     * @date 2016年11月7日14:32:53
     */
    @RequestMapping("getBodyPartsList")
    public JSONMessage getBodyList(String sex){
        if(StringUtils.isEmpty(sex)){
            throw new ServiceException("请选择性别");
        }

        return JSONMessage.success(auto.getBodyDisease(sex));
    }



    /**
     * @api {[get,post]} /autoDiagnose/getDiseaseList 根据病症获取疑似病症
     * @apiVersion 1.0.0
     * @apiName getDiseaseList
     * @apiGroup 自我诊断
     * @apiDescription 根据身体部位获取病症
     *
     *
     * @apiParam	{String}		symptomsCode	病症编码
     * @apiSuccess       {String[]}                         diseaseIds                          id集合
     * @apiSuccess 		{String} 							disease.id 							病种id
     * @apiSuccess 		{String} 							disease.name 						病种名称
     * @apiSuccess 		{String} 							disease.introduction 				病种简介
     * @apiSuccess 		{String} 							disease.remark 						常见症状
     * @apiSuccess 		{String} 							disease.attention 					注意事项
     * @apiSuccess 		{String[]} 							disease.alias						别名
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  李明
     * @date 2016年11月7日14:32:53
     */
    @RequestMapping("getDiseaseList")
    public JSONMessage getDiseaseList(String symptomsCode){
        if(StringUtils.isEmpty(symptomsCode)){
            throw new ServiceException("请传入对应的病症id");
        }
        return JSONMessage.success(auto.getDiseaseList(symptomsCode));
    }

    /**
     * 根据病症获取疑似病症详情(分页)
     * @return
     */
    @RequestMapping("getPageSuspectedByCode")
    public JSONMessage getPageSuspectedByCode(){
        return JSONMessage.success();
    }
    @RequestMapping("excel")
    public JSONMessage excel(@RequestParam("file") MultipartFile file){
        auto.excel(file);
        return JSONMessage.success();
    }


}
