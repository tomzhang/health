package com.dachen.line.stat.comon.constant;


/**
 *  100：恭喜您认证通过，您还需要设置服务才能接单哦
	101：您需要设置服务时间，患者才能会找到您哦
	102：您设置的服务时间只有N（非固定）天喽，快去再设置吧
	103：订单来了我第一时间通知您
	104：您的服务尚未开启

 * @author weilit
 *
 */
public enum VServiceProcessFromEnum {
    nurse_type(0, "V小护"), 
    system_type(1, "系统自动分配 "),
    servicer_type(2, "客服手动"),
    other_type(3, "其它");
    private int index;
    private String title;

    private VServiceProcessFromEnum(int index, String title) {
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

    public static  VServiceProcessFromEnum  getEnum(int index)
    {
    	VServiceProcessFromEnum e=null;
        for(VServiceProcessFromEnum e1:VServiceProcessFromEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
