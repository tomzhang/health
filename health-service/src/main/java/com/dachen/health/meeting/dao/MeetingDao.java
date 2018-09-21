package com.dachen.health.meeting.dao;

import java.util.List;

import com.dachen.health.meeting.po.Meeting;

public interface MeetingDao {

	Meeting insertMeeting(Meeting meeting);

	Long getDbAttendeesCount(Long startTime, Long endTime, String id);

	Meeting updateMeeting(Meeting meeting);

	Meeting getMeetingById(String meetingId);

	Long getMeetingCount(String companyId);

	List<Meeting> getMeetingList(String companyId, Integer pageIndex, Integer pageSize);

}
