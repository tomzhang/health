package com.dachen.line.stat.comon.constant;


/**
 * @author weilit
 *  1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
 *
     患者付款后：          下单  3 
      护士已接单：          预约成功  
      护士点击开始服务：进行中  
     患者确认结束服务：已结束

 */
public enum OrderStatusEnum {
    toGetOrder(1, "等待接单"), 
    onService(2, "预约成功"),
    toUploadResult(3, "等待上传结果"),
    toAppraise(4, "待评价"),
    close(5, "结束");
    private int index;
    private String title;

    private OrderStatusEnum(int index, String title) {
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

    public static  OrderStatusEnum  getEnum(int index)
    {
    	OrderStatusEnum e=null;
        for(OrderStatusEnum e1:OrderStatusEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
