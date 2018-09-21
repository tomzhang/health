package com.dachen.health.group.group.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.ExistRegionVo;
import com.dachen.health.base.entity.po.RegionVo;
import com.dachen.health.base.entity.vo.DepartmentVO;
import com.dachen.health.group.common.entity.vo.RecommendGroupVO;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.po.GroupUserApply;
import com.dachen.health.group.group.entity.vo.DeptVO;
import com.dachen.health.group.group.entity.vo.GroupApplyDetailPageVo;
import com.dachen.health.group.group.entity.vo.GroupDeptVO;
import com.dachen.sdk.exception.HttpApiException;
import java.util.List;
import java.util.Map;

/**
 * @author pijingwei
 * @date 2015/8/7
 */
public interface IGroupService {


    /**
     * 创建一个新的集团
     *
     * @param group
     * @return
     * @author wangqiao
     * @date 2016年3月5日
     */
    Group createGroup(Group group);

    /**
     * </p>修改(更新)医生集团</p>
     *
     * @param group
     * @return boolean
     * @author pijingwei
     * @date 2015年8月7日
     */
    Group updateGroup(Group group) throws HttpApiException;

    /**
     * </p>根据条件获取医生集团列表</p>
     *
     * @param group
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月7日
     */
    PageVO searchGroup(GroupParam group);

    /**
     * </p>删除医生集团</p>
     *
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月7日
     */
    boolean deleteGroup(String... ids);

    /**
     * </p>根据Id获取详细信息</p>
     *
     * @param id
     * @return Group
     * @author pijingwei
     * @date 2015年8月7日
     */
    Group getGroupById(String id);


    Group getPlatformInfo();

    /**
     * </p>就论人数++</p>
     *
     * @param groupId
     * @return
     */
    Group increaseCureNum(String groupId) throws HttpApiException;

//	PubPO createPubFromGroup(Group gp,String name,String rtype);

    /**
     * 发送会诊订单时候调用
     * 获取集团id和name
     *
     * @param doctorId
     * @return
     */
    List<Map<String, Object>> getConsultationGroupByDoctorId(Integer doctorId, List<String> groupIds);

    /**
     * </p>申请创建医生集团</p>
     *
     * @param groupApply
     */
    @Deprecated
    GroupApply groupApply(GroupApply groupApply) throws HttpApiException;


    /**
     * </p>获取集团申请记录信息</p>
     *
     * @param groupApplyId
     * @param applyUserId
     * @return
     */
    GroupApply getGroupApplyInfo(String groupApplyId, Integer applyUserId);


    /**
     * </p>管理员审核医生集团的申请</p>
     *
     * @param groupApplyId
     * @param status
     * @param userId
     */
    @Deprecated
    void processGroupApply(GroupApply groupApply) throws HttpApiException;

    /**
     * 管理员审核医生集团申请
     *
     * @param groupApply
     * @author wangqiao
     * @date 2016年3月4日
     */
    void processGroupApplyNew(GroupApply groupApply) throws HttpApiException;

    PageVO getApplyList(String status, String groupActive, String skip, String groupName, String doctorName, String hospitalName, String telephone,
                        Integer pageSize, Integer pageIndex);

    GroupApplyDetailPageVo getApplyDetail(String groupApplyId);

    void applyTransfer(GroupUserApply groupUserApply) throws HttpApiException;

    void confirmTransfer(String groupUserApplyId, String status) throws HttpApiException;

    Object getTransferInfo(String groupUserApplyId);

    void updateGroupApplyImageUrl(GroupApply groupApply);

    void updateGroupProfit(Group group);

    boolean hasCreateRole(Integer doctorId);

    /**
     * 新建一个集团申请记录（状态为待审核）
     *
     * @param group   集团信息
     * @param logoUrl 集团logo
     * @return
     * @author wangqiao
     * @date 2016年3月5日
     */
    GroupApply createNewGroupApply(Group group, String logoUrl);

    /**
     * 根据集团id，查询最后的审核记录
     *
     * @param groupId
     * @return
     * @author wangqiao
     * @date 2016年3月5日
     */
    GroupApply getLastGroupApply(String groupId);


    Object getGroupStatusInfo(String groupId);

    boolean isWithinDutyTime(String groupId);

    /**
     * 根据关键字和集团的申请状态来查询集团
     *
     * @param keyword
     * @param applyStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO findGroupByKeyword(String keyword, String applyStatus, int pageIndex, int pageSize);

    /**
     * 通过集团id读取集团名称
     *
     * @param id
     * @return
     * @author wangqiao
     * @date 2016年6月1日
     */
    String getGroupNameByGroupId(String id);

    /**
     * 激活集团
     *
     * @param groupApplyId
     * @return
     * @author tan.yf
     * @date 2016年6月2日
     */
    void activeGroup(String groupId) throws HttpApiException;

    String isOpenSelfGuideAndGetGroupId(Integer userId, int userType);

    /**
     * 屏蔽集团
     *
     * @param groupId 集团ID
     * @author tan.yf
     * @date 2016年6月6日
     */
    Group blockGroup(String groupId);

    /**
     * 取消屏蔽集团
     *
     * @param groupId 集团ID
     * @author tan.yf
     * @date 2016年6月6日
     */
    Group unBlockGroup(String groupId);

    /**
     * 判断集团（审核通过且激活且未屏蔽）是否同步到ES
     *
     * @param groupId
     * @return
     * @author tan.yf
     * @date 2016年6月15日
     */
    boolean isNormalGroup(String groupId);

    GroupApply getGroupApplyById(String groupApplyId);

    List<RegionVo> getGroupRegion(String groupId);

    /**
     * 查找存在医生的科室
     *
     * @param groupId
     * @return List
     * @author liangcs
     * @date 2016年7月14日
     */
    List<DepartmentVO> getDeptsOfDoctors(String groupId);

    List<Integer> getDoctorIdsByGroupId(String groupId);

    PageVO searchGroupByName(String name, Integer pageIndex, Integer pageSize);

    void setGroupToRecommended(String[] groupId);

    void removeGroupRecommended(String groupId);

    List<RecommendGroupVO> getGroupRecommendedList(Integer pageIndex, Integer pageSize);

    PageVO getRecommends(Integer pageIndex, Integer pageSize);

    void upWeight(String groupId);

    List<ExistRegionVo> getGroupExistRegion(String groupId);

    Map<String, Object> groupInfo(String groupId);

    List<Map<String, String>> searchBykeyword(Integer type, String keyword);

    List<String> getSkipGroupIds();

    void changAdmin(String groupId, Integer receiverId) throws HttpApiException;

    /**
     * 初始化所有集团的IM
     */
    void initAllGroupIM();

    void createGroupIm(Group group) throws HttpApiException;

    Object getIMInfo(String gid) throws HttpApiException;

    void fixAllGroupIM();

    void updateGroupIMLogo(Group group);

    void updateGroupIMName(Group group);

    List<GroupDeptVO> findActiveDeptList();

    List<DeptVO> findActiveDeptListByKeyword(String keyword);

    /**
     * 获取所有 医院和集团 不包括科室
     * @return
     */
    PageVO findAllGroupExDept(String name,int pageIndex, int pageSize);
}
