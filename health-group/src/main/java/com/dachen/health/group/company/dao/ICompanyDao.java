package com.dachen.health.group.company.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.po.Company;


/**
 * company相关 dao接口定义
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
public interface ICompanyDao {

	

	/**
	 * 新增 公司 记录
	 * @author wangqiao 重构
	 * @date 2016年4月20日
	 * @param company
	 * @return
	 */
	public Company add(Company company);
	

	/**
	 * 更新公司信息
	 * @author wangqiao
	 * @date 2016年4月20日
	 * @param company
	 * @return
	 */
	public Company update(Company company);
	

	/**
	 * 批量删除 公司 信息
	 * @author wangqiao 废弃
	 * @date 2016年4月20日
	 * @param ids
	 * @return
	 */
	@Deprecated //id不应该是 integer类型的
	public boolean delete(Integer[] ids);
	
	/**
     * </p>根据搜索条件获取公司列表</p>
     * @param company
     * @return List<Company>
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated //业务有问题，暂时废弃
	PageVO search(CompanyParam company);


	/**
	 * 通过id 读取公司详情
	 * @author wangqiao 重构
	 * @date 2016年4月22日
	 * @param id
	 * @return
	 */
	public Company getById(String id);
	
	

	/**
	 * 根据组织机构代码获取详情
	 * @author wangqiao 重构
	 * @date 2016年4月22日
	 * @param orgCode
	 * @return
	 */
	public Company getByOrgCode(String orgCode);
}
