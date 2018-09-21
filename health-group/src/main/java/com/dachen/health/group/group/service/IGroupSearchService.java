package com.dachen.health.group.group.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.RecommDiseaseVO;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;

import java.util.List;

public interface IGroupSearchService {
	
    /**
     * </p>获取全部医生集团</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findAllGroup(GroupSearchParam param);

    /**
     * </p>搜索医生集团（集团名／医生名／病种 ）</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findeGroupByKeyWord(GroupSearchParam param);

    /**
     * </p>搜索医生（集团名／医生名／病种 ）</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findDoctoreByKeyWord(GroupSearchParam param);

    /**
     * </p>根据病种搜索医生集团</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findGroupByDisease(GroupSearchParam param);

	/**
	 * 根据病种集合搜索医生集团
	 * @param diseaseIds
	 * @param pageIndex
	 * @param pageSize
     * @return
     */
	List<GroupSearchVO> findGroupByDiseaseIds(List<String> diseaseIds,int pageIndex,int pageSize );

    /**
     * </p>根据病种搜索医生</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findDoctorByDisease(GroupSearchParam param);
    
    
	/**
	 * 获取医生集团病种
	 * @param param
	 */
	List<RecommDiseaseVO> findRecommDisease(GroupSearchParam param);
	
	/**
	 * 根据医生集团ID 获取集团基本信息
	 * @param param
	 */
	GroupSearchVO findGroupBaseInfo(GroupSearchParam param);
	
	/**
	 * 根据集团ID返回专家信息
	 * 
	 */
	PageVO findDoctorByGroup(GroupSearchParam param);
	
	/**
	 * 根据集团或科室返回专家信息
	 * @param param
	 * @return
	 */
	PageVO findProDoctorByGroupId(GroupSearchParam param);
	
	
	List<RecommDiseaseVO> findRecommDiseaseByGroup(GroupSearchParam param);
	
	/**
	 * 根据集团或科室获取在线医生信息
	 * @param param
	 * @return
	 */
	public PageVO findDoctorOnlineByGroup(GroupSearchParam param);
	
    /**
     * 搜索医生可加入的医生集团
     * @param param
     * @return
     *@author wangqiao
     *@date 2015年12月21日
     */
    public List<GroupSearchVO> findGroupByName(GroupSearchParam param);
    
	/**
	 * 查询医生与医生集团的关系
	 * @param param
	 * @return
	 *@author wangqiao
	 *@date 2015年12月21日
	 */
	public GroupSearchVO findGroupDoctorStatus(GroupSearchParam param);
	
	void wrapGroupNames(List<CarePlanDoctorVO> list);

}
