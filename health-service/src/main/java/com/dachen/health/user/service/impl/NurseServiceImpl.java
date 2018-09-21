package com.dachen.health.user.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.dao.INurseDao;
import com.dachen.health.user.entity.param.NurseParam;
import com.dachen.health.user.entity.po.NurseImage;
import com.dachen.health.user.entity.vo.NurseVO;
import com.dachen.health.user.service.INurseService;

@Service(NurseServiceImpl.BEAN_ID)
public class NurseServiceImpl implements INurseService {
	public static final String BEAN_ID = "NurseServiceImpl";
	@Autowired
	private INurseDao nurseDao;

	/**
	 * </p>护士修改认证信息</p>
	 * 
	 * @param param
	 * @author liwei
	 * @date 2015年12月2日
	 */
	public void updateCheckInfo(NurseParam param) {
		param.setStatuses(new Integer[] {
				UserEnum.UserStatus.Unautherized.getIndex(),
				UserEnum.UserStatus.fail.getIndex(),UserEnum.UserStatus.uncheck.getIndex() });
		param.setStatus(UserEnum.UserStatus.uncheck.getIndex());
		nurseDao.updateCheckInfo(param);
	}

	/**
	 * </p>护士获取认证信息</p>
	 * 
	 * @param param
	 * @author liwei
	 * @date 2015年7月15日
	 */
	public NurseVO getCheckInfo(NurseParam param) {
		return nurseDao.getCheckInfo(param);
	}

	/**
	 * </p>护士提交认证信息</p>
	 * 
	 * @param param
	 * @author liwei
	 * @date 2015年12月2日
	 */
	public User createCheckInfo(NurseParam param) {
		return nurseDao.createNurse(param.getUserId(), param);
	}

	/**
     * 保存图片
     * @param userId
     * @param param
     * @return
     */
    public void createNurseImage(Integer userId, List<NurseImage> paramList) 
    {
    	nurseDao.createNurseImage(userId, paramList);
    }
    
    /**
     * 获取图片地址列表
     * @param userId
     * @param param
     * @return
     */
    public List<NurseImage> getNurseImageList(Integer userId){
    	
    	return nurseDao.getNurseImageList(userId);
    }

	@SuppressWarnings("unchecked")
	@Override
	public PageVO getNurseList(NurseParam param) {
		PageVO page = new PageVO();
		Map<String ,Object> map =  nurseDao.getNurseList(param);
		page.setPageData((List<NurseVO>)map.get("list"));
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));
        return page;
	}
    
}
