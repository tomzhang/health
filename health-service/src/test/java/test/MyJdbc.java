package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyJdbc {
	static final String user = "iktv";
	static final String password = "1yz789%4#!hjd@";
	static final String url = "jdbc:mysql://114.119.6.150:3306/iktvdb?autoReconnect=true&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";

	public static void main(String[] args) throws Exception {
		// Connection con = getConnection();
		// DatabaseMetaData metaData = con.getMetaData();
		// ResultSet rs = metaData.getTables(null, null, null,
		// new String[] { "TABLE" });
		//
		// while (rs.next()) {
		// System.out.println(rs.getString("TABLE_NAME"));
		// }
		// con.close();

		String[] s1 = { " a ", " accept_gift ", " ad ", " ad_result ",
				" ad_setting ", " advice ", " bid_setting ", " chat ",
				" chat_tables ", " condition ", " flower ", " flowerold ",
				" friends ", " gift ", " informuser ", " judgelevel ",
				" jx_setting ", " level ", " logs ", " logs_old ", " lyric ",
				" marry ", " messages ", " my_hobby ", " my_photos ",
				" my_store ", " myanswer ", " mymv ", " mymv_detail ",
				" mymv_top ", " myrecord ", " original ", " party ",
				" party_members ", " race ", " race_detail ", " race_songs ",
				" race_top ", " report ", " setting ", " sharedetail ",
				" song_detail ", " song_type ", " songsneed ", " user ",
				" user_account ", " user_exchange_detail ",
				" user_money_detail ", " user_vod " };
		String[] s2 = { " user ", " advice ", " race ", " race_songs ",
				" race_detail ", " chat ", " race_top ", " province   ",
				" city ", " friends ", " messages   ", " condition ", " mymv ",
				" mymv_detail ", " marry ", " marry_detail ", " marry_top ",
				" my_photos ", " my_hobby ", " recommend_code ",
				" user_money_detail ", " user_account_detail ",
				" user_exchange_detail ", " user_money_rule ", " setting ",
				" report " };
		compare(s1, s2);
	}

	public static void compare(String[] s1, String[] s2) {
		for (String ss1 : s1) {
			boolean hasSS1 = false;
			for (String ss2 : s2) {
				if (ss1.equals(ss2)) {
					hasSS1 = true;
					continue;
				}

			}
			if (!hasSS1)
				System.out.println(ss1);
		}
	}

	public static Connection getConnection() throws Exception {
		return DriverManager.getConnection(url, user, password);
	}

	public static void test() throws Exception {
		String sql = "select user_id from user_Account where blogId=? and type=?";

		Class.forName("com.mysql.jdbc.Driver");

		Connection con = getConnection();
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, "F731DCC45B8E9940BAF31535E102D5A4");
		pstmt.setInt(2, 2);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString("user_id"));
		}
	}
}
