package com.dachen.health.controller.api.qrcode;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/qrcode")
public class ApiQrCodeController extends ApiBaseController {

	@Resource
	protected IQrCodeService qrCodeService;

	@RequestMapping(value = "generateQr", method = RequestMethod.GET)
	public JSONMessage generateQr(String id, String type) {
		String tag = "generateQr";
		logger.info("{}. id={}, type={}", tag, id, type);
		String text = qrCodeService.generateQr(id, type);
		return JSONMessage.success(null, text);
	}
}
