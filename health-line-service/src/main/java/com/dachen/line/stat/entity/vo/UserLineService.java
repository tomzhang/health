package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;


@Entity(value="v_user_line_service", noClassnameStored = true)
public class UserLineService {
	
	@Id
	private  String id;
	
	private Integer userId;//用户id
	 
	private @NotSaved LineServiceProduct lineServiceProduct;//产品服务
	 
	private String productId;//产品服务
	
	private Integer status;  // 0 未设置   1 打开 2 关闭

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public LineServiceProduct getLineServiceProduct() {
		return lineServiceProduct;
	}

	public void setLineServiceProduct(LineServiceProduct lineServiceProduct) {
		this.lineServiceProduct = lineServiceProduct;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}