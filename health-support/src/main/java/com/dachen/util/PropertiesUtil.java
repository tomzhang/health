package com.dachen.util;

import java.text.MessageFormat;

import org.springframework.core.env.Environment;

/**
 * 
 * @ProjectNmae：util.
 * @ClassName：PropertysUtil
 * @Description： properties配置文件工具类
 * @author：fanp
 * @crateTime：2013-7-3
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class PropertiesUtil {//extends PropertyPlaceholderConfigurer {
	
	public static Environment env;
	
//	private static Map<String, String> ctxPropertiesMap;

//	@Override
//	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
//		super.processProperties(beanFactory, props);
//		// 读取propertys到ctxPropertiesMap属性中
//		ctxPropertiesMap = new HashMap<String, String>();
//		for (Object key : props.keySet()) {
//			String keyStr = key.toString();
//			String value = props.getProperty(keyStr);
//			ctxPropertiesMap.put(keyStr, value);
//		}
//	}

	/**
	 * 获取propertys值
	 * 
	 * @param name
	 *            propertys的key值
	 * @return
	 * @author fanp
	 * @date 2013-7-3
	 */
	public static String getContextProperty(String name) {
		return env.getProperty(name);
	}

	/**
	 * 获取头像前缀
	 * @author 			李淼淼
	 * @date 2015年8月25日
	 */
	private static String fileDownLoadPrex = null;
	public static String getHeaderPrefix() {
		if(fileDownLoadPrex==null)
		{
			fileDownLoadPrex = getFileDownloadPrefix();
		}
		StringBuffer sb=new StringBuffer(fileDownLoadPrex);
		if(!StringUtil.isEmpty(getContextProperty("fileserver.basepath"))){
			sb.append("/");
			sb.append(getContextProperty("fileserver.basepath"));
		}
		return sb.toString();
	}
	public static String getFileServerURL() 
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getContextProperty("fileserver.protocol")).append("://");
		sb.append(getContextProperty("fileserver.host"));
		return sb.toString();
	}
	
	public static String getFileDownloadPrefix() {
		if(fileDownLoadPrex==null)
		{
			StringBuffer sb=new StringBuffer();
			sb.append(getContextProperty("fileserver.protocol")).append("://");
			sb.append(getContextProperty("fileserver.host")).append(":").append(getContextProperty("fileserver.port"));
			fileDownLoadPrex = sb.toString();
		}
		return fileDownLoadPrex;
	}
	
	//移除全路径的主机，端口，访问根路径
	public static String removeUrlPrefix(String url){
		if(StringUtil.isEmpty(url)){
			return url;
		}else{
			if(url.indexOf(PropertiesUtil.getHeaderPrefix())>-1){
				url=url.replace(PropertiesUtil.getHeaderPrefix(), "");
			}
			return url;
		}
	}
	//添加全路径的主机，端口，访问根路径
	public static String addUrlPrefix(String url){
		if(StringUtil.isEmpty(url)){
			return url;
		}else{
			//
			if(!isUrl(url) && url.indexOf(PropertiesUtil.getHeaderPrefix())<0&&(url.indexOf("http://")<0)){
				url=getHeaderPrefix()+url;
			}
			return url;
			
		}
	}
	
	public static String addQiniuUrlPrefix(String bucket,String key){
		if(StringUtil.isEmpty(key)){
			return null;
		}else{
			//
			boolean isUrl = isUrl(key) || key.indexOf(QiniuUtil.QINIU_DOMAIN())>=0;
			if(isUrl){
				return key;
			}
			if(key.startsWith("/")){
				key=key.substring(1);
			}
			return MessageFormat.format(QiniuUtil.QINIU_URL(), bucket, key);
		}
	}
	
	public static boolean isUrl (String url) {
		return url.matches("^((https|http|ftp|rtsp|mms)?://)" 
			     + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" 
			     + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 
			     + "|" 
			     + "([0-9a-z_!~*'()-]+\\.)*" 
			     + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." 
			     + "[a-z]{2,6})" 
			     + "(:[0-9]{1,4})?" 
			     + "((/?)|" 
			     + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"); 
    }
	
	public static void main(String[] args) {
		String a1 = "http://www.baidu.com";
		System.out.println(isUrl(a1));
		
		String a2 = "http://192.168.3.7:9080/aa.html";
		System.out.println(isUrl(a2));
		
		String a21 = "http://192.168.3.7:9080";
		System.out.println(isUrl(a21));
		
		String a3 = "http://7xnqca.media1.z0.glb.clouddn.com/1.pic_hd.jpg";
		System.out.println(isUrl(a3));
		
		String a4 = "7xnqca.media1.z0.glb.clouddn.com/1.pic_hd.jpg";
		System.out.println(isUrl(a4));
		
		String a5 = "http://7xnqca.media1.z0.glb/1.pic_hd.jpg";
		System.out.println(isUrl(a5));
		
	}
	
	/**
	 * 是否为非生产环境<br>
	 * 环境：生产测试环境(PreposeEnv)、测试环境(TestingEnv)、开发环境(DevelopmentEnv)
	 * @return
	 */
	public static boolean isNonProductionEnv() {
//		return "ProductionEnv".equals(getContextProperty("system.environment"));
		return "TestingEnv".equals(getContextProperty("system.environment"))
				|| "PreposeEnv".equals(getContextProperty("system.environment"))
				|| "DevelopmentEnv".equals(getContextProperty("system.environment"));
	}

	public static String addUrlParemeter(String url, String k, String v) {
		if(url != null && k != null && v != null){
			if(url.contains("?")){
				url += "&"+k+"="+v;
			} else {
				url += "?"+k+"="+v;
			}
		}
		return url;
	}

//    public static void put(String key, String value) {
//        if (null == ctxPropertiesMap) {
//            ctxPropertiesMap = new HashMap<>();
//        }
//        ctxPropertiesMap.put(key, value);
//    }
}
