package com.dachen.health.group.group.entity.po;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "c_group_service_item", noClassnameStored = false)
public class GroupServiceItem {

	@Id
	private ObjectId id;
	
	private String groupId;
	
	private String serviceItemId;

	private Integer price;//集团制定的价格
	
	@Embedded
	private List<HospitalFee> hospitalFee;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getServiceItemId() {
		return serviceItemId;
	}

	public void setServiceItemId(String serviceItemId) {
		this.serviceItemId = serviceItemId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	

	public List<HospitalFee> getHospitalFee() {
		return hospitalFee;
	}

	public void setHospitalFee(List<HospitalFee> hospitalFee) {
		this.hospitalFee = hospitalFee;
	}


	public static class HospitalFee {
		
		private String hospitalId;
		
		private Integer price;

		public String getHospitalId() {
			return hospitalId;
		}

		public void setHospitalId(String hospitalId) {
			this.hospitalId = hospitalId;
		}

		public Integer getPrice() {
			return price;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}
	}
}
