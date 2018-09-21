package com.dachen.health.pack.income.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.pack.income.entity.param.IncomeParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailVO;
import com.dachen.health.pack.income.mapper.IncomeDetailMapper;
import com.dachen.health.pack.income.service.IIncomeDetailService;
import com.dachen.util.DateUtil;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeServiceImpl<br>
 * Description： 医生收入service实现类<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
@Service
public class IncomeDetailMonthServiceImpl implements IIncomeDetailService {

    @Autowired
    private IncomeDetailMapper incomeDetailMapper;

    @Autowired
    private IBaseUserService baseUserService;

    /**
     * </p>获取医生收入明细</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    public List<IncomeDetailVO> getIncomeDetail(IncomeParam param) {

        if(param.getMonth() != null){
            //转换时间查询范围
            Date date = DateUtil.intToDate(param.getMonth());
            if(date == null){
                throw new ServiceException("请选择正确月份");
            }
            param.setStartTime(DateUtil.getFirstDayOfMonth(date).getTime());
            param.setEndTime(DateUtil.getLastDayOfMonth(date).getTime());
        }
        
        
        List<IncomeDetailVO> list = incomeDetailMapper.getIncomeDetails(param);

        if (list.size() > 0) {
            Integer[] ids = new Integer[list.size()];
            for (int i = 0, j = list.size(); i < j; i++) {
                ids[i] = list.get(i).getUserId();
            }

            // 获取用户信息
            List<BaseUserVO> userList = baseUserService.getByIds(ids);

            for (IncomeDetailVO vo : list) {
                int place = this.binSearch(userList, vo.getUserId());
                if(place>-1){
                    vo.setUserName(userList.get(place).getName());
                    vo.setHeadPicFileName(userList.get(place).getHeadPicFileName());
                }
                vo.setUserId(null);
                vo.setStatus(null);
            }
        }

        return list;
    }

    // 二分法查找id所在位置，list经过排序
    private int binSearch(List<BaseUserVO> userList, int userId) {
        int mid = userList.size() / 2;

        int start = 0;
        int end = userList.size() - 1;
        while (start <= end) {
            mid = (end - start) / 2 + start;
            Integer id = userList.get(mid).getUserId();
            if (userId == id) {
                return mid;
            } else if (userId <= id) {
                end = mid - 1;
            } else if (userId >= id) {
                start = mid + 1;
            }
        }
        return -1;
    }

}
