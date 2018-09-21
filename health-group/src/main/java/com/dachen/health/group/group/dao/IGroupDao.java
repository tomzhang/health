package com.dachen.health.group.group.dao;

import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.Query;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.group.common.entity.vo.RecommendGroupVO;
import com.dachen.health.group.group.entity.param.GroupCertParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.param.GroupsParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.po.GroupCertification;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupUserApply;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.mongodb.DBCursor;

/**
 * @author pijingwei
 */
public interface IGroupDao {


    /**
     * 新增 group记录
     *
     * @param group
     * @return
     * @author wangqiao
     * @date 2016年4月7日
     */
    Group save(Group group);

    /**
     * </p>更新（修改）公司信息</p>
     *
     * @param group
     * @return Group
     * @author pijingwei
     * @date 2015年8月7日
     */
    Group update(Group group);

    /**
     * </p>根据公司Id删除</p>
     *
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月7日
     */
    boolean delete(String... ids);

    /**
     * </p>根据搜索条件获取公司列表</p>
     *
     * @param group
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月7日
     */
    PageVO search(GroupParam group);

    /**
     * </p>根据Id获取详细信息</p>
     *
     * @param id
     * @param creator
     * @return Group
     * @author pijingwei
     * @date 2015年8月7日
     */
    Group getById(String id, Integer creator);

    Group getById(String id);

    List<Group> getAllGroup();

    List<Group> getAllGroup(String companyId);

    Group getByName(String name);

    boolean updateDutyTime(String groupId, String dutyStartTime, String dutyEndTime);

    List<Group> findAll();

    /**
     * 集团提交公司认证资料
     *
     * @param groupId
     * @param certInfo
     */
    boolean submitCert(String groupId, Integer doctorId, GroupCertification certInfo);

    /**
     * 获取集团信息，包含公司认证资料
     *
     * @param param
     * @return
     */
    Query<Group> getGroupCerts(GroupCertParam param);

    /**
     * 修改认证备注
     *
     * @param groupId
     * @param remarks
     * @return
     */
    boolean updateRemarks(String groupId, String remarks);

    /**
     * 认证通过
     *
     * @param groupId
     * @param companyId
     * @return
     */
    boolean passCert(String groupId, String companyId);

    /**
     * 未通过
     *
     * @param groupId
     * @return
     */
    boolean noPass(String groupId);

    /**
     * 获取集团集合Query
     *
     * @param groupId
     * @param include 是否包含当前集团
     * @return
     */
    Query<Group> retrievedGroups(String groupId, boolean include);


    void openService(String groupId);

    void removeConsultationPackDoctor(String groupId, Integer doctorId);

    GroupApply insertgroupApply(GroupApply groupApply);

    GroupApply getGroupApplyByApplyUserId(Integer applyUserId);

    GroupApply getGroupApplyById(String id);

    void updateGroupApply(GroupApply groupApply);

    void updateGroupApplyByGroupId(GroupApply groupApply);

    List<GroupApply> getApplyList(String status, String groupName, List<Integer> doctorIds, List<String> groupIdList, Integer pageSize, Integer pageIndex);

    long getApplyListCount(String status, String groupName, List<Integer> doctorIds, List<String> groupIdList);

    void insertGroupUserApply(GroupUserApply groupUserApply);

    void updateGroupUserApplyToExpire(String groupId);

    GroupUserApply getGroupUserApplyById(String groupUserApplyId);

    void updateGroupUserApply(GroupUserApply groupUserApply);

    GroupUserApply getTransferInfo(String groupUserApplyId);

    void updateGroupApplyImageUrl(GroupApply groupApply);

    Group saveGroupAtAuditPass(Group group);

    void updateGroupProfit(Group group);

    /**
     * 查询最后一次的集团申请信息
     *
     * @param groupId
     * @return
     * @author wangqiao
     * @date 2016年3月5日
     */
    GroupApply getLastGroupApplyByGroupId(String groupId);

    /**
     * 读取医生所在的医院信息
     *
     * @param doctorId
     * @return
     */
    GroupDoctor checkDoctor(Integer doctorId);

    /**
     * 读取医生所在的科室信息
     *
     * @param doctorId
     * @return
     */
    GroupDoctor checkDept(Integer doctorId);

    /**
     * 根据医院id，读取医院集团信息
     *
     * @param hospitalId
     * @return
     */
    Group checkHospital(String hospitalId);

    /**
     * 根据关键字查询医院列表
     *
     * @param group
     * @return
     */
    PageVO groupHospitalList(GroupParam group);

    void activeGroup(String groupId);

    /**
     * 根据id查询创建医院的详情
     *
     * @param id
     * @return
     */
    Map<String, Object> getDetailByGroupId(String id);

    void updateAppointment(String groupId, Boolean openAppointment, Integer appointmentGroupProfit,
                           Integer appointmentParentProfit);

    /**
     * 根据类型，查询所有group
     *
     * @param type
     * @return
     * @author wangqiao
     * @date 2016年4月27日
     */
    List<Group> getGroupListByType(String type);

    String getAppointmentGroupId();

    void setGroupHospital(GroupParam param);

    /**
     * 根据关键字和审核状态来查询集团
     *
     * @param keyword
     * @param applyStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO getGroupByKeyword(String keyword, String applyStatus, int pageIndex, int pageSize);

    /**
     * 根据条件查询集团列表
     *
     * @param group
     * @return List<Group>
     * @author tan.yf
     * @date 2016年6月2日
     */
    List<Group> getGroupList(GroupsParam group);

    List<String> getIdListBySkipAndType(GroupEnum.GroupSkipStatus skipStatus, GroupEnum.GroupType groupType);

    /**
     * 获取全部的已经屏蔽的集团
     *
     * @return
     */
    List<Group> getSkipGroups();

    List<String> getSkipGroupIds();

    List<String> findOfflineHospitalIdByKeyword(String groupId, String keyWord);

    /**
     * 重建全文索引时获取全部正常状态的集团
     *
     * @return
     */
    List<Group> getAllGroupForEs();

    List<GroupDoctorVO> getGroupDoctorListByGroupId(String groupId);

    List<Group> searchGroupsByName(String groupName, Integer pageIndex, Integer pageSize);

    Long getCountOfSearch(String groupName);

    List<RecommendGroupVO> getAllRecommendGroups();

    List<RecommendGroupVO> getRecommenGroups();

    void saveRecommendGroup(RecommendGroupVO vo);

    void removeGroupRecommended(String groupId);


    /**
     * 根据集团id获取集团列表
     *
     * @return
     */
    List<Group> getGroupsListByIds(List<String> groupIds);

    void updateGroupRecommended(RecommendGroupVO vo);

    DBCursor searchByKeyword(Integer type, String keyword);

    /**
     * 更新集团的会话组的id
     *
     * @param groupId 集团id
     * @param gid     会话组id
     */
    void updateGroupGid(String groupId, String gid);

    List<Group> findByHospitalAndDept(String hospitalId, String deptId);

    List<Group> findActiveDeptList();

    List<Group> findActiveDeptListByKeyword(String keyword);

    /**
     * 获取所有 医院和集团 不包括科室
     * @return
     */
    PageVO findAllGroupExDept(String name,int pageIndex, int pageSize);

    List<Group> findAllGroupExDept();
}
