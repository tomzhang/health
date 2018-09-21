package com.dachen.health.controller.system;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.page.PageVO;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.system.entity.param.FeedBackQuery;
import com.dachen.health.system.entity.po.FeedBack;
import com.dachen.health.system.service.IFeedBackManager;
import com.dachen.util.ReqUtil;

/**
 * 提供意见反馈的相关接口
 * @author  qujunli
 */

@RestController
@RequestMapping("/feedback")
public class FeedBackController  extends AbstractController {
	    @Autowired
	    private IFeedBackManager feedBackManager;
	    
	    /**
	     * 新增一个意见反馈
	     * </br>此接口需要登录才可调用
	     *
	     * @param FeedBack {@link FeedBack}  其中content 意见反馈内容，必填
	     * @return
	     * 
	     */
	    @RequestMapping(value = "/save")
	    public JSONMessage saveFeedBack(@ModelAttribute FeedBack feedBack) {
	    	feedBack.setUserId(ReqUtil.instance.getUserId());
	        feedBackManager.saveFeedBack(feedBack);
	       return JSONMessage.success();
	    }
	    
	    /**
	     * 根据条件查询意见反馈，同时分页
	     * </br>此接口需要登录才可调用
	     *
	     * @param   FeedBackQuery  {@link FeedBackQuery}
	     * @return   PageVO  {@link PageVO}，PageVo封装页码信息和返回数据，PageVO中的pageData中的每个对象为{@link FeedBack}
	     */
	    @RequestMapping(value = "/query")
	    public JSONMessage queryFeedBack(@ModelAttribute FeedBackQuery feedBackQuery) {
	         Object data = feedBackManager.queryFeedBack(feedBackQuery);
	         return JSONMessage.success("成功", data);
	    }
	    
	    /**
	     * 根据反馈id查询反馈详细内容
	     * </br>此接口需要登录才可调用
	     *
	     * @param   id  String类型，意见反馈id
	     * @return   FeedBack  {@link FeedBack}
	     */
	    @RequestMapping(value = "/get")
	    public JSONMessage queryFeedBack(@RequestParam String id) {
	         Object data = feedBackManager.getFeedBackById(id);
	         return JSONMessage.success("成功", data);
	    }
	    
}
