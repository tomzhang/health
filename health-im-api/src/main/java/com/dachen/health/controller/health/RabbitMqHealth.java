package com.dachen.health.controller.health;

import com.dachen.mq.producer.MqStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * Created by sharp on 2018/1/8.
 */
@Component
public class RabbitMqHealth extends AbstractHealthIndicator {
    @Autowired
    MqStatusService mqStatusService;

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        boolean flag = mqStatusService.isRunning();
        if (flag) builder.up().withDetail("run", flag);
        else builder.down().withDetail("run", "false").withDetail("message", "mq故障").build();
    }
}
