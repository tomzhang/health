package com.dachen.health.group.group.dao;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.param.GroupProfitParam;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.health.group.group.entity.vo.GroupProfitVO;
import com.dachen.util.tree.ExtTreeNode;

/**
 * ProjectName： health-group<br>
 * ClassName： IGroupProfitDao<br>
 * Description：抽成关系dao <br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
public interface IGroupProfitDao {
	
	
//	GroupProfit save(GroupProfit profit);

    /**
     * </p>集团创建者邀请的人的树</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月28日
     */
    List<ExtTreeNode> getGroupProfit(String groupId);

    /**
     * </p>查找下级抽成关系</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月6日
     */
//    PageVO getGroupProfit(GroupProfitParam param);
    
    
    /**
     * 根据key（名字）搜索所有符合的数据
     * @param param
     * @param key
     * @return
     */
//    PageVO searchByKeyword(GroupProfitParam param);
    
    /**
     * 搜索医生名字，查询相关的抽成信息
     * @param searchKey
     * @param groupId
     * @param pageIndex
     * @param pageSize
     * @return
     *@author wangqiao
     *@date 2016年2月19日
     */
    public List<Map<String, Object>> searchByKeyword(String searchKey, String groupId,Integer pageIndex,Integer pageSize);
    
    /**
     * 计算 搜索结果总数（搜索医生名字，查询相关的抽成信息）
     * @param searchKey
     * @param groupId
     * @return
     *@author wangqiao
     *@date 2016年2月19日
     */
    public long countSearchByKeyword(String searchKey, String groupId);
    
    /**
     * </p>通过id查找抽成关系</p>
     * 
     * @param id
     * @return
     * @author fanp
     * @date 2015年9月2日
     */
    public GroupProfitVO getById(Integer doctorId,String groupId);

    
//    GroupProfit getById(Integer id);
    /**
     * </p>添加抽成关系</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年9月2日
     */
    void add(GroupProfit po);

    /**
     * </p>修改抽成关系</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    void updateParentId(Integer doctorId,Integer parentId,String groupId);

    /**
     * </p>删除抽成关系</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    void delete(Integer doctorId,String groupId);
    
    boolean delete(String... groupIds);

	/**
	 * 删除集团下所有的profit信息
	 * @author wangqiao
	 * @date 2016年3月23日
	 * @param groupId
	 */
	public void deleteByGroupId(String groupId);
	
	/**
	 * 根据id删除profit信息
	 * @author wangqiao
	 * @date 2016年3月23日
	 * @param groupProfitId
	 */
	public void deleteById(String groupProfitId);
    
    /**
     * </p>修改抽成比例</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月6日
     */
    void updateProfit(GroupProfitParam param);

    /**
     * 通过 医生id和集团id，查询抽成信息
     * 
     * @param doctorId
     * @param groupId
     * @return
     *@author wangqiao
     *@date 2016年1月23日
     */
    public GroupProfit getGroupProfitById(Integer doctorId,String groupId);
    
    /**
     * 通过 集团id和上级id，分页查询抽成信息
     * @param groupId
     * @param parentId
     * @param pageIndex
     * @param pageSize
     * @return
     *@author wangqiao
     *@date 2016年2月18日
     */
    public List<Map<String, Object>> getGroupProfitByParentId(String groupId,Integer parentId,Integer pageIndex,Integer pageSize);
    
    /**
     * 通过 集团id和上级id，查询记录总数
     * @param groupId
     * @param parentId
     * @return
     *@author wangqiao
     *@date 2016年2月18日
     */
    public long countGroupProfitByParentId(String groupId,Integer parentId);
    
    /**
     * 批量计算 抽成的子节点数量
     * @param groupId
     * @param userIds
     * @return
     *@author wangqiao
     *@date 2016年2月19日
     */
    public Map<Integer, Integer> getChildrenCount(String groupId, List<?> userIds) ;
    
    /**
     * 查询集团中所有的 profit信息
     * @author wangqiao
     * @date 2016年3月23日
     * @param groupId
     * @return
     */
    public List<GroupProfit> getGroupProfitByGroupId(String groupId);

	void updateAppointment(String groupId, Integer doctorId, Integer appointmentGroupProfit,
			Integer appointmentParentProfit);
    
}
