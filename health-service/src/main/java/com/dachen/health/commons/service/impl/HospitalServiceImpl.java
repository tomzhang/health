package com.dachen.health.commons.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.health.base.entity.vo.HospitalDeptVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.IHospitalService;
import com.dachen.sdk.exception.HttpApiException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements IHospitalService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;
    @Autowired
    private IBaseDataService baseDataService;


    @Override
    public PageVO getHospitalByUpdate(String appKey, String orgId, Long updateTime, Integer pageIndex, Integer pageSize, String hospital, String hospitalId) {
        //调用药企圈接口判断企业Id与appKey是否是绑定过的
        PageVO page = new PageVO();
        try {
//            validateAppkey(appKey, orgId);
            if(Objects.isNull(updateTime)){
                updateTime = new Long(0L);
            }
            page.setPageIndex(pageIndex);
            page.setPageSize(pageSize);
            page = baseDataService.getHospitals(updateTime, hospital, hospitalId, page);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            System.err.println(e);
            logger.error("--------------------------------------------------------------------------------------------------------------------------------------------------- getHospitalByUpdate -------------------------------------------------------------------------------------------------------------------------", e);
        }
        return page;
    }

    @Override
    public List<HospitalDeptVO> getDepartments(String appKey, String orgId) {
//        validateAppkey(appKey, orgId);
        return baseDataService.getDepartments();
    }

    private void validateAppkey(String appKey, String orgId) {
        try {
            Boolean result = drugOrgApiClientProxy.checkAppKey(appKey, orgId);
            if (Objects.equals(result, Boolean.FALSE)) {
                throw new ServiceException("企业Id与appKey未绑定");
            }
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
