package com.dachen.health.commons.service;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.param.OpenDoctorParam;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.example.UserQueryExample;
import com.dachen.health.commons.vo.DoctorBaseInfo;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.commons.vo.WeChatUserInfo;
import com.dachen.health.permission.entity.param.UserRoleParam;
import com.dachen.health.permission.entity.vo.UserRoleVO;
import com.dachen.health.user.entity.param.*;
import com.dachen.health.user.entity.po.Assistant;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.LearningExperience;
import com.dachen.health.user.entity.vo.IsNewAccountVO;
import com.dachen.health.user.entity.vo.UserInfoVO;
import com.dachen.health.wx.model.WXUserInfo;
import com.dachen.sdk.exception.HttpApiException;
import com.mongodb.DBObject;
import org.mongodb.morphia.query.UpdateResults;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserManager {

	User createInactiveUser(String telephone, UserSource userSource);

	User.UserSettings getSettings(int userId);

	User getUser(int userId);
	User getUserInfoById(int userId);

	WeChatUserInfo getWeChatUserInfoById(Integer userId);

	User getUser(int userId, int toUserId);

	User getUser(String telephone);
	
	User getUser(String telephone, Integer userType);
	
	List<User> getUserByNameAndTelephoneAndType(User user);
	/**
	 * 只查询状态正常的医生
	 * @return
	 */
	List<User> getNormalUser(int type);

    PageVO getNormalUserPaging(Integer type, Integer pageIndex, Integer pageSize);

	int getUserId(String accessToken);
 
	boolean isRegister(String telephone,int userType);
	
	boolean isRegister(User user);

	Map<String, Object> login(UserExample example);
	
	Map<String, Object> loginByCode(UserExample example);
	
	Map<String, Object> loginByCaptcha(UserExample example);

	Map<String, Object> loginAuto(String access_token, int userId, String serial);

	Map<String, Object> loginByWeChat4Open(UserExample example, String code) throws HttpApiException;
	Map<String, Object> loginByWeChat4MP(UserExample example, String code) throws HttpApiException;
	boolean logoutByWeChat4MP(String telephone);
	Map<String, Object> loginByWeChat(UserExample example, WXUserInfo wxinfo) throws HttpApiException;
	
	boolean isBindWechat(String telephone, Integer userType);
	
	void logout(String accessToken,String serial);
	
	Map<String, Object> getWeChatStatus4Open(String code, Integer userType);
	Map<String, Object> getWeChatStatus4MP(String code, Integer userType);
	Map<String, Object> getWeChatStatus(WXUserInfo weinfo, Integer userType);

	List<DBObject> query(UserQueryExample param);

	Map<String, Object> register(UserExample example);

	Map<String, Object> registerIMUser(UserExample example);
	
	Map<String, Object> registerByWechat(UserExample example, String code);
	
	//注册用户并添加到集团
	Integer registerGroup(UserExample example,String groupId,Integer inviteId);
	
	Integer registerByAdmin(UserExample example,String groupId);

	void updatePassword(int userId, String oldPassword, String newPassword);
	
	boolean existsUser(String telephone, Integer userType);
	
	void updateTel(int userId, String telephone);

	boolean updateSettings(int userId,User.UserSettings userSettings);

	User updateUser(int userId, UserExample example) throws HttpApiException;

	User updateDoctor(int userId, Doctor param) throws HttpApiException;

	User updateAssistant(int userId, Assistant param) throws HttpApiException;
	
	UpdateResults updateName(int userId, String newName);
	
	UpdateResults updatePassword(String userId,Integer userType,String password);
	
	UserSession getUserById(Integer id); 

	public List<User> getHeaderPicName(List<Integer>userIdList);
	
	boolean updateSettings(int userId,String serial,Integer ispushflag);
	
	void registerDeviceToken(int userId,String serial,String model);
	
	public List<UserInfoVO> getHeaderByUserIds(List<Integer> userIds);
	
	public boolean setRemarks(Integer userId, String remarks);
	
	public String getRemarks(Integer userId); 
	
	public User getRemindVoice(Integer userId);
	
	boolean updateStatus(Integer userId);
	boolean updateInvitationInfo(Integer userId, Integer inviterId, Integer subsystem, String way, Boolean deptInvitation);

	User getDoctorByTelOrNum(String number);

	User getDoctorByTelOrNumNoStatus(String number);

	PageVO searchConsultationDoctors(Set<Integer> doctorIds, List<String> hospitalIds, String name, String deptId, Integer pageIndex, Integer pageSize);

	List<User> getDoctorsByIds(List<Integer> doctorIds);
	
	/**
	 * 查找医生
	 * @param orderId
	 * @param isCity
	 * @param isHospital
	 * @param isTitle
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public PageVO fingDoctors(int flag,List<Integer> doctorList, Integer isCity,String isHospital, String isTitle,String deptId, Integer pageIndex,Integer pageSize);
	/**
	 * 根据医生姓名、医院、职称、擅长、科室查找医生信息
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public PageVO fingDoctorByKeyWord(String keyWord, Integer pageIndex,Integer pageSize,List<Integer> doctorList);

	PageVO searchConsultationDoctorsByKeyword(Set<Integer> beSearchIds, String keyword, Integer pageIndex, Integer pageSize);

	List<Integer> getDoctorsByNameOrHospitalNameOrTelephone(String doctorName, String hospitalName,String telephone);
	
	User getUser(int userId,String  name);
	
	//通过名称搜索用户id列表
	List<Integer>  getUserIdList(String  name);
	
	/**
	 * 一键重置密码
	 * @param userId
	 * @param telephone
	 */
	void oneKeyReset(int userId,String telephone,String pwd);
	/**
	 * 更新用户信息
	 * @param userId
	 * @param telephone
	 */
	void updateGuideInfo(int userId,String telephone,String username);

	List<User> findDoctorsInIds(List<Integer> packFriendIds, Integer pageIndex, Integer pageSize);

	long findDoctorsInIdsCount(List<Integer> packFriendIds);
	
	void updateGuideStatus(Integer userId,int state);
	
	
	String getGroupNameById(String id);

	public PageVO findDoctorsByCondition(DoctorParam param);

	PageVO findDoctorsByOrgId(String appKey, String orgId, String hospitalId, String deptId,
        String name,
        Integer userId, Integer status,
        Long ts,
        Integer pageIndex, Integer pageSize);

	PageVO findDoctorInfoByModifyTime(OpenDoctorParam param);

    PageVO findDoctorsByName(String keyword, Integer pageIndex, Integer pageSize);

	public PageVO findHospitalByCondition(DoctorParam param);
    List<Map<String, Object>> findHospitalByConditionNoPaging(String keywords);

	PageVO getPlatformSelectedDoctors(List<Integer> notInDoctorIds, String keyWord, Integer pageIndex, Integer pageSize);
	
	/**
	 * 根据用户模糊查询对应类型用户列表
	 * @param userType
	 * @param name
	 * @return
	 */
	List<User> getUserByTypeAndFuzzyName(Integer userType,String name);
	
	/**
	 * 模糊查询查找 用户列表中的用户信息
	 * @param userType 用户类型
	 * @param userStatus 用户状态
	 * @param name 姓名
	 * @param userIds 用户列表
	 * @return
	 */
	public List<User> getUserByTypeAndNameInUserIds(Integer userType, Integer userStatus, String name, List<Integer> userIds);

	/**
	 * 更新用户的端信息（登录的时候用到）
	 * @author 傅永德
	 * @param user
	 */
	public void updateTerminal(User user);

	PageVO searchAppointmentDoctor(String keyWord, Integer pageIndex, Integer pageSize, List<Integer> doctorList , List<Integer> keywordUserIds);
	
	List<User> searchDoctor(String[] hospitalIdList, String keyword, String departments);

	List<User> searchOkStatusDoctor(String[] hospitalIdList, String keyword, String departments);

	List<User> searchDoctorByKeyword(String keyword, String departments);

	/**
	 * 更新医生助手信息
	 * @param userId
	 * @param telephone
	 * @param name
	 */
	public void updateFeldsherInfo(Integer userId, String telephone, String name);
	
	/**
	 * 更新医生助手状态，启用禁用
	 * @param userId
	 * @param status
	 */
	public void updateFeldsherStatus(Integer userId, int status);
	
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
	public List<Map<String, String>> getAvailableFeldsherList(Integer userType);
	
	/**
	 * 获取医生助手，根据医生id
	 * @param doctorId
	 * @return
	 */
	public User getFeldsherByDoctor(Integer doctorId);
	
	/**
	 * 
	 * @Title: queryDoctorsByAssistantId   
	 * @Description: 根据医生助手ID查询对应的医生列表
	 * @param: assistantId
	 * @param: keywords
	 * @return     
	 * @author: qinyuan.chen 
	 * @date:   2016年7月26日 下午8:15:40 
	 * @throws
	 */
	public List<User> queryDoctorsByAssistantId(String keywords, Integer assistantId);
	
	List<User> findUserByIds(List<Integer> ids);

	List<User> getUsers(List<Integer> userIdList);
	
	public PageVO getAllUserIdByUserType(Integer userType, Integer pageIndex, Integer pageSize);
	
	public PageVO getRecord(String id, String keyword, Integer pageIndex, Integer pageSize);

	/**
	 * 根据医生姓名获取id集合
	 * @param name
	 * @return
     */
	public List<Integer> getDoctorIds(String name);
	
	/**
	 * 根据name和UserType查询用户id列表
	 * 
	 * @param name 姓名
	 * @param userType 用户类型
	 * @return
	 */
	public List<Integer> getUserIdsByName(String name, Integer userType);
	
	Map<String, Object> createDoctors(InputStream inputStream, String nurseShortLinkUrl, Integer adminId, String fileType) throws HttpApiException;
	
	Map<String, Object> checkUserData(InputStream inputStream, String nurseShortLinkUrl, Integer adminId, String fileType);
	
	boolean existUser(String doctorNum);
	
	Map<String, Object> resetPhoneAndPassword(String doctorNum, String newPhone, String newPassword);

	void updateDoctorServiceStatus(Integer userId, Integer serviceStatus);
	
	public void updateDoctorCheckInGiveStatus(Integer userId,Integer checkInGive);

	void initAllDoctorServiceStatus(Set<Integer> userIds);
	
	void patientRename(String name);
	
	Map<String, Object> loginBuyCareOrder(UserExample example);

	/**
	 * 根据id判断时候为正常状态的医生
	 * @param doctorId
	 * @return
     */
	boolean checkDoctor(Integer doctorId);
	
	User invitePatientFromDrugStore(String drugStoreId,String storeName, String userName, String telephone) throws HttpApiException;

	PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize);

	void uncheck(User doctor, Integer checkerId, String name);

	void updateUserSessionCache(User user);
	User getUserNoException(int userId);

    Integer validatePassword(String password);

	Map<String,Object> getDoctorStatusByTelephone(String telephone);

    void disable(Integer userId, DisableDoctorParam doctorParam);

	void enable(Integer userId);

	void updateGiveCoin(Integer userId, Integer giveCoin);

	void handLimitPeriodUser();

	boolean upgradeUserLevel(Integer userId,String reason);

	Object updateUserLimitPeriod(Integer userId, Long time);

	void sendSmsToDownload(Integer userId,String circleName,Integer type);

	void userInfoChangeNotify(Integer userId);

	List<HospitalVO> findHospitalByName(String name);
	
	void sendSmsToDownloadByTels(String tels,String circleName,String smsType);

	void saveUserLoginInfo(String serial, String telephone, String userAgent);

	PageVO getUserLoginInfo(int pageIndex,int pageSize,String telephone);

	Integer getRegistryUserCount(Long time);

	Integer getCertifyUserCount(Long time);

	Integer getActivityUserCount(Long today);

	Integer getUserPositionCount(String city);

	void activityDoctorLoginNotify(Integer userId);

	void activityCommitAuthNotify(Integer userId);

    Integer getSubmitUserCount(Long time);

	List<DoctorBaseInfo> getDoctorInfo(String name);

	List<Map<String,Object>> departmentTopn(Integer topn);

    void updateUserRole(UserRoleParam param);

    UserRoleVO getUserRolePermission(Integer userId);

    List<Map<String,Object>> departmentTopnNew(Integer topn);

	User findUserById(int i);

    void addAdminUser(AddAdminUserParam addAdminUserParam);

    void updateAdminUser(AddAdminUserParam addAdminUserParam);

	IsNewAccountVO isNewAccount(String telephone, Integer type);

	PageVO findDoctorsByParamCondition(DoctorParam param, List<String> phones);

	PageVO getUsersByTelList(List<String> telList, Integer pageIndex, Integer pageSize);

	Map<String,Map<String,String>> getUserSource();

    void updatePasswordV2(Integer userId, String password);

    void addLearningExperience(LearningExperienceParam param, Integer userId);

	void updateLearningExperience(LearningExperienceParam param);

	List<LearningExperience> getLearningExperience(Integer userId);

	Boolean delLearningExp(String id);

	void updateLearningExperienceMul(LearningExperienceParam param);

	void checkCustomCollege(String learningExpId, String checkCollegeName, String checkCollegeId, Integer userId);

	PageVO getCustomCollege(CustomCollegeParam param);

	List<User> getUsersByTelSet(Set<String> telSet);
}
