package com.dachen.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JsoupUtil {
	
	public static String getBody(String url){
		
		return "";
	}
	
	public static String getAllHtml(String url){
		return "";
	}
	
	public  static Document getDocument(String url){
		Document doc = null;
		if(!url.startsWith("https://") && !url.startsWith("http://") ){
			return doc;
		}
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			doc = null;
		}
		return doc;
	}

	public static void main(String[] args) {
		
		Document doc = getDocument("baidu.com");
		System.err.println("doc");
	}
}
