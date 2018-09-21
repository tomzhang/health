package com.dachen.health.community.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.community.dao.ICircleDao;
import com.dachen.health.community.dao.ITopicDao;
import com.dachen.health.community.entity.po.Circle;
import com.dachen.health.community.service.ICircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CircleServiceImpl implements ICircleService {
    @Autowired
    private ICircleDao circleDao;
    @Autowired
    private ITopicDao topicDao;

    @Override
    public List<Circle> getByGroupCircle(String groupId) {
        //先去获取有没有默认属性的
        Circle circle = circleDao.mainCircle(groupId);
        if (circle == null) {
            circle = new Circle();
            circle.setGroupId(groupId);
            circle.setName("集团信息");
            circle.setMain("1");
            circle.setState(CommunityEnum.CommunityCircleState.正常.getIndex());
            circleDao.insert(circle);
        }

        // TODO Auto-generated method stub
        return circleDao.getByGroupCircle(groupId);
    }

    @Override
    public void addCircle(String name, String groupId) {
        //校验时候有同名的圈子
        Circle vali = circleDao.getByNameCircle(groupId, name);
        if (vali != null) {
            throw new ServiceException("该名称的栏目已经存在");
        }

        Circle circle = new Circle();
        circle.setGroupId(groupId);
        circle.setName(name);
        //保证新增的圈子排在第一个
        Circle upCircle = circleDao.firstCircle(groupId);
        if (upCircle == null) {
            circle.setTop(0L);
        } else {
            circle.setTop(upCircle.getTop() + 1);
        }
        circle.setState(CommunityEnum.CommunityCircleState.正常.getIndex());
        circleDao.insert(circle);
    }

    @Override
    public void topCircle(String id, String type, String groupId) {
        Circle circle = circleDao.getByPK(Circle.class, id);
        if (type.equals("0")) {
            Circle upCircle = circleDao.upCircle(groupId, circle.getTop());
            if (upCircle == null) {
                throw new ServiceException("已经是第一个了");
            } else {
                Map upMap = new HashMap<>();
                Map map = new HashMap<>();
                upMap.put("top", circle.getTop());
                map.put("top", upCircle.getTop());
                //分别更新两条记录
                circleDao.update(Circle.class, circle.getId(), map);
                circleDao.update(Circle.class, upCircle.getId(), upMap);
            }

        } else {
            Circle nextCircle = circleDao.nextCircle(groupId, circle.getTop());
            if (nextCircle == null) {
                throw new ServiceException("已经是最后一个了");
            } else {
                Map nextMap = new HashMap<String, Object>();
                Map map = new HashMap<>();
                nextMap.put("top", circle.getTop());
                map.put("top", nextCircle.getTop());
                //分别更新两条记录
                circleDao.update(Circle.class, circle.getId(), map);
                circleDao.update(Circle.class, nextCircle.getId(), nextMap);
            }
        }

    }

    @Override
    public void deleteCircle(String id) {
        //先判断该圈子下面时候有帖子
        long topicCount = topicDao.getCircleCount(id);
        if (topicCount > 0) {
            throw new ServiceException("该栏目下面有帖子，请删除对应栏目下面的帖子，再进行删除操作");
        }

        Circle circle = circleDao.getByPK(Circle.class, id);
        if (circle == null || circle.getState() == CommunityEnum.CommunityCircleState.已删除.getIndex()) {
            throw new ServiceException("该条记录不存在或已被删除");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("state", CommunityEnum.CommunityCircleState.已删除.getIndex());
        circleDao.update(Circle.class, id, map);
    }

    @Override
    public void updateCircle(String id, String name) {
    
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        circleDao.update(Circle.class, id, map);

    }


}
