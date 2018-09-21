package com.dachen.health.commons.vo;

/**
 * 农牧项目新增的字段，用来记录农牧项目相关的标记
 * @author fuyongde
 *
 */
public class Farm {
	/**是否需要重置**/
	private Boolean needResetPhoneAndPass;
	/**是否为农牧项目**/
	private Boolean isFarm;

	public Boolean getNeedResetPhoneAndPass() {
		return needResetPhoneAndPass;
	}

	public void setNeedResetPhoneAndPass(Boolean needResetPhoneAndPass) {
		this.needResetPhoneAndPass = needResetPhoneAndPass;
	}

	public Boolean getIsFarm() {
		return isFarm;
	}

	public void setIsFarm(Boolean isFarm) {
		this.isFarm = isFarm;
	}
	
}
