package com.dachen.health.circle.form;

import java.util.List;

/**
 * @author wangj
 * @desc
 * @date:2017/6/15 10:04
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class AreaInfo {

    private AreaRangeInfo province;

    private AreaRangeInfo city;

	public AreaRangeInfo getProvince() {
		return province;
	}

	public void setProvince(AreaRangeInfo province) {
		this.province = province;
	}

	public AreaRangeInfo getCity() {
		return city;
	}

	public void setCity(AreaRangeInfo city) {
		this.city = city;
	}
}
