package com.dachen.health.community.entity.po;

import java.util.List;

public class TopicContent {
	private String text;
	private String richText;
	private List<String> label;
	private String title;
	private Integer richTextLength;
	/**
	 * 摘要
	 */
	private String digest;

	public Integer getRichTextLength() {
		return richTextLength;
	}

	public void setRichTextLength(Integer richTextLength) {
		this.richTextLength = richTextLength;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getRichText() {
		return richText;
	}
	public void setRichText(String richText) {
		this.richText = richText;
	}
	public List<String> getLabel() {
		return label;
	}
	public void setLabel(List<String> label) {
		this.label = label;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
