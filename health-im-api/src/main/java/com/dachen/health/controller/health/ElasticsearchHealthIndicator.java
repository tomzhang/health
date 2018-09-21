package com.dachen.health.controller.health;

import io.searchbox.client.JestClient;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * Created by sharp on 2018/1/8.
 * 暂时不对elasticsearch做健康检查
 */
@Component
public class ElasticsearchHealthIndicator extends AbstractHealthIndicator {
    private final JestClient jestClient;

    public ElasticsearchHealthIndicator(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
    }
}
