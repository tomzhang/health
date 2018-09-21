package com.dachen.health.checkbill.entity.po;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_check_bill",noClassnameStored = true)
public class CheckBill {

	@Id
	private String  id;

	/**订单id**/
	private Integer  orderId;

	/**患者id**/
	private Integer patientId;

	/**检查项Id**/
	private List<String> checkItemIds;
	
	//1：未下订单，2：已经下订单，3：已接单，4:已上传结果
	private Integer checkBillStatus;
	
	private Long createTime;
	
	private Long updateTime;

	/**建议检查时间**/
	private Long suggestCheckTime;

	/**注意事项**/
	private String attention;
	
	private List<String> checkupIds;
	
	/**
	 * 冗余对应的关怀项
	 */
	private String careItemId;
	
    public List<String> getCheckupIds() {
		return checkupIds;
	}

	public void setCheckupIds(List<String> checkupIds) {
		this.checkupIds = checkupIds;
	}

    public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public Long getSuggestCheckTime() {
		return suggestCheckTime;
	}

	public void setSuggestCheckTime(Long suggestCheckTime) {
		this.suggestCheckTime = suggestCheckTime;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Integer getOrderId() {
		return orderId;
	}


	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}


	public List<String> getCheckItemIds() {
		return checkItemIds;
	}


	public void setCheckItemIds(List<String> checkItemIds) {
		this.checkItemIds = checkItemIds;
	}


	public Long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}


	public Integer getCheckBillStatus() {
		return checkBillStatus;
	}


	public void setCheckBillStatus(Integer checkBillStatus) {
		this.checkBillStatus = checkBillStatus;
	}


	public Integer getPatientId() {
		return patientId;
	}


	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
	


	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
	public static void main(String[] args) {
		Set<Integer> set = new HashSet<Integer>();

		set.add(100684);
		set.add(101808); 
		set.add(101811);
		set.add(101812); 
		set.add(101804);
		set.add(101673);
		set.add(101824); 
		set.add(101821); 
		set.add(101827);
		set.add(101608);
		set.add(101646);
		set.add(100185); 
		set.add(100993);
		set.add(102002);
		set.add(102199); 
		set.add(102198);
		set.add(101902); 
		set.add(100576);
		set.add(102002); 
		set.add(102199); 
		set.add(101524); 
		set.add(101962); 
		set.add(101963); 
		set.add(102281); 
		set.add(102146); 
		set.add(102006); 
		set.add(101801); 
		set.add(101170); 
		set.add(100207); 
		set.add(101130); 
		set.add(101839);
		set.add(101130); 
		set.add(102002); 
		set.add(102199); 
		set.add(101839); 
		set.add(102146); 
		set.add(102006); 
		set.add(101963); 
		set.add(101962); 
		set.add(100207); 
		set.add(101801); 
		set.add(101170); 
		set.add(101524); 
		set.add(102281);
		set.add(102507); 
		set.add(102629); 
		set.add(102506); 
		set.add(102630); 
		set.add(101565); 
		set.add(102592);
		set.add(100180); 
		set.add(100911); 
		set.add(100767); 
		set.add(101660); 
		set.add(101955); 
		set.add(100993); 
		set.add(101564); 
		set.add(100187); 
		set.add(100846); 
		set.add(100552); 
		set.add(101828); 
		set.add(101130); 
		set.add(101233); 
		set.add(101176); 
		set.add(100186); 
		set.add(101453);
		set.add(101893); 
		set.add(102604);
		set.add(100180); 
		set.add(100177); 
		set.add(101771); 
		set.add(100846); 
		set.add(100667); 
		set.add(101016); 
		set.add(102636);
		
		
		
		Set<Integer> set1 = new HashSet<Integer>();
		set1.add(100911);
		set1.add(100767);
		set1.add(102199);
		set1.add(101771);
		set1.add(101608);
		set1.add(101812);
		set1.add(100684);
		set1.add(101821);
		set1.add(101808);
		set1.add(101824);
		set1.add(101827);
		set1.add(101804);
		set1.add(101811);
		set1.add(101646);
		set1.add(101016);
		set1.add(101176);
		set1.add(100180);
		set1.add(100187);
		set1.add(101233);
		set1.add(101673);
		set1.add(101564);
		set1.add(100552);
		set1.add(101902);
		set1.add(101801);
		set1.add(101170);
		set1.add(101963);
		set1.add(101962);
		set1.add(101839);
		set1.add(100207);
		set1.add(102198);
		set1.add(101828);
		set1.add(101453);
		set1.add(100993);
		set1.add(101565);
		set1.add(101130);
		set1.add(101660);
		set1.add(100576);
		set1.add(102002);
		set1.add(100185);
		set1.add(102146);
		set1.add(102281);
		set1.add(101524);
		set1.add(102006);
		set1.add(100846);
		set1.add(102507);
		set1.add(102506);
		set1.add(100186);
		set1.add(100667);
		set1.add(101955);
		set1.add(101893);
		set1.add(102604);
		set1.add(100177);
		set1.add(102592);
		set1.add(102629);
		set1.add(102630);
		set1.add(102636);
		
		set.removeAll(set1);
		
		System.out.println(set1.size());
		System.out.println(set.size());
//		set1.removeAll(set);
	//	System.out.println(set1);
		//System.out.println(set.size());
		System.out.println(StringUtils.trimToEmpty("  m  ")+1);
		
		List<String> imageUrls = new ArrayList<String>();
		imageUrls.add("aa");
		imageUrls.add("aa");
		imageUrls.add("aa");
		String[] aa = imageUrls.toArray(new String[imageUrls.size()]);
	}
}
