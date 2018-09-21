package com.dachen.health.common.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class AndroidLogFilter extends Filter<ILoggingEvent>{

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getMessage().contains("android") || event.getMessage().contains("ANDROID") ) {
		      return FilterReply.ACCEPT;
	    } else {
	      return FilterReply.NEUTRAL;
	    }
	}

}
