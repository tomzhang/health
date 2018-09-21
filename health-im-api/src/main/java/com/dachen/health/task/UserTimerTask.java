package com.dachen.health.task;

import java.util.List;

import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;
import com.dachen.health.system.service.IDoctorCheckService;
import com.dachen.line.stat.util.Constant;
import com.dachen.util.ReqUtil;

@Component("userTimerTask")
public class UserTimerTask {

	private static Logger logger = LoggerFactory.getLogger(UserTimerTask.class);
	
	public static boolean execute = false;
	
	@Autowired
    UserManager userManager;
	
	@Autowired
    IGroupDoctorService groupDoctorService;
	
	@Autowired 
    private IDoctorCheckService doctorCheckService;
	
	/**
	 * 自动审核
	 */
	public void autoCheck() {
		logger.info("------>auto check doctor start, execute is {}", execute);
		if (execute) {
			DoctorCheckParam param = new DoctorCheckParam();
			param.setStatus(2);
			param.setPageIndex(0);
			//暂时设置为200
			param.setPageSize(200);
			PageVO pageVO = doctorCheckService.getDoctors(param);
			
			List<DoctorCheckVO> doctorCheckVOs = (List<DoctorCheckVO>) pageVO.getPageData();
			
			if (doctorCheckVOs != null && doctorCheckVOs.size() > 0) {
				
				for (DoctorCheckVO doctor : doctorCheckVOs) {

					try {
						DoctorCheckParam doctorCheckParam = new DoctorCheckParam();
						
						Integer doctorId = doctor.getUserId();
						
						DoctorCheckVO doctorCheckVO = doctorCheckService.getDoctor(doctorId);
						
						doctorCheckParam.setUserId(doctorCheckVO.getUserId());
						doctorCheckParam.setTitle(doctorCheckVO.getTitle());
						doctorCheckParam.setHospitalId(doctorCheckVO.getHospitalId());
						doctorCheckParam.setHospital(doctorCheckVO.getHospital());
						doctorCheckParam.setDeptId(doctorCheckVO.getDeptId());
						doctorCheckParam.setDepartments(doctorCheckVO.getDepartments());
						doctorCheckParam.setRole(1);
						
						doctorCheckParam.setCheckTime(System.currentTimeMillis());
				        Integer userId = ReqUtil.instance.getUserId();
				        User admin = null;
				        try {			        
				        	admin = userManager.getUser(userId);
				        } catch (Exception e) {
				        	admin = null;
				        }
				    
				        doctorCheckParam.setChecker(admin == null ? "" : admin.getName());
				        doctorCheckParam.setCheckerId(admin == null ? 0 : admin.getUserId());
				        doctorCheckParam.setNurseShortLinkUrl(APP_NURSE_CLIENT_LINK());
				        doctorCheckService.checked(doctorCheckParam);
				        /**
				         * 医生审核通过时候判断是否需要集团对应的集团
				         */
				        if(doctorCheckParam.getUserType() != null && doctorCheckParam.getUserType() == UserType.doctor.getIndex()){
				        	groupDoctorService.activeAllUserGroup(doctorCheckParam.getUserId());
				        }
					} catch (Exception e) {
						logger.error("------>auto check doctor error, doctor id is {}, exception message is {}", doctor.getUserId(), e.getMessage());
						continue;
					}
				}
			}
		}
		
		logger.info("------>auto check doctor end");
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	public String APP_NURSE_CLIENT_LINK () throws HttpApiException {
		String nurseLink = PropertiesUtil
				.getContextProperty("app.nurse.client.link");

		String shorUrl = shortUrlComponent.generateShortUrl(nurseLink);
		return shorUrl;
	}
}
