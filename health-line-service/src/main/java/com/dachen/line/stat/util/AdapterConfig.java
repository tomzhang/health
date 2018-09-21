package com.dachen.line.stat.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author tianhong
 * @Description
 * @date 2018/5/3 14:39
 * @Copyright (c) 2018, DaChen All Rights Reserved.
 */
@Component
@RefreshScope
public class AdapterConfig {

    @Value("${adapter.path}")
    private String adapterPath;

    @Value("${adapter.token.path}")
    private String authenticationPath;

    @Value("${adapter.menu.path}")
    private String menuAuthorityPath;

    @Value("${adapter.update.path}")
    private String adapterUpdatePath;

    @Value("${adapter.client.id}")
    private String clientId;

    @Value("${adapter.client.secret}")
    private String clientSecret;

    public String getAdapterPath() {
        return adapterPath;
    }

    public void setAdapterPath(String adapterPath) {
        this.adapterPath = adapterPath;
    }

    public String getAuthenticationPath() {
        return authenticationPath;
    }

    public void setAuthenticationPath(String authenticationPath) {
        this.authenticationPath = authenticationPath;
    }

    public String getMenuAuthorityPath() {
        return menuAuthorityPath;
    }

    public void setMenuAuthorityPath(String menuAuthorityPath) {
        this.menuAuthorityPath = menuAuthorityPath;
    }

    public String getAdapterUpdatePath() {
        return adapterUpdatePath;
    }

    public void setAdapterUpdatePath(String adapterUpdatePath) {
        this.adapterUpdatePath = adapterUpdatePath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
