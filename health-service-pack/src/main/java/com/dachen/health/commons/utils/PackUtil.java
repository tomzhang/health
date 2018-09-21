package com.dachen.health.commons.utils;

import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.patient.model.ImageData;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.po.TagUtil;

public class PackUtil {

	/**
	 * 创建一个ImageData对象
	 * @param relaionId
	 * @param userId
	 * @param imagePath
	 * @param imageType
	 * @return
	 */
	public static ImageData newImageData(Integer relaionId, Integer userId, String imagePath, ImageDataEnum imageType) {
		ImageData imageData = new ImageData();
		
		/***begin add  by  liwei  2016年1月25日  不再上传图片的ip前缀********/
//		imageData.setImageUrl(PropertiesUtil.removeUrlPrefix(imagePath));
		imageData.setImageUrl(imagePath);
		/***end add  by  liwei  2016年1月25日********/

		imageData.setRelationId(relaionId);
		imageData.setUserId(userId);
		imageData.setImageType(imageType.getIndex());
		if (ImageDataEnum.cureVoice == imageType) {
			imageData.setTimeLong(getTimeLong(imagePath).longValue());
		}
		return imageData;
	}
	
	public static Integer getTimeLong(String imagePath) {
		String[] params = imagePath.split("[?]");
		if (params == null || params.length == 0) {
			return 0;
		}
		for (String param : params) {
			String[] pkv = param.split("=");
			if (pkv != null) {
				if ("timeLong".equals(pkv[0])) {
					if (pkv.length > 1) {
						return Integer.valueOf(pkv[1]);
					}
				}
			}
		}
		return 0;
	}
	
	public static TagParam convert(Order order) {
		TagParam param = new TagParam();
		param.setTagType(UserEnum.TagType.doctorPatient.getIndex());
		if (order.getOrderType() == OrderType.outPatient.getIndex()) {
			param.setTagName(TagUtil.OUTPATIENT);
		} else {
			param.setTagName(TagUtil.getSysTagName(order.getPackType()));
		}
		param.setUserId(order.getDoctorId());
		param.setUserIds(new Integer[]{order.getUserId()});
		param.setPatientId(order.getPatientId());
		return param;
	}
}
