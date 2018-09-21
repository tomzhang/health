package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Author: 许奂杰
 * Date: 2018-04-19
 * Time: 17:06
 * Description:
 */
public class AddAdminUserParam {

    @ApiModelProperty(value = "用户Id(编辑时候传)")
    private Integer userId;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "重复密码")
    private String repeatPassword;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "角色权限Id集合")
    private List<String> roleIds;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
