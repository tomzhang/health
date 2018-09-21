package com.dachen.health.pack.stat.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.pack.stat.entity.param.PackStatParam;
import com.dachen.health.pack.stat.entity.vo.PackStatVO;
import com.dachen.health.pack.stat.mapper.PackStatMapper;
import com.dachen.health.pack.stat.service.IPackStatService;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-group<br>
 * ClassName： AssessStatServiceImpl<br>
 * Description： 考核统计service实现类<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
@Service
public class PackStatServiceImpl implements IPackStatService {


    @Autowired
    private PackStatMapper packStatMapper;
    
    @Autowired
    private IBaseUserService baseUserService;

    /**
     * </p>统计订单金额数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO orderMoney(PackStatParam param) {
        if (StringUtil.isBlank(param.getGroupId())) {
            throw new ServiceException("集团id为空");
        }

        // 构造分页
        PageVO page = new PageVO();

        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());

        if (param.getDoctorId() == null) {
            // 按集团查询
            
            //查找集团医生id
            List<Integer> doctorIds = baseUserService.getDoctorIdByGroup(param.getGroupId(),
            		!param.isShowOnJob()?new String[]{"C","S","O"}:new String[]{"C"},param.getKeyword());
            
            if(doctorIds.size()==0){
                page.setPageData(new ArrayList<PackStatVO>());
                page.setTotal(0L);
                return page;
            }
            
            param.setUserIds(doctorIds);
            param.setStatus(OrderEnum.OrderStatus.已完成.getIndex());
            
            List<PackStatVO> list = packStatMapper.orderMoneyByGroup(param);
            
            //手动构造分页
            //this.buildPage(doctorIds, list);
            List<Integer> userIds = new ArrayList<Integer>();//需返回的数据的id
            
            if (list!=null && list.size() > 0) {
                list.forEach((a) -> {
                    userIds.add(a.getId());
            	});
        	}
            page.setPageData(list);
            page.setTotal((long) packStatMapper.orderMoneyByGroupList(param).size());
            
            //查询用户信息
            if(userIds.size()>0){
                List<BaseUserVO> userList = baseUserService.getByIds(userIds.toArray(new Integer[]{}));
                for(PackStatVO stat:list){
                    for(BaseUserVO user:userList){
                        if(stat.getId().equals(user.getUserId())){
                            stat.setName(user.getName());
                            stat.setHospital(user.getHospital());
                            stat.setDepartments(user.getDepartments());
                            stat.setTitle(user.getTitle());
                            stat.setHeadPicFileName(UserHelper.buildHeaderPicPath(user.getHeadPicFileName(),stat.getId()));
                            break;
                        }
                    }
                    stat.setMoney(stat.getMoney()!=null?stat.getMoney()/100:0L);
                }
                
            }

        } else {
            // 按医生查询
            
            param.setStatus(OrderEnum.OrderStatus.已完成.getIndex());
            //不忽略金额为0的订单(2016-8-10傅永德)
            param.setIgnoreZeroPrice(false);
            
            List<PackStatVO> list = packStatMapper.orderMoneyByDoctor(param);
            page.setPageData(list);
            page.setTotal(packStatMapper.orderMoneyByDoctorCount(param));
            
            //查询用户信息
            for(PackStatVO vo:list){
            	
            	if (vo.getPackType() != null && vo.getPackType().intValue() == PackEnum.PackType.checkin.getIndex()) {
            		//这种情况视为在线门诊
					vo.setPackName(PackEnum.PackType.online.getTitle());
				} else {
					vo.setPackName(PackEnum.PackType.getTitle(vo.getPackType()));
				}
            	
                vo.setPackType(null);
                
                BaseUserVO user = baseUserService.getUser(vo.getId());
                
                if(user!=null){
                    vo.setHeadPicFileName(UserHelper.buildHeaderPicPath(user.getHeadPicFileName(),vo.getId()));
                    vo.setName(user.getName());
                    vo.setTelephone(user.getTelephone());
                    vo.setId(null);
                }
                vo.setMoney(vo.getMoney()!=null?vo.getMoney()/100:0L);
            }
            
        }
        return page;
    }

    /**
     * </p>手动构造分页</p>
     * @param doctorIds 所有医生id
     * @param list 待构造的分页
     * @author fanp
     * @date 2015年9月23日
     */
    private void buildPage(List<Integer> doctorIds,List<PackStatVO> list){
        List<Integer> listIds = new ArrayList<Integer>();
        List<PackStatVO> remainList = new ArrayList<PackStatVO>();
        for(PackStatVO vo : list){
            listIds.add(vo.getId());
        }
        //查找出list中不存在的id
        for(Integer ids:doctorIds){
            int place = this.binSearch(listIds, ids);
            if(place == -1){
                PackStatVO vo = new PackStatVO();
                vo.setId(ids);
                vo.setMoney(0L);
                vo.setAmount(0);
                remainList.add(vo);
                
            }
        }
        list.addAll(remainList);
    }
    
    // 二分法查找id所在位置，list经过排序
    private int binSearch(List<Integer> listIds, int ids) {
        Integer[] sortIds = listIds.toArray(new Integer[]{});
        Arrays.sort(sortIds);
        
        int mid = sortIds.length / 2;

        int start = 0;
        int end = sortIds.length - 1;
        while (start <= end) {
            mid = (end - start) / 2 + start;
            Integer id = sortIds[mid];
            if (ids == id) {
                return mid;
            } else if (ids <= id) {
                end = mid - 1;
            } else if (ids >= id) {
                start = mid + 1;
            }
        }
        return -1;
    }
    
}
