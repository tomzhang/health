package com.dachen.health.pack.patient.model;

import java.util.ArrayList;
import java.util.List;

public class CallResultExample {
	
	
	private String 	recordStatus;
	
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;
    
    
    public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public CallResultExample() {
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
     * 回拨结果
     * @author 李淼淼
     * @version 1.0 2015-08-20
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

        public Criteria andEventIsNull() {
            addCriterion("event is null");
            return (Criteria) this;
        }

        public Criteria andEventIsNotNull() {
            addCriterion("event is not null");
            return (Criteria) this;
        }

        public Criteria andEventEqualTo(String value) {
            addCriterion("event =", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventNotEqualTo(String value) {
            addCriterion("event <>", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventGreaterThan(String value) {
            addCriterion("event >", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventGreaterThanOrEqualTo(String value) {
            addCriterion("event >=", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventLessThan(String value) {
            addCriterion("event <", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventLessThanOrEqualTo(String value) {
            addCriterion("event <=", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventLike(String value) {
            addCriterion("event like", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventNotLike(String value) {
            addCriterion("event not like", value, "event");
            return (Criteria) this;
        }

        public Criteria andEventIn(List<String> values) {
            addCriterion("event in", values, "event");
            return (Criteria) this;
        }

        public Criteria andEventNotIn(List<String> values) {
            addCriterion("event not in", values, "event");
            return (Criteria) this;
        }

        public Criteria andEventBetween(String value1, String value2) {
            addCriterion("event between", value1, value2, "event");
            return (Criteria) this;
        }

        public Criteria andEventNotBetween(String value1, String value2) {
            addCriterion("event not between", value1, value2, "event");
            return (Criteria) this;
        }

        public Criteria andCallidIsNull() {
            addCriterion("callid is null");
            return (Criteria) this;
        }

        public Criteria andCallidIsNotNull() {
            addCriterion("callid is not null");
            return (Criteria) this;
        }

        public Criteria andCallidEqualTo(String value) {
            addCriterion("callid =", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidNotEqualTo(String value) {
            addCriterion("callid <>", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidGreaterThan(String value) {
            addCriterion("callid >", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidGreaterThanOrEqualTo(String value) {
            addCriterion("callid >=", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidLessThan(String value) {
            addCriterion("callid <", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidLessThanOrEqualTo(String value) {
            addCriterion("callid <=", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidLike(String value) {
            addCriterion("callid like", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidNotLike(String value) {
            addCriterion("callid not like", value, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidIn(List<String> values) {
            addCriterion("callid in", values, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidNotIn(List<String> values) {
            addCriterion("callid not in", values, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidBetween(String value1, String value2) {
            addCriterion("callid between", value1, value2, "callid");
            return (Criteria) this;
        }

        public Criteria andCallidNotBetween(String value1, String value2) {
            addCriterion("callid not between", value1, value2, "callid");
            return (Criteria) this;
        }

        public Criteria andAccountidIsNull() {
            addCriterion("accountid is null");
            return (Criteria) this;
        }

        public Criteria andAccountidIsNotNull() {
            addCriterion("accountid is not null");
            return (Criteria) this;
        }

        public Criteria andAccountidEqualTo(String value) {
            addCriterion("accountid =", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidNotEqualTo(String value) {
            addCriterion("accountid <>", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidGreaterThan(String value) {
            addCriterion("accountid >", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidGreaterThanOrEqualTo(String value) {
            addCriterion("accountid >=", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidLessThan(String value) {
            addCriterion("accountid <", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidLessThanOrEqualTo(String value) {
            addCriterion("accountid <=", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidLike(String value) {
            addCriterion("accountid like", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidNotLike(String value) {
            addCriterion("accountid not like", value, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidIn(List<String> values) {
            addCriterion("accountid in", values, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidNotIn(List<String> values) {
            addCriterion("accountid not in", values, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidBetween(String value1, String value2) {
            addCriterion("accountid between", value1, value2, "accountid");
            return (Criteria) this;
        }

        public Criteria andAccountidNotBetween(String value1, String value2) {
            addCriterion("accountid not between", value1, value2, "accountid");
            return (Criteria) this;
        }

        public Criteria andAppidIsNull() {
            addCriterion("appid is null");
            return (Criteria) this;
        }

        public Criteria andAppidIsNotNull() {
            addCriterion("appid is not null");
            return (Criteria) this;
        }

        public Criteria andAppidEqualTo(String value) {
            addCriterion("appid =", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidNotEqualTo(String value) {
            addCriterion("appid <>", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidGreaterThan(String value) {
            addCriterion("appid >", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidGreaterThanOrEqualTo(String value) {
            addCriterion("appid >=", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidLessThan(String value) {
            addCriterion("appid <", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidLessThanOrEqualTo(String value) {
            addCriterion("appid <=", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidLike(String value) {
            addCriterion("appid like", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidNotLike(String value) {
            addCriterion("appid not like", value, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidIn(List<String> values) {
            addCriterion("appid in", values, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidNotIn(List<String> values) {
            addCriterion("appid not in", values, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidBetween(String value1, String value2) {
            addCriterion("appid between", value1, value2, "appid");
            return (Criteria) this;
        }

        public Criteria andAppidNotBetween(String value1, String value2) {
            addCriterion("appid not between", value1, value2, "appid");
            return (Criteria) this;
        }

        public Criteria andConfidIsNull() {
            addCriterion("confid is null");
            return (Criteria) this;
        }

        public Criteria andConfidIsNotNull() {
            addCriterion("confid is not null");
            return (Criteria) this;
        }

        public Criteria andConfidEqualTo(String value) {
            addCriterion("confid =", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidNotEqualTo(String value) {
            addCriterion("confid <>", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidGreaterThan(String value) {
            addCriterion("confid >", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidGreaterThanOrEqualTo(String value) {
            addCriterion("confid >=", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidLessThan(String value) {
            addCriterion("confid <", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidLessThanOrEqualTo(String value) {
            addCriterion("confid <=", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidLike(String value) {
            addCriterion("confid like", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidNotLike(String value) {
            addCriterion("confid not like", value, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidIn(List<String> values) {
            addCriterion("confid in", values, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidNotIn(List<String> values) {
            addCriterion("confid not in", values, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidBetween(String value1, String value2) {
            addCriterion("confid between", value1, value2, "confid");
            return (Criteria) this;
        }

        public Criteria andConfidNotBetween(String value1, String value2) {
            addCriterion("confid not between", value1, value2, "confid");
            return (Criteria) this;
        }

        public Criteria andCalltypeIsNull() {
            addCriterion("calltype is null");
            return (Criteria) this;
        }

        public Criteria andCalltypeIsNotNull() {
            addCriterion("calltype is not null");
            return (Criteria) this;
        }

        public Criteria andCalltypeEqualTo(Byte value) {
            addCriterion("calltype =", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeNotEqualTo(Byte value) {
            addCriterion("calltype <>", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeGreaterThan(Byte value) {
            addCriterion("calltype >", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("calltype >=", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeLessThan(Byte value) {
            addCriterion("calltype <", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeLessThanOrEqualTo(Byte value) {
            addCriterion("calltype <=", value, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeIn(List<Byte> values) {
            addCriterion("calltype in", values, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeNotIn(List<Byte> values) {
            addCriterion("calltype not in", values, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeBetween(Byte value1, Byte value2) {
            addCriterion("calltype between", value1, value2, "calltype");
            return (Criteria) this;
        }

        public Criteria andCalltypeNotBetween(Byte value1, Byte value2) {
            addCriterion("calltype not between", value1, value2, "calltype");
            return (Criteria) this;
        }

        public Criteria andCallertypeIsNull() {
            addCriterion("callertype is null");
            return (Criteria) this;
        }

        public Criteria andCallertypeIsNotNull() {
            addCriterion("callertype is not null");
            return (Criteria) this;
        }

        public Criteria andCallertypeEqualTo(Byte value) {
            addCriterion("callertype =", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeNotEqualTo(Byte value) {
            addCriterion("callertype <>", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeGreaterThan(Byte value) {
            addCriterion("callertype >", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("callertype >=", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeLessThan(Byte value) {
            addCriterion("callertype <", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeLessThanOrEqualTo(Byte value) {
            addCriterion("callertype <=", value, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeIn(List<Byte> values) {
            addCriterion("callertype in", values, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeNotIn(List<Byte> values) {
            addCriterion("callertype not in", values, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeBetween(Byte value1, Byte value2) {
            addCriterion("callertype between", value1, value2, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallertypeNotBetween(Byte value1, Byte value2) {
            addCriterion("callertype not between", value1, value2, "callertype");
            return (Criteria) this;
        }

        public Criteria andCallerIsNull() {
            addCriterion("caller is null");
            return (Criteria) this;
        }

        public Criteria andCallerIsNotNull() {
            addCriterion("caller is not null");
            return (Criteria) this;
        }

        public Criteria andCallerEqualTo(String value) {
            addCriterion("caller =", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerNotEqualTo(String value) {
            addCriterion("caller <>", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerGreaterThan(String value) {
            addCriterion("caller >", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerGreaterThanOrEqualTo(String value) {
            addCriterion("caller >=", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerLessThan(String value) {
            addCriterion("caller <", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerLessThanOrEqualTo(String value) {
            addCriterion("caller <=", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerLike(String value) {
            addCriterion("caller like", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerNotLike(String value) {
            addCriterion("caller not like", value, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerIn(List<String> values) {
            addCriterion("caller in", values, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerNotIn(List<String> values) {
            addCriterion("caller not in", values, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerBetween(String value1, String value2) {
            addCriterion("caller between", value1, value2, "caller");
            return (Criteria) this;
        }

        public Criteria andCallerNotBetween(String value1, String value2) {
            addCriterion("caller not between", value1, value2, "caller");
            return (Criteria) this;
        }

        public Criteria andCalledtypeIsNull() {
            addCriterion("calledtype is null");
            return (Criteria) this;
        }

        public Criteria andCalledtypeIsNotNull() {
            addCriterion("calledtype is not null");
            return (Criteria) this;
        }

        public Criteria andCalledtypeEqualTo(Byte value) {
            addCriterion("calledtype =", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeNotEqualTo(Byte value) {
            addCriterion("calledtype <>", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeGreaterThan(Byte value) {
            addCriterion("calledtype >", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("calledtype >=", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeLessThan(Byte value) {
            addCriterion("calledtype <", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeLessThanOrEqualTo(Byte value) {
            addCriterion("calledtype <=", value, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeIn(List<Byte> values) {
            addCriterion("calledtype in", values, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeNotIn(List<Byte> values) {
            addCriterion("calledtype not in", values, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeBetween(Byte value1, Byte value2) {
            addCriterion("calledtype between", value1, value2, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledtypeNotBetween(Byte value1, Byte value2) {
            addCriterion("calledtype not between", value1, value2, "calledtype");
            return (Criteria) this;
        }

        public Criteria andCalledIsNull() {
            addCriterion("called is null");
            return (Criteria) this;
        }

        public Criteria andCalledIsNotNull() {
            addCriterion("called is not null");
            return (Criteria) this;
        }

        public Criteria andCalledEqualTo(String value) {
            addCriterion("called =", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledNotEqualTo(String value) {
            addCriterion("called <>", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledGreaterThan(String value) {
            addCriterion("called >", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledGreaterThanOrEqualTo(String value) {
            addCriterion("called >=", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledLessThan(String value) {
            addCriterion("called <", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledLessThanOrEqualTo(String value) {
            addCriterion("called <=", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledLike(String value) {
            addCriterion("called like", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledNotLike(String value) {
            addCriterion("called not like", value, "called");
            return (Criteria) this;
        }

        public Criteria andCalledIn(List<String> values) {
            addCriterion("called in", values, "called");
            return (Criteria) this;
        }

        public Criteria andCalledNotIn(List<String> values) {
            addCriterion("called not in", values, "called");
            return (Criteria) this;
        }

        public Criteria andCalledBetween(String value1, String value2) {
            addCriterion("called between", value1, value2, "called");
            return (Criteria) this;
        }

        public Criteria andCalledNotBetween(String value1, String value2) {
            addCriterion("called not between", value1, value2, "called");
            return (Criteria) this;
        }

        public Criteria andStarttimeIsNull() {
            addCriterion("starttime is null");
            return (Criteria) this;
        }

        public Criteria andStarttimeIsNotNull() {
            addCriterion("starttime is not null");
            return (Criteria) this;
        }

        public Criteria andStarttimeEqualTo(String value) {
            addCriterion("starttime =", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotEqualTo(String value) {
            addCriterion("starttime <>", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeGreaterThan(String value) {
            addCriterion("starttime >", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeGreaterThanOrEqualTo(String value) {
            addCriterion("starttime >=", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeLessThan(String value) {
            addCriterion("starttime <", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeLessThanOrEqualTo(String value) {
            addCriterion("starttime <=", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeLike(String value) {
            addCriterion("starttime like", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotLike(String value) {
            addCriterion("starttime not like", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeIn(List<String> values) {
            addCriterion("starttime in", values, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotIn(List<String> values) {
            addCriterion("starttime not in", values, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeBetween(String value1, String value2) {
            addCriterion("starttime between", value1, value2, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotBetween(String value1, String value2) {
            addCriterion("starttime not between", value1, value2, "starttime");
            return (Criteria) this;
        }

        public Criteria andStoptimeIsNull() {
            addCriterion("stoptime is null");
            return (Criteria) this;
        }

        public Criteria andStoptimeIsNotNull() {
            addCriterion("stoptime is not null");
            return (Criteria) this;
        }

        public Criteria andStoptimeEqualTo(String value) {
            addCriterion("stoptime =", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeNotEqualTo(String value) {
            addCriterion("stoptime <>", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeGreaterThan(String value) {
            addCriterion("stoptime >", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeGreaterThanOrEqualTo(String value) {
            addCriterion("stoptime >=", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeLessThan(String value) {
            addCriterion("stoptime <", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeLessThanOrEqualTo(String value) {
            addCriterion("stoptime <=", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeLike(String value) {
            addCriterion("stoptime like", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeNotLike(String value) {
            addCriterion("stoptime not like", value, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeIn(List<String> values) {
            addCriterion("stoptime in", values, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeNotIn(List<String> values) {
            addCriterion("stoptime not in", values, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeBetween(String value1, String value2) {
            addCriterion("stoptime between", value1, value2, "stoptime");
            return (Criteria) this;
        }

        public Criteria andStoptimeNotBetween(String value1, String value2) {
            addCriterion("stoptime not between", value1, value2, "stoptime");
            return (Criteria) this;
        }

        public Criteria andLengthIsNull() {
            addCriterion("length is null");
            return (Criteria) this;
        }

        public Criteria andLengthIsNotNull() {
            addCriterion("length is not null");
            return (Criteria) this;
        }

        public Criteria andLengthEqualTo(Integer value) {
            addCriterion("length =", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotEqualTo(Integer value) {
            addCriterion("length <>", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthGreaterThan(Integer value) {
            addCriterion("length >", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthGreaterThanOrEqualTo(Integer value) {
            addCriterion("length >=", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthLessThan(Integer value) {
            addCriterion("length <", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthLessThanOrEqualTo(Integer value) {
            addCriterion("length <=", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthIn(List<Integer> values) {
            addCriterion("length in", values, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotIn(List<Integer> values) {
            addCriterion("length not in", values, "length");
            return (Criteria) this;
        }

        public Criteria andLengthBetween(Integer value1, Integer value2) {
            addCriterion("length between", value1, value2, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotBetween(Integer value1, Integer value2) {
            addCriterion("length not between", value1, value2, "length");
            return (Criteria) this;
        }

        public Criteria andRecordurlIsNull() {
            addCriterion("recordurl is null");
            return (Criteria) this;
        }

        public Criteria andRecordurlIsNotNull() {
            addCriterion("recordurl is not null");
            return (Criteria) this;
        }

        public Criteria andRecordurlEqualTo(String value) {
            addCriterion("recordurl =", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlNotEqualTo(String value) {
            addCriterion("recordurl <>", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlGreaterThan(String value) {
            addCriterion("recordurl >", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlGreaterThanOrEqualTo(String value) {
            addCriterion("recordurl >=", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlLessThan(String value) {
            addCriterion("recordurl <", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlLessThanOrEqualTo(String value) {
            addCriterion("recordurl <=", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlLike(String value) {
            addCriterion("recordurl like", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlNotLike(String value) {
            addCriterion("recordurl not like", value, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlIn(List<String> values) {
            addCriterion("recordurl in", values, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlNotIn(List<String> values) {
            addCriterion("recordurl not in", values, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlBetween(String value1, String value2) {
            addCriterion("recordurl between", value1, value2, "recordurl");
            return (Criteria) this;
        }

        public Criteria andRecordurlNotBetween(String value1, String value2) {
            addCriterion("recordurl not between", value1, value2, "recordurl");
            return (Criteria) this;
        }

        public Criteria andUserDataIsNull() {
            addCriterion("user_data is null");
            return (Criteria) this;
        }

        public Criteria andUserDataIsNotNull() {
            addCriterion("user_data is not null");
            return (Criteria) this;
        }

        public Criteria andUserDataEqualTo(String value) {
            addCriterion("user_data =", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataNotEqualTo(String value) {
            addCriterion("user_data <>", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataGreaterThan(String value) {
            addCriterion("user_data >", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataGreaterThanOrEqualTo(String value) {
            addCriterion("user_data >=", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataLessThan(String value) {
            addCriterion("user_data <", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataLessThanOrEqualTo(String value) {
            addCriterion("user_data <=", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataLike(String value) {
            addCriterion("user_data like", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataNotLike(String value) {
            addCriterion("user_data not like", value, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataIn(List<String> values) {
            addCriterion("user_data in", values, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataNotIn(List<String> values) {
            addCriterion("user_data not in", values, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataBetween(String value1, String value2) {
            addCriterion("user_data between", value1, value2, "userData");
            return (Criteria) this;
        }

        public Criteria andUserDataNotBetween(String value1, String value2) {
            addCriterion("user_data not between", value1, value2, "userData");
            return (Criteria) this;
        }

        public Criteria andReasonIsNull() {
            addCriterion("reason is null");
            return (Criteria) this;
        }

        public Criteria andReasonIsNotNull() {
            addCriterion("reason is not null");
            return (Criteria) this;
        }

        public Criteria andReasonEqualTo(String value) {
            addCriterion("reason =", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonNotEqualTo(String value) {
            addCriterion("reason <>", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonGreaterThan(String value) {
            addCriterion("reason >", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonGreaterThanOrEqualTo(String value) {
            addCriterion("reason >=", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonLessThan(String value) {
            addCriterion("reason <", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonLessThanOrEqualTo(String value) {
            addCriterion("reason <=", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonLike(String value) {
            addCriterion("reason like", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonNotLike(String value) {
            addCriterion("reason not like", value, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonIn(List<String> values) {
            addCriterion("reason in", values, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonNotIn(List<String> values) {
            addCriterion("reason not in", values, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonBetween(String value1, String value2) {
            addCriterion("reason between", value1, value2, "reason");
            return (Criteria) this;
        }

        public Criteria andReasonNotBetween(String value1, String value2) {
            addCriterion("reason not between", value1, value2, "reason");
            return (Criteria) this;
        }

        public Criteria andSubreasonIsNull() {
            addCriterion("subreason is null");
            return (Criteria) this;
        }

        public Criteria andSubreasonIsNotNull() {
            addCriterion("subreason is not null");
            return (Criteria) this;
        }

        public Criteria andSubreasonEqualTo(String value) {
            addCriterion("subreason =", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonNotEqualTo(String value) {
            addCriterion("subreason <>", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonGreaterThan(String value) {
            addCriterion("subreason >", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonGreaterThanOrEqualTo(String value) {
            addCriterion("subreason >=", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonLessThan(String value) {
            addCriterion("subreason <", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonLessThanOrEqualTo(String value) {
            addCriterion("subreason <=", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonLike(String value) {
            addCriterion("subreason like", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonNotLike(String value) {
            addCriterion("subreason not like", value, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonIn(List<String> values) {
            addCriterion("subreason in", values, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonNotIn(List<String> values) {
            addCriterion("subreason not in", values, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonBetween(String value1, String value2) {
            addCriterion("subreason between", value1, value2, "subreason");
            return (Criteria) this;
        }

        public Criteria andSubreasonNotBetween(String value1, String value2) {
            addCriterion("subreason not between", value1, value2, "subreason");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * 回拨结果
     * @author 李淼淼
     * @version 1.0 2015-08-20
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