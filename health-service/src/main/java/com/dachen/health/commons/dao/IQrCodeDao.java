package com.dachen.health.commons.dao;

import com.dachen.health.base.entity.po.QrScanParamPo;
import com.dachen.health.commons.entity.QrCode;

public interface IQrCodeDao {

	QrCode save(QrCode qrcode);
	
	QrCode get(String content);
	
	boolean delete();
	
	boolean delete(QrCode qrcode);
	
	boolean update(String id, String imageKey);

	String insert(QrScanParamPo param);

	QrScanParamPo getQrScanParam(String id);
}
