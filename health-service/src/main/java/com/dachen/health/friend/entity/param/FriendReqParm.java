package com.dachen.health.friend.entity.param;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.dachen.health.friend.entity.vo.SalesGoodsFileVO;

/**
 * 请求文件列表
 * @author weilit
 *
 */
public class FriendReqParm {
	
	private Integer doctorId;
	//药品文件列表
	private List<SalesGoodsFileVO> fileListReq = new ArrayList<SalesGoodsFileVO>();
	
	private String  fileList;

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public List<SalesGoodsFileVO> getFileListReq() {
		fileListReq = JSONArray.parseArray(fileList, SalesGoodsFileVO.class);
		return fileListReq;
	}

	public void setFileListReq(List<SalesGoodsFileVO> fileListReq) {
		this.fileListReq = fileListReq;
	}

	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}
	
	
	
}
