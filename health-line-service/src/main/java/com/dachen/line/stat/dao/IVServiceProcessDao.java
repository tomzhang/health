package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.line.stat.entity.param.ServiceProcessParm;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;

/**
 * 护士订单服务
 * 
 * @author weilit 2015 12 04
 */
public interface IVServiceProcessDao {

	/**
	 * 获取护士订单列表 分页
	 * 
	 * @param scp
	 * @return
	 */
	public PageVO getServiceProcessList(ServiceProcessParm scp);

	/**
	 * check 是不是存在
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public boolean checkVServiceProcessBean(String orderId, Integer userId);

	/**
	 * check 订单有没有别抢
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public VServiceProcess getVServiceProcessBeanByOrderId(String orderId);

	public List<VServiceProcess> getVServiceProcessListById(Integer userId);

	public List<VServiceProcess> getVServiceProcessList();

	/**
	 * 查询制定字段条件下的下面的医院
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<VServiceProcess> getVServiceProcessList(String column,
			Object sourceId, Integer userId);

	/**
	 * 批量插入用户服务数据
	 * 
	 * @param userId
	 * @return
	 */
	public void insertVServiceProcess(VServiceProcess process);

	/**
	 * 批量插入用户服务数据
	 * 
	 * @param userId
	 * @return
	 */
	public void updateVServiceProcess(String processServiceId, Integer status);

	/**
	 * 根据服务ID 查询服务信息
	 * 
	 * @param serviceId
	 * @return
	 */
	public VServiceProcess getVServiceProcessBean(String serviceId);

	/**
	 * 根据患者订单ID 查询服务信息
	 * 
	 * @param serviceId
	 * @return
	 */
	public VServiceProcess getVServiceInfoByOrderId(String orderid);

	/**
	 * 根据流程状态 查询服务流程信息
	 * 
	 * @param
	 * @return
	 */
	public List<VSPTracking> getVServiceInfoByState();

	/**
	 * 护士在预约时间之前就点击了开始服务按钮
	 * 
	 * @param appointmentTime
	 *            预约时间
	 * @param id
	 *            订单id
	 * @return
	 */
	public List<VServiceProcess> getExceptionOfNurseService(
			String appointmentTime, String id);

	/**
	 * 护士过了服务预约时间还没有点击开始服务按钮
	 * 
	 * @param appointmentTime
	 *            预约时间
	 * @param id
	 *            订单id
	 * @return
	 */
	public List<VServiceProcess> getExceptionOfNurseServiceNoClick(
			List<String> id);
}
