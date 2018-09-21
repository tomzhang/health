package com.dachen.health.commons.constants;

import java.util.Objects;

/**
 * ProjectName： health-service<br>
 * ClassName： UserEnum<br>
 * Description： 会员枚举类<br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
public class UserEnum {
	public final static String IS_Guest_TOKEN="guest_";
	/* 游客到期时间依据候桐需求改为永久 */
	public final static Long GUEST_LIMITED_PERIOD = 9999999999999L;
	
	@Deprecated
    /* 弃用，临时会员到期时间改为永久 */
	public final static Long TEMPUSER_LIMITED_PERIOD = 30 * 24 * 3600 * 1000L;
	
	public final static Long ADD_LIMITED_PERIOD=15*24*3600*1000L ;
	
	public final static Long FOREVER_LIMITED_PERIOD=9999999999999L ;
	
	/**发短信*/
    public static final String SEND_SMS_URL = "http://SMSSERVICE/sms/sendAllByExt";
    
    /**发短信内部接口*/
    public static final String SEND_SMS_INNER_URL = "http://SMSSERVICE//inner_api/sendAllByExt";
    
    public static  final String CIRCLE_SMS_CONTENT_TEMPLATE="%s【%s】";

    /**
     * 灰度方案
     */
    public static final String SEND_GRAY_VISIBILITY_URL = "http://GRAY/inner/AppGrayInfo/{deviceType}/{version}/{telephone}";

    /**
     * 医生注册MQ
     */
    public static final String DOCTOR_INFO_REGISTER = "doctorInfoRegister";

    /**
     * 管理员账号默认便捷验证码（不做验证）
     */
    public static final String ADMIN_FAST_PASSWORD = "6543";
	
    /**
     * ProjectName： health-service<br>
     * ClassName： UserType<br>
     * Description： 用户类型<br>
     * 
     * @author fanp
     * @crateTime 2015年6月29日
     * @version 1.0.0
     */
    public enum UserType {
        patient(1, "患者"), 
        assistant(2, "医助"), 
        doctor(3, "医生"), 
        customerService(4, "客服"),
    	companyService(5, "集团"),
    	DocGuide(6, "导医"),
    	nurse(8, "护士"),//add  by  liwei 
    	shopkeeper(9, "店主"),//add  by  yuxichu 
        enterpriseUser(10, "企业用户"),//药企使用
        shopAssistant(11, "药店成员"),//药店使用
    	guest(100,"游客");//游客
        private int index;

        private String title;

        private UserType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public static  UserType  getEnum(int index)
        {
        	UserType e=null;
            for(UserType e1:UserType.values())
                if(e1.index==index){
                    e=e1;
                    break;
                }
            return e;	
        }
    }

    /**
     * ProjectName： health-service<br>
     * ClassName： RelationStatus<br>
     * Description：关系状态 <br>
     * 
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum UserStatus {
        normal(1, "正常"),
        uncheck(2, "待审核"),
        fail(3, "审核未通过"),
        @Deprecated // 移至SuspendStatus
        tempForbid(4, "暂时禁用"),
        forbidden(5, "永久禁用"),
    	inactive(6, "未激活"),//患者端使用
    	Unautherized(7, "未认证"),//医生端使用
    	offLine(8,"离职"), //企业用户
    	logOut(9,"注销");//企业用户
        private int index;

        private String title;

        private UserStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

		public static UserStatus getEnum(int index) {
			UserStatus e = null;
			for (UserStatus e1 : UserStatus.values()){
				if (e1.index == index) {
					e = e1;
					break;
				}
			}
			return e;
		}

    }
    
    /**
     * 用户加入医院状态
     * @author wangqiao
     * @date 2016年3月26日
     */
    public enum UserHospitalStatus {
        join(1, "已加入医院"),
        unjoin(2, "未加入医院"),
    	quit(3,"已离职");
        private int index;

        private String title;

        private UserHospitalStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
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
     * ProjectName： health-service<br>
     * ClassName： RelationStatus<br>
     * Description：关系状态 <br>
     * 
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum RelationStatus {
        normal(1, "正常"), 
        deleted(2, "删除");

        private int index;

        private String title;

        private RelationStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
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
     * ProjectName： health-service<br>
     * ClassName： RelationType<br>
     * Description：关系类型 <br>
     * 
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum RelationType {
        doctorPatient("医患关系"),
        patientFriend("患者好友关系"),
        doctorFriend("医生好友关系"),
        doctorAssistant("医生医助关系");

        private String title;

        private RelationType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
    
    /**
     * ProjectName： health-service<br>
     * ClassName： TagType<br>
     * Description： 标签分类<br>
     * @author fanp
     * @crateTime 2015年7月2日
     * @version 1.0.0
     */
    public enum TagType {
        doctorPatient(1, "医患标签"),
        patientFriend(2, "患者好友标签"),
        doctorFriend(3, "医生好友标签");

        private int index;

        private String title;

        private TagType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
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
     * 医生角色
     * @author weilit
     *
     */
    public enum DoctorRole {
        doctor(1, "医生"), 
        nurse(2, "护士"),
        other(3, "其它"),
        admin(4, "管理员");

        private int index;

        private String title;

        private DoctorRole(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public static  DoctorRole  getEnum(int index)
        {
        	DoctorRole e=null;
            for(DoctorRole e1:DoctorRole.values())
                if(e1.index==index){
                    e=e1;
                    break;
                }
            return e;	
        }
    }
    
    public enum Source{
    	app(1, "app注册"),
    	group(2, "集团邀请"),
    	hospital(3, "医院邀请"),
    	groupAdmin(4, "集团新建"),
    	checkAdmin(5, "运营新建"),
    	hospitalAdmin(6, "医院新建"),
    	bdjlApp(7, "博德嘉联客户端注册"),
    	doctorInvite(8, "医生邀请"),//针对患者
    	wechatRegister(9, "微信用户注册"),
    	guideInvite(10, "博德嘉联医生助手邀请"),
    	farmAdminLot(11, "农牧项目批量导入"),
    	checkAdminLot(12, "运营平台批量导入"),
    	share(13, "分享页面注册"),
    	drugStore(14, "药店圈邀请"),
        ThirdParty(15, "第三方"),
        drugOrg(16, "药企圈"),
        doctorCircle(17, "医生圈"),
        doctorCircleInviteJoin(18, "医生圈H5邀请加入圈子"),
        circleWwhJoin(19, "医生圈周周乐分享");

    	private int index;
    	private String source;
    	
		Source(int index, String source) {
			this.index = index;
			this.source = source;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}

        public static Source getEnum(Integer index) {
            Source e = null;
            for (Source e1 : Source.values()) {
                if (e1.index == index) {
                    e = e1;
                    break;
                }
            }
            return e;
        }
    }

    /**
     * 邀请方式：短信、微信、二维码
     */
    public enum InviteWayEnum{
        sms, wechat, qrcode;
    }

    public enum Terminal{
    	xg(1, "玄关健康"),
    	bdjl(2, "博德嘉联");
    	
    	private int index;
    	private String name;
    	
		Terminal(int index, String name) {
			this.index = index;
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
    	
    }
    
    public enum ServiceStatus {
    	open(1, "开通"),
    	close(0, "未开通");
    	private int index;
    	private String name;
		private ServiceStatus(int index, String name) {
			this.index = index;
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
    }
    
    
    public enum UserLevel {
    	Expire(0, "过期用户"), 
    	Tourist(1, "游客"),
    	TemporaryUser(2, "临时用户"),
    	AuthenticatedUser(3, "已认证用户");
    	private int index;
    	private String name;
		private UserLevel(int index, String name) {
			this.index = index;
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

        public static String getName(Integer index) {
        	if(Objects.isNull(index)){
        		return "";
        	}
            for (UserLevel e1 : UserLevel.values()) {
                if (e1.index == index) {
                    return e1.getName();
               }
            }
            return "";
        }
    }
}
