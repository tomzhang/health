package com.dachen.health.commons.example;

import com.dachen.health.commons.vo.Farm;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.commons.vo.WeChatUserInfo;
import com.dachen.health.user.entity.po.Assistant;
import com.dachen.health.user.entity.po.Doctor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserExample extends BaseExample {

	private Long birthday;
	private Integer companyId;
	private String description;
	private String idcard;
	private String idtype;
	

	private String idcardUrl;
	private String name;
	private String nickname;
	private String password;
	private Integer sex;
	private String telephone;
	private Integer userType;
	
	private String headPicFileName;

	private Integer d = 0;
	private Integer w = 0;
	private String email;
	
	private Doctor doctor;
	
	private Assistant assistant;
	
	private Integer age;
	
	private String area;
	
	/**
	 * 登录时，选择登录哪个医生集团
	 */
	private String loginGroupId;
	
	private String enterpriseId;
	
	private String enterpriseName;
	
	private String remarks;
	
	private Integer status;
	//用户区分企业用户后台导入注册和 app的激活
	private Integer isEnterpriseImport = new Integer(0);
	
	private Boolean needResetPassword = new Boolean(false);
	
	private UserSource userSource;
	
	private WeChatUserInfo weUserInfo;
	
	private boolean isAddUser;//是否为新增用户;区别新增用户和激活用户
	
	private String accountType;
	
	/**
	 * 身高
	 */
	private String height;
	/**
	 * 体重
	 */
	private String weight;
	/**
	 * 婚姻
	 */
	private String marriage;
	/**
	 * 职业
	 */
	private String professional;
	
	private Farm farm;
	
	private String doctorNum;

	/* 参加工作时间 */
	private Long workTime;

}
