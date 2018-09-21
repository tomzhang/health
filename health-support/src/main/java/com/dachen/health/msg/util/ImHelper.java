package com.dachen.health.msg.util;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.net.HttpHelper;
import com.dachen.im.server.data.response.Result;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 与im交互的工具类
 *
 * @author Administrator
 */
@Component
public class ImHelper implements InitializingBean {

    public static String IM_URL = "http://IM/";
    public static String FILE_UPLOAD_URL = PropertiesUtil.getContextProperty("fileserver.upload");

    @Autowired
    private RibbonManager ribbonManager;

    public static String getFileUploadUrl() {
        return FILE_UPLOAD_URL;
    }

    /**
     * action:im/convers/send.action
     */
    public JSON postJson(String prefix, String action, Object request) {
        if (StringUtils.isEmpty(action)) {
            throw new ServiceException("参数错误，action为空");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(IM_URL);
        if (!IM_URL.endsWith("/")) {
            builder.append("/");
        }
        if (!StringUtils.isEmpty(prefix)) {
            builder.append(prefix).append("/");
        }
        builder.append(action);
        String response = ribbonManager.post(builder.toString(), request);
        if (response == null) {
            throw new ServiceException("IM没有返回结果。");
        }
        Result result = JSONUtil.parseObject(Result.class, response);
        if (result.getResultCode() != 1) {
            throw new ServiceException(result.getResultCode(), result.getDetailMsg());
        }
        return (JSON) result.getData();
    }

    public static void main(String args[]) {
        String url = IM_URL + "/convers/groupList.action";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("uid", "1009");
        String entity = HttpHelper.post(url, paramMap);
        System.out.println(entity);
    }

    public static ImHelper instance;

    private ImHelper() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }
}
