package com.dachen.health.controller.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.dachen.health.controller.system.handler.DonwMsgTemplateList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.entity.param.MsgTemplateParam;
import com.dachen.health.base.entity.po.MsgTemplate;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.doctor.IDoctorInfoChangeOperationService;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.repair.service.IDataRepairService;
import com.dachen.util.GeneralExcelUtil;

@RestController
@RequestMapping("/dataRepair")
public class DataRepairController {

	private static Logger logger = LoggerFactory.getLogger(DataRepairController.class);

	@Autowired
	JedisTemplate jedisTemplate;

	@Autowired
	private IDataRepairService dataRepairService;

	@Autowired
	private IBaseDataService baseDataService;

	@Autowired
	private IDoctorInfoChangeOperationService doctorInfoChangeOperationService;

	@RequestMapping("/repairDoctorStatus")
	public int repairDoctorStatus() {
		return dataRepairService.repairDoctorStatus();
	}

	@RequestMapping("/syncDoctorHospital")
	public void syncDoctorHospital() {
		dataRepairService.syncDoctorHospital();
	}

	@RequestMapping("/deleteUserData")
	public JSONMessage deleteUserData() {
		dataRepairService.deleteUserData();
		return JSONMessage.success();
	}
	/**
	 * 清除user的redis缓存（UserSession）
	 * @return
	 */
	@RequestMapping("/cleanUserCache")
	public JSONMessage cleanUserCache() {
		 String cursor = "0";
         ScanParams scanParams = new ScanParams().count(5000);
         do {
             ScanResult<String> scanResult = jedisTemplate.scan(cursor, scanParams);
             cursor = scanResult.getStringCursor();
             Set<String>userList = new HashSet<String>();
             for (String key : scanResult.getResult()) {
            	 if(key.startsWith("user:")){
            		 logger.info("delete user,key={}",key);
            		 userList.add(key);
            	 }
             }
             if(userList.size()>0)
             {
            	 String[]users = userList.toArray(new String[userList.size()]);
            	 jedisTemplate.del(users);
             }
         } while (!StringUtils.equals("0", cursor));
		return JSONMessage.success();
	}

	
	
	/**
	 * 修复收入数据（把收入相关的数据先删除，然后重新计算，没有了提现与支出项）
	 * 
	 * timeStart 待修复数据开始时间
	 * 
	 * 按月调用自动转存服务
	 * 
	 * 按月查询 对应时间区间订单金额在于0，且状态是已完成的按时间升序的所有订单
	 * 然后遍历所有订单的，去调用收入服务。
	 * 
	 * 重复上述操作直到当前月份
	 * 
	 */
	@RequestMapping("/fixedIncomes")
	public JSONMessage fixedIncomes(Long timeStart){
		Long timeNow = resetTime(System.currentTimeMillis(), 0, 2, 0, 0, 0);
		timeStart = resetTime(timeStart, 0, 1, 0, 0, 0);
		
		dataRepairService.delIncomesData();
		
		while(timeStart <= timeNow){
			dataRepairService.repairIncomes(timeStart);
			timeStart = resetTime(timeStart, 1, 1, 0, 0, 0);
		}
		return JSONMessage.success();
	}

	@RequestMapping("/closeAndDelteAssistantSession")
	public JSONMessage closeAndDelteAssistantSession(){
		doctorInfoChangeOperationService.closeAndDeleteOldSession();
		return JSONMessage.success();
	}
	
	private static Long resetTime(Long sourceTime,int addMonth,int day,int hour,int minute,int second){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sourceTime);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND,second);
        
        calendar.add(Calendar.MONTH, addMonth);
		return calendar.getTime().getTime();
	}
	
	/**
	 * 下载对应短信模板列表
	 * @param param
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("downMsgTemplateList")
	public String donwExcelMsgTemplateList(MsgTemplateParam param,
			HttpServletResponse response) throws IOException{
		List<MsgTemplate> list = baseDataService.queryMsgTemplate(param);
		if(list == null || list.size() == 0){
			return "没有找到下载数据";
		}
		String filenName = "短信模板";
		String[] keys = {"_id","category","content","paraNum","usage"};
		String[] coloumes = {"_id","category","content","paraNum","usage"};
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DonwMsgTemplateList dOrder = new DonwMsgTemplateList(keys, coloumes, list);
		dOrder.createWorkBook(os);
		byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="+ new String((filenName+".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(out);
        
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        if (bis != null)
            bis.close();
        if (bos != null)
            bos.close();
		return null;
	}
}
