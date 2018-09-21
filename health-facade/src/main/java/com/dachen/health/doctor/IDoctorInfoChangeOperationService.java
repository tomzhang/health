package com.dachen.health.doctor;

import com.dachen.health.commons.vo.User;
import com.dachen.health.system.entity.param.DoctorCheckParam;

public interface IDoctorInfoChangeOperationService {
	void closeAndDeleteOldSession();
	void assistantChanged(User user, DoctorCheckParam param);

}
