package com.dachen.health.common.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.dachen.manager.TipsManager;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;


public class LoginFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
	 
	private static final String template = "{\"resultCode\":%1$s,\"resultMsg\":\"%2$s\"}";
	 
	private final static String DEFAULT_USERID="anonymous";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		
		String accessToken = request.getHeader("access_token");
        if(StringUtil.isEmpty(accessToken))
        {
        	accessToken = request.getParameter("access_token");
        }
        if(StringUtil.isEmpty(accessToken))
        {
        	accessToken = request.getHeader("access-token");
        }
        if(StringUtil.isEmpty(accessToken))
        {
        	accessToken = request.getParameter("access-token");
        }
        // DEBUG**************************************************DEBUG
        StringBuilder sb = new StringBuilder();
        sb.append("请求：" + request.getRequestURI());
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            sb.append("?");
        }
        for (String key : paramMap.keySet()) {
            sb.append(key).append("=").append(paramMap.get(key)[0]).append("&");
        }
        sb.append("\n");
       // System.out.println(sb.toString());
        sb.append("Content-Type：" + request.getContentType()).append("\n");
        
        //System.out.println("Content-Type：" + request.getContentType());
        sb.append("User-Agent：" + request.getHeader("User-Agent")).append("\n");
        sb.append("**************************************************").append("\n");
        
        // 需要登录
        // 请求令牌是否包含
        if (StringUtil.isEmpty(accessToken)) {
            int tipsKey = 1030101;
            renderByTipsKey(response, tipsKey);
            MDC.put("userId",DEFAULT_USERID);  
            return ;
        } else {
            Integer userId = ReqUtil.instance.getUserId(accessToken);
            // 请求令牌是否有效
            if (0 == userId) {
                int tipsKey = 1030102;
                renderByTipsKey(response, tipsKey);
                MDC.put("userId",DEFAULT_USERID);  
                return ;
            }
            MDC.put("userId",userId+"");
        }
        
        logger.info(sb.toString());
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
	
    private static void renderByTipsKey(ServletResponse response, int tipsKey) {
        String tipsValue = TipsManager.getTipsValue(tipsKey);
        String s = String.format(template, tipsKey, tipsValue);

        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(s);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


}
