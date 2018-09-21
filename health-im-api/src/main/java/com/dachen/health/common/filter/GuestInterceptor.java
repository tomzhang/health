package com.dachen.health.common.filter;

import com.dachen.commons.web.WebContext;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.manager.TipsManager;
import com.dachen.sdk.exception.UserNotAuthorizationException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.web.MvcController;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by fuyongde on 2017/4/21.
 */
public class GuestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GuestInterceptor.class);

    /**
     * Gets access token.
     *
     * @param request the request
     * @return the access token
     * @throws UserNotAuthorizationException the user not authorization exception
     */
    protected String getAccessToken(HttpServletRequest request) throws UserNotAuthorizationException {
        String accessToken = request.getHeader("access_token");
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter("access_token");
        }
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getHeader("access-token");
        }
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter("access-token");
        }

        return accessToken;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        String tag = "preHandle";
        this.printRequestParams(request);

        String accessToken = this.getAccessToken(request);

        WebContext.getContext().mark();

        MvcController c = this.getController(handler);
        if (null != c) {
            c.init();
            if (!c.needLogin()) {
                return true;
            }
        }

        // 需要登录 请求令牌是否包含
        if (StringUtil.isEmpty(accessToken)) {
            int tipsKey = 1030101;
            renderByTipsKey(response, tipsKey);
            if (logger.isInfoEnabled()) {
                logger.info("{}. accessToken is null!. uri={}, ret={}", tag, request.getRequestURI(), false);
            }
            return false;
        }

        if (accessToken.startsWith(UserEnum.IS_Guest_TOKEN)) {
            return validataGuestToken(response,accessToken);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
        ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
        Exception e) throws Exception {

    }

    private MvcController getController(Object handler) {
        if (handler instanceof MvcController) {
            return (MvcController) handler;
        } else if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getBean() instanceof MvcController) {
                return (MvcController) hm.getBean();
            }
        }
        logger.warn("!!!Handler is not instanceof MvcController");
        return null;
    }

    protected void printRequestParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求：" + request.getRequestURI());
        sb.append("\n");

        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            sb.append("?");

            for (String key : paramMap.keySet()) {
                sb.append(key).append("=").append(paramMap.get(key)[0]).append("&");
            }

            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }

        sb.append("Content-Type：" + request.getContentType());
        sb.append("\n");

        sb.append("User-Agent：" + request.getHeader("User-Agent"));
        sb.append("\n");

        sb.append("**************************************************");
        sb.append("\n");

        if (logger.isInfoEnabled()) {
            logger.info(sb.toString());
        }
    }

    /**
     * 游客过滤方法
     *
     * @author liming
     */
    public boolean validataGuestToken(HttpServletResponse response, String guest_token) {
        if (StringUtil.isBlank(guest_token)) {
            int tipsKey = 1030103;
            renderByTipsKey(response, tipsKey);
            return false;
        } else {
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
