package com.dachen.health.pack.dynamic.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.dynamic.entity.param.DynamicParam;


public interface IDynamicService {

    /**
     * 查询该集团和所有集团医生的动态列表
     *
     * @param groupId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO getGroupAndDoctorDynamicListByGroupId(String groupId, Integer pageIndex, Integer pageSize);


    /**
     * 患者查询的 相关医生的动态以及该医生对应的集团动态列表
     *
     * @param groupId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO getPatientRelatedDynamicList(Integer userId, Long createTime, Integer pageSize);

    /**
     * 新增医生动态
     *
     * @param content
     * @param imageList
     */
    void addDoctorDynamic(Integer userId, String content, String[] imageList, Integer[] userIds);

    /**
     * 删除医生动态
     *
     * @param id
     */
    void deleteDoctorDynamic(String id);

    /**
     * 查询医生的动态列表
     *
     * @param userId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO getDoctorDynamicList(Integer userId, Integer pageIndex, Integer pageSize);

    PageVO getMyDynamicList(Integer doctorId, Integer pageIndex, Integer pageSize);


    /**
     * 新增医生动态
     *
     * @param content
     * @param imageList
     */
    void addDoctorDynamicForWeb(DynamicParam param);


    void addGroupDynamicForWeb(DynamicParam param);

    PageVO getDynamicListByGroupIdForWeb(String groupId, Integer pageIndex, Integer pageSize);

}
