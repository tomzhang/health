package com.dachen.health.group.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.group.common.dao.ILabelDao;
import com.dachen.health.group.common.entity.param.LabelParam;
import com.dachen.health.group.common.entity.po.Label;
import com.dachen.health.group.common.service.ILabelService;
import com.dachen.util.StringUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
@Service
public class LabelServiceImpl implements ILabelService {

	@Autowired
	protected ILabelDao labelDao;
	
	@Override
	public void saveLabel(Label label) {
		if(StringUtil.isEmpty(label.getRelationId())) {
			throw new ServiceException("标签所属Id为空");
		}
		
		if(this.validationName(label)) {
			throw new ServiceException("标签名称已经存在");
		}
		
		labelDao.save(label);
	}

	@Override
	public void updateLabel(Label label) {
		if(StringUtil.isEmpty(label.getId())) {
			throw new ServiceException("Id为空");
		}
		labelDao.update(label);
	}

	@Override
	public void deleteLabel(String... ids) {
		labelDao.delete(ids);
	}

	@Override
	public PageVO searchLabel(LabelParam param) {
		return labelDao.search(param);
	}

	public boolean validationName(Label label) {
		Label la = new Label();
		la.setName(label.getName());
		la.setRelationId(label.getRelationId());
		Label ll = labelDao.findLabelByName(la);
		if(null == ll) {
			return false;
		}
		return true;
	}
	
}
