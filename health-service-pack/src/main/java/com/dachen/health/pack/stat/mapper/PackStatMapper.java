package com.dachen.health.pack.stat.mapper;

import java.util.List;

import com.dachen.health.pack.stat.entity.param.PackStatParam;
import com.dachen.health.pack.stat.entity.vo.PackStatVO;

/**
 * ProjectName： health-group<br>
 * ClassName： PackStatMapper<br>
 * Description： 统计dao<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface PackStatMapper {

    /**
     * </p>统计集团订单金额数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    List<PackStatVO> orderMoneyByGroup(PackStatParam param);
    /**
     * </p>统计集团订单金额数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    List<PackStatVO> orderMoneyByGroupList(PackStatParam param);

    /**
     * </p>统计集团订单总数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月18日
     */
    long orderMoneyByGroupCount(PackStatParam param);

    /**
     * </p>统计医生订单金额数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月18日
     */
    List<PackStatVO> orderMoneyByDoctor(PackStatParam param);

    /**
     * </p>统计医生订单总数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月18日
     */
    long orderMoneyByDoctorCount(PackStatParam param);

}
