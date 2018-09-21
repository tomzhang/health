package com.dachen.health.user.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.constants.UserEnum.TagType;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.health.user.entity.vo.UserDetailVO;

/**
 * ProjectName： health-service<br>
 * ClassName： IDoctorTagService<br>
 * Description： 医生标签模块service<br>
 * 
 * @author fanp
 * @crateTime 2015年6月30日
 * @version 1.0.0
 */
public interface IRelationService {

    /**
     * </p>查找当前用户的关系类别和标签分类，医患关系，医生好友关系，医助患者关系，患者好友关系</p>
     * 
     * @param relationType
     *            关系类型
     * @param userId
     *            用户id
     * @return 用户信息列表、按标准分组的用户id数组、收藏列表
     * @author fanp
     * @date 2015年7月1日
     */
    Map<String, Object> getRelationAndTag(RelationType relationType, Integer userId);

    List<UserDetailVO> getRelation(RelationType relationType, Integer userId);
    
    /**
     * 返回给定过滤列表中与userId是好友的数据
     * @param relationType
     * @param userId 用户id
     * @param filterUserIds 给定的过滤列表
     * @return
     */
    List<Integer> filterFriends(RelationType relationType, Integer userId, Integer[] filterUserIds);

    List<Integer> getRelationUserIds(RelationType relationType, Integer userId);
    /**
     * </p>查找当前用户的关系列表</p>
     * 
     * @param relationType
     *            关系类型
     * @param userId
     *            用户id
     * @return 用户信息列表、收藏列表
     * @author fanp
     * @date 2015年7月1日
     */
    Map<String, Object> getRelations(RelationType relationType, Integer userId);

    /**
     * </p>获取标签列表</p>
     * @param userId
     * @param tagType
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<String> getTag(Integer userId,TagType tagType);
    
    /**
     * 添加系统标签（包含患者报到、图文咨询、电话咨询、健康关怀、在线门诊）
     * @param param
     */
    void addRelationTag(TagParam param);

    /**
     * 添加系统标签（包含患者报到、图文咨询、电话咨询、健康关怀、在线门诊）
     * @param param
     */
    void addRelationTag2(TagParam param);
    
    /**
     * </p>设置标签（包含修改，单个标签操作）</p>
     * 
     * @param param
     * @return false则为标签重复
     * @author fanp
     * @date 2015年7月2日
     */
    void addTag(TagParam param);

    /**
     * </p>删除标签</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void deleteTag(TagParam param);
    
    /**
     * </p>修改会员标签（多标签操作）</p>
     * @param param
     * @author fanp
     * @date 2015年7月9日
     */
    void updateUserTag(TagParam param);

    boolean isSystemTag(String tag);
    
    /**
     * 医生查看或者的自定义标签
     * @param doctorId
     * @param userId
     * @return
     */
	List<String> getDoctorPatientSelfTag(Integer doctorId, Integer userId);

    void addNoneTag(List<RelationVO> relaList, Integer userId, RelationType relationType);

    void addNoneTag2(List<RelationVO> relaList, Integer doctorId, RelationType relationType);

    void addSysTag(List<RelationVO> list, Integer userId);

    void fillProperty(List<RelationVO> list, Integer userId);

    boolean ifDoctorFriend(Integer userId, Integer toUserId);
}
