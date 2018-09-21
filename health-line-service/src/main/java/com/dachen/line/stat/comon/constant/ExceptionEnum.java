package com.dachen.line.stat.comon.constant;

public enum ExceptionEnum {
	//下单无人接（超过2个小时（相对于下单时间）
	Exception100(100,"100"),
	//下单无人接（超过”兜底（预约时间的前一天晚上10 若是无人接单就将订单取消掉）
	Exception101(101,"101"),
	//超过3天患者没有做出评价，护士服务订单需关闭
	Exception102(102,"102"),
	//调度情况2
	//抢单成功没有拨打电话（30分钟内）
	Exception200(200,"200"),
	//抢单成功没有发送短信（1个小时内）
	Exception201(201,"201"),
	//还未到预约的时间，就点击了开始服务
	Exception202(202,"202"),
	//到了预约时间还没有点击开始服务
	Exception203(203,"203"),
	Exception204(204,"204"),
	
	//患者下单开始
	Business_code_100(100,"100"),
	//护士抢单
	Business_code_200(200,"200"),
	//护士拨打电话 （多次）
	Business_code_300(300,"300"),
	//护士发送了短信（多次）
	Business_code_400(400,"400"),
	//开始服务
	Business_code_500(500,"500"),
	//上传结果（多次）
	Business_code_600(600,"600"),
	//护士服务流程结束
	Business_code_700(700,"700"),
	///患者评价结束（整个线下服务的流程终止）
	Business_code_800(800,"800"),
	///患者取消订单
	Business_code_900(900,"900");
	
	
	//业务编码
	
	private int index;
	private String title;

	private ExceptionEnum(int index, String title) {
		this.index = index;
		this.title = title;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	} 

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static ExceptionEnum getEnum(int index) {
		ExceptionEnum e = null;
		for (ExceptionEnum e1 : ExceptionEnum.values())
			if (e1.index == index) {
				e = e1;
				break;
			}
		return e;
	}
}
