package com.dachen.health.pack.patient.model;

import java.util.ArrayList;
import java.util.List;

public class OrderSessionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrderSessionExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * 订单会话关系表
     * @author 李淼淼
     * @version 1.0 2015-12-09
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(Integer value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(Integer value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(Integer value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(Integer value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(Integer value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<Integer> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<Integer> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(Integer value1, Integer value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(Integer value1, Integer value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdIsNull() {
            addCriterion("msg_group_id is null");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdIsNotNull() {
            addCriterion("msg_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdEqualTo(String value) {
            addCriterion("msg_group_id =", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdNotEqualTo(String value) {
            addCriterion("msg_group_id <>", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdGreaterThan(String value) {
            addCriterion("msg_group_id >", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdGreaterThanOrEqualTo(String value) {
            addCriterion("msg_group_id >=", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdLessThan(String value) {
            addCriterion("msg_group_id <", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdLessThanOrEqualTo(String value) {
            addCriterion("msg_group_id <=", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdLike(String value) {
            addCriterion("msg_group_id like", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdNotLike(String value) {
            addCriterion("msg_group_id not like", value, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdIn(List<String> values) {
            addCriterion("msg_group_id in", values, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdNotIn(List<String> values) {
            addCriterion("msg_group_id not in", values, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdBetween(String value1, String value2) {
            addCriterion("msg_group_id between", value1, value2, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andMsgGroupIdNotBetween(String value1, String value2) {
            addCriterion("msg_group_id not between", value1, value2, "msgGroupId");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIsNull() {
            addCriterion("last_modify_time is null");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIsNotNull() {
            addCriterion("last_modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeEqualTo(Long value) {
            addCriterion("last_modify_time =", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotEqualTo(Long value) {
            addCriterion("last_modify_time <>", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeGreaterThan(Long value) {
            addCriterion("last_modify_time >", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("last_modify_time >=", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeLessThan(Long value) {
            addCriterion("last_modify_time <", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeLessThanOrEqualTo(Long value) {
            addCriterion("last_modify_time <=", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIn(List<Long> values) {
            addCriterion("last_modify_time in", values, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotIn(List<Long> values) {
            addCriterion("last_modify_time not in", values, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeBetween(Long value1, Long value2) {
            addCriterion("last_modify_time between", value1, value2, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotBetween(Long value1, Long value2) {
            addCriterion("last_modify_time not between", value1, value2, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeIsNull() {
            addCriterion("appoint_time is null");
            return (Criteria) this;
        }

        public Criteria andAppointTimeIsNotNull() {
            addCriterion("appoint_time is not null");
            return (Criteria) this;
        }

        public Criteria andAppointTimeEqualTo(Long value) {
            addCriterion("appoint_time =", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeNotEqualTo(Long value) {
            addCriterion("appoint_time <>", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeGreaterThan(Long value) {
            addCriterion("appoint_time >", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("appoint_time >=", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeLessThan(Long value) {
            addCriterion("appoint_time <", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeLessThanOrEqualTo(Long value) {
            addCriterion("appoint_time <=", value, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeIn(List<Long> values) {
            addCriterion("appoint_time in", values, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeNotIn(List<Long> values) {
            addCriterion("appoint_time not in", values, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeBetween(Long value1, Long value2) {
            addCriterion("appoint_time between", value1, value2, "appointTime");
            return (Criteria) this;
        }

        public Criteria andAppointTimeNotBetween(Long value1, Long value2) {
            addCriterion("appoint_time not between", value1, value2, "appointTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeIsNull() {
            addCriterion("service_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeIsNotNull() {
            addCriterion("service_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeEqualTo(Long value) {
            addCriterion("service_begin_time =", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeNotEqualTo(Long value) {
            addCriterion("service_begin_time <>", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeGreaterThan(Long value) {
            addCriterion("service_begin_time >", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("service_begin_time >=", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeLessThan(Long value) {
            addCriterion("service_begin_time <", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("service_begin_time <=", value, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeIn(List<Long> values) {
            addCriterion("service_begin_time in", values, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeNotIn(List<Long> values) {
            addCriterion("service_begin_time not in", values, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeBetween(Long value1, Long value2) {
            addCriterion("service_begin_time between", value1, value2, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("service_begin_time not between", value1, value2, "serviceBeginTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeIsNull() {
            addCriterion("service_end_time is null");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeIsNotNull() {
            addCriterion("service_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeEqualTo(Long value) {
            addCriterion("service_end_time =", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeNotEqualTo(Long value) {
            addCriterion("service_end_time <>", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeGreaterThan(Long value) {
            addCriterion("service_end_time >", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("service_end_time >=", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeLessThan(Long value) {
            addCriterion("service_end_time <", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("service_end_time <=", value, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeIn(List<Long> values) {
            addCriterion("service_end_time in", values, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeNotIn(List<Long> values) {
            addCriterion("service_end_time not in", values, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeBetween(Long value1, Long value2) {
            addCriterion("service_end_time between", value1, value2, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andServiceEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("service_end_time not between", value1, value2, "serviceEndTime");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendIsNull() {
            addCriterion("patient_can_send is null");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendIsNotNull() {
            addCriterion("patient_can_send is not null");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendEqualTo(Boolean value) {
            addCriterion("patient_can_send =", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendNotEqualTo(Boolean value) {
            addCriterion("patient_can_send <>", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendGreaterThan(Boolean value) {
            addCriterion("patient_can_send >", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendGreaterThanOrEqualTo(Boolean value) {
            addCriterion("patient_can_send >=", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendLessThan(Boolean value) {
            addCriterion("patient_can_send <", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendLessThanOrEqualTo(Boolean value) {
            addCriterion("patient_can_send <=", value, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendIn(List<Boolean> values) {
            addCriterion("patient_can_send in", values, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendNotIn(List<Boolean> values) {
            addCriterion("patient_can_send not in", values, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendBetween(Boolean value1, Boolean value2) {
            addCriterion("patient_can_send between", value1, value2, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andPatientCanSendNotBetween(Boolean value1, Boolean value2) {
            addCriterion("patient_can_send not between", value1, value2, "patientCanSend");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeIsNull() {
            addCriterion("treat_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeIsNotNull() {
            addCriterion("treat_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeEqualTo(Long value) {
            addCriterion("treat_begin_time =", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeNotEqualTo(Long value) {
            addCriterion("treat_begin_time <>", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeGreaterThan(Long value) {
            addCriterion("treat_begin_time >", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("treat_begin_time >=", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeLessThan(Long value) {
            addCriterion("treat_begin_time <", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("treat_begin_time <=", value, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeIn(List<Long> values) {
            addCriterion("treat_begin_time in", values, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeNotIn(List<Long> values) {
            addCriterion("treat_begin_time not in", values, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeBetween(Long value1, Long value2) {
            addCriterion("treat_begin_time between", value1, value2, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andTreatBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("treat_begin_time not between", value1, value2, "treatBeginTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeIsNull() {
            addCriterion("is_send_over_time is null");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeIsNotNull() {
            addCriterion("is_send_over_time is not null");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeEqualTo(Boolean value) {
            addCriterion("is_send_over_time =", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeNotEqualTo(Boolean value) {
            addCriterion("is_send_over_time <>", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeGreaterThan(Boolean value) {
            addCriterion("is_send_over_time >", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_send_over_time >=", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeLessThan(Boolean value) {
            addCriterion("is_send_over_time <", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeLessThanOrEqualTo(Boolean value) {
            addCriterion("is_send_over_time <=", value, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeIn(List<Boolean> values) {
            addCriterion("is_send_over_time in", values, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeNotIn(List<Boolean> values) {
            addCriterion("is_send_over_time not in", values, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeBetween(Boolean value1, Boolean value2) {
            addCriterion("is_send_over_time between", value1, value2, "isSendOverTime");
            return (Criteria) this;
        }

        public Criteria andIsSendOverTimeNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_send_over_time not between", value1, value2, "isSendOverTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * 订单会话关系表
     * @author 李淼淼
     * @version 1.0 2015-12-09
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}