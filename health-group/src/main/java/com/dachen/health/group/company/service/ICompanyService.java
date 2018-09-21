package com.dachen.health.group.company.service;

import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.group.entity.po.GroupCertification;


/**
 * 公司相关业务的接口定义
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
public interface ICompanyService {
	
	
	/**
     * </p>添加医生</p>
     * @param company
     * @return boolean
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated
	Company saveCompany(Company company);
	

	/**
	 * 修改公司信息
	 * @author wangqiao 重构
	 * @date 2016年4月20日
	 * @param company
	 * @return
	 */
	public Company updateCompany(Company company);
	
	/**
     * </p>根据公司Id删除</p>
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated //id不应该是 integer类型的
	boolean deleteCompany(Integer ...ids);
	
	/**
     * </p>根据搜索条件获取公司列表</p>
     * @param company
     * @return List<Company>
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated //业务有问题，暂时废弃
	PageVO searchCompany(CompanyParam company);


	/**
	 * 根据公司id 读取公司信息
	 * @author wangqiao 重构
	 * @date 2016年4月22日
	 * @param id
	 * @return
	 */
	public Company getCompanyById(String id);
	
	/**
	 * 根据组织机构代码获取详情
	 * @author wangqiao 
	 * @date 2016年4月22日
	 * @param orgCode
	 * @return
	 */
	public Company getCompanyByOrgCode(String orgCode);

	/**
     * </p>根据邀请码添加集团到公司</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated //暂时废弃
	Map<String, Object> addGroupByInviteCode(String code);
	
	/**
	 * 公司认证成功后，新增 公司 记录
	 * @author wangqiao
	 * @date 2016年4月20日
	 * @param company
	 * @param operationUserId
	 * @return
	 */
	public 	Company addCompany(GroupCertification groupCert, Integer operationUserId);
	
}
