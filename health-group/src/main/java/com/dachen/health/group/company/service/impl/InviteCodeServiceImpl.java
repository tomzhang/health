package com.dachen.health.group.company.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.group.common.util.ShareCodeUtil;
import com.dachen.health.group.company.dao.InviteCodeDao;
import com.dachen.health.group.company.entity.param.InviteCodeParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.entity.po.InviteCode;
import com.dachen.health.group.company.service.ICompanyService;
import com.dachen.health.group.company.service.InviteCodeService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author pijingwei
 * @date 2015/8/20
 */
@Service
@Deprecated //暂时废弃，没有这块业务
public class InviteCodeServiceImpl implements InviteCodeService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected InviteCodeDao inviteCodeDao;

	@Autowired
    protected ICompanyService companyService;
	
	@Resource
    protected MobSmsSdk mobSmsSdk;
	
	@Autowired
    protected IBusinessServiceMsg businessMsgService;
	
	@Autowired
    protected IBaseDataService baseDataService;

	@Autowired
	protected AsyncTaskPool asyncTaskPool;
	
	@Override
	public InviteCode saveInviteCode(InviteCode code) {
		if(StringUtil.isEmpty(code.getCompanyId())) {
			throw new ServiceException("公司Id为空");
		}
		
		if(StringUtil.isEmpty(code.getTelephone())) {
			throw new ServiceException("手机号码为空");
		}
		
		/* 根据Id随机生成8位邀请码 */
		code.setCode(ShareCodeUtil.toSerialCode(code.getDoctorId() == null ? 0l : code.getDoctorId()));
		code.setCreator(ReqUtil.instance.getUserId());
		code.setGenerateDate(new Date().getTime());
		code.setStatus("N");
		code.setUseDate(new Date().getTime());
        inviteCodeDao.delete(code);
		
		InviteCode invite = inviteCodeDao.save(code);

		this.asyncTaskPool.getPool().submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendNoteByPhone(invite);
				} catch (HttpApiException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});

		this.sendSms(invite);
		return invite;
	}
	
	/**
	 * 发送app系统消息邀请函
	 */
	private String sendSms(InviteCode code) {
		if(null == code.getDoctorId() || 0 == code.getDoctorId() || StringUtil.isEmpty(code.getCompanyId())) {
			return "";
		}
		try {
			Company company = companyService.getCompanyById(code.getCompanyId());
			
			List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
			ImgTextMsg imgTextMsg = new ImgTextMsg();
			imgTextMsg.setStyle(7);
			imgTextMsg.setTitle(UserChangeTypeEnum.PROFILE_INVITE_CREATE.getAlias());
			imgTextMsg.setContent(company.getName() + "邀请您创建医生圈子");
			mpt.add(imgTextMsg);
			
			businessMsgService.sendTextMsg(code.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY, mpt, null);
			businessMsgService.sendTextMsg(code.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
		} catch (Exception e) {
			return "消息推送失败";
		}
		return "消息推送成功";
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;
	
	/**
	 * 发送短信邀请不在平台上的医生下载客户端进行注册成为医生
	 * @return
	 * @throws IOException 
	 */
	private String sendNoteByPhone(InviteCode code) throws HttpApiException {
		String tpl = "";
		String generateUrl = "";
//		Company company = companyDao.getById(code.getCompanyId());
//		if(null == code.getDoctorId() || 0 == code.getDoctorId()) {
//			generateUrl = ShortUrlGenerator.getGenerateUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download"));
//			tpl = "您好，" + company.getName() + "邀请您创建集团，请您下载玄关健康医生客户端并注册成为平台医生，下载地址：   " + generateUrl;
//		} else {
//			generateUrl = ShortUrlGenerator.getGenerateUrl(PropertiesUtil.getContextProperty("health.server"));
//			tpl = company.getName() + "邀请您创建医生集团，登录玄关健康医生平台：   " + generateUrl + "    输入集团码：   " + code.getCode() + "   创建医生集团。";
//		}
		Company company = companyService.getCompanyById(code.getCompanyId());
		if(null == code.getDoctorId() || 0 == code.getDoctorId()) {
			generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download"));
			tpl = baseDataService.toContent("0004", company.getName(), generateUrl);
		} else {
			generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("health.server"));
			tpl = baseDataService.toContent("0005", company.getName(), generateUrl, code.getCode());
		}
		
		mobSmsSdk.send(code.getTelephone(), tpl);
		return tpl;
	}
	

	@Override
	public InviteCode updateInviteCode(String code) {
		if(StringUtil.isEmpty(code)) {
			throw new ServiceException("邀请码为空");
		}
		InviteCode icode = inviteCodeDao.findByCode(code);
		if(null == icode || "Y".equals(icode.getStatus())) {
			throw new ServiceException("无效的邀请码");
		}
		Long useDate = new Date().getTime();
		icode.setStatus("Y");
		icode.setUseDate(useDate);
		Long differDate = useDate - icode.getGenerateDate();
		if((differDate / 1000 / 60) > (24 * 60)) {
            inviteCodeDao.update(icode);
			throw new ServiceException("邀请码已过期");
		}
        inviteCodeDao.update(icode);
		return icode;
	}

	@Override
	public PageVO searchInviteCode(InviteCodeParam param) {
		return inviteCodeDao.search(param);
	}

}
