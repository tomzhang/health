package com.dachen.health.controller.group.department;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DepartmentDoctorControllerTestIT {

	private String access_token = null;

	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken(TestConstants.telephone_doc, TestConstants.password_doc,
				TestConstants.userType_doc);

		RestAssured.basePath = "/department/doctor";

	}

	/**
	 * 测试集团增加医生
	 */
	// @Test
	public void testRegAddDoctorByDepartmentId() throws UnsupportedEncodingException {

		for (int i = 40; i < 50; i++) {
			RestAssured.basePath = "/user";
			String userName = "用户100" + i;
			Response response1 = given().param("access_token", access_token).param("nickname", userName)
					.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
					.post("/query");
			JsonPath jp1 = new JsonPath(response1.asString());
			Integer userId = (Integer) jp1.get("data[0].userId");

			RestAssured.basePath = "/department/doctor";
			Response response = given().param("access_token", access_token)
					.param("departmentId", "55d58a12cb827c15fc9d47b4")
					.param("doctorId", userId)
					.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
					.post("/addDoctorByDepartmentId");
			JsonPath jp = new JsonPath(response.asString());

			assertEquals(new Integer(1), jp.get("resultCode"));
		}

	}

//	@Test
	public void testUpdateDepartment() {
		RestAssured.basePath = "/department/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.param("doctorId", 531)
				.param("departmentId", "55d045054f13031bd408e8b2")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateDepartment");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

}
