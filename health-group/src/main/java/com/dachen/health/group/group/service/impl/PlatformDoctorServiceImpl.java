package com.dachen.health.group.group.service.impl;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.dao.OnlineRecordRepository;
import com.dachen.health.commons.entity.OnlineRecord;
import com.dachen.health.group.group.dao.IPlatformDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.util.ReqUtil;

@Service
public class PlatformDoctorServiceImpl extends NoSqlRepository implements IPlatformDoctorService {

	@Resource
    protected IPlatformDoctorDao pdocDao;
	
	@Resource
    protected OnlineRecordRepository onlineDao;
	
	@Resource
    protected JedisTemplate jedisTemplate;
	
	@Autowired
    protected IBusinessServiceMsg businessMsgService;
	
	@Override
	public boolean doctorOnline(GroupDoctor gdoc) throws HttpApiException {
		Long currentTime = System.currentTimeMillis();
		PlatformDoctor pdoc = convert(gdoc);
		pdoc = pdocDao.findAndModify(pdoc, currentTime);
		
		OnlineRecord record = new OnlineRecord();
		record.setCreateTime(currentTime);
		record.setLastModifyTime(currentTime);
		record.setOnLineTime(currentTime);
		//非集团医生上线记录groupDoctorId存储医生Id
		record.setGroupDoctorId(pdoc.getId().toString());
		onlineDao.save(record);
		
		// 逻辑错误：0005：不能用医生集团ID＋XXX标记，存储个SET或则LIST吗？
		jedisTemplate.set(KeyBuilder.buildGroupOnLineKey(pdoc.getGroupId()), pdoc.getId().toString());
	
		businessMsgService.userChangeNotify(UserChangeTypeEnum.DOCTOR_ONLINE, ReqUtil.instance.getUser().getUserId(), null);
		
		JobTaskUtil.doctorOffline(pdoc.getId().toString(), false);
		
		return true;
	}

	@Override
	public void doctorOffline(GroupDoctor gdoc, EventEnum event) throws HttpApiException {
		PlatformDoctor pdoc = getOne(convert(gdoc));

		doctorOffline(pdoc, event);
	}
	
	@Override
	public void doctorOffline(PlatformDoctor pdoc, EventEnum event) throws HttpApiException {
		pdoc = pdocDao.getById(pdoc);

		OnlineRecord record = onlineDao.findLastOneByGroupDoctor(pdoc.getId().toString());
		if (record == null) {
			throw new ServiceException(4006, "找不到对应上线记录");
		}

		if (pdoc.getOnLineState() != null && !pdoc.getOnLineState().equals("1")) {
			// 不等于1，表示下线。
			throw new ServiceException(4007, "已经下线");
		}

		Long currentTime = System.currentTimeMillis();
		record.setLastModifyTime(currentTime);
		record.setOffLineTime(currentTime);

		Long duration = (currentTime - record.getOnLineTime()) / 1000;// 时长
		record.setDuration(duration);
		onlineDao.update(record);

		Long hasDutyDuration = pdoc.getDutyDuration();
		if (hasDutyDuration == null) {
			hasDutyDuration = duration;
		} else {
			hasDutyDuration += duration;
		}
		pdoc.setDutyDuration(hasDutyDuration);
		pdoc.setOffLineTime(currentTime);
		pdoc.setOnLineState("2");
		pdocDao.update(pdoc);
		
		businessMsgService.userChangeNotify(UserChangeTypeEnum.DOCOTR_OFFLINE, event, pdoc.getDoctorId(), null);
	}
	
	public PlatformDoctor getOne(GroupDoctor gdoc) {
		PlatformDoctor pdoc = convert(gdoc);
		return getOne(pdoc);
	}
	
	public PlatformDoctor getOne(Integer doctorId) {
		PlatformDoctor pdoc = new PlatformDoctor();
		pdoc.setDoctorId(doctorId);
		return getOne(pdoc);
	}
	
	public PlatformDoctor getOne(PlatformDoctor pdoc) {
		if (StringUtil.isBlank(pdoc.getGroupId()) || pdoc.getDoctorId() == null) {
			throw new ServiceException("集团Id或医生Id为空");
		}
		return pdocDao.getById(pdoc);
	}
	
	private PlatformDoctor convert(GroupDoctor gdoc) {
		PlatformDoctor pdoc = new PlatformDoctor();
		
		pdoc.setDoctorId(gdoc.getDoctorId());
		
		return pdoc;
	}

	
}
