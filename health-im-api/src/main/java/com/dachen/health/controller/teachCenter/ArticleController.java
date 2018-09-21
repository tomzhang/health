package com.dachen.health.controller.teachCenter;

import java.util.List;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.teachCenter.service.IArticleService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("article")
@Deprecated
public class ArticleController {
	@Autowired
	private IArticleService articleService;

	@Autowired
	private IGroupDoctorService groupDoctorService;
	/**
     * @api  {get} /article/getArticleById 根据ID查询出文章
     * @apiVersion 1.0.0
     * @apiName getArticleById
     * @apiGroup 患教中心
     * @apiDescription 根据ID查询出文章，返回所有基本信息（移动端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    articleId             文章ID
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime    创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	      文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * 
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/getArticleById")
	public JSONMessage getArticleById(String articleId){
		return JSONMessage.success(null, articleService.getArticleById(articleId));
	}
    
    
    /**
     * @api  {get} /article/getArticleByIdWeb 根据ID查询出文章
     * @apiVersion 1.0.0
     * @apiName getArticleByIdWeb
     * @apiGroup 患教中心
     * @apiDescription 根据ID查询出文章，返回所有基本信息（web端）
     * @apiParam  {String}    access_token           token
     * @apiParam  {String}    articleId              文章ID
     * @apiParam  {String}    createType             查询类型     （1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}    createrId              createType=2 或者4 时，createrId是集团ID，其它不传。
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime    创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	      文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/getArticleByIdWeb")
	public JSONMessage getArticleByIdWeb(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.getArticleByIdWeb(articleParam));
	}
	
    /**
     * @api    {get}    /article/viewArticle 文章ID浏览指定文章
     * @apiVersion 1.0.0
     * @apiName viewArticle
     * @apiGroup 患教中心
     * @apiDescription 根据ID查询出文章，返回文章，后台增加一条浏览记录(默认是医生身份)。
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    articleId             文章ID
     * @apiParam  {String}    createType             查询类型     (1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}    createrId              createType=2 或者4 时，createrId是集团ID，其它不传。
     * 
     * @apiSuccess {String}     url                 文章链接
     * @apiSuccess {Integer}    collect             是否已被收藏
     * @apiSuccess {boolean}    top                 是否被置顶
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/viewArticle")
	public JSONMessage viewArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.viewArticle(articleParam));
	}
    
    /**
     * @api    {get}    /article/statisticsArticleVisit 统计文章被浏览的次数
     * @apiVersion 1.0.0
     * @apiName statisticsArticleVisit
     * @apiGroup 患教中心
     * @apiDescription 统计文章被浏览的次数。
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    articleId             文章ID
     * @apiAuthor  姜宏杰
     * @date 2016-4-16 14:58:33
     */
    @RequestMapping("/statisticsArticleVisit")
	public JSONMessage statisticsArticleVisit(String articleId){
    	Integer userId = ReqUtil.instance.getUserId();
		return JSONMessage.success(null,articleService.statisticsArticleVisit(articleId, userId));
	}
	
    /**
     * @api {get} /article/getArticleByDisease 根据病种查出对应范围内文章列表
     * @apiVersion 1.0.0
     * @apiName getArticleByDisease
     * @apiGroup 患教中心
     * @apiDescription 查看平台：包含平台创建的以及分享的。查看集团：集团创建和收藏的以及集团内医生创建的收藏。医生只查自己的新建和收藏
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     diseaseId              病种id  不传时就会查询所有
     * @apiParam  {Integer}    createType             查询类型     （0:平台和集团，1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}     createrId              createType=2 或者4 时，createrId是集团ID，其它不传。
     * @apiParam  {Integer}    pageIndex              页码           第一页是1（必传）
     * @apiParam  {String}     sortBy                 排序字段    可以不传，默认按时间升序排序asc:升序，desc:降序
     * @apiParam  {String}     sortType               升序/降序（asc/desc）可以不传默认降序
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/false：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	       作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/getArticleByDisease")
	public JSONMessage getArticleByDisease(ArticleParam articleParam){
    	
		return JSONMessage.success(null, articleService.getArticleByDisease(articleParam));
	}
	
    
    /**
     * @api {get} /article/getArticleByDiseaseForMoreGroup 根据病种查出对应范围内文章列表(多集团)
     * @apiVersion 1.0.0
     * @apiName getArticleByDiseaseForMoreGroup
     * @apiGroup 患教中心
     * @apiDescription 查看平台：包含平台创建的以及分享的。查看集团：集团创建和收藏的以及集团内医生创建的收藏。医生只查自己的新建和收藏
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     diseaseId              病种id  不传时就会查询所有
     * @apiParam  {Integer}    createType             查询类型     （0:平台和集团，1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}     createrId              可以不传。
     * @apiParam  {Integer}    pageIndex              页码           第一页是1（必传）
     * @apiParam  {String}     sortBy                 排序字段    可以不传，默认按时间升序排序asc:升序，desc:降序
     * @apiParam  {String}     sortType               升序/降序（asc/desc）可以不传默认降序
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/false：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	       作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/getArticleByDiseaseForMoreGroup")
	public JSONMessage getArticleByDiseaseForMoreGroup(ArticleParam articleParam){
    	List<String> groupIds = groupDoctorService
				.getGroupListByDoctorId(ReqUtil.instance.getUserId());
    	articleParam.setGroupIds(groupIds);
		return JSONMessage.success(null, articleService.getArticleByDiseaseForMoreGroup(articleParam));
	}
    /**
     * @api {get} /article/getArticleByDoctor 获取指定医生收藏文档
     * @apiVersion 1.0.0
     * @apiName getArticleByDoctor
     * @apiGroup 患教中心
     * @apiDescription 只查医生自己创建 以及收藏的，不包括集团和平台分享的。
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    pageIndex              页码           第一页是1（必传）
     * @apiParam  {String}     sortBy                 排序字段    可以不传，默认按时间排序
     * @apiParam  {String}     sortType               升序/降序（asc/desc）可以不传默认降序
     * 
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	       作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/getArticleByDoctor")
	public JSONMessage getArticleByDoctor(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.getCollectArticleByDoctorId(articleParam));
	}
	
    /**
     * @api {post} /article/findArticleByKeyWord 根据关键字搜索文章列表
     * @apiVersion 1.0.0
     * @apiName findArticleByKeyWord
     * @apiGroup 患教中心
     * @apiDescription 根据关键字搜索文章标题，只搜索当前所在维度（平台，集团，个体医生）里文章
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     title                  搜索关键字必传
     * @apiParam  {Integer}    createType             查询类型     （1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}     createrId              createType=2 或者4 时，createrId是集团ID，其它不传。
     * @apiParam  {Integer}    pageIndex              页码 必传
     * @apiParam  {Integer}    pageSize               页面大小 20
     * @apiParam  {String}     sortBy                 排序字段 默认是按时 间降序
     * @apiParam  {String}     sortType               升序/降序（asc/desc）
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	       作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findArticleByKeyWord")
	public JSONMessage findArticleByKeyWord(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.searchArticleByKeyWord(articleParam));
	}
    
    /**
     * @api {get} /article/findArticleByTag 根据标签搜索言语章列表
     * @apiVersion 1.0.0
     * @apiName findArticleByTag
     * @apiGroup 患教中心
     * @apiDescription 根据标签搜索tag字段,不带树形所选中疾病ID。
     *
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     tags                   标签关键字
     * @apiParam  {Integer}    createType             查询类型     （1：平台，2：集团，3：医生,4:集团查询平台数据，5：医生向平台查数据，6：医生向集团查数据）
     * @apiParam  {String}     createrId              createType=2 或者4 时，createrId是集团ID，其它不传。
     * @apiParam  {Integer}    pageIndex              页码
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {String}     sortBy                排序字段
     * @apiParam  {String}     sortType              升序/降序（asc/desc）
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     groupName         作者集团名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime    创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签名称
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	       作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findArticleByTag")
	public JSONMessage findArticleByTag(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.searchArticleByTag(articleParam));
	}
	
    
    /**
     * @api {get} /article/findTopArticle 获取置顶文章列表
     * @apiVersion 1.0.0
     * @apiName findTopArticle
     * @apiGroup 患教中心
     * @apiDescription 获取置顶文章列表（最多5条）
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     groupId           	  集团ID（非必传）
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     title             标题
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     url               文章链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     context       	     文章正文
     * @apiSuccess {String[]}   tags              标签集合
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findTopArticle")
	public JSONMessage findTopArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.getAllTopArticle(articleParam));
	}
	
    /**
     * @api {get} /article/topArticle 置顶指定文章
     * @apiVersion 1.0.0
     * @apiName topArticle
     * @apiGroup 患教中心
     * @apiDescription 置顶指定文章，如果已置顶文章大于5，则不置顶并返回提示信息
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     articleId              文档ID
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/topArticle")
	public JSONMessage topArticle(String articleId){
    	ArticleParam articleParam = new ArticleParam();
    	articleParam.setArticleId(articleId);
		return JSONMessage.success(null, articleService.TopArticle(articleParam));
	}
	
    /**
     * @api {get} /article/topArticleUp 上移置顶指定文章
     * @apiVersion 1.0.0
     * @apiName topArticleUp
     * @apiGroup 患教中心
     * @apiDescription 上移置顶指定文章
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     upId                   向上置顶文档ID
     * @apiParam  {String}     downId                 被挤下文档ID
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/topArticleUp")
	public JSONMessage topArticleUp(String upId,String downId){
		return JSONMessage.success(null, articleService.topArticleUp(upId, downId));
	}
	
	
    /**
     * @api {get} /article/topArticleRemove 取消指定文档置顶
     * @apiVersion 1.0.0
     * @apiName topArticleRemove
     * @apiGroup 患教中心
     * @apiDescription 取消指定文档置顶
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     articleId              置顶文档ID
     * 
     * @apiSuccess {String}     operation            true/flase
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/topArticleRemove")
	public JSONMessage topArticleRemove(String articleId){
    	ArticleParam articleParam = new ArticleParam();
    	articleParam.setArticleId(articleId);
		return JSONMessage.success(null, articleService.cancleTopArticle(articleParam));
	}
	
	
    /**
     * @api {get} /article/collectArticle 收藏文档
     * @apiVersion 1.0.0
     * @apiName collectArticle
     * @apiGroup 患教中心   
     * @apiDescription 收藏一个文档，已收藏不做重复收藏操作  （称动端仅获取自己收藏的文章不包含自己所在集团的收藏）所有收藏文章包含创建和收藏的
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     articleId              文档ID
     * @apiParam  {Integer}    createType            收藏类型（2：集团，3：个体医生）
     * @apiParam  {String}     createrId              收藏者ID
     * 
     * @apiSuccess {String}     operation            true/flase
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/collectArticle")
	public JSONMessage collectArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.collectArticle(articleParam));
	}

    /**
     * @api {get} /article/collectArticleRemove 取消收藏文档
     * @apiVersion 1.0.0
     * @apiName collectArticleRemove
     * @apiGroup 患教中心   
     * @apiDescription 取消收藏文档
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     articleId              文档ID
     * @apiParam  {Integer}    createType            收藏类型（2：集团，3：个体医生）
     * @apiParam  {String}     createrId              createType=2时，createrId为集团ID
     * 
     * @apiSuccess {boolean}     status            true/flase
     * @apiSuccess {String}      msg            		提示信息
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/collectArticleRemove")
	public JSONMessage collectArticleRemove(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.cancleCollectArticle(articleParam));
	}
	
	
    /**
     * @api  {post}  /article/addArticle 新建文章
     * @apiVersion 1.0.0
     * @apiName addArticle
     * @apiGroup 患教中心
     * @apiDescription 新建文章。由于后台没有表区分管理员区非管理员，所以只能通过createType判断。createType 1：平台管理员（代表分享），createrID=system;2：集团管理员;3:医生。
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     author            作者ID，如果是集团管理员创建的需要传，其它情况不用传，就是当前登录者。如果是平台则是玄关患教中心
     * @apiParam {String}     title             标题
     * @apiParam {String}     diseaseId         病种ID
     * @apiParam {Integer}    isShow            是否显示到正文
     * @apiParam {Integer}    isShare           是否分享到患教中心
     * @apiParam {String[]}   tags              文章标签
     * @apiParam {String}     copyPath          文章封面path
     * @apiParam {String}     copy_small        封面缩略图
     * @apiParam {String}     description       文章摘要
     * @apiParam {String}     content       	 文章正文
     * @apiParam {Integer}    createType        如果是平台创建时则为createType=1; 如果是集团管理员创建时createType=2，且createrId是集团ID
     * @apiParam {String}     createrId         如果是集团管理员创建时createType=2，且createrId是集团ID
     * 
     * @apiSuccess {String}     url              文章URL
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("addArticle")
	public JSONMessage addArticle(ArticleParam articleParam){
    	//把先根据模板生成一份静态html文件保存到服务器上并记下这个文件的url，然后把文章的相关属性和url 存数据库里
		return JSONMessage.success(null, articleService.addArticle(articleParam));
	}
    
    /**
     * @api  {post}  /article/delArticle 删除文章
     * @apiVersion 1.0.0
     * @apiName delArticle
     * @apiGroup 患教中心
     * @apiDescription 删除文章，只能删除useNum为0的所有文章
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     articleId             文章ID.
     * 
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("delArticle")
    public JSONMessage delArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.delArticle(articleParam));
	}
	
	
    /**
     * @api {post}   /article/updateArticle 编辑更新文档
     * @apiVersion 1.0.0
     * @apiName updateArticle
     * @apiGroup 患教中心
     * @apiDescription 编辑指定ID文章，返回所有基本信息，后台增加一条浏览记录
     *@apiParam {String}      access_token      token
     * @apiParam {String}     articleId         文章ID
     * @apiParam {String}     author            作者ID
     * @apiParam {String}     title             标题
     * @apiParam {String}     diseaseId         病种ID
     * @apiParam {Integer}    isShow            是否显示到正文
     * @apiParam {Integer}    isShare           是否分享
     * @apiParam {String[]}   tags              文章标签
     * @apiParam {String}     isShare           是否分享到患教中心（true/false）
     * @apiParam {String}     copyPath          文章封面path
     * @apiParam {String}     copy_small        封面缩略图
     * @apiParam {String}     description       文章摘要
     * @apiParam {String}     content       	 文章正文
     * 
     * @apiSuccess {String}   opeartion         true/false
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("updateArticle")
	public JSONMessage updateArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.updateArticle(articleParam));
	}
	
	/**
     * @api   {get}     /article/findDiseaseTreeForArticle 查询病种树
     * @apiVersion 1.0.0
     * @apiName findDiseaseTreeForArticle
     * @apiGroup 患教中心
     * @apiDescription 查询病种树并统计对应一级病种文档数量
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    createType            收藏类型（0:平台和集团的1：平台的，2：集团，）
     * @apiParam  {String}     groupId               当createType=0时，至少会有平台的树;createType=1时平台groupId =system;createType=2时平台groupId =集团ID
     * 
     * @apiSuccess {long}       count               总文章数量
     * @apiSuccess {List}       tree                
     * @apiSuccess {id}         tree.id             节点ID
     * @apiSuccess {name}       tree.name           节点名称
     * @apiSuccess {parent}     tree.parent         父节点ID
     * @apiSuccess {Integer}    count               数量
     * @apiSuccess {List}       children            子节点集合
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findDiseaseTreeForArticle")
	public JSONMessage findDiseaseTreeForArticle(Integer createType,String groupId){
		return JSONMessage.success(null, articleService.getDisaseTreeByParam(createType,groupId));
	}
	/**
	 * @api {get} /article/findDiseaseTreeForArticleForMoreGroup 查询病种树（多集团）
	 * @apiVersion 1.0.0
	 * @apiName findDiseaseTreeForArticleForMoreGroup
	 * @apiGroup 患教中心
	 * @apiDescription 查询病种树并统计对应一级病种文档数量 （多集团）
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} createType 收藏类型（0:平台和集团的1：平台的，2：集团） 
	 * @apiSuccess {long} count 总文章数量
	 * @apiSuccess {List} tree
	 * @apiSuccess {id} tree.id 节点ID
	 * @apiSuccess {name} tree.name 节点名称
	 * @apiSuccess {parent} tree.parent 父节点ID
	 * @apiSuccess {Integer} count 数量
	 * @apiSuccess {List} children 子节点集合
	 * 
	 * @apiAuthor liwei
	 * @date 2016年1月18日
	 */
	@RequestMapping("/findDiseaseTreeForArticleForMoreGroup")
	public JSONMessage findMoreGroupDiseaseTreeForArticle(Integer createType) {
		List<String> groupList = groupDoctorService
				.getGroupListByDoctorId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null,
				articleService.getDisaseTreeByUserId(createType, groupList));
	}
	/**
     * @api  {get}  /article/findWeekHotArticle 查看周热门文档
     * @apiVersion 1.0.0
     * @apiName findWeekHotArticle
     * @apiGroup 患教中心
     * @apiDescription 查平台和自己以及自己集团所有的
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    createType           收藏类型（0:平台和集团的1：平台的，2：集团，）
     * @apiParam  {String}    createrId             可不传，如果是平台则传system,如果是空则根据当前登录用户去取医生集团ID
     * @apiParam  {String}    diseaseId             病种ID，可不传
     * @apiParam  {Integer}   pageIndex              页码           第一页是1（必传）
     * @apiParam  {String}    sortType               升序/降序（asc/desc）可不传默认降序 
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     url               链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     title             标题
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess  {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findWeekHotArticle")
	public JSONMessage findWeekHotArticle(ArticleParam articleParam){
		return JSONMessage.success(null, articleService.getHotArticle(articleParam, "week"));
	}
	/**
	 * @api {get} /article/findWeekHotArticleForMoreGroup 查看周热门文档（多集团）
	 * @apiVersion 1.0.0
	 * @apiName findWeekHotArticleForMoreGroup
	 * @apiGroup 患教中心
	 * @apiDescription 查平台和自己以及自己集团所有的（多集团）
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} createType 收藏类型（0:平台和集团的1：平台的，2：集团，）
	 * @apiParam {String} createrId  可不传，如果是平台则传system,如果是空，服务器默认取医生所有的集团
	 * @apiParam {String} diseaseId 病种ID，可不传
	 * @apiParam {Integer} pageIndex 页码 第一页是1（必传）
	 * @apiParam {String} sortType 升序/降序（asc/desc）可不传默认降序
	 * 
	 * @apiSuccess {String} id 文章ID
	 * @apiSuccess {boolean} isCollect true:被收藏/fals：没有被收藏
	 * @apiSuccess {boolean} top true:被置顶/false：没有被置顶
	 * @apiSuccess {String} author 作者ID
	 * @apiSuccess {String} authorName 作者名称
	 * @apiSuccess {String} url 链接
	 * @apiSuccess {Integer} isShow 是否显示到正文
	 * @apiSuccess {Integer} isShare 是否分享到患教中心
	 * @apiSuccess {String} title 标题
	 * @apiSuccess {DiseaseTypeVO} disease 病种
	 * @apiSuccess {DiseaseTypeVO} disease.Id 病种ID
	 * @apiSuccess {DiseaseTypeVO} disease.name 病种名称
	 * @apiSuccess {String} createrId 创建者Id
	 * @apiSuccess {long} lastUpdateTime 创建时间
	 * @apiSuccess {String} copyPath 文章封面path
	 * @apiSuccess {String} copy_small 封面缩略图
	 * @apiSuccess {String} description 文章摘要
	 * @apiSuccess {String} content 文章正文
	 * @apiSuccess {String[]} tags 标签
	 * @apiSuccess {DiseaseTypeVO[]} tag 标签对象集合
	 * @apiSuccess {Doctor} doctor 作者信息
	 * @apiSuccess {Doctor} doctor.hospital 作者医院
	 * @apiSuccess {Doctor} doctor.title 作者职称
	 * @apiSuccess {long} visitCount 访问量
	 * @apiSuccess {Integer} collect 0：没有收藏，1：收藏，2：创建
	 * @apiSuccess {Integer} useNum 使用量
	 * 
	 * @apiAuthor 李伟
	 * @date 2016年1月18日
	 */
	@RequestMapping("/findWeekHotArticleForMoreGroup")
	public JSONMessage findWeekHotArticleForMoreGroup(ArticleParam articleParam) {
	
			List<String> groupList = groupDoctorService
					.getGroupListByDoctorId(ReqUtil.instance.getUserId());
            articleParam.setGroupIds(groupList);		
		return JSONMessage.success(null,
				articleService.getHotArticleForMoreGroup(articleParam, "week"));
	}

	/**
     * @api  {get}  /article/findAllHotArticle 查看所有的热门文档
     * @apiVersion 1.0.0
     * @apiName findAllHotArticle
     * @apiGroup 患教中心
     * @apiDescription 查平台和自己以及自己集团所有的
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    createType           收藏类型（0:平台和集团的1：平台的，2：集团，3:医生）
     * @apiParam  {String}    createrId             可不传，如果是平台则传system,如果是空则根据当前登录用户去取医生集团ID
     * @apiParam  {String}    diseaseId             病种ID，可不传
     * @apiParam  {Integer}   pageIndex             页码           第一页是1（必传）
     * @apiParam  {String}    sortType              升序/降序（asc/desc）可不传默认降序  
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     url               链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     title             标题
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime         创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findAllHotArticle")
	public JSONMessage findAllHotArticle(ArticleParam articleParam){
    	if(0==articleParam.getPageSize()){
    		articleParam.setPageSize(20);
    	}
//    	return JSONMessage.success(null, null);
		return JSONMessage.success(null, articleService.getHotArticle(articleParam, null));
	}
	/**
	 * @api {get} /article/findAllHotArticleForMoreGroup 查看所有的热门文档(多集团)
	 * @apiVersion 1.0.0
	 * @apiName findAllHotArticleForMoreGroup
	 * @apiGroup 患教中心
	 * @apiDescription 查平台和自己以及自己集团所有的(多集团)
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} createType 收藏类型（0:平台和集团的1：平台的，2：集团，3:医生）
	 * @apiParam {String} createrId 可不传，如果是平台则传system,如果是空，服务器默认取医生所有的集团  
	 * @apiParam {String} diseaseId 病种ID，可不传
	 * @apiParam {Integer} pageIndex 页码 第一页是1（必传）
	 * @apiParam {String} sortType 升序/降序（asc/desc）可不传默认降序
	 * 
	 * @apiSuccess {String} id 文章ID
	 * @apiSuccess {boolean} isCollect true:被收藏/fals：没有被收藏
	 * @apiSuccess {boolean} top true:被置顶/false：没有被置顶
	 * @apiSuccess {String} author 作者ID
	 * @apiSuccess {String} authorName 作者名称
	 * @apiSuccess {String} url 链接
	 * @apiSuccess {Integer} isShow 是否显示到正文
	 * @apiSuccess {Integer} isShare 是否分享到患教中心
	 * @apiSuccess {String} title 标题
	 * @apiSuccess {DiseaseTypeVO} disease 病种
	 * @apiSuccess {DiseaseTypeVO} disease.Id 病种ID
	 * @apiSuccess {DiseaseTypeVO} disease.name 病种名称
	 * @apiSuccess {String} createrId 创建者Id
	 * @apiSuccess {long} lastUpdateTime 创建时间
	 * @apiSuccess {String} copyPath 文章封面path
	 * @apiSuccess {String} copy_small 封面缩略图
	 * @apiSuccess {String} description 文章摘要
	 * @apiSuccess {String} content 文章正文
	 * @apiSuccess {String[]} tags 标签
	 * @apiSuccess {DiseaseTypeVO[]} tag 标签对象集合
	 * @apiSuccess {Doctor} doctor 作者信息
	 * @apiSuccess {Doctor} doctor.hospital 作者医院
	 * @apiSuccess {Doctor} doctor.title 作者职称
	 * @apiSuccess {long} visitCount 访问量
	 * @apiSuccess {Integer} collect 0：没有收藏，1：收藏，2：创建
	 * @apiSuccess {Integer} useNum 使用量
	 * 
	 * @apiAuthor 李伟
	 * @date 2016年1月18日
	 */
	@RequestMapping("/findAllHotArticleForMoreGroup")
	public JSONMessage findAllHotArticleForMoreGroup(ArticleParam articleParam) {
		//该BUG（XGSF-3575）没传递pageSize的话就给设个默认值20
		List<String> groupList = groupDoctorService.getGroupListByDoctorId(ReqUtil.instance.getUserId());
        articleParam.setGroupIds(groupList);		
		return JSONMessage.success(null, articleService.getHotArticleForMoreGroup(articleParam, null));
	}
	
	
	/**
     * @api  {get}  /article/findNewArticle 查看最新文档
     * @apiVersion 1.0.0
     * @apiName findNewArticle
     * @apiGroup 患教中心
     * @apiDescription 查平台和自己以及自己集团所有的
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     diseaseId              病种id  不传时就会查询所有
     * @apiParam  {Integer}    createType             查询类型     （1：平台，2：集团，3：个体医生）web端客理员查集团一定要传2，对应createrId是集团ID
     * @apiParam  {String}     createrId              访问者ID可以不传，后台就会自动查询登录用户，并判断有没有加入集团（平台传system）
     * @apiParam  {Integer}    pageIndex              页码           第一页是1（必传）
     * @apiParam  {String}     sortBy                 排序字段    可以不传，默认按时间排序
     * @apiParam  {String}     sortType               升序/降序（asc/desc）可以不传默认降序
     * 
     * @apiSuccess {String}     id                文章ID
     * @apiSuccess {boolean}    isCollect         true:被收藏/fals：没有被收藏
     * @apiSuccess {boolean}    top             true:被置顶/false：没有被置顶
     * @apiSuccess {String}     author            作者ID
     * @apiSuccess {String}     authorName        作者名称
     * @apiSuccess {String}     url               链接
     * @apiSuccess {Integer}    isShow            是否显示到正文
     * @apiSuccess {Integer}    isShare           是否分享到患教中心
     * @apiSuccess {String}     title             标题
     * @apiSuccess {DiseaseTypeVO}     disease    病种
     * @apiSuccess {DiseaseTypeVO}     disease.Id    病种ID
     * @apiSuccess {DiseaseTypeVO}     disease.name    病种名称
     * @apiSuccess {String}     createrId         创建者Id
     * @apiSuccess {long}       lastUpdateTime    创建时间
     * @apiSuccess {String}     copyPath          文章封面path
     * @apiSuccess {String}     copy_small        封面缩略图
     * @apiSuccess {String}     description       文章摘要
     * @apiSuccess {String}     content       	     文章正文
     * @apiSuccess {String[]}   tags              标签
     * @apiSuccess {DiseaseTypeVO[]}   tag        标签对象集合
     * @apiSuccess {Doctor}     doctor            作者信息
     * @apiSuccess {Doctor}     doctor.hospital   作者医院
     * @apiSuccess {Doctor}     doctor.title	      作者职称
     * @apiSuccess {long}       visitCount        访问量
     * @apiSuccess {Integer}       collect        0：没有收藏，1：收藏，2：创建
     * @apiSuccess {Integer}       useNum         使用量
     * 
     * @apiAuthor  张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/findNewArticle")
	public JSONMessage findNewArticle(ArticleParam articleParam,String type){
    	//该BUG（XGSF-3575）没传递pageSize的话就给设个默认值20
    	if(0==articleParam.getPageSize()){
    		articleParam.setPageSize(20);
    	}
		return JSONMessage.success(null, articleService.getNewArticle(articleParam, null));
	}
	/**
	 * @api {get} /article/findNewArticleForMoreGroup 查看最新文档(多集团)
	 * @apiVersion 1.0.0
	 * @apiName findNewArticleForMoreGroup
	 * @apiGroup 患教中心
	 * @apiDescription 查平台和自己以及自己集团所有的(多集团)
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} diseaseId 病种id 不传时就会查询所有
	 * @apiParam {Integer} createType 查询类型
	 *           （1：平台，2：集团，3：个体医生）web端客理员查集团一定要传2
	 * @apiParam {String} createrId 访问者ID可以不传，后台就会自动查询登录用户，并判断有没有加入集团（平台传system）
	 * @apiParam {Integer} pageIndex 页码 第一页是1（必传）
	 * @apiParam {String} sortBy 排序字段 可以不传，默认按时间排序
	 * @apiParam {String} sortType 升序/降序（asc/desc）可以不传默认降序
	 * 
	 * @apiSuccess {String} id 文章ID
	 * @apiSuccess {boolean} isCollect true:被收藏/fals：没有被收藏
	 * @apiSuccess {boolean} top true:被置顶/false：没有被置顶
	 * @apiSuccess {String} author 作者ID
	 * @apiSuccess {String} authorName 作者名称
	 * @apiSuccess {String} url 链接
	 * @apiSuccess {Integer} isShow 是否显示到正文
	 * @apiSuccess {Integer} isShare 是否分享到患教中心
	 * @apiSuccess {String} title 标题
	 * @apiSuccess {DiseaseTypeVO} disease 病种
	 * @apiSuccess {DiseaseTypeVO} disease.Id 病种ID
	 * @apiSuccess {DiseaseTypeVO} disease.name 病种名称
	 * @apiSuccess {String} createrId 创建者Id
	 * @apiSuccess {long} lastUpdateTime 创建时间
	 * @apiSuccess {String} copyPath 文章封面path
	 * @apiSuccess {String} copy_small 封面缩略图
	 * @apiSuccess {String} description 文章摘要
	 * @apiSuccess {String} content 文章正文
	 * @apiSuccess {String[]} tags 标签
	 * @apiSuccess {DiseaseTypeVO[]} tag 标签对象集合
	 * @apiSuccess {Doctor} doctor 作者信息
	 * @apiSuccess {Doctor} doctor.hospital 作者医院
	 * @apiSuccess {Doctor} doctor.title 作者职称
	 * @apiSuccess {long} visitCount 访问量
	 * @apiSuccess {Integer} collect 0：没有收藏，1：收藏，2：创建
	 * @apiSuccess {Integer} useNum 使用量
	 * 
	 * @apiAuthor 李伟
	 * @date 2016年1月18日
	 */
	@RequestMapping("/findNewArticleForMoreGroup")
	public JSONMessage findNewArticleForMoreGroup(ArticleParam articleParam, String type) {
		List<String> groupList = groupDoctorService
				.getGroupListByDoctorId(ReqUtil.instance.getUserId());
        articleParam.setGroupIds(groupList);		
		return JSONMessage.success(null,
				articleService.getNewArticleForMoreGroup(articleParam, null));
	}
	/**
     * @api {get} /article/updateArticleUse 增加使用次数
     * @apiVersion 1.0.0
     * @apiName updateArticleUse
     * @apiGroup 患教中心
     * @apiDescription 医生每发送一次文章，就会增加一次文章使用量
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     articleId              文档ID
     * 
     * @apiSuccess {String}     status               true：执行成功/false：执行失败
     * @apiSuccess {String}     msg                  对应提示信息
     * 
     * @apiAuthor  张垠
     * @date 2015年10月27日
     */
    @RequestMapping("/updateArticleUse")
    public JSONMessage  updateArticleUse(ArticleParam articleParam){
    	return JSONMessage.success(null, articleService.updateUseCount(articleParam));
    }
    
    
    /**
     * @api {get} /article/getTypeByParent 根据父节点获取专长
     * @apiVersion 1.0.0
     * @apiName getTypeByParent
     * @apiGroup 患教中心
     * @apiDescription 根据父节点获取专长，parentId为空查找一级病种
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     parentId               父节点id
     *
     * @apiSuccess {String}    id                     病种id
     * @apiSuccess {String}    name                   病种名称
     *
     * @apiAuthor  张垠
     * @date 2015年10月30日
     */
    @RequestMapping("/getTypeByParent")
    public JSONMessage getTypeByParent(String parentId) {
        return JSONMessage.success(null, articleService.getTypeByParent(parentId));
    }
    
    @RequestMapping("/sendArticleToUser")
    public JSONMessage sendArticleToUser(String fromUserId,String gid,String url) throws HttpApiException {
    	articleService.sendArticleToUser(fromUserId, gid, url);
        return JSONMessage.success("发送成功");
    }
    
    /**
     * @api {get，post} /article/addArticleByUrl 根据url获取文章
     * @apiVersion 1.0.0
     * @apiName addArticleByUrl
     * @apiGroup 患教中心
     * @apiDescription 医生通过url添加文章
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     url               	      对应的连接
     *
     * @apiSuccess {String}    url                     文章URL
     *
     * @apiAuthor  张垠
     * @date 2015年10月30日
     */
    @RequestMapping("/addArticleByUrl")
    public JSONMessage getArticleByUrl(ArticleParam articleParam,String url){
    	return JSONMessage.success(null, articleService.addArticleByUrl(articleParam,url));
    }
    @RequestMapping("/addDTG")
    public JSONMessage getAddDTG(String doctor,String groupId){
    	articleService.addDTG(doctor, groupId);
    	return JSONMessage.success(null, null);
    }
    
    
    /**
     * @api {get，post} /article/updateVisitNum 更新浏览量，修复数据
     * @apiVersion 1.0.0
     * @apiName updateVisitNum
     * @apiGroup 患教中心
     * @apiDescription 更新浏览量，修复数据
     *
     * @apiParam  {String}     access_token           token
     *
     * @apiAuthor  张垠
     * @date 2015年10月30日
     */
    @RequestMapping("/updateVisitNum")
    public JSONMessage updateVisitNum(){
    	articleService.updateVisitNum();
    	return JSONMessage.success(null, null);
    }
}
