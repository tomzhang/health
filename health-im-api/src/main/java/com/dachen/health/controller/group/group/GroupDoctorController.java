package com.dachen.health.controller.group.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.common.service.ICommonService;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.entity.param.GroupDoctorsParam;
import com.dachen.health.group.group.dao.IGroupUserDao;
import com.dachen.health.group.group.entity.param.GroupDoctorApplyParam;
import com.dachen.health.group.group.entity.param.GroupDoctorBatchParam;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.GroupDoctorApplyVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;


/**
 * @author pijingwei
 * @date 2015/8/11
 */
@RestController
@RequestMapping("/group/doctor")
public class GroupDoctorController extends AbstractController {

    @Autowired
    private IGroupDoctorService gdocService;

    @Autowired
    private IPlatformDoctorService pdocService;

    @Autowired
    private IGroupFacadeService groupFacadeService;

    @Autowired
    private ICompanyUserService companyUserService;

    @Autowired
    private UserManager userManager;

    @Resource
    private IBaseUserService baseUserService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IPackService packService;

    @Autowired
    private IGroupUserDao groupUserDao;


    /**
     * @api {post} /group/doctor/saveByGroupDoctor 邀请加入医生集团
     * @apiVersion 1.0.0
     * @apiName saveByGroupDoctor
     * @apiGroup 集团医生
     * @apiDescription 邀请加入医生集团
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		所属集团Id
     * @apiParam {String}   		doctorId           		医生Id
     * @apiParam {String}   		telephone           	手机号码（如果找不到用户，就把手机号码传过来）
     * @apiParam {String}   		againInvite           	再次邀请参数，如果是再次邀请传：1
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/saveByGroupDoctor")
    public JSONMessage saveByGroupDoctor(GroupDoctor gdoc, Integer againInvite, String telephone) throws HttpApiException {
        gdoc.setStatus(GroupEnum.GroupDoctorStatus.邀请待确认.getIndex());
        gdoc.setReferenceId(ReqUtil.instance.getUserId());
        gdoc.setCreator(ReqUtil.instance.getUserId());
        gdoc.setCreatorDate(new Date().getTime());
        gdoc.setUpdator(ReqUtil.instance.getUserId());
        gdoc.setUpdatorDate(new Date().getTime());
        return JSONMessage.success(null, gdocService.saveGroupDoctor(gdoc, againInvite, telephone));
    }


    /**
     * @api {post} /group/doctor/saveBatchInvite 批量邀请加入医生集团
     * @apiVersion 1.0.0
     * @apiName saveBatchInvite
     * @apiGroup 集团医生
     * @apiDescription 批量邀请加入医生集团
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		所属集团Id
     * @apiParam {String[]}   		telepNumsOrdocNums      医生号或手机号
     * @apiParam {String}   		ignore           		ignore（true ：忽略错误发送，false：不忽略错误）
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor 张垠
     * @date 2015年10月15日
     */
    @RequestMapping("/saveBatchInvite")
    public JSONMessage saveBatchInvite(String groupId, String[] telepNumsOrdocNums, String ignore) throws HttpApiException {
        //校验 登录用户是否有 邀请的权限（即是否集团管理员）
        Integer loginUserId = ReqUtil.instance.getUserId();
        List<Integer> adminDoctorIdList = companyUserService.getGroupAdminByGroupId(groupId);

        //审核平台的管理员也能调用发送邀请的接口（2016-05-17傅永德）
        List<User> checkAdminUsers = userManager.getNormalUser(UserEnum.UserType.customerService.getIndex());
        if (checkAdminUsers != null && checkAdminUsers.size() > 0) {
            for (User user : checkAdminUsers) {
                adminDoctorIdList.add(user.getUserId());
            }
        }

        if (adminDoctorIdList == null || adminDoctorIdList.size() == 0 || !adminDoctorIdList.contains(loginUserId)) {
            throw new ServiceException("您没有邀请权限");
        }

        Map retMap = gdocService.saveBatchGroupDoctor(groupId, ignore, telepNumsOrdocNums);

        //申请加入的医生，直接审核通过 FIXME 这段代码后面需要移到GroupFacadeService中
        if (retMap != null && retMap.get("applyJoinList") != null) {
            List<Map<String, GroupDoctor>> applyJoinList = (List<Map<String, GroupDoctor>>) retMap.get("applyJoinList");
            if (applyJoinList != null && applyJoinList.size() > 0) {
                for (Map<String, GroupDoctor> map : applyJoinList) {
                    if (map != null) {
                        Map<String, String> data = new HashMap<String, String>();
                        Iterator<Entry<String, GroupDoctor>> it = map.entrySet().iterator();
                        Entry<String, GroupDoctor> entry = it.hasNext() ? it.next() : null;
                        if (entry == null) {
                            continue;
                        }
                        String key = entry.getKey();
                        GroupDoctor gd = entry.getValue();
                        //直接审核通过该医生的申请
                        groupFacadeService.confirmByDoctorApply(gd, true);
                        data.put("msg", "您邀请的医生已经申请加入该圈子，系统已接受申请，您可以在“未分配”中对该医生进行操作");
                        retMap.put(key, data);
                    }
                }
            }
        }
        retMap.remove("applyJoinList");

        return JSONMessage.success(null, retMap);
    }


    /**
     * @api {post} /group/doctor/saveByApply 申请加入医生集团
     * @apiVersion 1.0.0
     * @apiName saveByApply
     * @apiGroup 集团医生
     * @apiDescription 申请加入医生集团
     *
     * @apiParam  {String}          access_token            token
     * @apiParam  {String}          groupId                 所属集团Id
     *
     * @apiSuccess {Number}         resultCode              返回状态码
     * @apiSuccess {String}         msg                     提示信息
     *
     * @apiAuthor 范鹏
     * @date 2015年9月16日
     */
//	@RequestMapping("/saveByApply")
//    public JSONMessage saveByApply(GroupDoctor gdoc) {
//        gdoc.setStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
//        gdoc.setDoctorId(ReqUtil.instance.getUserId());
//        gdoc.setReferenceId(0);
//        gdoc.setCreator(gdoc.getDoctorId());
//        gdoc.setCreatorDate(new Date().getTime());
//        gdoc.setUpdator(gdoc.getDoctorId());
//        gdoc.setUpdatorDate(gdoc.getCreatorDate());
//        return JSONMessage.success(null, gdocService.saveByApply(gdoc));
//    }


    /**
     * @api {post} /group/doctor/saveCompleteByGroupDoctor 医生加入集团直接通道
     * @apiVersion 1.0.0
     * @apiName saveCompleteByGroupDoctor
     * @apiGroup 集团医生
     * @apiDescription 医生加入集团直接通道
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		所属集团Id
     * @apiParam {Integer}   		doctorId           		医生Id
     * @apiParam {String}   		inviteId           		邀请人Id
     * @apiParam {String}   		telephone           	手机号码
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/saveCompleteByGroupDoctor")
    public JSONMessage saveCompleteByGroupDoctor(String groupId, Integer doctorId, String telephone, Integer inviteId) throws HttpApiException {
//		gdoc.setCreator(inviteId);
//		gdoc.setCreatorDate(new Date().getTime());
//		gdoc.setUpdator(inviteId);
//		gdoc.setUpdatorDate(new Date().getTime());
//		gdoc.setReferenceId(inviteId);
        return JSONMessage.success(null, groupFacadeService.saveCompleteGroupDoctor(groupId, doctorId, telephone, inviteId));
    }

    /**
     * @api {post} /group/doctor/dimission 离职
     * @apiVersion 1.0.0
     * @apiName dimission
     * @apiGroup 集团医生
     * @apiDescription 离职
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		集团Id
     * @apiParam {Integer}   		doctorId           		医生Id
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/dimission")
    public JSONMessage dimission(String groupId, Integer doctorId) throws HttpApiException {
//		gdocService.dimissionByCorrelation(gdoc);
        //更新groupDoctor 移到facadeService add by wangqiao

        groupFacadeService.updateGroupDoctorStatus(groupId, doctorId, GroupDoctorStatus.离职.getIndex());//离职
        return JSONMessage.success("success");
    }

    /**
     * @api {post} /group/doctor/quitGroup 医生自己离开集团
     * @apiVersion 1.0.0
     * @apiName quitGroup
     * @apiGroup 集团医生
     * @apiDescription 医生自己离开集团
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		集团Id
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor wangqiao
     * @date 2016年1月16日
     */
    @RequestMapping("/quitGroup")
    public JSONMessage quitGroup(String groupId) throws HttpApiException {
        Integer doctorId = ReqUtil.instance.getUserId();

        groupFacadeService.updateGroupDoctorStatus(groupId, doctorId, GroupDoctorStatus.离职.getIndex());//离职
        return JSONMessage.success("success");
    }

    /**
     * @api {post} /group/doctor/removeDoctors 运营平台将医生移除集团
     * @apiVersion 1.0.0
     * @apiName removeDoctors
     * @apiGroup 集团医生
     * @apiDescription 运营平台将医生移除集团
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		集团Id
     * @apiParam {Integer[]}   		doctorIds           	移除的医生id
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor 傅永德
     * @date 2016年10月24日
     */
    @RequestMapping("/removeDoctors")
    public JSONMessage removeDoctors(String groupId, Integer[] doctorIds) throws HttpApiException {
        groupFacadeService.removeDoctors(groupId, doctorIds);
        return JSONMessage.success("success");
    }

    /**
     * @api {post} /group/doctor/updateByGroupDoctor 修改集团医生信息
     * @apiVersion 1.0.0
     * @apiName updateByGroupDoctor
     * @apiGroup 集团医生
     * @apiDescription 修改集团医生信息
     * @apiParam {String}    		access_token            token
     * @apiParam {String}   		id           		            记录Id（集团医生Id）
     * @apiParam {String}   		contactWay              联系方式
     * @apiParam {String}   		remarks       		            备注
     * @apiSuccess {Number} resultCode    返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/updateByGroupDoctor")
    public JSONMessage updateByGroupDoctor(GroupDoctor gdoc) throws HttpApiException {
        gdoc.setUpdator(ReqUtil.instance.getUserId());
        gdoc.setUpdatorDate(new Date().getTime());
        //更新groupDoctor 移到facadeService add by wangqiao
        groupFacadeService.updateGroupDoctor(gdoc);
        return JSONMessage.success("success");
    }


    /**
     * @api {post} /group/doctor/confirmByGroupDoctor 同意加入集团
     * @apiVersion 1.0.0
     * @apiName confirmByGroupDoctor
     * @apiGroup 集团医生
     * @apiDescription 同意加入集团（此接口只用于医生加入集团使用）
     *
     * @apiParam  {String}    		access_token        token
     * @apiParam  {String}   		id           		记录Id
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
//	@RequestMapping("/confirmByGroupDoctor")
//	public JSONMessage confirmByGroupDoctor(String id) {
//		GroupDoctor gdoc = new GroupDoctor();
//		gdoc.setId(id);
//		gdoc.setStatus("C");
//		gdoc.setUpdator(ReqUtil.instance.getUserId());
//		gdoc.setUpdatorDate(new Date().getTime());
//		gdocService.updateGroupDoctor(gdoc);
//		return JSONMessage.success("success");
//	}

    /**
     * @api {post} /group/doctor/deleteByIds 删除集团医生
     * @apiVersion 1.0.0
     * @apiName deleteByIds
     * @apiGroup 集团医生
     * @apiDescription 批量删除集团医生
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		ids           		             记录Id
     * @apiSuccess {Number} 		resultCode    			返回状态吗
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/deleteByIds")
    public JSONMessage deleteByIds(String[] ids) {
        gdocService.deleteGroupDoctor(ids);
        return JSONMessage.success("success");
    }

    /**
     * @api {post} /group/doctor/searchByGroupDoctor 查找所有集团医生
     * @apiVersion 1.0.0
     * @apiName searchByGroupDoctor
     * @apiGroup 集团医生
     * @apiDescription 查找所有集团医生
     * @apiParam {String}    	access_token        			token
     * @apiParam {String}   	groupId           				集团Id
     * @apiParam {String}   	contactWay         				联系方式
     * @apiParam {String}   	status         					状态    I：已邀请待确认，C：正常使用，S：已停用（已离职），N：拒绝加入 J：已申请待确认
     * @apiParam {String}		deptId							科室Id
     * @apiParam {String}		packId							服务Id
     * @apiParam {String}		keyword							搜索关键字
     * @apiParam {List}		areaId							地区Id
     * @apiSuccess {String}  	id								记录Id（集团医生Id）
     * @apiSuccess {String}  	groupId							集团Id
     * @apiSuccess {Integer}  	doctorId        				医生Id
     * @apiSuccess {String}  	status        					状态  I：已邀请待确认，C：正常使用，S：已停用（已离职）   N：拒绝加入
     * @apiSuccess {Integer}  	referenceId        				推荐人Id
     * @apiSuccess {String}  	recordMsg        				邀请信息
     * @apiSuccess {String}  	contactWay       				联系方式
     * @apiSuccess {String}  	remarks        					备注
     * @apiSuccess {Integer}  	creator        					创建人
     * @apiSuccess {Long}  		creatorDate       				创建时间
     * @apiSuccess {Integer}  	updator        					更新人
     * @apiSuccess {Long}  		updatorDate       				更新时间
     * @apiSuccess {Integer}  	inviterId        				邀请人Id
     * @apiSuccess {String}  	inviterName       				邀请人名称
     * @apiSuccess {Integer}  	applyStatus        				审核状态
     * @apiSuccess {String}  	applyStatusName       			审核状态名称(已通过，未通过，待审核，未认证)
     * @apiSuccess {Object[]}  	doctor       					医生信息
     * @apiSuccess {String}  	doctor.name       				医生名称
     * @apiSuccess {String}  	doctor.position   				医生职称
     * @apiSuccess {String}  	doctor.doctorNum  				医生号
     * @apiSuccess {String}  	doctor.skill      				擅长领域
     * @apiSuccess {String}  	doctor.introduction       		医生个人简介
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/searchByGroupDoctor")
    public JSONMessage searchByGroupDoctor(GroupDoctorParam gdoc) {
        if (gdoc.getEndTime() != null && gdoc.getStartTime() > System.currentTimeMillis()) {
            throw new ServiceException("开始时间不能大于当前时间");
        }
        if (gdoc.getEndTime() != null && gdoc.getStartTime() != null && gdoc.getStartTime() > gdoc.getEndTime()) {
            throw new ServiceException("开始时间不能大于结束时间");
        }
        if (gdoc.getPackId() != null && gdoc.getGroupId() != null) {

            List<Integer> list = packService.getDocIdsByServiceType(gdoc.getGroupId(), gdoc.getPackId());
            gdoc.setHasTypeIds(list);

        }

        return JSONMessage.success(null, gdocService.searchGroupDoctor(gdoc));
    }


    /**
     * @api {post} /group/doctor/searchDoctorByKeyWord 查找集团医生
     * @apiVersion 1.0.0
     * @apiName searchDoctorByKeyWord
     * @apiGroup 集团医生
     * @apiDescription 查找集团的医生，如果未分配科室，则科室相关属性为空
     * @apiParam {String}    	access_token        						token
     * @apiParam {String}   	groupId       							       集团Id
     * @apiParam {String}   	consultationPackId       					会诊包id
     * @apiParam {String}   	keyword       								搜索关键字（同时匹配医生名称和医生职称，条件是或者（or），不是并且（and））
     * @apiParam {String}   	doctorStatus       						集团医生状态： C.正在使用，I.已邀请待确认， S.已停用（已离职），O.已踢出   N.已拒绝,,不传参数，默认查所有
     * @apiSuccess {Object[]} 	ddoc              							公司用户
     * @apiSuccess {String}  	ddoc.id										记录Id
     * @apiSuccess {String}  	ddoc.departmentId							组织Id
     * @apiSuccess {String}  	ddoc.departmentFullName						科室全名称
     * @apiSuccess {Integer}  	ddoc.doctorId        						医生Id
     * @apiSuccess {Integer}  	ddoc.creator        						创建人
     * @apiSuccess {Long}  		ddoc.creatorDate       						创建时间
     * @apiSuccess {Integer}  	ddoc.updator        						更新人
     * @apiSuccess {Long}  		ddoc.updatorDate       						更新时间
     * @apiSuccess {Object[]}  	ddoc.doctor       							医生信息
     * @apiSuccess {String}  	ddoc.doctor.name       						医生名称
     * @apiSuccess {String}  	ddoc.doctor.position   						医生职称
     * @apiSuccess {String}  	ddoc.doctor.doctorNum  						医生号
     * @apiSuccess {String}  	ddoc.doctor.skill      						擅长领域
     * @apiSuccess {String}  	ddoc.doctor.introduction       				医生个人简介
     * @apiSuccess {Object[]}  	ddoc.invite       							谁邀请了我的邀请信息
     * @apiSuccess {Integer}  	ddoc.invite.inviterId       				邀请人Id
     * @apiSuccess {Integer}  	ddoc.invite.inviteeId       				被邀请人Id
     * @apiSuccess {String}  	ddoc.invite.headPicFileName     			头像名称
     * @apiSuccess {String}  	ddoc.invite.headPicFilePath     			头像地址
     * @apiSuccess {String}  	ddoc.invite.name       						名称
     * @apiSuccess {Long}  		ddoc.invite.inviteDate       				邀请日期
     * @apiSuccess {String}  	ddoc.invite.inviteMsg       				邀请信息
     * @apiSuccess {Object[]}  	ddoc.invite.myInvite       					我邀请的人的信息列表
     * @apiSuccess {Integer}  	ddoc.invite.myInvite.inviterId       		邀请人Id
     * @apiSuccess {Integer}  	ddoc.invite.myInvite.inviteeId       		被邀请人Id
     * @apiSuccess {String}  	ddoc.invite.myInvite.headPicFileName     	头像名称
     * @apiSuccess {String}  	ddoc.invite.myInvite.headPicFilePath     	头像地址
     * @apiSuccess {String}  	ddoc.invite.myInvite.name       			名称
     * @apiSuccess {Long}  		ddoc.invite.myInvite.inviteDate       		邀请日期
     * @apiSuccess {String}  	ddoc.invite.myInvite.inviteMsg       		邀请信息
     * @apiAuthor 屈军利
     * @date 2015年8月11日
     */
    @RequestMapping("/searchDoctorByKeyWord")
    public JSONMessage searchDoctorByKeyWord(DepartmentDoctorParam ddoc) {
        return JSONMessage.success(null, gdocService.searchDoctorByKeyWord(ddoc));
    }

    /**
     * @api {post} /group/doctor/getUndistributedDoctor 获取未分配的集团医生
     * @apiVersion 1.0.0
     * @apiName getUndistributedDoctor
     * @apiGroup 集团医生
     * @apiDescription 获取未分配的集团医生
     * @apiParam {String}    	access_token        		token
     * @apiParam {String}   	groupId           			集团Id
     * @apiParam {String}   	consultationPackId          会诊包id
     * @apiParam {String}   	status          			状态  1：正常;2：待审核;3:审核未通过;4:暂时禁用;5:永久禁用;6:未激活;7:未认证;8：离职;9:注销.(默认所有)
     * @apiSuccess {String}  	id							记录Id（集团医生Id）
     * @apiSuccess {String}  	groupId						集团Id
     * @apiSuccess {Integer}  	doctorId        			医生Id
     * @apiSuccess {String}  	status        				状态   状态  I：已邀请待确认，C：正常使用，S：已停用（已离职）   N：拒绝加入
     * @apiSuccess {Integer}  	referenceId        			推荐人Id
     * @apiSuccess {String}  	recordMsg        			邀请信息
     * @apiSuccess {String}  	contactWay       			联系方式
     * @apiSuccess {String}  	remarks        				备注
     * @apiSuccess {Integer}  	inviterId        			邀请人Id
     * @apiSuccess {String}  	inviterName       			邀请人名称
     * @apiSuccess {Integer}  	applyStatus        			审核状态
     * @apiSuccess {String}  	applyStatusName       		审核状态名称(已通过，未通过，待审核，未认证)
     * @apiSuccess {Integer}  	creator        				创建人
     * @apiSuccess {Long}  		creatorDate       			创建时间
     * @apiSuccess {Integer}  	updator        				更新人
     * @apiSuccess {Long}  		updatorDate       			更新时间
     * @apiSuccess {Object[]}  	doctor       				医生信息
     * @apiSuccess {String}  	doctor.name       			医生名称
     * @apiSuccess {String}  	doctor.position   			医生职称
     * @apiSuccess {String}  	doctor.doctorNum  			医生号
     * @apiSuccess {String}  	doctor.skill      			擅长领域
     * @apiSuccess {String}  	doctor.introduction       	医生个人简介
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/getUndistributedDoctor")
    public JSONMessage getUndistributedDoctor(GroupDoctorParam gdoc) {
        return JSONMessage.success(null, gdocService.getUndistributedDoctorByGroupId(gdoc));
    }

    /**
     * @api {post} /group/doctor/getGroupDoctorListById 根据集团Id获取集团所有医生
     * @apiVersion 1.0.0
     * @apiName getGroupDoctorListById
     * @apiGroup 集团医生
     * @apiDescription 根据集团Id获取集团所有医生
     * @apiParam {String}    	access_token        		token
     * @apiParam {String}   	groupId           			集团Id
     * @apiSuccess {String}  	id							记录Id（集团医生Id）
     * @apiSuccess {String}  	groupId						集团Id
     * @apiSuccess {Integer}  	doctorId        			医生Id
     * @apiSuccess {String}  	status        				状态   状态  I：已邀请待确认，C：正常使用，S：已停用（已离职）   N：拒绝加入
     * @apiSuccess {Integer}  	referenceId        			推荐人Id
     * @apiSuccess {String}  	recordMsg        			邀请信息
     * @apiSuccess {String}  	contactWay       			联系方式
     * @apiSuccess {String}  	remarks        				备注
     * @apiSuccess {Integer}  	creator        				创建人
     * @apiSuccess {Long}  		creatorDate       			创建时间
     * @apiSuccess {Integer}  	updator        				更新人
     * @apiSuccess {Long}  		updatorDate       			更新时间
     * @apiSuccess {Object[]}  	doctor       				医生信息
     * @apiSuccess {String}  	doctor.name       			医生名称
     * @apiSuccess {String}  	doctor.position   			医生职称
     * @apiSuccess {String}  	doctor.doctorNum  			医生号
     * @apiSuccess {String}  	doctor.skill      			擅长领域
     * @apiSuccess {String}  	doctor.introduction    		医生个人简介
     * @apiAuthor pijingwei
     * @date 2015年8月11日
     */
    @RequestMapping("/getGroupDoctorListById")
    public JSONMessage getGroupDoctorListById(String groupId) {
        return JSONMessage.success(null, gdocService.getGroupDoctorListByGroupId(groupId));
    }


    /**
     * @api {post} /group/doctor/getGroupAndDepartmentById 获取我的集团及科室
     * @apiVersion 1.0.0
     * @apiName getGroupAndDepartmentById
     * @apiGroup mobile接口
     * @apiDescription 获取我的集团及科室
     * @apiParam {String}    	access_token        token
     * @apiParam {Integer}   	doctorId       		医生Id
     * @apiParam {String}   	status         		状态
     * @apiSuccess {groupList[]} 		groupList              			集团列表
     * @apiSuccess {String}  			groupList.id					集团Id
     * @apiSuccess {String}  			groupList.companyId				公司Id
     * @apiSuccess {String}  			groupList.name					集团名称
     * @apiSuccess {String}  			groupList.introduction			集团介绍
     * @apiSuccess {Integer}  			groupList.creator				创建人
     * @apiSuccess {Long}  				groupList.creatorDate			创建时间
     * @apiSuccess {Integer}  			groupList.updator				更新人
     * @apiSuccess {Long}  				groupList.updatorDate			更新时间
     * @apiSuccess {departmentList[]}  	departmentList					组织架构列表
     * @apiSuccess {String}  			departmentList.id				组织架构Id（组织id）
     * @apiSuccess {String}  			departmentList.groupId			集团Id
     * @apiSuccess {String}  			departmentList.name				组织名称
     * @apiSuccess {String}  			departmentList.introduction		组织介绍
     * @apiSuccess {Integer}  			departmentList.creator			创建人
     * @apiSuccess {Long}  				departmentList.creatorDate		创建时间
     * @apiSuccess {Integer}  			departmentList.updator			更新人
     * @apiSuccess {Long}  				departmentList.updatorDate		更新时间
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/getGroupAndDepartmentById")
    public JSONMessage getGroupAndDepartmentById(GroupDoctorParam gdoc) {
        return JSONMessage.success(null, gdocService.getGroupAndDepartmentById(gdoc));
    }


    /**
     * @api {post} /group/doctor/getDepartmentAndDoctorById 获取集团下的科室和医生
     * @apiVersion 1.0.0
     * @apiName getDepartmentAndDoctorById
     * @apiGroup mobile接口
     * @apiDescription 获取我的集团及科室
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	groupId        		集团Id
     * @apiSuccess {Object[]}  			departmentList							组织架构列表
     * @apiSuccess {String}  			departmentList.id						组织架构Id（组织id）
     * @apiSuccess {String}  			departmentList.groupId					集团Id
     * @apiSuccess {String}  			departmentList.name						组织名称
     * @apiSuccess {String}  			departmentList.introduction				组织介绍
     * @apiSuccess {Integer}  			departmentList.creator					创建人
     * @apiSuccess {Long}  				departmentList.creatorDate				创建时间
     * @apiSuccess {Integer}  			departmentList.updator					更新人
     * @apiSuccess {Long}  				departmentList.updatorDate				更新时间
     * @apiSuccess {Object[]}  			doctorList								医生列表
     * @apiSuccess {String}  			doctorList.id							集团医生Id
     * @apiSuccess {String}  			doctorList.groupId						集团Id
     * @apiSuccess {String}  			doctorList.doctorId						医生Id
     * @apiSuccess {String}  			doctorList.referenceId					推荐人
     * @apiSuccess {Integer}  			doctorList.recordMsg					邀请信息
     * @apiSuccess {Long}  				doctorList.contactWay					联系方式
     * @apiSuccess {Integer}  			doctorList.remarks						备注
     * @apiSuccess {Integer}  			doctorList.creator						创建人
     * @apiSuccess {Long}  				doctorList.creatorDate					创建时间
     * @apiSuccess {Integer}  			doctorList.updator						更新人
     * @apiSuccess {Long}  				doctorList.updatorDate					更新时间
     * @apiSuccess {Object[]}  			doctorList.doctor						医生个人信息
     * @apiSuccess {String}  			doctorList.doctor.name					医生名称
     * @apiSuccess {String}  			doctorList.doctor.position				职称
     * @apiSuccess {String}  			doctorList.doctor.doctorNum				医生号
     * @apiSuccess {String}  			doctorList.doctor.skill					擅长领域
     * @apiSuccess {String}  			doctorList.doctor.introduction			个人介绍
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/getDepartmentAndDoctorById")
    public JSONMessage getDepartmentAndDoctorById(String groupId) {
        return JSONMessage.success(null, gdocService.getDepartmentAndDoctorById(groupId));
    }

    /**
     * @api {post} /group/doctor/getAllDataById 获取所有数据
     * @apiVersion 1.0.0
     * @apiName getAllDataById
     * @apiGroup mobile接口
     * @apiDescription 获取所有数据
     * @apiParam {String}    	access_token        token
     * @apiParam {Integer}   	doctorId       		医生Id
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/getAllDataById")
    public JSONMessage getAllDataById(GroupDoctorParam gdoc) {
        return JSONMessage.success(null, gdocService.getGroupListAndAllSubListById(gdoc));
    }

    /**
     * @api {post} /group/doctor/getMyInviteRelationListById 获取我邀请的人的列表
     * @apiVersion 1.0.0
     * @apiName getMyInviteRelationListById
     * @apiGroup mobile接口
     * @apiDescription 获取我邀请的人的列表
     * @apiParam {String}    	access_token        	token
     * @apiParam {Integer}   	doctorId        		医生Id
     * @apiParam {String}   	groupId        			集团Id
     * @apiSuccess {String}  	inviterId				邀请人
     * @apiSuccess {String}  	inviteeId				被邀请人
     * @apiSuccess {String}  	name					被邀请人名称
     * @apiSuccess {String}  	headPicFileName			被邀请人头像
     * @apiSuccess {String}  	inviteDate				邀请日期
     * @apiSuccess {String}  	inviteMsg				邀请信息描述
     * @apiSuccess {Integer}  	inviteCount				被邀请人邀请的总人数
     * @apiAuthor pijingwei
     * @date 2015年8月10日
     */
    @RequestMapping("/getMyInviteRelationListById")
    public JSONMessage getMyInviteRelationListById(Integer doctorId, String groupId) {
        return JSONMessage.success(null, gdocService.getMyInviteRelationListByDoctorId(doctorId, groupId));
    }

    /**
     * @api {post} /group/doctor/getInviteRelationTree 获取集团邀请人员树结构
     * @apiVersion 1.0.0
     * @apiName getInviteRelationTree
     * @apiGroup mobile接口
     * @apiDescription 获取集团邀请人员树结构
     * @apiParam {String}      access_token                token
     * @apiParam {Integer}     groupId                     集团Id
     * @apiSuccess {String}     id                          id
     * @apiSuccess {String}     name                        姓名
     * @apiSuccess {String}     parentId                    父id
     * @apiSuccess {String}     leaf                        是否叶子节点
     * @apiSuccess {Object[]}   attributes                  其他属性
     * @apiSuccess {String}     attributes.inviteDate       邀请日期
     * @apiSuccess {String}     attributes.headPicFileName  头像
     * @apiAuthor 范鹏
     * @date 2015年8月10日
     */
    @RequestMapping("/getInviteRelationTree")
    public JSONMessage getInviteRelationTree(String groupId) {
        return JSONMessage.success(null, gdocService.getInviteRelationTree(ReqUtil.instance.getUserId(), groupId));
    }


    /**
     * @api {post} /group/doctor/HasPermissionByInvite 获取是否允许成员邀请医生加入权限
     * @apiVersion 1.0.0
     * @apiName HasPermissionByInvite
     * @apiGroup mobile接口
     * @apiDescription 获取集团是否允许成员邀请医生加入权限
     * @apiParam {String}      access_token                token
     * @apiParam {Integer}     groupId                     集团Id
     * @apiSuccess {Number} 	hasPermission    			权限状态：1，有权限，0，没有权限
     * @apiSuccess {String} 	msg    						提示信息
     * @apiAuthor pijingwei
     * @date 2015年8月29日
     */
    @RequestMapping("/HasPermissionByInvite")
    public JSONMessage HasPermissionByInvite(GroupDoctor cuser) {
        return JSONMessage.success(null, gdocService.HasPermissionByInvite(cuser));
    }


    /**
     * @api {post} /group/doctor/getInviteGroupDoctors 邀请时获取医生集团医生信息
     * @apiVersion 1.0.0
     * @apiName getInviteGroupDoctors
     * @apiGroup mobile接口
     * @apiDescription 邀请时获取医生集团医生信息和某个医生信息
     * @apiParam {Integer}         groupId                     集团Id
     * @apiParam {Integer}         doctorId                    医生Id
     * @apiSuccess {Integer}     	id                          医生id
     * @apiSuccess {String}     	name                        医生姓名
     * @apiSuccess {String}     	headPicFileName             头像地址
     * @apiSuccess {String}         groupId                     集团Id
     * @apiSuccess {String}         groupName                   集团名称
     * @apiSuccess {Object[]}   	users                  		邀请列表
     * @apiSuccess {String}     	users.name       			姓名
     * @apiSuccess {String}     	users.headPicFileName  		头像
     * @apiAuthor 范鹏
     * @date 2015年8月29日
     */
    @RequestMapping("/getInviteGroupDoctors")
    public JSONMessage getInviteGroupDoctors(String groupId, Integer doctorId) {
        //构造返回map
        Map<String, Object> data = new HashMap<String, Object>();
        //参数校验
        if (null == doctorId || 0 == doctorId) {
            data.put("status", 0);
            data.put("msg", "医生Id为空");
            return JSONMessage.success(null, data);
        }
        if (StringUtil.isEmpty(groupId)) {
            data.put("status", 0);
            data.put("msg", "集团Id为空");
            return JSONMessage.success(null, data);
        }

        //获取集团信息
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            data.put("status", 0);
            data.put("msg", "集团不存在");
            return JSONMessage.success(null, data);
        }

        //获取邀请医生信息
        BaseUserVO user = baseUserService.getUser(doctorId);
        if (user == null) {
            data.put("status", 0);
            data.put("msg", "邀请人不存在");
            return JSONMessage.success(null, data);
        }

        //先查询集团创建者
        GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);

        //获取集团医生Id
        List<Integer> doctorIds = baseUserService.getDoctorIdByGroup(groupId);
        if (groupUser != null && null != groupUser.getDoctorId()) {
            doctorIds.remove(groupUser.getDoctorId());
        }

        //获取所有医生信息
        List<BaseUserVO> userList = new ArrayList<BaseUserVO>();

        if (groupUser != null && null != groupUser.getDoctorId()) {
            List<BaseUserVO> rootUser = baseUserService.getByIds((new Integer[]{groupUser.getDoctorId()}));
            userList.addAll(rootUser);
        }

        if (doctorIds.size() > 0) {
            List<BaseUserVO> otherUsers = baseUserService.getByIds(doctorIds.toArray(new Integer[]{}));
            userList.addAll(otherUsers);
        }
        data.put("status", null);
        data.put("id", doctorId);
        data.put("name", user.getName());
        data.put("headPicFileName", user.getHeadPicFileName());
        data.put("groupId", group.getId());
        data.put("groupName", group.getName());
        data.put("users", userList);
        return JSONMessage.success(null, data);

    }


    /**
     * @api {post} /group/doctor/sendNoteInvite 发送短信邀请
     * @apiVersion 1.0.0
     * @apiName sendNoteInvite
     * @apiGroup mobile接口
     * @apiDescription 发送短信邀请
     * @apiParam {String}      access_token                token
     * @apiParam {Integer}     groupId                     集团Id
     * @apiParam {Integer}     doctorId                    医生Id（我的医生Id）
     * @apiSuccess {String}     	note  					短信模板
     * @apiSuccess {String}     	msg  					提示信息
     * @apiAuthor pijingwei
     * @date 2015年8月29日
     */
    @RequestMapping("/sendNoteInvite")
    public JSONMessage sendNoteInvite(GroupDoctor gdoc) throws HttpApiException {
        gdoc.setStatus("I");
        gdoc.setReferenceId(ReqUtil.instance.getUserId());
        gdoc.setCreator(ReqUtil.instance.getUserId());
        gdoc.setCreatorDate(new Date().getTime());
        gdoc.setUpdator(ReqUtil.instance.getUserId());
        gdoc.setUpdatorDate(new Date().getTime());
        return JSONMessage.success(null, gdocService.sendNoteInviteBydoctorId(gdoc));
    }

    /**
     * @api {post} /group/doctor/setTaskTimeLong				设置某个医生集团在线时间设置
     * @apiVersion 1.0.0
     * @apiName setTaskTimeLong
     * @apiGroup 在线值班
     * @apiDescription 设置某个医生集团在线时间设置
     * @apiParam {String}      access_token                token
     * @apiParam {String}		groupId						集团Id
     * @apiParam {Object[]}    entries                     要设置的对象数组
     * @apiParam {Integer}     entries.doctorId            医生Id
     * @apiParam {Long}     	entries.taskDuration        值班时间(秒)
     * @apiAuthor 李淼淼
     * @date 2015年9月25日
     */
    @RequestMapping("/setTaskTimeLong")
    public JSONMessage setTaskTimeLong(String groupId, GroupDoctorBatchParam p) {
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException(4006, "集团id为空！");
        }

        GroupDoctor[] gdocs = p.getEntries();

        gdocService.updateTaskDuration(groupId);
        if (gdocs != null) {
            for (GroupDoctor gdoc : gdocs) {
                gdoc.setGroupId(groupId);
                gdoc.setUpdator(ReqUtil.instance.getUserId());
                gdoc.setUpdatorDate(new Date().getTime());
                gdocService.setTaskTimeLong(gdoc);
            }
        }


        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /group/doctor/doctorOnline 上线（开始接单）
     * @apiVersion 1.0.0
     * @apiName doctorOnline
     * @apiGroup 在线值班
     * @apiDescription 上线（开始接单）
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} groupId 集团Id
     * @apiParam {Integer} doctorId 医生Id（我的医生Id）
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/doctorOnline")
    public JSONMessage doctorOnline(GroupDoctor gdoc) throws HttpApiException {
        boolean isOnline = false;
        if (StringUtil.isEmpty(gdoc.getGroupId()) || GroupUtil.PLATFORM_ID.equals(gdoc.getGroupId())) {
            isOnline = pdocService.doctorOnline(gdoc);
        } else {
            isOnline = gdocService.doctorOnline(gdoc);
        }
        if (isOnline) {
            return JSONMessage.success(null, null);
        } else {
            return JSONMessage.failure("请在集团值班时间段内值班");
        }
    }


    /**
     * @api {[get,post]} /group/doctor/doctorOffline 下线（结束接单）
     * @apiVersion 1.0.0
     * @apiName doctorOffline
     * @apiGroup 在线值班
     * @apiDescription 下线（结束接单）
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} groupId 集团Id
     * @apiParam {Integer} doctorId 医生Id（我的医生Id）
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/doctorOffline")
    public JSONMessage doctorOffline(GroupDoctor gdoc) throws HttpApiException {
        Object data = null;
        if (StringUtil.isEmpty(gdoc.getGroupId())) {
            pdocService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE);
            data = pdocService.getOne(gdoc);
        } else {
            gdocService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE);
            data = gdocService.getOne(gdoc);
        }
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} 					/group/doctor/findOnlineDoctorByGroupId					获取在线值班医生（每行只显示7个）
     * @apiVersion 1.0.0
     * @apiName findOnlineDoctorByGroupId
     * @apiGroup 在线值班
     * @apiDescription 获取在线值班医生（每行只显示7个）
     * @apiParam {String}    					access_token         		 凭证
     * @apiParam {String}    					groupId         		 		集团id
     * @apiSuccess {List}       doctorVOs                    医生对象list
     * @apiSuccess {String}     doctorVOs.userId              医生id
     * @apiSuccess {String}     doctorVOs.name                姓名
     * @apiSuccess {String}     doctorVOs.doctorNum           医生号
     * @apiSuccess {String}     doctorVOs.headPicFileName     头像
     * @apiSuccess {String}     doctorVOs.hospital            医院
     * @apiSuccess {String}     doctorVOs.departments         科室
     * @apiSuccess {String}     doctorVOs.title               职称
     * @apiSuccess {String}     doctorVOs.groupName           集团名称
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年9月21日
     */
    // 逻辑错误：0001：按照科室分组呢？
    @RequestMapping("/findOnlineDoctorByGroupId")
    public JSONMessage findOnlineDoctorByGroupId(GroupDoctorParam param) {
        param.setPageSize(7);
        param.setOnLineState("1");
        // 逻辑错误：0008：为什么上线下线还需要保存到Mongo，而且有多个状态？redis既然维护了列表，直接拿出来用就可以了
        Map sorter = new HashMap();
        sorter.put("onLineTime", -1);//最后一次上线时间倒序
        PageVO data = gdocService.searchGroupDoctor(param);
        return JSONMessage.success(null, data);
    }


    /**
     * @api {[get,post]} 					/group/doctor/listOnlineDoctorGroupByDept					获取在线值班医生（每行只显示7个，已分组）
     * @apiVersion 1.0.0
     * @apiName listOnlineDoctorGroupByDept
     * @apiGroup 在线值班
     * @apiDescription 获取在线值班医生（每行只显示7个,已分组）
     * @apiParam {String}    					access_token         		 凭证
     * @apiParam {String}    					groupId         		 		集团id
     * @apiSuccess {String}       id                   科室id
     * @apiSuccess {String}     name              科室名称
     * @apiSuccess {User}     doctorList.user          医生用户
     * @apiSuccess {String}     doctorList.user.headPicFileName   		头像
     * @apiSuccess {Doctor}     doctorList.user.doctor           医生
     * @apiSuccess {String}     doctorList.user.doctor.cureNum           问诊人数
     * @apiSuccess {String}     doctorList.user.doctor.title   					职称
     * @apiSuccess {boolean}     doctorList.isFree           是否收费（true：免费，false：收费）
     * @apiExample {javascript} Example usage:
     * 科室　/**
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/listOnlineDoctorGroupByDept")
    public JSONMessage listOnlineDoctorGroupByDept(GroupDoctorParam param) {
        param.setPageSize(7);
        param.setOnLineState("1");
        Map sorter = new HashMap();
        sorter.put("onLineTime", -1);//最后一次上线时间倒序
        Object data = gdocService.listGroupDoctorGroupByDept(param);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {post} /group/doctor/setOutpatientPrice			设置某个集团医生的门诊价格
     * @apiVersion 1.0.0
     * @apiName setOutpatientPrice
     * @apiGroup 在线值班
     * @apiDescription 设置某个集团医生的门诊价格
     * @apiParam {String}      access_token                token
     * @apiParam {String}      groupId                     集团Id
     * @apiParam {Integer}     outpatientPrice            门诊价格
     * @apiAuthor 李淼淼
     * @date 2015年9月25日
     */
    @RequestMapping("/setOutpatientPrice")
    public JSONMessage setOutpatientPrice(String groupId, Integer outpatientPrice) {
        gdocService.setOutpatientPrice(groupId, outpatientPrice);
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/getHasSetPrice			获取某个集团已设置门诊价格的医生
     * @apiVersion 1.0.0
     * @apiName getHasSetPrice
     * @apiGroup 在线值班
     * @apiDescription 获取某个集团已设置门诊价格的医生
     * @apiParam {String}      access_token                token
     * @apiParam {Integer}     groupId                     集团Id
     * @apiSuccess {GroupDoctorVO}	GroupDoctorVO			集团医生VO对象
     * @apiSuccess {Integer}	outpatientPrice				值班价格
     * @apiAuthor 李淼淼
     * @date 2015年9月25日
     */
    @RequestMapping("/getHasSetPrice")
    public JSONMessage getHasSetPrice(GroupDoctorParam p) {
        if (p == null) {
            throw new ServiceException(30004, "groupId is null");
        }
        if (StringUtil.isEmpty(p.getGroupId())) {
            throw new ServiceException(30004, "groupId is null");
        }
        List<GroupDoctorVO> data = gdocService.getHasSetPriceGroupDoctorListByGroupId(p.getGroupId());
        return JSONMessage.success(null, data);
    }

    /**
     * @api {post} /group/doctor/updateContactWay	修改集团医生联系方式
     * @apiVersion 1.0.0
     * @apiName updateContactWay
     * @apiGroup 集团医生
     * @apiDescription 修改集团医生联系方式
     * @apiParam    {String}	access_token	token
     * @apiParam    {String}	groupId			集团Id
     * @apiParam    {Integer}	doctorId		医生Id
     * @apiParam    {String}	contactWay		新的联系方式
     * @apiSuccess    {Number}	resultCode		返回状态码
     * @apiAuthor 谢平
     * @date 2015年11月3日
     */
    @RequestMapping("/updateContactWay")
    public JSONMessage updateContactWay(GroupDoctorParam param) {
        gdocService.updateContactWay(param);
        return JSONMessage.success(null);
    }

    /**
     * @api {post} /group/doctor/setMainGroup 设置主医生集团
     * @apiVersion 1.0.0
     * @apiName setMainGroup
     * @apiGroup 集团医生
     * @apiDescription 设置主医生集团
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	groupId       		集团Id
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor xieping
     * @date 2015年12月21日
     */
    @RequestMapping("/setMainGroup")
    public JSONMessage setMainGroup(GroupDoctorParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        gdocService.setMainGroup(param);
        return JSONMessage.success("success");
    }

    @RequestMapping("/getGroupInfo")
    public JSONMessage getGroupInfo(Integer doctorId) {
        return JSONMessage.success(gdocService.getByDoctorId(doctorId));
    }

    /**
     * @api {post} /group/doctor/getGroups 获取我所在的医生集团
     * @apiVersion 1.0.0
     * @apiName getGroups
     * @apiGroup 集团医生
     * @apiDescription 获取登录用户所在的医生集团（主集团排序置顶，其他集团按加入时间顺序排列）
     * @apiParam {String}    	access_token        token
     * @apiSuccess {String}  id    			集团Id
     * @apiSuccess {String}  name    		集团名
     * @apiSuccess {Boolean} isMain    		是否主集团
     * @apiSuccess {Boolean} isAdmin    	是否集团管理员
     * @apiSuccess {String}  dutyStartTime  值班开始时间
     * @apiSuccess {String}  dutyEndTime    值班结束时间
     * @apiSuccess {String}  introduction    		集团介绍
     * @apiSuccess {String}  groupIconPath    	集团头像
     * @apiSuccess {String}  certStatus     加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     * @apiSuccess {Number}  resultCode     返回状态码
     * @apiAuthor xieping
     * @date 2015年12月21日
     */
    @RequestMapping("/getGroups")
    public JSONMessage getGroups(GroupDoctorParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success("success", gdocService.getGroups(param.getDoctorId()));
    }

    /**
     * @api {post} /group/doctor/getMyGroups 获取我所在的医生集团(只查集团不查医院)
     * @apiVersion 1.0.0
     * @apiName getGroups
     * @apiGroup 集团医生
     * @apiDescription 获取登录用户所在的医生集团（主集团排序置顶，其他集团按加入时间顺序排列）(只查集团不查医院)
     * @apiParam {String}    	access_token        token
     * @apiSuccess {String}  id    			集团Id
     * @apiSuccess {String}  name    		集团名
     * @apiSuccess {Boolean} isMain    		是否主集团
     * @apiSuccess {Boolean} isAdmin    	是否集团管理员
     * @apiSuccess {String}  dutyStartTime  值班开始时间
     * @apiSuccess {String}  dutyEndTime    值班结束时间
     * @apiSuccess {String}  introduction    		集团介绍
     * @apiSuccess {String}  groupIconPath    	集团头像
     * @apiSuccess {String}  certStatus     加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     * @apiSuccess {Number}  resultCode     返回状态码
     * @apiAuthor jhj
     * @date 2016年5月11日14:58:59
     */
    @RequestMapping("/getMyGroups")
    public JSONMessage getMyGroups(GroupDoctorParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success("success", gdocService.getMyGroups(param.getDoctorId()));
    }

    /**
     * @api {post} /group/doctor/getGroupsOnDuty 获取医生集团
     * @apiVersion 1.0.0
     * @apiName getGroupsOnDuty
     * @apiGroup 集团医生
     * @apiDescription 获取当前时间可值班的医生集团
     * @apiParam {String}    	access_token        token
     * @apiSuccess {String}  id    			集团Id
     * @apiSuccess {String}  name    		集团名
     * @apiSuccess {Boolean} isMain    		是否主集团
     * @apiSuccess {String}  dutyStartTime  值班开始时间
     * @apiSuccess {String}  dutyEndTime    值班结束时间
     * @apiSuccess {Number}  resultCode     返回状态码
     * @apiAuthor xieping
     * @date 2015年12月21日
     */
    @RequestMapping("/getGroupsOnDuty")
    public JSONMessage getGroupsOnDuty(GroupDoctorParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success("success", gdocService.getGroupsOnDuty(param.getDoctorId()));
    }

    /**
     * @api {post} /group/doctor/getDoctorDutyInfo	获取医生的值班信息
     * @apiVersion 1.0.0
     * @apiName getDoctorDutyInfo
     * @apiGroup 集团医生
     * @apiDescription 获取医生的值班信息（如果没传医生ID，则获取当前登录医的值班信息）
     * @apiParam    {String}	access_token	token
     * @apiParam    {Integer}	doctorId		医生Id
     * @apiSuccess {String} 	troubleFree 		免打扰（1：正常，2：免打扰）
     * @apiSuccess {String} 	serverTime 			服务器时间
     * @apiSuccess {String} 	onLineState 		在线状态（1在线，2离线）注：非集团医生
     * @apiSuccess {Integer} 	dutyDuration 		已值班时长（秒）注：非集团医生
     * @apiSuccess {Integer} 	outpatientPrice 	门诊价格（分）注：非集团医生
     * @apiSuccess {Integer} 	groupDoctor.taskDuration 		在线时长（任务）/秒
     * @apiSuccess {Integer} 	groupDoctor.outpatientPrice 	门诊价格（分）
     * @apiSuccess {Integer} 	groupDoctor.dutyDuration 		已值班时长（秒）
     * @apiSuccess {String} 	groupDoctor.onLineState 		在线状态（1在线，2离线）
     * @apiAuthor 张垠
     * @date 2015年12月18日
     */
    @RequestMapping("getDoctorDutyInfo")
    public JSONMessage getDoctorDutyInfo(Integer doctorId) {
        if (doctorId == null) {
            doctorId = ReqUtil.instance.getUserId();
        }

        return JSONMessage.success(null, groupFacadeService.getDutyInfo(doctorId));
    }

    /**
     * @api {post} /group/doctor/saveByDoctorApply	保存 医生加入医生集团申请
     * @apiVersion 1.0.0
     * @apiName saveByDoctorApply
     * @apiGroup 集团医生
     * @apiDescription 保存 医生加入医生集团申请
     * @apiParam    {String}	 access_token	    token
     * @apiParam    {String} groupId		    医生集团Id
     * @apiParam    {String} applyMsg		    申请加入留言
     * @apiParam    {Integer} inviterId		   邀请人的id(选填)
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor 王峭
     * @date 2015年12月21日
     */
    @RequestMapping("/saveByDoctorApply")
    public JSONMessage saveByDoctorApply(
            @RequestParam(name = "groupId") String groupId,
            @RequestParam(name = "applyMsg") String applyMsg,
            @RequestParam(name = "inviterId", required = false) Integer inviterId) throws HttpApiException {
        //权限校验
        UserSession userSession = ReqUtil.instance.getUser();
        //医生状态可能还需要判断
        if (userSession == null || userSession.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("当前登录用户不是医生账号");
        }
        Integer userId = ReqUtil.instance.getUserId();

        if (inviterId == null) {
            return JSONMessage.success(null, gdocService.saveByDoctorApply(groupId, applyMsg, userId));
        } else {
            User user = userManager.getUser(userId);
            if (user == null) {
                throw new ServiceException("找不到医生信息");
            }
            return JSONMessage.success(null, groupFacadeService.joinGroupByQRCode(groupId, userId, user.getTelephone(), inviterId, applyMsg));
        }

    }

    /**
     * @api {post} /group/doctor/confirmByDoctorApply	审核 医生加入医生集团申请
     * @apiVersion 1.0.0
     * @apiName confirmByDoctorApply
     * @apiGroup 集团医生
     * @apiDescription 审核 医生加入医生集团申请
     * @apiParam    {String}	 access_token	    token
     * @apiParam    {String} id		               申请id
     * @apiParam    {boolean} approve			审核是否通过  true=审核通过，false=审核不通过
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor 王峭
     * @date 2015年12月21日
     */
    @RequestMapping("/confirmByDoctorApply")
    public JSONMessage confirmByDoctorApply(GroupDoctor gdoc, boolean approve) throws HttpApiException {
        return JSONMessage.success(null, groupFacadeService.confirmByDoctorApply(gdoc, approve));
    }


    /**
     * @api {post} /group/doctor/getDoctorApplyByGroupId	查询 医生加入医生集团申请 列表
     * @apiVersion 1.0.0
     * @apiName getDoctorApplyByGroupId
     * @apiGroup 集团医生
     * @apiDescription 查询 医生加入医生集团申请列表
     * @apiParam    {String}	 access_token	         token
     * @apiParam    {String} groupId	             医生集团Id
     * @apiParam {Integer}    pageIndex                   查询页，不传默认从0开始
     * @apiParam {Integer}    pageSize                    每页显示条数，不传默认15条
     * @apiSuccess {Number}                       resultCode    返回状态码
     * @apiSuccess {String}                            id                申请id
     * @apiSuccess {Long}                            applyDate    申请时间
     * @apiSuccess {String}                           applyMsg    申请留言
     * @apiSuccess {Integer}                         doctorId    申请医生id
     * @apiSuccess {String}                           groupId    医生集团id
     * @apiSuccess {String}                           headPicFileName    医生头像地址
     * @apiSuccess {String}                           title    医生职务
     * @apiSuccess {String}                           departments    医生科室
     * @apiSuccess {String}                           hospital    所在医院
     * @apiSuccess {Integer}                          status     医生账号状态 （暂时用不上）
     * @apiAuthor 王峭
     * @date 2015年12月21日
     */
    @RequestMapping("/getDoctorApplyByGroupId")
    public JSONMessage getDoctorApplyByGroupId(GroupDoctorApplyParam param) {
        return JSONMessage.success(null, gdocService.getDoctorApplyByGroupId(param));
    }

    /**
     * @api {post} /group/doctor/getDoctorApplyByApplyId	查询单个 医生加入医生集团申请 信息
     * @apiVersion 1.0.0
     * @apiName getDoctorApplyByApplyId
     * @apiGroup 集团医生
     * @apiDescription 查询 单个医生加入医生集团申请信息
     * @apiParam    {String}	 access_token	         token
     * @apiParam    {String} id	             申请Id
     * @apiSuccess {Number}                       resultCode    返回状态码
     * @apiSuccess {String}                            id                申请id
     * @apiSuccess {Long}                            applyDate    申请时间
     * @apiSuccess {String}                           applyMsg    申请留言
     * @apiSuccess {Integer}                         doctorId    申请医生id
     * @apiSuccess {String}                           groupId    医生集团id
     * @apiSuccess {String}                           headPicFileName    医生头像地址
     * @apiSuccess {String}                           title    医生职务
     * @apiSuccess {String}                           departments    医生科室
     * @apiSuccess {String}                           hospital    所在医院
     * @apiSuccess {String}                           applyStatus     申请状态     J=正在申请 ，C=已审核通过，M=已拒绝，Z=申请过期
     * @apiSuccess {String}                           name             医生名
     * @apiSuccess {Integer}                           status             医生资格的审核状态（1=正常,2=待审核,3=审核未通过,4=暂时禁用,5=永久禁用,6=未激活,7=未认证,8=离职,9=注销）
     * @apiSuccess {String}                           checkRemark             医生资格的审核不通过的原因
     * @apiAuthor 王峭
     * @date 2016年1月19日
     */
    @RequestMapping("/getDoctorApplyByApplyId")
    public JSONMessage getDoctorApplyByApplyId(String id) {

        GroupDoctorApplyVO vo = gdocService.getDoctorApplyByApplyId(id);

        return JSONMessage.success(null, vo);
    }

    /**
     * @api {post} /group/doctor/isSameGroupMembe	判断两个医生是否在同一个集团
     * @apiVersion 1.0.0
     * @apiName isSameGroupMembe
     * @apiGroup 集团医生
     * @apiDescription 判断两个医生是否在同一个集团
     * @apiParam    {String}	 access_token	         token
     * @apiParam    {Integer} doctorId1	            医生id
     * @apiParam    {Integer} doctorId2	            医生id
     * @apiSuccess {String}                       resultCode    返回状态码
     * @apiSuccess {Integer}                       data			0:没在统一集团，1：在同一集团
     * @date 2016年3月10日
     */
    @RequestMapping("/isSameGroupMembe")
    public JSONMessage isSameGroupMembe(@RequestParam(required = true) Integer doctorId1,
                                        @RequestParam(required = true) Integer doctorId2) {
        return JSONMessage.success();
    }

    /**
     * @api {get} /group/doctor/getMembers 获取集团下的成员列表,并返回审核状态
     * @apiVersion 1.0.0
     * @apiName doctor/getMembers
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取地区
     * @apiParam {String}    access_token          token
     * @apiParam {String}    name                  关键字（支持姓名、电话、医院）
     * @apiParam {String}    status                状态（1：审核通过，2：审核中，3：审核不通过，7：未认证）
     * @apiParam {String}    groupId               集团ID
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {String}   pageData.userId              用户id
     * @apiSuccess {String}   pageData.name                用户姓名
     * @apiSuccess {String}   pageData.telephone           手机
     * @apiSuccess {String}   pageData.createTime          注册时间
     * @apiSuccess {String}   pageData.checkTime          审核时间
     * @apiSuccess {String}   pageData.orderTime          排序时间
     * @apiSuccess {String}   pageData.headPicFileName     头像
     * @apiSuccess {Number}   pageData.status              状态（1：审核通过，2：审核中，3：审核不通过，7：未认证）
     * @apiSuccess {String}   pageData.hospital            医院
     * @apiSuccess {String}   pageData.departments         科室
     * @apiSuccess {String}   pageData.title               职称
     * @apiSuccess {String}   pageData.doctorNum           医生号
     * @apiSuccess {String}   pageData.nurseNum            护士
     * @apiSuccess {String}   pageData.userType            用户类型（3：医生，9：护士）
     * @apiSuccess {object[]} pageData.nurseImageList      护士图片列表
     * @apiSuccess {String}   pageData.nurseImageList[0].imageId         图片URL地址
     * @apiSuccess {Integer}  pageData.nurseImageList[0].order           图片排序
     * @apiSuccess {String}   pageData.nurseImageList[0].imageType       图片类型
     * @apiSuccess {String}   pageData.inviterName      	 邀请者的姓名
     * @apiSuccess {String}   pageData.source       		注册来源
     * @apiAuthor 谭永芳
     * @date 2016年6月2日
     */
    @RequestMapping("/getMembers")
    public JSONMessage getMembers(GroupDoctorsParam param) {
        return JSONMessage.success(null, gdocService.findDoctorsListByGroupDoctorsParam(param));
    }

    /**
     * @api {[get,post]} /group/doctor/isInBdjl 用户是否在博德嘉联集团内，并且是否为主集团
     * @apiVersion 1.0.0
     * @apiName isInBdjl
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 用户是否在博德嘉联集团内，并且是否为主集团
     * @apiParam {String} access_token 凭证
     * @apiSuccess {Integer} isInBdjl 0：不在博德嘉联集团内 1:在博德嘉联集团内，并且为主集团。2：在博德嘉联集团内，不是主集团。
     * @apiAuthor 傅永德
     * @date 2016年6月8日18:25:49
     */
    @RequestMapping("/isInBdjl")
    public JSONMessage isInBdjl() {
        return JSONMessage.success(gdocService.isInBdjl());
    }

    /**
     * @api {post} /group/doctor/getMyNormalGroups 获取我所在的医生集团(只查集团不查医院,过滤 已屏蔽、未激活、未审核的集团)
     * @apiVersion 1.0.0
     * @apiName getMyNormalGroups
     * @apiGroup 集团医生
     * @apiDescription 获取登录用户所在的医生集团（主集团排序置顶，其他集团按加入时间顺序排列）(只查集团不查医院)
     * @apiParam {String}    	access_token        token
     * @apiSuccess {String}  id    			集团Id
     * @apiSuccess {String}  name    		集团名
     * @apiSuccess {Boolean} isMain    		是否主集团
     * @apiSuccess {Boolean} isAdmin    	是否集团管理员
     * @apiSuccess {String}  dutyStartTime  值班开始时间
     * @apiSuccess {String}  dutyEndTime    值班结束时间
     * @apiSuccess {String}  introduction    		集团介绍
     * @apiSuccess {String}  groupIconPath    	集团头像
     * @apiSuccess {String}  skip    	集团的屏蔽状态（N：正常。 S:被屏蔽）
     * @apiSuccess {String}  certStatus     加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     * @apiSuccess {Number}  resultCode     返回状态码
     * @apiAuthor 谭永芳
     * @date 2016年6月151日18:58:59
     */
    @RequestMapping("/getMyNormalGroups")
    public JSONMessage getMyNormalGroups(GroupDoctorParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success("success", gdocService.getMyNormalGroups(param.getDoctorId()));
    }

    /**
     * @api {post} /group/doctor/getDoctorRecommendedList WEB端获取推荐医生列表
     * @apiVersion 1.0.0
     * @apiName getDoctorRecommendedList
     * @apiGroup 推荐医生
     * @apiDescription 获取推荐医生列表
     * @apiParam {String}    	access_token    token
     * @apiSuccess    {Object[]}	pageData							医生列表
     * @apiSuccess    {Integer} pageData.doctorId 					医生Id
     * @apiSuccess    {String}	pageData.headPicFileName			头像
     * @apiSuccess    {String}	pageData.name						医生名称
     * @apiSuccess    {String} pageData.hospital 					医院
     * @apiSuccess    {String}	pageData.pageData.deptName			科室名称
     * @apiSuccess    {String}	pageData.pageData.title				医生职称
     * @apiSuccess    {List[]}	pageData.pageData.company			所属集团
     * @apiSuccess    {boolean}	pageData.recommended				是否已被推荐（1：已推荐；0：未推荐）
     * @apiSuccess    {Number} pageData.resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("getDoctorRecommendedList")
    public JSONMessage getDoctorRecommendedList() {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/getDoctorRecommendedForAPP 移动端获取推荐医生列表
     * @apiVersion 1.0.0
     * @apiName getDoctorRecommendedForAPP
     * @apiGroup 推荐医生
     * @apiDescription 移动端获取推荐医生列表
     * @apiParam {String}    	access_token    token
     * @apiSuccess    {Object[]}	pageData							医生列表
     * @apiSuccess    {Integer} pageData.doctorId 					医生Id
     * @apiSuccess    {String}	pageData.headPicFileName			头像
     * @apiSuccess    {String}	pageData.name						医生名称
     * @apiSuccess    {String}	pageData.pageData.deptName			科室名称
     * @apiSuccess    {String}	pageData.pageData.title				医生职称
     * @apiSuccess    {Number} pageData.resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("getDoctorRecommendedForAPP")
    public JSONMessage getDoctorRecommendedForAPP() {
        return JSONMessage.success();
    }


    /**
     * @api {post} /group/doctor/addDoctorRecommended 添加推荐医生
     * @apiVersion 1.0.0
     * @apiName addDoctorRecommended
     * @apiGroup 推荐医生
     * @apiDescription 添加推荐医生
     * @apiParam    {String} access_token    token
     * @apiParam    {Integer}		doctorId		医生ID
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("addDoctorRecommended")
    public JSONMessage addDoctorRecommended(Integer doctorId) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/removeDoctorRecommended 移除推荐医生
     * @apiVersion 1.0.0
     * @apiName removeDoctorRecommended
     * @apiGroup 推荐医生
     * @apiDescription 移除推荐医生
     * @apiParam    {String} access_token    token
     * @apiParam    {Integer}		doctorId		医生ID
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("removeDoctorRecommended")
    public JSONMessage removeDoctorRecommended(Integer doctorId) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/searchDoctorListByName 根据医生名称或集团名称查找医生
     * @apiVersion 1.0.0
     * @apiName searchDoctorListByName
     * @apiGroup 推荐医生
     * @apiDescription 根据医生名称或集团名称查找医生
     * @apiParam    {String} access_token    token
     * @apiParam    {String}		name			查询关键字
     * @apiSuccess    {Object[]}	pageData							医生列表
     * @apiSuccess    {Integer} pageData.doctorId 					医生Id
     * @apiSuccess    {String}	pageData.headPicFileName			头像
     * @apiSuccess    {String}	pageData.name						医生名称
     * @apiSuccess    {String} pageData.hospital 					医院
     * @apiSuccess    {String}	pageData.pageData.deptName			科室名称
     * @apiSuccess    {String}	pageData.pageData.title				医生职称
     * @apiSuccess    {List[]}	pageData.pageData.company			所属集团
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("searchDoctorListByName")
    public JSONMessage searchDoctorListByName(String name) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/setDoctorToRecommended 将医生状态设为推荐状态
     * @apiVersion 1.0.0
     * @apiName setDoctorToRecommended
     * @apiGroup 推荐医生
     * @apiDescription 将医生状态设为推荐状态
     * @apiParam    {String} access_token    token
     * @apiParam    {String} doctorId		医生id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("setDoctorToRecommended")
    public JSONMessage setDoctorToRecommended(Integer doctorId) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/cancelDoctorFromRecommended 取消医生的推荐状态
     * @apiVersion 1.0.0
     * @apiName cancelDoctorFromRecommended
     * @apiGroup 推荐医生
     * @apiDescription 取消医生的推荐状态
     * @apiParam    {String} access_token    token
     * @apiParam    {String} doctorId		医生id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("cancelDoctorFromRecommended")
    public JSONMessage cancelDoctorFromRecommended(Integer doctorId) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/riseRecommendedOfDoctor 上移医生推荐排名
     * @apiVersion 1.0.0
     * @apiName riseRecommendedOfDoctor
     * @apiGroup 推荐医生
     * @apiDescription 上移医生推荐排名
     * @apiParam    {String} access_token    token
     * @apiParam    {String} doctorId		医生id
     * @apiSuccess {Number} 	resultCode    返回状态码
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("riseRecommendedOfDoctor")
    public JSONMessage riseRecommendedOfDoctor(Integer doctorId) {
        return JSONMessage.success();
    }

    /**
     * @api {post} /group/doctor/getGroupAddrBook 获取集团通讯录
     * @apiVersion 1.0.0
     * @apiName getGroupAddrBook
     * @apiGroup 推荐医生
     * @apiDescription 上移医生推荐排名
     * @apiParam    {String} access_token    token
     * @apiParam    {String} groupId		集团id
     * @apiParam    {Integer} areaCode		地区id
     * @apiParam    {String} deptId		科室id
     * @apiParam    {Integer} pageIndex		页码
     * @apiParam    {Integer} pageSize		页面大小
     * @apiSuccess {String} 	name 姓名
     * @apiSuccess {Integer} 	id 姓名
     * @apiSuccess {title}      title	职称
     * @apiAuthor liangcs
     * @date 2016年7月25日
     */
    @RequestMapping("getGroupAddrBook")
    public JSONMessage getGroupAddrBook(
            @RequestParam(name = "groupId", required = true) String groupId,
            @RequestParam(name = "areaCode", required = false) Integer areaCode,
            @RequestParam(name = "deptId", required = false) String deptId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        PageVO result = gdocService.getGroupAddrBook(groupId, areaCode, deptId, pageIndex, pageSize);
        return JSONMessage.success(result);
    }
}
