package com.dachen.health.pack.order.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.patient.model.ImageData;


public interface IImageDataService {
	
	public void add(ImageData imageData);
	
	/**
	 * 根据病情ID查找
	 * @param type
	 * @param id
	 * @return
	 */
	public List<String> findImgData(Integer type,Integer id);
	
	public void deleteImgData(Integer type,Integer id);
	
	
	public List<ImageData> findImgDataByRelationId(ImageData imageData);
	
	public void deleteByExampleByRelationId(ImageData imageData);
	
	public List<Integer> addDoctorImages(Integer userId, String... images);
	
	public List<Integer> addDoctorImagesCover(Integer userId, String... images);
	
	/**
	 * 添加医生身份证图片
	 * @param userId
	 * @param idcardImage
	 * @return 返回图片索引ID
	 */
	public Integer addDoctorIdcardImage(Integer userId, String idcardImage);
	
	public List<Map<String,Object>> findDoctorImgData(Integer type, Integer id);
	
	public void deleteImgDataById( Integer id);
	
	public void updateImgDataById(ImageData imageData);
	
	public List<Map<String, Object>> getOldDoctorCertImagesList(Integer doctorId) ;
	
}
