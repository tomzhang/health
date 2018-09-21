package com.dachen.health.commons.dao;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.param.OpenDoctorParam;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.example.UserQueryExample;
import com.dachen.health.commons.vo.*;
import com.dachen.health.user.entity.param.AddAdminUserParam;
import com.dachen.health.user.entity.param.CustomCollegeParam;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.LearningExperienceParam;
import com.dachen.health.user.entity.po.Assistant;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.LearningExperience;
import com.dachen.sdk.exception.HttpApiException;
import com.mongodb.DBObject;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRepository {

    Map<String, Object> addUser(int userId, UserExample param);

    void addUser(User user);

    List<User> findByTelephone(List<String> telephoneList);


    long getCount(String telephone);
    
    long getCount(String telephone,int userType);

    User.LoginLog getLogin(int userId);

    User.UserSettings getSettings(int userId);

    User getUser(int userId);
	User getUserWithFields(int userId, String ... fields);
    List<User> getUsers(List<Integer> userIdList);

    User getUser(String telephone);
    
    List<User> getUser(String telephone, List<Integer> userTypeList);

    User getUser(String userKey, String password);
    
    User getUser(String telephone, Integer userType);
    
    User getUserByTelAndType(String telephone,Integer userType);
    
    List<User> getUserByNameAndTelephoneAndType(String name,String telephone, Integer userType);
    
    User getUser(String telephone, Integer userType, String password);

    User getUser(String telephone, Integer userType, Integer status);
    
    User getUserByUnionid(String unionid, Integer userType);
    
    void updateWeChatInfo(Integer userId, WeChatUserInfo wechat);

    List<DBObject> queryUser(UserQueryExample param);

//    Map<String, Object> saveAT(User user);

    void updateLogin(int userId, String serial);

    void updateLogin(int userId, UserExample example);

    User updateUser(int userId, UserExample example);
    
    User updateUserNotChangeStatus(int userId, UserExample example);

    User updateUser(User user);

    void updatePassword(String telephone, String password);

    void updatePassowrd(int userId, String password);
    
    void updateTel(int userId, String telephone);

    User updateDoctor(int userId, Doctor param);

	boolean updateDoctorHospital(int userId, HospitalVO hospital);

    User updateAssistant(int userId, Assistant param);
    
    boolean exsistUser(String phone,Integer userType);

    boolean setHeaderPicName(Integer userId,String headerPicName) throws HttpApiException;
    
    void headPicModifyNotify(User user) throws HttpApiException;
    
    String getHeaderPicName(Integer userId);
    
    List<User> getHeaderPicName(List<Integer>userIdList);
    
    void setRemindVoice(Integer userId, UserConfig param);
    
    boolean setRemarks(Integer userId, String remarks);
    
    void updateUserSessionCache(User user);
    
    void updateUserRedisCache(User user);

	void removeUserRedisCache(Integer userId);
    
    /**
     * </p>判断是否为好友</p>
     * @param session
     * @param user
     * @return
     * @author fanp
     * @date 2015年7月16日
     */
    boolean isFriend(UserSession session,User user);
    
    void updateVoip(int userid,Map<String,Object> voip);

	User getUserByVoip(String voip);
	
	public List<User> findUsers(List<Integer> doctorIds);
	
	List<User> findUsersWithOutStatus(List<Integer> doctorIds);
	
	public List<User> findUsers(List<Integer> doctorIds,PageVO pageVO);
	
	public List<User> findUserList(List<Integer> doctorIds);
	
	public Query<User> findUserQuery(List<Integer> doctorIds,List<String> hospitalIds);
	
	public Integer countUsers(List<Integer> doctorIds,Integer userStatus);

	Integer countUsers2(List<Integer> doctorIds,Integer userStatus);

	public List<CarePlanDoctorVO> findUserByDocs(List<Integer> doctorIds);
	
	public List<CarePlanDoctorVO> findUserByDocs(List<Integer> doctorIds,Integer status);
	
	Query<User> getDoctorQuery(List<Integer> doctorIds, List<String> hospitals, String deptId);
	
	Query<User> getDoctorQuery(List<Integer> doctorIds, List<String> hospitals, String deptId, Integer code);
	
	public List<Integer> findUserBlurryByNameAndIphone(String userName,String telephone);
	
	PageVO getGuideDoctorList(String userName,String telephone,Integer pageIndex,Integer pageSize);
	
	PageVO getGuideDoctorList(String groupId,String userName,String telephone,Integer pageIndex,Integer pageSize);
	
	public boolean updateStatus(Integer userId, Integer userStatus);
	boolean updateInvitationInfo(Integer userId, Integer inviterId, Integer subsystem, String way, Boolean deptInvitation);
	public boolean updateFeldsherStatus(Integer userId, Integer userStatus);

	User getDoctorByTelOrNum(String number);

	User getDoctorByTelOrNumNoStatus(String number);

	long searchConsultationDoctorsCount(Set<Integer> doctorIds, List<String> hospitalIds, String name, String deptId);

	List<User> getConsultationDoctors(Set<Integer> doctorIds, List<String> hospitalIds, String name, String deptId, Integer pageIndex, Integer pageSize);

	List<User> getDoctorsByIds(List<Integer> doctorIds);
	
	public  long fingTotalDoctorCount(int flag,List<Integer>  doctorList, Integer  cityId,  String HospitalId,  String  title,String deptId, Integer pageIndex, Integer pageSize);
	/**
	 * 根据关键字查询医生信息
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public  long fingDoctorByKeyWord(String keyWord, Integer pageIndex, Integer pageSize,List<Integer> doctorList);
	
	
	/**
	 * 根据关键字查询医生信息
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public  List<Map<String,Object>> fingDoctorByKeyWordList(String keyWord, Integer pageIndex, Integer pageSize,List<Integer> doctorList);
	
	public  List<Map<String,Object>> fingDoctorList(int flag, List<Integer> doctorList,Integer  cityId,  String HospitalId,  String  title,String deptId, Integer pageIndex, Integer pageSize);

	long searchConsultationDoctorsByKeywordCount(Set<Integer> beSearchIds, String keyword);

	List<User> searchConsultationDoctorsByKeyword(Set<Integer> beSearchIds, String keyword, Integer pageIndex,Integer pageSize);

	List<Integer> getDoctorsByNameOrHospitalNameOrTelephone(String doctorName, String hospitalName,String telephone);
	
	public User getUser(int userId, String name) ;

	List<User> findByName(String name);

	public List<Integer> getUserIdList(String name);

	List<User> findDoctorsInIds(List<Integer> packFriendIds, Integer pageIndex, Integer pageSize);

	long findDoctorsInIdsCount(List<Integer> packFriendIds);
	/**
	 * 根据医生姓名、电话、医院名称查询医生
	 * @param param
	 * @return
	 */
	public  PageVO findDoctorsByCondition(DoctorParam param);

	PageVO findDoctorsByOrgId(String orgId, String hospitalId, String deptId, String name,
        Integer userId, Integer status, Long ts,
        Integer pageIndex,
        Integer pageSize);

	PageVO findDoctorInfoByModifyTime(OpenDoctorParam param);

    public PageVO findDoctorsByName(String keyword, Integer pageIndex, Integer pageSize);
	/**
	 * 根据医院名称查询医院
	 * @param param
	 * @return
	 */
	public  PageVO findHospitalByCondition(DoctorParam param);

    List<Map<String, Object>> findHospitalByConditionNoPaging(String keywords);

	List<User> searchDoctor(String[] hospitalIdList, String keyword, String departments);

	List<User> searchOkStatusDoctor(String[] hospitalIdList, String keyword, String departments);

	List<User> searchDoctorByKeyword(String keyword, String departments);

	List<User> searchOkStatusDoctorByKeyword(String keyword, String departments);

	public HospitalPO getHostpitalByPK(String id);

	PageVO getPlatformSelectedDoctors(List<Integer> notInDoctorIds, String keyWord, Integer pageIndex, Integer pageSize);

	String getDoctorRankById(Integer doctorId);
	
	List<User> getUserListByTypeAndFuzzyName(Integer type,String name);
	
	/**
	 * 
	 * @return
	 */
	List<User> getNormalUser(int type);

    PageVO getNormalUserPaging(Integer type, Integer pageIndex, Integer pageSize);

	User uncheck(User user, Integer checkerId, String checkerName);
	
	/**
	 * 模糊查询查找 用户列表中的用户信息
	 * @param userType 用户类型
	 * @param userStatus 用户状态
	 * @param name 姓名
	 * @param userIds 用户列表
	 * @return
	 */
	public List<User> getUserByTypeAndNameInUserIds(Integer userType, Integer userStatus , String name, List<Integer> userIds);
	
	/**
	 * 在某id中筛选用户数据
	 * @param ids
	 * @param countryId
	 * @param provinceId
	 * @param cityId
	 * @param deptId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	PageVO findByAreaAndDeptWithInIds(List<Integer> ids, Integer countryId, Integer provinceId, Integer cityId, String deptId, Integer pageIndex, Integer pageSize);
	
	GuestUser getGuestByDeviceID(String deviceID);
	/**
	 * 创建游客记录
	 * @author liming
	 */
	public void saveGuestUser(String driverId);
	
	Map<String,Object> saveGuest(String deviceID,String guest_token);
	
	public void updateGuestUser(GuestUser guser);
	/**
	 * 检查患者身份证是否过期
	 * @author liming
	 */
	public boolean checkIdCard(String idcard,Integer idtype,Integer userType);
	
	/**用户的端发生改变**/
	void terminalModifyNotify(User user);

	long searchAppointmentDoctorCount(String keyWord, Integer pageIndex, Integer pageSize, List<Integer> doctorList,
			List<Integer> keywordUserIds);

	List<Map<String, Object>> searchAppointmentDoctor(String keyWord, Integer pageIndex, Integer pageSize,
			List<Integer> doctorList, List<Integer> keywordUserIds);

	/**
	 * 根据用户id和科室id，过滤对应的用户id
	 * @return
	 */
	List<Integer> findByIdsAndDeptIds(List<Integer> doctorIds, List<String> deptIds);
	
	/**
	 * 根据用户id和位置id，过滤对应的用户id
	 * @return
	 */
	List<Integer> findByIdsAndProvinceId(List<Integer> doctorIds, String locId, String range);
	
	/**
	 * 根据用户id和位置id，过滤对应的用户id
	 * @return
	 */
	List<Integer> findNoLoctionDoctorsByIds(List<Integer> doctorIds);
	
	/**
	 * 获取医生助手列表
	 * @param keywords
	 * @param pageIndex
	 * @param pageSize
	 * @param userType
	 * @return
	 */
	public PageVO getFeldsherList(String keywords, Integer pageIndex, Integer pageSize, Integer userType);
	
	/**
	 * 获取可用的医生助手列表
	 * @param userType
	 * @return
	 */
	public List<User> getAvailableFeldsherList(Integer userType);
	
	/**
	 * 根据id获取User，排序按照就诊量降序，职称排序
	 * @param doctorIds
	 * @return
	 */
	List<User> findUsersByIds(List<Integer> doctorIds);
	
	List<User> queryDoctorsByAssistantId(String keywords, Integer assistantId);
	
	PageVO getGroupAddrBook(List<Integer> userIds, Integer areaCode, String deptId, Integer pageIndex, Integer pageSize);

	PageVO getDoctorsByDiseaseId(String diseaseId, Integer pageIndex, Integer pageSize);
	public PageVO getDoctorsByDiseaseIds(List<String> diseaseId, Integer pageIndex, Integer pageSize);
	
	public PageVO getAllUserIdByUserType(Integer userType, Integer pageIndex, Integer pageSize);

	List<User> findUsersWithOutStatusByIds(List<Integer> rootIds);
	
	/**
	 * @param userType
	 * @param name 
	 * 
	 * @Title: findByNameKeyWordAndUserType   
	 * @Description: 根据用户类型和姓名进行模糊匹配
	 * @param: @return      
	 * @return: List<User>      
	 * @author: qinyuan.chen 
	 * @date:   2016年8月19日 下午2:27:03 
	 * @throws
	 */
	List<User> findByNameKeyWordAndUserType(String name, Integer userType);

	/**
	 * 根据姓名，获取对应的用户集合
	 * @param name
	 * @param userType
     * @return
     */
	public List<Integer> getDoctorIds(String name,Integer userType);

	List<Integer> getDoctorIdsByName(String name);

	/**
	 * 根据关键字查询注册的医生信息，运营平台批量加入集团接口使用，查询范围所有注册医生
	 * @param keyword
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize);
	
	/**
	 * 根据name和UserType查询用户id列表
	 * 
	 * @param name 姓名
	 * @param userType 用户类型
	 * @return
	 */
	public List<Integer> getUserIdsByName(String name, Integer userType);
	
	User findByDoctorNum(String doctorNum);
	
	/**获取农牧项目的所有医生**/
	List<User> getFarmUser();
	
	Set<String> getFarmUserDoctorNums();
	
	/**
	 * 更新用户服务的状态
	 * @param userId
	 * @param serviceStatus
	 */
	void updateDoctorServiceStatus(Integer userId, Integer serviceStatus);
	
	/**
	 * 更新医生开通患者报道赠送服务
	 * @param userId
	 * @param checkInGive
	 */
	void updateDoctorCheckInGiveStatus(Integer userId,Integer checkInGive);
	
	void updateSubmitTime(Integer userId, Long submitTime);

    void updateLastLoginTime(Integer userId);

    List<User> findDoctorListByDoctorUserIds(List<Integer> doctorUserIds);
	List<CarePlanDoctorVO> findDoctorInfoGroup(List<Integer> doctorIds, Integer mainDoctorId);

    List<User> findByHospitalAndDept(String hospitalId, String departmentId, List<Integer> withOutIds);

	void updateGiveCoin(Integer userId, Integer giveCoin);
	
	public void updateUserLevel(int userId, Integer userLevel,Long limitedPeriodTime);

	List<User> getLimitPeriodUser(Long scanTime);

	void batchUserToPeriod(List<Integer> userIds, Integer userLevel);

	void saveUserLoginInfo(String serial, String version, String telephone, String phoneModel, String romVersion);

	void updateUserRole(Integer userId, List<String> roleIds);

	List<Map.Entry<String, Integer>> getDepartmentsTopnByUser(Integer topn,Integer userType ,Integer status);

	public Long getDoctorNumByDepartments(Integer userType,String department);

	List<Map<String, Object>> getDepartmentsTopnByUserNew(Integer topn, Integer userType);

	void updateSuspend(Integer userId, Integer suspend);
	
	/**
	 * 根据科室id获取用户id
	 * @param deptId
	 * @param pageIndex
	 * @param pageSize
	 * @param offset 偏移量,取0时则是pageIndex与pageSize的常规分页
	 * @return
	 */
    List<Long> findUserIdsByDeptId(String deptId, int pageIndex, int pageSize, int offset);

    void updateUserRoleIds(Integer userId, List<String> roleIds);

    void updateAdminUser(User user, AddAdminUserParam addAdminUserParam);

	/**
	 * 模糊查找医生,带手机号过滤条件
	 * @param param
	 * @param phones
	 * @return
	 */
	PageVO findDoctorsByParamCondition(DoctorParam param, List<String> phones);

	PageVO getUsersByTelList(List<String> telList, Integer pageIndex, Integer pageSize);

    String addLearningExperience(LearningExperienceParam param, Integer userId);

	void updateLearningExperience(LearningExperienceParam param);

	List<LearningExperience> getLearningExperience(Integer userId);

	Boolean delLearningExperience(String id);

	Integer getLearningExpCount(Integer userId);

	Boolean delLearningExpByUserId(Integer userId);

	void addCustomCollege(CustomCollegeParam param);

	void checkCustomCollege(String learningExpId, String checkCollegeName, String checkCollegeId);

	PageVO getCustomCollege(CustomCollegeParam param);

	Boolean existCustomCollege(String learningExpId);

	void updateCustomCollege(CustomCollegeParam param);

	List<User> getUsersByTelSet(Set<String> telSet);

	void updateLearningExperienceMul(LearningExperienceParam param);
}
