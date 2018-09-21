package com.ucpaas.restsdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

@Deprecated
@Order(1)
public class SysConfig {
	private Properties props = null;// config.properties
	private static org.slf4j.Logger log = LoggerFactory.getLogger(SysConfig.class);
	private static volatile SysConfig conf;

	private SysConfig() {
		props = new Properties();
		loadConfigProps();
	}

	public static SysConfig getInstance() {
		if (conf == null) {
			synchronized (SysConfig.class) {
				if (conf == null) {
					conf = new SysConfig();
				}
			}
		}
		return conf;
	}

	public void loadConfigProps() {
		InputStream is = null;
		try {
		  URL url=	getClass().getProtectionDomain().getClassLoader().getResource("/");
            String propFileName = "health.properties";

		  log.info("url +"+url);
			if(url!=null){
				String configPath=url.getPath()+"properties/" + propFileName;
				System.out.println(configPath);
				try {
					is = new FileInputStream(new File(configPath));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(is==null){
					 is= getClass().getResourceAsStream(configPath);
				}
			}
			
			if (is == null) {
				is = getClass().getResourceAsStream("/" + propFileName);
			}
			if (is == null) {
				is = getClass().getResourceAsStream("properties/" + propFileName);
			}
			if (is == null) {
				File file=new File(".");
				System.out.println(file.getAbsolutePath());
				String path=file.getAbsolutePath()+"/src/main/resources/properties/" + propFileName;
				is=new FileInputStream(new java.io.File(path));
				//is = getClass().getResourceAsStream("。。/health-im-api/src/main/resources/properties/yunzhixunconfig.properties");
			}
			InputStreamReader reader = new InputStreamReader(is, "UTF-8");
			props.load(reader);
			Iterator<String> iter = props.stringPropertyNames().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				props.setProperty(key, props.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("load config.properties error!please check the file!", e);
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getProperty(String key) {
		String tmp = props.getProperty(key);
		if (StringUtils.isNotEmpty(tmp)) {
			return tmp.trim();
		}
		return tmp;
	}

	public String getProperty(String key, String defaultValue) {
		String tmp = props.getProperty(key, defaultValue);
		if (StringUtils.isNotEmpty(tmp)) {
			return tmp.trim();
		}
		return tmp;
	}

	public int getPropertyInt(String key) {
		String tmp = props.getProperty(key);
		if (StringUtils.isNotEmpty(tmp)) {
			return Integer.parseInt(tmp.trim());
		}
		return 0;

	}

	public int getPropertyInt(String key, int defaultValue) {
		String tmp = props.getProperty(key);
		if (StringUtils.isNotEmpty(tmp)) {
			return Integer.parseInt(tmp.trim());
		}
		return defaultValue;
	}

	public long getPropertyLong(String key, long defaultValue) {
		String tmp = props.getProperty(key);
		if (StringUtils.isNotEmpty(tmp)) {
			return Integer.parseInt(tmp.trim());
		}
		return defaultValue;
	}
}
