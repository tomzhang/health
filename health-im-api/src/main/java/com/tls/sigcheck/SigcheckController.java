package com.tls.sigcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.util.PropertiesUtil;
import com.tencent.common.Configure;
import com.tencent.common.HttpsRequest;

// 由于生成 sig 和校验 sig 的接口使用方法类似，故此处只是演示了生成 sig 的接口调用

@RestController
@RequestMapping("/sig")
public class SigcheckController {

	private static Logger logger = LoggerFactory.getLogger(SigcheckController.class);

	private static String dll;
	static {
		try {
			dll = org.springframework.util.ResourceUtils.getURL(Configure.getJnisigcheck()).getPath();
			dachen_ec_key = org.springframework.util.ResourceUtils.getURL(Configure.getDachen_ec_key()).getPath();
			dachen_public_key =  org.springframework.util.ResourceUtils.getURL(Configure.getDachen_public_key()).getPath();
			kangze_ec_key = org.springframework.util.ResourceUtils.getURL(Configure.getKangze_ec_key()).getPath();
			kangze_public_key =  org.springframework.util.ResourceUtils.getURL(Configure.getKangze_public_key()).getPath();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}

	}
	//大辰签名
	private static String dachen_ec_key;
	private static String dachen_public_key;
	//康泽签名
	private static String kangze_ec_key;
	private static String kangze_public_key;
	/**
     * @api {get} /sig/getSig 获取腾讯云服务签名
     * @apiVersion 1.0.0
     * @apiName getSig
     * @apiParam  {String}     access_token          token
     * @apiAuthor 姜宏杰
     * @date 2016年1月18日10:11:37
     */
	@RequestMapping(value = "/getSig")
	public JSONMessage getSig(String userId) throws Exception{
//		String sdkappid = Constant.sdkappid;
		//String sdkappName = Constant.sdkappName;
		tls_sigcheck sig = new tls_sigcheck();
		System.out.println(dll);
		File f = new File(dll); 
		// 使用前请修改动态库的加载路径
		sig.loadJniLib(f.getPath());
		String sdkappid = PropertiesUtil.getContextProperty("tencent.live.account");
		File priKeyFile = null;
		File pubKeyFile = null;
		if("1400004103".equals(sdkappid)){
			 priKeyFile = new File(dachen_ec_key);
			 pubKeyFile = new File(dachen_public_key);
		}else if("1400008056".equals(sdkappid)){
			 priKeyFile = new File(kangze_ec_key);
			 pubKeyFile = new File(kangze_public_key);
		}
		StringBuilder strBuilder = new StringBuilder();
		String s = "";
		BufferedReader br = new BufferedReader(new FileReader(priKeyFile));
		while ((s = br.readLine()) != null) {
			strBuilder.append(s + '\n');
		}
		br.close();
		String priKey = strBuilder.toString();
		int ret = sig.tls_gen_signature_ex2(sdkappid, userId, priKey);
		if (0 != ret) {
			System.out.println("ret " + ret + " " + sig.getErrMsg());
		} else {
			System.out.println("sig:\n" + sig.getSig());
		}

		br = new BufferedReader(new FileReader(pubKeyFile));
		strBuilder.setLength(0);
		while ((s = br.readLine()) != null) {
			strBuilder.append(s + '\n');
		}
		br.close();
		String pubKey = strBuilder.toString();
		ret = sig.tls_check_signature_ex2(sig.getSig(), pubKey, sdkappid,userId);
		if (0 != ret) {
			System.out.println("ret " + ret + " " + sig.getErrMsg());
		} else {
			System.out.println("--\nverify ok -- expire time "
					+ sig.getExpireTime() + " -- init time "
					+ sig.getInitTime());
		}
		return JSONMessage.success(null,sig.getSig());
	}
}
