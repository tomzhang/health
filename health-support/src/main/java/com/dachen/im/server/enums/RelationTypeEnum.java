package com.dachen.im.server.enums;

/**
 * 会话类型
 * @author Administrator
 *
 */
public enum RelationTypeEnum {

	/**
	 * patient(1, "患者"), 
	 * assistant(2, "医助"), 
	 * doctor(3, "医生");
     * customerService(4, "客服"),
	 */
	SYS("0_0","系统"),
	PATIENT("1_1","患者_患者"),
	PATIENT_ASST("1_2","患者_医助"),
	DOC_PATIENT("1_3","患者_医生"),
	DOC_PATIENT_REPORT("1_3_1","患者_医生_患者报道"),
	DOC_OUTPATIENT("1_3_2","患者_医生_门诊"),
	PATIENT_CUSTOMER("1_4","患者_客服"),
	DOC_ASSISTANT("2_3","助理_医生"),
	DOC_ASSISTANT_NOORDER("2_3_1","助理_医生_无订单"),
	DOCTOR("3_3","医生_医生"),
	DOC_DRUGCORP("3_10","医生_药企代表"),
	DRUGCORP("10","药企代表");
	
	private String alias;
	private String value;
	private RelationTypeEnum(String value,String alias)
	{
		this.value = value;
		this.alias = alias;
	}
	public String getAlias() {
		return alias;
	}
	public String getValue() {
		return value;
	}
	
	public static RelationTypeEnum getEnum(String value) {
        for (RelationTypeEnum e : RelationTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
