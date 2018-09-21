package com.dachen.health.commons.vo;

/**
 * 用户登录 放在header  互助社区需要用的
 * Created By lim
 * Date: 2017/7/27
 * Time: 14:19
 */
public class CircleFilterVO {
    //圈子id
    String id;

    //在该圈子的角色1：管理员 2：圈主（负责人）3：顾问 逗号拼接 多个角色 可同时为管理员，圈主，顾问
    private String role;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
