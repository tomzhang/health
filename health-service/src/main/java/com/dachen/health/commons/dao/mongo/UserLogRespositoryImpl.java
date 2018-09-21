package com.dachen.health.commons.dao.mongo;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserLog;
import com.dachen.health.user.entity.po.Change;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class UserLogRespositoryImpl extends NoSqlRepository implements UserLogRespository {

	final static String  NULLVALUE = "无";


	 @Autowired
	  private UserManager userManager;
	
	@Override
	public void addUserLog(UserLog userLog) {
		BasicDBObject jo = new BasicDBObject();
		jo.put("userId", userLog.getUserId());
		jo.put("operaterId", userLog.getOperaterId());
		jo.put("operateType", userLog.getOperateType());
		jo.put("operaterTime", System.currentTimeMillis());
		
		dsForRW.getDB().getCollection("user_log").save(jo);
	}

	@Override
	public void addOperationRecord(OperationRecord operationRecord) {
		String content = "";
		Change change = operationRecord.getChange();
		String name = change.getParamName();
		
		if (StringUtil.isBlank(change.getOldValue())) {
			change.setOldValue(NULLVALUE);
		}
		if (StringUtil.isBlank(change.getNewValue())) {
			change.setNewValue(NULLVALUE);
		}
		
		if (StringUtil.equals(change.getOldValue(), change.getNewValue())) {
			return;
		}
		
		if (StringUtil.equals(name, UserLogEnum.infoType.sex.getType())) {
			content = "性别由【"+covertSex(change.getOldValue())+"】改为【"+covertSex(change.getNewValue())+"】";
		}else if (StringUtil.equals(name, UserLogEnum.infoType.role.getType())) {
			content = "角色标签由【"+covertRole(change.getOldValue())+"】改为【"+covertRole(change.getNewValue())+"】";
		}else if (StringUtil.equals(name, UserLogEnum.infoType.assistant.getType())) {
			String oldName = NULLVALUE;
			if (!change.getOldValue().equals(NULLVALUE)) {
				User oldUser = userManager.getUser(Integer.valueOf(change.getOldValue()));
				if (oldUser != null) {
					oldName = oldUser.getName();
				}
			}
			User newUser = userManager.getUser(Integer.valueOf(change.getNewValue()));
			if (newUser != null) {
				content = "医生助手由【" +oldName+ "】改为【" +newUser.getName()+ "】";
			}
		}else if(StringUtil.equals(name, UserLogEnum.infoType.headPic.getType())){
			content = "头像由【<a class='mblue' target='_blank' href='" + change.getOldValue() +
					"'>链接】</a>改为【<a class='mblue' target='_blank' href='" + 
						change.getNewValue() + "'>链接】</a>";
		}else if (StringUtil.equals(UserLogEnum.OperateType.check.getOperate(), operationRecord.getObjectType())) {
			content = operationRecord.getContent();
		}else if (StringUtil.equals(UserLogEnum.OperateType.uncheck.getOperate(), operationRecord.getObjectType())) {
			content = operationRecord.getContent();
		}else if (StringUtil.equals(name, UserLogEnum.infoType.checkPic.getType())) {
			content = "证书图片由【" + checkPicLink(change.getOldValue()) + "】</a>改为【" + checkPicLink(change.getNewValue()) + "】</a>";
		} else if (StringUtil.equals(UserLogEnum.OperateType.inport.getOperate(), operationRecord.getObjectType())) {
			content = "运营批量导入。";
		} else if (StringUtil.equals(UserLogEnum.OperateType.farm.getOperate(), operationRecord.getObjectType())) {
			content = "农牧项目批量导入。";
		} else if (StringUtil.equals(UserLogEnum.OperateType.suspend.getOperate(), operationRecord.getObjectType()) || StringUtil.equals(UserLogEnum.OperateType.removeSuspend.getOperate(), operationRecord.getObjectType())) {
			content = operationRecord.getContent();
		} else if (StringUtil.equals(name, UserLogEnum.infoType.faceRecognition.getType())) {
			content = "人身认证状态由【" + covertFaceRec(change.getOldValue()) + "】改为【" + covertFaceRec(change.getNewValue()) + "】";
		} else if (StringUtil.equals(UserLogEnum.OperateType.tempForbid.getOperate(), operationRecord.getObjectType())) {
			content = operationRecord.getContent();
		} else if (StringUtil.equals(UserLogEnum.OperateType.doctorRecheckInfo.getOperate(), operationRecord.getObjectType())) {
			content = operationRecord.getContent();
		}
		else {
			content = change.getParamName() + "由【" + change.getOldValue() + "】改为【" + change.getNewValue() + "】";
		}
		
		operationRecord.setContent(content);
		dsForRW.save(operationRecord);
		
	}
	
	public String covertSex(String sex){
		if (StringUtil.equals(sex, String.valueOf(1))) {
			sex = "男性";
		}else if (StringUtil.equals(sex, String.valueOf(2))) {
			sex = "女性";
		}else if (StringUtil.equals(sex, String.valueOf(3))) {
			sex = "保密";
		}else if (StringUtil.equals(sex, NULLVALUE)) {
			sex = "无";
		}
		return sex;
	}

	public String covertRole(String role) {
		if (StringUtil.equals(role, String.valueOf(1))) {
			role = "医生";
		} else if (StringUtil.equals(role, String.valueOf(2))) {
			role = "护士";
		} else if (StringUtil.equals(role, String.valueOf(3))) {
			role = "其他";
		} else if (StringUtil.equals(role, String.valueOf(4))) {
			role = "管理员";
		} else if (StringUtil.equals(role, NULLVALUE)) {
			role = "无";
		}
		return role;
	}
	
	public String checkPicLink(String string){
		if (string.equals("无")) {
			return string;
		}
		String[] links = string.split(",");
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < links.length; i++) {
			sb.append("<a class='mblue' target='_blank' href=' ");
			sb.append(links[i]);
			sb.append("'>链接");
			if (i+1 != links.length) {
				sb.append(",");
			}
		}
		
		return sb.toString();
	}

	public String covertFaceRec(String passed) {
		if (StringUtil.equals(passed, String.valueOf(1))) {
			passed = "人身验证成功";
		} else if (StringUtil.equals(passed, String.valueOf(0))) {
			passed = "人身验证失败";
		} else if (Objects.isNull(passed)) {
			passed = "无";
		}
		return passed;
	}

	@Override
	public void save(OperationRecord operationRecord) {
		dsForRW.save(operationRecord);
	}
}
