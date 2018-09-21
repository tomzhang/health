package com.dachen.health.group.fee.dao;

import java.util.List;

import com.dachen.commons.support.nosql.INoSqlRepository;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;

/**
 * ProjectName： health-group<br>
 * ClassName： FeeDao<br>
 * Description：医生集团收费设置 <br>
 * 
 * @author fanp
 * @createTime 2015年9月21日
 * @version 1.0.0
 */
public interface IFeeDao extends INoSqlRepository {

    /**
     * </p>获取收费设置</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    FeeVO get(String groupId);

    /**
     * </p>收费设置</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    void save(FeeParam param);
    
    /**
     * 通过doctorId 查询多个集团收费 列表
     * @param groupId
     * @return
     *@author wangqiao
     *@date 2015年12月28日
     */
    public List<FeeVO> getByGroupIds(List<String> groupIds );
}
