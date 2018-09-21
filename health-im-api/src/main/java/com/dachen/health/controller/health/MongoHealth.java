package com.dachen.health.controller.health;

import com.mongodb.CommandResult;
import org.mongodb.morphia.AdvancedDatastore;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by sharp on 2018/1/8.
 */
@Component
public class MongoHealth extends AbstractHealthIndicator {
    @Resource(name = "dsForRW")
    protected AdvancedDatastore dsForRW;

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        CommandResult result = this.dsForRW.getDB().command("buildInfo");
        builder.up().withDetail("version", result.getString("version"));
    }
}
