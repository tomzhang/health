package com.dachen.health.commons.constants;

public enum ImageDataEnum {
    caseImage(1, "病例图像"),
    dessImage(2, "病情图像"),
    cureImage(3, "诊断记录"),
    cureVoice(4, "诊断录音"),
    headImage(5, "头像"),
    doctorCheckImage(5, "医生认证图片"),
    /**身份证图片*/
    idcardImage(6, "医生身份证图片"),
    
	careImage(11,"关怀小结图片"),
	careVoice(12,"关怀小结录音");
	
	
    
    private int index;

    private String title;

    private ImageDataEnum(int index, String title) {
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
