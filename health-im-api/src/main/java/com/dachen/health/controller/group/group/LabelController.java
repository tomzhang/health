package com.dachen.health.controller.group.group;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.common.entity.param.LabelParam;
import com.dachen.health.group.common.entity.po.Label;
import com.dachen.health.group.common.service.ILabelService;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
@RestController
@RequestMapping("/common/label")
public class LabelController extends AbstractController {

	@Autowired
	private ILabelService labelService;
	
	
	/**
     * @api {post} /common/label/addByLabel 添加标签
     * @apiVersion 1.0.0
     * @apiName addByLabel
     * @apiGroup 标签模块
     * @apiDescription 针对公司或医生集团 添加标签
     *
     *
     * @apiParam {String}   	relationId           	标签所属模块Id
     * @apiParam {String}   	name       				标签名称
     * @apiParam {String}   	description       		标签描述
     *
     * @apiSuccess {Number} 	resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月13日
	 */
	@RequestMapping("/addByLabel")
	public JSONMessage addByLabel(Label label) {
		label.setCreatorDate(new Date().getTime());
		labelService.saveLabel(label);
		return JSONMessage.success("success");
	}
	
	
	/**
     * @api {post} /common/label/updateByLabel 修改标签
     * @apiVersion 1.0.0
     * @apiName updateByLabel
     * @apiGroup 标签模块
     * @apiDescription 修改标签
     *
     * @apiParam {String}   	id           		标签Id
     * @apiParam {String}   	name       			标签名称
     * @apiParam {String}   	description       	标签描述
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月13日
	 */
	@RequestMapping("/updateByLabel")
	public JSONMessage updateLabel(Label label) {
		labelService.updateLabel(label);
		return JSONMessage.success("success");
	}
	
	
	/**
     * @api {post} /common/label/searchByLabel 查询标签列表
     * @apiVersion 1.0.0
     * @apiName searchByLabel
     * @apiGroup 标签模块
     * @apiDescription 查询标签列表
     *
     *
     * @apiParam {String}   	relationId           	标签所属模块Id
     * @apiParam {String}   	name       				标签名称
     * @apiParam {String}   	description       		标签描述
     *
     * @apiSuccess {String} 	id    					id
     * @apiSuccess {String} 	relationId    			所属模块Id
     * @apiSuccess {String} 	name    				标签名称
     * @apiSuccess {String} 	description    			标签描述
     * @apiSuccess {Integer} 	creator    				创建人
     * @apiSuccess {Long} 		creatorDate    			创建时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/searchByLabel")
	public JSONMessage searchByLabel(LabelParam label) {
		return JSONMessage.success(null, labelService.searchLabel(label));
	}
	
	/**
     * @api {post} /common/label/deleteByLabel 删除标签
     * @apiVersion 1.0.0
     * @apiName deleteByLabel
     * @apiGroup 标签模块
     * @apiDescription 批量删除标签
     *
     * @apiParam {String}   	ids           	标签Id
     *
     * @apiSuccess {Number} 	resultCode    	返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/deleteByLabel")
	public JSONMessage deleteByLabelId(String[] ids) {
		labelService.deleteLabel(ids);
		return JSONMessage.success("success");
	}
	
	
}
