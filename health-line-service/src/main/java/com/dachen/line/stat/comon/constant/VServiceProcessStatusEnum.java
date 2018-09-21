package com.dachen.line.stat.comon.constant;

/**
 * @author weilit
 *
 */
public enum VServiceProcessStatusEnum {
	toStartOrder(1, "开始服务"),
	toUploadResult(2, "等待上传结果"),
	end(3, "结束"),
	toClosd(4, "申请关闭"),
	closed(5, "关闭");
	private int index;
	private String title;

	private VServiceProcessStatusEnum(int index, String title) {
		this.index = index;
		this.title = title;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static VServiceProcessStatusEnum getEnum(int index) {
		VServiceProcessStatusEnum e = null;
		for (VServiceProcessStatusEnum e1 : VServiceProcessStatusEnum.values())
			if (e1.index == index) {
				e = e1;
				break;
			}
		return e;
	}
}
