package com.dachen.health.file.listen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.health.file.service.IVpanFileService;
import com.dachen.mq.MQConstant;

/**
 * 文件分发监听 当用户发送文件给其他用户或者群组中，将该文件分发给群组中所有的人
 * 
 * @author wangqiao
 * @date 2016年1月15日
 *
 */
@Component
public class SendFileListen{

	private static String queueName = MQConstant.QUEUE_FILECARD_MSG;

	@Autowired
	private IVpanFileService vpanFileService;

}
