package com.dachen.health.base.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.param.AddHospitalParam;
import com.dachen.health.base.entity.param.CollegeParam;
import com.dachen.health.base.entity.param.DoctorParam;
import com.dachen.health.base.entity.param.MsgTemplateParam;
import com.dachen.health.base.entity.po.*;
import com.dachen.health.base.entity.vo.*;
import com.dachen.sdk.page.Pagination;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Map;



/**
 * ProjectName： health-service<br>
 * ClassName： IAreaDao<br>
 * Description： 地区分类dao<br>
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public interface IBaseDataDao {
	
	HospitalPO addHospital(HospitalPO hospitalPO);
	
	HospitalPO updateHospital(HospitalPO hospitalPO);
	
    /**
     * </p>根据父编码获取地区</p>
     * 
     * @param pcode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<AreaVO> getAreas(int pcode);
    
    /**
     * 通过code 读取区域信息
     * @author wangqiao
     * @date 2016年5月26日
     * @param code
     * @return
     */
    AreaVO getAreaByCode(Integer code);
    
    /**
     * </p>获取地区下的医院</p>
     * 
     * @param areaCode
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<HospitalVO> getHospitals(int country,String name);

	List<HospitalVO> getOkStatusHospitals(int country,String name);

    List<HospitalVO> getHospitals(Long timeline);
    
    PageVO getHospitals(Long timeline, String hospital, String hospitalId, PageVO page);
    
    List<HospitalVO> getHospitals(String name);
    
    /**
     * 获取全部的医院
     * @return
     */
    List<HospitalVO> getAllHospitals();

    /**
     * </p>根据父id获取科室</p>
     * 
     * @param deptId
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<DeptVO> getDepts(String deptId,String name);
    
    /**
     * </p>获取职称</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<TitleVO> getTitles();
    
    /**
     * </p>查找医院</p>
     * @param hospitalId
     * @return
     * @author fanp
     * @date 2015年7月17日
     */
    HospitalVO getHospital(String hospitalId);

	/**
	 * 根据级别集合 查询医院
	 * @param listLevel
	 * @return
     */
	List<HospitalVO> getHospitalBylevelList(List<String> listLevel);

		/**
         * </p>查找科室</p>
         * @param dept
         * @return
         * @author fanp
         * @date 2015年7月17日
         */
    DeptVO getDept(String dept);
    
    /**
     * </p>查找职称</p>
     * @param title
     * @return
     * @author fanp
     * @date 2015年7月17日
     */
    TitleVO getTitle(String title);
    
    /**
     * 获取全部的职称
     * @return
     */
    List<TitleVO> getAllTitles();
    
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
     * @param ids
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    List<DiseaseTypeVO> getDiseaseType(List<String> ids);

    /**
     * </p>根据prantId查找病种</p>
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年10月12日
     */
    List<DiseaseTypeVO> getDiseaseByParent(String parentId);
    
    /**
     * </p>根据prantId查找病种(返回到“科室－病种”级别)</p>
     * @param parentId
     * @return
     * @author dwju
     * @date 2015年11月18日
     */
    List<DiseaseTypeVO> getOneLevelDiseaseByParent(String parentId);
    
    /**
     * </p>获取所有病种</p>
     * @return
     * @author fanp
     * @date 2015年10月13日
     */
    List<DiseaseTypeVO> getAllDiseaseType();
    
    
    List<ServiceItem> getServiceItemByParent(String parentId);
    
    List<ServiceItem> getServiceItemByIds(String... ids);
    
    /**
     * </p>获取科室</p>
     * @param ids
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    List<DeptVO> getDeptByIds(List<String> ids);
    
	List<DeptVO> findDeptByParent(String parentId);
	
	List<CheckSuggest> getCheckSuggestByParentId(String parentId);

	CheckSuggestItem getCheckSuggestItemById(String id);

	List<CheckSuggest> getCheckSuggestByIds(String[] ids);
	
	CheckSuggest getCheckSuggestById(String id);
	
	List<CheckSuggest> searchCheckSuggest(String keyword);
	
	List<CheckSuggestItem> getCheckSuggestItemListByCheckupId(String checkupId);

	List<CheckSuggestItem> getCheckSuggestItemListByIds(List<String> Ids);

	MsgTemplate saveMsgTemplate(MsgTemplate param);
	
	/**
	 * 根据ID获取消息模版.
	 * @param id 模版ID
	 * @return 模版对象
	 */
	MsgTemplate queryMsgTemplateById(final String id);
	
	Query<MsgTemplate> queryMsgTemplate(MsgTemplateParam param);
	
	int deleteMsgTemplateById(String id);
	
	int deleteMsgTemplate(String[] ids);
	
	EvaluationItem getEvaluationItem(String id);
	
	List<EvaluationItem> getEvaluationItems(Integer level, Integer... packTypes);
	
	List<HospitalPO> getHospitals(Integer code, boolean is3A);
	List<HospitalVO> getHospitalInfos(Integer code, boolean is3A);
	
	List<HospitalPO> getHospitalsCodeNull(Integer code, boolean is3A);
	List<HospitalVO> getHospitalsByLocation(Integer code, boolean is3A, Double lng, Double lat);

	HospitalPO getHospitalDetail(String hospitalId);
	
    /**
     * 通过id读取科室信息
     * @author wangqiao
     * @date 2016年3月30日
     * @param deptId
     * @return
     */
    DeptVO getDeptById(String deptId);

	HospitalPO getHospitalByName(String name);

	/**
     * 根据地域信息以及一医院的名称查询该医院又没哟被添加过
     * @param param
     * @return
     */
    HospitalPO checkHospital(AddHospitalParam param);

	List<HospitalVO> getHospitals(List<String> hospitalIds);

	/**
	 * 获取医院级别信息
	 * @return
	 */
	List<HospitalLevelPo> getHospitalLevels();
	
	/**
	 * 获取订单期望预约标签列表
	 * @return
	 */
	List<ExpectAppointment> getExpectAppointments();
	
	
	/**
	 * 获取所有的科室
	 * @return
	 */
	List<DepartmentVO> getAllDepartments();
	
	/**
	 * 根据id获取科室信息
	 * @param id
	 * @return
	 */
	DepartmentVO getDepartmentById(String id);
	
	/**
	 * 获取全部的子节点
	 * @param areaCode
	 * @return
	 */
	List<Integer> getAllAreaChildByParentId(Integer areaCode);
	
	/**
	 * 获取全部的省市区信息
	 * @return
	 */
	List<AreaVO> getAllAreas();

	List<String> getAllDeptChildByParentId(String deptId);
	
	String getExpectAppointmentsByIds(String expectAppointmentIds);
	
	/**
	 * 获取所有科室（附近医生使用）
	 * @return
	 */
	List<GeoDeptPO> getAllGeoDepts();
	
	<T> Query<T> getIncInfos(Map<String,Object> map, String collection, Class<T> t);
	
	DBCursor  getIncDoc(DoctorParam param);

	List<CheckSuggest> getAllLeaf();

	/**
	 * 获取所有的医联体
	 * @return list
     */
	List<GroupUnionVo> getAllGroupUnion();

	/**
	 * 获取所有的医联体分页
	 * @return list
	 */
	Pagination<GroupUnionVo> getAllGroupUnionPage(String name,Integer pageIndex, Integer pageSize);
	/**
	 * 获取所有集团圈子
	 * @return
     */
	List<GroupVO> getAllGroup();

    List<HospitalDeptVO> getHospitalDeptList();

    DBCursor getAllDoctorInfo(DoctorParam param);

	void addCollegeData(List<DBObject> collegesPOS);

	String addCollegeData(CollegeParam collegeParam);

	void updateCollegeData(CollegeParam param);

	PageVO getCollegeData(CollegeParam collegeParam);

	List<CollegesPO> getCollege(String collegeArea, String collegeName);

	List<CollegeDeptPO> getCollegeDept();

	CollegesPO getCollegeById(String collegeId);
}
