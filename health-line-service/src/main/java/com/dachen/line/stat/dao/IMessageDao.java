package com.dachen.line.stat.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.line.stat.entity.vo.Message;
import com.dachen.line.stat.entity.vo.UserHospital;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IMessageDao {
	
	/**
	 * 获取单个短信对象
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public Message getMessageById(String  id);
	
	
	/**
	 * 获取短信列表
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<Message> getMessageList(Integer userId);
	
	/**
	 * 获取短信列表 (后面会支持系统短信)
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<Message> getMessageList(Integer[] userId);
	
	/**
	 * 修改短信内容
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateUserMessage(String  id, String content);
	
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
	public Object  insertUserMessage(Message message);
}
