package com.dachen.health.pack.order.mapper;

import java.util.List;

import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.po.CheckIn;
import com.dachen.health.pack.order.entity.vo.CheckInVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： CheckInMapper<br>
 * Description：报到mapper<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public interface CheckInMapper {

    /**
     * </p>添加报到病例</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年9月7日
     */
    void add(CheckIn po);
    
    /**
     * </p>报到列表</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月9日
     */
    List<CheckInVO> list(CheckInParam param);
    
    /**
     * </p>通过id查找患者报到</p>
     * @param id
     * @return
     * @author fanp
     * @date 2015年9月10日
     */
    CheckIn getById(Integer id);
    
    List<CheckIn> getByUserId(Integer userId);
    
    /**
     * </p>医生处理报到，关联订单</p>
     * @param id
     * @param doctorId
     * @author fanp
     * @date 2015年9月10日
     */
    void update(CheckInParam param);
    
    void updateLastConfirmTime(CheckInParam param);
    
    /**
     * 获取新报道个数
     * @param doctorId
     * @return
     */
    public int getNewCheckInCount(Integer doctorId);
    
    
    Integer countByPatientId(Integer patientId);
    
    
    /**
     * 根据相关参数查找对应报报道
     * @param param
     * @return
     */
    public List<CheckInVO> getCheckInByParam(CheckIn po);
    
    public List<CheckInVO> getCheckInByOrderId(Integer orderId);
    
    /**
     * 根据患者的id，查询患者报道
     * @param param
     * @return
     */
    List<CheckInVO> getCheckInByUserIds(CheckInParam param);
    
}