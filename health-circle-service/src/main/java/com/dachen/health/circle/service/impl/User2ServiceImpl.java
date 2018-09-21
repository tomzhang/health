package com.dachen.health.circle.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.entity.vo.GroupUnionVo;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.circle.form.AreaInfo;
import com.dachen.health.circle.service.DoctorFollowService;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.vo.MobileCircleHomeVO;
import com.dachen.health.circle.vo.MobileDoctorBriefVo;
import com.dachen.health.circle.vo.MobileDoctorHomeVO;
import com.dachen.health.circle.vo.MobileDoctorVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserConstant;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.common.util.RemoteSysManager;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.JSONUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Model(value = User.class)
@Service
public class User2ServiceImpl extends IntegerBaseServiceImpl implements User2Service {

    @Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;

    @Autowired
    protected UserManager userManager;

    @Autowired
    protected Auth2Helper auth2Helper;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Autowired
    private IGroupDao groupDao;

    @Autowired
    private GroupDoctor2Service groupDoctor2Service;

    @Autowired
    private DoctorFollowService doctorFollowService;

    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;

    private static final String FAQ_ACTION = "http://FAQ/collect/count/{userId}";

    @Override
    public List<User> findDoctorListByIds(List<Integer> userIdList) {
        List<User> list = this.findByIds(userIdList);
        this.wrapAll(list);
        return list;
    }

    @Autowired
    protected Group2Service group2Service;

    protected void wrapAll(List<User> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapDept(list);
    }

    protected void wrapDept(List<User> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

//        Set<Integer> userIdSet = new HashSet<>(list.size());
//        for (User user:list) {
//            userIdSet.add(user.getUserId());
//        }
//
//        List<GroupDoctor2> groupDoctor2List = groupDoctor2Service.findDeptListByDoctor(new ArrayList<>(userIdSet));
//        for (User user:list) {
//            for (GroupDoctor2 groupDoctor2:groupDoctor2List) {
//                if (user.getUserId().equals(groupDoctor2.getDoctorId())) {
//
//                }
//            }
//        }

    }


    public List<User> findList(String hospitalId, String deptId, List<Integer> excludeIdList) {
        Query<User> query = this.createQuery();
        query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        query.field("doctor.hospitalId").equal(hospitalId).field("doctor.deptId").equal(deptId);
        if (SdkUtils.isNotEmpty(excludeIdList)) {
            query.field(Mapper.ID_KEY).notIn(excludeIdList);
        }
        return query.asList();
    }

    @Override
    public List<User> findBaseList(Integer userId) {
        User user = this.findDoctorById(userId);
        if(user!=null && user.getDoctor()!=null && StringUtil.isNotEmpty(user.getDoctor().getDeptId()) && StringUtil.isNotEmpty(user.getDoctor().getHospitalId())){
            Query<User> query = this.createQuery();
            query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
            query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
            query.field("doctor.hospitalId").equal(user.getDoctor().getHospitalId()).field("doctor.deptId").equal(user.getDoctor().getDeptId());
            query.retrievedFields(true, Mapper.ID_KEY,"name","userType","sex","headPicFileName","doctor.hospital","doctor.departments","doctor.title","doctor.introduction");
            return query.asList();
        }else {
            List<User> users=new ArrayList<>();
            users.add(user);
            return users;
        }
    }

    @Override
    public User findAndCheckDoctor(Integer doctorId) {
        User user = this.findDoctorById(doctorId);
        if (Objects.isNull(user)) {
            throw new ServiceException("doctor Not Found. " + doctorId);
        }
        if (user.getUserType().intValue() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("Forbidden");
        }
        if (null == user.getDoctor()) {
            throw new ServiceException("Forbidden");
        }
        return user;
    }

    @Override
    public List<User> findByHospitalAndDeptAndVO(String hospitalId, String deptId) {
        Query<User> query = this.createQuery();
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        query.field("doctor.hospitalId").equal(hospitalId).field("doctor.deptId").equal(deptId);
        return query.asList();
    }

    @Override
    public List<MobileDoctorVO> findByHospitalAndDeptAndVO(String hospitalId, String deptId, List<Integer> excludeIdList) {
        List<User> userList = findList(hospitalId, deptId, excludeIdList);
        List<MobileDoctorVO> doctorVOList = this.convertToMobile(userList);
        return doctorVOList;
    }

    @Override
    public List<User> findByHospitalAndDept(String hospitalId, String deptId, List<Integer> excludeIdList) {
        List<User> userList = findList(hospitalId, deptId, excludeIdList);
        return userList;
    }

    protected List<MobileDoctorVO> convertToMobile(List<User> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileDoctorVO> voList = new ArrayList<>(list.size());
        for (User user:list) {
            voList.add(new MobileDoctorVO(user));
        }
        return voList;
    }

    @Override
    public User findDoctorById(Integer userId) {
        Query<User> query = this.createQueryByPK(userId);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        return query.get();
    }

    @Override
    public List<User> findDoctorByIds(List<Integer> doctorIdList) {
        Query<User> query = this.createQueryByPKs(doctorIdList);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        return query.asList();
    }

    @Override
    public List<User> findDoctorByIds(List<Integer> doctorIdList, String keyword) {
        Query<User> query = this.createQueryByPKs(doctorIdList);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        if (StringUtils.isNotBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);;
            query.field("name").equal(pattern);
        }
        return query.asList();
    }

    @Override
    public MobileDoctorVO getInfo(Integer userId) {
        User user = this.findDoctorById(userId);
        if (null == user) {
            return null;
        }

        MobileDoctorVO mobileDoctor = new MobileDoctorVO(user);
//        List<String> expertise = user.getDoctor().getExpertise();
//        if (SdkUtils.isNotEmpty(expertise)) {
//            List<DiseaseType> diseaseTypes=diseaseTypeRepository.findByIds(expertise);
//            mobileDoctor.setExpertise(diseaseTypes);
//        }
        return mobileDoctor;
    }

    @Override
    public int countExpert(List<Integer> doctorIdList) {
        Query<User> query = this.createQueryByPKs(doctorIdList);
        query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.field("doctor.hospitalId").exists();
        long count = query.countAll();
        return (int) count;
    }

    @Override
    public int countTotalCure(List<Integer> doctorIdList) {
        Query<User> query = this.createQueryByPKs(doctorIdList);
        query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.field("doctor").exists().field("doctor.cureNum").exists();
        query.retrievedFields(true, "doctor.cureNum");

        List<User> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return 0;
        }
        List<Integer> cureNumList = list.stream().map(o->o.getDoctor().getCureNum()).collect(Collectors.toList());
        int count = 0;
        for (Integer cureNum:cureNumList) {
            count += cureNum;
        }
        return count;
    }
    /**
     * 根据医生id 查询正常的用户id 医生id=userId
     * @param doctorByIds
     * @return
     */
    @Override
    public List<Integer> getNormalUserIdByDoctorByIds(List<Integer> doctorByIds) {
        if(null==doctorByIds && doctorByIds.size()==0){
            return null;
        }
        Query<User> query = this.createQueryByPKs(doctorByIds);
        query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.retrievedFields(true, Mapper.ID_KEY);
        List<User> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<Integer> ret = list.stream().map(o->o.getUserId()).collect(Collectors.toList());

        return ret;
    }
    /**
     * 根据圈子id,医联体id 查询正常的用户id 医生id=userId
     * @param groupIds
     * @param unionIds
     * @return 所有userId
     */
    @Override
    public Long getNormalUserIdByUnionIdAndGroupId(List<String> groupIds, List<String> unionIds) {
        /*if(null == unionIds && unionIds.size()==0 && null==groupIds && groupIds.size()==0){
            throw new ServiceException("请选择医生圈子和医联体范围");
        }*/
        //医生圈子为空,查询所有圈子
        if(SdkUtils.isEmpty(groupIds)){
            List<Group> allGroupExDept = groupDao.findAllGroupExDept();
            if(SdkUtils.isNotEmpty(allGroupExDept)){
                groupIds = allGroupExDept.stream().map(o->o.getId()).collect(Collectors.toList());
            }
        }

        //为空的话 后台拼接所有医联体id
        if(SdkUtils.isEmpty(unionIds)){
            List<GroupUnionVo> allGroupUnion = baseDataDao.getAllGroupUnion();
            if(SdkUtils.isNotEmpty(allGroupUnion)){
                for (GroupUnionVo groupUnionVo :allGroupUnion){
                    unionIds.add(groupUnionVo.getId());
                }
            }
        }
        List<Integer> groupIdList = groupDoctor2Service.findDoctorIdListByGroupAndUnions(groupIds,unionIds);
        return Long.valueOf(groupIdList.size());
    }

    /**
     * @param provinceJsons 省市id
     * @param levels 医院级别
     * @param deptIds 科室id
     * @param titles 职称
     * @return   正常的用户id
     */
    @Override
    public Long getNormalUserIdByCityAndLevelAndDepartmentsAndTitle(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
        userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        if(SdkUtils.isNotEmpty(levels)) {
            List<HospitalVO> hospitalBylevelList = baseDataDao.getHospitalBylevelList(levels);
            if (SdkUtils.isNotEmpty(hospitalBylevelList)) {
                Set<String> hospitalId = new HashSet<>();
                for (HospitalVO hospitalVO : hospitalBylevelList) {
                    hospitalId.add(hospitalVO.getId());
                }
                query.criteria("doctor.hospitalId").in(hospitalId);
            }
        }

        if(StringUtils.isNotEmpty(provinceJsons)) {
            List<AreaInfo> provinces= JSON.parseArray(provinceJsons,AreaInfo.class);
            Criteria[] criteria = new Criteria[provinces.size()];
            for (int i = 0; i < provinces.size(); i++) {
                AreaInfo info = provinces.get(i);
                if (Objects.nonNull(info.getProvince()) && null != info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()))
                            .criteria("doctor.cityId").equal(Integer.valueOf(info.getCity().getCode()));
                } else if (Objects.nonNull(info.getProvince()) && null == info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()));
                }
            }
            query.or(criteria);
         }

/*

            Criteria[] criteria = new Criteria[provinces.size()];
            for (int i=0; i<provinces.size(); i++) {
                AreaInfo areaInfo = provinces.get(i);
                if (null != areaInfo) {
                    if (areaInfo.getProvince() != null && StringUtils.isNotEmpty(areaInfo.getProvince().getCode())) {
                        criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(areaInfo.getProvince().getCode()));
                    }
                    if(SdkUtils.isNotEmpty(areaInfo.getCity())){
                        Set<Integer> cityIds=new HashSet<>();
                        for (AreaRangeInfo areaRangeInfo:areaInfo.getCity()){
                            cityIds.add(Integer.valueOf(areaRangeInfo.getCode()));
                        }
                        criteria[i] = query.criteria("doctor.cityId").in(cityIds);
                    }
                }

            }
            query.or(criteria);
        }
*/

        if(SdkUtils.isNotEmpty(deptIds)) {
            query.criteria("doctor.deptId").in(deptIds);
        }
        if(SdkUtils.isNotEmpty(titles)) {
            query.criteria("doctor.title").in(titles);

        }
        query.order("createTime");
        query.retrievedFields(true, Mapper.ID_KEY);
        return query.countAll();
    }

    /**
     * 获取所有正常的用户id
     * @return 正常的用户id
     */
    @Override
    public Long getNormalUser() {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
        userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        return query.countAll();
    }

    @Override
    public Pagination<Integer> getNormalUserIdByUnionIdAndGroupIdPage(List<String> groupIds, List<String> unionIds, Integer pageIndex, Integer pageSize) {
        //医生圈子为空,查询所有圈子
        if(SdkUtils.isEmpty(groupIds)){
            List<Group> allGroupExDept = groupDao.findAllGroupExDept();
            if(SdkUtils.isNotEmpty(allGroupExDept)){
                groupIds = allGroupExDept.stream().map(o->o.getId()).collect(Collectors.toList());
            }
        }

        //为空的话 后台拼接所有医联体id
        if(SdkUtils.isEmpty(unionIds)){
            List<GroupUnionVo> allGroupUnion = baseDataDao.getAllGroupUnion();
            if(SdkUtils.isNotEmpty(allGroupUnion)){
                for (GroupUnionVo groupUnionVo :allGroupUnion){
                    unionIds.add(groupUnionVo.getId());
                }
            }
        }
        Pagination<Integer> userIdPage = groupDoctor2Service.findDoctorIdListByGroupAndUnionsPage(groupIds, unionIds, pageIndex, pageSize);
        return userIdPage;
    }

    @Override
    public Pagination<Integer> getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles, Integer pageIndex, Integer pageSize) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
        userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());

        /*if(SdkUtils.isEmpty(levels)){
            //获取所有的医院
            List<HospitalLevelPo> hospitalLevels = baseDataDao.getHospitalLevels();
            if(SdkUtils.isNotEmpty(hospitalLevels)){
                for (HospitalLevelPo hospitalLevelPo:hospitalLevels){
                    levels.add(hospitalLevelPo.getLevel());
                }
            }
        }*/
        if(SdkUtils.isNotEmpty(levels)) {
            List<HospitalVO> hospitalBylevelList = baseDataDao.getHospitalBylevelList(levels);
            if (SdkUtils.isNotEmpty(hospitalBylevelList)) {
                Set<String> hospitalId = new HashSet<>();
                for (HospitalVO hospitalVO : hospitalBylevelList) {
                    hospitalId.add(hospitalVO.getId());
                }
                    query.criteria("doctor.hospitalId").in(hospitalId);
            }
        }

        if(StringUtils.isNotEmpty(provinceJsons)) {
            List<AreaInfo> provinces= JSON.parseArray(provinceJsons,AreaInfo.class);
            Criteria[] criteria = new Criteria[provinces.size()];
            for (int i = 0; i < provinces.size(); i++) {
                AreaInfo info = provinces.get(i);
                if (Objects.nonNull(info.getProvince()) && null != info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()))
                            .criteria("doctor.cityId").equal(Integer.valueOf(info.getCity().getCode()));
                } else if (Objects.nonNull(info.getProvince()) && null == info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()));
                }
            }
            query.or(criteria);
        }

        if(SdkUtils.isNotEmpty(deptIds)) {
            query.criteria("doctor.deptId").in(deptIds);
        }
        if(SdkUtils.isNotEmpty(titles)) {
            query.criteria("doctor.title").in(titles);

        }
        query.offset(pageIndex*pageSize).limit(pageSize);
        query.order("createTime");
        query.retrievedFields(true, Mapper.ID_KEY);
        List<User> list = query.asList();
        if(SdkUtils.isEmpty(list)){
            return null;
        }
        List<Integer> ret = list.stream().map(o->o.getUserId()).collect(Collectors.toList());
        long total = query.countAll();
        Pagination<Integer> page = new Pagination<>(ret, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<Integer> getNormalUserPage(Integer pageIndex, Integer pageSize) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
        userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        query.retrievedFields(true, Mapper.ID_KEY);
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<User> list = query.asList();
        if(SdkUtils.isEmpty(list)){
            return null;
        }
        List<Integer> ret = list.stream().map(o->o.getUserId()).collect(Collectors.toList());
        long total = query.countAll();
        Pagination<Integer> page = new Pagination<>(ret, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<MobileDoctorBriefVo> searchUserByKeyWord(Integer userId, String keyWord, Integer pageIndex, Integer pageSize){
        Query<User> query=this.createQuery();
        List<Integer> statusList=new ArrayList<>(4);
        statusList.add(UserEnum.UserStatus.normal.getIndex());
        statusList.add(UserEnum.UserStatus.uncheck.getIndex());
        statusList.add(UserEnum.UserStatus.fail.getIndex());
        statusList.add(UserEnum.UserStatus.Unautherized.getIndex());
        query.field("name").exists();
        query.field("doctor.hospital").exists();
        query.field("doctor.departments").exists();
        query.field("doctor.title").exists();
        query.criteria("status").in(statusList);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        query.field(Mapper.ID_KEY).notEqual(userId);
        query.field("suspend").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        if (StringUtil.isBlank(keyWord)) {
            return new Pagination<MobileDoctorBriefVo>();
        }
        keyWord = keyWord.replaceAll("\\(", "[(]");//解决正则表达式中的“(”的问题
        keyWord = keyWord.replaceAll("\\)", "[)]");//解决正则表达式中的“)”的问题

        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Criteria[] criteria;
        List<String> idByName = diseaseTypeRepository.findIdByName(keyWord);
        if(SdkUtils.isNotEmpty(idByName)) {
             criteria = new Criteria[6];
        }else {
             criteria = new Criteria[5];
        }
        criteria[0]=query.criteria("name").equal(pattern);
        criteria[1]=query.criteria("telephone").equal(pattern);
        criteria[2]=query.criteria("doctor.hospital").equal(pattern);
        criteria[3]=query.criteria("doctor.departments").equal(pattern);
        criteria[4]=query.criteria("doctor.skill").equal(pattern);
        if(SdkUtils.isNotEmpty(idByName)) {
            criteria[5] = query.criteria("doctor.expertise").in(idByName);
        }
        query.or(criteria);
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<User> users = query.asList();
        setExpertise(users);//擅长转换
        long total = query.countAll();
        List<MobileDoctorBriefVo> mobileDoctorBriefVos=wrapMobileDoctorBriefVo(users);
        Pagination<MobileDoctorBriefVo> pagination=new Pagination<>(mobileDoctorBriefVos,total,pageIndex,pageSize);
        return pagination;
    }

    @Override
    public MobileDoctorHomeVO getDoctorHomePage(Integer userId,Integer doctorId){
        User user = this.findById(doctorId);
        setExpertise(user);
        MobileDoctorHomeVO mobileDoctorHomeVO=new MobileDoctorHomeVO(user);
        Long fans = doctorFollowService.countFanByUserId(doctorId);
        Long follows = doctorFollowService.countFollowByUserId(doctorId);
        mobileDoctorHomeVO.setFans(fans);
        mobileDoctorHomeVO.setFollowers(follows);
        boolean whetherFollow = doctorFollowService.whetherFollow(userId, doctorId);
        mobileDoctorHomeVO.setIfFollower(whetherFollow);
       /* //获取该医生加入的圈 圈子和医院和医联体 (除了科室)
        List<String> groupIds = groupDoctor2Service.findGroupIdListExceptByDoctor(doctorId); //加入的圈子和医院
        List<MobileGroupVO> mobileGroupVOs=new ArrayList<>();
        if(SdkUtils.isNotEmpty(groupIds)) {
            Map<String, Integer> countMap = groupDoctor2Service.countByGroupList(groupIds);
             for (String groupId : groupIds) {
                    MobileGroupVO mobileGroupVO = new MobileGroupVO();
                    Group2 group2 = group2Service.findNormalById(groupId);
                    if(group2==null){
                        continue;
                    }
                    mobileGroupVO.setLogoPicUrl(group2.getLogoUrl());
                    mobileGroupVO.setGroupId(groupId);
                    mobileGroupVO.setName(group2.getName());
                    mobileGroupVO.setType(group2.getType());
                    mobileGroupVO.setPostedNumber(0l);
                    mobileGroupVO.setTotalMember(countMap.get(groupId));
                    mobileGroupVOs.add(mobileGroupVO);
                }


                List<MobileGroupUnionVO> unions = groupUnionService.findByGroupsAndVO(groupIds);

                if (SdkUtils.isNotEmpty(unions)) {
                    for (MobileGroupUnionVO mobileGroupUnionVO:unions) {
                        MobileGroupVO mobileGroupVO = new MobileGroupVO();
                        mobileGroupVO.setGroupId(mobileGroupUnionVO.getUnionId());
                        mobileGroupVO.setLogoPicUrl(mobileGroupUnionVO.getLogoPicUrl());
                        mobileGroupVO.setName(mobileGroupUnionVO.getName());
                        mobileGroupVO.setType("union");
                        mobileGroupVO.setPostedNumber(0l);
                        mobileGroupVO.setTotalMember(mobileGroupUnionVO.getTotalDoctor());
                        mobileGroupVOs.add(mobileGroupVO);
                    }
            }

        }

        try {
            wrapPostedNumber(mobileGroupVOs,userId); //获取我的圈的发帖数
        }catch (Exception e){
            logger.error("发帖数获取失败",e);
        }

        mobileDoctorHomeVO.setMyGroup(mobileGroupVOs);*/
        return mobileDoctorHomeVO;
    }

    private void setExpertise(User u) {
       List<User> users=new ArrayList<>();
        users.add(u);
        setExpertise(users);
    }

    private void setExpertise(List<User> list) {
        for (User u : list) {
            Doctor doc = u.getDoctor();
            String diseaseStr = "";
            if (doc != null) {
                List<String> diseaseTypeIds = doc.getExpertise();
                if (diseaseTypeIds != null && !diseaseTypeIds.isEmpty()) {
                    List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(diseaseTypeIds);
                    for (DiseaseType d : diseaseTypes) {
                        diseaseStr += d.getName() + ",";
                    }
                    if (diseaseStr.endsWith(",")) {
                        diseaseStr = diseaseStr.substring(0, diseaseStr.length() - 1);
                    }
                }
            }
            if (StringUtil.isNotEmpty(diseaseStr) 
                    || StringUtil.isNotEmpty(doc.getSkill())) {

                String skill = doc.getSkill() == null ? "" : doc.getSkill();

                if (StringUtil.isNotBlank(diseaseStr)) {
                    doc.setSkill(diseaseStr + "。" + skill);
                } else {
                    doc.setSkill(skill);
                }
            }
        }
    }
/*
    private void wrapPostedNumber(List<MobileGroupVO> mobileGroupVOs,Integer userId){
        if(SdkUtils.isEmpty(mobileGroupVOs)){
            return ;
        }
        logger.info("wrapPostedNumber entity info :{}", ToStringBuilder.reflectionToString(mobileGroupVOs));
        PublishCountParam publishCountParam=new PublishCountParam();
        publishCountParam.setUserId(userId+"");
        List<String> deptIds=new ArrayList<>();
        List<String> hospitalIds=new ArrayList<>();
        List<String> groupIds=new ArrayList<>();
        List<String> unionIds=new ArrayList<>();
        for (MobileGroupVO mobileGroupVO : mobileGroupVOs) {
            if (GroupEnum.GroupType.dept.getIndex().equals(mobileGroupVO.getType())) {
                deptIds.add(mobileGroupVO.getGroupId());
            }else if (GroupEnum.GroupType.group.getIndex().equals(mobileGroupVO.getType())) {
                groupIds.add(mobileGroupVO.getGroupId());
            }else if (GroupEnum.GroupType.hospital.getIndex().equals(mobileGroupVO.getType())) {
                hospitalIds.add(mobileGroupVO.getGroupId());
            }else if ("union".equals(mobileGroupVO.getType())) {
                unionIds.add(mobileGroupVO.getGroupId());
            }
        }
        logger.info("send params info :{}", ToStringBuilder.reflectionToString(publishCountParam));
        if(SdkUtils.isNotEmpty(deptIds) || SdkUtils.isNotEmpty(hospitalIds) || SdkUtils.isNotEmpty(groupIds) || SdkUtils.isNotEmpty(unionIds)){
            publishCountParam.setDeptIds(deptIds);
            publishCountParam.setGroupIds(groupIds);
            publishCountParam.setHospitalIds(hospitalIds);
            publishCountParam.setUnionIds(unionIds);
            Map<String, String> map=new HashedMap();
            map.put("param",JSONUtil.toJSONString(publishCountParam));
            String res = remoteSysManager.send(SERVER, ACTION, map);
            PublishCountParam pc = JSONUtil.parseObject(PublishCountParam.class, res);
            logger.info("return entity info :{}",ToStringBuilder.reflectionToString(pc));
            if(pc!=null) {
                for (MobileGroupVO mobileGroupVO : mobileGroupVOs) {
                    if (GroupEnum.GroupType.dept.getIndex().equals(mobileGroupVO.getType()) && pc.getDept()!=null && pc.getDept().get(mobileGroupVO.getGroupId())!=null) {
                        mobileGroupVO.setPostedNumber(pc.getDept().get(mobileGroupVO.getGroupId()));
                    } else if (GroupEnum.GroupType.group.getIndex().equals(mobileGroupVO.getType()) && pc.getGroup()!=null && pc.getGroup().get(mobileGroupVO.getGroupId())!=null) {
                        mobileGroupVO.setPostedNumber(pc.getGroup().get(mobileGroupVO.getGroupId()));
                    } else if (GroupEnum.GroupType.hospital.getIndex().equals(mobileGroupVO.getType()) && pc.getHospital()!=null && pc.getHospital().get(mobileGroupVO.getGroupId())!=null) {
                        mobileGroupVO.setPostedNumber(pc.getHospital().get(mobileGroupVO.getGroupId()));
                    } else if ("union".equals(mobileGroupVO.getType()) && pc.getUnion()!=null && pc.getUnion().get(mobileGroupVO.getGroupId())!=null) {
                        mobileGroupVO.setPostedNumber(pc.getUnion().get(mobileGroupVO.getGroupId()));
                    }
                }
            }
        }
    }*/

    private List<MobileDoctorBriefVo> wrapMobileDoctorBriefVo(List<User> users) {
        if (SdkUtils.isEmpty(users)){
            return null;
        }
        List<MobileDoctorBriefVo> mobileDoctorBriefVos=new ArrayList<>(users.size());
        for (User user:users){
            MobileDoctorBriefVo mobileDoctorBriefVo = new MobileDoctorBriefVo(user);
            mobileDoctorBriefVos.add(mobileDoctorBriefVo);
        }
        return mobileDoctorBriefVos;
    }


    /**
     * 是否设置了密码
     *
     * @param user
     * @return
     */
    private boolean hasPwd(User user) {
        Integer bindFlag = auth2Helper.isSetupPwd(user.getUserId());
        if (bindFlag != null && bindFlag.intValue() == 1) {
            return true;
        }
        return false;
    }

    @Autowired
    protected IdxRepository idxRepository;

    protected String nextDoctorNum() {
        String doctorNum = idxRepository.nextDoctorNum(IdxRepository.idxType.doctorNum);
        return doctorNum;
    }

    @Resource(name = "jedisTemplate")
    protected JedisTemplate jedisTemplate;

    public void removeUserRedisCache(Integer userId) {
        if (Objects.nonNull(userId)) {
            String userIdKey = KeyBuilder.userIdKey(userId);
            jedisTemplate.del(userIdKey);
        }
    }

    /**
     * 获取所有正常的用户id
     * @return 正常的用户
     */
    @Override
    public List<User> getNormalUserList() {
        Query<User> query = this.createQuery();
        query.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        return query.asList();
    }


    @Override
    public MobileCircleHomeVO getCircleIndex(Integer userId){
        Long fans = doctorFollowService.countFanByUserId(userId);
        Long follows = doctorFollowService.countFollowByUserId(userId);

        MobileCircleHomeVO mobileCircleHomeVO=new MobileCircleHomeVO();
        mobileCircleHomeVO.setFansNumber(fans);
        mobileCircleHomeVO.setFollowersNumber(follows);
        mobileCircleHomeVO.setCollectNumber(0l);
        try {
            String res = remoteSysManagerUtil.postForObject(FAQ_ACTION,userId);
            JSONMessage pc = JSONUtil.parseObject(JSONMessage.class, res);
            Integer num= (Integer) pc.get("count");
            mobileCircleHomeVO.setCollectNumber(Long.valueOf(num));
        }catch (Exception e){
            logger.error("获取收藏数失败 param userId:{}",userId,e);
        }
        return mobileCircleHomeVO;
    }

    @Override
    public Pagination<User> getInfoByUserIds(List<Integer> userIds, Integer pageSize, Integer pageIndex) {
        if (ObjectUtils.isEmpty(userIds)) {
            return new Pagination<User>();
        }
        Query<User> query=this.createQuery();
        query.field(Mapper.ID_KEY).in(userIds);
        query.offset(pageIndex * pageSize).limit(pageSize);
        query.order("createTime");
        long total = query.countAll();
        List<User> users = query.asList();
        Pagination<User> pagination=new Pagination<>(users,total,pageIndex,pageSize);
        return pagination;
    }

    @Override
    public Long getNormalUser(boolean userCheck) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        if (!userCheck){
            userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
            userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        }
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        return query.countAll();
    }

    @Override
    public Long getNormalUserIdByCityAndLevelAndDepartmentsAndTitle(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles, boolean userCheck) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        if (!userCheck){
            userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
            userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        }
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        if(SdkUtils.isNotEmpty(levels)) {
            List<HospitalVO> hospitalBylevelList = baseDataDao.getHospitalBylevelList(levels);
            if (SdkUtils.isNotEmpty(hospitalBylevelList)) {
                Set<String> hospitalId = new HashSet<>();
                for (HospitalVO hospitalVO : hospitalBylevelList) {
                    hospitalId.add(hospitalVO.getId());
                }
                query.criteria("doctor.hospitalId").in(hospitalId);
            }
        }

        if(StringUtils.isNotEmpty(provinceJsons)) {
            List<AreaInfo> provinces= JSON.parseArray(provinceJsons,AreaInfo.class);
            Criteria[] criteria = new Criteria[provinces.size()];
            for (int i = 0; i < provinces.size(); i++) {
                AreaInfo info = provinces.get(i);
                if (Objects.nonNull(info.getProvince()) && null != info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()))
                            .criteria("doctor.cityId").equal(Integer.valueOf(info.getCity().getCode()));
                } else if (Objects.nonNull(info.getProvince()) && null == info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()));
                }
            }
            query.or(criteria);
        }
        if(SdkUtils.isNotEmpty(deptIds)) {
            query.criteria("doctor.deptId").in(deptIds);
        }
        if(SdkUtils.isNotEmpty(titles)) {
            query.criteria("doctor.title").in(titles);

        }
        query.order("createTime");
        query.retrievedFields(true, Mapper.ID_KEY);
        return query.countAll();
    }

    @Override
    public Pagination<Integer> getNormalUserPage(Integer pageIndex, Integer pageSize, boolean userCheck) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        if (!userCheck){
            userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
            userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        }
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        query.retrievedFields(true, Mapper.ID_KEY);
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<User> list = query.asList();
        if(SdkUtils.isEmpty(list)){
            return null;
        }
        List<Integer> ret = list.stream().map(o->o.getUserId()).collect(Collectors.toList());
        long total = query.countAll();
        Pagination<Integer> page = new Pagination<>(ret, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<Integer> getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles, boolean userCheck, Integer pageIndex, Integer pageSize) {
        Query<User> query = this.createQuery();
        List<Integer> userStatus = new ArrayList<>();
        userStatus.add(UserEnum.UserStatus.normal.getIndex());
        if (!userCheck){
            userStatus.add(UserEnum.UserStatus.Unautherized.getIndex());
            userStatus.add(UserEnum.UserStatus.uncheck.getIndex());
        }
        query.criteria("status").in(userStatus);
        query.field("userType").equal(UserEnum.UserType.doctor.getIndex());

        /*if(SdkUtils.isEmpty(levels)){
            //获取所有的医院
            List<HospitalLevelPo> hospitalLevels = baseDataDao.getHospitalLevels();
            if(SdkUtils.isNotEmpty(hospitalLevels)){
                for (HospitalLevelPo hospitalLevelPo:hospitalLevels){
                    levels.add(hospitalLevelPo.getLevel());
                }
            }
        }*/
        if(SdkUtils.isNotEmpty(levels)) {
            List<HospitalVO> hospitalBylevelList = baseDataDao.getHospitalBylevelList(levels);
            if (SdkUtils.isNotEmpty(hospitalBylevelList)) {
                Set<String> hospitalId = new HashSet<>();
                for (HospitalVO hospitalVO : hospitalBylevelList) {
                    hospitalId.add(hospitalVO.getId());
                }
                query.criteria("doctor.hospitalId").in(hospitalId);
            }
        }

        if(StringUtils.isNotEmpty(provinceJsons)) {
            List<AreaInfo> provinces= JSON.parseArray(provinceJsons,AreaInfo.class);
            Criteria[] criteria = new Criteria[provinces.size()];
            for (int i = 0; i < provinces.size(); i++) {
                AreaInfo info = provinces.get(i);
                if (Objects.nonNull(info.getProvince()) && null != info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()))
                            .criteria("doctor.cityId").equal(Integer.valueOf(info.getCity().getCode()));
                } else if (Objects.nonNull(info.getProvince()) && null == info.getCity()) {
                    criteria[i] = query.criteria("doctor.provinceId").equal(Integer.valueOf(info.getProvince().getCode()));
                }
            }
            query.or(criteria);
        }

        if(SdkUtils.isNotEmpty(deptIds)) {
            query.criteria("doctor.deptId").in(deptIds);
        }
        if(SdkUtils.isNotEmpty(titles)) {
            query.criteria("doctor.title").in(titles);

        }
        query.offset(pageIndex*pageSize).limit(pageSize);
        query.order("createTime");
        query.retrievedFields(true, Mapper.ID_KEY);
        List<User> list = query.asList();
        if(SdkUtils.isEmpty(list)){
            return null;
        }
        List<Integer> ret = list.stream().map(o->o.getUserId()).collect(Collectors.toList());
        long total = query.countAll();
        Pagination<Integer> page = new Pagination<>(ret, total, pageIndex, pageSize);
        return page;
    }
}
