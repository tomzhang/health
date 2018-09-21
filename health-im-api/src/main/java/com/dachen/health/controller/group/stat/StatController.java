package com.dachen.health.controller.group.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.service.IAssessStatService;
import com.dachen.health.group.stat.service.IUserStatService;
import com.dachen.health.pack.patient.model.PatientDisease;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.stat.entity.param.PackStatParam;
import com.dachen.health.pack.stat.entity.param.PatientStatParam;
import com.dachen.health.pack.stat.service.IDiseaseTypeService;
import com.dachen.health.pack.stat.service.IPackStatService;
import com.dachen.health.pack.stat.service.IPatientStatService;
import com.dachen.line.stat.entity.vo.patientRelateLine;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： StatController<br>
 * Description： 统计<br>
 *
 * @author fanp
 * @version 1.0.0
 * @createTime 2015年9月17日
 */
@RestController
@RequestMapping("/group/stat")
public class StatController {

    @Autowired
    private IAssessStatService assessStatService;

    @Autowired
    private IUserStatService userStatService;

    @Autowired
    private IPackStatService packStatService;

    @Autowired
    private IPatientStatService patientStatService;

    @Autowired
    private IBaseDataService baseDataService;

    @Autowired
    private IDiseaseTypeService diseaseTypeService;

    @Autowired
    private IPatientService patientService;

    /**
     * @api {post} /group/stat/inviteDoctor 统计集团医生邀请数
     * @apiVersion 1.0.0
     * @apiName inviteDoctor
     * @apiGroup 统计
     * @apiDescription 统计医生邀请数
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      doctorId                    医生id，为空则查询整个集团的，不为空查询医生的
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiParam {String}      keyword                      关键词（医生姓名/电话号码）
     * @apiParam {String}      pageSize                    每页显示条数
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiSuccess {Integer}    id                          医生id
     * @apiSuccess {String}     name                        医生姓名
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    value                       邀请数量
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {Long}       time                        邀请时间
     * @apiSuccess {Stirng}     headPicFileName             头像
     * @apiSuccess {Integer}    status             			状态 （医生资格审核状态）
     * @apiSuccess {String}     statusName             		状态名称 （医生资格审核状态）
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/inviteDoctor")
    public JSONMessage inviteDoctor(StatParam param) {
        return JSONMessage.success(null, assessStatService.inviteDoctor(param));
    }

    /**
     * @api {post} /group/stat/invitePatient 统计医生邀请患者数
     * @apiVersion 1.0.0
     * @apiName invitePatient
     * @apiGroup 统计
     * @apiDescription 统计医生邀请患者数
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      doctorId                    医生id，为空则查询整个集团的，不为空查询医生的
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiParam {String}      keyword                      关键词（医生姓名/电话号码）
     * @apiParam {String}      pageSize                    每页显示条数
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiSuccess {Integer}    id                          id（查询集团时为医生id，查询医生时患者id）
     * @apiSuccess {String}     name                        姓名
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    value                       邀请数量
     * @apiSuccess {String}     headPicFileName             头像地址
     * @apiSuccess {String}     sex                         性别(1:男，2：女；3：保密)
     * @apiSuccess {String}     age                         年龄
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {Long}       time                        邀请时间
     * @apiSuccess {Stirng}     headPicFileName             头像
     * @apiSuccess {Integer}    status             			状态 （根据医生查询时 患者激活状态）
     * @apiSuccess {String}     statusName             		状态名称 （根据医生查询时 患者激活状态）
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/invitePatient")
    public JSONMessage invitePatient(StatParam param) {
        //return JSONMessage.success(null,assessStatService.addPatient(param));
        return JSONMessage.success(null, patientService.statPatient(param));
    }

    /**
     * @api {post} /group/stat/orderMoney 统计医生订单金额
     * @apiVersion 1.0.0
     * @apiName orderMoney
     * @apiGroup 统计
     * @apiDescription 统计医生订单金额
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      doctorId                    医生id，为空则查询整个集团的，不为空查询医生的
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiParam {String}      keyword                      关键词（医生姓名/电话号码）
     * @apiParam {String}      pageSize                    每页显示条数
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiSuccess {Integer}    id                          id（查询集团时为医生id，查询医生时患者id）
     * @apiSuccess {String}     name                        姓名（查询集团时为医生name，查询医生时患者name）
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    amount                      订单数量
     * @apiSuccess {Integer}    money                       订单金额
     * @apiSuccess {String}     orderNo                     订单编号
     * @apiSuccess {String}     orderType                   订单类型
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {Long}       time                        订单时间（完成时间）
     * @apiSuccess {Stirng}     headPicFileName             头像
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/orderMoney")
    public JSONMessage orderMoney(PackStatParam param) {
        return JSONMessage.success(null, packStatService.orderMoney(param));
    }

    /**
     * @api {post} /group/stat/disease 统计医生病种
     * @apiVersion 1.0.0
     * @apiName disease
     * @apiGroup 统计
     * @apiDescription 统计医生病种
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      diseaseId                   病种id（为“0”查科室）
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiSuccess {String}     diseaseId                   病种id
     * @apiSuccess {String}     name                        病种名称
     * @apiSuccess {Integer}    value                       数量
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/disease")
    public JSONMessage disease(StatParam param) {
        return JSONMessage.success(null, userStatService.statDoctorDisease(param));
    }

    /**
     * @api {post} /group/stat/title 统计医生职称
     * @apiVersion 1.0.0
     * @apiName title
     * @apiGroup 统计
     * @apiDescription 统计医生职称，职称为认证信息的职称，非职业信息的职称
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    value                       数量
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/title")
    public JSONMessage title(StatParam param) {
        return JSONMessage.success(null, userStatService.statTitle(param));
    }

    /**
     * @api {post} /group/stat/doctorArea 统计医生区域
     * @apiVersion 1.0.0
     * @apiName doctorArea
     * @apiGroup 统计
     * @apiDescription 统计医生区域
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      areaId                      地区编码（为空时查省）
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiSuccess {String}     id                          地区编码
     * @apiSuccess {String}     name                        地区名称
     * @apiSuccess {Integer}    value                       数量
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/doctorArea")
    public JSONMessage doctorArea(StatParam param) {
        return JSONMessage.success(null, userStatService.statDoctorArea(param));
    }

    /**
     * @api {post} /group/stat/statDoctor 统计医生
     * @apiVersion 1.0.0
     * @apiName statDoctor
     * @apiGroup 统计
     * @apiDescription 多维度统计医生（1：病种、2：职称、3：区域）
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      type                        统计维度（1：病种、2：职称、3：区域）
     * @apiParam {String}      typeId                      统计维度Id（职称无Id，传名称）
     * @apiParam {Boolean}     showOnJob                   是否显示在职医生(true 只显示在职医生，false 显示全部)
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiParam {String}      pageSize                    每页显示条数，默认15条
     * @apiSuccess {String}     userId                      用户Id
     * @apiSuccess {String}     name                        名称
     * @apiSuccess {Integer}    age                         年龄
     * @apiSuccess {Integer}    sex                         性别（1：男、2：女）
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {String}     headPicFileName             头像
     * @apiSuccess {String}    	doctorNum                   医生号
     * @apiSuccess {String}    	title                   	职称
     * @apiSuccess {String}     remarks             		备注
     * @apiAuthor 谢平
     * @date 2015年12月4日
     */
    @RequestMapping("/statDoctor")
    public JSONMessage statDoctor(StatParam param) {
        return JSONMessage.success(null, userStatService.statDoctor(param));
    }


    /**
     * @api {post} /group/stat/patient 统计患者
     * @apiVersion 1.0.0
     * @apiName patient
     * @apiGroup 统计
     * @apiDescription 多维度统计患者（集团、组织架构和医生、科室和病种）   -- 病种稍后实现
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      id                          统计维度id
     * @apiParam {String}      type                        统计维度（1：集团；2：组织机构、3：医生、4：病种  5：未分配医生）
     * @apiParam {String}      pageSize                    每页显示条数
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiSuccess {String}     id                          数据id
     * @apiSuccess {String}     name                        数据名称
     * @apiSuccess {Integer}    age                         年龄
     * @apiSuccess {Integer}    sex                         性别（1：男；2：女；3：保密）
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {String}     headPicFileName             头像
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     * @deprecated by qinyuan.chen
     */
    @Deprecated
    @RequestMapping("/patient")
    public JSONMessage patient(StatParam param, String id) {
        if (param.getType() != null && param.getType() == 4) {
            // 按病种统计
            PatientStatParam pParam = new PatientStatParam();
            pParam.setDiseaseIds(baseDataService.getDiseaseTypeChildren(id));
            pParam.setPageIndex(param.getPageIndex());
            pParam.setPageSize(param.getPageSize());
            //获取集团下所有医生
            param.setType(1);
            List<Integer> doctorIds = userStatService.getDoctorId(param, id);
            pParam.setDoctorIds(doctorIds);
            pParam.setGroupId(param.getGroupId());
            return JSONMessage.success(null, patientStatService.getUserByDiseaseAndDoctor(pParam));
        }

        return JSONMessage.success(null, patientStatService.statGroupPatient(param, id));
    }


    /**
     * @api {post} /group/stat/groupPatient 集团患者库,用户列表
     * @apiVersion 1.0.0
     * @apiName groupPatient
     * @apiGroup 统计
     * @apiDescription 统计集团患者
     * @apiParam {String}      access_token                token，必传
     * @apiParam {String}      groupId                     集团id，必传
     * @apiParam {String}      id                          统计维度id，可选
     * @apiParam {String}      type                        统计维度（1：集团；2：组织机构、3：医生、4：病种  5：未分配医生）,必传
     * @apiParam {String}      pageSize                    每页显示条数
     * @apiParam {Integer}     pageIndex                   查询页，从0开始
     * @apiSuccess {String}     id                          数据id
     * @apiSuccess {String}     name                        数据名称
     * @apiSuccess {Integer}    age                         年龄
     * @apiSuccess {Integer}    sex                         性别（1：男；2：女；3：保密）
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {String}     headPicFileName             头像
     * @apiAuthor qinyuan.chen
     * @date 2016年12月28日
     */
    @RequestMapping("/groupPatient")
    public JSONMessage groupPatient(StatParam param, String id) {
        if (Objects.nonNull(param.getType()) && param.getType().intValue() == 4) {
            // 按病种统计
            param.setDiseaseIds(baseDataService.getDiseaseTypeChildren(id));
            //获取集团下所有医生
            //param.setType(1);
            List<Integer> doctorIds = userStatService.getDoctorId(param, id);
            param.setDoctorIds(doctorIds);
            return JSONMessage.success(null, patientStatService.queryGroupPatient(param, id));
        }
        return JSONMessage.success(null, patientStatService.queryGroupPatient(param, id));
    }


    /**
     * @api {get} /group/stat/patient/member 当前用户被医生治疗过的患者
     * @apiVersion 1.0.0
     * @apiName patientMember
     * @apiGroup 统计
     * @apiDescription 当前用户被医生治疗过的患者(医生由集团、组织架构、病种或直接获得)
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      userId                      用户id
     * @apiParam {String}      id                          统计维度id
     * @apiParam {String}      type                        统计维度（1：集团；2：组织机构、3：医生、4：病种）
     * @apiSuccess {String}     id                          数据id
     * @apiSuccess {String}     name                        数据名称
     * @apiSuccess {Integer}    age                         年龄
     * @apiSuccess {Integer}    sex                         性别（1：男；2：女；3：保密）
     * @apiSuccess {String}     telephone                   手机
     * @apiSuccess {String}     relation                    关系
     * @apiSuccess {String}     top_path                    头像
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/patient/member")
    public JSONMessage patientMember(StatParam param, String id, Integer userId) {
        if (userId == null) {
            return JSONMessage.success();
        }

        if (param.getType() != null && param.getType() == 4) {
            // 按病种统计
            PatientStatParam pParam = new PatientStatParam();
            pParam.setDiseaseIds(baseDataService.getDiseaseTypeChildren(id));
            pParam.setUserId(userId);
            pParam.setGroupId(param.getGroupId());
            return JSONMessage.success(null, patientStatService.getByUserAndDisease(pParam));
        }

        List<Integer> doctorIds = userStatService.getDoctorId(param, id);
        if (doctorIds.size() == 0) {
            return JSONMessage.success();
        }
        PatientStatParam psParam = new PatientStatParam();
        psParam.setDoctorIds(doctorIds);
        psParam.setUserId(userId);
        psParam.setStatus(OrderEnum.OrderStatus.已完成.getIndex());
        psParam.setGroupId(param.getGroupId());
        return JSONMessage.success(null, patientStatService.getByUserAndDoctor(psParam));
    }

    /**
     * @api {get} /group/stat/patient/regions 根据集团id，获取集团患者地区信息
     * @apiVersion 1.0.0
     * @apiName patientRegions
     * @apiGroup 统计
     * @apiDescription 根据集团id，获取集团患者地区信息
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiSuccess {List}      data			          返回的省市区信息
     * @apiSuccess {String}    data.province   省份
     * @apiSuccess {List}      data.city	         城市
     * @apiAuthor 傅永德
     * @date 2016年11月8日
     */
    @RequestMapping("/patient/regions")
    public JSONMessage patientRegions(String groupId) {
        return JSONMessage.success(patientStatService.patientRegions(groupId));
    }

    /**
     * @api {get} /group/stat/patient/diseases 根据集团id，获取集团患者病种信息
     * @apiVersion 1.0.0
     * @apiName patientDiseases
     * @apiGroup 统计
     * @apiDescription 根据集团id，获取集团患者病种信息
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiSuccess {List}      data			          返回的病种信息
     * @apiSuccess {String}    data.id			病种id
     * @apiSuccess {List}      data.name		病种名称
     * @apiSuccess {List}      data.leaf		是否为叶子节点
     * @apiSuccess {List}      data.parent		病种的父级id
     * @apiSuccess {List}      data.children	病种的叶子节点信息
     * @apiAuthor 傅永德
     * @date 2016年11月8日
     */
    @RequestMapping("/patient/diseases")
    public JSONMessage patientDiseases(String groupId) {
        return JSONMessage.success(patientStatService.patientDiseases(groupId));
    }

    /**
     * @api {get} /group/stat/patient/infos 查询集团患者信息
     * @apiVersion 1.0.0
     * @apiName patientInfos
     * @apiGroup 统计
     * @apiDescription 查询集团患者信息
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      province                     省（暂无传"暂无"）
     * @apiParam {String}      city                     市（该字段在省不为空时才有意义）
     * @apiParam {String[]}      diseaseTypeId           疾病id(多个的话用英文逗号隔开)
     * @apiParam {Integer}      pageIndex                     页码
     * @apiParam {Integer}      pageSize                     页面大小
     * @apiSuccess {List}      data			          返回的患者信息
     * @apiSuccess {String}      data.ageStr		年龄
     * @apiSuccess {String}      data.area	    地区
     * @apiSuccess {List}      data.diseaseTypeNames 疾病信息
     * @apiSuccess {Integer}      data.id 	患者的id
     * @apiSuccess {String}      data.name 患者的姓名
     * @apiSuccess {Integer}      data.sex 	患者的性别（1表示男，2表示女）
     * @apiSuccess {String}      data.topPath 	患者的头像
     * @apiAuthor 傅永德
     * @date 2016年11月8日
     */
    @RequestMapping("/patient/infos")
    public JSONMessage patientInfos(
            @RequestParam(name = "groupId", required = true) String groupId,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "diseaseTypeId", required = false) String[] diseaseTypeId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize
    ) {
        PageVO pageVO = patientStatService.patientInfos(groupId, province, city, diseaseTypeId, pageIndex, pageSize);
        return JSONMessage.success(pageVO);
    }

    /**
     * @api {get} /group/stat/patient/count 查询集团患者总数
     * @apiVersion 1.0.0
     * @apiName patientCount
     * @apiGroup 统计
     * @apiDescription 查询集团患者总数
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiSuccess {Integer}      data			          返回集团的患者总数
     * @apiAuthor 傅永德
     * @date 2016年11月8日
     */
    @RequestMapping("/patient/count")
    public JSONMessage patientCount(
            @RequestParam(name = "groupId", required = true) String groupId
    ) {
        Integer count = patientStatService.patientCount(groupId);
        return JSONMessage.success(count);
    }

    /**
     * @api {get} /group/stat/patient/cureRecord 医生治疗过的患者的诊疗记录
     * @apiVersion 1.0.0
     * @apiName patientCureRecord
     * @apiGroup 统计
     * @apiDescription 医生治疗过的患者的诊疗记录(医生由集团、组织架构、病种或直接获得)
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiParam {String}      patientId                   患者id
     * @apiParam {String}      id                          统计维度id
     * @apiParam {String}      type                        统计维度（1：集团；2：组织机构、3：医生、4：病种(4已经废弃，在按病种统计时，type传1)）
     * @apiSuccess {String}     id                          数据id
     * @apiSuccess {String}     diseaseTypeName             病种
     * @apiSuccess {String}     name                  		所属医生
     * @apiSuccess {Long}       createTime                  时间
     * @apiAuthor 范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/patient/cureRecord")
    public JSONMessage patientCureRecord(StatParam param, String id, Integer patientId) {
        if (patientId == null) {
            return JSONMessage.success();
        }

        //if(param.getType()!=null && param.getType() == 4 ){
        // 按病种统计
        //PatientStatParam pParam = new PatientStatParam();
        //pParam.setDiseaseIds(baseDataService.getDiseaseTypeChildren(id));
        //pParam.setPatientId(patientId);
        //return JSONMessage.success(null,patientStatService.getCureRecordByDisease(pParam));
        //}
        //诊疗记录的统计不再区分纬度
        List<Integer> doctorIds = userStatService.getDoctorId(param, id);
        if (doctorIds.size() == 0) {
            return JSONMessage.success();
        }
        PatientStatParam psParam = new PatientStatParam();
        psParam.setDoctorIds(doctorIds);
        psParam.setPatientId(patientId);
        psParam.setGroupId(param.getGroupId());
        return JSONMessage.success(null, patientStatService.getCureRecordByDoctor(psParam));
    }

    /**
     * @api {get} /group/stat/getDiseaseTree 获取集团病种树
     * @apiVersion 1.0.0
     * @apiName getDiseaseTree
     * @apiGroup 统计
     * @apiDescription 获取集团病种树
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团id
     * @apiSuccess {String}      id                          病种id
     * @apiSuccess {String}      name                        病种名称
     * @apiSuccess {Object}      children                    子节点
     * @apiAuthor 范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDiseaseTree")
    public JSONMessage getDiseaseTree(String groupId) {
        StatParam param = new StatParam();
        param.setGroupId(groupId);
        param.setType(1);
        List<Integer> doctorIds = userStatService.getDoctorId(param, null);

        List<String> diseaseList = patientStatService.getDiseaseByDoctorAndGroup(doctorIds, groupId);

        return JSONMessage.success(null, baseDataService.getDiseaseTypeTree(diseaseList));
    }

    /**
     * @api {get} /group/stat/getDiseaseTypeTree4CareNew 获取有数据的集团病种树
     * @apiVersion 1.0.0
     * @apiName getDiseaseTypeTree4CareNew
     * @apiGroup 统计
     * @apiDescription 根据模板类型获取科室病种树（健康关怀）
     * @apiParam {String}	access_token	token
     * @apiParam {String}	groupId			集团ID
     * @apiSuccess {String}      id         病种id
     * @apiSuccess {String}      name       病种名称
     * @apiSuccess {Object}      children   子节点
     * @apiAuthor 谢平
     * @date 2015年10月29日
     */
    @RequestMapping("/getDiseaseTypeTree4CareNew")
    public JSONMessage getDiseaseTypeTree4CareNew(String groupId) {
    	
    	Set<String> diseaseIdSet = diseaseTypeService.findCarePlanDiseaseTypeIdSet(groupId, 1);
    	
    	List<ExtTreeNode> ret = diseaseTypeService.getLevel1DiseaseTypeTree(diseaseIdSet);

        return JSONMessage.success(null, ret);
    }

    /**
     * @api {get} /group/stat/getNewDiseaseTypeTree 获取有数据的集团病种树(新)
     * @apiVersion 1.0.0
     * @apiName getNewDiseaseTypeTree
     * @apiGroup 统计
     * @apiDescription 根据模板类型获取科室病种树(新)
     * @apiParam {String}	access_token	token
     * @apiParam {String}	groupId			集团ID
     * @apiParam {Integer}	tmpType			模板类型：5生活量表、6调查表表
     * @apiParam {Integer}	includePlatform	是否包含平台数据（1、包含；0、不包含）
     * @apiSuccess {String}      id         病种id
     * @apiSuccess {String}      name       病种名称
     * @apiSuccess {Object}      children   子节点
     * @apiAuthor 谢佩
     * @date 2016年01月22日
     */
    @RequestMapping("/getNewDiseaseTypeTree")
    public JSONMessage getNewDiseaseTypeTree(String groupId, Integer tmpType, Integer includePlatform) throws HttpApiException {
        if (includePlatform == null) {//不传默认只查找本集团，不包含平台数据
            includePlatform = 0;
        }
        List<DiseaseTypeVO> voList = diseaseTypeService.getNewDiseaseType(groupId, tmpType, includePlatform == 1 ? true : false);
        List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        for (DiseaseTypeVO vo : voList) {
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());

            nodeList.add(node);
        }

        return JSONMessage.success(null, ExtTreeUtil.buildTree(nodeList));
    }

}
