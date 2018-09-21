package com.dachen.health.meeting.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.net.HttpHelper;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.meeting.dao.MeetingDao;
import com.dachen.health.meeting.po.Meeting;
import com.dachen.health.meeting.service.MeetingService;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MeetingServiceImpl implements MeetingService{

	@Autowired
	MeetingDao meetingDao;
	
	@Autowired
	UserRepository userRepository;
	
    private static Lock lock = new ReentrantLock();
	
	@Override
	public Meeting createMeeting(Meeting meeting) {
		String meetingDomain = PropertiesUtil.getContextProperty("live.meeting.domain");
		String username = PropertiesUtil.getContextProperty("live.meeting.admin");
		String password = PropertiesUtil.getContextProperty("live.meeting.password");
		String maxAttendees = PropertiesUtil.getContextProperty("live.meeting.maxAttendees");
		Long maxAttendeesNum = null;
		try{
			maxAttendeesNum = Long.valueOf(maxAttendees);
		}catch(Exception e){
			throw new ServiceException("最大会议在线人数配置错误");
		}
		lock.lock();
		try{
			Long count = meetingDao.getDbAttendeesCount(meeting.getStartTime(),meeting.getEndTime(),null) + meeting.getAttendeesCount();
			if(maxAttendeesNum < count){
				throw new ServiceException("参加会议人数已超过最大人数"+maxAttendeesNum);
			}
			String startTime = meeting.getStartTime() != null ? meeting.getStartTime()+"" : ""; 
			String endTime = meeting.getEndTime() != null ? meeting.getEndTime()+"" : ""; 
			Map<String,String> params = new HashMap<String,String>();
			params.put("subject", meeting.getSubject());
			params.put("startTime", startTime);
			params.put("endTime", endTime);
			params.put("organizerToken", meeting.getOrganizerToken());
			params.put("maxAttendees", meeting.getAttendeesCount() == null ? "" : meeting.getAttendeesCount() + "");
			params.put("panelistToken", meeting.getPanelistToken());
			params.put("attendeeToken", meeting.getAttendeeToken());
			params.put("loginName", username);
			params.put("password", password);
			params.put("realtime", "true");
		    Map<String,String> rtnMap = invokeWebCastService(meetingDomain+"webcast/created",params);
		    String code = rtnMap.get("code");
		    /**
		     * 0	成功
				-1	失败
				101	参数错误
				102	参数转换错误
				200	认证失败
				201	口令过期
				300	系统错误
				500	业务错误
				501	业务错误 – 数据不存在
				502	业务错误 – 重复数据
		     */
		    if("0".equals(code)){
		    	String organizerJoinUrl = rtnMap.get("organizerJoinUrl");
			    String panelistJoinUrl = rtnMap.get("panelistJoinUrl");
			    String attendeeJoinUrl = rtnMap.get("attendeeJoinUrl");
			    String liveId = rtnMap.get("id");
			    String number = rtnMap.get("number");
			    String message = rtnMap.get("message");
			    
			    meeting.setOrganizerJoinUrl(organizerJoinUrl);
			    meeting.setPanelistJoinUrl(panelistJoinUrl);
			    meeting.setAttendeeJoinUrl(attendeeJoinUrl);
			    meeting.setLiveId(liveId);
			    meeting.setNumber(number);
			    meeting.setMessage(message);
			    meeting.setUpdateTime(System.currentTimeMillis());
			    meeting.setCreateUserId(ReqUtil.instance.getUserId());
			    meetingDao.insertMeeting(meeting);
		    }else{
		    	throw new ServiceException("第三方服务调用失败,错误吗："+code);
		    }
		}finally{
			lock.unlock();
		}
		return meeting;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> invokeWebCastService(String url, Map<String,String> params) {
		String jsonString = HttpHelper.post(url, params);
		return  JSONUtil.parseObject(HashMap.class, jsonString);
	}

	@Override
	public Meeting updateMeeting(Meeting meeting) {
		String meetingDomain = PropertiesUtil.getContextProperty("live.meeting.domain");
		String username = PropertiesUtil.getContextProperty("live.meeting.admin");
		String password = PropertiesUtil.getContextProperty("live.meeting.password");
		String maxAttendees = PropertiesUtil.getContextProperty("live.meeting.maxAttendees");
		Long maxAttendeesNum = null;
		try{
			maxAttendeesNum = Long.valueOf(maxAttendees);
		}catch(Exception e){
			throw new ServiceException("最大会议在线人数配置错误");
		}
		lock.lock();
		try{
			Long count = meetingDao.getDbAttendeesCount(meeting.getStartTime(),meeting.getEndTime(),meeting.getId()) + meeting.getAttendeesCount();
			if(maxAttendeesNum < count){
				throw new ServiceException("参加会议人数已超过最大人数"+maxAttendeesNum);
			}
			String startTime = meeting.getStartTime() != null ? meeting.getStartTime()+"" : ""; 
			String endTime = meeting.getEndTime() != null ? meeting.getEndTime()+"" : ""; 
			Map<String,String> params = new HashMap<String,String>();
			try{
				String liveId = meetingDao.getMeetingById(meeting.getId()).getLiveId();
				params.put("id", liveId);
			}catch(Exception e){
				throw new ServiceException("会议记录id错误，没有对应的直播信息");
			}
			params.put("subject", meeting.getSubject());
			params.put("startTime", startTime);
			params.put("endTime", endTime);
			params.put("organizerToken", meeting.getOrganizerToken());
			params.put("panelistToken", meeting.getPanelistToken());
			params.put("maxAttendees", meeting.getAttendeesCount() == null ? "" : meeting.getAttendeesCount() + "");
			params.put("attendeeToken", meeting.getAttendeeToken());
			params.put("loginName", username);
			params.put("password", password);
			params.put("realtime", "true");
		    Map<String,String> rtnMap = invokeWebCastService(meetingDomain+"webcast/update",params);
		    String code = rtnMap.get("code");
		    if("0".equals(code)){
		    	String message = rtnMap.get("message");
		    	meeting.setMessage(message);
		    	meeting.setUpdateTime(System.currentTimeMillis());
		    	meetingDao.updateMeeting(meeting);
		    }else{
		    	throw new ServiceException("第三方服务调用失败,错误码："+code);
		    }
		}finally{
			lock.unlock();
		}
		return meeting;
	}

	@Override
	public void stopMeeting(String meetingId) {
		String meetingDomain = PropertiesUtil.getContextProperty("live.meeting.domain");
		String username = PropertiesUtil.getContextProperty("live.meeting.admin");
		String password = PropertiesUtil.getContextProperty("live.meeting.password");
		Meeting m = meetingDao.getMeetingById(meetingId);
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("webcastId", m.getLiveId());
		params.put("loginName", username);
		params.put("password", password);
	    Map<String,String> rtnMap = invokeWebCastService(meetingDomain+"webcast/finish",params);
	    String code = rtnMap.get("code");
	    if("0".equals(code)){
	    	String message = rtnMap.get("message");
	    	m.setMessage(message);
	    	m.setIsStop(1);
	    	m.setUpdateTime(System.currentTimeMillis());
	    	meetingDao.updateMeeting(m);
	    }else{
	    	throw new ServiceException("第三方服务调用失败,错误吗："+code);
	    }
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Override
	public PageVO listMeeting(String companyId, Integer pageIndex, Integer pageSize) throws HttpApiException {
		PageVO pageVo = new PageVO();
		pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
		pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
		Long count = meetingDao.getMeetingCount(companyId);
		if(count != null && count > 0){
			List<Meeting> meets = meetingDao.getMeetingList(companyId,pageIndex,pageSize);
			for (Meeting m : meets) {
				String meetingDomain = PropertiesUtil.getContextProperty("live.meeting.domain");
				if(StringUtils.isNotBlank(meetingDomain)){
					try {
						URL url = new URL(meetingDomain);
						m.setDomain(url.getHost());
					} catch (MalformedURLException e) {
						throw new ServiceException("会议直播url配置错误");
					}
				}
				Long startTime = m.getStartTime();
				if(startTime > System.currentTimeMillis()){
					m.setStatus(1);//未开始
				}else{
					m.setStatus(2);//已开始
				}
				Integer createUserId = m.getCreateUserId();
				if(createUserId != null){
					User u = userRepository.getUser(createUserId);
					if (u != null) {
						m.setCreateUserName(u.getName());
						m.setHeadPicFileName(u.getHeadPicFileName());
					}
					Integer userId = ReqUtil.instance.getUserId();
					if(userId.intValue() == createUserId.intValue()){
						m.setIsMyCreate(1);
					}else{
						m.setIsMyCreate(0);
					}
				}
				/**
				 * 直播地址转成短连接
				 */
				if(StringUtils.isNotBlank(m.getAttendeeJoinUrl())){
					m.setAttendeeJoinUrl(shortUrlComponent.generateShortUrl(m.getAttendeeJoinUrl()));
				}
				if(StringUtils.isNotBlank(m.getOrganizerJoinUrl())){
					m.setOrganizerJoinUrl(shortUrlComponent.generateShortUrl(m.getOrganizerJoinUrl()));
				}
				if(StringUtils.isNotBlank(m.getPanelistJoinUrl())){
					m.setPanelistJoinUrl(shortUrlComponent.generateShortUrl(m.getPanelistJoinUrl()));
				}
			}
			pageVo.setPageData(meets);
		}
		pageVo.setTotal(count);
		return pageVo;
	}
}
