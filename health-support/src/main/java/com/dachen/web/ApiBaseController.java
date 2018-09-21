package com.dachen.web;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;
import com.dachen.commons.exception.ApiParamInvalidException;
import com.dachen.util.PropertiesUtil;

public abstract class ApiBaseController extends MvcController {

	@Override
	public void init() throws Exception {
		validateCall();
		super.init();
	}

	private void validateCall() throws ApiParamInvalidException {
		String ts = super.getParameter("_ats");
		String token = super.getParameter("_atoken");

		// 1.在本地配置文件读取访问Token.
		String apiToken = PropertiesUtil.getContextProperty("app.api_token");
		// 2.拼接调用参数.
		String qs = buildQueryString();
		// 3.校对Token
		String qsHex = DigestUtils.md5Hex(qs);
		String signToken = DigestUtils.md5Hex(apiToken + ts + qsHex);
		if (!token.equalsIgnoreCase(signToken)) {
			// this.logger.error("API非法调用. invokeFrom=" + oriApp +", ip=" +
			// super.getUserIp());
			logger.error("API非法调用. qs={}", qs);
			throw new ApiParamInvalidException("API非法调用", qs);
//		} else {
//			logger.info("API调用成功. qs={}", qs);
		}
	}

	@SuppressWarnings("rawtypes")
	private String buildQueryString() {
		Map map = super.getRequest().getParameterMap();

		Map<String, String> modified = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				return key1.compareToIgnoreCase(key2);
			}
		});

		Iterator itor = map.keySet().iterator();
		while (itor.hasNext()) {
			String key = itor.next().toString();
			modified.put(key, super.getRequest().getParameter(key));
		}

		itor = modified.keySet().iterator();
		String qs = "";
		while (itor.hasNext()) {
			String key = itor.next().toString();
			if (key.equalsIgnoreCase("_ats") || key.equalsIgnoreCase("_atoken")) {
				continue;
			}
			String v = modified.get(key);
			try {
				v = java.net.URLEncoder.encode(v, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("URL编码错误", e);
			}
			qs += String.format("&%s=%s", key, v);
		}
		return qs;
	}

}
