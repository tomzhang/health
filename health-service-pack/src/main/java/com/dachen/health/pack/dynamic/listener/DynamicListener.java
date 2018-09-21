package com.dachen.health.pack.dynamic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicListener {
	private static final Logger logger = LoggerFactory.getLogger(DynamicListener.class);

//    @EcEventMapping(type = {EventType.SendPubMsg})
//    public void sendPubMsg(EcEvent event) throws Exception {
//    	logger.info("send event {},{}",event.getType().getQueueName(),event.toJSONString());
//    	String mid = event.param("mid");
//    	String pid = event.param("pid");
//    	boolean needPush = event.param("needPush");
//    	List<String> users = event.param("subscriber");
////    	Set<String>subscriber = new HashSet<String>(users);
//    	
//		PubSendMessageRequest pubSendMsg = new PubSendMessageRequest();
//		pubSendMsg.setMid(mid);
//		pubSendMsg.setPid(pid);
//		pubSendMsg.setNeedPush(needPush);//
//		pubSendMsg.setSubscriber(new HashSet<String>(users));
//		MsgHelper.sendPubMsg(pubSendMsg);
//    }

}
