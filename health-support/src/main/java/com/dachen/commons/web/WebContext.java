package com.dachen.commons.web;

/**
 * 存放Web端上下文变量
 * @author xiaowei
 */
public class WebContext {
	private static ThreadLocal<WebContext> webContext = new ThreadLocal<WebContext>();
	private long startAt;
	private WebContext() {
		mark();
	}
	
	public void mark() {
		this.startAt = System.currentTimeMillis();
	}
	
	public static WebContext getContext() {
		WebContext ctx = webContext.get();
		if (null == ctx) {
			ctx = new WebContext();
			webContext.set(ctx);
		}
		return ctx;
	}

	public long getStartAt() {
		return startAt;
	}
	
}
