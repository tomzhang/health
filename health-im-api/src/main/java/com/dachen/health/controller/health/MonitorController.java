package com.dachen.health.controller.health;

import com.dachen.commons.JSONMessage;
import com.dachen.health.repair.service.IDataRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

	@Autowired
	IDataRepairService dataRepairService;

	@RequestMapping("/check")
	public JSONMessage check() {
		final String returnValue = "OK";
		return JSONMessage.success(null, returnValue);
	}
}
