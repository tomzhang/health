package com.dachen.health.controller.group;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class HospitalBaseInfoControllerTestIT extends BaseControllerTest {

    @Override
    public void setUp() throws UnsupportedEncodingException {
        RestAssured.baseURI = "http://localhost:9001";
        RestAssured.port = 9001;
        /*loginData = TestUtil.testGetLoginToken(TestConstants.telephone_admin,
                TestConstants.password_admin, TestConstants.userType_admin);
        data = ((Map<String, Object>) loginData.get("data"));
        // 获取登录token
        access_token = (String) ((Map<String, Object>) loginData.get("data")).get("access_token");
        param.clear();
        param.put("access_token", access_token);*/
        RestAssured.basePath = "/group/hospital";
    }

    @Test
    public void testGetHospitalLevel() throws UnsupportedEncodingException {
        Response response =
                given().param("access_token", access_token)
                        .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                .post("/hospitalLevel");
        JsonPath jp = new JsonPath(response.asString());

        assertEquals(new Integer(1), jp.get("resultCode"));

    }
}
