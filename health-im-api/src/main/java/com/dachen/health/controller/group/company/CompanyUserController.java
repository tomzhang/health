package com.dachen.health.controller.group.company;

import java.util.Date;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.util.ReqUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/12
 */
@RestController
@RequestMapping("/company/user")
public class CompanyUserController extends AbstractController {

	@Autowired
	private ICompanyUserService cuserService;
	
	
	/**
     * @api {post} /company/user/addCompanyUser 添加公司管理员
     * @apiVersion 1.0.0
     * @apiName addCompanyUser
     * @apiGroup 管理员
     * @apiDescription 注册公司用户
     *
     * @apiParam {String}    	access_token    	token
     * @apiParam {String}   	doctorId       		医生Id
     * @apiParam {String}   	objectId			公司Id
     * @apiParam {String}   	againInvite         再次邀请参数，如果是再次邀请传：1
     * @apiParam {String}   	telephone			手机号码
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/addCompanyUser")
	@Deprecated //暂时没有相关业务
	public JSONMessage addCompanyUser(GroupUserParam cuser) throws HttpApiException {
        cuser.setType(1);
		cuser.setStatus("I");
		cuser.setCreator(ReqUtil.instance.getUserId());
		cuser.setCreatorDate(new Date().getTime());
		cuser.setUpdator(ReqUtil.instance.getUserId());
		cuser.setUpdatorDate(new Date().getTime());
		return JSONMessage.success(null, cuserService.saveCompanyUser(cuser));
	}
	
	
	/**
     * @api {post} /company/user/searchByCompanyUser 查询公司管理员列表
     * @apiVersion 1.0.0
     * @apiName searchByCompanyUser
     * @apiGroup 管理员
     * @apiDescription 根据条件获取所有公司管理员列表
     *
     * @apiParam {String}    	access_token    		token
     * @apiParam {Object[]} 	cuser                   公司
     * @apiParam {String}   	cuser.objectId       	公司Id
     * @apiParam {String}  		cuser.status        	帐号状态   I：邀请待通过，C：正常使用， S：已离职
     *
     * @apiSuccess {Object[]} 	cuser              		公司用户
     * @apiSuccess {String}  	cuser.id				记录Id
     * @apiSuccess {Integer}  	cuser.doctorId			医生Id
     * @apiSuccess {String}  	cuser.objectId        	公司Id
     * @apiSuccess {String}  	cuser.type        		帐号类型   1：公司   2：集团
     * @apiSuccess {String}  	cuser.status        	帐号状态   I：邀请待通过，C：正常使用， S：已离职
     * @apiSuccess {Integer}  	cuser.creator        	创建人
     * @apiSuccess {Long}  		cuser.creatorDate       创建时间
     * @apiSuccess {Integer}  	cuser.updator        	更新人
     * @apiSuccess {Long}  		cuser.updatorDate       更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/searchByCompanyUser")
	@Deprecated //暂时没有相关业务
	public JSONMessage searchByCompanyUser(GroupUserParam cuser) {
		cuser.setType(1);
		return JSONMessage.success(null, cuserService.searchCompanyUser(cuser));
	}
	
	/**
     * @api {post} /company/user/deleteByCompanyUser 删除公司管理员
     * @apiVersion 1.0.0
     * @apiName deleteByCompanyUser
     * @apiGroup 管理员
     * @apiDescription 根据Id删除管理员
     *
     * @apiParam {String}    	access_token    token
     * @apiParam {String[]}   	ids       		记录Id
     *
     * @apiSuccess {Number} resultCode    返回状态码
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@Deprecated //暂时没有相关业务
	@RequestMapping("/deleteByCompanyUser")
	public JSONMessage deleteByCompanyUser(String[] ids) {
		cuserService.deleteGroupUserById(ids);
		return JSONMessage.success("success");
	}
	
	
	/**
     * @api {post} /company/user/companyLogin 公司用户登录入口
     * @apiVersion 1.0.0
     * @apiName companyLogin
     * @apiGroup 管理员
     * @apiDescription 公司用户登录入口
     *
     * @apiParam {String}   	telephone          			用户名
     * @apiParam {String}   	password          			密码
     * 
     * @apiSuccess {Object[]} 	company              		公司用户
     * @apiSuccess {String}  	company.id					记录Id
     * @apiSuccess {Integer}  	company.doctorId			医生Id
     * @apiSuccess {String}  	company.objectId        	公司Id
     * @apiSuccess {String}  	company.type        		帐号类型   1：公司   2：集团
     * @apiSuccess {String}  	company.status        		帐号状态   I：邀请待通过，C：正常使用， S：已离职
     * @apiSuccess {Integer}  	company.creator        		创建人
     * @apiSuccess {Long}  		company.creatorDate       	创建时间
     * @apiSuccess {Integer}  	company.updator        		更新人
     * @apiSuccess {Long}  		company.updatorDate       	更新时间
     * @apiSuccess {Object[]}  	companyUser       			公司用户信息
     * @apiSuccess {String}  	companyUser.id       		记录Id
     * @apiSuccess {Integer}  	companyUser.doctorId        医生Id
     * @apiSuccess {String}  	companyUser.objectId       	公司或集团Id，当type为：1，是公司Id，type为：2，是集团Id
     * @apiSuccess {Integer}  	companyUser.type       		用户类型：1，公司用户，2：集团用户
     * @apiSuccess {Integer}  	companyUser.creator        	创建人
     * @apiSuccess {Long}  		companyUser.creatorDate     创建时间
     * @apiSuccess {Integer}  	companyUser.updator        	更新人
     * @apiSuccess {Long}  		companyUser.updatorDate     更新时间
     * @apiSuccess {Object[]}  	groupList       			当前公司下的集团列表
     * @apiSuccess {String}  	groupList.id       			集团Id
     * @apiSuccess {String}  	groupList.companyId       	所属公司Id
     * @apiSuccess {String}  	groupList.name       		集团名称
     * @apiSuccess {String}  	groupList.introduction      集团介绍
     * @apiSuccess {Integer}  	groupList.creator        	创建人
     * @apiSuccess {Long}  		groupList.creatorDate       创建时间
     * @apiSuccess {Integer}  	groupList.updator        	更新人
     * @apiSuccess {Long}  		groupList.updatorDate       更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/companyLogin")
	@Deprecated //暂时没有相关业务
	public JSONMessage companyLogin(String telephone, String password) {
		return JSONMessage.success(null, cuserService.companyLogin(telephone, password));
	}
	
	/*
	/**
     * @api {post} /company/user/verifyByCuser 验证公司账户
     * @apiVersion 1.0.0
     * @apiName verifyByCuser
     * @apiGroup 管理员
     * @apiDescription 验证公司账户是否可用
     *
     * @apiParam {String}    	access_token    		token
     * @apiParam {String}   	doctorId          医生Id
     * 
     * @apiSuccess {Object[]} 	company              		公司用户
     * @apiSuccess {String}  	company.id					记录Id
     * @apiSuccess {Integer}  	company.doctorId			医生Id
     * @apiSuccess {String}  	company.objectId        	公司Id
     * @apiSuccess {String}  	company.type        		帐号类型   1：公司   2：集团
     * @apiSuccess {String}  	company.status        		帐号状态   I：邀请待通过，C：正常使用， S：已离职
     * @apiSuccess {Integer}  	company.creator        		创建人
     * @apiSuccess {Long}  		company.creatorDate       	创建时间
     * @apiSuccess {Integer}  	company.updator        		更新人
     * @apiSuccess {Long}  		company.updatorDate       	更新时间
     * @apiSuccess {Object[]}  	companyUser       			公司用户信息
     * @apiSuccess {String}  	companyUser.id       		记录Id
     * @apiSuccess {Integer}  	companyUser.doctorId        医生Id
     * @apiSuccess {String}  	companyUser.objectId       	公司或集团Id，当type为：1，是公司Id，type为：2，是集团Id
     * @apiSuccess {Integer}  	companyUser.type       		用户类型：1，公司用户，2：集团用户
     * @apiSuccess {Integer}  	companyUser.creator        	创建人
     * @apiSuccess {Long}  		companyUser.creatorDate     创建时间
     * @apiSuccess {Integer}  	companyUser.updator        	更新人
     * @apiSuccess {Long}  		companyUser.updatorDate     更新时间
     * @apiSuccess {Object[]}  	groupList       			当前公司下的集团列表
     * @apiSuccess {String}  	groupList.id       			集团Id
     * @apiSuccess {String}  	groupList.companyId       	所属公司Id
     * @apiSuccess {String}  	groupList.name       		集团名称
     * @apiSuccess {String}  	groupList.introduction      集团介绍
     * @apiSuccess {Integer}  	groupList.creator        	创建人
     * @apiSuccess {Long}  		groupList.creatorDate       创建时间
     * @apiSuccess {Integer}  	groupList.updator        	更新人
     * @apiSuccess {Long}  		groupList.updatorDate       更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 
	@RequestMapping("/verifyByCuser")
	public JSONMessage verifyByCuser(GroupUserVO cuser) {
		cuser.setType(1);
		return JSONMessage.success(null, cuserService.getLoginByCompanyUser(cuser));
	}
	*/
}
