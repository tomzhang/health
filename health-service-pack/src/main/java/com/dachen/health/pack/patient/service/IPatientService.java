package com.dachen.health.pack.patient.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.user.entity.vo.RelationVO;

/**
 * 
 * ProjectName： health-service-pack<br>
 * ClassName： IPatientService<br>
 * Description： <br>
 * 
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
public interface IPatientService extends IBaseService<Patient, Integer>{


	/**
	 * 
	 * 根据创建人查找
	 * 
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	public List<Patient> findByCreateUser(int createUserId);
	
	
	/**
	 * 用户（患者）注册、修改基本信息时，添加或修改本人患者信息
	 * @param user
	 * @return
	 */
	public Patient save(Integer userId);
	public Patient save(User user);
	
	/**
	 * 
	 * @param userId
	 * @param topPath
	 */
	public void updateTopPath(Integer userId, String topPath);

	/**
	 * 
	 * 根据创建人和关系查找
	 * 
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	public Patient findOne(Integer createUserId, String relation);
	
	/**
	 * </p>根据主键查找</p>
	 * @author fanp
	 * @date 2015年9月9日
	 */
	public Patient findByPk(Integer pk);

	/**
	 * </p>获取所有患者ids</p>
	 * @param userId
	 * @return
	 */
	public List<Integer> getPatientIdsByUserId(int userId);
	
	/**
	 * 删除记录时判断是否存在业务数据
	 * @param pk
	 * @return
	 */
	public boolean existsBizData(Integer pk);
	
	public List<Patient> getPatientsByFuzzyName(String name);
	
	/**
	 * 通过患者id，读取患者名称
	 * @author wangqiao
	 * @date 2016年6月1日
	 * @param id
	 * @return
	 */
	public String getPatientNameByPatientId(Integer id);
	
	public Map<String,Object> checkIdCard(int createUserId,String idcard,Integer id);

	public List<?> getPatientsByTelephone(String telephone);
	
	/**
     * </p>统计集团患者数（医生考核）</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO statPatient(StatParam param);


	List<Patient> findByIds(List<Integer> ids);

}
