package com.dachen.health.user.service.impl;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dachen.sdk.util.SdkUtils;
import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.constants.UserEnum.TagType;
import com.dachen.health.user.dao.IRelationDao;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.health.user.entity.vo.UserDetailVO;
import com.dachen.health.user.service.IRelationService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorTagServiceImpl<br>
 * Description： 医生标签service实现类<br>
 * 
 * @author fanp
 * @crateTime 2015年6月30日
 * @version 1.0.0
 */
@Service
public class RelationServiceImpl implements IRelationService {

    @Autowired
    private IRelationDao relationDao;

    @Autowired
    private ITagDao tagDao;

    /**
     * </p>
     * 查找当前用户的关系类别和标签分类，医患关系，医生好友关系，医助患者关系，患者好友关系,医生助理关系
     * </p>
     * 
     * @param relationType 关系类型
     * @param userId 用户id
     * @return 用户信息列表、按标准分组的用户id数组、收藏列表
     * @author fanp
     * @date 2015年7月1日
     */
    public Map<String, Object> getRelationAndTag(RelationType relationType, Integer userId) {
        Map<String, Object> map = new HashMap<String, Object>();

        // 用户信息
        List<UserDetailVO> userList = relationDao.getRelations(relationType, userId);

        Collections.sort(userList, new SortByChina<UserDetailVO>("name"));

        // 标签分组
        List<RelationVO> relaList = relationDao.getGroupTag(relationType, userId, userList);

        // 添加数量为0的标签
        addNoneTag(relaList, userId, relationType);

        // 添加系统标签
        addSysTag(relaList, userId);

        // 填充属性
        fillProperty(relaList, userId);

        // 个人收藏
        // RelationVO relation = relationDao.getCollection(relationType, userId);

        // 不存在标签的好友
        // RelationVO without = relationDao.getWithoutTag(relationType, userId);


        map.put("users", userList);
        map.put("tags", relaList);
        // map.put("collection", relation);
        // map.put("without", without);

        return map;
    }

    /**
     * </p>
     * 查找当前用户的关系类别，医患关系，医生好友关系，医助患者关系，患者好友关系,医生助理关系
     * </p>
     * copy method getRelationAndTag
     * 
     * @param relationType 关系类型
     * @param userId 用户id
     * @return 用户信息列表、按标准分组的用户id数组、收藏列表
     * @author limin
     * @date 2017年7月26日15:55:19
     */
    public List<UserDetailVO> getRelation(RelationType relationType, Integer userId) {
        // 用户信息
        List<UserDetailVO> userList = relationDao.getRelations(relationType, userId);

        // Collections.sort(userList, new SortByChina<UserDetailVO>("name"));
        return userList;
    }

    public List<Integer> getRelationUserIds(RelationType relationType, Integer userId) {
        // 用户信息
        List<UserDetailVO> userList = relationDao.getRelations(relationType, userId);

        if (SdkUtils.isEmpty(userList)) {
            return null;
        }
        List<Integer> idList =
                userList.stream().map(o -> o.getUserId()).collect(Collectors.toList());

        return idList;
    }

    class SortChineseName implements Comparator<UserDetailVO> {
        Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

        @Override
        public int compare(UserDetailVO o1, UserDetailVO o2) {
            if (o1 == null || o2 == null || o1.getName() == null || o2.getName() == null) {
                return 0;
            }

            if (cmp.compare(o1.getName(), o2.getName()) > 0) {
                return 1;
            } else if (cmp.compare(o1.getName(), o2.getName()) < 0) {
                return -1;
            }
            return 0;
        }

    }

    public void addNoneTag(List<RelationVO> relaList, Integer userId, RelationType relationType) {
        // 查找好友为零的标签
        List<String> tags = null;

        switch (relationType) {
            case doctorPatient:
                tags = tagDao.getTag(userId, UserEnum.TagType.doctorPatient);
                break;
            case doctorFriend:
                tags = tagDao.getTag(userId, UserEnum.TagType.doctorFriend);
                break;
            case patientFriend:
                tags = tagDao.getTag(userId, UserEnum.TagType.patientFriend);
                break;
            default:
                break;
        }

        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                boolean used = false;
                for (RelationVO rela : relaList) {
                    if (StringUtil.equals(tag, rela.getTagName())) {
                        used = true;
                        break;
                    }
                }
                if (!used) {
                    RelationVO vo = new RelationVO();
                    vo.setTagName(tag);
                    vo.setNum(0);
                    vo.setUserIds(new Integer[] {});
                    relaList.add(vo);
                }
            }
        }
    }

    public void addNoneTag2(List<RelationVO> relaList, Integer doctorId,
            RelationType relationType) {
        // 查找好友为零的标签
        List<String> tags = null;

        switch (relationType) {
            case doctorPatient:
                tags = tagDao.getTag(doctorId, UserEnum.TagType.doctorPatient);
                break;
            case doctorFriend:
                tags = tagDao.getTag(doctorId, UserEnum.TagType.doctorFriend);
                break;
            case patientFriend:
                tags = tagDao.getTag(doctorId, UserEnum.TagType.patientFriend);
                break;
            default:
                break;
        }

        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                boolean used = false;
                for (RelationVO rela : relaList) {
                    if (StringUtil.equals(tag, rela.getTagName())) {
                        used = true;
                        break;
                    }
                }
                if (!used) {
                    RelationVO vo = new RelationVO();
                    vo.setTagName(tag);
                    vo.setNum(0);
                    vo.setPatientIds(Lists.newArrayList());
                    relaList.add(vo);
                }
            }
        }
    }

    public void fillProperty(List<RelationVO> list, Integer userId) {
        for (RelationVO vo : list) {
            Tag tag = tagDao.getTag(TagUtil.SYS_TAG.contains(vo.getTagName()) ? 0 : userId,
                    vo.getTagName());
            if (tag == null) {
                vo.setSeq(88);
                vo.setSys(false);
                continue;
            }
            vo.setTagId(tag.getId().toString());
            // 非系统标签顺序默认88
            vo.setSeq(tag.getSeq() == null ? 88 : tag.getSeq());
            vo.setSys(tag.isSys());
        }
    }

    public void addSysTag(List<RelationVO> list, Integer userId) {

        list.add(tagDao.getInactiveTag(userId));
        List<String> names = new ArrayList<String>();
        for (RelationVO vo : list) {
            names.add(vo.getTagName());
        }
        for (String sysTag : TagUtil.SYS_TAG) {
            if (!names.contains(sysTag)) {
                RelationVO vo = new RelationVO();
                vo.setTagName(sysTag);
                vo.setNum(0);
                vo.setUserIds(new Integer[] {});
                list.add(vo);
            }
        }
    }

    /**
     * </p>
     * 查找当前用户的关系列表
     * </p>
     * 
     * @param relationType 关系类型
     * @param userId 用户id
     * @return 用户信息列表、收藏列表
     * @author fanp
     * @date 2015年7月1日
     */
    public Map<String, Object> getRelations(RelationType relationType, Integer userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 用户信息
        List<UserDetailVO> list = relationDao.getRelations(relationType, userId);
        // 个人收藏
        RelationVO vo = relationDao.getCollection(relationType, userId);

        map.put("users", list);
        map.put("collection", vo);

        return map;
    }

    /**
     * </p>
     * 获取标签列表
     * </p>
     * 
     * @param userId
     * @param tagType
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<String> getTag(Integer userId, TagType tagType) {
        return tagDao.getTag(userId, tagType);
    }

    /**
     * 好友关系tags添加系统标签（包含患者报到、图文咨询、电话咨询、健康关怀、在线门诊） 生成订单时调用
     */
    public void addRelationTag(TagParam param) {
        tagDao.addRelationTag(param);
    }

    /**
     * 好友关系tags添加系统标签（包含患者报到、图文咨询、电话咨询、健康关怀、在线门诊） 生成订单时调用
     */
    public void addRelationTag2(TagParam param) {
        tagDao.addRelationTag2(param);
    }

    /**
     * </p>
     * 设置标签（包含修改，单个标签操作）
     * </p>
     * 
     * @param param
     * @return false则为标签重复
     * @author fanp
     * @date 2015年7月2日
     */
    public void addTag(TagParam param) {
        // 1.判断是否能添加标签
        // 2.参数校验
        // 3.判断标签是否重复
        // 4.删除标签
        // 5.添加标签

        // 判断是否能添加标签
        UserSession session = ReqUtil.instance.getUser();

        // 用户类型为doctor时可以添加doctorPatient，doctorFriend标签，
        // 为patient时可以添加patientFriend标签，其他情况不能添加
        if (session == null) {
            throw new ServiceException("无法添加标签");
        } else if (session.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
                throw new ServiceException("无法添加标签");
            }
        } else if (session.getUserType() == UserEnum.UserType.patient.getIndex()) {
            if (param.getTagType() != UserEnum.TagType.patientFriend.getIndex()) {
                throw new ServiceException("无法添加标签");
            }
        }

        if (StringUtil.isBlank(param.getTagName())) {
            throw new ServiceException("标签名称为空");
        }
        param.setTagName(param.getTagName().toString());
        if (StringUtil.isNotBlank(param.getOldName())) {
            param.setOldName(param.getOldName().toString());
        }
        param.setSys(false);
        int count = tagDao.getTagCount(param);
        if (count == 0) {
            /**
             * 标签不存在时: <br>
             * a.没有原始标签则为添加;<br>
             * b.原始标签与新标签不等则为修改标签;<br>
             * c.如果原始标签与新标签相等则传递数据有误，不做处理
             */
            if (StringUtil.isBlank(param.getOldName())) {
                // 添加标签,关系的标签
                Tag tag = new Tag();
                BeanUtils.copyProperties(param, tag);
                addTag(tag);
                tagDao.addRelationTag(param);
            } else if (!StringUtil.equals(param.getOldName(), param.getTagName())) {
                // 修改标签,关系的标签
                tagDao.updateTag(param);
                tagDao.deleteRelationTag(param);
                tagDao.addRelationTag(param);
            }
        } else {
            /**
             * 标签存在时: <br>
             * a.如果原始标签与新标签相等则为修改关系的标签，标签不修改;<br>
             * b.其他情况则为重复添加;
             */
            if (StringUtil.equals(param.getOldName(), param.getTagName())) {
                // 修改关系的标签
                tagDao.deleteRelationTag(param);
                tagDao.addRelationTag(param);
            } else {
                // 标签重复
                throw new ServiceException("标签名称已存在");
            }

        }

    }

    /**
     * </p>
     * 删除标签
     * </p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月3日
     */
    public void deleteTag(TagParam param) {
        // 根据用户类型确定所在集合
        tagDao.deleteTag(param);
        param.setOldName(param.getTagName());
        tagDao.deleteRelationTag(param);
    }

    /**
     * </p>
     * 修改会员标签（多标签操作）
     * </p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月9日
     */
    public void updateUserTag(TagParam param) {
        // 1.判断是否能添加标签
        // 2.参数校验
        // 3.判断标签是否重复
        // 4.删除会员标签
        // 5.添加会员标签

        // 判断是否能添加标签
        UserSession session = ReqUtil.instance.getUser();

        // 用户类型为doctor时可以添加doctorPatient，doctorFriend标签，
        // 为patient时可以添加patientFriend标签，其他情况不能添加
        if (session == null) {
            throw new ServiceException("无法添加标签");
        } else if (session.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
                throw new ServiceException("无法添加标签");
            }
        } else if (session.getUserType() == UserEnum.UserType.patient.getIndex()) {
            if (param.getTagType() != UserEnum.TagType.patientFriend.getIndex()) {
                throw new ServiceException("无法添加标签");
            }
        }
        if (param.getId() == null) {
            return;
        }

        if (param.getTagNames() == null || param.getTagNames().length == 0) {
            // 没有传标签，则删除用户所有标签
            tagDao.deleteRelationTags(param);
            return;
        }

        // 去除空格
        String[] tagNames = param.getTagNames();
        List<String> tmp = new ArrayList<String>();
        for (String tagName : tagNames) {
            if (StringUtil.isNotBlank(tagName)) {
                tmp.add(tagName.trim());
            }
        }
        tagNames = tmp.toArray(new String[0]);
        if (tagNames == null || tagNames.length == 0) {
            // 没有传标签，则删除所有标签
            tagDao.deleteRelationTags(param);
            return;
        }
        param.setTagNames(tagNames);

        // 判断增加标签
        List<String> list = tagDao.getExistTag(param);
        List<String> addTag = new ArrayList<String>();// 增加标签
        if (list.size() > 0) {
            for (String tagName : tagNames) {
                if (!list.contains(tagName)) {
                    addTag.add(tagName);
                }
            }
        } else {
            addTag.addAll(Arrays.asList(tagNames));
        }

        // 增加标签
        if (addTag.size() > 0) {
            for (String tagName : addTag) {
                Tag tag = new Tag();
                tag.setUserId(param.getUserId());
                tag.setTagName(tagName);
                tag.setTagType(param.getTagType());
                tag.setSys(false);
                addTag(tag);
            }
        }

        // 删除关系标签
        tagDao.deleteRelationTags(param);

        // 添加患者标签
        tagDao.addRelationTags(param);
    }

    @Override
    public boolean isSystemTag(String tag) {
        if (StringUtils.isBlank(tag)) {
            throw new ServiceException("标签不能为空");
        }
        return TagUtil.SYS_TAG.contains(tag);
    }

    public void addTag(Tag tag) {
        verify(tag.getTagName());
        tagDao.addTag(tag);
    }

    public void updateTag(TagParam param) {
        verify(param.getTagName().trim());
        tagDao.updateTag(param);
    }

    private void verify(String tagName) {
        if (TagUtil.SYS_TAG.contains(tagName)) {
            throw new ServiceException(tagName + "为系统标签，不能添加");
        }
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.trim(null));
    }

    @Override
    public List<String> getDoctorPatientSelfTag(Integer doctorId, Integer userId) {
        String[] tags = tagDao.getDoctorPatientTag(doctorId, userId);
        List<String> list = new ArrayList<String>();
        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                if (!TagUtil.SYS_TAG.contains(StringUtils.trim(tag))) {
                    list.add(tag);
                }
            }
        }
        return list;
    }

    @Override
    public boolean ifDoctorFriend(Integer userId, Integer toUserId) {
        return relationDao.ifDoctorFriend(userId, toUserId);
    }

    @Override
    public List<Integer> filterFriends(RelationType relationType, Integer userId,
            Integer[] filterUserIds) {
        if (Objects.isNull(userId)) {
            return new ArrayList<>();
        }
        if (Objects.isNull(filterUserIds) || filterUserIds.length == 0) {
            return new ArrayList<>();
        }

        List<UserDetailVO> userList = relationDao.getRelations(relationType, userId);

        List<Integer> friendUserIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            for (int i = 0; i < filterUserIds.length; i++) {
                for (UserDetailVO user : userList) {
                    if (Objects.equals(filterUserIds[i], user.getUserId())) {
                        friendUserIds.add(filterUserIds[i]);
                        break;
                    }
                }
            }
        }

        return friendUserIds;
    }

}
