package com.dachen.health.controller.group.group;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.GroupEnum.GroupUserStatus;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.util.ReqUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
@RestController
@RequestMapping("/group/user")
public class GroupUserController extends AbstractController {

	@Autowired
	private ICompanyUserService cuserService;
	
	@Autowired
	private IGroupFacadeService groupFacadeService;
	
	
	
	/**
     * @api {post} /group/user/addGroupUser 添加集团管理员
     * @apiVersion 1.0.0
     * @apiName addGroupUser
     * @apiGroup 管理员
     * @apiDescription 添加集团管理员
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam {Integer}   	doctorId       		医生Id
     * @apiParam {String}   	objectId			集团Id
     * @apiParam {Integer}   	againInvite         再次邀请参数，如果是再次邀请传：1
     * @apiParam {String}   	telephone			手机号码
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  wangqiao 重构
     * @date 2016年4月20日 
	 */
	@RequestMapping("/addGroupUser")
	public JSONMessage addUserByGroup(Integer doctorId,String objectId,Integer againInvite , String telephone) throws HttpApiException {
//        guser.setType(GroupUserType.集团用户.getIndex());
//        guser.setStatus(GroupUserStatus.邀请待通过.getIndex());
//        guser.setCreator(ReqUtil.instance.getUserId());
//        guser.setCreatorDate(new Date().getTime());
//        guser.setUpdator(ReqUtil.instance.getUserId());
//        guser.setUpdatorDate(new Date().getTime());
//		return JSONMessage.success(null, cuserService.saveCompanyUser(guser));
		
		return JSONMessage.success(null, cuserService.inviteJoinGroupManage(doctorId,objectId,ReqUtil.instance.getUserId(),againInvite));
	}
	
	/**
     * @api {post} /group/user/confirmByJoin 确认加入
     * @apiVersion 1.0.0
     * @apiName confirmByJoin
     * @apiGroup 管理员
     * @apiDescription 确认加入（管理员，医生确认加入都可以）
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String}   	id       			记录Id
     * @apiParam  {Integer}   	type				类型：1 = 公司管理员，2 = 集团管理员，3 = 医生集团成员
     * @apiParam  {String}   	status				状态：C，同意，N，拒绝
     *
     * @apiSuccess {Number} 	resultCode    		返回状态码
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/confirmByJoin")
	public JSONMessage confirmByJoin(String id,Integer type,String status) throws HttpApiException {
		//读取操作人员id
		Integer operationUserId = ReqUtil.instance.getUserId();
		
		return JSONMessage.success("success", groupFacadeService.confirmByInvite(id,type,status,operationUserId));
	}
	
	/**
     * @api {post} /group/user/comfirmJoinAndSetMain 确认加入并设置为主集团（针对博德嘉联）
     * @apiVersion 1.0.0
     * @apiName comfirmJoinAndSetMain
     * @apiGroup 管理员
     * @apiDescription 确认加入并设置为主集团
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String}   	groupId       		集团id
     *
     * @apiSuccess {Number} 	resultCode    		返回状态码
     *
     * @apiAuthor  fuyongde
     * @date 2016年6月22日
	 */
	@RequestMapping("/comfirmJoinAndSetMain")
	public JSONMessage comfirmJoinAndSetMain(
			@RequestParam(name="groupId", required=true) String groupId
	) throws HttpApiException {
		//查询最新的一条申请的记录
		groupFacadeService.confirmByInviteAndSetMain(groupId);
		return JSONMessage.success();
	}
	
	
	/**
     * @api {post} /group/user/searchByGroup 查询集团管理员列表
     * @apiVersion 1.0.0
     * @apiName searchByGroup
     * @apiGroup 管理员
     * @apiDescription 根据条件获取所有集团管理员列表
     *
     * @apiParam  {String}    	access_token        	token
     * @apiParam  {String}   	objectId				集团Id
     *
     * @apiSuccess {String}  	id						记录Id
     * @apiSuccess {Integer}  	doctorId				医生Id
     * @apiSuccess {String}  	objectId        		集团Id
     * @apiSuccess {Integer}  	type        			帐号类型   1：公司   2：集团
     * @apiSuccess {String}  	status        			帐号状态   I：邀请待通过，C：正常使用， S：已离职
     * @apiSuccess {Integer}  	creator        			创建人
     * @apiSuccess {Long}  		creatorDate       		创建时间
     * @apiSuccess {Integer}  	updator        			更新人
     * @apiSuccess {Long}  		updatorDate       		更新时间
     * @apiSuccess {String}  	rootAdmin       		root:超级管理员，admin：普通管理员
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/searchByGroup")
	public JSONMessage searchByGroup(String  objectId , Integer pageIndex , Integer pageSize) {
		
		//初始化查询参数
		GroupUserParam guser = new GroupUserParam();
		guser.setObjectId(objectId);
		//只查询集团管理员
		guser.setType(2);
		//只查询 正常状态的管理员
		guser.setStatus(GroupUserStatus.正常使用.getIndex());
		//分页参数
		if(pageIndex != null && pageIndex != 0){
			guser.setPageIndex(pageIndex);
		}
		if(pageSize != null && pageSize != 0){
			guser.setPageSize(pageSize);
		}
		
		return JSONMessage.success(null, cuserService.searchCompanyUser(guser));
	}
	
	
	/**
     * @api {post} /group/user/deleteByGroupUser 删除集团管理员
     * @apiVersion 1.0.0
     * @apiName deleteByGroupUser
     * @apiGroup 管理员
     * @apiDescription 根据Id删除集团管理员
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String[]}   	ids       			记录Id
     *
     * @apiSuccess {Number} 	resultCode    		返回状态码
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/deleteByGroupUser")
	public JSONMessage deleteByGroupUser(String[]ids) {
		cuserService.deleteGroupUserById(ids);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /group/user/verifyByGuser 验证集团账户
     * @apiVersion 1.0.0
     * @apiName verifyByGuser
     * @apiGroup 管理员
     * @apiDescription 验证集团账户是否可用
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam  {Integer}   	doctorId          	医生Id
     * @apiParam  {String}   	groupId          	想要进入的集团Id （如果该医生未加入任何集团，不传这个参数）
     *
     * @apiSuccess {String}  	id					记录Id
     * @apiSuccess {Integer}  	doctorId			医生Id
     * @apiSuccess {String}  	objectId        	集团Id
     * @apiSuccess {String}  	type        		帐号类型   1：公司   2：集团
     * @apiSuccess {String}  	status        		帐号状态   I：邀请待通过，C：正常使用， S：已离职
     * @apiSuccess {Integer}  	creator        		创建人
     * @apiSuccess {String}		creatorName			创建人名字
     * @apiSuccess {Long}  		creatorDate       	创建时间
     * @apiSuccess {Integer}  	updator        		更新人
     * @apiSuccess {Long}  		updatorDate       	更新时间
     * @apiSuccess {String}  	group.certStatus    集团认证状态：NC未认证、NP未通过、A待审核、P已通过
     * @apiSuccess {Integer}  	userStatus    		用户状态 1:未加入集团 2：加入集团非管理员 3:管理员 4:加入集团非管理员且未认证
     *
     * @apiAuthor  王峭
     * @date 2015年12月29日
	 */
	@RequestMapping("/verifyByGuser")
	public JSONMessage verifyByGuser(Integer doctorId,String groupId) {
//		cuser.setType(2);
		return JSONMessage.success(null, cuserService.getLoginByGroupUser(doctorId,groupId));
	}
	
	/**
     * @api {post|get} /group/user/isAdminOfGroup 验证用户是否为某一集团的管理员
     * @apiVersion 1.0.0
     * @apiName isAdminOfGroup
     * @apiGroup 管理员
     * @apiDescription 验证用户是否为某一集团的管理员
     *
     * @apiParam  {String}    	access_token        token
     * @apiSuccess {boolen}  	data				true表示该用户是某一个集团的管理员，false表示该用户不是任何一个集团的管理员
     *
     * @apiAuthor  傅永德
     * @date 2016年6月24日
	 */
	@RequestMapping("/isAdminOfGroup")
	public JSONMessage isAdminForGroup() {
		return JSONMessage.success(cuserService.isAdminOfGroup());
	}
	
	/**
     * @api {post} /group/user/getInviteStatus  获取邀请的状态
     * @apiVersion 1.0.0
     * @apiName getInviteStatus
     * @apiGroup 集团医生
     * @apiDescription 获取邀请的状态
     *
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String}   	id       			记录Id
     * @apiParam  {Integer}   	type				类型：1，公司管理员，2，集团管理员，3，医生集团成员
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
     * @apiSuccess {String}  	status        		邀请状态    C：已同意   N：已拒绝    I：邀请待确认  X：邀请已过期
     * @apiSuccess {String}  	name          集团/公司名称（邀请过期时，不返回该数据）
     * 
     * @apiAuthor  王峭 
     * @date 2016年1月8日
	 */
	@RequestMapping("/getInviteStatus")
	public JSONMessage getInviteStatus(String id,Integer type) {
		return JSONMessage.success("success", cuserService.getInviteStatus(id,type));
	}
	
}
