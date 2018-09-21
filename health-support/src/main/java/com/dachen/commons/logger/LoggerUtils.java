package com.dachen.commons.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
    private static final Logger commonLog = LoggerFactory.getLogger("commonLog"); // 普通日志
    private static final Logger payLog = LoggerFactory.getLogger("payLog");// 支付日志
    private static final Logger tradeLog = LoggerFactory.getLogger("tradeLog");// 交易日志
    private static final Logger exceptionLogger = LoggerFactory.getLogger("exceptionLog");// 异常日志

    public static void printCommonLog(String msg) {
        commonLog.info(msg);
    }

    /**
     * </p>打印支付日志</p>
     * @param msg 日志
     * @param payNo 订单号
     * @author fanp
     * @date 2015年8月20日
     */
    public static void printPayLog(String msg, Integer payNo,PayType type ) {
        payLog.info(msg, payNo, type.getIndex() );
    }

    /**
     * </p>打印交易日志</p>
     * @param msg 日志
     * @param id id
     * @param Type 交易类型
     * @author fanp
     * @date 2015年8月20日
     */
    public static void printTradeLog(String msg, Integer id, TradeType type) {
        tradeLog.info(msg, id, type.getIndex());
    }

    public static void printExceptionLog(String msg, Throwable t) {
        exceptionLogger.info(msg, t);
    }

    
    public enum PayType {
        param(1, "参数"), 
        data(2, "返回");

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
        
    }
    
    public enum TradeType {
        order(1, "订单"), 
        withdraw(2, "提现"), 
        refund(3, "退款");

        private int index;

        private String title;

        private TradeType(int index, String title) {
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
