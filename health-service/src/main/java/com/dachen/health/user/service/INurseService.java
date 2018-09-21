package com.dachen.health.user.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.user.entity.param.NurseParam;
import com.dachen.health.user.entity.po.NurseImage;
import com.dachen.health.user.entity.vo.NurseVO;


public interface INurseService {

	/**
     * </p>护士修改认证信息</p>
     * @param param
     * @author liwei
     * @date 2015年12月2日
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
     * </p>护士提交认证信息</p>
     * @param param
     * @author liwei
     * @date 2015年12月2日
     */
    User createCheckInfo(NurseParam param);
    
    
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
     * </p>获取护士列表（包括未审核、已审核、未通过、未认证）</p>
     * @param param
     * @author jianghj
     * @date 2015年12月15日16:43:01
     */
    PageVO getNurseList(NurseParam param);
    
}
