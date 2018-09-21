package com.dachen.feature.mobile.controller;

import com.dachen.sdk.component.ReqComponent;

public abstract class MobileBaseController {
    protected Integer getCurrentUserId() {
        return ReqComponent.instance.getUserId();
    }
}
