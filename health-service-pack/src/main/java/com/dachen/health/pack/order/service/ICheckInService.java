package com.dachen.health.pack.order.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.po.Case;
import com.dachen.health.pack.order.entity.vo.CheckInVO;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.sdk.exception.HttpApiException;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： CheckInService<br>
 * Description： 报到service<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public interface ICheckInService {

    /**
     * </p>添加报到信息</p>
     * 
     * @param param
     * @return 报到id
     * @author fanp
     * @date 2015年9月7日
     */
    int add(CheckInParam param) throws HttpApiException;
    
    List<DiseaseTypeVO> getRecommendDisease(Integer docId);
    
    /**
     * 获取当前医生患者报道赠送服务的开通状况
     * @param param
     * @return
     */
    Integer getCheckInStatus(CheckInParam param);
    
    /**
     * 根据开通情况返回赠送次数
     * 默认开通送一次，没开通就零次
     */
    Integer getCheckInGiveTimes(Integer docId);
    
    /**
     * </p>报到列表</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月9日
     */
    List<CheckInVO> list(CheckInParam param);

    /**
     * </p>查看订单详情</p>
     * 
     * @param orderId
     * @return
     * @author fanp
     * @date 2015年9月8日
     */
    CheckInVO detail(CheckInParam param);
    
    /**
     * </p>修改报到状态</p>
     * @param param
     * @return IM会话组ID
     * @author fanp
     * @date 2015年9月10日
     */
    String updateStatus(CheckInParam param) throws HttpApiException;
    
    /**
     * 获取新报道的个数
     * @param doctorId
     * @return
     */
    public int getNewCheckInCount(Integer doctorId);
    
    /**
     * 修改病例
     * @param doctorId
     * @return
     */
    void updateCase(Case ca);
    
    /**
     * 是否已报到
     * @param doctorId
     * @param userId
     * @return
     */
    public boolean isCheckIn(Integer doctorId, Integer userId);
    
    
    List<Patient> getPatientsWithStatusByDocAndCreater(Integer docId,Integer creator);
    
    Map<String,Object> updateCheckInGiveStatus(Integer docId, Integer status);

}
