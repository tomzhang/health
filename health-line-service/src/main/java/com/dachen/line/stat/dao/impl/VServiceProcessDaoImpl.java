package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessStatusEnum;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.param.ServiceProcessParm;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.mongodb.BasicDBObject;

@Repository
public class VServiceProcessDaoImpl extends NoSqlRepository implements
		IVServiceProcessDao {
	
	@Autowired
	private IVSPTrackingDao vspdao;

	@Override
	public List<VServiceProcess> getVServiceProcessList(String column,
			Object sourceId,Integer userId) {
		List<VServiceProcess> result = new ArrayList<VServiceProcess>();
		Query<VServiceProcess> uq = dsForRW.createQuery(VServiceProcess.class)
				.filter(column, sourceId).filter("userId", userId);// 查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public void insertVServiceProcess(VServiceProcess process) {
		dsForRW.insert(process);
	}

	/**
	 * 批量插入用户服务数据
	 * 
	 * @param userId
	 * @return
	 */
	public void updateVServiceProcess(String processServiceId, Integer status) {
		Query<VServiceProcess> result = dsForRW
				.createQuery(VServiceProcess.class).field("_id")
				.equal(new ObjectId(processServiceId));
		VServiceProcess process = result.get();
		if (null != process) {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(processServiceId));

			BasicDBObject update = new BasicDBObject();
			// update.put("_id", id);
			update.put("status", status);
			update.put("updTime", new Date().getTime());//更新时间
			if (!update.isEmpty()) {
				dsForRW.getDB().getCollection("v_service_process")
						.update(query, new BasicDBObject("$set", update));
			}
		}
		else
		{	
			throw new ServiceException("护士对应的服务不存在！");
		}
	}

	@Override
	public List<VServiceProcess> getVServiceProcessList() {
		List<VServiceProcess> result = new ArrayList<VServiceProcess>();
		Query<VServiceProcess> uq = dsForRW.createQuery(VServiceProcess.class);// 查询搜有的数据
		result = uq.asList();
		return result;
	}
	
	@Override
	public List<VServiceProcess> getVServiceProcessListById(Integer userId) {
		List<VServiceProcess> result = new ArrayList<VServiceProcess>();
		Query<VServiceProcess> uq = dsForRW.createQuery(VServiceProcess.class).filter("userId", userId);// 查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public boolean checkVServiceProcessBean(String orderId, Integer userId) {
		
		boolean  result = false;
		Query<VServiceProcess> processQuery = dsForRW
				.createQuery(VServiceProcess.class).filter("orderId", orderId).filter("userId", userId);
		VServiceProcess process = processQuery.get();
		if(null!=process)
		{	
			result =true;	
		}
		return result;
	}
	
	/**
	 * 说明 一个订单可能对应着两条服务
	 * 这里必须是
	 */
	@Override
	public VServiceProcess getVServiceProcessBeanByOrderId(String orderId) {
		
		Integer[] status = new Integer[]{VServiceProcessStatusEnum.toStartOrder.getIndex(),VServiceProcessStatusEnum.toUploadResult.getIndex(),VServiceProcessStatusEnum.end.getIndex()};
		Query<VServiceProcess> processQuery = dsForRW
				.createQuery(VServiceProcess.class).filter("orderId", orderId).filter("status in ", status);
		VServiceProcess process = processQuery.get();
		return process;
	}
	
	@Override
	public VServiceProcess getVServiceProcessBean(String serviceId) {
		
		Query<VServiceProcess> processQuery = dsForRW
				.createQuery(VServiceProcess.class).filter("_id", new ObjectId(serviceId));
		VServiceProcess process = processQuery.get();
		return process;
	}

	@Override
	public VServiceProcess getVServiceInfoByOrderId(String orderid) {
		Query<VServiceProcess> processQuery = dsForRW
				.createQuery(VServiceProcess.class).filter("orderId", orderid);
		VServiceProcess process = processQuery.get();
		return process;
	}

	@Override
	public List<VSPTracking> getVServiceInfoByState() {
		List<VSPTracking> list = new ArrayList<VSPTracking>();
		//查询已经抢单的记录
		Query<VSPTracking> VSPlsit = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_200.getIndex());
		List<String> idlist = new ArrayList<String>();
		Long createTime=0l;
		boolean flag= true;
		if(null!=VSPlsit&&VSPlsit.asList().size()>0){
			for (VSPTracking vspTracking : VSPlsit) {
				//查询所有于此订单id有关的业务编码 不能包含已经取消掉的订单
				List<Integer> listcode = vspdao.getTrackListByOrderId(vspTracking.getOrderId());
				if(!listcode.contains(ExceptionEnum.Business_code_900.getIndex())){
					idlist.add(vspTracking.getId());
					createTime = vspTracking.getCreateTime();//记录创建时间
					flag = (new Date().getTime()-createTime)>1800?true:false;
					//当前的系统时间减去记录的创建时间如果大于30分钟  那么就是符合条件的记录 找出来
					Query<VSPTracking> re = dsForRW.createQuery(VSPTracking.class).filter("code",ExceptionEnum.Business_code_200.getIndex()).where("function(){ if("+flag+"){"
							+ "return true"
							+ "}else{"
							+ "return false}}");
					if(re.asList().size()>0){
						list.add(re.get());
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<VServiceProcess> getExceptionOfNurseService(String appointmentTime,String id) {
		List<VServiceProcess> list =new ArrayList<VServiceProcess>();
		//查询出所有已经开始服务的记录
		Query<VServiceProcess> processQuery = dsForRW.createQuery(VServiceProcess.class).filter("status",2);
		Long updateTime = 0l;//记录更新时间
		for (VServiceProcess vServiceProcess : processQuery) {
			updateTime = vServiceProcess.getUpdTime();
			//查询状态为1 并且时间范围在半小时之内的订单  更新时间小于预约时间的为异常记录
			Query<VServiceProcess> re = dsForRW.createQuery(VServiceProcess.class).filter("status",1).where("function(){ if("
					+ updateTime+"<"
					+appointmentTime+"){"
							+ "return true"
							+ "}else{"
							+ "return false}}");
			list.add(re.get());
		}
		return list;
	}

	@Override
	public List<VServiceProcess> getExceptionOfNurseServiceNoClick(List<String> id) {
		List<VServiceProcess> list =new ArrayList<VServiceProcess>();
		//查询出所有已经开始服务的记录
		if(list.size()>0){
			for (int i = 0; i <id.size(); i++) {
				Query<VServiceProcess> re = dsForRW.createQuery(VServiceProcess.class).filter("status",1).filter("orderId", id.get(i));	
				list.add(re.get());
			}
		}
		return list;
	}

	@Override
	public PageVO getServiceProcessList(ServiceProcessParm scp) {
		PageVO pageVo = new PageVO();
		pageVo.setPageIndex(scp.getPageIndex());
		pageVo.setPageSize(scp.getPageSize());
		Query<VServiceProcess> query = 
				dsForRW.createQuery(VServiceProcess.class).filter("status in", scp.getStatusList()).filter("userId", scp.getUserId());
		pageVo.setTotal(query.countAll());
		List<VServiceProcess> data= query.offset(pageVo.getPageIndex()*pageVo.getPageSize()).limit(pageVo.getPageSize()).order("-time").asList();
		pageVo.setPageData(data);
		return pageVo;
	}
}
