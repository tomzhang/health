package com.dachen.health.group.group.dao;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IGroupSearchService<br>
 * Description： 患者端医生集团和医生搜索<br>
 * 
 * @author fanp
 * @createTime 2015年9月28日
 * @version 1.0.0
 */
public interface IGroupSearchDao {

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
     * @param param 关键字或病种查询
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findGroup(GroupSearchParam param);

    List<GroupSearchVO> findGroupIds(List<String> diseaseIds,int pageIndex,int pageSize);
    
    /**从ES服务器上搜索集团信息
     * </p>搜索医生集团（集团名／医生名／病种/症状 ）</p>
     * @param param 关键字或病种查询
     * @return
     * @author 姜宏杰
     * @date 2016年5月5日14:37:29
     */
    List<GroupSearchVO> findGroupFromEs(GroupSearchParam param);

    /**
     * </p>搜索医生（集团名／医生名／病种 ）</p>
     * 
     * @param param 关键字或病种查询
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    List<GroupSearchVO> findDoctor(GroupSearchParam param);
    
    List<GroupSearchVO> findDoctor(String diseaseId, String deptId, List<Integer> doctorIds);
    
    /**
     * </p>统计集团下面的专家医生</p>
     * 
     * @param param 医生集团信息
     * @return
     * @author 谢佩
     * @date 2015年9月28日
     */
    Map<String,Integer> getGroupExperNum1(List<String> groupIds);
    
    public Map<String,Integer> getGroupExperNum(List<String> groupIds,Integer userStatus);
    
    public String findDiseaseOnIds(List<String> diseaseIds);
    
    public String findDoctorGroupName(Integer doctorId);
    
    String getDisease(List<String> diseaseIds);
    
    /**
     * 搜索医生可加入的医生集团
     * @param param
     * @return
     *@author wangqiao
     *@date 2015年12月21日
     */
    public List<GroupSearchVO> findGroupByName(GroupSearchParam param);
    
    /**
     * 查询医生与医生集团的关系（申请加入状态，是否管理员）
     * @param param
     * @return
     *@author wangqiao
     *@date 2015年12月21日
     */
    public GroupSearchVO findGroupDoctorStatus(GroupSearchParam param);
    
    
    /**
     * 患者端 搜索改造
     * @param param
     * @return
     */
    List<User> searchDocByKeyword(GroupSearchParam param);
    /**
     * 从ES服务器上查询医生信息
     * @param param
     * @return
     */
    List<User> searchDocByKeywordFomEsServer(GroupSearchParam param);
    
    /**
     * 根据集团Id或者集团科室ID查找医生
     * @param param
     */
    PageVO searchDocByGIdOrSId(GroupSearchParam param);
    
    /**
     * 根据集团Id或者集团科室ID查找医生(并排除集团所有者)
     * @param param
     */
    PageVO searchDocByGIdOrSIdWithoutOwnerId(GroupSearchParam param, Integer groupOwnerId, List<Integer> myDoctorIds);
    
    /**
     * 根据集团Id或者集团科室ID查找医生(并排除集团所有者，并添加距离的字段)
     * @param param
     */
    PageVO searchDocByGIdOrSIdWithoutOwnerIdAll(GroupSearchParam param, Integer groupOwnerId, List<Integer> myDoctorIds);
 
    /**
     * 获取集团就诊量
     * @param gorupId
     * @return
     */
    int getGroupCureNum(String gorupId);
}
