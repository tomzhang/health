package com.dachen.health.recommend.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.IntegralDoctorMapper;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.recommend.entity.param.IntegralDoctorParam;
import com.dachen.health.recommend.entity.vo.IntegralDoctorVO;
import com.dachen.health.recommend.entity.vo.IntegralPackVO;
import com.dachen.health.recommend.service.IIntegralDoctorService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class IntegralDoctorServiceImpl implements IIntegralDoctorService {

    @Autowired
    private IntegralDoctorMapper integralDoctorMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PackMapper packMapper;

    @Override
    public PageVO getIntegralDoctorList(Integer pageIndex, Integer pageSize) {
        if (pageIndex == null) {
            pageIndex = 0;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        IntegralDoctorParam param = new IntegralDoctorParam();
        param.setPageIndex(pageIndex);
        param.setPageSize(pageSize);
        List<Integer> doctorIdList = integralDoctorMapper.getIntegralDoctorList(param);

        Query<User> uQuery = userRepository.findUserQuery(doctorIdList, null);
        uQuery.filter("userType", UserType.doctor.getIndex());
        uQuery.filter("status", UserStatus.normal.getIndex());

        List<User> users = uQuery.asList();

        if (!CollectionUtils.isEmpty(users)) {
            List<IntegralDoctorVO> resultList = new ArrayList<>();
            for (User _user : users) {
                IntegralDoctorVO vo = new IntegralDoctorVO();
                vo.setDoctorId(_user.getUserId());
                vo.setName(_user.getName());
                vo.setHeadPicFileName(_user.getHeadPicFileName());
                if (_user.getDoctor() != null) {
                    vo.setDepartments(_user.getDoctor().getDepartments());
                }

                resultList.add(vo);
            }

            return new PageVO(resultList, Long.parseLong(resultList.size() + ""), pageIndex, pageSize);
        }
        return new PageVO(null, 0l, pageIndex, pageSize);
    }

    @Override
    public List<IntegralPackVO> getIntegralPackByDoctorId(Integer doctorId) throws HttpApiException {
        if (doctorId == null || doctorId == 0) {
            throw new ServiceException("参数doctorId不能为空");
        }

        //该医生开通的积分问诊套餐列表
        List<IntegralPackVO> voList = integralDoctorMapper.getIntegralPackByDoctorId(doctorId);

        //患者Id
		Integer patientId = ReqUtil.instance.getUserId();
//        Integer patientId = 101920;

        //患者积分列表，调用药店圈接口，获取患者积分列表
        Map<String, Integer> patientIntegralMap = drugApiClientProxy.getUserGoodsPointsListMap(patientId);

        List<IntegralPackVO> resultList = new ArrayList<>();
        /*if(patientIntegralMap == null || patientIntegralMap.size() == 0){
			return resultList;
		}*/

        if (!CollectionUtils.isEmpty(voList)) {
            for (IntegralPackVO vo : voList) {
                int patientIntegral = 0;//患者积分
                if (patientIntegralMap != null && patientIntegralMap.containsKey(vo.getGoodsGroupId())) {
                    patientIntegral = patientIntegralMap.get(vo.getGoodsGroupId());
                }
                vo.setPatientPoint(patientIntegral);
                vo.setBalance(patientIntegral - vo.getPoint());
            }

            resultList.addAll(voList);

            //排序，按照剩余积分降序排列
            Collections.sort(resultList, (a, b) -> {
                Integer b1 = a.getBalance();
                Integer b2 = b.getBalance();
                return b2.compareTo(b1);
            });
        }

        return resultList;
    }

    @Autowired
    protected DrugApiClientProxy drugApiClientProxy;

    //校验患者的积分是否足够支付订单
    @Override
    public boolean checkPatientPoint(Integer packId, Integer userId) throws HttpApiException {
        if(packId == null || packId == 0){
            throw new ServiceException("参数packId不能为空");
        }
        if(userId == null || userId == 0){
            throw new ServiceException("参数userId不能为空");
        }

        Pack pack = packMapper.selectByPrimaryKey(packId);

        if(pack != null){
            //患者积分列表，调用药店圈接口，获取患者积分列表
            Map<String, Integer> patientIntegralMap = drugApiClientProxy.getUserGoodsPointsListMap(userId);
            int patientIntegral = 0;//患者积分
            if (patientIntegralMap != null && patientIntegralMap.containsKey(pack.getGoodsGroupId())) {
                patientIntegral = patientIntegralMap.get(pack.getGoodsGroupId());
            }

            int point = pack.getPoint() == null ? 0 : pack.getPoint();
            if((patientIntegral - point) >= 0){
                return true;
            }
        }
        return false;
    }

}
