package com.dachen.health.controller.search;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.group.department.entity.vo.OnlineDocByGorupVO;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.vo.GroupDoctorInfoVO;
import com.dachen.health.group.group.service.IGroupSearchService;
import com.dachen.health.pack.pack.entity.param.PackDoctorParam;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.stat.service.IDoctorStatService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProjectName： health-im-api<br>
 * ClassName： SearchController<br>
 * Description： 患者端医生集团和医生搜索controller<br>
 * 
 * @author fanp
 * @createTime 2015年9月25日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/groupSearch")
public class GroupSearchController {

    @Autowired
    private IGroupSearchService groupSearchService;

    @Autowired
    private IPackService packService;
    
    @Autowired
    private IDoctorStatService docStatService;

	@Autowired
	private IPackDoctorService iPackDoctorServiceImpl;

    /**
     * @api {get} /groupSearch/findAllGroup 获取全部医生集团
     * @apiVersion 1.0.0
     * @apiName findAllGroup
     * @apiGroup 医生集团搜索
     * @apiDescription 获取全部医生集团
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     *
     * @apiSuccess {Integer}    groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     certPath                    集团头像
     * @apiSuccess {String}     introduction                集团介绍
     * @apiSuccess {String}     disease                     擅长病种
     * @apiSuccess {Integer}    expertNum                   专家数量
     * @apiSuccess {Integer}    cureNum                     问诊数量
     * @apiSuccess {String}    certStatus                  加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     *
     * @apiAuthor  范鹏
     * @date 2015年9月25日
     */
   @RequestMapping("/findAllGroup")
    public JSONMessage findAllGroup(GroupSearchParam param) {
        return JSONMessage.success(null,groupSearchService.findAllGroup(param));
    }
    
    /**
     * @api {get} /groupSearch/findGroupByKeyWord 搜索医生集团（集团名／医生名／病种 ）
     * @apiVersion 1.0.0
     * @apiName findeGroupByKeyWord
     * @apiGroup 医生集团搜索
     * @apiDescription 搜索医生集团（集团名／医生名／病种 ）
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {String}     keyword                     关键字
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     * @apiParam   {Integer}    pageSize                    每页显示条数，不传默认15条
     *
     * @apiSuccess {Integer}    groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     certPath                    集团头像
     * @apiSuccess {String}     introduction                集团介绍
     * @apiSuccess {String}     disease                     擅长病种
     * @apiSuccess {Integer}    expertNum                   专家数量
     * @apiSuccess {Integer}    cureNum                     问诊数量
     * @apiSuccess {String}    certStatus                  加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     *
     * @apiAuthor  范鹏
     * @date 2015年9月25日
     */

    @RequestMapping("/findGroupByKeyWord")
    public JSONMessage findeGroupByKeyWord(GroupSearchParam param) {
        return JSONMessage.success(null,groupSearchService.findeGroupByKeyWord(param));
    }
    
    
    
    /**
     * @api {get} /groupSearch/findDoctoreByKeyWord 搜索医生（集团名／医生名／病种 ）
     * @apiVersion 1.0.0
     * @apiName findDoctoreByKeyWord
     * @apiGroup 医生集团搜索
     * @apiDescription 搜索医生（集团名／医生名／病种 ）
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {String}     groupId                     集团ID
     * @apiParam   {String}     keyword                     关键字
     * @apiParam   {String}     lat                         纬度
     * @apiParam   {String}     lng                         经度
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     * @apiParam   {Integer}    pageSize                   页面大小
     *
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * @apiSuccess {String} 	lng 	   				经度
	 * @apiSuccess {String} 	lat 	   				纬度
     * @apiAuthor  张垠
     * @date 2015年9月25日
     */
    @RequestMapping("/findDoctoreByKeyWord")
    public JSONMessage findDoctoreByKeyWord(GroupSearchParam param) {
    	param.setSearchType("kId");
        return JSONMessage.success(null,docStatService.searchDoctorByKeyWord(param));
    }
    
    /**
     * @api {get} /groupSearch/findGroupByDisease 根据病种搜索医生集团
     * @apiVersion 1.0.0
     * @apiName findGroupByDisease
     * @apiGroup 医生集团搜索
     * @apiDescription 根据病种搜索医生集团
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     * @apiParam   {Integer}    pageSize                    每页显示条数，不传默认15条
     * @apiParam   {String}     diseaseId                   病种id
     *
     * @apiSuccess {Integer}    groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     certPath                    集团头像集团介绍
     * @apiSuccess {String}     introduction                集团简介
     * @apiSuccess {String}     disease                     擅长病种
     * @apiSuccess {Integer}    expertNum                   专家数量
     * @apiSuccess {Integer}    cureNum                     问诊数量
     *
     * @apiAuthor  范鹏
     * @date 2015年9月25日
     */
    @RequestMapping("/findGroupByDisease")
    public JSONMessage findGroupByDisease(GroupSearchParam param) {
        return JSONMessage.success(null,groupSearchService.findGroupByDisease(param));
    }
    
    /**
     * @api {get} /groupSearch/findDoctorByDisease 根据病种搜索医生
     * @apiVersion 1.0.0
     * @apiName findDoctorByDisease
     * @apiGroup 医生集团搜索
     * @apiDescription 根据病种搜索医生
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     * @apiParam   {String}     diseaseId                   病种id
     *
     * @apiSuccess {Integer}    doctorId                    医生id
     * @apiSuccess {String}     doctorName                  医生名字
     * @apiSuccess {String}     headPicFileName             头像
     * @apiSuccess {String}     groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    disease                     擅长病种
     * @apiSuccess {String}     skill			                        医生擅长介绍
     *
     * @apiAuthor  范鹏
     * @date 2015年9月25日
     */
    @RequestMapping("/findDoctorByDisease")
    public JSONMessage findDoctorByDisease(GroupSearchParam param) {
//        return JSONMessage.success(null,groupSearchService.findDoctorByDisease(param));
        return JSONMessage.success(null,docStatService.searchDoctorByDiseaseId(param));
    }
    
    
    /**
     * @api {get} /groupSearch/findDoctorByDiseaseType 根据病种搜索医生
     * @apiVersion 1.0.0
     * @apiName findDoctorByDiseaseType
     * @apiGroup 医生集团搜索
     * @apiDescription 根据病种搜索医生（支持点击上级病种查询出属于下级的所有医生）
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {String}     diseaseId                   病种id
     *
     * @apiSuccess {Integer}    doctorId                    医生id
     * @apiSuccess {String}     doctorName                  医生名字
     * @apiSuccess {String}     headPicFileName             头像
     * @apiSuccess {String}     groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    disease                     擅长病种
     * @apiSuccess {String}     skill			                              医生擅长介绍
     *
     * @apiAuthor  谢平
     * @date 2015年12月18日
     */
    @RequestMapping("/findDoctorByDiseaseType")
    public JSONMessage findDoctorByDisease(String diseaseId) {
        return JSONMessage.success(null,docStatService.findDoctorByDisease(diseaseId));
    }
    
    /**
     * @api {get} /groupSearch/findDoctorByDept 根据科室搜索医生
     * @apiVersion 1.0.0
     * @apiName findDoctorByDept
     * @apiGroup 医生集团搜索
     * @apiDescription 根据科室搜索医生（支持点击上级科室查询出属于下级的所有医生）
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {String}     deptId                   	科室id
     *
     * @apiSuccess {Integer}    doctorId                    医生id
     * @apiSuccess {String}     doctorName                  医生名字
     * @apiSuccess {String}     headPicFileName             头像
     * @apiSuccess {String}     groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     hospital                    医院
     * @apiSuccess {String}     departments                 科室
     * @apiSuccess {String}     title                       职称
     * @apiSuccess {Integer}    disease                     擅长病种
     * @apiSuccess {String}     skill			                              医生擅长介绍
     *
     * @apiAuthor  谢平
     * @date 2015年12月18日
     */
    @RequestMapping("/findDoctorByDept")
    public JSONMessage findDoctorByDept(String deptId) {
        return JSONMessage.success(null,docStatService.findDoctorByDept(deptId));
    }
    
    /**
     * @api {post/get} /groupSearch/findRecommDisease 获取推荐病种
     * @apiVersion 1.0.0
     * @apiName findRecommDisease
     * @apiGroup 集团搜索
     * @apiDescription 获取推荐病种
     *
     * @apiParam   {String}   access_token                           token
     * 
     * @apiSuccess {List}     diseases                               病种对象list
     * @apiSuccess {Integer}  diseases.diseasesId                    病种ID
     * @apiSuccess {String}   diseases.diseasesName                  病种名称
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/findRecommDisease")
	public JSONMessage findRecommDisease(GroupSearchParam param) {
		return JSONMessage.success(null,groupSearchService.findRecommDisease(param));
	}
	
	 /**
     * @api {post/get} /groupSearch/findDiseaseByGroup 获取集团病种
     * @apiVersion 1.0.0
     * @apiName findDiseaseByGroup
     * @apiGroup 集团搜索
     * @apiDescription 获取集团病种
     *
     * @apiParam   {String}   access_token                           token
     * @apiParam   {Integer}  docGroupId                             医生集团ID
     * 
     * @apiSuccess {List}     diseases                               病种对象list
     * @apiSuccess {Integer}  diseases.diseasesId                    病种ID
     * @apiSuccess {String}   diseases.diseasesName                  病种名称
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/findDiseaseByGroup")
	public JSONMessage findDiseaseByGroup(GroupSearchParam param) {
		return JSONMessage.success(null,groupSearchService.findRecommDiseaseByGroup(param));
	}
	
	
	/**
     * @api {post/get} /groupSearch/findGroupBaseInfo 获取医生集团信息
     * @apiVersion 1.0.0
     * @apiName findGroupBaseInfo
     * @apiGroup 集团搜索
     * @apiDescription 获取医生集团信息
     *
     * @apiParam   {String}     access_token                               token
     * @apiParam   {Integer}    docGroupId                                 医生集团ID
     * 
     * @apiSuccess {Integer}    groupId                                    集团id
     * @apiSuccess {String}     groupName                                  集团名称
     * @apiSuccess {String}     certPath                                   集团头像集团介绍
     * @apiSuccess {String}     introduction                               集团简介
     * @apiSuccess {boolean}     memberApply                       是否允许医生申请加入集团
     * @apiSuccess {boolean}     memberInvite                         是否允许医生邀请其他医生加入集团
     * @apiSuccess {Integer}    expertNum                                  专家数量 
     * @apiSuccess {Integer}    cureNum                                    问诊数量 
     * @apiSuccess {String}    certStatus                  加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过） 
     * @apiSuccess {String}    bannerUrl                  集团banner图片url 
     * @apiSuccess {String}    contentUrl                 集团h5简介的url 
     * 
     * @apiSuccess {String}     disease                                  集团擅长病种
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/findGroupBaseInfo")
	public JSONMessage findGroupBaseInfo(GroupSearchParam param) {
		return JSONMessage.success(null,groupSearchService.findGroupBaseInfo(param));
	}
	
	
	
	/**
     * @api {post/get} /groupSearch/findProDoctorByGroupId 获取集团专家医生
     * @apiVersion 1.0.0
     * @apiName findProDoctorByGroupId
     * @apiGroup 集团搜索
     * @apiDescription 获取集团专家医生
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  docGroupId                           医生集团ID
     * @apiParam   {Integer}  pageIndex                            当前页码
     * 
     * @apiSuccess {List}     doctor                               专家医生对象list
     * @apiSuccess {Long}     doctor.doctorId                      专家医生ID
     * @apiSuccess {String}   doctor.doctorName                    专家 医生名称
     * @apiSuccess {String}   doctor.doctorPath                    专家头像
     * @apiSuccess {String}   doctor.skill                         擅长领域
     * @apiSuccess {String}   doctor.specialist                    专长
     * @apiSuccess {String}   doctor.position                      职称 
     * @apiSuccess {Integer}  doctor.inquiryNum                    问诊数
     * @apiSuccess {String}   doctor.departments                   所属科室
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/findProDoctorByGroupId")
	public JSONMessage findProDoctorByGroupId(GroupSearchParam param) {
		return JSONMessage.success(null,groupSearchService.findProDoctorByGroupId(param));
	}
	
	
	/**
     * @api {post/get} /groupSearch/findDoctorByGroup 获取集团id和科室id查询医生（医生端）
     * @apiVersion 1.0.0
     * @apiName findDoctorByGroup
     * @apiGroup 集团搜索
     * @apiDescription 获取集团id和科室id查询医生
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  docGroupId                           医生集团ID
     * @apiParam   {Integer}  pageIndex                            当前页码
     * @apiParam   {Integer}  pageSize                             页面大小
     * 
    * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
     * @apiAuthor  张垠
     * @date 2015年9月25日
     */
	@RequestMapping("/findDoctorByGroup")
	public JSONMessage findDoctorByGroup(GroupSearchParam param) {
		PageVO page = groupSearchService.findDoctorByGroup(param);
		 
		if (page.getPageData() != null) {
			List<GroupDoctorInfoVO> list = (List<GroupDoctorInfoVO>)page.getPageData();
			handleIsPackService( list );
		}
 
		return JSONMessage.success(null, page);
	}
	
	
	/**
     * @api {post/get} /groupSearch/findDoctorByGroupForPatient 获取集团id和科室id查询医生（患者端）
     * @apiVersion 1.0.0
     * @apiName findDoctorByGroupForPatient
     * @apiGroup 集团搜索
     * @apiDescription 获取集团id和科室id查询医生
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  docGroupId                           医生集团ID
     * @apiParam   {String}   deptName                             部门
     * @apiParam   {String}   specialistId 						      科室Id
     * @apiParam   {Integer}  areaCode                             地区码
     * @apiParam   {String}  lat                             		纬度
     * @apiParam   {String}  lng                             		经度
     * @apiParam   {Integer}  sort                             	   排序规则（0：综合排序， 1：离我最近）
     * @apiParam   {Integer}  pageIndex                            当前页码
     * @apiParam   {Integer}  pageSize                             页面大小
     * 
    * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * @apiSuccess {String} 	lng 	   				经度
	 * @apiSuccess {String} 	lat 	   				纬度
     * @apiAuthor  张垠
     * @date 2015年9月25日
     */
	@RequestMapping("/findDoctorByGroupForPatient")
	public JSONMessage findDoctorByGroupForPatient(GroupSearchParam param) {
//		PageVO page = groupSearchService.findDoctorByGroup(param);
//	 
//			if (page.getPageData() != null) {
//				List<GroupDoctorInfoVO> list = (List<GroupDoctorInfoVO>)page.getPageData();
//				handleIsPackService( list );
//			}
	 
//		return JSONMessage.success(null, page);
		param.setSearchType("gId");
		return JSONMessage.success(null,docStatService.searchDoctorByKeyWord(param));
	}
	
	
	/**
     * @api {post/get} /groupSearch/findDoctorOnlineByGroup 根据医生集团ID返回在线医生
     * @apiVersion 1.0.0
     * @apiName findDoctorOnlineByGroup
     * @apiGroup 集团搜索
     * @apiDescription 根据医生集团ID返回在线医生
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  docGroupId                           医生集团ID
     * @apiParam   {Integer}  pageSize                             查询记录数
     * 
     * @apiSuccess {String}   deptName                             科室名称
     * @apiSuccess {List}     doctor                               专家医生对象list
     * @apiSuccess {Long}     doctor.doctorId                      专家医生ID
     * @apiSuccess {String}   doctor.doctorName                    专家医生名称
     * @apiSuccess {String}   doctor.doctorPath                    专家头像
     * @apiSuccess {String}   doctor.skill                         擅长
     * @apiSuccess {String}   doctor.specialist                    专长
     * @apiSuccess {String}   doctor.position                      专业 
     * @apiSuccess {Integer}  doctor.inquiryNum                    问诊数
     * @apiSuccess {String}   doctor.departments                   所属科室
     * @apiSuccess {String}   doctor.isFree                        是否免费 1:否，2:是
     * @apiSuccess {String}   doctor.onLineState                   在线状态1，在线，2离线
     * @apiSuccess {String}   doctor.isPackService          	      是否开通了预约服务    1：是，非1：否
     * @apiSuccess {String}   doctor.role          	                                         角色    1：医生，2：护士
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/findDoctorOnlineByGroup")
	public JSONMessage findDoctorOnlineByGroup(GroupSearchParam param) {
		PageVO page = groupSearchService.findDoctorOnlineByGroup(param);
		if(StringUtil.isNotBlank(param.getDeptName())) {
			if (page.getPageData() != null) {
				List<GroupDoctorInfoVO> list = (List<GroupDoctorInfoVO>)page.getPageData();
				handleIsPackService(list);
			}
		}else{
			if (page.getPageData() != null) {
				List<OnlineDocByGorupVO> list = (List<OnlineDocByGorupVO>)page.getPageData();
				for (OnlineDocByGorupVO aaa : list) {
					handleIsPackService( aaa.getGroupDoctorInfoLists() );
				}
			}
		}
		return JSONMessage.success(null, page);
	}
    
	/**
     * @api {post/get} /groupSearch/findDoctorOnlineByGroupAndDept 根据医生集团ID和科室名称返回在线医生
     * @apiVersion 1.0.0
     * @apiName findDoctorOnlineByGroupAndDept
     * @apiGroup 集团搜索
     * @apiDescription 根据医生集团ID和科室名称返回在线医生
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  docGroupId                           医生集团ID
     * @apiParam   {String}   deptName                             医生科室名称
     * @apiParam   {Integer}  pageIndex                            当前页码
     * 
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen 	   "1":开启；"0"：关闭
     * @apiAuthor  张垠
     * @date 2015年9月25日
     */
	@RequestMapping("/findDoctorOnlineByGroupAndDept")
	public JSONMessage findDoctorOnlineByGroupId(GroupSearchParam param) {
//		PageVO page = groupSearchService.findDoctorOnlineByGroup(param);
//		if(StringUtil.isNotBlank(param.getDeptName())) {
//			if (page.getPageData() != null) {
//				List<GroupDoctorInfoVO> list = (List<GroupDoctorInfoVO>)page.getPageData();
//				handleIsPackService(list);
//			}
//		}else{
//			if (page.getPageData() != null) {
//				List<OnlineDocByGorupVO> list = (List<OnlineDocByGorupVO>)page.getPageData();
//				for (OnlineDocByGorupVO aaa : list) {
//					handleIsPackService( aaa.getGroupDoctorInfoLists() );
//				}
//			}
//		}
		param.setSearchType("sId");
		return JSONMessage.success(null, docStatService.searchDoctorByKeyWord(param));
	}

	private void handleIsPackService(List<GroupDoctorInfoVO> list) {
		if (list == null) {
			return;
		}
		List<Integer> doctorUserIds = new ArrayList<Integer>();
		for (GroupDoctorInfoVO groupDoctor : list) {
			doctorUserIds.add(groupDoctor.getDoctorId());
		}
		Map doctor012Map = packService.getPack012Doctors(doctorUserIds);
		for (GroupDoctorInfoVO groupDoctor : list) {
			if (doctor012Map.containsKey(groupDoctor.getDoctorId())) {
				groupDoctor.setIsPackService("1");
			} else {
				groupDoctor.setIsPackService("2");
			}
		}
	}
	
	/**
	 * @api {post/get} /groupSearch/findOnDutyToday 今日门诊
	 * @apiVersion 1.0.0
	 * @apiName findOnDutyToday
	 * @apiGroup 集团搜索
	 * @apiDescription 获取今日门诊医生信息
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {Integer} 		areaCode 				地区code
	 * @apiParam {String} 		docGroupId 				医生集团Id
	 * @apiParam {String} 		specialistId 			科室Id
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
	 * 
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
	 * @apiSuccess {Integer} 	role 			                     角色        1 医生 2  护士
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    门诊价格
	 * @apiAuthor 谢平
	 * @date 2015年11月18日
	 */
	@RequestMapping("/findOnDutyToday")
	public JSONMessage findOnDutyToday(GroupSearchParam param) {
		Object data = docStatService.findOnDutyToday(param);
		return JSONMessage.success(null, data);
	}
	
	/**
	 * @api {post/get} /groupSearch/findAllDoctor 获取所有医生
	 * @apiVersion 1.0.0
	 * @apiName findAllDoctor
	 * @apiGroup 集团搜索
	 * @apiDescription 获取所有医生
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
	 * 
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
     * @apiSuccess {Integer} 	role 			                    角色        1 医生 2  护士
     * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    电话咨询价格
	 * @apiAuthor 张垠
	 * @date 2016年07月15日
	 */
	@RequestMapping("/findAllDoctor")
	public JSONMessage findAllDoctor(GroupSearchParam param){
		return JSONMessage.success(null,docStatService.findAllDoctor(param));
	}
	
	
	
	/**
	 * @api {post/get} /groupSearch/findOrderDoctor 预约名医
	 * @apiVersion 1.0.0
	 * @apiName findOrderDoctor
	 * @apiGroup 集团搜索
	 * @apiDescription 获取可预约的名医信息
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {Integer} 		areaCode 				地区code
	 * @apiParam {String} 		specialistId 			科室Id
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
	 * 
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
     * @apiSuccess {Integer} 	role 			         角色        1 医生 2  护士
     * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    电话咨询价格
	 * @apiAuthor 谢平
	 * @date 2015年11月18日
	 */
	@RequestMapping("/findOrderDoctor")
	public JSONMessage findOrderDoctor(GroupSearchParam param) {
		Object data = docStatService.findOrderDoctor(param);
		return JSONMessage.success(null, data);
	}
	
	/**
	 * @api {post/get} /groupSearch/findOnlineConsultDoctor 在线咨询
	 * @apiVersion 1.0.0
	 * @apiName findOnlineConsultDoctor
	 * @apiGroup 集团搜索
	 * @apiDescription 在线咨询（开通图文咨询、电话咨询，仅博德嘉联只用）
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {Integer} 		areaCode 				地区code
	 * @apiParam {String} 		specialistId 			科室Id
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
     * @apiParam {String}   	docGroupId              博德嘉联集团Id（非必填）
	 * 
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
     * @apiSuccess {Integer} 	role 			                    角色        1 医生 2  护士
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiAuthor 谢平
	 * @date 2015年11月18日
	 */
	@RequestMapping("/findOnlineConsultDoctor")
	public JSONMessage findOnlineConsultDoctor(GroupSearchParam param) {
		return JSONMessage.success(docStatService.findOnlineConsultDoctor(param));
	}
	
	/**
	 * @api {post/get} /groupSearch/findDoctorsByGID 获取指定集团医生列表
	 * @apiVersion 1.0.0
	 * @apiName findDoctorsByGID
	 * @apiGroup 集团搜索
	 * @apiDescription 根据集团ID，地区code，科室Id 获取医生列表
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {String} 		groupId 				集团ID（必传）
	 * @apiParam {Integer} 		areaCode 				地区code
	 * @apiParam {String} 		specialistId 			科室Id
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
	 * 
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
     * @apiSuccess {Integer} 	role 			                    角色        1 医生 2  护士
     * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    电话咨询价格
	 * @apiAuthor 张垠
	 * @date 2016年06月02日
	 */
	@RequestMapping("findDoctorsByGID")
	public JSONMessage findDoctorsByGID(GroupSearchParam param){
		return JSONMessage.success(null, docStatService.findDoctorsByGID(param));
//		param.setSearchType("gbd");
//		return JSONMessage.success(null,docStatService.searchDoctorByKeyWord(param));
	}
	
	 /**
     * @api {get} /groupSearch/getDeptsWithDoc 获取有医生的科室
     * @apiVersion 1.0.0
     * @apiName getDeptsWithDoc
     * @apiGroup 集团搜索
     * @apiDescription 获取不同类型有医生的科室
     * 
     * @apiParam {String} 		access_token 			token
	 * @apiParam {String} 		groupId 				集团ID（必传）
	 * @apiParam {String} 		type 					appoint:预约名医科室列表;online：在线医生科室列表;不传则所有医生科室列表
     *
     * @apiSuccess {String}   id          科室id
     * @apiSuccess {String}   name        科室名称
     * @apiSuccess {String}   isLeaf      是否为叶子节点(1:是；0：否)
     * @apiSuccess {String}   parentId    父id
     *
     * @apiAuthor  张垠
     * @date 2016年06月03日
     */
	@RequestMapping("getDeptsWithDoc")
	public JSONMessage getDeptsWithDoc(String groupId,String type){
		return JSONMessage.success(null, docStatService.getAllDoctDepts(groupId,type));
	}
	/**
	 * @api {post/get} /groupSearch/findAppointmentDoctor 预约名医(博德嘉联调用)
	 * @apiVersion 1.0.0
	 * @apiName findAppointmentDoctor
	 * @apiGroup 集团搜索
	 * @apiDescription 预约名医(博德嘉联调用)
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {Integer}   	pageIndex               查询页，从0开始
     * @apiParam {Integer}   	pageSize                每页显示条数，不传默认15条
     * @apiParam {String} 		lat 			                    用户当前位置经度
     * @apiParam {String} 		lng 			                    用户当前位置纬度
	 * @apiParam {Integer}  	sort              		排序方式（0-综合排序  1-按照距离排序） 默认为0
	 * @apiParam {Integer}  	doctorId              	推荐医生Id（非必填）
	 * 
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
     * @apiSuccess {Integer} 	role 			                    角色        1 医生 2  护士
     * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    电话咨询价格
	 * @apiSuccess {String} 	offline.distance      距离当前用户的距离km 			                 
	 * @apiSuccess {String} 	offline.hospital      医院名称
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月28日
	 */
	@RequestMapping("/findAppointmentDoctor")
	public JSONMessage findAppointmentDoctor(GroupSearchParam param) {
		return JSONMessage.success(docStatService.findAppointmentDoctor(param));
	}
	

    /**
     * @api {get} /groupSearch/findGroupByName   搜索医生可加入的医生集团
     * @apiVersion 1.0.0
     * @apiName findGroupByName
     * @apiGroup 集团搜索
     * @apiDescription  搜索医生可加入的医生集团（集团名称模糊匹配，不传关键字返回所有数据 ，过滤已加入的集团 ）
     *
     * @apiParam   {String}     access_token                token
     * @apiParam   {String}     keyword                     关键字（不传默认为空，返回所有集团）（可匹配集团名称和集团简介）

     * @apiParam   {boolean}   memberApply             是否过滤掉不允许申请加入的集团（true：只搜索允许加入的集团，false：搜索所有集团。不传默认为true）
     * @apiParam   {Integer}    pageIndex                   查询页，从0开始
     * @apiParam   {Integer}    pageSize                    每页显示条数，不传默认15条
     * 
     *
     * @apiSuccess {Integer}    groupId                     集团id
     * @apiSuccess {String}     groupName                   集团名称
     * @apiSuccess {String}     certPath                    集团头像
     * @apiSuccess {String}     introduction                集团介绍
     * @apiSuccess {String}     disease                     擅长病种
     * @apiSuccess {Integer}    expertNum                   专家数量
     * @apiSuccess {Integer}    cureNum                     问诊数量
     * @apiSuccess {String}    certStatus                  加V认证状态（NC：未认证，NP:未通过，A：待审核，P:已通过）
     * @apiSuccess {String}    applyStatus                  申请状态  A=可申请加入，J=申请待确认，C=已加入集团，D=不允许加入集团
     * @apiAuthor  王峭
     * @date 2015年12月18日
     */
	@RequestMapping("/findGroupByName")
    public JSONMessage findGroupByName(GroupSearchParam param) {
        return JSONMessage.success(null,groupSearchService.findGroupByName(param));
        
    }
	
	/**
     * @api {post/get} /groupSearch/findGroupDoctorStatus 获取医生与医生集团关系信息
     * @apiVersion 1.0.0
     * @apiName findGroupDoctorStatus
     * @apiGroup 集团搜索
     * @apiDescription 获取医生与医生集团关系信息
     *
     * @apiParam   {String}     access_token                               token
     * @apiParam   {String}    docGroupId                                 医生集团ID
     * 
     * @apiSuccess {String}    doctorStatus                                  医生与医生集团的关系，C=已加入集团，J=申请待确认，A=可申请加入
     * @apiSuccess {String}     groupAdmin                                  是否是医生集团的管理员  true=管理员，false=非管理员

     * 
     * @apiAuthor 王峭
     * @date 2015年12月21日
     */
	@RequestMapping("/findGroupDoctorStatus")
	public JSONMessage findGroupDoctorStatus(GroupSearchParam param) {
		return JSONMessage.success(null,groupSearchService.findGroupDoctorStatus(param));
	}
		
	/**
     * @api {post/get} /groupSearch/getPatientDoctor 获取患者应的医生列表
     * @apiVersion 1.0.0
     * @apiName getPatientDoctor
     * @apiGroup 患者
     * @apiDescription 获取患者应的医生列表
     *
     * @apiParam   {String}     access_token                               token
     * 
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * 
	 * 
     * 
     * @apiAuthor 张垠
     * @date 2016年3月31日
     */
	@RequestMapping("/getPatientDoctor")
	public JSONMessage getPatientDoctor(){
		return JSONMessage.success(null,docStatService.getDoctorPatient(RelationType.doctorPatient, ReqUtil.instance.getUserId()));
	}
	
	/**
     * @api {post/get} /groupSearch/groupOnlineDoctors get group online doctors
     * @apiVersion 1.0.0
     * @apiName groupOnlineDoctors
     * @apiGroup 患者
     * @apiDescription get group online doctors
     *
     * @apiParam   {String}     access_token                               token（必传）
     * @apiParam   {String}     groupId                               集团id（必传）
     * @apiParam   {Integer}     countryId                               国家的id（选填）
     * @apiParam   {Integer}     provinceId                               省份的id（选填）
     * @apiParam   {Integer}     cityId                               城市的id（选填）
     * @apiParam   {String}     deptId                               科室的id（选填）
     * @apiParam   {Integer}     pageIndex                               页码（默认为0）
     * @apiParam   {Integer}     pageSize                               页面大小（默认为10）
     * 
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
     * 
     * @apiAuthor 傅永德
     * @date 2016年5月23日
     */
	@RequestMapping("/groupOnlineDoctors")
	public JSONMessage groupOnlineDoctors(
			@RequestParam(value="groupId", required=true) String groupId,
			@RequestParam(value="provinceId", required=false)Integer provinceId,
			@RequestParam(value="cityId", required=false)Integer cityId,
			@RequestParam(value="countryId", required=false)Integer countryId,
			@RequestParam(value="deptId", required=false)String deptId,
			@RequestParam(value="pageIndex", defaultValue="0")Integer pageIndex,
			@RequestParam(value="pageSize", defaultValue="10")Integer pageSize
	){
		//1、先获取c_group_doctor表中在线的doctor的id
		return JSONMessage.success(null, docStatService.getGroupOnlineDoctors(groupId, countryId, provinceId, cityId, deptId, pageIndex, pageSize));
	}
	
	/**
	 * @api {get/post} /groupSearch/getDoctorsByPackType 根据套餐类型获取集团医生
     * @apiVersion 1.0.0
     * @apiName getDoctorsByPackType
     * @apiGroup 集团医生
     * 
     * @apiParam  {String}    	groupId        集团id
     * @apiParam  {String}    	packType       服务类型（1、图文咨询。2、电话咨询。3、健康关怀）
     * @apiParam  {Integer}    	pageIndex        页码（默认为0）
     * @apiParam  {Integer}    	pageSize        页面大小（默认为20）
	 * 
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {Integer} 	replyCount 	   		   消息回复次数
	 * 
	 * @apiAuthor  傅永德
     * @date 2016年7月25日
	 * @return
	 */
	@RequestMapping("/getDoctorsByPackType")
	public JSONMessage getDoctorsByPackType(
			@RequestParam(name = "groupId", required=true)String groupId,
			@RequestParam(name = "packType", required=true)Integer packType,
			@RequestParam(name = "pageIndex", defaultValue="0")Integer pageIndex,
			@RequestParam(name = "pageSize", defaultValue="20")Integer pageSize
			
	) {
		return JSONMessage.success(docStatService.getDoctorsByPackType(groupId, packType, pageIndex, pageSize));
	}
	
	/**
	 * @api {get/post} /groupSearch/getRecommendDoctorByGroupId 根据集团id获取名医推荐
     * @apiVersion 1.0.0
     * @apiName getRecommendDoctorByGroupId
     * @apiGroup 集团医生
     * 
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String}    	groupId        集团id
     * @apiParam  {Integer}    	pageIndex        页码（默认为0）
     * @apiParam  {Integer}    	pageSize        页面大小（默认为20）
	 * 
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * 
	 * @apiAuthor  傅永德
     * @date 2016年7月25日
	 * @return
	 */
	@RequestMapping("/getRecommendDoctorByGroupId")
	public JSONMessage getRecommendDoctorByGroupId(
			@RequestParam(name="groupId", required = true) String groupId,
			@RequestParam(name="pageIndex", defaultValue = "0")Integer pageIndex,
			@RequestParam(name="pageSize", defaultValue = "20")Integer pageSize
	){
		return JSONMessage.success(docStatService.getRecommendDoctorByGroupId(groupId, pageIndex, pageSize));
	}
	
	/**
	 * @api {get/post} /groupSearch/getDoctorsByDiseaseId 根据病种id，精确查找医生
     * @apiVersion 1.0.0
     * @apiName getDoctorsByDiseaseId
     * @apiGroup 集团医生
     * 
     * @apiParam  {String}    	access_token        token
     * @apiParam  {String}    	diseaseId        病种id
     * @apiParam  {String}    	lat        纬度
     * @apiParam  {String}    	lng        经度
     * @apiParam  {Integer}    	pageIndex        页码（默认为0）
     * @apiParam  {Integer}    	pageSize        页面大小（默认为20）
	 * 
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * 
	 * 
	 * @apiAuthor  傅永德
     * @date 2016年7月25日
	 * @return
	 */
	@RequestMapping("/getDoctorsByDiseaseId")
	public JSONMessage getDoctorsByDiseaseId(
			@RequestParam(name="diseaseId", required = true) String diseaseId,
			@RequestParam(name="lat", required=false)String lat,		    
			@RequestParam(name="lng", required=false)String lng,
			@RequestParam(name="pageIndex", defaultValue = "0")Integer pageIndex,
			@RequestParam(name="pageSize", defaultValue = "20")Integer pageSize
	){
		return JSONMessage.success(docStatService.getDoctorsByDiseaseId(diseaseId, lat, lng, pageIndex, pageSize));
	}
	/**
	 * @api {get/post} /groupSearch/getDoctorsByDiseaseIds 根据病种id，精确查找医生
	 * @apiVersion 1.0.0
	 * @apiName getDoctorsByDiseaseIds
	 * @apiGroup 集团医生
	 *
	 * @apiParam  {String}    	access_token        token
	 * @apiParam  {String}    	diseaseIds        病种id集合
	 * @apiParam  {String}    	lat        纬度
	 * @apiParam  {String}    	lng        经度
	 * @apiParam  {Integer}    	pageIndex        页码（默认为0）
	 * @apiParam  {Integer}    	pageSize        页面大小（默认为20）
	 *
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 *
	 *
	 * @apiAuthor  傅永德
	 * @date 2016年7月25日
	 * @return
	 */
	@RequestMapping("/getDoctorsByDiseaseIds")
	public JSONMessage getDoctorsByDiseaseIds(
			@RequestParam(name="diseaseIds", required = true) List diseaseIds,
			@RequestParam(name="lat", required=false)String lat,
			@RequestParam(name="lng", required=false)String lng,
			@RequestParam(name="pageIndex", defaultValue = "0")Integer pageIndex,
			@RequestParam(name="pageSize", defaultValue = "20")Integer pageSize
	){
		if(diseaseIds.size()==0){
			throw new ServiceException("病种id不能为空");
		}
		return JSONMessage.success(docStatService.getDoctorsByDiseaseIds(diseaseIds, lat, lng, pageIndex, pageSize));
	}

	/**
	 * @api {get} /groupSearch/findGroupByDiseaseIds 根据病种搜索医生集团
	 * @apiVersion 1.0.0
	 * @apiName findGroupByDiseaseIds
	 * @apiGroup 医生集团搜索
	 * @apiDescription 根据病种搜索医生集团
	 *
	 * @apiParam   {String}     access_token                token
	 * @apiParam   {Integer}    pageIndex                   查询页，从0开始
	 * @apiParam   {Integer}    pageSize                    每页显示条数，不传默认15条
	 * @apiParam   {String}     diseaseIds                   病种id
	 *
	 * @apiSuccess {Integer}    groupId                     集团id
	 * @apiSuccess {String}     groupName                   集团名称
	 * @apiSuccess {String}     certPath                    集团头像集团介绍
	 * @apiSuccess {String}     introduction                集团简介
	 * @apiSuccess {String}     disease                     擅长病种
	 * @apiSuccess {Integer}    expertNum                   专家数量
	 * @apiSuccess {Integer}    cureNum                     问诊数量
	 *
	 * @apiAuthor  范鹏
	 * @date 2015年9月25日
	 */
	@RequestMapping("/findGroupByDiseaseIds")
	public JSONMessage findGroupByDiseaseIds(
			@RequestParam(name="diseaseIds", required = true) List diseaseIds,
			@RequestParam(name="pageIndex", defaultValue = "0")Integer pageIndex,
			@RequestParam(name="pageSize", defaultValue = "20")Integer pageSize
	) {
		if(diseaseIds.size()==0){
			throw new ServiceException("病种id不能为空");
		}
		return JSONMessage.success(null,groupSearchService.findGroupByDiseaseIds(diseaseIds,pageIndex,pageSize));
	}

	/**
	 * @api {post/get} /groupSearch/findDoctorByGoodsGroupIds 推荐积分问诊的医生
	 * @apiVersion 1.0.0
	 * @apiName findDoctorByGoodsGroupIds
	 * @apiGroup 集团搜索
	 * @apiDescription 推荐积分问诊的医生接口
	 *
	 * @apiParam {String} 		access_token 			token
	 * @apiParam {String[]} 	goodsGroupIds		    积分问诊的即可欧

	 *
	 * @apiSuccess {Integer} 	doctorId 				医生Id
	 * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {Boolean} 	myDoctor 				我的医生（1：问诊过，0：非）
	 * @apiSuccess {Integer} 	role 			         角色        1 医生 2  护士
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                    电话咨询价格
	 * @apiAuthor 李明
	 * @date 2016年12月6日18:33:02
	 */
	@RequestMapping("findDoctorByGoodsGroupIds")
	public JSONMessage findDoctorByGoodsGroupIds(PackDoctorParam param){
		if(param.getGoodsGroupIds().size()==0){
			throw new ServiceException("品种组id不能为空");
		}
		List<Integer> doctorIds=iPackDoctorServiceImpl.getDoctorByGoodsGroupId(param.getGoodsGroupIds());
		if(doctorIds==null|| doctorIds.size()==0){
			return JSONMessage.success();
		}

		return JSONMessage.success(docStatService.findDoctorByGoodsGroupIds(doctorIds));
	}

}
