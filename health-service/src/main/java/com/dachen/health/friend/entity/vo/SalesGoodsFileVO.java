package com.dachen.health.friend.entity.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求文件列表
 * @author weilit
 *
 */
public class SalesGoodsFileVO {
	/**药品id **/
	private String goodsName;
	
	//药品文件列表
	private List<VpanGoodFileVO> fileList = new ArrayList<VpanGoodFileVO>();

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public List<VpanGoodFileVO> getFileList() {
		return fileList;
	}

	public void setFileList(List<VpanGoodFileVO> fileList) {
		this.fileList = fileList;
	}
    
	


}
