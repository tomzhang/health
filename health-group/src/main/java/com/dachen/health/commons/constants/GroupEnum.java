package com.dachen.health.commons.constants;

import java.util.HashMap;
import java.util.Map;

public class GroupEnum {

    /**
     * ProjectName： health-group<br>
     * ClassName： GroupDoctorStatus<br>
     * Description： 集团医生状态<br>
     * 
     * @author fanp
     * @createTime 2015年9月16日
     * @version 1.0.0
     */
    public enum GroupDoctorStatus {
        正在使用("C", "正在使用"), 
        邀请待确认("I", "邀请待确认"),
        离职("S", "离职"), 
        踢出("O", "踢出"), 
        邀请拒绝("N", "邀请拒绝"),
        申请待确认("J", "申请待确认"),
        申请拒绝("M", "申请拒绝"),
        
        //用于描述 医生与医生集团的关系而不是状态，为了统一，放在这里 add by wangqiao
    	允许申请("A", "允许申请"),
    	不允许申请("D", "不允许申请");
    	
        private String index;

        private String title;

        GroupDoctorStatus(String index, String title) {
            this.index = index;
            this.title = title;
        }

		private static final Map<String, GroupDoctorStatus> mapping;
		static {
			GroupDoctorStatus[] types = GroupDoctorStatus.values();
			mapping = new HashMap<>(types.length);
			for (GroupDoctorStatus type:types) {
				mapping.put(type.getIndex(), type);
			}
		}

		public static GroupDoctorStatus eval(String index) {
			return mapping.get(index);
		}

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
    
    public enum GroupCertStatus {
    	noCert("NC", "未认证"),
    	noPass("NP", "未通过"),
    	auditing("A", "待审核"),
    	passed("P", "已通过");
    	
    	private String index;

        private String title;
        
        private GroupCertStatus(String index, String title) {
            this.index = index;
            this.title = title;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    public enum GroupSkipStatus{
    	normal("N", "正常"),
    	skip("S", "屏蔽");
    	
    	private String index;
    	private String status;
		private GroupSkipStatus(String index, String status) {
			this.index = index;
			this.status = status;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
    	
    }
    
	/**
	 * 集团申请状态
	 * @author wangqiao
	 * @date 2016年3月4日
	 */
	public enum GroupApplyStatus {
		audit("A", "待审核"), 
		pass("P", "审核通过"),
		notpass("NP", "审核未通过");

		private String index;
		private String title;

		private GroupApplyStatus(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
    
    /**
     * 集团用户类型
     *@author wangqiao
     *@date 2015年12月22日
     *
     */
    public enum GroupUserType {
    	公司用户(1, "公司用户"),
    	集团用户(2, "集团用户");
//    	集团用户(2, "集团用户"),
//    	医院用户(3, "医院用户");
    	
    	private Integer index;

        private String title;
        
        private GroupUserType(Integer index, String title) {
            this.index = index;
            this.title = title;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    public enum GroupUserStatus {
    	//add by wangqiao  以下为对应数据库中的状态常量
    	邀请待通过("I", "邀请待通过"),
    	正常使用("C", "正常使用"),
    	已离职("S", "已离职"),
    	邀请拒绝("N", "邀请拒绝"),
    	
    	//以下为返回给客户端的状态
    	未加入集团("1", "未加入集团"),
    	加入集团非管理员("2", "加入集团非管理员"),
    	加入集团是管理员("3", "加入集团是管理员"),
    	加入集团非管理员且未认证("4", "加入集团非管理员且未认证");

    	
     
    	
    	private String index;

        private String title;
        
        private GroupUserStatus(String index, String title) {
            this.index = index;
            this.title = title;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
	public enum OnLineState {
		onLine("1", "在线"), 
		offLine("2", "离线");

		private String index;
		private String title;

		private OnLineState(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum GroupApply {
		audit("A", "待审核"), 
		pass("P", "审核通过"),
		notpass("NP", "审核未通过");

		private String index;
		private String title;

		private GroupApply(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum GroupTransfer {
		tobeConfirm("A", "待确认"), 
		confirmPass("P", "确认通过"),
		notpassNoPass("NP", "确认未通过"),
		expire("E", "过期");

		private String index;
		private String title;

		private GroupTransfer(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum GroupActive {
		//active=已激活，inactive=未激活
		active("active", "已激活"), 
		inactive("inactive", "未激活");

		private String index;
		private String title;

		private GroupActive(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum GroupType {
		hospital("hospital", "医院"), 
		group("group", "集团"),//现在是圈子 2017年6月26日
		dept("dept", "科室");

		private String index;
		private String title;

		GroupType(String index, String title) {
			this.index = index;
			this.title = title;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		private static final Map<String, GroupType> mapping;
		static {
			GroupType[] types = GroupType.values();
			mapping = new HashMap<>(types.length);
			for (GroupType type:types) {
				mapping.put(type.getIndex(), type);
			}
		}

		public static GroupType eval(String index) {
			return mapping.get(index);
		}
	}
	
	/**
	 * 管理员类型   
	 * 超级管理员/普通管理员
	 * @author wangqiao
	 * @date 2016年3月5日
	 */
	public enum GroupRootAdmin {
		//root=超级管理员，admin=普通管理员
		root("root", "超级管理员"), 
		admin("admin", "普通管理员");

		private String index;
		private String title;

		private GroupRootAdmin(String index, String title) {
			this.index = index;
			this.title = title;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	   /**
	    * 公司状态  A：审核中，B，审核不通过，P：审核通过，O：临时冻结，  S：已停用
	 * @author wangqiao
	 * @date 2016年4月20日
	 */
	public enum CompanyStatus {
	    	FREEZE("O", "临时冻结"),
	    	STOP("S", "已停用"),
	    	AUDITING("A", "待审核"),
	    	NOPASS("B", "审核不通过"),
	    	PASSED("P", "已通过");
	    	
	    	private String index;

	        private String title;
	        
	        private CompanyStatus(String index, String title) {
	            this.index = index;
	            this.title = title;
	        }

	        public String getIndex() {
	            return index;
	        }

	        public void setIndex(String index) {
	            this.index = index;
	        }

	        public String getTitle() {
	            return title;
	        }

	        public void setTitle(String title) {
	            this.title = title;
	        }
	    }

}
