package com.dachen.health.system.entity.vo;

import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.user.entity.po.LearningExperience;
import com.dachen.health.user.entity.po.NurseImage;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class DoctorCheckVO {

    private String openId;

    private Integer userId;

    private String name;

    /*医生学币数*/
    private Integer integral;

    /* 手机 */
    private String telephone;

    /* 创建时间 */
    private String createTime;
    
    /*头像*/
    private String headPicFileName;

    /* 医生上传证书 */
    private String certificateOne;

    /* 医生上传证书 */
    private String certificateTwo;

    /* 医生上传证书 */
    private String certificateThree;

    /* 审核状态 */
    private Integer status;

    /* 是否挂起 */
    private Integer suspend;

    /* 封号原因 */
    private String disableReason;

    /* 封号时间 */
    private Long disableTime;

    /* 人身验证状态 */
    private Integer faceRec;

    /* 人身验证头像 */
    private String faceRecImage;

    /* 所属医院 */
    private String hospital;
    
    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;
    
    /* 所属科室 */
    private String deptId;

    /* 所属科室电话 */
    private String deptPhone;

    /* 职称 */
    private String title;

    /* 医生号 */
    private String doctorNum;
    
    /* 护士号 */
    private String nurseNum;

    private String licenseNum;

    private String licenseExpire;

    private Integer checkerId;

    private String checker;

    private String remark;

    private String checkTime;
    
    private Integer userType;
    
    private List<NurseImage> nurseImageList;
    
    /**邀请人的id**/
    private Integer inviterId;
    /**邀请人的姓名**/
    private String inviterName;
    /**来源类型**/
    private Integer sourceType;
    /**注册来源**/
    private String source;
    /**注册来源集团的ID**/
    private String sourceGroupId;
    
    private Integer role;
    
    private String orderTime;
    
    //医生助手id
    private Integer assistantId;
    //医生助手姓名
    private String assistantName;
    
    //性别
    private Integer sex;// 性别1男，2女 3 保密
    
    private String skill;
    
    //学术成就
    private String scholarship;
    
    //社会任职
    private String experience;
    
    private String introduction;
    
    private DiseaseType[] Expertises;
    
    private String QRUrl;//医生二维码

    private List<String> groupNames; //集团名称
    
    private Map<String, String[]> circleNames;//圈子或科室

    private String certTime; //认证时间

    private String modifyTime; //修改时间

    private Set<String> services;//医生开启的服务
    
    private String limitedPeriodTime;

    /**
     * 来源子系统（医生圈-17、药企圈-16）
     * @see com.dachen.health.commons.constants.UserEnum.Source
     */
    private Integer subsystem;

    /* 身份证号 */
    private String IDNum;

    /* 参加工作时间 */
    private Long workTime;

    /* 生日 */
    private Long birthday;

    /* 学习经历 */
    private List<LearningExperience> learningExpList;

    /**
     * 用户级别 0到期 1游客 2临时用户 3认证用户
     */
    private Integer userLevel;


}
