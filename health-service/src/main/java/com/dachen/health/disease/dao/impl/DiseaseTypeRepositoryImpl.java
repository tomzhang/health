package com.dachen.health.disease.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.handler.param.SearchParam;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.util.TypeDefine.Constants;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.dao.mongo.BaseRepositoryImpl;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.commons.vo.RecommDiseaseVO;
import com.dachen.health.commons.vo.RecommomDiseaseType;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.disease.vo.DiseaseTypeVo;
import com.dachen.health.recommand.dao.IDiseaseLaberDao;
import com.dachen.util.MongodbUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.*;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 
 * @author vincent
 *
 */
@Repository
public class DiseaseTypeRepositoryImpl extends BaseRepositoryImpl<DiseaseType, String> implements DiseaseTypeRepository {
	
	@Autowired
	private IDiseaseLaberDao laberDao;

    @Override
    public List<DiseaseType> findByDept(String deptId) {
        Query<DiseaseType> dq = dsForRW.createQuery(DiseaseType.class);
        dq.field("department").equal(deptId);
        return dq.asList();
    }

    @Override
    public List<DiseaseType> findByName(String name) {
        Query<DiseaseType> dq = dsForRW.createQuery(DiseaseType.class);
		//dq.filter("name",name);
		dq.filter("enable",true);
        dq.field("name").contains(name);
        return dq.asList();
    }

    @Override
    public List<DiseaseType> findByIds(List<String> diseaseIds) {
        Query<DiseaseType> dq = dsForRW.createQuery(DiseaseType.class);
        dq.field("_id").in(diseaseIds);
        return dq.asList();
    }
    
    /**
     * 医生集团擅长病种
     */
    @Override
    public String findNameByIds(List<String> diseaseIds) {
		if (CollectionUtils.isEmpty(diseaseIds)) {
			return "";
		}

		List<DiseaseType> diseases = this.findByIds(diseaseIds);
		if (CollectionUtils.isEmpty(diseases)) {
			return "";
		}
		
		StringBuilder disease = new StringBuilder();
		for (DiseaseType type : diseases) {

			if (disease.length() == 0) {
				disease.append(type.getName());
			} else {
				disease.append("、");
				disease.append(type.getName());
			}
		}
		return disease.toString();
    }

    public List<RecommDiseaseVO> findDiseaseType(PageVO param) {
        DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type").find().sort(new BasicDBObject("weight", -1)).skip(param.getStart()).limit(param.getPageSize());
        List<RecommDiseaseVO> recommDiseaseVos = new ArrayList<RecommDiseaseVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            RecommDiseaseVO recommDiseaseVO = new RecommDiseaseVO();
            recommDiseaseVO.setDiseasesId(obj.get("_id").toString());
            recommDiseaseVO.setDiseasesName(obj.get("name").toString() == null ? "" : obj.get("name").toString());
            recommDiseaseVO.setDiseasesName(obj.get("name").toString() == null ? "" : obj.get("name").toString());
            recommDiseaseVO.setImgPath(obj.get("imgPath") == null ? "": this.getImgPath(obj.get("imgPath").toString()));
            recommDiseaseVos.add(recommDiseaseVO);
        }
        RecommDiseaseVO vo = new RecommDiseaseVO();
        vo.setMorePath(getImgPath("department/more.png"));//返回更多
        recommDiseaseVos.add(vo);
        return recommDiseaseVos;
    }
    //根据图片的名称去七牛获取相应的图片地址
    public String getImgPath(String imgPath) {
		return MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, imgPath);
	}
    
    @Override
	public DiseaseTypeVO getDiseaseTypeTreeById(String id) {
		if (StringUtil.isEmpty(id)){
			return null;
		}
		return dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).filter("_id", id).get();
	}

    /**
     * </p>根据病种名称模糊查询出id</p>
     * 
     * @param name
     * @return
     * @author fanp
     * @date 2015年10月8日
     */
    public List<String> findIdByName(String name) {
        List<String> ids = new ArrayList<String>();

        DBObject query = new BasicDBObject();
        Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
        query.put("name", pattern);

        DBObject project = new BasicDBObject();
        project.put("_id", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type").find(query, project).sort(new BasicDBObject("weight", -1));
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            ids.add(MongodbUtil.getString(obj, "_id"));
        }
        return ids;
    }

	public List<RecommDiseaseVO> findDiseaseType(PageVO param,
			List<String> diseaseIds) {
		
		DBObject in = new BasicDBObject();
		in.put("$in", diseaseIds);
		DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type").find(new BasicDBObject("_id",in)).sort(new BasicDBObject("weight", -1)).skip(param.getStart()).limit(param.getPageSize());
		List<RecommDiseaseVO> recommDiseaseVos = new ArrayList<RecommDiseaseVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            RecommDiseaseVO recommDiseaseVO = new RecommDiseaseVO();
            recommDiseaseVO.setDiseasesId(obj.get("_id").toString());
            recommDiseaseVO.setDiseasesName(obj.get("name").toString() == null ? "" : obj.get("name").toString());
            recommDiseaseVos.add(recommDiseaseVO);
        }
        return recommDiseaseVos;
		
	}

	@Override
	public DiseaseTypeVo getDiseaseAlias(String id) {
		
		DiseaseTypeVo result = new DiseaseTypeVo();
		
		DiseaseType diseaseType = dsForRW.createQuery(DiseaseType.class).field("_id").equal(id).get();
		
		String alias = diseaseType.getAlias(); 
		List<String> alia = Lists.newArrayList();
		if(StringUtils.isNotEmpty(alias)) {
			alias = alias.replaceAll(",", "，");
			String[] tempAlias = alias.split("，");
			Collections.addAll(alia, tempAlias);
		}
		
		result.setAlias(alia);
		
		List<RecommomDiseaseType> recommomDiseaseTypes =  dsForRW.createQuery(RecommomDiseaseType.class).order("weight").asList();
		for(RecommomDiseaseType type : recommomDiseaseTypes) {
			if (id.equals(type.getDiseaseId())) {
				result.setRecommendName(type.getName());
			}
		}
		
		return result;
	}

	@Override
	public List<DiseaseTypeVO> getTreeByKeyword(String keyword) {
		
		if (StringUtils.isEmpty(keyword)) {
			throw new ServiceException("请输入关键字！");
		}
		
		List<DiseaseTypeVO> result = Lists.newArrayList();
		
		//根据关键字模糊搜索对应节点id
		List<String> ids = Lists.newArrayList();
		DBObject query = new BasicDBObject();
		BasicDBList word = new BasicDBList();
		Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
		word.add(new BasicDBObject("name", pattern));
		word.add(new BasicDBObject("alias", pattern));
		query.put(QueryOperators.OR, word);
		query.put("enable",true);
		DBCursor dCursor = dsForRW.getDB().getCollection("b_disease_type").find(query);
		while (dCursor.hasNext()) {
			DBObject object = dCursor.next();
			ids.add(MongodbUtil.getString(object, "_id"));
		}
		
		if (ids.size() == 0) {
			return result;
		}

		result = buildTypeTreeByIds(ids);

		return result;
	}

	@Override
	public List<DiseaseTypeVO> buildTypeTreeByIds(List<String> diseaseIds) {

		List<DiseaseTypeVO> result = new ArrayList<>();

		List<DiseaseTypeVO> diseaseTypeVOs = dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).order("-weight").asList();

		//将diseaseTypeVOs转化为Map
		Map<String, DiseaseTypeVO> diseaseTypeMap = Maps.newHashMap();
		if (diseaseTypeVOs != null && diseaseTypeVOs.size() > 0){
			for (DiseaseTypeVO diseaseTypeVO : diseaseTypeVOs) {
				diseaseTypeMap.put(diseaseTypeVO.getId(), diseaseTypeVO);
			}
		}

		Set<DiseaseTypeVO> diseaseTypeSet = Sets.newHashSet();
		for(String id : diseaseIds) {
			DiseaseTypeVO diseaseTypeVO = diseaseTypeMap.get(id);
			if (diseaseTypeVO.isLeaf() == true) {
				getParentDiseaseTypes(diseaseTypeMap, diseaseTypeSet, diseaseTypeVO);
			}else {
				getChildDiseaseTypes(diseaseTypeMap, diseaseTypeSet, diseaseTypeVO);
			}
		}

		//构造树形结构

		if (diseaseTypeSet != null && diseaseTypeSet.size() > 0) {
			for (DiseaseTypeVO diseaseTypeVO : diseaseTypeSet) {
				if (StringUtils.equals(diseaseTypeVO.getParent(), "0")) {
					setAllDiseaseTypes(diseaseTypeSet, diseaseTypeVO);
					result.add(diseaseTypeVO);
				}
			}
		}

		return result;
	}
	
	public void getParentDiseaseTypes(Map<String, DiseaseTypeVO> map, Set<DiseaseTypeVO> set, DiseaseTypeVO diseaseTypeVO) {
		
		set.add(diseaseTypeVO);
		if (!StringUtils.equals(diseaseTypeVO.getParent(), "0")) {
			DiseaseTypeVO parent = map.get(diseaseTypeVO.getParent());
			getParentDiseaseTypes(map, set, parent);
		}
	}
	
	public void getChildDiseaseTypes(Map<String, DiseaseTypeVO> map, Set<DiseaseTypeVO> set, DiseaseTypeVO diseaseTypeVO) {
		set.add(diseaseTypeVO);
		if (diseaseTypeVO.isLeaf() == false) {
			for (DiseaseTypeVO value : map.values()) {  
				if (value.getParent().equals(diseaseTypeVO.getId())) {
					getChildDiseaseTypes(map, set, value);
				}  
			} 
		}
	}

	public void setAllDiseaseTypes(Set<DiseaseTypeVO> set, DiseaseTypeVO diseaseTypeVO) {
		String id = diseaseTypeVO.getId();
		List<DiseaseTypeVO> subList = Lists.newArrayList();
		for (DiseaseTypeVO diseaseTypeVO2 : set) {
			if (StringUtils.equals(id, diseaseTypeVO2.getParent())) {
				setAllDiseaseTypes(set, diseaseTypeVO2);
				subList.add(diseaseTypeVO2);
			}
		}
		diseaseTypeVO.setChildren(subList);
	}

	@Override
	public void addCommonDisease(String diseaseId, String name) {
		long weight = 2;
		if (diseaseId == null ) {
			throw new ServiceException("病种ID不能为空。");
		}
		
		List<RecommomDiseaseType> list =  dsForRW.createQuery(RecommomDiseaseType.class).order("weight").asList();
		
		if (list != null && list.size() > 0) {
			for(RecommomDiseaseType type : list) {
				if (diseaseId.equals(type.getDiseaseId()) && name.equals(type.getName())) {
					throw new ServiceException("请勿重复添加常见疾病。");
				}
				if (diseaseId.equals(type.getDiseaseId())) {
					removeCommonDisease(diseaseId);
				}
			}
		}

		RecommomDiseaseType disease = new RecommomDiseaseType();
		disease.setDiseaseId(diseaseId);
		disease.setName(name);
			
		if(list.isEmpty()){
			weight = 2;
		}else {
			weight = list.get(list.size() - 1).getWeight();
			weight = (weight+1);
		}
		disease.setWeight(weight);
			
		dsForRW.save(disease);
		
	}

	@Override
	public void removeCommonDisease(String diseaseId) {
		
		if (diseaseId == null ) {
			throw new ServiceException("病种ID不能为空。");
		}
		
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		values.add(diseaseId);
		in.put("$in", values);
		dsForRW.getDB().getCollection("t_disease_recommend").remove(new BasicDBObject("_id", in));
		
	}

	@Override
	public void upWeight(String diseaseId) {
		List<RecommomDiseaseType> types = dsForRW.createQuery(RecommomDiseaseType.class).order("-weight").asList();
		
		for (int i = 0; i < types.size(); i++) {
			Long temp = null;
			if (diseaseId.equals(types.get(i).getDiseaseId())) {
				if (i == 0) {
					throw new ServiceException("当前已为第一位，无需上移。");
				}
				temp = types.get(i).getWeight();
				types.get(i).setWeight(types.get(i-1).getWeight());
				types.get(i-1).setWeight(temp);
				updateDiseaseRecommended(types.get(i));
				updateDiseaseRecommended(types.get(i-1));
			}
		}
	}
	
	public void updateDiseaseRecommended(RecommomDiseaseType vo) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", vo.getDiseaseId());
		BasicDBObject jo = new BasicDBObject();
		jo.put("weight",vo.getWeight());
		dsForRW.getDB().getCollection("t_disease_recommend").update(query, new BasicDBObject("$set", jo));
	}

	@Override
	public PageVO getDiseaseList(Integer pageIndex, Integer pageSize) {
		
		List<RecommomDiseaseType> result = Lists.newArrayList();
		
		long count = dsForRW.createQuery(RecommomDiseaseType.class).countAll();
		if (count > 0) {
			DBCursor dbCursor = dsForRW.getDB().getCollection("t_disease_recommend").find()
			.sort(new BasicDBObject("weight", -1)).skip(pageIndex*pageSize).limit(pageSize);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				RecommomDiseaseType disease = new RecommomDiseaseType();
				disease.setDiseaseId(MongodbUtil.getString(object, "_id"));
				disease.setName(MongodbUtil.getString(object, "name"));
				disease.setWeight(MongodbUtil.getLong(object, "weight"));
				result.add(disease);
			}
		}
		
		PageVO pageVO = new PageVO();
		pageVO.setPageData(result);
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		pageVO.setTotal(count);
		
		return pageVO;
	}

	@Override
	public DiseaseTypeVo findByIds(String diseaseId) {
		Query<DiseaseType> query = dsForRW.createQuery("b_disease_type", DiseaseType.class).field("_id").equal(diseaseId);
		DiseaseType diseaseType = query.get();
		
		DiseaseTypeVo diseaseTypeVo = null;
		if (diseaseType != null) {
			diseaseTypeVo = DiseaseTypeVo.parseFromDiseaseType(diseaseType);
		}
		
		return diseaseTypeVo;
	}

	@Override
	public void setDiseaseInfo(String diseaseId, String introduction, String alias, String remark, String attention) {
		//1、查找疾病
		if(StringUtils.isEmpty(diseaseId)) {
			throw new ServiceException("疾病id为空");
		}
		Query<DiseaseType> query = dsForRW.createQuery("b_disease_type", DiseaseType.class).field("_id").equal(diseaseId);
		DiseaseType diseaseType = query.get();
		if (diseaseType == null) {
			throw new ServiceException("未找到该疾病");
		}
		//2、更新疾病
		UpdateOperations<DiseaseType> ops = dsForRW.createUpdateOperations(DiseaseType.class);
		ops.set("introduction", introduction);
		if (StringUtils.isNotEmpty(alias)) {
			if (alias.contains(",")) {
				alias = alias.replaceAll(",", "，");
			}
			if (alias.contains("，")) {
				String[] checkAlias = alias.split("，");
				if (checkAlias.length > 20) {
					throw new ServiceException("常用名不能超过20个");
				}
				for(String str : checkAlias){
					if (str.length() > 20) {
						throw new ServiceException("常用名字数不能大于20");
					}
				}
			} else {
				if (alias.length() > 20) {
					throw new ServiceException("常用名字数不能大于20");
				}
			}
			ops.set("alias", alias);
		} else {
			ops.set("alias", alias);			
		}
		if (StringUtils.isNotEmpty(remark)) {
			if (remark.contains(",")) {
				remark = remark.replaceAll(",", "，");
			}
			EsDiseaseType esDiseaseType = new EsDiseaseType(diseaseType.getName(), remark);
			esDiseaseType.setBizId(diseaseId);
			if (StringUtils.equals("/", remark)) {
				//将该疾病从全文索引中删除(2016-08-24傅永德)
				ElasticSearchFactory.getInstance().deleteDocument(esDiseaseType);
			} else {
				ElasticSearchFactory.getInstance().insertDocument(esDiseaseType);
			}
			
			ops.set("remark", remark);
		} else {
			ops.set("remark", remark);
		}
		ops.set("attention", attention);
		dsForRW.findAndModify(query, ops);
	}

	@Override
	public PageVO findByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
		SearchParam searchParam = new SearchParam.Builder(Constants.INDEX_HEALTH)
								.searchKey(keyword)
								.type(new String[]{Constants.TYPE_DISEASE})
								.from(pageIndex * pageSize)
								.size(pageSize)
								.build();
        Map<String,List<String>> map_es = ElasticSearchFactory.getInstance().searchAndReturnBizId(searchParam);
        if(null==map_es) return null;
        logger.info("map_es:"+map_es.get("disease"));
        int size = map_es.get("disease").size();
        //将返回的疾病转化为 
        List<String> diseaseIds = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
        	diseaseIds.add(map_es.get("disease").get(i).toString());
		}
        List<DiseaseType> diseaseTypes = dsForRW.createQuery("b_disease_type",DiseaseType.class).field("_id").in(diseaseIds).asList();
        List<DiseaseType> result = Lists.newArrayList();
        List<String> diseaseNames = Lists.newArrayList();
        if (diseaseIds != null && diseaseIds.size() > 0) {
			diseaseIds.forEach((diseaseId) -> {
				diseaseTypes.forEach((diseaseType) -> {
					if (StringUtils.equals(diseaseId, diseaseType.getId()) && StringUtils.isNotEmpty(diseaseType.getRemark()) && !StringUtils.equals("/", diseaseType.getRemark())) {
						result.add(diseaseType);
						diseaseNames.add(diseaseType.getName());
					}
				});
			});
			if (diseaseNames != null && diseaseNames.size() > 0) {
				List<DiseaseType> inNamesDiseases = dsForRW.createQuery("b_disease_type",DiseaseType.class).field("name").in(diseaseNames).asList();
				if (inNamesDiseases.size() > result.size()) {
					List<String> nameList = Lists.newArrayList();
					inNamesDiseases.forEach((tempDisease) -> {
						nameList.add(tempDisease.getName());
					});
					Multiset<String> nameSet = HashMultiset.create(nameList);
					Set<String> duplicateName = Sets.newHashSet();
					nameSet.forEach((name) -> {
						if (nameSet.count(name) > 1) {
							duplicateName.add(name);
						}
					});
					duplicateName.forEach((name) -> {
						result.forEach((a) -> {
							if (StringUtils.equals(name, a.getName())) {
								String parentId = a.getParent();
								DiseaseType diseaseType = dsForRW.createQuery("b_disease_type",DiseaseType.class).field("_id").equal(parentId).get();
								a.setName(diseaseType.getName() + "-" + a.getName());
							}
						});
					});
				}
			}
			
		}
        
        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setPageData(result);
		return pageVO;
	}
	
	void sort(List<RecommomDiseaseType> types, List<UserDiseaseLaber> labers) {
		if (CollectionUtils.isEmpty(labers)) {
			Collections.sort(types, new Comparator<RecommomDiseaseType>() {

				@Override
				public int compare(RecommomDiseaseType o1, RecommomDiseaseType o2) {
					return (int) (o2.getWeight() - o1.getWeight());
				}
			});
		}else {
			Map<String, UserDiseaseLaber> map = Maps.newHashMap();
			for(UserDiseaseLaber laber : labers) {
				map.put(laber.getDiseaseId(), laber);
			}
			
			Collections.sort(types, new Comparator<RecommomDiseaseType>() {

				@Override
				public int compare(RecommomDiseaseType o1, RecommomDiseaseType o2) {
					UserDiseaseLaber laber1 = map.get(o1.getDiseaseId());
					UserDiseaseLaber laber2 = map.get(o2.getDiseaseId());
					
					if (laber1 != null && laber2 != null) {
						if (laber1.getWeight() == laber2.getWeight()) {
							return (int) (o2.getWeight() - o1.getWeight());
						}
						return laber2.getWeight() -laber1.getWeight();
					}
					
					if (laber1 != null && laber2 == null) {
						return -1;
					}
					
					if (laber1 == null && laber2 != null) {
						return 1;
					}
					
					return (int) (o2.getWeight() - o1.getWeight());
				}
			});
		}
	}

	@Override
	public PageVO getDiseaseListAfterSort(Integer pageIndex, Integer pageSize) {
		
		PageVO pageVO = new PageVO();
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		
		//查找该用户关注的疾病列表
		Integer userId = ReqUtil.instance.getUserId();
		List<UserDiseaseLaber> labers = laberDao.findByUserId(userId);

		//查找运营平台推荐的疾病列表
		List<RecommomDiseaseType> types = new ArrayList<>();
		
		long count = dsForRW.createQuery(RecommomDiseaseType.class).countAll();
		if (count > 0) {
			DBCursor dbCursor = dsForRW.getDB().getCollection("t_disease_recommend").find();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				RecommomDiseaseType disease = new RecommomDiseaseType();
				disease.setDiseaseId(MongodbUtil.getString(object, "_id"));
				disease.setName(MongodbUtil.getString(object, "name"));
				disease.setWeight(MongodbUtil.getLong(object, "weight"));
				types.add(disease);
			}
			sort(types, labers);	
		}
		
		//内存分页
		if (types.size() < pageVO.getStart()) {
			types = Lists.newArrayList();
		} else {
			if ((pageVO.getPageIndex() + 1) * pageVO.getPageSize() < types.size()) {
				types = types.subList(pageVO.getStart(), (pageVO.getPageIndex() + 1) * pageVO.getPageSize());
			} else {
				types = types.subList(pageVO.getStart(), types.size());
			}
		}
		
		pageVO.setPageData(types);
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		pageVO.setTotal(count);
		
		return pageVO;
	}

	@Override
	public DiseaseType findByDiseaseId(String diseaseId) {
		return dsForRW.createQuery(DiseaseType.class).filter("_id", diseaseId).get();
	}
	
}
