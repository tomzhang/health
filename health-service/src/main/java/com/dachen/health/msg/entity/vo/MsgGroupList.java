package com.dachen.health.msg.entity.vo;

import java.util.ArrayList;
import java.util.List;

import com.dachen.im.server.data.response.MsgGroupDetail;

public class MsgGroupList {
	/**
	 * 未读消息总数
	 */
	private int ur;
	/**
	 * 会话组总个数
	 */
	private int count;
	private long ts;
	private boolean more;
	/**
	 * 会话组
	 */
	private List<MsgGroupDetail> list=new ArrayList<MsgGroupDetail>();

	public int getUr() {
		return ur;
	}

	public void setUr(int ur) {
		this.ur = ur;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<MsgGroupDetail> getList() {
		return list;
	}

	public void setList(List<MsgGroupDetail> list) {
		this.list = list;
	}


	public MsgGroupList(int ur, int count, List<MsgGroupDetail> list) {
		super();
		this.ur = ur;
		this.count = count;
		this.list = list;
	}
	public MsgGroupList() {
		super();
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
}
