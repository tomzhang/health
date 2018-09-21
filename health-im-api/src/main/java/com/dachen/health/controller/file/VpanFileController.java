package com.dachen.health.controller.file;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.file.constant.VpanFileEnum.VpanFileMode;
import com.dachen.health.file.entity.param.VpanFileParam;
import com.dachen.health.file.entity.po.VpanUploadFile;
import com.dachen.health.file.entity.vo.LatelyVpanFileVo;
import com.dachen.health.file.service.IVpanFileService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vpanfile")
public class VpanFileController {

    @Autowired
    private IVpanFileService vpanFileService;


    /**
     * @api {post} /vpanfile/getUploadToken  获取文件上传token
     * @apiVersion 1.0.0
     * @apiName getUploadToken
     * @apiGroup 文件管理
     * @apiDescription 获取文件上传token
     * @apiParam {String}     access_token          token
     * @apiParam {String}     bucket             空间名（允许为空，默认值为 vpan）
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {String}   data.token            文件上传token   （有效期24小时）
     * @apiSuccess {String}   data.domain          空间对应的域名
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("getUploadToken")
    public JSONMessage getUploadToken(String bucket) {
        //登录用户id
//		Integer userId= ReqUtil.instance.getUserId();
        //以后可以校验 用户是否有上传文件的权限

        String defaultBucket = PropertiesUtil.getContextProperty("qiniu.vpan.bucket");
        if (bucket == null || StringUtils.isEmpty(bucket)) {
            bucket = defaultBucket;
        }

        //返回结果
        Map<String, Object> map = new HashMap<String, Object>();


        //读取七牛的上传token
        String token = MsgHelper.getUploadToken(bucket);
        if (token != null) {
            map.put("token", token);
        } else {
            throw new ServiceException("读取uploadToken失败");
        }

        //读取域名
        String defaultDomain = vpanFileService.getVpanDomain();
        String domain = "";
        if (defaultBucket.equals(bucket)) {
            domain = defaultDomain;
        } else {
            domain = defaultDomain.replaceFirst(defaultBucket, bucket);
        }
        map.put("domain", domain);


        return JSONMessage.success(null, map);

    }

    /**
     * @api {post} /vpanfile/saveFileInfo  保存文件元信息
     * @apiVersion 1.0.0
     * @apiName saveFileInfo
     * @apiGroup 文件管理
     * @apiDescription 文件上传到七牛后，获取七牛返回的文件元信息并保存到本地业务系统中
     * @apiParam {String}     access_token          token
     * @apiParam {String}     name          文件名（必须包括后缀）
     * @apiParam {String}     mimeType          mime类型
     * @apiParam {Long}      size          文件名大小
     * @apiParam {String}     key          文件key值，七牛返回
     * @apiParam {String}     hash         文件hash值，七牛返回
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("saveFileInfo")
    public JSONMessage saveFileInfo(String name, String mimeType, String hash, String key, Long size) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        String id = vpanFileService.saveFileInfo(userId, name, mimeType, hash, key, size);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);

        return JSONMessage.success(null, map);
    }

    /**
     * @api {post} /vpanfile/saveMessageFileInfo  保存IM文件元信息
     * @apiVersion 1.0.0
     * @apiName saveMessageFileInfo
     * @apiGroup 文件管理
     * @apiDescription 文件上传到七牛后，获取七牛返回的文件元信息并保存到本地业务系统中
     * @apiParam {String}     access_token          token
     * @apiParam {String}     name          文件名（必须包括后缀）
     * @apiParam {String}     mimeType          mime类型
     * @apiParam {Long}      size          文件名大小
     * @apiParam {String}     key          文件key值，七牛返回
     * @apiParam {String}     hash         文件hash值，七牛返回
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 傅永德
     * @date 2017年3月28日
     */
    @RequestMapping("saveMessageFileInfo")
    public JSONMessage saveMessageFileInfo(String name, String mimeType, String hash, String key, Long size) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        String id = vpanFileService.saveMassageFileInfo(userId, name, mimeType, hash, key, size);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);

        return JSONMessage.success(null, map);
    }

    /**
     * @api {post} /vpanfile/updateFileName  修改文件名
     * @apiVersion 1.0.0
     * @apiName updateFileName
     * @apiGroup 文件管理
     * @apiDescription 修改文件名
     * @apiParam {String}     access_token          token
     * @apiParam {String}     newName          新文件名（必须包括后缀）
     * @apiParam {String}     newMimeType          mime类型
     * @apiParam {String}     id         	文件id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("updateFileName")
    public JSONMessage updateFileName(String id, String newName, String newMimeType) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        vpanFileService.updateFileName(id, userId, newName, newMimeType);

        return JSONMessage.success(null);
    }

    /**
     * @api {post} /vpanfile/deleteUploadFile  删除我的文件(包含我上传的和我接收的)
     * @apiVersion 1.0.1
     * @apiName deleteUploadFile
     * @apiGroup 文件管理
     * @apiDescription 删除我上传的文件
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id         	文件id
     * @apiParam {String[]}     ids         	多个文件id数组  (id和ids至少有一个不为空)
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("deleteUploadFile")
    public JSONMessage deleteUploadFile(String id, String[] ids) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();
        //删除ids数组对应的文件
        if (ids != null && ids.length > 0) {
            vpanFileService.deleteMultiVpanUploadFile(ids, userId);
            //vpanFileService.deleteMultiVpanSendFile(ids, userId);
        }
        //删除id对应的文件
        if (id != null && !StringUtils.isEmpty(id)) {
            vpanFileService.deleteVpanUploadFile(id, userId);
            vpanFileService.deleteVpanSendFile(id, userId);
        }

        return JSONMessage.success(null);
    }

    /**
     * @api {post} /vpanfile/deleteSendFile  删除我的文件(包含我上传的和我接收的)
     * @apiVersion 1.0.0
     * @apiName deleteSendFile
     * @apiGroup 文件管理
     * @apiDescription 删除我接收的文件
     * @apiParam {String}     access_token          token
     * @apiParam {String}     id         	文件发送记录id
     * @apiParam {String[]}     ids         	多个文件id数组  (id和ids至少有一个不为空)
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 王峭
     * @date 2016年1月14日
     */
    @RequestMapping("deleteSendFile")
    public JSONMessage deleteSendFile(String id, String[] ids) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        //删除ids数组对应的文件
        if (ids != null && ids.length > 0) {
            vpanFileService.deleteMultiVpanUploadFile(ids, userId);
            vpanFileService.deleteMultiVpanSendFile(ids, userId);
        }
        //删除id对应的文件
        if (id != null && !StringUtils.isEmpty(id)) {
            vpanFileService.deleteVpanUploadFile(id, userId);
            vpanFileService.deleteVpanSendFile(id, userId);
        }

        return JSONMessage.success(null);
    }

    /**
     * @api {post} /vpanfile/searchFile  搜索文件
     * @apiVersion 1.0.0
     * @apiName searchFile
     * @apiGroup 文件管理
     * @apiDescription 搜索文件（包括我上传的文件，我接收的文件）
     * @apiParam {String}     access_token          token
     * @apiParam {String}     mode         	搜索模式  upload=我上传的文件    receive=我接收的文件
     * @apiParam {String}     fileNameKey         	搜索关键字（可以为空）
     * @apiParam {String}     type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     * @apiParam {String}     sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     * @apiParam {Integer}     sortType         	排序顺序  1=顺序（默认），-1=倒序
     * @apiParam {Integer}     pageIndex     读取第几页数据
     * @apiParam {Integer}     pageSize       每页读取多少条数据
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {List}         pageData                分页数据
     * @apiSuccess {Integer}      pageIndex            页码
     * @apiSuccess {Integer}      pageSize             页面大小
     * @apiSuccess {Long}         total                总量
     * @apiSuccess {String}   fileId        文件记录id
     * @apiSuccess {String}   name        文件名
     * @apiSuccess {String}   suffix        文件后缀
     * @apiSuccess {String}   mimeType        mime类型
     * @apiSuccess {Long}   size        文件大小
     * @apiSuccess {String}   url        文件下载地址
     * @apiSuccess {String}   type        文件分类
     * @apiSuccess {Long}   uploadDate        文件上传时间
     * @apiSuccess {String}   fileSendId        文件发送记录id
     * @apiSuccess {Integer}   sendUserId        文件发送人id
     * @apiSuccess {Long}   sendDate        文件发送时间
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("searchFile")
    public JSONMessage searchFile(VpanFileParam param) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();
        PageVO ret;
        if (VpanFileMode.upload.getValue().equals(param.getMode())) {
            //查询我上传的文件
            param.setUploader(userId);
            ret = vpanFileService.searchUploadFile(param);
        } else if (VpanFileMode.receive.getValue().equals(param.getMode())) {
            //查询我接收的文件
            param.setReceiveUserId(userId);
            ret = vpanFileService.searchSendFile(param);
        } else {
            throw new ServiceException("参数mode为空");
        }

        return JSONMessage.success(null, ret);
    }


    /**
     * @api {post/get} /vpanfile/queryFile  搜索文件
     * @apiVersion 1.0.0
     * @apiName queryFile
     * @apiGroup 文件管理
     * @apiDescription 搜索文件（包括我上传的文件，我接收的文件）
     * @apiParam {String}     access_token          token
     * @apiParam {Integer}    userId                用户userId
     * @apiParam {String}     fileNameKey         	搜索关键字（可以为空）
     * @apiParam {String}     type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     * @apiParam {String}     sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     * @apiParam {Integer}     sortType         	排序顺序  1=顺序（默认），-1=倒序
     * @apiParam {Integer}     pageIndex     读取第几页数据
     * @apiParam {Integer}     pageSize       每页读取多少条数据
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {List}         pageData                分页数据
     * @apiSuccess {Integer}      pageIndex            页码
     * @apiSuccess {Integer}      pageSize             页面大小
     * @apiSuccess {Long}         total                总量
     * @apiSuccess {String}   fileId        文件记录id
     * @apiSuccess {String}   name        文件名
     * @apiSuccess {String}   suffix        文件后缀
     * @apiSuccess {String}   mimeType        mime类型
     * @apiSuccess {Long}   size        文件大小
     * @apiSuccess {String}   url        文件下载地址
     * @apiSuccess {String}   type        文件分类
     * @apiSuccess {Long}   uploadDate        文件上传时间
     * @apiSuccess {Long}   uploaderName      上传者
     * @apiAuthor wangl
     * @date 2016年2月20日10:51:29
     */
    @RequestMapping("queryFile")
    public JSONMessage queryFile(VpanFileParam param, Integer userId) {
        //登录用户id
        Integer tokenId = ReqUtil.instance.getUserId();
        if (tokenId == 0 && userId != null) {
            tokenId = userId;
        }
        PageVO ret;
        param.setUploader(tokenId);
        ret = vpanFileService.searchFile(param);
        return JSONMessage.success(ret);
    }

    /**
     * @api {post} /vpanfile/searchUploadAndSendFile  搜索我上传和我接收的文件
     * @apiVersion 1.0.0
     * @apiName searchUploadAndSendFile
     * @apiGroup 文件管理
     * @apiDescription 搜索我上传和我接收的文件 （不分页，各取前100条记录）
     * @apiParam {String}     access_token          token
     * @apiParam {String}     fileNameKey         	搜索关键字（可以为空）
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {List}   uploadList        我上传的文件列表
     * @apiSuccess {List}   sendList        	我接收的文件列表
     * @apiSuccess {String}   fileId        文件记录id
     * @apiSuccess {String}   name        文件名
     * @apiSuccess {String}   suffix        文件后缀
     * @apiSuccess {String}   mimeType        mime类型
     * @apiSuccess {Long}   size        文件大小
     * @apiSuccess {String}   url        文件下载地址
     * @apiSuccess {String}   type        文件分类
     * @apiSuccess {Long}   uploadDate        文件上传时间
     * @apiSuccess {String}   fileSendId        文件发送记录id
     * @apiSuccess {Integer}   sendUserId        文件发送人id
     * @apiSuccess {Long}   sendDate        文件发送时间
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("searchUploadAndSendFile")
    public JSONMessage searchUploadAndSendFile(String fileNameKey) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        //返回结果
        Map<String, Object> map = new HashMap<String, Object>();

        //查询 我上传的文件  取前100条
        VpanFileParam uploadParam = new VpanFileParam();
        uploadParam.setFileNameKey(fileNameKey);
        uploadParam.setPageSize(100);
        uploadParam.setPageIndex(0);
        uploadParam.setUploader(userId);
        uploadParam.setMode("upload");
        PageVO uploadRet = vpanFileService.searchUploadFile(uploadParam);
        map.put("uploadList", uploadRet.getPageData());

        //查询我接受的文件，取前100条
        uploadParam.setMode("receive");
        uploadParam.setReceiveUserId(userId);
        PageVO sendRet = vpanFileService.searchSendFile(uploadParam);
        map.put("sendList", sendRet.getPageData());

        return JSONMessage.success(null, map);
    }

    /**
     * @api {post} /vpanfile/sendFile  发送文件
     * @apiVersion 1.0.0
     * @apiName sendFile
     * @apiGroup 文件管理
     * @apiDescription 发送单个文件给多个人
     * @apiParam {String}     access_token          token
     * @apiParam {String}     fileId         	文件id
     * @apiParam {String}     receiveUserIds         	接收文件的用户id（多个用户id用逗号","分隔）
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 王峭
     * @date 2016年1月13日
     */
    @RequestMapping("sendFile")
    public JSONMessage sendFile(String fileId, String receiveUserIds) {
        //登录用户id
        Integer userId = ReqUtil.instance.getUserId();

        vpanFileService.saveSendFile(userId, fileId, receiveUserIds);

        return JSONMessage.success(null);
    }


    /**
     * @api {post} /vpanfile/saveFile  保存文件
     * @apiVersion 1.0.0
     * @apiName saveFile
     * @apiGroup 文件管理
     * @apiDescription 医生或患者保存文件
     * @apiParam {String}     access_token          token
     * @apiParam {String}     fileId         	文件id
     * @apiParam {Integer}    receiveUserId    文件接收用户Id
     * @apiParam {Integer}    sendUserId       文件发送用户Id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("saveFile")
    public JSONMessage saveFile(String fileId, Integer receiveUserId, Integer sendUserId) {
        vpanFileService.saveSendFile(sendUserId, fileId, receiveUserId + "");
        return JSONMessage.success(null);
    }

    /**
     * @api {post} /vpanfile/communitySaveFile  医生社区保存文件
     * @apiVersion 1.0.0
     * @apiName communitySaveFile
     * @apiGroup 文件管理
     * @apiDescription 医生加油站保存文件
     * @apiParam {String}     access_token          token
     * @apiParam {String}     fileId         	文件id
     * @apiParam {Integer}    receiveUserId    文件接收用户Id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 李明
     * @date 2016年11月9日14:09:31
     */
    @RequestMapping("communitySaveFile")
    public JSONMessage communitySaveFile(String fileId, Integer receiveUserId) {
        vpanFileService.communitySaveSendFile(fileId, receiveUserId + "");
        return JSONMessage.success(null);
    }

    /**
     * @api {post} /vpanfile/isInMyFileList  判断该文件是否在我的文件列表
     * @apiVersion 1.0.0
     * @apiName isInMyFileList
     * @apiGroup 文件管理
     * @apiDescription 判断该文件是否在我的文件列表
     * @apiParam {String}     access_token          token
     * @apiParam {String}     fileId         	文件id
     * @apiParam {Integer}    receiveUserId    文件接收用户Id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {String}   data        	 true:存在，false：不存在
     * @apiAuthor wangl
     * @date 2016年2月23日
     */
    @RequestMapping("isInMyFileList")
    public JSONMessage isInMyFileList(String fileId, Integer receiveUserId) {
        return JSONMessage.success(vpanFileService.isInMyFileList(fileId, receiveUserId));
    }

    /**
     * @api {post} /vpanfile/myLastFile  我的最近文件
     * @apiVersion 1.0.0
     * @apiName vpanfile.myLastFile
     * @apiGroup 文件管理
     * @apiDescription 我的最近文件
     * @apiParam {String}     access_token      token
     * @apiParam {Integer}    pageIndex         页码
     * @apiParam {Integer}    pageSize          页面大小
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiSuccess {Object}   data        	 true:存在，false：不存在
     * @apiSuccess {String}   data.pageData.fileId        文件id，对应VpanUploadFile
     * @apiSuccess {Integer}   data.pageData.sendUserId        发送人id
     * @apiSuccess {Integer[]}   data.pageData.receiveUserId        接收人id
     * @apiSuccess {Long}   data.pageData.sendDate        文件发送时间
     * @apiSuccess {String}   data.pageData.name        文件名（冗余）
     * @apiSuccess {String}   data.pageData.suffix        文件后缀（冗余）
     * @apiSuccess {String}   data.pageData.mimeType        mime类型（冗余）
     * @apiSuccess {String}   data.pageData.type        文件分类（冗余）
     * @apiSuccess {Long}   data.pageData.size        文件大小（冗余）
     * @apiSuccess {String}   data.pageData.url        文件下载地址（冗余）
     * @apiSuccess {String}   data.pageData.bucketType        文件类型（公有，私有，会影响下载所以必须冗余）
     * @apiSuccess {String}   data.pageData.gid        会话组id
     * @apiSuccess {String}   data.pageData.imName        会话组名称
     * @apiSuccess {String}   data.pageData.sourceName        来源名称
     * @apiSuccess {String}   data.pageData.targetName        发送目标名称
     * @apiSuccess {Boolean}   data.pageData.sender        是否为发送者
     * @apiSuccess {String}   data.pageData.sizeStr        文件大小（不满1KB，显示单位为bytes；不满1MB，显示单位为KB；不满1GB的，显示单位为MB）
     * @apiAuthor 傅永德
     * @date 2017年3月21日
     */
    @RequestMapping("/myLastFile")
    public JSONMessage myLastFile(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        return JSONMessage.success(vpanFileService.myLastFiles(keyword, pageIndex, pageSize));
    }

    /**
     * @api {post} /vpanfile/sendFileRecord  文件发送记录
     * @apiVersion 1.0.0
     * @apiName vpanfile.sendFileRecord
     * @apiGroup 文件管理
     * @apiDescription 文件发送记录
     * @apiParam   {String}   access_token      token
     * @apiParam   {String}   fileId         	文件id
     * @apiParam   {String}   gid               会话组id
     * @apiParam   {String}   url               文件的url
     * @apiParam   {String}   msgId             消息id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 傅永德
     * @date 2017年3月21日
     */
    @RequestMapping("/sendFileRecord")
    public JSONMessage sendFileRecord(
            @RequestParam(name = "fileId") String fileId,
            @RequestParam(name = "gid") String gid,
            @RequestParam(name = "url") String url,
            @RequestParam(name = "msgId") String msgId
    ) throws HttpApiException {
        vpanFileService.sendFileRecord(fileId, gid, url, msgId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /vpanfile/removeFileRecord  撤销文件发送
     * @apiVersion 1.0.0
     * @apiName vpanfile.removeFileRecord
     * @apiGroup 文件管理
     * @apiDescription 文件发送记录
     * @apiParam   {String}   access_token      token
     * @apiParam   {String}   msgId             消息id
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor 傅永德
     * @date 2017年3月30日
     */
    @RequestMapping("/removeFileRecord")
    public JSONMessage removeFileRecord(
            @RequestParam(name = "msgId")String msgId
    ) {
        vpanFileService.removeFileRecord(msgId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /vpanfile/updateLastFile  将下载的文件添加到最近文件列表
     * @apiVersion 1.0.0
     * @apiName vpanfile.updateLastFile
     * @apiGroup 文件管理
     * @apiDescription 将下载的文件添加到最近文件列表
     * @apiParam   {String}   access_token      token
     * @apiParam   {String}   id               文件ID
     * @apiParam   {String}   name             文件名
     * @apiParam   {String}   suffix           后缀
     * @apiParam   {String}   mimeType         mimeType
     * @apiParam   {String}   type             类型
     * @apiParam   {String}   size            大小 单位bite
     * @apiParam   {String}   url             url
     *
     * @apiSuccess {String}   resultCode        1=返回结果正常
     * @apiAuthor wangl
     * @date 2017-9-28 15:06:01
     */
    @RequestMapping("/updateLastFile")
    public JSONMessage updateLastFile(LatelyVpanFileVo latelyVpanFileVo) {
        vpanFileService.updateLastFile(latelyVpanFileVo);
        return JSONMessage.success();
    }

}

