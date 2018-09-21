package com.dachen.health.commons.dao.mybatis;

import java.util.List;

import com.dachen.health.commons.vo.AreaVO;
import com.dachen.health.commons.vo.OptionVO;

public interface BasicMapper {

	List<AreaVO> getAreaList(int parentId);

	List<OptionVO> getOptionList(int parentId);

}
