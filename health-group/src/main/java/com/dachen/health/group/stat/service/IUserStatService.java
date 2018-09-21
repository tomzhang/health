package com.dachen.health.group.stat.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;

/**
 * ProjectName： health-group<br>
 * ClassName： IUserStatService<br>
 * Description： 用户统计service<br>
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface IUserStatService {

    /**
     * </p>统计医生职称</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    List<StatVO> statTitle(StatParam param);
    
    /**
     * </p>统计医生分布区域</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    List<StatVO> statDoctorArea(StatParam param);
    
    /**
     * </p>统计医生分布病种</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    List<StatVO> statDoctorDisease(StatParam param);
    
    /**
     * 统计医生，统计维度（1：病种、2：职称、3：区域）
     * @param groupId
     * @param type
     * @param typeId
     * @return
     */
    PageVO statDoctor(StatParam param);
    
    /**
     * </p>统计患者，统计维度（1：集团、2：组织机构、3：医生、4：病种）</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO statPatient(StatParam param,String id);
    
    /**
     * </p>根据不同维度获取医生id（集团、组织架构、医生、病种）</p>
     * @param param
     * @param id （集团、组织架构、医生、病种id）
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<Integer> getDoctorId(StatParam param,String id);
}
