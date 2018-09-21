package com.dachen.health.commons.constants;

/**
 * ProjectName： health-service<br>
 * ClassName： UserLogEnum<br>
 * Description： 用户操作枚举类<br>
 * 
 * @author 傅永德
 * @crateTime 2016年5月20日
 * @version 1.0.0
 */
public class UserLogEnum {
	public enum OperateType {
		register(1, "注册"),
		update(2, "更新"),
		check(3, "审核"),
		uncheck(4, "反审核"),
		inport(5, "运营批量导入"),
		farm(6, "农牧项目批量导入"),
		suspend(7, "挂起"),
		removeSuspend(8, "解除挂起"),
		tempForbid(9, "暂时禁用"),
		doctorRecheckInfo(10,"认证用户资料变更申请审核");
		
		private Integer index;
        private String Operate;
		private OperateType(Integer index, String operate) {
			this.index = index;
			Operate = operate;
		}
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getOperate() {
			return Operate;
		}
		public void setOperate(String operate) {
			Operate = operate;
		}
        
	}
	
	public enum infoType {
		basicInfo(1,"基础信息"),
		headPic(2,"图像链接"),
		name(3,"名字"),
		hospital(4,"医院"),
		department(5,"科室"),
		title(6,"职称"),
		sex(7,"性别"),
		introduction(8,"简介"),
		skill(9,"补充擅长"),
		role(10,"角色标签"),
		assistant(11,"医生助手"),
		licenseNum(12,"证书编号"),
		licenseExpire(13,"证书期限"),
		expertise(14,"擅长"),
		checkPic(15,"证书链接"),
		telephone(16,"手机号码"),
		userLevel(17,"用户身份"),
		limitedPeriodTime(18,"用户有效期"),
		faceRecognition(19, "人身验证"),
		deptPhone(20,"科室电话"),
		disableReason(21,"封号原因");
		
		private Integer index;
		private String type;
		
		private infoType(Integer index, String type) {
			this.index = index;
			this.type = type;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		
	}
}
