package com.dachen.health.base.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.param.AddHospitalParam;
import com.dachen.health.base.entity.param.CollegeParam;
import com.dachen.health.base.entity.param.DoctorParam;
import com.dachen.health.base.entity.param.MsgTemplateParam;
import com.dachen.health.base.entity.po.*;
import com.dachen.health.base.entity.vo.*;
import com.dachen.health.commons.constants.HospitalLevelEnum;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.BeanUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * ProjectName： health-service<br>
 * ClassName： AreaDaoImpl<br>
 * Description：地区分类dao实现类 <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
@Repository
public class BaseDataDaoImpl extends NoSqlRepository implements IBaseDataDao {
	private static Logger logger = LoggerFactory.getLogger(BaseDataDaoImpl.class);

	 /**
     * 新增一个医院名称
     * 
     * @param hospitalPO 省份
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public HospitalPO addHospital(HospitalPO hospitalPO) {

    	if(hospitalPO.getProvince() <= 0 || hospitalPO.getCity() <= 0 || 
    			hospitalPO.getCountry() <= 0 || StringUtil.isBlank(hospitalPO.getName())) {
    		return null;
    	}

    	// 要返回的
    	HospitalPO hospitalPO_Result = new HospitalPO();
        
        Key<HospitalPO> _key = dsForRW.insert("b_hospital", hospitalPO);
        if (_key == null) {
        	return null;
        }
        
        String key_id = _key.getId().toString();
        System.out.println("addHospital():key_id:"+key_id);
        hospitalPO_Result.setId(key_id);
        hospitalPO_Result.setProvince(hospitalPO.getProvince());
        hospitalPO_Result.setCity(hospitalPO.getCity());
        hospitalPO_Result.setCountry(hospitalPO.getCountry());
        hospitalPO_Result.setName(hospitalPO.getName());
        
        return hospitalPO_Result;
    }
    
    /**
     * 更新医院信息
     * 
     * @param hospitalPO 省份
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public HospitalPO updateHospital(HospitalPO hspo) {
        HospitalPO hsp = dsForRW.createQuery(HospitalPO.class).field("_id").
                                equal(hspo.getId()).get();
        if(Objects.isNull(hsp)){
            return null;
        }
        UpdateOperations<HospitalPO> ops = dsForRW.createUpdateOperations(HospitalPO.class);
        if(StringUtils.isNotBlank(hspo.getName())){
            ops.set("name", hspo.getName());
        }
        if(StringUtils.isNotBlank(hspo.getAddress())){
            ops.set("address", hspo.getAddress());
        }
        if(StringUtils.isNotBlank(hspo.getLevel())){
            ops.set("level", hspo.getLevel());
        }
        if(StringUtils.isNotBlank(hspo.getNature())){
            ops.set("nature", hspo.getNature());
        }
        if(StringUtils.isNotBlank(hspo.getType())){
            ops.set("type", hspo.getType());
        }
        if(StringUtils.isNotBlank(hspo.getProperty())){
            ops.set("property", hspo.getProperty());
        }
        if(hspo.getStatus() > 0){
            ops.set("status", hspo.getStatus());
        }
        if(hspo.getProvince() > 0){
            ops.set("province", hspo.getProvince());
        }
        if(hspo.getCity() > 0){
            ops.set("city", hspo.getCity());
        }
        if(hspo.getCountry() > 0){
            ops.set("country", hspo.getCountry());
        }
        if(hspo.getLoc().getLng() > 0){
            ops.set("loc.lng", hspo.getLoc().getLng());
            ops.set("lng", hspo.getLoc().getLng());
        }
        if(hspo.getLoc().getLat() > 0){
            ops.set("loc.lat", hspo.getLoc().getLat());
            ops.set("lat", hspo.getLoc().getLat());
        }
        ops.set("lastUpdatorTime", hspo.getLastUpdatorTime());
        UpdateResults result = dsForRW.update(hsp, ops);
        if(result.getUpdatedCount() > 0){
            return dsForRW.createQuery(HospitalPO.class).field("_id").equal(hspo.getId()).get();
        }
        return null;
    }
    
    /**
     * </p>根据父编码获取地区</p>
     * 
     * @param pcode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<AreaVO> getAreas(int pcode){
        return dsForRW.createQuery("b_area",AreaVO.class).field("pcode").equal(pcode).retrievedFields(true, "code","name").order("code").asList();
    }
    
    public List<AreaVO> getAreas(List<Integer> areaCodes) {
    	return dsForRW.createQuery("b_area",AreaVO.class).field("pcode").in(areaCodes).retrievedFields(true, "code","name").order("code").asList();
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.base.dao.IBaseDataDao#getAreaByCode(java.lang.Integer)
     */
    @Override
    public AreaVO getAreaByCode(Integer code){
    	//参数校验
    	if(code == null || code == 0 ){
    		throw new ServiceException("区域code 不能为空");
    	}
    	
    	return dsForRW.createQuery("b_area",AreaVO.class).field("code").equal(code).get();
    	
    }
    
    
    /**
     * </p>获取地区下的医院</p>
     * 
     * @param country
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<HospitalVO> getHospitals(int country,String name){
        if(country ==0 && StringUtil.isBlank(name)){
            return null;
        }
        
        Query<HospitalVO> query = dsForRW.createQuery("b_hospital",HospitalVO.class).retrievedFields(true, "name","country");
        
        if(country != 0){
            query.field("country").equal(country);
        }
        if(StringUtil.isNotBlank(name)){
            query.field("name").contains(name.trim());
        }
        return query.limit(100).asList();
    }

    public List<HospitalVO> getOkStatusHospitals(int country,String name){
        if(country ==0 && StringUtil.isBlank(name)){
            return null;
        }
        Query<HospitalVO> query = dsForRW.createQuery("b_hospital",HospitalVO.class)
                .field("status").equal(1) //此处硬编码是因为引用不到 EnableStatusEnum
                .retrievedFields(true, "name","country");
        if(country != 0){
            query.field("country").equal(country);
        }
        if(StringUtil.isNotBlank(name)){
            query.field("name").contains(name.trim());
        }
        return query.limit(100).asList();
    }

    
    public List<HospitalVO> getHospitals(Long timeline){
        Query<HospitalVO> query = dsForRW.createQuery("b_hospital",HospitalVO.class).retrievedFields(true, "name", "country");
        if (timeline != null) {// 获取增量
        	query.field("lastUpdatorTime").greaterThanOrEq(timeline);
        }
        return query.asList();
    }
    
    public PageVO getHospitals(Long timeline, String hospital, String hospitalId, PageVO page){
        DBObject query = new BasicDBObject();
        if (timeline > 0) {
            query.put("lastUpdatorTime", new BasicDBObject("$gte", timeline));
        }
        if (StringUtil.isNotBlank(hospitalId)) {
            query.put("_id", hospitalId);
        }
        if (StringUtil.isNotBlank(hospital)) {
            Pattern pattern = Pattern.compile("^.*" + hospital + ".*$");
            query.put("name", pattern);
        }
        DBObject sort = new BasicDBObject();
        sort.put("lastUpdatorTime", 1);
        DBCursor cursor = dsForRW.getDB().getCollection("b_hospital").find(query).sort(sort).skip(page.getStart()).limit(page.getPageSize());
        
        List<HospitalVO> data = new ArrayList<HospitalVO>();
        Set<Integer> areaSet = new HashSet<Integer>();
        HospitalVO p = null;
        DBObject dbObj = null;
        while(cursor.hasNext()){
            dbObj = cursor.next();
            p = new HospitalVO();
            if(!Objects.isNull(dbObj.get("_id"))){
                p.setId(dbObj.get("_id").toString());
            }
            if(!Objects.isNull(dbObj.get("name"))){
                p.setName(dbObj.get("name").toString());
            }
            if(!Objects.isNull(dbObj.get("level"))){
                p.setLevel(dbObj.get("level").toString());
            }
            if(!Objects.isNull(dbObj.get("province"))){
                p.setProvince(Integer.valueOf(dbObj.get("province").toString()));
                areaSet.add(p.getProvince());
            }
            if(!Objects.isNull(dbObj.get("city"))){
                p.setCity(Integer.valueOf(dbObj.get("city").toString()));
                areaSet.add(p.getCity());
            }
            if(!Objects.isNull(dbObj.get("country"))){
                p.setCountry(Integer.valueOf(dbObj.get("country").toString()));
                areaSet.add(p.getCountry());
            }
            if(!Objects.isNull(dbObj.get("status"))){
                p.setStatus(Integer.valueOf(dbObj.get("status").toString()));
            }
            if(!Objects.isNull(dbObj.get("address"))){
                p.setAddress(dbObj.get("address").toString());
            }
            if(!Objects.isNull(dbObj.get("lastUpdatorTime"))){
                p.setLastUpdatorTime(Long.valueOf(dbObj.get("lastUpdatorTime").toString()));
            }
            if(!Objects.isNull(dbObj.get("lat"))){
                p.setLat(dbObj.get("lat").toString());
            }
            if(!Objects.isNull(dbObj.get("lng"))){
                p.setLng(dbObj.get("lng").toString());
            }
            data.add(p);
        }
        
        List<Map<String, String>> areaList = null;
        if(!CollectionUtils.isEmpty(areaSet)){
            areaList = findArea(areaSet);
            for(HospitalVO v : data){
                for(Map<String, String> m : areaList){
                    if(Objects.nonNull(v.getProvince()) &&
                            StringUtil.equals(m.get("code"), v.getProvince()+"")){
                        v.setProvinceName(m.get("name"));
                    }
                    if(Objects.nonNull(v.getCity()) &&
                            StringUtil.equals(m.get("code"), v.getCity()+"")){
                        v.setCityName(m.get("name"));
                    }
                    if(Objects.nonNull(v.getCountry()) &&
                            StringUtil.equals(m.get("code"), v.getCountry()+"")){
                        v.setCountryName(m.get("name"));
                    }
                }
            }
        }
        Long total = new Long(dsForRW.getDB().getCollection("b_hospital").find(query).count());
        page.setTotal(total);
        page.setPageData(data);
        return page;
    }
    
    public List<HospitalVO> getHospitals(String name) {
    	if (StringUtil.isBlank(name)) {
    		return null;
    	}
    	return dsForRW.createQuery("b_hospital",HospitalVO.class).filter("name", name.trim()).asList();
    }
    
    /**
     * </p>获取科室</p>
     * 
     * @param deptId
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<DeptVO> getDepts(String deptId,String name){
        
        Query<DeptVO> query = dsForRW.createQuery("b_hospitaldept",DeptVO.class).retrievedFields(true, "name","_id","isLeaf", "parentId").filter("enableStatus", 1);
        
        if(StringUtil.isNotBlank(name)){
            query.field("name").contains(name.trim()).field("isLeaf").equal(1);
        }
        if(StringUtil.isNotBlank(deptId)){
            query.field("parentId").equal(deptId.trim());
        }
        query.order("-weight");
        return query.asList();
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.base.dao.IBaseDataDao#getDeptById(java.lang.String)
     */
    @Override
    public DeptVO getDeptById(String deptId){
    	//参数校验
    	if(StringUtils.isEmpty(deptId)){
    		throw new ServiceException("科室id不能为空");
    	}
        Query<DeptVO> query = dsForRW.createQuery("b_hospitaldept",DeptVO.class).retrievedFields(true, "name","_id","isLeaf","parentId");
        query.field("_id").equal(deptId.trim());
    	
    	return query.get();
    }
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<TitleVO> getTitles(){
        List<TitleVO> titles = dsForRW.createQuery("b_doctortitle", TitleVO.class).retrievedFields(true, "name", "_id").asList();
        Collections.sort(titles, new Comparator<TitleVO>() {
            @Override
            public int compare(TitleVO o1, TitleVO o2) {
                if (Integer.parseInt(o1.getId()) < Integer.parseInt(o2.getId())){
                    return -1;
                }else if (Integer.parseInt(o1.getId())  == Integer.parseInt(o2.getId())){
                    return  0;
                }else {
                    return 1;
                }
            }
        });
        return titles;
    }
    
    /**
     * </p>查找医院</p>
     * @param hospitalId
     * @return
     * @author fanp
     * @date 2015年7月17日
     */
    public HospitalVO getHospital(String hospitalId){
        return dsForRW.createQuery("b_hospital",HospitalVO.class).field("_id").equal(hospitalId).get();
    }

	public List<HospitalVO> getHospitalBylevelList(List<String> listLevel){
		return dsForRW.createQuery("b_hospital",HospitalVO.class).field("level").in(listLevel).asList();
	}
    /**
     * </p>查找科室</p>
     * @param dept
     * @return
     * @author fanp
     * @date 2015年7月17日
     */
    public DeptVO getDept(String dept){
        return dsForRW.createQuery("b_hospitaldept",DeptVO.class).field("name").equal(dept).get();
    }
    
    /**
     * </p>查找职称</p>
     * @param title
     * @return
     * @author fanp
     * @date 2015年7月17日
     */
    public TitleVO getTitle(String title){
        return dsForRW.createQuery("b_doctortitle",TitleVO.class).retrievedFields(true, "name","_id","rank").field("name").equal(title).get();
    }
    
    /**
     * </p>获取病种下所有子病种</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<String> getDiseaseTypeChildren(String id){
        List<String> list = new ArrayList<String>();
        
        if(StringUtil.isBlank(id)){
            return list;
        }
        
        DBObject query = new BasicDBObject();
        
        Pattern pattern = Pattern.compile("^" + id, Pattern.CASE_INSENSITIVE);
        query.put("_id", pattern);
        
        DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type").find(query).sort(new BasicDBObject("weight", -1));
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            list.add(MongodbUtil.getString(obj, "_id"));
        }
        return list;
    }
    
    /**
     * </p>获取病种</p>
     * @param ids
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    public List<DiseaseTypeVO> getDiseaseType(List<String> ids){
        if(ids==null || ids.size()==0){
            return null;
        }
        
        return dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).filter("_id in", ids).order("-weight").asList();
    }
    
    /**
     * </p>根据prantId查找病种</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    public List<DiseaseTypeVO> getDiseaseByParent(String parentId){
        return dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).retrievedFields(true, "_id","name","isLeaf","weight").field("parent").equal(parentId).order("-weight").asList();
    }
    
    /**
     * </p>根据prantId查找病种（返回到“科室－病种级别”）</p>
     * @param parentId
     * @return
     * @author dwju
     * @date 2015年11月18日
     */
    public List<DiseaseTypeVO> getOneLevelDiseaseByParent(String parentId){
    	List<DiseaseTypeVO> list = dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).retrievedFields(true, "_id","name","isLeaf","weight")
				.filter("enable",true)
        		.field("parent")
        		.equal(parentId)
        		.filter("isLeaf <> ", true)
        		.order("-weight")
        		.asList();
    	if (parentId.equals("0")) return list;
    	
    	for (int i=0; i<list.size(); i++) {
    		DiseaseTypeVO dis = list.get(i);
    		dis.setLeaf(true);
    	}
    	
    	return list;
    }
    
    /**
     * </p>获取所有病种</p>
     * @return
     * @author fanp
     * @date 2015年10月13日
     */
    public List<DiseaseTypeVO> getAllDiseaseType(){
//        return dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).filter("parent", "0").order("-weight").asList();
        return dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).filter("enable",true).order("-weight").asList();
    }
    
    
    public List<ServiceItem> getServiceItemByParent(String parentId) {
    	return dsForRW.createQuery("b_service_item", ServiceItem.class).filter("parent", parentId).asList();
    }
    
    public List<ServiceItem> getServiceItemByIds(String... ids) {
    	return dsForRW.createQuery("b_service_item", ServiceItem.class).filter("_id in", Arrays.asList(ids)).asList();
    }
    
    /**
     * </p>获取科室</p>
     * @param ids
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    public List<DeptVO> getDeptByIds(List<String> ids){
        if(ids==null || ids.size()==0){
            return null;
        }
        return dsForRW.createQuery("b_hospitaldept",DeptVO.class).filter("_id in", ids).filter("enableStatus", 1).asList();
    }
    
    /**
     * </p>获取科室</p>
     * 
     * @param parentId
     * @return
     * @author 李淼淼
     * @date 2015年7月6日
     */
    @Override
    public List<DeptVO> findDeptByParent(String parentId){
        
        Query<DeptVO> query = dsForRW.createQuery("b_hospitaldept",DeptVO.class).filter("enableStatus", 1);
        
        if(StringUtil.isNotBlank(parentId)){
            query.field("parentId").equal(parentId.trim());
        }
        query.order("-weight");
        return query.asList();
    }
    
    /**
     * </p>根据prantId查找检查建议</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日 
     */
    public List<CheckSuggest> getCheckSuggestByParentId(String parentId) {
    	 DBObject query = new BasicDBObject();
         query.put("parent", parentId);
         
    	 return dsForRW.createQuery(CheckSuggest.class, query).order("weight").asList();
    }
    
    /**
     * </p>根据checkupId查找单项指标</p>
     * @param checkupId
     */
    @Override
    public List<CheckSuggestItem> getCheckSuggestItemListByCheckupId(String checkupId) {
    	 DBObject query = new BasicDBObject();
         query.put("checkupId", checkupId);
         
    	 Query<CheckSuggestItem> q = dsForRW.createQuery(CheckSuggestItem.class, query);
    	 
    	 return q.asList();
    }

	/**
	 * </p>根据checkupId查找单项指标</p>
	 * @param Ids
	 */
	@Override
	public List<CheckSuggestItem> getCheckSuggestItemListByIds(List<String> Ids) {
		BasicDBList list = new BasicDBList();
		if (Ids != null && Ids.size() > 0) {
            for (String id : Ids) {
                list.add(new ObjectId(id));
            }
		    Query<CheckSuggestItem> q = dsForRW.createQuery(CheckSuggestItem.class).field("_id").in(list);
            return q.asList();
        } else {
		    return null;
        }
	}

	@Override
	public CheckSuggestItem getCheckSuggestItemById(String id) {
		Query<CheckSuggestItem> q = dsForRW.createQuery(CheckSuggestItem.class).field("_id").equal(new ObjectId(id));
		return q.get();
	}

	public List<CheckSuggest> getCheckSuggestByIds(String[] ids) {
    	return dsForRW.createQuery(CheckSuggest.class).filter("_id in", ids).order("weight").asList();
    }
    
    public List<CheckSuggest> searchCheckSuggest(String keyword) {
    	DBObject query = new BasicDBObject();
    	Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
    	query.put("name", pattern);
		query.put("isLeaf",true);

    	return dsForRW.createQuery(CheckSuggest.class, query).limit(50).order("weight").asList();
    }
    
    public MsgTemplate saveMsgTemplate(MsgTemplate param) {
    	dsForRW.save(param);
    	return param;
    }
    
    public MsgTemplate queryMsgTemplateById(final String id) {
    	DBObject query =  new BasicDBObject();
    	query.put("_id", id);
    	final List<MsgTemplate> msgTemplateList =  dsForRW.createQuery(MsgTemplate.class, query).asList();
    	if (msgTemplateList != null && msgTemplateList.size() > 0) {
    		return msgTemplateList.get(0);
    	}
    	return null;
    }
    
    /*
     * 查询文案, 分页查询结果集
     */
    public Query<MsgTemplate> queryMsgTemplate(MsgTemplateParam param) {
    	int maxPageSize = param.pageSize;
    	int offset = param.pageIndex * maxPageSize;

    	DBObject where = new BasicDBObject();
    	
    	BasicDBList whereList = new BasicDBList();
		
    	boolean hasParam = false; // 是否有参数
    	if (StringUtils.isNotBlank(param.getId())) {
    		// 精确匹配
    		hasParam = true;
    		//DBObject dbobject = new BasicDBObject("$eq", new ObjectId(param.getId()));
    		//whereList.add(new BasicDBObject("_id", dbobject));
    		whereList.add(new BasicDBObject("_id", param.getId()));
    	}
    	
    	if (StringUtils.isNotBlank(param.getCategory())) {
    		// 精确匹配
    		hasParam = true;
    		DBObject dbobject = new BasicDBObject("$eq", param.getCategory());
    		whereList.add(new BasicDBObject("category", dbobject));
    	}
    	
    	if (StringUtils.isNotBlank(param.getTitle())) {
    		// 模糊匹配
    		hasParam = true;
			Pattern pattern = Pattern.compile("^.*" + param.getTitle() + ".*$", Pattern.CASE_INSENSITIVE);
			whereList.add(new BasicDBObject("title", pattern));
    	}
    	
    	if (StringUtils.isNotBlank(param.getContent())) {
    		// 模糊匹配
    		hasParam = true;
			Pattern pattern = Pattern.compile("^.*" + param.getContent() + ".*$", Pattern.CASE_INSENSITIVE);
			whereList.add(new BasicDBObject("content", pattern));
    	}
    	
    	// 满足上述四个条件之一就可以查出来
    	if (hasParam) {
    		where.put(QueryOperators.OR, whereList);
    	}

    	Query<MsgTemplate> query = dsForRW.createQuery(MsgTemplate.class, where)
    			.order("_id") // 排序(升序)，从小到大
    			.offset(offset) // 与skip同理，用于分页
    			.limit(maxPageSize); // 最大

    	return query;
    }
    
    public int deleteMsgTemplateById(String id) {
    	//WriteResult result = dsForRW.delete(MsgTemplate.class, new ObjectId(id));
    	WriteResult result = dsForRW.delete(MsgTemplate.class, id);
    	if (result == null) {
    		return 0;
    	}
    	return result.getN();
    }
    
    public int deleteMsgTemplate(String[] ids) {
    	// 因_id是由mongodb的ObjectId()生成的，所以下面要转成List<ObjectId>
    	List<ObjectId> listIds = new ArrayList<ObjectId>();
    	for (String id : ids) {
    		listIds.add(new ObjectId(id));
    	}

    	// 第二种删除（可行）
    	WriteResult result = dsForRW.delete(MsgTemplate.class, listIds);
    	if (result == null) {
    		return 0;
    	}
    	return result.getN();
    }
    
    public EvaluationItem getEvaluationItem(String id) {
    	return dsForRW.createQuery(EvaluationItem.class).filter("id", new ObjectId(id)).get();
    }
    
    public List<EvaluationItem> getEvaluationItems(Integer level, Integer... packTypes) {
    	List<Integer> packTypeList = new ArrayList<Integer>();
    	for (Integer packType : packTypes) {
    		packTypeList.add(packType);
    	}
    	Query<EvaluationItem> q = dsForRW.createQuery(EvaluationItem.class);
    	if (level != null) {
    		q.field("level").equal(level);
    	}
    	if (packTypeList.size() > 0) {
    		q.filter("packType in", packTypeList);
    	}
    	return q.asList();
    }
    
    
    public List<HospitalPO> getHospitals(Integer code, boolean is3A) {
    	if ((code == null || code == 0) && !is3A) {
    		return new ArrayList<HospitalPO>();
    	}
//    	Query<HospitalPO> q = dsForRW.createQuery("b_hospital",HospitalPO.class).retrievedFields(true, "_id", "name");
//    	if (code != null && code != 0) {
//    		if (code % 10000 == 0) {
//    			q.field("province").equal(code);
//    		} else if (code % 100 == 0) {
//    			q.field("city").equal(code);
//    		} else {
//    			q.field("country").equal(code);
//    		}
//    	}
//    	if (is3A) {//是否三甲医院
//    		q.field("level").equal(HospitalLevelEnum.Three3.getAlias());
//    	}
    	return getHospitalsCodeNull(code,is3A);
    }
    
    @Override
	public List<HospitalVO> getHospitalInfos(Integer code, boolean is3A) {
    	Query<HospitalVO> q = dsForRW.createQuery("b_hospital", HospitalVO.class);
    	if (code != null && code != 0) {
    		if (code % 10000 == 0) {
    			q.field("province").equal(code);
    		} else if (code % 100 == 0) {
    			q.field("city").equal(code);
    		} else {
    			q.field("country").equal(code);
    		}
    	}
    	if (is3A) {//是否三甲医院
    		q.field("level").equal(HospitalLevelEnum.Three3.getAlias());
    	}
    	return q.asList();
	}
    
    public List<HospitalPO> getHospitalsCodeNull(Integer code, boolean is3A) {
    	Query<HospitalPO> q = dsForRW.createQuery("b_hospital",HospitalPO.class).retrievedFields(true, "_id", "name");
    	if (code != null && code != 0) {
    		if (code % 10000 == 0) {
    			q.field("province").equal(code);
    		} else if (code % 100 == 0) {
    			q.field("city").equal(code);
    		} else {
    			q.field("country").equal(code);
    		}
    	}
    	if (is3A) {//是否三甲医院
    		q.field("level").equal(HospitalLevelEnum.Three3.getAlias());
    	}
    	return q.asList();
    }

	@Override
	public HospitalPO getHospitalDetail(String hospitalId) {
			if(StringUtil.isEmpty(hospitalId)){
				return null;
			}
		 return dsForRW.createQuery(HospitalPO.class).field("_id").equal(hospitalId).get();
	}
    @Override
    public HospitalPO getHospitalByName(String name) {
        if(StringUtil.isEmpty(name)){
            return null;
        }
        return dsForRW.createQuery(HospitalPO.class).field("name").equal(name).retrievedFields(true,"_id","name","province","city","country","level","status").get();
    }
	@Override
	public HospitalPO checkHospital(AddHospitalParam param) {
		HospitalPO hosptial = dsForRW.createQuery(HospitalPO.class).field("name").equal(param.getName()).field("province").equal(param.getProvinceId()).field("country").
				equal(param.getCountryId()).field("city").equal(param.getCityId()).get();
		return hosptial;
	}

	@Override
	public List<HospitalVO> getHospitals(List<String> hospitalIds) {
		if(hospitalIds != null && hospitalIds.size() > 0){
			return dsForRW.createQuery("b_hospital",HospitalVO.class).field("_id").in(hospitalIds).asList();
		}
		return null;
	}
	
	@Override
	public List<HospitalLevelPo> getHospitalLevels() {
		return dsForRW.createQuery(HospitalLevelPo.class).asList();
	}

	@Override
	public List<ExpectAppointment> getExpectAppointments() {
		return dsForRW.createQuery(ExpectAppointment.class).order("showOrder").asList();
	}
	
	@Override
	public List<DepartmentVO> getAllDepartments() {
		return dsForRW.createQuery("b_hospitaldept", DepartmentVO.class).filter("enableStatus", 1).asList();
	}

	public DepartmentVO getDepartmentById(String id) {
		return dsForRW.createQuery("b_hospitaldept", DepartmentVO.class).filter("enableStatus", 1).filter("_id", id).get();
	}
	
	public List<Integer> getAllAreaChildByParentId(Integer areaCode) {
		List<AreaVO> area1 = getAreas(areaCode);
		List<Integer> result = Lists.newArrayList();
		if (area1 != null && area1.size() > 0) {
			List<Integer> areaCodes = Lists.newArrayList();
			for(AreaVO areaVO : area1) {
				areaCodes.add(areaVO.getCode());
			}
			List<AreaVO> area2 = getAreas(areaCodes);
			if (area2 != null && area1.size() > 0) {
				area1.addAll(area1);
			}
			for(AreaVO areaVO : area1) {
				result.add(areaVO.getCode());
			}
		}
		return result;
	}
	
	@Override
	public List<AreaVO> getAllAreas() {
		return dsForRW.createQuery("b_area",AreaVO.class).retrievedFields(true, "code","name").order("code").asList();
	}

	@Override
	public List<String> getAllDeptChildByParentId(String deptId) {
		//1、获取全部的科室
		List<DepartmentVO> allDepartments = getAllDepartments();
		//2、递归获取全部的子节点
		List<String> result = Lists.newArrayList();
		if (allDepartments != null && allDepartments.size() > 0) {
			Set<DepartmentVO> allDeptChildSet = Sets.newHashSet();
			getAllDepts(allDepartments, allDeptChildSet, deptId);
			if (allDeptChildSet != null && allDeptChildSet.size() > 0) {
				for(DepartmentVO departmentVO : allDeptChildSet) {
					result.add(departmentVO.getId());
				}
			}
		}
		return result;
	}
	
	void getAllDepts(List<DepartmentVO> allDepartments, Set<DepartmentVO> result, String deptId) {
		for(DepartmentVO departmentVO : allDepartments) {
			if (StringUtils.equals(departmentVO.getParentId(), deptId)) {
				result.add(departmentVO);
				getAllDepts(allDepartments, result, departmentVO.getId());
			}
		}
		
	}
	
	@Override
	public String getExpectAppointmentsByIds(String expectAppointmentIds) {
		List<ObjectId> oids = new ArrayList<>();
		for(String strId : expectAppointmentIds.split(",")){
			oids.add(new ObjectId(strId));
		}
		String str = dsForRW.createQuery(ExpectAppointment.class)
				.field("_id").in(oids)
				.order("showOrder")
				.asList()
				.stream()
				.map(o -> o.getValue())
				.reduce("", (a , b) -> a+" #"+b);
		return StringUtils.isNotBlank(str) ? str.substring(1) : str;
	}

	@Override
	public List<HospitalVO> getAllHospitals() {
		return dsForRW.createQuery("b_hospital",HospitalVO.class).field("status").equal(1).asList();
	}

	@Override
	public List<TitleVO> getAllTitles() {
		return dsForRW.createQuery("b_doctortitle",TitleVO.class).asList();
	}

	@Override
	public List<HospitalVO> getHospitalsByLocation(Integer code, boolean is3A, Double lng, Double lat) {
		DBObject queryObj = new BasicDBObject();
		if (code != null && code != 0) {
			if (code % 10000 == 0) {
				queryObj.put("province", code);
			} else if (code % 100 == 0) {
				queryObj.put("city", code);
			} else {
				queryObj.put("country", code);
			}
		}
		if (is3A) {//是否三甲医院
			queryObj.put("level", HospitalLevelEnum.Three3.getAlias());
		}

		List<HospitalVO> voList = new ArrayList<>();
		CommandResult c = geoNear("b_hospital", queryObj, lng, lat, 100, Long.parseLong(BaseConstants.GEO_NEAR_MAXDISTANCE()));//默认最多查询100条记录，搜索半径为20公里
		logger.info("mongodb geoNear命令执行的结果："+c);
		if(c.ok()){//查询成功
			//获取数据
			Collection<DBObject> resultList=(Collection<DBObject>) c.get("results");
			for(DBObject o:resultList){
				DBObject hospital=(DBObject) o.get("obj");
				HospitalVO vo = new HospitalVO();
				vo.setId(hospital.get("_id")+"");
				vo.setName(hospital.get("name")+"");
				vo.setLng(hospital.get("lng")+"");
				vo.setLat(hospital.get("lat")+"");
				vo.setDis(o.get("dis")+"");
				
				voList.add(vo);
			}
		}

		return voList;
	}
	
	
	/**
    * 使用geoNear查询附近地理空间的数据
    * @param collectionName 集合名
    * @param queryObj 其他查询条件
    * @param centerLon 中心点的经度
    * @param centerLat 中心
    * @param limit 查询限制条数大小
    * @param maxDistance 最大距离（千米）
    * @return
    */
   private CommandResult geoNear(String collectionName, DBObject queryObj, double centerLon,
		   double centerLat, int limit, Long maxDistance) {
	   try {
		   DB db = dsForRW.getDB();
		   
		   BasicDBObject myCmd = new BasicDBObject();
		   myCmd.append("geoNear", collectionName);//集合名
		   
		   double[] loc = {centerLon, centerLat};
		   myCmd.append("near", loc); 
		   /**
		    * geoNear默认结果是根据距离排序有距离的记录，但是距离是经纬度的差值，
		    * MongoDB 1.8以后提供了Spherical Model，用distanceMultiplier指定地球半径来得到实际的公里或者米的距离，
		    * 记得加上spherical:true
		    */
		   myCmd.append("spherical", true);
		   myCmd.append("distanceMultiplier", 6371); //地球的半径，单位千米
		   myCmd.append("maxDistance", (double)maxDistance / 6371); //指定maxDistance千米范围内
		   if(queryObj != null){
			   myCmd.append("query", queryObj);//非地理位置域的查询条件
		   }
		   myCmd.append("limit", limit);
		   logger.info("mongodb geoNear命令为："+myCmd.toString());
		   CommandResult myResults = db.command(myCmd); 
		   return myResults;
	   } catch (Exception e) {
		   logger.info(e.getMessage(), e);
	   }

	   return null;
   }

	@Override
	public List<GeoDeptPO> getAllGeoDepts() {
		Query<GeoDeptPO> query = dsForRW.createQuery("b_hospital_geo_dept", GeoDeptPO.class).retrievedFields(true, "deptId","_id","weight").filter("enableStatus", 1);
		query.order("-weight");
		return query.asList();
	}

	@Override
	public <T> Query<T> getIncInfos(Map<String,Object> map, String collection, Class<T> t) {
		Query<T> query = dsForRW.createQuery(collection, t);
		Iterator<Entry<String,Object>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry = iterator.next();
			query.filter(entry.getKey(), entry.getValue());
		}
		return  query;
	}
	
	public   DBCursor  getIncDoc(DoctorParam param) {
		DBObject query = new BasicDBObject("status",1);
		query.put("userType", 3);
		if(param.getModifyTime() != null){
			query.put("modifyTime", new BasicDBObject("$gte", param.getModifyTime()));
		}
		return dsForRW.getDB().getCollection("user").find(query);
	}
	
    public CheckSuggest getCheckSuggestById(String id) {
    	return dsForRW.createQuery(CheckSuggest.class).field("_id").equal(id).get();
    }

	public List<CheckSuggest> getAllLeaf() {
		return dsForRW.createQuery(CheckSuggest.class).field("isLeaf").equal(true).asList();
	}

	@Override
	public Pagination<GroupUnionVo> getAllGroupUnionPage(String name,Integer pageIndex,Integer pageSize) {
		Query query=dsForRW.createQuery("c_group_union",GroupUnionVo.class);
		if(StringUtil.isNotEmpty(name)) {
			Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
			query.filter("name", pattern);
		}
		query.field("statusId").equal(2);
		query.retrievedFields(true,"_id", "name");
		query.offset(pageIndex*pageSize).limit(pageSize);
		List<GroupUnionVo> list=query.asList();
		Pagination<GroupUnionVo> page = new Pagination<>(list, query.countAll(), pageIndex, pageSize);
		return page;
	}

	@Override
	public List<GroupUnionVo> getAllGroupUnion() {
		Query query=dsForRW.createQuery("c_group_union",GroupUnionVo.class);
		query.field("statusId").equal(2);
		query.retrievedFields(true,"_id", "name");
		return query.asList();
	}

	public List<GroupVO> getAllGroup() {
		Query<GroupVO> query = dsForRW.createQuery("b_doctortitle",GroupVO.class)
				.filter("type", "hospital")
				.filter("skip", "N")
				.filter("active", "active")
				.filter("applyStatus", "P");
		return query.asList();
	}

    @Override
    public List<HospitalDeptVO> getHospitalDeptList() {
       return dsForRW.createQuery("b_hospitaldept",HospitalDeptVO.class).asList();
    }

    @Override
    public DBCursor getAllDoctorInfo(DoctorParam param) {
        DBObject query = new BasicDBObject();
        query.put("userType", 3);
        if (param.getModifyTime() != null) {
            query.put("modifyTime", new BasicDBObject("$gte", param.getModifyTime()));
        }
        return dsForRW.getDB().getCollection("user").find(query);
    }

    @Override
    public void addCollegeData(List<DBObject> collegesPOS) {
        dsForRW.getDB().getCollection("b_colleges").insert(collegesPOS);
    }

    @Override
    public String addCollegeData(CollegeParam collegeParam) {
        CollegesPO copy = BeanUtil.copy(collegeParam, CollegesPO.class);
        long timeMillis = System.currentTimeMillis();
        copy.setCreateTime(timeMillis);
        copy.setModifyTime(timeMillis);
        return dsForRW.insert(copy).getId().toString();
    }

    @Override
    public void updateCollegeData(CollegeParam param) {
        Query<CollegesPO> q = dsForRW.createQuery(CollegesPO.class).field(Mapper.ID_KEY).equal(new ObjectId(param.getId()));
        UpdateOperations<CollegesPO> ops = dsForRW.createUpdateOperations(CollegesPO.class);
        if (StringUtils.isNotBlank(param.getCollegeName())) {
            ops.set("collegeName", param.getCollegeName());
        }
        if (StringUtils.isNotBlank(param.getCollegeProvince())) {
            ops.set("collegeProvince", param.getCollegeProvince());
        }
        if (StringUtils.isNotBlank(param.getCollegeArea())) {
            ops.set("collegeArea", param.getCollegeArea());
        }
        if (StringUtils.isNotBlank(param.getCollegeLevel())) {
            ops.set("collegeLevel", param.getCollegeLevel());
        }
        if (StringUtils.isNotBlank(param.getCollegeCode())) {
            ops.set("collegeCode", param.getCollegeCode());
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public PageVO getCollegeData(CollegeParam collegeParam) {
        if (Objects.isNull(collegeParam.getPageSize())) {
            collegeParam.setPageSize(15);
        }
        if (Objects.isNull(collegeParam.getPageIndex())) {
            collegeParam.setPageIndex(0);
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(collegeParam.getPageIndex());
        pageVO.setPageSize(collegeParam.getPageSize());
        Query<CollegesPO> query = dsForRW.createQuery(CollegesPO.class);
        if (StringUtils.isNotBlank(collegeParam.getCollegeName())) {
            query.field("collegeName").contains(collegeParam.getCollegeName());
        }
        pageVO.setTotal(query.countAll());
        query.offset(collegeParam.getPageIndex() * collegeParam.getPageSize());
        query.limit(collegeParam.getPageSize());
        pageVO.setPageData(query.asList());
        return pageVO;
    }

    @Override
    public List<CollegesPO> getCollege(String collegeArea, String collegeName) {
        Query<CollegesPO> query = dsForRW.createQuery(CollegesPO.class).retrievedFields(true, "collegeName", "collegeArea");
        if (StringUtils.isNotBlank(collegeArea)) {
            query.field("collegeArea").equal(collegeArea);
        }
        if (StringUtils.isNotBlank(collegeName)) {
            query.field("collegeName").contains(collegeName);
        }
        return query.asList();
    }

    @Override
    public List<CollegeDeptPO> getCollegeDept() {
        Query<CollegeDeptPO> query = dsForRW.createQuery(CollegeDeptPO.class);
        return query.asList();
    }

    @Override
    public CollegesPO getCollegeById(String collegeId) {
        Query<CollegesPO> query = dsForRW.createQuery(CollegesPO.class).retrievedFields(true, "collegeName", "collegeArea");
        query.filter(Mapper.ID_KEY, new ObjectId(collegeId));
        return query.get();
    }

    /**
	 * 获取医院行政区域信息
	 */
    private List<Map<String, String>> findArea(Collection<Integer> coll){
        DBObject query = new BasicDBObject();
        query.put("code", new BasicDBObject("$in", coll));
        DBCursor cursor = dsForRW.getDB().getCollection("b_area").find(query);
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        DBObject obj = null;
        while(cursor.hasNext()){
            obj = cursor.next();
            map = new HashMap<String, String>();
            map.put("id", MongodbUtil.getString(obj, "_id"));
            map.put("code", MongodbUtil.getString(obj, "code"));
            map.put("name", MongodbUtil.getString(obj, "name"));
            map.put("pcode", MongodbUtil.getString(obj, "pcode"));
            data.add(map);
        }
        return data;
    }
}
