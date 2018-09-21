package com.dachen.health.pack.pack.entity.vo;

public class PackVO implements java.io.Serializable{

    private static final long serialVersionUID = -4250131181512732182L;
    
    private Integer id;

    /* 医生id */
    private Integer doctorId;

    /* 套餐服务名称 */
    private String name;

    /* 价格，单位分 */
    private Long price;
    
    private Integer timeLimit; 

    /* 套餐类型 {@link PackEnum.PackType} */
    private Integer packType;

    /* 套餐状态 {@link PackEnum.PackStatus} */
    private Integer status;
    
    /* 描述 */
    private String description;
    
    private String image;
    
    private String onLineState;
    
    private Boolean isFree;
    
    /*咨询回复次数*/
    private Integer replyCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Integer getPackType() {
        return packType;
    }

    public void setPackType(Integer packType) {
        this.packType = packType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public Boolean getIsFree() {
		return isFree;
	}

	public void setIsFree(Boolean isFree) {
		this.isFree = isFree;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}
    
}