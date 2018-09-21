//package test;
//
//import com.dachen.commons.constants.Constants.Result;
//import com.dachen.util.HttpUtil;
//
//public class TestHttp {
//	private static String app_id = "216461910000035461";
//
//	private static String app_secret = "6ee8640fa58fb452c69e265f455cabac";
//
//	public static void main(String[] args) throws Exception {
//		HttpUtil.Request request = new HttpUtil.Request();
//		request.setSpec("https://oauth.api.189.cn/emp/oauth2/v3/access_token");
//		request.setMethod(HttpUtil.RequestMethod.POST);
//		request.getData().put("grant_type", "client_credentials");
//		request.getData().put("app_id", app_id);
//		request.getData().put("app_secret", app_secret);
//
//		Result result = HttpUtil.asBean(request,
//				Result.class);
//		System.out.println(result.getAccess_token());
//	}
//
//}
