package com.dachen.health.circle.lightapp.entity;

import java.io.Serializable;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * 应用注册信息表
 */
@Entity(value="c_light_app", noClassnameStored = true)
public class LightApp implements Serializable{

	private static final long serialVersionUID = 4708092049430928885L;

	@Id
	private String id;

	private String appId;
	private String appCode;//轻应用 编码

	/** 轻应用的名称 **/
	@Indexed
	private String name;
	/** desc ***/
	private String desc;
	/** 应用图标*/
	private String pic;
	private String newPic;
	private String bgPic;
	/** WEB协议
	 * <ul>
	 *     <li>app://signed 原生应用协议</li>
	 *     <li>lightapp:// 轻应用的协议，需要前端替换组织认证信息</li>
	 *     <li>http:// 直接访问</li>
	 *     <li>self://signed 自己的协议</li>
	 * </ul>
	 * */
	private String webProtocol;
	/** IOS端协议 */
	private String iosProtocol;
	/** Android端协议 */
	private String androidProtocol;

	/** 显示的最低版限制 */
	private String minVersion;
	/** 排序 */
	private Integer sort;
	/** 启用和禁用 */
	private Integer status;   //1启用 已上架 2禁用 已下架 3-待上架

	/** 创建人 **/
	private Integer creator;
	/** 创建时间 */
	private Long createTime;
	/** 更新人 **/
	private Integer updator;
	/** 更新时间 **/
	private Long lastUpdateTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getNewPic() {
		return newPic;
	}

	public void setNewPic(String newPic) {
		this.newPic = newPic;
	}

	public String getBgPic() {
		return bgPic;
	}

	public void setBgPic(String bgPic) {
		this.bgPic = bgPic;
	}

	public String getWebProtocol() {
		return webProtocol;
	}

	public void setWebProtocol(String webProtocol) {
		this.webProtocol = webProtocol;
	}

	public String getIosProtocol() {
		return iosProtocol;
	}

	public void setIosProtocol(String iosProtocol) {
		this.iosProtocol = iosProtocol;
	}

	public String getAndroidProtocol() {
		return androidProtocol;
	}

	public void setAndroidProtocol(String androidProtocol) {
		this.androidProtocol = androidProtocol;
	}

	public String getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
