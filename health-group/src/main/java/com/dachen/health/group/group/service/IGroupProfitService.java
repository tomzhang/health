package com.dachen.health.group.group.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.param.GroupProfitParam;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.util.tree.ExtTreeNode;

/**
 * ProjectName： health-group<br>
 * ClassName： IGroupProfitService<br>
 * Description：抽成关系service <br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
public interface IGroupProfitService {

	/**
	 * 通过id 读取 抽成关系
	 * @param doctorId
	 * @param groupId
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public GroupProfit getGroupProfitById(Integer doctorId,String groupId);
	
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
     * @param groupId
     * @param parentId
     * @return
     * @author fanp
     * @date 2015年9月6日
     */
    PageVO getGroupProfit(GroupProfitParam param);
    
    /**
     * 搜索获取医生抽成列表结构
     * @param param
     * @return
     */
    PageVO searchByKeyword(GroupProfitParam param);
    
    /**
     * </p>添加抽成关系</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年9月2日
     */
    void add(GroupProfit po);


    /**
     * 更新抽成关系
     * @param doctorId
     * @param parentId
     * @param groupId
     *@author wangqiao
     *@date 2016年1月26日
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
    
    /**
     * </p>修改抽成比例</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    void updateProfit(GroupProfitParam param);
    
    
    /**
     * 医生加入集团时，初始化自己的profit
     * @param doctorId  医生id
     * @param groupId   集团id
     * @param referenceId  推荐人id
     *@author wangqiao
     *@date 2015年12月26日
     */
    public void initProfitByJoinGroup(Integer doctorId,String groupId,Integer referenceId);
    
    /**
     * 修复某个集团的profit数据（处理垃圾数据）
     * 参数为空时，修复所有集团的profit数据
     * @author wangqiao
     * @date 2016年3月23日
     * @param groupId
     */
    public void fixHistoryProfitData(String groupId);
    
}
