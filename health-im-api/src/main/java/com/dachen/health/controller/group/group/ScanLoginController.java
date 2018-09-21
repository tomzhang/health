package com.dachen.health.controller.group.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.common.entity.vo.QrCodeInfo;
import com.dachen.health.group.common.service.QrCodeLoginService;

/**
 * 扫描二维码的登录
 * @author pijingwei
 * @date 2015/8/19
 */
@RestController
@RequestMapping("/scan/login")
public class ScanLoginController {

	@Autowired
	private QrCodeLoginService codeService;
	
	/**
     * @api {post} /upload/scanLoginQrCodeServlet 获取二维码
     * @apiVersion 1.0.0
     * @apiName scanLoginQrCodeServlet
     * @apiGroup 二维码登录
     * @apiDescription 扫码登录
     *
     * @apiSuccess {String}   	url           		二维码地址
     * @apiSuccess {String}   	uuid       			uuid
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月13日
	 */
	public void scanLoginQrCodeServlet() {}
	
	/**
     * @api {post} /scan/login/verifyLogin 扫码登录
     * @apiVersion 1.0.0
     * @apiName verifyLogin
     * @apiGroup 二维码登录
     * @apiDescription 扫码登录
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	uuid           		uuid
     * @apiParam {Integer}   	doctorId       		医生Id
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月13日
	 */
	@RequestMapping("/verifyLogin")
	@Deprecated //业务不正确，暂时废弃， add by wangqiao
	public JSONMessage verifyLogin(QrCodeInfo codeInfo) {
		codeService.verifyLogin(codeInfo);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /scan/login/scanLoginCheck 检测登录状态
     * @apiVersion 1.0.0
     * @apiName scanLoginCheck
     * @apiGroup 二维码登录
     * @apiDescription 扫描二维码登录，检测登录状态
     *
     * @apiParam {String}    	access_token    			token
     * @apiParam {String}   	uuid          				uuid
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
	@RequestMapping("/scanLoginCheck")
	@Deprecated //业务不正确，暂时废弃， add by wangqiao
	public JSONMessage scanLoginCheck(String uuid) {
		return JSONMessage.success("success", codeService.getDoctorInfoByCheck(uuid));
	}
	
}
