package com.dachen.circle.api.client.group.proxy;

import com.dachen.circle.api.client.BaseCircleApiClientProxy;
import com.dachen.circle.api.client.group.entity.CGroup;
import com.dachen.circle.api.client.group.entity.CUserGroupAndUnionIdMap;
import com.dachen.circle.api.client.group.entity.CUserGroupAndUnionMap;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CircleGroupApiClientProxy extends BaseCircleApiClientProxy {

    /**
     * 获取用户的组织信息
     *
     * @param userId
     * @return
     * @throws HttpApiException
     */
    public CUserGroupAndUnionMap findUserGroupAndUnionMap(Integer userId) throws HttpApiException {
        Map<String, String> params = new HashMap<>(1);
        params.put("userId", userId.toString());
        try {
            String url = "circle/group/findUserGroupAndUnionMap";
            CUserGroupAndUnionMap ret = this.openRequest(url, params, CUserGroupAndUnionMap.class);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取用户的组织信息（只含id）
     * @param userId
     * @return
     * @throws HttpApiException
     */
    public CUserGroupAndUnionIdMap findUserGroupAndUnionIdMap(Integer userId) throws HttpApiException {
        Map<String, String> params = new HashMap<>(1);
        params.put("userId", userId.toString());
        try {
            String url = "circle/group/findUserGroupAndUnionIdMap";
            CUserGroupAndUnionIdMap ret = this.openRequest(url, params, CUserGroupAndUnionIdMap.class);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取科室分页数据
     *
     * @param keyword
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws HttpApiException
     */
    public Pagination<CGroup> findDeptPage(String keyword, Integer pageIndex, Integer pageSize) throws HttpApiException {
        Map<String, String> params = new HashMap<>(3);
        this.putIfNotBlank(params, "kw", keyword);
        params.put("pageIndex", pageIndex.toString());
        params.put("pageSize", pageSize.toString());
        try {
            String url = "circle/group/findDeptPage";
            Pagination<CGroup> ret = this.openRequest(url, params, Pagination.class);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取用户当前所属的科室
     *
     * @param userId
     * @return
     * @throws HttpApiException
     */
    public CGroup findDeptByUser(String userId) throws HttpApiException {
        try {
            String url = "circle/group/findDeptByUser/{userId}";
            CGroup ret = this.openRequest(url, CGroup.class, userId.toString());
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取科室详情
     *
     * @param id
     * @return
     * @throws HttpApiException
     */
    public CGroup findById(String id) throws HttpApiException {
        try {
            String url = "circle/group/findById/{id}";
            CGroup ret = this.openRequest(url, CGroup.class, id.toString());
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取科室的管理员id列表
     * @param id
     * @return
     * @throws HttpApiException
     */
    public List<Integer> findManagerIdList(String id) throws HttpApiException {
        try {
            String url = "circle/group/findManagerIdList/{id}";
            List<Integer> ret = this.openRequestList(url, Integer.class, id.toString());
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

    /**
     * 获取科室或医联体的所有医生id列表
     * @param groupIdList
     * @param unionIdList
     * @return
     * @throws HttpApiException
     */
    public List<Integer> findDoctorIdList(List<String> groupIdList, List<String> unionIdList) throws HttpApiException {
        Map<String, String> params = new HashMap<>(2);
        this.putArrayIfNotBlank(params, "groupIdList", groupIdList);
        this.putArrayIfNotBlank(params, "unionIdList", unionIdList);
        try {
            String url = "circle/group/findDoctorIdList";
            List<Integer> ret = this.postRequestList(url, params, Integer.class);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }
    /**
     * 根据userId查询 出关注的所有科室的所有人员 （去重）
     * @param userId
     * @return
     */
    public Set<Integer> getDeptUserByUserId(Integer userId) throws HttpApiException {
        try {
            String url = "circle/group/getDeptUserByUserId/{userId}";
            Set<Integer> ret = this.openRequest(url, Set.class, userId.toString());
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }
    /**
     * 根据userId 查询 粉丝的人员id列表
     * @param userId
     * @return
     */
    public List<Integer> getDoctorFansListByUserId(Integer userId) throws HttpApiException {
        try {
            String url = "circle/group/getDoctorFansListByUserId/{userId}";
            List<Integer> ret = this.openRequest(url, List.class, userId);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }
    /**
     * 根据userId 查询 关注的人员id列表
     * @param userId
     * @return
     */
    public List<Integer> getDoctorFollowListByUserId(Integer userId) throws HttpApiException {
        try {
            String url = "circle/group/getDoctorFollowListByUserId/{userId}";
            List<Integer> ret = this.openRequest(url, List.class, userId);
            return ret;
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

}
