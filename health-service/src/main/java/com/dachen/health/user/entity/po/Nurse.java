package com.dachen.health.user.entity.po;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.commons.exception.ServiceException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Nurse {

	/* 身份证号码 */
	private String idCard;

	private  @NotSaved List<String> imageList = new ArrayList<String>();

	/* 所属医院 */
	private String hospital;
	/* 所属医院Id */
	private String hospitalId;

	/* 所属科室 */
	private String departments;

	/* 职称 */
	private String title;
	
	/*职称排行*/
    private String titleRank;

	/* 职业区域，根据医生审核医院确定 */
	private Integer provinceId;

	private Integer cityId;

	private Integer countryId;

	private String province;

	private String city;

	private String country;
	
	private boolean opreatorGuide;// 电话短信指引   
	
	private String weights;
	
	//private  @NotSaved String token;//为了调用获取职业资料的接口
	
	//图片针对地址
	private List<NurseImage> images = new ArrayList<NurseImage>();

	/* 护士号 */
    private String nurseNum;
	/* 审核信息 */
	@Embedded
	private Check check;


	public boolean isOpreatorGuide() {
		return opreatorGuide;
	}

	public void setOpreatorGuide(boolean opreatorGuide) {
		this.opreatorGuide = opreatorGuide;
	}

	public String getWeights() {
		return weights;
	}


	public void setWeights(String weights) {
		this.weights = weights;
	}


	public List<NurseImage> getImages() {
		return images;
	}


	public void setImages(List<NurseImage> images) {
		this.images = images;
	}


	public String getNurseNum() {
		return nurseNum;
	}


	public void setNurseNum(String nurseNum) {
		this.nurseNum = nurseNum;
	}


//	public String getToken() {
//		return token;
//	}
//
//
//	public void setToken(String token) {
//		this.token = token;
//	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public Check getCheck() {
		return check;
	}

	public void setCheck(Check check) {
		this.check = check;
	}




	// 审核信息
	public static class Check {
		/* 所属医院 */
		private String hospital;

		/* 所属医院Id */
		private String hospitalId;

		/* 所属科室 */
		private String departments;

		/* 职称 */
		private String title;

		/* 审核人员 */
		private String checker;

		/* 审核人员Id */
		private String checkerId;

		/* 审核意见 */
		private String remark;
		
		 /* 审核时间 */
        private Long checkTime;

		public String getHospital() {
			return hospital;
		}
		
		public Long getCheckTime() {
			return checkTime;
		}

		public void setCheckTime(Long checkTime) {
			this.checkTime = checkTime;
		}

		public void setHospital(String hospital) {
			this.hospital = hospital;
		}

		public String getHospitalId() {
			return hospitalId;
		}

		public void setHospitalId(String hospitalId) {
			this.hospitalId = hospitalId;
		}

		public String getDepartments() {
			return departments;
		}

		public void setDepartments(String departments) {
			this.departments = departments;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}



		public String getChecker() {
			return checker;
		}

		public void setChecker(String checker) {
			this.checker = checker;
		}

		public String getCheckerId() {
			return checkerId;
		}

		public void setCheckerId(String checkerId) {
			this.checkerId = checkerId;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

	}


	public String getIdCard() {
		return idCard;
	}


	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}


	public List<String> getImageList() {//单独调用认证新
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}


	public Integer getProvinceId() {
		return provinceId;
	}


	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}


	public Integer getCityId() {
		return cityId;
	}


	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}


	public Integer getCountryId() {
		return countryId;
	}


	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getTitleRank() {
		return titleRank;
	}


	public void setTitleRank(String titleRank) {
		this.titleRank = titleRank;
	}


	/**
	 * 护士服务设置
	 * @author weilit
	 *
	 */
	public static class UserSettings {
		/**
		 * 服务是否设置
		 */
		private Integer  serviceSet;
		/**
		 * 时间是否设置
		 */
		private Integer timeSet;
		
		public static DBObject getDefault() {
			DBObject dbObj = new BasicDBObject();
			dbObj.put("serviceSet", 0);// 0  没有设置  1
			dbObj.put("timeSet", 0);// 0  没有设置  1 已经设置
			return dbObj;
		}
		
		public Integer getServiceSet() {
			return serviceSet;
		}

		public void setServiceSet(Integer serviceSet) {
			this.serviceSet = serviceSet;
		}

		public Integer getTimeSet() {
			return timeSet;
		}

		public void setTimeSet(Integer timeSet) {
			this.timeSet = timeSet;
		}

		/**
		 * 
		 * </p>校验指定参数值</p>
		 * @param i
		 * @return
		 * @author limiaomiao
		 * @date 2015年7月11日
		 */
		public boolean verify(Integer i){
			if(i!=null){
				if(i!=1&&i!=2){
					return false;
				}
			}
			return true;
		}
		
		/**
		 * 
		 * </p>参数校验</p>
		 * @return
		 * @author limiaomiao
		 * @date 2015年7月10日
		 */
		public int  verifys(){
			int count=0;
			Field[] fileds=getClass().getDeclaredFields();
			for (Field field : fileds) {
				try {
					Integer value=(Integer) field.get(this);
					if(!verify(value)){
						throw new ServiceException(""+field.getName()+" 's value "+value+" is not correct");
					}else{
						count++;
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			
			
			return count;
		}
	}
	}
