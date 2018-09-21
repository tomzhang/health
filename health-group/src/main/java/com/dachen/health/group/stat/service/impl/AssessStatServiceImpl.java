package com.dachen.health.group.stat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.group.stat.dao.IAssessStatDao;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.service.IAssessStatService;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-group<br>
 * ClassName： AssessStatServiceImpl<br>
 * Description： 考核统计service实现类<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
@Service
public class AssessStatServiceImpl implements IAssessStatService {

    @Autowired
    protected IAssessStatDao assessStatDao;

    /**
     * </p>统计邀请医生数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO inviteDoctor(StatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        String[] statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex()};
        if (!param.isShowOnJob()){
        	statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex(), 
                    GroupEnum.GroupDoctorStatus.离职.getIndex(), GroupEnum.GroupDoctorStatus.踢出.getIndex()};
        }
        param.setStatuses(statuses);
        if (param.getDoctorId() == null) {
            // 按集团查询
            return assessStatDao.inviteDoctorByGroup(param);
        } else {
            // 按医生查询
            return assessStatDao.inviteDoctorByDoctor(param);
        }

    }

    /**
     * </p>统计添加患者数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO addPatient(StatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        String[] statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex()};
        if (!param.isShowOnJob()){
        	statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex(), 
                    GroupEnum.GroupDoctorStatus.离职.getIndex(), GroupEnum.GroupDoctorStatus.踢出.getIndex()};
        }
        param.setStatuses(statuses);
        if (param.getDoctorId() == null) {
            // 按集团查询
            return assessStatDao.addPatientByGroup(param);
        } else {
            // 按医生查询
            return assessStatDao.addPatientByDoctor(param);
        }
    }

}
