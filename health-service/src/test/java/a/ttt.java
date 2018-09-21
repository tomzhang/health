package a;

import java.util.HashMap;
import java.util.Map;

import com.dachen.util.HttpUtil;
import com.dachen.util.HttpUtil.Request;

public class ttt {

	public static void main(String[] args) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("text", "[10001,10002]");
		data.put("body", "{userId:1}");
		data.put("access_token", "259daa18a7bb4b7b8b5cbaf472fd91c7");
		Request request = new Request(data, "http://localhost:8092/tigase/push");
		String result = HttpUtil.asString(request);
		System.out.println(result);
	}

}
