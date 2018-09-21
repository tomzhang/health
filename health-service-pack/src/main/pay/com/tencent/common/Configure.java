package com.tencent.common;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:40
 * 杩欓噷鏀剧疆鍚勭閰嶇疆鏁版嵁
 */
public class Configure {
	
	//提供注册，用以区分大众版和博德嘉联版
	private static boolean isBDJL = false;
    public static boolean isBDJL() {
		return isBDJL;
	}

	public static void setBDJL(boolean isBDJL) {
		Configure.isBDJL = isBDJL;
	}

	//sdk鐨勭増鏈彿
	private static final String sdkVersion = "java sdk 1.0.1";
   
	//杩欎釜灏辨槸鑷繁瑕佷繚绠″ソ鐨勭鏈塊ey浜嗭紙鍒囪鍙兘鏀惧湪鑷繁鐨勫悗鍙颁唬鐮侀噷锛屼笉鑳芥斁鍦ㄤ换浣曞彲鑳借鐪嬪埌婧愪唬鐮佺殑瀹㈡埛绔▼搴忎腑锛�
	// 姣忔鑷繁Post鏁版嵁缁橝PI鐨勬椂鍊欓兘瑕佺敤杩欎釜key鏉ュ鎵�鏈夊瓧娈佃繘琛岀鍚嶏紝鐢熸垚鐨勭鍚嶄細鏀惧湪Sign杩欎釜瀛楁锛孉PI鏀跺埌Post鏁版嵁鐨勬椂鍊欎篃浼氱敤鍚屾牱鐨勭鍚嶇畻娉曞Post杩囨潵鐨勬暟鎹繘琛岀鍚嶅拰楠岃瘉
	// 鏀跺埌API鐨勮繑鍥炵殑鏃跺�欎篃瑕佺敤杩欎釜key鏉ュ杩斿洖鐨勬暟鎹畻涓嬬鍚嶏紝璺烝PI鐨凷ign鏁版嵁杩涜姣旇緝锛屽鏋滃�间笉涓�鑷达紝鏈夊彲鑳芥暟鎹绗笁鏂圭粰绡℃敼

	//private static String key = "891794d1c969ca841747dc7cf0e339b7";//old
	private static String key = "3f80203d23160c1f11414b7406cdbded";//new
	private static String key_BDJL = "4f80203d23160c1f11414b7406cdbd2d";//博德嘉联
	
	//寰俊鍒嗛厤鐨勫叕浼楀彿ID锛堝紑閫氬叕浼楀彿涔嬪悗鍙互鑾峰彇鍒帮級
//	private static String appID = "wx6c571325c9240c68";//old
	private static String appID = "wx8b3a67ad899b632c";//new
	private static String appID_BDJL = "wxaacbd594a3a88e78";//博德嘉联

	//寰俊鏀粯鍒嗛厤鐨勫晢鎴峰彿ID锛堝紑閫氬叕浼楀彿鐨勫井淇℃敮浠樺姛鑳戒箣鍚庡彲浠ヨ幏鍙栧埌锛�
//	private static String mchID = "1262428001";//old
	private static String mchID = "1342696501";//new
	private static String mchID_BDJL = "1345205601";//博德嘉联

	//鍙楃悊妯″紡涓嬬粰瀛愬晢鎴峰垎閰嶇殑瀛愬晢鎴峰彿
	private static String subMchID = "";

	//HTTPS璇佷功鐨勬湰鍦拌矾寰�
//	private static String certLocalPath = "/properties/apiclient_cert.p12";//old
	private static String certLocalPath = "classpath:properties/apiclient_cert_xg.p12";//new
	private static String certLocalPath_BDJL = "classpath:properties/apiclient_cert_bd.p12";//博德嘉联
	//鑾峰彇鑵捐浜戠鍚嶅簱
	//private static String jnisigcheck = "/properties/jnisigcheck.dll";
	private static String jnisigcheck = "classpath:properties/jnisigcheck.so";
	private static String dachen_ec_key = "classpath:properties/dachen/private_key";
	private static String dachen_public_key = "classpath:properties/dachen/public_key";
	private static String kangze_ec_key = "classpath:properties/kangze/private_key";
	private static String kangze_public_key = "classpath:properties/kangze/public_key";
	//private static String certLocalPath = "\\WEB-INF\\classes\\properties\\apiclient_cert.p12";
	//private static String certLocalPath  = "D:\\apiclient_cert.p12";
	//HTTPS璇佷功瀵嗙爜锛岄粯璁ゅ瘑鐮佺瓑浜庡晢鎴峰彿MCHID
//	private static String certPassword = "1262428001";//old
	private static String certPassword = "1342696501";//new
	private static String certPassword_BDJL = "1345205601";//博德嘉联

	//鏄惁浣跨敤寮傛绾跨▼鐨勬柟寮忔潵涓婃姤API娴嬮�燂紝榛樿涓哄紓姝ユā寮�
	private static boolean useThreadToDoReport = true;

	//鏈哄櫒IP
	private static String ip = "120.24.94.126";
	
	//浠ヤ笅鏄嚑涓狝PI鐨勮矾寰勶細
	//0)缁熶竴涓嬪崟
	public static String UNIFIED_ORDER="https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	//1锛夎鎵敮浠楢PI
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

	//2锛夎鎵敮浠樻煡璇PI
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	//3锛夐��娆続PI
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	//4锛夐��娆炬煡璇PI
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	//5锛夋挙閿�API
	public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	//6锛変笅杞藉璐﹀崟API
	public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	//7) 缁熻涓婃姤API
	public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

	public static boolean isUseThreadToDoReport() {
		return useThreadToDoReport;
	}

	public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
		Configure.useThreadToDoReport = useThreadToDoReport;
	}

	public static String HttpsRequestClassName = "com.tencent.common.HttpsRequest";

	public static void setKey(String key) {
		Configure.key = key;
	}

	public static void setAppID(String appID) {
		Configure.appID = appID;
	}

	public static void setMchID(String mchID) {
		Configure.mchID = mchID;
	}

	public static void setSubMchID(String subMchID) {
		Configure.subMchID = subMchID;
	}

	public static void setCertLocalPath(String certLocalPath) {
		Configure.certLocalPath = certLocalPath;
	}

	public static void setCertPassword(String certPassword) {
		Configure.certPassword = certPassword;
	}

	public static void setIp(String ip) {
		Configure.ip = ip;
	}

	//*********区分博德嘉联*********
	public static String getKey(){
		return isBDJL ? key_BDJL : key;
	}
	
	public static String getAppid(){
		return isBDJL ? appID_BDJL : appID;
	}
	
	public static String getMchid(){
		return isBDJL ? mchID_BDJL : mchID;
	}
	//***************************
	public static String getAppid_BDJL() {
		return appID_BDJL;
	}

	public static String getSubMchid(){
		return subMchID;
	}
	
	public static String getCertLocalPath(){
		return isBDJL ? certLocalPath_BDJL : certLocalPath;
	}
	
	public static String getCertPassword(){
		return isBDJL ? certPassword_BDJL : certPassword;
	}

	public static String getIP(){
		return ip;
	}

	public static void setHttpsRequestClassName(String name){
		HttpsRequestClassName = name;
	}

	public static String getSdkVersion(){
		return sdkVersion;
	}

	public static String getJnisigcheck() {
		return jnisigcheck;
	}

	public static void setJnisigcheck(String jnisigcheck) {
		Configure.jnisigcheck = jnisigcheck;
	}

	public static String getDachen_ec_key() {
		return dachen_ec_key;
	}

	public static void setDachen_ec_key(String dachen_ec_key) {
		Configure.dachen_ec_key = dachen_ec_key;
	}

	public static String getDachen_public_key() {
		return dachen_public_key;
	}

	public static void setDachen_public_key(String dachen_public_key) {
		Configure.dachen_public_key = dachen_public_key;
	}

	public static String getKangze_ec_key() {
		return kangze_ec_key;
	}

	public static void setKangze_ec_key(String kangze_ec_key) {
		Configure.kangze_ec_key = kangze_ec_key;
	}

	public static String getKangze_public_key() {
		return kangze_public_key;
	}

	public static void setKangze_public_key(String kangze_public_key) {
		Configure.kangze_public_key = kangze_public_key;
	}

}
