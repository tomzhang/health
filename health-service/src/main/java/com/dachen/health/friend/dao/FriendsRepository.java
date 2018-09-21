package com.dachen.health.friend.dao;

import java.util.List;

public interface FriendsRepository {

    List<Object> queryBlacklist(int userId);

    List<Integer> getToUserIdList(Integer userId);

}
