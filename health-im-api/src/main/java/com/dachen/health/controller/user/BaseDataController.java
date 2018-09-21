package com.dachen.health.controller.user;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.cache.AreaDbCache;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.entity.param.CollegeParam;
import com.dachen.health.base.entity.param.MsgTemplateParam;
import com.dachen.health.base.entity.po.CollegeDeptPO;
import com.dachen.health.base.entity.po.CollegesPO;
import com.dachen.health.base.entity.po.MsgTemplate;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.circle.AdScopeEnum;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.controller.form.AdScopeForm;
import com.dachen.health.user.entity.param.DoctorParam;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/base")
public class BaseDataController extends AbstractController {

    @Autowired
    private IBaseDataService baseDataService;

    @Resource
    private UserManager userManager;

    @Autowired
	protected IdxRepository idxRepository;

    @Autowired
    private User2Service user2Service;

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataController.class);

    /**
     * @api {get} /base/getArea 获取地区
     * @apiVersion 1.0.0
     * @apiName getArea
     * @apiGroup 基础数据
     * @apiDescription 获取地区
     *
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
     * @api {get} /base/getAreaNameByCode 通过地区编码获取地区名称
     * @apiVersion 1.0.0
     * @apiName /base/getAreaNameByCode
     * @apiGroup 基础数据
     * @apiDescription 通过地区编码获取地区名称
     *
     * @apiParam  {Number}    code                  地区编码
     *
     * @apiSuccess {String}   name                  名称
     *
     * @apiAuthor  钟良
     * @date 2016年10月28日
     */
    @RequestMapping("getAreaNameByCode")
    public JSONMessage getAreaNameByCode(Integer code){
    	return JSONMessage.success(null, baseDataService.getAreaNameByCode(code));
    }

	/**
	 * @api {get} /base/getAllAreas 获取所有地区
	 * @apiVersion 1.0.0
	 * @apiName getAllAreas
	 * @apiGroup 基础数据
	 * @apiDescription 获取省市级别所有地区
	 *
	 * @apiSuccess {String} 	name 	名称
	 * @apiSuccess {String} 	code 	地区编码
	 * @apiSuccess {String} 	pcode 	父编码
	 *
	 * @apiAuthor 谢平
	 * @date 2015年11月18日
	 */
    @RequestMapping("/getAllAreas")
    public JSONMessage getAllAreas() {
    	return JSONMessage.success(null, baseDataService.getAllAreas());
    }
    /**
     * @api {get} /base/getAllAreasExDirect 获取所有地区 直辖市不返回第二级
     * @apiVersion 1.0.0
     * @apiName getAllAreasExDirect
     * @apiGroup 基础数据
     * @apiDescription 获取省市级别所有地区
     *
     * @apiSuccess {String} 	name 	名称
     * @apiSuccess {String} 	code 	地区编码
     * @apiSuccess {String} 	pcode 	父编码
     *
     * @apiAuthor 谢平
     * @date 2015年11月18日
     */
    @RequestMapping("/getAllAreasExDirect")
    public JSONMessage getAllAreasExDirect() {
        return JSONMessage.success(null, baseDataService.getAllAreasExDirect());
    }

    /**
	 * @api {get} /base/getAllAreasInfo 获取所有地区
	 * @apiVersion 1.0.0
	 * @apiName /base/getAllAreasInfo
	 * @apiGroup 基础数据
	 * @apiDescription 获取省市级别所有地区（省份，城市，地区/县）
	 *
	 * @apiSuccess {String} 	name 	名称
	 * @apiSuccess {String} 	code 	地区编码
	 * @apiSuccess {String} 	pcode 	父编码
	 *
	 * @apiAuthor 钟良
	 * @date 2016年10月27日
	 */
    @RequestMapping("/getAllAreasInfo")
    public JSONMessage getAllAreasInfo() {
    	return JSONMessage.success(null, baseDataService.getAllAreasInfo());
    }

    @ApiOperation(value = "清除所有地区缓存", notes = "清除所有地区缓存", response = JSONMessage.class)
    @ApiImplicitParam(name = "access_token", value = "token", required = true, dataType = "string", paramType = "query")
    @RequestMapping("/clearAreaListDbCache")
    public JSONMessage clearAreaListDbCache() {
        AreaDbCache.clearAreaListDbCache();
        return JSONMessage.success();
    }

    /**
	 * @api {get} /base/getHotAndAllAreas 获取所有地区以及热门城市列表
	 * @apiVersion 1.0.0
	 * @apiName getHotAndAllAreas
	 * @apiGroup 基础数据
	 * @apiDescription 获取所有地区以及热门城市列表
	 *
	 * @apiSuccess {List} 	    all 	         所有的城市集合
	 * @apiSuccess {String} 	all.name 	名称
	 * @apiSuccess {String} 	all.code 	地区编码
	 * @apiSuccess {String} 	all.pcode 	父编码
	 *  @apiSuccess {List} 	    hot 	         所有热门城市集合
	 * @apiSuccess {String} 	hot.name 	名称
	 * @apiSuccess {String} 	hot.code 	地区编码
	 * @apiSuccess {String} 	hot.pcode 	父编码
	 *
	 * @apiAuthor 张垠
	 * @date 2016年06月04日
	 */
    @RequestMapping("/getHotAndAllAreas")
    public JSONMessage getHotAndAllAreas(){
    	Map<String,List<AreaVO>> map = new HashMap<String,List<AreaVO>>();
    	map.put("all", baseDataService.getAllAreas());
    	map.put("hot", baseDataService.getHotCityList());
    	return JSONMessage.success(null, map);
    }

    /**
     * @api {get} /base/getHospitals 获取医院
     * @apiVersion 1.0.0
     * @apiName getHospitals
     * @apiGroup 基础数据
     * @apiDescription 根据地区获取地区
     *
     * @apiParam  {Number}    id                    地区id，可选
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
    public JSONMessage getHospitals(@RequestParam(defaultValue = "0") Integer id,String name) {
        return JSONMessage.success(null, baseDataService.getOkStatusHospitals(id,name));
    }

    /**
     * 增量获取医院数据
     * @auth : sharp
     * @desc : TODO
     * @date: 2017年6月8日 上午11:23:17
     * @param timeline
     * @return
     * JSONMessage
     */
    @RequestMapping("/getHospitalList")
    public JSONMessage getHospitals(Long timeline) {
        return JSONMessage.success(baseDataService.getHospitals(timeline));
    }

    @RequestMapping("/getHospitalByName")
    public JSONMessage getHospitalByName(String name) {
        return JSONMessage.success(null, baseDataService.getHospitals(name));
    }

    @RequestMapping("/getHospital")
    public JSONMessage getHospital(String hospitalId) {
        return JSONMessage.success(null, baseDataService.getHospital(hospitalId));
    }

    /**
     * @api {get} /base/getDepts 获取科室
     * @apiVersion 1.0.0
     * @apiName getDepts
     * @apiGroup 基础数据
     * @apiDescription 获取科室
     *
     * @apiParam  {Number}    id                    科室id（如果为空查询一级科室，不为空查找子科室）
     *
     * @apiSuccess {String}   id                    科室id
     * @apiSuccess {String}   name                  科室名称
     * @apiSuccess {String}   isLeaf                是否为叶子节点(1:是；0：否)
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDepts")
    public JSONMessage getDepts(String id) {
        return JSONMessage.success(null, baseDataService.getDepts(id,null));
    }

    /**
     * @api {get} /base/getAllDepts 获取所有科室
     * @apiVersion 1.0.0
     * @apiName getAllDepts
     * @apiGroup 基础数据
     * @apiDescription 获取所有科室
     *
     * @apiSuccess {String}   id          科室id
     * @apiSuccess {String}   name        科室名称
     * @apiSuccess {String}   isLeaf      是否为叶子节点(1:是；0：否)
     * @apiSuccess {String}   parentId    父id
     *
     * @apiAuthor  谢平
     * @date 2015年11月18日
     */
    @RequestMapping("/getAllDepts")
    public JSONMessage getAllDepts() {
        return JSONMessage.success(null, baseDataService.getAllDepts());
    }

    /**
     * @api {get} /base/getAllGroup 获取圈子(集团)
     * @apiVersion 1.0.0
     * @apiName getAllGroup
     * @apiGroup 基础数据
     * @apiDescription 获取圈子
     *
     * @apiSuccess {String}   id                    圈子id
     * @apiSuccess {String}   name                  圈子名称
     *
     * @apiAuthor  李敏
     * @date 2017年6月8日18:35:24
     */
    @RequestMapping("/getAllGroup")
    public JSONMessage getAllGroupForEs() {
        return JSONMessage.success(null, baseDataService.getAllGroup());
    }

    /**
     * @api {get} /base/getTitles 获取职称
     * @apiVersion 1.0.0
     * @apiName getTitles
     * @apiGroup 基础数据
     * @apiDescription 获取职称
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
     * @api {get} /base/getDiseaseTree 获取科室病种树
     * @apiVersion 1.0.0
     * @apiName getDiseaseTree
     * @apiGroup 基础数据
     * @apiDescription 获取科室病种树
     *
     * @apiSuccess {String}   id                     病种id
     * @apiSuccess {String}   name                   病种名称
     * @apiSuccess {String}   department             有值为病种，无值为科室
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDiseaseTree")
    public JSONMessage getDiseaseTree() {
        return JSONMessage.success(null, baseDataService.getDiseaseTypeTree());
    }

    /**
     * @api {get} /base/getDisease 根据父节点获取病种
     * @apiVersion 1.0.0
     * @apiName getDisease
     * @apiGroup 专长
     * @apiDescription 根据父节点获取专长，parentId为空查找一级病种
     *
     * @apiParam   {String}   parentId               父节点id
     *
     * @apiSuccess {String}   id                     病种id
     * @apiSuccess {String}   name                   病种名称
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDisease")
    public JSONMessage getDisease(String parentId) {
        return JSONMessage.success(null, baseDataService.getDiseaseByParent(parentId));
    }

    /**
     * @api {get} /base/getDiseases 批量获取病种
     * @apiVersion 1.0.0
     * @apiName getDiseases
     * @apiGroup 专长
     * @apiDescription 批量获取病种
     *
     * @apiParam   {String}   ids                    ids
     *
     * @apiSuccess {String}   id                     病种id
     * @apiSuccess {String}   name                   病种名称
     *
     * @apiAuthor  xieping
     * @date 2015年7月2日
     */
    @RequestMapping("/getDiseases")
    public JSONMessage getDiseases(List<String> ids) {
        return JSONMessage.success(baseDataService.getDiseaseType(ids));
    }

    /**
     * @api {get} /base/getOneLevelDisease 根据父节点获取专长（获取到“科室－病种一级”）
     * @apiVersion 1.0.0
     * @apiName getOneLevelDisease
     * @apiGroup 专长
     * @apiDescription 根据父节点获取专长，parentId为空查找顶级病种（科室）
     *
     * @apiParam   {String}   parentId               父节点id
     *
     * @apiSuccess {String}   id                     病种id
     * @apiSuccess {String}   name                   病种名称
     *
     * @apiAuthor  dwju
     * @date 2015年11月18日
     */
    @RequestMapping("/getOneLevelDisease")
    public JSONMessage getOneLevelDisease(String parentId) {
        return JSONMessage.success(null, baseDataService.getOneLevelDiseaseByParent(parentId));
    }

    /**
     * @api {get} /base/getServiceItem 根据父节点获取服务项
     * @apiVersion 1.0.0
     * @apiName getServiceItem
     * @apiGroup 基础数据
     * @apiDescription 根据父节点获取服务项，parentId为空查找一级服务项
     *
     * @apiParam   {String}   parentId               父节点Id
     *
     * @apiSuccess {String}   id                     服务项Id
     * @apiSuccess {String}   name                   服务项名称
     * @apiSuccess {Integer}  price                  服务项价格
     *
     * @apiAuthor  谢平
     * @date 2016年4月27日
     */
    @RequestMapping("/getServiceItem")
    public JSONMessage getServiceItem(String parentId) {
        return JSONMessage.success(baseDataService.getServiceItemByParent(parentId));
    }

    /**
     * @api {get} /base/getCheckSuggest 检查建议
     * @apiVersion 1.0.0
     * @apiName getCheckSuggest
     * @apiGroup 专长
     * @apiDescription 检查建议
     *
     * @apiParam   {String}   			parentId                父节点Id，查找第一级时，参数parentId为"0"或者null或者""
     *
     * @apiSuccess {CheckSuggest[]}  	data            		返回检查建议CheckSuggest对象数组
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getCheckSuggest")
    public JSONMessage getCheckSuggestion(String parentId) {
        return JSONMessage.success(null, baseDataService.getCheckSuggestByParentId(parentId));
    }

    /**
     * @api {get} /base/getCheckSuggestItemList/{checkupId} 获取检查建议单项指标列表
     * @apiVersion 1.0.0
     * @apiName getCheckSuggestItemList
     * @apiGroup 专长
     * @apiDescription 获取检查建议单项指标列表
     *
     * @apiParam   {String}	checkupId	检查建议的id
     *
     * @apiSuccess {List}  	list   		对应的单项指标列表
     *
     * @apiAuthor  肖伟
     * @date 2016年12月12日
     */
    @RequestMapping(value="/getCheckSuggestItemList/{checkupId}", method=RequestMethod.GET)
    public JSONMessage getCheckSuggestionItemList(@PathVariable String checkupId) {
        return JSONMessage.success(null, baseDataService.getCheckSuggestItemListByCheckupId(checkupId));
    }

    /**
     * @api {get} /base/searchCheckSuggest 模糊搜索检查建议（最多返回50条）
     * @apiVersion 1.0.0
     * @apiName searchCheckSuggest
     * @apiGroup 专长
     * @apiDescription 模糊搜索检查建议
     *
     * @apiParam   {String}   			text                	模糊搜索的文本
     *
     * @apiSuccess {CheckSuggest[]}  	data            		返回检查建议CheckSuggest对象数组
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/searchCheckSuggest")
    public JSONMessage searchCheckSuggest(String text) {
    	return JSONMessage.success(null, baseDataService.searchCheckSuggest(text));
    }

    /**
     * @api {get} /base/saveMsgTemplate 新增或者更新文案模板
     * @apiVersion 1.0.0
     * @apiName saveMsgTemplate
     * @apiGroup 文案模板
     * @apiDescription 新增或者更新文案模板
     *
     * @apiParam   {String}   				id                		惟一Id（如果新增则为null，如果更新则为更新的id）
     * @apiParam   {String}   				category                分类(填写汉字，如：短信、IM消息)
     * @apiParam   {String}   				title                	标题
     * @apiParam   {String}   				content                	内容
     * @apiParam   {String}   				usage                	用途
     * @apiParam   {String}   				sample                	样例
     *
     * @apiSuccess {int}  					resultCode            	返回结果码
     * @apiSuccess {MsgTemplate}		data            		MsgTemplate对象
     *
     * @apiAuthor  dwju
     * @date 2015年7月2日
     */
    @RequestMapping("/saveMsgTemplate")
    public JSONMessage saveMsgTemplate(MsgTemplate param) {
    	return JSONMessage.success(null, baseDataService.saveMsgTemplate(param));
    }

    /**
     * @api {get} /base/queryMsgTemplateById 根据ID查询或者搜索文案模板
     * @apiVersion 1.0.0
     * @apiName queryMsgTemplateById
     * @apiGroup 文案模板
     * @apiDescription 根据ID文案模板
     *
     * @apiParam   	{String}   				id                			[精确匹配]唯一Id
     *
     * @apiSuccess 	{int}  					resultCode            		返回结果码
     * @apiSuccess 	{PageVO}				data	            		MsgTemplate对象
     *
     * @apiAuthor  dwju
     * @date 2015年11月24日
     */
    @RequestMapping("/queryMsgTemplateById")
    public JSONMessage queryMsgTemplateById(final String id) {
        return JSONMessage.success(null, baseDataService.queryMsgTemplateById(id));
    }

    /**
     * @api {get} /base/queryMsgTemplate 查询或者搜索文案模板（可分页获取）
     * @apiVersion 1.0.0
     * @apiName queryMsgTemplate
     * @apiGroup 文案模板
     * @apiDescription 查询或者搜索文案模板（可分页获取）
     *
     * @apiParam   	{String}   				id                			[精确匹配，可选参数]惟一Id
     * @apiParam   	{String}   				category                	[精确匹配，可选参数]分类(填写汉字，如：短信、IM消息)
     * @apiParam	{String}   				title                		[模糊匹配，可选参数]标题
     * @apiParam	{String}   				content                		[模糊匹配，可选参数]内容
     *
     * @apiParam  	{int}       			pageIndex               	页码，从0开始
     * @apiParam  	{int}       			pageSize  	         		每页的数量，默认为20
     *
     * @apiSuccess 	{int}  					resultCode            		返回结果码
     * @apiSuccess 	{PageVO}				data	            		PageVO对象
     * @apiSuccess 	{int}					data.pageCount	        	共计几页，页的数量
     * @apiSuccess 	{int}					data.pageIndex	                        页码，从0开始
     * @apiSuccess 	{int}					data.pageSize	           	每页数量
     * @apiSuccess 	{int}					data.total	           		总计
     * @apiSuccess 	{MsgTemplate[]}	data.pageData	           	MsgTemplate对象数组
     *
     * @apiAuthor  dwju
     * @date 2015年7月2日
     */
    @RequestMapping("/queryMsgTemplate")
    public JSONMessage queryMsgTemplate(MsgTemplateParam param) {
    	List<MsgTemplate> list = baseDataService.queryMsgTemplate(param);
    	long count = baseDataService.queryMsgTemplateCount(param);
    	PageVO page = new PageVO(list, count, param.getPageIndex(), param.getPageSize());
        return JSONMessage.success(null, page);
    }

    /**
     * @api {get} /base/deleteCopyWriterTemplateById 删除文案模板
     * @apiVersion 1.0.0
     * @apiName deleteCopyWriterTemplateById
     * @apiGroup 文案模板
     * @apiDescription 删除文案模板
     *
     * @apiParam   {String}   				id                		惟一Id
     *
     * @apiSuccess {int}  						resultCode            	返回结果码
     *
     * @apiAuthor  dwju
     * @date 2015年7月2日
     */
    @RequestMapping("/deleteCopyWriterTemplateById")
    public JSONMessage deleteCopyWriterTemplateById(String id) {
    	int result = baseDataService.deleteMsgTemplateById(id);
    	if (result > 0) {
    		return JSONMessage.success(null, result);
    	}else{
    		return JSONMessage.failure("没有删除成功");
    	}
    }

    /**
     * @api {get} /base/deleteCopyWriterTemplate 删除文案模板（可批量删除）
     * @apiVersion 1.0.0
     * @apiName deleteCopyWriterTemplate
     * @apiGroup 文案模板
     * @apiDescription 删除文案模板
     *
     * @apiParam   {String[]}   				ids                		惟一Id数组
     *
     * @apiSuccess {int}  						resultCode            	返回结果码
     *
     * @apiAuthor  dwju
     * @date 2015年7月2日
     */
    @RequestMapping("/deleteCopyWriterTemplate")
    public JSONMessage deleteCopyWriterTemplate(String[] ids) {
    	int result = baseDataService.deleteMsgTemplate(ids);
    	if (result > 0) {
    		return JSONMessage.success(null, result);
    	}else{
    		return JSONMessage.failure("没有删除成功");
    	}
    }

    @RequestMapping("/nextIdx")
    public JSONMessage nextIdx(Integer idxType) {
    	return JSONMessage.success(idxRepository.nextIdx(idxType));
    }

    @RequestMapping("/nextDoctorNum")
    public JSONMessage nextDoctorNum(Integer idxType) {
    	return JSONMessage.success(idxRepository.nextDoctorNum(idxType));
    }

    /**
	 * @api {get} /base/findHospitalByCondition 模糊查找医院
	 * @apiVersion 1.0.0
	 * @apiName findHospitalByCondition
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 模糊查找医院
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
	@RequestMapping(value = "/findHospitalByCondition")
	public JSONMessage findHospitalByCondition(String keyWord,Integer pageIndex,Integer pageSize) {
		DoctorParam param = new DoctorParam();
		param.setKeyWord(keyWord);
		param.setPageSize(pageSize==null?param.getPageSize():pageSize);
		param.setPageIndex(pageIndex==null?param.getPageIndex():pageIndex);
		return JSONMessage.success(userManager.findHospitalByCondition(param));
	}

	@ApiOperation(value = "模糊查找医院，不分页", notes = "模糊查找医院，不分页", response = Map.class, httpMethod = "GET")
    @ApiImplicitParam(name = "keyword", value = "关键字", required = true, dataType = "String", paramType = "query")
    @RequestMapping(value = "/nologin/findHospitalByConditionNoPaging")
    public JSONMessage findHospitalByConditionNoPaging(String keyWord) {
        return JSONMessage.success(userManager.findHospitalByConditionNoPaging(keyWord));
    }

    /**
     * @api {get} /base/findAllGroupUnion 获取所有医联体
     * @apiVersion  1.0.0
     * @apiName findAllGroupUnion
     * @apiGroup 医联体
     * @apiDescription 获取所有医联体
     * @apiParam {string} access_token token
     * @apiParam {string} name 医联体名称
     * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
     * @apiParam {Integer} pageSize 每页的大小
     *
     * @apiSuccess {String}   id                    医联体id
     * @apiSuccess {String}   name                  医联体名称
     * @apiAuthor 李敏
     * @data 2017年6月6日10:20:38
     */
    @RequestMapping(value = "/findAllGroupUnion")
    public JSONMessage findAllGroupUnion(String name,@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize){
        return JSONMessage.success(null,baseDataService.getAllGroupUnionPage(name,pageIndex,pageSize));
    }

    /**
     * @api {get} /base/getUserIdByAdSope 根据范围获取user人数
     * @apiVersion  1.0.0
     * @apiName getUserIdByAdSope
     * @apiGroup 广告文章范围筛选
     * @apiDescription 根据范围获取user人数
     * @apiParam {string} access_token token
     * @apiParam {String}   type                    选择筛选范围类型 1：全平台 2：按条件筛选可领取范围 3: 按组织筛选可领取范围
     * @apiParam {Integer[]}   provinceIds           省级id数组
     * @apiParam {Integer[]}   cityIds               市级id数组
     * @apiParam {String[]}   levels                 医院级别名称数组
     * @apiParam {String[]}   deptIds                科室id数组
     * @apiParam {String[]}   titles                 医生职称名称
     * @apiParam {String[]}   groupIds               圈子id
     * @apiParam {String[]}   unionIds               医联体id
     *
     * @apiSuccess {Long}   data                  人数
     * @apiAuthor 李敏
     * @data 2017年6月9日11:37:24
     */
    @RequestMapping(value = "/getUserIdByAdSope")
    public JSONMessage getUserIdByAdSope(AdScopeForm adScopeForm) {
;        if (adScopeForm.getType() == null) {
            return null;
        }
        List<String> deptIds;
        List<String> groupIds;
        List<String> levels;
        List<String> titles;
        List<String> unionIds;

        if (adScopeForm.getDeptIds() == null || adScopeForm.getDeptIds().length == 0) {
            deptIds = new ArrayList<>();
        }else {
            deptIds=Arrays.asList(adScopeForm.getDeptIds());
        }
        if (adScopeForm.getGroupIds() == null || adScopeForm.getGroupIds().length == 0) {
            groupIds = new ArrayList<>();
        }else {
            groupIds=Arrays.asList(adScopeForm.getGroupIds());
        }
        if (adScopeForm.getLevels() == null || adScopeForm.getLevels().length == 0) {
            levels = new ArrayList<>();
        }else {
            levels=Arrays.asList(adScopeForm.getLevels());
        }
        if (adScopeForm.getTitles() == null || adScopeForm.getTitles().length == 0) {
            titles = new ArrayList<>();
        }else {
            titles=Arrays.asList(adScopeForm.getTitles());
        }
        if (adScopeForm.getUnionIds() == null || adScopeForm.getUnionIds().length == 0) {
            unionIds = new ArrayList<>();
        }else {
            unionIds=Arrays.asList(adScopeForm.getUnionIds());
        }
        boolean userCheck = false;
        if (adScopeForm.getUserCheck() != null){
            userCheck = adScopeForm.getUserCheck();
        }

        if (adScopeForm.getType().equals(AdScopeEnum.all.getIndex())) {
            return JSONMessage.success(null, user2Service.getNormalUser(userCheck));
        } else if (adScopeForm.getType().equals(AdScopeEnum.condition.getIndex())) {
            return JSONMessage.success(null, user2Service.getNormalUserIdByCityAndLevelAndDepartmentsAndTitle(adScopeForm.getAreaJson(),
                    levels, deptIds, titles,userCheck));
        } else if (adScopeForm.getType().equals(AdScopeEnum.organization.getIndex())) {
            return JSONMessage.success(null, user2Service.getNormalUserIdByUnionIdAndGroupId(groupIds, unionIds));
        }

        return null;
    }

    @ApiOperation(value = "院校基础数据导入", notes = "院校基础数据导入")
    @PostMapping(value = "college/loadSchoolExcel")
    public JSONMessage loadSchoolExcel(@RequestParam("file") MultipartFile file) throws Exception {
        baseDataService.loadSchoolExcel(file);
        return JSONMessage.success();
    }

    @ApiOperation(value = "增加院校基础数据(Web)", notes = "增加院校基础数据(Web);返回id(String)")
    @PostMapping(value = "college/addCollegeData")
    public JSONMessage addCollegeData(@RequestBody CollegeParam collegeParam) {
        LOGGER.info("运营后台获取院校信息:{}", "param = [" + JSON.toJSONString(collegeParam) + "]");
        return JSONMessage.success(baseDataService.addCollegeData(collegeParam));
    }

    @ApiOperation(value = "修改院校基础数据(Web)", notes = "修改院校基础数据(Web)")
    @PostMapping(value = "college/updateCollegeData")
    public JSONMessage updateCollegeData(@RequestBody CollegeParam collegeParam) {
        LOGGER.info("修改院校基础数据:{}", "param = [" + JSON.toJSONString(collegeParam) + "]");
        baseDataService.updateCollegeData(collegeParam);
        return JSONMessage.success();
    }

    @ApiOperation(value = "查询院校基础数据(Web)", notes = "查询院校基础数据(Web)", response = CollegesPO.class)
    @PostMapping(value = "college/getCollegeData")
    public JSONMessage getCollegeData(@RequestBody CollegeParam collegeParam) {
        LOGGER.info("查询院校基础数据:{}", "param = [" + JSON.toJSONString(collegeParam) + "]");
        return JSONMessage.success(baseDataService.getCollegeData(collegeParam));
    }

    @ApiOperation(value = "获取省市下的院校;根据院校名字模糊搜索(APP)", notes = "获取省市下的院校(APP)", response = CollegesPO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "collegeArea", value = "院校省市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "collegeName", value = "院校名字", dataType = "String", paramType = "query"),
    })
    @RequestMapping(value = "college/getCollege", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONMessage getCollege(String collegeArea, String collegeName) {
        LOGGER.info("获取省市下的院校:{}", "collegeArea = [" + collegeArea + "], collegeName = [" + collegeName + "]");
        return JSONMessage.success(baseDataService.getCollege(collegeArea, collegeName));
    }

    @ApiOperation(value = "获取院校院系(Web & APP)", response = CollegeDeptPO.class, notes = "获取院校院系(Web & APP)")
    @GetMapping(value = "college/getCollegeDept")
    public JSONMessage getCollegeDept() {
        return JSONMessage.success(baseDataService.getCollegeDept());
    }

}
