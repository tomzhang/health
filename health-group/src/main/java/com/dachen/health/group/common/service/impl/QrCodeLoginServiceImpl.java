package com.dachen.health.group.common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.group.common.entity.vo.QrCodeInfo;
import com.dachen.health.group.common.service.ICommonService;
import com.dachen.health.group.common.service.QrCodeLoginService;
import com.dachen.health.group.company.dao.ICompanyUserDao;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.entity.vo.GroupUserVO;
import com.dachen.health.group.company.service.ICompanyUserService;

/**
 * 
 * @author pijingwei
 * @date 2015/8/19
 */
@Service
public class QrCodeLoginServiceImpl implements QrCodeLoginService {

	@Autowired
	protected ICompanyUserService cuserService;
	
	@Autowired
    protected ICommonService commonService;

	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	
	@Override
	@Deprecated //业务不正确，暂时废弃， add by wangqiao
	public Map<String, Object> getDoctorInfoByCheck(String uuid) {
		Map<String, Object> data = null;
		String[] fields = new String[]{"id", "doctorId", "type", "objectId", "status", "creator", "creatorDate", "updator", "updatorDate"};
		List<String> fieldList = jedisTemplate.hmget(uuid, fields);
		if(null == fieldList || 0 == fieldList.size() || null == fieldList.get(0)) {
			throw new ServiceException("没有登录");
		}
		GroupUserVO guser = new GroupUserVO();
		guser.setId(fieldList.get(0));
		guser.setDoctorId(Integer.valueOf(fieldList.get(1)));
		guser.setType(Integer.valueOf(fieldList.get(2)));
		guser.setObjectId(fieldList.get(3));
		guser.setStatus(fieldList.get(4));
		guser.setCreator(Integer.valueOf(fieldList.get(5)));
		guser.setCreatorDate(Long.valueOf(fieldList.get(6)));
		guser.setUpdator(Integer.valueOf(fieldList.get(7)));
		guser.setUpdatorDate(Long.valueOf(fieldList.get(8)));
		if(1 == guser.getType()) {
			data = cuserService.getLoginByCompanyUser(guser);
		} else {
			data = cuserService.getLoginByGroupUser(Integer.valueOf(fieldList.get(1)),fieldList.get(3));
		}
		
		return data;
	}

	@Override
	@Deprecated
	public void verifyLogin(QrCodeInfo codeInfo) {
        commonService.verificationUserByDoctorId(codeInfo.getDoctorId());
//		GroupUser groupuser = new GroupUser();
//		groupuser.setDoctorId(codeInfo.getDoctorId());
//		GroupUser guser = cuserDao.getGroupUserById(groupuser);
		GroupUser guser = cuserService.getGroupUserByIdAndStatus(null, codeInfo.getDoctorId(), null, null, null);
		
		if(null == guser) {
			throw new ServiceException("帐号不存在");
		}
		if("I".equals(guser.getStatus())) {
			throw new ServiceException("您还未确认加入");
		}
		if("S".equals(guser.getStatus())) {
			throw new ServiceException("您已离职，请联系管理员");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", guser.getId());
		map.put("doctorId", guser.getDoctorId().toString());
		map.put("type", guser.getType().toString());
		map.put("objectId", guser.getObjectId());
		map.put("status", guser.getStatus());
		map.put("creator", guser.getCreator().toString());
		map.put("creatorDate", guser.getCreatorDate().toString());
		map.put("updator", guser.getUpdator().toString());
		map.put("updatorDate", guser.getUpdatorDate().toString());
		jedisTemplate.hmset(codeInfo.getUuid(), map);
	}

}
