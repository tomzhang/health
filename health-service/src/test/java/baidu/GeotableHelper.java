package baidu;

import java.util.Calendar;
import java.util.Map;

import com.google.common.collect.Maps;
import com.dachen.lbs.vo.NearbyUser;
import com.dachen.util.DateUtil;
import com.dachen.util.HttpUtil;
import com.dachen.util.HttpUtil.Request;

public class GeotableHelper {
	public static final String ak = "OuLCe9EHc0v6Ik5BiAE4oxfN";

	public static void main(String[] args) throws Exception {
		nearby();
		// listColumn();
	}

	private static void nearby() throws Exception {
		String spec = "http://api.map.baidu.com/geosearch/v3/nearby";
		Map<String, Object> data = Maps.newHashMap();
		data.put("ak", ak);
		data.put("geotable_id", 102177);
		data.put("q", "ha");
		// data.put("q", "杨");
		data.put("location", "114.05265,22.608367");
		data.put("coord_type", 3);
		data.put("radius", 1000000000);
//		NearbyUser poi = new NearbyUser();
//		poi.setNickname("杨");
//		String filter = getFilter(poi);
//		if (null != filter)
//			data.put("filter", filter);
		data.put("page_index", 0);
		data.put("page_size", 50);// 10-50

		// String text = HttpUtil.asString(new Request(data, spec));
		String text = HttpUtil.get(new HttpUtil.Request(data, spec));
		System.out.println(text);
	}

	static String getFilter(NearbyUser poi) {
		StringBuilder sb = new StringBuilder();
		if (null != poi.getNickname())
			sb.append("nickname:" + poi.getNickname() + "|");
		if (null != poi.getSex())
			sb.append("sex:[" + poi.getSex() + "]|");
		if (null != poi.getActive()) {
			sb.append("active:"
					+ (DateUtil.currentTimeSeconds() - poi.getActive()) + ","
					+ DateUtil.currentTimeSeconds() + "|");
		}
		if (null != poi.getMinAge() && null != poi.getMaxAge()) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			long start = DateUtil
					.toSeconds((year - poi.getMinAge()) + "-01-01");
			long end = DateUtil.toSeconds((year - poi.getMaxAge()) + "-12-31");
			sb.append("birthday:" + start + "," + end + "|");
		}

		return sb.length() == 0 ? null : sb.substring(0, sb.length() - 1);
	}

	public static void listColumn() throws Exception {
		String spec = "http://api.map.baidu.com/geodata/v3/column/list";
		Map<String, Object> data = Maps.newHashMap();
		data.put("ak", ak);
		data.put("geotable_id", 94416);
		String text = HttpUtil.get(new HttpUtil.Request(data, spec));
		System.out.println(text);
	}

	public static void addColumn() throws Exception {
		String spec = "http://api.map.baidu.com/geodata/v3/column/create";
		Map<String, Object> data = Maps.newHashMap();
		data.put("name", ak);
		data.put("key", ak);
		data.put("type", ak);
		data.put("max_length", ak);
		data.put("default_value", ak);// 默认值
		data.put("is_sortfilter_field", ak);// 是否检索引擎的数值排序
		data.put("is_search_field", ak);// 是否检索引擎的文本检索字段
		data.put("is_index_field", ak);// 是否存储引擎的索引字段
		data.put("is_unique_field", ak);// 是否云存储唯一索引字段
		data.put("geotable_id", ak);
		data.put("ak", ak);

		String text = HttpUtil.asString(new Request(data, spec));

		System.out.println(text);
	}

	public static void deleteTable(String id) throws Exception {
		String spec = "http://api.map.baidu.com/geodata/v3/geotable/delete";
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", id);
		data.put("ak", ak);
		String text = HttpUtil.asString(new Request(data, spec));

		System.out.println(text);
	}

	public static void createTable(String name) throws Exception {
		String spec = "http://api.map.baidu.com/geodata/v3/geotable/create";

		Map<String, Object> data = Maps.newHashMap();
		data.put("name", name);
		data.put("geotype", 1);
		data.put("is_published", 1);
		data.put("ak", 1);

		String text = HttpUtil.asString(new Request(data, spec));

		System.out.println(text);
	}

}
