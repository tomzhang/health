package com.dachen.health.group.group.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupProfitDao;
import com.dachen.health.group.group.entity.param.GroupProfitParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.health.group.group.entity.po.GroupProfitConfig;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupProfitVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.util.StringUtil;
import com.dachen.util.tree.ExtTreeNode;

/**
 * ProjectName： health-group<br>
 * ClassName： GroupProfitServiceImpl<br>
 * Description：集团抽成关系Service实现类 <br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
@Service
public class GroupProfitServiceImpl implements IGroupProfitService {

    @Autowired
    protected IGroupProfitDao groupProfitDao;

    @Autowired
    protected IBaseUserService baseUserService;

	@Autowired
    protected IGroupDao groupDao;
	
	@Autowired
    protected IGroupDoctorService groupDoctorService;
	
	@Autowired
    protected IDepartmentDoctorService departDocService;

    
    /**
     * </p>集团创建者邀请的人的树</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月28日
     */
    public List<ExtTreeNode> getGroupProfit(String groupId) {
        if (StringUtil.isBlank(groupId)) {
            return new ArrayList<ExtTreeNode>();
        }
        return groupProfitDao.getGroupProfit(groupId);
    }
    
    /**
     * </p>查找下级抽成关系</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月6日
     */
    public PageVO getGroupProfit(GroupProfitParam param){
    	//参数校验
        if(StringUtil.isBlank(param.getGroupId())){
            throw new ServiceException("集团id为空");
        }
        if(param.getParentId() == null){
            throw new ServiceException("父节点id为空");
        }
        
        //根据 groupId和父节点id  分页查询 抽成信息
        List<Map<String, Object>>  list = groupProfitDao.getGroupProfitByParentId(param.getGroupId(), param.getParentId(), param.getPageIndex(), param.getPageSize());
        long total = groupProfitDao.countGroupProfitByParentId(param.getGroupId(), param.getParentId());
        
        //读取用户信息,集团成员信息等信息（并修复历史数据）
        list = matchUserAttrForProfit(param.getGroupId(),list);
        
        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(total);
        
        
        return page;
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupProfitService#fixHistoryProfitData(java.lang.String)
     */
    @Override
    public void fixHistoryProfitData(String groupId){
    	if(!StringUtils.isEmpty(groupId)){
    		
    		fixProfitDataByGroupId(groupId);
    	}else{
    		//查询所有集团信息 TODO
    		
    		
    	}
    	
    	
    	
    }
    
    /**
     * 修复集团中的profit数据（1、删除多余的profit，2、父节点不存在的profit挂到根节点上，3、医生在职但没有profit记录的新增profit记录）
     * @author wangqiao
     * @date 2016年3月23日
     * @param groupId
     */
    private void fixProfitDataByGroupId(String groupId){
    	//参数校验
    	if(StringUtils.isEmpty(groupId)){
    		throw new ServiceException("集团id为空");
    	}
    	//查询所有groupProfit信息
    	List<GroupProfit> profitList = groupProfitDao.getGroupProfitByGroupId(groupId);
    	//profit中的doctorId集合
    	Set<Integer> profitDoctorIdSet = new HashSet<Integer> ();

    	
    	//查询集团所有在职医生
    	List<GroupDoctor> doctorList = groupDoctorService.findGroupDoctor(null,groupId,GroupDoctorStatus.正在使用.getIndex());
    	//在职医生id 集合
    	Set<Integer> doctorIdSet = new HashSet<Integer> ();
    	if(doctorList != null && doctorList.size() >0){
    		for(GroupDoctor doctor : doctorList){
    			if(doctor != null && doctor.getDoctorId() != null && doctor.getDoctorId() > 0 ){
    				doctorIdSet.add(doctor.getDoctorId());
    			}
    		}
    	}else{
    		//医生集团没有医生，可以删除所有profit 
    		groupProfitDao.deleteByGroupId(groupId);
    		return;
    	}
    	
    	//遍历每个groupProfit信息
    	if(profitList != null && profitList.size() > 0){
    		for(GroupProfit profit : profitList){
    			if(profit == null){
    				continue;
    			}else {
    				Integer userId = profit.getDoctorId();
    				Integer parentUserId = profit.getParentId();
    				if(userId == null || userId ==0 || !doctorIdSet.contains(userId)){
    					//profit关联的医生不存在，或不在集团中,删除该profit
    					groupProfitDao.deleteById(profit.getId());
    				}
    				if(parentUserId == null || !doctorIdSet.contains(parentUserId)){
    					//父节点不存在，或父节点不在集团中，将父节点设置为根节点
    					groupProfitDao.updateParentId(userId,0,profit.getGroupId());
    				}
    				profitDoctorIdSet.add(profit.getDoctorId());
    			}
    			    			
    		}
    	}
    	
    	//集团医生是否都有groupProfit，没有需要补充完整groupProfit信息
    	if(doctorList != null && doctorList.size() >0){
    		for(GroupDoctor doctor : doctorList){
    			if(doctor != null && doctor.getDoctorId() != null && doctor.getDoctorId() > 0 ){
    				if(!profitDoctorIdSet.contains(doctor.getDoctorId())){
    					//新增 groupProfit
    					initProfitByJoinGroup(doctor.getDoctorId(),doctor.getGroupId(),0);
    				}
    				
    			}
    		}
    	}
    	
    	
    }
    
    /**
     * 查找profit相关的user信息和集团成员信息，以及下级成员数量信息
     * 如果profit相关user不存在，或集体成员信息不存在，则删除该profit记录（修复历史数据）
     * @param list
     * @return
     *@author wangqiao
     *@date 2016年2月18日
     */
    private List<Map<String, Object>> matchUserAttrForProfit(String groupId,List<Map<String, Object>>  list ){
    	//参数校验
    	if(list == null){
    		return null;
    	}else if(list.size() ==0){
    		return list;
    	}
    	//读取profitList中 所有医生id
    	List<Integer> doctorIds = new ArrayList<Integer>();
    	for(Map<String, Object> map : list){
    		if(map != null || map.get("id") != null){
    			doctorIds.add((Integer)map.get("id"));
    		}
    	}
    	//读取医生相关的user信息
    	List<BaseUserVO> userList = baseUserService.getByIds(doctorIds.toArray(new Integer[] {}));
    	Map<Integer,BaseUserVO> userMap = new HashMap<Integer,BaseUserVO>();
    	//将userList转成userMap
    	for(BaseUserVO vo : userList){
    		if(vo != null && vo.getUserId() != null && vo.getUserId() != 0){
    			userMap.put(vo.getUserId(), vo);
    		}
    	}
    	
    	//读取医生在集团中的信息 
    	List<GroupDoctorVO> gdoctorList = groupDoctorService.getByIds(groupId, doctorIds);
    	Map<Integer,GroupDoctorVO> gdoctorMap = new HashMap<Integer,GroupDoctorVO>();
    	//将groupDoctorList 转成 groupDoctorMap
    	for(GroupDoctorVO vo : gdoctorList){
    		if(vo != null && vo.getDoctorId() != null && vo.getDoctorId() != 0){
    			gdoctorMap.put(vo.getDoctorId(), vo);
    		}
    	}
    	
    	//需要清理的垃圾数据
    	List<Map<String, Object>> deleteProfitList= new ArrayList<Map<String, Object>>();
    	
    	//读取医生的全组织路径
    	Map<Integer, String> fullNameMap = departDocService.getDepartmentFullName(groupId, doctorIds);
    	//计算 子节点的数量
    	Map<Integer, Integer> childrenCountMap = groupProfitDao.getChildrenCount(groupId, doctorIds);
    	
    	
    	
    	//为每个profit设置相关 user信息，集体成员信息，全组织路径，子节点数量
    	for (Map<String, Object> map : list) {
    		if(map == null || map.get("id") == null ){
    			continue;
    		}
    		Integer doctorId = (Integer)map.get("id");
    		BaseUserVO userVO = userMap.get(doctorId);
    		GroupDoctorVO groupDoctorVO = gdoctorMap.get(doctorId);
    		//当doctorId=0 或user信息查不到，或 集体成员信息查不到时，则该profit为垃圾数据
    		if(doctorId == 0 || userVO == null || groupDoctorVO == null){
    			deleteProfitList.add(map);
    		}else{
    			//设置user属性
    			map.put("name", userVO.getName());
				map.put("headPicFileName", userVO.getHeadPicFileName());
				map.put("doctorNum", userVO.getDoctorNum());
				map.put("hospital", userVO.getHospital());
				map.put("departments", userVO.getDepartments());
				map.put("title", userVO.getTitle());
				//设置集团成员属性
				map.put("contactWay", groupDoctorVO.getContactWay());
				map.put("remarks", groupDoctorVO.getRemarks());
				//设置全组织路径
				map.put("departmentFullName", fullNameMap.get(doctorId));
				//设置子节点数量
				Integer childrenCount = childrenCountMap.get(doctorId);
				if(childrenCount == null ){
					map.put("childrenCount",0);
				}else{
					map.put("childrenCount",childrenCount);
				}
    		}
    		
    	}
    	
    	//返回list数据中去掉垃圾数据
    	list.removeAll(deleteProfitList);
    	
    	//持久化删除垃圾数据
    	for(Map<String, Object> deleteMap : deleteProfitList){
    		if(deleteMap != null && deleteMap.get("id") != null ){
    			Integer doctorId = (Integer)deleteMap.get("id");
    			this.delete(doctorId,groupId);
    		}
    	}
    	
    	return list;
    }
    
    @Override
    public PageVO searchByKeyword(GroupProfitParam param) {
    	//参数校验
    	 if(StringUtil.isBlank(param.getGroupId())){
             throw new ServiceException("集团id为空");
         }
    	
    	List<Map<String, Object>>  list = groupProfitDao.searchByKeyword(param.getKeyword(), param.getGroupId(), param.getPageIndex(), param.getPageSize());
    	Long total = groupProfitDao.countSearchByKeyword(param.getKeyword(), param.getGroupId());
    	
        //读取用户信息,集团成员信息等信息（并修复历史数据）
        list = matchUserAttrForProfit(param.getGroupId(),list);
        
        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(total);
        
        
        return page;
    	
//    	return groupProfitDao.searchByKeyword(param);
    }

    /**
     * </p>添加抽成关系</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年9月2日
     */
    public void add(GroupProfit po) {
        if (po.getDoctorId() == null || po.getDoctorId() ==0) {
            throw new ServiceException("医生id为空");
        }
        if (po.getParentId() == null) {
            throw new ServiceException("上级id为空");
        }
        if (StringUtil.isBlank(po.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        //补充数据
        po.setUpdator(po.getDoctorId());
        po.setUpdatorDate(System.currentTimeMillis());
        
        //设置treePath
        if(po.getParentId() == 0){
        	//自己邀请自己
        	po.setTreePath("/" + po.getDoctorId() + "/");
        }else{
            GroupProfitVO vo = groupProfitDao.getById(po.getParentId(),po.getGroupId());
            if(vo!=null){
            	//找到邀请人
                po.setTreePath(vo.getTreePath() + po.getDoctorId() + "/");    
            }else{
            	//找不到邀请人
            	po.setTreePath("/" + po.getDoctorId() + "/");
            }
        }
        
        // 判断医生id和上级id是否存在
        if(po.getParentId()!=0){
            List<BaseUserVO> list = baseUserService.getByIds(new Integer[] { po.getDoctorId(), po.getParentId() });
            if (list.size() == 2) {
                groupProfitDao.add(po);
            } else {
                throw new ServiceException("医生id或上级id不存在");
            }
        }else{
            groupProfitDao.add(po);
        }
    }

    /**
     * </p>修改抽成关系</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    public void updateParentId(Integer doctorId,Integer parentId,String groupId) {

        if (doctorId== null || doctorId==0) {
            throw new ServiceException("医生id为空");
        }
        if (parentId == null) {
            throw new ServiceException("上级id为空");
        }
        if (StringUtil.isBlank(groupId)) {
            throw new ServiceException("集团id为空");
        }
        groupProfitDao.updateParentId(doctorId,parentId,groupId);
    }

    /**
     * </p>删除抽成关系</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    public void delete(Integer doctorId,String groupId) {
        if (doctorId == null ) {
            throw new ServiceException("医生id为空");
        }
        if (StringUtil.isBlank(groupId)) {
            throw new ServiceException("集团id为空");
        }
        groupProfitDao.delete(doctorId,groupId);
    }

    /**
     * </p>修改抽成比例</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年9月2日
     */
    public void updateProfit(GroupProfitParam param) {
        if (param.getDoctorId() == null || param.getDoctorId() == 0) {
            throw new ServiceException("医生id为空");
        }
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }
        groupProfitDao.updateProfit(param);
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupProfitService#initProfitByJoinGroup(java.lang.Integer, java.lang.String)
     */
    public void initProfitByJoinGroup(Integer doctorId,String groupId,Integer referenceId){
    	//参数校验
    	if(groupId == null || StringUtils.isEmpty(groupId)){
    		throw new ServiceException("集团id为空");
    	}
    	if(doctorId == null || doctorId ==0){
    		throw new ServiceException("医生id为空");
    	}
    	//推荐人可以为自己  referenceid==0
//    	if(referenceId == null || referenceId ==0){
//    		throw new ServiceException("推荐人id为空");
//    	}
    	Group group = groupDao.getById(groupId, null);
    	if(group == null){
    		throw new ServiceException("集团不存在");
    	}
    	//新建医生抽成 （从集团中继承抽成比例）
		GroupProfit profit = new GroupProfit();
		profit.setDoctorId(doctorId);
		profit.setGroupId(groupId);
		
		//设置关系树
		BaseUserVO baseUserVO = baseUserService.getUser(referenceId);
		if (baseUserVO!= null && baseUserVO.getStatus() != null 
				&& (UserEnum.UserStatus.normal.getIndex() == baseUserVO.getStatus().intValue())){
			profit.setParentId(referenceId);
		}else{
			profit.setParentId(0);
		}


		if(group.getConfig() != null){
			GroupProfitConfig config = new GroupProfitConfig();
			config.setCarePlanGroupProfit(group.getConfig().getCarePlanGroupProfit());
			config.setCarePlanParentProfit(group.getConfig().getCarePlanParentProfit());
			
			config.setClinicGroupProfit(group.getConfig().getClinicGroupProfit());
			config.setClinicParentProfit(group.getConfig().getClinicParentProfit());
			
			config.setPhoneGroupProfit(group.getConfig().getPhoneGroupProfit());
			config.setPhoneParentProfit(group.getConfig().getPhoneParentProfit());
			
			config.setTextGroupProfit(group.getConfig().getTextGroupProfit());
			config.setTextParentProfit(group.getConfig().getTextParentProfit());
			
			//添加会诊抽成比例
			config.setConsultationGroupProfit(group.getConfig().getConsultationGroupProfit());
			config.setConsultationParentProfit(group.getConfig().getConsultationParentProfit());
			
			config.setAppointmentGroupProfit(group.getConfig().getAppointmentGroupProfit());
			config.setAppointmentParentProfit(group.getConfig().getAppointmentParentProfit());
			
			config.setChargeItemGroupProfit(group.getConfig().getChargeItemGroupProfit());
			config.setChargeItemParentProfit(group.getConfig().getChargeItemParentProfit());
			
			//暂时兼容以前的数据方案，后续需要删除掉 FIXME
			profit.setGroupProfit(group.getConfig().getGroupProfit());
			profit.setParentProfit(group.getConfig().getParentProfit());
			
			//自己邀请自己，上级分成不变，只是对应的上级是0
//			if(referenceId == null || referenceId == 0){
//				config.setCarePlanParentProfit(0);
//				config.setClinicParentProfit(0);
//				config.setPhoneParentProfit(0);
//				config.setTextParentProfit(0);
//				config.setConsultationParentProfit(0);
//				profit.setParentProfit(0);
//			}
			
			profit.setConfig(config);
		}
		this.add(profit);

    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupProfitService#getGroupProfitById(java.lang.Integer, java.lang.String)
     */
    @Override 
	public GroupProfit getGroupProfitById(Integer doctorId,String groupId){
		return groupProfitDao.getGroupProfitById(doctorId, groupId);
		
	}

}
