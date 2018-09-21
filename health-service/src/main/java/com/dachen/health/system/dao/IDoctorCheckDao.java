package com.dachen.health.system.dao;

import java.util.List;

import org.mongodb.morphia.query.Query;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.param.DoctorNameParam;
import com.dachen.health.system.entity.param.FindDoctorByAuthStatusParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;

/**
 * ProjectName： health-service<br>
 * ClassName： IDoctorCheckDao<br>
 * Description：医生审核dao <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public interface IDoctorCheckDao {

    /**
     * </p>获取所有医生</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    PageVO getDoctors(DoctorCheckParam param);
    
    /**
     * </p>获取所有医生</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    PageVO getDoctorsByName(DoctorNameParam param);

    Query<User> findDoctorByAuthStatus(FindDoctorByAuthStatusParam param);

    
    /**
     * </p>获取医生详细信息</p>
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    DoctorCheckVO getDoctor(Integer doctorId);
    
    /**
     * </p>获取医生状态</p>
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    Integer getStatus(Integer doctorId);
    
    /**
     * </p>审核医生通过</p>
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    void checked(DoctorCheckParam param);
    
    /**
     * </p>审核医生为失败</p>
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    void fail(DoctorCheckParam param);
    
    /**
     * </p>设置执业区域</p>
     * @param hospitalId
     * @param userId
     * @author fanp
     * @date 2015年9月16日
     */
    void setWorkArea(String userId,String hospitalId);
    
    /**
     * </p>审核医生通过</p>
     * @param param
     * @author fanp
     * @date 2015年12月28
     */
	void checkedNurse(DoctorCheckParam param);
	
	/**
     * </p>审核护士失败</p>
     * @param param
     * @author fanp
     * @date 2015年12月28
     */
	void failNurse(DoctorCheckParam param);
	
	/**
     * </p>编辑医生</p>
     * @param param
     * @author 谭永芳
     * @date 2016年5月31日
     */
    void edit(DoctorCheckParam param);
    
    /**
     * </p>编辑护士</p>
     * @param param
     * @author 谭永芳
     * @date 2016年5月31日
     */
    void editNurse(DoctorCheckParam param);
    
    List<String> getMyGroupIds(Integer userId);

}
