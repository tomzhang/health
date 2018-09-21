package com.dachen.health.pack.income.entity.po;

import java.util.ArrayList;
import java.util.List;

public class DoctorIncomeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DoctorIncomeExample() {
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
     * @author 李淼淼
     * @version 1.0 2016-01-23
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

        public Criteria andOrderIncomeIsNull() {
            addCriterion("order_income is null");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeIsNotNull() {
            addCriterion("order_income is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeEqualTo(Double value) {
            addCriterion("order_income =", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeNotEqualTo(Double value) {
            addCriterion("order_income <>", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeGreaterThan(Double value) {
            addCriterion("order_income >", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeGreaterThanOrEqualTo(Double value) {
            addCriterion("order_income >=", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeLessThan(Double value) {
            addCriterion("order_income <", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeLessThanOrEqualTo(Double value) {
            addCriterion("order_income <=", value, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeIn(List<Double> values) {
            addCriterion("order_income in", values, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeNotIn(List<Double> values) {
            addCriterion("order_income not in", values, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeBetween(Double value1, Double value2) {
            addCriterion("order_income between", value1, value2, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andOrderIncomeNotBetween(Double value1, Double value2) {
            addCriterion("order_income not between", value1, value2, "orderIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeIsNull() {
            addCriterion("share_income is null");
            return (Criteria) this;
        }

        public Criteria andShareIncomeIsNotNull() {
            addCriterion("share_income is not null");
            return (Criteria) this;
        }

        public Criteria andShareIncomeEqualTo(Double value) {
            addCriterion("share_income =", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeNotEqualTo(Double value) {
            addCriterion("share_income <>", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeGreaterThan(Double value) {
            addCriterion("share_income >", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeGreaterThanOrEqualTo(Double value) {
            addCriterion("share_income >=", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeLessThan(Double value) {
            addCriterion("share_income <", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeLessThanOrEqualTo(Double value) {
            addCriterion("share_income <=", value, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeIn(List<Double> values) {
            addCriterion("share_income in", values, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeNotIn(List<Double> values) {
            addCriterion("share_income not in", values, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeBetween(Double value1, Double value2) {
            addCriterion("share_income between", value1, value2, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andShareIncomeNotBetween(Double value1, Double value2) {
            addCriterion("share_income not between", value1, value2, "shareIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeIsNull() {
            addCriterion("actual_income is null");
            return (Criteria) this;
        }

        public Criteria andActualIncomeIsNotNull() {
            addCriterion("actual_income is not null");
            return (Criteria) this;
        }

        public Criteria andActualIncomeEqualTo(Double value) {
            addCriterion("actual_income =", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeNotEqualTo(Double value) {
            addCriterion("actual_income <>", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeGreaterThan(Double value) {
            addCriterion("actual_income >", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeGreaterThanOrEqualTo(Double value) {
            addCriterion("actual_income >=", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeLessThan(Double value) {
            addCriterion("actual_income <", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeLessThanOrEqualTo(Double value) {
            addCriterion("actual_income <=", value, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeIn(List<Double> values) {
            addCriterion("actual_income in", values, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeNotIn(List<Double> values) {
            addCriterion("actual_income not in", values, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeBetween(Double value1, Double value2) {
            addCriterion("actual_income between", value1, value2, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andActualIncomeNotBetween(Double value1, Double value2) {
            addCriterion("actual_income not between", value1, value2, "actualIncome");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropIsNull() {
            addCriterion("division_doctor_prop is null");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropIsNotNull() {
            addCriterion("division_doctor_prop is not null");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropEqualTo(Integer value) {
            addCriterion("division_doctor_prop =", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropNotEqualTo(Integer value) {
            addCriterion("division_doctor_prop <>", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropGreaterThan(Integer value) {
            addCriterion("division_doctor_prop >", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropGreaterThanOrEqualTo(Integer value) {
            addCriterion("division_doctor_prop >=", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropLessThan(Integer value) {
            addCriterion("division_doctor_prop <", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropLessThanOrEqualTo(Integer value) {
            addCriterion("division_doctor_prop <=", value, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropIn(List<Integer> values) {
            addCriterion("division_doctor_prop in", values, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropNotIn(List<Integer> values) {
            addCriterion("division_doctor_prop not in", values, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropBetween(Integer value1, Integer value2) {
            addCriterion("division_doctor_prop between", value1, value2, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorPropNotBetween(Integer value1, Integer value2) {
            addCriterion("division_doctor_prop not between", value1, value2, "divisionDoctorProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropIsNull() {
            addCriterion("division_group_prop is null");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropIsNotNull() {
            addCriterion("division_group_prop is not null");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropEqualTo(Integer value) {
            addCriterion("division_group_prop =", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropNotEqualTo(Integer value) {
            addCriterion("division_group_prop <>", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropGreaterThan(Integer value) {
            addCriterion("division_group_prop >", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropGreaterThanOrEqualTo(Integer value) {
            addCriterion("division_group_prop >=", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropLessThan(Integer value) {
            addCriterion("division_group_prop <", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropLessThanOrEqualTo(Integer value) {
            addCriterion("division_group_prop <=", value, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropIn(List<Integer> values) {
            addCriterion("division_group_prop in", values, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropNotIn(List<Integer> values) {
            addCriterion("division_group_prop not in", values, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropBetween(Integer value1, Integer value2) {
            addCriterion("division_group_prop between", value1, value2, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupPropNotBetween(Integer value1, Integer value2) {
            addCriterion("division_group_prop not between", value1, value2, "divisionGroupProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropIsNull() {
            addCriterion("division_sys_prop is null");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropIsNotNull() {
            addCriterion("division_sys_prop is not null");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropEqualTo(Integer value) {
            addCriterion("division_sys_prop =", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropNotEqualTo(Integer value) {
            addCriterion("division_sys_prop <>", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropGreaterThan(Integer value) {
            addCriterion("division_sys_prop >", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropGreaterThanOrEqualTo(Integer value) {
            addCriterion("division_sys_prop >=", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropLessThan(Integer value) {
            addCriterion("division_sys_prop <", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropLessThanOrEqualTo(Integer value) {
            addCriterion("division_sys_prop <=", value, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropIn(List<Integer> values) {
            addCriterion("division_sys_prop in", values, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropNotIn(List<Integer> values) {
            addCriterion("division_sys_prop not in", values, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropBetween(Integer value1, Integer value2) {
            addCriterion("division_sys_prop between", value1, value2, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionSysPropNotBetween(Integer value1, Integer value2) {
            addCriterion("division_sys_prop not between", value1, value2, "divisionSysProp");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdIsNull() {
            addCriterion("division_doctor_id is null");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdIsNotNull() {
            addCriterion("division_doctor_id is not null");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdEqualTo(Integer value) {
            addCriterion("division_doctor_id =", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdNotEqualTo(Integer value) {
            addCriterion("division_doctor_id <>", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdGreaterThan(Integer value) {
            addCriterion("division_doctor_id >", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("division_doctor_id >=", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdLessThan(Integer value) {
            addCriterion("division_doctor_id <", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdLessThanOrEqualTo(Integer value) {
            addCriterion("division_doctor_id <=", value, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdIn(List<Integer> values) {
            addCriterion("division_doctor_id in", values, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdNotIn(List<Integer> values) {
            addCriterion("division_doctor_id not in", values, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdBetween(Integer value1, Integer value2) {
            addCriterion("division_doctor_id between", value1, value2, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionDoctorIdNotBetween(Integer value1, Integer value2) {
            addCriterion("division_doctor_id not between", value1, value2, "divisionDoctorId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdIsNull() {
            addCriterion("division_group_id is null");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdIsNotNull() {
            addCriterion("division_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdEqualTo(String value) {
            addCriterion("division_group_id =", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdNotEqualTo(String value) {
            addCriterion("division_group_id <>", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdGreaterThan(String value) {
            addCriterion("division_group_id >", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdGreaterThanOrEqualTo(String value) {
            addCriterion("division_group_id >=", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdLessThan(String value) {
            addCriterion("division_group_id <", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdLessThanOrEqualTo(String value) {
            addCriterion("division_group_id <=", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdLike(String value) {
            addCriterion("division_group_id like", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdNotLike(String value) {
            addCriterion("division_group_id not like", value, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdIn(List<String> values) {
            addCriterion("division_group_id in", values, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdNotIn(List<String> values) {
            addCriterion("division_group_id not in", values, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdBetween(String value1, String value2) {
            addCriterion("division_group_id between", value1, value2, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andDivisionGroupIdNotBetween(String value1, String value2) {
            addCriterion("division_group_id not between", value1, value2, "divisionGroupId");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIsNull() {
            addCriterion("settle_status is null");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIsNotNull() {
            addCriterion("settle_status is not null");
            return (Criteria) this;
        }

        public Criteria andSettleStatusEqualTo(String value) {
            addCriterion("settle_status =", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotEqualTo(String value) {
            addCriterion("settle_status <>", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusGreaterThan(String value) {
            addCriterion("settle_status >", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusGreaterThanOrEqualTo(String value) {
            addCriterion("settle_status >=", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusLessThan(String value) {
            addCriterion("settle_status <", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusLessThanOrEqualTo(String value) {
            addCriterion("settle_status <=", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusLike(String value) {
            addCriterion("settle_status like", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotLike(String value) {
            addCriterion("settle_status not like", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIn(List<String> values) {
            addCriterion("settle_status in", values, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotIn(List<String> values) {
            addCriterion("settle_status not in", values, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusBetween(String value1, String value2) {
            addCriterion("settle_status between", value1, value2, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotBetween(String value1, String value2) {
            addCriterion("settle_status not between", value1, value2, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusIsNull() {
            addCriterion("order_status is null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusIsNotNull() {
            addCriterion("order_status is not null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusEqualTo(String value) {
            addCriterion("order_status =", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusNotEqualTo(String value) {
            addCriterion("order_status <>", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusGreaterThan(String value) {
            addCriterion("order_status >", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusGreaterThanOrEqualTo(String value) {
            addCriterion("order_status >=", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusLessThan(String value) {
            addCriterion("order_status <", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusLessThanOrEqualTo(String value) {
            addCriterion("order_status <=", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusLike(String value) {
            addCriterion("order_status like", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusNotLike(String value) {
            addCriterion("order_status not like", value, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusIn(List<String> values) {
            addCriterion("order_status in", values, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusNotIn(List<String> values) {
            addCriterion("order_status not in", values, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusBetween(String value1, String value2) {
            addCriterion("order_status between", value1, value2, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andOrderStatusNotBetween(String value1, String value2) {
            addCriterion("order_status not between", value1, value2, "orderStatus");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
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

        public Criteria andCompleteTimeIsNull() {
            addCriterion("complete_time is null");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeIsNotNull() {
            addCriterion("complete_time is not null");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeEqualTo(Long value) {
            addCriterion("complete_time =", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotEqualTo(Long value) {
            addCriterion("complete_time <>", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeGreaterThan(Long value) {
            addCriterion("complete_time >", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("complete_time >=", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeLessThan(Long value) {
            addCriterion("complete_time <", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeLessThanOrEqualTo(Long value) {
            addCriterion("complete_time <=", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeIn(List<Long> values) {
            addCriterion("complete_time in", values, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotIn(List<Long> values) {
            addCriterion("complete_time not in", values, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeBetween(Long value1, Long value2) {
            addCriterion("complete_time between", value1, value2, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotBetween(Long value1, Long value2) {
            addCriterion("complete_time not between", value1, value2, "completeTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeIsNull() {
            addCriterion("settle_time is null");
            return (Criteria) this;
        }

        public Criteria andSettleTimeIsNotNull() {
            addCriterion("settle_time is not null");
            return (Criteria) this;
        }

        public Criteria andSettleTimeEqualTo(Long value) {
            addCriterion("settle_time =", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeNotEqualTo(Long value) {
            addCriterion("settle_time <>", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeGreaterThan(Long value) {
            addCriterion("settle_time >", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("settle_time >=", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeLessThan(Long value) {
            addCriterion("settle_time <", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeLessThanOrEqualTo(Long value) {
            addCriterion("settle_time <=", value, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeIn(List<Long> values) {
            addCriterion("settle_time in", values, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeNotIn(List<Long> values) {
            addCriterion("settle_time not in", values, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeBetween(Long value1, Long value2) {
            addCriterion("settle_time between", value1, value2, "settleTime");
            return (Criteria) this;
        }

        public Criteria andSettleTimeNotBetween(Long value1, Long value2) {
            addCriterion("settle_time not between", value1, value2, "settleTime");
            return (Criteria) this;
        }

        public Criteria andExtend1IsNull() {
            addCriterion("extend_1 is null");
            return (Criteria) this;
        }

        public Criteria andExtend1IsNotNull() {
            addCriterion("extend_1 is not null");
            return (Criteria) this;
        }

        public Criteria andExtend1EqualTo(String value) {
            addCriterion("extend_1 =", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1NotEqualTo(String value) {
            addCriterion("extend_1 <>", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1GreaterThan(String value) {
            addCriterion("extend_1 >", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1GreaterThanOrEqualTo(String value) {
            addCriterion("extend_1 >=", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1LessThan(String value) {
            addCriterion("extend_1 <", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1LessThanOrEqualTo(String value) {
            addCriterion("extend_1 <=", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1Like(String value) {
            addCriterion("extend_1 like", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1NotLike(String value) {
            addCriterion("extend_1 not like", value, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1In(List<String> values) {
            addCriterion("extend_1 in", values, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1NotIn(List<String> values) {
            addCriterion("extend_1 not in", values, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1Between(String value1, String value2) {
            addCriterion("extend_1 between", value1, value2, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend1NotBetween(String value1, String value2) {
            addCriterion("extend_1 not between", value1, value2, "extend1");
            return (Criteria) this;
        }

        public Criteria andExtend2IsNull() {
            addCriterion("extend_2 is null");
            return (Criteria) this;
        }

        public Criteria andExtend2IsNotNull() {
            addCriterion("extend_2 is not null");
            return (Criteria) this;
        }

        public Criteria andExtend2EqualTo(String value) {
            addCriterion("extend_2 =", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2NotEqualTo(String value) {
            addCriterion("extend_2 <>", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2GreaterThan(String value) {
            addCriterion("extend_2 >", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2GreaterThanOrEqualTo(String value) {
            addCriterion("extend_2 >=", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2LessThan(String value) {
            addCriterion("extend_2 <", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2LessThanOrEqualTo(String value) {
            addCriterion("extend_2 <=", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2Like(String value) {
            addCriterion("extend_2 like", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2NotLike(String value) {
            addCriterion("extend_2 not like", value, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2In(List<String> values) {
            addCriterion("extend_2 in", values, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2NotIn(List<String> values) {
            addCriterion("extend_2 not in", values, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2Between(String value1, String value2) {
            addCriterion("extend_2 between", value1, value2, "extend2");
            return (Criteria) this;
        }

        public Criteria andExtend2NotBetween(String value1, String value2) {
            addCriterion("extend_2 not between", value1, value2, "extend2");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * @author 李淼淼
     * @version 1.0 2016-01-23
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