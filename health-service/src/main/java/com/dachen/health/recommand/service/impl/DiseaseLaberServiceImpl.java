package com.dachen.health.recommand.service.impl;

import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.entity.DiseaseLaber;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.recommand.dao.IDiseaseLaberDao;
import com.dachen.health.recommand.entity.vo.DiseaseLaberCountVo;
import com.dachen.health.recommand.service.IDiseaseLaberService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by liangcs on 2016/12/5.
 */

@Service
public class DiseaseLaberServiceImpl implements IDiseaseLaberService {

	@Autowired
	private IDiseaseLaberDao laberDao;

	@Autowired
	private DiseaseTypeRepository diseaseTypeRepository;


	@Override
	public void addLaber(List<String> diseaseIds) {

		if (CollectionUtils.isEmpty(diseaseIds)) {
			return;
		}
		
		//删除所有原有标签
		laberDao.delAllLaber();
		
		for(String diseaseId : diseaseIds) {
			if (StringUtil.isEmpty(diseaseId)) {
				return;
			}
			
			DiseaseLaber entity = new DiseaseLaber();
			entity.setDiseaseId(diseaseId);
			entity.setStatus(1);
			entity.setCreateTime(System.currentTimeMillis());
			entity.setUpdateTime(System.currentTimeMillis());
			laberDao.save(entity);
		}

	}

	@Override
	public List<DiseaseTypeVO> getLaberTree() {

		List<DiseaseTypeVO> vos = new ArrayList<>();

		List<String> diseaseIds = laberDao.getDiseaseIdsByStatus(1);
		if (CollectionUtils.isEmpty(diseaseIds)) {
			return vos;
		}

		vos = diseaseTypeRepository.buildTypeTreeByIds(diseaseIds);
		markFollowSign(vos);

		Collections.sort(vos, new Comparator<DiseaseTypeVO>() {

			@Override
			public int compare(DiseaseTypeVO o1, DiseaseTypeVO o2) {
				return o2.getWeight() - o1.getWeight();
			}
		});
		
		return vos;
	}

	private void markFollowSign(List<DiseaseTypeVO> types) {
		for (DiseaseTypeVO type : types) {
			if (type.isLeaf() == false) {
				markFollowSign(type.getChildren());
			} else {
				UserDiseaseLaber laber = laberDao.findByUserIdAndDiseaseId(ReqUtil.instance.getUserId(), type.getId());
				if (null == laber || (null != laber.getFromTreat() && true == laber.getFromTreat())) {
					type.setFollowed(0);
				}else {
					type.setFollowed(1);
				}
			}
		}
	}

	@Override
	public Map<String, Integer> checkFollow() {
		Integer userId = ReqUtil.instance.getUserId();

		List<UserDiseaseLaber> labers = laberDao.findByUserId(userId);

		Map<String, Integer> map = Maps.newHashMap();
		if (CollectionUtils.isEmpty(labers)) {
			
			//插入一条空记录，表示用户已推荐过关注
			UserDiseaseLaber laber = new UserDiseaseLaber();
			laber.setUserId(ReqUtil.instance.getUserId());
			laber.setCreateTime(System.currentTimeMillis());
			laber.setStatus(1);
			laberDao.saveUserLaber(laber);
			
			map.put("check", 0);
		} else {
			map.put("check", 1);
		}
		return map;
	}

	@Override
	public void addUserLaber(List<String> ids) {

		if (CollectionUtils.isEmpty(ids)) {
			return;
		}

		Integer userId = ReqUtil.instance.getUserId();
		for (String id : ids) {

			UserDiseaseLaber userDiseaseLaber = laberDao.findByUserIdAndDiseaseId(userId, id);
			if (null != userDiseaseLaber) {
				//问诊时自动添加的疾病,设为可见并更新权重
				if (userDiseaseLaber.getWeight().intValue() == 2) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("weight", 3);
					map.put("fromTreat", false);
					map.put("createTime", System.currentTimeMillis());
					laberDao.updateUserLaber(userDiseaseLaber.getId(), map);
				}
				continue;
			}
			DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(id); 
			if (null == diseaseType) {
				continue;
			}

			UserDiseaseLaber laber = new UserDiseaseLaber();
			laber.setUserId(userId);
			laber.setDiseaseId(id);
			laber.setParentId(diseaseType.getParent());
			laber.setCreateTime(System.currentTimeMillis());
			laber.setStatus(1);
			//患者自行关注，权重为1
			laber.setWeight(1);
			laber.setFromTreat(false);
			laberDao.saveUserLaber(laber);
		}

	}

	@Override
	public List<DiseaseType> getMyLabers() {

		List<DiseaseType> result = Lists.newArrayList();
		List<UserDiseaseLaber> labers = laberDao.sortByCreateTime(ReqUtil.instance.getUserId());

		if (!CollectionUtils.isEmpty(labers)) {
			for(UserDiseaseLaber laber : labers) {
				if (null != laber.getFromTreat() && true == laber.getFromTreat() || StringUtil.isEmpty(laber.getDiseaseId())) {
					continue;
				}
				result.add(diseaseTypeRepository.findByDiseaseId(laber.getDiseaseId()));
			}
		}
		return result;
	}

	@Override
	public void delUserLaber(List<String> diseaseIds) {
		if (CollectionUtils.isEmpty(diseaseIds)) {
			return;
		}
		laberDao.delUserLaber(diseaseIds);
		
	}

	@Override
	public List<DiseaseType> getAllLabers() {
		List<String> ids = laberDao.getDiseaseIdsByStatus(1);
		
		return diseaseTypeRepository.findByIds(ids);
	}

	@Override
	public void addLaberByTreat(Integer userId, String id) {

		if (StringUtil.isEmpty(id)) {
			return;
		}

		if (userId == null) {
			userId = ReqUtil.instance.getUserId();
		}

		UserDiseaseLaber userDiseaseLaber = laberDao.findByUserIdAndDiseaseId(userId, id);
		if (null != userDiseaseLaber ) {
			if (userDiseaseLaber.getWeight() == 3 || userDiseaseLaber.getWeight() == 2) {
				return;
			}else {
				
				//患者自行关注后，又看过对应疾病，权重设为3
				Map<String, Object> map = Maps.newHashMap();
				map.put("weight", 3);
				laberDao.updateUserLaber(userDiseaseLaber.getId(), map);
				return;
			}
		}
		
		DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(id); 
		if (null == diseaseType) {
			return;
		}
		
		UserDiseaseLaber laber = new UserDiseaseLaber();
		laber.setUserId(userId);
		laber.setDiseaseId(id);
		laber.setParentId(diseaseType.getParent());
		laber.setCreateTime(System.currentTimeMillis());
		laber.setStatus(1);
		laber.setFromTreat(true);
		// 治疗时添加，权重为2
		laber.setWeight(2);
		laberDao.saveUserLaber(laber);

	}

	@Override
	public List<DiseaseLaberCountVo> getWebLaber() {
		List<DiseaseLaber> diseaseLabers=laberDao.getDiseaseLaber();
		List<DiseaseLaberCountVo> vos=new ArrayList<>();
		for(DiseaseLaber diseaseLaber:diseaseLabers){
			DiseaseLaberCountVo vo=new DiseaseLaberCountVo();
			vo.setDiseaseId(diseaseLaber.getDiseaseId());
			vo.setDiseaseName(diseaseTypeRepository.findByIds(diseaseLaber.getDiseaseId()).getName());
			//需要查询改疾病有多少用户关注
			Long count=laberDao.getUserLaberByDiseaseId(diseaseLaber.getDiseaseId());
			if(count.intValue()==0){
				continue;
			}
			vo.setCount(laberDao.getUserLaberByDiseaseId(diseaseLaber.getDiseaseId()));
			vos.add(vo);
		}
		return vos;
	}
}
