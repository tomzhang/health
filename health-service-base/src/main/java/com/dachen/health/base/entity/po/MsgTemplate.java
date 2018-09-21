package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 文案模板
 * 
 * @author dwju
 *
 */
@Entity(value = "b_msg_template", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "category"), @Field(value = "title"), @Field(value = "content") }) })
public class MsgTemplate {

	@Id
	private String id;

	/**
	 * 种类(SMS-短信，IM-会话消息)
	 */
	private String category;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 参数个数.
	 */
	private int paraNum;

	/**
	 * 用途说明
	 */
	private String usage;

	/**
	 * 样例
	 */
	private String sample;

	/**
	 * 腾讯sms平台申请的短信模板id
	 */
	private Integer tencentId;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public int getParaNum() {
		return paraNum;
	}

	public void setParaNum(int paraNum) {
		this.paraNum = paraNum;
	}

	public Integer getTencentId() {
		return tencentId;
	}

	public void setTencentId(Integer tencentId) {
		this.tencentId = tencentId;
	}
}
