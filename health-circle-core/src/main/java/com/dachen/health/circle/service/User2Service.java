package com.dachen.health.circle.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.circle.form.AreaInfo;
import com.dachen.health.circle.vo.MobileCircleHomeVO;
import com.dachen.health.circle.vo.MobileDoctorBriefVo;
import com.dachen.health.circle.vo.MobileDoctorHomeVO;
import com.dachen.health.circle.vo.MobileDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface User2Service extends IntegerServiceBase {

    List<User> findDoctorListByIds(List<Integer> userIdList);

    List<User> findBaseList(Integer userId);

    User findAndCheckDoctor(Integer doctorId);
    List<User> findByHospitalAndDeptAndVO(String hospitalId, String deptId);
    List<MobileDoctorVO> findByHospitalAndDeptAndVO(String hospitalId, String deptId, List<Integer> excludeIdList);
    List<User> findByHospitalAndDept(String hospitalId, String deptId, List<Integer> excludeIdList);

    User findDoctorById(Integer userId);

    List<User> findDoctorByIds(List<Integer> doctorIdList);
    List<User> findDoctorByIds(List<Integer> doctorIdList, String kw);

    MobileDoctorVO getInfo(Integer userId);

    int countExpert(List<Integer> doctorIdList);
    int countTotalCure(List<Integer> doctorIdList);

    /**
     * 根据圈子id,医联体id 查询正常的用户id 医生id=userId
     * @param doctorByIds
     * @return 所有正常的用户id
     */
    List<Integer> getNormalUserIdByDoctorByIds(List<Integer> doctorByIds);

    /**
     * 根据圈子id,医联体id 查询正常的用户id 医生id=userId
     * @param groupIds
     * @param unionIds
     * @return 所有正常的用户id
     */
    Long getNormalUserIdByUnionIdAndGroupId(List<String> groupIds,List<String> unionIds);

    /**
     * @param provinceJsons 省 市id
     * @param levels 医院级别
     * @param deptIds 科室id
     * @param titles 职称
     * @return   正常的用户id
     */
    Long getNormalUserIdByCityAndLevelAndDepartmentsAndTitle(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles);


    /**
     * 获取所有正常的用户id
     * @return 正常的用户id
     */
    Long getNormalUser();
    /**
     * 根据圈子id,医联体id 查询正常的用户id 医生id=userId
     * @param groupIds
     * @param unionIds
     * @return 所有正常的用户id
     */
    Pagination<Integer> getNormalUserIdByUnionIdAndGroupIdPage(List<String> groupIds,List<String> unionIds, Integer pageIndex, Integer pageSize);

    /**
     * @param provinceJsons 省 市id
     * @param levels 医院级别
     * @param deptIds 科室id
     * @param titles 职称
     * @return   正常的用户id
     */
    Pagination<Integer> getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles, Integer pageIndex, Integer pageSize);


    /**
     * 获取所有正常的用户id
     * @return 正常的用户id
     */
    Pagination<Integer> getNormalUserPage(Integer pageIndex, Integer pageSize);


    Pagination<MobileDoctorBriefVo> searchUserByKeyWord(Integer userId, String keyWord, Integer pageSize, Integer pageIndex);

    MobileDoctorHomeVO getDoctorHomePage(Integer userId, Integer doctorId);

    List<User> getNormalUserList();

    MobileCircleHomeVO getCircleIndex(Integer userId);

    Pagination<User> getInfoByUserIds(List<Integer> userIds, Integer pageSize, Integer pageIndex);

    /**
     *
     * @param userCheck  是否筛选已认证用户
     * @return
     */
    Long getNormalUser(boolean userCheck);

    /**
     * @param provinceJsons 省 市id
     * @param levels 医院级别
     * @param deptIds 科室id
     * @param titles 职称
     * @return   正常的用户id
     */
    Long getNormalUserIdByCityAndLevelAndDepartmentsAndTitle(String provinceJsons, List<String> levels, List<String> deptIds, List<String> titles,boolean userCheck);

    /**
     * 获取所有正常的用户id
     * @return 正常的用户id
     */
    Pagination<Integer> getNormalUserPage(Integer pageIndex, Integer pageSize,boolean userCheck);

    /**
     * @param provinceJsons 省 市id
     * @param levels 医院级别
     * @param deptIds 科室id
     * @param titles 职称
     * @return   正常的用户id
     */
    Pagination<Integer> getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(String provinceJsons, List<String> levels,
                                                                                List<String> deptIds, List<String> titles,boolean userCheck, Integer pageIndex, Integer pageSize);

}
