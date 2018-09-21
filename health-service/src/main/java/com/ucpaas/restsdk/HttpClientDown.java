package com.ucpaas.restsdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dachen.util.PropertiesUtil;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.util.StringUtil;


public class HttpClientDown {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientDown.class);
	public static final int cache = 10 * 1024;  
    public static final boolean isWindows;  
    public static final String path;  
    private static String accountSid(){
        return PropertiesUtil.getContextProperty(
                "accountSid");
    }
	private static String authToken() {
        return PropertiesUtil.getContextProperty(
                "authToken");
    }
    static {  
        if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("windows")) {  
            isWindows = true;  
            path = DownTaskEnum.DOWN_PATH_WINDOW;
        } else {  
            isWindows = false;  
            path = DownTaskEnum.DOWN_PATH_LINUX ;
        }  
    }  
      
    /** 
     * 根据url下载文件，文件名从response header头中获取 
     * @param url 
     * @return 
     */  
    public static Map<String,Object> downloadRecod(String url,String recordId) { 
    	Map<String,Object> map = new HashMap<String, Object>();
    	//MD5加密
		try {
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容 
			String sig = accountSid() + recordId + authToken();
			String signature = encryptUtil.md5Digest(sig);
			url = url + "?sig=" + signature;
			return download(url, null);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", false);
			return map;
		}
         
    }  
  
    /** 
     * 根据url下载文件，保存到filepath中 
     * @param url 
     * @param filepath 
     * @return 
     */  
    public static Map<String,Object> download(String url, String filepath) {  
    	Map<String,Object> map = new HashMap<String, Object>();
    	boolean result = false;
        try {  
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);  
            HttpResponse response = client.execute(httpget);  
  
            HttpEntity entity = response.getEntity();  
            Header[] header = response.getAllHeaders();
            String fileType = "";
            String fileName = "";
            logger.info("download  header", header);
            for(Header temp :header){
            	 logger.info("download  temp", temp);
            	if(temp.getName().equalsIgnoreCase("Content-Type")){
            		fileType = temp.getValue();
            	}else if(temp.getName().equalsIgnoreCase("Content-Disposition")){
            		String str = temp.getValue();
            		str = str.substring(str.indexOf("filename"));
            		fileName=str.substring(str.indexOf("=")+1);
            	}
            }
            logger.info("download  fileType", fileType);
            logger.info("download  fileName", fileName);
            if(fileType.contains("mp3") ){
            	fileType=".mp3";
            }else if(fileType.contains("wav")){
            	fileType=".wav";
            }else {
            	//TODO 
            	 map.put("result", result);
                 return map;  
            }
            if(StringUtil.isEmpty(fileName)){
            	fileName = UUID.randomUUID().toString().replace("-", "");
            	fileName += fileType;
            }
            if (filepath == null)  {
            	 filepath = path + fileName;
            }
                
            File file = new File(filepath);  
            file.getParentFile().mkdirs();  
            FileOutputStream fileout = new FileOutputStream(file);  
            /** 
             * 根据实际运行效果 设置缓冲区大小 
             */  
            byte[] buffer=new byte[cache];  
            int ch = 0;  
            InputStream is = entity.getContent();
            while ((ch = is.read(buffer)) != -1) {  
                fileout.write(buffer,0,ch);  
            }  
            is.close();  
            fileout.flush();  
            fileout.close();  
            result = true;
			map.put("path", filepath);
			map.put("uuid", fileName);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        map.put("result", result);
        return map;  
    }  
    
    /** 
     * 获取response header中Content-Disposition中的filename值 
     * @param response 
     * @return 
     */  
    public static String getFileName(HttpResponse response) {  
        Header contentHeader = response.getFirstHeader("Content-Disposition");  
        String filename = null;  
        if (contentHeader != null) {  
            HeaderElement[] values = contentHeader.getElements();  
            if (values.length == 1) {  
                NameValuePair param = values[0].getParameterByName("filename");  
                if (param != null) {  
                    try {  
                        //filename = new String(param.getValue().toString().getBytes(), "utf-8");  
                        //filename=URLDecoder.decode(param.getValue(),"utf-8");  
                        filename = param.getValue();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
        return filename;  
    }  
    /** 
     * 获取随机文件名 
     * @return 
     */  
    public static String getRandomFileName() {  
        return String.valueOf(System.currentTimeMillis());  
    }  
    public static void outHeaders(HttpResponse response) {  
        Header[] headers = response.getAllHeaders();  
        for (int i = 0; i < headers.length; i++) {  
            System.out.println(headers[i]);  
        }  
    }  
    
   
    public static void main(String[] args) {  
    	 String url="http://www.baidu.com";  
         String filepath = "D:\\test\\q.mp3";  
         HttpClientDown.download(url,"123");  
    }  
}
