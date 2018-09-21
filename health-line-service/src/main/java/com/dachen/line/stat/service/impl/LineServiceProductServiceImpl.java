package com.dachen.line.stat.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.IUserHospitalDao;
import com.dachen.line.stat.entity.vo.LineService;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.service.ICheckResultsService;
import com.dachen.line.stat.service.ILineServiceProductService;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.OutServiceHelper;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class LineServiceProductServiceImpl implements
		ILineServiceProductService {

	@Autowired
	private ILineServiceProductDao lineServiceProductDao;
	@Autowired
	private IUserHospitalDao userHospitalDao;
	@Autowired
	private ICheckResultsService checkResultsService;
	
	@Autowired
	private INurseOrderDao nurseOrderDao;

	@Override
	public List<LineServiceProduct> getSystemLineServiceProduct() {
		return lineServiceProductDao.getSystemLineServiceProduct();
	}
/**
	 * 通过检查单id查询对应的检查项列表
	 * @param id   
	 * @param type   0  产品id  1 检查单 id  
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getCheckItemListById(String id,int type)
	{
		if(StringUtils.isEmpty(id))
		{	
			throw new ServiceException("参数id不能够为空");
		}
		
		if(!(type==1 || type==0))
		{	
			throw new ServiceException("参数type取值错误");
		}
		
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		if(type==0)
		{	
			listMap =getCheckItemListByProductId(id);
		}
		else
		{	
//			PatientOrder order = nurseOrderDao.getPatientOrderByCheckId(id);
//			if(null==order)
//			{	
//				throw new ServiceException("检查单不存在！");
//			}
			listMap =getCheckItemListByCheckItemId(id);

		}
		
		return listMap;
	}
	private List<Map<String, Object>> getCheckItemListByCheckItemId(String checkId) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		
		List<Map<String, Object>> listMaps= checkResultsService.getCheckResultsServiceMapListByCheckId(checkId);
		if(ConfigUtil.checkCollectionIsEmpty(listMaps))
		{	
			for(Map<String, Object> item :listMaps)
			{	
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.get("id"));
				map.put("title", item.get("title"));
				map.put("type",1);
				listMap.add(map);
			}
		}
		return listMap;
	}
	
//	/**
//	 * 通过检查单id查询对应的检查项列表
//	 * @param checkId
//	 * @return
//	 */
//	private List<Map<String, Object>> getCheckItemListByCheckId(String checkId) {
//		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
//		
//		List<Map<String, Object>> listMaps= checkResultsService.getCheckResultsServiceMapList(checkId);
//		if(ConfigUtil.checkCollectionIsEmpty(listMaps))
//		{	
//			for(Map<String, Object> item :listMaps)
//			{	
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("id", item.get("id"));
//				map.put("title", item.get("title"));
//				map.put("type",1);
//				listMap.add(map);
//			}
//		}
//		return listMap;
//	}
	/**
	 *  通过检id查询对应的检查项列表
	 */
	
	private List<Map<String, Object>> getCheckItemListByProductId(String productId)
	{
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		
		List<LineService> lineServiceList =   lineServiceProductDao.getProductServiceItemList(productId);
		if(ConfigUtil.checkCollectionIsEmpty(lineServiceList))
		{	
			for(LineService line:lineServiceList)
			{	
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", line.getBasicId()==null ?"":line.getBasicId());
				map.put("title", line.getTitle());
				map.put("type", line.getType());
				listMap.add(map);
			}
		}
		
		return listMap;
	}
	
	/**
	 * 获取所有的认证通过的医院
	 */
	public List<Map<String, Object>> getCertificatedHospitalList(Integer status)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>(); 
		list =userHospitalDao.getCertificatedHospitalList(status);
		
		return list;
		
	}
	
	/**
	 * 患者填写订单的时候 显示的 检查项
	 */
	public  List<Map<String, Object>> getProductServiceItemList(String productId)
	{
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		
		List<LineService> lineServiceList =   lineServiceProductDao.getProductServiceItemList(productId);
		if(ConfigUtil.checkCollectionIsEmpty(lineServiceList))
		{	
			for(LineService line:lineServiceList)
			{	
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", line.getId());
				map.put("title", line.getTitle());
				listMap.add(map);
			}
		}
		
		return listMap;
	}

	@Override
	public LineServiceProduct getSystemLineServiceProductById(
			String productId) {
		// TODO Auto-generated method stub
		return lineServiceProductDao.getSystemLineServiceBean(productId);
	}
}
