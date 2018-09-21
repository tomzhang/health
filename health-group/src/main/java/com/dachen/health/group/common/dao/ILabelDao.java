package com.dachen.health.group.common.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.common.entity.param.LabelParam;
import com.dachen.health.group.common.entity.po.Label;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
public interface ILabelDao {

	/**
     * </p>新建标签</p>
     * @param label
     * @return
     * @author pijingwei
     * @date 2015年8月4日
     */
	void save(Label label);
	
	/**
     * </p>修改标签</p>
     * @param label
     * @return
     * @author pijingwei
     * @date 2015年8月3日
     */
	void update(Label label);
	
	/**
     * </p>删除标签</p>
     * @param ids
     * @return
     * @author pijingwei
     * @date 2015年8月4日
     */
	void delete(String ...ids);
	
	/**
     * </p>查询标签列表</p>
     * @param label
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月4日
     */
	PageVO search(LabelParam label);

	/**
     * </p>查询标签名称是否存在</p>
     * @param label
     * @return Label
     * @author pijingwei
     * @date 2015年8月4日
     */
	Label findLabelByName(Label label);
	
}
