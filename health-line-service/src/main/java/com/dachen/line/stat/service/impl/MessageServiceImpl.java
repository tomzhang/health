package com.dachen.line.stat.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.line.stat.dao.IMessageDao;
import com.dachen.line.stat.entity.vo.Message;
import com.dachen.line.stat.service.IMessageService;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;

/**
 * 短信
 * 
 * @author liwei
 * @date 2015/12/14
 */
@Service
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private IMessageDao messageDao;

	@Override
	public List<Map<String, Object>> getMessageJsonList(Integer userId) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<Message> messageList = messageDao.getMessageList(new Integer[]{userId,Constant.SYSTEM_MESSAGE});
		if (ConfigUtil.checkCollectionIsEmpty(messageList)) {
			for (Message message : messageList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageId", message.getId());
				map.put("content", message.getContent());
				listMap.add(map);
			}
		}
		return listMap;
	}

	@Override
	public List<Message> getMessageList(Integer userId) {

		return messageDao.getMessageList(userId);
	}

	/**
	 * 修改短信内容
	 */
	@Override
	public void updateUserMessage(String id, String content) {
		messageDao.updateUserMessage(id, content);

	}

	@Override
	public void deleteUserMessage(String messageId) {
		messageDao.deleteUserMessage(messageId);
	}

	@Override
	public void insertUserMessage(Integer userId, String content) {
		Message message = new Message();
		message.setContent(content);
		message.setTime(new Date().getTime());
		message.setType(0);
		message.setUserId(userId);
		messageDao.insertUserMessage(message);
	}

}
