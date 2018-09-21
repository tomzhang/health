package com.dachen.health.system.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.param.DoctorNameParam;
import com.dachen.health.system.entity.param.FindDoctorByAuthStatusParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;
import com.dachen.sdk.exception.HttpApiException;

/**
 * ProjectName： health-service<br>
 * ClassName： IDoctorCheckService<br>
 * Description： 医生审核service<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public interface IDoctorCheckService {

    /**
     * </p>获取医生信息列表</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    PageVO getDoctors(DoctorCheckParam param);
    
    /**
     * </p>获取医生信息列表</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    PageVO getDoctorsByName(DoctorNameParam param);

    public PageVO findDoctorByAuthStatus(FindDoctorByAuthStatusParam param);
    
    /**
     * </p>获取医生详细信息</p>
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    DoctorCheckVO getDoctor(Integer doctorId);
    
    /**
     * </p>审核医生通过</p>
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    void checked(DoctorCheckParam param) throws HttpApiException;
    
    /**
     * </p>审核医生为通过</p>
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    void fail(DoctorCheckParam param);
    
    /**
     * </p>当前医生审核状态</p>
     * @param doctorId
     * @return true 正在审核，false 未审核
     * @author fanp
     * @date 2015年7月10日
     */
    boolean checking(Integer doctorId);
    
    /**
     * 反审核一个医生
     * @param userId
     */
    void uncheck(Integer doctorId, Integer checkerId);
    
    /**
     * </p>编辑医生资料</p>
     * @param param
     * @author tanyf
     * @date 2016年5月31日
     */
	void edit(DoctorCheckParam param);


    /**
	 * </p>根据关键字查询医生信息（运营平台批量加入集团）</p>
     * @param keyword 关键字
     * @author 傅永德
     * @date 2016年10月24日
	 * @return
	 */
	PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize);

	void refreshIllegalUser(String[] tel, String pwd,Long time);

    void updateDoctorHospital(HospitalVO oldHospital, HospitalPO newHospital);

    /**
     * 挂起
     *
     * @param userId
     */
    void suspend(Integer userId, Integer adminUserId);

    /**
     * 解除挂起
     *
     * @param userId
     */
    void removeSuspend(Integer userId, Integer adminUserId);
}
