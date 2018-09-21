package com.dachen.health.controller.knowledge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.knowledge.entity.param.MedicalKnowledgeParam;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.health.knowledge.service.IMedicalKnowledgeService;
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
	
	@Autowired
	private IMedicalKnowledgeService knowledgeService;
	/**
	 * @api {post} /knowledge/getGroupMedicalKnowledgeList 获取集团就医知识列表
	 * @apiVersion 1.0.0
	 * @apiName getGroupMedicalKnowledgeList
	 * @apiGroup 就医知识
	 * @apiDescription 获取集团就医知识列表
	 * @apiParam {String} access_token token
     * @apiParam {String} groupId 医生集团id
     * @apiParam {Integer} pageIndex 页下标
     * @apiParam {Integer} pageSize 每页大小
     * 
     * @apiSuccess {String} id 就医知识id
     * @apiSuccess {String} categoryId 分类id 
     * @apiSuccess {String} categoryName 分类名称
     * @apiSuccess {String} copy 题图
     * @apiSuccess {String} title 标题
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} createTime 创建时间
     * @apiSuccess {String} updateTime 更新时间
     * @apiSuccess {String} authorName 作者名称
     * @apiSuccess {String} isTop 是否置顶 "1":置顶，"0"：非置顶
     * @apiSuccess {Integer} visitCount 访问量
     * @apiSuccess {Integer} shareCount 分享量
     * 
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/getGroupMedicalKnowledgeList")
	public JSONMessage getGroupMedicalKnowledgeList(MedicalKnowledgeParam param) {
		return JSONMessage.success(knowledgeService.getGroupMedicalKnowledgeList(param));
	}
	/**
	 * @api {post} /knowledge/getUrlById 访问就医知识
	 * @apiVersion 1.0.0
	 * @apiName getUrlById
	 * @apiGroup 就医知识
	 * @apiDescription 访问就医知识
	 * @apiParam {String} access_token token
     * @apiParam {String} id 就医知识id
     * 
     * @apiSuccess {String} url 详情页面
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/getUrlById")
	public JSONMessage getUrlById(String id) {
		return JSONMessage.success(knowledgeService.getUrlById(id));
	}
	/**
	 * @api {post} /knowledge/getDetailById 获取就医知识详情
	 * @apiVersion 1.0.0
	 * @apiName getDetailById
	 * @apiGroup 就医知识
	 * @apiDescription 获取就医知识详情
	 * @apiParam {String} access_token token
     * @apiParam {String} id 就医知识id
     * @apiParam {String} categoryId 分类id 为空时不返回分类名称
     * 
     * @apiSuccess {String} id 就医知识id
     * @apiSuccess {String} copy 题图
     * @apiSuccess {String} title 标题
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} createTime 创建时间
     * @apiSuccess {String} updateTime 更新时间
     * @apiSuccess {String} authorName 作者名称
     * @apiSuccess {String} categoryName 分类名
     * @apiSuccess {Integer} isShow 封面是否显示
     * @apiSuccess {String} isTop 是否置顶
     * @apiSuccess {Integer} visitCount 访问量
     * @apiSuccess {Integer} shareCount 分享量
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/getDetailById")
	public JSONMessage getDetailById(MedicalKnowledgeParam param) {
		return JSONMessage.success(knowledgeService.getDetailById(param));
	}
	/**
	 * @api {post} /knowledge/getDoctorMedicalKnowledgeList 获取与医生有关就医知识列表
	 * @apiVersion 1.0.0
	 * @apiName getDoctorMedicalKnowledgeList
	 * @apiGroup 就医知识
	 * @apiDescription 获取与医生有关就医知识列表，
	 * @apiParam {String} access_token token
     * @apiParam {String} doctorId 医生id
     * @apiParam {String} groupId 集团Id
     * @apiParam {String} authorType 作者类型 "0":只查询医生个人文章,"1":医生个人和医生所属集团的文章
     * @apiParam {Integer} pageIndex 页下标
     * @apiParam {Integer} pageSize 每页大小
     * 
     * @apiSuccess {String} id 就医知识id
     * @apiSuccess {String} categoryId 分类id 
     * @apiSuccess {String} categoryName 分类名称
     * @apiSuccess {String} copy 题图
     * @apiSuccess {String} title 标题
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} createTime 创建时间
     * @apiSuccess {String} updateTime 更新时间
     * @apiSuccess {String} authorName 作者名称
     * @apiSuccess {String} isTop 是否置顶
     * @apiSuccess {Integer} visitCount 访问量
     * @apiSuccess {Integer} shareCount 分享量
     * 
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/getDoctorMedicalKnowledgeList")
	public JSONMessage getDoctorMedicalKnowledgeList(MedicalKnowledgeParam param) {
		return JSONMessage.success(knowledgeService.getDoctorMedicalKnowledgeList(param));
	}
	/**
	 * @api {post} /knowledge/setTop 设置就医知识为置顶
	 * @apiVersion 1.0.0
	 * @apiName setTop
	 * @apiGroup 就医知识
	 * @apiDescription 设置就医知识为置顶
	 * @apiParam {String} access_token token
     * @apiParam {String} id 就医知识id
     * @apiParam {String} bizId 置顶的业务id 医生id或者集团Id
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/setTop")
	public JSONMessage setTop(String id,String bizId) {
		return JSONMessage.success(knowledgeService.setTop(id, bizId));
	}
	/**
	 * @api {post} /knowledge/cancelTop 取消就医知识为置顶
	 * @apiVersion 1.0.0
	 * @apiName cancelTop
	 * @apiGroup 就医知识
	 * @apiDescription 取消就医知识为置顶
	 * @apiParam {String} access_token token
     * @apiParam {String} id 就医知识id
     * @apiParam {String} bizId 置顶的业务id 医生id或者集团Id
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/cancelTop")
	public JSONMessage cancelTop(String id,String bizId) {
		return JSONMessage.success(knowledgeService.cancelTop(id, bizId));
	}
	/**
	 * @api {post} /knowledge/upKnowledge 上移已经置顶的就医知识
	 * @apiVersion 1.0.0
	 * @apiName upKnowledge
	 * @apiGroup 就医知识
	 * @apiDescription 上移已经置顶的就医知识
	 * @apiParam {String} access_token token
     * @apiParam {String} id 就医知识Id
     * @apiParam {String} bizId 置顶的业务id 医生id或者集团Id
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/upKnowledge")
	public JSONMessage upKnowledge(String id,String bizId) {
		return JSONMessage.success(knowledgeService.upKnowledge(id, bizId));
	}
	
	/**
	 * @api {post} /knowledge/addKnowledge 添加就医知识
	 * @apiVersion 1.0.0
	 * @apiName addKnowledge
	 * @apiGroup 就医知识
	 * @apiDescription 添加就医知识
	 * @apiParam {String} access_token token
     * @apiParam {String} title 标题
     * @apiParam {Integer} isShow 0：详情中不显示图片 1:在详情中显示图片
	 * @apiParam {String} copy 图片
	 * @apiParam {String} author 作者id
	 * @apiParam {String}  categoryId 集团分类id
	 * @apiParam {String} description 描述
	 * @apiParam {String} content 内容
	 * @apiParam {Integer} createrType 创建类型 2：医生集团，3：医生
	 * @apiParam {String} creater 创建者  集团Id或者医生Id
	 *
	 * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
     * 
	 * @apiAuthor 罗超
	 */
	@RequestMapping("/addKnowledge")
	public JSONMessage addKnowledge(MedicalKnowledgeParam param) {
		return JSONMessage.success(knowledgeService.addKnowledge(param));
	}
	
	/**
	 * @api {post} /knowledge/updateKnowledge 更新就医知识
	 * @apiVersion 1.0.0
	 * @apiName updateKnowledge
	 * @apiGroup 就医知识
	 * @apiDescription  更新就医知识
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
     * @apiParam {String} isShow 1:代表显示，其它不显示
     * @apiParam {String} title 标题
	 * @apiParam {String} copy 图片
	 * @apiParam {String} author 作者
	 * @apiParam {String} copy_small 缩略图
	 * @apiParam {String} sCategoryId 改变之前的分类Id
	 * @apiParam {String}  categoryId 改变后的集团分类Id
	 * @apiParam {String} description 描述
	 * @apiParam {String} content 内容
	 * 
	 * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 *
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/updateKnowledge")
	public JSONMessage updateKnowledge(MedicalKnowledgeParam param) {
		return JSONMessage.success(knowledgeService.updateKnowledg(param));
	}
	/**
	 * @api {post} /knowledge/delKnowledgeById 删除就医知识
	 * @apiVersion 1.0.0
	 * @apiName delKnowledgeById
	 * @apiGroup 就医知识
	 * @apiDescription 删除就医知识
	 * @apiParam {String} access_token token
     * @apiParam {String}  id 标题
     * @apiParam {String}  groupId 集团Id（判断web端当前登录用户是否为该集团的管理员）
	 *
	 * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/delKnowledgeById")
	public JSONMessage delKnowledgeById(String id,String groupId) {
		return JSONMessage.success(knowledgeService.delKnowledgeById(id,groupId));
	}
	
	/**
	 * @api {post} /knowledge/addGroupCategory 新增就医知识分类
	 * @apiVersion 1.0.0
	 * @apiName addGroupCategory
	 * @apiGroup 就医知识
	 * @apiDescription 新增就医知识分类
	 * @apiParam {String} access_token token
     * @apiParam {String} name 分类名称
     * @apiParam {String} groupId 集团ID
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/addGroupCategory")
	public JSONMessage addGroupCategory(KnowledgeCategory category){
		return JSONMessage.success(null,knowledgeService.addCategoryById(category));
	}
	
	/**
	 * @api {post} /knowledge/delCategoryById 删除就医知识分类
	 * @apiVersion 1.0.0
	 * @apiName delCategoryById
	 * @apiGroup 就医知识
	 * @apiDescription 删除就医知识列表
	 * @apiParam {String} access_token token
     * @apiParam {String} id 分类ID
     *
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/delCategoryById")
	public JSONMessage delCategoryById(String id){
		return JSONMessage.success(null,knowledgeService.delCategoryById(id));
	}
	/**
	 * @api {post} /knowledge/updateCategoryById 重命名就医知识分类
	 * @apiVersion 1.0.0
	 * @apiName updateCategoryById
	 * @apiGroup 就医知识
	 * @apiDescription 重命名就医知识分类
	 * @apiParam {String} access_token token
     * @apiParam {String} id 分类ID
     * @apiParam {String} name 分类名称
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/updateCategoryById")
	public JSONMessage updateCategoryById(String id,String name){
		return JSONMessage.success(null,knowledgeService.renameCategory(id, name));
	}

	/**
	 * @api {post} /knowledge/getCategoryList 获取分类列表
	 * @apiVersion 1.0.0
	 * @apiName getCategoryList
	 * @apiGroup 就医知识
	 * @apiDescription 获取分类列表
	 * @apiParam {String} access_token token
     * @apiParam {String} groupId 集团ID
     * 
     * @apiSuccess {String} name 分类名称
     * @apiSuccess {String} id 分类ID
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/getCategoryList")
	public JSONMessage getCategoryList(String groupId){
		return JSONMessage.success(null,knowledgeService.getCategoryList(groupId));
	}
	/**
	 * @api {post} /knowledge/getKnowledgeListByCategoryId 根据分类ID获取文章列表
	 * @apiVersion 1.0.0
	 * @apiName getKnowledgeListByCategoryId
	 * @apiGroup 就医知识
	 * @apiDescription 根据分类ID获取文章列表
	 * @apiParam {String} access_token token
     * @apiParam {String} categoryId  分类ID
     * 
     * @apiSuccess {String} id 就医知识id
     * @apiSuccess {String} copy_small 缩略图
     * @apiSuccess {String} title 标题
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} createTime 创建时间
     * @apiSuccess {String} updateTime 更新时间
     * @apiSuccess {String} authorName 作者名称
     * @apiSuccess {String} isTop 是否置顶
     * @apiSuccess {Integer} visitCount 访问量
     * @apiSuccess {Integer} shareCount 分享量
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/getKnowledgeListByCategoryId")
	public JSONMessage getKnowledgeListByCategoryId(MedicalKnowledgeParam param){
		return JSONMessage.success(null,knowledgeService.getMedicalKnowledgeListByCategoryId(param));
	}
	
	/**
	 * @api {post} /knowledge/findKnowledgeListByKeys 根据关键字搜索
	 * @apiVersion 1.0.0
	 * @apiName findKnowledgeListByKeys
	 * @apiGroup 就医知识
	 * @apiDescription 根据关键字搜索
	 * @apiParam {String} access_token token
     * @apiParam {String} keywords  搜索关键字
     * @apiParam {String} groupId  集团Id
     * 
     * @apiSuccess {String} id 就医知识id
     * @apiSuccess {String} copy_small 缩略图
     * @apiSuccess {String} title 标题
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} createTime 创建时间
     * @apiSuccess {String} updateTime 更新时间
     * @apiSuccess {String} authorName 作者名称
     * @apiSuccess {String} isTop 是否置顶
     * @apiSuccess {Integer} visitCount 访问量
     * @apiSuccess {Integer} shareCount 分享量
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/findKnowledgeListByKeys")
	public JSONMessage findKnowledgeListByKeys(MedicalKnowledgeParam param){
		return JSONMessage.success(null,knowledgeService.searchKnowledgeListByKeys(param));
	}
	
	
	/**
	 * @api {post} /knowledge/addShareCount 增加分享量
	 * @apiVersion 1.0.0
	 * @apiName addShareCount
	 * @apiGroup 就医知识
	 * @apiDescription 增加分享量
	 * @apiParam {String} access_token token
     * @apiParam {String} bizId    对应业务ID
     * @apiParam {Integer} bizType  对应业务类型 1:就医知识
     * 
	 * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
	 * @apiAuthor 张垠
	 */
	@RequestMapping("/addShareCount")
	public JSONMessage addShareCount(String bizId,Integer bizType){
		return JSONMessage.success(null, knowledgeService.addShareCount(bizId, bizType));
	}
}
