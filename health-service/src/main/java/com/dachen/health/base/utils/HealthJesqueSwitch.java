package com.dachen.health.base.utils;

import org.springframework.stereotype.Component;

import com.dachen.commons.schedule.task.JesqueSwitch;

@Component(value=JesqueSwitch.BEAN_ID)
public class HealthJesqueSwitch extends JesqueSwitch{

	@Override
	public String getJesqueSpace() {
		return "health";
	}

}
