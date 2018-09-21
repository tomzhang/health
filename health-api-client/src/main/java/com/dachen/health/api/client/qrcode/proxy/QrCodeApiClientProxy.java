package com.dachen.health.api.client.qrcode.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class QrCodeApiClientProxy extends HealthApiClientProxy {

	/**
	 * 生成二维码链接
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public String generateQr(String id, String type) {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("id", id.toString());
		params.put("type", type.toString());
		try {
			String url = "qrcode/generateQr";
			String text = this.openRequest(url, params, String.class);
			return text;
		} catch (HttpApiException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
