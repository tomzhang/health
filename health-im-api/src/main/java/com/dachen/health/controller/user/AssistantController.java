package com.dachen.health.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.service.InitAssistantService;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.user.entity.param.DrugVerifyParam;
import com.dachen.health.user.entity.vo.DrugVerifyInfo;
import com.dachen.health.user.service.IAssistantService;
import com.dachen.health.user.service.IRelationService;
import com.dachen.util.ReqUtil;

/**
 * 提供医助的相关接口
 * @author  qujunli
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController extends AbstractController {

    @Autowired
    private IAssistantService assistantService;

    @Autowired
    private IRelationService relationService;     

    @Autowired
    private InitAssistantService initAssistantService;
    
    @Autowired
    private UserManager userManager;
    
    
    @RequestMapping
    public JSONMessage getAssistant(@RequestParam String userId) {
        JSONMessage res = new JSONMessage();

        return res;
    }

    /**
     * 药品核查，只有医助可以使用，根据药监码返回药物的详细信息
     * 
     * @param verifyParam {@link DrugVerifyParam} 其中药监码、经度、维度不能为空
     * @return drugVerifyInfo {@link DrugVerifyInfo}
     */
    @RequestMapping(value = "/verifydrug")
    public JSONMessage verifydrug(@ModelAttribute DrugVerifyParam verifyParam) {
        verifyParam.setUserId(ReqUtil.instance.getUserId());
        Object data = assistantService.verifyDrug(verifyParam);
        return JSONMessage.success("成功", data);
    }

    /**
     * 医助初始化
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/init")
    public JSONMessage initAssistant() throws Exception {
        initAssistantService.impAllAssistant();
        return JSONMessage.success("成功", null);
    }

    /**
     * @api {get} /assistant/getDoctors 医助获取医生
     * @apiVersion 1.0.0
     * @apiName getDoctors
     * @apiGroup 医助
     * @apiDescription 医助获取我的医生
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {String}   users.userId              用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           手机
     * @apiSuccess {Number}   users.sex                 性别
     * @apiSuccess {String}   users.headPicFileName     头像
     * @apiSuccess {String}   users.hospital            医院
     * @apiSuccess {String}   users.departments         科室
     * @apiSuccess {String}   users.title               职称
     * @apiSuccess {Object[]} users.setting                   设置
     * @apiSuccess {Number}   users.setting.defriend          拉黑  1:否，2:是
     * @apiSuccess {Number}   users.setting.topNews           消息置顶 1:否，2：是
     * @apiSuccess {Number}   users.setting.messageMasking    消息屏蔽 1：否，2：是
     * @apiSuccess {Number}   users.setting.collection        收藏 1：否，2：是
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDoctors")
    public JSONMessage getDoctors() {
        return JSONMessage.success(null, relationService.getRelations(RelationType.doctorAssistant, ReqUtil.instance.getUserId()));
    }
    
    /**
     * @api {get} /assistant/getHospitals 医助获取分管医院
     * @apiVersion 1.0.0
     * @apiName getHospitals
     * @apiGroup 医助
     * @apiDescription 医助获取分管医院
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {String}   hospital            医院
     * @apiSuccess {String}   hospitalId          医院id
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getHospitals")
    public JSONMessage getHospitals() {
        return JSONMessage.success(null, assistantService.getHospitals(ReqUtil.instance.getUserId()));
    }
    
    /**
     * @api {get} /assistant/getHospitalDoctors 医助获取分管医院的医生
     * @apiVersion 1.0.0
     * @apiName getHospitalDoctors
     * @apiGroup 医助
     * @apiDescription 医助获取分管医院的医生
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    hospitalId            医院Id
     * 
     * @apiSuccess {String}   userId                用户id
     * @apiSuccess {String}   name                  用户姓名
     * @apiSuccess {String}   nickname              昵称
     * @apiSuccess {String}   headPicFileName     头像
     * @apiSuccess {String}   telephone             手机
     * @apiSuccess {String}   hospital              医院
     * @apiSuccess {String}   departments           科室
     * @apiSuccess {String}   title                 职称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getHospitalDoctors")
    public JSONMessage getHospitalDoctors(String hospitalId) {
        return JSONMessage.success(null, assistantService.getHospitalDoctors(ReqUtil.instance.getUserId(),hospitalId));
    }

}
