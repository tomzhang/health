package com.dachen.health.teachCenter.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.http.FileUploadUtil;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.constant.ArticleEnum;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.teachCenter.dao.IArticleDao;
import com.dachen.health.teachCenter.dao.IDiseaseTreeDao;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.teachCenter.entity.po.ArticleCollect;
import com.dachen.health.teachCenter.entity.po.ArticleTop;
import com.dachen.health.teachCenter.entity.po.ArticleVisit;
import com.dachen.health.teachCenter.entity.po.GroupDisease;
import com.dachen.health.teachCenter.entity.vo.ArticleVO;
import com.dachen.health.teachCenter.service.IArticleService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.JsoupUtil;
import com.dachen.util.ParserHtmlUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.tree.ExtTreeNode;

@Service
public class ArticleServiceImpl implements IArticleService {
	private final Log log = LogFactory.getLog(ArticleServiceImpl.class);

	@Autowired
	private IArticleDao articleDao;
	@Autowired
	private IDiseaseTreeDao diseaseTreeDao;
	@Autowired
	private ICommonGroupDoctorService groupDoctorService;

	@Autowired
	private IBaseDataService baseDataService;

	@Autowired
	private IMsgService msgService;

	@Resource
	UserManager userManager;
	
	@Resource
	private DiseaseTypeRepository diseaseTypeRepository;

	@Autowired
	FileUploadUtil fileUpload;
	
	@Override
	public ArticleVO getArticleById(String articleId) {
		if (StringUtil.isEmpty(articleId)) {
			throw new ServiceException("文章ID都不能为空");
		}
		ArticleVO vo = articleDao.getArticleByIdNoEnabled(articleId);
		if (vo != null) {
			ArticleParam articleParam = new ArticleParam();
			articleParam.setArticleId(vo.getId());
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			Map<String, ArticleCollect> collectMap = articleDao
					.getCollectArticleByParam(articleParam);
			// vo.setCollect(articleDao.getCollectArticle(articleParam,collectMap));
			articleDao.setCollectArticle(articleParam, collectMap, vo);
		}

		return vo;
	}

	@Override
	public ArticleVO getArticleByIdWeb(ArticleParam articleParam) {
		String articleId = articleParam.getArticleId();
		if (StringUtil.isEmpty(articleId)) {
			throw new ServiceException("文章ID都不能为空");
		}
		ArticleVO vo = articleDao.getArticleById(articleId);
		if (vo != null) {
			Integer createType = articleParam.getCreateType();
			String uid = String.valueOf(ReqUtil.instance.getUserId());
//			ArticleParam param = new ArticleParam();
//			param.setArticleId(articleId);
			if (createType != null) {
				if (createType == 1) {
					articleParam.setCreaterId("system");
				} else if (createType == 2 || createType == 4) {
					articleParam.setGroupId(articleParam.getCreaterId());
				} else if (articleParam.getCreateType() == 0 || articleParam.getCreateType() == 3 || articleParam.getCreateType() == 5 || articleParam.getCreateType() == 6) {
					articleParam.setCreaterId(uid);
				}
			} else {
				articleParam.setCreaterId("0");
			}
			Map<String, ArticleCollect> collectMap = articleDao
					.getCollectArticleByParam(articleParam);
			// vo.setCollect(articleDao.getCollectArticle(param,collectMap));
			articleDao.setCollectArticle(articleParam, collectMap, vo);
		}

		return vo;
	}

	@Override
	public Map<String, Object> viewArticle(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			throw new ServiceException("文章ID都不能为空");
		}

		if (StringUtil.isEmpty(articleParam.getCreaterId())) {
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			articleParam.setVisitorId(String.valueOf(ReqUtil.instance.getUserId()));
		}
		ArticleVO articleVO  = getArticleById(articleParam.getArticleId());;
//		if (articleParam.getCreateType() == null) {
//			articleVO = getArticleById(articleParam.getArticleId());
//		} else {
//			articleVO = getArticleByIdWeb(articleParam);
//		}
		if (articleVO == null) {
			map.put("status", false);
			map.put("msg", "找不到对应文章");
			return map;
		}
		articleDao.saveArticleVisitor(articleParam);

		map.put("collect", articleVO.isCollect());
		map.put("top", articleVO.isTop());
		map.put("url", articleVO.getUrl());
		map.put("status", true);
		return map;
	}

	@Override
	public PageVO getCollectArticleByDoctorId(ArticleParam articleParam) {
		articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("collectTime");
		}
		articleParam.setCreateType(ArticleEnum.CollecterType.doctor.getIndex());

		// 先从收藏表里查出所有收藏文章ID
		Map<String, Object> map = articleDao.getArticleByDoctId(articleParam);
		Map<String, ArticleCollect> collectMap = articleDao
				.getCollectArticleByParam(new ArticleParam());

		List<ArticleVO> aList = new ArrayList<ArticleVO>();
		List<ArticleCollect> list = (List<ArticleCollect>) map.get("list");
		// 根据IDS查出对应文章列表
		int i = 0;
		if (!list.isEmpty()) {
			for (ArticleCollect collect : list) {
				ArticleVO vo = articleDao
						.getArticleById(collect.getArticleId());
				if (vo != null) {
					vo.setCollectTime(collect.getCollectTime());
					articleParam.setArticleId(collect.getArticleId());
					// vo.setCollect(
					// articleDao.getCollectArticle(articleParam,collectMap));
					articleDao.setCollectArticle(articleParam, collectMap, vo);
					aList.add(vo);
				} else {
					i++;
				}
			}
		}

		PageVO page = new PageVO();
		page.setPageData(aList);
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());
		long total = Integer.parseInt(map.get("count").toString()) - i;
		page.setTotal(total);
		return page;
	}

	@Override
	public PageVO searchArticleByKeyWord(ArticleParam articleParam) {
		if (StringUtil.isEmpty(articleParam.getTitle())) {
			throw new ServiceException("关键字不能为空");
		}
		PageVO page = new PageVO();
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}
		if (articleParam.getPageSize() == 0) {
			articleParam.setPageSize(20);
		}
		
		
//		if (articleParam.getCreateType() == 0) {
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//		} else if (articleParam.getCreateType() == 1) {
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//		} else if (articleParam.getCreateType() == 2
//				|| articleParam.getCreateType() == 4) {
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//		} else if (articleParam.getCreateType() == 3
//				|| articleParam.getCreateType() == 5
//				|| articleParam.getCreateType() == 6) {
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//		} else {
//			return page;
//		}
		
		
		String uid = String.valueOf(ReqUtil.instance.getUserId());
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			// 集团查平台和集团一起的
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system", articleParam.getGroupId());
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex() || articleParam.getCreateType() == 4 || articleParam.getCreateType() == 5) {
			// 1:平台查平台 4：集团查平台 5：医生查平台数据
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == 2 || articleParam.getCreateType() == 6
				) {
			// 2：集团查集团，6：医生查集团数据
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),articleParam.getGroupId());
		}
		articleParam.setIds(ids);
		
		// 根据关键字去文章表的title字段来匹配，如果是根据发布时间排序则直接在文章表里查出对应文章，如果是根据浏览量则再去浏览表查
		Map<String, Object> map = articleDao.searchKeywordByParam(articleParam);

		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}

	@Override
	public String updateArticle(ArticleParam articleParam) {
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			throw new ServiceException("文章ID不能为空");
		}

		if (articleParam.getIsShare() == null) {
			throw new ServiceException("是否分享不能为空");
		}
		ArticleVO vo = articleDao.getArticleById(articleParam.getArticleId());
		if (vo == null) {
			return "false";
		}
		String newDisease = articleParam.getDiseaseId();
		if (StringUtil.isEmpty(newDisease)) {
			newDisease = "QT";
		} else {
			newDisease = newDisease.toUpperCase();
			if (newDisease.length() > 4) {
				newDisease = newDisease.substring(0, 4);
			}
		}
		articleParam.setDiseaseId(newDisease);

		String oldDisease = vo.getDiseaseId().trim().toUpperCase();

		GroupDisease system = new GroupDisease();
		system.setGroupId("system");
		List<String> list = new ArrayList<String>();
		list.add(articleParam.getArticleId());
		system.setArticleId(list);
		GroupDisease group = null;
		if (vo.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			group = new GroupDisease();
			group.setGroupId(vo.getCreaterId());
			group.setArticleId(list);
		}
		if (oldDisease.equals(newDisease)) {
			// 病种没变
			system.setDiseaseId(newDisease);
			// if(articleParam.getIsShare() == vo.getIsShare()){//没有任何变化
			// }else
			if (articleParam.getIsShare() != vo.getIsShare()) {// 有变化
				if (articleParam.getIsShare() == 1) {// 对平台和集团病种树数量加一
					updateDiseaseTree(system, "+");
				} else if (articleParam.getIsShare() == 0) {// 对平台病种树数量减一,集团不变
					updateDiseaseTree(system, "-");
				}
			}
		} else {
			// 病种变化
			// 对平台树数量改变
			system.setDiseaseId(oldDisease);
			updateDiseaseTree(system, "-");// 老病种减一
			if (articleParam.getIsShare() == 1) {// 新病种分享加一
				system.setDiseaseId(newDisease);
				updateDiseaseTree(system, "+");
			}
			// 对集团树数量改变
			ArticleParam param = new ArticleParam();
			param.setArticleId(vo.getId());
			Map<String, ArticleCollect> map = articleDao
					.getCollectArticleByParam(param);
			// 收藏筛选条件collectorType=2,articleId = vo.getId();得到集团ID
			List<String> groupIdList = new ArrayList<String>();
			Iterator<Entry<String, ArticleCollect>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, ArticleCollect> entry = iterator.next();
				ArticleCollect collect = entry.getValue();
				if (collect.getCollectorType() == 2) {
					groupIdList.add(collect.getCollectorId());
				}
			}

			GroupDisease gd = new GroupDisease();
			gd.setDiseaseId(oldDisease);
			gd.setArticleId(list);
			// 然后根据集团ID和老病种数量减一
			diseaseTreeDao.updateBatchGroup(gd, groupIdList, "-");
			// 然后根据集团ID和新病种数量加一
			gd.setDiseaseId(newDisease);
			diseaseTreeDao.updateBatchGroup(gd, groupIdList, "+");

		}

		if (articleParam.getIsShow() == null) {
			articleParam.setIsShow(1);
		}
		if (vo.getCreateType() != 2) {
			articleParam.setAuthor(vo.getAuthor());
		}
		// 更新对应的文档
		String host = PropertiesUtil.getContextProperty("host");
		String port = PropertiesUtil.getContextProperty("port");
		String path = PropertiesUtil.getContextProperty("path");
		String small = "";
		small = "http://" + host + ":" + port + "/html/" + small;
		String url = "";
		path = path + vo.getId() + ".html";
		File html = new File(path);

		if (StringUtil.isEmpty(articleParam.getDescription())
				&& !StringUtil.isEmpty(articleParam.getContent())) {
			String content = articleParam.getContent();
			// 取首段落
			int pEnd = content.indexOf("</p>") + 4;
			int bEnd = content.indexOf("<br/>") + 4;
			int nEnd = content.indexOf("</n>") + 4;
			int end = pEnd;
			if (end > bEnd) {
				end = bEnd;
			}
			if (end > nEnd) {
				end = nEnd;
			}

			if (end > content.length()) {
				end = content.length();
			}
			content = content.substring(0, end);

			content = ParserHtmlUtil.delHTMLTag(content);// 去标签
			content = ParserHtmlUtil.delTransferredCode(content);// 去转义符
			int size = content.length();
			if (size > 100) {
				size = 100;
				articleParam.setDescription(content.substring(0, size)
						+ "......");
			} else {
				articleParam.setDescription(content.substring(0, size));
			}

		}

		try {
			// 根据模板生成一份静态html文件保存到服务器上并记下这个文件的url，
			File tempFile = new File(this.getClass().getResource("/temp/preview/preview.html").getPath());
			FileReader reader = new FileReader(tempFile);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer sf = new StringBuffer();
			String str = "";
			while ((str = br.readLine()) != null) {

				if (str.contains("{{title}}")) {
					str = str.replace("{{title}}", articleParam.getTitle());
				}
				if(0==vo.getIsShow()){
					str = str.replace("{{tag}}", "");
					str = str.replace("{{imgSrc}}","");
				}else{
					if (str.contains("{{tag}}")) {
						String temp = "";
						if (articleParam.getTags() != null) {
							temp = "<div class=\"disease\">";
							for (String s : articleParam.getTags()) {
								DiseaseTypeVO v = diseaseTypeRepository.getDiseaseTypeTreeById(s);
								if(v !=null){
									temp += "<div class=\"tag\">" + v.getName() + "</div>";
								}
							}
							temp = temp + "</div>";
						}
						str = str.replace("{{tag}}", temp);
					}
					if (str.contains("{{imgSrc}}")) {
						if (!StringUtil.isEmpty(articleParam.getCopyPath())) {
							str = str.replace("{{imgSrc}}","<img src=\""+articleParam.getCopyPath()+"\" class=\"img-responsive font-img\">");
						} else {
							str = str.replace("<img src=\"{{imgSrc}}\" class=\"img-responsive font-img\">","");
						}
					}
				}
				if (str.contains("{{author}}")) {
					if (vo.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
						str = str.replace("{{author}}", "玄关患教中心");
					} else if (vo.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
						String name = userManager.getGroupNameById(vo.getGroupId());
						str = str.replace("{{author}}", name);
					} else if (vo.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
						User user = userManager.getUser(Integer.valueOf(vo.getAuthor()));
						str = str.replace("{{author}}", user == null ? "":user.getName());
					}
//					str = str.replace("{{author}}", vo.getAuthorName() == null ? "":vo.getAuthorName());
				}

				if (str.contains("{{content}}")) {
					if (articleParam.getContent() != null) {
						str = str.replace("{{content}}",
								articleParam.getContent());
					} else {
						str = str.replace("{{content}}", "");
					}
				}
				if (str.contains("{{date}}")) {
					final String dateStr = DateUtil.formatDate2Str(vo.getCreatTime(),
							DateUtil.FORMAT_YYYY_MM_DD);
					str = str.replace("{{date}}", dateStr);
				}
				if(str.contains("{{summary}}")){
					str = str.replace("{{summary}}", articleParam.getDescription());
				}
				sf.append(str);
			}
			reader.close();
			// 输出 (输入流)
			if (html.exists()) {
				html.delete();
				url = vo.getUrl();
			} else {
				html.createNewFile();
				url = "http://" + host + ":" + port + "/html/template/";
				url = url + vo.getId() + ".html";
			}

			FileOutputStream out = new FileOutputStream(html);
			out.write(sf.toString().getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}

		articleParam.setUrl(url);
		if (StringUtil.isEmpty(articleParam.getCopy_small())) {
			articleParam.setCopy_small(small);
		}
		boolean result = articleDao.updateArticle(articleParam);
		return String.valueOf(result);
	}

	@Override
	public Map<String, Object> cancleCollectArticle(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			map.put("status", false);
			map.put("msg", "文章ID不能为空");
			return map;
		}
		ArticleVO vo = getArticleById(articleParam.getArticleId());
		if (vo == null) {
			map.put("status", false);
			map.put("msg", "没有找到对应的文章");
			return map;
		}
		GroupDisease param = new GroupDisease();
		param.setDiseaseId(vo.getDiseaseId());
		if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
			// 平台无收藏，则无取消收藏
			map.put("status", false);
			map.put("msg", "平台无收藏，则无取消收藏");
			return map;
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			if (StringUtil.isEmpty(articleParam.getCreaterId())) {
				map.put("status", false);
				map.put("msg", "集团ID不能为空");
				return map;
			}
			param.setGroupId(articleParam.getCreaterId());
			List<String> list = new ArrayList<String>();
			list.add(articleParam.getArticleId());
			param.setArticleId(list);
			updateDiseaseTree(param, "-");// 对应树数量减一
		} else {
			if(StringUtil.isEmpty(articleParam.getCreaterId())){
				articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			}
			articleParam.setCreateType(ArticleEnum.CollecterType.doctor.getIndex());
		}
		Map<String, ArticleCollect> collectMap = articleDao
				.getCollectArticleByParam(new ArticleParam());
		int type = articleDao.getCollectArticle(articleParam, collectMap);
		if (type == 0) {
			map.put("status", false);
			map.put("msg", "该文章没有被您收藏过，无法取消收藏");
			return map;
		} else if (type == 2) {
			map.put("status", false);
			map.put("msg", "该文章您自建的，无法取消收藏");
			return map;
		}
		articleParam.setCollectType(ArticleEnum.CollectType.collect.getIndex());
		map.put("status", articleDao.delCollectArticleById(articleParam));
		return map;
	}

	@Override
	public String collectArticle(ArticleParam articleParam) {
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			throw new ServiceException("文章ID不能为空");
		}
		String result = "true";
		ArticleVO vo = getArticleById(articleParam.getArticleId());
		if (vo == null) {
			System.err.println("没有找到对应的文章");
			return "false";
		}
		GroupDisease param = new GroupDisease();
		param.setDiseaseId(vo.getDiseaseId());
		List<String> list = new ArrayList<String>();
		list.add(articleParam.getArticleId());
		param.setArticleId(list);
		String gid = null;
		//医生有可能会加入多个集团
		List<String> listGid = new ArrayList<String>();
		Map<String, ArticleCollect> collectMap = articleDao.getCollectArticleByParam(new ArticleParam());
		if (articleParam.getCreateType() == ArticleEnum.CollecterType.system
				.getIndex()) {
			return result;
			// 平台不存在收藏
		} else if (articleParam.getCreateType() == ArticleEnum.CollecterType.group
				.getIndex()) {
			if (StringUtil.isEmpty(articleParam.getCreaterId())) {
				System.err.println("集团ID不能为空");
				return "false";
			}
			// 收藏者ID就是集团ID在外层传进来了
			gid = articleParam.getCreaterId();
			if (articleDao.getCollectArticle(articleParam, collectMap) == 0) {
				// 保存收藏
				articleParam.setCollectType(ArticleEnum.CollectType.collect.getIndex());
				articleDao.saveCollectArticle(articleParam);
				// 更新集团树数量加一
				param.setGroupId(gid);
				updateDiseaseTree(param, "+");
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CollecterType.doctor.getIndex()) {
			if(StringUtil.isEmpty(articleParam.getCreaterId())){
				articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			}
			if (articleDao.getCollectArticle(articleParam, collectMap) == 0) {
				// 保存收藏
				articleParam.setCollectType(ArticleEnum.CollectType.collect.getIndex());
				
				articleDao.saveCollectArticle(articleParam);
			}
			
			listGid = groupDoctorService.getGroupListIdByUser(articleParam.getCreaterId());
			if(listGid.size()>0&&null!=listGid){
				for (String listgid : listGid) {
					ArticleParam group = new ArticleParam();
					group.setCreaterId(listgid);
					group.setArticleId(articleParam.getArticleId());
					group.setCollectType(ArticleEnum.CollectType.collect.getIndex());
					group.setCreateType(ArticleEnum.CollecterType.group.getIndex());
					if (articleDao.getCollectArticle(group, collectMap) == 0) {
						articleDao.saveCollectArticle(group);// 保存对应集团
						// 更新集团树数量加一
						param.setGroupId(listgid);
						updateDiseaseTree(param, "+");
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public String cancleTopArticle(ArticleParam articleParam) {
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			throw new ServiceException("置顶文章ID不能为空");
		}
		boolean result = articleDao.delTopArticle(articleParam);
		if (result) {
			articleParam.setTop(false);
			articleDao.updateTopArticle(articleParam);
		}

		return String.valueOf(result);
	}

	@Override
	public Map<String, Object> TopArticle(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			throw new ServiceException("置顶文章ID不能为空");
		}

		ArticleVO vo = getArticleById(articleParam.getArticleId());
		if (vo == null) {
			map.put("status", false);
			map.put("msg", "没有找到该文章");
			return map;

		}
		if (StringUtil.isEmpty(vo.getCopyPath())) {
			map.put("status", false);
			map.put("msg", "该文章没有封面，不能置顶");
			return map;
		}
		List<ArticleTop> list = articleDao.getAllTopArticle();
		if (list.size() >= ArticleEnum.TOP_SIZE) {
			map.put("status", false);
			map.put("msg", "置顶文章数量已达到上限");
			return map;
		}

		// 设置置顶排序
		if (list.isEmpty()) {
			articleParam.setPriority(1);
		} else {
			articleParam
					.setPriority(list.get(list.size() - 1).getPriority() + 1);
		}

		if (articleDao.saveTopArticle(articleParam)) {
			articleParam.setTop(true);
			articleDao.updateTopArticle(articleParam);
			map.put("status", true);
		}
		return map;
	}

	@Override
	public List<ArticleVO> getAllTopArticle(ArticleParam articleParam) {
		
		List<ArticleVO> vList = new ArrayList<ArticleVO>();//玄关
		List<ArticleVO> bdList = new ArrayList<ArticleVO>();//博德
		
		List<ArticleTop> list = articleDao.getAppTopArticle();
		for (ArticleTop top : list) {
			ArticleVO vo = articleDao.getArticleById(top.getArticleId());
			if (vo != null) {
				vo.setPriority(top.getPriority());
				if(StringUtil.isNotBlank(articleParam.getGroupId()) && vo.getGroupId().equals(articleParam.getGroupId())){
					bdList.add(vo);
				}
				vList.add(vo);
			}
		}
		if(StringUtil.isNotBlank(articleParam.getGroupId())){
			return bdList;
		}
		return vList;
	}

	@Override
	public Map<String, Object> topArticleUp(String up, String down) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isEmpty(up)) {
			throw new ServiceException("上移文章ID不能为空");
		}
		if (StringUtil.isEmpty(down)) {
			throw new ServiceException("被下移文章ID不能为空");
		}
		ArticleTop topArt = articleDao.getTopArticleById(up);
		if (topArt == null) {
			map.put("status", false);
			map.put("msg", "该文章没有被置顶");
			return map;
		}
		ArticleTop downArt = articleDao.getTopArticleById(down);
		if (downArt == null) {
			map.put("status", false);
			map.put("msg", "被下移文章文章没有被置顶");
			return map;
		}
		int priority = topArt.getPriority();
		topArt.setPriority(downArt.getPriority());
		articleDao.updateTopArticle(topArt);
		downArt.setPriority(priority);
		articleDao.updateTopArticle(downArt);
		map.put("status", true);
		return map;
	}

	@Override
	public PageVO getHotArticle(ArticleParam articleParam, String type) {
//		if (articleParam.getCreateType() == 0) {
//			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(String.valueOf(ReqUtil.instance.getUserId())));
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
//				.getIndex()) {
//			articleParam.setCreaterId("system");
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
//				.getIndex()) {
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(String.valueOf(ReqUtil.instance.getUserId())));
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
//				.getIndex()) {
//			String uid = String.valueOf(ReqUtil.instance.getUserId());
//			articleParam.setCreaterId(uid);
//			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(uid));
//		}else if(articleParam.getCreateType() == 7){
//			//针对博德
//			if(StringUtil.isEmpty(articleParam.getCreaterId())){
//				throw new ServiceException("createrId不能为空");
//			}
//			List<String> ids = new ArrayList<String>();
//			ids.add(articleParam.getCreaterId());
//			articleParam.setGroupIds(ids);
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//		}
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 list.add("system");
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
			
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
			articleParam.setCreaterId("system");
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			String[] str = (String[]) list.toArray(new String[list.size()]);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
		}else if(articleParam.getCreateType() == 7){
			//针对博德
			if(StringUtil.isEmpty(articleParam.getCreaterId())){
				throw new ServiceException("createrId不能为空");
			}
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),new String[]{articleParam.getCreaterId()});
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		}

		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (type != null && type.equals("week")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			articleParam.setCreatTime(cal.getTime().getTime());
		}
		
//		Map<String, Object> map = 
//				articleDao.getHotArcticleByParam(articleParam);
		Map<String, Object> map = articleDao.getArticleByIds(ids, articleParam);
		
		List<ArticleVO> list = (List<ArticleVO>) map.get("list");
//		Map<String, Long> visitMap = articleDao.getVisitCount(ids, articleParam, false);
		List<ArticleVO> resultList = new ArrayList<ArticleVO>();
		resultList.addAll(sortByVist(ids,articleParam,list));
		PageVO page = new PageVO();
		page.setPageData(resultList);
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}

	public List<ArticleVO> sortByVist(List<String> ids,ArticleParam articleParam,List<ArticleVO> list){
		List<ArticleVO> resultList = new ArrayList<ArticleVO>();
		Map<String, Long> visitMap = articleDao.getVisitCount(ids, articleParam, false);
		Iterator<Entry<String, Long>> iterator = visitMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Long> entry = iterator.next();
			String key = entry.getKey();
			Long value = entry.getValue();
			for(ArticleVO vo :list){
				if(vo.getId().equals(key)){
					vo.setVisitCount(value);
					resultList.add(vo);
				}
			}
		}
		return resultList;
	}
	
	@Override
	public PageVO getNewArticle(ArticleParam articleParam, String type) {
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}
		if (articleParam.getPageSize() == 0) {
			articleParam.setPageSize(20);
		}
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 list.add("system");
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
			
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
			articleParam.setCreaterId("system");
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			String[] str = (String[]) list.toArray(new String[list.size()]);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
		}else if(articleParam.getCreateType() == 7){
			//针对博德
			if(StringUtil.isEmpty(articleParam.getCreaterId())){
				throw new ServiceException("createrId不能为空");
			}
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),new String[]{articleParam.getCreaterId()});
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		}

//		Map<String, Object> map = articleDao.getNewArcticleByParam(articleParam);
		Map<String, Object> map = articleDao.getArticleByIds(ids, articleParam);
		PageVO page = new PageVO();
		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}

	@Override
	public Map<String, Object> getDisaseTreeByParam(Integer createType,
			String groupId) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<GroupDisease> result = new ArrayList<GroupDisease>();
		List<GroupDisease> all = new ArrayList<GroupDisease>();

		double sum = 0;// 全部文档数量
		Map<String, GroupDisease> map = new HashMap<String, GroupDisease>();
		List<GroupDisease> list = new ArrayList<GroupDisease>();
		Set<GroupDisease> voSet = new HashSet<GroupDisease>();

		if (createType == 0) {// 平台和集团所有的
			Map<String, GroupDisease> tempMap = new HashMap<String, GroupDisease>();// 代表有数据节点
			List<GroupDisease> system = diseaseTreeDao
					.getGroupDiseaseTree("system");
			List<GroupDisease> doctor = new ArrayList<GroupDisease>();
			if (!StringUtil.isEmpty(groupId)) {
				doctor = diseaseTreeDao.getGroupDiseaseTree(groupId);
			}
			system.addAll(doctor);
			for (GroupDisease temp : system) {// 集团
				String diseaseId = temp.getDiseaseId();
				GroupDisease gd = tempMap.get(diseaseId);
				if (gd == null) {
					gd = temp;
				}
				List<String> tList = temp.getArticleId();
				List<String> gList = gd.getArticleId();

				Set<String> sList = new HashSet<String>();
				if (tList != null) {
					sList.addAll(tList);
				}
				if (gList != null) {
					sList.addAll(gList);
				}
				tList = new ArrayList<String>();
				tList.addAll(sList);
				gd.setArticleId(tList);
				gd.setCount(tList.size());
				tempMap.put(diseaseId, gd);
			}

			all.addAll(diseaseTreeDao.getAllGroupDisease("system"));
			all.addAll(diseaseTreeDao.getAllGroupDisease(groupId));
			for (GroupDisease temp : all) {
				if (tempMap.get(temp.getDiseaseId()) != null) {
					temp = tempMap.get(temp.getDiseaseId());
				}
				map.put(temp.getDiseaseId(), temp);
			}
			Iterator<Entry<String, GroupDisease>> iterator = tempMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, GroupDisease> temp = iterator.next();
				GroupDisease value = temp.getValue();
				sum += value.getCount();
				addNode(voSet, value, map);

			}
			list.addAll(voSet);

		} else if (createType == 1) {// 平台的
			List<GroupDisease> system = diseaseTreeDao
					.getGroupDiseaseTree("system");// 有数据节点
			all.addAll(diseaseTreeDao.getAllGroupDisease("system")); // 所有节点

			for (GroupDisease temp : all) {
				sum += temp.getCount();
				map.put(temp.getDiseaseId(), temp);
			}
			for (GroupDisease vo : system) {
				addNode(voSet, vo, map);
			}
			list.addAll(voSet);
		} else if (createType == 2) {// 集团的
			if (StringUtil.isEmpty(groupId)) {
				mapResult.put("total", sum);
				mapResult.put("tree", result);
				return mapResult;
			}
			List<GroupDisease> doctor = diseaseTreeDao
					.getGroupDiseaseTree(groupId);// 有数据节点
			all.addAll(diseaseTreeDao.getAllGroupDisease(groupId));// 所有节点
			for (GroupDisease temp : all) {
				sum += temp.getCount();
				map.put(temp.getDiseaseId(), temp);
			}

			for (GroupDisease vo : doctor) {
				addNode(voSet, vo, map);
			}
			list.addAll(voSet);
		}

		for (GroupDisease temp : list) {
			if (map.containsKey(temp.getParent())) {
				GroupDisease partent = map.get(temp.getParent());
				List<GroupDisease> children = partent.getChildren();
				if (partent.getParent().equals("0")) {
					if (children == null) {
						children = new ArrayList<GroupDisease>();
					}
					children.add(temp);
				}
				partent.setChildren(children);
				if (temp.getCount() == null) {
					temp.setCount(0);
				}
				if (partent.getCount() == null) {
					partent.setCount(0);
				}
				partent.setCount(partent.getCount() + temp.getCount());
			}
			if (temp.getParent().equals(ExtTreeNode.ROOT_NODE_PARENT_ID)) {
				result.add(temp);
			}
		}

		mapResult.put("total", sum);
		mapResult.put("tree", sortList(result));
		return mapResult;
	}

	@Override
	public PageVO searchArticleByTag(ArticleParam articleParam) {
		if (articleParam.getTags() == null) {
			throw new ServiceException("标签不能为空");
		}
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}

		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}
		if (articleParam.getPageSize() == 0) {
			articleParam.setPageSize(20);
		}
		PageVO page = new PageVO();
		if (articleParam.getCreateType() == 0) {
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		} else if (articleParam.getCreateType() == 1) {
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		} else if (articleParam.getCreateType() == 2
				|| articleParam.getCreateType() == 4) {
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		} else if (articleParam.getCreateType() == 3
				|| articleParam.getCreateType() == 5
				|| articleParam.getCreateType() == 6) {
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		} else {
			return page;
		}

		// 根据关键字去文章表的title字段来匹配，如果是根据发布时间排序则直接在文章表里查出对应文章，如果是根据浏览量则再去浏览表查
		Map<String, Object> map = articleDao.searchTagByParam(articleParam);

		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}

	@Override
	public Map<String, Object> updateUseCount(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isEmpty(articleParam.getArticleId())) {
			map.put("status", false);
			map.put("msg", "文章ID不能为空");
			return map;
		}
		return articleDao.updateArticleUseCount(articleParam);
	}

	private String listToString(List<String> list){
		if(list == null || list.size() == 0){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(String str : list){
			sb.append(str).append(",");
		}
		return sb.substring(0,sb.length()-1).toString();
	}
	@Override
	public String addArticle(ArticleParam articleParam) {
		String host = PropertiesUtil.getContextProperty("host");
		String port = PropertiesUtil.getContextProperty("port");
		String path = PropertiesUtil.getContextProperty("path");
		String url = "http://" + host + ":" + port + "/html/template/";
		String small = "http://" + host + ":" + port + "/html/";

		String uid = null;
		String gid = null;
		//医生加入多集团时引用
		List<String> listGid = new ArrayList<String>();
		
		String diseaseId = articleParam.getDiseaseId();
		if (!StringUtil.isEmpty(diseaseId)) {
			diseaseId = diseaseId.toUpperCase();
			if (diseaseId.length() > 4) {
				diseaseId = diseaseId.substring(0, 4);
			}
		} else {
			diseaseId = "QT";
		}
		articleParam.setDiseaseId(diseaseId);
		if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			uid = "system";
			articleParam.setIsShare(1);
			articleParam.setAuthor("玄关患教中心");
			articleParam.setGroupId("system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {
			gid = articleParam.getCreaterId();
			uid = articleParam.getCreaterId();
			if (StringUtil.isEmpty(uid)) {
				throw new ServiceException("集团ID不能为空");
			}
			articleParam.setGroupId(gid);
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
				.getIndex()) {
			uid = String.valueOf(ReqUtil.instance.getUserId());
			articleParam.setAuthor(uid);
			listGid = groupDoctorService.getGroupListIdByUser(uid);
			gid = listToString(listGid);
		} else {
			return "false";
		}
		articleParam.setCreaterId(uid);
		articleParam.setGroupId(gid);
		if (articleParam.getIsShare() == null) {
			articleParam.setIsShare(1);
		}
		if (articleParam.getIsShow() == null) {
			articleParam.setIsShow(1);
		}
		if (StringUtil.isEmpty(articleParam.getDescription())
				&& !StringUtil.isEmpty(articleParam.getContent())) {
			String content = articleParam.getContent();
			// 取首段落</p> <br/> </n>(目前只以这三种为首段标准)
			int pEnd = content.indexOf("</p>") + 4;
			int bEnd = content.indexOf("<br/>") + 4;
			int nEnd = content.indexOf("</n>") + 4;
			int end = pEnd;
			if (end > bEnd) {
				end = bEnd;
			}
			if (end > nEnd) {
				end = nEnd;
			}
			if (end > content.length()) {
				end = content.length();
			}
			content = content.substring(0, end);

			content = ParserHtmlUtil.delHTMLTag(content);// 去标签
			content = ParserHtmlUtil.delTransferredCode(content);// 去转义符
			int size = content.length();
			if (size > 100) {
				size = 100;
				articleParam.setDescription(content.substring(0, size)
						+ "......");
			} else {
				articleParam.setDescription(content.substring(0, size));
			}
		}
		// 保存文章
		articleParam.setEdited(1);
		if (StringUtil.isEmpty(articleParam.getCopyPath())) {
			articleParam.setCopy_small(small);
		} else {
			if (StringUtil.isEmpty(articleParam.getCopy_small())) {
				articleParam.setCopy_small(small);
			}
		}

		ArticleVO vo = articleDao.saveArticle(articleParam);
		articleParam.setArticleId(vo.getId());
		// 更新文章URL
		path = path + vo.getId() + ".html";
		url = url + vo.getId() + ".html";
		articleParam.setUrl(url);
		articleDao.updateArticle(articleParam);
		// 保存收藏（平台，集团，个人）
		articleParam.setCollectType(ArticleEnum.CollectType.create.getIndex());
		articleDao.saveCollectArticle(articleParam);
		if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
			// 个人还需要保存集团
			//if (!StringUtil.isEmpty(gid)) {
			if(listGid.size()>0){
				for (String listgid : listGid) {
					ArticleParam collectParam = new ArticleParam();
					collectParam.setCreaterId(listgid);
					collectParam.setCreateType(ArticleEnum.CreaterType.group.getIndex());
					collectParam.setCollectType(ArticleEnum.CollectType.collect.getIndex());
					collectParam.setArticleId(vo.getId());
					articleDao.saveCollectArticle(collectParam);// 保存集团收藏
				}
			}
			//}
		}
		//集团管理员专用
		/*if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			if (StringUtil.isNotEmpty(gid)) {
				ArticleParam collectParam = new ArticleParam();
				collectParam.setCreaterId(gid);
				collectParam.setCreateType(ArticleEnum.CreaterType.group.getIndex());
				collectParam.setCollectType(ArticleEnum.CollectType.collect.getIndex());
				collectParam.setArticleId(vo.getId());
				articleDao.saveCollectArticle(collectParam);// 保存集团收藏
			}
		}*/

		// 先添加平台树 (临时写的)
		GroupDisease paramSystem = new GroupDisease();
		paramSystem.setDiseaseId(vo.getDiseaseId());
		paramSystem.setGroupId("system");
		diseaseTreeDao.saveGroupDisease(paramSystem);

		// 更新到平台对应病种的文章数量
		if (articleParam.getIsShare() == 1) {
			GroupDisease gdParam = new GroupDisease();
			List<String> list = new ArrayList<String>();
			list.add(vo.getId());
			gdParam.setArticleId(list);
			gdParam.setDiseaseId(articleParam.getDiseaseId());
			gdParam.setGroupId("system");
			updateDiseaseTree(gdParam, "+");
		}

		// 更新对应集团对应病种的文章数量
		//if (!StringUtil.isEmpty(gid)) {
		if(listGid.size()>0){
			for (String listgid : listGid) {
				GroupDisease gdParam = new GroupDisease();
				gdParam.setDiseaseId(articleParam.getDiseaseId());
				gdParam.setGroupId(listgid);
				List<String> list = new ArrayList<String>();
				list.add(vo.getId());
				gdParam.setArticleId(list);
				updateDiseaseTree(gdParam, "+");
			}
		}
		//}
		
		//集团管理员专用
		if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			if (StringUtil.isNotEmpty(gid)) {
				GroupDisease gdParam = new GroupDisease();
				gdParam.setDiseaseId(articleParam.getDiseaseId());
				gdParam.setGroupId(gid);
				List<String> list = new ArrayList<String>();
				list.add(vo.getId());
				gdParam.setArticleId(list);
				updateDiseaseTree(gdParam, "+");
			}
		}

		// 向浏览表中添加一条浏览纪录
		ArticleParam visitor = new ArticleParam();
		visitor.setArticleId(vo.getId());
		visitor.setCreaterId("system");
		articleDao.saveArticleVisitor(visitor);

		try {
			// 根据模板生成一份静态html文件保存到服务器上并记下这个文件的url，
			File tempFile = new File(this.getClass().getResource("/temp/preview/preview.html").getPath());
			FileReader reader = new FileReader(tempFile);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer sf = new StringBuffer();
			String str = "";
			while ((str = br.readLine()) != null) {
				if (str.contains("{{title}}")) {
					str = str.replace("{{title}}", vo.getTitle());
				}
				if(0==vo.getIsShow()){
					str = str.replace("{{tag}}", "");
					str = str.replace(	"{{imgSrc}}","");
				}else{
					if (str.contains("{{tag}}")) {
						String temp = "";
						if (vo.getTag() != null) {
							temp = "<div class=\"disease\">";
							for (DiseaseTypeVO v : vo.getTag()) {
								temp += "<div class=\"tag\">" + v.getName() + "</div>";
							}
							temp = temp + "</div>";
						}
						str = str.replace("{{tag}}", temp);
					}
					if (str.contains("{{imgSrc}}")) {
						if (!StringUtil.isEmpty(vo.getCopyPath())&& vo.getIsShow() == 1) {
							str = str.replace("{{imgSrc}}","<img src=\""+articleParam.getCopyPath()+"\" class=\"img-responsive font-img\">");
						} else {
							str = str.replace("<img src=\"{{imgSrc}}\" class=\"img-responsive font-img\">","");
						}
					}
				}
				if (str.contains("{{author}}")) {//姜宏杰2016年3月21日15:23:01修改 ＢＵＧ号：8776
//					if(vo.getCreateType()==ArticleEnum.CreaterType.system.getIndex()){
//						vo.setAuthorName("玄关患教中心");
//						str = str.replace("{{author}}", "玄关患教中心");
//					}else if(vo.getCreateType()==ArticleEnum.CreaterType.group.getIndex()){
////						vo.setAuthorName(userManager.getGroupNameById(vo.getGroupId()));
//						User user = userManager.getUser(Integer.valueOf(vo.getAuthor()));
//						str = str.replace("{{author}}",user == null?"": user.getName());
//					}else if(vo.getCreateType()==ArticleEnum.CreaterType.doctor.getIndex()){
//						User user = userManager.getUser(Integer.valueOf(vo.getAuthor()));
//						str = str.replace("{{author}}",user == null?"": user.getName());
//					}else {
//						str = str.replace("{{author}}", "");
//					}
					str = str.replace("{{author}}", vo.getAuthorName() == null ? "":vo.getAuthorName());
				}
				if (str.contains("{{imgSrc}}")) {
					if (!StringUtil.isEmpty(vo.getCopyPath())
							&& vo.getIsShow() == 1) {
						str = str.replace("{{imgSrc}}", vo.getCopyPath());
					} else {
						str = str.replace("<img src=\"{{imgSrc}}\" class=\"img-responsive font-img\">","");
					}
				}
				if (str.contains("{{content}}")) {
					if (vo.getContent() != null) {
						str = str.replace("{{content}}", vo.getContent());
					} else {
						str = str.replace("{{content}}", "");
					}
				}
				if (str.contains("{{date}}")) {
					final String dateStr = DateUtil.formatDate2Str(vo.getCreatTime(),
							DateUtil.FORMAT_YYYY_MM_DD);
					str = str.replace("{{date}}", dateStr);
				}
				if(str.contains("{{summary}}")){
					str = str.replace("{{summary}}", vo.getDescription());
				}
				sf.append(str);
			}
			reader.close();
			// 输出 (输入流)
			File html = new File(path);

			if (html.exists()) {
				html.delete();
			} else {
				html.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(html);
			out.write(sf.toString().getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	@Override
	public Map<String, Object> delArticle(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 删除前，先查看该文章的useNum 如果不是0，则不能删。如果是0，则可以删。
		String articleId = articleParam.getArticleId();
		if (StringUtil.isEmpty(articleId)) {
			map.put("status", false);
			map.put("msg", "文章ID不能为空");
			return map;
		}
		ArticleVO vo = articleDao.getArticleById(articleId);
		if (vo == null) {
			map.put("status", false);
			map.put("msg", "没有找到该文章");
			return map;
		}
		// 浏览表，收藏表，置顶表，文章表 相关数据删除
		articleDao.delArticleVisitor(vo.getId());// 删除浏览表
		// 对集团树数量改变
		ArticleParam param = new ArticleParam();
		param.setArticleId(vo.getId());
		Map<String, ArticleCollect> mapCollect = articleDao
				.getCollectArticleByParam(param);
		// 收藏筛选条件collectorType=2,articleId = vo.getId();得到集团ID
		List<String> groupIdList = new ArrayList<String>();
		Iterator<Entry<String, ArticleCollect>> iterator = mapCollect
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ArticleCollect> entry = iterator.next();
			ArticleCollect collect = entry.getValue();
			//删除文章的时候不分平台、集团、还是个人的
			//if (collect.getCollectorType() == 2||collect.getCollectorType() == 3) {
				groupIdList.add(collect.getCollectorId());
			//}
		}

		GroupDisease gd = new GroupDisease();
		gd.setDiseaseId(vo.getDiseaseId());
		List<String> sList = new ArrayList<String>();
		sList.add(vo.getId());
		gd.setArticleId(sList);
		// 然后根据集团ID和老病种数量减一
		diseaseTreeDao.updateBatchGroup(gd, groupIdList, "-");

//		// 更新平台树数量
		gd.setGroupId("system");
		updateDiseaseTree(gd, "-");
		// 删除文章（i_article表）
		articleDao.delArticleByLogic(articleId);
		//删除收藏表
		articleDao.delCollectArticleById(articleParam);
		String url = vo.getUrl();
		// http://120.24.94.126:8081/html/template/56480b0bb5222527a4baa52c.html
		if (!StringUtil.isEmpty(url)) {
			int pathIndex = url.indexOf("/html/template/") + 1;
			int nameIndex = url.lastIndexOf("/") + 1;
			String path = url.substring(pathIndex, nameIndex);
			String fileName = url.substring(nameIndex);
//			final String servletUrl = PropertiesUtil
//					.getContextProperty("fileserver.upload")
//					+ "upload/delDocument";
//			FileUploadUtil.delDocument(servletUrl, fileName, path);
			fileUpload.delDocument(fileName, path);
		}
		map.put("status", true);
		return map;
	}

	public String getGoupByUser(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		String groupId = null;
		try {
			groupId = groupDoctorService.getGroupIdByUser(userId);
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupId;
	}

	@Override
	public List<DiseaseTypeVO> getTypeByParent(String parentId) {
		if (StringUtil.isEmpty(parentId)) {
			return baseDataService.getDiseaseByParent("");
		} else {
			if (parentId.length() > 2) {
				return null;
			}
			List<DiseaseTypeVO> tree = baseDataService
					.getDiseaseByParent(parentId);
			List<DiseaseTypeVO> result = null;
			if (tree == null) {
				return null;
			} else {
				result = new ArrayList<DiseaseTypeVO>();
				for (DiseaseTypeVO vo : tree) {
					vo.setLeaf(true);
					result.add(vo);
				}
			}
			return result;
		}
	}

	@Override
	public PageVO getArticleByDisease(ArticleParam articleParam) {
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}

		String uid = String.valueOf(ReqUtil.instance.getUserId());
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			// 集团查平台和集团一起的
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system", articleParam.getGroupId());
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex() || articleParam.getCreateType() == 4 || articleParam.getCreateType() == 5) {
			// 1:平台查平台 4：集团查平台 5：医生查平台数据
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == 2 || articleParam.getCreateType() == 6
				) {
			// 2：集团查集团，6：医生查集团数据
			articleParam.setGroupId(articleParam.getCreaterId());
			articleParam.setCreaterId(uid);
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),articleParam.getGroupId());
		}
//		else if (articleParam.getCreateType() == 3) {
//			// 3：医生查医生
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(uid);
//		}

//		Map<String, Object> map = articleDao.getArticleByDisease(articleParam);
		Map<String, Object> map = articleDao.getArticleByIds(ids, articleParam);
		PageVO page = new PageVO();
		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}

	private void addNode(Set<GroupDisease> voSet, GroupDisease vo,
			Map<String, GroupDisease> map) {
		voSet.add(vo);
		if (vo.getParent() == null || "0".equals(vo.getParent())
				|| voSet.contains(map.get(vo.getParent())))
			return;
		if (map.containsKey(vo.getParent())) {
			addNode(voSet, map.get(vo.getParent()), map);
		}
	}

	private void updateDiseaseTree(GroupDisease param, String type) {
		GroupDisease gd = diseaseTreeDao.saveGroupDisease(param);
		List<String> list = gd.getArticleId();
		if (type.trim().equals("+")) {
			list.addAll(param.getArticleId());
		} else if (type.trim().equals("-")) {
			list.removeAll(param.getArticleId());
		}
		int count = list.size();
		count = count < 0 ? 0 : count;
		gd.setArticleId(list);
		gd.setCount(count);
		diseaseTreeDao.updateCount(gd);
	}

	/**
	 * 向某个用户发送患教资料卡片
	 */
	public void sendArticleToUser(String fromUserId, String gid, String url) throws HttpApiException {
		// http://192.168.3.7:8081/html/template/563c9e924203f3676fef2b25.html

		String articleId = StringUtil.substringBetween(url, "template/",
				".html");
		ArticleVO articleVO = articleDao.getArticleById(articleId);
		if (articleVO != null) {
			sendArticleToUser(articleVO, gid, fromUserId);
		}
	}

	public void sendArticleToUser(ArticleVO articleVO, String gid,
			String fromUserId) throws HttpApiException {

		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setStyle(6);
		imgTextMsg.setContent(articleVO.getDescription());
		imgTextMsg.setTitle(articleVO.getTitle());
		imgTextMsg.setPic(articleVO.getCopyPath());
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setUrl(articleVO.getUrl());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bizType", 9);
		params.put("bizId", articleVO.getId());
		imgTextMsg.setParam(params);
		mpt.add(imgTextMsg);

		MessageVO msg = new MessageVO();
		msg.setType(14);
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		msg.setMpt(mpt);
		msg.setParam(params);
		msgService.baseSendMsg(msg);
	}

	@Override
	public String addArticleByUrl(ArticleParam articleParam, String url) {
		if (StringUtil.isEmpty(url)) {
			throw new ServiceException("url 不能为空");
		}
		if (!url.startsWith("https://") && !url.startsWith("http://")) {
			throw new ServiceException("链接地址有误");
		}
		String uid = String.valueOf(ReqUtil.instance.getUserId());
		String gid = getGoupByUser(uid);
		Document doc = JsoupUtil.getDocument(url);
		if (doc == null) {
			throw new ServiceException("链接地址有误");
		}
		String title = "";
		Elements titleElement = doc.getElementsByTag("title");
		if (titleElement != null && !titleElement.isEmpty()) {
			title = titleElement.get(0).text();
		}
		// String description = "";
		// String content = "";
		// Elements bodyElement = doc.getElementsByTag("body");
		// if(bodyElement != null && !bodyElement.isEmpty() ){
		// content = bodyElement.html();
		// description = ParserHtmlUtil.delHTMLTag(content);
		// description = ParserHtmlUtil.delTransferredCode(description);
		// int size = description.length();
		// if(size>100){
		// size = 100;
		// }
		// description = description.substring(0, size);
		// }
		// 此种类型不能套用模板，
		articleParam.setCreaterId(uid);
		articleParam.setGroupId(gid);
		articleParam.setTitle(title);
		articleParam.setAuthor(uid);
		articleParam.setCreateType(ArticleEnum.CreaterType.doctor.getIndex());

		// articleParam.setDescription(description);
		// articleParam.setContent(content);
		articleParam.setDiseaseId("QT");
		if (articleParam.getIsShare() == null) {
			articleParam.setIsShare(0);
		}
		if (articleParam.getIsShow() == null) {
			articleParam.setIsShow(0);
		}
		articleParam.setUrl(url);
		articleParam.setEdited(0);
//		String host = PropertiesUtil.getContextProperty("host");
//		String port = PropertiesUtil.getContextProperty("port");
//		String copy_small = PropertiesUtil.getContextProperty("copy_small");
//		String small = "http://" + host + ":" + port + "/default/"+copy_small;
		String small = MessageFormat.format(QiniuUtil.QINIU_URL(), "default", "article/default.jpg");
		articleParam.setCopy_small(small);
		ArticleVO vo = articleDao.saveArticle(articleParam);
		articleParam.setArticleId(vo.getId());

		// 保存收藏（个人）
		articleParam.setCollectType(ArticleEnum.CollectType.create.getIndex());
		articleDao.saveCollectArticle(articleParam);
		// 向浏览表中添加一条浏览纪录
		ArticleParam visitor = new ArticleParam();
		visitor.setArticleId(vo.getId());
		visitor.setCreaterId("system");
		articleDao.saveArticleVisitor(visitor);

		// 先添加平台树 (临时写的)
		GroupDisease paramSystem = new GroupDisease();
		paramSystem.setDiseaseId(vo.getDiseaseId());
		paramSystem.setGroupId("system");
		diseaseTreeDao.saveGroupDisease(paramSystem);

		// 保存集团收藏
		if (!StringUtil.isEmpty(gid)) {
			ArticleParam collectParam = new ArticleParam();
			collectParam.setCreaterId(gid);
			collectParam
					.setCreateType(ArticleEnum.CreaterType.group.getIndex());
			collectParam.setCollectType(ArticleEnum.CollectType.collect
					.getIndex());
			collectParam.setArticleId(vo.getId());
			articleDao.saveCollectArticle(collectParam);
			// 更新对应集团对应病种的文章数量
			GroupDisease gdParam = new GroupDisease();
			gdParam.setDiseaseId(articleParam.getDiseaseId());
			gdParam.setGroupId(gid);
			List<String> list = new ArrayList<String>();
			list.add(vo.getId());
			gdParam.setArticleId(list);
			updateDiseaseTree(gdParam, "+");
		}

		// 更新到平台对应病种的文章数量
		if (articleParam.getIsShare() == 1) {
			GroupDisease gdParam = new GroupDisease();
			List<String> list = new ArrayList<String>();
			list.add(vo.getId());
			gdParam.setArticleId(list);
			gdParam.setDiseaseId(articleParam.getDiseaseId());
			gdParam.setGroupId("system");
			updateDiseaseTree(gdParam, "+");
		}

		return url;
	}

	private List<GroupDisease> sortList(List<GroupDisease> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		// int size = list.size();
		// for(int i=0;i<size-1;i++){
		// for(int j=0;j<size-1-i;j++){
		// GroupDisease temp1 =list.get(j);
		// GroupDisease temp2 =list.get(j+1);
		// int weight1 = temp1.getWeight() == null?-1:temp1.getWeight();
		// int weight2 = temp2.getWeight() == null?-1:temp2.getWeight();
		// if(weight1<weight2){
		// list.set(j, temp2);
		// list.set(j+1, temp1);
		// }
		// }
		// }
		Collections.sort(list, new Comparator<GroupDisease>() {
			public int compare(GroupDisease arg0, GroupDisease arg1) {
				if (arg1.getWeight() == null) {
					arg1.setWeight(-1);
				}
				if (arg0.getWeight() == null) {
					arg0.setWeight(-1);
				}
				if(arg0.getDiseaseId().equalsIgnoreCase("QT")){
					arg0.setWeight(-100);
				}
				if(arg1.getDiseaseId().equalsIgnoreCase("QT")){
					arg1.setWeight(-100);
				}
				return arg1.getWeight().compareTo(arg0.getWeight());
			}
		});
		return list;
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("1");
		Set<String> set = new HashSet<String>(list);
		set.addAll(list);
		if(set.contains("2")){
			System.err.println("----------");
		}
		for(String s :set){
			System.err.println(s);
			
		}
		
	}

	@Override
	public void addDTG(String doct, String gid) {
		articleDao.addDActToGAct(doct, gid);
	}
/***begin add  by  liwei  2016年1月18日********/

	@Override
	public Map<String, Object> getDisaseTreeByUserId(Integer createType,
			List<String> groupIds) {

		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<GroupDisease> result = new ArrayList<GroupDisease>();
		List<GroupDisease> all = new ArrayList<GroupDisease>();

		double sum = 0;// 全部文档数量
		Map<String, GroupDisease> map = new HashMap<String, GroupDisease>();
		List<GroupDisease> list = new ArrayList<GroupDisease>();
		Set<GroupDisease> voSet = new HashSet<GroupDisease>();

		if (createType == 0) {// 平台和集团所有的
			List<GroupDisease> system = diseaseTreeDao.getGroupDiseaseTree("system");
			List<GroupDisease> doctor = new ArrayList<GroupDisease>();
			if (null != groupIds && groupIds.size() > 0) {
				doctor = diseaseTreeDao.getMoreGroupDiseaseTree(groupIds);
			}
			system.addAll(doctor);
			system = calcCount(system);

			all.addAll(diseaseTreeDao.getAllGroupDisease("system"));
			for (GroupDisease temp : all) {
				map.put(temp.getDiseaseId(), temp);
			}
			for (GroupDisease vo : system) {
				sum += vo.getCount();
				addNode(voSet, vo, map);
			}
			list.addAll(voSet);

		} else if (createType == 1) {// 平台的
			List<GroupDisease> system = diseaseTreeDao.getGroupDiseaseTree("system");// 有数据节点
			system = calcCount(system);
			
			all.addAll(diseaseTreeDao.getAllGroupDisease("system")); // 所有节点
			for (GroupDisease temp : all) {
				map.put(temp.getDiseaseId(), temp);
			}
			for (GroupDisease vo : system) {
				sum += vo.getCount();
				addNode(voSet, vo, map);
			}
			list.addAll(voSet);
		} else if (createType == 2) {// 集团的
			if (null == groupIds || groupIds.size() == 0) {
				mapResult.put("total", sum);
				mapResult.put("tree", result);
				return mapResult;
			}
			List<GroupDisease> doctor = diseaseTreeDao.getMoreGroupDiseaseTree(groupIds);// 有数据节点
			doctor = calcCount(doctor);
			
			all.addAll(diseaseTreeDao.getAllGroupDisease("system"));// 所有节点
			for (GroupDisease temp : all) {
				map.put(temp.getDiseaseId(), temp);
			}

			for (GroupDisease vo : doctor) {
				sum += vo.getCount();
				addNode(voSet, vo, map);
			}
			list.addAll(voSet);
		}

		for (GroupDisease temp : list) {
			if (map.containsKey(temp.getParent())) {
				GroupDisease partent = map.get(temp.getParent());
				List<GroupDisease> children = partent.getChildren();
				if (partent.getParent().equals("0")) {
					if (children == null) {
						children = new ArrayList<GroupDisease>();
					}
					children.add(temp);
				}
				partent.setChildren(children);
				if (temp.getCount() == null) {
					temp.setCount(0);
				}
				if (partent.getCount() == null) {
					partent.setCount(0);
				}
				partent.setCount(partent.getCount() + temp.getCount());
			}
			if (temp.getParent().equals(ExtTreeNode.ROOT_NODE_PARENT_ID)) {
				result.add(temp);
			}
		}

		mapResult.put("total", sum);
		mapResult.put("tree", sortList(result));
		return mapResult;

	}
	
	/**
	 * 当多个数据源时，用来去重
	 * @param doctor
	 * @return
	 */
	private List<GroupDisease> calcCount(List<GroupDisease> doctor){
		Map<String,GroupDisease> gMap = new HashMap<String,GroupDisease>();
		for(GroupDisease vo :doctor){
			GroupDisease gd = gMap.get(vo.getDiseaseId());
			if(gd == null){
				gd = vo;
			}
			List<String> list = gd.getArticleId();
			if(list == null){
				list = new ArrayList<String>();
			}
			Set<String> setStr = new HashSet<String>(list);
			setStr.addAll(vo.getArticleId());
			gd.setArticleId(new ArrayList<String>(setStr));
			gd.setCount(gd.getArticleId().size());
			gMap.put(vo.getDiseaseId(), gd);
		}
		List<GroupDisease> result = new ArrayList<GroupDisease>();
		Iterator<Entry<String, GroupDisease>> iter = gMap.entrySet().iterator();
		while(iter.hasNext()){
			result.add(iter.next().getValue());
		}
		return result;
	}

	@Override
	public PageVO getHotArticleForMoreGroup(ArticleParam articleParam,
			String type) {
		List<String> parmsList = new ArrayList<String>();
		long startTime = System.currentTimeMillis();
		if (articleParam.getCreateType() == 0) {
			if (!StringUtil.isEmpty(articleParam.getCreaterId())) {
				parmsList.add(articleParam.getCreaterId());
				articleParam.setGroupIds(parmsList);
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			articleParam.setCreaterId("system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {
			parmsList.addAll(articleParam.getGroupIds());
			articleParam.setGroupIds(parmsList);
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
				.getIndex()) {
			String uid = String.valueOf(ReqUtil.instance.getUserId());
			articleParam.setCreaterId(uid);
			parmsList.add(articleParam.getCreaterId());
			articleParam.setGroupIds(parmsList);
		}

		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (type != null && type.equals("week")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			articleParam.setCreatTime(cal.getTime().getTime());
		}

		Map<String, Object> map = articleDao
				.getHotArcticleForMoreGroup(articleParam);
		PageVO page = new PageVO();
		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 榛樿20鏉�
		page.setTotal(Long.parseLong(map.get("count").toString()));

		long endTime = System.currentTimeMillis();
		log.info("GetHotArticle......" + (endTime - startTime));

		return page;

	}
	/**
	 * 查询最新文章
	 */
	@Override
	public PageVO getNewArticleForMoreGroup(ArticleParam articleParam, String type) {
//		List<String> parmsList = new ArrayList<String>();
//		if (StringUtil.isEmpty(articleParam.getSortType())) {
//			articleParam.setSortType("desc");
//		}
//		if (StringUtil.isEmpty(articleParam.getSortBy())) {
//			articleParam.setSortBy("creatTime");
//		}
//		if (articleParam.getPageSize() == 0) {
//			articleParam.setPageSize(20);
//		}
//		if (articleParam.getCreateType() == 0) {
////			articleParam.setGroupId(articleParam.getCreaterId());
//			parmsList.addAll(articleParam.getGroupIds());
//			articleParam.setGroupIds(parmsList);
//			articleParam.setCreaterId("system");
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
//				.getIndex()) {
//			articleParam.setCreaterId("system");
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
//				.getIndex()) {
////			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
//			parmsList.addAll(articleParam.getGroupIds());
//			articleParam.setGroupIds(parmsList);
//			
//			
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
//				.getIndex()) {
//			String uid = String.valueOf(ReqUtil.instance.getUserId());
//			articleParam.setCreaterId(uid);
////			articleParam.setGroupId(getGoupByUser(uid));
//			articleParam.setGroupIds(articleParam.getGroupIds());
//			
//		}
//
//		Map<String, Object> map = articleDao
//				.getNewArcticleForMoreGroup(articleParam);
//		PageVO page = new PageVO();
//		page.setPageData((List<ArticleVO>) map.get("list"));
//		page.setPageIndex(articleParam.getPageIndex());
//		page.setPageSize(articleParam.getPageSize());// 默认20条
//		page.setTotal(Long.parseLong(map.get("count").toString()));
//		return page;
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}
		if (articleParam.getPageSize() == 0) {
			articleParam.setPageSize(20);
		}
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 list.add("system");
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
			
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
			articleParam.setCreaterId("system");
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
			String uid = String.valueOf(ReqUtil.instance.getUserId());
			articleParam.setCreaterId(uid);
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
		}

//		Map<String, Object> map = articleDao.getNewArcticleByParam(articleParam);
		Map<String, Object> map = articleDao.getArticleByIds(ids, articleParam);
		PageVO page = new PageVO();
		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	}
	/***end add  by  liwei  2016年1月18日********/

	@Override
	public PageVO getArticleByDiseaseForMoreGroup(ArticleParam articleParam) {

//		if (StringUtil.isEmpty(articleParam.getSortType())) {
//			articleParam.setSortType("desc");
//		}
//		if (StringUtil.isEmpty(articleParam.getSortBy())) {
//			articleParam.setSortBy("creatTime");
//		}
//
//		String uid = String.valueOf(ReqUtil.instance.getUserId());
//		if (articleParam.getCreateType() == 0) {
//			// 集团查平台和集团一起的
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(uid);
//		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
//				.getIndex()) {
//			// 平台查平台
//			articleParam.setCreaterId(uid);
//		} else if (articleParam.getCreateType() == 2
//				|| articleParam.getCreateType() == 4) {
//			// 2：集团查集团，4：集团查平台
////			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(uid);
//		} else if (articleParam.getCreateType() == 3
//				|| articleParam.getCreateType() == 5
//				|| articleParam.getCreateType() == 6) {
//			// 3：医生查医生5：医生查平台数据6：医生查集团数据
//			articleParam.setGroupId(articleParam.getCreaterId());
//			articleParam.setCreaterId(uid);
//		}
//
//		Map<String, Object> map = articleDao.getArticleByDiseaseForMoreGroup(articleParam);
//		PageVO page = new PageVO();
//		page.setPageData((List<ArticleVO>) map.get("list"));
//		page.setPageIndex(articleParam.getPageIndex());
//		page.setPageSize(articleParam.getPageSize());// 默认20条
//		page.setTotal(Long.parseLong(map.get("count").toString()));
//		return page;
		if (StringUtil.isEmpty(articleParam.getSortType())) {
			articleParam.setSortType("desc");
		}
		if (StringUtil.isEmpty(articleParam.getSortBy())) {
			articleParam.setSortBy("creatTime");
		}
		if (articleParam.getPageSize() == 0) {
			articleParam.setPageSize(20);
		}
		List<String> ids = null;
		if (articleParam.getCreateType() == 0) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 list.add("system");
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
			
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
			articleParam.setCreaterId("system");
			ids = articleDao.getArticleIds(articleParam.getDiseaseId(),"system");
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group.getIndex()) {
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
			articleParam.setCreaterId(String.valueOf(ReqUtil.instance.getUserId()));
			List<String> list = articleParam.getGroupIds();
			 if(list == null){
				 list = new ArrayList<String>();
			 }
			 String[] str = (String[]) list.toArray(new String[list.size()]);
			 ids = articleDao.getArticleIds(articleParam.getDiseaseId(),str);
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor.getIndex()) {
			String uid = String.valueOf(ReqUtil.instance.getUserId());
			articleParam.setCreaterId(uid);
			articleParam.setGroupIds(groupDoctorService.getGroupListIdByUser(ReqUtil.instance.getUserId()+""));
		}

		Map<String, Object> map = articleDao.getArticleByIds(ids, articleParam);
		PageVO page = new PageVO();
		page.setPageData((List<ArticleVO>) map.get("list"));
		page.setPageIndex(articleParam.getPageIndex());
		page.setPageSize(articleParam.getPageSize());// 默认20条
		page.setTotal(Long.parseLong(map.get("count").toString()));
		return page;
	
	}

	@Override
	public String statisticsArticleVisit(String articleId,Integer userId) {
		if (StringUtil.isEmpty(articleId)) {
			throw new ServiceException("文章ID都不能为空");
		}
		ArticleVO articleVO = null;
		articleVO = getArticleById(articleId);
		if (articleVO == null) {
			throw new ServiceException("文章已经不存在！！！");
		}
		//"+1"之前得先看这篇文章是否已经被同一个人浏览过 要是已经浏览了 就不用加1了
		ArticleVisit visit = articleDao.isVisit(articleId, userId);
		if(null!=visit){
			throw new ServiceException("您已经阅读过了 不用再加！！！");
		}
		return articleDao.saveVisitTimes(articleId, userId);
	}

	@Override
	public void updateVisitNum() {
		articleDao.updateVisitNum();
	}
	
}
