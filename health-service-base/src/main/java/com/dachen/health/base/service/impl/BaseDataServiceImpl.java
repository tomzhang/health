package com.dachen.health.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.cache.AreaDbCache;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.dao.IdxRepository.idxType;
import com.dachen.health.base.entity.param.*;
import com.dachen.health.base.entity.po.*;
import com.dachen.health.base.entity.po.HospitalPO.Loc;
import com.dachen.health.base.entity.vo.*;
import com.dachen.health.base.helper.TemplateHelper;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.EvaluationItemEnum;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * ProjectName： health-service<br>
 * ClassName： AreaServiceImpl<br>
 * Description： 地区分类service实现类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
@Service
public class BaseDataServiceImpl implements IBaseDataService {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataServiceImpl.class);

    @Autowired
    private IBaseDataDao baseDataDao;
    
    @Autowired
    protected IdxRepository idxRepository;

    /*
     * 新增一个医院名称
     */
    public HospitalPO addHospital(AddHospitalParam param) {
        if (StringUtils.isBlank(param.getName())) {
            throw new ServiceException("更新医院名称为空");
        }
        if (Objects.isNull(param.getProvinceId())
                && Objects.isNull(param.getCityId())
                && Objects.isNull(param.getCountryId())) {
            throw new ServiceException("医院所在地信息为空");
        }
        if (StringUtil.isBlank(param.getType())) {
            throw new ServiceException("医院类型不能为空");
        }
        if (StringUtil.isBlank(param.getNature())) {
            throw new ServiceException("医院性质不能为空");
        }
        if (Objects.equals(param.getNature(), "公立医院") && Objects.equals(param.getType(), "诊所")) {
            throw new ServiceException("医院类型错误");
        }
        if (Objects.equals(param.getType(), "诊所") && Objects.nonNull(param.getLevel())) {
            throw new ServiceException("医院类型为“诊所”的不该有医院等级");
        }
        if (Objects.equals(param.getType(), "其他") && Objects.nonNull(param.getProperty())) {
            throw new ServiceException("医院类型为“其他”的不该有医院属性");
        }
        //检查同一个区域的医院有没有重复添加
        HospitalPO hospital = baseDataDao.checkHospital(param);
        if (null != hospital) {
            throw new ServiceException(param.getName() + "已经存在！！！ 不可以重复添加。");
        }
        HospitalPO hsp = new HospitalPO();
        String _id = String.valueOf(System.currentTimeMillis());
        System.out.println("addHospital():_id:" + _id);
        hsp.setId(_id);
        hsp.setProvince(param.getProvinceId());
        hsp.setCity(param.getCityId());
        hsp.setCountry(param.getCountryId());
        hsp.setName(param.getName());
        hsp.setType(param.getType());
        hsp.setNature(param.getNature());
        if (StringUtils.isNotBlank(param.getLevel())) {
            hsp.setLevel(param.getLevel());
        }
        if (StringUtils.isNotBlank(param.getProperty())) {
            hsp.setProperty(param.getProperty());
        }
        if (StringUtils.isNotBlank(param.getAddress())) {
            hsp.setAddress(param.getAddress());
        }
        Loc loc = new Loc();
        if (!Objects.isNull(param.getLat())) {
            hsp.setLat(param.getLat());
            loc.setLat(param.getLat());
        }
        if (!Objects.isNull(param.getLng())) {
            hsp.setLng(param.getLng());
            loc.setLng(param.getLng());
        }
        hsp.setLoc(loc);
        // 正常
        hsp.setStatus(1);
        hsp.setLastUpdatorTime(System.currentTimeMillis());
        hsp.setCreateTime(System.currentTimeMillis());
        return baseDataDao.addHospital(hsp);
    }
    
    /**
     * 更新医院名称
     */
    public HospitalPO updateHospital(AddHospitalParam param) {
        if(StringUtils.isBlank(param.getId())){
            throw new ServiceException("更新医院id为空");
        }
        if (StringUtil.isBlank(param.getType())) {
            throw new ServiceException("医院类型不能为空");
        }
        if (StringUtil.isBlank(param.getNature())) {
            throw new ServiceException("医院性质不能为空");
        }
        if (Objects.equals(param.getNature(), "公立医院") && Objects.equals(param.getType(), "诊所")) {
            throw new ServiceException("医院类型错误");
        }
        if (Objects.equals(param.getType(), "诊所") && Objects.nonNull(param.getLevel())) {
            throw new ServiceException("医院类型为“诊所”的不该有医院等级");
        }
        if (Objects.equals(param.getType(), "其他") && Objects.nonNull(param.getProperty())) {
            throw new ServiceException("医院类型为“其他”的不该有医院属性");
        }
        List<String> idlist = new ArrayList<String>();
        idlist.add(param.getId());
        List<HospitalVO> hlist = baseDataDao.getHospitals(idlist);
        if(CollectionUtils.isEmpty(hlist)){
            throw new ServiceException("更新医院不存在");
        }
        String paramStr = JSON.toJSONString(param);
        AddHospitalParam cloneParam = JSON.parseObject(paramStr, AddHospitalParam.class);
        cloneParam.setId(null);
        //检查同一个区域的医院有没有重复添加
        HospitalPO hospital = baseDataDao.checkHospital(cloneParam);
        if(null!=hospital 
                && !hospital.getId().equals(param.getId())){
            throw new ServiceException("更新失败与其他医院信息冲突");
        }
        HospitalPO hsp = new HospitalPO();
        hsp.setId(param.getId());
        hsp.setNature(param.getNature());
        hsp.setType(param.getType());
        if(!Objects.isNull(param.getProvinceId())){
            hsp.setProvince(param.getProvinceId());
        }
        if(!Objects.isNull(param.getCityId())){
            hsp.setCity(param.getCityId());
        }
        if(!Objects.isNull(param.getCountryId())){
            hsp.setCountry(param.getCountryId());
        }
        if(StringUtils.isNotBlank(param.getName())){
            hsp.setName(param.getName());
        }
        if(StringUtils.isNotBlank(param.getAddress())){
            hsp.setAddress(param.getAddress());
        }
        if(StringUtils.isNotBlank(param.getLevel())){
            hsp.setLevel(param.getLevel());
        }
        if (StringUtils.isNotBlank(param.getProperty())) {
            hsp.setProperty(param.getProperty());
        }
        if(!Objects.isNull(param.getStatus())){
            hsp.setStatus(param.getStatus());
        }
        Loc loc = new Loc();
        if(!Objects.isNull(param.getLat())){
            hsp.setLat(param.getLat());
            loc.setLat(param.getLat());
        }
        if(!Objects.isNull(param.getLng())){
            hsp.setLng(param.getLng());
            loc.setLng(param.getLng());
        }
        hsp.setLoc(loc);
        hsp.setLastUpdatorTime(System.currentTimeMillis());
        
        return baseDataDao.updateHospital(hsp);
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
        return baseDataDao.getAreas(pcode);
    }
    
    /**
     * 直辖市：北京、天津、上海、重庆
     */
    public List<Integer> directCityCodes = Arrays.asList(new Integer[] {110000,120000,310000,500000});
    public List<Integer> hotCityCodes = Arrays.asList(new Integer[]{110000,310000,440100,440300});
	public List<AreaVO> getAllAreas() {
		List<AreaVO> areas = getAreas(0);
		for (AreaVO area : areas) {
			//直辖市去除第2级“市辖区”、“县”，返回第3级
			if (directCityCodes.contains(area.getCode())) {
				List<AreaVO> areas3 = new ArrayList<AreaVO>();
				List<AreaVO> areas2 = getAreas(area.getCode());
				for (AreaVO area2 : areas2) {
					areas3.addAll(getAreas(area2.getCode()));
				}
				area.setChildren(areas3);
				continue;
			}
			area.setChildren(getAreas(area.getCode()));
		}
        return areas;
    }

    public List<AreaVO> getAllAreasExDirect() {
        List<AreaVO> areas = getAreas(0);
        for (AreaVO area : areas) {
            //直辖市去除第2级“市辖区”、“县”，返回第3级
            if (!directCityCodes.contains(area.getCode())) {
                area.setChildren(getAreas(area.getCode()));
            }else {
                area.setChildren(new ArrayList<>());
            }
        }
        return areas;
    }
	public List<AreaVO> getAllAreasInfo() {
	    //先从redis中获取
        List<AreaVO> cacheAreaList = AreaDbCache.getAllAreaInfoList();
        if (!CollectionUtils.isEmpty(cacheAreaList)) {
            return cacheAreaList;
        } else {
            List<AreaVO> areas = getAreas(0);
            for (AreaVO area : areas) {
                //直辖市去除第2级“市辖区”、“县”，返回第3级
                if (directCityCodes.contains(area.getCode())) {
                    List<AreaVO> areas3 = new ArrayList<AreaVO>();
                    List<AreaVO> areas2 = getAreas(area.getCode());
                    for (AreaVO area2 : areas2) {
                        areas3.addAll(getAreas(area2.getCode()));
                    }
                    area.setChildren(areas3);
                    continue;
                }

                List<AreaVO> areas1 = getAreas(area.getCode());
                area.setChildren(areas1);

                for (AreaVO area1 : areas1) {
                    area1.setChildren(getAreas(area1.getCode()));
                }

            }
            AreaDbCache.saveAreaList(areas);
            cacheAreaList = areas;
		}
        return cacheAreaList;
    }
	
	public List<AreaVO> getHotCityList(){
		List<AreaVO> areas = new ArrayList<AreaVO>();
		for(Integer code : hotCityCodes){
			AreaVO vo = baseDataDao.getAreaByCode(code);
			if(vo != null){
//				if (directCityCodes.contains(vo.getCode())) {
//					List<AreaVO> areas3 = new ArrayList<AreaVO>();
//					List<AreaVO> areas2 = getAreas(vo.getCode());
//					for (AreaVO area2 : areas2) {
//						areas3.addAll(getAreas(area2.getCode()));
//					}
//					vo.setChildren(areas3);
//					vo.setIsHot(1);
//					areas.add(vo);
//					continue;
//				}
//				vo.setChildren(getAreas(vo.getCode()));
				vo.setIsHot(1);
				areas.add(vo);
			}
		}
		return areas;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.base.service.IBaseDataService#getAreaNameByCode(java.lang.Integer)
	 */
	@Override
    public String getAreaNameByCode(Integer code){
    	//参数校验
    	if(code == null || code == 0 ){
    		throw new ServiceException("区域code 不能为空");
    	}
    	AreaVO area = baseDataDao.getAreaByCode(code);
    	if(area == null){
    		throw new ServiceException("根据区域code 找不到对应的区域信息");
    	}
    	return area.getName();
    	
    }
    
    /**
     * </p>获取地区下的医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<HospitalVO> getHospitals(int country,String name){
        return baseDataDao.getHospitals(country,name);
    }

    public List<HospitalVO> getOkStatusHospitals(int country,String name){
        return baseDataDao.getOkStatusHospitals(country,name);
    }
    
    /**
     * </p>增量获取地区下的医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<HospitalVO> getHospitals(Long timeline){
        return baseDataDao.getHospitals(timeline);
    }
    
    /**
     * </p>增量获取地区下的医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public PageVO getHospitals(Long timeline, String hospital, String hospitalId, PageVO page){
        return baseDataDao.getHospitals(timeline, hospital, hospitalId, page);
    }
    
    /**
     * </p>获取医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<HospitalVO> getHospitals(String name){
        return baseDataDao.getHospitals(name);
    }
    
    public HospitalVO getHospital(String hospitalId){
        return baseDataDao.getHospital(hospitalId);
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
        if(StringUtil.isBlank(deptId)){
            deptId = "A";
        }
        return baseDataDao.getDepts(deptId,name);
    }
    
	public List<DeptVO> getAllDepts() {
		List<DeptVO> depts = getDepts("A", null);
		for (DeptVO dept : depts) {
			dept.setChildren(getDepts(dept.getId(), null));
		}
        return depts;
    }
	
	/* (non-Javadoc)
	 * @see com.dachen.health.base.service.IBaseDataService#getDeptAndParentDeptById(java.lang.String)
	 */
	@Override
	public DeptVO getDeptAndParentDeptById(String deptId){
		//参数校验
		if(StringUtils.isEmpty(deptId)){
			throw new ServiceException("科室id不能为空");
		}
		DeptVO deptVO = baseDataDao.getDeptById(deptId);
		if(deptVO == null){
			return null;
		}
		if(!"A".equals(deptVO.getParentId())){
			DeptVO parentDeptVO = baseDataDao.getDeptById(deptVO.getParentId());
			deptVO.setParentDept(parentDeptVO);
		}
		
		return deptVO;
	}
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<TitleVO> getTitles(){
        return baseDataDao.getTitles();
    }
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public TitleVO getTitle(String title){
        return baseDataDao.getTitle(title);
    }
    
    /**
     * </p>获取病种下所有子病种</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<String> getDiseaseTypeChildren(String id){
        return baseDataDao.getDiseaseTypeChildren(id);
    }
    
    /**
     * </p>获取病种</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<DiseaseTypeVO> getDiseaseType(List<String> ids){
        return baseDataDao.getDiseaseType(ids);
    }
    
    /**
     * </p>获取病种树</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<ExtTreeNode> getDiseaseTypeTree(){
        List<DiseaseTypeVO> list = baseDataDao.getAllDiseaseType();
        
        List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        for(DiseaseTypeVO vo : list){
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());
            
            nodeList.add(node);
        }
        
        return ExtTreeUtil.buildTree(nodeList);
    }
    
    /**
     * </p>根据病种获取父节点构建树结构</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<ExtTreeNode> getDiseaseTypeTree(List<String> diseaseIds){
        //根据id规则获取父节点，id规则为两位一级，根节点为-1
        Set<String> ids = new HashSet<String>();
        
        for(String id:diseaseIds){
            if(id.length()!=0 && id.length()%2 == 0){
                for(int i = 0 ,j=id.length()/2;i<j;i++){
                    ids.add(id.substring(0,(i+1)*2));
                }
            }
        }
        
        List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        
        if(ids.size()==0){
            return nodeList;
        }
        
        //获取所有病种
        List<DiseaseTypeVO> list = baseDataDao.getDiseaseType(new ArrayList<String>(ids));
        
        for(DiseaseTypeVO vo : list){
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());
            
            nodeList.add(node);
        }
        
        return ExtTreeUtil.buildTree(nodeList);
    }
    
    /**
     * </p>根据prantId查找病种</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    public List<DiseaseTypeVO> getDiseaseByParent(String parentId){
        if(StringUtil.isBlank(parentId)){
            parentId = "0";
        }
        return baseDataDao.getDiseaseByParent(parentId);
    }
    
    /**
     * </p>根据prantId查找病种(返回到“科室－病种”类型的一级病种)</p>
     * @param parentId
     * @return
     * @author dwju
     * @date 2015年11月18日
     */
    public List<DiseaseTypeVO> getOneLevelDiseaseByParent(String parentId){
        if(StringUtil.isBlank(parentId)){
            parentId = "0";
        }
        return baseDataDao.getOneLevelDiseaseByParent(parentId);
    }
    
    /**
     * </p>根据prantId查找服务项</p>
     * @param parentId
     * @return
     * @author xieping
     * @date 2016年4月27日
     */
    public List<ServiceItem> getServiceItemByParent(String parentId){
        if(StringUtil.isBlank(parentId)){
            parentId = "0";
        }
        return baseDataDao.getServiceItemByParent(parentId);
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
    public List<DeptVO> findByParent(String parentId){
       
        return baseDataDao.findDeptByParent(parentId);
    }
    
    /**
     * </p>根据prantId查找检测建议</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    public List<CheckSuggest> getCheckSuggestByParentId(String parentId) {
        if(StringUtil.isBlank(parentId)) {
            parentId = "0";
        }
        return baseDataDao.getCheckSuggestByParentId(parentId);
    }
    
    /**
     * </p>根据checkupId查找检查单项指标列表</p>
     * @param checkupId
     * @return
     * @author xiaowei
     * @date 2016年12月12日
     */
    public List<CheckSuggestItem> getCheckSuggestItemListByCheckupId(String checkupId) {
        if(StringUtil.isBlank(checkupId)) {
            return null;
        }
        return baseDataDao.getCheckSuggestItemListByCheckupId(checkupId);
    }
    
    /**
     * </p>根据prantId查找检测建议</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    public List<CheckSuggest> getCheckSuggestByIds(String[] ids) {
        return baseDataDao.getCheckSuggestByIds(ids);
    }

    @Override
	public CheckSuggestItem getCheckSuggestItemById(String id) {
		return baseDataDao.getCheckSuggestItemById(id);
	}


	public List<CheckSuggest> searchCheckSuggest(String keyword) {
    	if(StringUtil.isBlank(keyword)) {
    		return null;
    	}
    	
        return baseDataDao.searchCheckSuggest(keyword);
    }
    
    public MsgTemplate saveMsgTemplate(MsgTemplate param) {
    	if (StringUtil.isEmpty(param.getId())) {
    		final String id = idxRepository.nextMsgTemplateNum(idxType.msgTemplateNum);
    		param.setId(id);
    	}
    	return baseDataDao.saveMsgTemplate(param);
    }
    
	public EvaluationItemPO getEvaluationItem(Integer... packTypes) {
		EvaluationItemPO po = new EvaluationItemPO();

		po.setGoodItem(baseDataDao.getEvaluationItems(EvaluationItemEnum.good.getIndex(), packTypes));
		po.setGeneralItem(baseDataDao.getEvaluationItems(EvaluationItemEnum.general.getIndex(), packTypes));

		return po;
	}
	
    
    /*
     * (non-Javadoc)
     * @see com.dachen.health.base.service.IBaseDataService#queryMsgTemplateById(java.lang.String)
     */
    public MsgTemplate queryMsgTemplateById(final String id) {
    	if (StringUtil.isEmpty(id)) {
    		return null;
    	}
    	return baseDataDao.queryMsgTemplateById(id);
    }
    
    public List<MsgTemplate> queryMsgTemplate(MsgTemplateParam param) {
    	return baseDataDao.queryMsgTemplate(param).asList();
    }
    
    public long queryMsgTemplateCount(MsgTemplateParam param) {
    	return baseDataDao.queryMsgTemplate(param).countAll();
    }
    
    public int deleteMsgTemplateById(String id) {
    	return baseDataDao.deleteMsgTemplateById(id);
    }
    
    public int deleteMsgTemplate(String[] ids) {
    	return baseDataDao.deleteMsgTemplate(ids);
    }

    /**
     * 格式化文案模板的标题
     * 
     * @param id
     * @param args
     * @return
     */
    public String toTitle(String id, Object... args) {
    	TemplateHelper helper = new TemplateHelper(id, args);

    	MsgTemplateParam param = new MsgTemplateParam();
    	param.setId(id);
    	
    	List<MsgTemplate> list = queryMsgTemplate(param);
    	if (list == null || list.isEmpty()) {
    		return null;
    	}

    	String text = list.get(0).getTitle();
    	return helper.formatText(text);
    }

	/**
	 * 格式化文案模板的内容
	 * 
	 * @param id
	 * @param args
	 * @return
	 */
	public String toContent(String id, Object... args) {
		final TemplateHelper helper = new TemplateHelper(id, args);
    	
    	final MsgTemplate template = queryMsgTemplateById(id);
    	if (template == null) {
    		throw new RuntimeException("ID" + id + "短信模版未找到");
    	}

    	final String text = template.getContent();
    	return helper.formatText(text);
	}

    /**
     * 获取短信内容和第三方平台对应模板的id
     * @param id
     * @param args
     * @return content, tencentId
     */
    public JSONObject toContentAndThirdId(String id, Object... args) {
        final TemplateHelper helper = new TemplateHelper(id, args);
        final MsgTemplate template = queryMsgTemplateById(id);
        if (template == null) {
            throw new RuntimeException("ID" + id + "短信模版未找到");
        }
        String text = template.getContent();
        String content = helper.formatText(text);
        JSONObject obj = new JSONObject();
        obj.put("content", content);
        if(template.getTencentId() != null){
            obj.put("tencentId", template.getTencentId());
        }
        return obj;
    }

    @Override
    public PageVO getAllDoctorInfo(DoctorParam param) {
        List<DoctorVO> data = new ArrayList<>();
        DBCursor cursor = baseDataDao.getAllDoctorInfo(param);
        cursor.skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            DoctorVO doctorVO = new DoctorVO();
            doctorVO.setUserId(MongodbUtil.getInteger(obj, "_id"));
            doctorVO.setName(MongodbUtil.getString(obj, "name"));
            doctorVO.setStatus(MongodbUtil.getInteger(obj, "status"));
            doctorVO.setModifyTime(MongodbUtil.getLong(obj, "modifyTime"));
            doctorVO.setLastLoginTime(MongodbUtil.getLong(obj, "lastLoginTime"));
            DBObject docObj = (DBObject) obj.get("doctor");
            doctorVO.setDoctorNum(docObj.get("doctorNum").toString());
            doctorVO.setTitleRank(MongodbUtil.getString(docObj, "titleRank"));
            if (Objects.nonNull(docObj.get("check")) && StringUtil.isNotEmpty(MongodbUtil.getString(docObj, "check"))) {
                DBObject checkObj = (DBObject) docObj.get("check");
                doctorVO.setTitle(MongodbUtil.getString(checkObj, "title"));
                doctorVO.setHospital(MongodbUtil.getString(checkObj, "hospital"));
                doctorVO.setHospitalId(MongodbUtil.getString(checkObj, "hospitalId"));
                doctorVO.setDepartments(MongodbUtil.getString(checkObj, "departments"));
                doctorVO.setDeptId(MongodbUtil.getString(checkObj, "deptId"));
                doctorVO.setDeptPhone(MongodbUtil.getString(checkObj, "deptPhone"));
            } else {
                doctorVO.setTitle(MongodbUtil.getString(docObj, "title"));
                doctorVO.setHospital(MongodbUtil.getString(docObj, "hospital"));
                doctorVO.setHospitalId(MongodbUtil.getString(docObj, "hospitalId"));
                doctorVO.setDepartments(MongodbUtil.getString(docObj, "departments"));
                doctorVO.setDeptId(MongodbUtil.getString(docObj, "deptId"));
                doctorVO.setDeptPhone(MongodbUtil.getString(docObj, "deptPhone"));
            }
            data.add(doctorVO);
        }
        cursor.close();
        return new PageVO(data, (long) (cursor.count()), param.getPageIndex(), param.getPageSize());
    }

    @Override
    public void loadSchoolExcel(MultipartFile file) throws IOException, InvalidFormatException {
        String fileName = file.getOriginalFilename();
        /* 文件类型校验 */
        if (!StringUtils.endsWithIgnoreCase(fileName, "xls") && !StringUtils.endsWithIgnoreCase(fileName, "xlsx")) {
            throw new ServiceException("文件类型错误");
        }
//        Resource resource = new ClassPathResource("xaa.xlsx");
//        InputStream is = file.getInputStream();
//        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet hssfSheet = workbook.getSheetAt(0);  //示意访问sheet
        if (hssfSheet == null) {
            return;
        }
        List<DBObject> collegesPOS = Lists.newArrayList();
        /* 处理当前页，从第2行循环读取每一行 */
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            /* 读取行信息 */
            HSSFRow hssfRow = (HSSFRow) hssfSheet.getRow(rowNum);
            if (Objects.isNull(hssfRow)) {
                throw new ServiceException("行信息为空,Row:---" + rowNum);
            }
            DBObject dbObject = new BasicDBObject();
            int minColIx = hssfRow.getFirstCellNum();
            /* 一般是每行第一格 */
            HSSFCell collegeNameCell = hssfRow.getCell(minColIx + 1);
            if (Objects.nonNull(collegeNameCell)) {
                /* 解析单元格字符类型（字符串） */
                if (Objects.equals(collegeNameCell.getCellType(), HSSFCell.CELL_TYPE_STRING)) {
                    String collegeName = collegeNameCell.getStringCellValue();
                    if (StringUtil.isBlank(collegeName)) {
                        throw new ServiceException("collegeName is Null,Row:---" + rowNum);
                    }
                    dbObject.put("collegeName", collegeName);
                }
            }
            /* 默认相邻 */
            HSSFCell collegeCodeCell = hssfRow.getCell(minColIx + 2);
            if (Objects.nonNull(collegeCodeCell)) {
                /* 解析单元格字符类型（数字） */
                if (Objects.equals(collegeCodeCell.getCellType(), HSSFCell.CELL_TYPE_STRING)) {
                    String collegeCode = collegeCodeCell.getStringCellValue();
                    if (StringUtil.isBlank(collegeCode)) {
                        throw new ServiceException("collegeCode is Null,Row:---" + rowNum);
                    }
                    dbObject.put("collegeCode", collegeCode);
                }
            }
            /* 默认相邻 */
            HSSFCell collegeProCell = hssfRow.getCell(minColIx + 4);
            if (Objects.nonNull(collegeProCell)) {
                /* 解析单元格字符类型（字符串） */
                if (Objects.equals(collegeProCell.getCellType(), HSSFCell.CELL_TYPE_STRING)) {
                    String collegeProvince= collegeProCell.getStringCellValue();
                    if (StringUtil.isBlank(collegeProvince)) {
                        throw new ServiceException("collegeProvince is Null,Row:---" + rowNum);
                    }
                    dbObject.put("collegeProvince", collegeProvince);
                }
            }
            /* 默认相邻 */
            HSSFCell collegeAreaCell = hssfRow.getCell(minColIx + 5);
            if (Objects.nonNull(collegeAreaCell)) {
                /* 解析单元格字符类型（字符串） */
                if (Objects.equals(collegeAreaCell.getCellType(), HSSFCell.CELL_TYPE_STRING)) {
                    String collegeArea = collegeAreaCell.getStringCellValue();
                    if (StringUtil.isBlank(collegeArea)) {
                        throw new ServiceException("collegeArea is Null,Row:---" + rowNum);
                    }
                    dbObject.put("collegeArea", collegeArea);
                }
            }
            /* 默认相邻 */
            HSSFCell collegeLevelCell = hssfRow.getCell(minColIx + 6);
            if (Objects.nonNull(collegeLevelCell)) {
                /* 解析单元格字符类型（字符串） */
                if (Objects.equals(collegeLevelCell.getCellType(), HSSFCell.CELL_TYPE_STRING)) {
                    String collegeLevel = collegeLevelCell.getStringCellValue();
                    dbObject.put("collegeLevel", collegeLevel);
                }
            }
            long time = System.currentTimeMillis();
            dbObject.put("createTime", time);
            dbObject.put("modifyTime", time);
            collegesPOS.add(dbObject);
        }
        baseDataDao.addCollegeData(collegesPOS);
        /* 返回VO */
    }

    @Override
    public String addCollegeData(CollegeParam collegeParam) {
        if (StringUtil.isBlank(collegeParam.getCollegeProvince()) || StringUtil.isBlank(collegeParam.getCollegeArea()) || StringUtil.isBlank(collegeParam.getCollegeLevel()) || StringUtil.isBlank(collegeParam.getCollegeName())) {
            throw new ServiceException("院校名称，省市或等级不能为空");
        }
        return baseDataDao.addCollegeData(collegeParam);
    }

    @Override
    public void updateCollegeData(CollegeParam param) {
        if (StringUtil.isBlank(param.getId())) {
            throw new ServiceException("id不能为空");
        }
        if (StringUtil.isBlank(param.getCollegeProvince()) || StringUtil.isBlank(param.getCollegeArea()) || StringUtil.isBlank(param.getCollegeLevel()) || StringUtil.isBlank(param.getCollegeName())) {
            throw new ServiceException("院校名称，省市或等级不能为空");
        }
        baseDataDao.updateCollegeData(param);
        // 院校信息更新发送MQ
        this.collegeInfoChangeNotify(param.getId());
    }

    @Override
    public PageVO getCollegeData(CollegeParam collegeParam) {
        return baseDataDao.getCollegeData(collegeParam);
    }

    @Override
    public List<CollegesPO> getCollege(String collegeArea, String collegeName) {
        if (StringUtil.isBlank(collegeArea) && StringUtil.isBlank(collegeName)) {
            return null;
        }
        return baseDataDao.getCollege(collegeArea, collegeName);
    }

    @Override
    public List<CollegeDeptPO> getCollegeDept() {
        return baseDataDao.getCollegeDept();
    }

    public static void main(String args[]) {
		final TemplateHelper helper = new TemplateHelper("1","");
		final String txt = "{0}-dfsfsklfjslkfjs";
		System.out.println(helper.formatText(txt));
	}

	@Override
	public List<HospitalLevelPo> getHospitalLevel() {
		return baseDataDao.getHospitalLevels();
	}
	
	@Override
	public List<ExpectAppointment> getExpectAppointments() {
		return baseDataDao.getExpectAppointments();
	}

	@Override
	public List<GeoDeptVO> getAllGeoDepts() {
		List<GeoDeptPO> poList = baseDataDao.getAllGeoDepts();
		
		List<GeoDeptVO> voList = new ArrayList<>();
		
		if(!CollectionUtils.isEmpty(poList)){
			for (GeoDeptPO po : poList) {
				GeoDeptVO vo = new GeoDeptVO();
				
				DeptVO dept = baseDataDao.getDeptById(po.getDeptId());
				
				if(dept == null){
					continue;
				}
				vo.setId(dept.getId());
				vo.setName(dept.getName());
				vo.setIsLeaf(dept.getIsLeaf());
				vo.setParentId(dept.getParentId());
				
				voList.add(vo);
			}
		}
		return voList;
	}
	@Override
	public PageVO getIncAreaInfos(AreaParam param){
		Map<String,Object> map = new HashMap<String,Object>(); 
		if(param.getLastUpdatorTime() != null){
			map.put("lastUpdatorTime >=", param.getLastUpdatorTime());
		}
		Query<AreaVO> query = baseDataDao.getIncInfos(map, "b_area", AreaVO.class);
		List<AreaVO> data = query.offset(param.getStart()).limit(param.getPageSize()).asList();
		return new PageVO(data,query.countAll(),param.getPageIndex(),param.getPageSize());
	}
	@Override
	public PageVO getIncDocTitleInfos(DoctTitleParam param){
		Map<String,Object> map = new HashMap<String,Object>(); 
		if(param.getLastUpdatorTime() != null){
			map.put("lastUpdatorTime >=", param.getLastUpdatorTime());
		}
		Query<DoctorTitleVO> query = baseDataDao.getIncInfos(map, "b_doctortitle", DoctorTitleVO.class);
		List<DoctorTitleVO> data = query.offset(param.getStart()).limit(param.getPageSize()).asList();
		return new PageVO(data,query.countAll(),param.getPageIndex(),param.getPageSize());
	}
	@Override
	public PageVO getIncHospitalDeptInfos(HospitalDeptParam param){
		Map<String,Object> map = new HashMap<String,Object>(); 
		if(param.getLastUpdatorTime() != null){
			map.put("lastUpdatorTime >=", param.getLastUpdatorTime());
		}
		Query<HospitalDeptVO> query = baseDataDao.getIncInfos(map, "b_hospitaldept", HospitalDeptVO.class);
		List<HospitalDeptVO> data = query.offset(param.getStart()).limit(param.getPageSize()).asList();
		return new PageVO(data,query.countAll(),param.getPageIndex(),param.getPageSize());
	}
	@Override
	public PageVO getIncHospitalInfos(HospitalParam param){
		Map<String,Object> map = new HashMap<String,Object>(); 
		if(param.getLastUpdatorTime() != null){
			map.put("lastUpdatorTime >=", param.getLastUpdatorTime());
		}
		Query<HospitalVO> query = baseDataDao.getIncInfos(map, "b_hospital", HospitalVO.class);
		List<HospitalVO> data = query.offset(param.getStart()).limit(param.getPageSize()).asList();
		return new PageVO(data,query.countAll(),param.getPageIndex(),param.getPageSize());
	}
	
	public PageVO getIncDoctorInfos(DoctorParam param){
		List<DoctorVO> data = new ArrayList<DoctorVO>();
		DBCursor cursror = baseDataDao.getIncDoc(param);
		cursror.skip(param.getStart()).limit(param.getPageSize());
		while(cursror.hasNext()){
			DBObject obj = cursror.next();
			DoctorVO vo = new DoctorVO();
			vo.setName(obj.get("name")==null?"":obj.get("name").toString());
			vo.setStatus(Integer.parseInt(obj.get("status").toString()));
			vo.setModifyTime(Long.parseLong(obj.get("modifyTime").toString()));
			DBObject docObj =(DBObject) obj.get("doctor");
			vo.setDoctorNum(docObj.get("doctorNum").toString());
			vo.setDepartments(docObj.get("departments")==null?"":docObj.get("departments").toString());
			vo.setDeptId(docObj.get("deptId")==null ? "" :docObj.get("deptId").toString());
			vo.setTitleRank(docObj.get("titleRank")==null?"":docObj.get("titleRank").toString());
			if(docObj.get("check") != null && StringUtil.isNotEmpty(com.dachen.util.MongodbUtil.getString(docObj,"check"))){
				DBObject checkObj = (DBObject)docObj.get("check");
				vo.setTitle(checkObj.get("title")==null?"":checkObj.get("title").toString());
				vo.setHospital(checkObj.get("hospital")==null?"":checkObj.get("hospital").toString());
				vo.setHospitalId(checkObj.get("hospitalId")==null?"":checkObj.get("hospitalId").toString());
			}
			
			data.add(vo);
		}cursror.close();
		return new PageVO(data,(long)(cursror.count()),param.getPageIndex(),param.getPageSize());
	}

	@Override
	public Pagination<GroupUnionVo> getAllGroupUnionPage(String name,Integer pageIndex, Integer pageSize) {
		return baseDataDao.getAllGroupUnionPage(name,pageIndex,pageSize);
	}


	public List<GroupVO> getAllGroup() {
		return baseDataDao.getAllGroup();
	}

    @Override
    public List<HospitalDeptVO> getDepartments() {
        return baseDataDao.getHospitalDeptList();
    }


    @Override
    public HospitalPO findHospitalByName(String name) {
        return baseDataDao.getHospitalByName(name);
    }

    public void collegeInfoChangeNotify(String collegeId) {
        LOGGER.info("院校基础信息更新发送MQ,collegeId:{}", " " + "collegeId = [" + collegeId + "]");
        CollegesPO collegesPO = baseDataDao.getCollegeById(collegeId);
        BasicProducer.fanoutMessage(BaseConstants.COLLEGE_INFO_CHANGE, JSON.toJSONString(collegesPO));
    }
}
