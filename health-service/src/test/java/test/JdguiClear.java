package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class JdguiClear {

	public static void main(String[] args) throws Exception {
		File rootFile = new File(
				"D:\\Workspace\\Java\\serverJson\\src\\com\\jx");
		doClear(rootFile);
	}

	public static void doClear(File parent) throws Exception {
		for (File child : parent.listFiles()) {
			if (child.isFile()) {
				BufferedReader reader = new BufferedReader(
						new FileReader(child));
				StringBuffer sb = new StringBuffer();
				String ln = null;
				while (null != (ln = reader.readLine())) {
					int startIndex = ln.indexOf("/*");
					int endIndex = ln.indexOf("*/");
					if (-1 != startIndex && -1 != endIndex) {
						// String ss = ln.substring(startIndex, endIndex + 2);
						String s = ln.substring(endIndex + 2);
						// System.out.println(s);
						sb.append(s).append("\r\n");
					}
				}
				reader.close();
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(child));
				writer.write(sb.toString());
				writer.flush();
				writer.close();
				// if
				// (child.getName().substring(child.getName().lastIndexOf("."))
				// .equals(".html")) {
				// FileInputStream fis = new FileInputStream(child);
				// String body = FileUtil.readAll(fis);
				// body = body.replace(a, "").replace(b, "");
				//
				// BufferedWriter writer = new BufferedWriter(new FileWriter(
				// child));
				// writer.write(body);
				// writer.flush();
				// writer.close();
				// }
			} else if (child.isDirectory()) {
				doClear(child);
			} else {
				return;
			}
		}
	}
}
