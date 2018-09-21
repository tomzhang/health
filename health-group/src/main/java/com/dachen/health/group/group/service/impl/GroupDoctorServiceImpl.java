package com.dachen.health.group.group.service.impl;

import com.dachen.circle.api.client.group.entity.CGroupUnion;
import com.dachen.circle.api.client.group.entity.CGroupUnionMember;
import com.dachen.circle.api.client.group.entity.CUserGroupAndUnionMap;
import com.dachen.circle.api.client.group.proxy.CircleGroupApiClientProxy;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.api.client.group.entity.CGroup;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.*;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.OnlineRecordRepository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.OnlineRecord;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.dao.ICommonDao;
import com.dachen.health.group.common.service.ICommonService;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.department.entity.vo.DepartmentMobileVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.entity.param.GroupDoctorsParam;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupProfitDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.param.GroupDoctorApplyParam;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.param.GroupsParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.health.group.group.entity.vo.*;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.group.schedule.dao.IOnlineDao;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.service.IDoctorCheckService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.AppEnum;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.*;
import com.dachen.util.tree.ExtTreeNode;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Service
public class GroupDoctorServiceImpl implements IGroupDoctorService {
	
	private Logger logger=LoggerFactory.getLogger(getClass());

    @Autowired
    protected IGroupDoctorDao gdocDao;

    @Autowired
    protected IDepartmentDao deparDao;

    @Autowired
    protected IGroupDao groupDao;

    @Autowired
    protected IDepartmentDoctorDao ddDao;

    @Autowired
    protected IOnlineDao onlineDao;

    @Autowired
    protected UserManager userManager;
	
    @Autowired
    protected IGroupService groupService;
	  
	@Autowired
    protected IDepartmentDoctorService departmentDoctorService;
	
	@Autowired
    protected IDepartmentService departmentService;
    
    @Resource
    protected MobSmsSdk mobSmsSdk;

    @Resource
    protected IGroupProfitService groupProfitService;
    
    @Resource
    protected IGroupProfitDao groupProfitDao;

    @Resource
    protected IBaseUserService baseUserService;

    @Resource
    protected IBaseDataService baseDataService;

    @Autowired
    protected IBusinessServiceMsg businessMsgService;
    
    @Resource
    protected OnlineRecordRepository onlineRecordRepository;
    
    @Resource
    protected JedisTemplate jedisTemplate;
    
    @Resource
    protected IGroupSearchDao groupSearchDao;
    
    @Resource
    protected IPlatformDoctorService platformDoctorService;
    
    @Autowired
    protected ICompanyUserService companyUserService;
    @Autowired
    protected IDoctorCheckService doctorCheckService;
    
	@Autowired
    protected IServiceCategoryService serviceCategoryService;
	
	@Autowired
    protected UserRepository userRepository;

	@Autowired
    private IGroupDoctorDao groupDoctorDao;
    

  //多集团改造  add by wangqiao 2015 12.24
    @Override
	public Map<String, Object> saveBatchGroupDoctor(String groupId, String ignore, String... telNumsOrDocNums) throws HttpApiException {
		if (StringUtil.isEmpty(groupId)) {
			throw new ServiceException("集团Id不能为空");
		}
		//查询集团信息
		Group group = groupDao.getById(groupId, null);
		if (group == null) {
			throw new ServiceException("找不到集团信息");
		}
		
		// 前端传入医生集团和一系列手机号和医生ID号，首先根据这一系列手机号和医生号查出医生，如果没有错就一次性过
		// 如果有部分号有错误 （查不到对应的医生），则根据ignore值进行判断：如果忽略错误则直接向正确医生发起邀请;如果不忽略则列出错误
		// 手机号和医生号让客户端做出选择
		Map<String, Object> maps = new HashMap<String, Object>();
		
		// 既不是手机号也不是医生号的号，error数据
		List<String> errorList = new ArrayList<String>();
		// 加入本集团但是状态不为C 需要发送消息（update记录）
		List<Map<String, GroupDoctor>> updateGroupDoctorList = new ArrayList<Map<String, GroupDoctor>>();
		// 未加入过本集团（add 记录）
		List<Map<String, BaseUserVO>> addGroupDoctorList = new ArrayList<Map<String, BaseUserVO>>();
		// 是手机号但未注册过医生（通知注册）
		List<String> regDoctorList = new ArrayList<String>();
		// 已加入本集团  且状态为C （什么都不用做）
		List<String> alreadyJoinedList = new ArrayList<String>();
		//医生申请加入该集团，等待审核
		List<Map<String, GroupDoctor>> applyJoinList = new ArrayList<Map<String, GroupDoctor>>();


		for (String telOrNum : telNumsOrDocNums) {
			BaseUserVO user = null;
			// 医生号优先
			user = baseUserService.getUserByDoctorNum(telOrNum);
			if (user == null) {
				// 根据手机号查找
				user = baseUserService.getUserByTelephoneAndType(UserEnum.UserType.doctor.getIndex(), telOrNum);
			}
			// 还是等于空
			if (user == null) {
				// 不存在医生类型账号
				if (telOrNum.trim().length() != 11) {
					errorList.add(telOrNum);// 既不是手机号也不是医生号的号
				} else {// 手机号发送，邀请开通并加入
					regDoctorList.add(telOrNum);// 是手机号但没有注册过医生需要发送
				}
			} else {
				// 存在医生类型账号
				GroupDoctor gdoc = new GroupDoctor();
				gdoc.setDoctorId(user.getUserId());
				gdoc.setGroupId(groupId);
				// 帐号状态 C：正在使用，I：已邀请待确认， S：已停用（已离职），O：已踢出 ，N：已拒绝
				List<GroupDoctor> gdList =  gdocDao.findGroupDoctor(gdoc.getDoctorId(),gdoc.getGroupId(),null);
				GroupDoctor gd = null;
				if(gdList != null && gdList.size() > 0){
					gd = gdList.get(0);
					
					if (GroupDoctorStatus.正在使用.getIndex().equals(gd.getStatus()) ) {
						//在本集团状态正常
						alreadyJoinedList.add(telOrNum);// 不发送
					}else if( GroupDoctorStatus.申请待确认.getIndex().equals(gd.getStatus())){
						//在本集团，申请加入
						Map<String, GroupDoctor> map = new HashMap<String, GroupDoctor>();
						map.put(telOrNum, gd);
						applyJoinList.add(map);// 发送
					}else {
						//在本集团状态不正常
						Map<String, GroupDoctor> map = new HashMap<String, GroupDoctor>();
						map.put(telOrNum, gd);
						updateGroupDoctorList.add(map);// 发送
					}
				}else{
					// 未加入本医生集团，需要发送
					user.setGroupId(groupId);
					user.setGroupType(group.getType());
					Map<String,BaseUserVO> map = new HashMap<String, BaseUserVO>();
					map.put(telOrNum, user);
					addGroupDoctorList.add(map);
				}

			}
		}
		// 没有任何错误号一次性发送
		if (errorList.size() == 0 || (StringUtil.isNotBlank(ignore) && "true".equals(ignore))) {
			// 忽略错误发送,向需要发送的队列发送消息
			doJoinGroupSend(updateGroupDoctorList, maps,group);
			//邀请医生加入集团，并通知医生确认
			doUnJoinGroup(addGroupDoctorList, maps,group);
			//发送短信提醒用户注册成为医生
			doTel(regDoctorList, group, maps);
			//提醒该医生已经加入本集团
			doJoinGroupUnSend(alreadyJoinedList, "已加入本圈子", maps);
			
			//已申请的 需要直接审核通过
			maps.put("applyJoinList", applyJoinList);
			
		} else {
			// 不忽略，则返回错误号
			StringBuffer buffer = new StringBuffer();
			for (String str : errorList) {
				buffer.append(str).append(",");
			}
			String temp = buffer.toString();
			temp = temp.substring(0, temp.length() - 1);
			maps.put("wrong", temp);
		}
		
		return maps;
	}
    
    private void doJoinGroupSend(List<Map<String,GroupDoctor>> joinGroupSendList, Map<String,Object> map,Group group) throws HttpApiException {
    	List<String> statusList = Arrays.asList(new String[] {"S","O","N","M","J"});
    	for (Map<String,GroupDoctor> mg : joinGroupSendList) {
    		Map<String,String> data = new HashMap<String,String>();
    		Iterator<Entry<String, GroupDoctor>> it = mg.entrySet().iterator();
			Entry<String, GroupDoctor> entry = it.hasNext() ? it.next() : null;
			if(entry == null){
				continue;
			}
			String key = entry.getKey();
			GroupDoctor gd = entry.getValue();
			if (null != gd) {
				if (statusList.contains(gd.getStatus())) {
					gd.setStatus("I");
					gdocDao.update(gd);
				}
                data.put("sms", this.sendSms(gd,group));
                data.put("note", this.sendNote(gd,group));
                data.put("msg", "再次邀请发送成功");
                data.put("status", String.valueOf(2));
            }
			map.put(key, data);
    	}
    }
    
    private void doUnJoinGroup(List<Map<String,BaseUserVO>> unJoinGroupList, Map<String,Object> map,Group group) throws HttpApiException {
    	for (Map<String,BaseUserVO> mb : unJoinGroupList) {
    		Map<String,String> data = new HashMap<String,String>();
    		Iterator<Entry<String, BaseUserVO>> it = mb.entrySet().iterator();
			Entry<String, BaseUserVO> entry = it.hasNext() ? it.next() : null;
			if(entry == null){
				continue;
			}
			String key = entry.getKey();
			BaseUserVO user = entry.getValue();
    		GroupDoctor  gdoc = new GroupDoctor();
    		gdoc.setGroupId(user.getGroupId());
    		gdoc.setDoctorId(user.getUserId());
    		gdoc.setStatus(GroupEnum.GroupDoctorStatus.邀请待确认.getIndex());
    		gdoc.setType(user.getGroupType());//设置集团类型，是集团还是医院
    		
    		//判断发送者是不是在医生集团，如果不是，则不设置当前groupDoc的ref属性
    		//FIXME 效果不好，循环中做了数据访问
    		GroupDoctor  refGdoc = new GroupDoctor();
    		refGdoc.setGroupId(user.getGroupId());
    		refGdoc.setDoctorId(ReqUtil.instance.getUserId());
    		GroupDoctor refDoc = this.getGroupDoctorById(refGdoc);
    		if (refDoc != null) {
    			gdoc.setReferenceId(refGdoc.getDoctorId());
    		} else {
    			gdoc.setReferenceId(0);
    		}
    		
    		gdoc.setCreator(ReqUtil.instance.getUserId());
    		gdoc.setCreatorDate(new Date().getTime());
    		gdoc.setUpdator(ReqUtil.instance.getUserId());
    		gdoc.setUpdatorDate(new Date().getTime());
    		
    		if (gdoc.getDoctorId().equals(gdoc.getReferenceId())) {
                gdoc.setReferenceId(0);
            }
            GroupDoctor gdoctor = gdocDao.save(gdoc);
            /** 发送邀请操作 **/
            data.put("sms", this.sendSms(gdoctor,group));
            data.put("note", this.sendNote(gdoctor,group));
            map.put(key, data);
    	}
    }

    @Autowired
    protected ShortUrlComponent shortUrlComponent;
    
    private void doTel(List<String> telList, Group group, Map<String,Object> map) throws HttpApiException {
		if (group == null) {
    		return;
    	}
    	
    	for (String tel : telList) {
    		Map<String,String> data = new HashMap<String,String>();
    		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin");
    		
    		if ( GroupType.hospital.getIndex().equals(group.getType())){
    			//加入医院的邀请注册链接
        		url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalRegister");
    		}
    		
            User doctor = userManager.getUser(ReqUtil.instance.getUserId());
            
            //审核端的管理员也可以进行邀请（2016-05-17傅永德）
            if(doctor == null || (doctor.getUserType() != UserType.doctor.getIndex() && doctor.getUserType() != UserType.customerService.getIndex())){
            	throw new ServiceException("找不到邀请人信息，或者邀请人不是医生，或者邀请人不是管理员");
            }
    	
            if(isBdjl(group.getId())){
            	url = url.replaceFirst("health/web", "health/web/bd_h5");
            }
            
    		url = url + "?type=2&groupId=" + group.getId() + "&name=" + group.getName() + "&doctorId=" + doctor.getUserId();
    		String generateUrl = shortUrlComponent.generateShortUrl(url);
    		
    		final String doctorName = doctor.getName();
    		final String groupName = group.getName();
    		
    		//tpl = tpl + "我是" + doctor.getName() + "，我诚挚的邀请你加入" + group.getName() + "，与我一起为广大患者服务，赶紧点击：   " + generateUrl + "   加入吧！";
    		String tpl = baseDataService.toContent("0001", doctorName, groupName, generateUrl);
    		
    		data.put("sms", "");
    		data.put("note", tpl);
    		data.put("status", "");
    		data.put("msg", "");
    		mobSmsSdk.send(tel, tpl, BaseConstants.XG_YSQ_APP);
    		map.put(tel, data);
    	}
    }
    
    private void doJoinGroupUnSend(List<String> TelList, String prompt, Map<String, Object> map) {
    	StringBuffer sb = new StringBuffer();
    	for (String tel : TelList) {
    		sb.append(tel).append("、");
    	}
    	if (sb.length() > 0) {
    		Map<String,String> data = new HashMap<String,String>();
    		data.put("msg", prompt);
    		map.put(sb.substring(0, sb.length()-1), data);
    	}
    }

    @Override
    public Map<String, Object> saveGroupDoctor(GroupDoctor gdoc, Integer againInvite, String telephone) throws HttpApiException {
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(gdoc.getGroupId())) {
            throw new ServiceException("集团Id为空");
        }
        
    	Group  group = groupDao.getById(gdoc.getGroupId(), null);
    	if(group == null){
    		 throw new ServiceException("找不到集团信息");
    	}
        gdoc.setType(group.getType());
        
        /**
         * 查找是否为管理员，如果是管理员则直接通过 如果不是管理员，则需要验证集团设置是否允许集团成员邀请医生
         */
        GroupUser guser = companyUserService.getGroupUserByIdAndStatus(null, gdoc.getCreator(), gdoc.getGroupId(), GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex());
        
        if (null == guser) {
            GroupUser comuser = companyUserService.getGroupUserByIdAndStatus(null, gdoc.getCreator(), group.getCompanyId(), GroupUserType.公司用户.getIndex(),GroupUserStatus.正常使用.getIndex());
            
            if (null == comuser) {
                if (null == group.getConfig() || !group.getConfig().isMemberInvite()) {
                    throw new ServiceException("您没有邀请权限");
                }
            }
        }
        
        if (null != gdoc.getDoctorId()) {
           
            // 验证医生NUM 来查找不是医生ID
            BaseUserVO user = baseUserService.getUser(gdoc.getDoctorId());
            if (user == null || user.getUserType() != UserEnum.UserType.doctor.getIndex()) {
                throw new ServiceException("医生不存在");
            }
            //帐号状态  C：正在使用，I：已邀请待确认， S：已停用（已离职），O：已踢出   N：已拒绝
            GroupDoctor gd = gdocDao.getById(gdoc, null);
            if (null != gd) {
                if ("I".equals(gd.getStatus())) {
                    if (null != againInvite && 1 == againInvite) {
                        data.put("sms", this.sendSms(gd,group));
                        data.put("note", this.sendNote(gd,group));
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
                } else if ("S".equals(gd.getStatus())) {
                    if (null != againInvite && 1 == againInvite) {
                        gd.setStatus("I");
                        gdocDao.update(gd);
                        data.put("sms", this.sendSms(gd,group));
                        data.put("note", this.sendNote(gd,group));
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
                } else if ("O".equals(gd.getStatus())) {
                    if (null != againInvite && 1 == againInvite) {
                        gd.setStatus("I");
                        gdocDao.update(gd);
                        data.put("sms", this.sendSms(gd,group));
                        data.put("note", this.sendNote(gd,group));
                        data.put("status", 2);
                        data.put("msg", "重新邀请成功");
                        return data;
                    } else {
                        data.put("sms", "");
                        data.put("note", "");
                        data.put("status", 2);
                        data.put("msg", "该医生已强制踢出，确定再次发送邀请吗？");
                        return data;
                    }
                } else if ("N".equals(gd.getStatus())) {
                    if (null != againInvite && 1 == againInvite) {
                        gd.setStatus("I");
                        gdocDao.update(gd);
                        data.put("sms", this.sendSms(gd,group));
                        data.put("note", this.sendNote(gd,group));
                        data.put("status", 2);
                        data.put("msg", "重新邀请成功");
                        return data;
                    } else {
                        data.put("sms", "");
                        data.put("note", "");
                        data.put("status", 2);
                        data.put("msg", "对方已拒绝加入，确定再次发送邀请吗？");
                        return data;
                    }
                } else if ("J".equals(gd.getStatus())) {
                    data.put("msg", "该医生已申请加入医生集团，请审核！");
                    return data;
                } else if ("M".equals(gd.getStatus())) {
                    if (null != againInvite && 1 == againInvite) {
                        gd.setStatus("I");
                        gdocDao.update(gd);
                        data.put("sms", this.sendSms(gd,group));
                        data.put("note", this.sendNote(gd,group));
                        data.put("status", 2);
                        data.put("msg", "重新邀请成功");
                        return data;
                    } else {
                        data.put("sms", "");
                        data.put("note", "");
                        data.put("status", 2);
                        data.put("msg", "该医生已拒绝加入医生集团，确定再次发送邀请吗？");
                        return data;
                    }
                } else if ("C".equals(gd.getStatus())) {
                    data.put("msg", "该医生已加入医生圈子");
                    return data;
                }
            }

            if (gdoc.getDoctorId().equals(gdoc.getReferenceId())) {
                gdoc.setReferenceId(0);
            }

            GroupDoctor gdoctor = gdocDao.save(gdoc);

            /** 发送邀请操作 **/
            data.put("sms", this.sendSms(gdoctor,group));
            data.put("note", this.sendNote(gdoctor,group));
            return data;
        } else {
            if (StringUtil.isEmpty(telephone) || StringUtil.isEmpty(gdoc.getGroupId())) {
                throw new ServiceException("手机号码或集团Id为空");
            }
            /**
             * 根据手机号码发送邀请操作 不在平台内的医生，通过手机号码发送邀请下载客户端注册成为医生
             */
            String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin");
    		if ( GroupType.hospital.getIndex().equals(group.getType())){
    			//加入医院的邀请注册链接
        		url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalRegister");
    		}

            User doctor = userManager.getUser(ReqUtil.instance.getUserId());
            
            //审核端管理员也可以邀请（2016-05-17傅永德）
            if(doctor == null || (doctor.getUserType() != UserType.doctor.getIndex() && doctor.getUserType() != UserType.customerService.getIndex())){
            	throw new ServiceException("找不到邀请人信息，或者邀请人不是医生，或者邀请人不是管理员");
            }
            if (isBdjl(group.getId())) {
            	url = url.replaceFirst("health/web", "health/web/bd_h5");
            }
            
            url = url + "?type=2&groupId=" + group.getId() + "&name=" + group.getName() + "&doctorId=" + doctor.getUserId();
            String generateUrl = shortUrlComponent.generateShortUrl(url);
            //tpl = tpl + "我是" + doctor.getName() + "，我诚挚的邀请你加入" + group.getName() + "，与我一起为广大患者服务，赶紧点击：   " + generateUrl + "   加入吧！";
			final String content = baseDataService.toContent("0059", doctor.getName(), group.getName(), generateUrl);
            data.put("sms", "");
            data.put("note", content);
            data.put("status", "");
            data.put("msg", "");
            
			mobSmsSdk.send(telephone, content, isBdjl(group.getId()) ? BaseConstants.BD_SIGN : BaseConstants.XG_SIGN);
            
            return data;
        }

    }

    /**
     * </p>申请加入医生集团</p>
     * 
     * @param gdoc
     * @return
     * @author fanp
     * @date 2015年9月16日
     */
//    public Map<String, Object> saveByApply(GroupDoctor gdoc) {
//        // 1.首先验证集团是否存在
//        // 2.验证是否为医生
//        // 3.加入医生集团待审核
//
//        Map<String, Object> data = new HashMap<String, Object>();
//        if (StringUtil.isEmpty(gdoc.getGroupId())) {
//            throw new ServiceException("集团Id为空");
//        }
//
//        Group group = groupDao.getById(gdoc.getGroupId(), null);
//        if (group == null) {
//            data.put("msg", "集团不存在");
//            return data;
//        }
//
//        BaseUserVO vo = baseUserService.getUser(gdoc.getDoctorId());
//        if (vo == null || vo.getUserType() != UserEnum.UserType.doctor.getIndex()) {
//            data.put("msg", "您不是医生，无法申请加入医生集团");
//            return data;
//        }
//
//        // 查询是否加入该医生集团
//
//        GroupDoctor gd = gdocDao.getById(gdoc, null);
//        if (gd != null) {
//            //存在该医生集团
//            
//            if (GroupEnum.GroupDoctorStatus.邀请待确认.getIndex().equals(gd.getStatus())) {
//                data.put("msg", "已邀请您加入该医生集团，请先确认");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.离职.getIndex().equals(gd.getStatus())) {
//                data.put("msg", "您已离职，无法申请加入该医生集团");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.踢出.getIndex().equals(gd.getStatus())) {
//                data.put("msg", "您已强制踢出，无法申请加入该医生集团");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.邀请拒绝.getIndex().equals(gd.getStatus())) {
//                gd.setStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
//                gdocDao.update(gd);
//                data.put("msg", "申请成功");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.申请待确认.getIndex().equals(gd.getStatus())) {
//                data.put("msg", "您已申请加入医生集团，待审核！");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.申请拒绝.getIndex().equals(gd.getStatus())) {
//                gd.setStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
//                gdocDao.update(gd);
//                data.put("msg", "重新申请成功");
//                return data;
//            } else if (GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(gd.getStatus())) {
//                data.put("msg", "您已加入该医生集团");
//                return data;
//            }
//        }else{
//        	gdoc.setStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
//            gdocDao.save(gdoc);
//            data.put("msg", "申请成功");
//            return data;
//        }
//
//        return data;
//    }


    /**
     * 发送app系统消息邀请函
     * 
     */
    private String sendSms(GroupDoctor user,Group group) {
        try {
            /**
             * url
             */
            String url = getInviteUrl(3, user.getId(), group, user.getDoctorId());

            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setStyle(6);
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setPic(GroupUtil.getInviteMemberImage());
            if(group.getType()!=null&&group.getType().equals(GroupType.hospital.getIndex()))
            {
            	imgTextMsg.setTitle(UserChangeTypeEnum.PROFILE_INVITE_DOCTOR_HOSPITAL.getAlias());
            	imgTextMsg.setContent("邀请您成为医院医生");
            }
            else
            {
            	 imgTextMsg.setTitle(UserChangeTypeEnum.PROFILE_INVITE_DOCTOR.getAlias());
                 imgTextMsg.setContent("邀请您成为集团医生");
            }
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
     * 发送短信邀请信息
     */
    private String sendNote(GroupDoctor user,Group group) throws HttpApiException {
//        DoctorBasicInfo doctor = commonService.getDoctorBasicInfoById(user.getDoctorId(), 3);
        User doctor = userManager.getUser(user.getDoctorId());
        
        String tpl = getInviteTpl(3, user.getId(), group, user.getDoctorId());
        mobSmsSdk.send(doctor.getTelephone(), tpl, BaseConstants.XG_YSQ_APP);
        return tpl;
    }


    @Override
    public boolean deleteGroupDoctor(String... ids) {
        if (null == ids || 0 == ids.length) {
            throw new ServiceException("ids为空");
        }
        List<GroupDoctorVO> gdList = gdocDao.findListByIds(ids);
        List<Integer> docList = new ArrayList<Integer>();
        for (GroupDoctorVO gdoc : gdList) {
            docList.add(gdoc.getDoctorId());
        }

        if (ddDao.getCountDepartmentDoctorByDoctorIds(docList) > 0) {
            throw new ServiceException("请先删除科室下面的医生关联");
        }

        for (String id : ids) {
            // 医生集团删除医生
            GroupDoctor gdoctor = new GroupDoctor();
            gdoctor.setId(id);
            GroupDoctor gd = gdocDao.getById(gdoctor);
            if (gd != null) {
//                GroupProfitParam param = new GroupProfitParam();
//                param.setGroupId(gd.getGroupId());
//                param.setDoctorId(gd.getDoctorId());
//            	GroupUser gu = gdocDao.findGroupUser(gd.getDoctorId(),gd.getGroupId(), GroupEnum.GroupUserStatus.正常使用.getIndex());
            	GroupUser gu = companyUserService.getGroupUserByIdAndStatus(null, gd.getDoctorId(),gd.getGroupId(),
            			null, GroupUserStatus.正常使用.getIndex());
            	if(gu != null && "root".equals(gu.getRootAdmin())){
            		throw new ServiceException("不能删除超级管理员");
            	}
                groupProfitService.delete(gd.getDoctorId(),gd.getGroupId());
                groupDao.removeConsultationPackDoctor(gd.getGroupId(), gd.getDoctorId());
            }
        }
        return gdocDao.delete(ids);
    }

    @Override
    public PageVO searchGroupDoctor(GroupDoctorParam param) {
 
        return gdocDao.search(param);
    }

	@Override
	public PageVO searchDoctorByKeyWord(DepartmentDoctorParam param) {
		if(StringUtil.isEmpty(param.getGroupId())) {
			throw new ServiceException("集团Id为空");
		}
		return gdocDao.searchDoctorByKeyWord(param);
	}

	
    // /**
    // * 验证当前帐号是否存在并且是否可用
    // * @param gdoc
    // * @return
    // */
    // private boolean validationExist(GroupDoctor gdoc) {
    // GroupDoctor gd = this.getGroupDoctorById(gdoc);
    // if(null == gd) {
    // return false;
    // }
    // if("I".equals(gd.getStatus())) {
    // throw new ServiceException("帐号还未确认加入");
    // }else if("S".equals(gd.getStatus()) || "O".equals(gd.getStatus())) {
    // throw new ServiceException("您已离职，请联系管理员");
    // }
    // if(null != gd && "C".equals(gd.getStatus())) {
    // return true;
    // }
    // return false;
    // }

    /**
     * 根据Id获取详情信息
     * 
     * @param gdoc
     * @return
     */
    private GroupDoctor getGroupDoctorById(GroupDoctor gdoc) {
        return gdocDao.getById(gdoc);
    }

    // @Override
    // public Map<String, Object> getGroupDoctorByDoctorId(GroupDoctor gdoc) {
    // gdoc = gdocDao.getById(gdoc);
    // return null;
    // }

    @Override
    public Map<String, List<? extends Object>> getGroupAndDepartmentById(GroupDoctorParam param) {
        if (null == param.getDoctorId()) {
            throw new ServiceException("医生Id为空");
        }
        List<GroupVO> gList = gdocDao.findAllCompleteStatusByDoctorId(param);
        List<Department> dList = gdocDao.getDepartmentById(gList, param.getDoctorId());
        Map<String, List<? extends Object>> data = new HashMap<String, List<?>>();
        data.put("groupList", gList);
        data.put("departmentList", dList);
        return data;
    }

    @Override
    public Map<String, List<? extends Object>> getDepartmentAndDoctorById(String groupId) {
//        DepartmentParam dparam = new DepartmentParam();
//        dparam.setGroupId(groupId);
//        List<DepartmentVO> dList = deparDao.search(dparam);
		List<DepartmentVO> dList = departmentService.searchDepartment(groupId, null, null, null);
        List<GroupDoctorVO> gdList = gdocDao.findListByDepartmentIds(dList, groupId);
        Map<String, List<? extends Object>> data = new HashMap<String, List<?>>();
        data.put("departmentList", dList);
        data.put("doctorList", gdList);
        return data;
    }

    @Autowired
    protected CircleGroupApiClientProxy circleGroupApiClientProxy;

    @Override
    public Map<String, Object> getGroupListAndAllSubListById(GroupDoctorParam gdoc) {
        Map<String, Object> data = Maps.newHashMap();
        if (null == gdoc.getDoctorId()) {
            return data;
        }
        GroupDoctor doctor = new GroupDoctor();
        doctor.setDoctorId(gdoc.getDoctorId());

        // 查询医联体列表
        try {
            CUserGroupAndUnionMap cUserGroupAndUnionMap = circleGroupApiClientProxy.findUserGroupAndUnionMap(gdoc.getDoctorId());
            if (null != cUserGroupAndUnionMap && SdkUtils.isNotEmpty(cUserGroupAndUnionMap.getUnions())) {
                List<CGroupUnion> memberList = cUserGroupAndUnionMap.getUnions();
                data.put("unionList", memberList);
            } else {
                data.put("unionList", new ArrayList<>(0));
            }
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }

        //查询医生所在的所有集团信息
        List<GroupDoctor> gds = gdocDao.getByDoctorId(gdoc.getDoctorId(),GroupType.group.getIndex());
        if (gds == null || gds.isEmpty()) {
//        	  return new HashMap<String, List<?>>();
              data.put("groupList", new ArrayList<GroupVO>());
              data.put("departmentList", new ArrayList<DepartmentMobileVO>());
        }else{
            List<GroupVO> groupList = getGroupListByGroupDoctorList(gds);
            List<DepartmentMobileVO> departmentList = gdocDao.getMyDepartmentListById(groupList, gdoc.getDoctorId());
            data.put("groupList", groupList);
            data.put("departmentList", departmentList);
        }
        //查询医生所在的所有科室信息
//        List<GroupDoctor> deptDoctorList = gdocDao.getByDoctorId(gdoc.getDoctorId(),GroupType.dept.getIndex());
//        if (deptDoctorList == null || deptDoctorList.isEmpty()) {
//            data.put("deptList", new ArrayList<GroupVO>());
//        }else{
//            List<GroupVO> deptId = getGroupListByGroupDoctorList(deptDoctorList);
//            data.put("deptList", deptId);
//        }
        //查询医生所在的医院信息
        List<GroupDoctor> hospitalDoctorList = gdocDao.getByDoctorId(gdoc.getDoctorId(),GroupType.hospital.getIndex());
        if (hospitalDoctorList == null || hospitalDoctorList.isEmpty()) {
//        	  return new HashMap<String, List<?>>();
              data.put("hospitalList", new ArrayList<GroupVO>());
              data.put("hospitalDepartmentList", new ArrayList<DepartmentMobileVO>());
        }else{
            List<GroupVO> hospitalList = getGroupListByGroupDoctorList(hospitalDoctorList);
            List<DepartmentMobileVO> hospitalDepartmentList = gdocDao.getMyDepartmentListById(hospitalList, gdoc.getDoctorId());
            data.put("hospitalList", hospitalList);
            data.put("hospitalDepartmentList", hospitalDepartmentList);
        }

        Map<String, Object> deptInfo = Maps.newHashMap();

        //添加科室信息
        User user = userRepository.getUser(gdoc.getDoctorId());
        if (Objects.nonNull(user) && Objects.nonNull(user.getDoctor()) && Objects.nonNull(user.getDoctor().getHospitalId())
            && Objects.nonNull(user.getDoctor().getDeptId())) {

            String hospitalId = user.getDoctor().getHospitalId();
            String deptId = user.getDoctor().getDeptId();

            List<Group> groupDepts = groupDao.findByHospitalAndDept(hospitalId, deptId);
            if (CollectionUtils.isEmpty(groupDepts)) {
                deptInfo.put("hasDept", false);
                deptInfo.put("deptCount", 0);
                deptInfo.put("hospitalName", user.getDoctor().getHospital());
                deptInfo.put("deptName", user.getDoctor().getDepartments());
            } else {
                GroupDoctor groupDoctor = groupDoctorDao.findByDoctorIdAndType(gdoc.getDoctorId(), GroupType.dept.getIndex());
                if (Objects.isNull(groupDoctor)) {
                    deptInfo.put("hasDept", false);
                    deptInfo.put("deptCount", groupDepts.size());
                    deptInfo.put("hospitalName", user.getDoctor().getHospital());
                    deptInfo.put("deptName", user.getDoctor().getDepartments());
                } else {
                    deptInfo.put("hasDept", true);
                    List<GroupVO> myGroup = new ArrayList<>(1);
                    myGroup.add(convertDoctorToGroup(groupDoctor.getGroupId()));
                    deptInfo.put("deptList", myGroup);  // 只显示我的科室
                }
            }
        } else {
            deptInfo.put("hasDept", false);
        }

        data.put("deptInfo", deptInfo);

        return data;
    }
    
    /**
     * 读取 groupDoctor 中 group相关属性
     * @param gdList
     * @return
     *@author wangqiao
     *@date 2016年1月12日
     */
    private List<GroupVO> getGroupListByGroupDoctorList(List<GroupDoctor> gdList ){
    	List<GroupVO> groupList = new ArrayList<GroupVO>();
    	for (GroupDoctor gdoc : gdList) {
            GroupVO vo = convertDoctorToGroup(gdoc.getGroupId());
            if (null == vo) {
                continue;
            }
            groupList.add(vo);
    	}
    	return groupList;
    }

    protected GroupVO convertDoctorToGroup(String groupId){
        Group group = groupDao.getById(groupId);
        if(group == null){
            return null;
        }
        GroupVO vo = new GroupVO();
        vo.setId(group.getId());
        vo.setCompanyId(group.getCompanyId());
        vo.setName(group.getName());
        vo.setIntroduction(group.getIntroduction());
        vo.setCreator(group.getCreator());
        vo.setUpdator(group.getUpdator());
        vo.setType(group.getType());

        vo.setUpdatorDate(group.getUpdatorDate());
        vo.setCreatorDate(group.getCreatorDate());
        vo.setDepartmentList(gdocDao.getAllDepartmentListById(vo));
        //vo.setGroupIconPath(UserHelper.buildCertPath(group.getId(), group.getCreator()));
        vo.setGroupIconPath(group.getLogoUrl());
        vo.setCertStatus(group.getCertStatus());

        if (StringUtils.equals(GroupVO.TYPE_HOSPITAL, group.getType()) || StringUtils.equals(GroupVO.TYPE_DEPT, group.getType())) {
            return vo;
        } else if (StringUtils.equals(GroupVO.TYPE_GROUP, group.getType()) && StringUtils.equals(GroupEnum.GroupApplyStatus.pass.getIndex(), group.getApplyStatus())) {
            return vo;
        }
        return null;

    }
    @Override
    public void setMainGroup(GroupDoctorParam param) {
    	if (param.getDoctorId() == null || param.getGroupId() == null) {
    		throw new IllegalArgumentException("doctorId or groupId is null");
    	}
    	gdocDao.updateMainGroup(param.getDoctorId(), param.getGroupId());
    }

    @Override
    public List<GroupDoctorVO> getByDoctorId(Integer doctorId) {
    	List<GroupDoctorVO> gdocs = gdocDao.getByDoctorId(doctorId, GroupType.group.getIndex(), GroupDoctorVO.class);
    	for (GroupDoctorVO gdoc : gdocs) {
    		Group group = groupDao.getById(gdoc.getGroupId());
    		if(group==null){
    			continue;
    		}
    		gdoc.setGroupName(group.getName());
    	}
    	return gdocs;
    }

    @Override
    public List<GroupDoctor> getByDoctorId(Integer doctorId, String type) {
        return gdocDao.getByDoctorId(doctorId, type);
    }
    
    public List<GroupVO> getGroups(Integer doctorId) {
    	if (doctorId == null) {
    		throw new IllegalArgumentException("doctorId is null");
    	}
    	//修改BUG 选择集团的时候要将集团与医院都显示出来 update by jhj
    	List<GroupDoctor> gdocs = gdocDao.getByDoctorId(doctorId,null);
    	
    	List<GroupVO> list = new ArrayList<GroupVO>();
    	
    	for (GroupDoctor gdoc : gdocs) {
    		Group group = groupDao.getById(gdoc.getGroupId());
    		if(group==null){
    			continue;
    		}
    		GroupVO vo = toVO(group, gdoc);
    		//当用户是管理员时，查询申请加入集团的人数，便于审核
    		if (vo.getIsAdmin()) {
    			vo.setApplyCount((long)gdocDao.findGroupDoctor(null, group.getId(), "J").size());
    		}
    		list.add(vo);
    	}
    	return list;
    }
    
    public List<GroupVO> getGroupsOnDuty(Integer doctorId) {
    	if (doctorId == null) {
    		throw new IllegalArgumentException("doctorId is null");
    	}
    	/**
    	 * 我要值班的列表需要添加医院信息 
    	 * udpate by wangl
    	 */
    	List<GroupDoctor> gdocs = gdocDao.getByDoctorId(doctorId,null);
    	
    	List<GroupVO> list = new ArrayList<GroupVO>();
    	
    	// 过滤屏蔽的集团 add by tanyf 20160608
    	for (GroupDoctor gdoc : gdocs) {
    		if(gdoc==null || (!groupService.isNormalGroup(gdoc.getGroupId()))){
    			continue;
    		}
    		Group group = groupDao.getById(gdoc.getGroupId());
    		if (group.getConfig() != null) {
    			int now = GroupUtil.getCurrentTime();
    			if (group.getConfig().getDutyStartTime() == null) {
    				group.getConfig().setDutyStartTime(GroupUtil.GROUP_DUTY_START_TIME);
    			}
    			if (group.getConfig().getDutyEndTime() == null) {
    				group.getConfig().setDutyEndTime(GroupUtil.GROUP_DUTY_END_TIME);
    			}
    			int start = GroupUtil.toTime(group.getConfig().getDutyStartTime());
    			int end = GroupUtil.toTime(group.getConfig().getDutyEndTime());
    			if (now < start || now > end) {
    				continue;
    			}
    		}
    		list.add(toVO(group, gdoc));
    	}
    	
    	return list;
    }
    
    private GroupVO toVO(Group group, GroupDoctor gdoc) {
    	GroupVO vo = new GroupVO();
		vo.setId(group.getId());
		vo.setName(group.getName());
		vo.setIsMain(gdoc.isMain());
//		GroupUser guser = new GroupUser();
//		guser.setDoctorId(gdoc.getDoctorId());
//		guser.setObjectId(group.getId());
//		guser.setStatus("C");
//		vo.setIsAdmin(cuserDao.getGroupUserById(guser) != null);
		
		GroupUser guser = companyUserService.getGroupUserByIdAndStatus(null, gdoc.getDoctorId(), group.getId(), null, GroupUserStatus.正常使用.getIndex());
		if(guser == null){
			vo.setIsAdmin(false );
		}else{
			vo.setIsAdmin(true );
		}
		vo.setType(gdoc.getType());
		if (group.getConfig() != null) {
			vo.setDutyStartTime(group.getConfig().getDutyStartTime());
			vo.setDutyEndTime(group.getConfig().getDutyEndTime());
		}
		//add by wangqiao 2016-01-18
		vo.setIntroduction(group.getIntroduction());
		vo.setGroupIconPath(group.getLogoUrl());
		vo.setCertStatus(group.getCertStatus());
		return vo;
    }
    
    private Map<String, List<? extends Object>> getAllDataByType(GroupUser guser, Integer doctroId) {
        Map<String, List<? extends Object>> data = null;
        if (1 == guser.getType()) {
            List<GroupVO> gList = gdocDao.findAllCompleteStatusByCompanyId(guser.getObjectId());
            List<DepartmentMobileVO> departmentList = gdocDao.getMyDepartmentListById(gList, doctroId);
            data = new HashMap<String, List<?>>();
            data.put("groupList", gList);
            data.put("departmentList", departmentList);
            return data;
        }
        if (2 == guser.getType()) {
            List<GroupVO> gList = gdocDao.findAllCompleteStatusByGroupId(guser.getObjectId());
            List<DepartmentMobileVO> departmentList = gdocDao.getMyDepartmentListById(gList, doctroId);
            data = new HashMap<String, List<?>>();
            data.put("groupList", gList);
            data.put("departmentList", departmentList);
            return data;
        }
        return null;
    }

    @Override
    public PageVO getUndistributedDoctorByGroupId(GroupDoctorParam gdoc) {
        return gdocDao.findUndistributedListById(gdoc);
    }

    public List<GroupDoctorVO> getGroupDoctorListByGroupId(String groupId) {
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("groupId为空");
        }
        return gdocDao.findGroupDoctorListByGroupId(groupId);
    }

    @Override
    public PageVO getMyInviteRelationListByDoctorId(Integer doctorId, String groupId) {
        if (null == doctorId || 0 == doctorId) {
            throw new ServiceException("医生Id为空");
        }
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }
        return gdocDao.findInviteListByIdWithNormalStatus(doctorId, groupId);
    }

    /**
     * </p>查找登录用户所属集团管理员所有的邀请</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月28日
     */
    public List<ExtTreeNode> getInviteRelationTree(Integer doctorId, String groupId) {
        List<ExtTreeNode> list = null;
        // 查找集团创建者
        Group group = groupDao.getById(groupId, null);

        if (group != null) {
            list = gdocDao.getMyInviteRelaions(group.getCreator(), groupId);
        }

        return list;
    }

    @Override
    public void dimissionByCorrelation(GroupDoctor gdoc) throws HttpApiException {
        if (StringUtil.isEmpty(gdoc.getGroupId())) {
            throw new ServiceException("集团Id为空");
        }
        if (null == gdoc.getDoctorId() || 0 == gdoc.getDoctorId()) {
            throw new ServiceException("医生Id为空");
        }
        //查询集团所有管理员列表
        List<GroupUser> groupUserList = companyUserService.getGroupUserByGroupId(gdoc.getGroupId());
        if(groupUserList != null && groupUserList.size() ==1){
        	if(groupUserList.get(0).getDoctorId() == gdoc.getDoctorId()){
        		throw new ServiceException("该医生是唯一管理员，不能解除！");
        	}
        }
        
//        Query<GroupUser> q = cuserDao.getQueryByGroupIds(gdoc.getGroupId());
//		if (q.countAll() == 1 && q.get().getDoctorId() == gdoc.getDoctorId()) {
//        	throw new ServiceException("该医生是唯一管理员，不能解除！");
//        }

        /* 删除所有集团管理员的关联信息 */
//        cuserDao.deleteByGroupUser(gdoc.getGroupId(), gdoc.getDoctorId());
        companyUserService.deleteGroupUserByLeaveGroup(gdoc.getGroupId(), gdoc.getDoctorId());
        /* 删除所有科室与医生的关联信息 */
        ddDao.deleteAllCorrelation(gdoc.getGroupId(), gdoc.getDoctorId());
        /* 删除所有值班信息 */
        onlineDao.deleteAllByDoctorData(gdoc.getGroupId(), gdoc.getDoctorId());
        gdoc.setStatus("S");
        gdoc.setOnLineState("0");
        gdocDao.updateStatus(gdoc);

        /**
         * 发送离职指令
         */
        businessMsgService.deleteGroupNotify(gdoc.getDoctorId(), gdoc.getGroupId());

        // 离职医生集团
        GroupDoctor gd = gdocDao.getById(gdoc);
        if (gd != null) {
//            GroupProfitParam param = new GroupProfitParam();
//            param.setGroupId(gd.getGroupId());
//            param.setDoctorId(gd.getDoctorId());
            groupProfitService.delete(gd.getDoctorId(),gd.getGroupId());
        }
    }

    @Override
    public Map<String, Object> HasPermissionByInvite(GroupDoctor cuser) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(cuser.getGroupId())) {
            data.put("hasPermission", 0);
            data.put("msg", "集团Id为空");
            return data;
        }
        try {
            Group group = groupDao.getById(cuser.getGroupId(), null);
            if (null != group) {
            	if (GroupEnum.GroupSkipStatus.normal.getIndex().equals(group.getSkip())){
            		if (group.getConfig().isMemberInvite()) {
                        data.put("hasPermission", 1);
                        data.put("msg", "");
                        return data;
                    } else {
                        data.put("hasPermission", 0);
                        data.put("msg", "");
                        return data;
                    }
            	}else{// 被屏蔽的集团
            		 data.put("hasPermission", 0);
                     data.put("msg", "");
                     return data;
            	}
            } else {
                data.put("hasPermission", 0);
                data.put("msg", "集团不存在");
                return data;
            }
        } catch (Exception e) {
            data.put("hasPermission", 0);
            data.put("msg", "集团不存在");
            return data;
        }
    }

    @Override
    public Map<String, Object> sendNoteInviteBydoctorId(GroupDoctor gdoc) throws HttpApiException {
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(gdoc.getGroupId())) {
            data.put("tpl", null);
            data.put("msg", "集团Id为空");
            return data;
        }
        if (null == gdoc.getDoctorId() || 0 == gdoc.getDoctorId()) {
            data.put("tpl", null);
            data.put("msg", "医生Id为空");
            return data;
        }
        /** 发送邀请操作 **/
        Group group = groupDao.getById(gdoc.getGroupId(), null);

        String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin");
        
		if ( GroupType.hospital.getIndex().equals(group.getType())){
			//加入医院的邀请注册链接
    		url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalRegister");
		}

		if (isBdjl(group.getId())) {
			url = url.replaceFirst("health/web", "health/web/bd_h5");
		}

		//0表示医生端
		Integer type = 0;
		if (ReqUtil.instance.isMedicalCircle()) {
		    //表示医生圈
		    type = 1;
        }
		
        User doctor = userManager.getUser(gdoc.getDoctorId());
        
        url = url + "?groupId=" + group.getId() + "&doctorId=" + doctor.getUserId() + "&type=" + type;
        String generateUrl = shortUrlComponent.generateShortUrl(url);
        //1031:我是{0}，我诚挚的邀请你加入{1}，与我一起为广大患者服务，赶紧点击： {2} 加入吧！
        String note = baseDataService.toContent("1031", doctor.getName(), group.getName(), generateUrl)+"【医生圈】";
        data.put("note", note);
        data.put("msg", "");
        data.put("shortUrl", generateUrl);
        return data;
    }

    public GroupDoctor getOne(String groupId, Integer doctorId) {
    	if (StringUtil.isEmpty(groupId) || doctorId == null) {
    		throw new ServiceException("集团Id或医生Id为空");
    	}
    	GroupDoctor gdoc = new GroupDoctor();
    	gdoc.setGroupId(groupId);
    	gdoc.setDoctorId(doctorId);
    	return getOne(gdoc);
    }

    @Override
	public GroupDoctor getOne(GroupDoctor gdoc) {
		if (gdoc == null) {
			throw new ServiceException(4005, "请求参数为空！");
		}
		if (StringUtil.isEmpty(gdoc.getId()) && gdoc.getDoctorId() == null) {
			throw new ServiceException(4006, "id和医生id不能同时为空！");
		}
		if (gdoc.getDoctorId() != null && StringUtil.isEmpty(gdoc.getGroupId())) {
    		throw new ServiceException("集团Id不能为空");
    	}

		GroupDoctor groupDoctor = gdocDao.getById(gdoc,null);//add by wangqiao  状态不是正在使用的医生集团信息也有查询需求
		if (groupDoctor == null) {
			throw new ServiceException(4007, "找不到对应医生集团！");
		}
		return groupDoctor;
	}

	@Override
	public void setTaskTimeLong(GroupDoctor gdoc) {
		
		Long taskDuration=gdoc.getTaskDuration();
		if(taskDuration==null){
			throw new ServiceException(40008, "时长为空！");
		}
		
		GroupDoctor groupDoctor = getOne(gdoc);
	
		groupDoctor.setTaskDuration(taskDuration);
		
		groupDoctor.setUpdator(gdoc.getUpdator());
		groupDoctor.setUpdatorDate(gdoc.getUpdatorDate());
		gdocDao.update(groupDoctor);
		
		
	}
	
	@Override
	public void updateTaskDuration(String groupId) {
		gdocDao.updateTaskDuration(groupId);
	}
	
	@Override
	public boolean doctorOnline(GroupDoctor gdoc) throws HttpApiException {

		// TODO 这里写一个判断，如果当前时间不是值班时间段里，不能在线值班。
        boolean isCanOnline = false;
        try {
            isCanOnline = groupService.isWithinDutyTime(gdoc.getGroupId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }


		if (isCanOnline) {
			// 逻辑错误：0004：上线下线既然在redis做了，不要在数据库GroupDoctor做状态保存了，数据库维护记录表即可
			gdoc = gdocDao.getById(gdoc);
			OnlineRecord record=new OnlineRecord();
			Long currentDateTime=new Date().getTime();
			record.setCreateTime(currentDateTime);
			record.setLastModifyTime(currentDateTime);
			record.setOnLineTime(currentDateTime);
			record.setGroupDoctorId(gdoc.getId());
			onlineRecordRepository.save(record);
			
			gdoc.setOnLineState("1");
			gdoc.setUpdator(ReqUtil.instance.getUserId());
			//用户在上线时不再更新UpdatorDate字段，该字段仅在用户申请加入集团、审核通过时进行更新。（相关故事XGSF-6880）
			//gdoc.setUpdatorDate(currentDateTime);
			gdoc.setOnLineTime(currentDateTime);
			gdocDao.update(gdoc);

			// 逻辑错误：0005：不能用医生集团ID＋XXX标记，存储个SET或则LIST吗？
			jedisTemplate.set(KeyBuilder.buildGroupOnLineKey(gdoc.getGroupId()), gdoc.getId());

			//	jedisTemplate.hset(KeyBuilder.buildGroupOnLineKey(gdoc.getGroupId()), gdoc.getDoctorId()+"", gdoc.getId());
			//	jedisTemplate.incr(KeyBuilder.buildGroupOnLineCountKey(gdoc.getGroupId()));

			businessMsgService.userChangeNotify(UserChangeTypeEnum.DOCTOR_ONLINE, ReqUtil.instance.getUser().getUserId(), null);
			
			JobTaskUtil.doctorOffline(gdoc.getId(), true);
		}

		return isCanOnline;
	}

	@Override
	public void doctorOffline(GroupDoctor gdoc, EventEnum event) throws HttpApiException {
		// 逻辑错误：0006：记录的时长为当月值班时长，当月没有体现,
		gdoc = gdocDao.getById(gdoc);
		
		OnlineRecord record=onlineRecordRepository.findLastOneByGroupDoctor(gdoc.getId());
		if(record==null) {
			throw new ServiceException(4006, "找不到对应上线记录");
		}
		
//		if(null!=record.getOffLineTime()) {
//			throw new ServiceException(4007, "已经下线");
//		}
		
		if (gdoc.getOnLineState() != null && gdoc.getOnLineState().equalsIgnoreCase("1") == false) {
			// 不等于1，表示下线。
			throw new ServiceException(4007, "已经下线");
		}

//		if (gdoc.getId().equalsIgnoreCase("55dd1c924203f32e5c3ac5c5")) {
//			System.out.println("groupId == 55dd1c924203f32e5c3ac5c5");
//		}
		
		Long currentDateTime=new Date().getTime();
		record.setLastModifyTime(currentDateTime);
		record.setOffLineTime(currentDateTime);
		
		Long duration=(currentDateTime-record.getOnLineTime())/1000;//时长
		record.setDuration(duration);
		onlineRecordRepository.update(record);
		
		//onlineRecordRepository
		gdoc.setOnLineState("2");
		
		if (ReqUtil.instance.getUserId() > 0) {
			gdoc.setUpdator(ReqUtil.instance.getUserId());
		}else{
			gdoc.setUpdator(new Integer(0));
		}
		//用户在下线时不再更新UpdatorDate字段，该字段仅在用户申请加入集团、审核通过时进行更新。（相关故事XGSF-6880）
		//gdoc.setUpdatorDate(currentDateTime);
		gdoc.setOffLineTime(currentDateTime);
		Long hasDutyDuration=gdoc.getDutyDuration();
		if(hasDutyDuration==null){
			hasDutyDuration=duration;
		}else{
			hasDutyDuration+=duration;
		}
		gdoc.setDutyDuration(hasDutyDuration);
		gdocDao.update(gdoc);

		if (event != null) {
//			System.out.println("execute ... businessMsgService event:"+event.getValue());
		}
		businessMsgService.userChangeNotify(UserChangeTypeEnum.DOCOTR_OFFLINE, event, gdoc.getDoctorId(), null);
		//jedisTemplate.del(KeyBuilder.buildOnLineKey(gdoc.getGroupId(),gdoc.getDoctorId()+""));

	}

	@Override
	public void setOutpatientPrice(String groupId, Integer outpatientPrice) {

		if (outpatientPrice == null) {
			throw new ServiceException(40009, "门诊价格为空！");
		}

		if (outpatientPrice.intValue() < 0) { // 可以等于0
			throw new ServiceException(40010, "门诊价格不能小于零");
		}
		
		gdocDao.updateOutpatientPrice(groupId, outpatientPrice);
		
		Group group=new Group();
		group.setId(groupId);
		group.setOutpatientPrice(outpatientPrice);
		groupDao.update(group);

	}

	@Override
	public GroupDoctor findOneByUserId(Integer userId) {
		GroupDoctor groupDoctor=gdocDao.findOneByUserId(userId,null);
		return groupDoctor;
	}
	
	@Override
	public GroupDoctor findOneByUserIdAndStatus(Integer userId,String status) {
		GroupDoctor groupDoctor=gdocDao.findOneByUserId(userId,status);
		return groupDoctor;
	}

	@Override
	public void autoResetDuration() {
		logger.info("重置已值班时长");
		//自动重置　当月已值班时长／／月初
		gdocDao.clearDuration();
		
	}

	@Override
	public Object listGroupDoctorGroupByDept(GroupDoctorParam param) {
		return gdocDao.listGroupDoctorGroupByDept(param);
	}

	@Override
	public List<GroupDoctorVO> getHasSetPriceGroupDoctorListByGroupId(String groupId) {
		return gdocDao.getHasSetPriceGroupDoctorListByGroupId(groupId);
	}

	@Override
	public boolean updateContactWay(GroupDoctorParam param) {
		return gdocDao.updateContactWay(param);
	}

	public DoctorInfoDetailsVO getDoctorInfoDetails(String groupId, int doctorId) {
		
		if(doctorId <= 0) {
    		throw new ServiceException(5, "需要参数doctorId大于零");
    	}
    	
    	// 获取doctorId的信息
    	User user = userManager.getUser(doctorId);
    	
		if (user.getDoctor() != null && user.getDoctor().getExpertise() != null
				&& !user.getDoctor().getExpertise().isEmpty()) {
			user.getDoctor().setSkill(groupSearchDao.getDisease(user.getDoctor().getExpertise())
					+ "; " + (user.getDoctor().getSkill() == null ? "" : user.getDoctor().getSkill()));
		}
		
		// 获取集团里医生信息
		GroupDoctor groupDoctor = getOne(groupId, doctorId);
        
		// 获取集团对象
		Group group = groupService.getGroupById(groupId);
		
		// 获取谁邀请我的人信息
		InviteRelation invite = gdocDao.getInviteInfo(doctorId, groupId);
		
//		System.out.println("SSSSdd1111000");
		
		// 从DepartmentDoctor表里获取DepartmentId
		List<DepartmentDoctor> departmentDoctorList = departmentDoctorService.getDepartmentDoctorByGroupIdAndDoctorId(groupId, doctorId);
		
//		System.out.println("SSSSdd1111");
		if (departmentDoctorList != null) {
//			System.out.println("departmentDoctorList size:"+departmentDoctorList.size());
		}else{
//			System.out.println("departmentDoctorList == null");
		}
		
		// 获取departmentFullName
		String departmentId = null;
		if (departmentDoctorList != null && departmentDoctorList.isEmpty() == false) {
			DepartmentDoctor a = departmentDoctorList.get(0);
			departmentId = a.getDepartmentId();
		}
		
		String departmentFullName = null;
		if (departmentId != null && departmentId.length() > 0) {
			List<DepartmentVO> departmentList = deparDao.findListById(groupId, null);
//			List<DepartmentVO> departmentList = departmentService.findAllListById(groupId);
			if (departmentList != null) {
				System.out.println("departmentList size:"+departmentList.size());
			}
			departmentFullName = GroupUtil.findFullNameById(departmentList, departmentId);
		}
//		System.out.println("departmentFullName:"+departmentFullName);
		
		// set vo
		DoctorInfoDetailsVO doctorInfoDetailsVO = new DoctorInfoDetailsVO();
		doctorInfoDetailsVO.user = user;
		doctorInfoDetailsVO.group = group;
		doctorInfoDetailsVO.groupDoctor = groupDoctor;
		doctorInfoDetailsVO.inviteRelation = invite;
		if (departmentDoctorList != null && departmentDoctorList.isEmpty() == false) {
			doctorInfoDetailsVO.departmentDoctor = departmentDoctorList.get(0);
		}
		doctorInfoDetailsVO.departmentFullName = departmentFullName;

		doctorInfoDetailsVO.groupProfit = groupProfitDao.getGroupProfitById(doctorId,groupId);
		
		return doctorInfoDetailsVO;
	}

	/** (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#saveByDoctorApply(com.dachen.health.group.group.entity.po.GroupDoctor)
	 */
	public Map<String, Object> saveByDoctorApply(String groupId,String applyMsg,Integer doctorId) throws HttpApiException {
		Map<String, Object> result=new HashMap<String, Object>();
		//参数校验
		if(groupId == null || StringUtils.isEmpty(groupId)){
			throw new ServiceException("医生圈子Id不能为空");
		}
		Group group = groupDao.getById(groupId, null);
		if (group == null) {
			throw new ServiceException("医生圈子不存在");
		}
		if(doctorId ==null || doctorId==0){
			throw new ServiceException("申请人不存在");
		}
		
    	//已加入集团的医生不允许再次申请加入
    	List<GroupDoctor> joinList = gdocDao.findGroupDoctor(doctorId,groupId,GroupDoctorStatus.正在使用.getIndex());
    	if(joinList.size() >0){
    		throw new ServiceException("当前用户已加入该医生圈子，不允许再次申请");
    	}
    	List<GroupDoctor> applyList = gdocDao.findGroupDoctor(doctorId,groupId,GroupDoctorStatus.申请待确认.getIndex());
    	if(applyList.size() >0){
    		throw new ServiceException("当前用户已申请过加入该医生圈子，不允许再次申请");
    	}
    	//更新/新增  的信息id
    	String groupDocorId="";
    	List<GroupDoctor> otherList = gdocDao.findGroupDoctor(doctorId,groupId,null);
    	if (otherList != null && otherList.size() > 0) {
    		//更新
			GroupDoctor updateGdoc = otherList.get(0);
			updateGdoc.setApplyMsg(applyMsg);
			updateGdoc.setUpdator(doctorId);
			updateGdoc.setUpdatorDate(new Date().getTime());
			updateGdoc.setStatus(GroupDoctorStatus.申请待确认.getIndex());
			updateGdoc.setReferenceId(0);
			updateGdoc.setTreePath("/" + doctorId + "/");
			
			updateGdoc.setType(group.getType());
			gdocDao.update(updateGdoc);
			groupDocorId = updateGdoc.getId();
    	}else{
    		//新增
    		GroupDoctor addGdoc = new GroupDoctor();
    		addGdoc.setGroupId(groupId);
    		addGdoc.setApplyMsg(applyMsg);
    		addGdoc.setDoctorId(doctorId);
    		addGdoc.setCreator(doctorId);
    		addGdoc.setCreatorDate(new Date().getTime());
    		addGdoc.setUpdator(doctorId);
    		addGdoc.setUpdatorDate(new Date().getTime());
    		addGdoc.setStatus(GroupDoctorStatus.申请待确认.getIndex());
    		addGdoc.setReferenceId(0);
    		addGdoc.setTreePath("/" + doctorId + "/");
    		addGdoc.setType(group.getType());
    		GroupDoctor newGdoc = gdocDao.save(addGdoc);
    		if(newGdoc != null){
    			groupDocorId = newGdoc.getId();
    		}
    		
    	}
        //TODO  申请成功 发送审核通知
    	sendNotifyForApplyJoin(doctorId,group.getName(),groupDocorId,groupId);
		
		return result;
	}
	
	/**
	 * 给集团所有管理员发送  有人申请加入集团 通知
	 * @param doctorId 申请人id
	 * @param groupName 集团名称
	 * @param groupDoctorId 申请id
	 * @param groupId 集团id
	 *@author wangqiao
	 *@date 2016年1月19日
	 */
	public void sendNotifyForApplyJoin(Integer doctorId,String groupName,String groupDoctorId,String groupId) throws HttpApiException {
		
		//初始化通知的内容
		User user = userManager.getUser(doctorId);
		String doctorName = user.getName();
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
    	ImgTextMsg imgTextMsg = new ImgTextMsg();
    	imgTextMsg.setStyle(6);
    	
    	imgTextMsg.setTime(System.currentTimeMillis());
    	//图标
    	imgTextMsg.setPic(GroupUtil.getApproveMemberImage());

    	Group group = groupDao.getById(groupId);

    	if (Objects.nonNull(group) && StringUtils.equals(group.getType(), GroupType.hospital.getIndex())) {
            imgTextMsg.setTitle(UserChangeTypeEnum.APPLY_JOIN_HOSPITAL.getAlias());
        } else if (Objects.nonNull(group) && StringUtils.equals(group.getType(), GroupType.group.getIndex())) {
            imgTextMsg.setTitle(UserChangeTypeEnum.APPLY_JOIN_GROUP.getAlias());
        } else if (Objects.nonNull(group) && StringUtils.equals(group.getType(), GroupType.dept.getIndex())) {
            imgTextMsg.setTitle(UserChangeTypeEnum.APPLY_JOIN_DEPT.getAlias());
        }
    	
    	if (StringUtils.isNotEmpty(doctorName)) {
    		imgTextMsg.setContent(doctorName+"申请加入"+groupName);			
		} else {
			imgTextMsg.setContent(user.getTelephone()+"申请加入"+groupName);						
		}
    	
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("bizType", 19);//申请加入集团通知
    	param.put("bizId", groupDoctorId);
    	imgTextMsg.setParam(param);
    	mpt.add(imgTextMsg);
    	
    	//读取集团的管理员作为 通知接收人
    	StringBuffer sendUserIds= new StringBuffer("");
    	
        //查询集团所有管理员列表
        List<GroupUser> groupUserList = companyUserService.getGroupUserByGroupId(groupId);
//    	List<GroupUser> groupUserList =  cuserDao.getQueryByGroupIds(groupId).asList();
    	if(groupUserList != null && groupUserList.size()>0){
    		//迭代 将所有管理员的doctorId用|连接成字符串
    		for(GroupUser groupUser : groupUserList){
    			if(groupUser != null && groupUser.getDoctorId() != null){
    				
    				if(sendUserIds.length() ==0){
    					sendUserIds.append(groupUser.getDoctorId().toString());
    				}else{
    					sendUserIds.append("|"+groupUser.getDoctorId().toString());
    				}
    				
    			}
    		}
    		
    	}
    	
    	
    	businessMsgService.sendTextMsg(sendUserIds.toString()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
    	businessMsgService.sendTextMsg(sendUserIds.toString()+"", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
	}

    @Override
    public List<Integer> getDoctorByStatus(String groupId, String[] statuses) {
        return gdocDao.getDoctorByStatus(groupId, statuses);
    }

    @Override
    public List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId) {
        return gdocDao.getNotInCurrentPackDoctorIds(groupId,consultationPackId);
    }

    @Override
    public GroupDoctor getById(GroupDoctor gdoc) {
        return gdocDao.getById(gdoc);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupDoctorService#getByDoctorApply(com.dachen.health.group.group.entity.po.GroupDoctor)
     */
	public List<GroupDoctorApplyVO> getDoctorApplyByGroupId(GroupDoctorApplyParam param){
		//参数校验
		if(param == null || StringUtils.isEmpty(param.getGroupId())){
			throw new ServiceException("医生集团Id不能为空");
		}
		
		return gdocDao.getDoctorApplyByGroupId(param,GroupDoctorStatus.申请待确认.getIndex());
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#getDoctorApplyByApplyId(java.lang.String)
	 */
	@Override 
	public GroupDoctorApplyVO getDoctorApplyByApplyId(String id){
		//参数校验
		if(id == null || StringUtils.isEmpty(id)){
			throw new ServiceException("Id不能为空");
		}
		GroupDoctor gdoc = gdocDao.getById(id);
		GroupDoctorApplyVO vo = new GroupDoctorApplyVO();
		if(gdoc == null){
			vo.setApplyStatus("Z");//申请已过期
		}else{
			//设置申请信息
			vo.setId(gdoc.getId());
			vo.setApplyDate(gdoc.getUpdatorDate());
			vo.setApplyMsg(gdoc.getApplyMsg());
			vo.setDoctorId(gdoc.getDoctorId());
			vo.setGroupId(gdoc.getGroupId());

			//设置状态
			if(GroupDoctorStatus.正在使用.getIndex().equals(gdoc.getStatus())){
				vo.setApplyStatus("C");
			}else if(GroupDoctorStatus.申请待确认.getIndex().equals(gdoc.getStatus())){
				vo.setApplyStatus("J");
			}else if(GroupDoctorStatus.申请拒绝.getIndex().equals(gdoc.getStatus())){
				vo.setApplyStatus("M");
			}else{
				vo.setApplyStatus("Z");
			}
			
			//查询医生信息
			BaseUserVO userVO = baseUserService.getUser(gdoc.getDoctorId());
			if(userVO != null){
				vo.setHeadPicFileName(userVO.getHeadPicFileName());
				vo.setTitle(userVO.getTitle());
				vo.setDepartments(userVO.getDepartments());
				vo.setHospital(userVO.getHospital());
				vo.setName(userVO.getName());
				vo.setStatus(userVO.getStatus());
				vo.setCheckRemark(userVO.getCheckRemark() == null ? "" : userVO.getCheckRemark());
			}
		}
		
		return vo;
	}
	
	@Override
	public OutpatientVO getOutpatientInfo(Integer doctorId) {
		OutpatientVO outpatient = new OutpatientVO();
		outpatient.setImage(PropertiesUtil.getHeaderPrefix() + "/"+ "default/"+PropertiesUtil.getContextProperty("outpatient"));
		List<GroupDoctor> ggdocs = gdocDao.getByDoctorId(doctorId,GroupType.group.getIndex());
        List<GroupDoctor> gdocs=new ArrayList<>();
        //处理屏蔽的集团和为审核通过的集团
        for(GroupDoctor groupDoctor:ggdocs){
            boolean flag=groupService.isNormalGroup(groupDoctor.getGroupId());
            if(flag){
                gdocs.add(groupDoctor);
            }
        }

		List<GroupDoctor> hospitalDocs = gdocDao.getByDoctorId(doctorId, GroupType.hospital.getIndex());
		if (gdocs == null) {
			gdocs = Lists.newArrayList();
		}
		gdocs.addAll(hospitalDocs);
        if (gdocs.isEmpty()) {//返回平台信息
        	PlatformDoctor pdoc = platformDoctorService.getOne(doctorId);
        	Integer price = groupService.getPlatformInfo().getOutpatientPrice();
 			outpatient.setPrice(price == null ? 0 : price.longValue());
            //
			outpatient.setOnLineState(pdoc == null ? OnLineState.offLine.getIndex()
					: pdoc.getOnLineState() == null ? OnLineState.offLine.getIndex() : pdoc.getOnLineState());
 			outpatient.setIsFree(outpatient.getPrice() == null || outpatient.getPrice() == 0);
 			outpatient.setDutyDuration(pdoc == null ? 0 : pdoc.getDutyDuration() == null ? 0 : pdoc.getDutyDuration());
 			outpatient.setTaskDuration(0L);
 			outpatient.setGroupId(pdoc == null ? GroupUtil.PLATFORM_ID : pdoc.getGroupId());
        } else {
        	GroupDoctor gdoc = getOnlineGroupDoctor(gdocs);
        	outpatient.setPrice(gdoc.getOutpatientPrice() == null ? 0 : gdoc.getOutpatientPrice().longValue());
        	outpatient.setOnLineState(gdoc.getOnLineState() == null ? OnLineState.offLine.getIndex() : gdoc.getOnLineState());
        	outpatient.setIsFree(!BusinessUtil.getDoctorIsFee(gdoc.getDutyDuration(), gdoc.getTaskDuration(), gdoc.getOutpatientPrice()));
        	outpatient.setDutyDuration(gdoc.getDutyDuration());
        	outpatient.setTaskDuration(gdoc.getTaskDuration());
        	outpatient.setGroupId(gdoc.getGroupId());
        }
    	return outpatient;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#initMainGroup(java.lang.Integer)
	 */
	@Override
	public void initMainGroup(Integer doctorId){
		//参数校验
		if(doctorId ==null || doctorId ==0){
			throw new ServiceException("医生id不能为空！");
		}
		
		//查询该医生是否有主集团
		List<GroupDoctor> mainGroupList = gdocDao.findMainGroupByDoctorId(doctorId);
		
		if(mainGroupList != null && mainGroupList.size() >0){
			// 过滤屏蔽的集团 add by tanyf 20160606
			List<String> mainGroupIds = Lists.transform(mainGroupList, new Function<GroupDoctor, String>() {
				@Override
				public String apply(GroupDoctor input) {
					return input.getGroupId();
				}
			});
			GroupsParam param = new GroupsParam();
			param.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
			param.setGroupIds(mainGroupIds);
			//只查询集团，不查询医院
			param.setType("group");
			List<Group> groupList = groupDao.getGroupList(param);
			if(!CollectionUtils.isEmpty(groupList)){
				//已有主集团了
				return;
			}
		}
		
		//查询最早加入的集团
//		GroupDoctor firstJoinGroupDoc = gdocDao.findFirstJoinGroupByDoctorId(doctorId);
		String firstJoinGroupDoc = gdocDao.findFirstJoinGroupIdByDoctorId(doctorId);
		if(firstJoinGroupDoc == null){
			//没有任何集团了
			return;
		}
		
		//设置最早加入的集团为主集团
		gdocDao.updateMainGroup(doctorId,firstJoinGroupDoc);
		
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#findGroupDoctor(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public List<GroupDoctor> findGroupDoctor (Integer doctorId,String groupId,String status){
		return gdocDao.findGroupDoctor(doctorId,groupId,status);
	}
	
	private GroupDoctor getOnlineGroupDoctor(List<GroupDoctor> gdocs) {
		if (gdocs == null || gdocs.isEmpty()) {
			throw new ServiceException("参数有误！");
		}
		GroupDoctor result = null;
		for (GroupDoctor gdoc : gdocs) {
			if (OnLineState.onLine.getIndex().equals(gdoc.getOnLineState())) {
				return gdoc;
			}
			result = gdoc;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#getGroupListByDoctorId(java.lang.Integer)
	 */
	@Override
	public List<String> getGroupListByDoctorId(Integer doctorId) {
		List<String> gList = new ArrayList<String>();
		List<GroupDoctor> gdocs = gdocDao.getByDoctorId(doctorId,GroupType.group.getIndex());
		
		//获取全部屏蔽的集团的id，若集团id在全部屏蔽的集团id内，则屏蔽掉该集团id
		List<String> skipGroupIds = groupDao.getSkipGroupIds();
		for (GroupDoctor gdoc : gdocs) {
			if (skipGroupIds != null && skipGroupIds.size() > 0) {
				if (!skipGroupIds.contains(gdoc.getGroupId())) {					
					gList.add(gdoc.getGroupId());
				}
			} else {				
				gList.add(gdoc.getGroupId());
			}
		}
		return gList;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#getByIds(java.lang.String, java.util.List)
	 */
	@Override
	public List<GroupDoctorVO> getByIds(String groupId, List<Integer> doctorIds){
		return gdocDao.getByIds(groupId, doctorIds);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#getGroupHospitalDoctorByDoctorId(java.lang.Integer)
	 */
	@Override
	public GroupHospitalDoctorVO getGroupHospitalDoctorByDoctorId(Integer doctorId){
		//参数校验
		if(doctorId == null || doctorId ==0){
			throw new ServiceException("医生Id不能为空");
		}
		
		GroupHospitalDoctorVO retVO = new GroupHospitalDoctorVO();
		retVO.setDoctorId(doctorId);
		
		//读取医生相关的医院信息
		GroupDoctor groupDoctor = gdocDao.getGroupHospitalDoctorByDoctorId(doctorId);
		if(groupDoctor == null){
			return null;
//			throw new ServiceException("找不到医生相关的医院信息");
		}
		retVO.setGroupHospitalId(groupDoctor.getGroupId());
		retVO.setId(groupDoctor.getId());
		Group group = groupService.getGroupById(groupDoctor.getGroupId());
		if(group != null){
			retVO.setHospitalId(group.getHospitalId());
			retVO.setHospitalName(group.getName());
			if(group.getConfig() != null){
				retVO.setMemberInvite(group.getConfig().isMemberInvite());
			}
			
		}
		
		//读取医生相关医院的部门信息
		List<DepartmentDoctor> departmentDoctorList =  departmentDoctorService.getDepartmentDoctorByGroupIdAndDoctorId(groupDoctor.getGroupId(),doctorId);
		if(departmentDoctorList != null && departmentDoctorList.size() > 0){
			DepartmentDoctor departmentDoctor = departmentDoctorList.get(0);
			if(departmentDoctor != null ){
				Department department = departmentService.getDepartmentById(departmentDoctor.getDepartmentId());
				retVO.setDepartmentId(departmentDoctor.getDepartmentId());
				if(department != null){
					retVO.setDepartmentName(department.getName());
				}
			}
		}
		//读取医生职称信息
		User user = userManager.getUser(doctorId);
		if(user != null && user.getDoctor() != null){
			retVO.setTitle(user.getDoctor().getTitle());
		}

		
		return retVO;
	}
	
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#getGroupHospitalDoctorByTelephone(java.lang.String)
	 */
	@Override
	public GroupHospitalDoctorVO getGroupHospitalDoctorByTelephone(String telephone,String inviteId){
		//参数校验
		if(StringUtils.isEmpty(telephone)  && StringUtils.isEmpty(inviteId)){
			throw new ServiceException("手机号和邀请id不能都为空");
		}
		GroupHospitalDoctorVO retVO = new GroupHospitalDoctorVO();
		
		//当前医生id
		Integer doctorId=0;
		//当前医生名称
		String doctorName = null;
		String retTelephone = null;
		
		//优先根据邀请id 查询 查询当前医生id
		if(!StringUtils.isEmpty(inviteId)){
			GroupDoctor groupDoctor = gdocDao.getById(inviteId);
			if(groupDoctor == null){
				throw new ServiceException("邀请信息已过期");
			}else{
				//设置邀请加入的医院id
				retVO.setInviteGroupHospitalId(groupDoctor.getGroupId());
				User user = userManager.getUser(groupDoctor.getDoctorId());
				if(user != null){
					retTelephone = user.getTelephone();
					doctorName = user.getName();
				}
				
				if( GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor.getStatus())){
					throw new ServiceException("您已加入该医院");
				}else if(GroupDoctorStatus.邀请待确认.getIndex().equals(groupDoctor.getStatus())){
					doctorId = groupDoctor.getDoctorId();
				}else if(GroupDoctorStatus.邀请拒绝.getIndex().equals(groupDoctor.getStatus())){
					throw new ServiceException("您已拒绝加入该医院");
				}else{
					throw new ServiceException("邀请信息已过期");
				}
			}
		}else{
			//根据手机号查询 当前医生id
			BaseUserVO user = baseUserService.getUserByTelephoneAndType(UserEnum.UserType.doctor.getIndex(), telephone);
			if(user == null ){
				//找不到用户，表示该手机号未注册
				retVO.setHospitalStatus("unregistered");
				return retVO;
			}else{
				doctorId = user.getUserId();
				doctorName = user.getName();
				retTelephone = user.getTelephone();
			}
			
		}

		retVO.setDoctorId(doctorId);
		retVO.setTelephone(retTelephone);
		
		//判断用户是否有名称
		if(StringUtils.isEmpty(doctorName)){
			retVO.setHospitalStatus("noUserName");
			return retVO;
		}
		
		//已注册用户，查询是否加入了医院
		GroupDoctor groupDoctor = gdocDao.getGroupHospitalDoctorByDoctorId(doctorId);
		if(groupDoctor == null){
			retVO.setHospitalStatus("register");
			return retVO;
		}
		retVO.setGroupHospitalId(groupDoctor.getGroupId());
		Group group = groupService.getGroupById(groupDoctor.getGroupId());
		if(group != null){
//			retVO.setHospitalId(group.getHospitalId());
			retVO.setHospitalName(group.getName());
		}
		retVO.setHospitalStatus("hospitalDoctor");
		
		//查询当前医生 是否是医院的管理员
//		List<GroupUser>  groupUserList= cuserDao.getGroupUserListByIdAndStatus(doctorId,
//				GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex(),groupDoctor.getGroupId());
		List<GroupUser>  groupUserList= companyUserService.getGroupUserListByIdAndStatus(null, doctorId, groupDoctor.getGroupId(), 
				GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex());
		
		if(groupUserList != null && groupUserList.size() >0 && groupUserList.get(0) != null){
			GroupUser groupUser = groupUserList.get(0);
			if(GroupRootAdmin.root.getIndex().equals(groupUser.getRootAdmin())){
				//是医院的超级管理员
				retVO.setHospitalStatus("root");
				return retVO;
			}
			//查询医院是否还有其他管理员
//			List<GroupUser>  otherGroupUserList= cuserDao.getGroupUserListByIdAndStatus(null,
//					GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex(),groupDoctor.getGroupId());
			List<GroupUser>  otherGroupUserList= companyUserService.getGroupUserListByIdAndStatus(null,null,groupDoctor.getGroupId(),
					GroupUserType.集团用户.getIndex(),GroupUserStatus.正常使用.getIndex());
			
			if(otherGroupUserList != null && otherGroupUserList.size() == 1){
				//是医院唯一的管理员
				retVO.setHospitalStatus("onlyManage");
				return retVO;
			}
		}
		
		return retVO;
		
	}

	@Override
	public void activeGroupByMemberNum(String groupId) throws HttpApiException {
		List<GroupDoctor> gds = gdocDao.findDoctorsByGroupId(groupId);
		int count = 0;
		for (GroupDoctor gd : gds) {
			try{
				User u = userManager.getUser(gd.getDoctorId());
				if(u.getStatus() != null && UserStatus.normal.getIndex() == u.getStatus().intValue()){
					count++;
				}
			}catch(Exception e){
			}
		}
		if(count > 2){
			groupService.activeGroup(groupId);// 激活集团并判断 是否同步到es中 add by tanyf 20160621
			/**
			 * 发送激活通知
			 */
//			GroupUser gu = gdocDao.findGroupRootAdmin(groupId);
//			if(gu != null){
//				int doctorId = gu.getDoctorId();
//				User user = userManager.getUser(doctorId);
//				List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
//				ImgTextMsg textMsg = new ImgTextMsg();
//				textMsg.setStyle(7);
//				textMsg.setTitle("医生集团已激活");
//				textMsg.setTime(System.currentTimeMillis());
//				textMsg.setContent("尊敬的"+user.getName()+"医生，您的医生集团已激活， 欢迎登录玄关健康平台使用更多医生集团专属功能");
//				Map<String, Object> param = new HashMap<String, Object>();
//				textMsg.setParam(param);
//				mpt.add(textMsg);
//				businessMsgService.sendTextMsg(doctorId+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
//				//mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您的医生集团已激活， 欢迎登录玄关健康平台使用更多医生集团专属功能");
//				final String msg = baseDataService.toContent("1021", user.getName());
//				mobSmsSdk.send(user.getTelephone(), msg);
//			}else{
//				logger.error("集团id= "+groupId +"的集团 ，找不到超级管理员");
//			}
		}
	}

	@Override
	public void leaveHospitalRoot(String id,Integer userId) {
		GroupDoctor updateGroupDoc = new GroupDoctor();
		updateGroupDoc.setUpdator(userId);
		updateGroupDoc.setUpdatorDate(System.currentTimeMillis());
		updateGroupDoc.setStatus(GroupEnum.GroupDoctorStatus.离职.getIndex());
		updateGroupDoc.setId(id);
		gdocDao.update(updateGroupDoc);
	}
	
	public GroupDoctor getGroupDoctor(Integer doctorId,String groupId){
		//参数校验
		if(doctorId == null || doctorId == 0){
			throw new ServiceException("doctorId 为空");
		}
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("groupId 为空");
		}
		//根据医生id，和集团id 查询关系记录
		return gdocDao.getByDoctorIdAndGroupId(doctorId, groupId);
		
	}
	
	/**
	 * 获取 邀请短链
	 * @author wangqiao 重构
	 * @date 2016年4月25日
	 * @param type
	 * @param id
	 * @param group
	 * @param doctorId
	 * @return
	 */
	private String getInviteUrl(Integer type, String id , Group group, Integer doctorId) throws HttpApiException {
		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");
		
		if(GroupType.hospital.getIndex().equals(group.getType())){
			url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalJoin");
		}
		
//		DoctorBasicInfo doctor = this.getDoctorBasicInfoById(doctorId, 3);
		User doctor = userManager.getUser(doctorId) ;
		if(doctor == null){
			throw new ServiceException("根据doctorId，找不到对应的医生信息");
		}
		
		
		url = url + "?id=" + id + "&doctorName=" + (doctor.getName() == null ? "" : doctor.getName());

		if(2 == type) {

			url = url + "&type=2" + "&name=" + group.getName();
		}
		if(3 == type) {

			url = url + "&type=3" + "&name=" + group.getName();
		}
		
		return shortUrlComponent.generateShortUrl(url);
	}
	
	/**
	 * 获取邀请短链
	 * @author wangqiao 重构
	 * @date 2016年4月26日
	 * @param type
	 * @param id
	 * @param group
	 * @param doctorId
	 * @return
	 */
	private String getInviteTpl(Integer type, String id, Group group, Integer doctorId) throws HttpApiException {
		String tpl = "";
		//String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle");
        /**修改成从应用宝获取应用**/
//        String url = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
		String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle");
		if(GroupType.hospital.getIndex().equals(group.getType())){
			url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalJoin");
		}
		
//		DoctorBasicInfo doctor = this.getDoctorBasicInfoById(doctorId, 3);
		User doctor = userManager.getUser(doctorId) ;
		if(doctor == null){
			throw new ServiceException("根据doctorId，找不到对应的医生信息");
		}
		
		url = url + "?id=" + id + "&doctorName=" + (doctor.getName() == null ? "" : doctor.getName());
		
		final String doctorName = doctor.getName() == null ? "" : doctor.getName();
		String unitName = "";
		String opName = "";
		String doc = BaseConstants.XG_YSQ_APP;

		if (2 == type) {
			url = url + "&type=2" + "&name=" + group.getName();
			unitName = group.getName();
			opName = "成为管理员";
		}
		if (3 == type) {
			url = url + "&type=3" + "&name=" + group.getName();
			unitName = group.getName();
            opName = "加入";
		}
        tpl = baseDataService.toContent("0002", doctorName, unitName, opName, shortUrlComponent.generateShortUrl(url), doc);
		return tpl;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupDoctorService#repairTypeForHospitalGroup()
	 */
	@Override
	public int repairTypeForHospitalGroup(){
		int errorCount = 0;
		
		//查询所有的type=hospital的 group
		List<Group> groupHospitalList = groupDao.getGroupListByType(GroupType.hospital.getIndex());
		
		//迭代获取 groupId List
		List<String> groupHospitalIdList = new ArrayList<String>();
		if(groupHospitalList != null && groupHospitalList.size() > 0){
			for(Group groupHospital : groupHospitalList){
				if(groupHospital == null || groupHospital.getId() == null){
					continue;
				}
				groupHospitalIdList.add(groupHospital.getId());
			}
		}
		//读取每个group相关的 c_group_doctor记录中 type为"group"的记录数量
		errorCount = gdocDao.getGroupDoctorCountByGroupIdsAndType(groupHospitalIdList,GroupType.group.getIndex());
		
		//将错误的type=group的医院医生记录，更新为 type=hospital
		gdocDao.updateGroupDoctorTypeByGroupIdsAndType(groupHospitalIdList, GroupType.group.getIndex(), GroupType.hospital.getIndex());
		
		return errorCount;
	}

	@Override
	public List<GroupVO> getMyGroups(Integer doctorId) {
    	if (doctorId == null) {
    		throw new IllegalArgumentException("doctorId is null");
    	}
    	List<GroupDoctor> gdocs = gdocDao.getByDoctorId(doctorId,GroupType.group.getIndex());
    	List<GroupVO> list = new ArrayList<GroupVO>();
    	for (GroupDoctor gdoc : gdocs) {
    		Group group = groupDao.getById(gdoc.getGroupId());
    		if(group==null){
    			continue;
    		}
    		
    		GroupVO vo = toVO(group, gdoc);
    		//判断一下group的状态，若状态为正常则正常处理，屏蔽状态的集团则不予显示（2016-6-4傅永德）
			vo.setSkip(group.getSkip());
			
    		//当用户是管理员时，查询申请加入集团的人数，便于审核
    		if (vo.getIsAdmin()) {
    			vo.setApplyCount((long)gdocDao.findGroupDoctor(null, group.getId(), "J").size());
    		}
    		list.add(vo);
    		
    	}
    	return list;
    }

	@Override
	public void activeAllUserGroup(Integer doctorId) throws HttpApiException {
		List<String> groupIds = gdocDao.findAllGroupIdByDoctorId(doctorId);
		if(groupIds.size() > 0){
			for (String groupId : groupIds) {
				Group g = groupDao.getById(groupId);
				if(g != null 
						&& !GroupActive.active.getIndex().equals(g.getActive())
						&& GroupType.group.getIndex().equals(g.getType())){
					activeGroupByMemberNum(groupId);
				}
			}	
		}
	}

	@Override
	public List<Integer> getDocIdsByGroupID(String groupId,String... status) {
		return gdocDao.getDoctorByStatus(groupId, status);
	}
	
	
	/**
	 *  集团下成员数
	 * @param groupId 集团ID
	 * @return
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	@Override
	public long findDoctorsCountByGroupId(String groupId) {
		return gdocDao.findDoctorsCountByGroupId(groupId);
	}
	
	/**
	 *  集团下成员列表
	 * @return
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	@Override
	public PageVO findDoctorsListByGroupDoctorsParam(GroupDoctorsParam param) {
		DoctorCheckParam doctorCheckParam = new DoctorCheckParam();
		BeanUtils.copyProperties(param, doctorCheckParam);
		List<GroupDoctor> groupDoctorList = gdocDao.findDoctorsListByGroupId(param.getGroupId());
		List<Integer> doctorIdList = Lists.transform(groupDoctorList, new Function<GroupDoctor, Integer>() {
			@Override
			public Integer apply(GroupDoctor input) {
				return input.getDoctorId();
			}
		});
		doctorCheckParam.setDoctorIds(doctorIdList);
		return doctorCheckService.getDoctors(doctorCheckParam);
	}
	
	/**
	 * 更新集团医生
	 * @param groupDoctor
	 * @author tan.yf
	 * @date 2016年6月6日
	 */
	@Override
	public void updateGroupDoctor(GroupDoctor groupDoctor){
		gdocDao.updateGroupDoctor(groupDoctor);
	}

	@Override
	public boolean allowdeJoinGroup(Integer doctorId, String groupId) {
		//参数校验
		if(groupId == null || StringUtils.isEmpty(groupId)){
			return false;
		}
		Group group = groupDao.getById(groupId, null);
		if (group == null) {
			return false;
		}
		if(doctorId ==null || doctorId==0){
			return false;
		}
		//已加入集团的医生不允许再次申请加入
    	List<GroupDoctor> joinList = gdocDao.findGroupDoctor(doctorId,groupId,GroupDoctorStatus.正在使用.getIndex());
    	if(joinList.size() >0){
    		return false;
    	}
    	List<GroupDoctor> applyList = gdocDao.findGroupDoctor(doctorId,groupId,GroupDoctorStatus.申请待确认.getIndex());
    	if(applyList.size() >0){
    		return false;
    	}
		return true;
	}

	@Override
	public Map<String, Object> isInBdjl() {
		//从token中获取用的id
		Integer userId = ReqUtil.instance.getUserId();
		//获取博德嘉联的集团id
		ServiceCategoryVO serviceCategoryVO = serviceCategoryService.getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
		String groupId = serviceCategoryVO.getGroupId();
		List<GroupVO> groupVOs = getMyGroups(userId);
		int isInBdjl = 0;
		if (groupVOs != null && groupVOs.size() > 0) {
			for(GroupVO groupVO : groupVOs) {
				if (StringUtils.equals(groupId, groupVO.getId())) {
					//在博德嘉联集团内
					if (groupVO.getIsMain()) {
						isInBdjl = 1;
					} else {
						isInBdjl = 2;
					}
				}
			}
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("isInBdjl", isInBdjl);
		return result;
	}
	
	private boolean isBdjl(String groupId)
	{
		boolean isBdjl=false;
		//获取博德嘉联的集团id
		ServiceCategoryVO serviceCategoryVO = serviceCategoryService.getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
		String bdjlGroupId = serviceCategoryVO.getGroupId();
		if(groupId!=null&&bdjlGroupId!=null&&groupId.equals(bdjlGroupId))
		{
			isBdjl=true;
		}
		return isBdjl;
	}
	
	/**
	 * 获取医生集团
	 */
	public List<GroupVO> getMyNormalGroups(Integer doctorId) {
		if (doctorId == null) {
    		throw new IllegalArgumentException("doctorId is null");
    	}
		List<GroupDoctor> gdocs = gdocDao.getByDoctorId(doctorId,GroupType.hospital.getIndex());//医院
    	List<GroupDoctor> groupDocs = gdocDao.getByDoctorId(doctorId,GroupType.group.getIndex());// 集团
    	
    	if(!CollectionUtils.isEmpty(gdocs)){
    		if (!CollectionUtils.isEmpty(groupDocs)){
    			gdocs.addAll(groupDocs);
    		}
    	}else{
    		gdocs = Lists.newArrayList();
    		if (!CollectionUtils.isEmpty(groupDocs)){
    			gdocs.addAll(groupDocs);
    		}
    	}
    	List<GroupVO> list = new ArrayList<GroupVO>();
    	for (GroupDoctor gdoc : gdocs) {
    		String groupId = gdoc.getGroupId();
			Group group = groupDao.getById(groupId);
    		if(group==null || (!groupService.isNormalGroup(groupId))){
    			continue;
    		}
    		
    		GroupVO vo = toVO(group, gdoc);
    		//判断一下group的状态，若状态为正常则正常处理，屏蔽状态的集团则不予显示（2016-6-4傅永德）
			vo.setSkip(group.getSkip());
			
    		//当用户是管理员时，查询申请加入集团的人数，便于审核
    		if (vo.getIsAdmin()) {
    			vo.setApplyCount((long)gdocDao.findGroupDoctor(null, group.getId(), "J").size());
    		}
    		list.add(vo);
    		
    	}
    	return list;
    }

	@Override
	public PageVO getGroupAddrBook(String groupId, Integer areaCode, String deptId, Integer pageIndex, Integer pageSize) {
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException("集团id为空");
		}
		
		List<Integer> doctorIds = gdocDao.getDoctorIdByGroupId(groupId);
		
		return userRepository.getGroupAddrBook(doctorIds, areaCode, deptId, pageIndex, pageSize);
	}

	@Override
	public List<String> getGroupIsMain(Integer doctorId) {
		List<GroupDoctorVO> gdocs = gdocDao.getByDoctorId(doctorId, GroupType.group.getIndex(), GroupDoctorVO.class);
		List<String> listMain=new ArrayList<>();
    	for (GroupDoctorVO gdoc : gdocs) {
    		//判断是否屏蔽且是否为主集团
    		if(gdoc!=null && groupService.isNormalGroup(gdoc.getGroupId())){
    			if(gdoc.isMain()){
    				listMain.add(gdoc.getGroupId());
    			}
    		}
    	}
    	
		return listMain;
	}

    @Override
    public List<String> getGroup(Integer doctorId, String type) {
        List<GroupDoctorVO> gdocs = gdocDao.getByDoctorId(doctorId, type, GroupDoctorVO.class);
        List<String> listMain=new ArrayList<>();
        if (!CollectionUtils.isEmpty(gdocs)){
            listMain.add(gdocs.get(0).getGroupId());
        }
        return listMain;
    }

    @Override
	public List<String> getGroupListByDoctorIds(List<Integer> doctorIds) {

		List<String> gList = new ArrayList<String>();
		List<GroupDoctor> gdocs = gdocDao.getByDoctorIds(doctorIds,GroupType.group.getIndex());
		
		//获取全部屏蔽的集团的id，若集团id在全部屏蔽的集团id内，则屏蔽掉该集团id
		List<String> skipGroupIds = groupDao.getSkipGroupIds();
		for (GroupDoctor gdoc : gdocs) {
			if (skipGroupIds != null && skipGroupIds.size() > 0) {
				if (!skipGroupIds.contains(gdoc.getGroupId())) {					
					gList.add(gdoc.getGroupId());
				}
			} else {				
				gList.add(gdoc.getGroupId());
			}
		}
		return gList;
	
	}
	
	@Override
	public List<String> getActiveGroupIdListByDoctor(Integer doctorId) {
		if (null == doctorId) {
			return null;
		}
		// 不区分type
		List<String> groupIdList = this.gdocDao.getActiveGroupIdListByDoctor(doctorId);
		return groupIdList;
	}
	
	@Override
	public List<Group> getActiveGroupListByDoctor(Integer doctorId) {
		if (null == doctorId) {
			return null;
		}
		// 不区分type
		List<String> groupIdList = this.gdocDao.getActiveGroupIdListByDoctor(doctorId);
		if (CollectionUtils.isEmpty(groupIdList)) {
			return null;
		}
		
		List<Group> groupList = new ArrayList<Group>(groupIdList.size());
		for (String groupId:groupIdList) {
			Group group = this.groupService.getGroupById(groupId);
			if (null == group || group.getSkip() == GroupSkipStatus.skip.getIndex()) {
				continue;
			}
			groupList.add(group);
		}
		
		return groupList;
	}

    @Override
    public List<CGroup> getActiveCGroupListByDoctor(Integer doctorId) {
        if (null == doctorId) {
            return null;
        }
        // 不区分type
        List<GroupDoctor> list = this.gdocDao.getActiveListByDoctor(doctorId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<CGroup> groupList = new ArrayList<CGroup>(list.size());
        for (GroupDoctor groupDoctor:list) {
            Group group = this.groupService.getGroupById(groupDoctor.getGroupId());
            if (null == group || group.getSkip() == GroupSkipStatus.skip.getIndex()) {
                continue;
            }
            CGroup cGroup = BeanUtil.copy(group, CGroup.class);
            if (null != groupDoctor.isMain() && true == groupDoctor.isMain()) {
                cGroup.setIfMain(1);
            }
            groupList.add(cGroup);
        }

        return groupList;
    }

}
