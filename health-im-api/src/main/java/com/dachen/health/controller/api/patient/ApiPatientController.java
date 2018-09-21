package com.dachen.health.controller.api.patient;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/patient")
public class ApiPatientController extends ApiBaseController {

	@Resource
	protected IPatientService patientService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable Integer id) {
		String tag = "{id}";

		logger.info("{}. id={}", tag, id);
		Patient patient = patientService.findByPk(id);
		if (null == patient) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, patient);
	}
}
