package com.dachen.health.circle.vo;

import com.dachen.health.commons.vo.User;
import io.swagger.annotations.ApiModelProperty;

/**
 * 登录返回信息
 * Created By lim
 * Date: 2017/7/19
 * Time: 17:51
 */
public class MobileLoginUserVO {

    @ApiModelProperty(value = "登录状态,3:未审核 2:待审核 4:未认证 5:用户名不存在 1:密码错误 0:登录成功 6:验证码错误 7:验证码失效")
    private Integer loginStatus;

    @ApiModelProperty(value = "调用接口的token")
    private String accessToken;

    @ApiModelProperty(value = "登录人信息")
    private User user;

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
