package com.dachen.health.controller.im;

import java.util.ArrayList;
import java.util.List;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.im.service.IMsgFacadeService;
import com.dachen.health.msg.entity.param.BusinessParam;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.polling.service.IPollingService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.GroupListRequestMessage;
import com.dachen.im.server.data.request.MsgListRequestMessage;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.enums.MsgTypeEnum;

@RestController
@RequestMapping("/im/msg")
public class MsgController {

    @Autowired
    private IMsgService msgService;

    @Autowired
    private IPollingService pollingService;
    
    @Autowired
    private IMsgFacadeService msgFacadeService;
    
    
	/**
     * @api {post} /im/msg/send 发送消息
     * @apiVersion 1.0.0
     * @apiName send
     * @apiGroup 消息
     * @apiDescription 发送消息使用, 根据消息类型不同，填充对应的消息字段
     * @apiParam {String}    access_token  token
     * @apiParam {int}       type          消息类型:1=文本，2=图片，3=语音消息，10=提醒 ，12=事件，13=文本链接，14=多图文
     * @apiParam {String}    fromUserId    发送者id
     * @apiParam {String}    clientMsgId    客户端生成的消息Id（发送的时候需要传入）
     * @apiParam {String}    gid           组id,固定组-待办列表"GROUP_0001"
     * @apiParam {String}    toUserId      接收者id（多人用 | 分隔，只有固定组有效）
     * @apiParam {int}       direction     会话方向：0—别人发送给我的, 1—自己发送的(发送的时候不传)
     * @apiParam {int}       msgId       	消息id(发送的时候不传)
     * @apiParam {long}      ts      	          消息时间戳(发送的时候不传)
     * @apiParam {String}    content      	文本消息内容(或提示内容)
     * @apiParam {String}    uri         	uri地址
     * @apiParam {String}    name          文件名称(2=图片，3=语音消息)
     * @apiParam {int}       width         图片宽带
     * @apiParam {int}       height        图片高度
     * @apiParam {String}    size         	大小
     * @apiParam {String}    time         	时长
     * @apiParam {Map}       param      	参数，事件等消息附加
     * @apiParam {List}      mpt  			多图文内容，可多条
     * @apiParam {ImgTextMsg}  	mpt.ImgTextMsg 图文内容明细
     * @apiParam {int}   		mpt.ImgTextMsg.style 样式，1=标题 ;2=正文+小图 ；3=标题+大图（背景图）;4=正文+大图；5=详情title+url；6=订单通知 ;7=患者报道
     * @apiParam {String}      	mpt.ImgTextMsg.title 标题,<18字符*2行
     * @apiParam {String}       mpt.ImgTextMsg.pic 图片文件
     * @apiParam {Long}       	mpt.ImgTextMsg.time 时间
     * @apiParam {String}       mpt.ImgTextMsg.remark 附加内容,<20字符*8行
     * @apiParam {String}       mpt.ImgTextMsg.content 具体内容HTML，<1万字符去除JS.优先打开
     * @apiParam {String}       mpt.ImgTextMsg.uri 链接地址,没有content则点击打开链接
     * @apiParam {Order}        mpt.ImgTextMsg.order  			订单对象
     * @apiParam {String}       mpt.ImgTextMsg.order.details    套餐详情
     * @apiParam {String}       mpt.ImgTextMsg.order.payment    支付方式
     * @apiParam {String}       mpt.ImgTextMsg.order.number     交易订单号
     * @apiParam {String}       mpt.ImgTextMsg.order.money   	支付金额
     * @apiSuccess {String}   gid           组id
     * @apiSuccess {String}   mid           消息id
     * @apiSuccess {long}     time          消息发送时间
     * @apiAuthor 屈军利
     * @date 2015年8月10日
     */

    @RequestMapping(value = "/send")
    public JSONMessage sendMsg(MessageVO msg) throws HttpApiException {
    	if(msg.getType()==MsgTypeEnum.TEXT_IMG.getValue() && msg.getMpt()==null)
    	{
    		String content = msg.getContent();
    		try
    		{
    			ImgTextMsg obj = JSON.parseObject(content, ImgTextMsg.class);
    			List<ImgTextMsg> mpt = new ArrayList<>();
    			mpt.add(obj);
    			msg.setMpt(mpt);
    			msg.setContent(null);
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
        Object data = pollingService.sendMsg(msg);
        return JSONMessage.success(null, data);
    }
    
  
    
	/**
     * @api {post} /im/msg/sendOvertimeMsg 发送超时消息
     * @apiVersion 1.0.0
     * @apiName sendOvertimeMsg
     * @apiGroup 消息
     * @apiDescription  给某个订单发送超时提醒，以便更新会话列表的会话状态（从咨询中改为超时）
     * @apiParam {String}    access_token  token
     * @apiParam {String}    gid           组id
    
     * @apiAuthor 屈军利
     * @date 2015年8月10日
     */
    @RequestMapping(value = "/sendOvertimeMsg")
    public JSONMessage sendOvertimeMsg( String gid) throws HttpApiException {
    	pollingService.sendOvertimeMsg(gid);
        return JSONMessage.success("成功");
    }


    /**
     * @api {get} /im/msg/get 获取消息
     * @apiVersion 1.0.0
     * @apiName get
     * @apiGroup 消息
     * @apiDescription 收取某个会话中的消息(该接口准备废弃，请直接调用IM提供的接口)
     * @apiParam {String}     access_token         token
     * @apiParam {String}     userId               用户id
     * @apiParam {String}     groupId              会话组id
     * @apiParam {int}        type       			消息类型：0新消息,1旧消息
     * @apiParam {String}     msgId       			基准消息id,查新消息传入最后一条,查旧消息传入最早一条,第一次传空
     * @apiParam {int}        cnt       			更新个数
     * @apiSuccess {int}     		 count             	        更新消息个数
     * @apiSuccess {String}    	 groupId        	        会话id
     * @apiSuccess {boolean}   	 more                  是否还有消息
     * @apiSuccess {long}    	     tms    	                            延迟时间(Millis),下一次发起更新时间=ts+tms
     * @apiSuccess {List}           msgList               消息列表
     * @apiSuccess {MessageVO}      msgList.msg           具体消息（具体结构见http://192.168.3.7/apidoc/#api-消息-send 的Message结构。
     * @apiSuccess {Map}           bussiness              业务参数
     * @apiSuccess {long}          bussiness.leftTime     剩余时长（开始服务之后才有）
     * @apiSuccess {long}          bussiness.duraTime     服务时长（结束服务后才有）
     * @apiSuccess {Integer}       bussiness.orderStatus    订单状态（http://192.168.3.7:8091/apidoc/#api-订单-findOrders）
     * @apiSuccess {Integer}       bussiness.sessionStatus    会话状态      在订单状态（http://192.168.3.7:8091/apidoc/#api-订单-findOrders）基础上增加      服务中：15，服务超时：16，人工取消：17；后台自动取消：18，等待队列中：19，咨询队列中：20
 	 * @apiSuccess {long}          bussiness.appointTime     预约时间（预约时间之后才有）
 	 * @apiSuccess {Integer}       bussiness.cancelFrom     取消人，0 是系统取消代表晚上12后取消，1代表门诊订单超时患者未开始咨询时系统取消，其它对应接口调用者
 	 * @apiSuccess {long}          bussiness.waitCount      当前等待人数
 	 * @apiSuccess {Integer}       bussiness.orderType      订单类型（1：套餐订单；2：报到；3：门诊订单）
 	 * @apiSuccess {long}          bussiness.treatWaitTime   门诊接单开始剩余时间
 	 * @apiSuccess {long}          bussiness.price   		  订单价格
 	 * 

     * @apiAuthor 屈军利
     * @date 2015年8月10日
     */
    @RequestMapping("/get")
    public JSONMessage getMsg(MsgListRequestMessage msgGetParam) {
        Object data = pollingService.getMsg(msgGetParam);
        return JSONMessage.success(null, data);
//    	return JSONMessage.failure("该接口已经废弃，请使用新的接口。");
    }

    /**
     * @api {get} /im/msg/getBusiness 业务轮询((该接口准备废弃，请直接调用IM提供的接口)）
     * @apiVersion 1.0.0
     * @apiName getBusiness
     * @apiGroup 消息
     * @apiDescription 业务轮询接口，目前获取会话列表+我收到的验证请求
     * @apiParam {String}            access_token          token
     * @apiParam {String}       	  userId        		用户id
     * @apiParam {long}      	      ts         			时间戳，上次更新时间，第一次传0
     * @apiParam {int}      	      cnt        			更新个数
     * @apiSuccess {Object}   		 	msgGroupVO          返回会话列表对象
     * @apiSuccess {long}    	 		msgGroupVO.ts    	        时间戳
     * @apiSuccess {long}    	 		msgGroupVO.tms    	        延迟时间(Millis),下一次发起更新时间=ts+tms
     * @apiSuccess {String}    	 		msgGroupVO.more  	        是否还有未更新数据
     * @apiSuccess {MsgGroupList}    	msgGroupVO.patientlist                患者会话组
     * @apiSuccess {int}  				msgGroupVO.patientlist.ur               未读消息数
     * @apiSuccess {int}  				msgGroupVO.patientlist.count            会话组总个数
     * @apiSuccess {List}  				msgGroupVO.patientlist.list            会话组明细列表
     * @apiSuccess {MsgGroupDetail} 	msgGroupVO.patientlist.list.msgGroupDetail           会话明细内容
     * @apiSuccess {String}		  		msgGroupVO.patientlist.list.msgGroupDetail.gid       组id
     * @apiSuccess {String} 		 	msgGroupVO.patientlist.list.msgGroupDetail.gname     组名称，双人会话时显示对方用户名称
     * @apiSuccess {String}   			msgGroupVO.patientlist.list.msgGroupDetail.gpic      组头像，双人会话时显示对方用户头像
     * @apiSuccess {String}  			msgGroupVO.patientlist.list.msgGroupDetail.rtype     会话类型
     * @apiSuccess {int} 			 	msgGroupVO.patientlist.list.msgGroupDetail.type      0:系统通知 1:双人会话；2：多人会话； 3、公众号组 ，6、公共号反馈组
     * @apiSuccess {Msg}  				msgGroupVO.patientlist.list.msgGroupDetail.lastMsg   最新消息（具体结构见http://192.168.3.7/apidoc/#api-消息-send 的Msg结构。
     * @apiSuccess {long} 			 	msgGroupVO.patientlist.list.msgGroupDetail.ts        消息发送时间
     * @apiSuccess {int}  				msgGroupVO.patientlist.list.msgGroupDetail.unread    未读消息数
     * @apiSuccess {List}  		    	msgGroupVO.patientlist.list.msgGroupDetail.userList   同groupInfo接口（type=3时无效）
     * @apiSuccess {Integer}  		    msgGroupVO.patientlist.list.msgGroupDetail.orderStatus   订单状态(OrderStatus,5种值)
     * @apiSuccess {String}  		    msgGroupVO.patientlist.list.msgGroupDetail.packType   套餐类型  2:图文/3:电话
     * @apiSuccess {String}  		    msgGroupVO.patientlist.list.msgGroupDetail.orderId   订单id
     * @apiSuccess {Map}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness              业务参数
     * @apiSuccess {long}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.leftTime     剩余时长（开始服务之后才有）
     * @apiSuccess {long}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.duraTime     服务时长（结束服务后才有）
     * @apiSuccess {Integer}       		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.orderStatus    订单状态（http://192.168.3.7:8091/apidoc/#api-订单-findOrders）
     * @apiSuccess {Integer}       		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.sessionStatus    会话状态      在订单状态（http://192.168.3.7:8091/apidoc/#api-订单-findOrders）基础上增加      服务中：15，服务超时：16，人工取消：17；后台自动取消：18，等待队列中：19，咨询队列中：20
 	 * @apiSuccess {long}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.appointTime     预约时间（预约时间之后才有）
 	 * @apiSuccess {Integer}       		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.cancelFrom     取消人，0 是系统取消代表晚上12后取消，1代表门诊订单超时患者未开始咨询时系统取消，其它对应接口调用者
 	 * @apiSuccess {long}       		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.waitCount      当前等待人数
 	 * @apiSuccess {Integer}       		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.orderType      订单类型（1：套餐订单；2：报到；3：门诊订单）
 	 * @apiSuccess {long}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.treatWaitTime      门诊接单开始剩余时间
 	 * @apiSuccess {long}          		msgGroupVO.patientlist.list.msgGroupDetail.bussiness.price           接单对应的价格
 	 * 
     * @apiSuccess {MsgGroupList}      	msgGroupVO.doctorlist               医生会话组，结构和patientlist相同
     * @apiSuccess {MsgGroupList}      	msgGroupVO.assistantlist            医助会话组，结构和patientlist相同
     * @apiSuccess {MsgGroupList} 		msgGroupVO.notificationGroup      	系统消息组信息，结构和patientlist相同
     * @apiSuccess {MsgGroupList}      	msgGroupVO.customerGroup           	客服会话组，结构和patientlist相同
     * @apiSuccess {EventResult}        event               				指令
     * @apiSuccess {Long}        		event.ts               				轮询时间戳（暂时无用，用的是外面轮询的时间戳）
     * @apiSuccess {List}      			event.list               			指令集合
     * @apiSuccess {EventVO}      		event.list.EventVO               	指令明细内容
     * @apiSuccess {String}        		event.list.EventVO.id               		指令id
     * @apiSuccess {String}        		event.list.EventVO.userId               	指令接收者（接收指令无此参数；发送时使用：多个接收者用|隔开） 
     * @apiSuccess {String}        		event.list.EventVO.eventType               	指令类型 
     * @apiSuccess {Long}        		event.list.EventVO.ts              			指令发送时间 
     * @apiSuccess {Map}                 event.list.EventVO.param              		指令扩展参数
     * @apiSuccess {Map}                business               				业务轮询内容
     * @apiSuccess {int}        		business.newOrder               	新订单个数
     * @apiSuccess {int}        		business.newCheckIn               	新报到个数
     * 
     * @apiSuccess {Long}        		business.totalLineTime              上线总时长（秒）
     * @apiSuccess {Long}        		business.nowLineTime               	当前上线值班时长（秒）
     * @apiAuthor 屈军利
     * @date 2015年8月10日
     */
    @RequestMapping("/getBusiness")
    public JSONMessage getBusiness(BusinessParam businessParam) {
//        Object data = pollingService.getBusiness(businessParam);
//        return JSONMessage.success(null, data);
    	return JSONMessage.failure("该接口已经废弃，请使用新的接口。");
    }


    /**
     * @api {post} /im/msg/createGroup 建立会话组
     * @apiVersion 1.0.0
     * @apiName createGroup
     * @apiGroup 消息
     * @apiDescription 选择好友后，建立会话组。组内后才能发送消息
     * @apiParam {String}     access_token   token
     * @apiParam {String}     fromUserId     建立者id
     * @apiParam {String}     toUserId       接收者id（多人用 | 分隔）
     * @apiParam {String}     gtype          会话类型:（患者-患者=1_1，患者-医生=1_3，医助-医生=2_3，医生-医生=3_3,系统通知=0_0）
     * @apiParam {int} 	   type           1:双人会话；2：多人会话；0:系统通知
     * @apiSuccess {String}   gid             组id
     * @apiSuccess {String}   userIds         用户id列表,type=1:双人会话有效；其它无效
     * @apiSuccess {String}   gname           组名称，双人会话无效（取用户信息）
     * @apiSuccess {String}   gpic            组头像，双人会话无效（取用户信息）
     * @apiAuthor zgl
     * @date 2015年8月20日
     */

    @RequestMapping(value = "/createGroup")
    public JSONMessage createGroup(CreateGroupRequestMessage createGroupParam) {
        Object data = msgService.createGroup(createGroupParam);
        return JSONMessage.success(null, data);
    }


    /**
     * @api {post} /im/msg/updateGroup 修改会话组
     * @apiVersion 1.0.0
     * @apiName updateGroup
     * @apiGroup 消息
     * @apiDescription 增删人员，修改名称图标，清除消息等
     * @apiParam {String}     access_token         token
     * @apiParam {String}     gid             		组id
     * @apiParam {int} 	      act           		1=增人员，2=删人员，3=修改名称，4=修改图标，5=全部消息已读，6=全部消息删除，7=不提醒，8=提醒
     * @apiParam {String}     fromUserId     		建立者id
     * @apiParam {String}     toUserId       		接收者id（增人员多人用 | 分隔，删人员只能是一人，退出=fromUserId）
     * @apiParam {String}     name       			名称或图标（3和4有效）
     * @apiAuthor zgl
     * @date 2015年8月20日
     */
    @RequestMapping(value = "/updateGroup")
    public JSONMessage updateGroup(UpdateGroupRequestMessage request) {
        Object data = msgService.updateGroup(request);
        return JSONMessage.success(null, data);
    }
    
    @RequestMapping(value = "/registerDeviceToken")
    public JSONMessage registerDeviceToken(String deviceToken, String client, Integer userId, boolean invalid) {
        msgService.registerDeviceToken(deviceToken, client, userId, invalid);
        return JSONMessage.success();
    }
    
    @RequestMapping(value = "/updateDeviceToken")
    public JSONMessage updateDeviceToken(String deviceToken) {
        msgService.updateDeviceToken(deviceToken);
        return JSONMessage.success();
    }
    
    @RequestMapping(value = "/removeDeviceToken")
    public JSONMessage removeDeviceToken(String deviceToken) {
        msgService.removeDeviceToken(deviceToken);
        return JSONMessage.success();
    }

    @RequestMapping(value = "/updatePushStatus")
    public JSONMessage updatePushStatus(Integer userId, String deviceToken, boolean invalid) {
        msgService.updatePushStatus(userId, deviceToken, invalid);
        return JSONMessage.success();
    }

    /**
     * @api {get} /im/msg/groupInfo 会话组信息
     * @apiVersion 1.0.0
     * @apiName groupInfo
     * @apiGroup 消息
     * @apiDescription 组信息（type==3时，不返回userList,需要根据公共号Id查询相关信息）
     * @apiParam {String}   access_token           token
     * @apiParam {String}   gid             		组id
     * @apiParam {String}   userId     			用户id
     * @apiSuccess {String}   gid            组id
     * @apiSuccess {String}   rtype          会话类型:（患者-患者=1_1，患者-医生=1_3，医助-医生=2_3，医生-医生=3_3,系统通知=0_0）
     * @apiSuccess {int} 	  type           0:系统通知 1:双人会话；2：多人会话； 3、公众号组 ，6、公共号反馈组
     * @apiSuccess {String}   gname          组名称
     * @apiSuccess {String}   gpic           组头像
     * @apiSuccess {String}   creator        创建用户
     * @apiSuccess {String}   notify         0=不提醒，1=提醒
     * @apiSuccess {List}     userList        用户id列表
     * @apiSuccess {String}   userList.id         用户id
     * @apiSuccess {String}   userList.name       用户名称
     * @apiSuccess {String}   userList.pic        用户图像
     * @apiSuccess {String}   userList.userType   用户类型
     * @apiAuthor zgl
     * @date 2015年8月20日
     */
    @RequestMapping(value = "/groupInfo")
    public JSONMessage groupInfo(GroupInfoRequestMessage request) throws HttpApiException {
        Object data = msgFacadeService.getGroupInfo(request);
        return JSONMessage.success(null, data);
    }
    

    /**
     * @api {get} /im/msg/groupParam 会话组状态及扩展信息(支持批量)
     * @apiVersion 1.0.0
     * @apiName groupParam
     * @apiGroup 消息
     * @apiDescription 组状态及扩展信息 。当传入单个组Id时，返回的是map结构（如下）；如果传入多个组Id，则返回的是list结构，list的每个元素都是一个map
     * @apiParam {String}   access_token            token
     * @apiParam {String}   groupId                 组id（可传多个组Id，逗号分隔）
     * @apiSuccess {String}   groupId            	组id
     * @apiSuccess {String}   bizStatus            	组状态
     * @apiSuccess {Map}      param        			组扩展信息（包含字段同会话组的param）
     * @apiAuthor 成伟
     * @date 2015年8月20日
     */
    @RequestMapping(value = "/groupParam")
    public JSONMessage groupParam(String groupId) throws HttpApiException {
        Object data = msgFacadeService.getGroupParam(groupId);
        return JSONMessage.success(null, data);
    }
    
    /**
     * 服务中：15，服务超时：16，人工取消：17；医后助手取肖：172;后台自动取消：18，等待队列中：19，咨询队列中：20, 图文订单已没有消息回复次数：24
     *
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/groupParams")
    public JSONMessage groupParams(String...groupId) throws HttpApiException {
        Object data = msgFacadeService.getGroupParams(groupId);
        return JSONMessage.success(null, data);
    }

    /**
     * {get} /im/msg/groupList 拉取会话
     * 拉取会话接口（新）详见 http://115.29.172.143:8090/pages/viewpage.action?pageId=918099
     * @return
     */
    @RequestMapping("/groupList")
    public JSONMessage groupList(GroupListRequestMessage requestParam) throws HttpApiException {
    	Object data = msgFacadeService.groupList(requestParam);
        return JSONMessage.success(null, data);
    }
}
