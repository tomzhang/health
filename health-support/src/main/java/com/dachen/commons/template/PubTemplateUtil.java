package com.dachen.commons.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.dachen.util.DateUtil;

public class PubTemplateUtil {
	public static void main(String[] args) throws IOException {
		//PubTemplateUtil.repPubContent("111", "dd.jpg", "111111111111");
		
		final String dateStr = DateUtil.formatDate2Str(System.currentTimeMillis(),
				DateUtil.FORMAT_YYYY_MM_DD_HH_MM);
		System.out.println(dateStr);
	}

	/**
	 * 公众号模版替换
	 * 
	 * @param title
	 *            文章title
	 * @param pic
	 *            图片
	 * @param content
	 *            上下文
	 * @return 返回替换后的字符串
	 * @throws IOException
	 */
	public static StringBuffer repPubContent(final String title, final String pic, final String content,String author) {
		File tempFile = new File(PubTemplateUtil.class.getResource("/template_pub/index.html").getPath());

		StringBuffer sf = new StringBuffer();
		try {
			FileReader reader = new FileReader(tempFile);
			BufferedReader br = new BufferedReader(reader);

			String str = "";
			while ((str = br.readLine()) != null) {
				if (str.contains("{{title}}")) {
					str = str.replace("{{title}}", title);
				}
				if (str.contains("{{date}}")) {
					final String dateStr = DateUtil.formatDate2Str(System.currentTimeMillis(),
							DateUtil.FORMAT_YYYY_MM_DD);//应产品要求，时间显示到天
					str = str.replace("{{date}}", dateStr);
				}
				if (str.contains("{{imgSrc}}")) {
					str = str.replace("{{imgSrc}}", pic);
				}

				if (str.contains("{{content}}")) {
					if (content != null) {
						str = str.replace("{{content}}", content);
					} else {
						str = str.replace("{{content}}", "");
					}
				}
				if (str.contains("{{author}}")) {
					str = str.replace("{{author}}", author);
				}
				sf.append(str);
			}
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return sf;
	}
}
