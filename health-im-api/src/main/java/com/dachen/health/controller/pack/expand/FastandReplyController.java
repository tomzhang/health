package com.dachen.health.controller.pack.expand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.user.entity.param.FastandReplyParam;
import com.dachen.health.user.service.IFastandReplyService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/fastandReply")
public class FastandReplyController {
	
	@Autowired
	IFastandReplyService fastandReplyService;
	
	/**
     * @api {get} /pack/fastandReply/getFastandReply 快捷回复列表
     * @apiVersion 1.0.0
     * @apiName getFastandReply
     * @apiGroup 快捷回复
     * @apiDescription 快捷回复列表
     *
     * @apiParam   {String}          access_token         token
     * 
     * @apiSuccess {List}            reply                集合对象
     * @apiSuccess {String}          reply.replyId        快捷回复ID
     * @apiSuccess {String}          reply.replyContent   快捷回复内容
     * @apiSuccess {Integer}         reply.replyTime      快捷回复日期
     * @apiSuccess {Number=1}        resultCode           返回状态吗
     * @apiSuccess {int}             is_system            是否为系统预制
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/getFastandReply")
    public JSONMessage getFastandReply(FastandReplyParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
	    param.setUserType(ReqUtil.instance.getUser().getUserType());
        return JSONMessage.success(null,fastandReplyService.getAll(param));
    }
	
	/**
     * @api {get} /pack/fastandReply/addFastandReply 添加快捷回复
     * @apiVersion 1.0.0
     * @apiName addFastandReply
     * @apiGroup 快捷回复
     * @apiDescription 添加快捷回复
     *
     * @apiParam   {String}          access_token          token
     * @apiParam   {String}          replyContent          快捷回复内容
     * @apiParam   {int}             is_system             是否是系统预制数据  1--不是 0--是
     * 
     * @apiSuccess {Object}          reply                快捷回复对象
     * @apiSuccess {String}          reply.replyId        快捷回复ID
     * @apiSuccess {String}          reply.replyContent   快捷回复内容
     * @apiSuccess {Integer}         reply.replyTime      快捷回复日期
     * @apiSuccess {Number=1}        resultCode           返回状态吗
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/addFastandReply")
    public JSONMessage addFastandReply(FastandReplyParam param) {
		if(0==param.getIs_system()){
			throw new ServiceException("is_system 参数有误（非预制回复）！！！");
		}
		param.setUserId(ReqUtil.instance.getUserId());
	    param.setUserType(ReqUtil.instance.getUser().getUserType());
        return JSONMessage.success(null, fastandReplyService.add(param));
    }
	
	/**
     * @api {get} /pack/fastandReply/deleteFastandReply 删除快捷回复
     * @apiVersion 1.0.0
     * @apiName deleteFastandReply
     * @apiGroup 快捷回复
     * @apiDescription 删除快捷回复
     *
     * @apiParam   {String}          access_token          token
     * @apiParam   {String}          replyId               快捷回复ID
     * 
     * @apiSuccess {Number=1}        resultCode            返回状态吗
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/deleteFastandReply")
    public JSONMessage deleteFastandReply(FastandReplyParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
	    param.setUserType(ReqUtil.instance.getUser().getUserType());
	    fastandReplyService.delete(param);
        return JSONMessage.success();
    }
	
	/**
     * @api {get} /pack/fastandReply/updateFastandReply 修改快捷回复
     * @apiVersion 1.0.0
     * @apiName updateFastandReply
     * @apiGroup 快捷回复
     * @apiDescription 修改快捷回复
     *
     * @apiParam   {String}          access_token          token
     * @apiParam   {String}         replyId               快捷回复ID
     * @apiParam   {String}          replyContent          快捷回复内容
     * 
     * @apiSuccess {Number=1}        resultCode            返回状态吗
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/updateFastandReply")
    public JSONMessage updateFastandReply(FastandReplyParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
	    param.setUserType(ReqUtil.instance.getUser().getUserType());
	    fastandReplyService.update(param);
	    return JSONMessage.success();
    }
}
