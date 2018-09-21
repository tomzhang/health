package com.dachen.health.group.common.util;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.net.HttpHelper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 远程调用 外部系统接口
 * Created By lim
 * Date: 2017/6/8
 * Time: 16:59
 */
@Component
public class RemoteSysManager {
    private Logger _log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RibbonManager ribbonManager;
    public static String sendPost(String serverUrl, String actionUrl,Map<String, String> params) {
        //参数校验
        if (StringUtils.isEmpty(serverUrl) && StringUtils.isEmpty(actionUrl)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        if (params == null) {
            throw new ServiceException("远程接口访问参数不能为空");
        }
        StringBuilder url = new StringBuilder();
        url.append("http://").append(serverUrl).append("/").append(actionUrl);

        // 远程调用
        String responseStr = HttpHelper.post(url.toString(), params);

        if (StringUtils.isEmpty(responseStr)) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);
        if (response == null) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        if (response.getResultCode() != 1) {
            throw new ServiceException("远程接口调用失败：" + response.getDetailMsg());
        }

        return JSON.toJSONString(response.getData());
    }

    public static String sendGet(String serverUrl, String actionUrl,Map<String, String> params) {

        //参数校验
        if (StringUtils.isEmpty(serverUrl) && StringUtils.isEmpty(actionUrl)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        if (params == null) {
            throw new ServiceException("远程接口访问参数不能为空");
        }
        StringBuilder url = new StringBuilder();
        url.append("http://").append(serverUrl).append("/").append(actionUrl);

        // 远程调用
        String responseStr = HttpHelper.get(url.toString(), params);

        if (StringUtils.isEmpty(responseStr)) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);

        if (response == null) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        if (response.getResultCode() != 1) {
            throw new ServiceException("远程接口调用失败：" + response.getDetailMsg());
        }

        return JSON.toJSONString(response.getData());
    }

    public  String send(String serverUrl, String actionUrl,Map<String,String> param){
        //参数校验
        if (StringUtils.isEmpty(serverUrl) && StringUtils.isEmpty(actionUrl)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        StringBuilder url = new StringBuilder();
        url.append("http://").append(serverUrl).append("/").append(actionUrl);
        String responseStr=ribbonManager.post(url.toString(),param);
        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);

        _log.info("内部访问地址:{},参数:{},返回值:{}",url,JSON.toJSONString(param),response);
        if (response == null) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        if (response.getResultCode() != 1) {
            _log.error("远程接口调用错误。错误码：{}，详细原因：{}"+response.getResultCode(),response.getDetailMsg());
            throw new ServiceException("远程接口调用失败：" + response.getDetailMsg());
        }

        return JSON.toJSONString(response.getData());
    }

    public  String post(String serverUrl, String actionUrl,Map<String,String> param,Map<String, String> headerMap){
        //参数校验
        if (StringUtils.isEmpty(serverUrl) && StringUtils.isEmpty(actionUrl)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        StringBuilder url = new StringBuilder();
        url.append("http://").append(serverUrl).append("/").append(actionUrl);
        String responseStr=ribbonManager.post(url.toString(),(Object) param,headerMap);
        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);

        _log.info("内部访问地址:{},参数:{},返回值:{}",url,JSON.toJSONString(param),response);
        if (response == null) {
            throw new ServiceException("远程接口调用失败！！！");
        }

        if (response.getResultCode() != 1) {
            _log.error("远程接口调用错误。错误码：{}，详细原因：{}"+response.getResultCode(),response.getDetailMsg());
            throw new ServiceException("远程接口调用失败：" + response.getDetailMsg());
        }

        return JSON.toJSONString(response.getData());
    }
}
