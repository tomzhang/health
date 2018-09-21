package com.dachen.health.user.dao;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.vo.User;
import com.dachen.health.user.entity.param.NurseParam;
import com.dachen.health.user.entity.po.NurseImage;
import com.dachen.health.user.entity.vo.NurseVO;


public interface INurseDao {

	/**
     * </p>修改护士认证信息</p>
     * @param param
     * @author liwei
     * @date 2015年7月7日
     */
    void updateCheckInfo(NurseParam param);
    
    
    /**
     * </p>护士获取认证信息</p>
     * @param param
     * @author liwei
     * @date 2015年7月15日
     */
    NurseVO getCheckInfo(NurseParam param);
    
    
    /**
     * </p>获取护士列表</p>
     * @param param
     * @author jianghj
     * @date 2015年12月15日16:45:03
     */
    Map<String,Object> getNurseList(NurseParam param);
    
    /**
     * 提交认证
     * @param userId
     * @param param
     * @return
     */
    public User createNurse(Integer userId, NurseParam param) ;
    
    
    /**
     * 保存图片
     * @param userId
     * @param param
     * @return
     */
    public void createNurseImage(Integer userId, List<NurseImage> paramList) ;
    
    /**
     * 获取图片地址列表
     * @param userId
     * @param param
     * @return
     */
    public List<NurseImage> getNurseImageList(Integer userId) ;
    
    /**
     * 删除图片地址
     * @param userId
     * @param param
     * @return
     */
    public void deleteNurseImage(Integer userId) ;
}
