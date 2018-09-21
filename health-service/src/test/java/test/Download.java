package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.dachen.util.FileUtil;

public class Download {
	public static int count = 0;
	public static String b = "<script type=\"text/javascript\">var _bdhmProtocol = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");document.write(unescape(\"%3Cscript src='\" + _bdhmProtocol + \"hm.baidu.com/h.js%3Fc1db76f3814dcb5c26b89de9614beba8' type='text/javascript'%3E%3C/script%3E\"));</script>";
	public static String a = "<script type=\"text/javascript\"src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\"></script>";

	public static void doFtech(File parent) throws Exception {
		for (File child : parent.listFiles()) {
			if (child.isFile()) {
				if (child.getName().substring(child.getName().lastIndexOf(".")).equals(".html")) {
					FileInputStream fis = new FileInputStream(child);
					String body = FileUtil.readAll(fis);
					body = body.replace(a, "").replace(b, "");

					BufferedWriter writer = new BufferedWriter(new FileWriter(child));
					writer.write(body);
					writer.flush();
					writer.close();

					System.out.println(child.getPath());
					count++;
					System.out.println(count);
				}
			} else if (child.isDirectory()) {
				doFtech(child);
			} else {
				return;
			}
		}
	}

	// file:///D:/Manual/game.ceeger.com/Manual/ProjectView40.html
	public static void main(String[] args) throws Exception {
		File parent = new File("D:/Manual/game.ceeger.com/Manual/");
		doFtech(parent);
		System.out.println(count);

	}

	public static void d(File child) throws Exception {
		String urlString = "http://game.ceeger.com/Manual/"
				+ child.getPath().replace("D:\\Manual\\game.ceeger.com\\Script\\", "").replace("\\", "/");
		System.out.println(urlString);
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		String c = FileUtil.readAll(conn.getInputStream());

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(child.getPath())));
		writer.write(c);
		writer.flush();
		writer.close();

		count++;
		System.out.println(count);
	}

}
