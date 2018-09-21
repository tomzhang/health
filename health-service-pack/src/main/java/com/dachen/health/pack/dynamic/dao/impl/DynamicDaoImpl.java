package com.dachen.health.pack.dynamic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.pack.dynamic.constant.DynamicEnum;
import com.dachen.health.pack.dynamic.dao.IDynamicDao;
import com.dachen.health.pack.dynamic.entity.param.DynamicParam;
import com.dachen.health.pack.dynamic.entity.po.Dynamic;
import com.dachen.health.pack.stat.entity.param.PatientStatParam;
import com.dachen.util.BeanUtil;
import com.dachen.util.StringUtils;

@Repository
public class DynamicDaoImpl extends NoSqlRepository implements IDynamicDao {
	
	
//    @Autowired
//    private PatientStatMapper patientStatMapper;
	 @Autowired
    private FriendsManager friendsManager;

    
    
	@Autowired
	private IGroupDoctorService groupDoctorService;
    
	@Override
	public Dynamic getDynamic(String id) {

		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(50002, "动态id不能为空！");
		}

		Dynamic result = null;
		result = dsForRW.createQuery(Dynamic.class)
				.filter("_id", new ObjectId(id)).get();
		return result;
	}

	@Override
	public List<Dynamic> getDynamicListByUserId(Integer userId) {

		if (null == userId || userId.intValue() == 0) {
			throw new ServiceException(50002, "用户id不能为空！");
		}

		Query<Dynamic> queryList = this.dsForRW.createQuery(Dynamic.class)
				.order("-updateTime");

		queryList.filter("creator", userId.toString());
		queryList.filter("userId", userId);

		return queryList.asList();
	}

	@Override
	public void deleteDynamic(String id) {

		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(50002, "动态id不能为空！");
		}

		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class).field("id").equal(new ObjectId(id));
		Dynamic dbGoods = q.get();
		if(null == dbGoods)
		{	
			throw new ServiceException(50002,"动态不存在！");
		}
		UpdateOperations<Dynamic> ops = dsForRW.createUpdateOperations(Dynamic.class);
		
		ops.set("isDelete", DynamicEnum.DeleteFlagEnum.deleted.getIndex());
		
		ops.set("updateTime", System.currentTimeMillis());
		
		dsForRW.findAndModify(q, ops);

	}

	@Override
	public void saveDynamic(DynamicParam param) {

		long createdDate = System.currentTimeMillis();
		Dynamic vo = BeanUtil.copy(param, Dynamic.class);
		vo.setCreateTime(createdDate);
		vo.setUpdateTime(createdDate);
		vo.setIsDelete(DynamicEnum.DeleteFlagEnum.un_delete.getIndex());
		this.dsForRW.insert(vo);
	}

	@Override
	public List<Dynamic> getDynamicListByGroupId(String groupId) {

		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException(50002, "医生集团id不能为空！");
		}
		Query<Dynamic> queryList = this.dsForRW.createQuery(Dynamic.class)
				.order("-updateTime");

		queryList.filter("creator", groupId);
		queryList.filter("groupId", groupId);

		return queryList.asList();
	}


	public PageVO getDynamicListByGroupIdForWeb(String groupId,Integer pageIndex, Integer pageSize) {
		
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException(50002, "集团id不能为空！");
		}

		PageVO pageVo = new PageVO();
		if (null ==  pageIndex ) {
			pageIndex=0;
		}
		if (null == pageSize) {
			pageSize=15;
		}
		pageVo.setPageIndex(pageIndex);
		pageVo.setPageSize(pageSize);
		
		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class);
		
		q.filter("groupId", groupId);
		q.filter("creator", groupId);
		q.filter("isDelete", DynamicEnum.DeleteFlagEnum.un_delete.getIndex());
		
		long count = q.countAll();
		int skip = pageIndex * pageSize;
		skip = skip < 0 ? 0 : skip;
		q.order("-updateTime");
		q.offset(skip);
		q.limit(pageSize);
		List<Dynamic> goodsList = q.asList();
		pageVo.setTotal(count);
		pageVo.setPageData(goodsList);

		return pageVo;
	}

	@Override
	public PageVO getDoctorDynamicList(Integer userId, Integer currentUserId, Integer pageIndex, Integer pageSize) {

		PageVO pageVo = new PageVO();
		if (null ==  pageIndex ) {
			pageIndex=0;
		}
		if (null == pageSize) {
			pageSize=15;
		}
		pageVo.setPageIndex(pageIndex);
		pageVo.setPageSize(pageSize);
		
		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class);
		
		q.filter("userId", userId);
		q.filter("creator", userId.toString());

		q.filter("isDelete", DynamicEnum.DeleteFlagEnum.un_delete.getIndex());

		List<Integer> currentUserIds = Lists.newArrayList();
		currentUserIds.add(currentUserId);
		currentUserIds.add(0);

		//若当前用户未登陆，则查询所有患者都能看到的
		q.or(
				q.criteria("userIds").in(currentUserIds),
				q.criteria("userIds").doesNotExist()
		);
		
		long count = q.countAll();
		int skip = pageIndex * pageSize;
		skip = skip < 0 ? 0 : skip;
		q.order("-updateTime");
		q.offset(skip);
		q.limit(pageSize);
		List<Dynamic> goodsList = q.asList();
		pageVo.setTotal(count);
		pageVo.setPageData(goodsList);

		return pageVo;
	}

	@Override
	public PageVO getMyDynamicList(Integer doctorId, Integer pageIndex, Integer pageSize) {
		PageVO pageVo = new PageVO();
		if (Objects.isNull(pageIndex)) {
			pageIndex=0;
		}
		if (Objects.isNull(pageSize)) {
			pageSize=15;
		}
		pageVo.setPageIndex(pageIndex);
		pageVo.setPageSize(pageSize);

		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class);

		q.filter("userId", doctorId);
		q.filter("creator", doctorId.toString());

		q.filter("isDelete", DynamicEnum.DeleteFlagEnum.un_delete.getIndex());

		long count = q.countAll();
		int skip = pageIndex * pageSize;
		skip = skip < 0 ? 0 : skip;
		q.order("-updateTime");
		q.offset(skip);
		q.limit(pageSize);
		List<Dynamic> goodsList = q.asList();
		pageVo.setTotal(count);
		pageVo.setPageData(goodsList);

		return pageVo;
	}

	@Override
	public List<String> getPatientGroupListByUserId(Integer userId)
	{
		List<String> groupIdList =groupDoctorService.getGroupListByDoctorId(userId);
		return groupIdList;
	}
	
	private List<String> getStringDoctorList(List<Integer> doctorList)
	{
		List<String> result = new ArrayList<String>();
		
		if(null!=doctorList&&doctorList.size()>0)
		{	
			for(Integer doctor:doctorList)
			{	
				result.add(doctor.toString());
			}
		}
		
		return result;
	}
	
	

	@Override
	public PageVO getGroupAndDoctorDynamicListByGroupId(String groupId, Integer currentUserId, Integer pageIndex, Integer pageSize) {
		if (StringUtils.isEmpty(groupId)) {
			throw new ServiceException(50002, "集团id不能为空！");
		}
		PageVO pageVo = new PageVO();
		if (null ==  pageIndex ) {
			pageIndex=0;
		}
		if (null == pageSize) {
			pageSize=15;
		}
		pageVo.setPageIndex(pageIndex);
		pageVo.setPageSize(pageSize);

		List<String> result = new ArrayList<String>();
		result.add(groupId);
		
		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class);
		
		q.filter("isDelete", DynamicEnum.DeleteFlagEnum.un_delete.getIndex());
		
		//查询集团下面的医生列表集团
		List<Integer> doctorList =	groupDoctorService.getDocIdsByGroupID(groupId, "C");
		if(null!=doctorList&&doctorList.size()>0) {
			List<String> temp= getStringDoctorList(doctorList);
			result.addAll(temp);
		}
		
		if(result.size()>0) {
			q.criteria("creator").in(result);
		}

		List<Integer> currentUserIds = Lists.newArrayList();
		currentUserIds.add(currentUserId);
		currentUserIds.add(0);

		//若当前用户未登陆，则查询所有患者都能看到的
		q.or(
				q.criteria("userIds").in(currentUserIds),
				q.criteria("userIds").doesNotExist()
		);
		
		long count = q.countAll();
		int skip = pageIndex * pageSize;
		skip = skip < 0 ? 0 : skip;
		q.order("-updateTime");
		q.offset(skip);
		q.limit(pageSize);
		List<Dynamic> goodsList = q.asList();
		pageVo.setTotal(count);
		pageVo.setPageData(goodsList);
		
		return pageVo;
	}

	@Override
	public PageVO getPatientRelatedDynamicList(Integer userId, Integer currentUserId, Long createTime, Integer pageSize) {

		if (null==userId ||userId.intValue()==0) {
			throw new ServiceException(50002, "用户id不能为空！");
		}
		PageVO pageVo = new PageVO();
		
		int pageIndex =0;
		if(null == createTime || createTime.longValue() <= 0) {
			pageIndex=0;
		}
		if (null == pageSize) {
			pageSize=15;
		}
		pageVo.setPageIndex(pageIndex);
		pageVo.setPageSize(pageSize);
		
		List<String> result = new ArrayList<String>();
		
		//查询患者对应的医生
//		List<Integer> doctorList =	friendsManager.getFriendReqListByUserId(userId);
		List<Integer> doctorList =	friendsManager.getMyDocIds(userId);
		
		Query<Dynamic> q = dsForRW.createQuery(Dynamic.class);
		
		q.filter("isDelete", DynamicEnum.DeleteFlagEnum.un_delete.getIndex());

		List<Integer> currentUserIds = Lists.newArrayList();
		currentUserIds.add(currentUserId);
		currentUserIds.add(0);

		//若当前用户未登陆，则查询所有患者都能看到的
		q.or(
				q.criteria("userIds").in(currentUserIds),
				q.criteria("userIds").doesNotExist()
		);
		
		if(null!=doctorList&&doctorList.size()>0) {
			List<String> temp = getStringDoctorList(doctorList);
			result.addAll(temp);
		}

		if(null!=doctorList&&doctorList.size()>0) {
			//查询医生对应的集团列表
			List<String> groupList = groupDoctorService.getGroupListByDoctorIds(doctorList);
			
			if(null!=groupList&&groupList.size()>0) {
				result.addAll(groupList);
				//查询医生集团
			}
		}

		if(result.size()>0) {
			q.criteria("creator").in(result);
		} else {
			result.add("");
			q.criteria("creator").in(result);
		}
		
		long count = q.countAll();
		
		q.order("-updateTime");
		q.offset(0);
		q.limit(pageSize);
		
		if(null!=createTime&&createTime.longValue()>0) {
			q.filter("createTime <", createTime);
		}
		
		List<Dynamic> goodsList = q.asList();
		pageVo.setTotal(count);
		pageVo.setPageData(goodsList);
		
		return pageVo;
	}
}
