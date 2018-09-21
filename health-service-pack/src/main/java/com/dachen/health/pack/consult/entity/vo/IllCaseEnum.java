package com.dachen.health.pack.consult.entity.vo;

public class IllCaseEnum {

	public enum SeekIllType {
		 manualInput (1),
         cureRecord(2),
		 checkItem(3),
		 transferRecord(4);
		
		 private int index;

		 private SeekIllType(int index) {
	            this.index = index;
	     }
		 
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
		
		
	}
}
