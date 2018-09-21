package com.dachen.health.commons.entity;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 二维码生产参数
 * @author longjh
 *      date:2017/09/14
 */
public class QrCodeParam {
    
    /**
     * 二维码的类型
     */
    private Integer type;
    /**
     * 二维码绑定的数据
     */
    private Map<String, String> data;
    /**
     * 校验码，md5(type+固定值)
     */
    private String verifyCode;
    /**
     * 扫描终端
     */
    private String sc;
    /**
     * 二维码唯一id
     */
    private String qrId;
    /**
     * 平台二维码标识md5(type+qrId)
     */
    private String tag;
    /**
     * 解析附加参数
     */
    private String scParam;
        
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Map<String, String> getData() {
        return data;
    }
    public void setData(Map<String, String> data) {
        this.data = data;
    }
    public String getVerifyCode() {
        return verifyCode;
    }
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
    public String getSc() {
        return StringUtils.isBlank(sc)? "none" : sc.trim().toLowerCase();
    }
    public void setSc(String sc) {
        this.sc = sc;
    }
    public String getQrId() {
        return qrId;
    }
    public void setQrId(String qrId) {
        this.qrId = qrId;
    }
    public String getScParam() {
        return scParam;
    }
    public void setScParam(String scParam) {
        this.scParam = scParam;
    }
    
    
    public enum QrCodeType{
        /**圈子/科室二维码*/
        CIRCLE_QRCODE(1001, "CIRCLE_QRCODE"),
        /**圈子个人信息二维码*/
        CIRCLE_PERSONAL_QRCODE(1002, "CIRCLE_PERSONAL_QRCODE"),
        /**邀请医生转学币二维码*/
        INVITE_DOCTOR_GET_COIN_QRCODE(1003, "INVITE_DOCTOR_GET_COIN_QRCODE");
        
        private Integer type;
        private String flag;
        
        QrCodeType(Integer type, String flag){
            this.type = type;
            this.flag = flag;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }
        
        public static QrCodeType valueOf(Integer type){
            QrCodeType qrCodeType = null;
            if(QrCodeType.CIRCLE_QRCODE.getType().equals(type)){
                qrCodeType = QrCodeType.CIRCLE_QRCODE;
            }
            if(QrCodeType.CIRCLE_PERSONAL_QRCODE.getType().equals(type)){
                qrCodeType = QrCodeType.CIRCLE_PERSONAL_QRCODE;
            }
            if(QrCodeType.INVITE_DOCTOR_GET_COIN_QRCODE.getType().equals(type)){
                qrCodeType = QrCodeType.INVITE_DOCTOR_GET_COIN_QRCODE;
            }
            return qrCodeType;
        }
        
    }
}
