package com.dachen.health.base.service;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.param.*;
import com.dachen.health.base.entity.po.*;
import com.dachen.health.base.entity.vo.*;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.tree.ExtTreeNode;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ProjectName： health-service<br>
 * ClassName： IAreaService<br>
 * Description：地区分类service <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public interface IBaseDataService {
	
	// AddHospitalParam param
//	public HospitalPO addHospital(int provinceId, int cityId, int countryId, String name);
	public HospitalPO addHospital(AddHospitalParam param);
	public HospitalPO updateHospital(AddHospitalParam param);
	
    /**
     * </p>根据父编码获取地区</p>
     * 
     * @param pcode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<AreaVO> getAreas(int pcode);
    
    
    List<AreaVO> getAllAreas();

	List<AreaVO> getAllAreasExDirect();

    List<AreaVO> getAllAreasInfo();
    
    List<AreaVO> getHotCityList();
    
    
    
    /**
     * 通过 区域code 读取区域名称
     * @author wangqiao
     * @date 2016年5月26日
     * @param code
     * @return
     */
    public String getAreaNameByCode(Integer code);

    /**
     * </p>获取地区下的医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<HospitalVO> getHospitals(int country,String name);

	/**
	 * 获取正常状态的医院 在医生注册之后选择医院使用
	 * @param country
	 * @param name
	 * @return
	 */
	List<HospitalVO> getOkStatusHospitals(int country,String name);

    List<HospitalVO> getHospitals(Long timeline);
    PageVO getHospitals(Long timeline, String hospital, String hospitalId, PageVO page);
    
    public List<HospitalVO> getHospitals(String name);
    
    public HospitalVO getHospital(String hospitalId);
    
    /**
     * </p>获取科室</p>
     * 
     * @param deptId
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<DeptVO> getDepts(String deptId,String name);
    
    /**
     * 获取所有科室
     * @return
     */
    List<DeptVO> getAllDepts();
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<TitleVO> getTitles();
    
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    TitleVO getTitle(String title);
    
    /**
     * </p>获取病种下所有子病种</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<String> getDiseaseTypeChildren(String id);
    
    /**
     * </p>获取病种</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<DiseaseTypeVO> getDiseaseType(List<String> ids);
    
    /**
     * </p>获取病种树</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<ExtTreeNode> getDiseaseTypeTree();
    
    /**
     * </p>根据病种获取父节点构建树结构</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<ExtTreeNode> getDiseaseTypeTree(List<String> diseaseIds);
    
    /**
     * </p>根据prantId查找病种</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    List<DiseaseTypeVO> getDiseaseByParent(String parentId);
    
    /**
     * </p>根据prantId查找病种</p>
     * @param parentId
     * @return
     * @author dwju
     * @date 2015年11月18日
     */
    List<DiseaseTypeVO> getOneLevelDiseaseByParent(String parentId);
    
    public List<ServiceItem> getServiceItemByParent(String parentId);
    
	List<DeptVO> findByParent(String parentId);

	public List<CheckSuggest> getCheckSuggestByParentId(String parentId);
	
	List<CheckSuggestItem> getCheckSuggestItemListByCheckupId(String checkupId);
	
	public List<CheckSuggest> getCheckSuggestByIds(String[] ids);

	CheckSuggestItem getCheckSuggestItemById(String id);

	public List<CheckSuggest> searchCheckSuggest(String keyword);

	public MsgTemplate saveMsgTemplate(MsgTemplate param);
	
	/**
	 * 根据ID查找消息模版.
	 * @param id 消息ID
	 * @return 消息模版对象
	 */
	public MsgTemplate queryMsgTemplateById(final String id);

	public List<MsgTemplate> queryMsgTemplate(MsgTemplateParam param);
	
	public long queryMsgTemplateCount(MsgTemplateParam param);
	
	public int deleteMsgTemplateById(String id);

	public int deleteMsgTemplate(String[] ids);
	
	EvaluationItemPO getEvaluationItem(Integer... packTypes);
	
	/**
	 * 格式化文案的标题
	 * 
	 * @param id
	 * @param args
	 * @return
	 */
	public String toTitle(String id, Object... args);
	
	/**
	 * 格式化文案的内容
	 * 
	 * @param id
	 * @param args
	 * @return
	 */
	public String toContent(String id, Object... args);
	
	/**
	 * 通过科室id，查询该科室和父节点科室的信息
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param deptId
	 * @return
	 */
	public DeptVO getDeptAndParentDeptById(String deptId);
	
	/**
	 * 获取医院的级别信息
	 * @return
	 */
	public List<HospitalLevelPo> getHospitalLevel();

	List<ExpectAppointment> getExpectAppointments();
	
	/**
	 * 获取所有科室（附近医生使用）
	 * @return
	 */
	List<GeoDeptVO> getAllGeoDepts();
	
	/**
	 * 增量获取
	 * @param parma
	 * @return
	 */
	public PageVO getIncAreaInfos(AreaParam param);
	/**
	 * 增量获取
	 * @param parma
	 * @return
	 */
	public PageVO getIncDocTitleInfos(DoctTitleParam param);
	/**
	 * 增量获取
	 * @param parma
	 * @return
	 */
	public PageVO getIncHospitalDeptInfos(HospitalDeptParam param);
	/**
	 * 增量获取
	 * @param parma
	 * @return
	 */
	public PageVO getIncHospitalInfos(HospitalParam param);
	
	public PageVO getIncDoctorInfos(DoctorParam param);

	/**
	 * 获取所有的医联体
	 * @return list
	 */
	public Pagination<GroupUnionVo> getAllGroupUnionPage(String name,Integer pageIndex, Integer pageSize);

	/**
	 * 获取所有集团圈子
	 * @return
	 */
	List<GroupVO> getAllGroup();

	List<HospitalDeptVO> getDepartments();

	HospitalPO findHospitalByName(String name);

	JSONObject toContentAndThirdId(String id, Object... args) ;

	PageVO getAllDoctorInfo(DoctorParam param);

    void loadSchoolExcel(MultipartFile file) throws IOException, InvalidFormatException;

	String addCollegeData(CollegeParam collegeParam);

	void updateCollegeData(CollegeParam param);

	PageVO getCollegeData(CollegeParam collegeParam);

	List<CollegesPO> getCollege(String collegeArea, String collegeName);

	List<CollegeDeptPO> getCollegeDept();
}
