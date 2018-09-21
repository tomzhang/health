package com.dachen.health.im.service;

import java.util.List;
import java.util.Map;

import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.GroupListRequestMessage;
import com.dachen.im.server.data.request.MsgListRequestMessage;
import com.dachen.im.server.data.response.GroupListVO;
import com.dachen.im.server.data.response.MsgListVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IMsgFacadeService {

	/**
	 * 获取会话列表接口
	 * @param request
	 * @return
	 */
	GroupListVO groupList(GroupListRequestMessage request) throws HttpApiException;
	
	/**
	 * 获取会话组状态信息
	 * @param gid
	 * @return
	 */
	List<Map<String, Object>> getGroupParams(String...gids) throws HttpApiException;
	
	Map<String, Object> getGroupParam(String gid) throws HttpApiException;
	
	/**
	 * 获取会话组明细信息
	 * @param gid
	 * @return
	 */
	Object getGroupInfo(GroupInfoRequestMessage request) throws HttpApiException;
}
