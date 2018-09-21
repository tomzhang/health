package com.dachen.commons.file;

import java.io.File;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.file.data.DownloadFileData;

public class FileTools {
	private static final Logger logger = LoggerFactory.getLogger(FileTools.class);
	
	/**
	 * 根据文件key
	 * @param fileKey
	 * @return
	 */
	public static DownloadFileData downloadFile(String fileKey) {
		if (StringUtils.isBlank(fileKey) )
		{
			logger.error("fileKey is null");
			return null;
		}
        DownloadFileData entity=null;
		try {
			entity = MongoFileDB.getInstance().findFileById(fileKey);
		} catch (Exception e) {
			logger.error(e.getCause().getMessage());
		}
        return entity;
    }
	
	public static void removeFileById(String fileKey){
		try {
			MongoFileDB.getInstance().removeFileById(fileKey);
		} catch (Exception e) {
			logger.error(e.getCause().getMessage());
		}
	}
	
	/**
	 * 以流的方式将文件存储到mongo
	 * @param fileStream
	 * @param fileName 文件名，可以随意取名
	 * @param fileType 文件类型，类似text/html，text/plain，image/png;如果为空，则默认根据文件后缀取，无后缀则默认值为application/octet-stream
	 * @return
	 * @throws Exception
	 */
	public static String saveFile2Mongo(InputStream fileStream, String fileName, String contentType) throws Exception {
        if (StringUtils.isEmpty(contentType)) {
        	contentType = "application/octet-stream";
        }
        String fileKey = MongoFileDB.getInstance().saveFile2Mongo(fileStream, fileName, contentType);
        return fileKey;
    }
	
	/**
	 * 以文件的方式将文件存储到mongo
	 * @param file
	 * @param fileName 文件名，可以随意取名
	 * @param fileType 文件类型，类似text/html，text/plain，image/png;如果为空，则默认根据文件后缀取，无后缀则默认值为application/octet-stream
	 * @return
	 * @throws Exception
	 */
	public static String saveFile2Mongo(File file, String fileName, String contentType) throws Exception {
        if (StringUtils.isEmpty(contentType)) {
        	contentType = getMimeType(file.getPath());
        }
        String fileKey = MongoFileDB.getInstance().saveFile2Mongo(file, fileName, contentType);
        return fileKey;
    }
	
	private static String getMimeType(String fileUrl)    
		      throws java.io.IOException    
    {    
      FileNameMap fileNameMap = URLConnection.getFileNameMap();    
      String type = fileNameMap.getContentTypeFor(fileUrl);    
      if(StringUtils.isEmpty(type)){
    	  type = "application/octet-stream";
      }
      return type;    
    }  
}
