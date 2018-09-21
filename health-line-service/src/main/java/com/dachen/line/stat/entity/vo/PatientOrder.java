package com.dachen.line.stat.entity.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.line.stat.util.RelativeDateFormat;

@Entity(value = "v_order", noClassnameStored = true)
public class PatientOrder {

	@Id
	private String id;
	private String basicId;// 基础订单id
	private String checkId;// 检查单id
	private String productId;// 产品id
	private int userId;// 当前用户id 
	private int patientId;// 关联患者用户表
	private String patientName;// 就诊人 默认为患者的名称
	private String patientTel;// 就诊人的电话 默认为患者的手机号
	private int cityId;// 城市的ID 默认为深圳
	private String doctorName;// 挂号医生 可以由患者用户修改
	private Integer doctorId;// 平台医生的ID 由平台医生推荐的线下服务
	private String appointmentTime;// 预约的时间戳 精确到分钟
	private String remark;// 备注留言 默认可以不填写
	private Long time;// 创建时间 系统生成，时间戳
	private Long updTime;// 更新时间
	private @NotSaved String timeAgo;// 创建时间 系统生成，时间戳
	private @NotSaved String day;// 创建时间 系统生成，时间戳
	private @NotSaved String week;
	private @NotSaved String hours;
	private double price;// 订单的价格 各种线下服务项的总和（为以后的打折做出预留）
	private int status;// 1等待接单 2等待上传结果 3待评价 4结束
	private int type;// 默认 0 未支付 1是正常订单 2是取消订单 3是异常订单
	
	@Embedded
	private LineServiceProduct product = null;

	@Embedded
	private List<LineService> lineList = new ArrayList<LineService>();
	@Embedded
	private List<String> hospitalList = new ArrayList<String>();
	@Embedded
	private List<String> departList = new ArrayList<String>();

	/**
	 * 线下服务集合
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getLineServiceMap() {

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

		if (ConfigUtil.checkCollectionIsEmpty(lineList)) {
			for (LineService line : lineList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", line.getBasicId());// 基础订单id
				map.put("title", line.getTitle());
				map.put("type", line.getType());
				listMap.add(map);
			}
		}
		return listMap;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientTel() {
		return patientTel;
	}

	public void setPatientTel(String patientTel) {
		this.patientTel = patientTel;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getHospitalList() {
		return hospitalList;
	}

	public void setHospitalList(List<String> hospitalList) {
		this.hospitalList = hospitalList;
	}

	public List<String> getDepartList() {
		return departList;
	}

	public void setDepartList(List<String> departList) {
		this.departList = departList;
	}

	public String getDay() {
		if (null != appointmentTime) {
			day = DateUtils
					.formatDate2Str(DateUtils.toDate(appointmentTime).getTime(), new SimpleDateFormat("MM月dd日"));
		}
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeek() {
		if (null != appointmentTime) {
			week = DateUtils.getWeekOfDate(DateUtils.toDate(appointmentTime));
		}
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getHours() {
		if (null != appointmentTime) {
			appointmentTime=appointmentTime+":00";
			hours = DateUtils.formatDate2Str(DateUtils.toDate(appointmentTime), new SimpleDateFormat(
					"HH点mm分"));
		}
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getTimeAgo() {
		if(null!=time)
		{	
			timeAgo = RelativeDateFormat.formatOrderTime(time);	
		}
		return timeAgo;
	}

	public void setTimeAgo(String timeAgo) {
		this.timeAgo = timeAgo;
	}

	public List<LineService> getLineList() {
		return lineList;
	}

	public void setLineList(List<LineService> lineList) {
		this.lineList = lineList;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getUpdTime() {
		return updTime;
	}

	public void setUpdTime(Long updTime) {
		this.updTime = updTime;
	}

	public String getBasicId() {
		return basicId;
	}

	public void setBasicId(String basicId) {
		this.basicId = basicId;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public LineServiceProduct getProduct() {
		return product;
	}

	public void setProduct(LineServiceProduct product) {
		this.product = product;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public static void main(String[] args) {
//		String orderTime = "2016-01-23 07:00:00";
//
//		System.out.println(DateUtils.formatDate2Str(DateUtils.toDate(orderTime)
//				.getTime(), new SimpleDateFormat("HH点mm分")));
//		long time = DateUtils.toDate(orderTime).getTime();
//		String timeAgo = RelativeDateFormat.format(DateUtils.toDate(orderTime));
//
//		System.out.println(DateUtils.formatDate2Str(time, new SimpleDateFormat(
//				"MM月dd日")));
//		
//		System.out.println(DateUtils.getWeekOfDate(new Date(time)));
	}
}
