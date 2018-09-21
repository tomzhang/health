package com.dachen.commons.file;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.springframework.stereotype.Component;

import com.dachen.commons.file.data.DownloadFileData;
import com.mongodb.DB;
import com.mongodb.ReadPreference;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Component
public class MongoFileDB {
	
	private static MongoFileDB instance;
	private static GridFS fs = null;
	
	@Resource(name = "dsForRW")
	private AdvancedDatastore dsForRW;
	
	private MongoFileDB(){
		instance = this;
	}
	
	public static MongoFileDB getInstance(){
		return instance;
	}
	
	private void init() throws Exception{
		if(fs==null){
			DB db = dsForRW.getDB();
			db.setReadPreference(ReadPreference.primaryPreferred());
			fs = new GridFS(db);
		}
        if (fs == null)
    	{
        	throw new IllegalAccessException("can not access MongoDB");
    	}
	}
	 /**
     * 下载文件
     */
	public DownloadFileData findFileById(String fileId) throws Exception {
		init();

        DownloadFileData entity = null;
        GridFSDBFile file = fs.findOne(new ObjectId(fileId));

        if (file != null) {
            entity = new DownloadFileData(file.getInputStream());
            entity.setFilename(file.getFilename());
            entity.setMimeType(file.getContentType());
        }

        return entity;
    }
	
	public void removeFileById(String fileId) throws Exception {
		 init();
         fs.remove(new ObjectId(fileId));
    }
	
	public String saveFile2Mongo(File file, String fileName, String contentType) throws Exception {

        if (file == null || StringUtils.isBlank(fileName) || StringUtils.isBlank(contentType))
        {
        	throw new IllegalAccessException("File or File Name or File Type is NULL");
        }
        init();
        
        GridFSInputFile mongoFile = fs.createFile(file);
        mongoFile.setFilename(fileName);
        mongoFile.setContentType(contentType);
        mongoFile.save();

        String fileKey = mongoFile.getId().toString();

        return fileKey;
    }

    public String saveFile2Mongo(InputStream fileStream, String fileName, String contentType) throws Exception {

        if (fileStream == null || StringUtils.isBlank(fileName) || StringUtils.isBlank(contentType))
        {
        	throw new IllegalAccessException("File or File Name or File Type is NULL");
        }

        init();

        GridFSInputFile mongoFile = fs.createFile(fileStream);
        mongoFile.setFilename(fileName);
        mongoFile.setContentType(contentType);
        mongoFile.save();

        String fileKey = mongoFile.getId().toString();

        return fileKey;
    }
}
