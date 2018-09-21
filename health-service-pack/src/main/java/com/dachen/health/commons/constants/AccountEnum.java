package com.dachen.health.commons.constants;

import com.dachen.health.commons.constants.PackEnum.PackType;

/**
 * ProjectName： health-service<br>
 * ClassName： AccountEnum<br>
 * Description：账户枚举 <br>
 * 
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public class AccountEnum {

    /**
     * ProjectName： health-service<br>
     * ClassName： PayType<br>
     * Description：支付类型枚举类 <br>
     * 
     * @author fanp
     * @createTime 2015年8月5日
     * @version 1.0.0
     */
    public enum PayType {
        wechat(1, "微信支付"), 
        alipay(2, "支付宝支付"),
        balances(3,"余额支付"),
        transfer(4,"手动转账"),
    	integral(12, "积分问诊");

        private int index;

        private String title;

        private PayType(int index, String title) {
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
        
        public static String getTitle(int index) {
            for (PayType pt : PayType.values()) {
                if (pt.getIndex() == index) {
                    return pt.title;
                }
            }
            return null;
        }

    }

    /**
     * ProjectName： health-service<br>
     * ClassName： SourceType<br>
     * Description： 账单或充值源头类型<br>
     * @author fanp
     * @createTime 2015年8月5日
     * @version 1.0.0
     */
    public enum SourceType {
        order(1, "订单"), 
        addOrder(2, "追加订单"),
        recharge(3, "充值");

        private int index;

        private String title;

        private SourceType(int index, String title) {
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

    }
    
    /**
     * ProjectName： health-service-trade<br>
     * ClassName： RechargeStatus<br>
     * Description：充值状态枚举类 <br>
     * @author fanp
     * @createTime 2015年8月6日
     * @version 1.0.0
     */
    public enum RechargeStatus {
        初始(1, "初始"), 
        成功(2, "成功"), 
        失败(3, "失败");

        private int index;

        private String title;

        private RechargeStatus(int index, String title) {
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
        
    }
    
}
