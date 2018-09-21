package com.dachen.manager;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dachen.health.commons.dao.TipsRepository;
import com.google.common.collect.Maps;

@Component
public class TipsManager implements ApplicationContextAware {

	private static Map<Integer, String> tipsMap = Maps.newHashMap();

	public static String getTipsValue(int tipsKey) {
		return tipsMap.get(tipsKey);
	}

	private TipsRepository tipsRepository;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
	    tipsRepository = context.getBean(TipsRepository.class);
	    tipsRepository.findAll().forEach(tips -> {
			tipsMap.put(tips.getTipsKey(), tips.getTipsValue());
		});
	}

}
