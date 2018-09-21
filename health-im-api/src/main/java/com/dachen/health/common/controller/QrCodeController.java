package com.dachen.health.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CCarePlan;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.po.QrScanParamPo;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.entity.QrCodeParam;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.wx.remote.WXRemoteManager;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.util.DESUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/qr")
public class QrCodeController {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	
	@Autowired
	private PackMapper packMapper;
	
	@Autowired
    private ICommonGroupDoctorService commonGroupDoctorService;
	
	@Autowired
    private IQrCodeService qrCodeService;
	
	@Autowired
    private WXRemoteManager wxManager;
			
	private Integer getUserId(String _AccessToken) {
		return ReqUtil.instance.getUserId(_AccessToken);
	}
	
	
	/**
	 * @api {[post]} /qr/createQRImage 生成用户二维码
	 * @apiVersion 1.0.0
	 * @apiName createQRImage
	 * @apiGroup 二维码
	 * @apiDescription 生成用户二维码
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} userId 用户Id
	 * @apiParam {String} userType 用户类型
	 * 
	 * @apiSuccess String data.url 用户对象实体
	 * @apiAuthor 李淼淼
	 * @date 2015年7月30日
	 */
	@RequestMapping(value = "/createQRImage",method = { RequestMethod.POST })
	public JSONMessage createQRImage(String userId, String userType) {
		Map<String,Object> result = new HashMap<>();
		result.put("url", qrCodeService.generateUserQr(userId, userType));
		return JSONMessage.success(result);
	}
	
	/**
	 * @api {[post]} /qr/generateCircleQRImage 生成圈子/科室二维码
	 * @apiVersion 1.0.0
	 * @apiName generateCircleQRImage
	 * @apiGroup 二维码
	 * @apiDescription 生成圈子/科室二维码
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} circleId   圈子/科室id
	 * @apiParam {String} logoUrl    圈子/科室logo地址
	 * @apiParam {String} inviteUrl  邀请加入圈子/科室的h5地址
	 * 
	 * @apiSuccess String data.url 用户对象实体
	 * @apiAuthor longjh
	 * @date 2017/09/07
	 */
	@RequestMapping(value = "/generateCircleQRImage",method = { RequestMethod.POST })
	public JSONMessage generateCircleQRImage(String circleId, String logoUrl, String inviteUrl) {
		Map<String,Object> result = new HashMap<>(1);
		result.put("url", qrCodeService.generateQr(circleId, logoUrl, inviteUrl));
		return JSONMessage.success(result);
	}
	
	/**
     * @api {[post]} /qr/generateQRImage 生成集团二维码、关怀二维码
     * @apiVersion 1.0.0
     * @apiName generateQRImage
     * @apiGroup 二维码
     * @apiDescription 生成集团二维码、健康关怀二维码
     * 
     * @apiParam {String} access_token token
     * @apiParam {String} id Id
     * @apiParam {String} type 类型, group表示集团，dept表示科室，care表示关怀计划
     * 
     * @apiSuccess String data.url 用户对象实体
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/generateQRImage",method = { RequestMethod.POST })
    public JSONMessage generateQRImage(String id, String type) {
        Map<String,Object> result = new HashMap<String,Object>(1);
        Integer doctorId = ReqUtil.instance.getUserId();
        result.put("url", qrCodeService.generateQr(id, type, doctorId));
        return JSONMessage.success(result);
    }

	/**
	 * 用户扫描二维码调用接口
	 * 如何是外部app扫描，则返回下载地址
	 * 如果是内部app扫描则返回用户信息。
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/scanning",method = { RequestMethod.GET })
	public Object qrScanning(String paramId, String tk, String access_token, HttpServletResponse response) throws Exception {
	    String src = null;
		/*JSONObject jo = getQrScanParam(paramId);
		String tk = jo.getString("tk");*/
        try{
            src = DESUtil.decrypt(tk);
        }catch(Exception ex){
            src = DESUtil.decrypt(java.net.URLDecoder.decode(tk, "UTF-8"));
        }
        //如果是第三方扫描
		if (StringUtils.isBlank(access_token)) {
			/*String generateUrl = shortUrlComponent.generateShortUrl(
			        String.format("%1$s%2$s", PropertiesUtil.getContextProperty("invite.url"), 
			                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle")));*/
			/**修改成从应用宝获取应用**/
			String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
			response.sendRedirect(generateUrl);
			
			return JSONMessage.success("请求已转发", null);
		}
		
		try {
			String toUserId = src.split("&")[0];
			User data = getUserInfo(Integer.valueOf(toUserId));

			Integer curUserId = ReqUtil.instance.getUserIdFromAuth(access_token);
			User curUser = userManager.getUserInfoById(curUserId);
			if (Objects.nonNull(curUser) && Objects.equals(curUser.getBaseUserLevel(), UserLevel.Tourist.getIndex())) {
                return JSONMessage.failure("您还未通过资格审核，暂时无法浏览");
            }
			
			return JSONMessage.success(data);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			return JSONMessage.failure("请求处理失败");
		}
	}

	private User getUserInfo(Integer fromUserId, Integer toUserId) {
		User data = userManager.getUser(toUserId);
		
		Map<String,Object> map = commonGroupDoctorService.getContactBySameGroup(fromUserId, toUserId);
		data.setGroupContact((String)map.get("groupContact"));
		data.setGroupRemark((String)map.get("groupRemark"));
		data.setGroupSame((Integer)map.get("groupSame"));
		return data;
	}
	
	private User getUserInfo(Integer toUserId) {
        User data = userManager.getUser(toUserId);
        return data;
    }
	
	@Resource
	protected CarePlanApiClientProxy carePlanApiClientProxy;

	/**
	 * for test
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/carePlan/{id}")
	public JSONMessage generateScan(@PathVariable String id) throws Exception {
		CCarePlan cCarePlan = carePlanApiClientProxy.findById(id);
		return JSONMessage.success(null, cCarePlan);
	}


	@Autowired
	protected ShortUrlComponent shortUrlComponent;
	
	/**
	 * @api {[post]} /qr/generateScan 扫描二维码
	 * @apiVersion 1.0.0
	 * @apiName generateScan
	 * @apiGroup 二维码
	 * @apiDescription 扫描二维码（集团、健康关怀）
	 * 
	 * @apiParam {String} tk tk
	 * 
	 * @apiSuccess String data.url 用户对象实体
	 * @apiAuthor 李淼淼
	 * @date 2015年7月30日
	 */
	@RequestMapping(value = "/generateScan",method = { RequestMethod.GET })
	public Object generateScan(String tk, String access_token, HttpServletResponse response) throws Exception {
		//检验是否在应用中扫码

		/*type =  group 集团二维码
		  type =  dept  科室
		  type =  care  关怀套餐二维码
		  */
		//返回用户的信息
		try {
			String src = DESUtil.decrypt(tk);
			if(src == null || src.split("&").length < 2){
				return JSONMessage.failure("数据解密出错");
			}
			String id = src.split("&")[0];
			String type = src.split("&")[1];

			// 处理客户端传了两个token，access_token会拼接成"39c88a6888c24db3ab2f420fe56c296d,39c88a6888c24db3ab2f420fe56c296d"
			if (StringUtil.isNoneBlank(access_token) && access_token.contains(",")) {
				access_token = access_token.split(",")[0];
			}
			//集团二维码扫描返回
			if (StringUtils.equals("group", type) || StringUtils.equals("dept", type)) {
				String[] array = src.split("&");
				if (access_token == null) {
					//返回下载地址
					StringBuffer url = new StringBuffer();
                    url.append(PropertiesUtil.getContextProperty("invite.url"))
                        .append(PropertiesUtil.getContextProperty("invite.registerJoin"))
                        .append("?")
                        .append("groupId=").append(id);
					if (ReqUtil.isMedicalCircle()) {
                        url.append("&type=1");
					} else {
                        url.append("&type=0");
					}
					String doctorId = null;
					if (array != null && array.length>=3) {
						doctorId = array[2];
						url.append("&").append("doctorId=").append(doctorId);
					}
					
					if (StringUtil.isEmpty(doctorId)) {
						url = new StringBuffer().append(PropertiesUtil.getContextProperty("invite.url"))
								.append(PropertiesUtil.getContextProperty("invite.downloadDoctor"));
					}
					
					String	generateUrl = shortUrlComponent.generateShortUrl(url.toString());
					response.sendRedirect(generateUrl);
					return null;
				}
				String fromUserId = getUserId(access_token)+"";
				// 请求令牌是否有效
				if (StringUtil.isBlank(fromUserId)){
					return JSONMessage.failure("请求令牌无效!");
				}
				if (array != null && array.length>=3) {
					String doctorId = array[2];
					Map<String, String> data = getData(id, type, doctorId);
					return JSONMessage.success(data);
				} else {
					Map<String, String> data = getData(id, type);
					return JSONMessage.success(data);
				}
			} else if ("care".equals(type)) {
				//根据access_token来识别二维码扫描来之客户端还是第三方
				Map<String,String> mapResult = new HashMap<String,String>();
				if (access_token == null) {
					String generateUrl = PropertiesUtil.getContextProperty("application.rootUrl") + PropertiesUtil.getContextProperty("care.share.link") + "?packId=" + id;
					response.sendRedirect(generateUrl);
					return null;
				}
				Integer userId = getUserId(access_token);
				if (null == userId || 0 == userId){	// 支持匿名请求
					mapResult.put("id", id);
					mapResult.put("type","care");
					return JSONMessage.success(null, mapResult);
				}
				
				User user = userManager.getUser(userId);
				if (user == null) {
					return JSONMessage.failure("无效的用户!");
				}
				Pack pack = packMapper.selectByPrimaryKey(Integer.valueOf(id));
				if(pack==null || pack.getGroupId()==null) {
					return JSONMessage.failure("找不到信息!");
				}
				
				String careTemplateId = pack.getFollowTemplateId();
				if (StringUtil.isBlank(careTemplateId)) {
					CCarePlan carePlan = carePlanApiClientProxy.findById(pack.getCareTemplateId());
					careTemplateId = carePlan.getSourceId();
				}
				mapResult.put("id", careTemplateId);
				mapResult.put("type","care");
				if(userId.equals(pack.getDoctorId())){
					mapResult.put("packId", id);
					mapResult.put("careStatus", "1");
					return JSONMessage.success(null, mapResult);
				}
				if(UserEnum.UserType.doctor.getIndex() ==user.getUserType()) {
					
					List<GroupDoctor> groupDoctors = groupDoctorDao.findGroupDoctor(userId, null, "C");
					//添加平台ID
					GroupDoctor groupDoc = new GroupDoctor();
					groupDoc.setGroupId(GroupUtil.PLATFORM_ID);
					groupDoctors.add(groupDoc);
					boolean bool = false;
					for(GroupDoctor groupDoctor : groupDoctors) {
						if(pack.getGroupId().equals(groupDoctor.getGroupId())) {
							bool = true;
						}
					}
					//判断是否有权限访问
					if(bool) {
						//判断是否已添加过
						Pack packParam = new Pack();
						packParam.setDoctorId(userId);
						packParam.setCareTemplateId(pack.getCareTemplateId());
						Integer count = packMapper.countCarePack(packParam);
						if(count==0) {
							mapResult.put("careStatus", "0");
							mapResult.put("groupId", pack.getGroupId());
						}else {
							mapResult.put("careStatus", "1");
							mapResult.put("packId", id);
						}
						return JSONMessage.success(null, mapResult);
					}else {
						return JSONMessage.failure("暂无权限查看，请扫描其它二维码试试！");
					}
				} else {
					Map<String, String> data = getData(id, type);
					return JSONMessage.success(null, data);
				}
			} else {
				return JSONMessage.failure("无效的请求！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return JSONMessage.failure("请求处理失败！");
		}
	}
	
	/**
     * @api {[post]} /qr/circleQrcodeScan 扫描圈子二维码
     * @apiVersion 1.0.0
     * @apiName circleQrcodeScan
     * @apiGroup 扫描圈子二维码
     * @apiDescription 扫描圈子二维码
     * 
     * @apiParam {String} sc 扫描端的标识，圈子：circle-app
     * @apiParam {String} paramId paramId
     * @apiParam {String} access-token  
     * 
     * @apiSuccess String data.circleId   圈子id
     * @apiSuccess String data.inviteUrl  邀请链接
     * @apiSuccess String data.md5Key     圈子logo地址的MD5
     * @apiSuccess String data.type       传入的参数sc的值
     * @apiAuthor longjh
     * @date 2017/09/08
     */
    @RequestMapping(value = "/circleQrcodeScan/nologin",method = { RequestMethod.GET })
    public Object circleQrcodeScan(@RequestParam(defaultValue="none") String sc, 
            @RequestParam(defaultValue="") String tk, String access_token,
        HttpServletResponse response) throws Exception {
        String source = null;
        try{
            source = DESUtil.decrypt(tk);
        }catch(Exception ex){
            source = DESUtil.decrypt(java.net.URLDecoder.decode(tk, "UTF-8"));
        }
        if(StringUtils.isBlank(source)){
            return JSONMessage.failure("请求参数无效");
        }
        String[] sources = source.split("&");
        if(sources.length < 3){
            return JSONMessage.failure("请求参数不匹配");
        }
        Map<String, String> param = new HashMap<>();
        param.put("circleId", sources[0]);
        param.put("inviteUrl", sources[1]);
        param.put("md5Key", sources[2]);
        param.put("type", "circle-app");

        if(StringUtil.isEmpty(access_token)){
            //第三方扫描
            response.sendRedirect(param.get("inviteUrl"));
            return JSONMessage.success(param);
        } else {
            Integer curUserId = ReqUtil.instance.getUserIdFromAuth(access_token);
            User curUser = userManager.getUser(curUserId);
            if (Objects.nonNull(curUser) && Objects.equals(curUser.getBaseUserLevel(), UserLevel.Tourist.getIndex())) {
                return JSONMessage.failure("您还未通过资格审核，暂时无法浏览");
            }
            //医生圈扫描
            return JSONMessage.success(param);
        }
    }


	private Map<String, String> getData(String id, String type) {
		Map<String,String> data = new HashMap<String,String>();
		data.put("id", id);
		data.put("type", type);
		return data;
	}
	
	private Map<String, String> getData(String id, String type, String doctorId) {
		Map<String,String> data = Maps.newHashMap();
		data.put("id", id);
		data.put("type", type);
		data.put("doctorId", doctorId);
		return data;
	}
	
	/**
	 * @throws Exception 
	 * @api {[post]} /qr/wxQrScanning 扫描带微信域名的二维码
	 * @apiVersion 1.0.0
	 * @apiName wxQrScanning
	 * @apiGroup 二维码
	 * @apiDescription 扫描带微信域名的二维码（包含医生、集团）
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} tk tk
	 * 
	 * @apiSuccess String data.url 用户对象实体
	 * @apiAuthor 李淼淼
	 * @date 2015年7月30日
	 */
	@RequestMapping(value = "/wxQrScanning")
	public JSONMessage wxQrScanning(String tk,String access_token) throws Exception {
		try {
			if (StringUtil.isBlank(access_token)) {
				return JSONMessage.failure("请求令牌无效!");
			}
			String scene = wxManager.getRelateBizId(tk);
			if (StringUtil.isBlank(scene)) {
				return new JSONMessage(1030303 ,"未找到微信公众号二维码URL的对应关系");
			}
			String id = scene;
			String type = scene.matches("[0-9]+") ? "3" : "group";
//			String src = DESUtil.decrypt(tk);
//			if(src == null || src.split("&").length != 2){
//				return JSONMessage.failure("数据解密出错");
//			}
//			String id = src.split("&")[0];
//			String type = src.split("&")[1];
			
			if (type.equals("group")) {
				return JSONMessage.success(getData(id, type));
			} else if (type.equals("3")) {
				return JSONMessage.success(getUserInfo(getUserId(access_token), Integer.valueOf(id)));
			} else {
				return JSONMessage.failure("无效的请求！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return JSONMessage.failure("请求处理失败！");
		}
	}
	
	
	@RequestMapping(value = "/modifyUserQr",method = { RequestMethod.POST })
	public JSONMessage modifyUserQr(String userId, String userType) {
		return JSONMessage.success(qrCodeService.modifyUserQr(userId, userType));
	}


	/**
	 * @api {[post]} /qr/generateSignUpImage 生成医生圈签到二维码
	 * @apiVersion 1.0.0
	 * @apiName wxQrScanning
	 * @apiGroup 二维码
	 * @apiDescription 生成医生圈签到二维码
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} call_back_url 第三方回调地址
	 * @apiParam {String} call_back_param 第三方回调参数
	 *
	 * @apiSuccess String data 二维码图片链接
	 * @apiAuthor wangl
	 * @date 2017-6-6
	 */
	@RequestMapping(value = "/generateSignUpImage",method = { RequestMethod.POST })
	public JSONMessage generateSignUpImage(String call_back_url, String call_back_param) {
		Object data = qrCodeService.generateSignUpImage(call_back_url, call_back_param);
		return JSONMessage.success(data);
	}
	
	
	/**
     * @api {[post]} /qr/generateCode 生成二维码
     * @apiVersion 1.0.1
     * @apiName 生成二维码[generateCode]
     * @apiGroup 二维码
     * @apiDescription 生成二维码
     *
     * @apiParam {String} access_token token
     * @apiParam {Integer} type 生成二维码的类型
     * @apiParam {Object} data 二维码携带的数据map结构
     * @apiParam {String} checkCode 校验码(type+固定值)
     *
     * @apiSuccess String data 二维码图片链接
     * @apiAuthor longjh
     * @date 2017/09/14
     */
    @RequestMapping(value = "/generateCode",method = { RequestMethod.POST })
    public JSONMessage generateCode(@RequestBody QrCodeParam param) {
        if(Objects.isNull(param.getType())){
            return JSONMessage.failure("请求参数无效");
        }
        String qrCode = qrCodeService.generateQrCode(param);
        return JSONMessage.success(qrCode);
    }
    
    /**
     * @api {[post]} /qr/scanCode 解析二维码扫描数据
     * @apiVersion 1.0.1
     * @apiName 解析二维码扫描数据[scanCode]
     * @apiGroup 二维码
     * @apiDescription 解析二维码扫描数据
     *
     * @apiParam {String} access_token token
     * @apiParam {Integer} type 生成二维码的类型
     * @apiParam {Object} data 二维码携带的数据map结构
     * @apiParam {String} checkCode 校验码(type+固定值)
     *
     * @apiSuccess String data 解析信息数据
     * @apiAuthor longjh
     * @date 2017/09/14
     */
    @RequestMapping(value = "/scanCode",method = { RequestMethod.GET })
    public JSONMessage scanCode(QrCodeParam param) {
        Map<String, Object> data = qrCodeService.scanQrCode(param);
        return JSONMessage.success(data);
    }
    
    /**
   	 * @api {[post]} /qr/generateMeetingActivityQRImage 生成大会活动二维码
   	 * @apiVersion 1.0.0
   	 * @apiName generateMeetingActivityQRImage
   	 * @apiGroup 二维码
   	 * @apiDescription 生成大会活动二维码
   	 * 
   	 * @apiParam {String} access_token token
   	 * @apiParam {String} id   会议id
   	 * @apiParam {String} logoUrl    会议宣传图
   	 * 
   	 * @apiSuccess String data.url 用户对象实体
   	 * @apiAuthor wj
   	 * @date 2018/03/10
   	 */
   	@RequestMapping(value = "/generateMeetingActivityQRImage",method = { RequestMethod.POST })
   	public JSONMessage generateMeetingActivityQRImage(String id, String logoUrl,String type) {
   		Map<String,Object> result = new HashMap<>(1);
   		result.put("url", qrCodeService.generateMeetingActivityQr(id, logoUrl,type));
   		return JSONMessage.success(result);
   	}
   	
   	/**
     * @api {[post]} /qr/meetingActivityQrcodeScan 扫描会议二维码
     * @apiVersion 1.0.0
     * @apiName meetingActivityQrcodeScan
     * @apiGroup 扫描会议二维码
     * @apiDescription 扫描会议二维码
     * 
     * @apiParam {String} sc 扫描端的标识，圈子：circle-app
     * @apiParam {String} tk tk
     * @apiParam {String} access_token 
     * 
     * @apiSuccess String data.id   会议id
     * @apiSuccess String data.inviteUrl  邀请链接
     * @apiSuccess String data.md5Key     圈子logo地址的MD5
     * @apiSuccess String data.type       传入的参数sc的值
     * @apiSuccess String data.url 用户对象实体
   	 * @apiAuthor wj
   	 * @date 2018/03/10
     */
    @RequestMapping(value = "/meetingActivityQrcodeScan/nologin",method = { RequestMethod.GET })
    public Object meetingActivityQrcodeScan(@RequestParam(defaultValue="") String tk, String access_token,
        HttpServletResponse response) throws Exception {
        String source = null;
        try{
            source = DESUtil.decrypt(tk);
        }catch(Exception ex){
            source = DESUtil.decrypt(java.net.URLDecoder.decode(tk, "UTF-8"));
        }
        if(StringUtils.isBlank(source)){
            return JSONMessage.failure("请求参数无效");
        }
        String[] sources = source.split("&");
        if(sources.length !=  2){
            return JSONMessage.failure("请求参数不匹配");
        }
        Map<String, String> param = new HashMap<>();
        param.put("id", sources[0]);
        param.put("type", sources[1]);
        //这个参数很重要啊
        param.put("businessType", "meeting-activity");
       
        if(StringUtil.isEmpty(access_token)){
            //第三方扫描
            if(Objects.equals(param.get("type"),"YSQ")){
            	try{
            		response.sendRedirect(shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.arouseDoctor.MedicalCircle")));
            	}catch(Exception e){
            		//短链不行 试一下长链
                	response.sendRedirect(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.arouseDoctor.MedicalCircle"));
            	}
            }else{
            	return JSONMessage.failure("第三方扫描活动会议跳转类型错误");
            }
            return JSONMessage.success(param);
        } else {
        	if(Objects.equals(param.get("type"),"YSQ")){
        		Integer curUserId = ReqUtil.instance.getUserIdFromAuth(access_token);            
                User curUser = userManager.getUser(curUserId);
                if(Objects.isNull(curUser)){
                	logger.info("meetingActivityQrcodeScan userId.{}",curUserId);
                	return JSONMessage.failure("您还未通过资格审核，无法浏览");
                }
                /*if (Objects.equals(curUser.getBaseUserLevel(), UserLevel.Tourist.getIndex())) {
                    return JSONMessage.failure("您还未通过资格审核，暂时无法浏览");
                }*/
                //医生圈扫描
                return JSONMessage.success(param);
            }else{
            	return JSONMessage.failure("第三方扫描活动会议跳转类型错误");
            }
        }
    }
}
