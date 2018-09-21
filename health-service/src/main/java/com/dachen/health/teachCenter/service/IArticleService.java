package com.dachen.health.teachCenter.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.teachCenter.entity.vo.ArticleVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IArticleService {
	
	/**
	 * 根据文章ID获取文章移动端
	 * @param articleId
	 * @return
	 */
	ArticleVO getArticleById(String articleId);
	
	/**
	 * 根据文章ID获取文章web端
	 * @param articleId
	 * @return
	 */
	ArticleVO getArticleByIdWeb(ArticleParam articleParam);
	
	/**
	 * 浏览指定文章，并在浏览表里添加一条纪录
	 * @param articleParam
	 * @return
	 */
	Map<String,Object>  viewArticle(ArticleParam articleParam);
	/**
	 * 患者打开文章时给加1（统计浏览量）
	 * @param articleParam
	 * @return
	 */
	String  statisticsArticleVisit(String articleId,Integer userId);
	
	/**
	 * 更新文章
	 * @param articleParam
	 * @return
	 */
	String  updateArticle(ArticleParam articleParam);
	
	/**
	 * 取消置顶
	 * @param articleParam
	 * @return
	 */
	String cancleTopArticle(ArticleParam articleParam);
	
	/**
	 * 置顶文章
	 * @param articleParam
	 * @return
	 */
	Map<String,Object> TopArticle(ArticleParam articleParam);
	
	/**
	 * 置顶文章上移
	 * @param up
	 * @param down
	 * @return
	 */
	Map<String,Object>  topArticleUp(String up, String down);
	
	/**
	 * 取消收藏
	 * @param articleParam
	 * @return
	 */
	Map<String,Object> cancleCollectArticle(ArticleParam articleParam);
	
	/**
	 * 收藏文章
	 * @param articleParam
	 * @return
	 */
	String collectArticle(ArticleParam articleParam);
	
	/**
	 * 根据置顶优先级获取所有置顶文章
	 * @return
	 */
	List<ArticleVO> getAllTopArticle(ArticleParam articleParam);
	
	/**
	 * 获取指定医生的收藏文档
	 * @param articleParam
	 * @return
	 */
	PageVO getCollectArticleByDoctorId(ArticleParam articleParam);
	
	/**
	 * 根据关键字查询文章
	 * @param articleParam
	 * @return
	 */
	PageVO searchArticleByKeyWord(ArticleParam articleParam);
	
	/**
	 * 根据关键字查询文章
	 * @param articleParam
	 * @return
	 */
	PageVO searchArticleByTag(ArticleParam articleParam);
	
	/**
	 * 获取热门文档
	 * @param articleParam
	 * @param type
	 * @return
	 */
	PageVO getHotArticle(ArticleParam articleParam,String type);
	
	
	/**
	 * 获取最新文档
	 * @param articleParam
	 * @param type
	 * @return
	 */
	PageVO getNewArticle(ArticleParam articleParam,String type);
	
	PageVO getArticleByDisease(ArticleParam articleParam);
	
	String addArticle(ArticleParam articleParam);
	
	Map<String,Object> delArticle(ArticleParam articleParam);
	
	Map<String,Object> getDisaseTreeByParam(Integer createType,String groupID);
	
	Map<String ,Object> updateUseCount(ArticleParam articleParam);
	
	List<DiseaseTypeVO> getTypeByParent(String parentId);
	
	void sendArticleToUser(String fromUserId,String gid,String url) throws HttpApiException;
	
	String addArticleByUrl(ArticleParam articleParam,String url);
	
	public void addDTG(String doct,String gid);
	
	public void sendArticleToUser(ArticleVO articleVO,String gid,String fromUserId) throws HttpApiException;
	
	/***begin add  by  liwei  2016年1月18日   支持多集团********/
	Map<String,Object> getDisaseTreeByUserId(Integer createType,List<String> doctorIds);
	
	/**
	 * 获取热门文档(多集团)  liwei
	 * @param articleParam
	 * @param type
	 * @return
	 */
	PageVO getHotArticleForMoreGroup(ArticleParam articleParam,String type);
	
	/**
	 * 获取最新文档(多集团) 
	 * @param articleParam
	 * @param type
	 * @return
	 */
	public PageVO getNewArticleForMoreGroup(ArticleParam articleParam, String type);
	
	PageVO getArticleByDiseaseForMoreGroup(ArticleParam articleParam);
	
	/***end add  by  liwei  2016年1月18日********/
	
	public void updateVisitNum();

}
