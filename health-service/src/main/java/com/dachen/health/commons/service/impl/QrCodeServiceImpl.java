package com.dachen.health.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.common.HttpClient;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.entity.po.QrScanParamPo;
import com.dachen.health.commons.dao.IQrCodeDao;
import com.dachen.health.commons.entity.QrCode;
import com.dachen.health.commons.entity.QrCodeParam;
import com.dachen.health.commons.entity.QrCodeParam.QrCodeType;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.wx.remote.WXRemoteManager;
import com.dachen.qr.QRCodeUtil;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.util.DESUtil;
import com.dachen.util.Md5Util;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QrCodeServiceImpl extends NoSqlRepository implements IQrCodeService {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	protected IQrCodeDao qrCodeDao;
	
	@Resource
    protected UserManager userManager;
	
	@Resource
    protected WXRemoteManager wxManager;

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Autowired
	@Qualifier(value = "remoteRestTemplate")
	RestTemplate httpTemplate;

//	private static String domain = PropertiesUtil.getContextProperty("health.server") + "/health";
    private static String domain() {
        return PropertiesUtil.getContextProperty("health.server") + "/health";
    }

	private static String qrServer() {
		return PropertiesUtil.getContextProperty("qr.server");
	}

	/**
	 * 创建用户二维码
	 * @param userType 3：医生；1：患者
	 */
	public String generateUserQr(String userId, String userType) {
		try {
			String text = "/qr/scanning?tk=" + encryt(userId, userType);
			return cacheQrCode(text, userId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}

	/**
	 * 创建集团、关怀二维码
	 * @param type group：集团；care：关怀
	 */
	public String generateQr(String id, String type) {
		
		try {
			String text ="/qr/generateScan?tk="+encryt(id, type);
			
			return cacheQrCode(text, id, type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}
	
	public String generateQr(String id, String type, Integer doctorId) {
		try {
			String text ="/qr/generateScan?tk="+encryt(id, type, doctorId);
			return cacheQrCode(text, id, type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}
	
	@Override
    public String generateQr(String circleId, String logoUrl, String inviteUrl) {
	    try {
            String tk = encryt(circleId, inviteUrl, Md5Util.md5Hex(logoUrl));
            String text ="/qr/circleQrcodeScan/nologin?tk="+tk;
            return cacheQrCodeLogo(text, logoUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new ServiceException("生成二维码失败");
    }
	
	private String cacheQrCode(String text, String id, String type) throws Exception {
		QrCode qr = qrCodeDao.get(text);
		
		if(Objects.isNull(qr)){
            qr = new QrCode();
        }
        if(Objects.isNull(qr.getCreateTime())){
            qrCodeDao.delete(qr);
            qr = new QrCode();
        }
        if(StringUtils.isBlank(qr.getImageKey())) {
			qr.setContent(text);
			qr.setCreateTime(System.currentTimeMillis());
			
			if (type.equals("3")) { //医生二维码、就医知识二维码使用微信公众号带场景二维码
				qr.setImageKey(wxManager.limitQrCode(id));
			} else {
				String key = getQiniuKey(text, true);
				if (key != null) {
					qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
				}
			}
			qr = qrCodeDao.save(qr);
		}
		return qr.getImageKey();
	}
	
	private String cacheQrCode(String text, String userId) throws Exception {
        QrCode qr = qrCodeDao.get(text);
        
        if(Objects.isNull(qr)){
            qr = new QrCode();
        }
        if(Objects.isNull(qr.getCreateTime())){
            qrCodeDao.delete(qr);
            qr = new QrCode();
        }
        if(StringUtils.isBlank(qr.getImageKey())) {
            qr.setContent(text);
            qr.setCreateTime(System.currentTimeMillis());
            
            Integer uid = 0;
            try{
                uid = Integer.valueOf(userId);
            }catch(Exception ex){
                uid = 0;
            }
            
            String key = getQiniuKey(text, uid, true);
            
            if (key != null) {
                qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
            }
        
            qr = qrCodeDao.save(qr);
        }
        return qr.getImageKey();
    }
	
	private String cacheQrCodeLogo(String text, String logoUrl) throws Exception {
        QrCode qr = qrCodeDao.get(text);
        
        if(Objects.isNull(qr)){
            qr = new QrCode();
        }
        if(Objects.isNull(qr.getCreateTime())){
            qrCodeDao.delete(qr);
            qr = new QrCode();
        }
        if(StringUtils.isBlank(qr.getImageKey())) {
            qr.setContent(text);
            qr.setCreateTime(System.currentTimeMillis());
            String key = getQiniuKey(text, logoUrl);
            if (StringUtils.isNotEmpty(key)) {
                qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
            }
            qr = qrCodeDao.save(qr);
        }
        return qr.getImageKey();
    }

	private String encryt(String id, String type) throws Exception, UnsupportedEncodingException {
		String token = DESUtil.encrypt(id + "&" + type);
		// 防止有特殊字符
		token = java.net.URLEncoder.encode(token, "UTF-8");
		return token;
	}
	
	private String encryt(String id, String type, Integer doctorId) throws Exception, UnsupportedEncodingException {
		String token = DESUtil.encrypt(id + "&" + type + "&" + doctorId);
		// 防止有特殊字符
		token = java.net.URLEncoder.encode(token, "UTF-8");
		return token;
	}
	
	private String encryt(String circleId, String inviteId, String md5Key) throws Exception, UnsupportedEncodingException {
        String token = DESUtil.encrypt(circleId + "&" + inviteId + "&" + md5Key);
        // 防止有特殊字符
        token = java.net.URLEncoder.encode(token, "UTF-8");
        return token;
    }
	
	private String getQiniuKey(String text) throws Exception {
		byte[] bytes = QRCodeUtil.encode(domain()+text);
		String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
		return key;
	}
	
	private String getQiniuKey(String text, boolean hasLogo) throws Exception {
		if (hasLogo) {
			Integer doctorId = ReqUtil.instance.getUserId();
			if (0 != doctorId) {
				User user = userManager.getUser(doctorId);
				if (user != null && StringUtils.isNotEmpty(user.getHeadPicFileName())) {
					String logoUrl = user.getHeadPicFileName();
					byte[] respons = httpTemplate.getForObject(logoUrl, byte[].class);
					InputStream inputStream = new ByteArrayInputStream(respons);
					byte[] bytes;
					try {
						bytes = QRCodeUtil.encode(domain()+text, inputStream, true);
						String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
						return key;
					} catch (Exception e) {
						bytes = QRCodeUtil.encode(domain()+text);
						String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
						return key;
					}
				}
			}
		}
		byte[] bytes = QRCodeUtil.encode(domain()+text);
		String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
		return key;
	}


	private String convertShortUrl(String realUrl) {
		QrScanParamPo po = new QrScanParamPo();
		po.url = realUrl;
		po.createTime = System.currentTimeMillis();
		String oid = qrCodeDao.insert(po);
		StringBuilder url = new StringBuilder();
		url.append(domain()).append("/qr/p/nologin/").append(oid).append("?");
		logger.info("生成二维码短链:"+url.toString());
		return url.toString();
	}

	private String getQiniuKey(String text, Integer userId, boolean hasLogo) throws Exception {
		String scanUrl = convertShortUrl(domain()+text);
        if (hasLogo && 0 != userId) {
            User user = userManager.getUser(userId);
            
            if (user != null && StringUtils.isNotEmpty(user.getHeadPicFileName())) {
                String logoUrl = user.getHeadPicFileName();
                byte[] respons=null;
                try{
					respons = httpTemplate.getForObject(logoUrl, byte[].class);
                }
                catch (Exception e){
                    logger.error(e.getMessage(), e);
                }
                InputStream inputStream=null;
                if(respons!=null)
                    inputStream = new ByteArrayInputStream(respons);
                byte[] bytes;
                try {
                    bytes = QRCodeUtil.encode(scanUrl, inputStream, true);
                    String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
                    
                    return key;
                } catch (Exception e) {
                    bytes = QRCodeUtil.encode(scanUrl);
                    String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
                    
                    return key;
                }
            }
        }
        byte[] bytes = QRCodeUtil.encode(scanUrl);
        String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
        return key;
    }
	
	private String getQiniuKey(String text, String logoUrl) throws Exception {
		String scanUrl = convertShortUrl(domain()+text);
        if (StringUtils.isNotBlank(logoUrl)) {
			byte[] respons=null;
		    try{
				respons = httpTemplate.getForObject(logoUrl, byte[].class);
			}
			catch (Exception e){
				logger.error(e.getMessage(), e);
			}
			InputStream inputStream=null;
			if(respons!=null)
                inputStream = new ByteArrayInputStream(respons);
            byte[] bytes;
            try {
                bytes = QRCodeUtil.encode(scanUrl, inputStream, true);
				return QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
            } catch (Exception e) {
                bytes = QRCodeUtil.encode(scanUrl);
				return QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
            }
        }
        byte[] bytes = QRCodeUtil.encode(scanUrl);
        String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
        return key;
    }
	
	/**
	 * 修改用户头像更新二维码
	 * @param userId
	 * @param userType
	 * @return
	 */
	public String modifyUserQr(String userId, String userType) {
		try {
			String token = encryt(userId, userType);
			String text ="/qr/scanning?tk="+token;
			
			QrCode qr = qrCodeDao.get(text);
			
			if (Objects.nonNull(qr)) {
				String key = getQiniuKey(text);
				if (key != null) {
					qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
				}
				
				qr = qrCodeDao.save(qr);
			}
			
			return qr.getImageKey();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String generateSignUpImage(String call_back_url, String call_back_param) {
		String access_token = ReqUtil.instance.getTokenInfo().getToken();
		if(StringUtils.isBlank(access_token))
			throw new ServiceException("访问令牌为空");
		Map<String,String> tkMap = new HashMap<>();
		tkMap.put("call_back_url", call_back_url);
		tkMap.put("call_back_param", call_back_param);
		tkMap.put("business_type", "SIGNUP");
		try{
			String tk = DESUtil.outerEncrypt(JSON.toJSONString(tkMap));
			tk = java.net.URLEncoder.encode(tk, "UTF-8");
			String text = "/qr/scan/process?tk=" + tk+"&access_token="+access_token;
			QrCode qr = qrCodeDao.get(text);
			if (qr == null || StringUtils.isBlank(qr.getImageKey())) {
				qr = new QrCode();
				qr.setContent(text);
				String shortUrl = shortUrlComponent.generateShortUrl(qrServer()+text);
				byte[] bytes = QRCodeUtil.encode(shortUrl);
				String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
				if (key != null) {
					qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
				}
				qr = qrCodeDao.save(qr);
			}
			return qr.getImageKey();
		}catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

    @Override
    public String generateQrCode(QrCodeParam param) {
        // TODO Auto-generated method stub
        QrCodeType type = QrCodeType.valueOf(param.getType());
        if(Objects.isNull(type)){
            throw new ServiceException("请求参数type无效");
        }
        
        switch(type){
            case CIRCLE_QRCODE:
                break;
            case CIRCLE_PERSONAL_QRCODE:
                break;
            case INVITE_DOCTOR_GET_COIN_QRCODE:
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public Map<String, Object> scanQrCode(QrCodeParam param) {
        // TODO Auto-generated method stub
        QrCodeType type = QrCodeType.valueOf(param.getType());
        if(Objects.isNull(type)){
            throw new ServiceException("请求参数type无效");
        }
        
        switch(type){
            case CIRCLE_QRCODE:
                break;
            case CIRCLE_PERSONAL_QRCODE:
                break;
            case INVITE_DOCTOR_GET_COIN_QRCODE:
                break;
            default:
                break;
        }
        return null;
    }

	@Override
	public QrScanParamPo getQrScanParam(String paramId) {
		return qrCodeDao.getQrScanParam(paramId);
	}

	@Override
	public String getUrlByOid(String oid) {
		return qrCodeDao.getQrScanParam(oid).url;
	}

	@Override
	public String generateMeetingActivityQr(String id, String logo,String type) {
		try {
			if(Objects.isNull(id) || Objects.isNull(logo) || Objects.isNull(type)){
				throw new ServiceException("请求参数不正确");
			}
            String text ="/qr/meetingActivityQrcodeScan/nologin?tk="+encryt(id, type);
            return cacheQrCodeLogo(text+logo, logo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new ServiceException("生成二维码失败");
	}


}
