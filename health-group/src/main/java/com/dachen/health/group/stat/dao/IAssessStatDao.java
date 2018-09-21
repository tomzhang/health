package com.dachen.health.group.stat.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.mongodb.DBCollection;

/**
 * ProjectName： health-group<br>
 * ClassName： IAssessStatDao<br>
 * Description： 考核统计dao<br>
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface IAssessStatDao {

    /**
     * </p>统计集团邀请医生数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO inviteDoctorByGroup(StatParam param);
    
    /**
     * </p>统计医生邀请医生数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO inviteDoctorByDoctor(StatParam param);
    
    /**
     * </p>统计集团添加患者数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO addPatientByGroup(StatParam param);
    
    /**
     * </p>统计集团医生添加患者数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO addPatientByDoctor(StatParam param);
    
    /**
     * 返回一个DBCollection
     * @param collection
     * @return
     */
    DBCollection getDBCollection(String collectionName);
    
}
