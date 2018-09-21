package com.dachen.health.pack.pack.service;

import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.entity.po.PackDoctorGroup;

import java.util.List;

/**
 * Created by xiaowei on 2016/9/8.
 */
public interface IPackDoctorService {
	
	int insert(PackDoctor tmp);
	
	void insertBatch(List<PackDoctor> tmpList);
	
    List<PackDoctor> findByPackId(Integer packId);
    List<Integer> findDoctorIdListByPackId(Integer packId);
    
    List<PackDoctor> findAndDeleteByPackId(Integer packId);
    
    int deleteByPackId(Integer packId);
    
    List<PackDoctorGroup> groupByPackIdList(List<Integer> packIdList);

    List<Integer> getDoctorByGoodsGroupId(List<String> ids);

	List<DoctoreRatioVO> getDoctorRatiosByPack(Integer packId, Integer mainDoctorId);
}
