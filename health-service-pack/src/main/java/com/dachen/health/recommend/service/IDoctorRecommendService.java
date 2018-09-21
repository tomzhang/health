package com.dachen.health.recommend.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.health.recommend.entity.param.DoctorRecommendParam;
import com.dachen.health.recommend.entity.vo.DoctorRecommendVO;

public interface IDoctorRecommendService {
	
	/**
	 * 根据集团Id，医生Id(前端传过来) 去c_group_docotr表里获取对应Id
	 * 根据groupId去t_doctor_recommend表查权重
	 * 设置属性保存到数据库
	 * 返回保存后的vo
	 * @param param
	 * @return
	 */
	DoctorRecommendVO createDoctorRecommend(DoctorRecommendParam param);
	
	/**
	 * 运营平台批量添加多个医生推荐
	 * @param param
	 * @return
	 */
	void createRecommendDoctorForPlatform(DoctorRecommendParam param);
	
	/**
	 * 删除当前纪录，然后删除对应的document
	 * @param id
	 * @return
	 */
	Map<String ,Object> delDoctorRecommend(String id);
	
	
	/**
	 * 获取名医推荐列表 isApp=true,获取显示的推荐名医，isApp=false获其它,获取所有推荐名医
	 * @param groupId
	 * @param isApp
	 * @return
	 */
	PageVO getRecommendDoctorList(DoctorRecommendParam param);
	
	/**
	 * 根据ID设置或者取消名医推荐
	 * @param param
	 * @return
	 */
	Map<String,Object> setRecommend(DoctorRecommendParam param);
	
	DocumentVO getRecommendDoc(String recommendId);

	Map<String, Object> upWeight(String id);
	
	PageVO getDoctorsByKeyword(String keyword, Integer pageIndex, Integer pageSize);
	
	List<Integer> getDoctorIdsByGroup(String groupId);
}
