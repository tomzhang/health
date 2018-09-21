package com.dachen.health.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.mongodb.morphia.AdvancedDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.commons.dao.DownTaskRepository;
import com.dachen.health.commons.entity.DownDetail;
import com.dachen.health.commons.entity.DownTask;
import com.dachen.health.commons.service.DownTaskService;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.ucpaas.restsdk.HttpClientDown;
import com.ucpaas.restsdk.UcPaasRestSdk;

@Service
public class DownTaskServiceImpl implements DownTaskService {
	
	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;
	
	Logger logger=LoggerFactory.getLogger(getClass());
//	private static String  DEFAULT_BUCKET= PropertiesUtil.getContextProperty("qiniu.call.bucket");
    private static String  DEFAULT_BUCKET() {
      return PropertiesUtil.getContextProperty("qiniu.call.bucket");
    }

//	private static String DEFALUT_DOMAIN = PropertiesUtil.getContextProperty("qiniu.call.domain");
    private static String DEFALUT_DOMAIN() {
        return PropertiesUtil.getContextProperty("qiniu.call.domain");
    }


	private static String Default_FORMAT = "mp3";
	@Autowired
	private DownTaskRepository dao;

	@Override
	public void save(DownTask dt) {
		String id = dao.sava(dt);
//		System.err.println(id);
	}
	@Override
	public List<DownTask> getAllToDown(DownTask dt){
		
		return dao.findAllTaskToDown(dt);
	}
	
	public static void main(String[] args) {
		
	}
	
	
	@Override
	public DownTask getDownTaskByUrl(String url) {
		// TODO Auto-generated method stub
		return dao.getDownTaskByUrl(url);
	}
	@Override
	public DownTask getDownTaskByRecordId(String recordId) {
		return  dao.getDownTaskByRecordId(recordId);
	}
	
	@Override
	public void downAndUpdate(DownTask  temp ){
		if(temp == null  || StringUtil.isEmpty(temp.getSourceUrl()) || StringUtil.isEmpty(temp.getRecordId())){
			return;
		}
		int status = DownTaskEnum.DownStatus.recordDownFail.getIndex();
		String url = temp.getSourceUrl();
		String recordId = temp.getRecordId();
		String filePath = null;
		String dest = null;
		Boolean downStatus  = false;
		Map<String,Object> map  = null;
		if(temp.getStatus() < DownTaskEnum.DownStatus.recordDownSuccess.getIndex() ){
			//下载失败,再次下载同时再次创建新文件
			map = HttpClientDown.downloadRecod(url,recordId);
			downStatus = Boolean.parseBoolean(map.get("result").toString());
		}else{
			//下载成功直接上传
			status = DownTaskEnum.DownStatus.recordDownSuccess.getIndex();
			filePath = temp.getFilePath();
			dest = filePath.substring(filePath.lastIndexOf(File.separator)+1); 
		}
		
		if(downStatus){
			status = DownTaskEnum.DownStatus.recordDownSuccess.getIndex();
			filePath = map.get("path").toString();
			dest = map.get("uuid").toString();
		}
		DownDetail dd = new DownDetail();
		dd.setStartTime(System.currentTimeMillis());
		dd.setEndTime(System.currentTimeMillis());
		dd.setStatus(status);
		temp.setStatus(status);
		temp.setFilePath(filePath);
		
		List<DownDetail> tList = temp.getDetails();
		if(tList == null){
			tList = new ArrayList<DownDetail>();
		}
		tList.add(dd);
		dao.updateDownTask(temp);//更新下载状态，添加下载明细
		
		if( status == DownTaskEnum.DownStatus.recordDownSuccess.getIndex()&&filePath!=null){
			String toUrl = QiniuUtil.upload(filePath, dest, DEFAULT_BUCKET());//上传
			if(!StringUtil.isEmpty(toUrl)){
				 status = DownTaskEnum.DownStatus.recordUploadSuccess.getIndex();
				 if(temp.getBussessType().equals(DownTaskEnum.TableToClass.callRecord.getBusessType())){//双向回拔
					 toUrl = "http://" + DEFALUT_DOMAIN() +"/" + toUrl;
					 temp.setToUrl(toUrl);
					 DBObject update = new BasicDBObject("videoUrl",toUrl);
		         	 dsForRW.getDB().getCollection("t_call_record").update(new BasicDBObject("callid", recordId),new BasicDBObject("$set",update),false,false);
				 }else if(temp.getBussessType().equals(DownTaskEnum.TableToClass.confRecord.getBusessType())){//电话会议
					 toUrl = "http://" + DEFALUT_DOMAIN() +"/" + toUrl;
					 temp.setToUrl(toUrl);
					 DBObject update = new BasicDBObject("videoUrl",toUrl);
		         	 dsForRW.getDB().getCollection("t_call_record").update(new BasicDBObject("recordId", recordId),new BasicDBObject("$set",update),false,false);
				 }
				
				 //上传成功，删除源文件
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
			}else{
				status = DownTaskEnum.DownStatus.recordUploadFail.getIndex();
				temp.setFilePath(filePath);
			}
			temp.setStatus(status);
			if(status == DownTaskEnum.DownStatus.recordUploadSuccess.getIndex()){
				temp.setFilePath("");
			}
			dao.updateDownTask(temp);
		}
	}
}
