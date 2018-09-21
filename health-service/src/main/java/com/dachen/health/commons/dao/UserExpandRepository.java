package com.dachen.health.commons.dao;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.User.ThridPartyAccount;

public interface UserExpandRepository {

	JSONMessage roomAdd(Integer userId, BasicDBObject room);

	JSONMessage roomDelete(Integer userId, Integer roomId);

	JSONMessage roomList(Integer userId);

	boolean deleteAccount(int userId, String tpName);


	boolean addAccount(int userId, User.ThridPartyAccount account);
}
