package com.dachen.health.common.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dachen.commons.component.VersionComponent;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.manager.TipsManager;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@Deprecated
public class LoginGuestInterceptor implements HandlerInterceptor{
	private static final Logger logger = LoggerFactory.getLogger(LoginGuestInterceptor.class);
	 
	@Autowired
	VersionComponent versionComponent;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
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
	        logger.info(sb.toString());
	        // DEBUG**************************************************DEBUG
	        //版本控制
	        versionComponent.requestProcess(request);
	        // 需要登录
	        // 请求令牌是否包含
	        if (StringUtil.isEmpty(accessToken)) {
	        	 int tipsKey = 1030101;
	             renderByTipsKey(response, tipsKey);
	             return false;
	           
	        } else {
	        	
	        	if (accessToken.startsWith(UserEnum.IS_Guest_TOKEN)) {
	        		return validataGuestToken(response,accessToken);
	        	} else {
	        		Integer userId = ReqUtil.instance.getUserId(accessToken);
	        		if(userId==0){
	        			int tipsKey = 1030102;
	    	            renderByTipsKey(response, tipsKey);
	    	            return false;	
	        		}else{
	        			return true;
	        		}
	        	}
	            
	          
	        }
	        
	}
	/**
	 * 游客过滤方法
	 * @param response
	 * @param guest_token
	 * @return
	 * @author liming
	 * 
	 */
	public boolean validataGuestToken(HttpServletResponse response,String guest_token){
		if(StringUtil.isEmpty(guest_token)){
			 int tipsKey = 1030103;
	            renderByTipsKey(response, tipsKey);
	            return false;	
		}else{
			Integer userId = ReqUtil.instance.getGuestUserId(guest_token);
            // 请求令牌是否有效
            if (0 == userId) {
                int tipsKey = 1030104;
                renderByTipsKey(response, tipsKey);
                return false;
            }
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	private static final String template = "{\"resultCode\":%1$s,\"resultMsg\":\"%2$s\"}";

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
