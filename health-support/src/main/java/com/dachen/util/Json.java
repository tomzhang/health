package com.dachen.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 快速JSON转换分析工具类，只支持简单数据类型 及 Map、List的组合。
 * 
 * @author yuxichu
 * 
 */
public class Json {
	public static final int MAX_LEVEL = 36;
	public static final String JSON_PREFIX = "json:";
	public static final Object NULL = new Object();

	public static void escape(String s, StringBuilder sb) {
		sb.append('"');
		int i = 0;
		for (int j = s.length(); i < j; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\'':
				sb.append("\\'");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			default:
				sb.append(c);
			}
		}
		sb.append('"');
	}

	public static String toString(Object o) {
		return toString(o, false);
	}

	/**
	 * 
	 * @param o
	 * @param withoutBrackets
	 *            不需要小括号()
	 * @return
	 */
	public static String toString(Object o, boolean withoutBrackets) {
		if (o instanceof String) {
			String s = (String) o;
			if (s.startsWith(JSON_PREFIX)) {
				return s.substring(JSON_PREFIX.length());
			}
		}

		StringBuilder sb = new StringBuilder();
		toString(o, sb, 0, withoutBrackets);
		return sb.toString();
	}

	public static StringBuilder toString(Object o, StringBuilder buffer, int level) {
		return toString(o, buffer, level, false);
	}

	private static StringBuilder toString(Object o, StringBuilder buffer, int level, boolean withoutBrackets) {
		if (level >= MAX_LEVEL) {
			throw new RuntimeException(
					"Collection or Map nested level is too deep, and there may be exist a recursive reference.");
		}

		if (o == null || o == NULL) {
			return buffer.append("null");
		}

		if (o instanceof Boolean) {// 布尔值
			return buffer.append(o);
		}

		if (o instanceof BigDecimal) {// 小数, 非如此在JS中无法保留小数位数.
			return buffer.append('"').append(o).append('"');
		}

		if (o instanceof Number) { // 其他数值
			return buffer.append(o).append(' ');
		}

		if (o instanceof String) {// 字符串
			escape((String) o, buffer);
			return buffer;
		}

		if ((o instanceof Object[])) {
			return toString((Object[]) o, buffer, level + 1, withoutBrackets);
		}

		if ((o instanceof Collection<?>)) {
			return toString((Collection<?>) o, buffer, level + 1, withoutBrackets);
		}

		if ((o instanceof Map<?, ?>)) {
			return toString((Map<?, ?>) o, buffer, level + 1, withoutBrackets);
		}

		if (o instanceof java.sql.Date) {
			return buffer.append('"').append(o).append('"');
		}

		if (o instanceof java.sql.Time) {
			return buffer.append('"').append(o).append('"');
		}

		if (o instanceof java.util.Date) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return buffer.append('"').append(df.format((java.util.Date) o)).append('"');
		}

		if (o instanceof Entry) {
			Entry<?, ?> p = (Entry<?, ?>) o;
			if (withoutBrackets == false) {
				buffer.append("(");
			}

			buffer.append("{");
			buffer.append("\"key\":");
			toString(p.getKey(), buffer, level + 1, withoutBrackets);
			buffer.append(",\"value\":");
			toString(p.getValue(), buffer, level + 1, withoutBrackets);
			buffer.append("}");

			if (withoutBrackets == false) {
				buffer.append(")");
			}
			return buffer;
		}

		escape(o.toString(), buffer);
		return buffer;
	}

	private static StringBuilder toString(Collection<?> l, StringBuilder buffer, int level, boolean withoutBrackets) {
		if (withoutBrackets == false) {
			buffer.append("(");
		}
		buffer.append("[");
		boolean first = true;
		for (Iterator<?> it = l.iterator(); it.hasNext();) {
			if (first)
				first = false;
			else
				buffer.append(",");

			toString(it.next(), buffer, level + 1, withoutBrackets);
		}
		buffer.append("]");
		if (withoutBrackets == false) {
			buffer.append(")");
		}
		return buffer;
	}

	private static StringBuilder toString(Object[] a, StringBuilder buffer, int level, boolean withoutBrackets) {
		if (withoutBrackets == false) {
			buffer.append("(");
		}
		buffer.append("[");
		for (int i = 0; i < a.length; i++) {
			if (i > 0)
				buffer.append(",");
			toString(a[i], buffer, level + 1, withoutBrackets);
		}

		buffer.append("]");
		if (withoutBrackets == false) {
			buffer.append(")");
		}
		return buffer;
	}

	private static StringBuilder toString(Map<?, ?> m, StringBuilder buffer, int level, boolean withoutBrackets) {
		if (withoutBrackets == false) {
			buffer.append("(");
		}
		buffer.append("{");
		boolean first = true;
		for (Map.Entry<?, ?> entry : m.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			if (first)
				first = false;
			else {
				buffer.append(",");
			}
			Object key = entry.getKey();
			toString(key, buffer, level + 1, withoutBrackets);
			buffer.append(":");
			toString(value, buffer, level + 1, withoutBrackets);
		}

		buffer.append("}");
		if (withoutBrackets == false) {
			buffer.append(")");
		}
		return buffer;
	}

	public static Object toObject(String json) {
		if (json == null || json.length() == 0) {
			return null;
		}

		StringReader sr = new StringReader(json);
		try {
			if (json.startsWith(JSON_PREFIX)) {
				for (int i = 0; i < JSON_PREFIX.length(); i++) {
					sr.read();
				}
			}
			return toObject(sr);
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid argument: json");
		}
	}

	public static Object toObject(Reader sr) throws IOException {
		int i = sr.read();
		if (i == 40)
			i = sr.read();

		switch (i) {
		case 34:
		case 42:
		case 91:
		case 123:
		case 'n':
		case 't':
		case 'f':
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '.':
			return asObject(sr, i);
		}
		
		throw new IllegalArgumentException("Invalid json format, unexpected char : " + ((char) i) + "(" + i + ")");
	}

	private static Object asObject(Reader sr, int priorChar) throws IOException {
		switch (priorChar) {
		case 'n':
			return asNull(sr);
		case 't':
		case 'f':
			return asBoolean(sr);
		case 34:
			return asString(sr);
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '.':
			return asNumber(sr, priorChar);
		case 91:
			return asList(sr);
		case 123:
			return asMap(sr);
		case 42:
			return null;
		}
		throw new RuntimeException("invalid json format");
	}

	private static Object asNumber(Reader sr, int priorChar) throws IOException {
		StringBuilder s = new StringBuilder();
		s.append((char) priorChar);
		int c = sr.read();
		while (c != ' ') {
			if (c < 0) {
				break;
				// throw new IllegalArgumentException();
			}

			s.append((char) c);
			c = sr.read();
		}

		String t = s.toString();
		if (t.indexOf('.') >= 0) {
			return new BigDecimal(t);
		} else if (t.length() > 18) {
			return new BigInteger(t);
		} else if (t.length() >= 9) {
			return new Long(t);
		} else {
			return new Integer(t);
		}
	}

	private static Object asString(Reader s) throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int c = s.read(); c >= 0; c = s.read()) {
			switch (c) {
			case 34:// "
				return sb.toString();
			case 92:// \
				c = s.read();
				switch (c) {
				case 114: // \r
					sb.append("\r");
					continue;
				case 110: // \n
					sb.append("\n");
					continue;
				case 39: // '
					sb.append("'");
					continue;
				case 34: // "
					sb.append('"');
					continue;
				case 116: // \t
					sb.append("\t");
					continue;
				case 92: // \\
					sb.append("\\");
					continue;
				default:
					throw new RuntimeException("\\" + (char) c + " is not valid char.");
				}
			default:
				sb.append((char) c);
			}
		}

		return sb.length() != 0 ? sb.toString() : null;
	}

	private static Object asNull(Reader s) throws IOException {
		s.read();// 跳过u
		s.read();// 跳过l
		s.read();// 跳过l
		return null;
	}

	private static Object asBoolean(Reader s) throws IOException {
		s.read();// 跳过r或a
		s.read();// 跳过u或l
		int i = s.read();// e或s
		if (i == 'e') {
			return Boolean.TRUE;
		}
		s.read();// 跳过false最后一个 e
		return Boolean.FALSE;
	}

	private static ArrayList<Object> asList(Reader s) throws IOException {
		ArrayList<Object> list = new ArrayList<Object>();
		int c = s.read();

		while (c >= 0) {
			switch (c) {
			case 93:
				return list;
			case 34:
			case 42:
			case 91:
			case 123:
			case 't':
			case 'f':
			case 'n':
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				list.add(asObject(s, c));
				c = s.read();
				break;
			case 40:
			case 41:
			case 44:
				c = s.read();
				break;
			default:
				throw new RuntimeException("char {" + (char) c + "} is an invalid char.");
			}
		}

		return list;
	}

	private static LinkedHashMap<Object, Object> asMap(Reader s) throws IOException {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
		int c = s.read();

		while (c >= 0) {
			switch (c) {
			case 125:
				return map;
			case 34:
			case 42:
			case 91:
			case 123:
			case 'n':
			case 't':
			case 'f':
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				Object key = asObject(s, c);
				s.read();
				Object value = toObject(s);
				map.put(key, value);
				c = s.read();
				break;
			case 40:
			case 41:
			case 44:
				c = s.read();
				break;
			default:
				throw new RuntimeException("char {" + (char) c + "} is an invalid char.");
			}
		}

		return map;
	}

	public static boolean isQName(String name) {
		if (!isLetter(name.charAt(0))) {
			return false;
		}

		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!isLetterOrDigital(c)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isInteger(String name) {
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!isDigital(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否空白符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSpace(char c) {
		return c <= ' ';
	}

	public static boolean isLetterOrDigital(char c) {
		return isLetter(c) || isDigital(c);
	}

	public static boolean isLetter(char c) {
		if (c == '_' || c == '$'//
				|| c >= 'A' && c <= 'Z' //
				|| c >= 'a' && c <= 'z') {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDigital(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
		map.put("key", "value1");
		map.put(true, false);
		map.put("key2", "value2");
		map.put("key3", "value3");
		map.put(true, false);
		map.put(33, 6);
		ArrayList<Object> list = new ArrayList<Object>();
		list.add("LIST");
		list.add(1);
		list.add(-1.2);
		Object[] array = { true, false, "1", "2", map, list, "xx", null, -99.3, -0.3, 0.4, "", new java.util.Date(),
				false, 5 };
		String s = toString(array);
		System.out.println(s);

		Object o = toObject(s);
		System.out.println(toString(o));
	}
}