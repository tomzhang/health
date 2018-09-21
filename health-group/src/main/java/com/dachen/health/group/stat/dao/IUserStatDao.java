package com.dachen.health.group.stat.dao;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;

/**
 * ProjectName： health-group<br>
 * ClassName： IUserStatDao<br>
 * Description： 用户统计dao<br>
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface IUserStatDao {

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
    
    
    PageVO statDoctor(StatParam param);
    
    /**
     * </p>统计医生的患者</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO statPatientByDoctor(StatParam param,List<Integer> doctorIds);
    
}
