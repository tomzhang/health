package com.dachen.health.controller.user;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.commons.constants.HospitalLevelEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.vo.DoctorInfoDetailsVO;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.order.entity.vo.CareSimpleVO;
import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.GetRecheckInfo;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.vo.DoctorDealingInfoVO;
import com.dachen.health.user.entity.vo.DoctorRecheckInfoDetailVO;
import com.dachen.health.user.entity.vo.DoctorRecheckInfoVO;
import com.dachen.health.user.entity.vo.DoctorVO;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.health.user.service.IRelationService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
public class DoctorController extends AbstractController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IRelationService relationService;

    @Autowired
    private IDoctorService doctorService;
    
    @Autowired
    private IBaseUserService baseUserService;
    
    @Resource
    private IGroupDoctorDao groupDoctorDao;
    
    @Resource
    private IPlatformDoctorService pdocService;
    
    @Resource
    private IGroupDoctorService groupDoctorService;
    
    @Resource
    private UserManager userManager;
    
	@Autowired
	IPackService packService;
    
    @Resource
    private IInvitePatientService invitePatient;
    
    @Resource
    private IGroupService groupService;
    
    @Resource
    private IGroupFacadeService groupFacadeService;
    
    @Autowired
	private IBaseDataDao baseDataDao;
    
    @Resource
    private IQrCodeService qrcodeService;

    @Autowired
    private IllHistoryInfoService illHistoryInfoService;

    @Autowired
    private IImageDataService imageDataService;

    @Autowired
    private Auth2Helper auth2Helper;

    /**
     * @api {get} /doctor/getPatients 医生获取患者
     * @apiVersion 1.0.0
     * @apiName getPatients
     * @apiGroup 医生
     * @apiDescription 医生获取患者
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {Integer}   users.userId             用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           手机
     * @apiSuccess {Integer}   users.sex                性别
     * @apiSuccess {Integer}   users.status             状态（1, "正常";2, "待审核";3, "审核未通过";4, "暂时禁用";5, "永久禁用";6未激活;7, "未认证;"8,"离职";9,"注销"）
     * @apiSuccess {String}   users.headPicFileName     头像
     * @apiSuccess {Object[]} tags                      标签分类
     * @apiSuccess {String}   tags.tagId              	标签Id
     * @apiSuccess {String}   tags.tagName              标签名
     * @apiSuccess {Integer}   tags.num               	数量
     * @apiSuccess {Integer[]} tags.userIds             用户id数组
     * @apiSuccess {Integer}  tags.seq              	排序
     * @apiSuccess {Boolean}  tags.isSys              	是否系统标签（true不可修改；false可修改）
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getPatients")
    public JSONMessage getPatients() {
        return JSONMessage.success(null, relationService.getRelationAndTag(RelationType.doctorPatient, ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /doctor/getAllMyPatients 获取我的患者
     * @apiVersion 1.0.0
     * @apiName getAllMyPatients
     * @apiGroup 医生
     * @apiDescription 获取我的患者
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess  {String} users.ageStr 年龄
     * @apiSuccess  {String} users.name 姓名
     * @apiSuccess  {String} users.birthday 生日
     * @apiSuccess  {String} users.headPicFileName 头像
     * @apiSuccess  {String} users.patientId 患者id
     * @apiSuccess  {String} users.sex 性别（1男，2女）
     * @apiSuccess  {String} users.userId 用户Id
     * @apiSuccess  {String} users.area 地区
     * @apiSuccess  {String} users.remarkName 备注名
     * @apiSuccess  {String} users.remark 备注
     * @apiSuccess {Object[]} tags                      标签分类
     * @apiSuccess {String}   tags.tagId              	标签Id
     * @apiSuccess {String}   tags.tagName              标签名
     * @apiSuccess {Integer}   tags.num               	数量
     * @apiSuccess {Integer[]} tags.userIds             用户id数组
     * @apiSuccess {Integer}  tags.seq              	排序
     * @apiSuccess {Boolean}  tags.isSys              	是否系统标签（true不可修改；false可修改）
     * @apiAuthor  傅永德
     * @date 2016年12月12日
     */
    @RequestMapping("/getAllMyPatients")
    public JSONMessage getAllMyPatients() {
        Map<String, Object> result = illHistoryInfoService.getAllMyPatients();
        return JSONMessage.success(result);
    }

    /**
     * @api {get} /doctor/addPatient 医生添加患者
     * @apiVersion 1.0.0
     * @apiName addPatient
     * @apiGroup 医生
     * @apiDescription 医生添加患者
     *
     * @apiParam  {String} access_token token
     * @apiParam  {String...} telephones 电话号码
     *
     * @apiSuccess {String} userId 当前电话用户Id
     * @apiSuccess {String} msg 当前电话状态信息
     * @apiSuccess {Number=1} resultCode    返回状态码
     *
     * @apiAuthor  谢平
     * @date 2015年12月3日
     */
    @RequestMapping("/addPatient")
    public JSONMessage addPatient(String... telephones) {
    	Object data = invitePatient.invitePatient(ReqUtil.instance.getUserId(), telephones);
    	return JSONMessage.success("success", data);
    }
    
    /**
     * @api {get} /doctor/addOnePatient 医生添加单个患者
     * @apiVersion 1.0.0
     * @apiName addOnePatient
     * @apiGroup 医生
     * @apiDescription 医生添加单个患者
     *
     * @apiParam  {String} access_token token
     * @apiParam  {String} telephone 电话号码
     *
     * @apiSuccess {Integer} userId          当前电话用户Id
     * @apiSuccess {Integer} patientId       当前电话患者Id
     * @apiSuccess {Number=1} resultCode    返回状态码
     *
     * @apiAuthor liwei
     * @date 2016年3月5日
     */
    @RequestMapping("/addOnePatient")
    public JSONMessage addOnePatient(String telephone) {
    	Map<String, Object> resultMap = invitePatient.addOnePatient(ReqUtil.instance.getUserId(), telephone);
    	return JSONMessage.success(resultMap);
    }
    
    /**
     * @api {get} /doctor/sendSms 发送短信给患者
     * @apiVersion 1.0.0
     * @apiName sendSms
     * @apiGroup 医生
     * @apiDescription 医生给未激活用户发送会话转换成短信发给患者
     *
     * @apiParam  {String} access_token token
     * @apiParam  {Integer} toUserId 患者用户Id
     * @apiParam  {Integer} smsType 会话类型（1语音、2文字、3照片、4随访计划、5健康关怀、6其他）
     * @apiParam  {String}  text  文本（15字文本）
     *
     * @apiSuccess {Number=1} resultCode    返回状态码
     *
     * @apiAuthor  谢平
     * @date 2015年12月18日
     */
    @RequestMapping("/sendSms")
    public JSONMessage sendSms(Integer toUserId, Integer smsType, String text) {
    	invitePatient.sendSmsAsync(ReqUtil.instance.getUserId(), toUserId, smsType, text);
    	return JSONMessage.success("success");
    }
    
    /**
     * @api {get} /doctor/getFriends 医生获取好友
     * @apiVersion 1.0.0
     * @apiName getFriends
     * @apiGroup 医生
     * @apiDescription 医生获取好友
     *
     * @apiParam  {String}    access_token              token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {String}   users.userId              用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           手机
     * @apiSuccess {Number}   users.sex                 性别
     * @apiSuccess {String}   users.headPicFileName     头像
     * @apiSuccess {String}   users.hospital            医院
     * @apiSuccess {String}   users.departments         科室
     * @apiSuccess {String}   users.title               职称
     * @apiSuccess {Object[]} users.setting                   设置
     * @apiSuccess {Number}   users.setting.defriend          拉黑  1:否，2:是
     * @apiSuccess {Number}   users.setting.topNews           消息置顶 1:否，2：是
     * @apiSuccess {Number}   users.setting.messageMasking    消息屏蔽 1：否，2：是
     * @apiSuccess {Number}   users.setting.collection        收藏 1：否，2：是
     * @apiSuccess {Object[]} tags                      标签分类
     * @apiSuccess {Number}   tags.tagName              标签名
     * @apiSuccess {Number}   tags.count                会员数量
     * @apiSuccess {Number[]} tags.userIds              会员id数组
     * @apiSuccess {Object[]} collection                收藏
     * @apiSuccess {Number}   collection.tagName        个人收藏
     * @apiSuccess {Number[]} collection.userIds        会员id数组
     * @apiSuccess {Object[]} without                   无标签
     * @apiSuccess {Number[]} without.userIds           会员id数组
     *
     * @apiAuthor  范鹏60195
     * @date 2015年7月2日
     */
    @RequestMapping("/getFriends")
    public JSONMessage getFriends() {
        return JSONMessage.success(null, relationService.getRelationAndTag(RelationType.doctorFriend, ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /doctor/getAssistants 医生获取医助
     * @apiVersion 1.0.0
     * @apiName getAssistants
     * @apiGroup 医生
     * @apiDescription 医生获取医助
     *
     * @apiParam  {String}    access_token              token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {String}   users.userId              用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           手机
     * @apiSuccess {Number}   users.sex                 性别
     * @apiSuccess {String}   users.headPicFileName     头像
     * @apiSuccess {Object[]} setting                   设置
     * @apiSuccess {Number}   setting.defriend          拉黑  1:否，2:是
     * @apiSuccess {Number}   setting.topNews           消息置顶 1:否，2：是
     * @apiSuccess {Number}   setting.messageMasking    消息屏蔽 1：否，2：是
     * @apiSuccess {Number}   setting.collection        收藏 1：否，2：是
     * @apiSuccess {Object[]} collection                收藏
     * @apiSuccess {Number}   collection.tagName        个人收藏
     * @apiSuccess {Number[]} collection.userIds        会员id数组
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getAssistants")
    public JSONMessage getAssistants() {
        return JSONMessage.success(null, relationService.getRelations(RelationType.doctorAssistant, ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /doctor/getPatientTags 医生获取标签（患者）
     * @apiVersion 1.0.0
     * @apiName getPatientTags
     * @apiGroup 医生
     * @apiDescription 医生获取添加的标签（患者）
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {String[]} data  标签数组          
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getPatientTags")
    public JSONMessage getPatientTags() {
        return JSONMessage.success(null, relationService.getTag(ReqUtil.instance.getUserId(), UserEnum.TagType.doctorPatient));
    }


    /**
     * @api {post} /doctor/addPatientTag 医生设置标签（患者）
     * @apiVersion 1.0.0
     * @apiName addPatientTag
     * @apiGroup 医生
     * @apiDescription 医生设置标签（患者），只有oldName为空时为添加，不为空时修改；先设置标签，然后加入或减少好友
     *
     * @apiParam  {String} access_token     token
     * @apiParam  {String} tagName          标签名
     * @apiParam  {String} oldName          修改标签时原始名称
     * @apiParam  {Number[]} userIds        用户id数组
     *
     * @apiSuccess {Number=1} resultCode    返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/addPatientTag")
    public JSONMessage addPatientTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorPatient.getIndex());
        relationService.addTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {post} /doctor/updatePatientTag 医生修改标签（患者）
     * @apiVersion 1.0.0
     * @apiName updatePatientTag
     * @apiGroup 医生
     * @apiDescription 医生修改某个患者标签，可选择所有添加的患者标签
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   id               好友id
     * @apiParam   {String[]} tagNames         标签数组
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updatePatientTag")
    public JSONMessage updatePatientTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorPatient.getIndex());
        relationService.updateUserTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {post} /doctor/isSystemTag 是否为系统标签
     * @apiVersion 1.0.0
     * @apiName isSystemTag
     * @apiGroup 医生
     * @apiDescription 判断是否为系统标签
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   tag              标签名
     * @apiSuccess {Integer} resultCode       返回状态码
     * @apiSuccess {Boolean} data.isSystemTag 是否为系统标签（true：是系统标签，false：不是系统标签）
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/isSystemTag")
    public JSONMessage isSystemTag(String tag) {
        Boolean b = relationService.isSystemTag(tag);
        Map<String, Object> result = Maps.newHashMap();
        result.put("isSystemTag", b);
        return JSONMessage.success(result);
    }
    
    /**
     * @api {post} /doctor/deletePatientTag 医生删除标签（患者）
     * @apiVersion 1.0.0
     * @apiName deletePatientTag
     * @apiGroup 医生
     * @apiDescription 医生删除标签（患者），删除标签及标签下患者
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   tagName          标签名称
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/deletePatientTag")
    public JSONMessage deletePatientTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorPatient.getIndex());
        relationService.deleteTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {get} /doctor/getFriendTags 医生获取标签（好友）
     * @apiVersion 1.0.0
     * @apiName getFriendTags
     * @apiGroup 医生
     * @apiDescription 医生获取添加的标签（好友）
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {String[]} data  标签数组          
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getFriendTags")
    public JSONMessage getFriendTags() {
        return JSONMessage.success(null, relationService.getTag(ReqUtil.instance.getUserId(), UserEnum.TagType.doctorFriend));
    }
    
    /**
     * @api {post} /doctor/addFriendTag 医生设置标签（好友）
     * @apiVersion 1.0.0
     * @apiName addFriendTag
     * @apiGroup 医生
     * @apiDescription 医生设置标签（好友），只有oldName为空时为添加，不为空时修改；先设置标签，然后加入或减少好友
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   tagName          标签名
     * @apiParam   {String}   oldName          修改标签时原始名称
     * @apiParam   {Number[]} userIds          用户id数组
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/addFriendTag")
    public JSONMessage addFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorFriend.getIndex());
        relationService.addTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {post} /doctor/updateFriendTag 医生修改标签（好友）
     * @apiVersion 1.0.0
     * @apiName updateFriendTag
     * @apiGroup 医生
     * @apiDescription 医生修改某个好友标签，可选择所有添加的好友标签
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   id               好友id
     * @apiParam   {String[]} tagNames         标签数组
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updateFriendTag")
    public JSONMessage updateFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorFriend.getIndex());
        relationService.updateUserTag(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /doctor/deleteFriendTag 医生删除标签（好友）
     * @apiVersion 1.0.0
     * @apiName deleteFriendTag
     * @apiGroup 医生
     * @apiDescription 医生删除标签（好友），删除标签及标签下好友
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   tagName          标签名称
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/deleteFriendTag")
    public JSONMessage deleteFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.doctorFriend.getIndex());
        relationService.deleteTag(param);
        return JSONMessage.success();
    }

    /**
	 * @api {post} /doctor/getIntro 获取个人介绍
	 * @apiVersion 1.0.0
	 * @apiName getIntro
	 * @apiGroup 医生
	 * @apiDescription 医生获取个人介绍
	 *
	 * @apiParam {String} 		access_token 	token
	 * @apiParam {Integer} 		userId 			医生的ID
	 * @apiParam {Integer} 		packType 		获取套餐类型（-1：在线门诊（默认）。1：图文咨询。2：电话咨询。3：健康关怀）
	 *
	 * @apiSuccess {String} 	introduction 	个人介绍
	 * @apiSuccess {String} 	skill 			专业特长
	 * @apiSuccess {String}     scholarship     学识成就
	 * @apiSuccess {String}     experience      社会任职
	 * @apiSuccess {DiseaseType[]} expertise 	专长
	 * @apiSuccess {String} 	expertise.id 	病种id
	 * @apiSuccess {String} 	expertise.name 	病种名
	 * @apiSuccess {String} 	expertise.department 科室id
	 * @apiSuccess {String} 	onLineState 	在线状态（1在线、2离线）
	 * @apiSuccess {Integer} 	price 			门诊价格
	 * @apiSuccess {Boolean} 	isFree 			是否收费（true：免费，false：收费）
     * @apiSuccess {Integer} 	role 			角色	    1 医生 2 护士
     * @apiSuccess {Integer} 	cureNum 		治愈人数	
     * @apiSuccess {Object[]}   care			健康关怀套餐
     * @apiSuccess {String}     care.name		健康关怀名称
     * @apiSuccess {String}     care.price      健康关怀名称
     * @apiSuccess {Integer}     timeLimit     	服务时长（以分钟为单位）
     * @apiSuccess {Integer}     replyCount     消息回复次数
     * @apiSuccess {Object[]}   care.doctoreRatios      健康关怀的医生列表
     * @apiSuccess    {String}    care.doctoreRatiodo.ctoreRatios.doctorePic   医生头像
     * @apiSuccess    {String}    care.doctoreRatiodo.ctoreRatios.doctoreName  医生名称
     * @apiSuccess    {String}    care.doctoreRatiodo.ctoreRatios.ratioNum     分成比例
     * @apiSuccess    {Boolean}   care.doctoreRatiodo.ctoreRatios.receiveRemind     是否接收提醒（1接收提醒、0否）
     * @apiSuccess    {String}    care.doctoreRatiodo.ctoreRatios.groupType           类型1：主医生，默认为0 
	 *
	 * @apiAuthor 范鹏
	 * @date 2015年7月2日
	 */
    @RequestMapping("/getIntro")
	public JSONMessage getIntro( @RequestParam(name="userId") Integer userId,
			@RequestParam(name="packType", defaultValue = "-1")Integer packType) {
        
        if(Objects.isNull(userId)){
            userId = ReqUtil.instance.getUserId();
        }
        
        Map<String, Object> data = doctorService.getIntro(userId);
		if (Objects.nonNull(data)) {
		    OutpatientVO vo = groupDoctorService.getOutpatientInfo(userId);
			data.put("onLineState", vo.getOnLineState());
			data.put("isFree", vo.getIsFree());
			
			Pack queryPack = new Pack();
	        queryPack.setDoctorId(userId);
	        queryPack.setPackType(packType);
	        queryPack.setStatus(PackEnum.PackStatus.open.getIndex());
	        List<Pack> packs = packService.queryPack(queryPack);
	        
			if (packType.equals(-1)) {
			    
			    if (Objects.nonNull(vo) && Objects.nonNull(vo.getIsFree()) && vo.getIsFree()) {
			        data.put("price", 0l);
                } else {
				    data.put("price", vo.getPrice());
			    }
			    
			} else if (packType.equals(1) || packType.equals(2)) {
			    
				if (CollectionUtils.isNotEmpty(packs)) {
					for(Pack pack : packs) {
						if (pack.getStatus().equals(PackEnum.PackStatus.open.getIndex())) {
						    
							data.put("price", pack.getPrice());
							data.put("timeLimit", pack.getTimeLimit());
							data.put("packId", pack.getId());
							data.put("replyCount", pack.getReplyCount());
						}
					}
				} else {
					data.put("price", 0l);
				}
				
			} else if (packType.equals(3)) {
			    
				List<CareSimpleVO> careSimpleVOs = Lists.newArrayList();
				if (CollectionUtils.isNotEmpty(packs)) {
					for(Pack pack : packs) {
					    
						CareSimpleVO careSimpleVO = new CareSimpleVO();
						careSimpleVO.setPackId(pack.getId());
						careSimpleVO.setName(pack.getName());
						careSimpleVO.setPrice(pack.getPrice());
						
						List<DoctoreRatioVO> vos = packDoctorService.getDoctorRatiosByPack(pack.getId(), pack.getDoctorId());
						careSimpleVO.setDoctoreRatios(vos);
						
						careSimpleVOs.add(careSimpleVO);
					}
					
				}
				data.put("care", careSimpleVOs);
			}
		}
		
		return JSONMessage.success(null, data);
	}
    
    @Resource
    protected IPackDoctorService packDoctorService;

    /**
     * @api {post} /doctor/setIntro 设置个人介绍
     * @apiVersion 1.0.0
     * @apiName setIntro
     * @apiGroup 医生
     * @apiDescription 医生设置个人介绍
     *
     * @apiParam   {String}          access_token     token
     * @apiParam   {String{..200}}   introduction     个人介绍
     *
     * @apiSuccess {Number=1}        resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/setIntro")
    public JSONMessage setIntro(String introduction) throws HttpApiException {
        doctorService.updateIntro(introduction);
        return JSONMessage.success();
    }

    /**
     * @api {post} /doctor/setSkill 专业特长
     * @apiVersion 1.0.0
     * @apiName setSkill
     * @apiGroup 医生
     * @apiDescription 医生设置擅长领域
     *
     * @apiParam   {String}          access_token     token
     * @apiParam   {String{..4000}}   skill            擅长领域
     *
     * @apiSuccess {Number=1}        resultCode       返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/setSkill")
    public JSONMessage setSkill(String skill) throws HttpApiException {
        doctorService.updateSkill(skill);
        Integer userId=ReqUtil.instance.getUserId();
        userManager.userInfoChangeNotify(userId);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /doctor/setScholarship 设置学术成就
     * @apiVersion 1.0.0
     * @apiName setScholarship
     * @apiGroup 医生
     * @apiDescription 设置医生学术成就
     *
     * @apiParam   {String}          access_token     token
     * @apiParam   {String{..4000}}  scholarship      学术成就
     *
     * @apiSuccess {Number=1}        resultCode       返回状态码
     *
     * @apiAuthor  longjh
     * @date 2017/09/06
     */
    @RequestMapping("/setScholarship")
    public JSONMessage setScholarship(String scholarship) throws HttpApiException {
        doctorService.updateScholarship(scholarship);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /doctor/setExperience 社会任职
     * @apiVersion 1.0.0
     * @apiName setExperience
     * @apiGroup 医生
     * @apiDescription 设置医生社会任职
     *
     * @apiParam   {String}          access_token     token
     * @apiParam   {String{..4000}}  experience       社会任职
     *
     * @apiSuccess {Number=1}        resultCode       返回状态码
     *
     * @apiAuthor  longjh
     * @date 2017/09/06
     */
    @RequestMapping("/setExperience")
    public JSONMessage setExperience(String experience) throws HttpApiException {
        doctorService.updateExperience(experience);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /doctor/getWork 获取职业信息
     * @apiVersion 1.0.0
     * @apiName getWork
     * @apiGroup 医生
     * @apiDescription 医生获取职业信息
     *
     * @apiParam   {String}   access_token        token
     * @apiParam   {Integer} userId               用户id 为空时查询的当前登录人的内容
     *
     * @apiSuccess {String}   hospital            医院
     * @apiSuccess {String}   departments         科室
     * @apiSuccess {String}   title               职称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getWork")
    public JSONMessage getWork(Integer userId) {
        userId = userId == null ? ReqUtil.instance.getUserId():userId;
        return JSONMessage.success(null, doctorService.getWork(userId));
    }

    /**
     * @api {post} /doctor/setWork 设置职业信息
     * @apiVersion 1.0.0
     * @apiName setWork
     * @apiGroup 医生
     * @apiDescription 医生设置职业信息,不修改则不传
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     hospital            医院
     * @apiParam   {String}     hospitalId          医院Id，可选,手填时不用传
     * @apiParam   {String}     departments         科室
     * @apiParam   {String}     deptId         		科室Id
     * @apiParam   {String}     title               职称
     * 
     * @apiSuccess {int}     	provinceId              省份Id
     * @apiSuccess {String}     provinceNa              省份名称
     * @apiSuccess {int}     	cityId                  城市Id
     * @apiSuccess {String}     cityName                城市名称
     * @apiParam   {int}     	countyId               	区县Id
     * @apiParam   {String}     countyName              区县名称
     *
     * @apiSuccess {Number=1}   resultCode          返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @Deprecated
    @RequestMapping("/setWork")
    public JSONMessage setWork(DoctorParam param) throws HttpApiException {
        param.setUserId(ReqUtil.instance.getUserId());
        doctorService.updateWork(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /doctor/getCheckInfo 获取认证信息
     * @apiVersion 1.0.0
     * @apiName getCheckInfo
     * @apiGroup 医生
     * @apiDescription 医生获取认证信息
     *
     * @apiParam     {String}     access_token        token
     * 
     * @apiSuccess   {String}     name                姓名
     * @apiSuccess   {String}     status              状态（1.审核通过，2.正在审核，3.审核不通过）
     * @apiSuccess   {String}     hospital            医院
     * @apiSuccess   {String}     departments         科室
     * @apiSuccess   {String}     title               职称
     * @apiSuccess   {String}     remark              审核不通过时备注
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getCheckInfo")
    public JSONMessage getCheckInfo(DoctorParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null,doctorService.getCheckInfo(param));
    }
    
    /**
     * @api {post} /doctor/updateCheckInfo 修改认证信息
     * @apiVersion 1.0.0
     * @apiName updateCheckInfo
     * @apiGroup 医生
     * @apiDescription 医生未认证或认证失败后修改认证信息,不修改则不传。如果没有医院选择，则hospitalId传""
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     name			          姓名
     * @apiParam   {String}     hospital            医院
     * @apiParam   {String}     hospitalId          医院Id（如果没有医院选择，则传""）
     * @apiParam   {String}     departments         科室
     * @apiParam   {String}     deptId         		科室Id
     * @apiParam   {String}     title               职称
     * 
     * @apiSuccess {Number=1}   resultCode          返回状态码
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updateCheckInfo")
    public JSONMessage updateCheckInfo(DoctorParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        doctorService.updateCheckInfo(param);
        return JSONMessage.success();
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(web获取用户提交审核时的原资料)", httpMethod = "GET", response = DoctorVO.class)
    @RequestMapping(value = "/afterCheck/getCheckInfo/{userId}", method = RequestMethod.GET)
    public JSONMessage getCheckInfo(@PathVariable Integer userId) {
        DoctorParam doctorParam = new DoctorParam();
        doctorParam.setUserId(userId);
        return JSONMessage.success(doctorService.getCheckInfo(doctorParam));
    }

    @ApiOperation(value = "获取医生是否有未处理的认证资料修改申请(0:没有;1:有)", httpMethod = "GET", response = JSONMessage.class)
    @RequestMapping(value = "/afterCheck/getCheckInfoStatus", method = RequestMethod.GET)
    public JSONMessage getCheckInfoStatus() {
        return JSONMessage.success(null, doctorService.getCheckInfoStatus(ReqUtil.instance.getUserId()));
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(客户端增加或更新)", httpMethod = "POST", response = JSONMessage.class)
    @RequestMapping(value = "/afterCheck/updateCheckInfo", method = RequestMethod.POST)
    public JSONMessage resetCheckInfo(@RequestBody ResetDoctorInfo param) {
        param.setUserId(ReqUtil.instance.getUserId());
        doctorService.resetCheckInfo(param);
        return JSONMessage.success();
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(web获取列表)", httpMethod = "POST", response = DoctorRecheckInfoVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "infoStatus", value = "信息状态", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "医生姓名", dataType = "String")})
    @RequestMapping(value = "/afterCheck/getCheckInfo", method = RequestMethod.POST)
    public JSONMessage getCheckInfo(@RequestBody GetRecheckInfo getRecheckInfo) {
        return JSONMessage.success(doctorService.getAfterCheckInfo(getRecheckInfo));
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(web获取详情)", httpMethod = "GET", response = DoctorDealingInfoVO.class)
    @RequestMapping(value = "/afterCheck/getCheckInfoDetail/{id}", method = RequestMethod.GET)
    public JSONMessage getCheckInfoDetail(@PathVariable String id) {
        return JSONMessage.success(doctorService.getCheckInfoDetail(id));
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(客户端获取详情)", httpMethod = "GET", response = DoctorRecheckInfoDetailVO.class)
    @RequestMapping(value = "/afterCheck/getDealingInfo", method = RequestMethod.GET)
    public JSONMessage getCheckInfoDetail() {
        return JSONMessage.success(doctorService.getDealingInfo());
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(web管理员处理结果)", httpMethod = "POST", response = JSONMessage.class)
    @RequestMapping(value = "/afterCheck/handleCheckInfo", method = RequestMethod.POST)
    public JSONMessage handleCheckInfo(@RequestBody ResetDoctorInfo resetDoctorInfo) {
        doctorService.handleCheckInfo(resetDoctorInfo);
        if (Objects.equals(resetDoctorInfo.getVerifyResult(), DoctorInfoChangeEnum.VerifyResult.agree.getIndex()) && !CollectionUtils.isEmpty(resetDoctorInfo.getCheckImage())) {
            String[] images = resetDoctorInfo.getCheckImage().toArray(new String[0]);
            imageDataService.addDoctorImagesCover(resetDoctorInfo.getUserId(), images);
        }
        /* 审核通过强制退出 */
        if (Objects.equals(resetDoctorInfo.getVerifyResult(), DoctorInfoChangeEnum.VerifyResult.agree.getIndex())) {
            auth2Helper.invalidToken(resetDoctorInfo.getUserId());
        }
        return JSONMessage.success();
    }

    @ApiOperation(value = "已认证的医生申请修改认证资料(web获取待处理数量)", httpMethod = "GET", response = JSONMessage.class)
    @RequestMapping(value = "/afterCheck/getUncheckCount", method = RequestMethod.GET)
    public JSONMessage getUncheckCount() {
        return JSONMessage.success(null, doctorService.getUncheckInfoCount());
    }

    /**
     * @api {post} /doctor/basicInfo 获取医生基本信息
     * @apiVersion 1.0.0
     * @apiName basicInfo
     * @apiGroup 医生
     * @apiDescription 获取医生基本信息
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {Integer}    doctorId            医生id（为空，则查询当前登录人）
     * @apiParam   {String}     groupId             集团id（可为空）
     * 
     * @apiSuccess {String}     userId              医生id
     * @apiSuccess {String}     name                姓名
     * @apiSuccess {String}     doctorNum           医生号
     * @apiSuccess {Integer}    isConsultationMember  1:是，0：否 
	 * @apiSuccess {Integer}    consultationPrice       会诊价格
	 * @apiSuccess {String}     consultationRequired    会诊要求
     * @apiSuccess {String}     headPicFileName     头像
     * @apiSuccess {String}     hospital            医院
     * @apiSuccess {String}     departments         科室
     * @apiSuccess {String}     title               职称
     * @apiSuccess {String}     groupId           	集团Id
     * @apiSuccess {String}     groupName           集团名称
     * @apiSuccess {String}     contactWay          集团联系方式
     * @apiSuccess {String}     departmentId        组织Id
     * @apiSuccess {String}     departmentName      组织名称
     * @apiSuccess {Integer}    settings.doctorVerify	好友添加验证
     * @apiSuccess {String}     certStatus      	P已认证，其它非认证
     * @apiSuccess {String}     is3A      			"1":三甲；"0"：非三甲
     *
     * @apiAuthor  范鹏
     * @date 2015年9月11日
     */
    @RequestMapping("/basicInfo")
    public JSONMessage basicInfo(Integer doctorId, String groupId) {
        if(doctorId == null){
            doctorId = ReqUtil.instance.getUserId();
        }
        BaseUserVO vo = baseUserService.getUser(doctorId);
		if (vo != null) {
			
			HospitalVO hospital = baseDataDao.getHospital(vo.getHospitalId());
			if (hospital!= null && hospital.getLevel() != null && hospital.getLevel().equals(HospitalLevelEnum.Three3.getAlias())) {
				vo.setIs3A("1");
			} else {
				vo.setIs3A("0");
			}
			
			BaseUserVO tempvo = baseUserService.getGroupById(vo.getUserId(), groupId);
			if (tempvo != null) {
				// 过滤 屏蔽的集团 add by tanyf 20160616
				Group group = groupService.getGroupById(tempvo.getGroupId());
				if (!GroupSkipStatus.skip.getIndex().equals(group.getSkip())){
					vo.setGroupId(tempvo.getGroupId());
					vo.setGroupName(tempvo.getGroupName());
					vo.setContactWay(tempvo.getContactWay());
					vo.setCertStatus(tempvo.getCertStatus());
				}
			}
			tempvo = baseUserService.getDepartment(vo.getUserId(), vo.getGroupId());
			if (tempvo != null) {
				vo.setDepartmentId(tempvo.getDepartmentId());
				vo.setDepartmentName(tempvo.getDepartmentName());
			}
			if(UserEnum.UserType.doctor.getIndex() == vo.getUserType()){
	    		Pack pack = packService.getDoctorPackByType(doctorId, PackEnum.PackType.consultation.getIndex());
				if(pack != null){
					vo.setConsultationPrice(pack.getPrice().intValue());
					vo.setConsultationRequired(pack.getDescription());
					vo.setIsConsultationMember(1);
				}else{
					vo.setIsConsultationMember(0);
				}
	    	}
			
			vo.setDoctorPageURL(UserUtil.DOCTOR_PAGE_URL() + doctorId + "/0");
			vo.setDoctorQrCodeURL(qrcodeService.generateUserQr(doctorId+"", "3"));
		}
        
        return JSONMessage.success(null,vo);
    }
    
    /**
     * @api {post} /doctor/getDoctorInfoDetails 获取集团的医生详情
     * @apiVersion 1.0.0
     * @apiName getDoctorInfoDetails
     * @apiGroup 医生
     * @apiDescription 获取集团的医生详情
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     groupId             集团Id
     * @apiParam   {int}        doctorId            医生Id
     * 
     * @apiSuccess {DoctorInfoDetailsVO}     	data              		返回结果对象
     * @apiSuccess {User} 		    			data.user		    	User对象[status 审核状态 1, "正常" 2, "待审核" 3，"审核未通过"，7, "未认证"]
     * @apiSuccess {Group} 			   			data.group		    	Group对象
     * @apiSuccess {GroupDoctor}    			data.groupDoctor    	GroupDoctor对象
     * @apiSuccess {GroupProfitVO} 				data.groupProfit		GroupProfitVO对象
     * @apiSuccess {InviteRelation} 			data.inviteRelation		InviteRelation对象[name 邀请人姓名]
     * @apiSuccess {DepartmentDoctor} 			data.departmentDoctor	DepartmentDoctor对象
     * @apiSuccess {String} 					data.departmentFullName	组织全称
     *
     * @apiAuthor  范鹏
     * @date 2015年9月11日
     */
    @RequestMapping("/getDoctorInfoDetails")
    public JSONMessage getDoctorInfoDetails(String groupId, int doctorId) {
  	
    	DoctorInfoDetailsVO doctorInfoDetailsVO = groupDoctorService.getDoctorInfoDetails(groupId, doctorId);
    	packService.setPackForDoctorVO(doctorInfoDetailsVO);
        return JSONMessage.success(null, doctorInfoDetailsVO);
    }
    
    /**
     * @api {post} /doctor/search 医生查找
     * @apiVersion 1.0.0
     * @apiName search
     * @apiGroup 医生
     * @apiDescription 医生查找
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     doctorNum           医生号
     * @apiParam   {String}     telephone          	电话号码

     * 
	 * @apiSuccess {Integer}   hasGroup          是否在集团内1是，2否
     * @apiSuccess {User}      user				   用户
     *
     * @apiAuthor  李淼淼
     * @date 2015年7月2日
     */
    @RequestMapping("/search")
    public JSONMessage findDoctor(DoctorParam param) {
    	if(StringUtil.isEmpty(param.getDoctorNum())&&StringUtil.isEmpty(param.getTelephone())){
    		throw new ServiceException(20005, "医生号 /手机号不能全空");
    	}
    	param.setUserId(ReqUtil.instance.getUserId());
    	Object data=doctorService.search(param);
        return JSONMessage.success(null,data);
    }
    
    /**
     * @api {post} /doctor/searchs 医生查找(多条件，返回数组)
     * @apiVersion 1.0.0
     * @apiName searchs
     * @apiGroup 医生
     * @apiDescription 医生查找
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     keyWord           	查询关键字/目前会根据医生号/手机号查找

     * 
	 * @apiSuccess {Integer}   hasGroup          是否在集团内1是，2否
     *@apiSuccess {User}   	   user				  用户
     *
     * @apiAuthor  李淼淼
     * @date 2015年7月2日
     */
    @RequestMapping("/searchs")
    public JSONMessage findDoctors(DoctorParam param) {
    	if(StringUtil.isEmpty(param.getKeyWord())){
    		throw new ServiceException(20005, "keyWord不能为空");
    	}
    	param.setUserId(ReqUtil.instance.getUserId());
    	Object data=doctorService.searchs(param);
        return JSONMessage.success(null,data);
    }
    
    /**
     * 
     * @api 			{[get,post]} 					/doctor/setExpertise   设置专长
     * @apiVersion 		1.0.0
     * @apiName 		setExpertise	
     * @apiGroup 		专长	
     * @apiDescription 	专长设置(新增时需要带上原有专长)
     * @apiParam  		{String}    					access_token         		 凭证
     * @apiParam  		{String[]}    					expertises         		 	病种id
     * 

     * @apiAuthor 		李淼淼
     * @author 			李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/setExpertise")
    public JSONMessage setExpertise(String expertises[]){
    	Integer userId=ReqUtil.instance.getUserId();
    	doctorService.updateExpertise(userId, expertises);
        userManager.userInfoChangeNotify(userId);
    	return JSONMessage.success();
    }
    
    /**
     * 
     * @api 			{[get,post]} 					/doctor/deleteExpertise		专长删除
     * @apiVersion 		1.0.0
     * @apiName 		deleteExpertise	
     * @apiGroup 		专长
     * @apiDescription 	专长删除
     * @apiParam  		{String}    					access_token         		 凭证
     * @apiParam  		{String[]}    					expertises         		 	病种id
     * 

     * @apiAuthor 		李淼淼
     * @author 			李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/deleteExpertise")
    public JSONMessage deleteExpertise(String expertises[]){
    	int userId= ReqUtil.instance.getUserId();
    	doctorService.deleteExpertise(userId, expertises);
    	return JSONMessage.success();
    }
    
    /**
     * 
     * @api 			{[get,post]} 					/doctor/getExpertise					专长获取
     * @apiVersion 		1.0.0
     * @apiName 		getExpertise	
     * @apiGroup 		专长
     * @apiDescription 	专长获取
     * @apiParam  		{String}    					access_token         		 凭证
     * 
     * 
 	 * @apiSuccess 		{int} 							id 		病种id 
	 * @apiSuccess 		{int} 							name 						病种名称
     * @apiAuthor 		李淼淼
     * @author 			李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/getExpertise")
    public JSONMessage getExpertise(){
    	int userId= ReqUtil.instance.getUserId();
    	Object data=doctorService.getExpertise(userId);
    	return JSONMessage.success(null,data);
    }
    

	/**
     * @api {post/get} /doctor/updateMsgDisturb 修改医生的免打扰开关
     * @apiVersion 1.0.0
     * @apiName updateMsgDisturb
     * @apiGroup 医生
     * @apiDescription 修改医生的免打扰开关
     *
     * @apiParam   {String}    access_token                  token
     * @apiParam   {Integer}   doctorUserId                医生ID
     * @apiParam   {String}   troubleFree                     1关闭,2开启
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/updateMsgDisturb")
	public JSONMessage updateMsgDisturb(int doctorUserId, String troubleFree) {
		// TODO 这个参数doctorUserId可以不要，可以通过token获取userId
		int _doctorUserId = ReqUtil.instance.getUserId();
		doctorService.updateMsgDisturb(_doctorUserId, troubleFree);
		return JSONMessage.success();
	}
	 /**
     * @api {post} /doctor/searchDoctorList  根据医生姓名查询制定医院下面的搜有的医生(分页，返回数组)
     * @apiVersion 1.0.0
     * @apiName searchs
     * @apiGroup 医生
     * @apiDescription 根据医生姓名查询制定医院下面的搜有的医生(分页，返回数组)
     *
     * @apiParam   {String}     access_token        token
     * @apiParam   {String}     doctorName           	医生姓名
     * @apiParam   {String}     hospitalId           	医院id
     * @apiParam   {Integer}     pageIndex           	页数  从第一页开始
     * @apiParam   {Integer}     pageSize           	每页大小 默认15条

     * 
	 *@apiSuccess {Integer}   headPicFileName          头像
	 *@apiSuccess {Integer}   name                     姓名
	 *@apiSuccess {Integer}   userId                   医生id 
	 *@apiSuccess {Integer}   doctor                   医生对象
	 *@apiSuccess {Integer}   doctor.deptId            科室id
	 *@apiSuccess {Integer}   doctor.departments       科室
	 *@apiSuccess {Integer}   doctor.hospitalId        医院id
	 *@apiSuccess {Integer}   doctor.hospital          医院名称
	 *@apiSuccess {Integer}   doctor.title             职称

	 *@apiSuccess {Integer}   total                    总记录
	 *@apiSuccess {Integer}   pageSize                 每页大小
	 *@apiSuccess {Integer}   pageIndex                第几页

     * @apiAuthor  李淼淼
     * @date 2015年7月2日
     */
    @RequestMapping("/searchDoctorList")
    public JSONMessage findDoctors(String doctorName,String hospitalId,Integer pageIndex, Integer pageSize ) {
    	if(StringUtil.isEmpty(hospitalId)){
    		throw new ServiceException(20005, "hospitalId不能为空");
    	}
    	Object data=doctorService.researchDoctors(doctorName, hospitalId, pageIndex, pageSize);
        return JSONMessage.success(null,data);
    }
    
    /**
	 * @api {get} /doctor/findDoctorsByCondition 模糊查找医生
	 * @apiVersion 1.0.0
	 * @apiName findDoctorsByCondition
	 * @apiGroup 医生
	 * @apiDescription 模糊查找医生
	 * @apiParam {String} access_token token
	 * @apiParam {String} keyWord 搜索关键字
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月28日13:38:54
	 */
    @RequestMapping(value = "/findDoctorsByCondition")
    public JSONMessage findDoctorsByCondition(String keyWord,
        @RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "20") Integer pageSize, String keyword) {
        DoctorParam param = new DoctorParam();
        if (StringUtil.isNotEmpty(keyWord)) {
            param.setKeyWord(keyWord);
        } else {
            param.setKeyWord(keyword);
        }
        param.setPageIndex(pageIndex == null ? param.getPageIndex() : pageIndex);
        param.setPageSize(pageSize == null ? param.getPageSize() : pageSize);
        return JSONMessage.success(userManager.findDoctorsByCondition(param));
    }

    /**
     * @api {get} /doctor/findDoctorsByCondition 模糊查找医生,带过滤条件
     * @apiVersion 1.0.0
     * @apiName findDoctorsByCondition
     * @apiGroup 医生
     * @apiDescription 模糊查找医生
     * @apiParam {String} access_token token
     * @apiParam {String} keyWord 搜索关键字
     * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
     * @apiParam {Integer} pageSize 每页的大小
     *
     * @apiSuccess {Integer} pageCount 总页数
     * @apiSuccess {Integer} pageIndex 页数
     * @apiSuccess {Integer} pageSize 每页的大小
     * @apiSuccess {Integer} total 总记录数
     * @apiSuccess {Object[]} pageData 数据集合
     * @apiAuthor 姜宏杰
     * @date 2016年3月28日13:38:54
     */
    @RequestMapping(value = "/findDoctorsByParamCondition")
    public JSONMessage findDoctorsByParamCondition(String keyWord,
                                                   @RequestParam(defaultValue = "0") Integer pageIndex,
                                                   @RequestParam(defaultValue = "20") Integer pageSize,
                                                   @RequestParam List<String> phones) {
        DoctorParam param = new DoctorParam();
        param.setKeyWord(keyWord);
        param.setPageIndex(pageIndex == null ? param.getPageIndex() : pageIndex);
        param.setPageSize(pageSize == null ? param.getPageSize() : pageSize);
        return JSONMessage.success(userManager.findDoctorsByParamCondition(param,phones));
    }

    @RequestMapping("searchByName")
    public JSONMessage findDoctorsByName(String keyword, @RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "20") Integer pageSize) {
        return JSONMessage.success(null, userManager.findDoctorsByName(keyword, pageIndex, pageSize));

    }


    /**
     * @api {get} doctor/findHospitalByUserId 获取医院信息根据userId
     * @apiVersion  1.0.0
     * @apiName findHospitalByUserId
     * @apiGroup 医生
     * @apiParam {String} access_token token
     * @apiDescription 获取医院信息根据userId
     *
     * @apiAuthor 李敏
     * @data 2017年10月26日14:39:12
     */
    @RequestMapping(value = "/findHospitalByUserId")
    public JSONMessage findHospitalByUserId(){
        int userId = ReqUtil.instance.getUserId();
        User user = userManager.getUser(userId);
        List<HospitalVO> hospitalByName=new ArrayList<>();
        if(user.getDoctor()!=null){
            hospitalByName = userManager.findHospitalByName(user.getDoctor().getHospital());
        }

        return JSONMessage.success(hospitalByName.size()>0?hospitalByName.get(0):null);
    }

}
