package com.dachen.health.circle.service.impl;


import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.dao.IFriendReqDao;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.group.common.util.RemoteSysManager;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.impl.GroupDoctorServiceImpl;
import com.dachen.health.group.schedule.dao.IOnlineDao;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.user.service.IRelationService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.JSONUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Model(GroupDoctor2.class)
@Service
public class GroupDoctor2ServiceImpl extends BaseServiceImpl implements GroupDoctor2Service {

    @Autowired
    protected Group2Service group2Service;
    @Autowired
    protected GroupUser2Service groupUser2Service;
    @Autowired
    protected PubGroupService pubGroupService;
    @Autowired
    protected User2Service user2Service;
    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;
    @Autowired
    protected UserManager userManager;
    @Autowired
    private IRelationService relationService;
    @Autowired
    private  IFriendReqDao iFriendReqDao;
    @Autowired
    protected GroupDoctorApplyService groupDoctorApplyService;

    @Autowired
    protected GroupDoctorInviteService groupDoctorInviteService;

    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;

    private static final String CIRCLE_IFSAMECIRCLE_ACTION = "http://CIRCLE/inner/base/getUserIfSameCircle/{userId}/{toUserId}";
    private static final String CIRCLE_SAMECIRCLE_ACTION = "http://CIRCLE/inner/base/getSameCircleIds/{userId}/{toUserId}";

    @Override
    public List<String> findGroupIdListByDoctor(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.retrievedFields(true, "groupId");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> groupIdList = list.stream().map(o -> o.getGroupId()).collect(Collectors.toList());
        return groupIdList;
    }

    @Override
    public List<String> findGroupIdListExceptByDoctor(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("type").notEqual(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.retrievedFields(true, "groupId");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> groupIdList = list.stream().map(o -> o.getGroupId()).collect(Collectors.toList());
        return groupIdList;
    }

    @Autowired
    protected GroupUnionMemberService groupUnionMemberService;

    @Override
    public List<Integer> findDoctorIdListByGroupAndUnions(List<String> groupIdList, List<String> unionIdList) {
        List<String> idList = new ArrayList<>();
        if (SdkUtils.isNotEmpty(groupIdList)) {
            idList.addAll(groupIdList);
        }
        if (SdkUtils.isNotEmpty(unionIdList)) {
            List<String> unionGroupIdList = groupUnionMemberService.findGroupIdsByUnions(unionIdList);
            if (SdkUtils.isNotEmpty(unionGroupIdList)) {
                idList.addAll(unionGroupIdList);
            }
        }

        if (SdkUtils.isEmpty(idList)) {
            return null;
        }

        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").in(idList)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.retrievedFields(true, "doctorId");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<Integer> doctorIdList = list.stream().map(o -> o.getDoctorId()).collect(Collectors.toList());
        return doctorIdList;
    }

    /**
     * 返回这个组织正在使用的医生id
     * @param groupId
     * @return
     */
    @Override
    public List<Integer> findDoctorIdListByGroup(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.retrievedFields(true, "doctorId");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<Integer> doctorIdList = list.stream().map(o -> o.getDoctorId()).collect(Collectors.toList());
        return doctorIdList;
    }

    @Override
    public List<String> findDeptIdListByDoctor(List<Integer> doctorIdList) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").in(doctorIdList)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.retrievedFields(true, "groupId");
        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<String> groupIdList = list.stream().map(o->o.getGroupId()).collect(Collectors.toList());
        return groupIdList;
    }

    @Override
    public List<GroupDoctor2> findDeptListByDoctor(List<Integer> doctorIdList) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").in(doctorIdList)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapGroup(list);
        return list;
    }

    @Override
    public Map<Integer, Group2> findDeptMapByDoctor(List<Integer> doctorIdList) {
        List<GroupDoctor2> list = this.findDeptListByDoctor(doctorIdList);
        if (SdkUtils.isEmpty(list)) {
            return Collections.EMPTY_MAP;
        }
        Map<Integer, Group2> map = new HashMap<>();
        for (GroupDoctor2 groupDoctor2:list) {
            map.put(groupDoctor2.getDoctorId(), groupDoctor2.getGroup());
        }
        return map;
    }

    @Override
    public String findDeptIdByDoctor(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getGroupId();
    }

    @Override
    public void checkCurDeptIdWhenJoinDept(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isNotEmpty(list)) {
            throw new ServiceException("一名医生只能加入一个科室");
        }
    }
    @Override
    public boolean isJoinDept(Integer currentUserId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(currentUserId)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        List<GroupDoctor2> list = query.asList();
        if(SdkUtils.isNotEmpty(list)){
            return true;
        }
        return false;
    }

    @Override
    public boolean infoIsDept(Integer currentUserId, String groupId) {
        User user = user2Service.findDoctorById(currentUserId);
        if(user==null){
            throw new ServiceException("用户信息不存在");
        }
        Group2 group=group2Service.findAndCheckById(groupId);
        if(user.getDoctor().getHospitalId().equals(group.getHospitalId()) && user.getDoctor().getDeptId().equals(group.getDeptId())){
            return true;
        }
        return false;
    }

    @Override
    public List<GroupDoctor2> findByDoctor(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    @Override
    public List<GroupDoctor2> findFullByDoctor(Integer doctorId) {
        List<GroupDoctor2> list = this.findByDoctor(doctorId);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);
        return list;
    }

    @Override
    public GroupDoctor2 findFullById(String id) {
        GroupDoctor2 dbItem = this.findById(id);
        this.wrapAll(dbItem);
        return dbItem;
    }

    @Override
    public GroupDoctor2 findFullAndCheckById(String id) {
        GroupDoctor2 dbItem = this.findById(id);
        this.wrapAll(dbItem);
        group2Service.checkDept(dbItem.getGroup());
        return dbItem;
    }

    @Override
    public List<MobileGroupDoctorVO> findByDoctorAndVO(Integer doctorId) {
        List<GroupDoctor2> list = this.findByDoctor(doctorId);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);
        List<MobileGroupDoctorVO> voList = this.convertToMobile(list);
        return voList;
    }


    @Override
    public List<String> findGroupIdListByDoctorExcept(Integer doctorId, List<String> exceptGroupIdList) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());

        Criteria[] typeParams = new Criteria[2];
        typeParams[0] = query.criteria("type").equal(GroupEnum.GroupType.group.getIndex());
        typeParams[1] = query.criteria("type").equal(GroupEnum.GroupType.dept.getIndex());
        query.or(typeParams);

        if (SdkUtils.isNotEmpty(exceptGroupIdList)) {
            query.field("groupId").notIn(exceptGroupIdList);
        }
        query.retrievedFields(true, "groupId");

        query.order("-creatorDate");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> groupIdList = list.stream().map(o -> o.getGroupId()).collect(Collectors.toList());
        return groupIdList;
    }

    @Override
    public Pagination<MobileGroupDoctorVO> findPage(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize) {
        if(pageIndex>=1){
            return null;
        }
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);

        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());

        //long total = query.countAll();

        query.order("creatorDate");
        //query.offset(pageIndex * pageSize).limit(pageSize);

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        List<MobileGroupDoctorVO> ret = this.convertToMobile(list);
        ret=sort(ret);
        Pagination<MobileGroupDoctorVO> pagination = new Pagination(ret, (long) pageSize, pageIndex, pageSize);
        return pagination;
    }

    public List<GroupDoctor2> findByGroup(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());

        List<GroupDoctor2> list = query.asList();

        return list;
    }

    public List<GroupDoctor2> findByGroup(String groupId, String kw) {
        List<GroupDoctor2> groupDoctor2List = this.findByGroup(groupId);

        List<Integer> doctorIdList = new ArrayList<>(groupDoctor2List.size());
        for (GroupDoctor2 groupDoctor2 : groupDoctor2List) {
            doctorIdList.add(groupDoctor2.getDoctorId());
        }

        List<User> userList = user2Service.findDoctorByIds(doctorIdList, kw);
        for (GroupDoctor2 groupDoctor2 : groupDoctor2List) {
            for (User user : userList) {
                if (groupDoctor2.getDoctorId().equals(user.getUserId())) {
                    groupDoctor2.setUser(user);
                    break;
                }
            }
        }

        // 删除user为空的数据
        Iterator<GroupDoctor2> iterator = groupDoctor2List.iterator();
        while (iterator.hasNext()) {
            GroupDoctor2 groupDoctor2 = iterator.next();
            if (null == groupDoctor2.getUser()) {
                iterator.remove();
            }
        }
        return groupDoctor2List;
    }


    public List<GroupDoctor2> findNormalAndInviteDoctorByGroup(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId);
        query.or(query.criteria("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex()),
                query.criteria("status").equal(GroupEnum.GroupDoctorStatus.邀请待确认.getIndex()));

//        query.field("status").notEqual(GroupEnum.GroupDoctorStatus.正在使用.getIndex());

        List<GroupDoctor2> list = query.asList();

        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        // only need normal user
        Set<Integer> doctorIdSet = list.stream().map(o->o.getDoctorId()).collect(Collectors.toSet());
        List<Integer> normalUserIdList = user2Service.getNormalUserIdByDoctorByIds(new ArrayList<>(doctorIdSet));
        if (SdkUtils.isNotEmpty(normalUserIdList)) {
            List<GroupDoctor2> tmpList = new ArrayList<>(normalUserIdList.size());
            for (GroupDoctor2 groupDoctor2:list) {
                for (Integer userId:normalUserIdList) {
                    if (groupDoctor2.getDoctorId().equals(userId)) {
                        tmpList.add(groupDoctor2);
                        continue;
                    }
                }
            }
            return tmpList;
        }


        return null;
    }


    @Override
    public List<MobileGroupDoctorVO> findNoManagerListAndVO(Integer currentUserId, String groupId) {
        Group2 group2 = group2Service.findAndCheckDept(groupId);

        List<Integer> doctorIdList = groupUser2Service.findDoctorIdList(groupId);

        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        if (SdkUtils.isNotEmpty(doctorIdList)) {
            query.field("doctorId").notIn(doctorIdList);
        }

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        List<MobileGroupDoctorVO> ret = this.convertToMobile(list);
        return ret;
    }
    @Override
    public List<MobileGroupDoctorVO> findList(Integer currentUserId, String groupId) {
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);

        List<GroupDoctor2> list = this.findByGroup(groupId);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        List<MobileGroupDoctorVO> ret = this.convertToMobile(list);

        // 排序
        ret=sort(ret);

        return ret;
    }

    @Override
    public Pagination<MobileGroupDoctorVO> findListPage(Integer currentUserId, String groupId, String kw, Integer pageIndex, Integer pageSize) {
        if(pageIndex>=1){
            return null;
        }
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);
        Query<GroupDoctor2> query = this.createQuery();

        query.field("groupId").equal(groupId)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.order("creatorDate");
        //query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupDoctor2> list = query.asList();
        long total = query.countAll();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        Set<Integer> userIdSet = new HashSet<>();
        for (GroupDoctor2 groupDoctor2:list) {
            userIdSet.add(groupDoctor2.getDoctorId());
        }

        List<User> user2List = user2Service.findDoctorByIds(new ArrayList<>(userIdSet), kw);
        if (SdkUtils.isEmpty(user2List)) {
           return null;
        }

        Iterator<GroupDoctor2> iterator = list.iterator();
        while (iterator.hasNext()) {
            GroupDoctor2 groupDoctor2 = iterator.next();
            boolean found = false;
            for (User user2:user2List) {
                if (user2.getUserId().equals(groupDoctor2.getDoctorId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                iterator.remove();
            }
        }

        this.wrapAll(list);
//        this.wrapRole(list);
        List<MobileGroupDoctorVO> ret = this.convertToMobile(list);

        // 排序
        ret=sort(ret);

        Pagination<MobileGroupDoctorVO> page = new Pagination<>(ret, (long) pageSize, pageIndex, pageSize);

        return page;
    }


    /**
     * 排序方式：
     * 1、科室创建人并且管理员
     * 2、管理员
     * 3、普通医生
     *
     * @param list
     */
    public List<MobileGroupDoctorVO> sort(List<MobileGroupDoctorVO> list) {
        List<MobileGroupDoctorVO> listAdmin=new ArrayList<>(list.size());
        List<MobileGroupDoctorVO> listRoot=new ArrayList<>(list.size());
        List<MobileGroupDoctorVO> listNormal=new ArrayList<>(list.size());
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        Iterator<MobileGroupDoctorVO> iterator = list.iterator();

        //第一次取科室创建人并且管理员
        while (iterator.hasNext()) {
            MobileGroupDoctorVO mobileGroupDoctorVO = iterator.next();
            if (mobileGroupDoctorVO.getGroup()!=null
                    && mobileGroupDoctorVO.getGroup().getCreator()!=null
                    && mobileGroupDoctorVO.getGroup().getCreator().getUserId().equals(mobileGroupDoctorVO.getDoctorId())
                    && mobileGroupDoctorVO.getRole()!=null) {
                listAdmin.add(mobileGroupDoctorVO);
            }else if (mobileGroupDoctorVO.getGroup()!=null
                    && null!=mobileGroupDoctorVO.getRole()
                    && mobileGroupDoctorVO.getRole().equals(GroupEnum.GroupRootAdmin.admin.getIndex())) {
                listRoot.add(mobileGroupDoctorVO);
            }else {
                listNormal.add(mobileGroupDoctorVO);
            }
            iterator.remove();
        }
        List<MobileGroupDoctorVO> all=new ArrayList<>();
        all.addAll(listAdmin);
        all.addAll(listRoot);
        all.addAll(listNormal);
        return all;

        /*Collections.sort(list, new Comparator<MobileGroupDoctorVO>() {
            @Override
            public int compare(MobileGroupDoctorVO o1, MobileGroupDoctorVO o2) {

                if (o1.getGroup()!=null && o1.getGroup().getCreator().getUserId().equals(o1.getDoctorId()) && o1.getRole()!=null) {
                    return 0;
                }else if (o1.getGroup()!=null && null!=o1.getRole() && o1.getRole().equals(GroupEnum.GroupRootAdmin.admin.getIndex())) {
                    return 1;
                }else if (o1.getGroup()!=null && null!=o1.getRole() &&  o1.getRole().equals(GroupEnum.GroupRootAdmin.root.getIndex())) {
                    return 2;
                }else {
                    return 3;
                }
*//*
                if (StringUtils.isBlank(o1.getRole()) && StringUtils.isBlank(o2.getRole())) {
                    return 0;
                } if (StringUtils.isNotBlank(o1.getRole()) && StringUtils.isBlank(o2.getRole())) {
                    return 1;
                } else if (StringUtils.isBlank(o1.getRole()) && StringUtils.isNotBlank(o2.getRole())) {
                    return -1;
                }*//*

            }
        });*/
    }

    protected void wrapAll(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return;
        }
        this.wrapUser(groupDoctor);
        this.wrapGroup(groupDoctor);
    }

    protected void wrapAll(List<GroupDoctor2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapGroup(list);
        this.wrapUser(list);
        this.wrapRole(list);
    }

    protected void wrapGroup(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return;
        }
        if (null != groupDoctor.getGroup()) {
            return;
        }

        Group2 group2 = group2Service.findById(groupDoctor.getGroupId());
        groupDoctor.setGroup(group2);
    }

    protected void wrapGroup(List<GroupDoctor2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<String>(list.size());
        for (GroupDoctor2 groupDoctor2:list) {
            groupIdSet.add(groupDoctor2.getGroupId());
        }

        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (GroupDoctor2 groupDoctor2:list) {
            for (Group2 group2:group2List) {
                if (groupDoctor2.getGroupId().equals(group2.getId().toString())) {
                    groupDoctor2.setGroup(group2);
                    break;
                }
            }
        }
    }

    protected void wrapUser(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return;
        }
        if (null != groupDoctor.getUser()) {
            return;
        }
        User user = user2Service.findById(groupDoctor.getDoctorId());
        groupDoctor.setUser(user);
    }

    protected void wrapUser(List<GroupDoctor2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> doctorIdSet = new HashSet<>(list.size());
        for (GroupDoctor2 gd : list) {
            doctorIdSet.add(gd.getDoctorId());
        }

        List<User> userList = user2Service.findByIds(new ArrayList<>(doctorIdSet));
        Map<Integer, Group2> deptMap = this.findDeptMapByDoctor(new ArrayList<>(doctorIdSet));
        for (GroupDoctor2 groupDoctor2 : list) {
            for (User user : userList) {
                if (groupDoctor2.getDoctorId().equals(user.getUserId())) {
                    groupDoctor2.setUser(user);
                    groupDoctor2.setUserDept(deptMap.get(user.getUserId()));
                    break;
                }
            }
        }
    }

    protected void wrapRole(List<GroupDoctor2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        String groupId = null;
        for (GroupDoctor2 gd : list) {
            groupId = gd.getGroupId();
            break;
        }

        List<GroupUser2> groupUserList = groupUser2Service.findList(groupId);
        if (SdkUtils.isEmpty(groupUserList)) {
            return;
        }

        for (GroupDoctor2 groupDoctor2 : list) {
            for (GroupUser2 groupUser2 : groupUserList) {
                if (groupUser2.getDoctorId().equals(groupDoctor2.getDoctorId())) {
                    if(StringUtils.isEmpty(groupUser2.getRootAdmin())){
                        groupDoctor2.setRole("admin");
                    }else {
                        groupDoctor2.setRole(groupUser2.getRootAdmin());
                    }
                    break;
                }
            }
        }
    }

    protected List<MobileGroupDoctorVO> convertToMobile(List<GroupDoctor2> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupDoctorVO> ret = new ArrayList<>(list.size());
        for (GroupDoctor2 groupDoctor2 : list) {
            ret.add(new MobileGroupDoctorVO(groupDoctor2));
        }
        return ret;
    }

    protected MobileGroupDoctorVO convertToMobile(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return null;
        }
        MobileGroupDoctorVO ret = new MobileGroupDoctorVO(groupDoctor);
        return ret;
    }


    @Override
    public List<MobileGroupDoctorVO> findRecInviteList(Integer currentUserId, String groupId) {
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);
        /*List<GroupDoctor2> groupDoctor2List = this.findNormalAndInviteDoctorByGroup(groupId);  // 获取正在使用的正常医生列表
        if(SdkUtils.isEmpty(groupDoctor2List)){
            groupDoctor2List=new ArrayList<>();
        }*/
        //List<Integer> existsDoctorId = groupDoctor2List.stream().map(o->o.getDoctorId()).collect(Collectors.toList());

        List<Integer> existsDoctorId2 = this.findDoctorIdListByGroup(groupId); //这个组织正在使用的医生id

        List<User> userList = user2Service.findByHospitalAndDept(group2.getHospitalId(), group2.getDeptId(), existsDoctorId2); //在这个医院这个组织下的注册用户 排除existsDoctorId2

        List<GroupDoctor2> groupDoctor2List=new ArrayList<>(userList.size());

        // 排序：没有加入科室的医生显示在最前面，已经有科室的医生显示在下面
        if (SdkUtils.isNotEmpty(userList)) {
            for (User user : userList) {
                /*if (existsDoctorId.contains(user.getUserId())) {  //筛出 已经在改组织的人
                    continue;
                }*/
                GroupDoctor2 tmp = new GroupDoctor2(groupId, user);
                groupDoctor2List.add(tmp);
            }
        }

        //
        this.wrapUser(groupDoctor2List);
        //优先显示 已经加入的科室 没加入科室 显示账号录入科室信息
        if(SdkUtils.isNotEmpty(groupDoctor2List)){
            Set<Integer> setId=new HashSet<>();
            for (GroupDoctor2 groupDoctor2:groupDoctor2List){
                if(null==groupDoctor2.getGroup()) {
                    setId.add(groupDoctor2.getDoctorId());
                     userList = user2Service.findByIds(new ArrayList<>(setId));
                }
                String joinDept = this.findDeptIdByDoctor(groupDoctor2.getDoctorId());
                if(joinDept!=null){ //有加入科室
                    groupDoctor2.setStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
                }else {//没有加入科室  则查询有没有在该科室存在申请记录
                    boolean inviteingByGroupAndDoctor = groupDoctorInviteService.findInviteingByGroupAndDoctor(groupId, groupDoctor2.getDoctorId());// 查询邀请表状态
                    if (inviteingByGroupAndDoctor) {
                        groupDoctor2.setStatus(GroupDoctorStatus.邀请待确认.getIndex());
                    }
                }
            }
            if (SdkUtils.isNotEmpty(userList)) {
                for (GroupDoctor2 groupDoctor2 : groupDoctor2List) {
                    for (User user : userList) {
                        if(null== groupDoctor2.getGroup() && groupDoctor2.getDoctorId().equals(user.getUserId())) {
                            Group2 group = new Group2();
                            if(null!=groupDoctor2.getUserDept()) {
                                group.setDeptName(groupDoctor2.getUserDept().getName());
                            }else {
                                group.setDeptName(user.getDoctor().getHospital() + "" + user.getDoctor().getDepartments());
                            }
                            groupDoctor2.setGroup(group);
                        }
                    }
                }
            }

        }
        List<MobileGroupDoctorVO> voList = this.convertToMobile(groupDoctor2List);
       /* if (SdkUtils.isNotEmpty(voList)) {
            for (MobileGroupDoctorVO mobileGroupDoctorVO:voList){
                if(mobileGroupDoctorVO.getGroup()==null){
                    MobileGroupVO mobileGroupVO=new MobileGroupVO();
                    mobileGroupVO.setDeptName(mobileGroupDoctorVO.getGroup().getDeptName());
                }
            }
        }*/
        voList=sortRecInvite(voList);
        return voList;
    }

    protected List<MobileGroupDoctorVO> sortRecInvite(List<MobileGroupDoctorVO> list){
        List<MobileGroupDoctorVO> sortList=new ArrayList<>();
        if(SdkUtils.isNotEmpty(list)) {
            Iterator<MobileGroupDoctorVO> iterator = list.iterator();
            while (iterator.hasNext()) {
                MobileGroupDoctorVO mobileGroupDoctorVO = iterator.next();
                if (null != mobileGroupDoctorVO.getStatus() && mobileGroupDoctorVO.getStatus().equals(GroupEnum.GroupDoctorStatus.正在使用.getIndex())) {
                    sortList.add(mobileGroupDoctorVO);
                    iterator.remove();
                }
            }
            list.addAll(sortList);
        }
        return list;
    }

    /**
     * @param currentUserId
     * @param id
     * @return
     * @see GroupDoctorServiceImpl#deleteGroupDoctor(java.lang.String...)
     */
    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupDoctor2 dbItem = this.findById(id);
        groupUser2Service.checkRootOrAdminPri(dbItem.getGroupId(), currentUserId);
        boolean ret = this.deleteById(id);
        if (ret) {
            groupUser2Service.removeByGroupDoctor(dbItem);
            try {
                // 取关公众号
                pubGroupService.delSubUser(dbItem.getDoctorId(), dbItem.getGroupId());
                businessServiceMsg.refreshCircleTab(String.valueOf(dbItem.getDoctorId()));
            } catch (HttpApiException e) {
                logger.error("取关失败 userId:{} groupId:{}",currentUserId, dbItem.getGroupId(),e);
            }
        }
        return true;
    }

    @Autowired
    protected IDepartmentDoctorDao ddDao;

    @Autowired
    protected IOnlineDao onlineDao;

    @Autowired
    protected GroupFollowService groupFollowService;

    /**
     * 普通医生退出科室
     *
     * 最后一个管理员不能退出科室
     *
     * @param currentUserId
     * @return
     * @see com.dachen.health.group.group.service.impl.GroupDoctorServiceImpl#dimissionByCorrelation
     */
    @Override
    public boolean quitDept(Integer currentUserId, String id) throws HttpApiException {
        GroupDoctor2 dbItem = this.findById(id);
        if (!dbItem.getDoctorId().equals(currentUserId)) {  // 只有自己能够退出自己
            throw new ServiceException("Forbidden");
        }
        Group2 group = this.group2Service.findAndCheckDept(dbItem.getGroupId());
        if (!GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(dbItem.getStatus())) {
            throw new ServiceException("状态不对，不能退出：" + dbItem.getStatus());
        }

        this.deleteById(id); //删除groupDoctor数据
        GroupDoctor2 finalDbItem = dbItem;
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 删除管理员记录
                    groupUser2Service.quitGroup(finalDbItem);

                    // 维护User冗余字段deptId
                    // user2Service.updateDeptId(dbItem.getDoctorId(), "");

                    // 取消关注
                    groupFollowService.removeByGroup(finalDbItem.getDoctorId(), finalDbItem.getGroupId());

                    Integer doctorId = finalDbItem.getDoctorId();
                    String groupId = finalDbItem.getGroupId();

                     /* 删除所有科室与医生的关联信息 */
                    ddDao.deleteAllCorrelation(groupId, doctorId);
                      /* 删除所有值班信息 */
                    onlineDao.deleteAllByDoctorData(groupId, doctorId);

                    /**
                     * 发送离职指令
                     */
                    businessMsgService.deleteGroupNotify(doctorId, groupId);

                    // 离职医生集团删除抽成关系
                    groupProfitService.delete(doctorId, groupId);
                } catch (Exception e) {
                    logger.error("退出科室异常了! {}", ToStringBuilder.reflectionToString(finalDbItem),e.getMessage());
                }
            }
        });


        return true;
    }

    @Override
    public void checkNormal(GroupDoctor2 groupDoctor) {
        if (!GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor.getStatus())) {
            throw new ServiceException("状态不正常");
        }
    }

    @Override
    public void checkInvitePri(Group2 group, Integer doctorId) {
        String groupId = group.getId().toString();
        GroupUser2 groupUser2 = groupUser2Service.findByUK(groupId, doctorId);
        if (null == groupUser2) {
            if (null == group.getConfig() || !group.getConfig().isMemberInvite()) {
                throw new ServiceException("您没有邀请权限");
            }
        }
    }

    @Autowired
    protected IGroupDoctorService gdocService;


    @Autowired
    protected ImService imService;

    @Deprecated
    protected void inviteSendIMMsgToDoctor(User currentUser, GroupDoctor2 groupDoctor2) {
        this.wrapAll(groupDoctor2);

        Group2 group = groupDoctor2.getGroup();

        String title = String.format("加入%s", GroupEnum.GroupType.eval(group.getType()).getTitle());
        String content = String.format("%s邀请您加入%s", currentUser.getName(), group.getName());
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupInvite.getId());
        params.put("bizId", groupDoctor2.getId().toString());
        ImgTextMsg imgTextMsg=new ImgTextMsg();
        imgTextMsg.setStyle(6);
        imgTextMsg.setPic(getDefaultGroupInviteLogoPicUrl());
        imService.sendTodoNotifyMsg(groupDoctor2.getDoctorId(), title, content, null, params, imgTextMsg);
    }

    @Deprecated
    protected String getDefaultGroupInviteLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.invite.pic"));
        return pic;
    }
    @Deprecated
    protected void applySendIMMsgToManagers(GroupDoctor2 groupDoctor) throws HttpApiException {
        List<Integer> managerIdList = groupUser2Service.findDoctorIdList(groupDoctor.getGroupId());
        if (SdkUtils.isEmpty(managerIdList)) {
            return;
        }

        this.wrapAll(groupDoctor);

        Group2 group = groupDoctor.getGroup();
        User user = groupDoctor.getUser();

        for (Integer mangerId : managerIdList) {  // 通知所有的管理员
            String title = String.format("加入%s", GroupEnum.GroupType.eval(group.getType()).getTitle());
            String msg;
            if(groupDoctor.getApplyMsg()==null){
                msg="";
            }else {
                msg=groupDoctor.getApplyMsg();
            }
            String content = String.format("%s申请加入%s：%s", user.getName(), group.getName(), msg);
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupApply.getId());
            params.put("bizId", groupDoctor.getId().toString());
            ImgTextMsg imgTextMsg=new ImgTextMsg();
            imgTextMsg.setStyle(6);
            //+ GroupEnum.GroupType.eval(group.getType()).getTitle()
            imgTextMsg.setPic(getDefaultGroupCheckLogoPicUrl());
            imService.sendTodoNotifyMsg(mangerId, title, content, null, params, imgTextMsg);
            /**
             * 给科室管理员发送短信
             */
            //{医生名称}申请加入{科室名称}，点击立即查看{打开医生圈APP链接}
            /*String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
            /**修改成从应用宝获取应用**/
            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
//            String shortLink = ShortUrlUtil.generateShortUrl(shareLink);
//            User doctor = userManager.getUser(pack.getDoctorId());
            final String smsContent = baseDataService.toContent("1052", user.getName(), group.getName(), generateUrl);
//            smsManager.sendSMS(param.getTelephone(), smsContent);
            User manager = userManager.getUser(mangerId);
            mobSmsSdk.send(manager.getTelephone(), smsContent);
        }
    }
    protected String getDefaultGroupCheckLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.check.pic"));
        return pic;
    }
    @Resource
    protected MobSmsSdk mobSmsSdk;

    /**
     * 发送短信邀请信息
     */
    @Deprecated
    protected void sendInviteSms(GroupDoctor2 groupDoctor) throws HttpApiException {
        this.wrapAll(groupDoctor);

        String sms = this.getInviteTpl(3, groupDoctor.getId().toString(), groupDoctor.getGroup(), groupDoctor.getUser());
        mobSmsSdk.send(groupDoctor.getUser().getTelephone(), sms, BaseConstants.XG_YSQ_APP);
    }

    /**
     * 获取邀请短链
     */
    @Deprecated
    protected String getInviteTpl(Integer type, String id, Group2 group, User doctorUser) throws HttpApiException {
        String tpl = null;
        String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");

        if (GroupEnum.GroupType.hospital.getIndex().equals(group.getType())) {
            url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalJoin");
        }

        final String doctorName = doctorUser.getName() == null ? "" : doctorUser.getName();
        url = url + "?id=" + id + "&doctorName=" + doctorName;

        String unitName = "";
        String opName = "";
        String doc = BaseConstants.XG_YSQ_APP;
        if (2 == type) {
            url = url + "&type=2" + "&name=" + group.getName();
            unitName = group.getName();
            opName = String.format("成为%s管理员", GroupEnum.GroupType.eval(group.getType()).getTitle());
        } else if (3 == type) {
            url = url + "&type=3" + "&name=" + group.getName();
            unitName = group.getName();
            opName = "加入科室";
        }
        tpl = baseDataService.toContent("0002", doctorName, unitName, opName, shortUrlComponent.generateShortUrl(url), doc);

        return tpl;
    }

    @Resource
    protected IBaseDataService baseDataService;

    /**
     * 获取邀请链接
     */
    @Deprecated
    protected String getInviteUrl(Integer type, String id, Group2 group, User doctorUser) throws HttpApiException {
        String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");

        if (GroupEnum.GroupType.hospital.getIndex().equals(group.getType())) {
            url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalJoin");
        }

        url = url + "?id=" + id + "&doctorName=" + (doctorUser.getName() == null ? "" : doctorUser.getName());

        if (2 == type) {
            url = url + "&type=2" + "&name=" + group.getName();
        }
        if (3 == type) {
            url = url + "&type=3" + "&name=" + group.getName();
        }
        return shortUrlComponent.generateShortUrl(url);
    }

    @Autowired
    protected IGroupService groupService;

    @Deprecated
    protected GroupDoctor2 doApplyAdd(Integer currentUserId, Group2 group2, Integer doctorId, GroupEnum.GroupDoctorStatus status, String applyMsg) {
        long now = System.currentTimeMillis();
        GroupDoctor2 tmp = new GroupDoctor2();
        tmp.setGroupId(group2.getId().toString());
        tmp.setType(group2.getType());

        tmp.setDoctorId(doctorId);

        tmp.setApplyMsg(applyMsg);
        tmp.setStatus(status.getIndex());
        tmp.setReferenceId(currentUserId);
        if (currentUserId.equals(doctorId)) { // 自己的时候不需要
            tmp.setReferenceId(0);
        }
        tmp.setCreator(currentUserId);
        tmp.setCreatorDate(now);
//        gdoc.setUpdator(currentUserId);
//        gdoc.setUpdatorDate(now);

        GroupDoctor2 groupDoctor2 = this.saveEntityAndFind(tmp);
        return groupDoctor2;
    }

    protected GroupDoctor2 doAdd(Integer currentUserId, Group2 group2, Integer doctorId, GroupEnum.GroupDoctorStatus status) {
        return this.doApplyAdd(currentUserId, group2, doctorId, status, null);
    }


    @Override
    public GroupDoctor2 addByCreateDept(Integer currentUserId, Group2 group2, Integer doctorId) {
        GroupDoctor2 groupDoctor2 = this.doAdd(currentUserId, group2, doctorId, GroupEnum.GroupDoctorStatus.正在使用);
        // 将当前医生设置为超级超级管理员
        groupUser2Service.addByCreateDept(currentUserId, group2, doctorId);
        return groupDoctor2;
    }

    @Override
    public GroupDoctor2 addByApply(Integer currentUserId, GroupDoctorApply apply) {
        this.checkCurDeptIdWhenJoinDept(apply.getUserId());
        this.checkNormalByUK(apply.getGroupId(), apply.getUserId());
        GroupDoctor2 groupDoctor2 = this.doApplyAdd(currentUserId, apply.getGroup(), apply.getUserId(), GroupEnum.GroupDoctorStatus.正在使用, apply.getMsg());

        afterAddByApplyOrInviteAsync(groupDoctor2);

        return groupDoctor2;
    }

    @Override
    public GroupDoctor2 addByInvite(Integer currentUserId, GroupDoctorInvite invite) {
        this.checkCurDeptIdWhenJoinDept(invite.getUserId());
        this.checkNormalByUK(invite.getGroupId(), invite.getUserId());
        GroupDoctor2 groupDoctor2 = this.doAdd(currentUserId, invite.getGroup(), invite.getUserId(), GroupEnum.GroupDoctorStatus.正在使用);

        afterAddByApplyOrInviteAsync(groupDoctor2);

        return groupDoctor2;
    }

    protected void afterAddByApplyOrInviteAsync(GroupDoctor2 groupDoctor2) {
        if (null == groupDoctor2) {
            return;
        }

        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    afterAddByApplyOrInvite(groupDoctor2);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected void afterAddByApplyOrInvite(GroupDoctor2 dbItem) throws HttpApiException {
        if (null == dbItem) {
            return;
        }
        this.wrapAll(dbItem);
        Integer fromUserId = dbItem.getReferenceId();
        Group2 group = dbItem.getGroup();
        User user = dbItem.getUser();
        if(group.getType().equals(GroupEnum.GroupType.group.getIndex()) && StringUtils.isNotEmpty(group.getGid())) {
            imService.joinGroupIM(group.getGid(), fromUserId, dbItem.getDoctorId(), user.getName());
        }else if(group.getType().equals(GroupEnum.GroupType.dept.getIndex()) ) {
            // 当前用户订阅公众号
            groupFollowService.add(dbItem.getDoctorId(), group.getId().toString());
        }
    }

    @Override
    public Map<String, Integer> countByGroupList(List<String> groupIdList) {
        if (SdkUtils.isEmpty(groupIdList)) {
            return new HashMap<>(0);
        }
        DBObject matchFields = new BasicDBObject();
        matchFields.put("groupId", new BasicDBObject("$in", groupIdList));
        matchFields.put("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());

        DBObject match = new BasicDBObject();
        match.put("$match", matchFields);

        DBObject _group = new BasicDBObject("groupId", "$groupId");

        DBObject groupFields = new BasicDBObject();
        groupFields.put("_id", _group);
        groupFields.put("count", new BasicDBObject("$sum", 1));

        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = new ArrayList<>(2);
        pipeline.add(match);
        pipeline.add(group);
        AggregationOutput output = null;
        try {
            output = dsForRW.getDB().getCollection("c_group_doctor").aggregate(pipeline);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (output != null) {
            Map<String, Integer> ret = new HashMap<>();
            Iterator<DBObject> it = output.results().iterator();
            while (it.hasNext()) {
                DBObject obj = it.next();
                Map mapObj = (Map) obj.get("_id");
                String groupId = (String) mapObj.get("groupId");
                Integer count = MongodbUtil.getInteger(obj, "count");
                ret.put(groupId, count);
            }
            return ret;
        }
        return new HashMap<>(0);
    }

    @Override
    public Map<String, Integer> countTotalExpertByGroupList(List<String> groupIdList) {
        Map<String, Integer> map = new HashedMap(groupIdList.size());
        for (String groupId:groupIdList) {
            List<Integer> doctorIdList = this.findDoctorIdListByGroup(groupId);
            int count = 0;
            if (SdkUtils.isEmpty(doctorIdList)) {
            } else {
                count = user2Service.countExpert(doctorIdList);
            }
            map.put(groupId, count);
        }
        return map;
    }

    @Override
    public Integer countByGroup(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId).field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        int count = (int) query.countAll();
        return count;
    }

    @Deprecated
    public GroupDoctor2 findMyDept(Integer currentUserId) {
        Integer doctorId = currentUserId;
        Query<GroupDoctor2> query = this.createQuery();
        query.field("doctorId").equal(doctorId)
                .field("type").equal(GroupEnum.GroupType.dept.getIndex())
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        GroupDoctor2 groupDoctor2 = query.get();
        this.wrapAll(groupDoctor2);
        return groupDoctor2;
    }

    @Override
    public int deleteByGroup(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId);
        return this.deleteByQuery(query);
    }

    @Override
    @Deprecated
    public MobileGroupDoctorVO findApplyDetailByIdAndVO(Integer currentUserId, String id) {
        GroupDoctor2 dbItem = this.findFullAndCheckById(id);
        MobileGroupDoctorVO vo = this.convertToMobile(dbItem);
        // 刘琦申请加入大辰中西医眼科
        vo.setMsg(dbItem.getApplyMsg());
        return vo;
    }

    @Override
    @Deprecated
    public MobileGroupDoctorVO findInviteDetailByIdAndVO(Integer currentUserId, String id) {
        GroupDoctor2 dbItem = this.findFullAndCheckById(id);
        MobileGroupDoctorVO vo = this.convertToMobile(dbItem);
        // 刘琦邀请您加入大辰中西医结合眼科
        User inviteUser = user2Service.findDoctorById(dbItem.getReferenceId());
        vo.setMsg(String.format("%s邀请您加入%s", inviteUser.getName(), dbItem.getGroup().getName()));
        return vo;
    }

    @Override
    public int deleteByDept(String groupId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId);
        int ret = this.deleteByQuery(query);
//        user2Service.updateDeptIdNull(groupId);
        return ret;
    }

    Integer getFromUserId(Group2 group, GroupUser2 groupUser) {
        Integer fromUserId = null;
        if (Objects.nonNull(groupUser)) {
            fromUserId = groupUser.getDoctorId();
        } else {
            fromUserId = group.getCreator();
        }
        return fromUserId;
    }

    @Override
    public Map<String,Object> ifFriendAndInfo(Integer userId,Integer toUserId){
        String tag = "ifFriendAndInfo";
        logger.info("{}. userId={}, toUserId={}", tag, userId, toUserId);

        Map<String, Object> result = new HashedMap();
        boolean b = relationService.ifDoctorFriend(userId, toUserId); //true 好友 false 不是好友
        logger.info("{}. b={}", tag, b);

        result.put("ifFriend", b);

         String circleIds =remoteSysManagerUtil.postForObject(CIRCLE_SAMECIRCLE_ACTION,userId,toUserId);

        if(StringUtil.isNotEmpty(circleIds) && !circleIds.equals("null")) {
            String[] circleId = circleIds.split(",");
            List<String> circleIdList = Arrays.asList(circleId);
            if(SdkUtils.isNotEmpty(circleIdList)) {
                result.put("groupId", circleIdList.get(0));
            }
        }

        return result;
    }

    @Deprecated
    protected void inviteAcceptSendIMNotifyToManagers(GroupDoctor2 groupDoctor) throws HttpApiException {
        String tag = "inviteAcceptSendIMNotifyToManagers";
        logger.info("{}. groupDoctor={}", tag, groupDoctor);

        this.wrapAll(groupDoctor);

        // 查询集团管理员列表
        List<GroupUser2> groupUserList = groupUser2Service.findList(groupDoctor.getGroupId());
        if (SdkUtils.isEmpty(groupUserList)) {
            return;
        }

        logger.info("{}. groupUserList.size={}", tag, groupUserList.size());

        //初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        //+ GroupEnum.GroupType.eval(group.getType()).getTitle()
        textMsg.setTitle("加入科室");
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", groupDoctor.getId().toString());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        String managerContent = "";
        String inviteManagerContent = "";
        User user = groupDoctor.getUser();
        Group2 group = groupDoctor.getGroup();
        if (StringUtils.isNotEmpty(user.getName())) {
            managerContent = user.getName() + "医生已加入" + group.getName();
            inviteManagerContent = user.getName() + "医生已接受您的邀请，加入" + group.getName();
        } else {
            managerContent = user.getTelephone() + "医生已加入" + group.getName();
            inviteManagerContent = user.getTelephone() + "医生已接受您的邀请，加入" + group.getName();
        }

        for (GroupUser2 guser : groupUserList) {
            if (null != groupDoctor.getReferenceId() && guser.getDoctorId().equals(groupDoctor.getReferenceId())) {
                textMsg.setContent(inviteManagerContent);
            } else {
                textMsg.setContent(managerContent);
            }
            logger.info("{}. userId: {} textMsg: {}", tag, guser.getDoctorId(), JSONUtil.toJSONString(textMsg));
            businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        }
    }

    @Deprecated
    protected void applyAcceptSendIMMsgToManagers(Integer currentUserId, GroupDoctor2 groupDoctor) throws HttpApiException {
        String tag = "applyAcceptSendIMMsgToManagers";
        logger.info("{}. groupDoctor={}", tag, groupDoctor);

        this.wrapAll(groupDoctor);

        // 查询集团管理员列表
        List<GroupUser2> groupUserList = groupUser2Service.findList(groupDoctor.getGroupId());
        if (SdkUtils.isEmpty(groupUserList)) {
            return;
        }

        logger.info("{}. groupUserList.size={}", tag, groupUserList.size());

        //初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        //+ GroupEnum.GroupType.eval(group.getType()).getTitle()
        //textMsg.setPic("http://group.dev.file.dachentech.com.cn/o_1bivr073114hl17i6q431vggspdl?100");
        textMsg.setTitle("加入科室");
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", groupDoctor.getId().toString());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        String managerContent = "";
        User user = groupDoctor.getUser();
        Group2 group = groupDoctor.getGroup();
        if (StringUtils.isNotEmpty(user.getName())) {
            managerContent = user.getName() + "医生已加入" + group.getName();
        } else {
            managerContent = user.getTelephone() + "医生已加入" + group.getName();
        }
        textMsg.setContent(managerContent);

        for (GroupUser2 guser : groupUserList) {
            logger.info("{}. userId: {} textMsg: {}", tag, guser.getDoctorId(), JSONUtil.toJSONString(textMsg));
            businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        }
    }

    protected void inviteRefuseSendIMMsg(GroupDoctor2 groupDoctor) throws HttpApiException {
        String tag = "inviteRefuseSendIMMsg";
        logger.info("{}. groupDoctor={}", tag, groupDoctor);

        this.wrapAll(groupDoctor);

        Group2 group = groupDoctor.getGroup();
        User user = groupDoctor.getUser();

        String content = user.getName() + "医生已拒绝您的邀请，未加入" + group.getName();

        // 初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        textMsg.setContent(content);
        textMsg.setTitle("加入" + GroupEnum.GroupType.eval(group.getType()).getTitle());
        //textMsg.setFooter("查看详情");

        // 设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", groupDoctor.getId().toString());
        textMsg.setParam(imParam);

        // 查询集团管理员列表
//        List<GroupUser2> groupUserList = groupUser2Service.findList(group.getId().toString());
//        if (SdkUtils.isNotEmpty(groupUserList)) {
//            for (GroupUser2 guser : groupUserList) {
//                if (guser.getDoctorId().equals(groupDoctor.getReferenceId())) {
//                    textMsg.setFooter("查看医生");
//                }
//            }
//        }
        mpt.add(textMsg);

        businessServiceMsg.sendTextMsg(groupDoctor.getReferenceId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    protected void applyRefuseSendIMMsg(Integer currentUserId, GroupDoctor2 groupDoctor) throws HttpApiException {
        String tag = "applyRefuseSendIMMsg";
        logger.info("{}. currentUserId={}, groupDoctor={}", tag, currentUserId, groupDoctor);

        this.wrapAll(groupDoctor);

        User currentUser = user2Service.findDoctorById(currentUserId);

        Group2 group = groupDoctor.getGroup();
        User user = groupDoctor.getUser();

        String content = currentUser.getName() + "医生已拒绝您的申请，未加入" + group.getName();

        // 初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        textMsg.setContent(content);
        textMsg.setTitle("加入" + GroupEnum.GroupType.eval(group.getType()).getTitle());

        // 设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", groupDoctor.getId().toString());
        textMsg.setParam(imParam);

        // 查询集团管理员列表
//        List<GroupUser2> groupUserList = groupUser2Service.findList(group.getId().toString());
//        if (SdkUtils.isNotEmpty(groupUserList)) {
//            for (GroupUser2 guser : groupUserList) {
//                if (guser.getDoctorId().equals(groupDoctor.getReferenceId())) {
//                    textMsg.setFooter("查看医生");
//                }
//            }
//        }
        mpt.add(textMsg);

        businessServiceMsg.sendTextMsg(groupDoctor.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    /**
     * 医生加入医生集团的相关数据初始化
     */
    private void initDataForDoctorJoinGroup(GroupDoctor2 groupDoctor2) throws HttpApiException {

//        // 将医生相关文章加入集团
//        articleService.addDTG(String.valueOf(doctorId), groupId);
//        //就医知识加入集团
//        knowledgeService.addDoctorToGroup(String.valueOf(doctorId), groupId);

        // 初始化医生与集团的抽成比例
        groupProfitService.initProfitByJoinGroup(groupDoctor2.getDoctorId(), groupDoctor2.getGroupId(), groupDoctor2.getReferenceId());

//        // 适配医生的套餐价格，在集团套餐价格范围内
//        List<Integer> doctorIds = new ArrayList<Integer>();
//        doctorIds.add(doctorId);
//        packService.executeFeeUpdate(groupId, doctorIds);
//
//        //如果是加入医院，则需要将医生所在的科室加入到医院中，并将该医生加入这个科室
//        initGroupHospitalDepartment(doctorId, groupId);

        // 发送指令，通知移动端刷新通讯录
        businessMsgService.addGroupNotify(groupDoctor2.getDoctorId(), groupDoctor2.getGroupId());

//        //更新groupdoctor中的createDate为当前时间
//        gdocDao.updateCreateDate(groupId, doctorId);

//        try {
//            User doc = userManager.getUser(doctorId);
//            if (doc.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
//                Group g = groupDao.getById(groupId);
//                if ((g != null)
//                        && !GroupEnum.GroupActive.active.getIndex().equals(g.getActive())
//                        && GroupEnum.GroupType.group.getIndex().equals(g.getType())) {
//                    // 判断集团的人数并激活集团
//                    groupDoctorService.activeGroupByMemberNum(groupId);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("error cause by ", e);
//        }
    }

    /**
     * 计算 groupDoctor的 treepath 和 reference
     *
     * @param groupDoctor2Temp
     * @param doctorId
     * @param inviteId
     * @param groupId
     * @return
     */
    protected void updateTreePath(GroupDoctor2 groupDoctor2Temp, Integer doctorId, Integer inviteId, String groupId) {
        if (doctorId.equals(inviteId)) { // 自己是邀请人
            groupDoctor2Temp.setReferenceId(0);
            groupDoctor2Temp.setTreePath("/" + doctorId);
            return;
        }

        // 查询邀请人是否在集团中
        GroupDoctor2 docInvite = this.findByUK(groupId, inviteId);
        if (null == docInvite) { // 邀请人不在本集团中
            groupDoctor2Temp.setReferenceId(0);
            groupDoctor2Temp.setTreePath("/" + doctorId);
            return;
        }

        // 邀请人在本集团中
        groupDoctor2Temp.setReferenceId(inviteId);
        groupDoctor2Temp.setTreePath(docInvite.getTreePath() + "/" + inviteId);
    }

    public List<GroupDoctor2> findMainGroupByDoctorId(Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.filter("doctorId", doctorId);
        query.filter("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.filter("isMain", true);
        return query.asList();
    }

    /**
     * 设置主集团
     */
    protected void updateMainGroup(GroupDoctor2 groupDoctor2) {
        if (!GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor2.getStatus())) {
            return;
        }

        List<GroupDoctor2> mainGroupList = this.findMainGroupByDoctorId(groupDoctor2.getDoctorId());
        if (SdkUtils.isEmpty(mainGroupList)) {
            groupDoctor2.setMain(true); // 第一个时是true
            return;
        }

        List<String> skipGroupIds = group2Service.getSkipGroupIds();
        if (SdkUtils.isEmpty(skipGroupIds)) {
            groupDoctor2.setMain(true);
            return;
        }

        boolean foundSkip = false;
        for (GroupDoctor2 groupDoctor : mainGroupList) {
            if (skipGroupIds.contains(groupDoctor.getGroupId())) {
                foundSkip = true;
                groupDoctor.setMain(false);
            }
        }

        if (foundSkip) {
            this.saveEntityBatch(mainGroupList);
            groupDoctor2.setMain(true);
        } else {
            groupDoctor2.setMain(false);
        }
    }

    @Resource
    protected IGroupProfitService groupProfitService;

    @Autowired
    protected IBusinessServiceMsg businessMsgService;

    @Override
    public GroupDoctor2 findByUK(String groupId, Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId).field("doctorId").equal(doctorId);
        return query.get();
    }

    @Override
    public GroupDoctor2 findNormalByUK(String groupId, Integer doctorId) {
        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").equal(groupId).field("doctorId").equal(doctorId);
        query.field("status").equal(GroupDoctorStatus.正在使用.getIndex());
        return query.get();
    }

    @Override
    public void checkNormalByUK(String groupId, Integer doctorId) {
        GroupDoctor2 groupDoctor2 = this.findNormalByUK(groupId, doctorId);
        if (null != groupDoctor2) {
            throw new ServiceException("您已经是成员");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        super.afterPropertiesSet();
//        fixDataUserDeptIdAsync();
    }

//    protected void fixDataUserDeptIdAsync() {
//        this.asyncTaskPool.getPool().submit(new Runnable() {
//            @Override
//            public void run() {
//                fixDataUserDeptId();
//            }
//        });
//    }

//    protected void fixDataUserDeptId() {
//        String tag = "fixDataUserDeptId";
//        Query<GroupDoctor2> query = this.createQuery();
//        query.field("type").equal(GroupEnum.GroupType.dept.getIndex());
//        List<GroupDoctor2> list = query.asList();
//        if (SdkUtils.isEmpty(list)) {
//            logger.debug("{}. list empty.", tag);
//            return;
//        }
//        this.wrapUser(list);
//        for (GroupDoctor2 groupDoctor2:list) {
//            if (StringUtils.isNotBlank(groupDoctor2.getUser().getDeptId())) {
//                if (!groupDoctor2.getUser().getDeptId().equals(groupDoctor2.getGroupId())) {
//                    throw new ServiceException("不一致" + groupDoctor2.getId().toString());
//                }
//            }
//            user2Service.updateDeptId(groupDoctor2.getDoctorId(), groupDoctor2.getGroupId());
//        }
//        logger.debug("{}. list size={}", tag, list.size());
//    }


    @Override
    public Pagination<Integer> findDoctorIdListByGroupAndUnionsPage(List<String> groupIdList, List<String> unionIdList, Integer pageIndex, Integer pageSize) {
        List<String> idList = new ArrayList<>();
        if (SdkUtils.isNotEmpty(groupIdList)) {
            idList.addAll(groupIdList);
        }
        if (SdkUtils.isNotEmpty(unionIdList)) {
            List<String> unionGroupIdList = groupUnionMemberService.findGroupIdsByUnions(unionIdList);
            if (SdkUtils.isNotEmpty(unionGroupIdList)) {
                idList.addAll(unionGroupIdList);
            }
        }

        if (SdkUtils.isEmpty(idList)) {
            return null;
        }

        Query<GroupDoctor2> query = this.createQuery();
        query.field("groupId").in(idList)
                .field("status").equal(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        query.order("creatorDate");
        long total = query.countAll();

        query.offset(pageIndex * pageSize).limit(pageSize);
        query.retrievedFields(true, "doctorId");

        List<GroupDoctor2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<Integer> doctorIdList = list.stream().map(o -> o.getDoctorId()).collect(Collectors.toList());

        Pagination<Integer> pagination = new Pagination(doctorIdList, total, pageIndex, pageSize);
        return pagination;
    }

    @Override
    public void closeApplyAndInviteAsync(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                closeApplyAndInvite(groupDoctor);
            }
        });
    }

    public void closeApplyAndInvite(GroupDoctor2 groupDoctor) {
        if (null == groupDoctor) {
            return;
        }

        groupDoctorApplyService.closeByGroupDoctor(groupDoctor);
        groupDoctorInviteService.closeByGroupDoctor(groupDoctor);
    }

    @Override
    public int updateNormalById(String id) {
        Query<GroupDoctor2> query = this.createQueryByPK(id);
        UpdateOperations<GroupDoctor2> ops = this.createUpdateOperations();
        ops.set("status",GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        return this.update(query, ops);
    }

    /**
     * 医生主页好友状态  5种   (同一个圈子指 在同一个科室 或者 圈子 或者 医联体)
     * 1、已添加到好友
     * 2、同一个圈子但是没有添加到好友
     * 3、没有在同一个圈子没有添加到好友
     * 4、等待验证
     * 5、接受
     */
    @Override
    public Map<String,String> getBothFriendStatus(Integer userId, Integer toUserId) {
        String tag = "getBothFriendStatus";
        logger.info("{}. userId={}, toUserId={}", tag, userId, toUserId);
        Map<String,String> map=new HashedMap();
        /*boolean ifCircle = false;
        List<String> userGroupList = this.findGroupIdListByDoctor(userId);
        List<String> toUsergroupList = this.findGroupIdListByDoctor(toUserId);

        logger.info("{}. userGroupList={}", tag, userGroupList);
        logger.info("{}. toUsergroupList={}", tag, toUsergroupList);

        if (SdkUtils.isNotEmpty(userGroupList) && SdkUtils.isNotEmpty(toUsergroupList)) {
            userGroupList.retainAll(toUsergroupList);
            logger.info("{}. after retainAll. userUnion={}", tag, userGroupList);
            if (SdkUtils.isNotEmpty(userGroupList)) { //在同一个圈子
                ifCircle = true;
            }
            if (SdkUtils.isNotEmpty(userGroupList)) {
                List<String> userUnion = groupUnionMemberService.findUnionIdsByGroups(userGroupList);
                List<String> toUserUnion = groupUnionMemberService.findUnionIdsByGroups(toUsergroupList);
                if (SdkUtils.isNotEmpty(userUnion) && SdkUtils.isNotEmpty(toUserUnion)) {
                    //获取 同一个医联体id
                    userUnion.retainAll(toUserUnion);
                    logger.info("{}. after retainAll. userUnion={}", tag, userUnion);
                    if (SdkUtils.isNotEmpty(userUnion)) { //在同一个圈子
                        ifCircle = true;
                    }
                }
            }
        }*/

        boolean b = relationService.ifDoctorFriend(userId, toUserId); //true 好友 false 不是好友
        logger.info("{}. b={}", tag, b);
        if (b) {
                map.put("status","1");
                return map;    //已添加到同行好友
        } else {
            //不是好友先判断是否 等待验证 或者 接受
            FriendReq friendReq = iFriendReqDao.getWaitAcceptFriendReq(userId, toUserId);
            logger.info("{}. friendReq={}", tag, friendReq);
            if (friendReq != null) {
                map.put("status","4");
                return map;//等待验证
            }
            FriendReq friendReq1 = iFriendReqDao.getWaitAcceptFriendReq(toUserId, userId);
            logger.info("{}. friendReq1={}", tag, friendReq1);
            if (friendReq1 != null) {
                map.put("status","5");
                map.put("fId",friendReq1.getId().toString());
                return map;//待接受
            }

            boolean ifCircle = Boolean.parseBoolean(remoteSysManagerUtil.postForObject(CIRCLE_IFSAMECIRCLE_ACTION,userId,toUserId));

            if (ifCircle) {
                map.put("status","2");
                return map;   //同一个圈子但是没有添加到好友
            } else {
                map.put("status","3");
                return map;  //没有在同一个圈子里并不是好友
            }

        }
    }


   /* @Override
    public void createWalletAcc(String token){
        List<User> normalUserList = user2Service.getNormalUserList();
        logger.info(String.valueOf(normalUserList.size())+"：初始化用户数");
        if(SdkUtils.isNotEmpty(normalUserList)) {
            for (User user:normalUserList) {
                WalletVO walletVO = new WalletVO();
                walletVO.setBusinessId(businessId);
                walletVO.setType(1);
                walletVO.setId(String.valueOf(user.getUserId()));
                walletVO.setName(user.getName());

                CreateWalletForm createWalletForm=new CreateWalletForm();
                createWalletForm.setBusinessId(businessId);
                createWalletForm.setBusinessKey(businessKey);
                createWalletForm.setWalletVO(walletVO);
                JSONMessage jsonMessage = restTemplate.postForObject("http://wallet/wallet/regWalletAccount", createWalletForm, JSONMessage.class);
                logger.info("wallet/regWalletAccount return {}",JSONUtil.toJSONString(jsonMessage));
            }
        }
    }*/
}
