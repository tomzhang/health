package com.dachen.health.pack.guide.dao;

import java.util.List;

import com.dachen.careplan.api.entity.CHelpRecord;
import com.dachen.careplan.api.entity.CWarningRecord;
import com.dachen.health.pack.guide.entity.po.ConsultOrderOtherPO;
import com.dachen.health.pack.guide.entity.vo.HelpVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IConsultOrderOtherDao {

	public String receiveCareOrder(ConsultOrderOtherPO po);

	public boolean checkCareOrder(HelpVO vo);

	public boolean checkByOrderId(Integer orderId);

	public List<ConsultOrderOtherPO> getHandleCareOrder(Integer userId);

	CHelpRecord updateHelpInfoByOrderId(String id) throws HttpApiException;

	CWarningRecord updateWarningInfoByOrderId(String id) throws HttpApiException;
}
