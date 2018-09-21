package com.dachen.line.stat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.net.HttpHelper;
import com.dachen.health.group.common.util.HttpUtil;
import com.dachen.line.stat.entity.UserRequestParam;
import com.dachen.line.stat.service.IAdapterService;
import com.dachen.line.stat.util.AdapterConfig;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author tianhong
 * @Description 病例库调用服务
 * @date 2018/5/3 15:10
 * @Copyright (c) 2018, DaChen All Rights Reserved.
 */
@Service
public class AdapterServiceImpl implements IAdapterService {

    protected static Logger logger = LoggerFactory.getLogger(AdapterServiceImpl.class);

    @Autowired
    private AdapterConfig adapterConfig;

    @Autowired
    private Auth2Helper auth2Helper;

    @Override
    public HashMap<String, Object> getAuthenticationInfo(String userId, String communityId) {
        // 获取圈子成员openID
        String openId = "";
        List<AccessToken> openIdList = auth2Helper.getOpenIdList(Arrays.asList(Integer.valueOf(userId)));
        if (!CollectionUtils.isEmpty(openIdList)) {
            AccessToken accessToken = openIdList.get(0);
            openId = accessToken.getOpenId();
        }
        Assert.notNull(openId,"当前用户没有获取到openID.");

        HashMap<String, Object> map = Maps.newHashMap();
        map.put("ClientID", adapterConfig.getClientId());
        map.put("ClientSecret", adapterConfig.getClientSecret());
        map.put("OpenId", openId);
        map.put("CommunityId", communityId);

        // 获取token
        String token = reqToken(map);
        Assert.notNull(token,"获取token失败.");
        // 获取菜单权限
        String result = sendGet(adapterConfig.getAdapterPath().concat(adapterConfig.getMenuAuthorityPath().replace("{communityId}", communityId)), token);
        if (!StringUtil.isEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String value = jsonObject.getString("value");
            if (Objects.nonNull(value)) {
                JSONArray array = JSON.parseArray(value);
                map.clear();
                map.put("token", token);
                map.put("display", array.size());
                map.put("openId", openId);
            }
        }
        return map;
    }

    @Override
    public Map<String,Object> updateUser(UserRequestParam userRequestParam) {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("CommunityId", userRequestParam.getCommunityId());
        map.put("OpenId", userRequestParam.getOpenId());
        map.put("Role", userRequestParam.getRole());
        map.put("Name", userRequestParam.getName());
        map.put("Departments", userRequestParam.getDepartments());
        map.put("Title", userRequestParam.getTitle());
        map.put("Hospital", userRequestParam.getHospital());
        map.put("headPicFileName", userRequestParam.getHeadPicFileName());

        String path =adapterConfig.getAdapterPath().concat(adapterConfig.getAdapterUpdatePath());
        logger.info("调用病例库修改用户信息接口:{} ，请求参数：{}",path, JSONMessage.toJSONString(map));
        String result = sendPostJson(path, JSONObject.toJSONString(map), userRequestParam.getToken());
        HashMap<String,Object> resultMap = new HashMap<>();
        Boolean success = false;
        String url=null;
        if (Objects.nonNull(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            success = (Boolean) jsonObject.get("success");
            JSONObject json = (JSONObject)jsonObject.get("value");
            url= json.getString("url");
            resultMap.put("success",success);
            resultMap.put("url",url);
        }
        return resultMap;
    }

    private String reqToken(HashMap<String, Object> map) {
        String path = adapterConfig.getAdapterPath().concat(adapterConfig.getAuthenticationPath());
        logger.info("接口:{} ,请求参数：{}", path, JSONObject.toJSONString(map));
        String token = null;
        try {
            String result = sendPostJson(path, JSONObject.toJSONString(map), null);
            JSONObject jsonObj = JSONObject.parseObject(result);
            if(Objects.nonNull(jsonObj)) {
                String flag = jsonObj.getString("success");
                if (Objects.equals("true", flag)) {
                    JSONObject jsonObject = (JSONObject) jsonObj.get("value");
                    token = jsonObject.getString("token");
                } else {
                    throw new ServiceException("调用接口获取token 失败.");
                }
            }
        } catch (RestClientException e) {
            throw new ServiceException("POST 请求，远程调用失败.");
        }
        return token;
    }

    private static String sendPostJson(String url, String jsonParam, String token) {
        logger.info("远程请求地址：{}",url);
        String respContent = null;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(url);

        //解决中文乱码问题
        StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        if (!StringUtil.isEmpty(token)) {
            httpPost.setHeader("token", token);
        }
        try {
            httpPost.setEntity(entity);
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                respContent = EntityUtils.toString(httpResponse.getEntity(), "UTF-8").trim();
            }
        } catch (Exception e) {
            throw  new ServiceException("远程调用："+url+"异常..."+e.getMessage());
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                throw new ServiceException("关闭远程调用异常:"+e.getMessage());
            }
        }
        return respContent;
    }


    public static String sendGet(String url, String token) {
        String respContent = null;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("token", token);
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                respContent = EntityUtils.toString(httpResponse.getEntity(), "UTF-8").trim();
            }
        } catch (Exception e) {
            throw  new ServiceException("远程调用："+url+"异常.");
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                throw new ServiceException("关闭远程调用异常:"+e.getMessage());
            }
        }
        return respContent;
    }
}
