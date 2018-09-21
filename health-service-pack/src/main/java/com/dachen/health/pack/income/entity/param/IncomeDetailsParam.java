package com.dachen.health.pack.income.entity.param;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.income.entity.po.IncomeDetails;

public class IncomeDetailsParam extends IncomeDetails {
	
	
	
		private Integer type;//类型（1：已完成；2：未完成；3：总收入）
		
		private String time;
		
		private String name;
		
		private String telephone;
		
		private Integer orderType;
		
		private Long finishTime;//订单结束时间
		
		private Integer userType;//用户类型（1：集团，2：医生）

		/**
		 *  总页数
		 */
		protected int pageCount;
		
		/**
		 * 封装返回的业务数据
		 */
		protected List<?> pageData;
		
		/**
		 * 返回的页码
		 */
		protected int pageIndex = 0;
		
		/**
		 * 每页数据大小
		 */
		protected int pageSize = 15;
		
		protected int start;
		
		/**
		 * 总记录数
		 */
		protected Long total=0l;
		
		/**
		 * 排序对象key，字段，value 方式
		 */
		private Map sorter;
		
		

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public Integer getOrderType() {
			return orderType;
		}

		public void setOrderType(Integer orderType) {
			this.orderType = orderType;
		}

		public Integer getUserType() {
			return userType;
		}

		public void setUserType(Integer userType) {
			this.userType = userType;
		}

		public int getPageCount() {
			return pageCount;
		}

		public void setPageCount(int pageCount) {
			this.pageCount = pageCount;
		}

		public List<?> getPageData() {
			return pageData;
		}

		public void setPageData(List<?> pageData) {
			this.pageData = pageData;
		}

		public int getPageIndex() {
			return pageIndex;
		}

		public void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public Map getSorter() {
			return sorter;
		}

		public void setSorter(Map sorter) {
			this.sorter = sorter;
		}

		public Long getFinishTime() {
			return finishTime;
		}

		public void setFinishTime(Long finishTime) {
			this.finishTime = finishTime;
		}
		
}
