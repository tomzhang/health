package com.dachen.health.group.common.service;

import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.vo.InviteRelation;


/**
 * 查询医生信息的 service接口
 * @author wangqiao 重构
 * @date 2016年4月22日
 */
@Deprecated
public interface ICommonService {

	/**
     * </p>验证用户类型和审核状态</p>
     * @param doctorId
     * @return DoctorBasicInfo
     * @author pijingwei
     * @date 2015年8月4日
     */
	@Deprecated
	void verificationUserByDoctorId(Integer doctorId);

}
