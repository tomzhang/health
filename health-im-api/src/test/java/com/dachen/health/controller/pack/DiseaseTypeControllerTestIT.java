package com.dachen.health.controller.pack;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DiseaseTypeControllerTestIT extends BaseControllerTest {

	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/diseaseType";
	}

	


	@Test
	public void findByDept() {

		param.put("deptId", "AA");

		Response response = RestAssured.given().queryParams(param)
				.get("/findByDept");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	



	@Test
	public void findByName() {

		param.put("name", "结");

		Response response = RestAssured.given().queryParams(param)
				.get("/findByName");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	//获取常见疾病
	@Test
	public void getCommonDiseases() {

		param.put("pageIndex", 0);
		param.put("pageSize", 20);

		Response response = RestAssured.given().queryParams(param)
				.get("/getCommonDiseases");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	//添加常见疾病
	@Test
	public void addCommonDisease() {
		
		param.put("diseaseId", "WK0110");
		param.put("name", "破伤风");
		
		Response response = RestAssured.given().queryParams(param)
				.get("/addCommonDisease");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		
		Assert.assertEquals(1, resultCode);
		
	}
	
	//移除常见疾病
	@Test
	public void removeCommonDisease() {
		
		param.put("diseaseId", "WK0110");
		
		Response response = RestAssured.given().queryParams(param)
				.get("/removeCommonDisease");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		
		Assert.assertEquals(1, resultCode);
		
	}
	
	//上升常见疾病排序
	@Test
	public void riseCommonDisease() {
		
		param.put("diseaseId", "WK0110");
		
		Response response = RestAssured.given().queryParams(param)
				.get("/riseCommonDisease");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		
		Assert.assertEquals(1, resultCode);
		
	}
	
	//搜索疾病
	@Test
	public void searchDiseaseTreeByKeyword() {
		
		param.put("keyword", "病毒");
		
		Response response = RestAssured.given().queryParams(param)
				.get("/searchDiseaseTreeByKeyword");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		
		Assert.assertEquals(1, resultCode);
		
	}
	
	//获取疾病别名
	@Test
	public void getDiseaseAlias() {
		
		param.put("diseaseId", "WK0110");
		
		Response response = RestAssured.given().queryParams(param)
				.get("/getDiseaseAlias");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		
		Assert.assertEquals(1, resultCode);
		
	}
	
}
