package com.dachen.health.controller.group.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.ExistRegionVo;
import com.dachen.health.base.entity.po.RegionVo;
import com.dachen.health.base.entity.vo.ServiceTypeVO;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.po.GroupUserApply;
import com.dachen.health.group.group.entity.vo.GroupApplyDetailPageVo;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author pijingwei
 * @date 2015/8/7
 */
@RestController
@RequestMapping("/group")
public class GroupController extends AbstractController {

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IGroupFacadeService groupFacadeService;

    @Autowired
    private ICompanyUserService companyUserService;

    @Autowired
    IImageDataService imageDataService;

    @Autowired
    IBusinessServiceMsg businessMsgServiceImpl;

    @Autowired
    private PackMapper packMapper;

    @Autowired
    private IPackService packService;

    /**
     * @api {post} /group/regGroup 注册集团
     * @apiVersion 1.0.0
     * @apiName regGroup
     * @apiGroup 集团模块
     * @apiDescription 注册集团
     * @apiParam {String}    	access_token                token
     * @apiParam {String}   	companyId           		所属公司Id
     * @apiParam {String}   	name       					集团名称
     * @apiParam {String}   	introduction       			集团简介
     * @apiParam {Object[]}  	config    					集团设置信息
     * @apiParam {boolean}  	config.memberInvite    		是否允许成员邀请医生加入  true：允许，false：不允许
     * @apiParam {boolean}  	config.passByAudit   		成员邀请是否需要审核  true：允许，false：不允许
     * @apiParam {boolean}  	config.memberApply   	是否允许医生申请加入  true：允许，false：不允许
     * @apiParam {Integer}   	config.textParentProfit     图文资讯 上级抽成比例
     * @apiParam {Integer}   	config.textGroupProfit     图文资讯 集团抽成比例
     * @apiParam {Integer}   	config.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiParam {Integer}   	config.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiParam {Integer}   	config.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiParam {Integer}   	config.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiParam {Integer}   	config.clinicParentProfit     	 门诊 上级抽成比例
     * @apiParam {Integer}   	config.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiParam {Integer}   	config.consultationParentProfit     	 会诊 上级抽成比例
     * @apiParam {Integer}   	config.consultationGroupProfit     		会诊 集团抽成比例
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/regGroup")
    @Deprecated  //使用新接口groupApply,走集团申请流程
    public JSONMessage regGroup(Group group) throws HttpApiException {
        group.setCreator(ReqUtil.instance.getUserId());
        group.setCreatorDate(new Date().getTime());
        group.setUpdator(ReqUtil.instance.getUserId());
        group.setUpdatorDate(new Date().getTime());
        // 集团屏蔽 正常 add by tanyf 20160604
        group.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
        //注册集团
        Group retGroup = groupService.createGroup(group);
        //update by wangqiao

        //没有公司id，则为医生创建，需要将医生加入集团并设置为管理员
        if (StringUtil.isEmpty(group.getCompanyId())) {
            //医生加入集团
            groupFacadeService.saveCompleteGroupDoctor(retGroup.getId(), ReqUtil.instance.getUserId(), null, ReqUtil.instance.getUserId());
            //医生设置为管理员
            companyUserService.addGroupManage(retGroup.getId(), ReqUtil.instance.getUserId(), ReqUtil.instance.getUserId());
        }

        //add  by  liwei
        if (StringUtil.isNotEmpty(retGroup.getId())) {
            //创建集团的时候，需要将医生之前创建的文章挂在病例下面
            groupFacadeService.updateDoctorDiseaseBeforeCreateGroup(retGroup.getId(), ReqUtil.instance.getUserId());
        }
        return JSONMessage.success("success", retGroup);
    }


    /**
     * @api {post} /group/updateByGroup 修改集团
     * @apiVersion 1.0.0
     * @apiName updateByGroup
     * @apiGroup 集团模块
     * @apiDescription 修改医生集团信息
     * @apiParam {String}    	access_token                token
     * @apiParam {String}   	id           				集团Id
     * @apiParam {String}   	name       					集团名称
     * @apiParam {String}   	introduction       			集团简介
     * @apiParam {String}   	logoUrl       			    集团logo地址
     * @apiParam {String}   	standard					医生加入集团标准
     * @apiParam {Object[]}  	config    					集团设置信息
     * @apiParam {boolean}  	config.memberInvite    	是否允许成员邀请医生加入  true：允许，false：不允许
     * @apiParam {boolean}  	config.passByAudit   		成员邀请是否需要审核  true：允许，false：不允许
     * @apiParam {boolean}  	config.memberApply   	是否允许医生申请加入  true：允许，false：不允许
     * @apiParam {Integer}   	config.textParentProfit     图文资讯 上级抽成比例
     * @apiParam {Integer}   	config.textGroupProfit     图文资讯 集团抽成比例
     * @apiParam {Integer}   	config.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiParam {Integer}   	config.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiParam {Integer}   	config.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiParam {Integer}   	config.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiParam {Integer}   	config.clinicParentProfit     	 门诊 上级抽成比例
     * @apiParam {Integer}   	config.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiParam {Integer}   	config.consultationParentProfit     	 会诊 上级抽成比例
     * @apiParam {Integer}   	config.consultationGroupProfit     		会诊 集团抽成比例
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/updateByGroup")
    public JSONMessage updateByGroup(Group group) throws HttpApiException {
        group.setUpdator(ReqUtil.instance.getUserId());
        group.setUpdatorDate(new Date().getTime());
        return JSONMessage.success("success", groupService.updateGroup(group));
    }


    /**
     * @api {post} /group/getGroupById 获取集团详情
     * @apiVersion 1.0.0
     * @apiName getGroupById
     * @apiGroup 集团模块
     * @apiDescription 根据集团Id获取集团详情
     * @apiParam {String}    	access_token                token
     * @apiParam {String} 		id                  		集团Id
     * @apiSuccess {String}   	id           				记录Id
     * @apiSuccess {String}   	companyId           		所属公司Id
     * @apiSuccess {String}   	name       					集团名称
     * @apiSuccess {String}   	introduction       			集团简介
     * @apiSuccess {Integer}  	creator        				创建人
     * @apiSuccess {Long}  		creatorDate       			创建时间
     * @apiSuccess {Integer}  	updator        				更新人
     * @apiSuccess {Long}  		updatorDate       			更新时间
     * @apiSuccess {Long}  		logoUrl       				logo
     * @apiParam {String}   	standard					医生加入集团标准
     * @apiSuccess {GroupConfig}  	config    				集团设置信息
     * @apiSuccess {boolean}  	config.memberInvite    		是否允许成员邀请医生加入  true：允许，false：不允许
     * @apiSuccess {boolean}  	config.passByAudit   		成员邀请是否需要审核  true：允许，false：不允许
     * @apiSuccess {boolean}  	config.memberApply   	是否允许医生申请加入  true：允许，false：不允许
     * @apiSuccess {boolean}  	config.openAppointment   	是否开启名医面对面  true：开启，false：不开启
     * @apiSuccess {String}   	config.dutyStartTime     	值班的开始时间，如：08:00
     * @apiSuccess {String}   	config.dutyEndTime     		值班的结束时间，如：22:00
     * @apiSuccess {Integer}  	outpatientPrice        		值班价格
     * @apiSuccess {Integer}   	config.textParentProfit     图文资讯 上级抽成比例
     * @apiSuccess {Integer}   	config.textGroupProfit     图文资讯 集团抽成比例
     * @apiSuccess {Integer}   	config.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiSuccess {Integer}   	config.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiSuccess {Integer}   	config.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiSuccess {Integer}   	config.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiSuccess {Integer}   	config.clinicParentProfit     	 门诊 上级抽成比例
     * @apiSuccess {Integer}   	config.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiSuccess {Integer}   	config.consultationParentProfit     	 会诊 上级抽成比例
     * @apiSuccess {Integer}   	config.consultationGroupProfit     		会诊 集团抽成比例
     * @apiSuccess {Integer}   	config.appointmentGroupProfit     	 预约(名医面对面)集团抽成比例
     * @apiSuccess {Integer}   	config.appointmentParentProfit     	 预约(名医面对面)上级抽成比例
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/getGroupById")
    public JSONMessage getGroupById(String id) {
        return JSONMessage.success(null, groupService.getGroupById(id));
    }

    /**
     * @api {post} /group/searchByGroup 获取集团列表
     * @apiVersion 1.0.0
     * @apiName searchByGroup
     * @apiGroup 集团模块
     * @apiDescription 获取所有集团列表
     * @apiParam {String}    	access_token                token
     * @apiParam {String}   	companyId           		所属公司Id
     * @apiParam {String}   	name       					集团名称
     * @apiParam {String}   	introduction       			集团简介
     * @apiSuccess {String}  	id							集团Id
     * @apiSuccess {String}  	companyId					所属公司Id
     * @apiSuccess {String}  	name        				集团名称
     * @apiSuccess {String}  	introduction      			集团描述
     * @apiSuccess {Integer}  	creator        				创建人
     * @apiSuccess {Long}  		creatorDate       			创建时间
     * @apiSuccess {Integer}  	updator        				更新人
     * @apiSuccess {Long}  		updatorDate       			更新时间
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/searchByGroup")
    public JSONMessage searchByGroup(GroupParam group) {
        PageVO page = groupService.searchGroup(group);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {post} /group/deleteByGroup 删除集团
     * @apiVersion 1.0.0
     * @apiName deleteByGroup
     * @apiGroup 集团模块
     * @apiDescription 批量删除医生集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String[]}   	ids         	集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/deleteByGroup")
    @Deprecated //目前集团不允许删除
    public JSONMessage deleteByGroup(String[] ids) {
        groupService.deleteGroup(ids);
        return JSONMessage.success("success");
    }


    //----------------------------------------------------------------

    /**
     * @api {post} /group/groupApply 申请创建医生集团,再次创建医生集团
     * @apiVersion 1.0.0
     * @apiName groupApply
     * @apiGroup 集团模块
     * @apiDescription 申请创建医生集团, 再次创建医生集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	id         	            医生集团申请id(再次申请需要)
     * @apiParam {String}   	name         	名称
     * @apiParam {String}   	introduction    简介
     * @apiParam {String}   	logoUrl    		集团logoUrl
     * @apiParam {Integer}   	applyUserId    	申请者id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/groupApply")
    public JSONMessage groupApply(GroupApply groupApply) throws HttpApiException {
//		return JSONMessage.success(groupService.groupApply(groupApply));
        return JSONMessage.success(groupFacadeService.groupApply(groupApply));
    }


    /**
     * @api {post} /group/updateGroupApplyImageUrl 申请创建医生集团,再次创建医生集团
     * @apiVersion 1.0.0
     * @apiName groupApply
     * @apiGroup 集团模块
     * @apiDescription 申请创建医生集团, 再次创建医生集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	id         	           集团申请id
     * @apiParam {String}   	logoUrl    		集团logoUrl
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/updateGroupApplyImageUrl")
    public JSONMessage updateGroupApplyImageUrl(GroupApply groupApply) {
        groupService.updateGroupApplyImageUrl(groupApply);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/groupApplyInfo 查询集团最新的审核详情
     * @apiVersion 1.0.0
     * @apiName groupApplyInfo
     * @apiGroup 集团模块
     * @apiDescription 查询集团最新的审核详情（返回最新的一次审核记录）
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId    集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {String}   	name         	名称
     * @apiSuccess {String}   	introduction    简介
     * @apiSuccess {String}   	logoUrl    		集团logoUrl
     * @apiSuccess {String}   	auditDate    	审核日期
     * @apiSuccess {String}   	auditMsg    	审核信息
     * @apiSuccess {String}   	status    	    审核状态  (A=待审核，P=审核通过，NP=审核未通过)
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/groupApplyInfo")
    public JSONMessage groupApplyInfo(String groupId) {

        GroupApply ga = groupService.getLastGroupApply(groupId);

        return JSONMessage.success(ga);
    }

    /**
     * @api {post} /group/groupApplyHistoryInfo 查询集团审核历史信息
     * @apiVersion 1.0.0
     * @apiName groupApplyHistoryInfo
     * @apiGroup 集团模块
     * @apiDescription 查询集团审核历史信息（用于读取审核通知的内容）
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupApplyId    集团申请Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {String}   	name         	名称
     * @apiSuccess {String}   	introduction    简介
     * @apiSuccess {String}   	logoUrl    		集团logoUrl
     * @apiSuccess {String}   	auditDate    	审核日期
     * @apiSuccess {String}   	auditMsg    	审核信息
     * @apiSuccess {String}   	status    	    审核状态  (A=待审核，P=审核通过，NP=审核未通过)
     * @apiAuthor wangqiao
     * @date 2016年3月7日
     */
    @RequestMapping("/groupApplyHistoryInfo")
    public JSONMessage groupApplyHistoryInfo(String groupApplyId) {

        GroupApply ga = groupService.getGroupApplyInfo(groupApplyId, null);

        return JSONMessage.success(ga);
    }


    /**
     * @api {post} /group/processGroupApply 系统管理员审核集团
     * @apiVersion 1.0.0
     * @apiName processGroupApply
     * @apiGroup 集团模块
     * @apiDescription WEB 系统管理员审核集团(禁止APP调用)
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	id              集团申请记录Id (必传)
     * @apiParam {String}   	status          审核状态(P=审核通过，NP=审核未通过)(必传)
     * @apiParam {String}   	auditMsg    	审核留言
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/processGroupApply")
    public JSONMessage processGroupApply(GroupApply groupApply) throws HttpApiException {
        groupApply.setAuditUserId(ReqUtil.instance.getUserId());
//		groupService.processGroupApply(groupApply);

        groupService.processGroupApplyNew(groupApply);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/applyList 医生集团申请审核列表
     * @apiVersion 1.0.0
     * @apiName applyList
     * @apiGroup 集团模块
     * @apiDescription 医生集团申请审核列表
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	status    	            集团状态（A=待审核，P=审核通过，NP=审核未通过）
     * @apiParam {String}   	groupActive     集团激活状态（active=已激活 ,inactive=未激活,不填或为空 = 查询全部）
     * @apiParam {String}   	skip     		 屏蔽状态  （"N", "正常" "S", "屏蔽",不填或为空 = 查询全部）
     * @apiParam {String}   	keyword    	            关键词（集团名称，医生名称，医院名称，联系电话）
     * @apiParam {Integer}   	pageSize        页记录数
     * @apiParam {Integer}   	pageIndex       页码数
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {Integer} 	pageCount    	总页数
     * @apiSuccess {Long} 	    total    	            总记录数
     * @apiSuccess {Object[]} 	pageData    	 数据对象列表
     * @apiSuccess {String} 	pageData.groupApplyId    	             集团申请记录Id
     * @apiSuccess {String} 	pageData.groupName    	 集团名称
     * @apiSuccess {String} 	pageData.doctorName 集团管理员名称
     * @apiSuccess {String} 	pageData.title      职称
     * @apiSuccess {String} 	pageData.hospitalName   医院名称
     * @apiSuccess {String} 	pageData.level      医院等级
     * @apiSuccess {String} 	pageData.telephone  联系电话
     * @apiSuccess {String} 	pageData.applyDate  提交时间
     * @apiSuccess {String} 	pageData.auditDate  审核时间
     * @apiSuccess {String} 	pageData.applyDoctorName  申请人姓名
     * @apiSuccess {String} 	pageData.adminName  审核人姓名
     * @apiSuccess {String} 	pageData.memberNum  集团成员数
     * @apiSuccess {String} 	pageData.groupActive  集团激活状态
     * @apiSuccess {String} 	pageData.skip  		 屏蔽状态  （"N", "正常" "S", "屏蔽"）
     * @apiAuthor wangl、谭永芳
     * @date 2016年06月02日
     */
    @RequestMapping("/applyList")
    public JSONMessage applyList(String status,
                                 String groupActive,
                                 String skip,
                                 /*String groupName,
                                 String doctorName,
								 String hospitalName,
								 String telephone*/
                                 String keyword,
                                 Integer pageSize,
                                 Integer pageIndex) {
        PageVO pageVo = groupService.getApplyList(status, groupActive, skip, keyword, keyword, keyword, keyword, pageSize, pageIndex);
        return JSONMessage.success(pageVo);
    }

    /**
     * @api {get} /group/findGroupByKeyword searh group by keyword and group apply status
     * @apiVersion 1.0.0
     * @apiName findGroupWithKeyWord
     * @apiGroup 医生集团搜索
     * @apiDescription searh group by keyword and group apply status
     * @apiParam {String}     access_token                token
     * @apiParam {String}     keyword                     关键字
     * @apiParam {String}     applyStatus                 集团审核状态(不传默认为P)
     * @apiParam {Integer}    pageIndex                   查询页，从0开始
     * @apiParam {Integer}    pageSize                    每页显示条数，不传默认15条
     * @apiSuccess {String}    	id                     		集团id
     * @apiSuccess {String}     name                   		集团名称
     * @apiAuthor 傅永德
     * @date 2016年5月19日
     */
    @RequestMapping("/findGroupByKeyword")
    public JSONMessage findGroupByKeyword(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "applyStatus", defaultValue = "P") String applyStatus,
            @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageVO pageVo = groupService.findGroupByKeyword(keyword, applyStatus, pageIndex, pageSize);
        return JSONMessage.success(pageVo);
    }

    /**
     * @api {post} /group/applyDetail 集团申请详情
     * @apiVersion 1.0.0
     * @apiName applyDetail
     * @apiGroup 集团模块
     * @apiDescription 集团申请详情
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupApplyId    集团申请记录Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {String} 	groupId    	            集团Id
     * @apiSuccess {String} 	groupName    	集团名称
     * @apiSuccess {String} 	doctorName   	管理员姓名
     * @apiSuccess {Integer}	doctorId		管理员id
     * @apiSuccess {String} 	telephone    	管理员电话
     * @apiSuccess {String} 	hospitalName    医院名称
     * @apiSuccess {String} 	level           医院等级
     * @apiSuccess {String} 	departments     科室
     * @apiSuccess {String} 	title			职称
     * @apiSuccess {String} 	licenseNum		证书编号
     * @apiSuccess {String} 	licenseExpire	过期时间
     * @apiSuccess {String}     QRUrl			二维码图片地址
     * @apiSuccess {String} 	applyStatus		审核状态（A=待审核，P=审核通过，NP=审核未通过）
     * @apiSuccess {String} 	adminName   	审核人员名称
     * @apiSuccess {String} 	auditMsg		审核意见
     * @apiSuccess {String[]} 	imageUrls		认证图片
     * @apiSuccess {String} 	memberNum  集团成员数
     * @apiSuccess {String} 	groupActive		集团激活状态（active 已激活 ,inactive 未激活）
     * @apiSuccess {String} 	skip		屏蔽状态  （"N", "正常" "S", "屏蔽"）
     * @apiSuccess {String}    auditDate       审核时间
     * @apiSuccess {Integer}    userStatus 	(1, "正常"),(2, "待审核"),(3, "审核未通过"),(4, "暂时禁用"),(5, "永久禁用"),(6, "未激活"),(7, "未认证"),(8,"离职"), (9,"注销");
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/applyDetail")
    public JSONMessage applyDetail(String groupApplyId) {
        GroupApplyDetailPageVo vo = groupService.getApplyDetail(groupApplyId);
        GroupApply ga = groupService.getGroupApplyInfo(groupApplyId, null);
        List<Map<String, Object>> result = imageDataService.findDoctorImgData(ImageDataEnum.doctorCheckImage.getIndex(), ga.getApplyUserId());
        if (result != null && result.size() > 0) {
            List<String> images = new ArrayList<String>();
            for (Map<String, Object> map : result) {
                images.add(map.get("url") + "");
            }
            vo.setImageUrls(images);
        }
        if (StringUtil.isEmpty(vo.getLogoUrl())) {
            vo.setLogoUrl(ga.getLogoUrl());
        }
        vo.setIntroduction(ga.getIntroduction());
        return JSONMessage.success(vo);
    }

    /**
     * @api {post} /group/applyTransfer 申请医生集团转让
     * @apiVersion 1.0.0
     * @apiName applyTransfer
     * @apiGroup 集团模块
     * @apiDescription 申请医生集团转让
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId    集团id
     * @apiParam {String}   	inviteUserId    申请转让用户Id
     * @apiParam {String}   	confirmUserId   接受用户Id
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/applyTransfer")
    public JSONMessage applyTransfer(GroupUserApply groupUserApply) throws HttpApiException {
        groupService.applyTransfer(groupUserApply);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/confirmTransfer 医生集团转让确认
     * @apiVersion 1.0.0
     * @apiName confirmTransfer
     * @apiGroup 集团模块
     * @apiDescription 医生集团转让确认
     * @apiParam {String}    	access_token    token
     * @apiParam {String}    	groupUserApplyId 医生集团转让记录Id
     * @apiParam {String}   	status   		   转让状态（A=待确认，P=确认通过，NP=确认未通过，E=已过期）
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/confirmTransfer")
    public JSONMessage confirmTransfer(@RequestParam(required = true) String groupUserApplyId,
                                       @RequestParam(required = true) String status) throws HttpApiException {
        groupService.confirmTransfer(groupUserApplyId, status);
        return JSONMessage.success();
    }


    /**
     * @api {post} /group/getTransferInfo 确认获取转让信息详情
     * @apiVersion 1.0.0
     * @apiName getTransferInfo
     * @apiGroup 集团模块
     * @apiDescription 确认获取转让信息详情
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupUserApplyId 申请转让记录Id
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiSuccess {String} 	groupName    	集团名称
     * @apiSuccess {String} 	inviteUserName  转让者名称
     * @apiSuccess {Long} 	    inviteDate      转让时间
     * @apiSuccess {Long} 	    confirmDate     确认时间
     * @apiSuccess {String} 	status          状态（A=待确认，P=确认通过，NP=确认未通过，E=已过期）
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/getTransferInfo")
    public JSONMessage getTransferInfo(String groupUserApplyId) {
        return JSONMessage.success(groupService.getTransferInfo(groupUserApplyId));
    }


    /**
     * @api {post} /group/updateGroupProfit 修改集团分成
     * @apiVersion 1.0.0
     * @apiName updateGroupProfit
     * @apiGroup 集团模块
     * @apiDescription 修改集团分成
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	id		 集团id
     * @apiParam {String}   	config.textGroupProfit  图文咨询 集团抽成比例
     * @apiParam {String}   	config.textParentProfit  图文咨询 上级抽成比例
     * @apiParam {String}   	config.phoneGroupProfit  电话咨询 集团抽成比例
     * @apiParam {String}   	config.phoneParentProfit  电话咨询 上级抽成比例
     * @apiParam {String}   	config.carePlanGroupProfit  关怀计划 集团抽成比例
     * @apiParam {String}   	config.carePlanParentProfit 关怀计划 上级抽成比例
     * @apiParam {String}   	config.clinicGroupProfit  门诊 集团抽成比例
     * @apiParam {String}   	config.clinicParentProfit  门诊 上级抽成比例
     * @apiParam {Integer}   	config.consultationParentProfit     	 会诊 上级抽成比例
     * @apiParam {Integer}   	config.consultationGroupProfit     		会诊 集团抽成比例
     * @apiParam {Integer}     config.appointmentGroupProfit      预约集团抽成
     * @apiParam {Integer}     config.appointmentParentProfit     预约上级抽成
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/updateGroupProfit")
    public JSONMessage updateGroupProfit(Group group) {
        groupService.updateGroupProfit(group);
        return JSONMessage.success();
    }


    /**
     * @api {post} /group/hasCreateRole 是否能申请创建集团
     * @apiVersion 1.0.0
     * @apiName hasCreateRole
     * @apiGroup 集团模块
     * @apiDescription 是否能申请创建集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	doctorId		 医生id
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiSuccess {boolean} 	data    		true:有，false：没有
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/hasCreateRole")
    public JSONMessage hasCreateRole(@RequestParam(required = true) Integer doctorId) {
        return JSONMessage.success(groupService.hasCreateRole(doctorId));
    }


    /**
     * @api {post} /group/getGroupStatusInfo 查询集团的申请 和 激活状态
     * @apiVersion 1.0.0
     * @apiName getGroupStatusInfo
     * @apiGroup 集团模块
     * @apiDescription 查询集团的申请 和 激活状态
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId		 	集团id
     * @apiSuccess {String} 	resultCode    	返回状态码
     * @apiSuccess {String} 	active    	(active=已激活，inactive=未激活)
     * @apiSuccess {String} 	applyStatus  (A=待审核，P=审核通过，NP=审核未通过)
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("/getGroupStatusInfo")
    public JSONMessage getGroupStatusInfo(@RequestParam(required = true) String groupId) {
        return JSONMessage.success(groupService.getGroupStatusInfo(groupId));
    }

    /*---------------------2016年4月26日18:06:57-----------------------*/
    @RequestMapping("/updateAppoientInfo")
    public JSONMessage updateAppoientInfo() {
        //return JSONMessage.success(groupService.getGroupStatusInfo(groupId));
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/activeGroup 医生集团审核设置集团激活状态为已激活状态
     * @apiVersion 1.0.0
     * @apiName activeGroup
     * @apiGroup 集团模块
     * @apiDescription 设置集团激活状态为已激活状态
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupApplyId	集团申请记录Id（查询集团申请记录明细）
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {String} 	groupId    	            集团Id
     * @apiSuccess {String} 	groupName    	集团名称
     * @apiSuccess {String} 	doctorName   	管理员姓名
     * @apiSuccess {String} 	telephone    	管理员电话
     * @apiSuccess {String} 	hospitalName    医院名称
     * @apiSuccess {String} 	level           医院等级
     * @apiSuccess {String} 	departments     科室
     * @apiSuccess {String} 	title			职称
     * @apiSuccess {String} 	licenseNum		证书编号
     * @apiSuccess {String} 	licenseExpire	过期时间
     * @apiSuccess {String} 	applyStatus		审核状态（A=待审核，P=审核通过，NP=审核未通过）
     * @apiSuccess {String} 	adminName   	审核人员名称
     * @apiSuccess {String} 	auditMsg		审核意见
     * @apiSuccess {String[]} 	imageUrls		认证图片
     * @apiSuccess {String} 	groupActive		集团激活状态（active 已激活 ,inactive 未激活）
     * @apiAuthor tanyf
     * @date 2016年6月2日
     */
    @RequestMapping("/activeGroup")
    public JSONMessage activeGroup(@RequestParam(required = true) String groupApplyId) throws HttpApiException {
        if (StringUtil.isEmpty(groupApplyId)) {
            throw new ServiceException("集团审核申请id不能为空");
        }
        GroupApply groupApply = groupService.getGroupApplyById(groupApplyId);
        if (groupApply.getStatus() == null || !groupApply.getStatus().equals(GroupEnum.GroupApplyStatus.pass.getIndex())) {
            throw new ServiceException("圈子审核状态未通过不允许激活圈子");
        }
        String groupId = groupApply.getGroupId();
        groupService.activeGroup(groupId);
        return this.applyDetail(groupApplyId);
    }

    /**
     * @api {post} /group/blockGroup 屏蔽集团
     * @apiVersion 1.0.0
     * @apiName blockGroup
     * @apiGroup 集团模块
     * @apiDescription 屏蔽集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor tanyf
     * @date 2016年6月6日
     */
    @RequestMapping("/blockGroup")
    public JSONMessage blockGroup(@RequestParam(required = true) String groupId) {
        groupService.blockGroup(groupId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/unBlockGroup 取消屏蔽集团
     * @apiVersion 1.0.0
     * @apiName unBlockGroup
     * @apiGroup 集团模块
     * @apiDescription 取消屏蔽集团
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiAuthor tanyf
     * @date 2016年6月6日
     */
    @RequestMapping("/unBlockGroup")
    public JSONMessage unBlockGroup(@RequestParam(required = true) String groupId) {
        groupService.unBlockGroup(groupId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/getGroupRegion 获取集团医生的省市区树
     * @apiVersion 1.0.0
     * @apiName getGroupRegion
     * @apiGroup 集团模块
     * @apiDescription 获取集团医生的省市区树
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {String} 	id    	地区的id
     * @apiSuccess {String}     name	地区的名称
     * @apiSuccess {String}     parentId	地区的父级id
     * @apiSuccess {List}     subList	地区的叶子节点
     * @apiAuthor fuyongde
     * @date 2016年7月18日
     */
    @RequestMapping("/getGroupRegion")
    public JSONMessage getGroupRegion(String groupId) {
        List<RegionVo> regionVos = groupService.getGroupRegion(groupId);
        return JSONMessage.success(regionVos);
    }

    /**
     * @api {post} /group/getGroupExistRegion 获取集团医生的省市区树
     * @apiVersion 1.0.0
     * @apiName getGroupExistRegion
     * @apiGroup 集团模块
     * @apiDescription 获取集团医生的省市区树
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {Integer} 	code    	地区的id
     * @apiSuccess {String}     name	地区的名称
     * @apiSuccess {Integer}     parentId	地区的父级id
     * @apiSuccess {List}     children	地区的叶子节点
     * @apiAuthor fuyongde
     * @date 2016年7月18日
     */
    @RequestMapping("/getGroupExistRegion")
    public JSONMessage getGroupExistRegion(String groupId) {
        List<ExistRegionVo> regionVos = groupService.getGroupExistRegion(groupId);
        return JSONMessage.success(regionVos);
    }

    /**
     * @api {post} /group/getDepartments 获取存在医生的科室
     * @apiVersion 1.0.0
     * @apiName getDepartments
     * @apiGroup 集团模块
     * @apiDescription 获取存在医生的科室
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {Object[]} 	DepartmentVO    	 科室对象
     * @apiSuccess {String} 	DepartmentVO.id    	             科室Id
     * @apiSuccess {String} 	DepartmentVO.name    	科室名称
     * @apiSuccess {String} 	DepartmentVO.parentId	科室父节点
     * @apiSuccess {String} 	DepartmentVO.isLeaf      科室是否为叶子节点
     * @apiSuccess {String} 	DepartmentVO.enableStatus   科室是否启用
     * @apiSuccess {String} 	DepartmentVO.subList      科室的子科室
     * @apiSuccess {String} 	DepartmentVO.weight  科室权重
     * @apiAuthor liangs
     * @date 2016年7月18日
     */
    @RequestMapping("/getDepartments")
    public JSONMessage getDepartments(@RequestParam(required = true) String groupId) {
        return JSONMessage.success(groupService.getDeptsOfDoctors(groupId));
    }


    /**
     * @api {post} /group/getServiceType 获取服务类型
     * @apiVersion 1.0.0
     * @apiName getServiceType
     * @apiGroup 集团模块
     * @apiDescription 获取服务类型
     * @apiParam {String}    	access_token    token
     * @apiParam {String}   	groupId	集团Id
     * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {Object[]} 	ServiceTypeVO    	 服务类型对象
     * @apiSuccess {String} 	ServiceTypeVO.id    	        服务类型Id
     * @apiSuccess {String} 	ServiceTypeVO.name    	服务类型名称
     * @apiSuccess {String} 	ServiceTypeVO.parentId	服务类型父节点
     * @apiSuccess {String} 	ServiceTypeVO.subList      服务类型的子科室
     * @apiAuthor liangcs
     * @date 2016年7月18日
     */
    @RequestMapping("/getServiceType")
    public JSONMessage getServiceType(@RequestParam(required = true) String groupId) {

        List<Integer> docIds = groupService.getDoctorIdsByGroupId(groupId);

        if (docIds == null || docIds.size() == 0) {
            return JSONMessage.success();
        }

        List<Pack> packs = packService.queryByUserIds(docIds);

        List<ServiceTypeVO> result = Lists.newArrayList();
        Map<Integer, String> map = Maps.newHashMap();

        if (packs != null && packs.size() > 0) {
            for (Pack pack2 : packs) {
                if (pack2.getPackType() == 1) {
                    map.put(1, "图文咨询");
                } else if (pack2.getPackType() == 2) {
                    map.put(2, "电话咨询");
                } else if (pack2.getPackType() == 3 && pack2.getGroupId().equals(groupId)) {
                    map.put(3, "健康关怀");
                }
            }
        }

        for (Integer key : map.keySet()) {
            ServiceTypeVO vo = new ServiceTypeVO();
            vo.setId(key);
            vo.setName(map.get(key));
            result.add(vo);
        }

        return JSONMessage.success(result);
    }


    /**
     * @api {post} /group/getGroupRecommendedList 获取被推荐的集团列表
     * @apiVersion 1.0.0
     * @apiName getGroupRecommendedList
     * @apiGroup 集团设置
     * @apiDescription 获取被推荐的集团列表
     * @apiParam {String} access_token    token
     * @apiParam {Integer}		pageIndex		页码
     * @apiParam {Integer}		pageSize		页容量
     * @apiSuccess {Object[]}		group					集团列表
     * @apiSuccess {String}		group.logoUrl			集团图标
     * @apiSuccess {String}		group.id				集团Id
     * @apiSuccess {String}		group.name				集团名称
     * @apiSuccess {String}		group.manager			集团管理者
     * @apiSuccess {String}		group.managerTitle		管理者职称
     * @apiSuccess {String}		group.memberNumber		集团成员数
     * @apiSuccess {String}		group.groupCureNum 集团就诊量
     * @apiSuccess {String}		gruop.introduction		集团介绍
     * @apiSuccess {String}		gruop.skill				集团擅长
     * @apiSuccess {String}		group.certStatus		加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("getGroupRecommendedList")
    public JSONMessage getGroupRecommendedList(@RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                               @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return JSONMessage.success(groupService.getGroupRecommendedList(pageIndex, pageSize));
    }

    @RequestMapping("getRecommends")
    public JSONMessage getRecommands(@RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                     @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return JSONMessage.success(groupService.getRecommends(pageIndex, pageSize));
    }

    /**
     * @api {post} /group/setGroupToRecommended 设置集团为推荐状态
     * @apiVersion 1.0.0
     * @apiName setGroupToRecommended
     * @apiGroup 集团设置
     * @apiDescription 设置集团为推荐状态
     * @apiParam {String} access_token    token
     * @apiParam {String[]}		groupId			集团id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("setGroupToRecommended")
    public JSONMessage setGroupToRecommended(String[] groupId) {
        groupService.setGroupToRecommended(groupId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/removeGroupRecommended 移除集团推荐
     * @apiVersion 1.0.0
     * @apiName removeGroupRecommended
     * @apiGroup 集团设置
     * @apiDescription 移除集团推荐
     * @apiParam {String} access_token    token
     * @apiParam {Integer}		groupId			集团id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("removeGroupRecommended")
    public JSONMessage removeGroupRecommended(String groupId) {
        groupService.removeGroupRecommended(groupId);
        return JSONMessage.success();
    }


    /**
     * @api {post} /group/riseRecommendedOfGroup 上移集团推荐排名
     * @apiVersion 1.0.0
     * @apiName riseRecommendedOfGroup
     * @apiGroup 集团设置
     * @apiDescription 上移集团推荐排名
     * @apiParam {String} access_token    token
     * @apiParam {String} groupId			集团id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("riseRecommendedOfGroup")
    public JSONMessage riseRecommendedOfGroup(String groupId) {
        groupService.upWeight(groupId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/searchGroupByName 通过集团名称查找集团
     * @apiVersion 1.0.0
     * @apiName searchGroupByName
     * @apiGroup 集团设置
     * @apiDescription 通过集团名称查找集团
     * @apiParam {String} access_token    token
     * @apiParam {String} name			集团名称
     * @apiParam {Integer}		pageIndex		页码
     * @apiParam {Integer}		pageSize		页记录数
     * @apiSuccess {Object[]}		group		集团列表
     * @apiSuccess {String}		group.headPicFileName	集团图标
     * @apiSuccess {String}		group.id				集团Id
     * @apiSuccess {String}		group.name				集团名称
     * @apiSuccess {String}		group.manager			集团管理者
     * @apiSuccess {String}		group.managerTitle		管理者职称
     * @apiSuccess {String}		group.memberNumber		集团成员数
     * @apiSuccess {Integer}		group.setIsSelect		集团是否被推荐（1未已推荐，0为未推荐）
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("searchGroupByName")
    public JSONMessage searchGroupByName(String name, Integer pageIndex, Integer pageSize) {
        return JSONMessage.success(groupService.searchGroupByName(name, pageIndex, pageSize));
    }

    /**
     * @api {post} /group/groupInfo 集团所有者与集团管理员信息
     * @apiVersion 1.0.0
     * @apiName groupInfo
     * @apiGroup 集团设置
     * @apiDescription 集团所有者与集团管理员信息
     * @apiParam {String} access_token    token
     * @apiParam {String} groupId			集团id
     * @apiSuccess {String}		count					集团人数
     * @apiSuccess {Object[]}		roots					集团负责人
     * @apiSuccess {String}		roots.id				集团负责人id
     * @apiSuccess {String}		roots.name				集团负责人姓名
     * @apiSuccess {String}		roots.headPicFileName	集团负责人头像
     * @apiSuccess {Object[]}		admins					集团管理员
     * @apiSuccess {String}		admins.id				集团负责人id
     * @apiSuccess {String}		admins.name				集团负责人姓名
     * @apiSuccess {String}		admins.headPicFileName	集团负责人头像
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("groupInfo")
    public JSONMessage groupInfo(@RequestParam(name = "groupId", required = true) String groupId) {
        return JSONMessage.success(groupService.groupInfo(groupId));
    }


    /**
     * @api {post} /group/searchByKeyword 根据名称搜索医生或者集团
     * @apiVersion 1.0.0
     * @apiName searchByKeyword
     * @apiGroup 集团设置
     * @apiDescription 根据名称搜索医生或者集团
     * @apiParam {String} access_token    token
     * @apiParam {Integer} type			搜索类型1：集团 2：医生
     * @apiSuccess {Map}		data				结果集
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年8月25日
     */
    @RequestMapping("searchByKeyword")
    public JSONMessage searchByKeyword(Integer type, String keyword) {
        return JSONMessage.success(groupService.searchBykeyword(type, keyword));
    }

    /**
     * @api {post} /group/changeAdmin 平台变更集团创建人
     * @apiVersion 1.0.0
     * @apiName changeAdmin
     * @apiGroup 集团设置
     * @apiDescription 平台变更集团创建人
     * @apiParam {String} access_token    token
     * @apiParam {String} groupId			集团id
     * @apiParam {Integer}      receiverId      接受者id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年11月30日
     */
    @RequestMapping("changeAdmin")
    public JSONMessage changeAdmin(String groupId, Integer receiverId) throws HttpApiException {
        groupService.changAdmin(groupId, receiverId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/initAllGroupIM 初始化集团会话组
     * @apiVersion 1.0.0
     * @apiName initAllGroupIM
     * @apiGroup 集团设置
     * @apiDescription 初始化集团会话组
     * @apiParam {String} access_token    token
     * @apiParam {String} groupId			集团id
     * @apiAuthor 傅永德
     * @date 2017年3月20日
     */
    @RequestMapping("/initAllGroupIM")
    public JSONMessage initAllGroupIM() {
        groupService.initAllGroupIM();
        return JSONMessage.success();
    }

    @RequestMapping("/fixAllGroupIM")
    public JSONMessage fixAllGroupIM() {
        groupService.fixAllGroupIM();
        return JSONMessage.success();
    }

    @RequestMapping("/getIMInfo")
    public JSONMessage getIMInfo(String gid) throws HttpApiException {
        return JSONMessage.success(groupService.getIMInfo(gid));
    }

    /**
     * @api {post} /group/findAllGroupExDept 获取所有圈子（除了科室类型的）
     * @apiVersion 1.0.0
     * @apiName groupInfo
     * @apiGroup 广告文章范围筛选
     * @apiDescription 广告文章范围筛选-圈子范围
     * @apiParam {String} access_token    token
     * @apiParam {string} name 圈子名称
     * @apiParam {Integer} pageIndex		页数
     * @apiParam {Integer} pageSize			每页条数
     * @apiSuccess {String}		page.group.id				集团Id
     * @apiSuccess {String}		page.group.name				集团名称
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor limin
     * @date 2017年6月14日15:36:59
     */
    @RequestMapping("/findAllGroupExDept")
    public JSONMessage findAllGroupExDept(String name,@RequestParam(defaultValue = "0") Integer pageIndex,@RequestParam(defaultValue = "15") Integer pageSize) {
        PageVO allGroupExDept = groupService.findAllGroupExDept(name,pageIndex, pageSize);
        return JSONMessage.success(allGroupExDept);
    }
}

