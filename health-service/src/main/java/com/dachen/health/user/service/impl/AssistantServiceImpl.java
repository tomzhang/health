package com.dachen.health.user.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.dao.IAssistantDao;
import com.dachen.health.user.entity.param.DrugVerifyParam;
import com.dachen.health.user.entity.po.DrugVerifyRecord;
import com.dachen.health.user.entity.vo.DoctorDetailVO;
import com.dachen.health.user.entity.vo.DrugVerifyInfo;
import com.dachen.health.user.service.IAssistantService;
import com.dachen.health.user.service.ws.BarCodeService;
import com.dachen.health.user.service.ws.IBarCodeService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@Service(AssistantServiceImpl.BEAN_ID)
public class AssistantServiceImpl extends NoSqlRepository implements IAssistantService {
    public static final String BEAN_ID = "AssistantManagerImpl";

    Logger logger = LoggerFactory.getLogger(AssistantServiceImpl.class);

    @Autowired
    private IAssistantDao assistantDao;

    @Autowired
    private UserRepository userRepository;

    /**
     * 核查药品 1、从手机端接收核查请求； 2、根据核查请求向康哲请求核查内容； 3、将核查信息记录，同时返回核查内容给手机端
     */
    @Override
    public DrugVerifyInfo verifyDrug(DrugVerifyParam verifyParam) {

        if (StringUtils.isEmpty(verifyParam.getDrugCode())) {
            throw new ServiceException(1000101, "药监码不能为空！");
        }
        if (verifyParam.getLongitude() == 0 || verifyParam.getLatitude() == 0) {
            throw new ServiceException(1000102, "经度或者纬度不能为0！");
        }
        User user = userRepository.getUser(verifyParam.getUserId());
        if (user.getUserType() != UserType.assistant.getIndex()) {
            throw new ServiceException(1000103, "只有医助用户才可以进行药品核查！");
        }
        verifyParam.setUserName(user.getName());
        verifyParam.setUserNumber(user.getAssistant().getNumber());

        DrugVerifyInfo drugVerifyInfo = null;
        try {
            drugVerifyInfo = verifyDrugFromKz(verifyParam);
        } catch (Exception e) {
            logger.error("调用康哲药品核查接口失败，错误信息如下：" + e.getMessage());
        }
        if (drugVerifyInfo == null) {
            throw new ServiceException(1000104, "从康哲获取药品核查信息失败！");
        }
        if (StringUtils.isEmpty(drugVerifyInfo.getDrugCode())) {
            throw new ServiceException(1000105, drugVerifyInfo.getRemark());
        }
        DrugVerifyRecord verifyRecord = new DrugVerifyRecord();
        BeanUtils.copyProperties(verifyParam, verifyRecord);
        BeanUtils.copyProperties(drugVerifyInfo, verifyRecord);

        assistantDao.addDrugVerifyRecord(verifyRecord);
        return drugVerifyInfo;

    }

    /**
     * 从康哲请求核查信息
     */
    private DrugVerifyInfo verifyDrugFromKz(DrugVerifyParam verifyParam) {

        logger.info("begin invoke kangzhe service,drugCoce:" + verifyParam.getDrugCode());
        IBarCodeService barCodeService = new BarCodeService().getBasicHttpBindingIBarCodeService();
        String returnstr = barCodeService.getBarCodeInfo(verifyParam.getDrugCode(), String.valueOf(verifyParam.getLongitude()), String.valueOf(verifyParam.getLatitude()), verifyParam.getUserName(),
                verifyParam.getUserNumber());
        logger.info("end invoke kangzhe service,drugCode:" + verifyParam.getDrugCode());
        DrugVerifyInfo drugVerifyInfo = new DrugVerifyInfo();
        JSONObject drugVerifyJson = JSON.parseObject(returnstr);
        drugVerifyInfo.setDrugCode(drugVerifyJson.getString("BarCode"));
        drugVerifyInfo.setDrugName(drugVerifyJson.getString("Ctg_Name"));
        drugVerifyInfo.setDrugSpec(drugVerifyJson.getString("Ctg_Spec"));
        drugVerifyInfo.setBatchNo(drugVerifyJson.getString("BatchNo"));
        drugVerifyInfo.setMadeDate(drugVerifyJson.getString("MadeDate"));
        drugVerifyInfo.setValidateDate(drugVerifyJson.getString("ValidateDate"));
        drugVerifyInfo.setSupplier(drugVerifyJson.getString("Ctg_Supplier"));
        drugVerifyInfo.setOutDate(drugVerifyJson.getString("BC_OutDate"));
        drugVerifyInfo.setRecMerchant(drugVerifyJson.getString("Cst_Name"));
        drugVerifyInfo.setRemark(drugVerifyJson.getString("Remark"));
        return drugVerifyInfo;
    }

    /**
     * </p>获取医助分管医院</p>
     * 
     * @param userId
     * @return hospital:医院名称，hospitalId:医院编码
     * @author fanp
     * @date 2015年7月8日
     */
    public List<DoctorDetailVO> getHospitals(Integer userId) {
        UserSession session = ReqUtil.instance.getUser();
        if (session.getUserType() != UserEnum.UserType.assistant.getIndex()) {
            return null;
        }
        return assistantDao.getHospitals(userId);
    }

    /**
     * </p>获取医助分管医院的医生</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
    public List<DoctorDetailVO> getHospitalDoctors(Integer userId, String hospitalId) {
        UserSession session = ReqUtil.instance.getUser();
        if (session.getUserType() != UserEnum.UserType.assistant.getIndex()) {
            return null;
        }
        if (StringUtil.isBlank(hospitalId)) {
            throw new ServiceException("请选择医院");
        }
        return assistantDao.getHospitalDoctors(userId, hospitalId);
    }

}
