package com.dachen.health.teachCenter.dao;

import java.util.List;
import java.util.Map;

import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.teachCenter.entity.po.ArticleCollect;
import com.dachen.health.teachCenter.entity.po.ArticleTop;
import com.dachen.health.teachCenter.entity.po.ArticleVisit;
import com.dachen.health.teachCenter.entity.vo.ArticleVO;

public interface IArticleDao {

	ArticleVO getArticleById(String articleId);

	Map<String, Object> getHotArcticleByParam(ArticleParam articleParam);

	Map<String, Object> getNewArcticleByParam(ArticleParam articleParam);

	Map<String, Object> getArticleByDisease(ArticleParam articleParam);

	Map<String, Object> searchKeywordByParam(ArticleParam articleParam);

	Map<String, Object> searchTagByParam(ArticleParam articleParam);

	boolean updateArticle(ArticleParam articleParam);

	ArticleVO saveArticle(ArticleParam articleParam);

	boolean delArticle(ArticleParam articleParam);

	boolean delArticleByLogic(String articleId);

	void saveArticleVisitor(ArticleParam articleParam);

	boolean delArticleVisitor(String articleId);

	Map<String, ArticleCollect> getCollectArticleByParam(
			ArticleParam articleParam);

	boolean delCollectArticleById(ArticleParam articleParam);

	boolean saveCollectArticle(ArticleParam articleParam);

	int getCollectArticle(ArticleParam articleParam,
			Map<String, ArticleCollect> collectMap);

	void setCollectArticle(ArticleParam articleParam,
			Map<String, ArticleCollect> collectMap, ArticleVO vo);

	boolean delTopArticle(ArticleParam articleParam);

	boolean saveTopArticle(ArticleParam articleParam);

	boolean updateTopArticle(ArticleParam articleParam);

	List<ArticleTop> getAllTopArticle();
	
	List<ArticleTop> getAppTopArticle();

	ArticleTop getTopArticleById(String articleId);

	boolean updateTopArticle(ArticleTop articleTop);

	Map<String, Object> getArticleByDoctId(ArticleParam articleParam);

	Map<String, Object> updateArticleUseCount(ArticleParam articleParam);

	Map<String, User> getAllUser(Integer... uid);

	Map<String, DiseaseTypeVO> getAllDisease(String id);

	Map<String, Object> findArticleById(String articleId);

	void addDActToGAct(String doctorId, String groupId);

	public ArticleVO getArticleByIdNoEnabled(String articleId);

	public List<ArticleVO> getArcticleListByCreatorId(String createrId);

	/*** begin add by liwei 2016年1月18日   支持多集团********/
	public Map<String, Object> getHotArcticleForMoreGroup(ArticleParam articleParam);

	public Map<String, Object> getNewArcticleForMoreGroup(ArticleParam articleParam);
	
	Map<String, Object> getArticleByDiseaseForMoreGroup(ArticleParam articleParam);

	/*** end add by liwei 2016年1月18日 ********/
	/**
	 * 患者打开文章时给加1
	 * @param articleParam
	 * @return
	 */
	String  saveVisitTimes(String articleId,Integer userId);
	/**
	 * 查看相同的一篇文章同一个人是否已经浏览过 
	 * @param articleId
	 * @param userId
	 * @return
	 */
	ArticleVisit  isVisit(String articleId,Integer userId);
	
	/**
	 * 根据疾病ID获取对应以及集团ID获取对应的文章ID列表
	 * @param groupId
	 * @param diseaseId
	 * @return 
	 */
	List<String> getArticleIds(String diseaseId,String... groupId);
	
	/**
	 * 
	 * @param ids 根据id列表获取对应的文章
	 * @param articleParam
	 * @return
	 */
	Map<String, Object> getArticleByIds(List<String> ids,ArticleParam articleParam);
	
	
	void updateVisitNum();
	
	Map<String, Long> getVisitCount(List<String> list,ArticleParam articleParam, boolean page);
	
}
