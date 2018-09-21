package com.dachen.health.commons.constants;

public class IllHistoryEnum {

	public enum IllHistoryRecordType {
		checkItem(1, "检查项"),
		order(2, "订单类型"),
		care(3, "健康关怀"),
		checkIn(4, "患者报道"),
		consultation(5, "会诊"),
		drug(6, "用药"),
		normal(7, "正常添加的病程");

		private int index;
		private String name;

		IllHistoryRecordType(int index, String name) {
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
	
}
