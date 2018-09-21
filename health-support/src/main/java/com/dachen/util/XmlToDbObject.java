package com.dachen.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class XmlToDbObject extends DefaultHandler {
	private List<DBObject> rootlist = null; 
	private List<DBObject> childrenList = null;
    private DBObject requestObject = null;  
    private DBObject memberObject = null;
	private String preTag = null;//作用是记录解析时的上一个节点名称
	private boolean isMember = false;
	

	@Override
	public void startDocument() throws SAXException {
		rootlist = new ArrayList<DBObject>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if("request".equals(qName)){  
			requestObject = new BasicDBObject();  
			isMember = false;
        }else if("members".equals(qName)){
        	childrenList = new ArrayList<DBObject>();
        }else if("member".equals(qName)){
        	memberObject = new BasicDBObject();
        	isMember =true;
        }
        preTag = qName;//将正在解析的节点名称赋给preTag  
	}
	
	@Override  
    public void characters(char[] ch, int start, int length) throws SAXException { 
		if(preTag!=null ){ 
			 String content = new String(ch,start,length);
			if(isMember == true){
				memberObject.put(preTag, content);
			}else{
				requestObject.put(preTag, content);
			}
        } 
    }  
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if("request".equals(qName)){  
			rootlist.add(requestObject);  
			requestObject = null;  
        }else if("members".equals(qName)){
        	requestObject.put("members", childrenList);
        }else if("member".equals(qName)){
        	isMember = false;
        	childrenList.add(memberObject);
        	memberObject = null;
        }
        preTag = null;
	}
	
	@Override
	public void endDocument() throws SAXException {
		System.err.println(rootlist.size());
	}
	
	public static List<DBObject> getDBObjects(InputStream xmlStream) throws Exception{  
        SAXParserFactory factory = SAXParserFactory.newInstance();  
        SAXParser parser = factory.newSAXParser();  
        XmlToDbObject handler = new XmlToDbObject();  
        parser.parse(xmlStream, handler);  
        return handler.rootlist;  
    }  

	public static void main(String[] args) {
		String xmString = (new StringBuilder("<?xml version='1.0'?><request>")
	            .append(" <event>confCreate</event>")
	            .append("<appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>")
	            .append("<members>")
	            .append("<member>")
	            .append("<callId>cl3333</callId>")
	            .append("<confId>cf2222</confId>")
	            .append("</member>")
	            .append("<member>")
	            .append("<callId>cl5555</callId>")
	            .append("<confId>cf4444</confId>")
	            .append("</member>")
	            .append("</members>")
	            .append("</request>")).toString();
		try {
			InputStream is = new ByteArrayInputStream(xmString.getBytes("UTF-8"));
			List<DBObject> list = XmlToDbObject.getDBObjects(is);
			System.err.println("all is over"+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
