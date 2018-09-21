package com.dachen.feature.mobile.controller;

import com.dachen.feature.FeatureEnum;
import com.dachen.feature.entity.FeatureAd;
import com.dachen.feature.service.FeatureAdService;
import com.dachen.feature.vo.MobileFeatureAdVO;
import com.dachen.sdk.json.JSONMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/m/feature/ad")
public class MobileFeatureAdController extends MobileBaseController {

    @Autowired
    protected FeatureAdService featureAdService;

    protected static final String appName = "health";

    /**
     * @api {GET} /m/feature/ad/findList 获取广告列表
     * @apiVersion 1.0.0
     * @apiName FeatureAd.findList
     * @apiGroup 广告
     * @apiDescription 获取广告列表
     *
     * @apiParam {String}     access_token          token
     * @apiParam {Long}     ts          updateTime时间戳
     *
     * @apiSuccess {Object[]}  list        广告数据列表
     * @apiSuccess {String}  list.id        广告id
     * @apiSuccess {String}  list.title        广告title
     * @apiSuccess {String}  list.picUrl        广告picUrl
     * @apiSuccess {Integer}  list.objectKindId        objectKindId，1表示跳转链接，2表示富文本，3表示推荐
     * @apiSuccess {String}  list.objectValue        objectValue，当objectKindId为3时，存放推荐id
     * @apiSuccess {String}  list.objectTitle        objectTitle，当是推荐id时，冗余推荐的title
     * @apiSuccess {String}  list.httpUrl        当objectKindId=1或2时时，存放跳转链接
     * @apiSuccess {Long}  list.publishTime        广告发布时间
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/findList")
    public JSONMessage findList(@RequestParam(defaultValue = "0") Long ts) {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileFeatureAdVO> ret = featureAdService.findListAndVO(currentUserId, appName, ts);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/feature/ad/getShareParam 获取分享参数
     * @apiVersion 1.0.0
     * @apiName FeatureAd.getShareParam
     * @apiGroup 广告
     * @apiDescription 获取分享参数
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     adId          广告id
     * @apiSuccess {String}     adId          广告id
     * @apiSuccess {String}     title          标题
     * @apiSuccess {String}     picUrl          题图
     * @apiSuccess {String}     desc          描述
     * @apiSuccess {String}     shareUrl          分享链接
     *
     * @apiAuthor 肖伟
     * @date 2017年06月12日
     */
    @RequestMapping("/getShareParam")
    public JSONMessage getShareParam(@RequestParam("adId") String id, HttpServletResponse response) throws IOException {
        Integer currentUserId = this.getCurrentUserId();
        MobileFeatureAdVO ret = featureAdService.viewItemAndVOInfo(currentUserId, appName, id);
        Map<String, Object> map = new HashMap<>(4);
        map.put("adId", ret.getId());
        map.put("title", ret.getTitle());
        map.put("picUrl", ret.getPicUrl());
        map.put("desc", "");
        map.put("shareUrl", ret.getShareUrl());
        return JSONMessage.success(null, map);
    }

    /**
     * @api {POST} /m/feature/ad/toView 跳转到广告详情页
     * @apiVersion 1.0.0
     * @apiName FeatureAd.toView
     * @apiGroup 广告
     * @apiDescription 跳转到广告详情页
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     adId          广告id
     *
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/toView")
    public void toView(@RequestParam("adId") String id, HttpServletResponse response) throws IOException {
        Integer currentUserId = this.getCurrentUserId();
        MobileFeatureAdVO ret = featureAdService.viewItemAndVO(currentUserId, appName, id);
        switch (FeatureEnum.FeatureObjectKindIdEnum.eval(ret.getObjectKindId())) {
            case HttpUrl:
                response.sendRedirect(ret.getHttpUrl());
                break;
            case RichText:
                response.sendRedirect(ret.getHttpUrl());
                break;
            case RecArticle:
                break;
        }
    }

}
