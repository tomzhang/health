package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.line.stat.entity.vo.Message;


/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IMessageService {
	
	public  List<Message> getMessageList(Integer userId);
	/**
	 * 获取短信列表
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<Map<String,Object>> getMessageJsonList(Integer userId);
	
	/**
	 * 修改短信内容
	 * @param messageId
	 * @param content
	 */
	public void updateUserMessage(String  messageId, String content);
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public void  deleteUserMessage(String messageId);
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public void  insertUserMessage(Integer userId, String content);
	
}
