package com.dachen.commons.user;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.SimpleDoctorInfo;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.util.MongodbUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;
import com.google.common.collect.Maps;
import com.mongodb.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service(UserSessionService.BEAN_ID)
public class UserSessionService extends NoSqlRepository {

    public static final String BEAN_ID = "UserSessionService";

    public String getUserId(String telephone, int userType) {
        DBObject query = new BasicDBObject();
        DBObject projection = new BasicDBObject();

        query.put("telephone", telephone);
        query.put("userType", userType);

        projection.put("_id", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, projection);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            return obj.get("_id").toString();
        }
        return null;
    }

    public List<UserSession> getUserSessionByIds(List<Integer> userIds) {
        List<UserSession> users = new ArrayList<>();
        if (CollectionUtils.isEmpty(userIds)) {
            return users;
        }
        for (Integer id : userIds) {
            UserSession user = getUserSession(id);
            users.add(user);
        }
        return users;
    }

    public UserSession getUserSession(Integer userId) {
        Map<String, String> map = RedisUtil.hgetAll(KeyBuilder.userIdKey(userId));
        if (CollectionUtils.isEmpty(map)) {
            map = storeUserSessionInCache(userId);
        }
        if (!CollectionUtils.isEmpty(map)) {

            // 获取不到手机号，则再查询一遍
            Integer userType = Integer.parseInt(map.get("userType"));
            String phone = map.get("telephone");
            String birthday = map.get("birthday");
            String status = map.get("status");
            String createTime = map.get("createTime");

            if (StringUtils.isBlank(phone) || StringUtils.isBlank(birthday) || StringUtils.isBlank(status) || StringUtils.isBlank(createTime)) {
                map = storeUserSessionInCache(userId);
            }

            if (userType.intValue() == 3) {
                //若为医生
                String hospital = map.get("hospital");
                String departments = map.get("departments");
                String title = map.get("title");

                if (StringUtils.isBlank(phone) || StringUtils.isBlank(hospital) || StringUtils.isBlank(departments) || StringUtils.isBlank(title)) {
                    map = storeUserSessionInCache(userId);
                }
            }
            
            if(Objects.isNull(map.get("userId"))){
            	UserSession session = new UserSession();
                session.setUserId(0);
                session.setUserType(100);
                session.setName("游客");
                return session;
            }

            UserSession session = new UserSession();
            session.setUserId(Integer.parseInt(map.get("userId")));
            session.setUserType(Integer.parseInt(map.get("userType")));
            session.setSex(StringUtils.isEmpty(map.get("sex")) ? null : Integer.parseInt(map.get("sex")));
            session.setName(map.get("name"));

            // fixbug: 解决sex=""时的报错
            Integer sexVal = StringUtils.isEmpty(map.get("sex")) ? null : Integer.parseInt(map.get("sex"));
            String headPicFileName = session.getHeadImgPath(map.get("headPicFileName"), userId, sexVal, Integer.parseInt(map.get("userType")));
            session.setHeadPicFileName(headPicFileName);

            session.setTelephone(map.get("telephone"));
            session.setTerminal(map.get("terminal"));
            if (session.getUserType() == 3) {
                //如果存在专业特长expertise，则取专业特长的name属性，拼接到skill的前面
                String expertiseStr = "";
                if (map.containsKey("expertise") && Objects.nonNull(map.get("expertise")) && !StringUtils.equals("null", map.get("expertise"))) {
                    String[] expertiseArray =  map.get("expertise")
                            .replace("[","")
                            .replace("]","")
                            .replace("\"","")
                            .split(",");
                    for(int i=0;i<expertiseArray.length;i++){
                        expertiseArray[i] = expertiseArray[i].trim();
                    }
                    DBObject query = new BasicDBObject();
                    query.put("_id", new BasicDBObject("$in", expertiseArray));
                    DBObject projection = new BasicDBObject();
                    projection.put("name", 1);
                    DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type").find(query, projection);
                    while(cursor.hasNext()){
                        DBObject obj = cursor.next();
                        expertiseStr += obj.get("name") + "，";
                    }
                }

                String expertiseAndSkill = "";
                if (map.containsKey("skill") && Objects.nonNull(map.get("skill")) && !StringUtils.equals("null", map.get("skill"))) {
                    String skillStr = map.get("skill");
                    expertiseAndSkill = expertiseStr + skillStr;
                } else if (StringUtils.isNotBlank(expertiseStr)) {
                    expertiseAndSkill = expertiseStr.substring(0,expertiseStr.length()-1);
                }

                SimpleDoctorInfo doc = new SimpleDoctorInfo(map.get("doctorNum"), map.get("hospital"), map.get("departments"), map.get("title"), expertiseAndSkill);
                if (map.containsKey("departments") && Objects.nonNull(map.get("departments")) && !StringUtils.equals("null", map.get("departments"))) {
                    doc.setDepartments(map.get("departments"));
                }
                if (map.containsKey("hospitalId") && Objects.nonNull(map.get("hospitalId")) && !StringUtils.equals("null", map.get("hospitalId"))) {
                    doc.setHospitalId(map.get("hospitalId"));
                }
                if (map.containsKey("deptId") && Objects.nonNull(map.get("deptId")) && !StringUtils.equals("null", map.get("deptId"))) {
                    doc.setDeptId(map.get("deptId"));
                }
                if (map.containsKey("provinceId") && Objects.nonNull(map.get("provinceId")) && !StringUtils.equals("null", map.get("provinceId"))) {
                    doc.setProvinceId(Integer.valueOf(map.get("provinceId")));
                }
                if (map.containsKey("cityId") && Objects.nonNull(map.get("cityId")) && !StringUtils.equals("null", map.get("cityId"))) {
                    doc.setCityId(Integer.valueOf(map.get("cityId")));
                }
                if (map.containsKey("countryId") && Objects.nonNull(map.get("countryId")) && !StringUtils.equals("null", map.get("countryId"))) {
                    doc.setCountryId(Integer.valueOf(map.get("countryId")));
                }
                session.setDoctor(doc);
            }
            if (map.containsKey("birthday") && Objects.nonNull(map.get("birthday")) && !StringUtils.equals("null", map.get("birthday"))) {
                session.setBirthday(Long.parseLong(map.get("birthday")));
            }
            if (map.containsKey("status") && Objects.nonNull(map.get("status")) && !StringUtils.equals("null", map.get("status"))) {
                session.setStatus(Integer.parseInt(map.get("status")));
            }
            if (map.containsKey("suspend") && Objects.nonNull(map.get("suspend")) && !StringUtils.equals("null", map.get("suspend"))) {
                session.setSuspend(Integer.parseInt(map.get("suspend")));
            }
            if (map.containsKey("createTime") && Objects.nonNull(map.get("createTime")) && !StringUtils.equals("null", map.get("birthday"))) {
                session.setCreateTime(Long.parseLong(map.get("createTime")));
            }
            if (map.containsKey("lastLoginTime") && Objects.nonNull(map.get("lastLoginTime")) && !StringUtils.equals("null", map.get("lastLoginTime"))) {
                session.setLastLoginTime(Long.parseLong(map.get("lastLoginTime")));
            }
            if (map.containsKey("userLevel") && Objects.nonNull(map.get("userLevel")) && !StringUtils.equals("null", map.get("userLevel"))) {
                session.setUserLevel(Integer.valueOf(map.get("userLevel")));
            }

            return session;
        } else {
            UserSession session = new UserSession();
            session.setUserId(0);
            session.setUserType(100);
            session.setName("游客");
            return session;
        }

    }

    public Map<String, String> storeUserSessionInCache(Integer userId) {
        Map<String, String> map = Maps.newConcurrentMap();
        DBObject query = new BasicDBObject();
        DBObject projection = new BasicDBObject();
        query.put("_id", userId);
        projection.put("_id", 1);
        projection.put("name", 1);
        projection.put("userType", 1);
        projection.put("headPicFileName", 1);
        projection.put("telephone", 1);
        projection.put("source.terminal", 1);
        projection.put("sex", 1);
        projection.put("birthday", 1);
        projection.put("status", 1);
        projection.put("suspend", 1);
        projection.put("createTime", 1);
        projection.put("lastLoginTime", 1);
        projection.put("userLevel", 1);
        projection.put("doctor.hospital", 1);
        projection.put("doctor.departments", 1);
        projection.put("doctor.doctorNum", 1);
        projection.put("doctor.title", 1);
        projection.put("doctor.skill", 1);
        projection.put("doctor.hospitalId", 1);
        projection.put("doctor.deptId", 1);
        projection.put("doctor.provinceId", 1);
        projection.put("doctor.cityId", 1);
        projection.put("doctor.countryId", 1);
        projection.put("doctor.expertise", 1);
        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, projection);
        while (cursor.hasNext()) {
            map = Maps.newHashMap();
            DBObject obj = cursor.next();
            map.put("userId", String.valueOf(userId));
            Integer userType = MongodbUtil.getInteger(obj, "userType");
            if (Objects.nonNull(userType)) {
                map.put("userType", String.valueOf(userType));
            }
            String name = MongodbUtil.getString(obj, "name");
            if (StringUtils.isNotBlank(name)) {
                map.put("name", name);
            }
            String telephone = MongodbUtil.getString(obj, "telephone");
            if (StringUtils.isNotBlank(telephone)) {
                map.put("telephone", telephone);
            }
            String headPicFileName = MongodbUtil.getString(obj, "headPicFileName");
            map.put("headPicFileName", headPicFileName);

            Long birthday = MongodbUtil.getLong(obj, "birthday");
            if (Objects.nonNull(birthday)) {
                map.put("birthday", String.valueOf(birthday));
            }
            Integer status = MongodbUtil.getInteger(obj, "status");
            if (Objects.nonNull(status)) {
                map.put("status", String.valueOf(status));
            }
            Integer suspend = MongodbUtil.getInteger(obj, "suspend");
            if (Objects.nonNull(suspend)) {
                map.put("suspend", String.valueOf(suspend));
            }
            Long createTime = MongodbUtil.getLong(obj, "createTime");
            if (Objects.nonNull(createTime)) {
                map.put("createTime", String.valueOf(createTime));
            }
            Long lastLoginTime = MongodbUtil.getLong(obj, "lastLoginTime");
            if (Objects.nonNull(lastLoginTime)) {
                map.put("lastLoginTime", String.valueOf(lastLoginTime));
            }
            Integer sex = MongodbUtil.getInteger(obj,"sex");
            if (Objects.nonNull(sex)) {
                map.put("sex", String.valueOf(sex));
            }
            Integer userLevel = MongodbUtil.getInteger(obj,"userLevel");
            if (Objects.nonNull(userLevel)) {
            	Long limitedPeriodTime = MongodbUtil.getLong(obj, "limitedPeriodTime");
                if (Objects.nonNull(limitedPeriodTime)&&System.currentTimeMillis()>limitedPeriodTime) {
                	//过期用户
                    map.put("userLevel", "0");
                }else{
                	map.put("userLevel", String.valueOf(userLevel));
                }
            }
            DBObject userSource = (DBObject) obj.get("source");
            if (userSource != null) {
                Integer terminal = MongodbUtil.getInteger(userSource, "terminal");
                if (terminal != null) {
                    map.put("terminal", String.valueOf(terminal));
                }
            }

            if (Objects.nonNull(userType) && userType.intValue() == 3) {
                Object doc = obj.get("doctor");
                if (Objects.nonNull(doc)) {
                    DBObject docValue = (DBObject) doc;
                    String hospital = MongodbUtil.getString(docValue, "hospital");
                    if (StringUtils.isNotBlank(hospital)) {
                        map.put("hospital", hospital);
                    }
                    String departments = MongodbUtil.getString(docValue, "departments");
                    if (StringUtils.isNotBlank(departments)) {
                        map.put("departments", departments);
                    }
                    String title = MongodbUtil.getString(docValue, "title");
                    if (StringUtils.isNotBlank(title)) {
                        map.put("title", title);
                    }
                    String skill = MongodbUtil.getString(docValue, "skill");
                    if (StringUtils.isNotBlank(skill)) {
                        map.put("skill", skill);
                    }
                    String doctorNum = MongodbUtil.getString(docValue, "doctorNum");
                    if (StringUtils.isNotBlank(doctorNum)) {
                        map.put("doctorNum", doctorNum);
                    }
                    String hospitalId = MongodbUtil.getString(docValue, "hospitalId");
                    if (StringUtils.isNotBlank(hospitalId)) {
                        map.put("hospitalId", hospitalId);
                    }
                    String deptId = MongodbUtil.getString(docValue, "deptId");
                    if (StringUtils.isNotBlank(deptId)) {
                        map.put("deptId", deptId);
                    }
                    String provinceId = MongodbUtil.getString(docValue, "provinceId");
                    if (StringUtils.isNotBlank(provinceId)) {
                        map.put("provinceId", provinceId);
                    }
                    String cityId = MongodbUtil.getString(docValue, "cityId");
                    if (StringUtils.isNotBlank(cityId)) {
                        map.put("cityId", cityId);
                    }
                    String countryId = MongodbUtil.getString(docValue, "countryId");
                    if (StringUtils.isNotBlank(countryId)) {
                        map.put("countryId", countryId);
                    }
                    String expertise = MongodbUtil.getString(docValue, "expertise");
                    if (StringUtils.isNotBlank(expertise)) {
                        map.put("expertise", expertise);
                    }
                }
            }

            jedisTemplate.hmset(KeyBuilder.userIdKey(userId), map);
        }

        return map;
    }

    public List<Map<String, Object>> getSimpleUserInfo(List<Integer> userId) {
        if (userId == null || userId.size() == 0) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Integer uid : userId) {
            UserSession session = this.getUserSession(uid);
            if (session == null) {
                return null;
            }
            String headImgPath = session.getHeadImgPath();
            Map<String, Object> eachUser = new HashMap<>();
            eachUser.put("id", session.getUserId());
            eachUser.put("name", session.getName());
            eachUser.put("userType", session.getUserType());
            eachUser.put("headPic", headImgPath);
            eachUser.put("telephone", session.getTelephone());
            eachUser.put("terminal", session.getTerminal());
            SimpleDoctorInfo doctorInfo = session.getDoctor();
            if (Objects.nonNull(doctorInfo)) {
                eachUser.put("title", doctorInfo.getTitle());
                eachUser.put("departments", doctorInfo.getDepartments());
            }

            result.add(eachUser);
        }

        return result;
    }

    public List<Map<String, String>> getDetailUserInfo(List<Integer> userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        DBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject(QueryOperators.IN, userId));
        DBObject projection = new BasicDBObject();

        projection.put("_id", 1);
        projection.put("name", 1);
        projection.put("userType", 1);
        projection.put("headPicFileName", 1);
        projection.put("telephone", 1);
        projection.put("source.terminal", 1);
        projection.put("sex", 1);
        projection.put("birthday", 1);
        projection.put("status", 1);
        projection.put("createTime", 1);

        projection.put("doctor.hospital", 1);
        projection.put("doctor.departments", 1);
        projection.put("doctor.title", 1);
        projection.put("doctor.skill", 1);
        projection.put("doctor.doctorNum", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, projection);
        while (cursor.hasNext()) {
            Map<String, String> map = Maps.newHashMap();
            DBObject obj = cursor.next();
            map.put("userId", String.valueOf(MongodbUtil.getInteger(obj, "_id")));
            Integer userType = MongodbUtil.getInteger(obj, "userType");
            if (Objects.nonNull(userType)) {
                map.put("userType", String.valueOf(userType));
            }
            String name = MongodbUtil.getString(obj, "name");
            if (StringUtils.isNotBlank(name)) {
                map.put("name", name);
            }
            String telephone = MongodbUtil.getString(obj, "telephone");
            if (StringUtils.isNotBlank(telephone)) {
                map.put("telephone", telephone);
            }
            String headPicFileName = MongodbUtil.getString(obj, "headPicFileName");
            map.put("headPicFileName", headPicFileName);

            Long birthday = MongodbUtil.getLong(obj, "birthday");
            if (Objects.nonNull(birthday)) {
                map.put("birthday", String.valueOf(birthday));
            }
            Integer status = MongodbUtil.getInteger(obj, "status");
            if (Objects.nonNull(status)) {
                map.put("status", String.valueOf(status));
            }
            Long createTime = MongodbUtil.getLong(obj, "createTime");
            if (Objects.nonNull(createTime)) {
                map.put("createTime", String.valueOf(createTime));
            }

            Integer sex = MongodbUtil.getInteger(obj,"sex");
            if (Objects.nonNull(sex)) {
                map.put("sex", String.valueOf(sex));
            }

            DBObject userSource = (DBObject) obj.get("source");
            if (userSource != null) {
                Integer terminal = MongodbUtil.getInteger(userSource, "terminal");
                map.put("terminal", String.valueOf(terminal));
            }

            if (Objects.nonNull(userType) && userType.intValue() == 3) {
                Object doc = obj.get("doctor");
                if (Objects.nonNull(doc)) {
                    DBObject docValue = (DBObject) doc;
                    String hospital = MongodbUtil.getString(docValue, "hospital");
                    if (StringUtils.isNotBlank(hospital)) {
                        map.put("hospital", hospital);
                    }
                    String departments = MongodbUtil.getString(docValue, "departments");
                    if (StringUtils.isNotBlank(departments)) {
                        map.put("departments", departments);
                    }
                    String title = MongodbUtil.getString(docValue, "title");
                    if (StringUtils.isNotBlank(title)) {
                        map.put("title", title);
                    }
                    String skill = MongodbUtil.getString(docValue, "skill");
                    if (StringUtils.isNotBlank(skill)) {
                        map.put("skill", skill);
                    }
                    String doctorNum = MongodbUtil.getString(docValue, "doctorNum");
                    if (StringUtils.isNotBlank(doctorNum)) {
                        map.put("doctorNum", doctorNum);
                    }
                }
            }

            jedisTemplate.hmset(KeyBuilder.userIdKey(MongodbUtil.getInteger(obj, "_id")), map);
            result.add(map);
        }
        cursor.close();
        return result;

    }

    public List<Map<String, String>> getModifiedUser(List<Integer> userId, Long startTime) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        DBObject query = new BasicDBObject();
        if (userId != null) {
            if (userId.size() == 0) {
                return result;
            }
            query.put("_id", new BasicDBObject("$in", userId));
        }
        if (startTime != null) {
            query.put("modifyTime", new BasicDBObject("$gte", startTime));
        }

        DBObject projection = new BasicDBObject();
        projection.put("_id", 1);
        projection.put("name", 1);
        projection.put("userType", 1);
        projection.put("headPicFileName", 1);
        projection.put("telephone", 1);
        projection.put("sex", 1);
        projection.put("birthday", 1);// 生日
        projection.put("source.terminal", 1);
        projection.put("status", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, projection);
        while (cursor.hasNext()) {
            Map<String, String> map = new HashMap<String, String>();
            DBObject obj = cursor.next();
            map.put("userId", MongodbUtil.getString(obj, "_id"));
            map.put("userType", MongodbUtil.getString(obj, "userType"));
            map.put("name", MongodbUtil.getString(obj, "name"));
            map.put("telephone", MongodbUtil.getString(obj, "telephone"));
            map.put("headPicFileName", MongodbUtil.getString(obj, "headPicFileName") == null ? "" : MongodbUtil.getString(obj, "headPicFileName"));
            map.put("sex", MongodbUtil.getString(obj, "sex") == null ? "" : MongodbUtil.getString(obj, "sex"));
            map.put("birthday", MongodbUtil.getString(obj, "birthday"));
            map.put("status", MongodbUtil.getString(obj, "status"));

            DBObject userSource = (DBObject) obj.get("source");
            if (userSource != null) {
                Integer terminal = MongodbUtil.getInteger(userSource, "terminal");
                map.put("terminal", String.valueOf(terminal));
            }
            result.add(map);
        }
        cursor.close();
        return result;
    }

}
