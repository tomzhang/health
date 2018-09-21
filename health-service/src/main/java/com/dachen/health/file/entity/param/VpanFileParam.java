package com.dachen.health.file.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * 文件查询参数VO
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
public class VpanFileParam extends PageVO{

	/**
	 * 文件id
	 */
//	private String fileId;
	
	/**
	 * 文件发送id
	 */
//	private String fileSendId;
	
	/**
	 * 上传者 id
	 */
	private Integer uploader;
	
	/**
	 * 接收者id
	 */
	private Integer receiveUserId;
	
	/**
	 * 查询 模式    upload=我上传的文件    receive=我接收的文件
	 */
	private String mode;
	
	/**
	 * 搜索  文件名称关键字
	 */
	private String fileNameKey;
	
	/**
	 * 文件分类  文档=document，图片=picture，视频=video，音乐=music，其它=other
	 */
	private String type;
	
	/**
	 * 排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序
	 */
	private String sortAttr;
	
	/**
	 * 排序顺序  1=顺序，-1=倒序
	 */
	private Integer sortType;
	
	/**
	 * 文件ids
	 */
	private List<String> fileIdList;

	public Integer getUploader() {
		return uploader;
	}

	public void setUploader(Integer uploader) {
		this.uploader = uploader;
	}

	public Integer getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Integer receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getFileNameKey() {
		return fileNameKey;
	}

	public void setFileNameKey(String fileNameKey) {
		this.fileNameKey = fileNameKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSortAttr() {
		return sortAttr;
	}

	public void setSortAttr(String sortAttr) {
		this.sortAttr = sortAttr;
	}

	public Integer getSortType() {
		return sortType;
	}

	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}

	public List<String> getFileIdList() {
		return fileIdList;
	}

	public void setFileIdList(List<String> fileIdList) {
		this.fileIdList = fileIdList;
	}
	
}

