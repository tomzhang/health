package com.dachen.health.recommand.service;

import com.dachen.health.auto.entity.po.Disease;
import com.dachen.health.base.entity.vo.DiseaseLaberVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.recommand.entity.vo.DiseaseLaberCountVo;

import java.util.List;
import java.util.Map;

/**
 * Created by liangcs on 2016/12/5.
 */
public interface IDiseaseLaberService {

	/**
	 * 运营平台添加疾病标签
	 * @param diseaseId
	 */
    void addLaber(List<String> diseaseId);

    /**
     * 获取所有标签树
     * @return
     */
    List<DiseaseTypeVO> getLaberTree();

    /**
     * web端获取标签列表
     * @return
     */
    List<DiseaseLaberCountVo> getWebLaber();
    
    /**
     * 判断是否推荐关注
     * @return
     */
    Map<String, Integer> checkFollow();
    
    /**
     * 用户添加疾病标签
     * @param diseaseIds
     */
    void addUserLaber(List<String> diseaseIds);
     
    /**
     * 用户获取自身绑定标签
     * @return
     */
    List<DiseaseType> getMyLabers();
    
    /**
     * 用户删除自身绑定标签
     * @param diseaseId
     */
    void delUserLaber(List<String> diseaseIds);
    
    /**
     * 获取所有标签信息，不包含父节点
     * @return
     */
    List<DiseaseType> getAllLabers();
    
    /**
     * 看病时添加疾病标签
     * @param id
     */
    void addLaberByTreat(Integer userId, String id);

}
