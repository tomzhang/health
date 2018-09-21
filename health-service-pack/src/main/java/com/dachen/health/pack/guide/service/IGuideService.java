package com.dachen.health.pack.guide.service;

import java.util.List;
import java.util.Map;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.param.GroupConfigAndFeeParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.pack.guide.entity.param.ConsultOrderParam;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.DialogueImgPO;
import com.dachen.health.pack.guide.entity.vo.AppointmentGuideOrderDetail;
import com.dachen.health.pack.guide.entity.vo.ConsultOrderVO;
import com.dachen.health.pack.guide.entity.vo.HelpVO;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.vo.AppointmentOrderWebParams;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.sdk.exception.HttpApiException;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IGuideService<br>
 * Description： 导医service<br>
 * 
 * @author chengwei
 * @createTime 2015年9月28日
 * @version 1.0.0
 */
public interface IGuideService {
	public Object existServingOrder(UserSession session);
	
	public GroupInfo createConsultOrder(ConsultOrderParam param,UserSession session) throws HttpApiException;
	
	/**
	 * 导医接单
	 * @param id :咨询订单的id
	 * @param session
	 */
	public Object receivingOrder(String id,UserSession session) throws HttpApiException;
	
	/**
	 * 根据医生id查询是否传过来的医生已经进行过登记
	 * @param doctorId
	 * @return
	 */
	public Map<String,Object> getConsultOrderDoctor(int doctorId,String orerId,int userId);
	
	/**
	 * 结束服务
	 * @param id
	 */
	public Object endService(String id) throws HttpApiException;
	
	/**
	 * 结束服务(自动结束服务)
	 * @param id
	 */
	public Object endServiceAuto(String id) throws HttpApiException;
	
	/**
	 * 结束服务(自动结束服务)
	 * @param id
	 */
	public Object endService(ConsultOrderPO order,String type) throws HttpApiException;
	/**
	 * 支付完成
	 * @param id
	 * @param orderId
	 */
	public Object endPay(Integer orderId) throws HttpApiException;
	
	/**
	 * 导医向患者发送预约时间
	 */
	/**
	 * 预约时间
	 */
	public Object appointTime(String gid,Integer packId,Long startTime,Long endTime,String type) throws HttpApiException;
	
	/**
	 * 修改预约时间
	 */
	public Object updateAppointTime(Integer orderId,Long time) throws HttpApiException;
	
	/**
	 * 等待接单列表
	 * @param count
	 * @param startScore
	 * @param endScore
	 * @return
	 */
	public List<ConsultOrderVO> waitOrderList(int count,Long startScore,Long endScore);
	
	/**
	 * 获取所有超时订单
	 * @return
	 */
	public List<ConsultOrderPO> getTimeOutOrderList();
	/**
	 * 获取导医接单记录
	 * @param userId
	 * @param startTime
	 * @param count
	 * @return
	 */
	public Object orderList(Integer userId,Long startTime,Integer count);
	
	/**
	 * 预约医生
	 * @param gid
	 * @param doctorId
	 * @return
	 */
	public Object doctorInfo(String gid,Integer doctorId);
	
	/**
     * 添加医生可预约时间
     * @apiAuthor 成伟
     * @date 2015年10月8日
     */
	public Object addDocTime(Integer doctorId,Long startTime,Long endTime);
	
	/**
	 * 添加医生备注
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	public Object addDocRemark(Integer doctorId,String remark,String guideId,String guideName);
	
	/**
     * 删除医生可预约时间
     * @apiAuthor 成伟
     * @date 2015年10月8日
     */
	public Object removeDocTime(Integer doctorId,Long startTime,Long endTime);
	
	public long getMaxServiceTime();
	

	/**
	 * 查看病情详情（患者-导医）
	 * @param param
	 * @return
	 */
	public OrderDiseaseVO findOrderDisease(String id);
	/**
	 * 查看病情详情（患者-导医）备注---以及患者信息
	 * @param param
	 * @return
	 */
	public OrderDiseaseVO findOrderDiseaseAndRemarks(String id,Integer userId);
	
	/**
	 * 修改病情详情（患者）
	 * @param param
	 * @return
	 */
	public Object updateOrderDisease(OrderDiseaseVO param) throws HttpApiException;
	
	/**
	 * 根据会话Id获取当前订单Id
	 */
	public String getOrderIdByGid(String gid);
	
	/**
	 * 获取订单预约时间
	 * @param orderId
	 * @return
	 */
	public Long getAppointTime(Integer orderId);
	
	PageVO getOrders(OrderParam param);
	
	void handleConfirm(Integer orderId) throws HttpApiException;
	
	public void dataUpgrade();
	/**
	 * 约不到医生，继续等待接口
	 * @param groupId
	 * @param orderId
	 * add by  liwei  2016年1月20日
	 */
	public void sendOnWaiterMsg(String groupId,String orderId) throws HttpApiException;
	
	/**
	 * 请导医帮忙推荐接口
	 * @param groupId
	 * @param orderId
	 */
	public void sendCommendMsg(String groupId, String orderId) throws HttpApiException;
	
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
	public PageVO fingDoctors(String  orderId, boolean  isCity,  boolean  isHospital,  boolean  isTitle, Integer pageIndex, Integer pageSize);
	
	/**
	 * 根据医生姓名、医院、职称、擅长、科室查找医生信息
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @param pageSize2 
	 * @return
	 */
	public PageVO findDoctorByKeyWord(String keyWord, Integer queryType, Integer pageIndex, Integer pageSize);
	
	
	public Object sendDoctorCard(String groupId, String orderId,Integer doctorId) throws HttpApiException;
	
	/**
	 * 患者与导医在会话的过程当中将或者发送的图片记录下来
	 */
	public void addDialogueImg(String guideId,String[] imgs, String orderId);
	/**
	 * 获取或者订单与导医之间对话的时候发送给导医的图片
	 */
	public List<DialogueImgPO> getDialogueImg(String orderId);
	
	
	//********************************导医咨询记录2.0版本开始***************************************
	//********************************导医咨询记录2.0版本***************************************
	public PageVO get8HourOrder(OrderParam param);
	/**
	 * 导医已经处理过的订单
	 * @param param
	 * @return
	 */
	public PageVO getGuideAlreadyServicedOrder(OrderParam param);
	
	
	public List<HelpVO> heathWaitOrderList() throws HttpApiException;
	
	
	public String receiveCareOrder(HelpVO vo);
	
	
	public List<User> getDoctorTeamByOrderId(Integer orderId);
	
	
	public Object getCareOrderDetail(Integer orderId,String careType,String sourceId) throws HttpApiException;
	
	
	public Object updateCareOrder(Integer orderId,String careType,String id) throws HttpApiException;
	
	public List<HelpVO> getHandleCareOrder(Integer userId) throws HttpApiException;

	public Object getAppointmentOrders(Integer status, Integer pageIndex, Integer pageSize);

	public AppointmentGuideOrderDetail getAppointmentDetail(Integer orderId) throws HttpApiException;

	public void submitAppointmentOrder(Integer orderId, String hospitalId, Long appointTime) throws HttpApiException;

	public List<HospitalInfo> getGroupHospital(String groupId);

	public void setAppointmentInfo(String groupId, Boolean openAppointment, Integer appointmentGroupProfit,
			Integer appointmentParentProfit, Integer appointmentMin,Integer appointmentMax,
			  Integer appointmentDefault) throws HttpApiException;

	public Object getAppointmentInfo(String groupId);

	public void setGroupHospital(GroupParam param) throws HttpApiException;

	public Object getHaveAppointmentListByDate(OrderParam param);

	public Object getAppointmentListByCondition(OrderParam param);
	
	public Object getAppointmentPaidOrders(String groupId, String hospitalId, Long date);

	public Object searchAppointmentOrderByKeyword(String keyword, Integer pageIndex, Integer pageSize);

	public PageVO doctorOfflinesByDate(String groupId, String hospitalId, Long date, Integer period, Integer pageSize, Integer pageIndex);

	public Map<String, List<OfflineItem>> getPatientAppointmentByCondition(OrderParam param);

	public void changeAppointmentTime(String offlineItemId, Integer orderId) throws HttpApiException;

	public Map<String,List<Map<String,Object>>> getDoctorOneDayOffline(String hospitalId, Long date, Integer doctorId);

	public int isTimeToAppointment(Long startTime, Integer doctorId);

	public Object takeAppointmentOrder(AppointmentOrderWebParams webParams) throws HttpApiException;
	
	/**
	 * 集团财务设置
	 * @param param
	 * @author tan.yf
	 * @throws HttpApiException 
	 * @date 2016年6月22日
	 */
	public void updateGroupConfigAndFee(GroupConfigAndFeeParam param) throws HttpApiException;
	
	PageVO getBeServicedPatients(Integer pageIndex, Integer pageSize);

	void replyPatientMessage(Integer userId);

	void finish(String msgGroupId) throws HttpApiException;

	void sendDoctorCard2Patient(String gid, Integer packId) throws HttpApiException;
	
	PageVO getCustomerWorkDate(Long baseTime, Integer pageIndex, Integer pageSize);

	PageVO getDayRecords(Long dateTime, Integer pageIndex, Integer pageSize);

	public String clearAllGuideSession();

	public Object getUserInfo(Integer userId);

	boolean hasService(Integer userId);

}
