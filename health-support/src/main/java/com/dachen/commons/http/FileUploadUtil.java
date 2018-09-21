package com.dachen.commons.http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.im.server.data.response.Result;

@Component
public class FileUploadUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);
	
	public static final String FILE_BASE_URL = "http://FILE";
	
	@Autowired
	private RibbonManager ribbonManager;
	
	public boolean delDocument(String fileName, String filePath) {
//		try {
//			Map<String,String> paramMap = new HashMap<String,String>();
//			paramMap.put("path",path);
//			paramMap.put("fileName",fileName);
//			Object result = HttpRequestUtil.get(servletUrl, paramMap,0);
//			return true;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("filePath", filePath);
		paramMap.put("fileName", fileName);
		String response = ribbonManager.post(FILE_BASE_URL + "/file/delete", (Object) paramMap);
		logger.info("删除服务器文件。返回值：{}", response);
		return true;
	}
	
	/**
	 * 文件上传（Java调用Servlet）
	 * @param servletUrl servletUrl
	 * @param fileName 文件名
	 * @param fileContent 文件内容，如果文件内容不为空，则使用内容填充到文件
	 * @return 文件上传后的URL
	 */
	public String uploadToFileServer(String fileName, String filePath, String fileContent) {
//		final CloseableHttpClient httpclient = HttpClients.createDefault();
//		String fileUploadUrl = null;
//		try {
//			File file = new File(fileName);  
//			
//			if (!StringUtils.isEmpty(fileContent)) {
//				byte[] bytes = fileContent.getBytes();
//				
//			    OutputStream output = new FileOutputStream(file);  
//			    BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);  
//			    bufferedOutput.write(bytes); 
//			    bufferedOutput.flush();
//			    bufferedOutput.close();
//			    output.close();
//			}
//		    FileBody bin = new FileBody(file);
//		    
//		    HttpPost httppost = new HttpPost(servletUrl);
//		    
//			StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
//
//			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment).addPart("path", new StringBody(path,ContentType.TEXT_PLAIN))
//					.build();
//			httppost.setEntity(reqEntity);
//			CloseableHttpResponse response = httpclient.execute(httppost);
//
//			try {
//				HttpEntity resEntity = response.getEntity();
//				if (resEntity != null) {
//					final String respJson = EntityUtils.toString(resEntity);
//					UploadResponseBean bean = JSON.parseObject(respJson,UploadResponseBean.class);
//					if (bean != null && bean.getData() != null 
//							&& bean.getData().getOthers() != null
//							&& bean.getData().getOthers().length > 0) {
//						fileUploadUrl = bean.getData().getOthers()[0].getoUrl();
//					}
//
//					System.out.println(respJson);
//				}
//				EntityUtils.consume(resEntity);
//			} finally {
//				response.close();
//			}
//			file.delete();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			try {
//				httpclient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return fileUploadUrl;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("filePath", filePath);
		paramMap.put("fileName", fileName);
		paramMap.put("contentBytes", fileContent.getBytes());
		String response = ribbonManager.post(FILE_BASE_URL + "/file/upload", (Object) paramMap);
		logger.info("上传文件至服务器。返回值：{}", response);
		Result result = JSON.parseObject(response, Result.class);
		if (result.sucess()) {
			return result.getData().toString();
		}
		throw new ServiceException(result.getResultCode(), result.getDetailMsg());
	}
	
}
