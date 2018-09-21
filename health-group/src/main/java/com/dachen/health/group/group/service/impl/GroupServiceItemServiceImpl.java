package com.dachen.health.group.group.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.ServiceItem;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.group.group.dao.IGroupServiceItemDao;
import com.dachen.health.group.group.entity.po.GroupServiceItem;
import com.dachen.health.group.group.entity.po.GroupServiceItem.HospitalFee;
import com.dachen.health.group.group.entity.vo.GroupServiceItemVO;
import com.dachen.health.group.group.service.IGroupServiceItemService;
import com.dachen.util.BeanUtil;

@Service
public class GroupServiceItemServiceImpl implements IGroupServiceItemService {

	@Resource
	protected IGroupServiceItemDao serviceItemDao;
	
	@Resource
    protected IBaseDataDao baseDataDao;
	
	public List<HospitalVO> getHospitals(String groupId) {
		List<HospitalVO> hospitals = new ArrayList<HospitalVO>();
		List<GroupServiceItem> serviceItems = serviceItemDao.getByFilter(groupId, null);
		for (GroupServiceItem serviceItem : serviceItems) {
			if (serviceItem.getHospitalFee() != null) {
				List<HospitalFee> hospitalFees = serviceItem.getHospitalFee();
				for (HospitalFee hospitalFee : hospitalFees) {
					if (exists(hospitals, hospitalFee.getHospitalId())) {
						continue;
					}
					HospitalVO hospital = baseDataDao.getHospital(hospitalFee.getHospitalId());
					hospitals.add(hospital);
				}
			}
		}
		Collections.sort(hospitals, new SortByChina<HospitalVO>("name"));
		return hospitals;
	}
	
	private boolean exists(List<HospitalVO> hospitals, String hospitalId) {
		for (HospitalVO hospital : hospitals) {
			if (hospital.getId().equals(hospitalId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取服务项（包含下级节点）
	 * @param hospitalId
	 * @return
	 */
	public List<GroupServiceItemVO> getGroupServiceItem(String groupId, String hospitalId) {
		List<GroupServiceItemVO> vos = new ArrayList<GroupServiceItemVO>();
		
		List<GroupServiceItem> gserviceItems = serviceItemDao.getByFilter(groupId, hospitalId);
		
		Map<String, List<GroupServiceItemVO>> statMap = new HashMap<String, List<GroupServiceItemVO>>();
		for (GroupServiceItem gserviceItem : gserviceItems) {
			GroupServiceItemVO vo = BeanUtil.copy(gserviceItem, GroupServiceItemVO.class);
			ServiceItem serviceItem = baseDataDao.getServiceItemByIds(vo.getServiceItemId()).get(0);
			vo.setServiceItemName(serviceItem.getName());
			if (!statMap.containsKey(serviceItem.getParent())) {
				List<GroupServiceItemVO> volist = new ArrayList<GroupServiceItemVO>();
				volist.add(vo);
				statMap.put(serviceItem.getParent(), volist);
			} else {
				statMap.get(serviceItem.getParent()).add(vo);
			}
		}
		for (Iterator<String> it = statMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			ServiceItem serviceItem = baseDataDao.getServiceItemByIds(key).get(0);
			GroupServiceItemVO vo = new GroupServiceItemVO();
			vo.setServiceItemId(serviceItem.getId());
			vo.setServiceItemName(serviceItem.getName());
			vo.setChildren(statMap.get(key));
			vos.add(vo);
		}
		return vos;
	}
	
	/**
	 * 获取服务项
	 * @param hospitalId
	 * @return
	 */
	public GroupServiceItem getGroupServiceItem(String groupId, String hospitalId, String serviceItemId) {
		return serviceItemDao.getByFilter(groupId, hospitalId, serviceItemId);
	}
	
}
