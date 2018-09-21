package com.dachen.health.group.common.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.common.entity.param.LabelParam;
import com.dachen.health.group.common.entity.po.Label;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
public interface ILabelService {

	
	/**
     * </p>新建标签</p>
     * @param company
     * @return boolean
     * @author pijingwei
     * @date 2015年8月4日
     */
	void saveLabel(Label label);
	
	/**
     * </p>修改标签</p>
     * @param company
     * @return boolean
     * @author pijingwei
     * @date 2015年8月3日
     */
	void updateLabel(Label label);
	
	/**
     * </p>删除标签</p>
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月4日
     */
	void deleteLabel(String ...ids);
	
	/**
     * </p>查询标签列表</p>
     * @param company
     * @return List<Company>
     * @author pijingwei
     * @date 2015年8月4日
     */
	PageVO searchLabel(LabelParam label);
	
}
