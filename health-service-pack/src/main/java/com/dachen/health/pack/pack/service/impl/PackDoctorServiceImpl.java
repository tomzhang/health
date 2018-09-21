package com.dachen.health.pack.pack.service.impl;

import com.dachen.commons.constants.UserSession;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.entity.po.PackDoctorExample;
import com.dachen.health.pack.pack.entity.po.PackDoctorGroup;
import com.dachen.health.pack.pack.mapper.PackDoctorMapper;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.util.ReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class PackDoctorServiceImpl implements IPackDoctorService {

	@Autowired
	protected PackDoctorMapper packDoctorMapper;

	@Autowired
	private IDoctorService doctorService;

	@Override
	public int insert(PackDoctor tmp) {
		int ret = this.packDoctorMapper.insert(tmp);
		return ret;
	}

	@Override
	public void insertBatch(List<PackDoctor> tmpList) {
		if (CollectionUtils.isEmpty(tmpList)) {
			return;
		}
		
		for(PackDoctor tmp:tmpList) {
			this.packDoctorMapper.insert(tmp);
		}
	}

	@Override
	public List<PackDoctor> findByPackId(Integer packId) {
		PackDoctorExample example = new PackDoctorExample();
		example.createCriteria().andPackIdEqualTo(packId);
		List<PackDoctor> packDoctorList = packDoctorMapper.selectByExample(example);
		return packDoctorList;
	}
	
	@Override
	public List<Integer> findDoctorIdListByPackId(Integer packId) {
		List<PackDoctor> packDoctorList = this.findByPackId(packId);
		if (CollectionUtils.isEmpty(packDoctorList)) {
			return null;
		}
		
		List<Integer> doctorIdList = new ArrayList<Integer>(packDoctorList.size());
		for (PackDoctor packDoctor : packDoctorList) {
			doctorIdList.add(packDoctor.getDoctorId());
		}
		return doctorIdList;
	}

	@Override
	public int deleteByPackId(Integer packId) {
		PackDoctorExample example = new PackDoctorExample();
		example.createCriteria().andPackIdEqualTo(packId);
		return packDoctorMapper.deleteByExample(example);
	}

	@Override
	public List<PackDoctor> findAndDeleteByPackId(Integer packId) {
		// 1. 先查询
		List<PackDoctor> packDoctorList = this.findByPackId(packId);
		
		// 2. 再删除（需要事务）
		this.deleteByPackId(packId);
		
		// 3. 返回查询的结果
		return packDoctorList;
	}

	@Override
	public List<PackDoctorGroup> groupByPackIdList(List<Integer> packIdList) {
		if (CollectionUtils.isEmpty(packIdList)) {
			return null;
		}
		
		PackDoctorExample example = new PackDoctorExample();
		example.createCriteria().andPackIdIn(packIdList);
		List<PackDoctorGroup> packDoctorGroups = packDoctorMapper.groupByExample(example);
		return packDoctorGroups;
	}

	@Override
	public List<Integer> getDoctorByGoodsGroupId(List<String> goodsGroupIds) {

		List<Integer> doctorIds=packDoctorMapper.getDoctorByGoodsGroupId(goodsGroupIds);

		//Query<User> q = userRepository.getDoctorQuery(doctorIds, hospitalIds, param.getSpecialistId());

		//List<DoctorStatVO> vos = getDoctorStats(param, q);
		List<Object> rest=new ArrayList<>();
		for(Integer doctorId:doctorIds){

			Map<String,Object> map=doctorService.getIntro(doctorId);
			UserSession userSession= ReqUtil.instance.getUser(doctorId);
			map.put("headUrl",userSession.getHeadImgPath());
			map.put("doctorId",doctorId);
			rest.add(map);

		}

		return doctorIds;
	}
	
	@Resource
	protected UserRepository userRepository;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<DoctoreRatioVO> getDoctorRatiosByPack(Integer packId, Integer mainDoctorId) {
		String tag = "getDoctorRatiosByPack";
		logger.info("{}. packId={}, mainDoctorId={}", tag, packId, mainDoctorId);
		
		List<PackDoctor> packDoctorList = this.findByPackId(packId);
		if (CollectionUtils.isEmpty(packDoctorList)) {
			return null;
		}
		
		logger.info("{}. packDoctorList.size()={}", tag, packDoctorList.size());
		
		List<Integer> doctorIdList = new ArrayList<Integer>(packDoctorList.size());
		for (PackDoctor packDoctor : packDoctorList) {
			doctorIdList.add(packDoctor.getDoctorId());
		}
		logger.info("{}. doctorIdList={}", tag, doctorIdList);
		
		// 注意要与packService.findUserInfoByPack返回顺序保持一致，即与userRepository.findDoctorListByDoctorUserIds返回顺序为准
		List<User> doctorUserList = this.userRepository.findDoctorListByDoctorUserIds(doctorIdList);
		logger.info("{}. doctorUserList.size()={}", tag, null == doctorUserList?0:doctorUserList.size());
		
		List<DoctoreRatioVO> list = new ArrayList<DoctoreRatioVO>(packDoctorList.size());
		for (User doctorUser:doctorUserList) {
			logger.info("{}. doctorUser.getUserId()={}", tag, doctorUser.getUserId());
			for (PackDoctor packDoctor : packDoctorList) {
				logger.info("{}. packDoctor={}", tag, packDoctor);
				if (doctorUser.getUserId().equals(packDoctor.getDoctorId())) {
					DoctoreRatioVO doctoreRatioVO = new DoctoreRatioVO();
					doctoreRatioVO.setRatioNum(packDoctor.getSplitRatio());
					doctoreRatioVO.setReceiveRemind(packDoctor.getReceiveRemind());
					doctoreRatioVO.setUserId(packDoctor.getDoctorId());
					if (mainDoctorId.intValue() == packDoctor.getDoctorId().intValue()) {
						doctoreRatioVO.setGroupType(1);	// 主医生
					}
					doctoreRatioVO.setDoctoreName(doctorUser.getName());
					doctoreRatioVO.setDoctorePic(doctorUser.getHeadPicFileName());
					
					if (1 == doctoreRatioVO.getGroupType()) {
		                list.add(0, doctoreRatioVO); // 主医生排在第1位
		            } else {
		                list.add(doctoreRatioVO);
		            }

					break;
				}
			}
		}
		
		return list;
	}
	
}
