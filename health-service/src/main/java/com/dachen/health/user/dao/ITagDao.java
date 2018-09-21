package com.dachen.health.user.dao;

import java.util.List;

import com.dachen.health.commons.constants.UserEnum.TagType;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.health.user.entity.vo.RelationVO;
import org.bson.types.ObjectId;

/**
 * ProjectName： health-service<br>
 * ClassName： ITagDao<br>
 * Description： 标签模块dao<br>
 *
 * @author fanp
 * @version 1.0.0
 * @crateTime 2015年7月2日
 */
public interface ITagDao {

    /**
     * </p>获取用户某个标签个数，用户判断标签是否重复</p>
     *
     * @param param
     * @return 标签数量
     * @author fanp
     * @date 2015年7月2日
     */
    int getTagCount(TagParam param);

    /**
     * </p>查询用户存在的标签</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月9日
     */
    List<String> getExistTag(TagParam param);

    /**
     * </p>获取标签列表</p>
     *
     * @param userId
     * @param tagType
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    List<String> getTag(Integer userId, TagType tagType);

    /**
     * </p>添加标签</p>
     *
     * @param tag
     * @author fanp
     * @date 2015年7月3日
     */
    void addTag(Tag tag);

    /**
     * </p>修改标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void updateTag(TagParam param);

    /**
     * </p>删除标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void deleteTag(TagParam param);

    /**
     * 获取标签
     *
     * @param userId
     * @param tagName
     * @return
     */
    Tag getTag(Integer userId, String tagName);

    /**
     * 获取未激活对象
     *
     * @param userId
     * @return
     */
    RelationVO getInactiveTag(Integer userId);

    /**
     * </p>给关系打标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void addRelationTag(TagParam param);

    /**
     * </p>给关系打标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void addRelationTag2(TagParam param);

    /**
     * </p>删除关系标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void deleteRelationTag(TagParam param);

    /**
     * </p>给关系打标签(多个标签)</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void addRelationTags(TagParam param);

    /**
     * </p>删除关系标签(多个标签)</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    void deleteRelationTags(TagParam param);

    String[] getDoctorPatientTag(Integer doctorId, Integer userId);

    /**
     * 根据医生和患者的id查询u_doctor_patient表
     *
     * @param doctorId  医生id
     * @param patientId 患者id
     * @return
     */
    DoctorPatient findByDoctorIdAndPatientId(Integer doctorId, Integer patientId);

    /**
     * 更新医生患者关系的标签信息
     *
     * @param id   id
     * @param tags 标签信息
     */
    void updateDoctorPatient(ObjectId id, String[] tags, String remarkName, String remark);

    boolean existTag(Tag tag);

    List<DoctorPatient> getAllMyPatient(Integer doctorId);

}
