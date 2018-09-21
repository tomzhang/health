package com.dachen.pub.util;

import java.util.ArrayList;
import java.util.List;

import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;

public class WelcomeMessageHelper {
	public static List<ImgTextMsg> getGroupHelpMsg(){
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>(3);
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("如何开通服务套餐");
		msg.setStyle(0);
		msg.setUrl(PropertiesUtil.getFileServerURL()+"/health/web/attachments/beginner_guide/how_to_make_a_service_bundle.html");
		msg.setPic(PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pubmsg/makeService.png"));
		mpt.add(msg);
		
		msg = new ImgTextMsg();
		msg.setTitle("如何加入医生圈子");
		msg.setStyle(1);
		msg.setUrl(PropertiesUtil.getFileServerURL()+"/health/web/attachments/beginner_guide/how_to_join_a_group.html");
		msg.setPic(PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pubmsg/joinGroup.png"));
		mpt.add(msg);
		
		msg = new ImgTextMsg();
		msg.setStyle(1);
		msg.setTitle("如何创建医生圈子");
		msg.setUrl(PropertiesUtil.getFileServerURL()+"/health/web/attachments/beginner_guide/how_to_create_a_group.html");
		msg.setPic(PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pubmsg/createGroup.png"));
		mpt.add(msg);
		return mpt;
	}
	
	public static List<ImgTextMsg>getWelcomeMsg(String client,int userType){
		ImgTextMsg msg = new ImgTextMsg();
		String title = null;
		String pic = null;
		String url = null;
		if(PubUtils.BDJL.equalsIgnoreCase(client)){
			title = "欢迎使用博德嘉联医生集团掌上医院";
			pic = PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pub/bdjl/welcome.png");
			url= PropertiesUtil.getFileServerURL()+"/health/web/bd_h5/attachments/join_guide/welcome_guider.html";
		}else{
			title = "欢迎您使用玄关健康";
			pic = PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pub/welcome.png");
			url= PropertiesUtil.getFileServerURL()+"/health/web/attachments/join_guide/welcome_guider.html";
			
		}
		msg.setTitle(title);
		msg.setPic(pic);
		msg.setDigest("如果您在使用过程中遇到了任何问题，可以随时在这里向我们反馈。");
		if(userType==3){
			msg.setUrl(url);
		}
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>(1);
		mpt.add(msg);
		return mpt;
	}
	
	public static List<ImgTextMsg> getEduGuiderWelcomeMsg(){
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>(1);
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("爱心宣教使用方法");
		msg.setUrl(PropertiesUtil.getFileServerURL()+"/health/web/attachments/edu_guider/edu_guider.html");
		msg.setPic(PropertiesUtil.addQiniuUrlPrefix(QiniuUtil.DEFAULT_BUCKET,"pub/banner_love.png"));
		msg.setDigest("医生可以通过爱心宣教给患者发送医疗科普、健康、饮食等方面的文章，有助于帮助患者更好的了解健康知识。");
		mpt.add(msg);
		return mpt;
	}
}
