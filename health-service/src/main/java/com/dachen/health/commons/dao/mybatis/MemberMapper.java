package com.dachen.health.commons.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.vo.Member;

public interface MemberMapper {

	int insert(Member member);

	int delete(int memberId);

	int update(Member member);

	List<Member> selectByExample(Map<String, Object> parameter);

	Member selectById(int memberId);

}
