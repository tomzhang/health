package com.dachen.health.system.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.user.entity.param.LearningExperienceParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorCheckParam<br>
 * Description：集团成员下的医生审核参数实体 <br>
 * 
 * @author tanyf
 * @crateTime 2016年6月2日
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DoctorCheckParam extends PageVO {

    private Integer userId;

    /* 姓名 */
    private String name;

    /* 开始时间 */
    private Long startTime;

    /* 结束时间 */
    private Long endTime;

    /* 是否审核 */
    private Integer status;

    /* 是否挂起或禁用 */
    private Integer suspend;

    /* 所属医院 */
    private String hospital;

    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;

    /* 所属科室电话 */
    private String deptPhone;
    
    /* 所属科室Id */
    private String deptId;

    /* 职称 */
    private String title;

    /* 证书编号 */
    private String licenseNum;

    /* 证书到期日期 */
    private String licenseExpire;

    /* 审核时间 */
    private Long checkTime;

    /* 审核人员 */
    private String checker;

    /* 审核人员 */
    private Integer checkerId;

    /* 审核意见 */
    private String remark;
    
    /* 用户类型 */
    private Integer userType;
    
    /*护士短连接*/
    private String nurseShortLinkUrl;
    
    //医生护士角色
    private  Integer role;
    
    //医生助手id
    private Integer assistantId;
    
    private String enentType;//时间类型
    /*头像*/
    private String headPicFileName;
    
    /** 医生ID 列表  add by tanyf */
    private List<Integer> doctorIds;
    
    private Integer forceQuitApp;// 编辑页面使用 1:表示强制退出APP [+] add by tanyf 20160603
    
    private Integer sex;
    
    private String introduction; //医生介绍
    
    private String[] expertises;//医生擅长
    
    private String skill;//擅长补充
    
    private String scholarship;//学术成就
    
    private String experience;//社会任职
    
	private Boolean sendSMS; // 是否发送短信通知
	
	private Integer userLevel;//用户级别
    
	private Long limitedPeriodTime; // 有效期限
	
    /* 下拉框查询参数 */
    private String qDoctorName;

    private String qTelephone;

    private String qHospital;

    private String qInviteName;

    private String qTitle;

    /* 身份证号 */
    private String IDNum;

    /* 参加工作时间 */
    private Long workTime;

    /* 生日 */
    private Long birthday;

    /* 学习经历 前端传序列化JSON */
    private String learningExp;

}
