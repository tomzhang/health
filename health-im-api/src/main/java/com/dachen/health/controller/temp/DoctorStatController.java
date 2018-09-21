package com.dachen.health.controller.temp;

import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.group.group.entity.vo.DoctorExcelVo;
import com.dachen.health.group.group.entity.vo.ItemAnswerInfo;
import com.dachen.health.group.group.entity.vo.OrderExcelVo;
import com.dachen.health.group.group.entity.vo.PatientExcelVo;
import com.dachen.health.pack.income.util.ExcelUtil;
import com.dachen.health.temp.service.DoctorExcelService;
import com.google.common.collect.Lists;
import com.google.gson.internal.Streams;

@RestController
@RequestMapping("/statdoctor")
public class DoctorStatController {
	
	@Autowired
	private DoctorExcelService doctorExcelService;
	
	
	/**
	 * @api {GET} /statdoctor/statdoctor 获取医生统计信息、患者统计信息、订单统计信息的excel
	 * @aipVersion 1.0.0
	 * @apiName statdoctor
	 * @apiGroup 获取统计信息的excel
	 * @apiDescription 获取医生统计信息、患者统计信息、订单统计信息的excel
	 * 
	 * @apiParam {start} start 开始时间
	 * @apiParam {end} end 结束时间
	 * @apiSccess {Object}
	 * @apiAuthor 傅永德
	 * @date 2016年5月9日
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/statdoctor")
	public String statDoctor(
			@RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end,
			HttpServletRequest request, HttpServletResponse response
	) throws IOException, ParseException {
		String fileName = "doctor";
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = simpleDateFormat.parse(start);
		Date endDate = simpleDateFormat.parse(end);
		
		if (startDate.after(endDate)) {
			throw new ServiceException("开始时间必须在于结束时间");
		}
		if (endDate.getTime() - startDate.getTime() > 10 * 24 * 60 * 60 * 1000) {//跨度不能超过10天
			throw new ServiceException("开始时间-结束时间 时间跨度不能超过10天");
		}
		
		String[] totalKey = new String[] {"date", "authedDoctor", "unAuthedDoctor", "authFailDoctor", "activePatient", "unActivePatient", "pictureAdvise", "phoneAdvise", "careAdvise" };
		String[] totalColumnName = new String[] {"日期", "新增审核通过医生数", "新增未认证医生数", "新增未通过审核医生数", "新增患者数", "未激活患者总数", "新增图文订单数", "新增电话订单数", "新增健康关怀订单数" };

		//待审核列名
        String[] waitAuthKey = new String[] { "registerTime", "authTime", "phone", "name", "openId", "isFull", "hospital", "hospitalId", "province", "city", "country", "department", "title", "source", "registerGroup", "inviter", "inviteOpenId", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise", "lastLoginTime", "userLevel", "limitedPeriodTime", "circleNames", "deptName"};
        String[] waitAuthColumnName = new String[] { "注册日期", "审核日期", "电话", "姓名", "用户OPENID", "是否填写职业信息", "医疗机构", "医院ID", "省", "市", "区", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "所属集团/医院", "邀请人", "邀请人OPENID", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀", "最后登录时间", "身份类型", "到期时间", "已加入的圈子", "已加入的科室"};

		// 已审核列名
		String[] authedKey = new String[] { "registerTime", "authTime", "phone", "name", "openId", "isFull", "hospital", "hospitalId", "province", "city", "country", "department", "title", "source", "registerGroup", "inviter", "inviteOpenId", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise", "lastLoginTime", "userLevel", "limitedPeriodTime", "circleNames", "deptName"};
		String[] authedColumnName = new String[] { "注册日期", "审核日期", "电话", "姓名", "用户OPENID", "是否填写职业信息", "医疗机构", "医院ID", "省", "市", "区", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "所属集团/医院", "邀请人", "邀请人OPENID", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀", "最后登录时间", "身份类型", "到期时间", "已加入的圈子", "已加入的科室"};
		
		//未认证列名
		String[] unAuthedKey = new String[] { "registerTime", "authTime", "phone", "name", "openId", "isFull", "hospital", "hospitalId", "province", "city", "country", "department", "title", "source", "registerGroup", "inviter", "inviteOpenId", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise", "lastLoginTime", "userLevel", "limitedPeriodTime", "circleNames", "deptName"};
		String[] unAuthedColumnName = new String[] { "注册日期", "审核日期", "电话", "姓名", "用户OPENID", "是否填写职业信息", "医疗机构", "医院ID", "省", "市", "区", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "所属集团/医院", "邀请人", "邀请人OPENID", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀", "最后登录时间", "身份类型", "到期时间", "已加入的圈子", "已加入的科室"};
		
		//未通过审核
		String[] authFailKey = new String[] { "registerTime", "authTime", "phone", "name", "openId", "isFull", "hospital", "hospitalId", "province", "city", "country", "department", "title", "source", "registerGroup", "inviter", "inviteOpenId", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise", "lastLoginTime", "userLevel", "limitedPeriodTime", "circleNames", "deptName"};
		String[] authFailColumnName = new String[] { "注册日期", "审核日期", "电话", "姓名", "用户OPENID", "是否填写职业信息", "医疗机构", "医院ID", "省", "市", "区", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "所属集团/医院", "邀请人", "邀请人OPENID", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀", "最后登录时间", "身份类型", "到期时间", "已加入的圈子", "已加入的科室"};
		
		//已经激活的患者
		String[] activedPatientKey = new String[] {"registerTime", "name", "phone", "sex", "birthday", "age", "address", "lastLoginTime", "labers"};
		String[] activedPatientColumnName = new String[] {"注册日期", "姓名", "电话", "性别", "出生日期", "年龄", "所在地", "最后登录时间", "患者标签"};

		//订单相关的
		String[] picAdviseKey = new String[] {"orderNo", "createTime", "doctorName", "doctorPhone", "patientName", "patientPhone", "userName", "hasPay", "price", "payTime", "orderStatus", "groupName", "picMessageStatus"};
		String[] picAdviseColumnName = new String[] {"订单编号", "订单生成时间", "医生姓名", "医生手机号", "患者姓名", "患者手机号", "用户姓名", "是否付款", "订单金额(单位：元)", "付款时间", "订单状态", "订单归属集团", "当前状态"};

		
		//订单相关的
		String[] adviseKey = new String[] {"orderNo", "createTime", "doctorName", "doctorPhone", "patientName", "patientPhone", "userName", "hasPay", "price", "payTime", "orderStatus", "groupName"};
		String[] adviseColumnName = new String[] {"订单编号", "订单生成时间", "医生姓名", "医生手机号", "患者姓名", "患者手机号", "用户姓名", "是否付款", "订单金额(单位：元)", "付款时间", "订单状态", "订单归属集团"};

		List<String> tempCareKeyList = Lists.newArrayList();
		tempCareKeyList.add("orderNo");
		tempCareKeyList.add("createTime");
		tempCareKeyList.add("doctorName");
		tempCareKeyList.add("doctorPhone");
		tempCareKeyList.add("patientName");
		tempCareKeyList.add("patientPhone");
		tempCareKeyList.add("userName");
		tempCareKeyList.add("hasPay");
		tempCareKeyList.add("price");
		tempCareKeyList.add("payTime");
		tempCareKeyList.add("orderStatus");
		tempCareKeyList.add("groupName");
		tempCareKeyList.add("careName");
		tempCareKeyList.add("lastAnswerTime");
		tempCareKeyList.add("answerCount");
		tempCareKeyList.add("message");
		List<String> tempCareColumnNameList = Lists.newArrayList();
		tempCareColumnNameList.add("订单编号");
		tempCareColumnNameList.add("订单生成时间");
		tempCareColumnNameList.add("医生姓名");
		tempCareColumnNameList.add("医生手机号");
		tempCareColumnNameList.add("患者姓名");
		tempCareColumnNameList.add("患者手机号");
		tempCareColumnNameList.add("用户姓名");
		tempCareColumnNameList.add("是否付款");
		tempCareColumnNameList.add("订单金额(单位：元)");
		tempCareColumnNameList.add("付款时间");
		tempCareColumnNameList.add("订单状态");
		tempCareColumnNameList.add("订单归属集团");
		tempCareColumnNameList.add("关怀计划名称");
		tempCareColumnNameList.add("最近一次答题时间");
		tempCareColumnNameList.add("答题次数");
		tempCareColumnNameList.add("医患留言");
	
		//未激活患者
		String[] unActivePatientKey = new String[] {"registerTime", "phone", "inviterName", "inviterPhone"};
		String[] unActivePatientColumnName = new String[] {"注册时间", "患者手机", "邀请人", "邀请人手机"};
		
		//顺序和下面三个要保持一致
		List<String> sheetNames = new ArrayList<>();
		sheetNames.add("汇总");
		sheetNames.add("待审核医生");
		sheetNames.add("审核通过医生");
		sheetNames.add("未认证医生");
		sheetNames.add("未通过医生");
		sheetNames.add("激活患者明细");
		sheetNames.add("未激活患者");
		sheetNames.add("图文咨询订单");
		sheetNames.add("电话咨询订单");
		sheetNames.add("健康关怀订单");
		
		List<List<Map<String, Object>>> values = new ArrayList<>();

		//待审核
        List<DoctorExcelVo> waitAuthDoctorVos = doctorExcelService.getDoctorStatVos(UserStatus.uncheck.getIndex(), startDate.getTime(), endDate.getTime());

		//已审核
		List<DoctorExcelVo> authedDoctorVos = doctorExcelService.getDoctorStatVos(UserEnum.UserStatus.normal.getIndex(), startDate.getTime(), endDate.getTime());
		//图文咨询
		doctorExcelService.setHasAdvise(authedDoctorVos, 1);
		//电话咨询
		doctorExcelService.setHasAdvise(authedDoctorVos, 2);
		//健康关怀
		doctorExcelService.setHasAdvise(authedDoctorVos, 3);
		List<DoctorExcelVo> unAuthedDoctorVos = doctorExcelService.getDoctorStatVos(UserEnum.UserStatus.Unautherized.getIndex(), startDate.getTime(), endDate.getTime());
		List<DoctorExcelVo> authFailDoctorVos = doctorExcelService.getDoctorStatVos(UserEnum.UserStatus.fail.getIndex(), startDate.getTime(), endDate.getTime());
		
		List<PatientExcelVo> activePatients = doctorExcelService.getPatientExcelVo(UserEnum.UserStatus.normal.getIndex(), startDate.getTime(), endDate.getTime());
		List<PatientExcelVo> unActivePatients = doctorExcelService.getPatientExcelVo(UserEnum.UserStatus.inactive.getIndex(), startDate.getTime(), endDate.getTime());
		//设置未激活患者的邀请人的信息
		doctorExcelService.setInviterInfo(unActivePatients);
		
		List<OrderExcelVo> pictureOrders = doctorExcelService.getByPackTypeAndTime(PackEnum.PackType.message.getIndex(), startDate.getTime(), endDate.getTime());
		List<OrderExcelVo> phoneOrders = doctorExcelService.getByPackTypeAndTime(PackEnum.PackType.phone.getIndex(), startDate.getTime(), endDate.getTime());
		List<OrderExcelVo> careOrders = doctorExcelService.getByPackTypeAndTime(PackEnum.PackType.careTemplate.getIndex(), startDate.getTime(), endDate.getTime());
		careOrders = doctorExcelService.setCareLastAnswerTime(careOrders);
		//获取最长标题行
		Map<String, List<String>> tMap = doctorExcelService.getLongTitle(careOrders);
		List<String> extCareKeys = tMap.get("keys");
		List<String> extCareTitles = tMap.get("title");
		
		tempCareKeyList.addAll(extCareKeys);
		tempCareColumnNameList.addAll(extCareTitles);
		
		String[] careKey = tempCareKeyList.toArray(new String[tempCareKeyList.size()]);
		String[] careColumnName = tempCareColumnNameList.toArray(new String[tempCareColumnNameList.size()]);

		List<Map<String, Object>> waitAuthDoctorValues = changeDoctorVos2Map(waitAuthDoctorVos);
		List<Map<String, Object>> authedDoctorValues = changeDoctorVos2Map(authedDoctorVos);
		List<Map<String, Object>> unAuthedDoctorValues = changeDoctorVos2Map(unAuthedDoctorVos);
		List<Map<String, Object>> authFailDoctorValues = changeDoctorVos2Map(authFailDoctorVos);
		List<Map<String, Object>> activePatientValues = changeActivePatientVos2Map(activePatients);
		List<Map<String, Object>> unActivePatientValues = changeUnActivePatient2Map(unActivePatients);
		
		List<Map<String, Object>> pictureAdviseValues = changeAdvise2Map(pictureOrders);
		List<Map<String, Object>> phoneAdviseValues = changeAdvise2Map(phoneOrders);
		List<Map<String, Object>> careAdviseValues = changeAdvise2Map(careOrders, 3);
		
		List<Map<String, Object>> total = getTotal(startDate, endDate, authedDoctorVos, unAuthedDoctorVos, authFailDoctorVos, activePatients, unActivePatients, pictureOrders, phoneOrders, careOrders);
		
		values.add(total);

		//待审核通过
		values.add(waitAuthDoctorValues);

		//审核通过的医生
		values.add(authedDoctorValues);
		
		//未审核的医生
		values.add(unAuthedDoctorValues);
		
		//审核未通过的医生
		values.add(authFailDoctorValues);
		
		//激活的患者
		values.add(activePatientValues);
		
		//未激活患者
		values.add(unActivePatientValues);
		
		//图文咨询
		values.add(pictureAdviseValues);
		
		//电话咨询
		values.add(phoneAdviseValues);
		
		//健康关怀
		values.add(careAdviseValues);
		
		List<String[]> keys = new ArrayList<>();
		keys.add(totalKey);
		keys.add(waitAuthKey);
		keys.add(authedKey);
		keys.add(unAuthedKey);
		keys.add(authFailKey);
		keys.add(activedPatientKey);
		keys.add(unActivePatientKey);
		keys.add(picAdviseKey);
		keys.add(adviseKey);
		keys.add(careKey);
		
		List<String[]> columnNames = new ArrayList<>();
		columnNames.add(totalColumnName);
		columnNames.add(waitAuthColumnName);
		columnNames.add(authedColumnName);
		columnNames.add(unAuthedColumnName);
		columnNames.add(authFailColumnName);
		columnNames.add(activedPatientColumnName);
		columnNames.add(unActivePatientColumnName);
		columnNames.add(picAdviseColumnName);
		columnNames.add(adviseColumnName);
		columnNames.add(careColumnName);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ExcelUtil.createSXSSFWorkBooks(sheetNames, values, keys, columnNames).write(os);
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		
		response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="+ new String((fileName+".xlsx").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
	}
	
	/**
	 * @api {GET} /statdoctor/statAdviseDoctor 统计开启服务的医生
	 * @aipVersion 1.0.0
	 * @apiName statAdviseDoctor
	 * @apiGroup 获取统计信息的excel
	 * @apiDescription 统计开启服务的医生
	 * 
	 * @apiSccess {Object}
	 * @apiAuthor 傅永德
	 * @date 2016年5月9日
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/statAdviseDoctor")
	public String statDoctor(
			HttpServletRequest request, HttpServletResponse response
	) throws IOException, ParseException {
		String fileName = "doctor";
		
		String[] key = new String[] { "registerTime", "authTime", "phone","price", "name", "isFull", "hospital", "department", "title", "source", "registerGroup", "inviter", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise"};
		String[] columnName = new String[] { "注册日期", "审核日期", "电话", "价格", "姓名", "是否填写职业信息", "医疗机构", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "注册说明（所属集团/医院）", "邀请人", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀"};
		
		String[] key4Care = new String[] { "registerTime", "authTime", "phone", "name", "isFull", "hospital", "department", "title", "source", "registerGroup", "inviter", "hasPictureAdvise", "hasPhoneAdvise", "hasCareAdvise"};
		String[] columnName4Care = new String[] { "注册日期", "审核日期", "电话", "姓名", "是否填写职业信息", "医疗机构", "科室", "职称", "注册来源（app注册，集团邀请，医院邀请）", "注册说明（所属集团/医院）", "邀请人", "是否开启图文咨询", "是否开启电话咨询", "是否开启健康关怀"};

		
		// 开启图文咨询的医生的基本信息
		List<DoctorExcelVo> pictureDoctors = doctorExcelService.getAdviseDoctor(PackEnum.PackType.message.getIndex());
		//图文咨询
		doctorExcelService.setHasAdvise(pictureDoctors, 1);
		//电话咨询
		doctorExcelService.setHasAdvise(pictureDoctors, 2);
		//健康关怀
		doctorExcelService.setHasAdvise(pictureDoctors, 3);
		// 开启电话咨询的医生的基本信息
		List<DoctorExcelVo> phoneDoctors = doctorExcelService.getAdviseDoctor(PackEnum.PackType.phone.getIndex());
		//图文咨询
		doctorExcelService.setHasAdvise(phoneDoctors, 1);
		//电话咨询
		doctorExcelService.setHasAdvise(phoneDoctors, 2);
		//健康关怀
		doctorExcelService.setHasAdvise(phoneDoctors, 3);
		// 开启健康关怀的医生的基本信息
		List<DoctorExcelVo> careDoctors = doctorExcelService.getAdviseDoctor(PackEnum.PackType.careTemplate.getIndex());
		//图文咨询
		doctorExcelService.setHasAdvise(careDoctors, 1);
		//电话咨询
		doctorExcelService.setHasAdvise(careDoctors, 2);
		//健康关怀
		doctorExcelService.setHasAdvise(careDoctors, 3);
		
		//顺序和下面三个要保持一致
		List<String> sheetNames = new ArrayList<>();
		sheetNames.add("开启图文咨询医生明细");
		sheetNames.add("开启电话咨询医生明细");
		sheetNames.add("开启健康关怀医生明细");

		List<List<Map<String, Object>>> values = new ArrayList<>();
		values.add(changeDoctorVos2Map(pictureDoctors, 1));
		values.add(changeDoctorVos2Map(phoneDoctors, 2));
		values.add(changeDoctorVos2Map(careDoctors, 3));

		List<String[]> keys = new ArrayList<>();
		keys.add(key);
		keys.add(key);
		keys.add(key4Care);

		List<String[]> columnNames = new ArrayList<>();
		columnNames.add(columnName);
		columnNames.add(columnName);
		columnNames.add(columnName4Care);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ExcelUtil.createWorkBooks(sheetNames, values, keys, columnNames).write(os);
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		
		response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="+ new String((fileName+".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
	}
	
	/**
	 * 将医生统计的信息转化为Map
	 * @param doctorExcelVos
	 * @return
	 */
	List<Map<String, Object>> changeDoctorVos2Map(List<DoctorExcelVo> doctorExcelVos) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> values = new ArrayList<>();
		if (doctorExcelVos != null && doctorExcelVos.size() > 0) {
			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				Map<String, Object> mapValue = new HashMap<>();
				mapValue.put("registerTime", doctorExcelVo.getRegisterTimeStr());
				mapValue.put("authTime", doctorExcelVo.getAuthTimeStr());
				mapValue.put("phone", doctorExcelVo.getPhone());
				mapValue.put("name", doctorExcelVo.getName());
                mapValue.put("openId", doctorExcelVo.getOpenId());
				mapValue.put("isFull", doctorExcelVo.getIsFull());
				mapValue.put("hospital", doctorExcelVo.getHospital());
				mapValue.put("hospitalId", doctorExcelVo.getHospitalId());
				mapValue.put("department", doctorExcelVo.getDepartment());
				mapValue.put("title", doctorExcelVo.getTitle());
				if (doctorExcelVo.getPrice() != null) {					
					Double price = Double.valueOf(doctorExcelVo.getPrice()/100);
					mapValue.put("price", String.valueOf(price));
				} else {
					mapValue.put("price", "未知");
				}
				
				mapValue.put("province", doctorExcelVo.getProvince());
				mapValue.put("city", doctorExcelVo.getCity());
				mapValue.put("country", doctorExcelVo.getCountry());
				mapValue.put("lastLoginTime", doctorExcelVo.getLastLoginTimeStr());
				
				StringBuffer group = new StringBuffer();
				StringBuffer inviter = new StringBuffer();
				if (doctorExcelVo.getGroups() != null && doctorExcelVo.getGroups().size() > 0) {
					for(String groupName : doctorExcelVo.getGroups()) {
						group.append(groupName).append(",");
					}
					if (group != null && group.length() > 0) {
						group.setLength(group.length() - 1);
					}
					mapValue.put("registerGroup", group.toString());
				}

                mapValue.put("inviter", doctorExcelVo.getInviterName());
				mapValue.put("inviteOpenId", doctorExcelVo.getInviteOpenId());
                mapValue.put("source", doctorExcelVo.getSourceTypeName());
				
				mapValue.put("hasPictureAdvise", doctorExcelVo.getHasPictureAdvise());
				mapValue.put("hasPhoneAdvise", doctorExcelVo.getHasPhoneAdvise());
				mapValue.put("hasCareAdvise", doctorExcelVo.getHasCareAdvise());
				mapValue.put("checkRemark", doctorExcelVo.getCheckRemark());
				mapValue.put("userLevel", UserLevel.getName(doctorExcelVo.getUserLevel()));
				if (Objects.nonNull(doctorExcelVo.getLimitedPeriodTime())) {
                    mapValue.put("limitedPeriodTime", simpleDateFormat.format(doctorExcelVo.getLimitedPeriodTime()));
                }

                mapValue.put("circleNames", doctorExcelVo.getCircleNames());
				mapValue.put("deptName", doctorExcelVo.getDeptNames());

				values.add(mapValue);
			}
		}
		return values;
	}
	
	List<Map<String, Object>> changeDoctorVos2Map(List<DoctorExcelVo> doctorExcelVos, int type) {
		List<Map<String, Object>> values = new ArrayList<>();
		if (doctorExcelVos != null && doctorExcelVos.size() > 0) {
			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				Map<String, Object> mapValue = new HashMap<>();
				mapValue.put("registerTime", doctorExcelVo.getRegisterTimeStr());
				mapValue.put("authTime", doctorExcelVo.getAuthTimeStr());
				mapValue.put("phone", doctorExcelVo.getPhone());
				mapValue.put("name", doctorExcelVo.getName());
//				mapValue.put("id", doctorExcelVo.getId());
				mapValue.put("isFull", doctorExcelVo.getIsFull());
				mapValue.put("hospital", doctorExcelVo.getHospital());
				mapValue.put("department", doctorExcelVo.getDepartment());
				mapValue.put("title", doctorExcelVo.getTitle());
				
				if (type != 3 && null != doctorExcelVo.getPrice()) {
					Double price = Double.valueOf(doctorExcelVo.getPrice()/100);
					mapValue.put("price", price);
				}
				
				StringBuffer inviter = new StringBuffer();
				if (!CollectionUtils.isEmpty(doctorExcelVo.getGroups())) {
					String group = String.join(",", doctorExcelVo.getGroups());
					mapValue.put("registerGroup", group);
				}
				mapValue.put("inviter", doctorExcelVo.getInviterName());
				mapValue.put("source", doctorExcelVo.getSourceTypeName());
				
				mapValue.put("hasPictureAdvise", doctorExcelVo.getHasPictureAdvise());
				mapValue.put("hasPhoneAdvise", doctorExcelVo.getHasPhoneAdvise());
				mapValue.put("hasCareAdvise", doctorExcelVo.getHasCareAdvise());
				mapValue.put("checkRemark", doctorExcelVo.getCheckRemark());
				
				values.add(mapValue);
			}
		}
		return values;
	}
	
	/**
	 * 将激活的患者信息转化为Map
	 * @param patientExcelVos
	 * @return
	 */
	List<Map<String, Object>> changeActivePatientVos2Map(List<PatientExcelVo> patientExcelVos) {
		List<Map<String, Object>> patientValues = new ArrayList<>();
		
		if (patientExcelVos != null && patientExcelVos.size() > 0) {
			for (PatientExcelVo patientExcelVo : patientExcelVos) {
				Map<String, Object> mapValue = new HashMap<>();
				mapValue.put("registerTime", patientExcelVo.getRegisterTimeStr());
				mapValue.put("phone", patientExcelVo.getPhone());
				mapValue.put("name", patientExcelVo.getName());
				mapValue.put("sex", patientExcelVo.getSex());
				mapValue.put("birthday", patientExcelVo.getBirthday());
				mapValue.put("age", patientExcelVo.getAgeStr());
				mapValue.put("address", patientExcelVo.getAddress());
				if (!CollectionUtils.isEmpty(patientExcelVo.getLabers())) {
					String labers = String.join("，", patientExcelVo.getLabers());
					mapValue.put("labers", labers);
				}
				mapValue.put("lastLoginTime", patientExcelVo.getLastLoginTimeStr());
				
				patientValues.add(mapValue);
			}
		}
		return patientValues;
	} 
	
	/**
	 * 将未激活的患者转化为Map
	 * @param patientExcelVos
	 * @return
	 */
	List<Map<String, Object>> changeUnActivePatient2Map(List<PatientExcelVo> patientExcelVos) {
		List<Map<String, Object>> patientValues = new ArrayList<>();
		
		if (patientExcelVos != null && patientExcelVos.size() > 0) {
			for (PatientExcelVo patientExcelVo : patientExcelVos) {
				Map<String, Object> mapValue = new HashMap<>();
				mapValue.put("registerTime", patientExcelVo.getRegisterTimeStr());
				mapValue.put("phone", patientExcelVo.getPhone());
				mapValue.put("inviterName", patientExcelVo.getInviterName());
				mapValue.put("inviterPhone", patientExcelVo.getInviterPhone());
				patientValues.add(mapValue);
			}
		}
		return patientValues;
	}
	
	/**
	 * 将订单信息转化为Map
	 * @param orderExcelVos
	 * @return
	 */
	List<Map<String, Object>> changeAdvise2Map(List<OrderExcelVo> orderExcelVos) {
		List<Map<String, Object>> adviseValues = new ArrayList<>();
		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			for (OrderExcelVo orderExcelVo : orderExcelVos) {
				Map<String, Object> mapValue = new HashMap<>();
				mapValue.put("orderNo", orderExcelVo.getOrderNo());
				mapValue.put("createTime", orderExcelVo.getCreateTimeStr());
				mapValue.put("doctorName", orderExcelVo.getDoctorName());
				mapValue.put("doctorPhone", orderExcelVo.getDoctorPhone());
				mapValue.put("patientName", orderExcelVo.getPatientName());
				mapValue.put("patientPhone", orderExcelVo.getPatientPhone());
				mapValue.put("userName", orderExcelVo.getUserName());
				mapValue.put("hasPay", orderExcelVo.getHasPay());
				mapValue.put("price", orderExcelVo.getPrice());
				mapValue.put("payTime", orderExcelVo.getPayTimeStr());
				mapValue.put("orderStatus", orderExcelVo.getOrderStatusStr());
				mapValue.put("groupName", orderExcelVo.getGroupName());
				
				mapValue.put("picMessageStatus", orderExcelVo.getPicMessageStatus());
				
				adviseValues.add(mapValue);
			}	
		}
		
		return adviseValues;
	}
	
	List<Map<String, Object>> changeAdvise2Map(List<OrderExcelVo> orderExcelVos, Integer packType) {
		if (packType.intValue() == 3) {
			List<Map<String, Object>> adviseValues = new ArrayList<>();
			if (orderExcelVos != null && orderExcelVos.size() > 0) {
				for (OrderExcelVo orderExcelVo : orderExcelVos) {
					Map<String, Object> mapValue = new HashMap<>();
					mapValue.put("orderNo", orderExcelVo.getOrderNo());
					mapValue.put("createTime", orderExcelVo.getCreateTimeStr());
					mapValue.put("doctorName", orderExcelVo.getDoctorName());
					mapValue.put("doctorPhone", orderExcelVo.getDoctorPhone());
					mapValue.put("patientName", orderExcelVo.getPatientName());
					mapValue.put("patientPhone", orderExcelVo.getPatientPhone());
					mapValue.put("userName", orderExcelVo.getUserName());
					mapValue.put("hasPay", orderExcelVo.getHasPay());
					mapValue.put("price", orderExcelVo.getPrice());
					mapValue.put("payTime", orderExcelVo.getPayTimeStr());
					mapValue.put("orderStatus", orderExcelVo.getOrderStatusStr());
					mapValue.put("groupName", orderExcelVo.getGroupName());
					
					mapValue.put("careName", orderExcelVo.getCareName());
					mapValue.put("lastAnswerTime", orderExcelVo.getLastAnswerTimeStr());
					mapValue.put("answerCount", orderExcelVo.getAnswerCount());
					mapValue.put("message", orderExcelVo.getMessage());
					
					List<ItemAnswerInfo> track = orderExcelVo.getTrack();
					if (track != null && track.size() > 0) {
						for (int i = 0; i < track.size(); i++) {
							ItemAnswerInfo info = track.get(i);
							if (info != null) {
								mapValue.put("trackSendTime_" + i, info.getPlanTimeStr());
								mapValue.put("trackAnswerTime_" + i, info.getAnswerTimeStr());
								mapValue.put("trackStatus_" + i, info.getStatusDesc());
							}
						}
					}
					
					List<ItemAnswerInfo> life = orderExcelVo.getLife();
					if (life != null && life.size() > 0) {
						for(int i = 0; i < life.size(); i++) {
							ItemAnswerInfo info = life.get(i);
							if (info != null) {
								mapValue.put("lifeSendTime_" + i, info.getPlanTimeStr());
								mapValue.put("lifeAnswerTime_" + i, info.getAnswerTimeStr());
								mapValue.put("lifeStatus_" + i, info.getStatusDesc());
							}
						}
					}
					
					List<ItemAnswerInfo> survey = orderExcelVo.getSurvey();
					if (survey != null && survey.size() > 0) {
						for (int i = 0; i < survey.size(); i++) {
							ItemAnswerInfo info = survey.get(i);
							if (info != null) {
								mapValue.put("surveySendTime_" + i, info.getPlanTimeStr());
								mapValue.put("surveyAnswerTime_" + i, info.getAnswerTimeStr());
								mapValue.put("surveyStatus_" + i, info.getStatusDesc());
							}
						}

					}
					
					adviseValues.add(mapValue);
				}	
			}
			
			return adviseValues;
		} else {
			return changeAdvise2Map(orderExcelVos);
		}
		
	}
	
	/**
	 * 获取总计信息的Map
	 * @param start 开始时间
	 * @param end 结束时间
	 * @param authDoctorExcelVos 认证的医生
	 * @param unAuthDoctorExcelVos 未认证的医生
	 * @param authFailDoctorExcelVos 认证失败的医生
	 * @param activePatientExcelVos 激活的患者
	 * @param unActivePatientExcelVos 未激活的患者
	 * @param pictureOrderExcelVos 图文咨询列表
	 * @param phoneOrderExcelVos 电话咨询列表
	 * @param careOrderExcelVos 健康关怀列表
	 * @return
	 */
	List<Map<String, Object>> getTotal(
		Date start,
		Date end,
		List<DoctorExcelVo> authDoctorExcelVos,
		List<DoctorExcelVo> unAuthDoctorExcelVos,
		List<DoctorExcelVo> authFailDoctorExcelVos,
		List<PatientExcelVo> activePatientExcelVos,
		List<PatientExcelVo> unActivePatientExcelVos,
		List<OrderExcelVo> pictureOrderExcelVos,
		List<OrderExcelVo> phoneOrderExcelVos,
		List<OrderExcelVo> careOrderExcelVos		
	) {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//先解析日期
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		endCalendar.setTime(end);
		
		List<Map<String, Object>> totalValues = new ArrayList<>();
		
		Map<String, Object> total = new HashMap<>();
		
		total.put("date", "汇总");
		total.put("authedDoctor", authDoctorExcelVos == null ? 0 : authDoctorExcelVos.size());
		total.put("unAuthedDoctor", unAuthDoctorExcelVos == null ? 0 : unAuthDoctorExcelVos.size());
		total.put("authFailDoctor", authFailDoctorExcelVos == null ? 0 : authFailDoctorExcelVos.size());
		total.put("activePatient", activePatientExcelVos == null ? 0 : activePatientExcelVos.size());
		total.put("unActivePatient", unActivePatientExcelVos == null ? 0 : unActivePatientExcelVos.size());
		total.put("pictureAdvise", pictureOrderExcelVos == null ? 0 : pictureOrderExcelVos.size());
		total.put("phoneAdvise", phoneOrderExcelVos == null ? 0 : phoneOrderExcelVos.size());
		total.put("careAdvise", careOrderExcelVos == null ? 0 : careOrderExcelVos.size());
		
		totalValues.add(total);
		
		while(startCalendar.before(endCalendar)) {
			//取startCalendar当天的0点0分0秒
			Date startDate = endCalendar.getTime();
			
			Calendar tempStart = Calendar.getInstance();
			Calendar tempEnd = Calendar.getInstance();
			tempStart.setTime(startDate);
			tempStart.set(Calendar.HOUR_OF_DAY, 0);
			tempStart.set(Calendar.MINUTE, 0);
			tempStart.set(Calendar.SECOND, 0);
			tempEnd.setTime(startDate);
			tempEnd.add(Calendar.DAY_OF_MONTH, 1);
			tempEnd.set(Calendar.HOUR_OF_DAY, 0);
			tempEnd.set(Calendar.MINUTE, 0);
			tempEnd.set(Calendar.SECOND, 0);
			
			long compareStart = tempStart.getTimeInMillis();
			long compareEnd = tempEnd.getTimeInMillis();
			
			Map<String, Object> mapValue = new HashMap<>();
			
			//已认证的医生
			if (authDoctorExcelVos != null && authDoctorExcelVos.size() > 0) {
				int authDoctorCount = 0;
				for(DoctorExcelVo doctorExcelVo : authDoctorExcelVos) {
					if (compareStart <= doctorExcelVo.getAuthTime() && doctorExcelVo.getAuthTime() < compareEnd) {
						authDoctorCount ++;
					}
				}
				mapValue.put("authedDoctor", authDoctorCount);
			} else {
				mapValue.put("authedDoctor", 0);
			}
			//未认证的医生
			if (unAuthDoctorExcelVos != null && unAuthDoctorExcelVos.size() > 0) {
				int auAuthDoctorCount = 0;
				for(DoctorExcelVo doctorExcelVo : unAuthDoctorExcelVos) {
					if (compareStart <= doctorExcelVo.getRegisterTime() && doctorExcelVo.getRegisterTime() < compareEnd) {
						auAuthDoctorCount ++;
					}
				}
				mapValue.put("unAuthedDoctor", auAuthDoctorCount);
			} else {
				mapValue.put("unAuthedDoctor", 0);
			}
			//审核未通过的医生
			if (authFailDoctorExcelVos != null && authFailDoctorExcelVos.size() > 0) {
				int authFailDoctorCount = 0;
				for(DoctorExcelVo doctorExcelVo : authFailDoctorExcelVos) {
					if (compareStart <= doctorExcelVo.getAuthTime() && doctorExcelVo.getAuthTime() < compareEnd) {
						authFailDoctorCount ++;
					}
				}
				mapValue.put("authFailDoctor", authFailDoctorCount);
			} else {
				mapValue.put("authFailDoctor", 0);
			}
			//已激活患者
			if (activePatientExcelVos != null && activePatientExcelVos.size() > 0) {
				int activePatientCount = 0;
				for (PatientExcelVo patientExcelVo : activePatientExcelVos) {
					if (compareStart <= patientExcelVo.getRegisterTime() && patientExcelVo.getRegisterTime() < compareEnd) {
						activePatientCount ++;
					}
				}
				mapValue.put("activePatient", activePatientCount);
			} else {
				mapValue.put("activePatient", 0);
			}
			//未激活患者
			if (unActivePatientExcelVos != null && unActivePatientExcelVos.size() > 0) {
				int unActivePatientCount = 0;
				for (PatientExcelVo patientExcelVo : unActivePatientExcelVos) {
					if (compareStart <= patientExcelVo.getRegisterTime() && patientExcelVo.getRegisterTime() < compareEnd) {
						unActivePatientCount ++;
					}
				}
				mapValue.put("unActivePatient", unActivePatientCount);
			} else {
				mapValue.put("unActivePatient", 0);
			}
			//图文订单
			if (pictureOrderExcelVos != null && pictureOrderExcelVos.size() > 0) {
				int pictureOrderCount = 0;
				for (OrderExcelVo orderExcelVo : pictureOrderExcelVos) {
					if (compareStart <= orderExcelVo.getCreateTime() && orderExcelVo.getCreateTime() <= compareEnd) {
						pictureOrderCount ++;
					}
				}
				mapValue.put("pictureAdvise", pictureOrderCount);
			} else {
				mapValue.put("pictureAdvise", 0);
			}
			//电话订单
			if (phoneOrderExcelVos != null && phoneOrderExcelVos.size() > 0) {
				int phoneOrderCount = 0;
				for (OrderExcelVo orderExcelVo : phoneOrderExcelVos) {
					if (compareStart <= orderExcelVo.getCreateTime() && orderExcelVo.getCreateTime() <= compareEnd) {
						phoneOrderCount ++;
					}
				}
				mapValue.put("phoneAdvise", phoneOrderCount);
			} else {
				mapValue.put("phoneAdvise", 0);
			}
			//健康关怀订单
			if (careOrderExcelVos != null && careOrderExcelVos.size() > 0) {
				int careOrderCount = 0;
				for (OrderExcelVo orderExcelVo : careOrderExcelVos) {
					if (compareStart <= orderExcelVo.getCreateTime() && orderExcelVo.getCreateTime() <= compareEnd) {
						careOrderCount ++;
					}
				}
				mapValue.put("careAdvise", careOrderCount);
			} else {
				mapValue.put("careAdvise", 0);
			}
			totalValues.add(mapValue);
			
			mapValue.put("date", simpleDateFormat.format(tempStart.getTime()) + "~" + simpleDateFormat.format(tempEnd.getTime()));
			endCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		
		return totalValues;
	}
	
}
