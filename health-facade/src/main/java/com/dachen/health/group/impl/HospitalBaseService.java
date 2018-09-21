package com.dachen.health.group.impl;

import java.util.List;
import java.util.Objects;

import com.dachen.health.base.entity.vo.HospitalVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalLevelPo;
import com.dachen.health.group.IHospitalBaseService;
import com.dachen.health.group.group.entity.param.HospitalBaseParam;
import com.dachen.health.group.hospital.dao.IHospitalBaseDao;

/**
 * 
 * @author longjh
 *      date:2017/08/18
 */
@Service
public class HospitalBaseService implements IHospitalBaseService {
    
    @Autowired
    private IHospitalBaseDao hospitalBaseDao;
    
    @Override
    public List<HospitalLevelPo> findAllHospitalLevel() {
        return hospitalBaseDao.findAllHospitalLevel();
    }

    @Override
    public List<Area> findArea(Area area) {
        if(Objects.isNull(area)){
            area = new Area();
        }
        if(area.getId() == null
             && StringUtils.isBlank(area.getCode())
             && StringUtils.isBlank(area.getName())
             && StringUtils.isBlank(area.getPcode())){
            area.setPcode("0");
        }
        return hospitalBaseDao.findArea(area);
    }

    @Override
    public PageVO findHospital(HospitalBaseParam hospitalBaseParam) {
        if(hospitalBaseParam.getPageIndex() <= 0){
            hospitalBaseParam.setPageIndex(0);
        }else{
            int index = hospitalBaseParam.getPageIndex();
            hospitalBaseParam.setPageIndex(index - 1);
        }
        PageVO page = hospitalBaseDao.findHospital(hospitalBaseParam);
        page.setPageIndex(page.getPageIndex() + 1);
        return page; 
    }


    @Override
    public String findLevelByHospitalName(List<String> listName) {
        return hospitalBaseDao.findLevelByHospitalName(listName);
    }

    @Override
    public List<HospitalVO> pageQueryHospital() {
        return hospitalBaseDao.pageQueryHospital();
    }

    @Override
    public List<HospitalVO> getByIds(List<String> ids) {


        return hospitalBaseDao.getByIds(ids);
    }
}
