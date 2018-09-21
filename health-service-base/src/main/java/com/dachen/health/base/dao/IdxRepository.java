package com.dachen.health.base.dao;

/**
 * ProjectName： health-service<br>
 * ClassName： IdxRepository<br>
 * Description：统一id生成器 <br>
 * 
 * @author fanp
 * @createTime 2015年8月7日
 * @version 1.0.0
 */
public interface IdxRepository {

	public interface idxType {
		static final int PayNo = 1; // 第三方支付编号
		static final int userId = 2; // 用户id
		static final int doctorNum = 3; // 医生号
		static final int companyNum = 4; // 集团账户
		static final int orderNo = 5; // 订单号
		static final int nurseNo = 6; // 护士id add by liwei
		static final int msgTemplateNum = 7;
		static final int refundNo = 8; // 退款批次号

		static final int FormId = 9;// 剂型Id
		static final int UnitId = 10;// 包装单位Id
		static final int doseId = 11;// 服药单位Id
		static final int manageId = 12;// 管理类别Id
		static final int bizId = 13;// 产品类别Id
		static final int yyyllbId = 14;// 药理类别
		static final int diseaseId = 15;// 病种Id
	}

	public interface JedisKey {
		static final String keyUserId = "KEY_USER_ID"; // 用户Id自增Key
		static final String keyDoctorNum = "KEY_DOCTOR_NUM"; // 医生号自增Key
	}

	/**
	 * </p>获取自增id</p>
	 * 
	 * @param idxType
	 * @return
	 * @author fanp
	 * @date 2015年8月7日
	 */
	Integer nextIdx(int idxType);

	/**
	 * </p>获取自增id</p>
	 * 
	 * @param idxType
	 * @return
	 * @author fanp
	 * @date 2015年8月7日
	 */
	Integer nextPayNoIdx(int idxType);

	/**
	 * </p>获取自增id</p>
	 * 
	 * @param idxType
	 * @return
	 * @author fanp
	 * @date 2015年8月7日
	 */
	String nextOrderNoIdx(int idxType);

	/**
	 * </p>获取医生号</p>
	 * 
	 * @param idxType
	 * @return
	 * @author fanp
	 * @date 2015年8月11日
	 */
	String nextDoctorNum(int idxType);

	/**
	 * </p>获取消息模版号</p>
	 * 
	 * @param idxType
	 * @return
	 * @author duanwuju
	 * @date 2015年12月26日
	 */
	String nextMsgTemplateNum(int idxType);

}
