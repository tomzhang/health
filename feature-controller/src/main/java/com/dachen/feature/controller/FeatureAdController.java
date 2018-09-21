package com.dachen.feature.controller;

import com.dachen.commons.exception.ServiceException;
import com.dachen.feature.entity.FeatureAd;
import com.dachen.feature.form.FeatureAdAddForm;
import com.dachen.feature.service.FeatureAdService;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.ReqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/feature/ad")
public class FeatureAdController extends FeatureBaseController {

    @Autowired
    protected FeatureAdService featureAdService;
    @Autowired
    private OperationLogMqProducer operationLogMqProducer;

    protected static final String appName = "health";

    /**
     * @api {GET} /feature/ad/findPage 获取广告列表
     * @apiVersion 1.0.0
     * @apiName FeatureAd.findPage
     * @apiGroup 广告
     * @apiDescription 已发布的两个广告位置互移
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     kw                    关键字
     * @apiParam {Integer}     pageIndex             pageIndex
     * @apiParam {Integer}     pageSize              pageSize
     *
     * @apiSuccess {Object}  page        一页广告数据
     * @apiSuccess {Integer}  total        总记录数
     * @apiSuccess {Integer}  pageIndex        pageIndex
     * @apiSuccess {Integer}  pageSize        pageIndex
     * @apiSuccess {Object[]}  page.pageData        广告数据列表
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/findPage")
    public JSONMessage findPage(@RequestParam(required = false) String kw,
                                @RequestParam(required = false) Integer bannerType,
                                @RequestParam(required = false) String deptId,
                                @RequestParam(defaultValue = "0") Integer pageIndex,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<FeatureAd> ret = featureAdService.findPage(currentUserId, appName,
                                                kw,bannerType,deptId,pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /feature/ad/exchange 已发布的两个广告位置互移
     * @apiVersion 1.0.0
     * @apiName FeatureAd.exchange
     * @apiGroup 广告
     * @apiDescription 已发布的两个广告位置互移
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id1                 id1
     * @apiParam {String}     id2              id2
     *
     * @apiSuccess {Object[]}  FeatureAdList 两个广告
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("exchange")
    public JSONMessage exchange(@RequestParam String id1, @RequestParam String id2) {
        Integer currentUserId = this.getCurrentUserId();
        List<FeatureAd> ret = featureAdService.exchange(appName, id1, id2);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /feature/ad/publish 发布广告（显示广告）
     * @apiVersion 1.0.0
     * @apiName FeatureAd.publish
     * @apiGroup 广告
     * @apiDescription 发布广告（显示广告）
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                 id
     *
     * @apiSuccess {Object}  featureAd          广告详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/publish")
    public JSONMessage publish(@RequestParam String id,@RequestParam int bannerType) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.publish(currentUserId, appName, id,bannerType);

        //记录操作日志
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
            OperationLogTypeDesc.RECOMMENDATION, String.format("设置平台Banner显示（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {get} /feature/ad/cancelPublish 取消发布（取消显示）
     * @apiVersion 1.0.0
     * @apiName FeatureAd.cancelPublish
     * @apiGroup 广告
     * @apiDescription 取消发布（取消显示）
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                 id
     *
     * @apiSuccess {Object}  featureAd          广告详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/cancelPublish")
    public JSONMessage cancelPublish(@RequestParam String id,@RequestParam int bannerType) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.cancelPublish(currentUserId, appName, id,bannerType);
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
            OperationLogTypeDesc.RECOMMENDATION, String.format("取消Banner显示（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /feature/ad/add 新建广告
     * @apiVersion 1.0.0
     * @apiName FeatureAd.add
     * @apiGroup 广告
     * @apiDescription 新建广告
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     title                 广告标题
     * @apiParam {Boolean}  ifShowPicInText        是否在正文显示图片
     * @apiParam {String}  objectKindId        objectKindId，1表示跳转链接，2表示富文本，3表示推荐
     * @apiParam {String}  objectValue        objectValue，当objectKindId为3时传推荐id
     * @apiParam {String}  objectTitle        objectTitle，当是推荐id时，冗余推荐的title
     * @apiParam {String}  httpUrl        当objectKindId为1时，传跳转链接
     * @apiParam {String}  content        当objectKindId为2时，传富文本内容
     *
     *
     * @apiSuccess {Object}  FeatureAd      广告详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/add")
    public JSONMessage add(@Valid FeatureAdAddForm form) throws Exception {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.add(currentUserId, appName, form);
        return JSONMessage.success(null, ret);
    }

    /**
     * 检查当前科室是否已有banner
     * @param deptIds
     * @return
     */
    @RequestMapping("/ifDeptHasBanner")
    public JSONMessage ifDeptHasBanner(@RequestParam List<String> deptIds){
        if(CollectionUtils.isEmpty(deptIds)){
            throw new ServiceException("科室ID为空");
        }
        return JSONMessage.success(null,featureAdService.ifDeptHasBanner(deptIds));
    }


    /**
     * @api {POST} /feature/ad/delete 删除广告
     * @apiVersion 1.0.0
     * @apiName FeatureAd.delete
     * @apiGroup 广告
     * @apiDescription 删除广告
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                 广告id
     *
     * @apiSuccess {Object}  FeatureAd      删除的广告详情
     *
     * @apiAuthor 李敏
     * @date 2017年6月6日11:07:54
     */
    @RequestMapping("/delete")
    public JSONMessage delete(String id) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.deletePublish(id,currentUserId);
        return JSONMessage.success(null, ret);
    }
    /**
     * @api {POST} /feature/ad/update 更新广告
     * @apiVersion 1.0.0
     * @apiName FeatureAd.update
     * @apiGroup 广告
     * @apiDescription 更新广告，参数格式与add接口一致
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id          id
     *
     * @apiSuccess {Object}  FeatureAd      广告详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("update")
    public JSONMessage update(@RequestParam String id,@Valid FeatureAdAddForm form) throws Exception {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.update(currentUserId, appName, id, form);
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
            OperationLogTypeDesc.RECOMMENDATION, String.format("编辑Banner（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {get} /feature/ad/findById 查询广告详情
     * @apiVersion 1.0.0
     * @apiName FeatureAdFindById
     * @apiGroup 广告
     * @apiDescription 创建广告
     *
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id                 id
     *
     * @apiSuccess {Object}  FeatureAd      广告详情
     * @apiSuccess {String}  FeatureAd.id        广告id
     * @apiSuccess {String}  FeatureAd.title        广告title
     * @apiSuccess {String}  FeatureAd.picUrl        广告图片
     * @apiSuccess {Boolean}  FeatureAd.ifShowPicInText        是否在正文显示图片
     * @apiSuccess {String}  FeatureAd.objectKindId        objectKindId，1表示跳转链接，2表示富文本，3表示推荐
     * @apiSuccess {String}  FeatureAd.objectValue        objectValue，当objectKindId为3时，存推荐id
     * @apiSuccess {String}  FeatureAd.objectTitle        objectTitle，当是推荐id时，冗余推荐的title
     * @apiSuccess {String}  FeatureAd.httpUrl        当objectKindId为1时，放在跳转链接
     * @apiSuccess {String}  FeatureAd.content        当objectKindId为2时，放在富文本内容
     * @apiSuccess {Integer}  FeatureAd.statusId        广告状态，0就绪，2发布，9删除
     * @apiSuccess {Long}  FeatureAd.createTime        广告创建时间
     * @apiSuccess {Long}  FeatureAd.publishTime        广告发布时间
     * @apiSuccess {Integer}  FeatureAd.totalView        广告查看次数
     * @apiSuccess {Integer}  FeatureAd.weight        广告权重weight，按weight倒序、createTime倒序排列
     *
     *
     * @apiAuthor 肖伟
     * @date 2017年05月04日
     */
    @RequestMapping("/findById")
    public JSONMessage findById(@RequestParam String id) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.findById(currentUserId, appName, id);
        return JSONMessage.success(null, ret);
    }


    @RequestMapping("/reGenerateStatic")
    public JSONMessage reGenerateStatic(@RequestParam("adId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        featureAdService.updateStaticHtmlAsync(id);
        return JSONMessage.success();
    }

    @RequestMapping("/reGenerateStaticAll")
    public JSONMessage reGenerateStaticAll(@RequestParam("adId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        featureAdService.reGenerateStaticAll();
        return JSONMessage.success();
    }

    @RequestMapping("/preview")
    public JSONMessage preview(FeatureAdAddForm form) {
        return JSONMessage.success(null, featureAdService.preview(this.getCurrentUserId(), appName, form));
    }

    @RequestMapping("/preview/{id}")
    public JSONMessage previewContent(@PathVariable(value = "id") String id) {
        return JSONMessage.success(null, featureAdService.previewDetail(id));
    }

    /**
     * 科室banner 添加显示
     * @param kw
     * @param deptId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @RequestMapping("/addDeptDisplay")
    public JSONMessage addDisplay(@RequestParam(required = false) String kw,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(defaultValue = "0") Integer pageIndex,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<FeatureAd> ret = featureAdService.addDeptDisplay(currentUserId, appName,kw,deptId ,pageIndex,pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * 发布科室banner
     * @param id
     * @param deptId
     * @return
     */
    @RequestMapping("/publishDeptBanner")
    public JSONMessage publishDeptBanner(@RequestParam String id,@RequestParam String deptId) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.publishDeptBanner(currentUserId, appName, id,deptId);
        //记录操作日志
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.RECOMMENDATION, String.format("设置科室Banner显示（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    @RequestMapping("/cancelDeptBanner")
    public JSONMessage cancelDeptBanner(@RequestParam String id,@RequestParam String deptId) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.cancelDeptBanner(currentUserId, appName, id,deptId);
        //记录操作日志
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.RECOMMENDATION, String.format("取消科室Banner显示（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    @RequestMapping("/replaceDeptBanner")
    public JSONMessage replaceDeptBanner(@RequestParam String oldId,@RequestParam String newId,@RequestParam String deptId) {
        Integer currentUserId = this.getCurrentUserId();
        FeatureAd ret = featureAdService.replaceDeptBanner(currentUserId, appName, oldId,newId,deptId);
        //记录操作日志
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.RECOMMENDATION, String.format("替换科室Banner显示（%s）", ret.getTitle()));
        return JSONMessage.success(null, ret);
    }

    @RequestMapping("/checkIfAdReferred")
    public JSONMessage checkIfAdReferred(@RequestParam String materialId) {
        Map<String,Object> map = new HashMap<>();
        FeatureAd ret = featureAdService.getByObjectValue(materialId,appName);
        map.put("exist",!Objects.isNull(ret));
        return JSONMessage.success(null, map);
    }

}
