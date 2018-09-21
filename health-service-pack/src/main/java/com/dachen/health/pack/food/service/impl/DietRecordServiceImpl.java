package com.dachen.health.pack.food.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.food.dao.DietRecordDao;
import com.dachen.health.pack.food.entity.po.DietRecord;
import com.dachen.health.pack.food.service.DietRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * Created by fuyongde on 2017/2/23.
 */
@Service
public class DietRecordServiceImpl implements DietRecordService {

    @Autowired
    private DietRecordDao dietRecordDao;

    @Override
    public void save(String foodName, String reactions, Long dietTime, Integer patientId) {
        if (StringUtils.isNotBlank(foodName) && foodName.length() > 80) {
            throw new ServiceException("食物名称过长");
        }
        if (StringUtils.isNotBlank(reactions) && reactions.length() > 400) {
            throw new ServiceException("不良反应过长");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String diteDate = simpleDateFormat.format(dietTime);

        DietRecord dietRecord = new DietRecord();
        dietRecord.setFoodName(foodName);
        dietRecord.setReactions(reactions);
        dietRecord.setDietTime(dietTime);
        dietRecord.setPatientId(patientId);
        dietRecord.setDietDate(diteDate);

        dietRecordDao.save(dietRecord);
    }

    @Override
    public PageVO findByPatientId(Integer patientId, Integer pageIndex, Integer pageSize) {
        return dietRecordDao.findByPatientId(patientId, pageIndex, pageSize);
    }
}
