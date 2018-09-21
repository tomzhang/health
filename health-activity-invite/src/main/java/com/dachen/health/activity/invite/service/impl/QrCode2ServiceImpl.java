package com.dachen.health.activity.invite.service.impl;

import com.dachen.common.HttpClient;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.activity.invite.form.QrCodeForm;
import com.dachen.health.activity.invite.service.QrCode2Service;
import com.dachen.health.base.entity.po.QrScanParamPo;
import com.dachen.health.commons.constants.UserEnum.InviteWayEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.dao.IQrCodeDao;
import com.dachen.health.commons.entity.QrCode;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.qr.QRCodeUtil;
import com.dachen.util.DESUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QrCode2ServiceImpl extends NoSqlRepository implements QrCode2Service {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	protected IQrCodeDao qrCodeDao;
	
	@Resource
    protected UserManager userManager;

	@Autowired
	@Qualifier(value = "remoteRestTemplate")
	RestTemplate httpTemplate;

    private static String domain() {
        return PropertiesUtil.getContextProperty("health.server") + "/health";
    }

	private static String qrServer() {
		return PropertiesUtil.getContextProperty("qr.server") ;
	}

	@Override
	public String generateDoctorQrCode(QrCodeForm form) {
		try {
			String tk = encryt(form.getType(), form.getTypeId(),
				form.getInviteActivityId(), form.getRegisterActivityId(), Source.doctorCircle.getIndex()+"",
				InviteWayEnum.qrcode.name());
			String text ="/activity/invite/qrcode/scan?tk="+tk;
			return cacheQrCode(text);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}

	@Override
	public String generateActivityDoctorQrCode(QrCodeForm form) {
		try {
			String tk = encryt(form.getType(), form.getTypeId(),
					form.getInviteActivityId(), form.getRegisterActivityId(), Source.circleWwhJoin.getIndex()+"",
					InviteWayEnum.qrcode.name());
			String text ="/activity/invite/qrcode/scan?tk="+tk;
			return cacheQrCode(text);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}
	@Override
	public String generateDeptQrCode(QrCodeForm form) {
		try {
			String tk = encryt(form.getType(), form.getTypeId(), form.getDoctorId()+"",
				Source.doctorCircle.getIndex()+"", InviteWayEnum.qrcode.name());
			String text ="/activity/invite/qrcode/scan?tk="+tk;
			return cacheQrCode(text);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		throw new ServiceException("生成二维码失败");
	}

	private String cacheQrCode(String text) throws Exception {
		QrCode qr = qrCodeDao.get(text);
		if (Objects.isNull(qr)
		        || Objects.isNull(qr.getCreateTime())
		        || StringUtil.isBlank(qr.getImageKey())) {
			qr = new QrCode();
			qr.setContent(text);
			qr.setCreateTime(System.currentTimeMillis());
			
			String key = getQiniuKey(text, true);
			if (key != null) {
				qr.setImageKey(MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.QRCODE_BUCKET, key));
			}
			qr = qrCodeDao.save(qr);
		}
		return qr.getImageKey();
	}
	
	private String encryt(String type, String typeId, String inviteActivityId, String registerActivityId, String subsystem, String way) throws Exception {
		String token = DESUtil.encrypt(type + "&" + typeId + "&" + inviteActivityId + "&" + registerActivityId + "&" + subsystem + "&" + way);
		// 防止有特殊字符
		token = java.net.URLEncoder.encode(token, "UTF-8");
		return token;
	}

	private String encryt(String type, String typeId, String doctorId, String subsystem, String way) throws Exception {
		String token = DESUtil.encrypt(type + "&" + typeId + "&" + doctorId + "&" + subsystem + "&" + way);
		// 防止有特殊字符
		token = java.net.URLEncoder.encode(token, "UTF-8");
		return token;
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
	
	private String getQiniuKey(String text, boolean hasLogo) throws Exception {
		String scanUrl = convertShortUrl(domain() + text);
		if (hasLogo) {

			Integer doctorId = ReqUtil.instance.getUserId();
			if (0 != doctorId) {
				User user = userManager.getUser(doctorId);
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
		}
		byte[] bytes = QRCodeUtil.encode(scanUrl);
		String key = QiniuUtil.upload(bytes, null, QiniuUtil.QRCODE_BUCKET);
		return key;
	}
	
}
