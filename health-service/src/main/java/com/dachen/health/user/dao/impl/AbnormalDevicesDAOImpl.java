package com.dachen.health.user.dao.impl;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.vo.UserLoginInfo;
import com.dachen.health.user.dao.IAbnormalDevicesDAO;
import com.dachen.health.user.entity.param.LoginDevicesParam;
import com.dachen.health.user.entity.po.AbnormalLoginDevice;
import com.dachen.health.user.entity.vo.AbnormalLoginVO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.mongodb.morphia.aggregation.Group.*;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 15:45
 * Description:
 */
@Repository
public class AbnormalDevicesDAOImpl extends NoSqlRepository implements IAbnormalDevicesDAO {

    @Override
    public Iterator<AbnormalLoginVO> statsLoginDevices(Long startTime, Long endTime, Long limit) {
        Query<UserLoginInfo> query = dsForRW.createQuery(UserLoginInfo.class);
        query.field("loginTime").greaterThanOrEq(startTime);
        query.field("loginTime").lessThan(endTime);
        String blankStr = "";
        String telephone = "telephone";
        String serial = "serial";
        String id_serial = "_id.serial";
        String count = "count";
        query.field(telephone).exists();
        query.field(serial).exists();
        query.field(serial).notEqual(blankStr);
        Query<AbnormalLoginVO> queryPipeline = dsForRW.createQuery(AbnormalLoginVO.class);
        // count大于或等于配置数
        queryPipeline.field(count).greaterThanOrEq(limit);
        AggregationPipeline pipeline = dsForRW.createAggregation(UserLoginInfo.class);
        // 通过设备号和手机号分组
        List<Group> g1 = id(grouping(serial), grouping(telephone));
        // 从G1的分组结果再用设备号分组
        List<Group> g2 = id(grouping(serial, id_serial));
        // 导出设备号
        Group serialPro = grouping(serial, first(id_serial));
        // 计算分组数量
        Group countPro = grouping(count, new Accumulator("$sum", 1));
        pipeline.match(query).group(g1).group(g2, serialPro, countPro).match(queryPipeline);
        Iterator<AbnormalLoginVO> iterator = pipeline.aggregate(AbnormalLoginVO.class);
        return iterator;
    }

    @Override
    public void updateLoginDevice(AbnormalLoginVO abnormalLoginDevice) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("deviceId").equal(abnormalLoginDevice.getSerial());
        // 待处理
        query.field("status").equal(0);
        UpdateOperations<AbnormalLoginDevice> ops = dsForRW.createUpdateOperations(AbnormalLoginDevice.class);
        ops.inc("abnormalCount");
        ops.set("loginUsers", abnormalLoginDevice.getLoginUser());
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(query, ops);

    }

    @Override
    public AbnormalLoginDevice getAbnormalLoginDeviceByDeviceId(String deviceId, Integer status) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("deviceId").equal(deviceId);
        query.field("status").equal(status);
        return query.get();
    }

    @Override
    public Iterator<UserLoginInfo> getUserLoginInfoByDeviceIds(Long startTime, Long endTime, List<String> deviceIds) {
        Query<UserLoginInfo> query = dsForRW.createQuery(UserLoginInfo.class);
        query.field("loginTime").greaterThanOrEq(startTime);
        query.field("loginTime").lessThan(endTime);
        String serial = "serial";
        String telephone = "telephone";
        String loginTime = "loginTime";
        query.field(serial).in(deviceIds);
        AggregationPipeline pipeline = dsForRW.createAggregation(UserLoginInfo.class);
        List<Group> g = id(grouping(serial), grouping(telephone));
        Group minLoginTime = grouping(loginTime, max(loginTime));
        // 导出手机号 最近登录时间 设备号
        Group telephonePro = grouping(telephone, first(telephone));
        Group loginTimePro = grouping(loginTime, first(loginTime));
        Group serialPro = grouping(serial, first(serial));
        pipeline.match(query).group(g, minLoginTime, telephonePro, loginTimePro, serialPro);
        Iterator<UserLoginInfo> iterator = pipeline.aggregate(UserLoginInfo.class);
        return iterator;
    }

    @Override
    public Boolean existsAbnormalLogin(String deviceId) {
        DBObject query = new BasicDBObject();
        DBObject projection = new BasicDBObject();
        query.put("deviceId", deviceId);
        // 未处理
        query.put("status", 0);
        int count = dsForRW.getDB().getCollection("t_abnormal_device").find(query, projection).count();
        return count > 0;
    }

    @Override
    public void insertAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice) {
        AbnormalLoginDevice abnormalLoginDeviceObject = new AbnormalLoginDevice();
        // 待处理
        abnormalLoginDeviceObject.setStatus(0);
        abnormalLoginDeviceObject.setDeviceId(abnormalLoginDevice.getSerial());
        abnormalLoginDeviceObject.setLoginUsers(abnormalLoginDevice.getLoginUser());
        // 取设备的异常次数
        abnormalLoginDeviceObject.setAbnormalCount(abnormalLoginDevice.getCount());
        abnormalLoginDeviceObject.setImportant(abnormalLoginDevice.getImportant());
        long timeMillis = System.currentTimeMillis();
        abnormalLoginDeviceObject.setCreateTime(timeMillis);
        abnormalLoginDeviceObject.setModifyTime(timeMillis);
        dsForRW.insert(abnormalLoginDeviceObject);
    }

    @Override
    public PageVO getAbnormalLoginDevices(LoginDevicesParam param) {
        if (Objects.isNull(param.getPageSize())) {
            param.setPageSize(10);
        }
        if (Objects.isNull(param.getPageIndex())) {
            param.setPageIndex(0);
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(param.getPageIndex());
        pageVO.setPageSize(param.getPageSize());
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("status").equal(param.getStatus());
        if (Objects.nonNull(param.getOnlyImportant()) && Objects.equals(param.getOnlyImportant(), Boolean.TRUE)) {
            query.field("important").equal(Boolean.TRUE);
        }
        if (StringUtils.isNoneBlank(param.getStartTime())) {
            Long start = Long.parseLong(param.getStartTime());
            if (Objects.nonNull(start)) {
                query.field("loginUsers.loginTime").greaterThanOrEq(start);
            }
        }
        if (StringUtils.isNoneBlank(param.getEndTime())) {
            Long end = Long.parseLong(param.getEndTime());
            if (Objects.nonNull(end)) {
                query.field("loginUsers.loginTime").lessThanOrEq(end);
            }
        }
        if (StringUtils.isNoneBlank(param.getSource())) {
            query.field("loginUsers.source").equal(param.getSource());
        }
        if (StringUtils.isNotBlank(param.getPhone())) {
            query.field("loginUsers.phone").equal(param.getPhone());
        }
        if (StringUtils.isNotBlank(param.getTitle())) {
            query.field("loginUsers.title").equal(param.getTitle());
        }
        if (StringUtils.isNotBlank(param.getName())) {
            query.field("loginUsers.userName").equal(param.getName());
        }
        if (StringUtils.isNotBlank(param.getHospital())) {
            query.field("loginUsers.hospital").equal(param.getHospital());
        }
        if (StringUtils.isNotBlank(param.getInviterName())) {
            query.field("loginUsers.inviteName").equal(param.getInviterName());
        }
        query.order("-createTime");
        pageVO.setTotal(query.countAll());
        query.offset(param.getPageIndex() * param.getPageSize());
        query.limit(param.getPageSize());
        pageVO.setPageData(query.asList());
        return pageVO;
    }

    @Override
    public void updateStatus(String id, Integer handlerId, String handlerName) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("deviceId").equal(id);
        query.field("status").equal(0);
        UpdateOperations<AbnormalLoginDevice> ops = dsForRW.createUpdateOperations(AbnormalLoginDevice.class);
        long timeMillis = System.currentTimeMillis();
        ops.set("status", 1);
        ops.set("handlerId", handlerId);
        ops.set("handlerName", handlerName);
        ops.set("handleTime", timeMillis);
        ops.set("modifyTime", timeMillis);
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public void updateImportant(String deviceId, Boolean status) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("deviceId").equal(deviceId);
        UpdateOperations<AbnormalLoginDevice> ops = dsForRW.createUpdateOperations(AbnormalLoginDevice.class);
        long timeMillis = System.currentTimeMillis();
        ops.set("important", status);
        ops.set("modifyTime", timeMillis);
        dsForRW.update(query, ops);
    }

    @Override
    public List<AbnormalLoginDevice> listAllQuery(LoginDevicesParam param) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("status").equal(param.getStatus());
        if (Objects.nonNull(param.getOnlyImportant()) && Objects.equals(param.getOnlyImportant(), Boolean.TRUE)) {
            query.field("important").equal(Boolean.TRUE);
        }
        if (StringUtils.isNoneBlank(param.getStartTime())) {
            Long start = Long.parseLong(param.getStartTime());
            if (Objects.nonNull(start)) {
                query.field("loginUsers.loginTime").greaterThanOrEq(start);
            }
        }
        if (StringUtils.isNoneBlank(param.getEndTime())) {
            Long end = Long.parseLong(param.getEndTime());
            if (Objects.nonNull(end)) {
                query.field("loginUsers.loginTime").lessThanOrEq(end);
            }
        }
        if (StringUtils.isNoneBlank(param.getSource())) {
            query.field("loginUsers.source").equal(param.getSource());
        }
        if (StringUtils.isNotBlank(param.getPhone())) {
            query.field("loginUsers.phone").contains(param.getPhone());
        }
        if (StringUtils.isNotBlank(param.getTitle())) {
            query.field("loginUsers.title").equal(param.getTitle());
        }
        if (StringUtils.isNotBlank(param.getName())) {
            query.field("loginUsers.userName").contains(param.getName());
        }
        if (StringUtils.isNotBlank(param.getHospital())) {
            query.field("loginUsers.hospital").contains(param.getHospital());
        }
        if (StringUtils.isNotBlank(param.getInviterName())) {
            query.field("loginUsers.InviteName").contains(param.getInviterName());
        }
        return query.asList();
    }

    @Override
    public void updateHandleDeviceCount(String deviceId) {
        Query<AbnormalLoginDevice> query = dsForRW.createQuery(AbnormalLoginDevice.class);
        query.field("deviceId").equal(deviceId);
        query.field("status").equal(1);
        UpdateOperations<AbnormalLoginDevice> ops = dsForRW.createUpdateOperations(AbnormalLoginDevice.class);
        long timeMillis = System.currentTimeMillis();
        ops.inc("abnormalCount");
        ops.set("modifyTime", timeMillis);
        dsForRW.update(query, ops);
    }

}
