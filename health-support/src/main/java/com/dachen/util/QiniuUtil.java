package com.dachen.util;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.msg.util.ImHelper;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.qr.QRCodeUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class QiniuUtil {
	private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);

//	public static final String QINIU_DOMAIN = PropertiesUtil.getContextProperty("qiniu.domain");
    public static final String QINIU_DOMAIN() {
        return PropertiesUtil.getContextProperty("qiniu.domain");
    }

	public static final String QRCODE_BUCKET = "qrcode";
	public static final String DEFAULT_BUCKET = "default";
//	public static final String QINIU_URL = "http://{0}." + QINIU_DOMAIN + "/" + "{1}";// {0}:空间名; // {1}:key
    public static final String QINIU_URL(){
        return "http://{0}." + QINIU_DOMAIN() + "/" + "{1}";// {0}:空间名; // {1}:key
    }

	public static final String VOICE_BUCKET = "patient";
	
	public static final String DEFAULT_AVATAR(){
        return MessageFormat.format(QINIU_URL(), DEFAULT_BUCKET, "user/default.jpg");
    }

	public static final String DEFAULT_AVATAR_MEN() {
        return MessageFormat.format(QINIU_URL(), DEFAULT_BUCKET, "user/default_man.jpg");
    }

	public static final String DEFAULT_AVATAR_FEMALE(){
        return MessageFormat.format(QINIU_URL(), DEFAULT_BUCKET, "user/default_female.jpg");
    }

	/**病历中用药的默认图片**/
	public static final String DEFAULT_IM_DRUG_IMG(){
        return MessageFormat.format(QINIU_URL(), DEFAULT_BUCKET, "yao_icon_xxhdpi.png");
    }

	/**病历中检查项的默认图片**/
	public static final String DEFAULT_IM_CHECKITEM_IMG(){
        return MessageFormat.format(QINIU_URL(), DEFAULT_BUCKET, "inspection_item_xxhdpi.png");
    }

	private static Auth auth = null;

    public static Auth auth() {
        if (null == auth) {
            synchronized (QiniuUtil.class) {
                if (null == auth) {
                    String access_key = PropertiesUtil.getContextProperty("qiniu.access.key");
                    String secret_key = PropertiesUtil.getContextProperty("qiniu.secret.key");
                    auth = Auth.create(access_key, secret_key);
                }
            }
        }
        return auth;
    }

	private static OperationManager operater;

//	static {
//		String access_key = PropertiesUtil.getContextProperty("qiniu.access.key");
//		String secret_key = PropertiesUtil.getContextProperty("qiniu.secret.key");
//		auth = Auth.create(access_key, secret_key);
//	}

	public static OperationManager getOperationManager() {
		if (operater == null) {
			operater = new OperationManager(auth());
		}
		return operater;
	}

	/**
	 * 
	 * @param sourceFileName
	 *            待上传的文件全路径
	 * @param destFileName
	 *            七牛上显示的文件名
	 * @param bucket
	 *            七牛上的存储空间
	 * @return 七牛上显示的文件名
	 */
	public static String upload(String sourceFileName, String destFileName, String bucket) {
		if (StringUtil.isEmpty(destFileName)) {
			int index = sourceFileName.lastIndexOf(File.separator);
			if (index > -1) {
				destFileName = sourceFileName.substring(index);
			} else {
				throw new ServiceException(sourceFileName + "有误");
			}
		}
		File file = new File(sourceFileName);
		if (!file.exists()) {
			throw new ServiceException("文件没有找到");
		}
		if (StringUtil.isEmpty(bucket)) {
			throw new ServiceException(sourceFileName + "不能为空");
		}

		String token = MsgHelper.getUploadToken(bucket);
//		String token = auth.uploadToken(bucket);
		if (StringUtil.isEmpty(token)) {
			throw new ServiceException("token获取异常");
		}
		try {
			UploadManager manger = new UploadManager();
			Response res = manger.put(file, destFileName, token);
			if (res.isOK()) {
				MyRet ret = res.jsonToObject(MyRet.class);
				System.err.println("----" + ret.key);
				logger.info("sourceFileName:" + sourceFileName + "  bucket:" + bucket + "  ret.key:" + ret.key);
				return ret.key;
			}
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 *            待上传的文件全路径
	 * @param destFileName
	 *            七牛上显示的文件名
	 * @param bucket
	 *            七牛上的存储空间
	 * @return 七牛上显示的文件名
	 */
	public static String upload(byte[] bytes, String destFileName, String bucket) {
		String tag = "upload";
		logger.info("{}. destFileName={}, bucket={}", tag, destFileName, bucket);
		if (bytes == null || bytes.length == 0) {
			throw new ServiceException("bytes不能为空");
		}

		String token = MsgHelper.getUploadToken(bucket);
		logger.info("{}. token={}", tag, token);
//		String token = auth.uploadToken(bucket);
		if (StringUtil.isEmpty(token)) {
			throw new ServiceException("token获取异常");
		}
		try {
			UploadManager manger = new UploadManager();
			Response res = manger.put(bytes, destFileName, token);
			logger.info("{}. res={}, res.isOK()={}", tag, res, res.isOK());
			if (res.isOK()) {
				MyRet ret = res.jsonToObject(MyRet.class);
				logger.info("{}. bucket:{}  ret.key: {}", tag, bucket, ret.key);
				return ret.key;
			}
		} catch (QiniuException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static JSON converterToFormat(String key, String format, String bucket) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("key", key);
		request.put("format", format);
		request.put("bucket", bucket);
		return ImHelper.instance.postJson("file", "avthumb.action", request);
	}

	public static void main(String[] args) {
//		upload("D:\\162fb964b9644f329c175d4033be97d6",null,"telrecord");
		String text = "/qr/scanning?tk=" + 123;
//		String fileName = "225.jpg";
		byte[] bytes;
		try {
			String urlStr = "http://patient.dev.file.dachentech.com.cn/9f64bdfe1d284e38aacc66e9c39d0bb9";
			bytes = QRCodeUtil.encode(text, urlStr);
			String key = QiniuUtil.upload(bytes, null, "qrcode");
			System.err.println("----" + key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String avthumb(String key) {
		
		if (operater == null) {
			operater = getOperationManager();
		}
		
		//设置转码操作参数
        String fops = "avthumb/mp3";
        //设置转码的队列
        String pipeline = "amr2mp3";
        String urlbase64 = UrlSafeBase64.encodeToString(VOICE_BUCKET + ":" + key +  "_mp3");
        String pfops = fops + "|saveas/" + urlbase64;
        //设置pipeline参数
        StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", pipeline).putNotEmpty("notifyURL", "");
        
        try {
			String persistid = operater.pfop(VOICE_BUCKET, key, pfops, params);
			return persistid;
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}

}
class MyRet {
    public long fsize;
    public String key;
    public String hash;
    public int width;
    public int height;
}