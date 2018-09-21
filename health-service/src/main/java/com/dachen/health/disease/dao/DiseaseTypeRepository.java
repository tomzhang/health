package com.dachen.health.disease.dao;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.dao.BaseRepository;
import com.dachen.health.commons.vo.RecommDiseaseVO;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.disease.vo.DiseaseTypeVo;

/**
 * 
 * @author vincent
 *
 */
public interface DiseaseTypeRepository extends BaseRepository<DiseaseType, String> {
	
	List<DiseaseType> findByDept(String deptId);

	List<DiseaseType> findByName(String name);
	
	List<DiseaseType> findByIds(List<String> diseaseIds);
	
	String findNameByIds(List<String> diseaseIds);
	
	DiseaseType findByDiseaseId(String diseaseId);
	
	List<RecommDiseaseVO> findDiseaseType(PageVO param);
	
	List<RecommDiseaseVO> findDiseaseType(PageVO param,List<String> diseaseIds);
	
	/**
	 * </p>根据病种名称模糊查询出id</p>
	 * @param name
	 * @return
	 * @author fanp
	 * @date 2015年10月8日
	 */
	List<String> findIdByName(String name);
	
	public DiseaseTypeVO getDiseaseTypeTreeById(String id);
	
	public DiseaseTypeVo getDiseaseAlias(String id);
	
	List<DiseaseTypeVO> getTreeByKeyword(String keyword);
	
	void addCommonDisease(String diseaseId, String name);
	
	void removeCommonDisease(String diseaseId);
	
	void upWeight(String diseaseId);
	
	PageVO getDiseaseList(Integer pageIndex, Integer pageSize);

	/**
	 * 根据id查询疾病
	 * @param id
	 */
	DiseaseTypeVo findByIds(String diseaseId);

	/**
	 * 设置疾病的详细信息
	 * @param diseaseId
	 * @param introduction
	 * @param remark
	 * @param attention
	 */
	void setDiseaseInfo(String diseaseId, String introduction, String alias, String remark, String attention);

	PageVO findByKeyword(String keyword, Integer pageIndex, Integer pageSize);

	List<DiseaseTypeVO> buildTypeTreeByIds(List<String> diseaseIds);
	
	PageVO getDiseaseListAfterSort(Integer pageIndex, Integer pageSize);
	
}
