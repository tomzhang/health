package com.dachen.health.circle.service.impl;


import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupUser2;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.GroupUser2Service;
import com.dachen.health.circle.vo.MobileGroupUserVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.MongodbUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Model(GroupUser2.class)
@Service
public class GroupUser2ServiceImpl extends BaseServiceImpl implements GroupUser2Service {

    @Override
    public List<GroupUser2> findList(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return null;
        }
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
        return query.asList();
    }

    @Override
    public List<Integer> findDoctorIdList(String groupId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
        query.retrievedFields(true, "doctorId");

        List<GroupUser2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<Integer> doctorIdList = list.stream().map(o -> o.getDoctorId()).collect(Collectors.toList());
        return doctorIdList;
    }

    @Override
    public List<String> findGroupIdByDoctor(Integer doctorId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("doctorId").equal(doctorId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
        query.retrievedFields(true, "objectId");

        List<GroupUser2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> doctorIdList = list.stream().map(o -> o.getObjectId()).collect(Collectors.toList());
        return doctorIdList;
    }

    public void filterHospital(List<GroupUser2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<>(list.size());
        for (GroupUser2 groupUser2:list) {
            groupIdSet.add(groupUser2.getObjectId());
        }
        List<String> groupIdList = group2Service.filterGroupOrDeptIds(new ArrayList<>(groupIdSet));

        Iterator<GroupUser2> iterator = list.iterator();
        while (iterator.hasNext()) {
            GroupUser2 groupUser2 = iterator.next();
            if (SdkUtils.isEmpty(groupIdList) || !groupIdList.contains(groupUser2.getObjectId())) {
                iterator.remove();
            }
        }
    }

    @Override
    public List<MobileGroupUserVO> findMyGroupList(Integer doctorId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("doctorId").equal(doctorId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());

        List<GroupUser2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        filterHospital(list);

        this.wrapAll(list);
        List<MobileGroupUserVO> ret = this.convertToMobileGroupUserVO(list);
        return ret;
    }

    @Override
    public Pagination<MobileGroupUserVO> findPage(String groupId, Integer pageIndex, Integer pageSize) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);

        int start = pageIndex * pageSize;
        query.offset(start).limit(pageSize);

        List<GroupUser2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        List<MobileGroupUserVO> ret = this.convertToMobileGroupUserVO(list);

        Pagination<MobileGroupUserVO> pagination = new Pagination<>(ret, query.countAll(), pageIndex, pageSize);
        return pagination;
    }

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected Group2Service group2Service;

    protected void wrapAll(List<GroupUser2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapUser(list);
        this.wrapGroup(list);
    }

    protected void wrapGroup(List<GroupUser2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<>(list.size());
        for (GroupUser2 groupUser2 : list) {
            groupIdSet.add(groupUser2.getObjectId());
        }

        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (GroupUser2 groupUser2 : list) {
            for (Group2 group : group2List) {
                if (group.getId().toString().equals(groupUser2.getObjectId())) {
                    groupUser2.setGroup(group);
                    break;
                }
            }
        }
    }

    protected void wrapUser(List<GroupUser2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupUser2 groupUser2 : list) {
            userIdSet.add(groupUser2.getDoctorId());
        }

        List<User> userList = user2Service.findDoctorByIds(new ArrayList<>(userIdSet));
        for (GroupUser2 groupUser2 : list) {
            for (User user : userList) {
                if (user.getUserId().equals(groupUser2.getDoctorId())) {
                    groupUser2.setUser(user);
                    break;
                }
            }
        }
    }

    private List<MobileGroupUserVO> convertToMobileGroupUserVO(List<GroupUser2> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupUserVO> ret = new ArrayList<>(list.size());
        for (GroupUser2 GroupUser2 : list) {
            ret.add(new MobileGroupUserVO(GroupUser2));
        }
        return ret;
    }

    @Override
    public void checkRootOrAdminPri(String groupId, Integer doctorId) {
        GroupUser2 groupUser2 = this.findByUK(groupId, doctorId);
        if (null == groupUser2) {
            throw new ServiceException("Forbidden");
        }
    }

    @Override
    public GroupUser2 findByUK(String groupId, Integer doctorId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId).field("doctorId").equal(doctorId);
//        query.order("-creatorDate");
        return query.get();
    }

    @Override
    public boolean ifMyGroup(Integer doctorId, String groupId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId).field("doctorId").equal(doctorId);
//        query.order("-creatorDate");
        GroupUser2 groupUser2 = query.get();
        return null != groupUser2;
    }

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Override
    public GroupUser2 add(Integer currentUserId, GroupDoctor2 groupDoctor) {
        groupDoctor2Service.checkNormal(groupDoctor);
        return this.add(currentUserId, groupDoctor.getGroupId(), groupDoctor.getDoctorId());
    }

    @Override
    public GroupUser2 addByCreateDept(Integer currentUserId, Group2 group2, Integer doctorId) {
        return this.doAdd(currentUserId, group2.getId().toString(), doctorId);
    }

    private GroupUser2 doAdd(Integer currentUserId, String groupId, Integer doctorId) {
        long now = System.currentTimeMillis();
        GroupUser2 groupUserTemp = new GroupUser2();
        groupUserTemp.setObjectId(groupId);
        groupUserTemp.setDoctorId(doctorId);
        groupUserTemp.setRootAdmin(GroupEnum.GroupRootAdmin.admin.getIndex());
        groupUserTemp.setType(GroupEnum.GroupUserType.集团用户.getIndex());    // type字段没什么用，科室用户也是用集团用户
        groupUserTemp.setStatus(GroupEnum.GroupUserStatus.正常使用.getIndex());
        groupUserTemp.setCreator(currentUserId);
        groupUserTemp.setCreatorDate(now);
        groupUserTemp.setUpdator(currentUserId);
        groupUserTemp.setUpdatorDate(now);
        GroupUser2 groupUser2 = this.saveEntityAndFind(groupUserTemp);
        return groupUser2;
    }

    protected GroupUser2 add(Integer currentUserId, String groupId, Integer doctorId) {
        GroupUser2 dbItem = this.findByUK(groupId, doctorId);
        if (null == dbItem) {
            dbItem = this.doAdd(currentUserId, groupId, doctorId);
        }
        return dbItem;
    }

    @Override
    public synchronized boolean removeBySelf(Integer currentUserId, String id) {
        GroupUser2 dbItem = this.findById(id);
        List<Integer> doctorIdList = this.findDoctorIdList(dbItem.getObjectId());
        if(doctorIdList.size()==1){
            throw new ServiceException("最后一个管理员不能移除");
        }
        if (currentUserId.equals(dbItem.getDoctorId())) {
            throw new ServiceException("不能删除自己");
        }

        return this.deleteById(id);
    }

    @Override
    public boolean removeByGroupDoctor(GroupDoctor2 groupDoctor) {
        GroupUser2 dbItem = this.findByUK(groupDoctor.getGroupId(), groupDoctor.getDoctorId());
        if (null == dbItem) {
            return true;
        }
        return this.deleteById(dbItem.getId().toString());
    }


    /**
     * 最后一个管理员不能退出科室
     *
     * @return
     */
    @Override
    public boolean quitGroup(GroupDoctor2 groupDoctor) {
        // check
        List<GroupUser2> groupUserList = this.findList(groupDoctor.getGroupId());
        if (SdkUtils.isNotEmpty(groupUserList)) {
            if (1 == groupUserList.size() && groupUserList.get(0).getDoctorId().equals(groupDoctor.getDoctorId())) {
                throw new ServiceException("您是最后一个管理员，添加其他管理员后才能退出");
            }
        }

        Integer doctorId = groupDoctor.getDoctorId();

        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupDoctor.getGroupId()).field("doctorId").equal(doctorId);
        return this.deleteByQuery(query) > 0;
    }

    @Override
    public int deleteByGroup(String groupId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);
        return this.deleteByQuery(query);
    }

    @Override
    public Integer countByGroup(String groupId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);
        int count = (int) query.countAll();
        return count;
    }

    @Override
    public Map<String, Integer> countByGroupList(List<String> groupIdList) {
        if (SdkUtils.isEmpty(groupIdList)) {
            return new HashMap<>(0);
        }
        DBObject matchFields = new BasicDBObject();
        matchFields.put("objectId", new BasicDBObject("$in", groupIdList));

        DBObject match = new BasicDBObject();
        match.put("$match", matchFields);

        DBObject _group = new BasicDBObject("groupId", "$objectId");

        DBObject groupFields = new BasicDBObject();
        groupFields.put("_id", _group);
        groupFields.put("count", new BasicDBObject("$sum", 1));

        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = new ArrayList<>(2);
        pipeline.add(match);
        pipeline.add(group);
        AggregationOutput output = null;
        try {
            output = dsForRW.getDB().getCollection("c_group_user").aggregate(pipeline);
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
    public List<GroupUser2> findAdminUserByGroupId(String groupId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("objectId").equal(groupId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
        return query.asList();
    }

    @Override
    public List<String> findGroupIdListByDoctor(Integer doctorId) {
        Query<GroupUser2> query = this.createQuery();
        query.field("doctorId").equal(doctorId);
        query.field("status").equal(GroupEnum.GroupUserStatus.正常使用.getIndex());
        query.retrievedFields(true, "objectId");
        List<GroupUser2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        List<String> groupIdList = list.stream().map(o->o.getObjectId()).collect(Collectors.toList());
        return groupIdList;
    }
}
