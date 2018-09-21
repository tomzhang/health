package com.dachen.health.user.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.dao.IdxRepository.idxType;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.entity.vo.TitleVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.constants.UserNurseEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.document.constant.DocumentEnum;
import com.dachen.health.document.entity.vo.ContentTypeVO;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.health.user.dao.INurseDao;
import com.dachen.health.user.entity.param.NurseParam;
import com.dachen.health.user.entity.po.NurseImage;
import com.dachen.health.user.entity.vo.NurseVO;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class NurseDaoImpl extends NoSqlRepository implements INurseDao {

	@Autowired
	private IBaseDataDao baseDataDao;
	@Autowired
	protected IdxRepository idxRepository;

	/**
	 * </p>修改医生职业信息，未认证是修改认证信息也是修改职业信息</p>
	 * 
	 * @param param
	 * @author fanp
	 * @date 2015年7月13日
	 */
	public void updateCheckInfo(NurseParam param) {
		DBObject query = new BasicDBObject();
		query.put("_id", param.getUserId());
		query.put("userType", UserEnum.UserType.nurse.getIndex());

		if (param.getStatuses() != null && param.getStatuses().length > 0) {
			query.put("status", new BasicDBObject("$in", param.getStatuses()));
		}

		BasicDBObject update = new BasicDBObject();

		if (StringUtil.isNotBlank(param.getIdCard())) {// idCard
			update.put("nurse.idCard", param.getIdCard());
		}
		if (StringUtil.isNotBlank(param.getName())) {
			update.put("name", param.getName());
		}
        //医院名称校验
		if (StringUtil.isNotBlank(param.getHospital())
				&& StringUtil.isNotBlank(param.getHospitalId())) {
			HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
			if (hvo == null
					|| !StringUtil.equals(hvo.getName(), param.getHospital())) {
				throw new ServiceException("医院有误，请重新选择");
			}
		}
		if (StringUtil.isNotBlank(param.getHospital())) {
			update.put("nurse.hospital", StringUtil.trim(param.getHospital()));
		}
		if (StringUtil.isNotBlank(param.getHospitalId())) {
			update.put("nurse.hospitalId", StringUtil.trim(param.getHospitalId()));
		}
		
		
		if (StringUtil.isNotBlank(param.getTitle())) {
			TitleVO tvo = baseDataDao.getTitle(param.getTitle());
			if (tvo == null) {
				throw new ServiceException("职称有误，请重新选择");
			}

			update.put("nurse.title", StringUtil.trim(param.getTitle()));
		}

		/*
		 * private Integer provinceId; private Integer cityId; private Integer
		 * countryId; private String province; private String city; private
		 * String country;
		 */
		if (param.provinceId > 0 && StringUtil.isNotBlank(param.provinceName)) {
			// update
			update.put("nurse.provinceId", param.provinceId);
			update.put("nurse.province", param.provinceName);
		}

		if (param.cityId > 0 && StringUtil.isNotBlank(param.cityName)) {
			// update
			update.put("nurse.cityId", param.cityId);
			update.put("nurse.city", param.cityName);
		}

		if (param.countyId > 0 && StringUtil.isNotBlank(param.countyName)) {
			// update
			update.put("nurse.countryId", param.countyId);
			update.put("nurse.country", param.countyName);
		}

		// 医生重新提交审核信息时需修改
		if (param.getStatus() != null) {
			update.put("status", param.getStatus());
		}

		if (!update.isEmpty()) {
			dsForRW.getDB().getCollection("user")
					.update(query, new BasicDBObject("$set", update));
			
			dealImages(param);//更新以后再处理图片
		}
	}

	/**
	 * </p>医生获取认证信息</p>
	 * 
	 * @param param
	 * @author fanp
	 * @date 2015年7月15日
	 */
	public NurseVO getCheckInfo(NurseParam param) {
		DBObject query = new BasicDBObject();
		query.put("_id", param.getUserId());
		query.put("userType", UserEnum.UserType.nurse.getIndex());

		DBObject field = new BasicDBObject();
		field.put("_id", 0);
		field.put("name", 1);
		field.put("status", 1);
		field.put("nurse", 1);

		DBObject obj = dsForRW.getDB().getCollection("user")
				.findOne(query, field);
		if (obj != null) {
			NurseVO vo = new NurseVO();
			vo.setName(MongodbUtil.getString(obj, "name"));
			vo.setStatus(MongodbUtil.getInteger(obj, "status"));

			DBObject nurse = (BasicDBObject) obj.get("nurse");
			if (nurse != null) {
				vo.setIdCard(MongodbUtil.getString(nurse, "idCard"));
				// 医生未审核通过，查找职业信息
				vo.setHospital(MongodbUtil.getString(nurse, "hospital"));
				vo.setHospitalId(MongodbUtil.getString(nurse, "hospitalId"));
				vo.setDepartments(MongodbUtil.getString(nurse,
						"departments"));
				vo.setTitle(MongodbUtil.getString(nurse, "title"));
				DBObject check = (BasicDBObject) nurse.get("check");
				if (check != null) {
					vo.setRemark(MongodbUtil.getString(check, "remark"));
				}
				List<NurseImage> getNurseImageList=getNurseImageList(param.getUserId());
				List<Map<String,Object>> maps = new ArrayList<Map<String,Object>>();
				setUserImageToJson(maps, getNurseImageList);
				vo.setImages(maps);

				return vo;
			}
		}

		return null;
	}
	
	@Override
	public Map<String,Object> getNurseList(NurseParam param) {
		Map<String,Object> map = new HashMap<String,Object>();
		int limit = param.getPageSize();
		DBObject query = new BasicDBObject();
		query.put("userType", UserEnum.UserType.customerService.getIndex());
     	query.put("status", param.getStatus());
		DBObject sort = new BasicDBObject();
		sort.put("weight", 1);
		sort.put("createTime",-1);
		int skip = (param.getPageIndex()-1)*param.getPageSize();
		skip = skip < 0 ? 0 : skip;
		List<NurseVO> list = new ArrayList<NurseVO>();
		//Map<String, ContentTypeVO> ctMap = getAllType(param);
		DBCollection collection = dsForRW.getDB().getCollection("t_document");
		
		DBCursor cursor = collection.find(query).sort(sort).skip(skip).limit(limit);
		NurseVO vo = null;
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = setField(obj);
			list.add(vo);
		}
		cursor.close();
		map.put("count", collection.find(query).count());
		map.put("list", list);
		return map;
	}
	
	private NurseVO setField(DBObject obj){
		if(obj == null){
			return null;
		}
		NurseVO vo = new NurseVO();
		/*vo.setContent(MongodbUtil.getString(obj, "content"));
		vo.setContentType(MongodbUtil.getString(obj, "contentType"));
		vo.setCopyPath(MongodbUtil.getString(obj, "copyPath"));
		vo.setCreateTime(MongodbUtil.getLong(obj, "createTime"));
		vo.setDescription(MongodbUtil.getString(obj, "description"));
		vo.setDocumentType(MongodbUtil.getInteger(obj, "documentType"));
		vo.setId(MongodbUtil.getString(obj, "_id"));
		vo.setIsShow(MongodbUtil.getInteger(obj, "isShow"));
		vo.setIsTop(MongodbUtil.getInteger(obj, "isTop"));
		vo.setLastUpdateTime(MongodbUtil.getLong(obj, "lastUpdateTime"));
		vo.setTitle(MongodbUtil.getString(obj, "title"));
		vo.setVisitCount(MongodbUtil.getInteger(obj, "visitCount"));
		vo.setWeight(MongodbUtil.getInteger(obj, "weight"));
		vo.setUrl(MongodbUtil.getString(obj, "url"));*/
		return vo;
	}
	
	/**
	 * 更新护士信息
	 */
	@Override
	public User createNurse(Integer userId, NurseParam param) {
		Query<User> q = dsForRW.createQuery(User.class).field("_id")
				.equal(userId);
		User oldUser = q.get();
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

		if (UserType.nurse.getIndex() == oldUser.getUserType()) {
			// 护士
			if (param != null) {
				if (oldUser.getNurse() == null) {
					String nurseNum = idxRepository
							.nextDoctorNum(idxType.nurseNo);
					ops.set("nurse.nurseNum", nurseNum);
				}
				if (param.getIdCard() != null) {
					ops.set("nurse.idCard", param.getIdCard());
				}
				if (param.getHospital() != null) {
					ops.set("nurse.hospital", param.getHospital());
				}
				if (param.getHospitalId() != null) {
					ops.set("nurse.hospitalId", param.getHospitalId());
				}
				if (param.getDepartments() != null) {
					ops.set("nurse.departments", param.getDepartments());
				}
				if (param.getTitle() != null) {
					ops.set("nurse.title", param.getTitle());
				}
				ops.set("status",
						UserEnum.UserStatus.uncheck.getIndex());// 提交资料之后
																			// 就修改为审核中
				ops.set("name", param.getName());// 提交资料之后 就修改为审核中
			}
		}

		User user = dsForRW.findAndModify(q, ops);
		dealImages(param);// 创建图片
		return user;
	}

	/**
	 * 批量保存图片
	 * 
	 * @param userId
	 * @param param
	 * @return
	 */
	public void createNurseImage(Integer userId, List<NurseImage> paramList) {
		List<BasicDBObject> documentList = new ArrayList<BasicDBObject>();
		if (null != paramList && paramList.size() > 0) {
			
			deleteNurseImage(userId);// 图片不为空的时候 才进行删除
			int i = 0;
			for (NurseImage param : paramList) {
				BasicDBObject jo = new BasicDBObject();
				jo.put("userId", userId);// 索引
				jo.put("imageId", param.getImageId());
				jo.put("imageType", param.getImageType());
				jo.put("order", i);
				documentList.add(jo);
				i++;
			}
		}
		if (documentList.size() > 0) {
			dsForRW.getDB().getCollection("v_nurse_image").insert(documentList);
		}

	}

	/**
	 * 获取图片地址列表
	 * 
	 * @param userId
	 * @param param
	 * @return
	 */
	public List<NurseImage> getNurseImageList(Integer userId) {
		List<NurseImage> result = new ArrayList<NurseImage>();
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		Query<NurseImage> uq = dsForRW.createQuery(NurseImage.class)
				.field("userId").in(userIds);
		result = uq.asList();

		return result;
	}

	/**
	 * 删除图片地址
	 * 
	 * @param userId
	 * @param param
	 * @return
	 */
	public void deleteNurseImage(Integer userId) {
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		DBObject in = new BasicDBObject();
		in.put("$in", userIds);
		if (userIds.size() > 0) {
			dsForRW.getDB().getCollection("v_nurse_image")
					.remove(new BasicDBObject("userId", in));
		}
	}

	/**
	 * 获取图片
	 * 
	 * @param param
	 */
	private void dealImages(NurseParam param) {
		List<NurseImage> paramList = new ArrayList<NurseImage>();
		setUserImageList(param.getImages().split(","), paramList);// 组装图片对象
		createNurseImage(param.getUserId(), paramList);
	}

	/**
	 * 转换图片对象
	 * 
	 * @param strs
	 * @param paramList
	 */
	private void setUserImageList(String[] strs, List<NurseImage> paramList) {
		if (null != strs && strs.length > 0) {
			for (String str : strs) {
				if(StringUtils.isNotEmpty(str))
				{	
					NurseImage image = new NurseImage();
					image.setImageId(str);
					paramList.add(image);
				}
			}
		}
	}
	
	/**
	 * 转换图片对象
	 * 
	 * @param strs
	 * @param paramList
	 */
	private void setUserImageToJson(List<Map<String,Object>> maps, List<NurseImage> paramList) {
		if (null != paramList && paramList.size() > 0) {
			for (NurseImage str : paramList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("imageId", str.getImageId());
				map.put("imageType", str.getImageType());
				map.put("order", str.getOrder());
				maps.add(map);
			}
		}
	}
}
