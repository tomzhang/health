package com.dachen.health.meeting.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.meeting.po.Meeting;
import com.dachen.sdk.exception.HttpApiException;

public interface MeetingService {

	Meeting createMeeting(Meeting meeting);

	Meeting updateMeeting(Meeting meeting);

	void stopMeeting(String webcastId);

	PageVO listMeeting(String companyId, Integer pageIndex, Integer pageSize) throws HttpApiException;

}
