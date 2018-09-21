package com.dachen.line.stat.comon.constant;


/**
 * @author weilit
 *
 */
public enum OrderTypeEnum {
	unPay(0, "未支付"), 
    normal(1, "正常订单"), 
    cancle(2, "取消订单"),
    exceptional(3, "异常订单");
    private int index;
    private String title;

    private OrderTypeEnum(int index, String title) {
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

    public static  OrderTypeEnum  getEnum(int index)
    {
    	OrderTypeEnum e=null;
        for(OrderTypeEnum e1:OrderTypeEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
