package com.dachen.health.friend.entity.po;

import java.lang.reflect.Field;

import com.dachen.commons.exception.ServiceException;

/**
 * 
 * ProjectName： health-service<br>
 * ClassName： FriendSetting<br>
 * Description： <br>
 * @author limiaomiao
 * @crateTime 2015年7月7日
 * @version 1.0.0
 */
public class FriendSetting {
	
	public FriendSetting(){
		defriend=1;
		topNews=1;
		messageMasking=1;
		collection=1;
	}

	/**
	 *拉黑  1:否，2:是
	 */
	private Integer defriend;
	
	/**
	 * 消息置顶 1:否，2：是
	 */
	private  Integer topNews;
	
	/**
	 * 消息屏蔽 1：否，2：是
	 */
	private Integer messageMasking;
	
	/**
	 * 收藏 1：否，2：是
	 */
	private Integer collection;

	public Integer getDefriend() {
		return defriend;
	}

	public void setDefriend(Integer defriend) {
		this.defriend = defriend;
	}

	public Integer getTopNews() {
		return topNews;
	}

	public void setTopNews(Integer topNews) {
		this.topNews = topNews;
	}

	public Integer getMessageMasking() {
		return messageMasking;
	}

	public void setMessageMasking(Integer messageMasking) {
		this.messageMasking = messageMasking;
	}

	public Integer getCollection() {
		return collection;
	}

	public void setCollection(Integer collection) {
		this.collection = collection;
	}
	/**
	 * 
	 * </p>校验指定参数值</p>
	 * @param i
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月10日
	 */
	public boolean verify(Integer i){
		if(i!=null){
			if(i!=1&&i!=2){
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 * </p>参数校验</p>
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月10日
	 */
	public int  verifys(){
		int count=0;
		Field[] fileds=getClass().getDeclaredFields();
		for (Field field : fileds) {
			try {
				Integer value=(Integer) field.get(this);
				if(!verify(value)){
					throw new ServiceException(""+field.getName()+" 's value "+value+" is not correct");
				}else{
					count++;
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}
	public static void main(String[] args) {
		FriendSetting setting=new FriendSetting();
		setting.setCollection(4);
		int count=setting.verifys();
		System.out.println(count);
	}
	
}
