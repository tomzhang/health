package com.dachen.health.user.service;

import java.util.List;

import com.dachen.health.user.entity.param.DrugVerifyParam;
import com.dachen.health.user.entity.vo.DoctorDetailVO;
import com.dachen.health.user.entity.vo.DrugVerifyInfo;

public interface IAssistantService {

    DrugVerifyInfo verifyDrug(DrugVerifyParam verifyParam);

    /**
     * </p>获取医助分管医院</p>
     * 
     * @param userId
     * @return 
     * @author fanp
     * @date 2015年7月8日
     */
    List<DoctorDetailVO> getHospitals(Integer userId);
    
    /**
     * </p>获取医助分管医院的医生</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
    List<DoctorDetailVO> getHospitalDoctors(Integer userId,String hospitalId);

}
