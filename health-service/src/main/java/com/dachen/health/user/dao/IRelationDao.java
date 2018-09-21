package com.dachen.health.user.dao;

import java.util.List;

import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.health.user.entity.vo.UserDetailVO;
import org.bson.types.ObjectId;

/**
 * ProjectName： health-service<br>
 * ClassName： IDoctorTagDao<br>
 * Description： 医生标签模块dao<br>
 *
 * @author fanp
 * @version 1.0.0
 * @crateTime 2015年6月30日
 */
public interface IRelationDao {

    /**
     * </p>按标签分组获取好友</p>
     *
     * @param relationType 关系类型
     * @param userId       用户id
     * @return 用户所有标签及标签下关系用户数量及id的数组
     * @author fanp
     * @date 2015年6月30日
     */
    List<RelationVO> getGroupTag(RelationType relationType, Integer userId);

    /**
     * </p>按标签分组获取好友</p>
     *
     * @param relationType 关系类型
     * @param userId       用户id
     * @return 用户所有标签及标签下关系用户数量及id的数组
     * @author fanp
     * @date 2015年6月30日
     */
    List<RelationVO> getGroupTag(RelationType relationType, Integer userId, List<UserDetailVO> userDetailVOs);

    /**
     * </p>查找有关系的用户信息</p>
     *
     * @param relationType 关系类型
     * @param userId       用户id
     * @return 用户信息列表
     * @author fanp
     * @date 2015年7月1日
     */
    List<UserDetailVO> getRelations(RelationType relationType, Integer userId);

    /**
     * 获取全部我的患者的id
     *
     * @param doctorId 医生的id
     * @return
     */
    List<Integer> getAllMyPatientIds(Integer doctorId);

    /**
     * add by zhangyin
     * 患者端 “我的医生” 改造
     *
     * @param relationType
     * @param userId
     * @return
     */
    public List<User> getRelation(RelationType relationType, Integer userId);

    /**
     * </p>查找个人收藏</p>
     *
     * @param relationType 关系类型
     * @param userId       用户id
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    RelationVO getCollection(RelationType relationType, Integer userId);

    /**
     * </p>未打标签的好友</p>
     *
     * @param relationType 关系类型
     * @param userId       用户id
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    RelationVO getWithoutTag(RelationType relationType, Integer userId);

    List<RelationVO> getGroupTag2(RelationType relationType, Integer userId, List<Integer> patientIds);

    List<DoctorPatient> getAll();

    void fixDoctorPatient(ObjectId id, Integer patientId);

    void addDoctorPatient(DoctorPatient doctorPatient);

    /**
     * 是否医生好友
     * @param userId
     * @param toUserId
     * @return
     */
    boolean ifDoctorFriend(Integer userId,Integer toUserId);
}
