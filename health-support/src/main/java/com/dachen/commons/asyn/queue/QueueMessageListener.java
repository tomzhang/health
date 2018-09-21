package com.dachen.commons.asyn.queue;


import java.util.concurrent.Executor;

public interface QueueMessageListener {

    public void recieveMessages(String topic, String msg);

    public Executor getExecutor();
}
