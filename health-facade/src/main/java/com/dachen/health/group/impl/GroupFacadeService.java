package com.dachen.health.group.impl;

import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.*;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupUserDao;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.*;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupHospitalDoctorVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.group.schedule.dao.IOnlineDao;
import com.dachen.health.knowledge.service.IMedicalKnowledgeService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.teachCenter.dao.IArticleDao;
import com.dachen.health.teachCenter.dao.IDiseaseTreeDao;
import com.dachen.health.teachCenter.entity.po.GroupDisease;
import com.dachen.health.teachCenter.entity.vo.ArticleVO;
import com.dachen.health.teachCenter.service.IArticleService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.manager.ISMSManager;
import com.dachen.pub.model.PubTypeEnum;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class GroupFacadeService implements IGroupFacadeService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IPlatformDoctorService platformDoctorService;

    @Autowired
    private IGroupProfitService groupProfitService;

    @Autowired
    private IBusinessServiceMsg businessMsgService;

    @Autowired
    private ICompanyUserService companyUserService;

    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private IDepartmentDoctorService departmentDoctorService;

    @Autowired
    private IBaseDataService baseDataService;

    @Autowired
    private IPackService packService;

    @Autowired
    private IGroupDoctorDao gdocDao;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IMedicalKnowledgeService knowledgeService;

    @Autowired
    private IDepartmentDoctorDao ddDao;

    @Autowired
    private UserManager userManager;

    @Autowired
    IBusinessServiceMsg businessServiceMsg;

    @Autowired
    private IGroupDao groupDao;

    @Autowired
    private IOnlineDao onlineDao;

    @Autowired
    private IGroupDoctorService groupDoctorService;

    @Autowired
    private IArticleDao articleDao;

    @Autowired
    private IDiseaseTreeDao diseaseTreeDao;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Autowired
    private ConsultationPackDao consultationPackDao;

    @Autowired
    private IFeeService feeService;

    @Autowired
    private MobSmsSdk mobSmsSdk;

    @Autowired
    private PubGroupService pubGroupService;

    @Autowired
    PackMapper packMapper;

    @Autowired
    private ISMSManager smsManager;

    @Resource
    private IServiceCategoryService serviceCategoryService;

    @Autowired
    private IMsgService iMsgService;

    @Autowired
    private IGroupUserDao groupUserDao;

    @Autowired
    private UserRepository userRepository;
    
    /*
     * (non-Javadoc)
     *
     * @see
     * com.dachen.health.group.IGroupFacadeService#confirmByInvite(java.lang
     * .String, java.lang.Integer, java.lang.String)
     */
    @Override
    public String confirmByInvite(String id, Integer type, String status, Integer operationUserId) throws HttpApiException {
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("id为空");
        }
        if ((null == type) || (type == 0)) {
            throw new ServiceException("type为空");
        }
        if (StringUtil.isEmpty(status)) {
            throw new ServiceException("status为空");
        }
        if (type.intValue() == 3) {
            // 医生加入集团
            return confirmByJoinGroup(id, status);

        } else {
            // 加入 集团管理员/医生管理员
            return confirmByJoinManage(id, type, status, operationUserId);
        }
    }

    /**
     * 同意集团的邀请并设置为主集团
     *
     * @param groupId
     * @return
     */
    @Override
    @Transactional
    public void confirmByInviteAndSetMain(String groupId) throws HttpApiException {
        //1、根据token获取当前用户
        Integer userId = ReqUtil.instance.getUserId();

        //2、根据用户id和集团id，查询c_group_doctor表中的申请记录
        GroupDoctor groupDoctor = gdocDao.findOneByUserIdAndGroupId(userId, groupId, GroupDoctorStatus.邀请待确认.getIndex());
        if (groupDoctor == null) {
            throw new ServiceException("没有集团的邀请记录");
        }

        confirmByJoinGroup(groupDoctor.getId(), GroupDoctorStatus.正在使用.getIndex());

        gdocDao.updateMainGroup(userId, groupId);
    }


    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#confirmByJoinGroupHospital(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    public String confirmByJoinGroupHospital(String id, Integer type, String status, Integer operationUserId) throws HttpApiException {
        //参数校验
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("id为空");
        }
        if ((null == type) || (type == 0)) {
            throw new ServiceException("type为空");
        }
        if (StringUtil.isEmpty(status)) {
            throw new ServiceException("status为空");
        }
        if (type == 3) {
            //如果是确认加入，需要 先离开其它医院
            if (GroupDoctorStatus.正在使用.getIndex().equals(
                    status)) {
                GroupDoctor groupDoctor = gdocDao.getById(id);
                if (groupDoctor != null) {
                    doctorLeaveOtherHospital(groupDoctor.getDoctorId());
                }
            }

            // 再加入 新医院
            return confirmByJoinGroup(id, status);

        } else {
            // 加入 医院管理员
            return confirmByJoinManage(id, GroupUserType.集团用户.getIndex(), status, operationUserId);
        }
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#doctorLeaveOtherHospital(java.lang.Integer)
     */
    @Override
    public void doctorLeaveOtherHospital(Integer doctorId) throws HttpApiException {
        if ((doctorId == null) || (doctorId == 0)) {
            return;
        }
        //查询医生所在的医院
        GroupHospitalDoctorVO groupHospitalDoctor = groupDoctorService.getGroupHospitalDoctorByDoctorId(doctorId);
        if (groupHospitalDoctor != null) {
            //医生离职医院
            GroupDoctor leaveGroupDoctor = new GroupDoctor();
            leaveGroupDoctor.setId(groupHospitalDoctor.getId());
            leaveGroupDoctor.setDoctorId(groupHospitalDoctor.getDoctorId());
            leaveGroupDoctor.setGroupId(groupHospitalDoctor.getGroupHospitalId());
            leaveGroupDoctor.setStatus(GroupDoctorStatus.离职.getIndex());
            updateGroupDoctor(leaveGroupDoctor);
        }
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#confirmByRegisterJoinGroupHospital(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String confirmByRegisterJoinGroupHospital(Integer inviteDoctorId, String groupHospitalId,
                                                     String telephone, String password, String name, String deptId, String departments, String title) throws HttpApiException {
        //参数校验
        if (StringUtils.isEmpty(telephone)) {
            throw new ServiceException("手机号不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new ServiceException("密码不能为空");
        }
        if (StringUtils.isEmpty(name)) {
            throw new ServiceException("姓名不能为空");
        }

        //注册账号
        UserExample example = new UserExample();
        example.setTelephone(telephone);
        example.setPassword(password);
        example.setName(name);
        example.setUserType(UserType.doctor.getIndex());

        Group g = groupDao.getById(groupHospitalId);
        if (g == null) {
            throw new ServiceException("该医生集团数据异常！");
        }
        //查询加入的医院信息
        HospitalVO hospitalVO = baseDataDao.getHospital(g.getHospitalId());
        if (hospitalVO == null) {
            throw new ServiceException("该医生集团医院数据异常！");
        }

        Map<String, Object> retMap = userManager.registerIMUser(example);
        if ((retMap == null) || (retMap.get("user") == null)) {
            //注册账号失败
            throw new ServiceException("账号注册失败");
        }
        User user = (User) retMap.get("user");


        //更新用户的  科室，科室id，职称，医院，医院id 信息
        Doctor doctor = new Doctor();
        doctor.setDepartments(departments);
        doctor.setDeptId(deptId);
        doctor.setTitle(title);
        //更新医院，医院id信息

        doctor.setHospital(hospitalVO.getName());
        doctor.setHospitalId(g.getHospitalId());
        example.setDoctor(doctor);

        //设置医生的来源信息（2016-05-26傅永德）
        UserSource userSource = new UserSource();
        userSource.setGroupId(groupHospitalId);
        if (StringUtils.equals(g.getType(), "hospital")) {
            userSource.setSourceType(UserEnum.Source.hospital.getIndex());
        } else if (StringUtils.equals(g.getType(), "group")) {
            userSource.setSourceType(UserEnum.Source.group.getIndex());
        } else {
            userSource.setSourceType(UserEnum.Source.group.getIndex());
        }

        userSource.setInviterId(inviteDoctorId);
        example.setUserSource(userSource);


        userManager.updateUser(user.getUserId(), example);

        //将用户直接加入医院
        saveCompleteGroupDoctor(groupHospitalId, user.getUserId(), telephone, inviteDoctorId);

        return "恭喜您成为医院成员";
    }

    /**
     * 决定 是否 加入 医生集团
     *
     * @param id     groupdoctor表的id
     * @param status C，同意，N，拒绝
     * @return
     * @author wangqiao
     * @date 2016年1月7日
     */
    private String confirmByJoinGroup(String id, String status) throws HttpApiException {
        // 数据校验（申请是否还有效）
        GroupDoctor oldGDoc = gdocDao.getById(id);
//		String groupId = "";
//		String groupName = "";

        if (oldGDoc == null) {
            throw new ServiceException("邀请已过期");
        }

        Group group = groupService.getGroupById(oldGDoc.getGroupId());
        if (group == null) {
            throw new ServiceException("邀请的集团不存在");
        }

        // 判断已有数据的状态
        if (GroupDoctorStatus.正在使用.getIndex().equals(oldGDoc.getStatus())) {
            // 已经加入了集团
            throw new ServiceException("您已加入" + group.getName());
        } else if (GroupDoctorStatus.邀请拒绝.getIndex().equals(oldGDoc.getStatus())) {
            // 已经拒绝加入
            throw new ServiceException("您已拒绝");
        } else if (!GroupDoctorStatus.邀请待确认.getIndex().equals(oldGDoc.getStatus())) {
            // 处于其它 非邀请待确认 状态 统一认为邀请已过期
            throw new ServiceException("邀请已过期");
        }

        // 更新 医生集团关系数据
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setId(id);
        gdoc.setStatus(status);
        Integer currentUserId = ReqUtil.instance.getUserId();
        gdoc.setUpdator(currentUserId);
        gdoc.setUpdatorDate(new Date().getTime());
        updateGroupDoctor(gdoc);

        User user = userManager.getUser(gdoc.getDoctorId());
        if (user == null) {
            throw new ServiceException("根据医生id找不到医生信息");
        }

        //发送通知
        if ("N".equals(gdoc.getStatus())) {
            //发送通知给邀请人
            String content = user.getName() + "医生已拒绝您的邀请，未加入" + group.getName();
            joinGroupSendMsgToInviteDoctor(user, group, gdoc.getReferenceId(), content);

            return "您已成功拒绝";
        } else {

            //发送通知给邀请人
            String content = user.getName() + "医生已接受您的邀请，加入" + group.getName();
            joinGroupSendMsgToInviteDoctor(user, group, gdoc.getReferenceId(), content);
            //发送通知给管理员
            joinGroupSendMsgToManage(user, group, gdoc.getReferenceId());

            //加入集团会话组
            if (Objects.nonNull(group) ) {
                if (StringUtils.isBlank(group.getGid())) {
                    groupService.createGroupIm(group);
                }

                Group temp = groupDao.getById(group.getId());
                String gid = temp.getGid();

                GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
                Integer fromUserId = getFromUserId(group, groupUser);

                if (StringUtils.isNotBlank(gid)) {
                    Thread thread = new Thread(() -> joinGroupIM(gid, fromUserId, oldGDoc.getDoctorId(), user.getName(), group.getName()));
                    thread.start();
                }
            }

            return "恭喜您成为圈子成员";
        }
    }

    Integer getFromUserId(Group group, GroupUser groupUser) {
        Integer fromUserId = null;
        if (Objects.nonNull(groupUser)) {
            fromUserId = groupUser.getDoctorId();
        } else {
            fromUserId = group.getCreator();
        }
        return fromUserId;
    }

    /**
     * 决定是否成为 集团/公司 管理员
     *
     * @param id              groupuser表的id
     * @param type            1，公司管理员，2，集团管理员 ， 3，医院管理员
     * @param status          C，同意，N，拒绝
     * @param operationUserId 操作人id
     * @return
     * @author wangqiao
     * @date 2016年1月7日
     */
    private String confirmByJoinManage(String id, Integer type, String status, Integer operationUserId) {
//		GroupUser u = cuserDao.getGroupUserById(id);
        GroupUser groupUser = companyUserService.getGroupUserByIdAndStatus(id, null, null, null, null);
        if (groupUser == null) {
            // 找不到数据
            throw new ServiceException("邀请已过期");
        } else if (GroupUserStatus.正常使用.getIndex().equals(groupUser.getStatus())) {
            // 已同意
            throw new ServiceException("您已同意");
        } else if (GroupUserStatus.邀请拒绝.getIndex().equals(groupUser.getStatus())) {
            // 已拒绝
            throw new ServiceException("您已拒绝");
        } else if (!GroupUserStatus.邀请待通过.getIndex().equals(groupUser.getStatus())) {
            // 处于其它 非邀请待确认 状态 统一认为邀请已过期
            throw new ServiceException("邀请已过期");
        }

//		GroupUser cuser = new GroupUser();
//		cuser.setId(id);
//		cuser.setStatus(status);
//		cuser.setUpdator(ReqUtil.instance.getUserId());
//		cuser.setUpdatorDate(new Date().getTime());
//		cuserDao.update(cuser);

        // 更新管理员关系数据
        companyUserService.updateStatusById(id, status, operationUserId);

        if (GroupUserStatus.邀请拒绝.getIndex().equals(status)) {
            return "您已成功拒绝";
        }
        if (GroupUserType.集团用户.getIndex().intValue() == type.intValue()) {
            businessMsgService.addGroupNotify(groupUser.getDoctorId(), groupUser.getObjectId());
            return "恭喜您成为管理员";
        }
        if (GroupUserType.公司用户.getIndex().intValue() == type.intValue()) {
            return "恭喜您成为公司管理员";
        }
        return "恭喜您成为管理员";
    }

    /**
     * (non-Javadoc)
     *
     * @see
     * com.dachen.health.group.IGroupFacadeService#saveCompleteGroupDoctor(com.dachen.health.group.group.entity.po.GroupDoctor, java.lang.String, java.lang.Integer)
     */
    @Override
    public Map<String, Object> saveCompleteGroupDoctor(String groupId, Integer doctorId, String telephone, Integer inviteId)
        throws HttpApiException {
        Map<String, Object> data = Maps.newHashMap();
        GroupDoctor saveGroupDoctor = new GroupDoctor();
        // 参数校验
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }
        if (null == inviteId) {
            throw new ServiceException("邀请人Id为空");
        }
        if (null == doctorId) {
            if (StringUtil.isEmpty(telephone)) {
                throw new ServiceException("手机号码为空");
            } else {
                User doctor = userManager.getUser(telephone, UserType.doctor.getIndex());
                if ((null != doctor) && (null != doctor.getUserId())) {
                    doctorId = doctor.getUserId();
                } else {
                    throw new ServiceException("找不到医生信息");
                }
            }
        }

        // 是否已有该医生与集团的关系记录
        GroupDoctor already = gdocDao.getByDoctorIdAndGroupId(doctorId, groupId);

        User user = userManager.getUser(doctorId);
        Group group = groupService.getGroupById(groupId);

        if (already != null) {
            // 已加入该集团，则返回异常 已加入
            if (GroupDoctorStatus.正在使用.getIndex().equals(already.getStatus())) {
                Group gp = groupService.getGroupById(already.getGroupId());// 查找集团
                data.put("status", 0);
                if (gp != null) {
                    data.put("msg", "您已经加入了" + gp.getName());
                } else {
                    data.put("msg", "您已经加入了");
                }
                return data;
            } else {
                // 未加入该集团，则将状态改为已加入
                // 更新推荐人
                already = updateTreePath(already, already.getDoctorId(), inviteId, groupId);

                // 更新状态
                already.setStatus(GroupDoctorStatus.正在使用.getIndex());
                already.setUpdator(inviteId);
                already.setUpdatorDate(new Date().getTime());
                updateGroupDoctor(already);

                //发送通知给邀请人
                String content = user.getName() + "医生已接受您的邀请，加入" + group.getName();
                joinGroupSendMsgToInviteDoctor(user, group, inviteId, content);
                //发送通知给管理员
                joinGroupSendMsgToManage(user, group, inviteId);
            }

        } else {
            // 新加入该集团
            saveGroupDoctor(groupId, doctorId, inviteId, GroupDoctorStatus.正在使用.getIndex());

            //发送通知给邀请人
            String content = user.getName() + "医生已接受您的邀请，加入" + group.getName();
            joinGroupSendMsgToInviteDoctor(user, group, inviteId, content);
            //发送通知给管理员
            joinGroupSendMsgToManage(user, group, inviteId);
        }

        data.put("groupDoctor", saveGroupDoctor);
        data.put("status", 0);
        data.put("msg", "");
        return data;
    }

    /**
     * (non-Javadoc)
     *
     * @see
     * com.dachen.health.group.IGroupFacadeService#saveCompleteGroupDoctor(com.dachen.health.group.group.entity.po.GroupDoctor, java.lang.String, java.lang.Integer)
     */
    @Override
    public Map<String, Object> saveCompleteGroupDoctorByRegister(String groupId,
                                                                 Integer doctorId, String telephone, Integer inviteId) throws HttpApiException {
        Map<String, Object> data = new HashMap<String, Object>();
        GroupDoctor saveGroupDoctor = new GroupDoctor();
        // 参数校验
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }
        if (null == doctorId) {
            if (StringUtil.isEmpty(telephone)) {
                throw new ServiceException("手机号码为空");
            } else {
                User doctor = userManager.getUser(telephone, UserType.doctor.getIndex());

                if ((null != doctor) && (null != doctor.getUserId())) {
                    doctorId = doctor.getUserId();
                } else {
                    throw new ServiceException("找不到医生信息");
                }
            }
        }

        User user = userManager.getUser(doctorId);
        Group group = groupService.getGroupById(groupId);

        // 新加入该集团
        saveGroupDoctor(groupId, doctorId, inviteId, GroupDoctorStatus.正在使用.getIndex());

        //发送通知给管理员
        joinGroupSendMsgToManage(user, group, inviteId);

        data.put("groupDoctor", saveGroupDoctor);
        data.put("status", 0);
        data.put("msg", "");
        return data;
    }

    /**
     * 加入 集团/医院 时，给邀请人发送通知
     *
     * @param user
     * @param group
     * @param inviteId
     * @author wangqiao
     * @date 2016年3月31日
     */
    private void joinGroupSendMsgToInviteDoctor(User user, Group group, Integer inviteId, String content) throws HttpApiException {
        //参数校验
        if (user == null) {
            return;
        }
        if (group == null) {
            return;
        }

        //初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        textMsg.setContent(content);
        if (GroupType.group.getIndex().equals(group.getType())) {
            textMsg.setTitle("加入圈子");
            textMsg.setFooter("查看详情");
        }
        if (GroupType.hospital.getIndex().equals(group.getType())) {
            textMsg.setTitle("加入医院");
            textMsg.setFooter("查看详情");
        }
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", 24);
        imParam.put("bizId", user.getUserId());
        textMsg.setParam(imParam);

        //查询集团管理员列表
        List<GroupUser> groupUserList = companyUserService.getGroupUserByGroupId(group.getId());
        if (groupUserList != null) {
            for (GroupUser guser : groupUserList) {
                if (guser.getDoctorId().intValue() == inviteId.intValue()) {
                    textMsg.setFooter("查看医生");
                }
            }
        }

        mpt.add(textMsg);

        businessServiceMsg.sendTextMsg(inviteId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
        businessServiceMsg.sendTextMsg(inviteId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    /**
     * 加入 集团/医院 时，给管理员发送通知（管理员同时也是邀请人时，不再发送通知）
     *
     * @param user
     * @param group
     * @param inviteId
     * @author wangqiao
     * @date 2016年3月31日
     */
    private void joinGroupSendMsgToManage(User user, Group group, Integer inviteId) throws HttpApiException {
        //参数校验
        if (user == null) {
            return;
        }
        if (group == null) {
            return;
        }

        //初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);

        if (GroupType.group.getIndex().equals(group.getType())) {
            textMsg.setTitle("加入圈子");
        }
        if (GroupType.hospital.getIndex().equals(group.getType())) {
            textMsg.setTitle("加入医院");
        }
        textMsg.setFooter("查看医生");
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", 24);
        imParam.put("bizId", user.getUserId());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        //查询集团管理员列表
        List<GroupUser> groupUserList = companyUserService.getGroupUserByGroupId(group.getId());
        if (groupUserList != null) {
            if (StringUtils.isNotEmpty(user.getName())) {
                textMsg.setContent(user.getName() + "医生已加入" + group.getName());
            } else {
                textMsg.setContent(user.getTelephone() + "医生已加入" + group.getName());
            }
            for (GroupUser guser : groupUserList) {
                if ((inviteId == null) || ((inviteId != null) && (guser.getDoctorId().intValue() == inviteId.intValue()))) {
                    continue;
                }
                businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.dachen.health.group.IGroupFacadeService#confirmByDoctorApply(com.
     * dachen.health.group.group.entity.po.GroupDoctor, boolean)
     */
    @Override
    public Map<String, Object> confirmByDoctorApply(GroupDoctor gdoc, boolean approve) throws HttpApiException {
        Map<String, Object> result = Maps.newHashMap();
        // 参数校验
        if (Objects.isNull(gdoc) || StringUtils.isBlank(gdoc.getId())) {
            throw new ServiceException("申请Id不能为空");
        }
        GroupDoctor searchResult;
        try {
            searchResult = gdocDao.getById(gdoc.getId());
            if (searchResult == null) {
                // throw new ServiceException("加入医生集团的申请不存在，id为："+gdoc.getId());
                throw new ServiceException("申请已过期");
            } else if (!GroupDoctorStatus.申请待确认.getIndex().equals(
                    searchResult.getStatus())) {
                // 如果该记录不处于申请状态，则显示申请已过期
                throw new ServiceException("申请已过期");
            }

        } catch (Exception e) {
            // id 不是24字节的字符串 会抛异常
            // throw new ServiceException("加入医生集团的申请不存在，id为："+gdoc.getId());
            throw new ServiceException("申请已过期");
        }

        // 审核人的管理员权限判断 //暂时关闭权限判断
        // GroupUser gu = new GroupUser();
        // gu.setObjectId(searchResult.getGroupId());
        // gu.setDoctorId(ReqUtil.instance.getUserId());
        // gu.setType(GroupUserType.集团用户.getIndex());
        // GroupUser guser = cuserDao.getGroupUserById(gu);
        // if(guser == null){
        // throw new ServiceException("对不起，您没有审核权限");
        // }

        // 更新时间戳和变更人
        Integer currentUserId = ReqUtil.instance.getUserId();
        searchResult.setUpdator(currentUserId);
        searchResult.setUpdatorDate(new Date().getTime());

        Integer doctorId = searchResult.getDoctorId();
        User user = userManager.getUser(doctorId);

        // 审核通过
        if (approve) {
            searchResult.setStatus(GroupDoctorStatus.正在使用.getIndex());
            //searchResult.setReferenceId(0);
            //searchResult.setTreePath("/" + ReqUtil.instance.getUserId() + "/");

            // 设置主集团
            searchResult = updateMainGroup(searchResult, searchResult.getDoctorId(), searchResult.getStatus());


            result.put("msg", "审核成功");
        } else {
            // 审核不通过
            searchResult.setStatus(GroupDoctorStatus.申请拒绝.getIndex());
            result.put("msg", "忽略成功");
        }
        // 提交更新
        updateGroupDoctor(searchResult);

        if (approve) {
            // 加入集团的会话组
            Group group = groupDao.getById(searchResult.getGroupId());
            if (Objects.nonNull(group) ) {
                if (StringUtils.isBlank(group.getGid())) {
                    groupService.createGroupIm(group);
                }

                Group temp = groupDao.getById(group.getId());
                String gid = temp.getGid();

                GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
                Integer fromUserId = getFromUserId(group, groupUser);
                Integer toDoctorId = searchResult.getDoctorId();
                if (StringUtils.isNotBlank(gid)) {
                    Thread thread = new Thread(() -> joinGroupIM(gid, fromUserId, toDoctorId, user.getName(), group.getName()));
                    thread.start();
                }
            }
        }

        // 发送审核结果通知
        Group group = groupDao.getById(searchResult.getGroupId());
        sendManageConfirmMsg(approve, group.getName(), searchResult.getDoctorId(), searchResult.getGroupId());

        sendManageConfirmSms(user.getName(), group.getName(), user.getTelephone(), approve);
        return result;
    }

    /**
     * 发送管理员审核结果通知 给申请加入集团的医生
     *
     * @param approve   审核结果 false=不通过， true=通过
     * @param groupName 集团名称
     * @param doctorId  医生id
     * @author wangqiao
     * @date 2016年1月7日
     */
    private void sendManageConfirmMsg(boolean approve, String groupName, Integer doctorId, String groupId) throws HttpApiException {
        List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        //  通知内容需要放到资源配置文件中，方便统一更新
        imgTextMsg.setTitle("系统通知");
        imgTextMsg.setTime(System.currentTimeMillis());
        if (approve) {

            Group group = groupDao.getById(groupId);
            if (Objects.nonNull(group) && StringUtils.equals(group.getType(), "hospital")) {
                imgTextMsg.setContent(groupName + "已同意了您的加入医院申请");
            } else {
                imgTextMsg.setContent(groupName + "已同意了您的加入圈子申请");
            }
        } else {
            imgTextMsg.setContent(groupName + "已拒绝了您的加入圈子申请");
        }
        list.add(imgTextMsg);
        //imgTextMsg.setFooter("查看详情");
        businessServiceMsg.sendTextMsg(String.valueOf(doctorId), SysGroupEnum.TODO_NOTIFY, list, null);
        businessServiceMsg.sendTextMsg(String.valueOf(doctorId), SysGroupEnum.TODO_NOTIFY_DOC, list, null);
    }

    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    /**
     * 发送医生  集团对医生加入集团的审核结果
     *
     * @param doctorName 医生的姓名
     * @param groupName  集团的名称
     * @param phone      电话
     * @param approve    审核结果
     */
    private void sendManageConfirmSms(String doctorName, String groupName, String phone, boolean approve) throws HttpApiException {

        if (approve) {
            /*String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
            /**修改成从应用宝获取应用**/
            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
            String msg = baseDataService.toContent("0848", doctorName, groupName, generateUrl);
            smsManager.sendSMS(phone, msg, BaseConstants.XG_YSQ_APP);
        }
    }

    /**
     * 目前该方法仅用作医生离开集团使用
     */
    @Override
    public void updateGroupDoctorStatus(String groupId, Integer doctorId, String status) throws HttpApiException {
        // 读取关系数据的id
        GroupDoctor oldGdoc = gdocDao.getByDoctorIdAndGroupId(doctorId, groupId);
        if (oldGdoc == null) {
            return;// 找不到要更新的数据
        }
        if(StringUtils.equals(status, GroupDoctorStatus.离职.getIndex())){
        	if(isInGroupConsultPack(groupId,doctorId)){
            	throw new ServiceException("解除失败！当前医生已在本集团会诊包内，如需解除请移出集团会诊包");
            }
        }
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setId(oldGdoc.getId());
        gdoc.setGroupId(groupId);
        gdoc.setDoctorId(doctorId);
        gdoc.setStatus(status);
        updateGroupDoctor(gdoc);
        User doctor = userManager.getUser(doctorId);

        Group group = groupDao.getById(groupId);
        if (Objects.nonNull(group) && StringUtils.isNotBlank(group.getGid()) && StringUtils.equals(status, GroupDoctorStatus.离职.getIndex())) {
            // 应该做异步
            Integer currentUserId = ReqUtil.instance.getUserId();
            Integer fromUserId = null;
            User currentUser = userRepository.getUser(currentUserId);
            if (Objects.nonNull(currentUser) && Objects.nonNull(currentUser.getUserType()) && currentUser.getUserType().intValue() == UserType.customerService.getIndex()) {
                GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
                fromUserId = getFromUserId(group, groupUser);
            } else {
                fromUserId = currentUserId;
            }
            leaveGroupIM(group.getGid(), fromUserId, doctorId, doctor.getName(), group.getName());
        }

        //判断该集团是否为博德嘉联，若为博德嘉联集团，则离职需要清除token
        ServiceCategoryVO serviceCategoryVO = serviceCategoryService.getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
        String bdjlGroupId = serviceCategoryVO.getGroupId();

        if (StringUtils.equals(bdjlGroupId, groupId) && StringUtils.equals(status, GroupDoctorStatus.离职.getIndex())) {
            UserUtil.clearUserTokens(doctorId);
        }
    }

    private void removeConsultationPack(String groupId, Integer doctorId) {
        consultationPackDao.removeDoctor(groupId, doctorId);
        Object pack = consultationPackDao.findOneOkPack(doctorId);
        if (pack == null) {
            packService.deleteConsultationPackByDoctorId(doctorId);
        }
    }
    private boolean isInGroupConsultPack(String groupId,int doctorId){
    	GroupConsultationPack pack = consultationPackDao.getConsultationPackByGroupIdAndDoctorId(groupId, doctorId);
    	if(pack!=null){
    		return true;
    	}
    	return false;
    	
    }

    /**
     * (non-Javadoc)
     *
     * @see
     * com.dachen.health.group.IGroupFacadeService#updateGroupDoctor(com.dachen.health.group.group.entity.po.GroupDoctor)
     */
    @Override
    public void updateGroupDoctor(GroupDoctor updateGroupDoc) throws HttpApiException {
        // 参数校验
        if ((updateGroupDoc == null) || StringUtil.isEmpty(updateGroupDoc.getId())) {
            throw new ServiceException("Id为空！");
        }
        // 校验doctorId
        if (updateGroupDoc.getDoctorId() == null) {
            // 查找doctorId
            GroupDoctor applyGdoc = gdocDao.getById(updateGroupDoc.getId());
            updateGroupDoc.setDoctorId(applyGdoc.getDoctorId());
            updateGroupDoc.setGroupId(applyGdoc.getGroupId());
            updateGroupDoc.setReferenceId(applyGdoc.getReferenceId());
        }

        //离职校验
        if (StringUtil.equals(updateGroupDoc.getStatus(), GroupEnum.GroupDoctorStatus.离职.getIndex())
                || StringUtil.equals(updateGroupDoc.getStatus(), GroupEnum.GroupDoctorStatus.踢出.getIndex())) {
            //离职需要校验 不能是最后一个管理员
            if ((updateGroupDoc.getGroupId() == null) || StringUtils.isEmpty(updateGroupDoc.getGroupId())) {
                GroupDoctor applyGdoc = gdocDao.getById(updateGroupDoc.getId());
                updateGroupDoc.setGroupId(applyGdoc.getGroupId());
                updateGroupDoc.setDoctorId(applyGdoc.getDoctorId());
            }

            //超级管理员不能离职
//			GroupUser gu = gdocDao.findGroupUser(updateGroupDoc.getDoctorId(),updateGroupDoc.getGroupId(), GroupEnum.GroupUserStatus.正常使用.getIndex());
            GroupUser gu = companyUserService.getGroupUserByIdAndStatus(null, updateGroupDoc.getDoctorId(), updateGroupDoc.getGroupId(), null, GroupUserStatus.正常使用.getIndex());
            if ((gu != null) && "root".equals(gu.getRootAdmin())) {
                throw new ServiceException("不能删除超级管理员");
            }

            //查询集团的所有管理员
            List<GroupUser> groupUserList = companyUserService.getGroupUserByGroupId(updateGroupDoc.getGroupId());
            if ((groupUserList != null) && (groupUserList.size() == 1)) {
                if (updateGroupDoc.getDoctorId().equals(groupUserList.get(0).getDoctorId())) {
                    throw new ServiceException("该医生是唯一管理员，不能解除！");
                }
            }

            // 离职需要设置属性
            updateGroupDoc.setOnLineState("0");
            updateGroupDoc.setTaskDuration(0L);
        }

        // 如果是加入集团，需要判断是否设置主集团
        updateGroupDoc = updateMainGroup(updateGroupDoc, updateGroupDoc.getDoctorId(), updateGroupDoc.getStatus());

        // 更新数据持久化
        gdocDao.update(updateGroupDoc);
        GroupDoctor gd = gdocDao.getById(updateGroupDoc.getId());

        // 确认加入医生集团
        if (StringUtil.equals(gd.getStatus(), GroupEnum.GroupDoctorStatus.正在使用.getIndex())) {
            initDataForDoctorJoinGroup(gd.getDoctorId(), gd.getGroupId(), gd.getReferenceId());
        } else if (StringUtil.equals(gd.getStatus(), GroupEnum.GroupDoctorStatus.离职.getIndex()) || StringUtil.equals(gd.getStatus(), GroupEnum.GroupDoctorStatus.踢出.getIndex())) {
            // 离职、踢出医生集团
            initDataForDoctorLeaveGroup(gd.getDoctorId(), gd.getGroupId());
        }

    }

    /**
     * 计算 groupDoctor的 treepath 和 reference
     *
     * @param gdoc
     * @param doctorId
     * @param inviteId
     * @param groupId
     * @return
     * @author wangqiao
     * @date 2016年1月7日
     */
    private GroupDoctor updateTreePath(GroupDoctor gdoc, Integer doctorId, Integer inviteId, String groupId) {
        if (doctorId.equals(inviteId)) {
            // 自己是邀请人
            gdoc.setReferenceId(0);
            gdoc.setTreePath("/" + doctorId + "/");
        } else {
            // 查询邀请人是否在集团中
            GroupDoctor docInvite = gdocDao.getByDoctorIdAndGroupId(inviteId, groupId);
            if (docInvite == null) {
                // 邀请人不在本集团中
                gdoc.setReferenceId(0);
                gdoc.setTreePath("/" + doctorId + "/");
            } else {
                // 邀请人在本集团中
                gdoc.setReferenceId(inviteId);
                gdoc.setTreePath(docInvite.getTreePath() + "/" + inviteId + "/");
            }
        }
        return gdoc;
    }

    /**
     * 设置 是否主集团
     *
     * @param gdoc
     * @param doctorId
     * @param status
     * @return
     * @author wangqiao
     * @date 2016年1月7日
     */
    private GroupDoctor updateMainGroup(GroupDoctor gdoc, Integer doctorId, String status) {
        // 如果是加入，设置 是否主集团
        if (StringUtil.equals(status, GroupEnum.GroupDoctorStatus.正在使用.getIndex())) {
            List<GroupDoctor> mainGroupList = gdocDao.findMainGroupByDoctorId(doctorId);

            if ((mainGroupList != null) && (mainGroupList.size() > 0)) {

                //判断mainGroupList里的元素是否有屏蔽的集团，若有，则设置主集团为true，并将这部分记录的主集团状态设置为false
                boolean setMain = false;
                List<String> skipGroupIds = groupDao.getSkipGroupIds();
                for (GroupDoctor groupDoctor : mainGroupList) {
                    Group aGroup = groupService.getGroupById(groupDoctor.getGroupId());
                    if (aGroup.getType().equals("hospital") || ((skipGroupIds != null) && skipGroupIds.contains(groupDoctor.getGroupId()))) {
                        setMain = true;
                        GroupDoctor tempGroupDoctor = new GroupDoctor();
                        tempGroupDoctor.setGroupId(groupDoctor.getGroupId());
                        tempGroupDoctor.setMain(Boolean.FALSE);
                        tempGroupDoctor.setId(groupDoctor.getId());
                        groupDoctorService.updateGroupDoctor(tempGroupDoctor);
                    }
                }

                gdoc.setMain(setMain);
            } else {
                gdoc.setMain(true);
            }
        }
        return gdoc;
    }

    private void joinGroupIM(String gid, Integer operater, Integer doctorId, String doctorName, String groupName) {
        UpdateGroupRequestMessage updateGroupRequestMessage = new UpdateGroupRequestMessage();
        updateGroupRequestMessage.setGid(gid);
        updateGroupRequestMessage.setAct(1);
        updateGroupRequestMessage.setRole(1);
        updateGroupRequestMessage.setFromUserId(String.valueOf(operater));
        updateGroupRequestMessage.setToUserId(String.valueOf(doctorId));
        try {
            Object o = iMsgService.updateGroup(updateGroupRequestMessage);
        } catch (Exception e) {
            logger.info("{}加入集团时未成功加入到集团会话组", doctorName);
        }
    }

    private void leaveGroupIM(String gid, Integer operater, Integer doctorId, String doctorName, String groupName) {
        UpdateGroupRequestMessage updateGroupRequestMessage = new UpdateGroupRequestMessage();
        updateGroupRequestMessage.setGid(gid);
        updateGroupRequestMessage.setAct(2);
        updateGroupRequestMessage.setFromUserId(String.valueOf(operater));
        updateGroupRequestMessage.setToUserId(String.valueOf(doctorId));
        iMsgService.updateGroup(updateGroupRequestMessage);
    }

    /**
     * 新增 医生与集团关系信息
     *
     * @param groupId
     * @param doctorId
     * @param inviteId
     * @param status
     * @author wangqiao
     * @date 2016年1月7日
     */
    private void saveGroupDoctor(String groupId, Integer doctorId, Integer inviteId, String status) throws HttpApiException {
        // 参数校验
        if (groupId == null) {
            throw new ServiceException("集团id为空！");
        }
        if ((doctorId == null) || (doctorId == 0)) {
            throw new ServiceException("医生id为空！");
        }
        // 邀请人为空，则设置为医生本人邀请的
        if ((inviteId == null) || (inviteId == 0)) {
            inviteId = doctorId;
        }
        Group group = groupService.getGroupById(groupId);
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setGroupId(groupId);
        gdoc.setDoctorId(doctorId);
        gdoc.setStatus(status);
        // 设置邀请人 计算tree信息
        gdoc = updateTreePath(gdoc, doctorId, inviteId, groupId);
        // 设置主集团
        gdoc = updateMainGroup(gdoc, doctorId, status);

        gdoc.setCreator(inviteId);
        gdoc.setUpdator(inviteId);
        gdoc.setReferenceId(inviteId);
        gdoc.setCreatorDate(new Date().getTime());
        gdoc.setUpdatorDate(new Date().getTime());
        if (GroupType.group.getIndex().equals(group.getType())) {
            gdoc.setType(GroupType.group.getIndex());
        } else if (GroupType.dept.getIndex().equals(group.getType())){
            gdoc.setType(GroupType.dept.getIndex());
        } else {
            gdoc.setType(GroupType.hospital.getIndex());
        }
        gdocDao.save(gdoc);

        // 关联业务数据初始化
        if (StringUtil.equals(gdoc.getStatus(), GroupEnum.GroupDoctorStatus.正在使用.getIndex())) {
            initDataForDoctorJoinGroup(gdoc.getDoctorId(), gdoc.getGroupId(), gdoc.getReferenceId());
        }// 新增记录，不存在离职情况

    }

    /**
     * 新增 医生与集团关系信息
     *
     * @param groupId
     * @param doctorId
     * @param inviteId
     * @param status
     * @author wangqiao
     * @date 2016年1月7日
     */
    private void saveGroupDoctor2(String groupId, Integer doctorId, Integer inviteId, String status, String applyMsg) throws HttpApiException {
        // 参数校验
        if (groupId == null) {
            throw new ServiceException("集团id为空！");
        }
        if ((doctorId == null) || (doctorId == 0)) {
            throw new ServiceException("医生id为空！");
        }
        // 邀请人为空，则设置为医生本人邀请的
        if ((inviteId == null) || (inviteId == 0)) {
            inviteId = doctorId;
        }
        Group group = groupService.getGroupById(groupId);
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setGroupId(groupId);
        gdoc.setDoctorId(doctorId);
        gdoc.setStatus(status);
        // 设置邀请人 计算tree信息
        gdoc = updateTreePath(gdoc, doctorId, inviteId, groupId);
        // 设置主集团
        gdoc = updateMainGroup(gdoc, doctorId, status);

        gdoc.setCreator(inviteId);
        gdoc.setUpdator(inviteId);
        gdoc.setReferenceId(inviteId);
        gdoc.setCreatorDate(new Date().getTime());
        gdoc.setUpdatorDate(new Date().getTime());
        if (GroupType.group.getIndex().equals(group.getType())) {
            gdoc.setType(GroupType.group.getIndex());
        } else if (GroupType.dept.getIndex().equals(group.getType())) {
            gdoc.setType(GroupType.dept.getIndex());
        }else {
            gdoc.setType(GroupType.hospital.getIndex());
        }
        gdoc.setApplyMsg(applyMsg);
        gdocDao.save(gdoc);

        // 关联业务数据初始化
        if (StringUtil.equals(gdoc.getStatus(), GroupEnum.GroupDoctorStatus.正在使用.getIndex())) {
            initDataForDoctorJoinGroup(gdoc.getDoctorId(), gdoc.getGroupId(), gdoc.getReferenceId());
        }

        //产品要求扫描二维码的需要发送通知
        if (StringUtils.equals(gdoc.getStatus(), GroupDoctorStatus.申请待确认.getIndex())) {
            groupDoctorService.sendNotifyForApplyJoin(doctorId,group.getName(),gdoc.getId(),groupId);
        }
    }

    /**
     * 医生加入医生集团的相关数据初始化
     *
     * @author wangqiao
     * @date 2015年12月26日
     */
    private void initDataForDoctorJoinGroup(Integer doctorId, String groupId,
                                            Integer referenceId) throws HttpApiException {

        // 将医生相关文章加入集团
        articleService.addDTG(String.valueOf(doctorId), groupId);
        //就医知识加入集团
        knowledgeService.addDoctorToGroup(String.valueOf(doctorId), groupId);

        // 初始化医生与集团的抽成比例
        groupProfitService.initProfitByJoinGroup(doctorId, groupId, referenceId);

        // 适配医生的套餐价格，在集团套餐价格范围内
        List<Integer> doctorIds = new ArrayList<Integer>();
        doctorIds.add(doctorId);
        packService.executeFeeUpdate(groupId, doctorIds);

        //如果是加入医院，则需要将医生所在的科室加入到医院中，并将该医生加入这个科室
        initGroupHospitalDepartment(doctorId, groupId);

        // 发送指令，通知移动端刷新通讯录
        businessMsgService.addGroupNotify(doctorId, groupId);

        //更新groupdoctor中的createDate为当前时间
        gdocDao.updateCreateDate(groupId, doctorId);

        try {
            User doc = userManager.getUser(doctorId);
            if (doc.getStatus() == UserStatus.normal.getIndex()) {
                Group g = groupDao.getById(groupId);
                if ((g != null)
                        && !GroupActive.active.getIndex().equals(g.getActive())
                        && GroupType.group.getIndex().equals(g.getType())) {
                    //判断集团的人数并激活集团
                    groupDoctorService.activeGroupByMemberNum(groupId);
                }
            }
        } catch (Exception e) {
            logger.error("error cause by ", e);
        }
    }

    /**
     * 医生离开医生集团的相关数据初始化
     *
     * @param doctorId
     * @param groupId
     * @author wangqiao
     * @date 2015年12月26日
     */
    private void initDataForDoctorLeaveGroup(Integer doctorId, String groupId) throws HttpApiException {
        /* 删除所有集团管理员的关联信息 */
//		cuserDao.deleteByGroupUser(groupId, doctorId);
        companyUserService.deleteGroupUserByLeaveGroup(groupId, doctorId);
		/* 删除所有科室与医生的关联信息 */
        ddDao.deleteAllCorrelation(groupId, doctorId);
		/* 删除所有值班信息 */
        onlineDao.deleteAllByDoctorData(groupId, doctorId);

        // 删除医生与集团的抽成比例
//		GroupProfitParam param = new GroupProfitParam();
//		param.setGroupId(groupId);
//		param.setId(doctorId);
        groupProfitService.delete(doctorId, groupId);

        // 当该医生有其它集团没没有主集团时，将最早加入的集团设置为主集团
        //这里初始化医生集团的时候得区分医院（hospital）与集团 （Group）
        Group g = groupDao.getById(groupId);
        if ((g != null) && GroupType.group.getIndex().equals(g.getType())) {
            groupDoctorService.initMainGroup(doctorId);//集团操作
        }

        // 发送指令，通知移动端刷新通讯录
        businessMsgService.deleteGroupNotify(doctorId, groupId);

        //离职时，删除该医生在集团中的会诊套餐
        removeConsultationPack(groupId, doctorId);
        //删除预约套餐
        if ((g != null) && (g.getConfig() != null) && g.getConfig().isOpenAppointment()) {
            removeAppointmentPack(groupId, doctorId);
        }
    }

    private void removeAppointmentPack(String groupId, Integer doctorId) {
        Map<String, Object> sqlParam = new HashMap<>();
        sqlParam.put("status", PackEnum.PackStatus.close.getIndex());
        packMapper.updateDoctorAppointmentPack(sqlParam);
    }


    /**
     * 医生加入医院时，初始化科室信息
     *
     * @param doctorId
     * @param groupId
     * @author wangqiao
     * @date 2016年3月30日
     */
    private void initGroupHospitalDepartment(Integer doctorId, String groupId) {
        //参数校验
        if ((doctorId == null) || (doctorId == 0)) {
            throw new ServiceException("医生Id不能为空");
        }
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团Id不能为空");
        }
        //查询集团信息 TODO
        Group group = groupDao.getById(groupId);
        if (group == null) {
            return;
        }
        //只处理医院类型的group
        if (!GroupType.hospital.getIndex().equals(group.getType())) {
            return;
        }

        //查询医生的科室信息，以及其他父节点科室信息
        User user = userManager.getUser(doctorId);
        if ((user == null) || (user.getDoctor() == null)) {
            return;
        }
        //没有科室id，则不用新增组织
        String deptId = user.getDoctor().getDeptId();
        if (StringUtils.isEmpty(deptId)) {
            return;
        }
        //找不到科室信息，则不用新增组织
        DeptVO deptVO = baseDataService.getDeptAndParentDeptById(deptId);
        if (deptVO == null) {
            return;
        }
        //存在上级科室
        if (deptVO.getParentDept() != null) {
            DeptVO parentDeptVO = deptVO.getParentDept();
            Department parentDepartment = departmentService.getDepartmentByName(parentDeptVO.getName(), groupId);
            if (parentDepartment == null) {
                //新增 上级科室作为新的组织
                parentDepartment = departmentService.addDepartment(groupId, parentDeptVO.getName(), null, user.getUserId());
            }

            Department department = departmentService.getDepartmentByName(deptVO.getName(), groupId);
            if (department == null) {
                // 新增 科室 作为新的组织
                department = departmentService.addDepartment(groupId, deptVO.getName(), parentDepartment.getId(), user.getUserId());
                if (department == null) {
                    return;
                }
            }
            //将医生加入到科室中
            departmentDoctorService.createDepartmentDoctor(groupId, department.getId(), doctorId);


        } else {
            //不存在上级科室
            Department department = departmentService.getDepartmentByName(deptVO.getName(), groupId);
            if (department == null) {
                // 新增 科室 作为新的组织
                department = departmentService.addDepartment(groupId, deptVO.getName(), null, user.getUserId());
                if (department == null) {
                    return;
                }
            }
            //将医生加入到科室中
            departmentDoctorService.createDepartmentDoctor(groupId, department.getId(), doctorId);
        }


    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#getDutyInfo(java.lang.Integer)
     */
    @Override
    public Map<String, Object> getDutyInfo(Integer userId) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<GroupDoctorVO> ggdocs = gdocDao.getByDoctorId(userId, null, GroupDoctorVO.class);
        // 免打扰从医生资料里取。
        String troubleFree = "1";
        User user = userManager.getUser(userId);
        if (user != null) {
            if ((user.getDoctor() != null)
                    && (user.getDoctor().getTroubleFree() != null)) {
                troubleFree = user.getDoctor().getTroubleFree();
            }
        }
        data.put("troubleFree", troubleFree);
        data.put("serverTime", System.currentTimeMillis());
        //处理被屏蔽的集团医生信息
        List<GroupDoctorVO> gdocs = new ArrayList<>();
        //处理屏蔽的集团和为审核通过的集团
        for (GroupDoctorVO groupDoctorVO : ggdocs) {
            boolean flag = groupService.isNormalGroup(groupDoctorVO.getGroupId());
            if (flag) {
                gdocs.add(groupDoctorVO);
            }
        }


        if (gdocs.isEmpty()) {
            PlatformDoctor pdoc = platformDoctorService.getOne(userId);
            data.put("onLineState", pdoc == null ? "2" : pdoc.getOnLineState());
            data.put("onLineTime", pdoc == null ? 0l : pdoc.getOnLineTime());
            data.put("dutyDuration", pdoc == null ? 0 : pdoc.getDutyDuration() == null ? 0 : pdoc.getDutyDuration());
            data.put("outpatientPrice", groupService.getPlatformInfo().getOutpatientPrice());
        } else {
            for (GroupDoctorVO gdoc : gdocs) {
                Group group = groupService.getGroupById(gdoc.getGroupId());
                if (group != null) {
                    gdoc.setGroupName(group.getName());
                }
            }
            data.put("groupDoctor", gdocs);
            //平台的值班信息也返回
            PlatformDoctor pdoc = platformDoctorService.getOne(userId);
            data.put("onLineState", pdoc == null ? "2" : pdoc.getOnLineState());
            data.put("onLineTime", pdoc == null ? 0l : pdoc.getOnLineTime());
            data.put("dutyDuration", pdoc == null ? 0 : pdoc.getDutyDuration() == null ? 0 : pdoc.getDutyDuration());
            data.put("outpatientPrice", groupService.getPlatformInfo().getOutpatientPrice());
        }
        return data;
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#groupApply(com.dachen.health.group.group.entity.po.GroupApply)
     */
    @Override
    public GroupApply groupApply(GroupApply groupApply) throws HttpApiException {
        //参数校验
        if (groupApply == null) {
            throw new ServiceException("参数不能为空");
        }
        if (groupApply.getApplyUserId() == null) {
            throw new ServiceException("申请者为空");
        }
        if (StringUtil.isEmpty(groupApply.getName())) {
            throw new ServiceException("集团名称为空");
        }
        //设置logo 不需要截取  update by wangl
		/*if(StringUtil.isNotBlank(groupApply.getLogoUrl())){
			String logoPath = PropertiesUtil.removeUrlPrefix(groupApply.getLogoUrl());
			groupApply.setLogoUrl(logoPath);
		}*/

        //权限校验（如果用户已经在一个已审核通过的集团是超级管理员，则不允许再次申请）
        int applyUserId = groupApply.getApplyUserId();
        GroupUser gu = gdocDao.findByRootAndDoctorId(applyUserId);

        //当前用户不存在任何超级管理员
        if (gu == null) {
            // 首次申请集团
            return groupApplyFirst(groupApply.getApplyUserId(), groupApply.getName(), groupApply.getIntroduction(), groupApply.getLogoUrl());
        }
        //存在某个集团是超级管理员
        Group group = groupService.getGroupById(gu.getObjectId());
        if (group == null) {
            //groupUser有垃圾数据 需要 删除垃圾数据
            //首次申请集团
            return groupApplyFirst(groupApply.getApplyUserId(), groupApply.getName(), groupApply.getIntroduction(), groupApply.getLogoUrl());

        } else if (GroupEnum.GroupApplyStatus.audit.getIndex().equals(group.getApplyStatus())) {
            //已有待审核的集团，不能申请集团
            throw new ServiceException("您已经有一个集团正在申请中，不能再次申请");

        } else if (GroupEnum.GroupApplyStatus.notpass.getIndex().equals(group.getApplyStatus())) {
            //已有审核不通过的集团
            // 再次申请集团
            return groupApplyAgain(groupApply.getApplyUserId(), groupApply.getName(), groupApply.getIntroduction(), groupApply.getLogoUrl(), group);

        } else {
            //已有无状态的集团（历史数据），或者审核通过状态的集团
            //不能申请集团
            throw new ServiceException("您已经是其他集团超级管理员，不能再次申请");
        }
    }

    /**
     * 第一次申请创建医生集团
     * 新建集团记录，申请人加入集团，申请人设置为超级管理员，新建集团申请审核记录
     *
     * @param doctorId
     * @param groupName
     * @param introduction
     * @param logoUrl
     * @return
     * @author wangqiao
     * @date 2016年3月4日
     */
    private GroupApply groupApplyFirst(Integer doctorId, String groupName, String introduction, String logoUrl) throws HttpApiException {
        //新建集团
        Long nowDate = System.currentTimeMillis();
        Group group = new Group();
        group.setCreator(doctorId);
        group.setCreatorDate(nowDate);
        group.setIntroduction(introduction);
        group.setName(groupName);
        //初始化 集团默认的配置参数
        GroupConfig config = initGroupConfigForCreateGroup();
        group.setConfig(config);
        //注册医生集团时标示集团未认证
        group.setCertStatus(GroupCertStatus.noCert.getIndex());
        //标注集团待审核
        group.setApplyStatus(GroupEnum.GroupApplyStatus.audit.getIndex());
        //标注集团未激活
        group.setActive(GroupActive.inactive.getIndex());
        //图片
        group.setLogoUrl(logoUrl);
        //默认值班价格
        group.setOutpatientPrice(1000);

        group.setType(GroupType.group.getIndex());
        // 集团屏蔽状态 add by tanyf 20160604
        group.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());

        //注册集团
        Group retGroup = groupService.createGroup(group);

        FeeParam param = new FeeParam();
        param.setGroupId(retGroup.getId());

        param.setTextMin(1000);
        param.setTextMax(10000);

        param.setPhoneMin(1000);
        param.setPhoneMax(10000);

        param.setCarePlanMin(0);
        param.setCarePlanMax(10000);

        /** add by tanyf 20160627
         param.setAppointmentDefault(2000*100);
         param.setAppointmentMax(20000*100);
         param.setAppointmentMin(500*100); end */

        feeService.save(param);//保存集团收费
//		//开通公共号
//		pubGroupService.createPubForGroupCreate("患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue(),
//				retGroup.getCreator(),retGroup.getId(),retGroup.getName(),retGroup.getIntroduction(),retGroup.getLogoUrl());
//		pubGroupService.createPubForGroupCreate("集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue(),
//				retGroup.getCreator(),retGroup.getId(),retGroup.getName(),retGroup.getIntroduction(),retGroup.getLogoUrl());

        //医生加入集团
        saveCompleteGroupDoctor(retGroup.getId(), ReqUtil.instance.getUserId(), null, ReqUtil.instance.getUserId());
        //医生设置为超级管理员
        companyUserService.addRootGroupManage(retGroup.getId(), doctorId, doctorId);

        if (StringUtil.isNotEmpty(retGroup.getId())) {
            //创建集团的时候，需要将医生之前创建的文章挂在病例下面
            updateDoctorDiseaseBeforeCreateGroup(retGroup.getId(), doctorId);
        }

        //新建集团申请审核记录
        GroupApply groupApply = groupService.createNewGroupApply(retGroup, logoUrl);

        //发送通知和短信提醒
        sendManageForCreateGroup(doctorId);

        return groupApply;
    }

    /**
     * 创建集团/医院时，设置默认的配置参数
     *
     * @return
     * @author wangqiao
     * @date 2016年4月29日
     */
    private GroupConfig initGroupConfigForCreateGroup() {
        //设置集团为允许医生申请加入，也允许医生邀请其他医生加入
        GroupConfig config = new GroupConfig();
        config.setMemberApply(true);
        config.setMemberInvite(true);

        //抽成比例默认都设置为0
        config.setCarePlanGroupProfit(GroupConfig.CARE_PLAN_GROUP_PROFIT_DEFAULT);
        config.setCarePlanParentProfit(GroupConfig.CARE_PLAN_PARENT_PROFIT_DEFAULT);

        config.setTextGroupProfit(GroupConfig.TEXT_GROUP_PROFIT_DEFAULT);
        config.setTextParentProfit(GroupConfig.TEXT_PARENT_PROFIT_DEFAULT);

        config.setPhoneGroupProfit(GroupConfig.PHONE_GROUP_PROFIT_DEFAULT);
        config.setPhoneParentProfit(GroupConfig.PHONE_PARENT_PROFIT_DEFAULT);

        config.setConsultationGroupProfit(GroupConfig.CONSULTATION_GROUP_PROFIT_DEFAULT);
        config.setConsultationParentProfit(GroupConfig.CONSULTATION_PARENT_PROFIT_DEFAULT);

        config.setClinicGroupProfit(GroupConfig.CLINIC_GROUP_PROFIT_DEFAULT);
        config.setClinicParentProfit(GroupConfig.CLINIC_PARENT_PROFIT_DEFAULT);

        config.setChargeItemGroupProfit(GroupConfig.CHARGE_ITEM_GROUP_PROFIT_DEFAULT);
        config.setChargeItemParentProfit(GroupConfig.CHARGE_ITEM_PARENT_PROFIT_DEFAULT);

        config.setAppointmentGroupProfit(GroupConfig.APPOINTMENT_GROUP_PROFIT_DEFAULT);
        config.setAppointmentParentProfit(GroupConfig.APPOINTMENT_PARENT_PROFIT_DEFAULT);

        config.setGroupProfit(GroupConfig.GROUP_PROFIT_DEFAULT);
        config.setParentProfit(GroupConfig.PARENT_PROFIT_DEFAULT);

        // 默认设置值班开始和结束时间段为08:00-22:00
        config.setDutyStartTime(GroupUtil.GROUP_DUTY_START_TIME);
        config.setDutyEndTime(GroupUtil.GROUP_DUTY_END_TIME);
        return config;
    }

    /**
     * 重新提交集团创建申请
     *
     * @param doctorId
     * @param groupName
     * @param introduction
     * @param logoUrl
     * @param group
     * @return
     * @author wangqiao
     * @date 2016年3月4日
     */
    private GroupApply groupApplyAgain(Integer doctorId, String groupName, String introduction, String logoUrl, Group group) throws HttpApiException {
        Long nowDate = System.currentTimeMillis();
        //更新集团信息
//		group.setCreator(doctorId);
        group.setUpdator(doctorId);
        group.setUpdatorDate(nowDate);
        group.setName(groupName);
        group.setIntroduction(introduction);
        group.setLogoUrl(logoUrl);

        //初始化 集团默认的配置参数
        GroupConfig config = initGroupConfigForCreateGroup();
        group.setConfig(config);

        //注册医生集团时标示集团未认证
        group.setCertStatus(GroupCertStatus.noCert.getIndex());
        //标注集团待审核
        group.setApplyStatus(GroupEnum.GroupApplyStatus.audit.getIndex());
        //标注集团未激活
        group.setActive(GroupActive.inactive.getIndex());

        groupService.updateGroup(group);

        FeeParam param = new FeeParam();
        param.setGroupId(group.getId());

        param.setTextMin(1000);
        param.setTextMax(10000);

        param.setPhoneMin(1000);
        param.setPhoneMax(10000);

        param.setCarePlanMin(0);
        param.setCarePlanMax(10000);

        feeService.save(param);//保存集团收费

        //新建集团申请审核记录
        GroupApply groupApply = groupService.createNewGroupApply(group, logoUrl);

        //发送通知和短信提醒
        sendManageForCreateGroup(doctorId);

        return groupApply;
    }

    //TODO
    private void initDataForCreateGroup(Integer doctorId, String groupId,
                                        Integer referenceId) {

    }

    /**
     * 创建医生集团给创建人发送通知和短信提醒
     *
     * @param applyUserId
     * @author wangqiao
     * @date 2016年3月7日
     */
    private void sendManageForCreateGroup(Integer applyUserId) throws HttpApiException {
        User user = userManager.getUser(applyUserId);
        if (user == null) {
            return;
        }

        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setStyle(7);
        textMsg.setTitle("申请创建医生圈子");
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setContent("尊敬的" + user.getName() + "医生，您已申请创建医生圈子，目前正在审核中，请您耐心等待。");
        Map<String, Object> param = new HashMap<String, Object>();
        textMsg.setParam(param);
        mpt.add(textMsg);
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您已申请创建医生集团，目前正在审核中，请您耐心等待。");
        final String msg = baseDataService.toContent("1020", user.getName(), BaseConstants.XG_YSQ_APP);
        mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.IGroupFacadeService#updateDoctorDiseaseBeforeCreateGroup(java.lang.String, java.lang.Integer)
     */
    @Override
    public void updateDoctorDiseaseBeforeCreateGroup(String groupId,
                                                     Integer creatorId) {
        System.out.println("");
        List<ArticleVO> articleList = articleDao
                .getArcticleListByCreatorId(String.valueOf(creatorId));

        if ((null != articleList) && (articleList.size() > 0)) {
            GroupDisease disease = null;
            for (ArticleVO article : articleList) {
                disease = new GroupDisease();
                String diseaseId = article.getDiseaseId();
                String name = null;
                String parentName = null;
                if (StringUtils.isNotEmpty(diseaseId)) {
                    List<String> ids = new ArrayList<String>();
                    ids.add(diseaseId);
                    List<DiseaseTypeVO> diseaseTypeVO = baseDataDao
                            .getDiseaseType(ids);
                    if ((null != diseaseTypeVO) && (diseaseTypeVO.size() > 0)) {
                        name = diseaseTypeVO.get(0).getName();
                        parentName = diseaseTypeVO.get(0).getParent();
                    }
                }
                disease.setDiseaseId(diseaseId);
                disease.setName(name);
                disease.setParent(parentName);
                disease.setGroupId(groupId);
                List<String> articles = new ArrayList<String>();
                articles.add(article.getId());
                disease.setArticleId(articles);
                disease.setCount(1);
                // 更新对应集团对应病种的文章数量
                if (!StringUtil.isEmpty(groupId)) {
                    updateDiseaseTree(disease, "+");
                }
            }
        }

    }

    /**
     * 更新兵种库
     *
     * @param param
     * @param type
     */
    private void updateDiseaseTree(GroupDisease param, String type) {
        GroupDisease gd = diseaseTreeDao.saveGroupDisease(param);
        List<String> list = gd.getArticleId();
        if (type.trim().equals("+")) {
            list.addAll(param.getArticleId());
        } else if (type.trim().equals("-")) {
            list.removeAll(param.getArticleId());
        }
        int count = list.size();
        count = count < 0 ? 0 : count;
        gd.setArticleId(list);
        gd.setCount(count);
        diseaseTreeDao.updateCount(gd);
    }


    @Override
    public Object createHospital(String hospitalId, Integer doctorId, Integer operationUserId) throws HttpApiException {
        //参数校验
        if (StringUtil.isEmpty(hospitalId)) {
            throw new ServiceException("医院id不能为空");
        }
        if (doctorId == 0) {
            throw new ServiceException("医生id不能为空");
        }
        //判断医院是否存在
        HospitalPO hospital = baseDataDao.getHospitalDetail(hospitalId);//医院基本信息
        if (hospital == null) {
            throw new ServiceException("此医院不存在，请检查传入的医院参数");
        }
        //判断当前医生有没有加入其他的医院集团
        Integer userId = ReqUtil.instance.getUserId();
        GroupDoctor checkGroup = groupDao.checkDoctor(doctorId);
        if (checkGroup != null) {
            Group gp = groupDao.getById(checkGroup.getGroupId());
            throw new ServiceException("该医生已加入:" + gp.getName());
        }
        //检查医院是否已经被激活
        Group checkHospital = groupDao.checkHospital(hospitalId);
        if (checkHospital != null) {
            Integer uId = checkHospital.getCreator();
            User user = userManager.getUser(uId);
            throw new ServiceException("该医院已经被：" + user.getName() + "激活，不可重复进行！！！ ");
        }
        //现在就可以创建医院了
        Group group = new Group();
        group.setCreator(doctorId);
        group.setCreatorDate(System.currentTimeMillis());
        group.setName(hospital.getName());

        //初始化 集团默认的配置参数
        GroupConfig config = initGroupConfigForCreateGroup();
        group.setConfig(config);

        group.setUpdator(userId);
        group.setUpdatorDate(System.currentTimeMillis());
        group.setActive(GroupActive.active.getIndex());
        //默认值班价格
        group.setOutpatientPrice(1000);
        group.setHospitalId(hospitalId);
        group.setType(GroupType.hospital.getIndex());
        //注册集团
        Group retGroup = this.createHospitalGroup(group, doctorId, operationUserId);
        return retGroup;
    }

    public Group createHospitalGroup(Group group, Integer doctorId, Integer operationUserId) throws HttpApiException {
        HospitalPO hospital = baseDataDao.getHospitalDetail(group.getHospitalId());//医院基本信息

        // 集团屏蔽状态 add by tanyf 20160604
        group.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
        //持久化新建集团
        Group gp = groupService.createGroup(group);
        if (gp == null) {
            throw new ServiceException("新增医院失败");
        }

        FeeParam param = new FeeParam();
        param.setGroupId(gp.getId());
        param.setTextMin(1000);
        param.setTextMax(10000);
        param.setPhoneMin(1000);
        param.setPhoneMax(10000);
        param.setCarePlanMin(0);
        param.setCarePlanMax(10000);
        feeService.save(param);//保存集团收费
        //开通公共号
        createPubForHospital("患者之声", PubTypeEnum.PUB_GROUP_PATIENT.getValue(),
                gp.getCreator(), gp.getId(), gp.getName(), hospital.getDescription());
        createPubForHospital("集团动态", PubTypeEnum.PUB_GROUP_DOCTOR.getValue(),
                gp.getCreator(), gp.getId(), gp.getName(), hospital.getDescription());
        //医生加入集团
        saveCompleteGroupDoctor(gp.getId(), doctorId, null, ReqUtil.instance.getUserId());
        //医生设置为超级管理员
//		companyUserService.addRootHospitalManage(gp.getId() , doctorId);
        companyUserService.addRootGroupManage(gp.getId(), doctorId, operationUserId);

        if (StringUtil.isNotEmpty(gp.getId())) {
            //创建集团的时候，需要将医生之前创建的文章挂在病例下面
            updateDoctorDiseaseBeforeCreateGroup(gp.getId(), doctorId);
        }
        //发送通知和短信提醒
        sendManageForCreateHosPital(doctorId, group.getHospitalId(), gp.getId(), gp.getName());
        return gp;
    }

    /**
     * 创建医院公众号
     *
     * @param pubName
     * @param rtype
     * @param creator
     * @param groupId
     * @param groupName
     * @param groupIntroduction
     * @return
     */
    public PubPO createPubForHospital(String pubName, String rtype, Integer creator, String groupId, String groupName, String groupIntroduction) throws HttpApiException {
        String photoUrl = getHosPitalImage();
        PubPO pub = pubGroupService.createPubForGroupCreate(pubName, rtype, creator, groupId, groupName, groupIntroduction, photoUrl);
        return pub;
    }

    public static String getHosPitalImage() {
        return PropertiesUtil.getHeaderPrefix() + "/" + "default/" + PropertiesUtil.getContextProperty("pub.hospitalImg");
    }

    /**
     * 创建医院成功之后给创建人发送短信
     *
     * @param applyUserId
     * @param hospitalId
     * @param groupId
     * @param groupName
     * @author 姜宏杰
     * @date 2016年3月26日17:53:30
     */
    private void sendManageForCreateHosPital(Integer applyUserId, String hospitalId, String groupId, String groupName) throws HttpApiException {
        User user = userManager.getUser(applyUserId);
        if (user == null) {
            return;
        }
        HospitalPO hospital = baseDataDao.getHospitalDetail(hospitalId);//医院基本信息
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setStyle(7);
        textMsg.setTitle("医院创建通知");
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setContent("尊敬的" + user.getName() + "医生，" + hospital.getName() + "已成功创建，现在可邀请您医院的医生入驻医院。");
        Map<String, Object> param = new HashMap<String, Object>();
        textMsg.setFooter("立即邀请");
        param.put("bizType", 23);
        param.put("bizId", groupId);
        param.put("groupName", groupName);
        textMsg.setParam(param);
        mpt.add(textMsg);
        //发送通知给医生
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        //发送短信并提示医生下载医生端APP
        /*String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
            PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/

        /**修改成从应用宝获取应用**/
        String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
        String doc = BaseConstants.XG_YSQ_APP;

        final String msg = baseDataService.toContent("1023", user.getName(), hospital.getName(), doc, generateUrl);
        mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);

    }

    @Override
    public PageVO groupHospitalList(GroupParam group) {
        return groupDao.groupHospitalList(group);
    }


    @Override
    public Map<String, Object> getDetailByGroupId(String id) {
        return groupDao.getDetailByGroupId(id);
    }

    @Override
    public String updateHospitalRoot(String hospitalId, Integer newdoctorId, Integer operationUserId) throws HttpApiException {
        //参数校验
        if (StringUtil.isEmpty(hospitalId)) {
            throw new ServiceException("医院id不能为空");
        }
        if (newdoctorId == 0) {
            throw new ServiceException("医生id不能为空");
        }
        //判断医院是否存在
        HospitalPO hospital = baseDataDao.getHospitalDetail(hospitalId);//医院基本信息
        if (hospital == null) {
            throw new ServiceException("此医院不存在，请检查传入的医院参数");
        }
        //读取医生所在的医院信息
        GroupDoctor checkGroup = groupDao.checkDoctor(newdoctorId);
        if (checkGroup != null) {
            Group gp = groupDao.getById(checkGroup.getGroupId());
            throw new ServiceException("该医生已加入:" + gp.getName());
        }
        Group group = groupDao.checkHospital(hospitalId);
        if (group == null) {
            throw new ServiceException("根据医院id没有查询到集团信息，该医院集团可能已经不存在！" + hospitalId);
        }
        //根据集团id查询集团是否已经存在的超级管理员信息
        GroupUser gu = gdocDao.findGroupRootAdmin(group.getId());
        if (null == gu) {
            return "该医院集团尚未有超级管理员！";
        }
        //查询集团旧的超级管理员
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setDoctorId(gu.getDoctorId());
        gdoc.setGroupId(group.getId());
        gdoc.setStatus(GroupDoctorStatus.正在使用.getIndex());
        GroupDoctor oldDoc = gdocDao.getById(gdoc);
        if (oldDoc == null) {
            return "医生集团信息已经查询不到！";
        }
        //不要物理删除 更改state为离职（根据集团id、用户id）
        groupDoctorService.leaveHospitalRoot(oldDoc.getId(), operationUserId);
        //清除离职医生的关联信息
        initDataForDoctorLeaveGroup(gu.getDoctorId(), group.getId());
        //完成之后在进行新的加入
        //医生加入集团
        saveCompleteGroupDoctor(group.getId(), newdoctorId, null, operationUserId);
        //医生设置为超级管理员
//		GroupUser gUser = companyUserService.addRootHospitalManage(group.getId() , newdoctorId);
        GroupUser gUser = companyUserService.addRootGroupManage(group.getId(), newdoctorId, operationUserId);

//		User user = userManager.getUser(newdoctorId);
        //发送短信、通知并提示医生下载医生端APP
        sendManageForUpdateAdmin(newdoctorId, group.getId(), group.getName());
        return gUser.getId();
    }

    /**
     * 创建医院修改成功之后给新的管理员发送短信
     *
     * @param applyUserId
     * @param groupId
     * @param groupName
     * @author 姜宏杰
     * @date 2016年3月26日17:53:30
     */
    private void sendManageForUpdateAdmin(Integer applyUserId, String groupId, String groupName) throws HttpApiException {
        User user = userManager.getUser(applyUserId);
        if (user == null) {
            return;
        }
//		HospitalPO hospital = baseDataDao.getHospitalDetail(hospitalId);//医院基本信息
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setStyle(7);
        textMsg.setTitle("医院管理员变更通知");
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setContent("尊敬的" + user.getName() + "医生，您已成为" + groupName + "的管理员，现在可邀请您医院的医生入驻医院。");
        Map<String, Object> param = new HashMap<String, Object>();
        textMsg.setFooter("立即邀请");
        param.put("bizType", 23);
        param.put("bizId", groupId);
        param.put("groupName", groupName);
        textMsg.setParam(param);
        mpt.add(textMsg);
        //发送通知给医生
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
        businessServiceMsg.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        String url = null;
        String platForm = null;
        if (mobSmsSdk.isBDJL(user)) {//博得嘉联
            url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download.bdjl") + "?userType=3";
            platForm = BaseConstants.BD_DOC_APP;
        } else {
            url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download") + "?userType=3";
            platForm = BaseConstants.XG_DOC_APP;
        }
        //发送短信并提示医生下载医生端APP
        //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您已成为"+groupName+"的管理员，您可以登录玄关健康医生APP邀请其他医生加入医院。"+ShortUrlGenerator.getGenerateUrl(url));
        final String msg = baseDataService.toContent("1022", user.getName(), groupName, platForm, shortUrlComponent.generateShortUrl(url));
        mobSmsSdk.send(user.getTelephone(), msg);
    }


    @Override
    public boolean isInGroup(Integer doctorId, String groupId) {
        // 参数校验
        if (null == doctorId) {
            throw new ServiceException("医生Id为空");
        }
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }

        // 是否已有该医生与集团的关系记录
        GroupDoctor already = gdocDao.getByDoctorIdAndGroupId(doctorId, groupId);

        if ((already != null) && GroupDoctorStatus.正在使用.getIndex().equals(already.getStatus())) {
            return true;
        }

        return false;
    }

    @Override
    public Map<String, Object> joinGroup(String groupId, Integer userId, String telephone, Integer currentUserId, Integer platform) throws HttpApiException {
        Map<String, Object> data = Maps.newHashMap();
        // 参数校验
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }
        if (userId == null) {
            throw new ServiceException("医生Id为空");
        }

        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("手机号码为空");
        } else {
            User doctor = userManager.getUser(telephone, UserType.doctor.getIndex());

            if ((null != doctor) && (null != doctor.getUserId())) {
                userId = doctor.getUserId();
            } else {
                throw new ServiceException("找不到医生信息");
            }
        }

        // 是否已有该医生与集团的关系记录
        GroupDoctor already = gdocDao.getByDoctorIdAndGroupId(userId, groupId);

        User user = userManager.getUser(userId);
        Group group = groupService.getGroupById(groupId);

        if (already != null) {
            // 已加入该集团，则返回异常 已加入
            if (GroupDoctorStatus.正在使用.getIndex().equals(already.getStatus())) {
                // 查找集团
                Group gp = groupService.getGroupById(already.getGroupId());
                data.put("status", 3);
                if (gp != null) {
                    data.put("msg", "您已经加入了" + gp.getName());
                } else {
                    data.put("msg", "您已经加入了");
                }
                return data;
            } else {
                // 未加入该集团，则将状态改为已加入
                // 更新推荐人
                if (platform == 1) {
                    already = updateTreePath(already, already.getDoctorId(), currentUserId, groupId);
                } else if (platform == 2) {
                    //当由审核平台的管理员注册的医生时，邀请者为医生本人
                    already = updateTreePath(already, already.getDoctorId(), already.getDoctorId(), groupId);
                }

                // 更新状态
                already.setStatus(GroupDoctorStatus.正在使用.getIndex());
                already.setUpdator(currentUserId);
                already.setUpdatorDate(new Date().getTime());
                updateGroupDoctor(already);

                //发送通知给邀请人
                String content = user.getName() + "医生已接受您的邀请，加入" + group.getName();

                if (platform == 1) {
                    joinGroupSendMsgToInviteDoctor(user, group, currentUserId, content);
                    //发送通知给管理员
                    joinGroupSendMsgToManage(user, group, currentUserId);
                } else if (platform == 2) {
                    //发送通知给管理员
                    joinGroupSendMsgToManage(user, group, null);
                }
            }

        } else {

            //发送通知给邀请人
            String content = user.getName() + "医生已接受您的邀请，加入" + group.getName();

            // 新加入该集团
            if (platform == 1) {
                saveGroupDoctor(groupId, userId, currentUserId, GroupDoctorStatus.正在使用.getIndex());
                //发送通知给管理员
                joinGroupSendMsgToInviteDoctor(user, group, currentUserId, content);
                joinGroupSendMsgToManage(user, group, currentUserId);
            } else if (platform == 2) {
                saveGroupDoctor(groupId, userId, null, GroupDoctorStatus.正在使用.getIndex());
                //发送通知给管理员
                joinGroupSendMsgToManage(user, group, null);
            }


        }

        data.put("status", 0);
        data.put("msg", "");
        return data;
    }

    public Map<String, Object> joinGroupByQRCode(String groupId, Integer userId, String telephone, Integer inviterId, String applyMsg) throws HttpApiException {
        Map<String, Object> data = Maps.newHashMap();
        // 参数校验
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id为空");
        }
        if (userId == null) {
            throw new ServiceException("医生Id为空");
        }

        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("手机号码为空");
        } else {
            User doctor = userManager.getUser(telephone, UserType.doctor.getIndex());

            if ((null != doctor) && (null != doctor.getUserId())) {
                userId = doctor.getUserId();
            } else {
                throw new ServiceException("找不到医生信息");
            }
        }

        // 是否已有该医生与集团的关系记录
        GroupDoctor already = gdocDao.getByDoctorIdAndGroupId(userId, groupId);

        if (already != null) {
            // 已加入该集团，则返回异常 已加入
            if (GroupDoctorStatus.正在使用.getIndex().equals(already.getStatus())) {
                // 查找集团
                Group gp = groupService.getGroupById(already.getGroupId());
                throw new ServiceException("您已经加入了" + gp.getName());
            } else {
                // 未加入该集团，则将状态改为已加入，更新推荐人
                already = updateTreePath(already, already.getDoctorId(), inviterId, groupId);
                // 更新状态
                already.setStatus(GroupDoctorStatus.申请待确认.getIndex());
                already.setUpdator(inviterId);
                already.setUpdatorDate(new Date().getTime());
                //上级以第一次加入的为准
                //already.setReferenceId(inviterId);
                updateGroupDoctor(already);
            }

        } else {
            saveGroupDoctor2(groupId, userId, inviterId, GroupDoctorStatus.申请待确认.getIndex(), applyMsg);
        }
        return data;
    }

    @Override
    public void removeDoctors(String groupId, Integer[] doctorIds) throws HttpApiException {
        //1、先检查数据
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException("集团ID为空");
        }
        if (doctorIds.length <= 0) {
            throw new ServiceException("医生id为空");
        }
        for (int i = 0; i < doctorIds.length; i++) {
            updateGroupDoctorStatus(groupId, doctorIds[i], GroupEnum.GroupDoctorStatus.离职.getIndex());
        }
    }

    @Override
    public void inviterJoinGroup(String groupId, Integer[] doctorIds) throws HttpApiException {
        //1、先检查数据
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException("集团ID为空");
        }
        if (doctorIds.length <= 0) {
            throw new ServiceException("医生id为空");
        }
        for (int i = 0; i < doctorIds.length; i++) {
            User user = userManager.getUser(doctorIds[i]);
            //这里2表示运营平台，1表示集团后台
            Integer currentUserId = ReqUtil.instance.getUserId();
            joinGroup(groupId, doctorIds[i], user.getTelephone(), currentUserId, 2);
        }
    }
}
