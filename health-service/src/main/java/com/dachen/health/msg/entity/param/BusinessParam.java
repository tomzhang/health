package com.dachen.health.msg.entity.param;


public class BusinessParam {

//	private GroupListRequestMessage msgGroupParam;
//
//	public GroupListRequestMessage getMsgGroupParam() {
//		return msgGroupParam;
//	}
//
//	public void setMsgGroupParam(GroupListRequestMessage msgGroupParam) {
//		this.msgGroupParam = msgGroupParam;
//	}
	private String userId;
    /**
     * MessageStat:
     * 	"10200":表示IOS客户端
     *  "10201":表示ANDROID
     */
//    private String client;

    private long ts;
    
    /**
     * 是否获取指令1:需要获取指令，0：不需要获取指令
     */
    private int needEvent=1;
    
    private int cnt = 0;
    
    private int newData=0;

    public String getNo() {
        return null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String uid) {
        this.userId = uid;
    }


    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getCnt() {
        if (cnt < 0) return 0;
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

	public int getNeedEvent() {
		return needEvent;
	}

	public void setNeedEvent(int needEvent) {
		this.needEvent = needEvent;
	}

	public int getNewData() {
		return newData;
	}

	public void setNewData(int newData) {
		this.newData = newData;
	}


 
	
}
