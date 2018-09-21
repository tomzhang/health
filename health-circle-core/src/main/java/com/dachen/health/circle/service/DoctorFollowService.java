package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.DoctorFollow;
import com.dachen.health.circle.vo.MobileDoctorFollowVO;
import com.dachen.sdk.page.Pagination;

import java.util.List;

/**
 * 医生的关注
 * Created By lim
 * Date: 2017/7/10
 * Time: 11:06
 */
public interface DoctorFollowService {
    DoctorFollow addFollow(Integer doctorId,Integer userId);
    int removeFollow(Integer doctorId, Integer userId);
    Pagination<MobileDoctorFollowVO> getDoctorFollowPage(Integer userId,Integer doctorId, Integer pageSize, Integer pageIndex);

    Pagination<MobileDoctorFollowVO> getDoctorFansPage(Integer userId,Integer doctorId, Integer pageSize, Integer pageIndex);

    List<Integer> getDoctorFansList(Integer userId);

    List<Integer> getDoctorFollowList(Integer userId);

    Long countFollowByUserId(Integer userId);

    Long countFanByUserId(Integer userId);

    boolean whetherFollow(Integer userId, Integer doctorId);
}
