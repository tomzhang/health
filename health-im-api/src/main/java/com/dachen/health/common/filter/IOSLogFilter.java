package com.dachen.health.common.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class IOSLogFilter extends Filter<ILoggingEvent>{

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getMessage().contains("iPhone") || event.getMessage().contains("iOS") ) {
		      return FilterReply.ACCEPT;
	    } else {
	      return FilterReply.NEUTRAL;
	    }
	}

}
