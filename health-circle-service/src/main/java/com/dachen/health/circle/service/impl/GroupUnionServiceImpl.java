package com.dachen.health.circle.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.entity.GroupUnionApply;
import com.dachen.health.circle.entity.GroupUnionMember;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupUnionHomePageVO;
import com.dachen.health.circle.vo.MobileGroupUnionVO;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Model(GroupUnion.class)
@Service
public class GroupUnionServiceImpl extends BaseServiceImpl implements GroupUnionService {

    public static final Integer STATUS_NORMAL = 2;
    public static final Integer STATUS_DELETE = 9;

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected GroupUnionMemberService groupUnionMemberService;

    public GroupUnion add(Integer currentUserId, String groupId, String name, String intro, String logoPicUrl) {
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);

        // 检查是不是我名下的组织
        boolean match = groupUser2Service.ifMyGroup(currentUserId, groupId);
        if (!match) {
            throw new ServiceException("groupId is wrong!");
        }

        long now = System.currentTimeMillis();

        GroupUnion tmp = new GroupUnion();
        tmp.setGroupId(groupId);
        this.setNameAndCheck(tmp, name);
        tmp.setIntro(intro);
        if (StringUtils.isEmpty(logoPicUrl)) {
            // TODO: 提供默认图片
//            tmp.setLogoPicUrl();
        } else {
            tmp.setLogoPicUrl(logoPicUrl);
        }

        tmp.setStatusId(STATUS_NORMAL);
        tmp.setStatusTime(now);
        tmp.setGroupType(group2.getType());
        tmp.setCreateTime(now);
        tmp.setCreateUserId(currentUserId);
        tmp.setCreateGroupId(groupId);
        tmp.setCreateGroupId(groupId);
        tmp.setTotalMember(0);  // 默认有1个成员

        GroupUnion groupUnion = this.saveEntityAndFind(tmp);

        // 将主体加入到成员表中
        groupUnionMemberService.addByCreateUnion(currentUserId, groupUnion, group2);

        return groupUnion;
    }

    @Override
    public MobileGroupUnionVO createAndVO(Integer currentUserId, String groupId, String name, String intro, String logoPicUrl) {
        GroupUnion groupUnion = this.add(currentUserId, groupId, name, intro, logoPicUrl);
        MobileGroupUnionVO vo = this.convertToMobile(groupUnion);
        return vo;
    }

    protected MobileGroupUnionVO convertToMobile(GroupUnion groupUnion) {
        if (null == groupUnion) {
            return null;
        }
        MobileGroupUnionVO vo = new MobileGroupUnionVO(groupUnion);
        return vo;
    }

    protected List<MobileGroupUnionVO> convertToMobile(List<GroupUnion> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupUnionVO> voList = new ArrayList<>(list.size());
        for (GroupUnion groupUnion : list) {
            MobileGroupUnionVO vo = new MobileGroupUnionVO(groupUnion);
            voList.add(vo);
        }
        return voList;
    }


    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected GroupUnionApplyService groupUnionApplyService;

    @Autowired
    protected GroupUnionInviteService groupUnionInviteService;

    protected MobileGroupUnionHomePageVO convertToMobileHomePage(Integer doctorId, GroupUnion groupUnion) {
        if (null == groupUnion) {
            return null;
        }
        MobileGroupUnionHomePageVO vo = new MobileGroupUnionHomePageVO(groupUnion);

        // 确定role
        vo.setRole(this.getRole(doctorId, groupUnion));

        return vo;
    }

    @Override
    public boolean incrTotalMember(String id, int count) {
        Query<GroupUnion> query = this.createQueryByPK(id);
        UpdateOperations<GroupUnion> ops = this.createUpdateOperations();
        ops.inc("totalMember",count);
        return this.update(query, ops) > 0;
    }

    protected String getRole(Integer doctorId, GroupUnion groupUnion) {
        List<String> doctorGroupIdList = groupUser2Service.findGroupIdByDoctor(doctorId);   // 获取医生所管理的组织列表
        if (SdkUtils.isEmpty(doctorGroupIdList)) {
           return null;
        }
        if (doctorGroupIdList.contains(groupUnion.getGroupId())) {
            return ROLE_root;
        }
        List<String> unionGroupIdList = groupUnionMemberService.findGroupIdByUnion(groupUnion.getId().toString());  // 获取医联体的成员列表
        Collection<String> ret = CollectionUtils.intersection(doctorGroupIdList, unionGroupIdList); // 求交集
        if (!ret.isEmpty()) {
            return ROLE_admin;
        }
        return null;
    }

    private static final String ROLE_root = "root";
    private static final String ROLE_admin = "admin";

    protected void checkRootPri(Integer doctorId, GroupUnion groupUnion) {
        String role = getRole(doctorId, groupUnion);
        if (!ROLE_root.equals(role)) {
            throw new ServiceException("Forbidden");
        }
    }
    @Override
    public void checkAdminPri(Integer doctorId, GroupUnion groupUnion) {
        String role = getRole(doctorId, groupUnion);
        if (!ROLE_admin.equals(role)) {
            throw new ServiceException("Forbidden");
        }
    }

    @Override
    public void check(String id){
        GroupUnion groupUnion = this.findById(id);
        this.check(groupUnion);
    }

    @Override
    public void check(GroupUnion groupUnion){
        if(null == groupUnion){
            throw new ServiceException("联盟已解散");
        }

        if (!groupUnion.getStatusId().equals(STATUS_NORMAL)) {
            throw new ServiceException("联盟已解散");
        }
    }

    @Override
    public GroupUnion findAndCheckById(String id){
        GroupUnion groupUnion = this.findById(id);
        check(groupUnion);
        return groupUnion;
    }

    @Override
    public GroupUnion findNormalById(String id){
        GroupUnion groupUnion = this.findById(id);
        if (null != groupUnion) {
            if (!groupUnion.getStatusId().equals(STATUS_NORMAL)) {
                return null;
            }
        }
        return groupUnion;
    }



//    protected void checkRootOrAdminPri(Integer doctorId, GroupUnion groupUnion) {
//        String role = getRole(doctorId, groupUnion);
//        if (!ROLE_root.equals(role) && !ROLE_admin.equals(role)) {
//            throw new ServiceException("Forbidden");
//        }
//    }

    protected void setNameAndCheck(GroupUnion groupUnion, String nameNew) {
        if (StringUtils.isEmpty(nameNew)) {
            throw new ServiceException("name is Empty");
        }
        nameNew = nameNew.trim();
        if (nameNew.length() > 15) {
            throw new ServiceException("name is too long");
        }
        groupUnion.setName(nameNew);
    }

    @Override
    public MobileGroupUnionVO updateLogoAndVO(Integer currentUserId, String id, String logoPicUrl) {
        GroupUnion dbItem = this.findById(id);
        this.checkRootPri(currentUserId, dbItem);

        dbItem.setLogoPicUrl(logoPicUrl);
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem.setUpdateUserId(currentUserId);
        dbItem = this.saveEntityAndFind(dbItem);
        return this.convertToMobile(dbItem);
    }

    @Override
    public MobileGroupUnionVO updateNameAndVO(Integer currentUserId, String id, String name) {
        GroupUnion dbItem = this.findById(id);
        this.checkRootPri(currentUserId, dbItem);

        this.setNameAndCheck(dbItem, name);
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem.setUpdateUserId(currentUserId);
        dbItem = this.saveEntityAndFind(dbItem);
        return this.convertToMobile(dbItem);
    }

    @Override
    public MobileGroupUnionVO updateIntroAndVO(Integer currentUserId, String id, String intro) {
        GroupUnion dbItem = this.findById(id);
        this.checkRootPri(currentUserId, dbItem);

        dbItem.setIntro(intro);
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem.setUpdateUserId(currentUserId);
        dbItem = this.saveEntityAndFind(dbItem);
        return this.convertToMobile(dbItem);
    }

    @Override
    public List<GroupUnion> findFullByIds(List<String> idList) {
        List<GroupUnion> list = this.findByIds(idList);
        this.wrapAll(list);
        return list;
    }

    @Override
    public MobileGroupUnionHomePageVO findGroupHomePagAndVO(Integer currentUserId, String id, String groupId) {
        GroupUnion dbItem = this.findById(id);
        this.wrapAll(dbItem);
        this.wrapMemberAndApply(dbItem, groupId);
        MobileGroupUnionHomePageVO vo = this.convertToMobileHomePage(currentUserId, dbItem);
        return vo;
    }

    /**
     * 1、科室联盟内各组织管理员可以退出科室联盟
     * 2、科室联盟的管理员不能退出科室联盟，只能解散
     *
     * @param currentUserId
     * @param id
     * @return
     */
    @Override
    public boolean quit(Integer currentUserId, String id) {
        Integer doctorId = currentUserId;
        GroupUnion dbItem = this.findById(id);
        // check pri
        this.checkAdminPri(currentUserId, dbItem); // admin can quit

        List<String> memberList = groupUnionMemberService.findIdListByUnionAndDoctor(id, doctorId);
        if (SdkUtils.isNotEmpty(memberList)) {
            groupUnionMemberService.deleteByIds(memberList);
            dbItem.setTotalMember(dbItem.getTotalMember() - memberList.size());
            //发送退出医联体通知
            //this.sendQuitUnionIMMsgToClients(currentUserId,dbItem.getName(),id)
            this.saveEntity(dbItem);
        }
        return false;
    }



    /**
     * 1、只有科室联盟的管理员能解散科室联盟
     * 2、科室联盟的管理员不能退出科室联盟，只能解散
     *
     * @param currentUserId
     * @param id
     * @return
     */
    @Override
    public boolean dismiss(Integer currentUserId, String id) {
        GroupUnion dbItem = this.findById(id);
        // check pri
        this.checkRootPri(currentUserId, dbItem);   // only root can dismiss

        dbItem.setStatusId(STATUS_DELETE);
        dbItem.setStatusTime(System.currentTimeMillis());
        this.saveEntity(dbItem);

        boolean ret = true;
//        boolean ret = this.deleteById(id);
        if (ret) {
            groupUnionMemberService.deleteByGroupUnion(currentUserId, id);
            groupUnionApplyService.closeByGroupUnionDismiss(id);
            groupUnionInviteService.closeByGroupUnionDismiss(id);
        }

        return ret;
    }

    @Override
    public List<MobileGroupUnionVO> findByGroupsAndVO(List<String> groupIdList) {
        List<String> idList = groupUnionMemberService.findUnionIdsByGroups(groupIdList);
        if (SdkUtils.isEmpty(idList)) {
            return null;
        }
        List<GroupUnion> list = this.findByIds(idList);
        wrapAll(list);
        List<MobileGroupUnionVO> voList = this.convertToMobile(list);
        return voList;
    }

    @Override
    public Pagination<GroupUnion> findPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize) {
        Query<GroupUnion> query = this.createQuery();
        query.field("statusId").equal(STATUS_NORMAL);
        if (StringUtils.isNotBlank(kw)) {
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("name").equal(pattern);
        }
        if (SdkUtils.isNotEmpty(exceptIdList)) {
            query.field(Mapper.ID_KEY).notIn(this.convertToObjectId(exceptIdList));
        }
        long total = query.countAll();

        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupUnion> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        Pagination<GroupUnion> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public int countByMain(String groupId) {
        Query<GroupUnion> query = this.createQuery();
        query.field("groupId").equal(groupId);
        query.field("statusId").equal(STATUS_NORMAL);
        long count = query.countAll();
        return (int) count;
    }

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    protected void wrapAll(List<GroupUnion> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapTotal(list);
        this.wrapGroup(list);
    }

    protected void wrapAll(GroupUnion groupUnion) {
        if (null == groupUnion) {
            return;
        }
        this.wrapTotal(groupUnion);
        this.wrapGroup(groupUnion);
    }

    protected void wrapTotal(GroupUnion groupUnion) {
        if (null == groupUnion) {
            return;
        }
        int total = this.getTotalDoctor(groupUnion.getId().toString());
        groupUnion.setTotalDoctor(total);
    }

    @Override
    public void wrapTotal(List<GroupUnion> unionList) {
        if (SdkUtils.isEmpty(unionList)) {
            return;
        }
        // TODO: 查询次数太多，待优化
        for (GroupUnion groupUnion:unionList) {
            int total = this.getTotalDoctor(groupUnion.getId().toString());
            groupUnion.setTotalDoctor(total);
        }
    }

    @Override
    public boolean ifCanCreate(Integer doctorId) {
        // 管理员可以创建科室联盟
        List<String> groupIdList = groupUser2Service.findGroupIdByDoctor(doctorId);
        groupIdList = group2Service.filterGroupOrDeptIds(groupIdList);

        if (SdkUtils.isNotEmpty(groupIdList)) {
            return true;
        }
        return false;
    }

    public int getTotalDoctor(String id) {
        List<String> groupIdList = groupUnionMemberService.findGroupIdsByUnion(id);
        Map<String, Integer> map = groupDoctor2Service.countByGroupList(groupIdList);
        int total = 0;
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            total += entry.getValue();
        }
        return total;
    }

    protected void wrapMemberAndApply(GroupUnion groupUnion, String groupId) {
        if (null == groupUnion || StringUtils.isBlank(groupId)) {
            return;
        }
        GroupUnionMember member = groupUnionMemberService.findByUK(groupUnion.getId().toString(), groupId);
        if (null != member) {
            groupUnion.setMember(member);
        } else {
            GroupUnionApply apply = groupUnionApplyService.findLatestHandlingByGroupAndUnion(groupId, groupUnion.getId().toString());
            groupUnion.setApply(apply);
        }
    }

    protected void wrapGroup(GroupUnion groupUnion) {
        if (null == groupUnion) {
            return;
        }
        Group2 group2 = group2Service.findById(groupUnion.getGroupId());
        groupUnion.setGroup(group2);
    }

    protected void wrapGroup(List<GroupUnion> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<>(list.size());
        for (GroupUnion groupUnion : list) {
            groupIdSet.add(groupUnion.getGroupId());
        }
        List<Group2> groupList = group2Service.findByIds(new ArrayList<>(groupIdSet));
        for (GroupUnion groupUnion : list) {
            for (Group2 group2 : groupList) {
                if (groupUnion.getGroupId().equals(group2.getId().toString())) {
                    groupUnion.setGroup(group2);
                    break;
                }
            }
        }
    }
}
