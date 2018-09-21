package com.dachen.health.controller.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.service.IDocumentService;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private IDocumentService service;

    /**
     * @api {get} /document/createDocument 创建文档
     * @apiVersion 1.0.0
     * @apiName createDocument
     * @apiGroup 文档中心
     * @apiDescription 创建文档（患者广告/健康科普）
     * @apiParam {String}     access_token          token
     * @apiParam {String}     title                 文档标题
     * @apiParam {Integer}    documentType          文档类型(1,"患者广告"),(2,"健康科普"),(3,"健康关怀"),(4,"医生首页")，(5,"集团首页")
     * @apiParam {String}     contentType           文档内容分类
     * @apiParam {String}     copyPath              题图路径
     * @apiParam {String}     description           摘要
     * @apiParam {String}     content               正文
     * @apiParam {String}     groupId               集团ID documentType=5时必传
     * @apiParam {Boolean}     externalAd            是否为外部广告（true：外部广告链接，false（或者不传该参数）：非外部广告链接）
     * @apiParam {Integer}     isShowImg            是否显示在正文（1:显示，2：隐藏）
     * @apiParam {String}     recommendDoctId       名医推荐表Id documentType=4时必传
     * @apiParam {Integer}		isRecommend			1:跳转主页 0：不跳转
     * @apiParam {Integer}		recommendDetails.recommendType 	1：集团类型 2：医生类型
     * @apiParam {String}		recommendDetails.groupId	集团id
     * @apiParam {String}		recommendDetails.groupName	集团名称
     * @apiParam {Integer}		recommendDetails.doctorId	医生id
     * @apiParam {String}		recommendDetails.doctorName 医生名称
     * @apiSuccess DocumentVo  {@link DocumentVo}
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("createDocument")
    public JSONMessage createDocument(DocumentParam param) {
        return JSONMessage.success(null, service.addDcoument(param));
    }

    /**
     * @api {get} /document/delDocument 删除文档
     * @apiVersion 1.0.0
     * @apiName delDocument
     * @apiGroup 文档中心
     * @apiDescription 删除文档
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                    文档ID
     * @apiParam {String}     groupId               集团Id(如果删除的是集团首页则需要传此值)
     * @apiSuccess {boolean}   status                true成功 ;false失败
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("delDocument")
    public JSONMessage delDocument(DocumentParam param) {
        return JSONMessage.success(null, service.delDocument(param));
    }

    /**
     * @api {get} /document/updateDocument 更新文档
     * @apiVersion 1.0.0
     * @apiName updateDocument
     * @apiGroup 文档中心
     * @apiDescription 更新文档
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id          			 id
     * @apiParam {String}     title                 文档标题
     * @apiParam {String}     contentType           文档内容分类
     * @apiParam {String}     copyPath              题图路径
     * @apiParam {String}     description           摘要
     * @apiParam {String}     content               正文
     * @apiParam {Integer}		isRecommend			1:跳转主页 0：不跳转
     * @apiParam {Integer}		recommendDetails.recommendType 	1：集团类型 2：医生类型
     * @apiParam {String}		recommendDetails.groupId	集团id
     * @apiParam {String}		recommendDetails.groupName	集团名称
     * @apiParam {Integer}		recommendDetails.doctorId	医生id
     * @apiParam {String}		recommendDetails.doctorName 医生名称
     * @apiSuccess {boolean}   status                true成功 ;false失败
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("updateDocument")
    public JSONMessage updateDocument(DocumentParam param) {
        return JSONMessage.success(null, service.updateDocument(param));
    }

    /**
     * @api {get} /document/updateRecDocument 更新文档
     * @apiVersion 1.0.0
     * @apiName updateRecDocument
     * @apiGroup 文档中心
     * @apiDescription 更新文档
     * @apiParam {String}     access_token          token
     * @apiParam {String}     recommendDoctId       recommendid
     * @apiParam {String}     content               正文
     * @apiParam {Ingteger}     isShow                1：显示，2：不显示
     * @apiParam {String}     doctorName            医生姓名
     * @apiSuccess {boolean}   status                true成功 ;false失败
     * @apiAuthor 张垠
     * @date 2016年06月6日
     */
    @RequestMapping("updateRecDocument")
    public JSONMessage updateRecommentDocument(DocumentParam param) {
        return JSONMessage.success(null, service.updateRecommendDocument(param));
    }

    /**
     * @api {get} /document/getGoupDescInfoById 根据集团ID获取集团简介
     * @apiVersion 1.0.0
     * @apiName getGoupDescInfoById
     * @apiGroup 文档中心
     * @apiDescription 根据集团ID获取集团简介
     * @apiParam {String}     access_token          token
     * @apiParam {String}     groupId               集团Id
     * @apiSuccess {String}     url                 文档url
     * @apiSuccess {String}     content             文档内容
     * @apiSuccess {String}     copyPath            文档封面
     * @apiSuccess {Integer}    isShowImg              1为显示；2为非显示
     * @apiSuccess {long}       createTime           创建时间
     * @apiSuccess {String}     documentType        文档类型(1,"患者广告"),(2,"健康科普"),(3,"健康关怀"),(4,"医生首页")，(5,"集团首页")
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("getGoupDescInfoById")
    public JSONMessage getDocumentDetail(String groupId) {
        return JSONMessage.success(null, service.getDocumentByGid(groupId));
    }

    /**
     * @api {get} /document/getDocumentDetail 根据ID获取文档详情
     * @apiVersion 1.0.0
     * @apiName getDocumentDetail
     * @apiGroup 文档中心
     * @apiDescription 根据ID获取文档详情
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                   文档ID
     * @apiSuccess {String}     url                 文档url
     * @apiSuccess {String}     content             文档内容
     * @apiSuccess {String}     title               文档标题
     * @apiSuccess {String}     copyPath            文档封面
     * @apiSuccess {String}     isTOP               1为置顶；2为非置顶
     * @apiSuccess {String}     isShow              1为显示；2为非显示
     * @apiSuccess {Integer}     visitCount         浏览量
     * @apiSuccess {long}       createTime           创建时间
     * @apiSuccess {String}     contentType         文档内容分类（科普所用）
     * @apiSuccess {Boolean}     externalAd         是否为外部广告（true：外部广告链接，false（或者不传该参数）：非外部广告链接）
     * @apiSuccess {String}     documentType        文档类型(1,"患者广告"),(2,"健康科普"),(3,"健康关怀"),(4,"医生首页")，(5,"集团首页")
     * @apiParam {Integer}		isRecommend			1:跳转主页 0：不跳转
     * @apiParam {Integer}		recommendDetails.recommendType 	1：集团类型 2：医生类型
     * @apiParam {String}		recommendDetails.groupId	集团id
     * @apiParam {String}		recommendDetails.groupName	集团名称
     * @apiParam {Integer}		recommendDetails.doctorId	医生id
     * @apiParam {String}		recommendDetails.doctorName 医生名称
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("getDocumentDetail")
    public JSONMessage getDocumentDetail(DocumentParam param) {
        return JSONMessage.success(null, service.getDocumentDetail(param));
    }

    /**
     * @api {get} /document/getDocumentList 获取文档列表
     * @apiVersion 1.0.0
     * @apiName getDocumentList
     * @apiGroup 文档中心
     * @apiDescription 获取文档列表
     * @apiParam {String}     access_token          token
     * @apiParam {String}     documentType          文档类型(1,"患者广告"),science(2,"健康科普");）
     * @apiParam {String}     title                 文档标题
     * @apiParam {String}     contentType           内容分类(为健康科普时用)
     * @apiParam {String}     isShow                显示状态(患者广告时用)
     * @apiParam {Integer}     pageIndex            页码(第一页为0)
     * @apiParam {Integer}     pageSize             页面大小(默认是15条)
     * @apiParam {Integer}		isRecommond			 移动端该值设为1
     * @apiSuccess {List}         pageData                分页数据
     * @apiSuccess {object}		  recommendDetails				跳转数据
     * @apiSuccess {Integer}	  recommendDetails.recommendType  1:集团类型 2:医生类型
     * @apiSuccess {Integer}	  recommendDetails.doctorId		医生ID
     * @apiSuccess {String}	  	  recommendDetails.groupId		集团ID
     * @apiSuccess {Integer}      pageIndex            页码
     * @apiSuccess {Integer}      pageSize             页面大小
     * @apiSuccess {Long}         total                总量
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("getDocumentList")
    public JSONMessage getDocumentList(DocumentParam param) {
        return JSONMessage.success(null, service.getDocumentList(param));
    }

    /**
     * @api {get} /document/getHealthSicenceDocumentList 获取健康科普文档列表
     * @apiVersion 1.0.0
     * @apiName getHealthSicenceDocumentList
     * @apiGroup 文档中心
     * @apiDescription 获取健康科普文档列表
     * @apiParam {String}     access_token         	token
     * @apiParam {String}     kw			          	标题关键字
     * @apiParam {String}     contentType          	内容分类(为健康科普时用)
     * @apiParam {Integer}    pageIndex            	页码(第一页为0)
     * @apiParam {Integer}    pageSize             	页面大小(默认是10条)
     * @apiSuccess {List}      pageData             	文档的分页数据
     * @apiSuccess {List}      pageData.id          	文档的id
     * @apiSuccess {String}     pageData.title       	文档标题
     * @apiSuccess {Integer}    pageData.documentType    文档类型(1,"患者广告"),(2,"健康科普"),(3,"健康关怀"),(4,"医生首页")，(5,"集团首页")
     * @apiSuccess {String}     pageData.contentType     文档内容分类
     * @apiSuccess {String}     pageData.copyPath        题图路径
     * @apiSuccess {String}     pageData.description     摘要
     * @apiSuccess {String}     pageData.content         正文
     * @apiSuccess {String}     pageData.groupId         集团ID documentType=5时必传
     * @apiSuccess {Integer}      pageIndex            	页码
     * @apiSuccess {Integer}      pageSize             	页面大小
     * @apiSuccess {Long}         total                	总量
     * @apiAuthor 肖伟
     * @date 2016年10月14日
     */
    @RequestMapping(value = "getHealthSicenceDocumentList", method = RequestMethod.GET)
    public JSONMessage getHealthSicenceDocumentList(String contentType, String kw,
                                                    @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return JSONMessage.success(null, service.getHealthSicenceDocumentList(contentType, kw, pageIndex, pageSize));
    }

    /**
     * @api {get} /document/viewDocumentDetail 根据ID浏览文档
     * @apiVersion 1.0.0
     * @apiName viewDocumentDetail
     * @apiGroup 文档中心
     * @apiDescription 根据ID浏览文档
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                   文档ID
     * @apiSuccess {String}     url                 文档url
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("viewDocumentDetail")
    public JSONMessage viewDocumentDetail(DocumentParam param) {
        return JSONMessage.success(null, service.viewDocument(param));
    }

    /**
     * @api {get} /document/getContentType 获取文档分类列表
     * @apiVersion 1.0.0
     * @apiName getContentType
     * @apiGroup 文档中心
     * @apiDescription 根据文档类型获取对应的文档内容分类列表
     * @apiParam {String}     access_token          token
     * @apiParam {Integer}    documentType          文档类型(1,"患者广告"),science(2,"健康科普");）
     * @apiSuccess {List}   list       分类
     * @apiSuccess {String}   code       分类代码
     * @apiSuccess {String}   name       分类名称
     * @apiSuccess {Integer}  weight     排序权重
     * @apiSuccess {Integer}  count      总量
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("getContentType")
    public JSONMessage getContentType(DocumentParam param) {
        return JSONMessage.success(null, service.getAllTypeByDocumentType(param));
    }

    /**
     * @api {post} /document/getHealthSienceDocumentType 获取健康科普的分类列表
     * @apiVersion 1.0.0
     * @apiName getHealthSienceDocumentType
     * @apiGroup 文档中心
     * @apiDescription 获取健康科普的分类列表，并且过滤了总量为0的数据
     * @apiParam {String}     access_token          token
     * @apiSuccess {List}   	list			分类列表
     * @apiSuccess {String}   	list.code       分类代码
     * @apiSuccess {String}   	list.name       分类名称
     * @apiSuccess {Integer}  	list.weight     排序权重
     * @apiSuccess {Integer}  	list.count      总量
     * @apiAuthor 肖伟
     * @date 2016年10月14日
     */
    @RequestMapping(value = "getHealthSienceDocumentType", method = RequestMethod.POST)
    public JSONMessage getHealthSienceDocumentType() {
        return JSONMessage.success(null, service.getAllTypeByHealthSicenceDocument());
    }

    /**
     * @api {get} /document/getTopScienceList 获取科普置顶文章
     * @apiVersion 1.0.0
     * @apiName getTopScienceList
     * @apiGroup 文档中心
     * @apiDescription 获取科普置顶文章（按权重排序），不足五条，则补充至五条按浏览量倒序排序
     * @apiParam {String}     access_token          token
     * @apiSuccess {Integer}   count       总数
     * @apiSuccess {List}      list        实际数据
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("getTopScienceList")
    public JSONMessage getTopScienceList(DocumentParam param) {
        return JSONMessage.success(null, service.getTopScienceList(param));
    }

    /**
     * @api {get} /document/setAdverShowStatus 设置广告显示或不显示
     * @apiVersion 1.0.0
     * @apiName setAdverShowStatus
     * @apiGroup 文档中心
     * @apiDescription 置广告显示或不显示
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id          	                           文档ID
     * @apiParam {Integer}    isShow          	     1:显示; 2:取消显示
     * @apiSuccess {String}    status       操作结果（true/false）
     * @apiSuccess {String}    msg          提示消息
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("setAdverShowStatus")
    public JSONMessage setAdverShowStatus(DocumentParam param) {
        return JSONMessage.success(null, service.setADVShowStatus(param));
    }

    /**
     * @api {get} /document/setTopScience 科普文档置顶/取消置顶
     * @apiVersion 1.0.0
     * @apiName setTopScience
     * @apiGroup 文档中心
     * @apiDescription 设置科普文档置顶
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id          	                           文档ID
     * @apiParam {Integer}     isTop          	     1:置顶; 2:取消置顶
     * @apiSuccess {String}    status       操作结果（true/false）
     * @apiSuccess {String}    msg          提示消息
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("setTopScience")
    public JSONMessage setTopScience(DocumentParam param) {
        return JSONMessage.success(null, service.setTopScience(param));
    }

    /**
     * @api {get} /document/upOrDownWeight 上移或者下移
     * @apiVersion 1.0.0
     * @apiName upOrDownWeight
     * @apiGroup 文档中心
     * @apiDescription 上移或者下移已置顶或者已显示的文档
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id          	                           文档ID
     * @apiParam {String}     type          	     +:上移；—：下移
     * @apiSuccess {String}    status       操作结果（true/false）
     * @apiSuccess {String}    msg          提示消息
     * @apiAuthor 张垠
     * @date 2015年12月8日
     */
    @RequestMapping("upOrDownWeight")
    public JSONMessage upOrDownWeight(DocumentParam param, String type) {
        return JSONMessage.success(null, service.changeWeight(param, type));
    }


}
