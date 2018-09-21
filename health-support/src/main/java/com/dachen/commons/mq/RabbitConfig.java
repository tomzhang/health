package com.dachen.commons.mq;

import com.dachen.mq.config.MessageQueueConfig;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtils;
import org.springframework.stereotype.Component;

@Component(value=MessageQueueConfig.BEAN_ID)
public class RabbitConfig implements MessageQueueConfig{

//	@Autowired
//	private PropertiesUtil propertiesUtil;
//	 @Value("${mq.host:localhost}")
//	 private String host;
//	 
//	 @Value("${mq.port}")
//	 private String port;
//	 
//	 @Value("${mq.username:}")
//	 private String username;
//	 
//	 @Value("${mq.password:}")
//	 private String password;
//	 
//	 @Value("${mq.virtual.host:}")
//	 private String virtualHost;
//
//	/**
//	 * 和rabbitmq建立连接的超时时间
//	 */
//	@Value("${mq.timeout}")
//	private String connectionTimeout;
//	
//	/**
//	 * 事件消息处理线程数，默认是 CPU核数 * 2
//	 */
//	@Value("${mq.channel.cache.size}")
//	private String channelCacheSize;
	
	/**
	 * 每次消费消息的预取值
	 */
//	@Value("${mq.prefetch.size}")
//	private String prefetchSize;
	 
	 public String getHost() {
		return PropertiesUtil.getContextProperty("mq.host");
	 }


	 public int getPort() {
		return Integer.valueOf(PropertiesUtil.getContextProperty("mq.port"));
	 } 


	public String getUsername() {
		return PropertiesUtil.getContextProperty("mq.username");
	}


	public String getPassword() {
		return PropertiesUtil.getContextProperty("mq.password");
	}


	public String getVirtualHost() {
		return PropertiesUtil.getContextProperty("mq.virtual.host");
	}


	@Override
	public int getConnectionTimeout() {
		String connectionTimeout =PropertiesUtil.getContextProperty("mq.timeout");
		if(StringUtils.isEmpty(connectionTimeout))
		{
			return 0;
		}
		return Integer.valueOf(connectionTimeout);
	}

	@Override
	public int getChannelCacheSize() {
		String channelCacheSize =PropertiesUtil.getContextProperty("mq.channel.cache.size");
		if(StringUtils.isEmpty(channelCacheSize))
		{
			return MessageQueueConfig.DEFAULT_PROCESS_THREAD_NUM;
		}
		return Integer.valueOf(channelCacheSize);
	}

	@Override
	public int getPrefetchSize() {
		String prefetchSize =PropertiesUtil.getContextProperty("mq.prefetch.size");
		if(StringUtils.isEmpty(prefetchSize))
		{
			return MessageQueueConfig.PREFETCH_SIZE;
		}
		return Integer.valueOf(prefetchSize);
	}


}
