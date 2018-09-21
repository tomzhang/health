package com.dachen.health.commons.service.impl;

import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.vo.SuspendInfo;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.user.entity.param.DisableDoctorParam;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.po.Change;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.util.BeanUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by sharp on 2017/11/22.
 */
@Service
public class UserOperationLogService {

    @Autowired
    protected UserLogRespository userLogRespository;

    @Autowired
    private DiseaseTypeRepository diseaseTypeRepository;

    @Async
    public void logOperationRecord(User oldUserInfo, DoctorCheckParam param) {
        //操作记录
        OperationRecord operationRecord = new OperationRecord();
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setCreator(param.getCheckerId());
        operationRecord.setObjectId(param.getUserId() + "");
        operationRecord.setObjectType(UserLogEnum.OperateType.check.getOperate());
        operationRecord.setContent("医生通过审核，审核意见：" + param.getRemark());
        operationRecord.setChange(new Change(UserLogEnum.OperateType.check.getOperate(), "check", null, "医生通过审核。"));
        userLogRespository.addOperationRecord(operationRecord);

        this.logDoctorChange(oldUserInfo, param);
    }

    @Async
    public void logDoctorChange(User oldUserInfo, DoctorCheckParam param) {
        List<Change> changes = Lists.newArrayList();
        if (!StringUtil.equals(oldUserInfo.getName(), param.getName())) {
            changes.add(new Change(UserLogEnum.infoType.name.getType(),"name",oldUserInfo.getName(),param.getName()));
        }

        if (!Objects.equals(oldUserInfo.getUserLevel(), param.getUserLevel())&&Objects.nonNull(param.getUserLevel())) {
            changes.add(new Change(UserLogEnum.infoType.userLevel.getType(),"userLevel", UserEnum.UserLevel.getName(oldUserInfo.getUserLevel()),UserEnum.UserLevel.getName(param.getUserLevel())));
        }

        if (!Objects.equals(oldUserInfo.getLimitedPeriodTime(), param.getLimitedPeriodTime())&&Objects.nonNull(param.getLimitedPeriodTime())) {
            changes.add(new Change(UserLogEnum.infoType.limitedPeriodTime.getType(),"limitedPeriodTime", DateUtil.formatDate2Str(oldUserInfo.getLimitedPeriodTime(), null),DateUtil.formatDate2Str(param.getLimitedPeriodTime(), null)));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getHospital(), param.getHospital())) {
            changes.add(new Change(UserLogEnum.infoType.hospital.getType(),"hospital",oldUserInfo.getDoctor().getHospital(),param.getHospital()));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getDepartments(), param.getDepartments())) {
            changes.add(new Change(UserLogEnum.infoType.department.getType(),"department",oldUserInfo.getDoctor().getDepartments(),param.getDepartments()));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getDeptPhone(), param.getDeptPhone())) {
            changes.add(new Change(UserLogEnum.infoType.deptPhone.getType(),"deptPhone",oldUserInfo.getDoctor().getDeptPhone(),param.getDeptPhone()));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getTitle(), param.getTitle())) {
            changes.add(new Change(UserLogEnum.infoType.title.getType(),"title",oldUserInfo.getDoctor().getTitle(),param.getTitle()));
        }

        if (oldUserInfo.getSex() != param.getSex()) {
            changes.add(new Change(UserLogEnum.infoType.sex.getType(),"sex",oldUserInfo.getSex() != null ? oldUserInfo.getSex().toString() : null, param.getSex() != null ? param.getSex().toString() : null));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getIntroduction(), param.getIntroduction())) {
            changes.add(new Change(UserLogEnum.infoType.introduction.getType(),"introduction",oldUserInfo.getDoctor().getIntroduction(),param.getIntroduction()));
        }

        if (!StringUtil.equals(oldUserInfo.getDoctor().getSkill(), param.getSkill())) {
            changes.add(new Change(UserLogEnum.infoType.skill.getType(),"skill",oldUserInfo.getDoctor().getSkill(),param.getSkill()));
        }

        if (param.getRole() != oldUserInfo.getDoctor().getRole()) {
            changes.add(new Change(UserLogEnum.infoType.role.getType(),"role",oldUserInfo.getDoctor().getRole() != null ? oldUserInfo.getDoctor().getRole().toString() :  null, param.getRole() != null ? param.getRole().toString() :  null));
        }

        if (param.getAssistantId()!=null && !param.getAssistantId().equals(oldUserInfo.getDoctor().getAssistantId())) {
            changes.add(new Change(UserLogEnum.infoType.assistant.getType(),"assistant",oldUserInfo.getDoctor().getAssistantId() != null ? oldUserInfo.getDoctor().getAssistantId().toString() : null, param.getAssistantId() != null ? param.getAssistantId().toString() : null));
        }

        if (oldUserInfo.getDoctor().getCheck() != null && !StringUtil.equals(oldUserInfo.getDoctor().getCheck().getLicenseNum(), param.getLicenseNum())) {
            changes.add(new Change(UserLogEnum.infoType.licenseNum.getType(),"licenseNum",oldUserInfo.getDoctor().getCheck().getLicenseNum(),param.getLicenseNum()));
        }

        if (oldUserInfo.getDoctor().getCheck() != null && !StringUtil.equals(oldUserInfo.getDoctor().getCheck().getLicenseExpire(), param.getLicenseExpire())) {
            changes.add(new Change(UserLogEnum.infoType.licenseExpire.getType(),"licenseExpire",oldUserInfo.getDoctor().getCheck().getLicenseExpire(),param.getLicenseExpire()));
        }

        if (StringUtil.isNotEmpty(param.getHeadPicFileName()) && !StringUtil.equals(oldUserInfo.getHeadPicFileName(), param.getHeadPicFileName())) {
            changes.add(new Change(UserLogEnum.infoType.headPic.getType(),"headPicFileName",oldUserInfo.getHeadPicFileName(),param.getHeadPicFileName()));
        }

        //判断擅长病种设置
        List<String> expsOld = oldUserInfo.getDoctor().getExpertise();
        List<String> expsNew = Lists.newArrayList();
        if (param.getExpertises() != null) {
            expsNew = Arrays.asList(param.getExpertises());
        }

        if ((expsNew!=null && expsNew.size()>0) && (expsOld!=null && expsOld.size()>0)) {
            if (!expsOld.containsAll(expsNew)||!expsNew.containsAll(expsOld)) {
                changes.add(new Change(UserLogEnum.infoType.expertise.getType(),"expertise", getDiseaseNameByIds(expsOld), getDiseaseNameByIds(expsNew)));
            }
        }else if ((expsNew ==null || expsNew.size()==0) && (expsOld==null || expsOld.size()==0)) {

        }else {
            changes.add(new Change(UserLogEnum.infoType.expertise.getType(),"expertise", getDiseaseNameByIds(expsOld), getDiseaseNameByIds(expsNew)));
        }

        if (changes!=null && changes.size() > 0) {
            for(Change change: changes) {
                OperationRecord operationRecord = new OperationRecord();
                operationRecord.setChange(change);
                operationRecord.setCreateTime(System.currentTimeMillis());
                operationRecord.setCreator(param.getCheckerId());
                operationRecord.setObjectId(param.getUserId()+"");
                operationRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
                userLogRespository.addOperationRecord(operationRecord);
            }
        }
    }

    private String getDiseaseNameByIds(List<String> list) {
        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(list)) {
            List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(list);
            for (int i = 0; i < diseaseTypes.size(); i++) {
                sb.append(diseaseTypes.get(i).getName());
                if (diseaseTypes.size() != (i + 1)) {
                    sb.append("、 ");
                }
            }
        } else {
            return "空";
        }
        return sb.toString();
    }

    @Async
    public void logDisableRecord(User oldUserInfo, DisableDoctorParam param) {
        /* 禁用操作记录操作记录 */
        OperationRecord operationRecord = new OperationRecord();
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setCreator(param.getAdminId());
        operationRecord.setObjectId(param.getUserId().toString());
        operationRecord.setObjectType(UserLogEnum.OperateType.tempForbid.getOperate());
        operationRecord.setContent("医生被暂时禁用");
        operationRecord.setChange(new Change(UserLogEnum.OperateType.tempForbid.getOperate(), "disable", null, "医生被暂时禁用"));
        userLogRespository.addOperationRecord(operationRecord);
        /* 禁用原因记录操作记录 */
        OperationRecord reasonRecord = new OperationRecord();
        SuspendInfo suspendInfo = oldUserInfo.getSuspendInfo() == null ? new SuspendInfo() : oldUserInfo.getSuspendInfo();
        reasonRecord.setChange(new Change(UserLogEnum.infoType.disableReason.getType(), "reason", suspendInfo.getReason(), param.getReason()));
        reasonRecord.setCreateTime(System.currentTimeMillis());
        reasonRecord.setCreator(param.getAdminId());
        reasonRecord.setObjectId(param.getUserId().toString());
        reasonRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
        userLogRespository.addOperationRecord(reasonRecord);
    }


    @Async
    public void logReChangeDoctorInfoSusRecord(User oldUserInfo, ResetDoctorInfo param) {
        //操作记录
        OperationRecord operationRecord = new OperationRecord();
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setCreator(param.getCheckerId());
        operationRecord.setObjectId(String.valueOf(param.getUserId()));
        operationRecord.setObjectType(UserLogEnum.OperateType.doctorRecheckInfo.getOperate());
        operationRecord.setContent("认证用户资料变更申请通过审核");
        operationRecord.setChange(new Change(UserLogEnum.OperateType.doctorRecheckInfo.getOperate(), "doctorRecheckInfo",  null, "认证用户资料变更申请通过审核"));
        userLogRespository.addOperationRecord(operationRecord);
        DoctorCheckParam copy = BeanUtil.copy(param, DoctorCheckParam.class);
        this.logDoctorChange(oldUserInfo, copy);
    }

    @Async
    public void logReChangeDoctorInfoFailRecord(ResetDoctorInfo param) {
        //操作记录
        OperationRecord operationRecord = new OperationRecord();
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setCreator(param.getCheckerId());
        operationRecord.setObjectId(String.valueOf(param.getUserId()));
        operationRecord.setObjectType(UserLogEnum.OperateType.doctorRecheckInfo.getOperate());
        operationRecord.setContent("认证用户资料变更申请未通过审核");
        operationRecord.setChange(new Change(UserLogEnum.OperateType.doctorRecheckInfo.getOperate(), "doctorRecheckInfo", null, "认证用户资料变更申请未通过审核"));
        userLogRespository.addOperationRecord(operationRecord);
    }

    /**
     * 新增操作记录
     * @param creator
     * @param objectType
     * @param objectId
     * @param content
     */
    public void addOperationRecord(Integer creator, String objectType,String objectId,String content){
        OperationRecord record = new OperationRecord();
        record.setCreator(creator);
        record.setObjectType(objectType);
        record.setObjectId(objectId);
        record.setContent(content);
        record.setCreateTime(System.currentTimeMillis());
        userLogRespository.save(record);
    }
}
