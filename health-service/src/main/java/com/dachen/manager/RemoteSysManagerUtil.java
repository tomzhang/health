package com.dachen.manager;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.net.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * 远程调用 外部系统接口
 * Created By lim
 * Date: 2017/6/8
 * Time: 16:59
 */
@Component
public class RemoteSysManagerUtil {
    private Logger _log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RibbonManager ribbonManager;
    @Autowired
    private RestTemplate restTemplate;
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
    
    public  String post(String url, Map<String,String> param){
        //参数校验
        if (StringUtils.isEmpty(url)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        String responseStr=ribbonManager.post(url, param);
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
    
    public  String get(String url, Map<String,String> param){
        //参数校验
        if (StringUtils.isEmpty(url)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        String responseStr=ribbonManager.get(url, param);
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

    public String postForObject(String url,Object... param){
        if (StringUtils.isEmpty(url)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        RemoteServiceResult response = restTemplate.postForObject(url, null, RemoteServiceResult.class, param);

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

    public Object getNotJson(String url, Object... uriVariables) {
        if (StringUtils.isEmpty(url)) {
            throw new ServiceException("远程接口地址不能为空");
        }
        String responseStr = ribbonManager.get(url, uriVariables);
        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);
        _log.info("内部访问地址:{},参数:{},返回值:{}", url, response);
        if (Objects.isNull(response)) {
            throw new ServiceException("远程接口调用失败！！！");
        }
        if (response.getResultCode() != 1) {
            _log.error("远程接口调用错误。错误码：{}，详细原因：{}" + response.getResultCode(), response.getDetailMsg());
            throw new ServiceException("远程接口调用失败：" + response.getDetailMsg());
        }
        return response.getData();
    }
}
