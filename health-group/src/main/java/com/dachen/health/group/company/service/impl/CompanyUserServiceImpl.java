package com.dachen.health.group.company.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.GroupEnum.*;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.dao.ICompanyUserDao;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.entity.vo.CompanyVO;
import com.dachen.health.group.company.entity.vo.GroupUserVO;
import com.dachen.health.group.company.service.ICompanyService;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupUserDao;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.GroupHospitalDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


/**
 * 集团管理员相关业务 接口实现
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
@Service
public class CompanyUserServiceImpl implements ICompanyUserService {

	@Autowired
	protected ICompanyUserDao cuserDao;

	@Autowired
    protected IGroupDoctorDao groupDoctorDao;

	@Autowired
    protected IGroupDoctorService groupDoctorService;

	@Autowired
    protected IGroupDao groupDao;

	@Autowired
    protected IDepartmentService departmentService;

	@Autowired
    protected ICompanyService companyService;

	@Autowired
    protected IBaseDataService baseDataService;

	@Autowired
    protected MobSmsSdk mobSmsSdk;

	@Autowired
    protected IBusinessServiceMsg businessMsgService;

	@Autowired
    protected UserManager userManager;
	
	@Autowired
    protected IGroupUserDao groupUserDao;

	@Override
	public GroupUser addGroupManage(String groupId, Integer doctorId, Integer operationUserId) {
		// 参数校验
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("groupId 为空");
		}
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("doctorId 为空");
		}

		GroupUser guser = new GroupUser();
		guser.setObjectId(groupId);
		guser.setDoctorId(doctorId);
		guser.setType(GroupUserType.集团用户.getIndex());
		guser.setStatus(GroupUserStatus.正常使用.getIndex());
		guser.setRootAdmin(GroupRootAdmin.admin.getIndex());
		guser.setCreator(operationUserId);
		guser.setCreatorDate(System.currentTimeMillis());
		guser.setUpdator(operationUserId);
		guser.setUpdatorDate(System.currentTimeMillis());
		return cuserDao.add(guser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * addCompanyManage(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public GroupUser addCompanyManage(String companyId, Integer doctorId, Integer operationUserId) {
		// 参数校验
		if (StringUtils.isEmpty(companyId)) {
			throw new ServiceException("groupId 为空");
		}
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("doctorId 为空");
		}
		GroupUser guser = new GroupUser();
		guser.setObjectId(companyId);
		guser.setDoctorId(doctorId);
		guser.setType(GroupUserType.公司用户.getIndex());
		guser.setStatus(GroupUserStatus.正常使用.getIndex());
		guser.setRootAdmin(GroupRootAdmin.admin.getIndex());
		guser.setCreator(operationUserId);
		guser.setCreatorDate(System.currentTimeMillis());
		guser.setUpdator(operationUserId);
		guser.setUpdatorDate(System.currentTimeMillis());
		return cuserDao.add(guser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * addRootGroupManage(java.lang.String, java.lang.Integer)
	 */
	@Override
	public GroupUser addRootGroupManage(String groupId, Integer doctorId, Integer operationUserId) {
		// 参数校验
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("groupId 为空");
		}
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("doctorId 为空");
		}
		GroupUser guser = new GroupUser();
		guser.setObjectId(groupId);
		guser.setDoctorId(doctorId);
		guser.setType(GroupUserType.集团用户.getIndex());	// type字段没什么用，科室用户也是用集团用户
		guser.setStatus(GroupUserStatus.正常使用.getIndex());
		guser.setRootAdmin(GroupRootAdmin.root.getIndex());
		guser.setCreator(operationUserId);
		guser.setCreatorDate(System.currentTimeMillis());
		guser.setUpdator(operationUserId);
		guser.setUpdatorDate(System.currentTimeMillis());
		return cuserDao.add(guser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * getGroupAdminByGroupId(java.lang.String)
	 */
	@Override
	public List<Integer> getGroupAdminByGroupId(String groupId) {
		// 参数校验
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("groupId 为空");
		}

		// 查询集团所有管理员
		// List<GroupUser> groupUserList =
		// cuserDao.getGroupUserListByIdAndStatus(null,GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex(),groupId);
		List<GroupUser> groupUserList = cuserDao.getGroupUserListByIdAndStatus(null, null, groupId,
				GroupUserType.集团用户.getIndex(), GroupUserStatus.正常使用.getIndex());

		// 迭代读取 医生id
		List<Integer> userIdList = new ArrayList<Integer>();
		if (groupUserList != null) {
			for (GroupUser gUser : groupUserList) {
				if (gUser != null && gUser.getDoctorId() != null && gUser.getDoctorId() != 0) {
					userIdList.add(gUser.getDoctorId());
				}
			}
		}

		return userIdList;
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyUserService#inviteJoinGroupManage(java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Map<String, Object> inviteJoinGroupManage(Integer doctorId, String groupId, Integer operationUserId,
			Integer againInvite) throws HttpApiException {
		// 参数校验
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("doctorId 为空");
		}
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("groupId 为空");
		}
		// 返回参数
		Map<String, Object> data = new HashMap<String, Object>();

		// 必须是集团成员才能邀请成为管理员
		GroupDoctor groupDoctor = groupDoctorService.getGroupDoctor(doctorId, groupId);
		if (groupDoctor == null) {
			throw new ServiceException("医生不在集团内，请先邀请到圈子");
		}
		if (GroupDoctorStatus.离职.getIndex().equals(groupDoctor.getStatus())) {
			throw new ServiceException("该医生已经离职，请等待医生加入集团之后再邀请为管理员");
		}
		if (!GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor.getStatus())) {
			throw new ServiceException("该医生还未加入集团，请等待医生加入集团之后再邀请为管理员");
		}

		// 查询groupUser记录
		GroupUser groupUser = cuserDao.getGroupUserByIdAndStatus(null, doctorId, groupId, null, null);
		// 没有groupUser记录
		if (groupUser == null) {
			// 需要新增 gruopUser记录
			GroupUser gu = new GroupUser();
			gu.setDoctorId(doctorId);
			gu.setObjectId(groupId);
			gu.setStatus(GroupUserStatus.邀请待通过.getIndex());
			gu.setType(GroupUserType.集团用户.getIndex());
			gu.setCreator(operationUserId);
			gu.setCreatorDate(System.currentTimeMillis());
			gu.setRootAdmin(GroupRootAdmin.admin.getIndex());
			gu.setUpdator(operationUserId);
			gu.setUpdatorDate(System.currentTimeMillis());
			GroupUser newuser = cuserDao.add(gu);
			data.put("sms", this.sendSysNote(newuser));
			data.put("note", this.sendSms(newuser));
			data.put("status", "");
			data.put("msg", "");
			return data;
		}
		// 已有groupUser记录，需要更新groupUser记录
		else {

			if (GroupUserStatus.邀请待通过.getIndex().equals(groupUser.getStatus())) {
				//再次发送消息通知被邀请人
				if (null != againInvite && 1 == againInvite) {
					data.put("sms", this.sendSysNote(groupUser));
					data.put("note", this.sendSms(groupUser));
					data.put("status", 2);
					data.put("msg", "再次邀请发送成功");
					return data;
				} else {
					data.put("sms", "");
					data.put("note", "");
					data.put("status", 2);
					data.put("msg", "该医生上次未接受您的邀请，确定再次发送邀请吗？");
					return data;
				}
			} else if (GroupUserStatus.已离职.getIndex().equals(groupUser.getStatus())) {
				if (null != againInvite && 1 == againInvite) {
					//更新状态，并发送消息通知被邀请人
					this.updateStatusById(groupUser.getId(), GroupUserStatus.邀请待通过.getIndex(), operationUserId);
					data.put("sms", this.sendSysNote(groupUser));
					data.put("note", this.sendSms(groupUser));
					data.put("status", 2);
					data.put("msg", "再次邀请发送成功");
					return data;
				} else {
					data.put("sms", "");
					data.put("note", "");
					data.put("status", 2);
					data.put("msg", "该医生已离职，确定再次发送邀请吗？");
					return data;
				}
			} else if (GroupUserStatus.邀请拒绝.getIndex().equals(groupUser.getStatus())) {
				if (null != againInvite && 1 == againInvite) {
					//更新状态，并发送消息通知被邀请人
					this.updateStatusById(groupUser.getId(), GroupUserStatus.邀请待通过.getIndex(), operationUserId);
					data.put("sms", this.sendSysNote(groupUser));
					data.put("note", this.sendSms(groupUser));
					data.put("status", 2);
					data.put("msg", "再次邀请发送成功");
					return data;
				} else {
					data.put("sms", "");
					data.put("note", "");
					data.put("status", 2);
					data.put("msg", "该医生已拒绝，确定再次发送邀请吗？");
					return data;
				}
			} else if (GroupUserStatus.正常使用.getIndex().equals(groupUser.getStatus())) {
				throw new ServiceException("该医生已经是管理员");
			} else {
				throw new ServiceException("帐号状态异常");
			}
		}
//		return null;
	}

	@Override
	@Deprecated
	public Map<String, Object> saveCompanyUser(GroupUserParam cuser) throws HttpApiException {
		Map<String, Object> data = new HashMap<String, Object>();
		if ((null != cuser.getDoctorId() && 0 != cuser.getDoctorId()) && !StringUtil.isEmpty(cuser.getObjectId())) {
			if (2 == cuser.getType()) {
				GroupDoctor gd = new GroupDoctor();
				gd.setGroupId(cuser.getObjectId());
				gd.setDoctorId(cuser.getDoctorId());
				GroupDoctor gdoc = groupDoctorDao.getById(gd);
				if (gdoc == null) {
					throw new ServiceException("医生不在集团内，请先邀请到圈子");
				}
				if (GroupDoctorStatus.离职.getIndex().equals(gdoc.getStatus())) {
					throw new ServiceException("该医生已经离职，请等待医生加入集团之后再邀请为管理员");
				}
				if (!GroupDoctorStatus.正在使用.getIndex().equals(gdoc.getStatus())) {
					throw new ServiceException("该医生还未加入集团，请等待医生加入集团之后再邀请为管理员");
				}
			}

			/** 1，验证是否已经添加 */
			// GroupUser gu1 = new GroupUser();
			// gu1.setDoctorId(cuser.getDoctorId());
			// gu1.setObjectId(cuser.getObjectId());
			// GroupUser guser = this.validationUser(gu1);
			// GroupUser guser =
			// cuserDao.getGroupUserByIdAndStatus(cuser.getDoctorId(),cuser.getObjectId(),null);
			GroupUser guser = cuserDao.getGroupUserByIdAndStatus(null, cuser.getDoctorId(), cuser.getObjectId(), null,
					null);

			if (null != guser) {

				if (GroupUserStatus.邀请待通过.getIndex().equals(guser.getStatus())) {
					if (null != cuser.getAgainInvite() && 1 == cuser.getAgainInvite()) {
						data.put("sms", this.sendSysNote(guser));
						data.put("note", this.sendSms(guser));
						data.put("status", 2);
						data.put("msg", "再次邀请发送成功");
						return data;
					} else {
						data.put("sms", "");
						data.put("note", "");
						data.put("status", 2);
						data.put("msg", "该医生上次未接受您的邀请，确定再次发送邀请吗？");
						return data;
					}
				} else if (GroupUserStatus.已离职.getIndex().equals(guser.getStatus())) {
					if (null != cuser.getAgainInvite() && 1 == cuser.getAgainInvite()) {
						// guser.setStatus("I");
						// cuserDao.update(guser);
						this.updateStatusById(guser.getId(), GroupUserStatus.邀请待通过.getIndex(), cuser.getUpdator());
						data.put("sms", this.sendSysNote(guser));
						data.put("note", this.sendSms(guser));
						data.put("status", 2);
						data.put("msg", "再次邀请发送成功");
						return data;
					} else {
						data.put("sms", "");
						data.put("note", "");
						data.put("status", 2);
						data.put("msg", "该医生已离职，确定再次发送邀请吗？");
						return data;
					}
				} else if (GroupUserStatus.邀请拒绝.getIndex().equals(guser.getStatus())) {
					if (null != cuser.getAgainInvite() && 1 == cuser.getAgainInvite()) {
						// guser.setStatus("I");
						// cuserDao.update(guser);
						this.updateStatusById(guser.getId(), GroupUserStatus.邀请待通过.getIndex(), cuser.getUpdator());
						data.put("sms", this.sendSysNote(guser));
						data.put("note", this.sendSms(guser));
						data.put("status", 2);
						data.put("msg", "再次邀请发送成功");
						return data;
					} else {
						data.put("sms", "");
						data.put("note", "");
						data.put("status", 2);
						data.put("msg", "该医生已拒绝，确定再次发送邀请吗？");
						return data;
					}
				} else if (GroupUserStatus.正常使用.getIndex().equals(guser.getStatus())) {
					throw new ServiceException("该医生已经是管理员");
				} else {
					throw new ServiceException("帐号未知状态");
				}
			} else {
				GroupUser gu = new GroupUser();
				gu.setDoctorId(cuser.getDoctorId());
				gu.setObjectId(cuser.getObjectId());
				gu.setStatus(cuser.getStatus());
				gu.setType(cuser.getType());
				gu.setCreator(cuser.getCreator());
				gu.setCreatorDate(cuser.getCreatorDate());
				gu.setUpdator(cuser.getUpdator());
				gu.setUpdatorDate(cuser.getUpdatorDate());
				GroupUser newuser = cuserDao.add(gu);
				data.put("sms", this.sendSysNote(newuser));
				data.put("note", this.sendSms(newuser));
				data.put("status", "");
				data.put("msg", "");
				return data;
			}
		} else {
			if (StringUtil.isEmpty(cuser.getTelephone()) || StringUtil.isEmpty(cuser.getObjectId())) {
				throw new ServiceException("手机号码为空");
			}
			/**
			 * 根据手机号码发送邀请操作 不在平台内的医生，通过手机号码发送邀请下载客户端注册成为医生
			 */
			data.put("sms", "");
			data.put("note", this.sendNoteByPhone(cuser));
			data.put("status", "");
			data.put("msg", "");
			return data;
		}
	}


	/**
	 * 发送系统通知
	 * @author wangqiao 重构
	 * @date 2016年4月20日
	 * @param user
	 * @return
	 */
	private String sendSysNote(GroupUser user) {
		//构造通知内容
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setStyle(6);
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setPic(GroupUtil.getInviteAdminImage());
		try {
			if (1 == user.getType()) {
				imgTextMsg.setTitle(UserChangeTypeEnum.PROFILE_INVITE_COMPANY.getAlias());
				imgTextMsg.setContent("公司管理员邀请函");

			} else {
				imgTextMsg.setTitle(UserChangeTypeEnum.PROFILE_INVITE_GROUP.getAlias());
				imgTextMsg.setContent("集团管理员邀请函");
			}

			//生成邀请短链
			String url = getCompanyInviteUrl(user.getType(), user.getId(), user.getObjectId(),
					user.getDoctorId());
			imgTextMsg.setUrl(url);
			mpt.add(imgTextMsg);

			businessMsgService.sendTextMsg(user.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY, mpt, null);
			businessMsgService.sendTextMsg(user.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
		} catch (Exception e) {
			return "消息推送失败";
		}
		return "消息推送成功";
	}

	/**
	 * 发送短信邀请不在平台上的医生下载客户端进行注册成为医生
	 * 
	 * @return
	 * @throws IOException
	 */
	@Deprecated //暂时用不到 废弃 add by wangqiao
	private String sendNoteByPhone(GroupUserParam cuser) throws HttpApiException {
//		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin") + "?";
//		String tpl = "";
		
		String fromName = "";
		if (1 == cuser.getType()) {
			Company company = companyService.getCompanyById(cuser.getObjectId());
			fromName = company.getName();
//			url = url + "&type=1&companyId=" + company.getId() + "&name=" + company.getName();
		}
		if (2 == cuser.getType()) {
			Group group = groupDao.getById(cuser.getObjectId(), null);
			fromName = group.getName();
//			url = url + "&type=2&groupId=" + group.getId() + "&name=" + group.getName();
		}
		
		String tpl = "";
		if (mobSmsSdk.isBDJL(cuser.getTelephone())) {
			String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download.bdjl");
			String generateUrl = shortUrlComponent.generateShortUrl(url);
			tpl = baseDataService.toContent("0003", fromName, BaseConstants.BD_DOC_APP, generateUrl);
		} else {
			String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download");
			String generateUrl = shortUrlComponent.generateShortUrl(url);
			tpl = baseDataService.toContent("0003", fromName, BaseConstants.XG_DOC_APP, generateUrl);
		}

		mobSmsSdk.send(cuser.getTelephone(), tpl);
		return tpl;
	}

	/**
	 * 发送短信邀请信息
	 * 
	 */
	private String sendSms(GroupUser user) throws HttpApiException {
//		DoctorBasicInfo doctor = commonService.getDoctorBasicInfoById(user.getDoctorId(), 3);
		User doctor = userManager.getUser(user.getDoctorId());
		
		String tpl = getCompanyInviteTpl(user.getType(), user.getId(), user.getObjectId(), user.getDoctorId());
		mobSmsSdk.send(doctor.getTelephone(), tpl, BaseConstants.XG_YSQ_APP);
		return tpl;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * deleteCompanyUser(java.lang.String[])
	 */
	@Override
	public void deleteGroupUserById(String[] ids) {
		// 参数校验
		if (ids == null) {
			throw new ServiceException("参数不能为空");
		}
		// 校验超级管理员不能删除
		for (String id : ids) {
			// GroupUser gu =
			// dsForRW.createQuery(GroupUser.class).field("_id").equal(new
			// ObjectId(id)).get();
			GroupUser gu = this.getGroupUserByIdAndStatus(id, null, null, null, null);

			if (gu != null && GroupRootAdmin.root.getIndex().equals(gu.getRootAdmin())) {
				throw new ServiceException("不能删除集团超级管理员");
			}
		}
		// 持久化
		cuserDao.deleteById(ids);
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyUserService#deleteGroupUserByLeaveGroup(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void deleteGroupUserByLeaveGroup(String groupId, Integer doctorId) {
		// 参数校验
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("参数groupId不能为空");
		}
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("参数doctorId不能为空");
		}
		// 持久化
		cuserDao.deleteByGroupIdAndDoctorId(groupId, doctorId);

	}

	@Override
	public PageVO searchCompanyUser(GroupUserParam param) {
		if (StringUtil.isEmpty(param.getObjectId())) {
			throw new ServiceException("objectId为空");
		}
		if (StringUtil.isEmpty(param.getStatus())) {
			throw new ServiceException("账号状态参数不能为空");
		}
		if (param.getEndTime() != null && param.getStartTime() > System.currentTimeMillis()) {
			throw new ServiceException("开始时间不能大于当前时间");
		}
		if (param.getEndTime() != null && param.getStartTime() != null && param.getStartTime() > param.getEndTime()) {
			throw new ServiceException("开始时间不能大于结束时间");
		}
		return cuserDao.search(param);
	}

	// @Override
	// public GroupUser validationUser(GroupUser cuser) {
	// return cuserDao.getGroupUserById(cuser);
	// }


	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyUserService#companyLogin(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public Map<String, Object> companyLogin(String telephone, String password) {
		if (StringUtil.isEmpty(telephone)) {
			throw new ServiceException("帐号不能为空");
		}
		if (StringUtil.isEmpty(password)) {
			throw new ServiceException("密码不能为空");
		}
		UserExample example = new UserExample();
		example.setTelephone(telephone);
		example.setPassword(password);

		Map<String, Object> userMap = null;
		Map<String, Object> data = null;
		try {
			example.setUserType(5);
			userMap = userManager.login(example);
		} catch (Exception e) {
			example.setUserType(3);
			userMap = userManager.login(example);
		}

		GroupUserVO cuser = new GroupUserVO();
		cuser.setDoctorId((Integer) userMap.get("userId"));
		cuser.setType(1);
		data = this.getLoginByCompanyUser(cuser);
		data.put("user", userMap);
		return data;
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyUserService#getLoginByCompanyUser(com.dachen.health.group.company.entity.vo.GroupUserVO)
	 */
	@Override
	@Deprecated
	public Map<String, Object> getLoginByCompanyUser(GroupUserVO cuser) {
		if (null == cuser.getDoctorId() || 0 == cuser.getDoctorId()) {
			throw new ServiceException("doctorId为空");
		}
		CompanyVO company = null;
		// GroupUser uu = new GroupUser();
		// uu.setDoctorId(cuser.getDoctorId());
		// uu.setType(1);
		// GroupUser guser = cuserDao.getGroupUserById(uu);
		GroupUser guser = this.getGroupUserByIdAndStatus(null, cuser.getDoctorId(), cuser.getObjectId(), GroupUserType.公司用户.getIndex(),
				GroupUserStatus.正常使用.getIndex());

		if (null == guser) {
//			DoctorBasicInfo info = commonService.getDoctorBasicInfoById(cuser.getDoctorId(), 5);
			User info = userManager.getUser(cuser.getDoctorId());
			
			if (null != info && 5 == info.getUserType()) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("companyUser", "");
				data.put("company", "");
				data.put("groupList", "");
				data.put("status", 1);
				data.put("msg", "您还未创建公司，请先创建");
				return data;
			} else {
				throw new ServiceException("帐号不存在");
			}
		}
		/** I：邀请待通过，C：正常使用， S：已离职 **/
		if ("I".equals(guser.getStatus())) {
			throw new ServiceException("您的帐号还未邀请通过，请确认成为管理员");
		}
		if ("S".equals(guser.getStatus())) {
			throw new ServiceException("您已离职，请联系管理员");
		}
		CompanyParam comParam = new CompanyParam();
		// comParam.setCreator(cuser.getDoctorId());
		comParam.setId(guser.getObjectId());
		
		List<?> list = companyService.searchCompany(comParam).getPageData();
		if (null != list && 0 != list.size()) {
			company = (CompanyVO) list.get(0);
		} else {
			throw new ServiceException("您还没有创建公司，请先创建！");
		}
		GroupParam gparam = new GroupParam();
		gparam.setCompanyId(guser.getObjectId());
		PageVO page = groupDao.search(gparam);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("companyUser", guser);
		data.put("company", company);
		data.put("groupList", page);
		data.put("status", "");
		data.put("msg", "");
		return data;
	}

	/**
	 * 校验医生账号 ：type=3或5，status=1，或者医生已加入一个医院集团
	 * @author wangqiao 重构
	 * @date 2016年4月25日
	 * @param doctorId
	 */
	private void verificationUserByDoctorId(Integer doctorId){
		//参数校验
		if(doctorId== null || doctorId== 0){
			throw new ServiceException("参数doctorId不能为空！");
		}
		
//		DoctorBasicInfo doc = commonDao.getDoctorBasicInfoById(doctorId, 3, null);
		User user = userManager.getUser(doctorId);
		
		
		GroupHospitalDoctorVO groupHospitalDoctorVO= groupDoctorService.getGroupHospitalDoctorByDoctorId(doctorId);
		if(groupHospitalDoctorVO!=null)
			return ;
		if(user.getStatus() != 1) {
			throw new ServiceException("帐号没有审核");
		}
		if (user.getUserType() == null) {
			throw new ServiceException("帐号类型为空");
		}
		if(user.getUserType() == 3 || user.getUserType() == 5) {
			return ;
		}
		throw new ServiceException("帐号类型验证失败");
	}
	
	/**
	 * 校验医生账号 ：type=3或5，status=1，或者医生已加入一个医院集团
	 * @author wangqiao 重构
	 * @date 2016年4月25日
	 * @param doctorId
	 */
	private void verificationUserByDoctorIdWithoutStatus(Integer doctorId){
		//参数校验
		if(doctorId== null || doctorId== 0){
			throw new ServiceException("参数doctorId不能为空！");
		}
		
//		DoctorBasicInfo doc = commonDao.getDoctorBasicInfoById(doctorId, 3, null);
		User user = userManager.getUser(doctorId);
		
		
		GroupHospitalDoctorVO groupHospitalDoctorVO= groupDoctorService.getGroupHospitalDoctorByDoctorId(doctorId);
		if(groupHospitalDoctorVO!=null) {
			return ;
		}
		if (user.getUserType() == null) {
			throw new ServiceException("帐号类型为空");
		}
		if(user.getUserType() == 3 || user.getUserType() == 5) {
			return ;
		}
		throw new ServiceException("帐号类型验证失败");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * getLoginByGroupUser(com.dachen.health.group.company.entity.vo.
	 * GroupUserVO)
	 */
	@Override
	public Map<String, Object> getLoginByGroupUser(Integer doctorId,String groupId) {
		//参数校验
		if(doctorId== null || doctorId== 0){
			throw new ServiceException("参数doctorId不能为空！");
		}
		//返回map
		Map<String, Object> data = null;
		Company company = null;
		// 验证医生账号 type=3或5，status=1，或者医生已加入一个医院集团

		verificationUserByDoctorIdWithoutStatus(doctorId);
		//当 groupId为空时， 未加入任何集团
		if (StringUtils.isEmpty(groupId)) {
			data = new HashMap<String, Object>();
			data.put("userStatus", GroupUserStatus.未加入集团.getIndex());
			data.put("msg", "欢迎您来到玄关健康平台，您可以创建自己的医生圈子");
			return data;
		}
		
		

		// 集团管理员 getGroupUserByIdAndStatus
		// List<GroupUser> normalGroupUser =
		// cuserDao.getGroupUserByIdAndStatus(cuser.getDoctorId(),
		// GroupUserType.集团用户.getIndex(),
		// GroupUserStatus.正常使用.getIndex(),cuser.getGroupId());

//		GroupUser gu = groupDoctorDao.findGroupUser(cuser.getDoctorId(), cuser.getGroupId(),
//				GroupEnum.GroupUserStatus.正常使用.getIndex());
		
		//查询集团管理员
		GroupUser gu = this.getGroupUserByIdAndStatus(null, doctorId, groupId, null, GroupUserStatus.正常使用.getIndex());

		if (gu != null) {
			//集团管理员
			GroupUser guser = gu;
			User u = userManager.getUser(doctorId);
			guser.setName(u.getName());
			guser.setHeadPicFileName(u.getHeadPicFileName());
			Group group = groupDao.getById(guser.getObjectId(), null);
			
			GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);
			if (groupUser != null) {
				User creator = userManager.getUser(groupUser.getDoctorId());
				if (null != creator) {
					group.setCreatorName(creator.getName());
				}
			}
			
			if (null != group && !StringUtil.isEmpty(group.getCompanyId())) {
				company = companyService.getCompanyById(group.getCompanyId());
			}
//			DepartmentParam param = new DepartmentParam();
//			param.setGroupId(guser.getObjectId());
//			List<DepartmentVO> departList = deparDao.search(param);
			List<DepartmentVO> departList = departmentService.searchDepartment(guser.getObjectId(), null, null, null);
			
			data = new HashMap<String, Object>();
			data.put("userStatus", GroupUserStatus.加入集团是管理员.getIndex());
			data.put("company", company);
			data.put("group", group);
			data.put("groupUser", guser);
			data.put("departmentList", departList);
			return data;
		} else {

			// 非集团管理员
			GroupDoctorParam paramDoctor = new GroupDoctorParam();
			paramDoctor.setDoctorId(doctorId);
			// 不再校验group_doctor 关系十分存在，信任传过来的参数groupId
			GroupVO groupVO = groupDoctorDao.findGroupById(groupId);
			if (null == groupVO) {
				throw new ServiceException("找不到要登录的圈子");
			} else {
				
				GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);
				if (groupUser != null) {
					User creator = userManager.getUser(groupUser.getDoctorId());
					if (null != creator) {
						groupVO.setCreatorName(creator.getName());
					}
				}
				
				data = new HashMap<String, Object>();
				
				User u = userManager.getUser(doctorId);
				
				if (u.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
					data.put("userStatus", GroupUserStatus.加入集团非管理员.getIndex());					
				} else {
					data.put("userStatus", GroupUserStatus.加入集团非管理员且未认证.getIndex());
				}
				data.put("group", groupVO);
				return data;
			}
		}
	}

	@Override
	public Map<String, Object> getInviteStatus(String id, Integer type) {
		// 参数校验
		if (id == null || StringUtils.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		if (type == null || type == 0) {
			throw new ServiceException("type不能为空");
		}
		List<Integer> statusList = Arrays.asList(new Integer[] { 1, 2, 3 });
		if (!statusList.contains(type)) {
			throw new ServiceException("type的值不正确");
		}
		// 返回map
		Map<String, Object> data = new HashMap<String, Object>();
		String status = "";
		String groupId = "";

		// 根据type，查询groupdoctor 或者 groupuser
		if (type == 3) {
			// 加入医生集团邀请
			GroupDoctor groupDoctor = groupDoctorDao.getById(id);
			if (groupDoctor == null) {
				status = "X";
			} else if (GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor.getStatus())) {
				status = "C";
			} else if (GroupDoctorStatus.邀请拒绝.getIndex().equals(groupDoctor.getStatus())) {
				status = "N";
			} else if (GroupDoctorStatus.邀请待确认.getIndex().equals(groupDoctor.getStatus())) {
				status = "I";
			} else {
				status = "X";
			}
			// 查询集团id
			if (groupDoctor != null) {
				groupId = groupDoctor.getGroupId();
			}

		} else {
			// 管理员邀请
			// GroupUser groupUser = cuserDao.getGroupUserById(id);
			GroupUser groupUser = this.getGroupUserByIdAndStatus(id, null, null, null, null);
			if (groupUser == null) {
				status = "X";
			} else if (GroupUserStatus.正常使用.getIndex().equals(groupUser.getStatus())) {
				status = "C";
			} else if (GroupUserStatus.邀请拒绝.getIndex().equals(groupUser.getStatus())) {
				status = "N";
			} else if (GroupUserStatus.邀请待通过.getIndex().equals(groupUser.getStatus())) {
				status = "I";
			} else {
				status = "X";
			}
			// 查询集团id
			if (groupUser != null) {
				groupId = groupUser.getObjectId();
			}

		}
		// 查询集团/公司名称
		if (!StringUtils.isEmpty(groupId)) {
			if (type == 1) {
				// 公司名称
				Company company = companyService.getCompanyById(groupId);
				if (company != null) {
					data.put("name", company.getName());
				}
			} else {
				// 集团名称
				Group group = groupDao.getById(groupId);
				if (group != null) {
					data.put("name", group.getName());
				}
			}
		}

		data.put("status", status);
		return data;
	}

	@Override
	public GroupUser getRootGroupManage(String groupId) {
		//参数校验
		if (StringUtil.isEmpty(groupId)) {
			return null;
		}
		return cuserDao.getRootAdminByGroupId(groupId);
		
//		List<GroupUser> list = groupDoctorDao.getGroupUserByGroupId(groupId);
//		for (GroupUser user : list) {
//			if (!StringUtil.isEmpty(user.getRootAdmin()) && user.getRootAdmin().equals("root")) {
//				return user;
//			}
//		}
//		return null;
	}

	/**
	 * 如果医生不属于管理员，也不属于任何集团，则让用户登录，可以直接创建医生集团 如果医生属于一个集团，返回集团信息和是否为集团管理者信息
	 */
	// @Override
	// public Map<String, Object> getLoginByGroupUser(GroupUserVO cuser) {
	// commonService.verificationUserByDoctorId(cuser.getDoctorId());
	// Map<String, Object> data = null;
	// Company company = null;
	//
	// GroupUser uu = new GroupUser();
	// uu.setDoctorId(cuser.getDoctorId());
	// uu.setType(2);
	//// GroupUser guser = cuserDao.getGroupUserById(uu);
	//
	// List<GroupUser> normalGroupUser =
	// cuserDao.getGroupUserByIdAndStatus(cuser.getDoctorId(),
	// GroupUserType.集团用户.getIndex(), GroupUserStatus.正常使用.getIndex());
	// //有正在使用的管理员
	// if(normalGroupUser.size() > 0){
	// GroupUser guser = normalGroupUser.get(0);
	// Group group = groupDao.getById(guser.getObjectId(), null);
	// if(null != group && !StringUtil.isEmpty(group.getCompanyId())) {
	// company = companyDao.getById(group.getCompanyId());
	// }
	// DepartmentParam param = new DepartmentParam();
	// param.setGroupId(guser.getObjectId());
	// List<DepartmentVO> departList = deparDao.search(param);
	// data = new HashMap<String, Object>();
	// data.put("userStatus", GroupUserStatus.加入集团是管理员.getIndex());
	// data.put("company", company);
	// data.put("group", group);
	// data.put("groupUser", guser);
	// data.put("departmentList", departList);
	// return data;
	//
	// }
	// //有其它状态的管理员
	// List<GroupUser> otherGroupUser =
	// cuserDao.getGroupUserByIdAndStatus(cuser.getDoctorId(),
	// GroupUserType.集团用户.getIndex(), null);
	// if(otherGroupUser.size() > 0){
	// GroupUser guser = otherGroupUser.get(0);
	// if("I".equals(guser.getStatus())) {
	// throw new ServiceException("您的帐号还未邀请通过，请确认成为管理员");
	// }
	// if("S".equals(guser.getStatus())) {
	// throw new ServiceException("您已离职，请联系管理员");
	// }
	// }
	//
	//
	// GroupDoctorParam paramDoctor=new GroupDoctorParam();
	// paramDoctor.setDoctorId(cuser.getDoctorId());
	// List<GroupVO>
	// GroupVOs=groupDoctorDao.findAllCompleteStatusByDoctorId(paramDoctor);
	// //集团管理员表中找不到，而且在c_group_doctor中也找不到
	// if (null == GroupVOs || GroupVOs.size() == 0) {
	//// throw new ServiceException("您不是管理员，暂不能登录");
	// data = new HashMap<String, Object>();
	// data.put("userStatus", GroupUserStatus.未加入集团.getIndex());
	// data.put("msg", "欢迎您来到玄关健康平台，您可以创建自己的医生集团");
	// return data;
	// }
	//
	// //************自己创建的集团start*************
	// GroupParam param = new GroupParam();
	// param.setCreator(cuser.getDoctorId());
	// List<?> list = groupDao.search(param).getPageData();
	// if(null != list && 0 != list.size()) {//是否是自己创建的集团
	// GroupVO groupVO = (GroupVO) list.get(0);
	// if(!StringUtil.isEmpty(groupVO.getCompanyId())) {
	// //医生集团有关联公司
	// company = companyDao.getById(groupVO.getCompanyId());
	// }
	// DepartmentParam dparam = new DepartmentParam();
	// dparam.setGroupId(groupVO.getId());
	// List<DepartmentVO> departList = deparDao.search(dparam);
	// data = new HashMap<String, Object>();
	// data.put("userStatus", GroupUserStatus.加入集团是管理员.getIndex());
	// data.put("company", company);
	// data.put("group", groupVO);
	// data.put("departmentList", departList);
	// return data;
	// }
	// //************自己创建的集团end*************
	//
	// //不是管理员，也不是自己创建的集团
	//// throw new ServiceException("您不是管理员，暂不能登录");
	// data = new HashMap<String, Object>();
	// data.put("userStatus", GroupUserStatus.加入集团非管理员.getIndex());//
	// data.put("group", GroupVOs.get(0));
	// return data;
	//
	// }

	// @Override
	// public GroupUser addRootHospitalManage(String groupId,Integer doctorId){
	// GroupUser guser = new GroupUser();
	// guser.setObjectId(groupId);
	// guser.setDoctorId(doctorId);
	// guser.setType(GroupUserType.集团用户.getIndex());
	// guser.setStatus(GroupUserStatus.正常使用.getIndex());
	// guser.setRootAdmin(GroupRootAdmin.root.getIndex());
	// guser.setCreator(ReqUtil.instance.getUserId());
	// guser.setCreatorDate(System.currentTimeMillis());
	// guser.setUpdator(ReqUtil.instance.getUserId());
	// guser.setUpdatorDate(System.currentTimeMillis());
	// return cuserDao.save(guser);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * getGroupUserByGroupId(java.lang.String)
	 */
	@Override
	public List<GroupUser> getGroupUserByGroupId(String groupId) {
		// 参数校验
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("参数groupId不能为空");
		}

		// 查询在职的，集团用户类型的，集团groupId相关的 管理员信息列表
		// return
		// cuserDao.getGroupUserListByIdAndStatus(null,GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex(),groupId);
		return cuserDao.getGroupUserListByIdAndStatus(null, null, groupId, GroupUserType.集团用户.getIndex(),
				GroupUserStatus.正常使用.getIndex());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * getGroupUserByIdAndStatus(java.lang.String, java.lang.Integer,
	 * java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public GroupUser getGroupUserByIdAndStatus(String id, Integer doctorId, String groupId, Integer type,
			String status) {
		return cuserDao.getGroupUserByIdAndStatus(id, doctorId, groupId, type, status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * getGroupUserListByIdAndStatus(java.lang.String, java.lang.Integer,
	 * java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<GroupUser> getGroupUserListByIdAndStatus(String id, Integer doctorId, String groupId, Integer type,
			String status) {
		return cuserDao.getGroupUserListByIdAndStatus(id, doctorId, groupId, type, status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dachen.health.group.company.service.ICompanyUserService#
	 * updateStatusById(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void updateStatusById(String id, String status, Integer updateUserId) {
		// 参数校验
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException("参数id不能为空");
		}
		if (StringUtils.isEmpty(status)) {
			throw new ServiceException("参数status不能为空");
		}
		cuserDao.updateStatusById(id, status, updateUserId);
	}
	
	/**
	 * 获取邀请 短链
	 * @author wangqiao 重构
	 * @date 2016年4月25日
	 * @param type
	 * @param id
	 * @param companyId
	 * @param doctorId
	 * @return
	 */
	private String getCompanyInviteUrl(Integer type, String id, String companyId, Integer doctorId) throws HttpApiException {
		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");
		
//		DoctorBasicInfo doctor = this.getDoctorBasicInfoById(doctorId, 3);
		User doctor = userManager.getUser(doctorId) ;
		if(doctor == null){
			throw new ServiceException("根据doctorId，找不到对应的医生信息");
		}
		
		url = url + "?id=" + id + "&doctorName=" + (doctor.getName() == null ? "" : doctor.getName());
		if(1 == type) {
			Company company = companyService.getCompanyById(companyId);
			url = url + "&type=1" + "&name=" + company.getName();
		}else if(2 == type){
			Group group = groupDao.getById(companyId);
			if(group != null){
				url = url + "&type=2" + "&name=" + group.getName();
			}
		}
		return shortUrlComponent.generateShortUrl(url);
	}
	
	/**
	 * 获取邀请 短链
	 * @author wangqiao
	 * @date 2016年4月26日 重构
	 * @param type
	 * @param id
	 * @param companyId
	 * @param doctorId
	 * @return
	 */
	private String getCompanyInviteTpl(Integer type, String id, String companyId, Integer doctorId) throws HttpApiException {
		String tpl = "";
		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");
		
//		DoctorBasicInfo doctor = this.getDoctorBasicInfoById(doctorId, 3);
		User doctor = userManager.getUser(doctorId) ;
		if(doctor == null){
			throw new ServiceException("根据doctorId，找不到对应的医生信息");
		}
		
		url = url + "?id=" + id + "&doctorName=" + (doctor.getName() == null ? "" : doctor.getName());
		
		final String doctorName = doctor.getName() == null ? "" : doctor.getName();
		String unitName = "";
		String opName = "";
		
		if (1 == type) {
			Company company = companyService.getCompanyById(companyId);
			url = url + "&type=1" + "&name=" + company.getName();
			unitName = company.getName();
			opName = "成为公司管理员";
		} else if (2 == type) {
			Group group = groupDao.getById(companyId);
			if (group != null) {
				url = url + "&type=2" + "&name=" + group.getName();
				unitName = group.getName();
				if (GroupType.group.getIndex().equals(group.getType())) {
					opName = "成为圈子管理员";
				} else {
					opName = "成为医院管理员";
				}
			}
		}
		tpl = baseDataService.toContent("0002", doctorName, unitName, opName, shortUrlComponent.generateShortUrl(url), BaseConstants.XG_YSQ_APP);
		return tpl;
	}


	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Override
	public boolean isAdminOfGroup() {
		int userId = ReqUtil.instance.getUserId();
		return groupUserDao.findUserIsAdmin(userId);
	}

}
