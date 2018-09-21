package com.dachen.health.user.service;

import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.GetRecheckInfo;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.vo.DoctorDealingInfoVO;
import com.dachen.health.user.entity.vo.DoctorRecheckInfoDetailVO;
import com.dachen.health.user.entity.vo.DoctorVO;
import com.dachen.sdk.exception.HttpApiException;


public interface IDoctorService {

    /**
     * </p>获取个人介绍和擅长领域</p>
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月7日
     */
    Map<String,Object> getIntro(Integer userId);
    
    /**
     * </p>设置医生个人介绍</p>
     * @param userId 用户id
     * @param introduction 个人介绍
     * @author fanp
     * @date 2015年7月7日
     */
    void updateIntro(String introduction) throws HttpApiException;
    
    /**
     * </p>设置医生擅长领域</p>
     * @param userId 用户id
     * @param skill 擅长
     * @author fanp
     * @date 2015年7月7日
     */
    void updateSkill(String skill) throws HttpApiException;
    
    /**
     * </p>设置医生学术成就</p>
     * @param userId 用户id
     * @param scholarship 学术成就
     * @author fanp
     * @date 2015年7月7日
     */
    void updateScholarship(String scholarship) throws HttpApiException;
    
    /**
     * </p>设置医生社会任职</p>
     * @param userId 用户id
     * @param experience 社会任职
     * @author fanp
     * @date 2015年7月7日
     */
    void updateExperience(String experience) throws HttpApiException;
    
    /**
     * </p>设置医生就论人数</p>
     * @param userId 用户id
     * @author zhangyin
     * @date 2015年9月28日
     */
    void updateCureNum(Integer dcoID);

    /**
     * </p>获取职业信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月7日
     */
    DoctorVO getWork(Integer userId);
    
    /**
     * </p>修改职业信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月7日
     */
    void updateWork(DoctorParam param) throws HttpApiException;
    
    /**
     * </p>医生获取认证信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月15日
     */
    DoctorVO getCheckInfo(DoctorParam param);
    
    /**
     * </p>医生认证失败后修改认证信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月13日
     */
    void updateCheckInfo(DoctorParam param);
    
    /**
     * 
     * @author 			李淼淼
     * @date 2015年8月18日
     */
    Object search(DoctorParam param);
    
    /**
     * 
     * @author 			李淼淼
     * @date 2015年8月18日
     */
    Object searchs(DoctorParam param);

    /**
     * 
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	void updateExpertise(Integer userId,String[] diseaseTypeId);
	
	 /**
     * 
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	void deleteExpertise(Integer userId,String[] diseaseTypeId);
	
	 /**
     * 
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	Object getExpertise(Integer userId);
	
	/**
	 * 设置免打扰（医生使用）
	 * */
	void updateMsgDisturb(int doctorUserId, String troubleFree);
	
	
	/**
	 * 搜索制定医院下面的医生
	 * @param doctorName
	 * @param hospitalId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
    PageVO researchDoctors(String doctorName,String hospitalId,Integer pageIndex, Integer pageSize );

    /**
     * 获取医生是否有未处理的认证资料修改申请(0:没有;1:有)
     *
     * @param userId
     * @return
     */
    Integer getCheckInfoStatus(int userId);

    /**
     * 已认证的医生是申请修改认证资料(修改)
     *
     * @param param
     */
    void resetCheckInfo(ResetDoctorInfo param);

    /**
     *  已认证的医生是申请修改认证资料(新增)
     * @param param
     */
    void addCheckInfo(ResetDoctorInfo param);

    /**
     * 已认证的医生是申请修改认证资料(获取列表)
     * @param getRecheckInfo
     * @return
     */
    PageVO getAfterCheckInfo(GetRecheckInfo getRecheckInfo);

    /**
     *  已认证的医生是申请修改认证资料(管理员处理结果)
     * @param resetDoctorInfo
     */
    void handleCheckInfo(ResetDoctorInfo resetDoctorInfo);

    DoctorRecheckInfoDetailVO getCheckInfoDetail(String id);

    DoctorDealingInfoVO getDealingInfo();

    Long getUncheckInfoCount();
}
