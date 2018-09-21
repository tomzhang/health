package com.dachen.health.pack.stat.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.stat.entity.param.PatientStatParam;
import com.dachen.health.pack.stat.entity.vo.DiseaseInfoVo;
import com.dachen.health.pack.stat.entity.vo.PatientStatVO;
import com.dachen.health.pack.stat.mapper.PatientStatMapper;
import com.dachen.health.pack.stat.service.IPatientStatService;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： PatientStatServiceImpl<br>
 * Description： 患者统计<br>
 *
 * @author fanp
 * @version 1.0.0
 * @createTime 2015年9月22日
 */
@Service
public class PatientStatServiceImpl implements IPatientStatService {

    @Autowired
    private PatientStatMapper patientStatMapper;

    @Autowired
    private IBaseUserService baseUserService;

    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private IDepartmentDoctorDao dpartmentDoctorDao;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Autowired
    private PatientMapper patientMapper;

    /**
     * </p>统计用户被医生治疗过的患者</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<PatientStatVO> getByUserAndDoctor(PatientStatParam param) {
        if (param.getUserId() != null && param.getDoctorIds() != null && param.getDoctorIds().size() > 0 && param.getStatus() != null) {
            List<PatientStatVO> list = patientStatMapper.getByUserAndDoctor(param);
            for (PatientStatVO vo : list) {
                if (vo.getBirthday() != null) {
                    vo.setAge(DateUtil.calcAge(vo.getBirthday()));
                    vo.setAgeStr(getAgeStr(vo.getBirthday()));
                    vo.setBirthday(null);
                }
                if (StringUtil.isNotBlank(vo.getTopPath())) {
                    vo.setTopPath(PropertiesUtil.addUrlPrefix(vo.getTopPath()));
                }
            }
            return list;
        }

        return null;
    }

    /**
     * 计算生日
     *
     * @param birthday
     * @return
     */
    public String getAgeStr(Long birthday) {
        if (birthday != null) {
            int ages = DateUtil.calcAge(birthday);
            if (ages == 0 || ages == -1) {
                return DateUtil.calcMonth(birthday) <= 0 ? "1个月" : DateUtil.calcMonth(birthday) + "个月";
            }
            return ages + "岁";
        } else {
            return null;
        }
    }

    /**
     * </p>按用户和病种查找患者</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<PatientStatVO> getByUserAndDisease(PatientStatParam param) {
        if (param.getUserId() != null && param.getDiseaseIds() != null && param.getDiseaseIds().size() > 0) {

            List<PatientStatVO> list = patientStatMapper.getByUserAndDisease(param);
            for (PatientStatVO vo : list) {
                if (vo.getBirthday() != null) {
                    vo.setAge(DateUtil.calcAge(vo.getBirthday()));
                    vo.setAgeStr(getAgeStr(vo.getBirthday()));
                    vo.setBirthday(null);
                }
                if (StringUtil.isNotBlank(vo.getTopPath())) {
                    vo.setTopPath(PropertiesUtil.addUrlPrefix(vo.getTopPath()));
                }
            }
            return list;
        }

        return null;
    }

    /**
     * </p>按病种统计出被医生治疗过用户</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月25日
     */
    public PageVO getUserByDiseaseAndDoctor(PatientStatParam param) {
        // 构造分页
        PageVO page = new PageVO();

        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());

        if (param.getDoctorIds() != null && param.getDoctorIds().size() > 0 &&
                param.getDiseaseIds() != null && param.getDiseaseIds().size() > 0) {

            //获取用户id
            List<Integer> userIds = patientStatMapper.getUserByDiseaseAndDoctor(param);

            if (userIds.size() == 0) {
                page.setPageData(new ArrayList<PatientStatVO>());
                page.setTotal(0L);
            } else {
                // 获取用户信息
                List<BaseUserVO> userList = baseUserService.getByIds(userIds.toArray(new Integer[]{}));

                List<PatientStatVO> list = new ArrayList<PatientStatVO>();
                for (BaseUserVO vo : userList) {
                    PatientStatVO p = new PatientStatVO();
                    p.setId(vo.getUserId());
                    p.setName(vo.getName());
                    p.setAge(vo.getAge());
                    p.setAgeStr(vo.getAgeStr());
                    p.setSex(vo.getSex());
                    p.setTelephone(vo.getTelephone());
                    p.setTopPath(vo.getHeadPicFileName());
                    p.setHeadPicFileName(vo.getHeadPicFileName());
                    list.add(p);
                }

                page.setPageData(list);

                page.setTotal(patientStatMapper.getUserByDiseaseAndDoctorCount(param));
            }
            return page;
        }

        page.setPageData(new ArrayList<PatientStatVO>());
        page.setTotal(0L);
        return page;
    }

    /**
     * </p>获取诊疗记录,通过患者和医生获取</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<PatientStatVO> getCureRecordByDoctor(PatientStatParam param) {
        if (param.getDoctorIds() == null || param.getDoctorIds().size() == 0 || param.getPatientId() == null) {
            return null;
        }
        //List<PatientStatVO> list = patientStatMapper.getCureRecordByDoctor(param);

        // 查询诊疗记录表        
        List<PatientStatVO> list = patientStatMapper.getCureRecordByGroupDoctor(param);

        // 再根据诊疗记录获取订单id，再去查询患者病种表（t_patient_disease）获取病种
        if (list != null && list.size() > 0) {
            Set<Integer> orderIds = Sets.newHashSet();
            for (PatientStatVO vo : list) {
                orderIds.add(vo.getOrderId());
            }

            PatientStatParam dieaseParam = new PatientStatParam();
            dieaseParam.setOrderIds(new ArrayList<>(orderIds));
            //根据订单查询病种
            List<PatientStatVO> tempList = patientStatMapper.getDieaseByGroupDoctor(dieaseParam);

            // 查找医生姓名
            Set<Integer> doctorIds = new HashSet<Integer>();
            for (PatientStatVO vo : list) {
                doctorIds.add(vo.getId());
            }
            List<BaseUserVO> userList = baseUserService.getByIds(doctorIds.toArray(new Integer[]{}));

            for (PatientStatVO vo : list) {
                for (BaseUserVO user : userList) {
                    if (vo.getId().equals(user.getUserId())) {
                        vo.setName(user.getName());
                        break;
                    }
                }
                StringBuffer dieaseNames = new StringBuffer();
                Set<String> dieaseTypeNameSet = Sets.newHashSet();
                for (PatientStatVO pVo : tempList) {
                    if (pVo.getOrderId().equals(vo.getOrderId())) {
                        if (StringUtils.isNotEmpty(pVo.getDiseaseTypeName())) {
                            dieaseTypeNameSet.add(pVo.getDiseaseTypeName());
                            dieaseNames.append(pVo.getDiseaseTypeName()).append(",");
                        }
                    }
                }
                if (dieaseNames != null && dieaseNames.length() > 0) {
                    dieaseNames.setLength(dieaseNames.length() - 1);
                }
                vo.setDiseaseTypeName(dieaseNames.toString());
                vo.setDiseaseTypeNames(dieaseTypeNameSet);
            }

            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * </p>获取诊疗记录,通过患者和病种获取</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<PatientStatVO> getCureRecordByDisease(PatientStatParam param) {
        if (param.getDiseaseIds() == null || param.getDiseaseIds().size() == 0 || param.getPatientId() == null) {
            return null;
        }
        List<PatientStatVO> list = patientStatMapper.getCureRecordByDisease(param);

        // 查找医生姓名
        Set<Integer> doctorIds = new HashSet<Integer>();
        for (PatientStatVO vo : list) {
            doctorIds.add(vo.getId());
        }
        List<BaseUserVO> userList = baseUserService.getByIds(doctorIds.toArray(new Integer[]{}));

        for (PatientStatVO vo : list) {
            for (BaseUserVO user : userList) {
                if (vo.getId().equals(user.getUserId())) {
                    vo.setName(user.getName());
                    break;
                }
            }
        }

        return list;
    }

    /**
     * </p>获取医生给患者打的病种</p>
     *
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    public List<String> getDiseaseByDoctor(List<Integer> doctorIds) {
        if (doctorIds == null || doctorIds.size() == 0) {
            return null;
        }
        return patientStatMapper.getDiseaseByDoctor(doctorIds);
    }

    /**
     * </p>获取医生给患者打的病种</p>
     *
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    public List<String> getDiseaseByDoctorAndGroup(List<Integer> doctorIds, String groupId) {
        if (doctorIds == null || doctorIds.size() == 0) {
            return null;
        }
        StatParam param = new StatParam();
        param.setDoctorIds(doctorIds);
        param.setGroupId(groupId);

        return patientStatMapper.getDiseaseByDoctorAndGroup(param);
    }

    /**
     * 统计  集团 患者库
     *
     * @param param
     * @param id
     * @return
     * @author wangqiao
     * @date 2015年12月30日
     */
    public PageVO statGroupPatient(StatParam param, String id) {
        //参数校验
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        if (param.getType() == null) {
            throw new ServiceException("统计类型为空");
        }
        if ((param.getType() == 2 || param.getType() == 3) && StringUtil.isBlank(id)) {
            throw new ServiceException("数据 id为空");
        }

        //当前页列表数据
        List<StatVO> patientList = new ArrayList<StatVO>();
        List<Integer> idList = new ArrayList<Integer>();
        //数据总数
        long total = 0;

        //1、先查询患者id

        //按照type 区分 统计维度  1：集团；2：组织机构、3：医生 , 5:未分配医生
        if (param.getType() == 1) {
            //统计集团 患者库
            idList = patientStatMapper.statGroupPatient(param);
            total = patientStatMapper.countGroupPatient(param);

        } else if (param.getType() == 2 || param.getType() == 5) {
            //统计 组织下所有医生的患者库
            //统计医生id
            List<Integer> doctorIds = new ArrayList<Integer>();

            if (param.getType() == 2) {
                //按科室查询医生
                List<String> departmentIds = null;
                // 查询子科室
                departmentIds = departmentService.getSubDepartment(param.getGroupId(), id);
                departmentIds.add(id);
                //FIXME  需要拆分
                String[] statuses = new String[]{GroupEnum.GroupDoctorStatus.正在使用.getIndex()};

                doctorIds = dpartmentDoctorDao.getDepartmentDoctorId(param.getGroupId(), departmentIds, statuses);
            } else if (param.getType() == 5) {
                //查询 未分配的医生
                doctorIds = dpartmentDoctorDao.getUndistributedDoctorId(param.getGroupId());
            }

            param.setDoctorIds(doctorIds);
            idList = this.statDoctorsPatient(param);
            total = this.countDoctorsPatient(param);

        } else if (param.getType() == 3) {
            //统计  医生 患者库
            param.setDoctorId(Integer.parseInt(id));
            idList = this.statDoctorPatient(param);
            total = this.countDoctorPatient(param);
        }

        //2、再查询患者详细信息
        if (idList != null && idList.size() > 0) {
            List<BaseUserVO> userList = baseUserService.getByIds(idList.toArray(new Integer[]{}));
            for (BaseUserVO user : userList) {
                StatVO stat = new StatVO();
                stat.setId(user.getUserId());
                stat.setName(user.getName());
                stat.setAge(user.getAge());
                stat.setSex(user.getSex());
                stat.setAgeStr(user.getAgeStr());
                stat.setTelephone(user.getTelephone());
                stat.setHeadPicFileName(user.getHeadPicFileName());

                patientList.add(stat);
            }

        }


        //3、封装成pageVO
        PageVO page = new PageVO();
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(total);
        page.setPageData(patientList);

        return page;
    }


    @Override
    public PageVO queryGroupPatient(StatParam param, String id) {
        //参数校验
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        if (Objects.isNull(param.getType())) {
            throw new ServiceException("统计类型为空");
        }
        if ((param.getType().intValue() == 2 || param.getType().intValue() == 3) && StringUtils.isBlank(id)) {
            throw new ServiceException("数据 id为空");
        }

        //当前页列表数据
        List<StatVO> patientList = Lists.newArrayList();
        List<Integer> idList = Lists.newArrayList();
        //数据总数
        long total = 0l;

        //按照type 区分 统计维度  1：集团；2：组织机构、3：医生 , 5:未分配医生
        if (param.getType().intValue() == 1) {
            idList = patientStatMapper.queryGroupUserByCondition(param);
            total = patientStatMapper.countGroupUserByCondition(param);
        } else if (param.getType().intValue() == 2 || param.getType().intValue() == 5) {
            //统计 组织下所有医生的患者库
            //统计医生id
            List<Integer> doctorIds = Lists.newArrayList();

            if (param.getType().intValue() == 2) {
                //按科室查询医生
                List<String> departmentIds;
                // 查询子科室
                departmentIds = departmentService.getSubDepartment(param.getGroupId(), id);
                departmentIds.add(id);

                String[] statuses = new String[]{GroupEnum.GroupDoctorStatus.正在使用.getIndex()};

                doctorIds = dpartmentDoctorDao.getDepartmentDoctorId(param.getGroupId(), departmentIds, statuses);
            } else if (param.getType().intValue() == 5) {
                //查询 未分配的医生
                doctorIds = dpartmentDoctorDao.getUndistributedDoctorId(param.getGroupId());
            }

            if (CollectionUtils.isEmpty(doctorIds)) {
                idList = null;
                total = 0l;
            } else {
                param.setDoctorIds(doctorIds);
                idList = patientStatMapper.queryGroupUserByCondition(param);
                total = patientStatMapper.countGroupUserByCondition(param);
            }

        } else if (param.getType().intValue() == 3) {
            //统计  医生 患者库
            param.setDoctorId(Integer.parseInt(id));
            idList = patientStatMapper.queryGroupUserByCondition(param);
            total = patientStatMapper.countGroupUserByCondition(param);
        } else if (param.getType().intValue() == 4) {
            //统计病种
            //处理病种id
            List<DiseaseTypeVO> allDiseaseTypes = baseDataDao.getAllDiseaseType();
            if (!CollectionUtils.isEmpty(allDiseaseTypes)) {
                Map<String, DiseaseTypeVO> diseaseTypeVOMap = allDiseaseTypes.stream()
                        .collect(Collectors.toMap(DiseaseTypeVO::getId, (diseaseTypeVO -> diseaseTypeVO)));
                List<DiseaseTypeVO> needDiseaseTypeVos = Lists.newArrayList();
                needDiseaseTypeVos = getAllDiseaseNode(diseaseTypeVOMap, allDiseaseTypes, needDiseaseTypeVos, id);
                if (!CollectionUtils.isEmpty(needDiseaseTypeVos)) {
                    List<String> diseaseIds = needDiseaseTypeVos.stream().map(DiseaseTypeVO::getId).collect(Collectors.toList());
                    param.setDiseaseIds(diseaseIds);
                }
            }

            idList = patientStatMapper.queryGroupUserByDisease(param);
            total = patientStatMapper.countGroupUserByDisease(param);
        }

        if (idList != null && idList.size() > 0) {

            List<Patient> patients = patientMapper.findByIds(idList);

            if (!CollectionUtils.isEmpty(patients)) {
                for (Patient patient : patients) {
                    StatVO stat = new StatVO();
                    stat.setId(patient.getId());
                    stat.setName(patient.getUserName());
                    stat.setAge(patient.getAge());
                    stat.setSex(patient.getSex() == null ? 1 : Integer.valueOf(patient.getSex()));
                    stat.setAgeStr(patient.getAgeStr());
                    stat.setTelephone(patient.getTelephone());
                    stat.setHeadPicFileName(patient.getTopPath());
                    stat.setUserId(patient.getUserId());
                    patientList.add(stat);
                }
            }

        }

        PageVO page = new PageVO();
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(total);
        page.setPageData(patientList);

        return page;
    }

    private List<DiseaseTypeVO> getAllDiseaseNode(Map<String, DiseaseTypeVO> diseaseTypeVOMap, List<DiseaseTypeVO> allDiseaseTypeVOs,
                                                  List<DiseaseTypeVO> result, String diseaseId) {
        DiseaseTypeVO diseaseTypeVO = diseaseTypeVOMap.get(diseaseId);
        if (Objects.nonNull(diseaseId)) {
            result.add(diseaseTypeVO);
            for (DiseaseTypeVO vo : allDiseaseTypeVOs) {
                if (Objects.nonNull(vo) ) {
                    if (StringUtils.equals(vo.getParent(), diseaseId) && vo.isLeaf() == false) {
                        getAllDiseaseNode(diseaseTypeVOMap, allDiseaseTypeVOs, result, vo.getId());
                    } else if (StringUtils.equals(vo.getParent(), diseaseId) && vo.isLeaf()) {
                        result.add(vo);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 查询列表分页  指定集团中 多个医生 相关的患者id
     *
     * @param param
     * @return
     * @author wangqiao
     * @date 2016年1月27日
     */
    private List<Integer> statDoctorsPatient(StatParam param) {
        if (StringUtils.isEmpty(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }

        //sql中批量in的参数不允许为null和size=0
        if (param.getDoctorIds() == null || param.getDoctorIds().size() == 0) {
            List<Integer> doctorIds = new ArrayList<Integer>();
            doctorIds.add(Integer.MIN_VALUE);
            param.setDoctorIds(doctorIds);
        }

        return patientStatMapper.statDoctorsPatient(param);
    }

    /**
     * 查询总数  指定集团中 多个医生 相关的患者id
     *
     * @param param
     * @return
     * @author wangqiao
     * @date 2016年1月27日
     */
    private Integer countDoctorsPatient(StatParam param) {
        if (StringUtils.isEmpty(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }

        //sql中批量in的参数不允许为null和size=0
        if (param.getDoctorIds() == null || param.getDoctorIds().size() == 0) {
            List<Integer> doctorIds = new ArrayList<Integer>();
            doctorIds.add(Integer.MIN_VALUE);
            param.setDoctorIds(doctorIds);
        }

        return patientStatMapper.countDoctorsPatient(param);
    }


    /**
     * 查询列表分页  指定集团 指定医生 相关的患者id
     *
     * @param param
     * @return
     * @author wangqiao
     * @date 2016年1月27日
     */
    private List<Integer> statDoctorPatient(StatParam param) {
        if (StringUtils.isEmpty(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        if (param.getDoctorId() == null || param.getDoctorId() == 0) {
            throw new ServiceException("医生id为空");
        }

        return patientStatMapper.statDoctorPatient(param);
    }

    /**
     * 查询总数，指定集团 指定医生 相关的患者id
     *
     * @param param
     * @return
     * @author wangqiao
     * @date 2016年1月27日
     */
    private Integer countDoctorPatient(StatParam param) {
        if (StringUtils.isEmpty(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        if (param.getDoctorId() == null || param.getDoctorId() == 0) {
            throw new ServiceException("医生id为空");
        }

        return patientStatMapper.countDoctorPatient(param);
    }

    @Override
    public List<Map<String, Object>> patientRegions(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团id为空");
        }
        //联合p_order和t_patient表，查询集团的所有患者的信息，然后解析area字段
        List<String> regions = patientStatMapper.getGroupPatientRegions(groupId);
        Set<String> provinceSet = Sets.newHashSet();
        Set<String> citySet = Sets.newHashSet();

        List<Map<String, Object>> result = Lists.newArrayList();

        if (regions != null && regions.size() > 0) {
            for (String area : regions) {
                if (StringUtils.isNotEmpty(area)) {
                    if (area.contains(" ")) {
                        String[] array = area.split(" ");
                        String province = array[0];
                        String city = array[1];
                        if (StringUtils.isNotEmpty(province)) {
                            provinceSet.add(province);
                        }
                        if (StringUtils.isNotEmpty(province)) {
                            citySet.add(city);
                        }
                    } else {
                        provinceSet.add(area);
                    }
                }
            }

            for (String province : provinceSet) {
                Map<String, Object> region = Maps.newHashMap();
                region.put("province", province);
                Set<String> child = Sets.newHashSet();
                for (String city : citySet) {
                    for (String area : regions) {
                        if (StringUtils.isNotEmpty(area) && area.contains(province) && area.contains(city)) {
                            child.add(city);
                        }
                    }
                }
                region.put("city", child);
                result.add(region);
            }

        }
        Map<String, Object> noneRegion = Maps.newHashMap();
        noneRegion.put("province", "暂无");
        result.add(noneRegion);
        return result;
    }

    @Override
    public List<DiseaseTypeVO> patientDiseases(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团id为空");
        }

        //联合p_order和t_patient表，查询集团的所有患者的信息，然后解析area字段
        List<String> diseaseIds = patientStatMapper.getGroupPatientDiseases(groupId);

        //获取全部的疾病
        List<DiseaseTypeVO> allDiseaseTypeVos = baseDataDao.getAllDiseaseType();

        Map<String, DiseaseTypeVO> allDiseaseTypeMap = Maps.newHashMap();
        allDiseaseTypeVos.forEach((disease) -> {
            allDiseaseTypeMap.put(disease.getId(), disease);
        });

        Set<DiseaseTypeVO> nDiseaseTypeVos = Sets.newHashSet();
        reverseDiseases(nDiseaseTypeVos, diseaseIds, allDiseaseTypeMap);

        List<DiseaseTypeVO> reslut = Lists.newArrayList();
        for (DiseaseTypeVO diseaseTypeVO : nDiseaseTypeVos) {
            if (diseaseTypeVO == null) {
                continue;
            }
            if (diseaseTypeVO != null && StringUtils.equals(diseaseTypeVO.getParent(), "0")) {
                coverDiseases(diseaseTypeVO, nDiseaseTypeVos);
                reslut.add(diseaseTypeVO);
            }
        }

        DiseaseTypeVO diseaseTypeVO = new DiseaseTypeVO();
        //这种表示暂无
//		diseaseTypeVO.setId("-1");
//		diseaseTypeVO.setName("暂无");
//		diseaseTypeVO.setLeaf(true);
//		diseaseTypeVO.setParent("-1");
//		diseaseTypeVO.setChildren(Lists.newArrayList());
//		diseaseTypeVO.setWeight(0);
//		reslut.add(diseaseTypeVO);

        return reslut;
    }

    void reverseDiseases(Set<DiseaseTypeVO> result, List<String> diseaseIds, Map<String, DiseaseTypeVO> allDiseaseTypeMap) {
        if (diseaseIds != null && diseaseIds.size() > 0) {
            diseaseIds.forEach((diseaseId) -> {
                if (StringUtils.isNotEmpty(diseaseId)) {
                    DiseaseTypeVO diseaseTypeVO = allDiseaseTypeMap.get(diseaseId);
                    result.add(diseaseTypeVO);
                    if (diseaseTypeVO != null && !StringUtils.equals("0", diseaseTypeVO.getParent())) {
                        List<String> parentDiseaseIds = Lists.newArrayList();
                        parentDiseaseIds.add(diseaseTypeVO.getParent());
                        reverseDiseases(result, parentDiseaseIds, allDiseaseTypeMap);
                    }
                }

            });
        }
    }

    void coverDiseases(DiseaseTypeVO diseaseTypeVO, Set<DiseaseTypeVO> inAllDiseaseTypeVos) {
        if (inAllDiseaseTypeVos != null && inAllDiseaseTypeVos.size() > 0) {
            List<DiseaseTypeVO> child = Lists.newArrayList();
            for (DiseaseTypeVO tempDiseaseTypeVo : inAllDiseaseTypeVos) {
                if (tempDiseaseTypeVo == null) {
                    continue;
                }
                if (StringUtils.equals(tempDiseaseTypeVo.getParent(), diseaseTypeVO.getId())) {
                    coverDiseases(tempDiseaseTypeVo, inAllDiseaseTypeVos);
                    child.add(tempDiseaseTypeVo);
                }
            }
            diseaseTypeVO.setChildren(child);
        }
    }

    @Override
    public PageVO patientInfos(String groupId, String province, String city, String[] diseaseTypeId, Integer pageIndex, Integer pageSize) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团id为空");
        }

        PatientStatParam param = new PatientStatParam();
        param.setPageIndex(pageIndex);
        param.setPageSize(pageSize);
        param.setGroupId(groupId);

        if (diseaseTypeId != null && diseaseTypeId.length > 0) {
            List<String> diseaseIds = Lists.newArrayList();
            diseaseIds.addAll(Arrays.asList(diseaseTypeId));
            param.setDiseaseIds(diseaseIds);
        }

        List<PatientStatVO> patientStatVOs = Lists.newArrayList();
        Integer count = 0;

        StringBuffer areaBuffer = new StringBuffer();
        if (StringUtils.isNotEmpty(province)) {
            if (StringUtils.equals(province, "暂无")) {
                param.setArea("暂无");
            } else {
                areaBuffer.append(province);
                if (StringUtils.isNotEmpty(city)) {
                    areaBuffer.append(" ").append(city);
                }

                if (StringUtils.isNotEmpty(areaBuffer.toString())) {
                    param.setArea(areaBuffer.toString());
                }

            }
        }

        patientStatVOs = patientStatMapper.getGroupPatientInfos(param);
        count = patientStatMapper.getGroupPatientInfosCount(param);

        List<Integer> patiendIds = Lists.newArrayList();
        if (patientStatVOs != null && patientStatVOs.size() > 0) {
            patientStatVOs.forEach((patient) -> {
                if (patient.getBirthday() != null) {
                    patient.setAge(DateUtil.calcAge(patient.getBirthday()));
                    patient.setAgeStr(getAgeStr(patient.getBirthday()));
                }
                patiendIds.add(patient.getId());
            });

            PatientStatParam diseaseParam = new PatientStatParam();
            diseaseParam.setGroupId(groupId);
            diseaseParam.setPatientIds(patiendIds);

            List<DiseaseInfoVo> diseaseInfoVos = patientStatMapper.getDiseaseNames(diseaseParam);

            if (diseaseInfoVos != null && diseaseInfoVos.size() > 0) {
                patientStatVOs.forEach((patient) -> {
                    Set<String> diseaseName = Sets.newHashSet();
                    diseaseInfoVos.forEach((diseaseInfo) -> {
                        if (patient.getId().intValue() == diseaseInfo.getPatientId().intValue() && StringUtils.isNoneEmpty(diseaseInfo.getDiseaseTypeName())) {
                            diseaseName.add(diseaseInfo.getDiseaseTypeName());
                        }
                    });
                    patient.setDiseaseTypeNames(diseaseName);
                });
            }
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageData(patientStatVOs);
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(Long.valueOf(count));
        return pageVO;
    }

    @Override
    public Integer patientCount(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团id为空");
        }
        Integer count = patientStatMapper.getGroupPatientCount(groupId);
        return count;
    }


}
