package com.dachen.health.controller.group.company;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.service.ICompanyService;
import com.dachen.util.ReqUtil;

/**
 * 
 * @author pijingwei
 *
 */
@RestController
@RequestMapping("/company")
public class CompanyController extends AbstractController {
	
	@Autowired
	private ICompanyService companyService;


	/**
     * @api {post} /company/regCompany 注册公司
     * @apiVersion 1.0.0
     * @apiName regCompany
     * @apiGroup 公司模块
     * @apiDescription 注册公司
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	name              公司名称
     * @apiParam {String}   	description       公司描述（公司说明）
     * @apiParam {String}   	corporation       法人
     * @apiParam {String}   	license           营业执照编号
     * @apiParam {String}   	bankAccount       银行账户
     * @apiParam {String}   	bankNumber     	       银行帐号
     * @apiParam {String}   	openBank          开户行
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/regCompany")
	public JSONMessage regCompany(Company company) {
		company.setStatus("A");
		company.setCreator(ReqUtil.instance.getUserId());
		company.setCreatorDate(new Date().getTime());
		company.setUpdator(ReqUtil.instance.getUserId());
		company.setUpdatorDate(new Date().getTime());
		return JSONMessage.success(null, companyService.saveCompany(company));
	}
	
	
	/**
     * @api {post} /company/applyjoinCompany 邀请码加入公司
     * @apiVersion 1.0.0
     * @apiName applyjoinCompany
     * @apiGroup 公司模块
     * @apiDescription 邀请码加入公司
     *
     * @apiParam {String}    	access_token    	token
     * @apiParam {String}   	code          		邀请码
     *
     * @apiSuccess {Object[]} 	company              						公司
     * @apiSuccess {String}  	company.name								公司名称
     * @apiSuccess {Integer}  	company.description							公司描述
     * @apiSuccess {String}  	company.corporation        					法人
     * @apiSuccess {String}  	company.license        						营业执照编号
     * @apiSuccess {String}  	company.status        						状态   状态：A：审核中，P：审核通过，S：已停用 
     * @apiSuccess {String}  	company.bankAccount        					银行账户名
     * @apiSuccess {String}  	company.bankNumber        					银行帐号
     * @apiSuccess {String}  	company.openBank        					开户行
     * @apiSuccess {Integer}  	company.creator        						创建人
     * @apiSuccess {Long}  		company.creatorDate       					创建时间
     * @apiSuccess {Integer}  	company.updator        						更新人
     * @apiSuccess {Long}  		company.updatorDate       					更新时间
     * @apiSuccess {Object[]} 	group              							集团
     * @apiSuccess {String}   	group.id           							记录Id
     * @apiSuccess {String}   	group.companyId           					所属公司Id
     * @apiSuccess {String}   	group.name       							集团名称
     * @apiSuccess {String}   	group.introduction       					集团简介
     * @apiSuccess {Integer}  	group.creator        						创建人
     * @apiSuccess {Long}  		group.creatorDate       					创建时间
     * @apiSuccess {Integer}  	group.updator        						更新人
     * @apiSuccess {Long}  		group.updatorDate       					更新时间
     * @apiParam {Object[]}  	group.config    							集团设置信息
     * @apiSuccess {boolean}  	group.config.memberInvite    				是否允许成员邀请医生加入  true：允许，false：不允许
     * @apiSuccess {boolean}  	group.config.passByAudit   					成员邀请是否需要审核  true：允许，false：不允许
     * @apiSuccess {String}   	group.config.parentProfit     				上级抽成比例
     * @apiSuccess {String}   	group.config.groupProfit     				集团抽成比例
     * @apiSuccess {Object[]}   departmentList     							当前集团科室列表
     * @apiSuccess {String}  	departmentList.id							组织Id
     * @apiSuccess {String}  	departmentList.groupId						集团Id
     * @apiSuccess {String}  	departmentList.parentId        				父节点Id
     * @apiSuccess {String}  	departmentList.name        					组织名称
     * @apiSuccess {String}  	departmentList.description        			组织描述
     * @apiSuccess {Integer}  	departmentList.creator        				创建人
     * @apiSuccess {Long}  		departmentList.creatorDate       			创建时间
     * @apiSuccess {Integer}  	departmentList.updator        				更新人
     * @apiSuccess {Long}  		departmentList.updatorDate       			更新时间
     * @apiSuccess {Object[]}  	departmentList.subList       				子科室
     * @apiSuccess {String}  	departmentList.subList.id					组织Id
     * @apiSuccess {String}  	departmentList.subList.groupId				集团Id
     * @apiSuccess {String}  	departmentList.subList.parentId        		父节点Id
     * @apiSuccess {String}  	departmentList.subList.name        			组织名称
     * @apiSuccess {String}  	departmentList.subList.description        	组织描述
     * @apiSuccess {Integer}  	departmentList.subList.creator        		创建人
     * @apiSuccess {Long}  		departmentList.subList.creatorDate       	创建时间
     * @apiSuccess {Integer}  	departmentList.subList.updator        		更新人
     * @apiSuccess {Long}  		departmentList.subList.updatorDate       	更新时间
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/applyjoinCompany")
	@Deprecated //暂时废弃
	public JSONMessage applyjoinCompany(String code) {
		return JSONMessage.success(null, companyService.addGroupByInviteCode(code));
	}
	
	
	/**
     * @api {post} /company/updateByCompany 修改公司信息
     * @apiVersion 1.0.0
     * @apiName updateByCompany
     * @apiGroup 公司模块
     * @apiDescription 修改（更新）公司信息
     *
     * @apiParam {String}    	access_token    		    token
     * @apiParam {Object[]} 	company                   	公司
     * @apiParam {String}   	company.id              	公司Id
     * @apiParam {String}   	company.name              	公司名称
     * @apiParam {String}   	company.description       	公司描述（公司说明）
     * @apiParam {String}   	company.checkRemarks       	审核备注
     * @apiParam {String}   	company.corporation       	法人
     * @apiParam {String}   	company.license           	营业执照编号
     * @apiParam {String}  		company.bankAccount        	银行账户名
     * @apiParam {String}  		company.bankNumber        	银行帐号
     * @apiParam {String}  		company.openBank        	开户行
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/updateByCompany")
	public JSONMessage updateByCompany(Company company) {
		company.setUpdator(ReqUtil.instance.getUserId());
		company.setUpdatorDate(new Date().getTime());
		return JSONMessage.success("success", companyService.updateCompany(company));
	}
	
	
	/**
     * @api {post} /company/getCompanyById 获取公司详情
     * @apiVersion 1.0.0
     * @apiName getCompanyById
     * @apiGroup 公司模块
     * @apiDescription 根据公司Id获取公司详情
     *
     * @apiParam {String}    	access_token    		    token
     * @apiParam {String}   	company.id              	公司Id
     * 
     * @apiSuccess {Object[]} 	company              		公司
     * @apiSuccess {String}  	company.name				公司名称
     * @apiSuccess {Integer}  	company.description			公司描述
     * @apiSuccess {String}  	company.corporation        	法人
     * @apiSuccess {String}  	company.license        		营业执照编号
     * @apiSuccess {String}  	company.status        		状态   状态：A：审核中，P：审核通过，S：已停用 
     * @apiSuccess {String}  	company.bankAccount        	银行账户名
     * @apiSuccess {String}  	company.bankNumber        	银行帐号
     * @apiSuccess {String}  	company.openBank        	开户行
     * @apiSuccess {Integer}  	company.creator        		创建人
     * @apiSuccess {Long}  		company.creatorDate       	创建时间
     * @apiSuccess {Integer}  	company.updator        		更新人
     * @apiSuccess {Long}  		company.updatorDate       	更新时间
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/getCompanyById")
	public JSONMessage getCompanyById(String id) {
		Company com = companyService.getCompanyById(id);
		if(null == com) {
			return JSONMessage.success("success", "找不到记录");
		}
		return JSONMessage.success(null, com);
	}
	
	
	/**
     * @api {post} /company/searchByCompany 获取公司列表
     * @apiVersion 1.0.0
     * @apiName searchByCompany
     * @apiGroup 公司模块
     * @apiDescription 获取所有公司列表，可根据条件搜索查询公司信息
     *
     * @apiParam {String}    	access_token    		  token
     * @apiParam {Object[]} 	company                   公司
     * @apiParam {String}   	company.name              公司名称
     * @apiParam {String}   	company.description       公司描述（公司说明）
     * @apiParam {String}   	company.corporation       法人
     * @apiParam {String}   	company.license           营业执照编号
     * @apiParam {String}   	company.status         	       状态   A：审核中，P：审核通过，S：已停用（已冻结）
     * 
     * @apiSuccess {Object[]} 	company              		公司
     * @apiSuccess {String}  	company.name				公司名称
     * @apiSuccess {Integer}  	company.description			公司描述
     * @apiSuccess {String}  	company.corporation        	法人
     * @apiSuccess {String}  	company.license        		营业执照编号
     * @apiSuccess {String}  	company.status        		状态   状态：A：审核中，P：审核通过，S：已停用 
     * @apiSuccess {Integer}  	company.creator        		创建人
     * @apiSuccess {Long}  		company.creatorDate       	创建时间
     * @apiSuccess {Integer}  	company.updator        		更新人
     * @apiSuccess {Long}  		company.updatorDate       	更新时间
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/searchByCompany")
	@Deprecated //业务有问题，暂时废弃
	public JSONMessage searchByCompany(CompanyParam company) {
		return JSONMessage.success(null, companyService.searchCompany(company));
	}
	
	
	
}
