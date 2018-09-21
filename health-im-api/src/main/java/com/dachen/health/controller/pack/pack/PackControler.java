package com.dachen.health.controller.pack.pack;

import java.util.*;

import com.dachen.health.commons.constants.UserEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.GroupEnum.OnLineState;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.param.PackDoctorParam;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.vo.PackVO;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/pack/pack")
public class PackControler {

    @Autowired
    private IPackService packService;
    
    @Autowired
    private IGroupDoctorService gdocService;
   
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private UserManager userManager;
    
	@Autowired
	private IGroupSearchDao groupSearchDao;
   
	/**
	    * @api {post} /pack/pack/findDoctorGroupByPackId 获取套餐的专家组成员用户信息
	    * @apiVersion 1.0.0
	    * @apiName findDoctorGroupByPackId
	    * @apiGroup 服务套餐
	    * @apiDescription 获取套餐的专家组成员用户信息
	    * 
	    * @apiParam      {String}    access_token           	token
	    * @apiParam      {Integer}   packId             		套餐ID
	    * 
	    * @apiSuccess    {String}    groupName         		集团名称
	    * @apiSuccess    {String}    disease         			擅长病种
	    * @apiSuccess    {String}    skill         			擅长
	    * @apiSuccess    {String}    cureNum         			就诊数
	    * @apiSuccess    {String}    doctorId         			医生ID
	    * @apiSuccess    {String}    doctorName         	 	医生名称
	    * @apiSuccess    {String}    headPicFileName           头像图片
	    * @apiSuccess    {String}    departments         		科室
	    * @apiSuccess    {String}    title         			职称
	    * @apiSuccess    {String}    groupType         		类型1：主医生，默认为0   
	    * 
	    * 
	    * @apiAuthor  肖伟
	    * @date 2017年01月10日
	    * 
	    * 此接口从/pack/carePlan/findDoctorGroupByPackId迁移过来，by xiaowei, 170110
	    */
	  @RequestMapping(value = "/findDoctorGroupByPackId")
	  public JSONMessage findDoctorGroupByPackId(@RequestParam(required=true) Integer packId){
		  List<CarePlanDoctorVO> list = packService.findUserInfoByPack(packId);
	  	  return JSONMessage.success(null, list);
	  }
	  
    /**
     * @throws HttpApiException 
     * @api {post} /pack/pack/add 新建套餐
     * @apiVersion 1.0.0
     * @apiName add
     * @apiGroup 服务套餐
     * @apiDescription 新建套餐，不同类型套餐，传递不同的packType和对应内容（免费套餐也是价格为0的消息类套餐）
     *   消息类，通话类 只允许存在一个开启的同类型套餐，并且价格自动调整到所属集团的上/下限
     * @apiParam {String} access_token           token
     * @apiParam {String}  name  套餐名称(关怀计划、随访类不需要传）
     * @apiParam {long}  price  价格 （免费类套餐价格传0）(关怀计划、随访类不需要传）
     * @apiParam {int}   packType  套餐类型：1：消息类，2：通话类 ，3:关怀计划，4：随访
     * @apiParam {String}  description  套餐描述(关怀计划、随访类不需要传）
     * @apiParam {int}      timeLimit  时限，以分钟为单位(关怀计划、随访类不需要传）
     * @apiParam {String}   careTemplateId  关怀计划Id或随访Id
     * @apiParam {String}   groupId  集团id（随访,关怀套餐必传）
     * @apiParam {Integer}  replyCount  聊天问题次数
     * 
     * @apiSuccess {Integer}      packId  套餐id
     * @apiSuccess {Long}      price  套餐价格
     * @apiSuccess {Integer}      replyCount  回复次数

     * @apiAuthor  屈军利
     * @date 2015年8月2日
     * 
     */
    @RequestMapping(value = "/add")
    public JSONMessage addPack(@ModelAttribute Pack pack) throws HttpApiException {
    	pack.setDoctorId(ReqUtil.instance.getUserId());
    	Object data=packService.addPack(pack);
        return JSONMessage.success("添加成功",data);
    }
    
    /**
     * @api {get} /pack/pack/get 获取套餐
     * @apiVersion 1.0.0
     * @apiName get
     * @apiGroup 服务套餐
     * @apiDescription 根据id查询服务套餐
     * 
     * @apiParam {String}    access_token           token
     * @apiParam {Integer}   id                   套餐id
     * 
     * @apiSuccess {Integer}  id  返回套餐id
     * @apiSuccess {Integer}  doctorId  医生id
     * @apiSuccess {String}  name  套餐名称
     * @apiSuccess {long}   price 套餐价格
     * @apiSuccess {int}    packType  套餐类型：1：消息类，2：通话类
     * @apiSuccess {int}    status  套餐状态：1.开通，2：关闭
     * @apiSuccess {String}  description  套餐描述
     * @apiSuccess {int}       timeLimit  时限，以分钟为单位
     * @apiSuccess {String}    Image   套餐图片
     * @apiSuccess {Integer}  doctorCount 医生个数(关怀计划类套餐才有)
     * @apiSuccess {Integer}  isSearched  是否可用1：是，0：否，null:否
     * @apiSuccess {Integer}  replyCount  聊天问题次数

     * @apiAuthor  屈军利
     * @date 2015年8月2日
     */
	@RequestMapping(value = "/get")
	public JSONMessage getPack(@RequestParam Integer id) {
		Object data = packService.getPack(id);
		return JSONMessage.success("成功", data);
	}
   
    /**
     * @api {post} /pack/pack/query 查询套餐
     * @apiVersion 1.0.0
     * @apiName query
     * @apiGroup 服务套餐
     * @apiDescription 查询套餐，根据条件查询套餐
     * 
     * @apiParam {String}  	access_token           token
     * @apiParam {Integer}  doctorId           医生id
     * @apiParam {long}    	status         套餐状态：1：开通，2：关闭，可为空
     * @apiParam {Integer}  isSearched     是否可被搜索1：是，0：否，null:否
     * @apiParam {Integer}  packType     套餐类型（1：消息类，2：通话类 3:关怀计划，8：会诊套餐）
     * @apiParam {String}   groupId  集团id（随访,关怀套餐必传）
     * 
     * @apiSuccess {List} packList  返回符合要求的pack的集合，每个pack的详细结构可参看：/pack/pack/get返回的结构
     * @apiAuthor  屈军利
     * @date 2015年8月2日
     * 
     * checked by xiaowei, 20160914
     */
    @RequestMapping(value = "/query")
    public JSONMessage queryPack(@ModelAttribute Pack pack) {
    	Object data=packService.queryPack(pack);
        return JSONMessage.success("成功",data);
    }
    
    
    /**
     * @throws HttpApiException 
     * @api {post} /pack/pack/getDoctorAppointment 查询预约套餐
     * @apiVersion 1.0.0
     * @apiName getDoctorAppointment
     * @apiGroup 服务套餐
     * @apiDescription 查询预约套餐
     * 
     * @apiParam {String}  	access_token           token
     * @apiParam {Integer}  doctorId           医生id 必传
     * @apiParam {String}   groupId  		        集团id 必传
     * 
     * @apiSuccess {String}   isOpen   "1":开通，"0"：未开通
     * @apiSuccess {Integer}  pack.id  返回套餐id
     * @apiSuccess {Integer}  pack.doctorId  医生id
     * @apiSuccess {String}  pack.name  套餐名称
     * @apiSuccess {long}   pack.price 套餐价格
     * @apiSuccess {int}    pack.packType  套餐类型：1：消息类，2：通话类
     * @apiSuccess {int}    pack.status  套餐状态：1.开通，2：关闭
     * @apiSuccess {String}  pack.description  套餐描述
     * @apiSuccess {int}       pack.timeLimit  时限，以分钟为单位
     * @apiSuccess {String}    pack.Image   套餐图片
     * @apiAuthor  wangl
     * @date 2016年4月29日
     */
    @RequestMapping(value = "/getDoctorAppointment")
    public JSONMessage getDoctorAppointment(@RequestParam(required=true) String groupId ,
    									    @RequestParam(required=true) Integer doctorId
    									    ) throws HttpApiException {
    	return JSONMessage.success(packService.getDoctorAppointment(groupId,doctorId));
    }
    
    /**
     * @api {post} /pack/pack/queryPack 查询套餐
     * @apiVersion 1.0.0
     * @apiName queryPack
     * @apiGroup 服务套餐
     * @apiDescription 查询套餐，根据条件查询套餐（包含在线门诊）
     * 
     * @apiParam {String}  	access_token           token
     * @apiParam {Integer}  doctorId           医生id
     * @apiParam {long}    	status         套餐状态：1：开通，2：关闭，可为空
     * 
     * @apiSuccess {List} packList  返回在线门诊、图文咨询、电话咨询的集合，每个pack的详细结构可参看：/pack/pack/get返回的结构
     * @apiAuthor  谢平
     * @date 2015年12月24日
     */
    @RequestMapping(value = "/queryPack")
    public JSONMessage queryPacks(@ModelAttribute Pack pack) {
    	List<PackVO> data = new ArrayList<PackVO>();
    	//添加在线门诊
    	OutpatientVO outpatient = gdocService.getOutpatientInfo(pack.getDoctorId());
    	if (OnLineState.onLine.getIndex().equals(outpatient.getOnLineState())) {
    		PackVO vo = new PackVO();
    		BeanUtils.copyProperties(outpatient, vo);
    		data.add(vo);
    	}
    	//添加图文咨询、电话咨询
    	List<PackVO> message_phone = packService.queryPack12(pack);
    	if (!message_phone.isEmpty()) {
    		data.addAll(message_phone);
    	}
        
        return JSONMessage.success("成功",data);
    }
    
    /**
     * @api {post} /pack/pack/packInfo 查询套餐
     * @apiVersion 1.0.0
     * @apiName packInfo
     * @apiGroup 根据订单id查询服务套餐
     * @apiDescription 查询套餐，根据条件查询套餐（包含在线门诊）
     * 
     * @apiParam {String}  	access_token           token
     * @apiParam {Integer}  orderId           医生id
     * 
     * @apiSuccess {Integer} doctor.doctorId 医生id
     * @apiSuccess {String} doctor.name 医生姓名
     * @apiSuccess {String} doctor.title 医生职称
     * @apiSuccess {String} doctor.departments 医生科室
     * @apiSuccess {String} doctor.hospital 医生所在的医院
     * @apiSuccess {String} doctor.headPicFileName 医生头像
     * @apiSuccess {List} packList  返回在线门诊、图文咨询、电话咨询的集合，每个pack的详细结构可参看：/pack/pack/get返回的结构
     * @apiAuthor  傅永德
     * @date 2016年9月22日
     */
    @RequestMapping(value = "/packInfo")
    public JSONMessage packInfo(Integer orderId) {
    	
    	Map<String, Object> result = Maps.newHashMap();
    	
    	//先查询订单信息
    	Order order = orderService.getOne(orderId);
    	Integer doctorId = order.getDoctorId();
    	User user = userManager.getUser(doctorId);
    	Doctor doctor = user.getDoctor();
    	Map<String, Object> userMap = Maps.newHashMap();
    	userMap.put("doctorId", doctorId);
    	userMap.put("name", user.getName());
    	userMap.put("title", doctor.getTitle());
    	userMap.put("departments", doctor.getDepartments());
    	userMap.put("hospital", doctor.getHospital());
    	userMap.put("headPicFileName", user.getHeadPicFileName());
    	
    	Pack pack = new Pack();
    	pack.setDoctorId(doctorId);
    	pack.setStatus(PackEnum.PackStatus.open.getIndex());
    	
    	List<PackVO> data = new ArrayList<PackVO>();
    	//添加在线门诊
//    	OutpatientVO outpatient = gdocService.getOutpatientInfo(doctorId);
//    	if (OnLineState.onLine.getIndex().equals(outpatient.getOnLineState())) {
//    		PackVO vo = new PackVO();
//    		BeanUtils.copyProperties(outpatient, vo);
//    		data.add(vo);
//    	}
    	//添加图文咨询、电话咨询
    	List<PackVO> message_phone = packService.queryPack12(pack);
    	if (!message_phone.isEmpty()) {
    		data.addAll(message_phone);
    	}
    	
    	result.put("doctor", userMap);
    	result.put("pack", data);
    	
    	return JSONMessage.success(result);
    }
    
    
    /**
     * @api {post} /pack/pack/getPackInfoByOrderId 查询套餐
     * @apiVersion 1.0.0
     * @apiName getPackInfoByOrderId
     * @apiGroup 根据订单id查询服务套餐
     * @apiDescription 查询套餐，根据条件查询套餐（
     * 
     * @apiParam {String}  	access_token           token
	 * @apiParam {Integer} orderId           orderId
	 * @apiParam {Integer[]} docIds           会话组医生id数组
     * 
     * @apiSuccess {Integer} doctor.doctorId 医生id
     * @apiSuccess {String} doctor.name 医生姓名
     * @apiSuccess {String} doctor.title 医生职称
     * @apiSuccess {String} doctor.departments 医生科室
     * @apiSuccess {String} doctor.hospital 医生所在的医院
     * @apiSuccess {String} doctor.headPicFileName 医生头像
     * @apiSuccess {String} doctor.doctorSkill 医生擅长
     * @apiSuccess {String} doctor.integral 是否开通积分问诊0-未开通 1已开通
     * @apiSuccess {List} packList  返回积分问诊、图文咨询、电话咨询的集合，每个pack的详细结构可参看：/pack/pack/get返回的结构
     * @apiAuthor  李明
     * @date 2016年9月22日
     */
    @RequestMapping(value = "/getPackInfoByOrderId")
    public JSONMessage getPackInfoByOrderId(Integer orderId, Integer[] docIds) {

		if(docIds==null||docIds.length==0){
			throw new ServiceException("docIds不能为空");
		}
		List<Integer> doctorList=new ArrayList<>();
		//校验医生时候为正常的医生（按产品要求，医生被反审核了，不返回套餐信息，但是返回医生的基本信息）
//		for(Integer doctorId:docIds){
//			if(userManager.checkDoctor(doctorId)){
//				doctorList.add(doctorId);
//			}
//		}
		doctorList.addAll(Arrays.asList(docIds));
		List<Map<String, Object>> list = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(doctorList)) {
			mainDocFirst(doctorList, orderId);
			//主医生id
			for (Integer orderDoctor : doctorList) {
				Map<String, Object> result = Maps.newHashMap();
				User user = userManager.getUser(orderDoctor);

				if (Objects.isNull(user)) {
					continue;
				}

				Doctor doctor = user.getDoctor();
				Map<String, Object> userMap = Maps.newHashMap();
				userMap.put("doctorId", orderDoctor);
				userMap.put("name", user.getName());
				userMap.put("title", doctor.getTitle());
				userMap.put("departments", doctor.getDepartments());
				userMap.put("hospital", doctor.getHospital());
				userMap.put("headPicFileName", user.getHeadPicFileName());
				String doctorSkill = "";
				//设置擅长
				List<String> diseaseIds = user.getDoctor().getExpertise();
				if (diseaseIds != null && !diseaseIds.isEmpty()) {
					if (StringUtil.isEmpty(user.getDoctor().getSkill())) {
						//skill为空的话 只单单返回expertise
						doctorSkill = groupSearchDao.getDisease(diseaseIds);
					} else {
						//expertise、skill都不为空则一起返回
						if (StringUtil.isNotEmpty(groupSearchDao.getDisease(diseaseIds))) {
							doctorSkill = groupSearchDao.getDisease(diseaseIds) + "、" + user.getDoctor().getSkill();
						} else {
							doctorSkill = user.getDoctor().getSkill();
						}
					}
				} else {
					doctorSkill = user.getDoctor().getSkill();
				}
				userMap.put("doctorSkill", doctorSkill);
				result.put("doctor", userMap);
				if (Objects.nonNull(user.getUserType()) && user.getUserType().intValue() == UserEnum.UserType.doctor.getIndex() &&
						Objects.nonNull(user.getStatus()) && user.getStatus() == UserEnum.UserStatus.normal.getIndex()) {

					Pack pack = new Pack();
					pack.setDoctorId(orderDoctor);
					pack.setStatus(PackEnum.PackStatus.open.getIndex());

					List<PackVO> data = new ArrayList<PackVO>();
					//添加图文咨询、电话咨询
					List<PackVO> message_phone = packService.queryPack12(pack);

					//检查是否开通积分问诊
					userMap.put("integral", packService.queryPack12Point(pack));
					if (!message_phone.isEmpty()) {
						data.addAll(message_phone);
					}
					result.put("pack", data);
				}

				list.add(result);
			}
		}
		return JSONMessage.success(list);
    }
    private void mainDocFirst(List<Integer> list,Integer orderId){
    	Order order = orderService.getOne(orderId);
    	if(order != null){
    		Integer mainDoc = null;
    		for(Integer docId :list){
    			if(order.getDoctorId().intValue() == docId.intValue()){
    				mainDoc = docId;
    				list.remove(docId);
    				break;
    			}
    		}
    		if(mainDoc != null){
    			list.add(0, mainDoc);
    		}
    	}
    }
    
    /**
     * @api {post} /pack/pack/update 修改套餐
     * @apiVersion 1.0.0
     * @apiName update
     * @apiGroup 服务套餐
     * @apiDescription 修改套餐，包括套餐关闭   价格自动调整到所属集团的上/下限
     * 
     * @apiParam {String}   access_token           token
     * @apiParam {String}  id  套餐id，不能为空
     * @apiParam {String}  name  套餐名称
     * @apiParam {long}  price  价格
     * @apiParam {Integer}    status  套餐状态：1：开通，2：关闭
     * @apiParam {Integer}    isSearched 是否可用1：是，0：否（可不传）
     * @apiParam {String}   description  套餐描述
     * @apiParam {Integer}      timeLimit  时限，以分钟为单位
     * @apiParam {Integer}      helpTimes  求助次数
     * @apiParam {Integer}      replyCount  赠送咨询次数
     * @apiParam {Integer}      ifLeaveMessage  是否开启答题留言功能，1表示开启，0表示未开启
     * @apiParam {Integer}      replyCount  聊天回复次数
     * 
     * @apiSuccess {Map}      price  map中包含price
     * 
     * @apiAuthor  屈军利
     * @date 2015年8月2日
     */
    @RequestMapping(value = "/update")
    public JSONMessage updatePack(@ModelAttribute Pack pack) {
    	pack.setDoctorId(ReqUtil.instance.getUserId());
    	Object data= packService.updatePack(pack);
//	    return JSONMessage.success();
        return JSONMessage.success("修改成功",data);
    }
    
   
    /**
     * @api {post} /pack/pack/savePackDoctor 设置关怀计划医生分成
     * @apiVersion 1.0.0
     * @apiName savePackDoctor
     * @apiGroup 服务套餐
     * @apiDescription  给关怀计划设置医生分成信息
     *
     * @apiExample {javascript} Example usage:
     *  传递一个参数data，以json格式，如下：
     *  String data="{'packId':'123','packDoctorList':[{'doctorId':'1','splitRatio':'85','receiveRemind':'1或0,1為接收提醒'},
     *                                                 {'doctorId':'2','splitRatio':'15','receiveRemind':'1或0'}]}";
     *  
     * json格式各个字段的含义如下
     *   
     * @apiParam  {String}    access_token                  token
     * @apiParam  {Integer}   packId                        套餐id
     * @apiParam  {Object[]}  packDoctorList                    医生分成列表
     * @apiParam  {Integer}   packDoctorList.doctorId           医生id
     * @apiParam  {Integer}   packDoctorList.splitRatio         分成比例（每个为小于100的数字，合计必须等于100）
     * @apiParam  {Integer}   packDoctorList.receiveRemind    	接收提醒（至少需要设置一位医生接收提醒！）
     * @apiSuccess {Number=1} resultCode    返回状态码
     *
     * @apiAuthor  屈军利
     * @date 2015年10月25日
     */
    @RequestMapping(value = "/savePackDoctor", method = RequestMethod.POST)
    public JSONMessage savePackDoctor(String data) throws CloneNotSupportedException, HttpApiException {
        PackDoctorParam param = JSONUtil.parseObject(PackDoctorParam.class, data);
        packService.savePackDoctor(param.getPackId(),param.getPackDoctorList());
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /pack/pack/delPack 删除套餐
     * @apiVersion 1.0.0
     * @apiName delPack
     * @apiGroup 服务套餐
     * @apiDescription 删除套餐
     * 
     * @apiParam {String}   access_token           token
     * @apiParam {String}  id  套餐id，不能为空

     * @apiAuthor  屈军利
     * @date 2015年8月2日
     */
    @RequestMapping(value = "/delPack")
    public JSONMessage delPack(@RequestParam(required=true) Integer id) {
    	packService.deletePack(id);
    	return JSONMessage.success("成功");
    }
   
    /**
     * @api {post} /pack/pack/delPackDrug 删除 套餐用药
     * @apiVersion 1.0.0
     * @apiName delPackDrug
     * @apiGroup 服务套餐
     * @apiDescription 删除 套餐用药
     * 
     * @apiParam {String}   access_token           token
     * @apiParam {String}  	packDrugId  		          套餐用药id，不能为空

     * @apiAuthor  谢佩
     * @date 2015年12月9日
     */
    @RequestMapping(value = "/delPackDrug")
    public JSONMessage delPackDrug(@RequestParam Integer packDrugId) {
	    packService.deletePackDrug(packDrugId);
	    return JSONMessage.success("成功");
    }
   
   
    /**
     * @api {post} /pack/pack/savePackDrug 添加一个套餐用药
     * @apiVersion 1.0.0
     * @apiName savePackDrug
     * @apiGroup 服务套餐
     * @apiDescription 添加一个套餐用药
     * 
     * @apiParam {String}   access_token           token
     * @apiParam {String}  	packId  		                      套餐id，不能为空
     * @apiParam {String[]}  	drugIds  		                      用药Id，不能为空
     * 
     * @apiAuthor  谢佩
     * @date 2015年12月9日
     */
    @RequestMapping(value = "/savePackDrug")
    public JSONMessage savePackDrug(@RequestParam Integer packId,@RequestParam List<String> drugIds) {
	    packService.addPackDrug(packId, drugIds);
	    return JSONMessage.success("成功");
    }
   
    /**
     * @api {post} /pack/pack/findPackDrugView 查询一个套餐用药数据
     * @apiVersion 1.0.0
     * @apiName findPackDrugView
     * @apiGroup 服务套餐
     * @apiDescription 查询一个套餐用药数据
     * 
     * @apiParam {String}   access_token           token
     * @apiParam {String}  	packId  		                      套餐id，不能为空
     * 
     * @apiSuccess    {Object[]}  GoodsViews    					药品详细信息
     * @apiSuccess    {String}    GoodsViews.general_name 			药品名称
     * @apiSuccess    {String}    GoodsViews.pack_specification    	药品规格
     * @apiSuccess    {String}    GoodsViews.manufacturer    		药品厂家
     * @apiSuccess    {String}    GoodsViews.image		                                    药品图片
     * @apiSuccess    {String}    GoodsViews.packDrugId		                         套餐药品ID
     * @apiSuccess    {String}    GoodsViews.drugId					药品ID
     * 
     * @apiAuthor  谢佩
     * @date 2015年12月9日
     */
    @RequestMapping(value = "/findPackDrugView")
    public JSONMessage findPackDrugView(@RequestParam Integer packId) throws HttpApiException {
	    return JSONMessage.success(null,packService.findPackDrugView(packId, ReqUtil.instance.getToken()));
    }
	   	
	   
}
