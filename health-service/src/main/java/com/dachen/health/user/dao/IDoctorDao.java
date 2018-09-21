package com.dachen.health.user.dao;

import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.GetRecheckInfo;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.po.DoctorCheckInfoChange;
import com.dachen.health.user.entity.vo.DoctorVO;


public interface IDoctorDao {

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
     * @param skill 擅长
     * @author fanp
     * @date 2015年7月7日
     */
    void updateIntro(Integer userId,String introduction);
	
    /**
     * </p>设置医生擅长领域</p>
     * @param userId 用户id
     * @param skill 擅长
     * @author fanp
     * @date 2015年7月7日
     */
    void updateSkill(Integer userId,String skill);
    
    /**
     * </p>设置医生学术成就</p>
     * @param userId 用户id
     * @param scholarship 学术成就
     */
    void updateScholarship(Integer userId,String scholarship);
    
    /**
     * </p>设置医生社会任职</p>
     * @param userId 用户id
     * @param experience 社会任职
     */
    void updateExperience(Integer userId,String experience);
    
    /**
     * </p>设置医生就诊人数</p>
     * @param userId 用户id
     * @param cureNum 就诊人数
     * @author zhangyin
     * @date 2015年9月28日
     */
    void updateCureNum(Integer userId,Integer cureNum);
    
    /**
     * </p>获取职业信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月7日
     */
    DoctorVO getWork(Integer userId);
    
    /**
     * </p>修改医生职业信息，未认证是修改认证信息也是修改职业信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月7日
     */
    void updateWork(DoctorParam param);
    
    /**
     * </p>医生获取认证信息</p>
     * @param param
     * @author fanp
     * @date 2015年7月15日
     */
    DoctorVO getCheckInfo(DoctorParam param);
    
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
     * 更新专长
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	void updateExpertise(Integer userId,String[] diseaseTypeId);

	  /**
     * 删除专长
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	void deleteExpertise(Integer userId, String[] diseaseTypeId);
	
	 /**
     * 删除专长
     * @author 			李淼淼
     * @date 2015年9月22日
     */
	Object getExpertise(Integer userId);
	
	/**
	 * 设置免打扰
	 * 
	 * @param userIdByDoctor
	 * @param isOpenMsg
	 */
	void updateMsgDisturb(int userIdByDoctor, String troubleFree);
	public Map<String,Object> researchDoctors(String doctorName, String hospitalId,
			int pageIndex, int pageSize);

    Integer getCheckInfoStatus(int userId);

    void resetCheckInfo(ResetDoctorInfo param);

    void addCheckInfo(ResetDoctorInfo param);

    PageVO getAfterCheckInfo(GetRecheckInfo getRecheckInfo);

    DoctorCheckInfoChange getCheckInfoDetail(String id);

    void handleCheckInfo(ResetDoctorInfo resetDoctorInfo);

    User updateUserCheckInfo(ResetDoctorInfo resetDoctorInfo);

    DoctorCheckInfoChange getDealingInfo(int userId, int infoStatus);

    Long getUncheckInfoCount();
}
