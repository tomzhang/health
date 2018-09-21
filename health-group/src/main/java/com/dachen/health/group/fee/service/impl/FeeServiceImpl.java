package com.dachen.health.group.fee.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.group.fee.dao.IFeeDao;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeeServiceImpl implements IFeeService{

    @Autowired
    protected IFeeDao feeDao;

    @Autowired
    protected IGroupDoctorService groupDoctorService;
    
    
    /**
     * </p>获取收费设置</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public FeeVO get(String groupId){
        if(StringUtil.isBlank(groupId)){
            throw new ServiceException("集团id为空");
        }
        return feeDao.get(groupId);
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.fee.service.IFeeService#getByDoctorId(java.lang.Integer)
     */
    public FeeVO getByDoctorId(Integer doctorId){
    	//通过登录医生id 查询所属所有集团
    	List<String> groupIds = new ArrayList<String>();
//    	ReqUtil.instance.getUserId();
        List<GroupDoctor> gdocs = groupDoctorService.getByDoctorId(doctorId,GroupType.group.getIndex());
    	for(GroupDoctor gdoc : gdocs){
    		groupIds.add(gdoc.getGroupId());
    	}
    	if(groupIds.size()==0)
    		return null;
    	
    	//查询所有集团的价格设置
    	List<FeeVO> feeVOList = feeDao.getByGroupIds(groupIds);
    	
    	//查询所有集团 中的 最大值 和最小值
    	FeeVO retVO = new FeeVO();
    	for(FeeVO vo : feeVOList){
    		retVO.setCarePlanMax(getGroupMaxFee(retVO.getCarePlanMax(),vo.getCarePlanMax()));
    		retVO.setClinicMax(getGroupMaxFee(retVO.getClinicMax(),vo.getClinicMax()));
    		retVO.setPhoneMax(getGroupMaxFee(retVO.getPhoneMax(),vo.getPhoneMax()));
    		retVO.setTextMax(getGroupMaxFee(retVO.getTextMax(),vo.getTextMax()));
    		
    		retVO.setCarePlanMin(getGroupMinFee(retVO.getCarePlanMin(),vo.getCarePlanMin()));
    		retVO.setClinicMin(getGroupMinFee(retVO.getClinicMin(),vo.getClinicMin()));
    		retVO.setPhoneMin(getGroupMinFee(retVO.getPhoneMin(),vo.getPhoneMin()));
    		retVO.setTextMin(getGroupMinFee(retVO.getTextMin(),vo.getTextMin()));
    		// 预约
    		retVO.setAppointmentMax(getGroupMinFee(retVO.getAppointmentMax(),vo.getAppointmentMax()));
    		retVO.setAppointmentMin(getGroupMinFee(retVO.getAppointmentMin(),vo.getAppointmentMin()));
    	}
    	return retVO;
    }
    
    public FeeVO get(Integer doctorId, String groupId) {
    	FeeVO feeVo = getByDoctorId(doctorId);
		if (StringUtils.isNotBlank(groupId)) {
			if(feeVo == null)
			{
				feeVo=new FeeVO(); 
			}	 
			FeeVO ownGroupFeeVo = get(groupId);
			feeVo.setCarePlanMin(ownGroupFeeVo == null ? 0 : (ownGroupFeeVo.getCarePlanMin()==null?0:ownGroupFeeVo.getCarePlanMin()));
			feeVo.setCarePlanMax(ownGroupFeeVo == null ? 0 : (ownGroupFeeVo.getCarePlanMax()==null?0:ownGroupFeeVo.getCarePlanMax()));
		}
    	return feeVo;
    }
    /**
     * 比较 group中的fee值，返回更大的那个
     * @param retFee
     * @param groupFee
     * @return
     *@author wangqiao
     *@date 2015年12月28日
     */
    private Integer getGroupMaxFee(Integer retFee,Integer groupFee ){
    	//集团有价格上限
    	if(groupFee != null && groupFee > 0){
    		//不存在已有价格，则用集团价格代替
    		if(retFee == null){
    			return groupFee;
    		}else if(retFee >= groupFee){
    			//已有返回值，比集团价格高
    			return retFee;
    		}else{
    			//已有返回值，比集团价格低
    			return groupFee;
    		}
    	}else{
        	//集团没有设置价格上限，直接返回已有价格
        	return retFee;
    	}
    }
    
    /**
     * 比较 group中的fee值，返回更小的那个
     * @param retFee
     * @param groupFee
     * @return
     *@author wangqiao
     *@date 2015年12月28日
     */
    private Integer getGroupMinFee(Integer retFee,Integer groupFee ){
    	//集团有价格下限
    	if(groupFee != null && groupFee >= 0){
    		//不存在已有价格，则用集团价格代替
    		if(retFee == null){
    			return groupFee;
    		}else if(retFee <= groupFee){
    			//已有返回值，比集团价格高
    			return retFee;
    		}else{
    			//已有返回值，比集团价格低
    			return groupFee;
    		}
    	}else{
        	//集团没有设置价格下限，直接返回已有价格
        	return retFee;
    	}
    }
    

    /**
     * </p>收费设置</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public void save(FeeParam param){
        if(StringUtil.isBlank(param.getGroupId())){
            throw new ServiceException("集团id为空");
        }
        feeDao.save(param);
    }
    
    public Boolean checkFeeIsCarePlan(String groupId,String price) {
    	
    	FeeVO feeVO = get(groupId);
    	if (feeVO == null) {
    		feeVO = new FeeVO();
    		feeVO.setCarePlanMin(0);
    		feeVO.setCarePlanMax(0);
    	}
    	try {
    		Integer priceNum = Integer.valueOf(price);
    		if(priceNum<=feeVO.getCarePlanMax() && priceNum>=feeVO.getCarePlanMin()) {
    			return true;
    		}
    		return false;
    	}catch(Exception e) {
    		throw new ServiceException("您输入的价格已超出最大限制!");
    	}
    	
		
    }
    
//    /**
//     * </p>收费设置</p>
//     * 
//     * @param groupId
//     * @return
//     * @author fanp
//     * @date 2015年9月21日
//     */
//    public void saveOrUpdate(FeeParam param){
//        if(StringUtil.isBlank(param.getGroupId())){
//            throw new ServiceException("集团id为空");
//        }
//        FeeVO  feeVo = feeDao.get(param.getI);
//        if (feeVo!=null){
//        	feeDao.save(param);
//        }else{
//        	feeDao.insert
//        }
//    }
}
