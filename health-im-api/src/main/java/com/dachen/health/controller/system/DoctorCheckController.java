package com.dachen.health.controller.system;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constant.EnableStatusEnum;
import com.dachen.health.base.entity.param.AddHospitalParam;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.SendDoctorTopicService;
import com.dachen.health.commons.vo.User;
import com.dachen.health.controller.system.handler.DoctorCheckBizHandler;
import com.dachen.health.doctor.IDoctorInfoChangeOperationService;
import com.dachen.health.mq.RabbitSender;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.param.DoctorNameParam;
import com.dachen.health.system.entity.param.FindDoctorByAuthStatusParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;
import com.dachen.health.system.service.IDoctorCheckService;
import com.dachen.health.task.UserTimerTask;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/check")
public class DoctorCheckController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired 
    private IDoctorCheckService doctorCheckService;
    @Autowired
    private IBaseDataService baseDataService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private IPackService packService;
    @Autowired
    private IDoctorInfoChangeOperationService doctorInfoChangeOperationService;
    @Autowired
    private IBusinessServiceMsg businessServiceMsg;
    @Autowired
    private DoctorCheckBizHandler doctorCheckBizHandler;
    @Autowired
    private OperationLogMqProducer operationLogMqProducer;
    @Autowired
    private Auth2Helper auth2Helper;
    @Autowired
    private SendDoctorTopicService sendDoctorTopicService;
    /**
     * @api {get} /admin/check/getDoctors 获取医生或护士审核列表
     * @apiVersion 1.0.0
     * @apiName getDoctors
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取地区
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    name                  关键字（支持姓名、电话、医院）
     * @apiParam  {String}    deptId                科室id
     * @apiParam  {Long}    startTime               开始时间戳
     * @apiParam  {Long}    endTime                 结束时间戳
     * @apiParam  {String}    status                状态（1：审核通过，2：审核中，3：审核不通过，4: 禁用, 7：未认证, ）
     * @apiParam  {Integer}    suspend              挂起状态（0：非挂起，1：挂起）
     * 
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {String}   pageData.userId              用户id
     * @apiSuccess {String}   pageData.name                用户姓名
     * @apiSuccess {String}   pageData.telephone           手机
     * @apiSuccess {String}   pageData.createTime          注册时间
     * @apiSuccess {String}   pageData.checkTime          审核时间
     * @apiSuccess {String}   pageData.orderTime          排序时间
     * @apiSuccess {String}   pageData.headPicFileName     头像
     * @apiSuccess {Number}   pageData.status              状态（1：审核通过，2：审核中，3：审核不通过，7：未认证）
     * @apiSuccess {String}   pageData.hospital            医院
     * @apiSuccess {String}   pageData.departments         科室
     * @apiSuccess {String}   pageData.title               职称
     * @apiSuccess {String}   pageData.doctorNum           医生号
     * @apiSuccess {String}   pageData.nurseNum            护士
     * @apiSuccess {String}   pageData.userType            用户类型（3：医生，9：护士）
     * @apiSuccess {object[]} pageData.nurseImageList      护士图片列表
     * @apiSuccess {String}   pageData.nurseImageList[0].imageId         图片URL地址
     * @apiSuccess {Integer}  pageData.nurseImageList[0].order           图片排序
     * @apiSuccess {String}   pageData.nurseImageList[0].imageType       图片类型
     * @apiSuccess {String}   pageData.inviterName      	 邀请者的姓名
     * @apiSuccess {String}   pageData.source       		注册来源
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDoctors")
    public JSONMessage getDoctors(DoctorCheckParam param) {
        return JSONMessage.success(null, doctorCheckService.getDoctors(param));
    }
    
    /**
     * @api {get} /admin/check/getDoctorsByName 获取医生或护士审核列表
     * @apiVersion 1.0.0
     * @apiName getDoctors
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取地区
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    name                  关键字（支持姓名、电话、医院）
     * @apiParam  {String}    deptId                科室id
     * @apiParam  {Long}    startTime               开始时间戳
     * @apiParam  {Long}    endTime                 结束时间戳
     * @apiParam  {String}    status                状态（1：审核通过，2：审核中，3：审核不通过，4: 禁用, 7：未认证, ）
     * @apiParam  {Integer}    suspend              挂起状态（0：非挂起，1：挂起）
     * 
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {String}   pageData.userId              用户id
     * @apiSuccess {String}   pageData.name                用户姓名
     * @apiSuccess {String}   pageData.telephone           手机
     * @apiSuccess {String}   pageData.createTime          注册时间
     * @apiSuccess {String}   pageData.checkTime          审核时间
     * @apiSuccess {String}   pageData.orderTime          排序时间
     * @apiSuccess {String}   pageData.headPicFileName     头像
     * @apiSuccess {Number}   pageData.status              状态（1：审核通过，2：审核中，3：审核不通过，7：未认证）
     * @apiSuccess {String}   pageData.hospital            医院
     * @apiSuccess {String}   pageData.departments         科室
     * @apiSuccess {String}   pageData.title               职称
     * @apiSuccess {String}   pageData.doctorNum           医生号
     * @apiSuccess {String}   pageData.nurseNum            护士
     * @apiSuccess {String}   pageData.userType            用户类型（3：医生，9：护士）
     * @apiSuccess {object[]} pageData.nurseImageList      护士图片列表
     * @apiSuccess {String}   pageData.nurseImageList[0].imageId         图片URL地址
     * @apiSuccess {Integer}  pageData.nurseImageList[0].order           图片排序
     * @apiSuccess {String}   pageData.nurseImageList[0].imageType       图片类型
     * @apiSuccess {String}   pageData.inviterName           邀请者的姓名
     * @apiSuccess {String}   pageData.source               注册来源
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDoctorsByName")
    public JSONMessage getDoctorsByName(DoctorNameParam param) {
        return JSONMessage.success(null, doctorCheckService.getDoctorsByName(param));
    }
    
    /**
     * @api {get} /admin/check/getDoctorsByKeywork 根据关键字查找全平台医生信息。
     * @apiVersion 1.0.0
     * @apiName getDoctorsByKeywork
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 根据关键字查找全平台医生信息。
     *
     * @apiParam  {String}    access_token           token
     * @apiParam  {String}    keyword                关键字（支持姓名、电话、医院）
     * @apiParam  {Integer}    pageIndex              页码
     * @apiParam  {Integer}    pageSize               页面大小
     * 
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {Integer}  pageData.doctorId          医生id
     * @apiSuccess {String}   pageData.name              医生姓名
     * @apiSuccess {String}   pageData.hospital          医院
     * @apiSuccess {String}   pageData.department        科室
     * @apiSuccess {String}   pageData.title             职称
     * @apiSuccess {Integer}  pageData.status            平台审核状态
     * @apiSuccess {String}   pageData.group             集团
     * @apiSuccess {String}   pageData.group.groupId     集团id
     * @apiSuccess {String}   pageData.group.name        集团名称
     *
     * @apiAuthor  傅永德
     * @date 2016年10月25日
     */
    @RequestMapping("/getDoctorsByKeywork")
    public JSONMessage getDoctorsByKeywork(
    		@RequestParam(name="keyword", required=true)String keyword, 
    		@RequestParam(name="pageSize", defaultValue="15")Integer pageSize, 
    		@RequestParam(name="pageIndex", defaultValue="0")Integer pageIndex) {
    	return JSONMessage.success(doctorCheckService.getByKeyword(keyword, pageIndex, pageSize));
    }
    
    /**
     * @api {get} /admin/check/findDoctorByAuthStatus 获取医生的指定认证状态
     * @apiVersion 1.0.0
     * @apiName findDoctorByAuthStatus
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取医生的指定认证状态
     *
     * @apiParam  {String}    access_token          	  token
     * @apiParam  {int}   	  isAuthStatus                认证状态（1：已认证，2：未认证）
     * @apiParam  {String}    keyword                	     搜索关键字（支持姓名、电话）
     * @apiParam  {int}       pageIndex                	     页码，从0开始
     * @apiParam  {int}       pageSize                	     每页的数量，默认为15
     * 
     * @apiSuccess {Object} 	data                      对象
     * @apiSuccess {int} 		resultCode                返回结果码
     * 
     * @apiSuccess {int} 	data.pageCount                总页数量
     * @apiSuccess {int} 	data.pageIndex                页码，从0开始
     * @apiSuccess {int} 	data.pageSize                 每页数量
     * @apiSuccess {int} 	data.start                	  ????
     * @apiSuccess {int} 	data.total                	    总数量
     * @apiSuccess {User[]} 	data.pageData             User对象数组
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/findDoctorByAuthStatus")
    public JSONMessage findDoctorByAuthStatus(FindDoctorByAuthStatusParam param) {
        return JSONMessage.success(null, doctorCheckService.findDoctorByAuthStatus(param));
    }
    
    /**
     * @api {post} /admin/check/addHospital 新增一个医院名称
     * @apiVersion 1.0.0
     * @apiName addHospital
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 新增一个医院名称
     * 
     * @apiParam  {String}    access_token          	  token
     * @apiParam  {int}   	  provinceId                  省份Id
     * @apiParam  {int}       cityId                	    市级Id
     * @apiParam  {int}       countryId                	    区县Id
     * @apiParam  {int}       name                	            医院名称
     * @apiParam  {String}    level						医院的级别
     * @apiParam  {String}    address                   医院的地址
     * @apiParam  {String}    lng                       经度
     * @apiParam  {String}    lat                       纬度
     * 
     * @apiSuccess {int} 	  resultCode                返回结果码
     * @apiSuccess {Object}   data                      返回对象
     * @apiSuccess {String}   data.id                   id
     * @apiSuccess {int} 	  data.province             省份Id
     * @apiSuccess {int} 	  data.city                 市级Id
     * @apiSuccess {int} 	  data.country              区县Id
     * @apiSuccess {String}   data.name                 医院名称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/addHospital")
    public JSONMessage addHospital(@RequestBody AddHospitalParam param) {
        HospitalPO hospitalPO = baseDataService.addHospital(param);
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getTokenInfo().getUserId(),
            OperationLogTypeDesc.HOSPITAL, String.format("添加基础医院数据（%s）", param.getName()));
        return JSONMessage.success(null, hospitalPO);
    }
    
    /**
     * @api {POST} /admin/check/updateHospital 更新医院信息
     * @apiVersion 1.0.0
     * @apiName addHospital
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 新增一个医院名称
     * 
     * @apiParam  {String}    access_token                token
     * @apiParam  {String}    id                         医院的id
     * @apiParam  {int}       provinceId                  省份Id
     * @apiParam  {int}       cityId                        市级Id
     * @apiParam  {int}       countryId                     区县Id
     * @apiParam  {int}       name                       医院名称
     * @apiParam  {String}    level                     医院的级别
     * @apiParam  {String}    address                   医院的地址
     * @apiParam  {Integer}    status                   医院状态 normal(1,"正常"),frozen(2,"冻结");
     * @apiParam  {String}    lng                       经度
     * @apiParam  {String}    lat                       纬度
     * 
     * @apiSuccess {int}      resultCode                返回结果码
     * @apiSuccess {Object}   data                      返回对象
     * @apiSuccess {String}   data.id                   id
     * @apiSuccess {int}      data.province             省份Id
     * @apiSuccess {int}      data.city                 市级Id
     * @apiSuccess {int}      data.country              区县Id
     * @apiSuccess {String}   data.name                 医院名称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updateHospital")
    public JSONMessage updateHospital(@RequestBody AddHospitalParam param) {
        HospitalVO oldHospital = baseDataService.getHospital(param.getId());
        HospitalPO newHospital = baseDataService.updateHospital(param);

        if (Objects.nonNull(oldHospital) && Objects.nonNull(newHospital)) {

            // 同步医生医院信息
            doctorCheckService.updateDoctorHospital(oldHospital, newHospital);

            StringBuilder changes = new StringBuilder();
            if (!Objects.equals(oldHospital.getName(), newHospital.getName())) {
                changes.append(oldHospital.getName()).append("变更为").append(newHospital.getName()).append("\n");
            } else if (!Objects.equals(oldHospital.getLevel(), newHospital.getLevel())) {
                changes.append(oldHospital.getLevel()).append("变更为").append(newHospital.getLevel()).append("\n");
            } else if (!Objects.equals(oldHospital.getProvince(), newHospital.getProvince())) {
                String oldProvince = baseDataService.getAreaNameByCode(oldHospital.getProvince());
                String newProvince = baseDataService.getAreaNameByCode(newHospital.getProvince());
                changes.append(oldProvince).append("变更为").append(newProvince).append("\n");
            } else if (!Objects.equals(oldHospital.getCity(), newHospital.getCity())) {
                String oldCity = baseDataService.getAreaNameByCode(oldHospital.getCity());
                String newCity = baseDataService.getAreaNameByCode(newHospital.getCity());
                changes.append(oldCity).append("变更为").append(newCity).append("\n");
            } else if (!Objects.equals(oldHospital.getCountry(), newHospital.getCountry())) {
                String oldCountry = baseDataService.getAreaNameByCode(oldHospital.getCountry());
                String newCountry = baseDataService.getAreaNameByCode(newHospital.getCountry());
                changes.append(oldCountry).append("变更为").append(newCountry).append("\n");
            } else if (!Objects.equals(oldHospital.getAddress(), newHospital.getAddress())) {
                changes.append(oldHospital.getAddress()).append("变更为").append(newHospital.getAddress()).append("\n");
            }

            if (changes.length() > 0) {
                operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getTokenInfo().getUserId(),
                    OperationLogTypeDesc.HOSPITAL, String.format("编辑基础医院数据（变更的内容，%s）", changes));
            }

            if(!Objects.equals(oldHospital.getStatus(), newHospital.getStatus())){
                String name = StringUtil.isNotEmpty(newHospital.getName()) ? newHospital.getName() : oldHospital.getName();
                String action = EnableStatusEnum.getAlias(newHospital.getStatus());
                operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getTokenInfo().getUserId(),
                        OperationLogTypeDesc.HOSPITAL, String.format(" %1$s %2$s(id: %3$s)",action,name, newHospital.getId()));
            }
        }
        
        return JSONMessage.success(null, newHospital);
    }
    
    /**
     * @api {get} /admin/check/getHospitalLevel 获取医院的级别信息
     * @apiVersion 1.0.0
     * @apiName getHospitalLevel
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取医院的级别信息
     * @apiParam  {String}    access_token          token
     * 
     * @apiSuccess {String}	  id              		Id
     * @apiSuccess {String}   level                 医院级别
     *
     * @apiAuthor  傅永德
     * @date 2016年5月17日
     */
    @RequestMapping("/getHospitalLevel")
    public JSONMessage getHospitalLevel() {
    	return JSONMessage.success(null, baseDataService.getHospitalLevel());
    }
    
    /**
     * @api {get} /admin/check/getDoctor 获取医生或护士详情
     * @apiVersion 1.0.0
     * @apiName getDoctor
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取医生，当回数据为1时表示正在审核
     *
     * @apiParam  {String}    access_token        token
     * @apiParam  {String}    id                  医生id
     * 
     * @apiSuccess {String}   userId              用户id
     * @apiSuccess {String}   name                用户姓名
     * @apiSuccess {Integer}  sex				  性别
     * @apiSuccess {String}   introduction		  医生介绍
     * @apiSuccess {List}     expertises		  医生擅长
     * @apiSuccess {String}   skill 			  补充擅长
     * @apiSuccess {String}   scholarship        学术成就
     * @apiSuccess {String}   experience         社会责任  
     * @apiSuccess {String}   telephone           手机
     * @apiSuccess {Number}   createTime          注册时间
     * @apiSuccess {String}   headPicFileName     头像
     * @apiSuccess {Number}   status              状态（1：审核通过，2：审核中，3：审核不通过）
     * @apiSuccess {String}   hospital            医院
     * @apiSuccess {String}   hospitalId          医院Id
     * @apiSuccess {String}   departments         科室
     * @apiSuccess {String}   deptId			  科室Id
     * @apiSuccess {String}   title               职称
     * @apiSuccess {String}   doctorNum           医生号
     * @apiSuccess {String}   licenseNum          证件号
     * @apiSuccess {String}   licenseExpire       证件有效期
     * @apiSuccess {String}   checkTime           审核时间
     * @apiSuccess {String}   certTime            认证时间
     * @apiSuccess {String}   modifyTime          修改时间
     * @apiSuccess {String}   checher             审核人
     * @apiSuccess {String}   remark              审核意见
     * @apiSuccess {String}   nurseNum            护士
     * @apiSuccess {Integer}   role            	  角色（0：字段为空，1：医生， 2：护士， 3：其他）
     * @apiSuccess {String}   userType            用户类型（3：医生，9：护士）
     * @apiSuccess {Integer}   assistantId        医生助手Id
     * @apiSuccess {String}   assistantName       医生助手姓名
     * @apiSuccess {object[]} nurseImageList      护士图片列表
     * @apiSuccess {String}   nurseImageList[0].imageId         图片URL地址
     * @apiSuccess {Integer}  nurseImageList[0].order           图片排序
     * @apiSuccess {String}   nurseImageList[0].imageType       图片类型
     * @apiSuccess {String[]} groupNames        所在集团
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDoctor")
    public JSONMessage getDoctors(Integer id) {
        boolean checking = doctorCheckService.checking(id);
        if(checking){
            //当前医生正在被审核，请10分钟后重试
            return JSONMessage.success("1", doctorCheckService.getDoctor(id));
        }

        //添加医生已开启的服务
        DoctorCheckVO vo = doctorCheckService.getDoctor(id);
        vo.setServices(packService.getPackNameByDoctorId(id));
        //获取邀请人openId
        Integer inviterId = vo.getInviterId();
        if(Objects.nonNull(inviterId)){
            List<Integer> idList= new ArrayList<>();
            idList.add(inviterId);
            List<AccessToken> tokenList = auth2Helper.getOpenIdList(idList);
            vo.setOpenId(tokenList.get(0).getOpenId());
        }
        return JSONMessage.success(null, vo);
    }
    
    /**
     * @api {get} /admin/check/getArea 获取地区
     * @apiVersion 1.0.0
     * @apiName getArea
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取地区
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Number}    id                    地区id，为空表示查询省，不为空查询市或县
     * 
     * @apiSuccess {String}   code                  地区编码
     * @apiSuccess {String}   name                  名称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getArea")
    public JSONMessage getArea(@RequestParam(defaultValue = "0") Integer id) {
        return JSONMessage.success(null, baseDataService.getAreas(id));
    }
    
    /**
     * @api {get} /admin/check/getHospitals 获取医院
     * @apiVersion 1.0.0
     * @apiName getHospitals
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 根据地区获取地区
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Number}    id                    地区id,可为空，但与那么不能同时为空
     * @apiParam  {Number}    name                  医院名称，用于模糊查询
     * 
     * @apiSuccess {String}   id                    医院id
     * @apiSuccess {String}   name                  医院名称
     * @apiSuccess {String}   country               县
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getHospitals")
    public JSONMessage getHospitals(@RequestParam(defaultValue = "0") Integer id, String name) {
        return JSONMessage.success(null, baseDataService.getHospitals(id,name));
    }
    
    /**
     * @api {get} /admin/check/getDepts 获取科室
     * @apiVersion 1.0.0
     * @apiName getDepts
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取科室，当按名称搜索时，查询的是叶子节点
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Number}    id                    地区id
     * @apiParam  {String}    name                  科室名称
     * 
     * @apiSuccess {String}   id                    科室id
     * @apiSuccess {String}   name                  科室名称
     * @apiSuccess {String}   isLeaf                是否为叶子节点(1:是；0：否)
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDepts")
    public JSONMessage getDepts(String id,String name) {
        return JSONMessage.success(null, baseDataService.getDepts(id,name));
    }
    
    /**
     * @api {get} /admin/check/getTitles 获取职称
     * @apiVersion 1.0.0
     * @apiName getTitles
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 获取职称
     *
     * @apiParam  {String}    access_token          token
     * 
     * @apiSuccess {String}   id                    职称id
     * @apiSuccess {String}   name                  职称名称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getTitles")
    public JSONMessage getTitles() {
        return JSONMessage.success(null, baseDataService.getTitles());
    }
    
    /**
     * @throws HttpApiException 
     * @api {post} /admin/check/checked 医生或者护士审核通过
     * @apiVersion 1.0.0
     * @apiName checked
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 医生或者护士审核通过，不能同时审核
     *
     * @apiParam   {String}   access_token      token
     * @apiParam   {Number}   userId            用户id
     * @apiParam   {String}   hospital          医院
     * @apiParam   {String}   hospitalId        医院Id
     * @apiParam   {String}   departments       科室
     * @apiParam   {String}   deptId       		科室Id
     * @apiParam   {String}   title             职称
     * @apiParam   {String}   licenseNum        证件号
     * @apiParam   {String}   licenseExpire     证件有效期
     * @apiParam   {String}   remark            审核意见
     * @apiParam   {String}   headPicFileName   头像
     * @apiParam   {String}   name              姓名
     * @apiParam   {Integer}  role              角色  1 医生 2 护士
     * @apiParam   {Integer}  assistantId       医生助手id 
     * @apiParam   {Boolean}  sendSMS           是否发送短信
     * 
     * @apiSuccess {Number=1} resultCode        返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/checked")
    public JSONMessage checked(DoctorCheckParam param) throws HttpApiException {
        String key = "checked" + "_" + param.getUserId();
        boolean ok = RedisUtil.setnx(key, "Locked", 30);
        if (!ok) {
            throw new ServiceException("正在执行审核操作,请稍后！");
        }
        try {
            param.setCheckTime(System.currentTimeMillis());
            Integer userId = ReqUtil.instance.getUserId();
            User admin = userManager.getUser(userId);
            param.setChecker(admin.getName());
            param.setCheckerId(admin.getUserId());
            param.setNurseShortLinkUrl(APP_NURSE_CLIENT_LINK());
            doctorCheckService.checked(param);
            // MQ
            userManager.userInfoChangeNotify(param.getUserId());
            User user = userManager.getUser(param.getUserId());
            // 异步处理其他业务
            doctorCheckBizHandler.handleRelationBiz(user);
            // 记录操作日志
            if (Objects.nonNull(user) && UserType.doctor.getIndex() == user.getUserType()) {
                operationLogMqProducer.sendMsgToMq(userId, OperationLogTypeDesc.DOCTORAUDIT,
                        String.format("变更%1$s（%2$s）账号状态-%3$s", user.getName(), user.getTelephone(), "审核通过"));
            }
            // 异步发送Topic消息队列
            sendDoctorTopicService.sendCheckTopicMes(user);
            //上报热力值
            RabbitSender.sendUserCheckHeatValue(param.getUserId());
        } finally {
            RedisUtil.del(key);
        }
        return JSONMessage.success();
    }



    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    public String APP_NURSE_CLIENT_LINK () throws HttpApiException {
        String nurseLink = PropertiesUtil.getContextProperty("app.nurse.client.link");
        String shorUrl = shortUrlComponent.generateShortUrl(nurseLink);
        return shorUrl;
    }



    /**
     * @api {post} /admin/check/uncheck 医生或者护士反审核
     * @apiVersion 1.0.0
     * @apiName uncheck
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 医生或者护士反审核
     * 
     * @apiParam   {String}   access_token      token
     * @apiParam   {Integer}   userId      用户的id
     *
     * @apiSuccess {Integer} resultCode        返回状态码（1表示成功）
     *
     * @apiAuthor  傅永德
     * @date 2016年5月20日
     */
    @RequestMapping("/uncheck")
    public JSONMessage uncheck(@RequestParam(value="userId") Integer userId) {
    	//先查询到该医生，更新医生的审核状态为待审核，移除全文索引里面改医生
    	Integer currentUserId = ReqUtil.instance.getUserId();

    	doctorCheckService.uncheck(userId, currentUserId);

        userManager.userInfoChangeNotify(userId);
    	//反审核之后再关闭医生开启的服务
        updatePackStatus(userId);

        User user = userManager.getUser(userId);
        // 记录操作日志
        if (Objects.nonNull(user) && UserType.doctor.getIndex() == user.getUserType()) {
            operationLogMqProducer.sendMsgToMq(currentUserId, OperationLogTypeDesc.DOCTORAUDIT,
                    String.format("变更%1$s（%2$s）账号状态-%3$s", user.getName(), user.getTelephone(), "反审核"));
        }
    	
    	return JSONMessage.success();
    }

    @RequestMapping("/tempUncheck")
    public JSONMessage tempUncheck(@RequestParam(required = true) String phones){
        logger.info("params :"+phones);
        String[] phoneArr = phones.split(",");
        List<String> process = Lists.newArrayList();
        List<String> notFound = Lists.newArrayList();
        for(String phone : phoneArr){
            logger.info("process phone:"+phone);
            User u = userManager.getUser(phone, UserType.doctor.getIndex());
            logger.info("user json "+ JSON.toJSONString(u));
            if(u != null){
                Integer userId = u.getUserId();
                Integer currentUserId = ReqUtil.instance.getUserId();
                logger.info("currentUserId----------为什么为空-------------------"+currentUserId);
                doctorCheckService.uncheck(userId, currentUserId);
                //反审核之后再关闭医生开启的服务
                updatePackStatus(userId);
                User user = userManager.getUser(userId);
                if (Objects.nonNull(user) && UserType.doctor.getIndex() == user.getUserType()) {
                    operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                        OperationLogTypeDesc.DOCTORAUDIT, String.format("变更%1$s（%2$s）账号状态-%3$s", user.getName(), user.getTelephone(), "反审核"));
                }
                process.add(phone);
            } else {
                notFound.add(phone);
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("process", process);
        map.put("notFound", notFound);
        return JSONMessage.success(map);
    }

    private void updatePackStatus(Integer userId) {
        List<Integer> doctorIds = Lists.newArrayList();
        doctorIds.add(userId);
        List<Pack> packs = packService.queryByUserIds(doctorIds);
        if ((packs != null) && (packs.size() > 0)) {
            for (Pack pack : packs) {
                if ((pack!= null) && (pack.getStatus() != null) && pack.getStatus().equals(PackStatus.open.getIndex())) {

                    if ((pack.getPackType() == PackEnum.PackType.message.getIndex())
                        || (pack.getPackType() == PackEnum.PackType.phone.getIndex())
                        || (pack.getPackType() == PackEnum.PackType.appointment.getIndex())
                        ) {
                        //关闭图文咨询、电话咨询、预约名医
                        pack.setStatus(PackStatus.close.getIndex());
                        pack.setDoctorId(userId);
                        packService.updatePack(pack);
                    } else if(pack.getPackType() == PackEnum.PackType.careTemplate.getIndex()) {
                        //删除健康关怀
                        packService.deletePack(pack.getId());
                    }
                }
            }
        }
    }

    /**
     * @api {post} /admin/check/fail 医生或者护士审核失败
     * @apiVersion 1.0.0
     * @apiName fail
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 医生审核失败，不能同时审核
     *
     * @apiParam   {String}   access_token      token
     * @apiParam   {Number}   userId            用户id
     * @apiParam   {String}   remark            审核意见
     * @apiParam   {Boolean}  sendSMS           是否发送短信
     *
     * @apiSuccess {Number=1} resultCode        返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/fail")
    public JSONMessage fail(DoctorCheckParam param) {
        param.setCheckTime(System.currentTimeMillis());
        Integer userId = ReqUtil.instance.getUserId();
        User admin = userManager.getUser(userId);
        param.setChecker(admin.getName());
        param.setCheckerId(admin.getUserId());
        doctorCheckService.fail(param);
        User user = userManager.getUser(param.getUserId());
        // 异步发送Topic消息队列
        sendDoctorTopicService.sendCheckTopicMes(user);
        userManager.userInfoChangeNotify(param.getUserId());

        if (Objects.nonNull(user) && UserType.doctor.getIndex() == user.getUserType()) {
            operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCTORAUDIT, String.format("变更%1$s（%2$s）账号状态-%3$s", user.getName(), user.getTelephone(), "审核不通过"));
        }
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /admin/check/edit 医生或者护士资料编辑
     * @apiVersion 1.0.0
     * @apiName edit
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 医生或者护士编辑
     *
     * @apiParam   {String}   access_token      token
     * @apiParam   {Number}   userId            用户id
     * @apiParam   {String}   name              姓名
     * @apiParam   {String}   status            审核状态 （1：正常  2：待审核 3：审核未通过 7：未认证）
     * @apiParam   {String}   headPicFileName   头像
     * @apiParam   {Integer}  sex				性别1男，2女 3 保密
     * @apiParam   {String}	  introduction		医生介绍
     * @apiParam   {List}     expertises        医生擅长
     * @apiParam   {String}   skill				补充擅长
     * @apiParam   {String}   scholarship       学术成就
     * @apiParam   {String}   experience        社会责任  
     * @apiParam   {String}   hospital          医院
     * @apiParam   {String}   hospitalId        医院Id
     * @apiParam   {String}   departments       科室
     * @apiParam   {String}   deptId       		科室Id
     * @apiParam   {String}   title             职称
     * @apiParam   {String}   licenseNum        证件号
     * @apiParam   {String}   licenseExpire     证件有效期
     * @apiParam   {Integer}   role            角色  1 医生 2 护士
     * @apiParam   {Integer}  assistantId       医生助手id  
     * @apiParam   {Integer}  forceQuitApp      是否强制用户退出APP（1：表示强制退出APP,为空 不做操作）
     * @apiSuccess {Number=1} resultCode        返回状态吗
     * @apiAuthor  谭永芳
     * @date 2016年5月31日
     */
    @RequestMapping("/edit")
    public JSONMessage edit(DoctorCheckParam param) throws HttpApiException {
        Integer userId = ReqUtil.instance.getUserId();
        
        User admin = userManager.getUser(userId);

        param.setChecker(admin.getName());
        param.setCheckerId(admin.getUserId());
        param.setNurseShortLinkUrl(APP_NURSE_CLIENT_LINK());

        User user = userManager.getUser(param.getUserId());
        
        if(Objects.nonNull(user)&&Objects.equals(user.getStatus(),UserEnum.UserStatus.normal.getIndex())){
        	if (!Objects.equals(user.getUserLevel(), param.getUserLevel())&&Objects.nonNull(param.getUserLevel())) {
        		throw new ServiceException("该用户已通过审核,不能编辑身份!");	
            }
     		if (!Objects.equals(DateUtil.formatDate2Str(user.getLimitedPeriodTime(), null), DateUtil.formatDate2Str(param.getLimitedPeriodTime(), null))
     				&&Objects.nonNull(param.getLimitedPeriodTime())) {
     			throw new ServiceException("该用户已通过审核,不能编辑用户到期时间!");
     		}
        }
        
        doctorCheckService.edit(param);

        userManager.userInfoChangeNotify(param.getUserId());
        sendEventUserLevelChangeEvent(user,param);

        if(UserEnum.UserType.doctor.getIndex() == user.getUserType()){
        	if(param.getAssistantId()!=null && !param.getAssistantId().equals(user.getDoctor().getAssistantId())){
        		doctorInfoChangeOperationService.assistantChanged(user, param);
        	}
    	}
    	if (Objects.equals(UserEnum.UserType.doctor.getIndex(), user.getUserType())) {
            operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCTORAUDIT, String.format("编辑%1$s（%2$s）账号信息", user.getName(), user.getTelephone()));
        }

        if (UserType.doctor.getIndex() == user.getUserType()){
            sendEventUserExpertiseChangeEvent(user , param);
            if (param.getName() != null && !param.getName().equals(user.getName())) {
                businessServiceMsg.userInfoChangeEvent(user.getUserId(), param.getName());
            }
        }

        return JSONMessage.success();
    }

    /**
     * 编辑医生的擅长信息发生有无的变化时,发送指令通知
     */
    private void sendEventUserExpertiseChangeEvent(User oldUser,DoctorCheckParam param) {
        List<String> expertise1 = oldUser.getDoctor().getExpertise();
        String skill1 = oldUser.getDoctor().getSkill();
        List<String> expertise2 = Arrays.asList(param.getExpertises());
        String skill2 = param.getSkill();

        boolean f1 = CollectionUtils.isNotEmpty(expertise1);
        boolean f2 = CollectionUtils.isNotEmpty(expertise2);
        boolean f3 = StringUtils.isNotEmpty(skill1);
        boolean f4 = StringUtils.isNotEmpty(skill2);

        //有(任意一个有值)->无(两个都变为为无值)
        if ( (f1 || f3) && ( !f2 && !f4) ){
            businessServiceMsg.sendExpertiseChangeEventForDoctor(oldUser.getUserId(), oldUser.getUserId(), oldUser.getStatus(),param.getName(), 0);
        }

        //无(两个都为无)->有(任意一个有值)
        if ( (!f1 && !f3) && (f2 || f4)){
            businessServiceMsg.sendExpertiseChangeEventForDoctor(oldUser.getUserId(), oldUser.getUserId(), oldUser.getStatus(),param.getName(), 1);
        }

    }

    /**
	 * 
	 * @api {[get,post]} /admin/check/openAutoCheck 控制自动审核的开关
	 * @apiVersion 1.0.0
	 * @apiName 打开或关闭自动审核的功能
	 * @apiGroup 用户
	 * @apiDescription 发送短信的接口，内容可以自己定制
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} open 是否打开（1、打开；2、关闭）
	 * 
	 * @apiSuccess {Map} data 结果
	 * @apiAuthor 傅永德
	 * @date 2016年6月1日
	 */
	@RequestMapping(value = "/openAutoCheck")
    public JSONMessage openAutoCheck(Integer open) {
		Map<Object, Object> data = new HashMap<Object, Object>();
		if (open == 1) {
			UserTimerTask.execute = true;
			return JSONMessage.success(null, data);
		} else {
			UserTimerTask.execute = false;
			return JSONMessage.success(null, data);
		}
    	
    }

	private void sendEventUserLevelChangeEvent(User oldUser,DoctorCheckParam param){
    	Integer sendUserLevel = oldUser.getUserLevel();
		Long sendLimitedPeriodTime = oldUser.getLimitedPeriodTime();
		boolean isChange = false;
		if (!Objects.equals(oldUser.getUserLevel(), param.getUserLevel())&&Objects.nonNull(param.getUserLevel())) {
			sendUserLevel = param.getUserLevel();
			isChange = true;
		}

		if (!Objects.equals(oldUser.getLimitedPeriodTime(), param.getLimitedPeriodTime())&&Objects.nonNull(param.getLimitedPeriodTime())) {
			sendLimitedPeriodTime = param.getLimitedPeriodTime();
			isChange = true;
		}

		if(isChange){
			if(Objects.nonNull(sendLimitedPeriodTime)&&System.currentTimeMillis()>sendLimitedPeriodTime){
				sendUserLevel = UserEnum.UserLevel.Expire.getIndex();
			}
			businessServiceMsg.sendEventForDoctor(oldUser.getUserId(), oldUser.getUserId(), oldUser.getStatus(),param.getName(),sendUserLevel,sendLimitedPeriodTime);
			logger.info("编辑医生身份变更发送指令.userId{}.name{}.userLevel{}.sendLimitedPeriodTime{}", oldUser.getUserId(),param.getName(),sendUserLevel,sendLimitedPeriodTime);
		}
    }
	
	
	/**
	 * 
	 * @api {[get,post]} /admin/check/refreshIllegalUser 刷新非法用户
	 * @apiVersion 1.0.0
	 * @apiName 刷新非法用户
	 * @apiGroup 用户
	 * @date 2017年10月20日
	 */
	@RequestMapping(value = "/refreshIllegalUser")
    public JSONMessage refreshIllegalUser(@RequestParam String[] tel,@RequestParam String pwd,@RequestParam Long time) {
		doctorCheckService.refreshIllegalUser(tel,pwd,time);
		return JSONMessage.success();
    }

    /**
     * @api {post} /admin/check/suspend
     * @apiVersion 1.0.0
     * @apiName suspend
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 挂起
     * @apiParam {Number}   userId            用户id
     * @apiSuccess {Number=1} resultCode      返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月2日
     */
    @RequestMapping("/suspend")
    public JSONMessage suspend(@RequestParam Integer userId) {
        Integer adminUserId = ReqUtil.instance.getUserId();
        doctorCheckService.suspend(userId, adminUserId);
        userManager.userInfoChangeNotify(userId);
        return JSONMessage.success();
    }

    /**
     * @api {post} /admin/check/removeSuspend
     * @apiVersion 1.0.0
     * @apiName removeSuspend
     * @apiGroup WEB 禁止app端调用
     * @apiDescription 挂起
     * @apiParam {Number}   userId            用户id
     * @apiSuccess {Number=1} resultCode      返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月2日
     */
    @RequestMapping("/removeSuspend")
    public JSONMessage removeSuspend(@RequestParam Integer userId) {
        Integer adminUserId = ReqUtil.instance.getUserId();
        doctorCheckService.removeSuspend(userId, adminUserId);
        userManager.userInfoChangeNotify(userId);
        return JSONMessage.success();
    }

}
