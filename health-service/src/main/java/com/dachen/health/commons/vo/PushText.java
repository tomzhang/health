package com.dachen.health.commons.vo;

import java.net.HttpURLConnection;
import java.net.URL;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.commons.support.spring.convert.MappingFastjsonHttpMessageConverter.ObjectIdSerializer;
import com.dachen.util.FileUtil;
import com.mongodb.BasicDBObject;

public class PushText {
	public static final SerializeConfig serializeConfig;
	static {
		serializeConfig = new SerializeConfig();
		serializeConfig.put(ObjectId.class, new ObjectIdSerializer());
	}

	public static final int CTU_APPLY_AGREE = 201;// 收到招聘方同意申请，进入初试
	public static final int CTU_AFT_PASS = 202;// 收到招聘方通过初试，进入面试
	public static final int CTU_AFT_NOT_PASS = 203;// 收到招聘方未通过初试
	public static final int CTU_ART_INVITE = 204;// 收到招聘方面试邀请
	public static final int CTU_ART_TIME_SET = 205;// 收到招聘方重设的面试时间
	public static final int CTU_ART_PASS = 206;// 收到招聘方通过面试
	public static final int CTU_ART_NOT_PASS = 207;// 收到招聘方未通过面试
	public static final int CTU_APPLY_PASS_ART = 210;// 申请通过，直接面试
	public static final int CTU_APPLY_PASS_END = 211;// 申请通过，直接线下复试
	public static final int CTU_AFT_PASS_END = 212;// 初试通过，直接线下复试

	public static final int APPLY_SAVE = 300;// 收到应聘方应聘申请
	public static final int AFT_ANSWER = 301;// 收到应聘方交卷
	public static final int AFT_REFUSE = 302;// 收到应聘方拒绝初试
	public static final int ART_AGREE = 303;// 收到应聘方确定面试时间
	public static final int ART_REFUSE = 304;// 收到应聘方拒绝面试

	public static final int JOB_PAUSE = 305;//
	public static final int JOB_RESUME = 306;//
	public static final int JOB_CANCEL = 307;//
	public static final int JOB_DELETE = 308;//

	private int type;
	private String fromUserName;
	private String fromUserId;
	private String toUserId;
	private Object objectId;
	private Object content;
	private long timeSend = System.currentTimeMillis();

	public PushText(int type, int fromUserId, int toUserId, Object objectId) {
		super();
		this.type = type;
		this.fromUserId = String.valueOf(fromUserId);
		this.toUserId = String.valueOf(toUserId);
		this.objectId = objectId;
	}

	public PushText(int type, int fromUserId, int toUserId, Object objectId, Object content) {
		super();
		this.type = type;
		this.fromUserId = String.valueOf(fromUserId);
		this.toUserId = String.valueOf(toUserId);
		this.objectId = objectId;
		this.content = content;
	}

	public PushText(int fromUserId, String fromUserName, int toUserId, Object content) {
		super();
		this.type = 1;
		this.fromUserId = String.valueOf(fromUserId);
		this.fromUserName = fromUserName;
		this.toUserId = String.valueOf(toUserId);
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public Object getObjectId() {
		return objectId;
	}

	public void setObjectId(Object objectId) {
		this.objectId = objectId;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getTimeSend() {
		return timeSend;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this, serializeConfig);
	}

	public void sendByFromUserId() {
		sendBy(Integer.parseInt(fromUserId));
	}

	public void send() {
		sendBy(10004);
	}

	public void sendBy(int sUserId) {
		try {
			String spec = "";//String.format(SystemConfig.getInstance().getPushUrl(), sUserId, JSON.toJSONString(Arrays.asList(getToUserId())),this);
			URL url = new URL(spec);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setReadTimeout(10000);

			String text = FileUtil.readAll(urlConn.getInputStream());
			System.out.println(text);
			Datastore dsForRW = SpringBeansUtils.getBean("dsForRW");

			BasicDBObject jo = new BasicDBObject();
			jo.put("fromUserId", fromUserId);
			jo.put("toUserId", toUserId);
			jo.put("spec", spec.replaceAll("\"", "'"));

			dsForRW.getDB().getCollection("p_logs").save(jo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
