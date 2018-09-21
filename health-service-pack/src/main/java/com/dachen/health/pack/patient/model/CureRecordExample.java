package com.dachen.health.pack.patient.model;

import java.util.ArrayList;
import java.util.List;

public class CureRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CureRecordExample() {
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
     * 诊疗记录表
     * @author 李淼淼
     * @version 1.0 2015-11-25
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

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Integer value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Integer value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Integer value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Integer value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Integer value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Integer> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Integer> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Integer value1, Integer value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Integer value1, Integer value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdIsNull() {
            addCriterion("doctor_id is null");
            return (Criteria) this;
        }

        public Criteria andDoctorIdIsNotNull() {
            addCriterion("doctor_id is not null");
            return (Criteria) this;
        }

        public Criteria andDoctorIdEqualTo(Integer value) {
            addCriterion("doctor_id =", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdNotEqualTo(Integer value) {
            addCriterion("doctor_id <>", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdGreaterThan(Integer value) {
            addCriterion("doctor_id >", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("doctor_id >=", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdLessThan(Integer value) {
            addCriterion("doctor_id <", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdLessThanOrEqualTo(Integer value) {
            addCriterion("doctor_id <=", value, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdIn(List<Integer> values) {
            addCriterion("doctor_id in", values, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdNotIn(List<Integer> values) {
            addCriterion("doctor_id not in", values, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdBetween(Integer value1, Integer value2) {
            addCriterion("doctor_id between", value1, value2, "doctorId");
            return (Criteria) this;
        }

        public Criteria andDoctorIdNotBetween(Integer value1, Integer value2) {
            addCriterion("doctor_id not between", value1, value2, "doctorId");
            return (Criteria) this;
        }

        public Criteria andPatientIdIsNull() {
            addCriterion("patient_id is null");
            return (Criteria) this;
        }

        public Criteria andPatientIdIsNotNull() {
            addCriterion("patient_id is not null");
            return (Criteria) this;
        }

        public Criteria andPatientIdEqualTo(Integer value) {
            addCriterion("patient_id =", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdNotEqualTo(Integer value) {
            addCriterion("patient_id <>", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdGreaterThan(Integer value) {
            addCriterion("patient_id >", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("patient_id >=", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdLessThan(Integer value) {
            addCriterion("patient_id <", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdLessThanOrEqualTo(Integer value) {
            addCriterion("patient_id <=", value, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdIn(List<Integer> values) {
            addCriterion("patient_id in", values, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdNotIn(List<Integer> values) {
            addCriterion("patient_id not in", values, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdBetween(Integer value1, Integer value2) {
            addCriterion("patient_id between", value1, value2, "patientId");
            return (Criteria) this;
        }

        public Criteria andPatientIdNotBetween(Integer value1, Integer value2) {
            addCriterion("patient_id not between", value1, value2, "patientId");
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

        public Criteria andTreatAdviseIsNull() {
            addCriterion("treat_advise is null");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseIsNotNull() {
            addCriterion("treat_advise is not null");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseEqualTo(String value) {
            addCriterion("treat_advise =", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseNotEqualTo(String value) {
            addCriterion("treat_advise <>", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseGreaterThan(String value) {
            addCriterion("treat_advise >", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseGreaterThanOrEqualTo(String value) {
            addCriterion("treat_advise >=", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseLessThan(String value) {
            addCriterion("treat_advise <", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseLessThanOrEqualTo(String value) {
            addCriterion("treat_advise <=", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseLike(String value) {
            addCriterion("treat_advise like", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseNotLike(String value) {
            addCriterion("treat_advise not like", value, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseIn(List<String> values) {
            addCriterion("treat_advise in", values, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseNotIn(List<String> values) {
            addCriterion("treat_advise not in", values, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseBetween(String value1, String value2) {
            addCriterion("treat_advise between", value1, value2, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andTreatAdviseNotBetween(String value1, String value2) {
            addCriterion("treat_advise not between", value1, value2, "treatAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseIsNull() {
            addCriterion("drug_advise is null");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseIsNotNull() {
            addCriterion("drug_advise is not null");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseEqualTo(String value) {
            addCriterion("drug_advise =", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseNotEqualTo(String value) {
            addCriterion("drug_advise <>", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseGreaterThan(String value) {
            addCriterion("drug_advise >", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseGreaterThanOrEqualTo(String value) {
            addCriterion("drug_advise >=", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseLessThan(String value) {
            addCriterion("drug_advise <", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseLessThanOrEqualTo(String value) {
            addCriterion("drug_advise <=", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseLike(String value) {
            addCriterion("drug_advise like", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseNotLike(String value) {
            addCriterion("drug_advise not like", value, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseIn(List<String> values) {
            addCriterion("drug_advise in", values, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseNotIn(List<String> values) {
            addCriterion("drug_advise not in", values, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseBetween(String value1, String value2) {
            addCriterion("drug_advise between", value1, value2, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andDrugAdviseNotBetween(String value1, String value2) {
            addCriterion("drug_advise not between", value1, value2, "drugAdvise");
            return (Criteria) this;
        }

        public Criteria andAttentionIsNull() {
            addCriterion("attention is null");
            return (Criteria) this;
        }

        public Criteria andAttentionIsNotNull() {
            addCriterion("attention is not null");
            return (Criteria) this;
        }

        public Criteria andAttentionEqualTo(String value) {
            addCriterion("attention =", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionNotEqualTo(String value) {
            addCriterion("attention <>", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionGreaterThan(String value) {
            addCriterion("attention >", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionGreaterThanOrEqualTo(String value) {
            addCriterion("attention >=", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionLessThan(String value) {
            addCriterion("attention <", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionLessThanOrEqualTo(String value) {
            addCriterion("attention <=", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionLike(String value) {
            addCriterion("attention like", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionNotLike(String value) {
            addCriterion("attention not like", value, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionIn(List<String> values) {
            addCriterion("attention in", values, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionNotIn(List<String> values) {
            addCriterion("attention not in", values, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionBetween(String value1, String value2) {
            addCriterion("attention between", value1, value2, "attention");
            return (Criteria) this;
        }

        public Criteria andAttentionNotBetween(String value1, String value2) {
            addCriterion("attention not between", value1, value2, "attention");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseIsNull() {
            addCriterion("consult_advise is null");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseIsNotNull() {
            addCriterion("consult_advise is not null");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseEqualTo(String value) {
            addCriterion("consult_advise =", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseNotEqualTo(String value) {
            addCriterion("consult_advise <>", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseGreaterThan(String value) {
            addCriterion("consult_advise >", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseGreaterThanOrEqualTo(String value) {
            addCriterion("consult_advise >=", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseLessThan(String value) {
            addCriterion("consult_advise <", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseLessThanOrEqualTo(String value) {
            addCriterion("consult_advise <=", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseLike(String value) {
            addCriterion("consult_advise like", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseNotLike(String value) {
            addCriterion("consult_advise not like", value, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseIn(List<String> values) {
            addCriterion("consult_advise in", values, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseNotIn(List<String> values) {
            addCriterion("consult_advise not in", values, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseBetween(String value1, String value2) {
            addCriterion("consult_advise between", value1, value2, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseNotBetween(String value1, String value2) {
            addCriterion("consult_advise not between", value1, value2, "consultAdvise");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesIsNull() {
            addCriterion("consult_advise_diseases is null");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesIsNotNull() {
            addCriterion("consult_advise_diseases is not null");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesEqualTo(String value) {
            addCriterion("consult_advise_diseases =", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesNotEqualTo(String value) {
            addCriterion("consult_advise_diseases <>", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesGreaterThan(String value) {
            addCriterion("consult_advise_diseases >", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesGreaterThanOrEqualTo(String value) {
            addCriterion("consult_advise_diseases >=", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesLessThan(String value) {
            addCriterion("consult_advise_diseases <", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesLessThanOrEqualTo(String value) {
            addCriterion("consult_advise_diseases <=", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesLike(String value) {
            addCriterion("consult_advise_diseases like", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesNotLike(String value) {
            addCriterion("consult_advise_diseases not like", value, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesIn(List<String> values) {
            addCriterion("consult_advise_diseases in", values, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesNotIn(List<String> values) {
            addCriterion("consult_advise_diseases not in", values, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesBetween(String value1, String value2) {
            addCriterion("consult_advise_diseases between", value1, value2, "consultAdviseDiseases");
            return (Criteria) this;
        }

        public Criteria andConsultAdviseDiseasesNotBetween(String value1, String value2) {
            addCriterion("consult_advise_diseases not between", value1, value2, "consultAdviseDiseases");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * 诊疗记录表
     * @author 李淼淼
     * @version 1.0 2015-11-25
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