package com.dachen.health.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants;
import com.dachen.health.commons.dao.UserExpandRepository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@Deprecated
@RestController
@RequestMapping("/user")
public class ThridPartyAccountController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserExpandRepository userExpandRepository;

 

	@RequestMapping(value = "/accounts/add")
	public JSONMessage add(@ModelAttribute User.ThridPartyAccount account) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(account.getTpName()) || StringUtil.isEmpty(account.getTpAccount())
				|| StringUtil.isEmpty(account.getTpUserId())) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			int userId = ReqUtil.instance.getUserId();
			userExpandRepository.addAccount(userId, account);

			jMessage = JSONMessage.success(null);
		}

		return jMessage;
	}

	@RequestMapping(value = "/accounts/delete")
	public JSONMessage delete(@RequestParam String tpName) {
		int userId = ReqUtil.instance.getUserId();
		userExpandRepository.deleteAccount(userId, tpName);

		return JSONMessage.success(null);
	}

}
