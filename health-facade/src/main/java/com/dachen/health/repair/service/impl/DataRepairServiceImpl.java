package com.dachen.health.repair.service.impl;

import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.incomeNew.mapper.CashMapper;
import com.dachen.health.pack.incomeNew.mapper.ExpendMapper;
import com.dachen.health.pack.incomeNew.mapper.IncomelogMapper;
import com.dachen.health.pack.incomeNew.mapper.RefundOrderMapper;
import com.dachen.health.pack.incomeNew.mapper.SettleMapperNew;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.patient.mapper.ImageDataMapper;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.model.ImageDataExample;
import com.dachen.health.pack.patient.model.OrderSessionExample;
import com.dachen.health.repair.service.IDataRepairService;
import com.dachen.health.user.entity.po.Doctor;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataRepairServiceImpl implements IDataRepairService {

	static Logger logger = LoggerFactory.getLogger(DataRepairServiceImpl.class);

	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;
	
	@Autowired
    private OrderMapper orderMapper;
	@Autowired
    private OrderSessionMapper orderSessionMapper;
	@Autowired
	private IncomelogMapper incomelogMapper;
	@Autowired
	private SettleMapperNew settleMapperNew;
	@Autowired
	private CashMapper cashMapper;
	@Autowired
	private ExpendMapper expendMapper;
	@Autowired
	private RefundOrderMapper refundOrderMapper;
	@Autowired
	private IncomelogService incomelogService;
	@Autowired
	ImageDataMapper imageDataMapper;
	@Autowired
	UserManager userManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	IBaseDataService baseDataService;
	
	public int repairDoctorStatus() {
		int pageSize = 500;

		int total = 0;
		for (int pageIndex = 0; true; pageIndex++) {
			List<User> users = dsForRW.createQuery(User.class)
				.filter("userType", UserType.doctor.getIndex())
				.filter("status", UserStatus.Unautherized.getIndex())
				.retrievedFields(true,"_id")
				.offset(pageIndex * pageSize).limit(pageSize)
				.asList();
			if (users.size() == 0) {
				break;
			}
			List<Integer> userIdList = new ArrayList<>();
			for (User user : users) {
				ImageDataExample example = new ImageDataExample();
				example.createCriteria()
					.andImageTypeEqualTo(ImageDataEnum.doctorCheckImage.getIndex())
					.andUserIdEqualTo(user.getUserId())
					.andImageUrlIsNotNull();
				int count = imageDataMapper.countByExample(example);
				if (count > 0) {
					userIdList.add(user.getUserId());
				}
			}
			if (userIdList.size() == 0) {
				continue;
			}
			logger.info("第{}页：用户ID.size={}, list={}", pageIndex+1, userIdList.size(), userIdList);
			Query<User> q = dsForRW.createQuery(User.class).filter("_id in", userIdList);
			UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
			ops.set("status", UserStatus.uncheck.getIndex());
			UpdateResults results = dsForRW.update(q, ops);
			total = total + results.getUpdatedCount();
		}
		logger.info("total.UpdatedCount={}", total);
		return total;
	}

	public void syncDoctorHospital() {
		int pageSize = 500;
		for (int pageIndex = 0; true; pageIndex++) {
			List<HospitalPO> pos = dsForRW.createQuery(HospitalPO.class).filter("status", 1).retrievedFields(true,"_id", "name","province","city","country")
				.offset(pageIndex * pageSize).limit(pageSize)
				.asList();
			if (pos.size() == 0) {
				break;
			}
			for (HospitalPO po : pos) {
				List<User> users = dsForRW.createQuery(User.class).filter("doctor.hospitalId", po.getId())
					.retrievedFields(true, "doctor.hospital","doctor.provinceId","doctor.cityId","doctor.countryId").asList();
				for (User user : users) {
					try {
						if (user.getDoctor() == null) {
							continue;
						}
						Doctor doctor = user.getDoctor();
						if ((po.getName() != null && !po.getName().equals(doctor.getHospital()))
							|| (doctor.getProvinceId() != null && doctor.getProvinceId() != po.getProvince())
							|| (doctor.getCityId() != null && doctor.getCityId() != po.getCity())
							|| (doctor.getCountryId() != null && doctor.getCountryId() != po.getCountry())
							) {
							HospitalVO hospitalVO = new HospitalVO();
							hospitalVO.setId(po.getId());
							hospitalVO.setName(po.getName());
							hospitalVO.setProvince(po.getProvince());
							hospitalVO.setProvinceName(baseDataService.getAreaNameByCode(po.getProvince()));
							hospitalVO.setCity(po.getCity());
							hospitalVO.setCityName(baseDataService.getAreaNameByCode(po.getCity()));
							hospitalVO.setCountry(po.getCountry());
							hospitalVO.setCountryName(baseDataService.getAreaNameByCode(po.getCountry()));
							boolean updateSuccess = userRepository.updateDoctorHospital(user.getUserId(), hospitalVO);
							if (updateSuccess && !po.getName().equals(doctor.getHospital())) {
								userManager.userInfoChangeNotify(user.getUserId());
							}
							logger.info(
								"syncDoctorHospital..同步成功hospital={_id:{},name:{}}, user={_id:{},hospital:{}}",
								po.getId(), po.getName(), user.getUserId(),
								user.getDoctor().getHospital());
						}
					} catch (Exception e) {
						logger.error(
							"syncDoctorHospital..同步失败hospital={_id:{},name:{}}, user={_id:{},hospital:{}}",
							po.getId(), po.getName(), user.getUserId(),
							user.getDoctor().getHospital());
					}
				}
			}
			logger.info("syncDoctorHospital..第{}页完成。。。", pageIndex+1);
		}
		logger.info("syncDoctorHospital..全部完成。。。");
	}

	/**
	 * 删除不存在的用户的相关数据
	 */
	public void deleteUserData() {
		//找出所有医生用户的idList
		List<String> useridList = new ArrayList<String>();
		DBCursor cursorUser = dsForRW.getDB().getCollection("user").find();
		while (cursorUser.hasNext()) {
			DBObject obj = cursorUser.next();
			useridList.add(obj.get("_id").toString());
		}
		
		cleanGroupDoctor(useridList);
		cleanOrder(useridList);
		cleanFriends(useridList,"u_doctor_patient");
		cleanFriends(useridList,"u_doctor_friend");
		cleanFriends(useridList,"u_patient_friend");
		
	}
	
	/**
	 * 清除医生集团关系表
	 */
	private void cleanGroupDoctor(List<String> useridList) {
		//医生集团用户表中的没有用户的脏数据
		BasicDBList dirtyGroupDoctorList = new BasicDBList();
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			if(!useridList.contains(obj.get("doctorId").toString()))
			{
				dirtyGroupDoctorList.add(Integer.valueOf(obj.get("doctorId").toString()));
			}
		}
		//清除医生集团用户表中的脏数据
		DBObject in = new BasicDBObject();
		in.put("$in", dirtyGroupDoctorList);
		if(dirtyGroupDoctorList.size()>0)
		{
		  dsForRW.getDB().getCollection("c_group_doctor").remove(new BasicDBObject("doctorId", in));
		}
	}
	

	/**
	 * 清除好友关系
	 */
	private void cleanFriends(List<String> useridList,String tableName) {
		//医生集团用户表中的没有用户的脏数据
		BasicDBList dirtyList = new BasicDBList();
		DBCursor cursor = dsForRW.getDB().getCollection(tableName).find();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			if(obj.get("userId")==null||!useridList.contains(obj.get("userId").toString()))
			{
				dirtyList.add(new ObjectId(obj.get("_id").toString()));
			}
			if(obj.get("toUserId")==null||!useridList.contains(obj.get("toUserId").toString()))
			{
				dirtyList.add(new ObjectId(obj.get("_id").toString()));
			}
		}
		//清除脏数据
		DBObject in = new BasicDBObject();
		in.put("$in", dirtyList);
		if(dirtyList.size()>0)
		{
		  dsForRW.getDB().getCollection(tableName).remove(new BasicDBObject("_id", in));
		}
	}
	
	/**
	 * 清除订单相关数据
	 */
	private void cleanOrder(List<String> useridList) {
		List<Integer> dirtyOrderList = new ArrayList<Integer>();
		
		 List<Order> orderList=orderMapper.getAll();
		 for (Order order : orderList) {
			 if(!useridList.contains(order.getDoctorId().toString())||!useridList.contains(order.getUserId().toString()))
			 {
				 dirtyOrderList.add(order.getId());
			 }
		}
		 for (Integer orderId : dirtyOrderList) {
			 //清除脏订单
			 orderMapper.deleteById(orderId);
			 
			 //清除脏订单关联的会话
			 OrderSessionExample example=new OrderSessionExample();
			 example.createCriteria().andOrderIdEqualTo(orderId);
			 orderSessionMapper.deleteByExample(example);
		}
	}

	@Override
	public void repairIncomes(Long timeStart) {
		//先调用转存服务
		incomelogService.autorConvertToNext(timeStart);
		//计算指定月份收入
		Long timeEnd = getNextMonthLong(timeStart);
		OrderParam param = new OrderParam();
		param.setStartCreateTime(timeStart);
		param.setEndCreateTime(timeEnd);
		List<Order> list = orderMapper.getOrdersToRepairIncomes(param);
		for(Order vo : list){
			incomelogService.addOrderIncomelog(vo);
		}
	}
	/**
	 * 删除所有收入相关数据
	 */
	@Override
	public void delIncomesData(){
		refundOrderMapper.deleteByExample(null);
		cashMapper.deleteByExample(null);
		expendMapper.deleteByExample(null);
		settleMapperNew.deleteByExample(null);
		incomelogMapper.deleteByExample(null);
	}
	private static Long getNextMonthLong(Long timeStart) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStart);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, 1);
		return calendar.getTime().getTime();
	}
}
