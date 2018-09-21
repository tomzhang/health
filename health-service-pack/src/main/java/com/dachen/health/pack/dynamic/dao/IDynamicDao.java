package com.dachen.health.pack.dynamic.dao;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.dynamic.entity.param.DynamicParam;
import com.dachen.health.pack.dynamic.entity.po.Dynamic;


/**
 * 动态
 *
 * @author weilit
 */
public interface IDynamicDao {


    /**
     * 查询动态
     *
     * @param id
     * @return
     */
    Dynamic getDynamic(String id);


    /**
     * 查询动态列表
     *
     * @return
     */
    List<Dynamic> getDynamicListByUserId(Integer userId);

    /**
     * 删除动态
     *
     * @param id
     */
    void deleteDynamic(String id);


    /**
     * 保存动态
     *
     * @param param
     */
    void saveDynamic(DynamicParam param);


    /**
     * 查询集团的动态列表
     *
     * @return
     */
    List<Dynamic> getDynamicListByGroupId(String groupId);


    /**
     * 保存动态
     *
     * @param param
     */
//    public String getGroupLogoUrlById(String id);

    PageVO getDynamicListByGroupIdForWeb(String groupId, Integer pageIndex, Integer pageSize);

    PageVO getDoctorDynamicList(Integer userId, Integer currentUserId, Integer pageIndex, Integer pageSize);

    PageVO getMyDynamicList(Integer doctorId, Integer pageIndex, Integer pageSize);

    PageVO getGroupAndDoctorDynamicListByGroupId(String groupId, Integer currentUserId, Integer pageIndex, Integer pageSize);

    PageVO getPatientRelatedDynamicList(Integer userId, Integer currentUserId, Long createTime, Integer pageSize);


    //查询患者对应的集团
    List<String> getPatientGroupListByUserId(Integer userId);

}
