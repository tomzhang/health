package com.dachen.health.group.stat.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.stat.dao.IUserStatDao;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;
import com.dachen.health.group.stat.service.IUserStatService;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-group<br>
 * ClassName： UserStatServiceImpl<br>
 * Description： 用户统计service实现类<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
@Service
public class UserStatServiceImpl implements IUserStatService {

    @Autowired
    protected IUserStatDao userStatDao;

    @Autowired
    protected IBaseUserService baseUserService;

    @Autowired
    protected IDepartmentService departmentService;

    @Autowired
    protected IDepartmentDoctorDao dpartmentDoctorDao;
    
    /**
     * </p>统计医生职称</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statTitle(StatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        return userStatDao.statTitle(param);
    }

    /**
     * </p>统计医生分布区域</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statDoctorArea(StatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        return userStatDao.statDoctorArea(param);
    }

    /**
     * </p>统计医生分布病种</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statDoctorDisease(StatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        return userStatDao.statDoctorDisease(param);
    }
    
    @Override
    public PageVO statDoctor(StatParam param) {
    	
		if (StringUtil.isBlank(param.getGroupId())) {
			throw new ServiceException("集团id为空");
		}
    	
    	return userStatDao.statDoctor(param);
    }

    /**
     * </p>统计患者,统计维度（1：集团；2：组织机构、3：医生、4：科室、5：病种）</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO statPatient(StatParam param, String id) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        if (param.getType() == null) {
            return null;
        }
        if (param.getType() != 1 && StringUtil.isBlank(id)) {
            return null;
        }

        if (param.getType() == 1 || param.getType() == 2 || param.getType() == 3) {
            // 按医生统计
            List<Integer> doctorIds = this.getDoctorId(param, id);
            return userStatDao.statPatientByDoctor(param, doctorIds);
        } 
        return null;
    }

    /**
     * </p>根据不同维度获取医生id（集团、组织架构、医生）</p>
     * 
     * @param param
     * @param id
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<Integer> getDoctorId(StatParam param, String id) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }

        String[] statuses = new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex(), GroupEnum.GroupDoctorStatus.离职.getIndex(), GroupEnum.GroupDoctorStatus.踢出.getIndex() };

        List<Integer> doctorIds = new ArrayList<Integer>();
        if (param.getType() == 1) {
            // 查找集团下所有医生
            doctorIds = baseUserService.getDoctorIdByGroup(param.getGroupId(), statuses);
        } else if (param.getType() == 2 && StringUtil.isNotBlank(id)) {
            // 查询科室及科室下所有医生
            List<String> departmentIds = null;

            // 查询子科室
            departmentIds = departmentService.getSubDepartment(param.getGroupId(), id);
            departmentIds.add(id);

            doctorIds = dpartmentDoctorDao.getDepartmentDoctorId(param.getGroupId(), departmentIds, statuses);

        } else if (param.getType() == 3 && StringUtil.isNotBlank(id)) {
            // 直接查询医生的患者
            doctorIds.add(Integer.parseInt(id));

        }
        return doctorIds;
    }

}
