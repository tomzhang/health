package com.dachen.health.pack.order.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： CheckInParam<br>
 * Description： 报到参数接收类<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public class CheckInParam extends PageVO{
	
	
    public enum CheckInFrom{
		App(1),
		WX(2);
    	
    	private int index;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		private CheckInFrom(int index) {
			this.index = index;
		}
		
		public static boolean isCheckInFromEffective(int type) {
			for(CheckInFrom c : CheckInFrom.values()){
				if(c.getIndex() == type){
					return true;
				}
			}
			return false;
		}
    	
	}
	
	/**
	 *  报道来源
	 *  1:App
	 *  2:微信
	 * */
	private Integer checkInFrom;

    /* 报到id */
    private Integer checkInId;
    
    /* 订单id */
    private Integer orderId;

    /* 医生id */
    private Integer doctorId;
    
    /* 用户id */
    private Integer userId;

    /* 患者id */
    private Integer patientId;

    /* 手机 */
    private String phone;

    /* 图像地址 */
    private List<String> imageUrls;

    /* 就诊医院 */
    private String hospital;

    /* 病例号 */
    private String recordNum;

    /* 上一次就诊时间 */
    private Long lastCureTime;

    /* 诊断描述 */
    private String description;

    /* 留言 */
    private String message;

    /* 状态 {@link CheckInEnum.CheckInStatus} */
    private Integer status;
    
    private List<Integer> userIds;
    
    private Integer freePack;
    
    private List<String> diseaseIds;
    
    private Long lastUpdateTime;

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
	
	public Integer getCheckInFrom() {
		return checkInFrom;
	}

	public void setCheckInFrom(Integer checkInFrom) {
		this.checkInFrom = checkInFrom;
	}

	public Integer getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(Integer checkInId) {
        this.checkInId = checkInId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public Long getLastCureTime() {
        return lastCureTime;
    }

    public void setLastCureTime(Long lastCureTime) {
        this.lastCureTime = lastCureTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Integer getFreePack() {
		return freePack;
	}

	public void setFreePack(Integer freePack) {
		this.freePack = freePack;
	}


	public List<String> getDiseaseIds() {
		return diseaseIds;
	}

	public void setDiseaseIds(List<String> diseaseIds) {
		this.diseaseIds = diseaseIds;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
}
