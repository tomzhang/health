package com.dachen.health.controller.group.group;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.group.entity.param.GroupCertParam;
import com.dachen.health.group.group.entity.po.GroupCertification;
import com.dachen.health.group.group.service.IGroupCertService;

@RestController
@RequestMapping("/group/cert")
public class GroupCertController extends AbstractController {
	
	@Resource
	private IGroupCertService certService;

	/**
     * @api {post} /group/cert/submitCert 提交认证
     * @apiVersion 1.0.0
     * @apiName submitCert
     * @apiGroup 集团认证
     * @apiDescription 集团提交公司认证资料
     *
     * @apiParam  {String}    	access_token    token
     * @apiParam  {String}   	groupId         集团Id
     * @apiParam  {Integer}   	doctorId        医生Id
     * @apiParam  {String}   	companyName     公司全称
     * @apiParam  {String}   	orgCode         组织机构代码
     * @apiParam  {String}   	license         工商执照注册号
     * @apiParam  {String}   	corporation     法定代表人
     * @apiParam  {String}   	businessScope   经营范围
     * @apiParam  {String}   	accountName     公司开户名称
     * @apiParam  {String}   	openBank        公司开户银行
     * @apiParam  {String}   	bankAcct        公司银行账号
     * @apiParam  {String}   	adminName       管理者姓名
     * @apiParam  {String}   	idNo         	管理者身份证号码
     * @apiParam  {String}   	adminTel        管理者联系电话
     * @apiParam  {String}   	idImage 		管理者证件照片
     * @apiParam  {String}   	orgCodeImage 	组织机构代码证
     * @apiParam  {String}   	licenseImage 	企业工商营业执照
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/submitCert")
	public JSONMessage submitCert(String groupId, Integer doctorId, GroupCertification certInfo) {
		Object data = certService.submitCert(groupId, doctorId, certInfo);
		return JSONMessage.success("success", data);
	}
	
	/**
     * @api {post} /group/cert/getGroupCert 获取集团及认证资料
     * @apiVersion 1.0.0
     * @apiName getGroupCert
     * @apiGroup 集团认证
     * @apiDescription 获取集团及认证资料
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId         		     集团Id
     * 
     * @apiSuccess	{String}	name					     集团名称
     * @apiSuccess	{String}	introduction			     集团简介
     * @apiSuccess	{String}	groupIconPath			     集团图标
     * @apiSuccess	{String}	diseaseName			               集团擅长病种
     * @apiSuccess  {String}   	groupCert.companyName     公司全称
     * @apiSuccess  {String}   	groupCert.orgCode         组织机构代码
     * @apiSuccess  {String}   	groupCert.license         工商执照注册号
     * @apiSuccess  {String}   	groupCert.corporation     法定代表人
     * @apiSuccess  {String}   	groupCert.businessScope   经营范围
     * @apiSuccess  {String}   	groupCert.accountName     公司开户名称
     * @apiSuccess  {String}   	groupCert.openBank        公司开户银行
     * @apiSuccess  {String}   	groupCert.bankAcct        公司银行账号
     * @apiSuccess  {String}   	groupCert.adminName       管理者姓名
     * @apiSuccess  {String}   	groupCert.idNo         	     管理者身份证号码
     * @apiSuccess  {String}   	groupCert.adminTel        管理者联系电话
     * @apiSuccess  {String}   	groupCert.idImage 		     管理者证件照片
     * @apiSuccess  {String}   	groupCert.orgCodeImage 	     组织机构代码证
     * @apiSuccess  {String}   	groupCert.licenseImage 	     企业工商营业执照
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/getGroupCert")
	public JSONMessage getGroupCert(String groupId) {
		Object data = certService.getGroupCert(groupId);
		return JSONMessage.success("success", data);
	}
	
	/**
     * @api {post} /group/cert/getGroupCerts 获取集团及认证资料集合
     * @apiVersion 1.0.0
     * @apiName getGroupCerts
     * @apiGroup 集团认证
     * @apiDescription 获取集团及认证资料
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	status				   	     认证状态：A待审核、P已通过、NP未通过
     * @apiParam  {String}   	keyword				   	     搜索关键字（不用搜索时可传null或""）
     * @apiParam  {Integer}   	pageIndex                 查询页，从0开始
     * @apiParam  {Integer}   	pageSize                  每页显示条数，不传默认15条
     * 
     * @apiSuccess	{String}	id					               集团Id
     * @apiSuccess	{String}	name					     集团名称
     * @apiSuccess	{String}	introduction			     集团简介
     * @apiSuccess	{String}	groupIconPath			     集团图标
     * @apiSuccess	{String}	diseaseName			               集团擅长病种
     * @apiSuccess  {Long}   	processVTime 	     	  +v认证时间
     * @apiSuccess  {String}   	groupCert.companyName     公司全称
     * @apiSuccess  {String}   	groupCert.orgCode         组织机构代码
     * @apiSuccess  {String}   	groupCert.license         工商执照注册号
     * @apiSuccess  {String}   	groupCert.corporation     法定代表人
     * @apiSuccess  {String}   	groupCert.businessScope   经营范围
     * @apiSuccess  {String}   	groupCert.accountName     公司开户名称
     * @apiSuccess  {String}   	groupCert.openBank        公司开户银行
     * @apiSuccess  {String}   	groupCert.bankAcct        公司银行账号
     * @apiSuccess  {String}   	groupCert.adminName       管理者姓名
     * @apiSuccess  {String}   	groupCert.idNo         	     管理者身份证号码
     * @apiSuccess  {String}   	groupCert.adminTel        管理者联系电话
     * @apiSuccess  {String}   	groupCert.idImage 		     管理者证件照片
     * @apiSuccess  {String}   	groupCert.orgCodeImage 	     组织机构代码证
     * @apiSuccess  {String}   	groupCert.licenseImage 	     企业工商营业执照
     * @apiSuccess  {String}   	otherGroupCount 	               其他认证医生集团数量
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/getGroupCerts")
	public JSONMessage getGroupCerts(GroupCertParam param) {
		Object data = certService.getGroupCerts(param);
		return JSONMessage.success("success", data);
	}
	
	/**
     * @api {post} /group/cert/getOtherGroupCerts 获取其他认证医生集团集合
     * @apiVersion 1.0.0
     * @apiName getOtherGroupCerts
     * @apiGroup 集团认证
     * @apiDescription 获取其他认证医生集团集合
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId				               集团Id
     * @apiParam  {Integer}   	pageIndex                 查询页，从0开始
     * @apiParam  {Integer}   	pageSize                  每页显示条数，不传默认15条
     * 
     * @apiSuccess	{String}	name					     集团名称
     * @apiSuccess	{String}	introduction			     集团简介
     * @apiSuccess	{String}	groupIconPath			     集团图标
     * @apiSuccess	{String}	diseaseName			               集团擅长病种
     * @apiSuccess  {String}   	groupCert.companyName     公司全称
     * @apiSuccess  {String}   	groupCert.orgCode         组织机构代码
     * @apiSuccess  {String}   	groupCert.license         工商执照注册号
     * @apiSuccess  {String}   	groupCert.corporation     法定代表人
     * @apiSuccess  {String}   	groupCert.businessScope   经营范围
     * @apiSuccess  {String}   	groupCert.accountName     公司开户名称
     * @apiSuccess  {String}   	groupCert.openBank        公司开户银行
     * @apiSuccess  {String}   	groupCert.bankAcct        公司银行账号
     * @apiSuccess  {String}   	groupCert.adminName       管理者姓名
     * @apiSuccess  {String}   	groupCert.idNo         	     管理者身份证号码
     * @apiSuccess  {String}   	groupCert.adminTel        管理者联系电话
     * @apiSuccess  {String}   	groupCert.idImage 		     管理者证件照片
     * @apiSuccess  {String}   	groupCert.orgCodeImage 	     组织机构代码证
     * @apiSuccess  {String}   	groupCert.licenseImage 	     企业工商营业执照
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/getOtherGroupCerts")
	public JSONMessage getOtherGroupCerts(GroupCertParam param) {
		Object data = certService.getOtherGroupCerts(param);
		return JSONMessage.success("success", data);
	}
	
	
	/**
     * @api {post} /group/cert/updateRemarks 修改备注
     * @apiVersion 1.0.0
     * @apiName updateRemarks
     * @apiGroup 集团认证
     * @apiDescription 修改备注
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId         		     集团Id
     * @apiParam  {String}   	remarks				   	     备注
     * 
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/updateRemarks")
	public JSONMessage updateRemarks(String groupId, String remarks) {
		Object data = certService.updateRemarks(groupId, remarks);
		return JSONMessage.success("success", data);
	}
	
	/**
     * @api {post} /group/cert/passCert 认证通过
     * @apiVersion 1.0.0
     * @apiName passCert
     * @apiGroup 集团认证
     * @apiDescription 认证通过
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId         		     集团Id
     * 
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/passCert")
	public JSONMessage passCert(String groupId) {
		Object data = certService.passCert(groupId);
		return JSONMessage.success("success", data);
	}
	
	/**
     * @api {post} /group/cert/noPass 认证不通过
     * @apiVersion 1.0.0
     * @apiName noPass
     * @apiGroup 集团认证
     * @apiDescription 认证不通过
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId         		     集团Id
     * @apiParam  {String}   	remarks				   	     备注
     * 
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/noPass")
	public JSONMessage noPass(String groupId, String remarks) {
		certService.noPass(groupId);
		certService.updateRemarks(groupId, remarks);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /group/cert/getGroups 获取集团集合
     * @apiVersion 1.0.0
     * @apiName getGroups
     * @apiGroup 集团认证
     * @apiDescription 根据公司Id获取集团集合
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	companyId         		     公司Id
     * 
     * @apiSuccess {com.dachen.health.group.group.entity.po.Group} group 集团对象
     * 
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	public JSONMessage getGroups(String companyId) {
		Object data = certService.getGroups(companyId);
		return JSONMessage.success("success", data);
	}
	
}
