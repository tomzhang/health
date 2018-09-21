package com.dachen.feature.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.feature.FeatureEnum;
import com.dachen.feature.FeatureEnum.FeatureKindEnum;
import com.dachen.feature.FeatureEnum.FeatureObjectKindIdEnum;
import com.dachen.feature.entity.BaseFeature;
import com.dachen.feature.entity.FeatureAd;
import com.dachen.feature.entity.FeatureAdPreview;
import com.dachen.feature.entity.FeatureContent;
import com.dachen.feature.form.FeatureAdAddForm;
import com.dachen.feature.service.FeatureAdService;
import com.dachen.feature.vo.MobileFeatureAdVO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.document.constant.DocumentEnum;
import com.dachen.health.mq.RabbitMqProducer;
import com.dachen.qr.QRCodeUtil;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.component.FreemarkerComponent;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Model(FeatureAd.class)
@Service
public class FeatureAdServiceImpl extends BaseFeatureServiceImpl implements FeatureAdService {

    @Autowired
    RabbitMqProducer rabbitMqProducer;

    private static final String SHARE_URL = "%s?title=%s&link=%s&logo=%s&desc=%s";

    @Override
    public <T extends BaseFeature> T publish(Integer currentUserId, String appName, String id,int bannerType) {

        List<FeatureAd> featureAds = this.getPublished(appName,bannerType);
        if(featureAds.size() >= 5){
            throw new ServiceException("平台banner显示数量不能超过5个");
        }

        T featureAd = super.publish(currentUserId, appName, id,bannerType);

        List<FeatureAd> newFeatureAds = this.getPublished(appName,bannerType);

        this.sendPlatformBannerMsg(newFeatureAds);

        return featureAd;
    }

    /**
     * 向首页显示发送消息 平台banner
     * @param featureAds
     */
    private void sendPlatformBannerMsg(List<FeatureAd> featureAds){
        List<Map<String,Object>> messageList = new ArrayList<>();
        featureAds.forEach(featureAd -> {
            Map<String,Object> message = new HashMap<>(7);
            message.put("objectId",String.valueOf(featureAd.getId()));
            message.put("title",featureAd.getTitle());
            message.put("picUrl",featureAd.getPicUrl());
            message.put("httpUrl",featureAd.getHttpUrl());
            message.put("sort",featureAd.getWeight());
            message.put("objectKindId",featureAd.getObjectKindId());
            message.put("objectValue",featureAd.getObjectValue());
            messageList.add(message);
        });

        rabbitMqProducer.sendFanoutMessage(RabbitMqProducer.PLATFORM_BANNER_EXCHANGE,
                JSONObject.toJSONString(messageList));
    }

    /**
     * 获取已显示的banner
     * @param appName
     * @return
     */
    protected List<FeatureAd> getPublished(String appName,int bannerType) {
        Query<FeatureAd> query = this.createQuery();
        query.field("appName").equal(appName);
        query.field("bannerType").equal(bannerType);
        query.field("statusId").equal(FeatureEnum.FeatureStatusEnum.Published.getId());
        query.order("-weight");
        return query.asList();
    }

    @Override
    public <T extends BaseFeature> T cancelPublish(Integer currentUserId, String appName, String id,int bannerType) {
        List<FeatureAd> featureAds = this.getPublished(appName,bannerType);
        if(featureAds.size() <= 1){
            throw new ServiceException("平台banner显示数量不能少于1个");
        }
        T featureAd = super.cancelPublish(currentUserId, appName, id,bannerType);
        List<FeatureAd> newFeatureAds = this.getPublished(appName,bannerType);
        this.sendPlatformBannerMsg(newFeatureAds);
        return featureAd;
    }

    @Override
    public <T extends BaseFeature> List<T> exchange(String appName, String id1, String id2) {
        List<T> exchanges = super.exchange(appName,id1,id2);
        List<FeatureAd> featureAds = this.getPublished(appName,0);
        this.sendPlatformBannerMsg(featureAds);
        return exchanges;
    }

    @Override
    public FeatureAd add(Integer currentUserId, String appName, FeatureAdAddForm form) throws Exception {
        FeatureAd tmp = form.toAdAndCheck();
        tmp.setAppName(appName);
        if(Objects.equals(form.getBannerType(),1)){
            tmp.setStatusPublished(currentUserId);
        }else {
            tmp.setStatusPrepared(currentUserId);
        }
        tmp.setWeight(this.nextPrepareWeight(appName));
        String content = null;

        FeatureAd featureAd = this.saveEntityAndFind(tmp);

        if (FeatureEnum.FeatureObjectKindIdEnum.RichText == FeatureEnum.FeatureObjectKindIdEnum.eval(tmp.getObjectKindId())) {
            this.addStaticHtmlAsync(featureAd.getId().toString(),  form.getContent());
            featureAd.setContent( form.getContent());
        }else if(Objects.equals(featureAd.getBannerType(),1)){
            this.sendDeptBannerMsg(featureAd,"ADD",featureAd.getRange());
        }

        return featureAd;
    }

    @Override
    public FeatureAd update(Integer currentUserId, String appName, String id, FeatureAdAddForm form) throws Exception {
        FeatureAd dbItem = this.findNormalById(id);
        //科室banner更新时先发MQ消息全部删除
        if(Objects.equals(dbItem.getBannerType(),1)){
            this.sendDeptBannerMsg(dbItem,"REMOVE",dbItem.getRange());
        }
        if (StringUtils.isNotBlank(form.getTitle())) {
            dbItem.setTitle(form.getTitle());
        }
        if (StringUtils.isNotBlank(form.getPicUrl())) {
            dbItem.setPicUrl(form.getPicUrl());
        }
        if (null != form.getIfShowPicInText()) {
            dbItem.setIfShowPicInText(form.getIfShowPicInText());
        }
        if (null != form.getObjectKindId()) {
            dbItem.setObjectKindId(form.getObjectKindId());
        }
        if (StringUtils.isNotBlank(form.getObjectValue())) {
            dbItem.setObjectValue(form.getObjectValue());
        }
        if (StringUtils.isNotBlank(form.getObjectTitle())) {
            dbItem.setObjectTitle(form.getObjectTitle());
        }
        if (StringUtils.isNotBlank(form.getHttpUrl())) {
            dbItem.setHttpUrl(form.getHttpUrl());
        }
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem.setUpdateUserId(currentUserId);
        dbItem.setBannerType(form.getBannerType());
        dbItem.setRange(form.getRange());
        dbItem.setShareUrl("");
        dbItem = this.saveEntityAndFind(dbItem);

        if (FeatureEnum.FeatureObjectKindIdEnum.RichText == FeatureEnum.FeatureObjectKindIdEnum.eval(dbItem.getObjectKindId())) {
            this.updateStaticHtmlAsync(id, form.getContent());
            dbItem.setContent(form.getContent());
        }else {
            sendUpdateMessage(id);
        }
        return dbItem;
    }

    /**
     * 更新banner时，如果更新的是发布状态的banner则发送相应的mq消息
     * @param id
     */
    private void sendUpdateMessage(String id) {
        FeatureAd dbItem = this.findNormalById(id);
        if(Objects.equals(dbItem.getStatusId(), FeatureEnum.FeatureStatusEnum.Published.getId())){
            if(Objects.equals(dbItem.getBannerType(),0) ){
                List<FeatureAd> newFeatureAds = this.getPublished("health",dbItem.getBannerType());
                this.sendPlatformBannerMsg(newFeatureAds);
            }else if(Objects.equals(dbItem.getBannerType(),1)){
                this.sendDeptBannerMsg(dbItem,"REPLACE",dbItem.getRange());
            }else {
                throw new ServiceException("bannerType错误");
            }
        }
    }

    protected void addStaticHtmlAsync(String id, String content) throws Exception {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    addStaticHtml(id, content);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                FeatureAd featureAd = findById(id);
                if(Objects.equals(featureAd.getBannerType(),1)){
                    sendDeptBannerMsg(featureAd,"ADD",featureAd.getRange());
                }
            }
        });
    }

    protected void updateStaticHtmlAsync(String id, String content) throws Exception {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    updateStaticHtml(id, content);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                sendUpdateMessage(id);
            }
        });
    }

    @Override
    public void updateStaticHtmlAsync(String id) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    updateStaticHtml(id);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void reGenerateStaticAll() {
        String tag = "reGenerateStaticAll";
        int pageIndex = 0;
        int pageSize = 10;
        List<String> idList = this.findRichTextIdList(pageIndex, pageSize);
        while (SdkUtils.isNotEmpty(idList)) {
            logger.debug("{}. pageIndex={}, pageSize={}, idList={}", tag, pageIndex, pageSize, idList);
            for (String id:idList) {
                this.updateStaticHtmlAsync(id);
            }
            pageIndex++;
            idList = this.findRichTextIdList(pageIndex, pageSize);
        }
    }

    public List<String> findRichTextIdList(int pageIndex, int pageSize) {
        Query<FeatureAd> query = this.createQuery();
        query.field("objectKindId").equal(FeatureEnum.FeatureObjectKindIdEnum.RichText.getId());
        query.offset(pageIndex*pageSize).limit(pageSize);
        List<FeatureAd> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<String> idList = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList;
    }

    protected void addStaticHtml(String id, String content) throws Exception {
        String tag = "addStaticHtml";
        FeatureAd dbItem = this.findById(id);
        if (FeatureEnum.FeatureObjectKindIdEnum.RichText != FeatureEnum.FeatureObjectKindIdEnum.eval(dbItem.getObjectKindId())) {
            return;
        }

        String contentHash = featureContentService.hash(content);

        dbItem = uploadHtmlFile(dbItem, content, contentHash);

        featureContentService.add(dbItem, content, contentHash, dbItem.getHttpUrl());
    }

    private FeatureAd uploadHtmlFile(FeatureAd dbItem, String content, String contentHash) throws Exception {
        String tag = "uploadHtmlFile";
        String htmlContent = generateStaticHtml(dbItem, content);
        logger.info("{}. htmlContent={}", tag, htmlContent);
        String filePath = "feature";
//        String destFileName = filePath + File.separator + fileName;
        String destFileName = filePath + File.separator + dbItem.getId().toString() + File.separator + System.currentTimeMillis()  + ".html";
        logger.info("{}. destFileName={}", tag, destFileName);
        String key = QiniuUtil.upload(htmlContent.getBytes(),  destFileName, QiniuUtil.DEFAULT_BUCKET);
        logger.info("{}. key={}", tag, key);
        String htmlUrl =MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, key);
        logger.info("{}. htmlUrl={}", tag, htmlUrl);

        dbItem.setContentHash(contentHash);
        dbItem.setHttpUrl(htmlUrl);
        dbItem.setUpdateTime(System.currentTimeMillis());
        return this.saveEntityAndFind(dbItem);
    }

    protected void updateStaticHtml(String id, String content) throws Exception {
        String tag = "updateStaticHtml";
        FeatureAd dbItem = this.findById(id);
        if (FeatureEnum.FeatureObjectKindIdEnum.RichText != FeatureEnum.FeatureObjectKindIdEnum.eval(dbItem.getObjectKindId())) {
            return;
        }

        String contentHash = featureContentService.hash(content);

        // 不能使用下面的判断，因为还有title, picUrl和ifShowPicInText
//        if (null != dbItem.getContentHash() && dbItem.getContentHash().equals(contentHash)) {
//            return;
//        }

        dbItem = uploadHtmlFile(dbItem, content, contentHash);

        featureContentService.update(id, content, contentHash, dbItem.getHttpUrl(), dbItem.getAppName(), dbItem.getCreateUserId());
    }

    protected void updateStaticHtml(String id) throws Exception {
        String tag = "updateStaticHtml";
        logger.debug("{}. id={}", tag, id);

        FeatureAd dbItem = this.findById(id);
        if (FeatureEnum.FeatureObjectKindIdEnum.RichText != FeatureEnum.FeatureObjectKindIdEnum.eval(dbItem.getObjectKindId())) {
            return;
        }

        FeatureContent featureContent = featureContentService.findById(id);

        dbItem = uploadHtmlFile(dbItem, featureContent.getContent(), featureContent.getContentHash());

        featureContentService.update(id, featureContent.getContent(), featureContent.getContentHash(), dbItem.getHttpUrl(), dbItem.getAppName(), dbItem.getCreateUserId());
    }

    @Autowired
    protected FreemarkerComponent freemarkerComponent;

    protected String generateStaticHtml(FeatureAd featureAd, String content) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("title", featureAd.getTitle());
        params.put("publishTime", SdkUtils.formatDate_YYYY_MM_DD_HH_MM(featureAd.getCreateTime()));
        if (featureAd.getIfShowPicInText()) {
            params.put("picUrl", featureAd.getPicUrl());
        }
        params.put("content", content);
        String htmlContent = freemarkerComponent.render("/feature/ad-template.html", params);
        return htmlContent;
    }

    protected String generateStaticHtml(FeatureAdPreview preview, String content) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("title", preview.getTitle());
        params.put("publishTime", SdkUtils.formatDate_YYYY_MM_DD_HH_MM(preview.getCreateTime()));
        if (preview.getIfShowPicInText()) {
            params.put("picUrl", preview.getPicUrl());
        }
        params.put("content", content);
        String htmlContent = freemarkerComponent.render("/feature/ad-template.html", params);
        return htmlContent;
    }


    public MobileFeatureAdVO convertToMobile(FeatureAd featureAd) {
        if (null == featureAd) {
            return null;
        }
        MobileFeatureAdVO vo = new MobileFeatureAdVO(featureAd);
        return vo;
    }

    public List<MobileFeatureAdVO> convertToMobile(List<FeatureAd> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<MobileFeatureAdVO> ret = new ArrayList<>(list.size());
        for (FeatureAd fa:list) {
            ret.add(this.convertToMobile(fa));
        }
        return ret;
    }


    @Override
    public Pagination<FeatureAd> findPage(Integer currentUserId, String appName,
                                          String kw,Integer bannerType, String deptId,
                                          Integer pageIndex, Integer pageSize) {

        Pagination<FeatureAd> ret = this.findPage(appName, kw, bannerType, deptId, pageIndex, pageSize);
        //获取科室name
        Set<String> deptIdSet = new HashSet<>();
        ret.getPageData().forEach(featureAd -> {
            if(!CollectionUtils.isEmpty(featureAd.getRange())){
                for (String range : featureAd.getRange()) {
                    deptIdSet.add(range);
                }
            }
        });
        if(!CollectionUtils.isEmpty(deptIdSet)){
            this.fillDeptInfo(ret.getPageData(), deptIdSet);
        }
        return ret;
    }

    /**
     * 填充科室name字段
     * @param featureAds
     * @param deptIds
     */
    private void fillDeptInfo(List<FeatureAd> featureAds,Set<String> deptIds){
        Query<DeptVO> query = dsForRW.createQuery("b_hospitaldept",DeptVO.class);
        query.field("_id").in(deptIds);
        query.retrievedFields(true,"_id","name","isLeaf");
        query.filter("enableStatus", 1);
        List<DeptVO> depts = query.asList();
        Map<String,DeptVO> deptMap = depts.stream().collect(Collectors.toMap(DeptVO::getId,Function.identity()));
        featureAds.forEach(featureAd -> {
            if(Objects.nonNull(featureAd.getRange())) {
                List<DeptVO> deptsInfo = new ArrayList<>();
                for (String deptId : featureAd.getRange()) {
                    deptsInfo.add(deptMap.get(deptId));
                }
                featureAd.setDeptsInfo(deptsInfo);
            }
        });
    }

    @Override
    public List<MobileFeatureAdVO> findListAndVO(Integer currentUserId, String appName, Long ts) {
        List<FeatureAd> list = this.findList(appName, ts);
//        this.wrapAll(list);
        List<MobileFeatureAdVO> voList = this.convertToMobile(list);
        return voList;
    }

    protected void wrapAll(List<FeatureAd> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapContent(list);
    }

    protected void wrapContent(List<FeatureAd> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> idSet = new HashSet<>(list.size());
        for (FeatureAd featureAd:list) {
            if (FeatureEnum.FeatureObjectKindIdEnum.RichText == FeatureEnum.FeatureObjectKindIdEnum.eval(featureAd.getObjectKindId())) {
                idSet.add(featureAd.getId().toString());
            }
        }

        if (SdkUtils.isEmpty(idSet)) {
            return;
        }

        List<FeatureContent> featureContentList = featureContentService.findByIds(new ArrayList<>(idSet));

        for (FeatureAd featureAd:list) {
            for (FeatureContent featureContent:featureContentList) {
                if (featureAd.getId().toString().equals(featureContent.getFeatureId().toString())) {
                    featureAd.setObjectValue(featureContent.getContent());
                    break;
                }
            }
        }
    }

    @Override
    public FeatureAd findById(Integer currentUserId, String appName, String id) {
        FeatureAd dbItem = this.findById(id);
        if (FeatureEnum.FeatureObjectKindIdEnum.RichText == FeatureEnum.FeatureObjectKindIdEnum.eval(dbItem.getObjectKindId())) {
            String content = featureContentService.findContentById(id);
            dbItem.setContent(content);
        }
        //增加科室信息
        if(!CollectionUtils.isEmpty(dbItem.getRange())) {
            List<FeatureAd> featureAds = new ArrayList<>();
            featureAds.add(dbItem);
            Set<String> deptIds = new HashSet<>();
            for (String deptId : dbItem.getRange()) {
                deptIds.add(deptId);
            }
            this.fillDeptInfo(featureAds, deptIds);
        }
        return dbItem;
    }

    @Override
    public MobileFeatureAdVO viewItemAndVO(Integer currentUserId, String appName, String id) {
        FeatureAd dbItem = this.findById(id);
        dbItem.setTotalView(dbItem.getTotalView() + 1);
        dbItem = this.saveEntityAndFind(dbItem);

        MobileFeatureAdVO vo = this.convertToMobile(dbItem);
        return vo;
    }

    @Override
    public MobileFeatureAdVO viewItemAndVOInfo(Integer currentUserId, String appName, String id) {
        FeatureAd dbItem = this.findById(id);
        dbItem.setTotalView(dbItem.getTotalView() + 1);
        dbItem = this.saveEntityAndFind(dbItem);

        MobileFeatureAdVO vo = this.convertToMobile(dbItem);
        String documentUrl = null;
        if(StringUtils.isNotEmpty(dbItem.getShareUrl())) {
            documentUrl=dbItem.getShareUrl();
        }else{
            // 生成静态文件
            JSONObject jsonObject = buildMaterialShareParam(vo);
            documentUrl = createShareMaterialFile(jsonObject);

            Map<String, Object> updateFieldMap=new HashMap<>();
            updateFieldMap.put("shareUrl", documentUrl);
            update(id,updateFieldMap);
        }
        // 将静态文件嵌套iframe
        vo.setShareUrl(embedIframe(documentUrl, vo.getTitle(), vo.getPicUrl(), ""));
        return vo;
    }

    @Override
    public FeatureKindEnum getKindId() {
        FeatureKindEnum kind = FeatureKindEnum.Advertisement;
        return kind;
    }

    @Override
    public Map<String, String> preview(Integer currentUserId, String appName, FeatureAdAddForm form) {

        if (!Objects.equals(FeatureObjectKindIdEnum.RichText.getId(), form.getObjectKindId())) {
            throw new ServiceException("该类型不支持预览");
        }

        FeatureAd tmp = form.toAdAndCheck();
        tmp.setAppName(appName);
        tmp.setStatusPrepared(currentUserId);
        tmp.setWeight(0);
        FeatureAdPreview preview = new FeatureAdPreview();
        BeanUtils.copyProperties(tmp, preview);

        String content = form.getContent();

        if (StringUtils.isEmpty(content)) {
            throw new ServiceException("文本内容不能为空");
        }

        try {
            content = generateStaticHtml(preview, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String contentKey = QiniuUtil.upload(content.getBytes("utf-8"), StringUtil.randomString(24) + ".html", QiniuUtil.DEFAULT_BUCKET);
            String htmlPath =MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, contentKey);
            preview.setHttpUrl(htmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dsForRW.save(preview);

        Map<String, String> result = Maps.newHashMap();

        try {
            String key = QiniuUtil.upload(QRCodeUtil.encode("dachen://H5View?type=3&id=" + preview.getId()), StringUtil.randomString(32), QiniuUtil.DEFAULT_BUCKET);
            String path =MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, key);
            result.put("url", path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public FeatureAdPreview previewDetail(String id) {
        Query<FeatureAdPreview> query = dsForRW.createQuery(FeatureAdPreview.class);
        FeatureAdPreview featureAdPreview = query.field("_id").equal(new ObjectId(id)).get();
        if (Objects.isNull(featureAdPreview)) {
            throw new ServiceException("找不到该预览内容");
        }

        return featureAdPreview;
    }

    @Override
    public List<DeptVO> ifDeptHasBanner(List<String> deptIds) {
        Query<FeatureAd> query = dsForRW.createQuery(FeatureAd.class);
        query.field("appName").equal("health");
        query.field("range").hasAnyOf(deptIds);
        query.field("statusId").equal(FeatureEnum.FeatureStatusEnum.Published.getId());
        List<FeatureAd> featureAds = query.asList();

        List<String> deptIdExist = new ArrayList<>();
        featureAds.forEach(featureAd -> {
            for(String deptId : featureAd.getRange()){
                if(deptIds.contains(deptId)){
                    deptIdExist.add(deptId);
                }
            }
        });
        List<DeptVO> depts = null;
        if(!CollectionUtils.isEmpty(deptIdExist)) {
            depts = dsForRW.createQuery("b_hospitaldept", DeptVO.class)
                    .field("_id").in(deptIdExist)
                    .retrievedFields(true, "_id", "name", "isLeaf")
                    .filter("enableStatus", 1)
                    .asList();
        }
        return depts;
    }

    @Override
    public Pagination<FeatureAd> addDeptDisplay(Integer currentUserId, String appName, String kw,
                                            String deptId, Integer pageIndex, Integer pageSize) {

        Query<FeatureAd> query =  this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
        query.field("bannerType").equal(1);
        query.field("statusId").notEqual(FeatureEnum.FeatureStatusEnum.Deleted.getId());
        if(StringUtils.isNotBlank(kw)){
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("title").equal(pattern);
        }
        if(StringUtils.isNotBlank(deptId)){
            Set<String> deptIds = new HashSet<>();
            deptIds.add(deptId);
            query.field("range").hasNoneOf(deptIds);
        }
        query.order("-weight, -createTime");
        query.offset(pageIndex*pageSize).limit(pageSize);

        Pagination<FeatureAd> pagination = new Pagination<>(query.asList(), query.countAll(), pageIndex, pageSize);
        return pagination;
    }

    @Override
    public <T extends BaseFeature> T publishDeptBanner(Integer currentUserId, String appName, String id, String deptId) {
        FeatureAd dbItem = this.findNormalById(id);
        List<String> range = dbItem.getRange();
        if(CollectionUtils.isEmpty(range)){
            range = new ArrayList<>();
        }
        range.add(deptId);
        dbItem.setRange(range);
        dbItem.setStatusPublished(currentUserId);
        T featureAd = this.saveEntityAndFind((T)dbItem);

        List<String> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        this.sendDeptBannerMsg((FeatureAd)featureAd,"ADD",deptIds);

        return featureAd;
    }

    @Override
    public <T extends BaseFeature> T cancelDeptBanner(Integer currentUserId, String appName, String id, String deptId) {
        FeatureAd dbItem = this.findNormalById(id);
        List<String> range = dbItem.getRange();
        range.remove(deptId);
        dbItem.setRange(range);
        dbItem.setStatusPublished(currentUserId);
        T featureAd = this.saveEntityAndFind((T)dbItem);

        List<String> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        this.sendDeptBannerMsg((FeatureAd)featureAd,"REMOVE",deptIds);
        return featureAd;
    }

    @Override
    public <T extends BaseFeature> T replaceDeptBanner(Integer currentUserId, String appName,
                                                       String oldId,String newId, String deptId) {
        this.cancelDeptBanner(currentUserId,appName,oldId,deptId);
        T featureAd = this.publishDeptBanner(currentUserId,appName,newId,deptId);
        List<String> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        this.sendDeptBannerMsg((FeatureAd)featureAd,"REPLACE",deptIds);
        return featureAd;
    }

    /**
     * 科室banner更改 发送mq消息
     * @param featureAd
     * @param opsType
     * @param deptIds
     */
    private void sendDeptBannerMsg(FeatureAd featureAd,String opsType,List<String> deptIds){
        Map<String,Object> message = new HashMap<>(3);
        message.put("opsType",opsType);
        message.put("deptIds",deptIds);

        Map<String,Object> banner = new HashMap<>(6);
        banner.put("objectId",String.valueOf(featureAd.getId()));
        banner.put("title",featureAd.getTitle());
        banner.put("picUrl",featureAd.getPicUrl());
        banner.put("httpUrl",featureAd.getHttpUrl());
        banner.put("objectKindId",featureAd.getObjectKindId());
        banner.put("objectValue",featureAd.getObjectValue());
        message.put("banner",banner);

        rabbitMqProducer.sendFanoutMessage(RabbitMqProducer.DEPT_BANNER_EXCHANGE,
                JSONObject.toJSONString(message));
    }


    /**
     * 微信二次分享需要，嵌入一层iframe
     */
    private String embedIframe(String url, String title, String icon, String desc) {
        // 前端要求，link地址以双斜杠开头
        if (url.startsWith("http")) {
            String[] temp = url.split(":");
            url = temp[1];
        }
        return String.format(SHARE_URL, PropertiesUtil.getContextProperty("wechatShare"), encode(title), encode(url), encode(icon), encode(desc));
    }

    public String createShareMaterialFile(JSONObject adMaterialDetailVO) {
        String fileKey = com.dachen.util.StringUtils.randomUUID();
        String key = null;
        BufferedReader bufferedReader = null;
        String H5_SHARE_MATERIAL_FILE = "/template/materialshareIframe.html";
        try {
            ClassPathResource classPathResource = new ClassPathResource(H5_SHARE_MATERIAL_FILE);
            Assert.isTrue(classPathResource.exists(), "分享的模板文件不存在");
            bufferedReader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(), Charset.forName("UTF-8")));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            String content = builder.toString().replace("${iframeUrl}", adMaterialDetailVO.getString("httpUrl"));
            key = QiniuUtil.upload(content.getBytes(),  fileKey, QiniuUtil.DEFAULT_BUCKET);
        } catch (IOException e) {
            logger.error("创建分享文件失败，{}", e);
            throw new ServiceException("分享文件生成失败，请联系管理员。");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, key);
    }

    private JSONObject buildMaterialShareParam(MobileFeatureAdVO mobileFeatureAdVO) {
        JSONObject jsonObject=JSONObject.parseObject(JSON.toJSONString(mobileFeatureAdVO));
        StringBuilder builder=new StringBuilder();
        builder.append("http://");
        builder.append(PropertiesUtil.getContextProperty("file.host"));
        builder.append(PropertiesUtil.getContextProperty("circle.download.url"));
        //下载app的链接
        jsonObject.put("downloadURL", builder.toString());
        Date date = mobileFeatureAdVO.getPublishTime()==null?new Date():new Date(mobileFeatureAdVO.getPublishTime());
        jsonObject.put("createTime", DateUtils.formatDate(date,"yyyy-MM-dd HH:mm"));
        jsonObject.put("shareType", "2000");
        jsonObject.put("content", encode(mobileFeatureAdVO.getContent()));
        jsonObject.put("httpUrl",mobileFeatureAdVO.getHttpUrl());
        return jsonObject;
    }

    private String encode(String content) {
        try {
            if(!org.springframework.util.StringUtils.hasText(content)){
                return StringUtils.EMPTY;
            }
            String encodeStr = URLEncoder.encode(content, "utf-8");
            // 对空格特殊处理
            encodeStr = encodeStr.replaceAll("\\+", "%20");
            return encodeStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看广告文章是否被引用
     * @param value
     * @return
     */
    @Override
    public FeatureAd getByObjectValue(String value,String appName){
        Query<FeatureAd> query = dsForRW.createQuery(FeatureAd.class);
        query.field("appName").equal(appName);
        query.field("objectValue").equal(value);
        return query.get();
    }

}
