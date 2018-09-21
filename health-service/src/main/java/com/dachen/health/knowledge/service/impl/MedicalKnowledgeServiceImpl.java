package com.dachen.health.knowledge.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.http.FileUploadUtil;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.knowledge.constant.KnowledgeEnum;
import com.dachen.health.knowledge.dao.IKnowledgeCategoryDao;
import com.dachen.health.knowledge.dao.IMedicalKnowledgeDao;
import com.dachen.health.knowledge.entity.param.MedicalKnowledgeParam;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.health.knowledge.entity.po.MedicalKnowledge;
import com.dachen.health.knowledge.entity.po.MedicalKnowledgeTop;
import com.dachen.health.knowledge.entity.vo.MedicalKnowledgeVO;
import com.dachen.health.knowledge.service.IMedicalKnowledgeService;
import com.dachen.util.BeanUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ParserHtmlUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mongodb.DBObject;

@Service
public class MedicalKnowledgeServiceImpl implements IMedicalKnowledgeService {
	
	@Autowired
	private IKnowledgeCategoryDao categoryDao;
	
	@Autowired
	private IMedicalKnowledgeDao knowledgeDao;
	
	@Autowired
	private UserManager userManger;
	
	@Autowired
	private IQrCodeService codeService;
	
	@Autowired
	FileUploadUtil fileUpload;
	
//	private static final String  HOST = PropertiesUtil.getContextProperty("host");
    private static final String  HOST() {
        return PropertiesUtil.getContextProperty("host");
    }

//	private static final String PORT = PropertiesUtil.getContextProperty("port");
    private static final String PORT() {
        return PropertiesUtil.getContextProperty("port");
    }

//	private static final String PATH = PropertiesUtil.getContextProperty("path");
    private static final String PATH() {
        return PropertiesUtil.getContextProperty("path");
    }
	
	
	@Override
	public Map<String, Object> updateKnowledg(MedicalKnowledgeParam param) {
		if(StringUtil.isEmpty(param.getId()) ||StringUtil.isEmpty(param.getAuthor()) || StringUtil.isEmpty(param.getTitle())||StringUtil.isEmpty(param.getCopy())|| StringUtil.isEmpty(param.getContent())){
			throw new ServiceException("参数有误");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(param.getId());
		if(knowledge == null){
			map.put("status", false);
			map.put("msg", "你所修改的就医识已删除");
			return map;
		}
		//权限判断
		if(!isAdmin(knowledge.getCreater(),param.getGroupId())){
			map.put("status", false);
			map.put("msg", "你无权限修改");
			return map;
		}
		setDescription(param);
		param.setCreaterType(knowledge.getCreaterType());
		param.setCreateTime(knowledge.getCreateTime());
		param.setUpdateTime(knowledge.getUpdateTime());
		param.setIsShow(param.getIsShow()==null?KnowledgeEnum.ShowStatus.show.getIndex():param.getIsShow());
		try {
			setAuthorInfo(param);
			genKnowledgeFileAndUrl(param,false);//生成非分享
			genKnowledgeFileAndUrl(param,true);//生成分享
			if(!param.getCategoryId().equals(param.getsCategoryId())){
				//旧节点需要移除该就医知识
				KnowledgeCategory scategroy = categoryDao.selectKnowledgeCategoryById(param.getsCategoryId());
				//新节点需增加该就医知识
				KnowledgeCategory categroy = categoryDao.selectKnowledgeCategoryById(param.getCategoryId());
				if((scategroy==null)||(categroy==null)||(!scategroy.getGroupId().equals(categroy.getGroupId()))){
					map.put("status", false);
					map.put("msg", "分类不属于同一个集团或者为空不能修改");
					return map;
				}
				if( scategroy.getKnowledgeIds() !=null && scategroy.getKnowledgeIds().length !=0){
					List<String> oList = new ArrayList<String>(Arrays.asList(scategroy.getKnowledgeIds()));
					if(oList.contains(knowledge.getId())){
						oList.remove(knowledge.getId());
						scategroy.setKnowledgeIds((String[])oList.toArray(new String[oList.size()]));
						categoryDao.updateKnowledgeCategoryById(scategroy);
					}
				}
				
				List<String> oList = new ArrayList<String>();
				if(categroy.getKnowledgeIds()!=null && categroy.getKnowledgeIds().length !=0){
					oList.addAll(Arrays.asList(categroy.getKnowledgeIds()));
				}
				if(!oList.contains(knowledge.getId())){
					oList.add(knowledge.getId());
					categroy.setKnowledgeIds((String[])oList.toArray(new String[oList.size()]));
					categoryDao.updateKnowledgeCategoryById(categroy);
				}
			}
			knowledgeDao.updateMedicalKnowledgeById(param);
			map.put("status", true);
			map.put("msg", "更新成功");
			return map;
		} catch (ServiceException e){
			map.put("status", false);
			map.put("msg", e.getMessage());
			return map;
		} catch (Exception e) {
			map.put("status", false);
			map.put("msg", "更新失败");
			return map;
		}
	}

	@Override
	public Map<String, Object> delCategoryById(String id) {
		//如果是其它未分配节点不能删除
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		KnowledgeCategory category =categoryDao.selectKnowledgeCategoryById(id);
		if(category == null){
			map.put("status", false);
			map.put("msg", "未找到对应的分类");
			return map;
		}
		if(!isAdmin("",category.getGroupId())){
			map.put("status", false);
			map.put("msg", "无权限");
			return map;
		}
		KnowledgeCategory  defaultCategory = categoryDao.selectDefaultKnowledgeCategoryById(category.getGroupId(), KnowledgeEnum.OTHER_CATEGORY);
		if(id.trim().equals(defaultCategory.getId().trim())){
			map.put("status", false);
			map.put("msg", "默认分类不能删除");
			return map;
		}
		String[] knowledgeIds = category.getKnowledgeIds();
		if(knowledgeIds != null && knowledgeIds.length>0){
			Set<String> list = new HashSet<String>(Arrays.asList(knowledgeIds));
			
			String defaultId = defaultCategory.getId();
			String[] defaultIds = defaultCategory.getKnowledgeIds();
			if(defaultIds != null && defaultIds.length >0){
				list.addAll(Arrays.asList(defaultIds));
			}
			defaultCategory = new KnowledgeCategory();
			defaultCategory.setId(defaultId);
			defaultCategory.setKnowledgeIds((String[])list.toArray(new String[list.size()]));
			categoryDao.updateKnowledgeCategoryById(defaultCategory);
		}
		//最后再删除该节点
		boolean result = categoryDao.delKnowledgeCategoryById(id);
		map.put("status", result);
		map.put("msg", result?"删除成功":"删除失败");
		return map;
	}

	@Override
	public Map<String, Object> addCategoryById(KnowledgeCategory category) {
		if(StringUtil.isEmpty(category.getGroupId())||StringUtil.isEmpty(category.getName()) || category.getName().contains(KnowledgeEnum.OTHER_CATEGORY)){
			throw new ServiceException("参数为空或分类名称有误");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if(!isAdmin("",category.getGroupId())){
			map.put("status", false);
			map.put("msg", "无权限");
			return map;
		}
		KnowledgeCategory addCategory = null;
		List<KnowledgeCategory> list = categoryDao.selectKnowledgeCategoryListByGroupId(category.getGroupId(),false);
		if(list.isEmpty()){
			addNoCategery(category.getGroupId());
			category.setWeight(1);
		}else{
			addCategory = list.get(0);
			category.setWeight(addCategory.getWeight() + 1);
		}
		addCategory = categoryDao.insertKnowledgeCategory(category);
		boolean result = addCategory!=null? true:false;
		map.put("status", result);
		map.put("msg", result?"添加成功":"添加失败");
		return map;
	}

	@Override
	public Map<String, Object> renameCategory(String id, String name){
		if(StringUtil.isEmpty(name) || name.contains(KnowledgeEnum.OTHER_CATEGORY) || StringUtil.isEmpty(id)){
			throw new ServiceException("参数有误");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		KnowledgeCategory category = categoryDao.selectKnowledgeCategoryById(id);
		if(category == null){
			map.put("status", false);
			map.put("msg", "未找到对应的分类");
			return map;
		}
		if(!isAdmin("",category.getGroupId())){
			map.put("status", false);
			map.put("msg", "无权限");
			return map;
		}
		category = new KnowledgeCategory();
		category.setId(id);
		category.setName(name);
		categoryDao.updateKnowledgeCategoryById(category);
		boolean result = category!=null? true:false;
		map.put("status", result);
		map.put("msg", result?"修改成功":"修改失败");
		return map;
	}
	
	@Override
	public List<KnowledgeCategory> getCategoryList(String groupId){
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("集团Id不能为空");
		}
		List<KnowledgeCategory> list = categoryDao.selectKnowledgeCategoryListByGroupId(groupId,false);
		if(list.isEmpty()){
			list.add(addNoCategery(groupId));
//			return categoryDao.selectKnowledgeCategoryListByGroupId(groupId,false);
		}
		return list;
	}

	@Override
	public Map<String, Object> addShareCount(String bizId, Integer bizType) {
		if(StringUtil.isEmpty(bizId) || bizType == null) {
			throw new ServiceException("参数不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Object obj = null ;
		if(bizType == 1){//就医知识
			obj = knowledgeDao.selectMedicalKnowledgeById(bizId);
		}else{
			//待添加业务
		}
		if(obj == null){
			map.put("status", false);
			map.put("msg", "未找到相关对象");
			return map;
		}
		boolean result = false;
		if(obj instanceof MedicalKnowledge){
			MedicalKnowledge source = (MedicalKnowledge)obj;
			MedicalKnowledge m = new MedicalKnowledge();
			m.setId(bizId);
			m.setShareCount(source.getShareCount()==null?1:source.getShareCount()+1);
			result = knowledgeDao.updateMedicalKnowledgeById(m);
		}else{
			//待添加业务
		}
		map.put("status", false);
		map.put("msg", result?"增加成功":"增加失败");
		return map;
	}

	@Override
	public PageVO getMedicalKnowledgeListByCategoryId(MedicalKnowledgeParam param) {
		if(StringUtil.isEmpty(param.getCategoryId())){
			throw new ServiceException("分类Id不能为空");
		}
		KnowledgeCategory category = categoryDao.selectKnowledgeCategoryById(param.getCategoryId());
		if(category == null || category.getKnowledgeIds() == null || category.getKnowledgeIds().length == 0){
			return new PageVO(null,0L,param.getPageIndex(),param.getPageSize());
		}
		List<KnowledgeCategory> knowledgeCategoryList = new ArrayList<KnowledgeCategory>();
		knowledgeCategoryList.add(category);
		Map<String,KnowledgeCategory> map = setCategoryRelation(knowledgeCategoryList);
		//要据分类id 去找对应的就医知识Id列表
		List<String> allIds = Arrays.asList(category.getKnowledgeIds());//所有Id列表
		List<MedicalKnowledgeVO>  result = getMedicalKnowledgeByIds(allIds,category.getGroupId(),param.getPageIndex(),param.getPageSize());
		setCategeory( result, map);
		return new PageVO(result,Long.valueOf(allIds.size()),param.getPageIndex(),param.getPageSize());
	}
	
	private boolean isAdmin(String creater,String groupId){
		Integer currentUser = ReqUtil.instance.getUserId();
		boolean result1 = String.valueOf(currentUser).equals(creater);
		if(result1){
			return result1;
		}
		if(StringUtil.isNotEmpty(groupId)){
			result1 = knowledgeDao.isAdminToGroup(currentUser,groupId);
		}else{
			result1 = knowledgeDao.isAdminToGroup(currentUser,creater);
		}
		return result1 ;
	}

	@Override
	public Map<String, Object> delKnowledgeById(String id,String groupId) {
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(id);
		if(knowledge == null){
			map.put("status", false);
			map.put("msg", "未找到对应的就医知识文章");
			return map;
		}
		if(!isAdmin(knowledge.getCreater(),groupId)){
			map.put("status", false);
			map.put("msg", "你无权限修改");
			return map;
		}
		//查出所有置顶的需要删除
		knowledgeDao.delKnowledgeTopByKnowledgeId(id);
		//查出来所有分类的需要移除
		knowledgeDao.delCategoryByKnowledgeId(id);
		//删除静态文件
		String url = knowledge.getUrl();
		if (StringUtil.isNotEmpty(url)) {
			int pathIndex = url.indexOf("/html/template/") + 1;
			int nameIndex = url.lastIndexOf("/") + 1;
			String path = url.substring(pathIndex, nameIndex);
			String fileName = url.substring(nameIndex);
//			final String servletUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/delDocument";
//			FileUploadUtil.delDocument(servletUrl, fileName, path);
			fileUpload.delDocument(fileName, path);
		}
		knowledgeDao.delMedicalKnowledge(id);
		map.put("status", true);
		map.put("msg", "删除成功");
		return map;
	}

	@Override
	public PageVO searchKnowledgeListByKeys(MedicalKnowledgeParam param) {
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("groupId不能为空");
		}
		//先获取该集团的所有文章Id列表，
		List<KnowledgeCategory> list = knowledgeDao.selectMedicalKnowledgeListByGroupId(param);
		List<ObjectId> idList = (List<ObjectId>)convertToObjectId(list,true);
		if(idList.size() == 0){
			return new PageVO(null,0L,param.getPageIndex(),param.getPageSize());
		}
		Map<String,KnowledgeCategory> map = setCategoryRelation(list);
		//拿着关键字去用户表里查找字作者
		List<String> authorList = new ArrayList<String>();
		if(StringUtil.isNotEmpty(param.getKeywords())){
			List<Integer> userIds = userManger.getUserIdList(param.getKeywords());
			for(Integer id :userIds){
				authorList.add(String.valueOf(id));
			}
		}
		//拿着关键字去文章表里查询作者和标题两个字段
		String[] authorS = null;
		if(authorList.size() > 0 ){
			authorS = (String[])authorList.toArray(new String[authorList.size()]);
		}
		
		Query<MedicalKnowledge> query = knowledgeDao.selectMedicalByCondition(idList, param.getKeywords(),authorS);
		List<MedicalKnowledge> mList = query.asList();
		if(mList.size() == 0){
			return new PageVO(null,0L,param.getPageIndex(),param.getPageSize());
		}
		List<String> allIds = (List<String>)convertToObjectId(mList, false);
		List<MedicalKnowledgeVO>  result = getMedicalKnowledgeByIds(allIds,param.getGroupId(),param.getPageIndex(),param.getPageSize());
		setCategeory(result, map);
		return new PageVO(result,Long.valueOf(allIds.size()),param.getPageIndex(),param.getPageSize());
	}
	
	/**
	 * 
	 * @param ids 指定范围Id列表
	 * @param bizId  集团或者医生Id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	private List<MedicalKnowledgeVO> getMedicalKnowledgeByIds(List<String> ids,String bizId,Integer pageIndex,Integer pageSize) {
		List<MedicalKnowledge> result = new ArrayList<MedicalKnowledge>();
		//先根据bizID 和Ids 查出指定范围内被置顶的文章ID及其排序规则（此时分页）
		List<MedicalKnowledgeTop> topList = knowledgeDao.getMedicalKnowledgeTopIds(ids,bizId,pageIndex*pageSize,pageSize);
		List<ObjectId> topIdList = (List<ObjectId>)convertToObjectId(topList, true);
		List<MedicalKnowledge> topMedicalList = knowledgeDao.selectMedicalKnowledgeListByIds(topIdList,false);
		topMedicalList = sortTop(topList,topMedicalList);//置顶文章排序
		List<MedicalKnowledgeTop> allTopList = knowledgeDao.getMedicalKnowledgeTopIds(ids,bizId);
		List<String> allTopIds = (List<String>)convertToObjectId(allTopList, false);
		if(topMedicalList.size() == pageSize){
			return setNameAndIsTop(topMedicalList,allTopIds);
		}
		result.addAll(topMedicalList);//非置顶
		long topCount = knowledgeDao.getMedicalKnowledgeTopCount(bizId);
		int offSet = (int) (pageIndex * pageSize - topCount);
		offSet = offSet<0?0:offSet;
		int limit = pageSize - topList.size();
		result.addAll(knowledgeDao.selectMedicalKnowledgeListByIds(getUnTopIds(ids,allTopIds),false, offSet, limit));//置顶
		return setNameAndIsTop(result,allTopIds);
	}
	
	private List<MedicalKnowledgeVO> setNameAndIsTop(List<MedicalKnowledge> medicalList,List<String> topList){
		List<MedicalKnowledgeVO> list = new ArrayList<MedicalKnowledgeVO>(medicalList.size());
		for(MedicalKnowledge m :medicalList){
			MedicalKnowledgeVO vo = BeanUtil.copy(m, MedicalKnowledgeVO.class);
			vo.setAuthorName(getName(vo.getCreaterType(), vo.getAuthor()));
			if(topList != null && topList.contains(vo.getId())){
				vo.setIsTop("1");
			}else{
				vo.setIsTop("0");
			}
			list.add(vo);
		}
		return list;
	}
	
	private void setDescription(MedicalKnowledgeParam param){
		if (StringUtil.isEmpty(param.getDescription())) {
			String content = param.getContent();
			// 取首段落</p> <br/> </n>(目前只以这三种为首段标准)
			int pEnd = content.indexOf("</p>") + 4;
			pEnd = pEnd==3?Integer.MAX_VALUE:pEnd;
			int bEnd = content.indexOf("<br/>") + 4 ;
			bEnd = bEnd==3?Integer.MAX_VALUE:bEnd;
			int nEnd = content.indexOf("</n>") + 4;
			nEnd = nEnd==3?Integer.MAX_VALUE:nEnd;
			
			pEnd = pEnd>bEnd? bEnd:pEnd;
			pEnd = pEnd>nEnd? nEnd:pEnd;
			pEnd = pEnd>content.length()? content.length():pEnd;
			content = content.substring(0, pEnd);
			content = ParserHtmlUtil.delHTMLTag(content);// 去标签
			content = ParserHtmlUtil.delTransferredCode(content);// 去转义符
			int size = content.length();
			if (size > 100) {
				size = 100;
				param.setDescription(content.substring(0, size)+ "......");
			} else {
				param.setDescription(content.substring(0, size));
			}
		}
	}
	
	private void setAuthorInfo(MedicalKnowledgeParam param){
		if(isNumeric(param.getAuthor())){
			User u = userManger.getUser(Integer.parseInt(param.getAuthor()));
			if(u != null){
				param.setAuthorName(u.getName());
				param.setAuthorIoc(u.getHeadPicFileName());
				param.setDocTitle(u.getDoctor().getTitle());
				param.setDocDept(u.getDoctor().getDepartments());
				param.setHospital(u.getDoctor().getHospital());
				param.setDesc(u.getDoctor().getIntroduction());
			}
		}else{
			DBObject obj = knowledgeDao.getGroupInfoByGId(param.getAuthor());
			if(obj !=null){
				param.setAuthorName(MongodbUtil.getString(obj, "name"));
				param.setDesc(MongodbUtil.getString(obj, "introduction"));
				param.setAuthorIoc(MongodbUtil.getString(obj, "logoUrl"));
			}
		}
	}
	private String getName(Integer type,String authorId){
		String name = "";
		if(isNumeric(authorId)){
			User u = userManger.getUser(Integer.valueOf(authorId),null);
			name = u==null?"":u.getName();
		}else{
			DBObject obj = knowledgeDao.getGroupInfoByGId(authorId);
			name = obj==null?"":MongodbUtil.getString(obj, "name");
		}
		return name;
	}
	private List<MedicalKnowledge> sortTop(List<MedicalKnowledgeTop> topList,List<MedicalKnowledge> topMedicalList){
		Map<String,MedicalKnowledge> temp = new HashMap<String,MedicalKnowledge>();
		for(MedicalKnowledge m :topMedicalList){
			temp.put(m.getId(), m);
		}
		List<MedicalKnowledge>	list = new ArrayList<MedicalKnowledge>();
		for(MedicalKnowledgeTop t:topList){
			list.add(temp.get(t.getKnowledgeId()));
		}
		return list;
	}
	private List<ObjectId> getUnTopIds(List<String> allIds,List<String> topIds){
		List<ObjectId> result = new ArrayList<ObjectId>();
		for(String str: allIds){
			if(!topIds.contains(str)){
				result.add(new ObjectId(str));
			}
		}
		return result;
	}
	private static List<?> convertToObjectId(List<?> list,boolean isObjectId){
		Set<String> set = new HashSet<String>();
		for(Object c : list){
			if( c instanceof KnowledgeCategory){
				KnowledgeCategory mc = (KnowledgeCategory)c;
				if(mc.getKnowledgeIds() != null){
					set.addAll(Arrays.asList(mc.getKnowledgeIds()));
				}
			}else if( c instanceof MedicalKnowledgeTop){
				MedicalKnowledgeTop mt = (MedicalKnowledgeTop) c;
				set.add(mt.getKnowledgeId());
			}else if(c instanceof MedicalKnowledge){
				MedicalKnowledge mk = (MedicalKnowledge)c;
				set.add(mk.getId());
			}
		}
		List result =null;
		if(isObjectId){
			result = new ArrayList<ObjectId>();
		}else{
			result = new ArrayList<String>();
		}
		for(String s :set){
			if(isObjectId){
				result.add(new ObjectId(s));
			}else{
				result.add(s);
			}
		}
		return result;
	}
	
	private void genKnowledgeFileAndUrl(MedicalKnowledgeParam param,boolean isShareUrl) throws IOException,ServiceException{
		String url =  "http://" + HOST() + ":" + PORT() + "/html/template/";
		String fileName = PATH() + param.getId() +".html";
		String templateFile = "/temp/preview/preview.html";
		if(isShareUrl){
			url += "share_"+ param.getId() + ".html";
			param.setShareUrl(url);
			try{
				String codeSrc = codeService.generateUserQr(param.getAuthor(),"3");
				param.setCodeSrc(codeSrc);
			} catch (Exception e) {
				throw new ServiceException("生成二维码出错");
			}
			
			templateFile = "/temp/preview/share_preview.html";
			fileName = PATH() + "share_"+param.getId() +".html";
			
		}else{
			url += param.getId() + ".html";
			param.setUrl(url);
		}
		param.setFileName(fileName);
		param.setTemplateFile(templateFile);
		creatFile(param);
	}
	
	private boolean isNumeric(String str){ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	/**
	 * @param str 不能为空
	 * @return
	 */
	private String replaceSpecialToken(String str){
		if (StringUtil.isEmpty(str)) {
			return str;
		}
		return str.replace("<", "&lt").replace(">", "&gt");
	}
	private void creatFile(MedicalKnowledgeParam param) throws IOException{
		// 更新文章URL
		File tempFile = new File(this.getClass().getResource(param.getTemplateFile()).getPath());
		FileReader reader = new FileReader(tempFile);
		BufferedReader br = new BufferedReader(reader);
		StringBuffer sf = new StringBuffer();
		String str = "";
		while ((str = br.readLine()) != null) {
			if (str.contains("{{title}}")) {
				String title = param.getTitle();
				str = str.replace("{{title}}", replaceSpecialToken(param.getTitle()));
			}
			if (str.contains("{{imgSrc}}")) {
				if (StringUtil.isNotEmpty(param.getCopy()) && param.getIsShow()!= null && param.getIsShow() == 1) {
					str = str.replace("{{imgSrc}}",replaceSpecialToken(param.getCopy()) );
				} else {
					str = str.replace("<img src=\"{{imgSrc}}\" class=\"font-img\">", "");
				}
			}
			if (str.contains("{{author}}")) {
				str = str.replace("{{author}}", replaceSpecialToken(param.getAuthorName()));
			}
			
			if (str.contains("{{content}}")) {
				if (StringUtil.isNotEmpty(param.getContent())) {
					str = str.replace("{{content}}", param.getContent());
				} else {
					str = str.replace("{{content}}", "");
				}
			}
			if(str.contains("{{summary}}")){
				if (StringUtil.isNotEmpty(param.getDescription())) {
					str = str.replace("{{summary}}", replaceSpecialToken(param.getDescription()));
				} else {
					str = str.replace("{{summary}}", "");
				}
			}
			if (str.contains("{{date}}")) {
				String dateStr = DateUtil.formatDate2Str(param.getCreateTime(),DateUtil.FORMAT_YYYY_MM_DD);
				str = str.replace("{{date}}", dateStr);
			}
			if(str.contains("{{docHostpital}}")){
				str = str.replace("{{docHostpital}}", param.getHospital()==null?"":param.getHospital());
			}
			if(str.contains("{{docImgSrc}}")){
				if (StringUtil.isNotEmpty(param.getAuthorIoc())) {
					str = str.replace("{{docImgSrc}}", param.getAuthorIoc());
				} else {
					str = str.replace("<img src=\"{{docImgSrc}}\" class=\"author-img\">", "");
				}
			}
			if(str.contains("{{docName}}")){
				str = str.replace("{{docName}}", param.getAuthorName()==null?"":param.getAuthorName());
			} 
			if(str.contains("{{docTitle}}")){
				if(param.getCreaterType().intValue() == KnowledgeEnum.CreaterType.group.getIndex() || StringUtil.isEmpty(param.getDocTitle())){
					str = str.replace("{{docTitle}}", "");
				}else{
					str = str.replace("{{docTitle}}", param.getDocTitle());
				}
			}
			if(str.contains("{{docDept}}")){
				if(param.getCreaterType() == KnowledgeEnum.CreaterType.group.getIndex() || StringUtil.isEmpty(param.getDocDept())){
					str = str.replace("{{docDept}}", "");
				}else{
					str = str.replace("{{docDept}}", param.getDocDept());
				}
			}
			if(str.contains("{{docDesc}}")){
				str = str.replace("{{docDesc}}", param.getDesc()==null?"":param.getDesc());
			}
			if(str.contains("{{codeSrc}}")){
				if (StringUtil.isNotEmpty(param.getCodeSrc())) {
					str = str.replace("{{codeSrc}}", param.getCodeSrc());
				} else {
					str = str.replace("<img class=\"code\" src=\"{{codeSrc}}\"/>", "");
				}
			}
			sf.append(str);
		}
		reader.close();
		// 输出 (输入流)
		File html = new File(param.getFileName());

		if (html.exists()) {
			html.delete();
		} else {
			html.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(html);
		out.write(sf.toString().getBytes());
		out.close();
	}
	
	@Override
	public PageVO getGroupMedicalKnowledgeList(MedicalKnowledgeParam param) {
		PageVO result = new PageVO();
		result.setPageIndex(param.getPageIndex());
		result.setPageSize(param.getPageSize());
		
		String groupId = param.getGroupId();
		if (StringUtil.isBlank(groupId)) {
			throw new ServiceException("集团id不能为空");
		}
		//查找category表，查询所有的就医知识Id
		List<KnowledgeCategory> knowledgeCategoryList = categoryDao.selectKnowledgeCategoryListByGroupId(groupId,true);
		Map<String,KnowledgeCategory> map = setCategoryRelation(knowledgeCategoryList);
		Set<String> knowledgeIdSet = new HashSet<String>();
		for (int i = 0; i < knowledgeCategoryList.size(); i++) {
			KnowledgeCategory kc = knowledgeCategoryList.get(i);
			String[] knowledgeIds = kc.getKnowledgeIds();
			if (knowledgeIds!=null) {
				for (int j = 0; j < knowledgeIds.length; j++) {
					knowledgeIdSet.add(knowledgeIds[j]);
				}
			}
		}
		long total = knowledgeIdSet.size();
		int startIndex = param.getPageIndex()*param.getPageSize();
		//没有需要返回的就医知识
		if (knowledgeIdSet.size()<=startIndex) {
			return result;
		}
		List<String> ids = new ArrayList<String>(knowledgeIdSet);
		List<MedicalKnowledgeVO> medicalList = getMedicalKnowledgeByIds1(ids,param.getGroupId(),param.getPageIndex(),param.getPageSize());
		if (medicalList!=null && map != null) {
			setCategeory( medicalList, map);
		}
		result.setTotal(total);
		result.setPageData(medicalList);
		
		return result;
	}
	private void setCategeory(List<MedicalKnowledgeVO> medicalList,Map<String,KnowledgeCategory> map){
		for(MedicalKnowledgeVO vo : medicalList){
			KnowledgeCategory temp = map.get(vo.getId());
			if(temp !=null){
				vo.setCategoryId(temp.getId());
				vo.setCategoryName(temp.getName());
			}
		}
	}
	private Map<String,KnowledgeCategory> setCategoryRelation(List<KnowledgeCategory> knowledgeCategoryList ){
		Map<String,KnowledgeCategory> map = new HashMap<String,KnowledgeCategory>();
		for(int i=0;i<knowledgeCategoryList.size(); i++){
			KnowledgeCategory category = knowledgeCategoryList.get(i);
			String[] knowlegeList = category.getKnowledgeIds()==null?new String[]{}:category.getKnowledgeIds();
			for(String str :knowlegeList){
				map.put(str, category);
			}
		}
		return map;
	}
	
	private List<MedicalKnowledgeVO> getMedicalKnowledgeByIds1(List<String> ids,String bizId,Integer pageIndex,Integer pageSize) {
		// 置顶就医知识总数
		long topCount = knowledgeDao.getMedicalKnowledgeTopCount(bizId);
		// 分页查询置顶就医知识,topList 不能为空
		List<MedicalKnowledgeTop> topList = new ArrayList<MedicalKnowledgeTop>();
		if (ids!=null && ids.size()>0) {
			topList = knowledgeDao.getMedicalKnowledgeTopIds(ids,bizId,
					pageIndex * pageSize, pageSize);
		}
		List<ObjectId> topIdList = new ArrayList<ObjectId>(topList.size());
		List<String> topIdStringList = new ArrayList<String>(topList.size());
		for (int i = 0; i < topList.size(); i++) {
			topIdStringList.add(topList.get(i).getKnowledgeId());
			topIdList.add(new ObjectId(topList.get(i).getKnowledgeId()));
		}
		// 批量查询置顶就医知识
		List<MedicalKnowledge> medicalTopList = null;
		if (topIdList.size() > 0) {
			medicalTopList = knowledgeDao.selectMedicalKnowledgeListByIds(topIdList,false);
			//组成map，方便查询
			Map<String,MedicalKnowledge> medicalTopMap = new HashMap<String,MedicalKnowledge>();
			for (int i = 0; i < medicalTopList.size(); i++) {
				MedicalKnowledge knowledge = medicalTopList.get(i);
				medicalTopMap.put(knowledge.getId(), knowledge);
			}
			// 按照原有置顶优先级排序
			medicalTopList.clear();//清空数组
			for (int i = 0; i < topList.size(); i++) {
				MedicalKnowledge knowledge = medicalTopMap.get(topList.get(i).getKnowledgeId());
				if (knowledge!=null) {
					medicalTopList.add(knowledge);
				}
			}
		}
		// 查询非置顶就医知识
		List<MedicalKnowledge> medicalUnTopList = null;
		if (topIdList.size() < pageSize) {
			int offset = (int) (pageIndex * pageSize - topCount);
			if (offset < 0) {
				offset = 0;
			}
			int limit = pageSize - topIdList.size();
			// 查询所有的置顶就医知识
			topList = knowledgeDao.getMedicalKnowledgeTopIds(null,bizId);
			Set<String> topSet = new HashSet<String>(topList.size());
			for (int i = 0; i < topList.size(); i++) {
				topSet.add(topList.get(i).getKnowledgeId());
			}
			
			// 获取非置顶就医知识id集合
			List<ObjectId> unTopIdList = new ArrayList<ObjectId>();
			for (int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				if (!topSet.contains(id)) {
					unTopIdList.add(new ObjectId(id));
				}
			}
			// 查找非置顶就医知识,按照时间降序排序
			if (unTopIdList.size()>0) {
				medicalUnTopList = knowledgeDao.selectMedicalKnowledgeListByIds(unTopIdList,false, offset, limit);
			}
		}
		
		// 合并两个list
		List<MedicalKnowledge> medicalList = null;
		if (medicalTopList!=null) {
			medicalList = medicalTopList;
			if (medicalUnTopList!=null) {
				medicalList.addAll(medicalUnTopList);
			}
		} else {
			medicalList = medicalUnTopList;
		}
		if (medicalList== null) {
			return null;
		}
		List<MedicalKnowledgeVO> result =  setNameAndIsTop(medicalList, topIdStringList);
		return result;
	}
	
	@Override
	public PageVO getDoctorMedicalKnowledgeList(MedicalKnowledgeParam param) {
		PageVO result = new PageVO();
		result.setPageIndex(param.getPageIndex());
		result.setPageSize(param.getPageSize());
		//根据doctorId 查询所有就医知识id
		if (param.getDoctorId()==null) {
			throw new ServiceException("医生id不能为空");
		}
		String doctorId = String.valueOf(param.getDoctorId());
		//web端调用--获取医生在集团的文章
		Map<String,KnowledgeCategory> map = null;
		List<String> ids = null;
		List<String> authorList = null;
		if ("0".equals(param.getAuthorType())) {
			// 只查询医生个人文章
			authorList = new ArrayList<String>();

		} else {
			// 查询医生个人和医生所属集团的文章
			authorList = knowledgeDao.getGroupIdByDoctorId(param.getDoctorId());
		}
		authorList.add(doctorId);
		List<MedicalKnowledge> knowledgeList = knowledgeDao.selectMedicalKnowledgeListByDoctorId(authorList);
		ids = new ArrayList<String>(knowledgeList.size());
		for (int i = 0; i < knowledgeList.size(); i++) {
			ids.add(knowledgeList.get(i).getId());
		}
		//医生个人文章与集团的就医知识文章取交集
		if ("0".equals(param.getAuthorType()) && StringUtil.isNotEmpty(param.getGroupId())) {
			// 查找category表，查询所有的就医知识Id
			List<KnowledgeCategory> knowledgeCategoryList = categoryDao
					.selectKnowledgeCategoryListByGroupId(param.getGroupId(), true);
			map = setCategoryRelation(knowledgeCategoryList);
			//获取集团的就医知识id
			Set<String> groupKnowledgeIdSet = new HashSet<String>();
			for (int i = 0; i < knowledgeCategoryList.size(); i++) {
				KnowledgeCategory kc = knowledgeCategoryList.get(i);
				String[] knowledgeIds = kc.getKnowledgeIds();
				if (knowledgeIds!=null) {
					for (int j = 0; j < knowledgeIds.length; j++) {
						groupKnowledgeIdSet.add(knowledgeIds[j]);
					}
				}
			}
			//遍历医生的就医知识id,如果不在这个范围以内的就医知识，则删除
			List<String> notinGroup = new ArrayList<String>();
			for (int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				if (!groupKnowledgeIdSet.contains(id)) {
					notinGroup.add(id);
				}
			}
			ids.removeAll(notinGroup);
		}
		long total = ids.size();
		
		List<MedicalKnowledgeVO> medicalList = getMedicalKnowledgeByIds1(ids,doctorId,param.getPageIndex(),param.getPageSize());
		//设置分类
		if (medicalList!=null && map != null) {
			setCategeory( medicalList, map);
		}
		result.setTotal(total);
		result.setPageData(medicalList);
		return result;
	}

	@Override
	public  Map<String, Object> getUrlById(String id) {
		if (StringUtil.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		boolean result = false;
		Map<String, Object> map = new HashMap<String,Object>();
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(id);
		if (knowledge==null) {
			map.put("status", result);
			map.put("msg", "未找到对应的分类");
			return map;
		}
		//浏览量加1
		MedicalKnowledge updateParam = new MedicalKnowledge();
		updateParam.setId(knowledge.getId());
		updateParam.setVisitCount(knowledge.getVisitCount()==null?1:knowledge.getVisitCount()+1);
		result = knowledgeDao.updateMedicalKnowledgeById(updateParam);
		map.put("status", result);
		if (result) {
			map.put("url", knowledge.getUrl());
		} else {
			map.put("msg", "浏览量增加失败");
		}
		return map;
	}
	
	@Override
	public MedicalKnowledgeVO getDetailById(MedicalKnowledgeParam param){
		if (StringUtil.isEmpty(param.getId())) {
			throw new ServiceException("id不能为空");
		}
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(param.getId());
		MedicalKnowledgeVO vo = BeanUtil.copy(knowledge, MedicalKnowledgeVO.class);
		vo.setAuthorName(getName(vo.getCreaterType(), vo.getAuthor()));
		//查询分类名
		if (StringUtil.isNotEmpty(param.getCategoryId())) {
			KnowledgeCategory category = categoryDao.selectKnowledgeCategoryById(param.getCategoryId());
			if (category!=null) {
				vo.setCategoryName(category.getName());
			}
		}
		return vo;
	}

	@Override
	public MedicalKnowledge getById(String id){
		if (StringUtil.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(id);
		return knowledge;
	}

    @Override
    public MedicalKnowledge findByIdSimple(String id){
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("id不能为空");
        }
        MedicalKnowledge knowledge = knowledgeDao.findByIdSimple(id);
        return knowledge;
    }
	
	@Override
	public List<MedicalKnowledge> getByIds(List<String> ids){
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		List<MedicalKnowledge> ret = new ArrayList<MedicalKnowledge>(ids.size());
		for (String id:ids) {
			MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(id);
			ret.add(knowledge);
		}
		return ret;
	}

    @Override
    public List<MedicalKnowledge> findByIdsSimple(List<String> ids){
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<MedicalKnowledge> ret = knowledgeDao.findByIdsSimple(ids);
        return ret;
    }
	
	@Override
	public Map<String, Object> setTop(String id,String bizId) {
		if (StringUtil.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		if (StringUtil.isEmpty(bizId)) {
			throw new ServiceException("bizId不能为空");
		}
		Map<String, Object> map = new HashMap<String,Object>();
		//查询就医知识
		MedicalKnowledge knowledge = knowledgeDao.selectMedicalKnowledgeById(id);
		if (knowledge==null) {
			map.put("status", false);
			map.put("msg", "未找到对应的就医知识");
			return map;
		}
		MedicalKnowledgeTop top = knowledgeDao.selectMedicalKnowledgeTopByKnowledgeId(id, bizId);
		if (top!=null) {
			map.put("status", false);
			map.put("msg", "就医知识已置顶");
			return map;
		}
		
		Integer priority = knowledgeDao.getMaxPriority(bizId)+1;
		//设置就医知识置顶
		top = new MedicalKnowledgeTop();
		top.setKnowledgeId(id);
		top.setBizId(bizId);
		top.setPriority(priority);
		//增加记录
		top = knowledgeDao.insertMedicalKnowledgeTop(top);
		boolean result = top!=null? true:false;
		map.put("status", result);
		map.put("msg", result?"置顶成功":"置顶失败");
		return map;
	}

	@Override
	public Map<String, Object> cancelTop(String id,String bizId) {
		if (StringUtil.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		if (StringUtil.isBlank(bizId)) {
			throw new ServiceException("bizId不能为空");
		}
		boolean result = knowledgeDao.delMedicalKnowledgeTop(id, bizId);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("status", result);
		map.put("msg", result?"取消置顶成功":"取消置顶失败");
		return map;
	}
	
	@Override
	public Map<String, Object> upKnowledge(String id,String bizId){
		if (StringUtil.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		if (StringUtil.isBlank(bizId)) {
			throw new ServiceException("bizId不能为空");
		}
		Map<String, Object> map = new HashMap<String,Object>();
		boolean result = false;
		//查找置顶信息
		MedicalKnowledgeTop knowledgeTop= knowledgeDao.selectMedicalKnowledgeTopByKnowledgeId(id, bizId);
		if (knowledgeTop==null) {
			map.put("status", result);
			map.put("msg", "没有找到置顶就医知识");
			return map;
		}
		Integer priority = knowledgeTop.getPriority();
		//查找priority比这个要大的一条记录
		MedicalKnowledgeTop knowledgeTopBigger = knowledgeDao.selectMedicalKnowledgeTopBiggerThanPriority(bizId, priority);
		if (knowledgeTopBigger==null) {
			map.put("status", result);
			map.put("msg", "没有找到上一条置顶就医知识");
			return map;
		}
		knowledgeTop.setPriority(knowledgeTopBigger.getPriority());
		knowledgeTopBigger.setPriority(priority);
		//互换priority
		result = knowledgeDao.updateMedicalKnowledgeTopById(knowledgeTop);
		if (result) {
			result = knowledgeDao.updateMedicalKnowledgeTopById(knowledgeTopBigger);
		} 
		map.put("status", result);
		map.put("msg", result?"上移就医知识成功":"上移就医知识失败");
		return map;
	}
	
	private List<KnowledgeCategory> searchOtherCategery(List<String> groupIds){
		String keyword = KnowledgeEnum.OTHER_CATEGORY;
		List<KnowledgeCategory> category = categoryDao.searchCategory(groupIds, keyword);
		return category;
	}
	
	private KnowledgeCategory addNoCategery(String groupId){
		KnowledgeCategory addCategory = new KnowledgeCategory();
		addCategory.setName(KnowledgeEnum.OTHER_CATEGORY);
		addCategory.setGroupId(groupId);
		addCategory.setWeight(0);
		return categoryDao.insertKnowledgeCategory(addCategory);
	}
	
	private MedicalKnowledge genMedicalKnowledgeByParam(MedicalKnowledgeParam param){
		return BeanUtil.copy(param, MedicalKnowledge.class);
	}
	@Override
	public Map<String, Object> addKnowledge(MedicalKnowledgeParam param) {
		//参数校验
		if (StringUtil.isEmpty(param.getTitle())) {
			throw new ServiceException("标题不能为空");
		}
		if (param.getCreaterType()==null) {
			throw new ServiceException("创建者类型不能为空");
		}
		if (StringUtil.isEmpty(param.getCreater())) {
			throw new ServiceException("创建者不能为空");
		}
		if (StringUtil.isEmpty(param.getAuthor())) {
			throw new ServiceException("作者不能为空");
		}
		if (StringUtil.isEmpty(param.getCopy())) {
			throw new ServiceException("题图不能为空");
		}
		if (StringUtil.isEmpty(param.getContent())) {
			throw new ServiceException("正文不能为空");
		}
		
		Map<String, Object> map = new HashMap<String,Object>();
		//参数设置
		//如果是医生
		param.setVisitCount(0);
		param.setShareCount(0);
		param.setEnabled(true);
		long time = System.currentTimeMillis();
		param.setCreateTime(time);
		param.setUpdateTime(time);
		//作者名称
//		param.setAuthorName(getName(param.getCreaterType(), param.getAuthor()));
		setDescription(param);
		//增加就医知识
		MedicalKnowledge insertKnowledge = genMedicalKnowledgeByParam(param);
		MedicalKnowledge knowledge = knowledgeDao.insertMedicalKnowledge(insertKnowledge);
		boolean result = false;
		MedicalKnowledge updateParam = new MedicalKnowledge();
		if (knowledge!=null) {
			param.setId(knowledge.getId());
			//获取名称authorName
			try {
				//获得url连接
//				String url = genKnowledgeUrl(param);
//				updateParam.setUrl(url);
				setAuthorInfo(param);
				genKnowledgeFileAndUrl(param,false);//生成非分享
				genKnowledgeFileAndUrl(param,true);//生成分享
				updateParam.setUrl(param.getUrl());
				updateParam.setId(knowledge.getId());
				updateParam.setShareUrl(param.getShareUrl());
				updateParam.setIsShow(param.getIsShow());
				result = knowledgeDao.updateMedicalKnowledgeById(updateParam);
			} catch (ServiceException e){
				knowledgeDao.delMedicalKnowledgePhysics(knowledge.getId());
				map.put("status", result);
				map.put("msg", e.getMessage());
				return map;
			} catch (Exception e) {
				//回滚
				knowledgeDao.delMedicalKnowledgePhysics(knowledge.getId());
				map.put("status", result);
				map.put("msg", "存储文件失败");
				return map;
			}
		}
		List<String> groupIds = null;
		//查询医生所有集团
		if (isNumeric(param.getAuthor())) {
			groupIds = knowledgeDao.getGroupIdByDoctorId(Integer.parseInt(param.getAuthor()));
		} else {
			groupIds = new ArrayList<String>();
			groupIds.add(param.getAuthor());
		}
		//添加到categery 表中
		if (result) {
			//添加到主集团或者指定集团
			String categoryId = param.getCategoryId();
			List<KnowledgeCategory> otherCategory = null;
			//查询其他分类
			int index = 0;
			if(StringUtil.isEmpty(categoryId)){
				if(groupIds!=null && groupIds.size()>0){
					otherCategory = searchOtherCategery(groupIds);
				}
			} else {
				index = 1;
				if (index<groupIds.size()) {
					otherCategory = searchOtherCategery(groupIds.subList(index, groupIds.size()));
				}
				////添加到主集团或者指定集团categery 表中
				result = categoryDao.insertKnowledgeintoCategory(categoryId, knowledge.getId());	
			}
			if (otherCategory==null) {
				otherCategory = new ArrayList<KnowledgeCategory>();
			}
			Map<String,KnowledgeCategory> otherCategoryMap = new HashMap<String,KnowledgeCategory>();
			for (int i = 0; i < otherCategory.size(); i++) {
				KnowledgeCategory category = otherCategory.get(i);
				otherCategoryMap.put(category.getGroupId(), category);
			}
			//其他分类
			for (int i = index; i < groupIds.size(); i++) {
				String  groupId = groupIds.get(i);
				KnowledgeCategory category = otherCategoryMap.get(groupId);
				if (category==null) {
					category = addNoCategery(groupId);
				}
				//添加到categery 表中
				result = categoryDao.insertKnowledgeintoCategory(category.getId(), knowledge.getId());
			}
		}	
		
		map.put("status", result);
		map.put("msg", result?"添加就医知识成功":"添加就医知识失败");
		return map;
	}
	
	/**
	 * 医生加入集团时，把医生创建的文章加入到集团里
	 * @param source 医生Id
	 * @param to 储团Id
	 * @return
	 */
	@Override
	public boolean addDoctorToGroup(String source,String to){
		boolean result = false;
		if(StringUtil.isEmpty(source)||StringUtil.isEmpty(to)){
			return result;
		}
		//查询医生所创建的文章
		List<MedicalKnowledge> list = knowledgeDao.getKnowledgeListByCreaterId(source, false);
		if(list.isEmpty()){
			return true;
		}
		List<String> idList =(List<String>) convertToObjectId(list, false);
		
		KnowledgeCategory  defaultCategory = categoryDao.selectDefaultKnowledgeCategoryById(to, KnowledgeEnum.OTHER_CATEGORY);
		if(defaultCategory == null){
			defaultCategory = addNoCategery(to);
		}
		List<String> defaultIds = defaultCategory.getKnowledgeIds()==null?new ArrayList<String>():Arrays.asList(defaultCategory.getKnowledgeIds());
		Set<String> set = new HashSet<String>(defaultIds);
		set.addAll(idList);
		
		String defaultId = defaultCategory.getId();
		defaultCategory = new KnowledgeCategory();
		defaultCategory.setId(defaultId);
		defaultCategory.setKnowledgeIds((String[])set.toArray(new String[set.size()]));
		categoryDao.updateKnowledgeCategoryById(defaultCategory);
		return true;
	}
}

