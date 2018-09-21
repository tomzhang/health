package com.dachen.health.pack.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.net.HttpHelper;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.utils.OrderNotify;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.patient.mapper.ImageDataMapper;
import com.dachen.health.pack.patient.model.ImageData;
import com.dachen.health.pack.patient.model.ImageDataExample;
import com.dachen.health.user.entity.po.Change;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ImageDataServiceImpl implements IImageDataService {
	@Autowired
    protected ImageDataMapper mapper;

    @Autowired
    protected UserLogRespository userLogRespository;

	@Override
	public void add(ImageData imageData) {
		if (imageData != null) {
			imageData.setImageUrl(PropertiesUtil.removeUrlPrefix(imageData
					.getImageUrl()));
			
			//音频格式异步转格式
			if (imageData.getImageType().equals(ImageDataEnum.careVoice.getIndex()) || imageData.getImageType().equals(ImageDataEnum.cureVoice.getIndex())) {
				String url = imageData.getImageUrl();
				String key = null;
				if (url.contains("timeLong")) {
					String[] tmp = url.split("\\?");
					String[] tmp2 = tmp[tmp.length -2].split("/");
					key = tmp2[tmp2.length -1];
				}else {
					String[] tmp = url.split("/");
					key = tmp[tmp.length -1];
				}
				
				//异步将音频格式改为MP3
				OrderNotify.armCovertToMP3(key);
				
				url = url.replace(key, key+"_mp3");
				imageData.setImageUrl(url);
			}
		}
		mapper.insert(imageData);
	}

	public List<String> findImgData(Integer type, Integer id) {
		ImageDataExample example = new ImageDataExample();
		example.createCriteria().andImageTypeEqualTo(type)
				.andRelationIdEqualTo(id);

		List<ImageData> list = mapper.selectByExample(example);
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String url = PropertiesUtil.addUrlPrefix(list.get(i).getImageUrl());
			ret.add(url);

		}
		return ret;
	}

	public void deleteImgData(Integer type, Integer id) {
		mapper.deleteImgData(type,id);
	}

	@Override
	public List<ImageData> findImgDataByRelationId(ImageData imageData) {
		ImageDataExample example = new ImageDataExample();
		example.createCriteria()
				.andRelationIdEqualTo(imageData.getRelationId());
		return mapper.selectByExample(example);
	}

	@Override
	public void deleteByExampleByRelationId(ImageData imageData) {
		ImageDataExample example = new ImageDataExample();
		example.createCriteria()
				.andRelationIdEqualTo(imageData.getRelationId());
		mapper.deleteByExample(example);
	}

	@Override
	public List<Integer> addDoctorImages(Integer userId, String... images) {
		List<Integer> imageIds = new ArrayList<Integer>();
		if (null != images && images.length > 0) {
			for (int i = 0; i < images.length; i++) {
				String imageUrl = images[i];
				if (StringUtils.isNotEmpty(imageUrl)) {
					ImageData image = new ImageData();
					image.setImageType(ImageDataEnum.doctorCheckImage
							.getIndex());
					image.setImageUrl(imageUrl);
					image.setRelationId(userId);
					image.setUserId(userId);
					image.setTimeLong(System.currentTimeMillis());
					mapper.insert(image);
					imageIds.add(image.getId());
				}
			}
		}

		return imageIds;
	}
	
	private List<Integer> addDoctorImages(Integer userId,Integer imageType, String... images) {
        List<Integer> imageIds = new ArrayList<Integer>();
        if (null != images && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                String imageUrl = images[i];
                if (StringUtils.isNotEmpty(imageUrl)) {
                    ImageData image = new ImageData();
                    image.setImageType(imageType);
                    image.setImageUrl(imageUrl);
                    image.setRelationId(userId);
                    image.setUserId(userId);
                    image.setTimeLong(System.currentTimeMillis());
                    mapper.insert(image);
                    imageIds.add(image.getId());
                }
            }
        }

        return imageIds;
    }
	
	@Override
	public List<Integer> addDoctorImagesCover(Integer userId, String... images) {

		List<Map<String, Object>> result = findDoctorImgData(5, userId);
		List<String> urlList = Lists.newArrayList();
		if(result!=null && result.size() > 0) {
			for(Map<String, Object> map : result) {
				urlList.add(map.get("url").toString());
			}
		}
		List<String> imagesList = Lists.newArrayList();
		
		if (images.length>0) {
			imagesList =  Arrays.asList(images);
		}
		
		//医生自己上传认证照片时，不作记录
		if (!Integer.valueOf(ReqUtil.instance.getUserId()).equals(Integer.valueOf(userId))) {
			
			OperationRecord operationRecord = new OperationRecord();
			operationRecord.setCreateTime(System.currentTimeMillis());
			operationRecord.setCreator(ReqUtil.instance.getUserId());
			operationRecord.setObjectId(userId+"");
			operationRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
			
			if ((imagesList!=null && imagesList.size()>0) && (urlList!=null && urlList.size()>0)) {
				if (!urlList.containsAll(imagesList)||!imagesList.containsAll(urlList)) {
					operationRecord.setChange(new Change(UserLogEnum.infoType.checkPic.getType(),"checkPic",ListToString(urlList),ListToString(Arrays.asList(images))));
					userLogRespository.addOperationRecord(operationRecord);
				}
			}else if ((imagesList ==null || imagesList.size()==0) && (urlList==null || urlList.size()==0)) {
				
			}else {
				operationRecord.setChange(new Change(UserLogEnum.infoType.checkPic.getType(),"checkPic",ListToString(urlList),ListToString(Arrays.asList(images))));
				userLogRespository.addOperationRecord(operationRecord);
			}
		}
		
		deleteImgData(ImageDataEnum.doctorCheckImage.getIndex(), userId);
		return addDoctorImages(userId, images);
	}
	
	@Transactional
	@Override
    public Integer addDoctorIdcardImage(Integer userId, String idcardImage) {
	    
	    deleteImgData(ImageDataEnum.idcardImage.getIndex(), userId);
	    List<Integer> ids = addDoctorImages(userId, ImageDataEnum.idcardImage.getIndex(), idcardImage);
	    
	    if(CollectionUtils.isEmpty(ids)){
	        return null;
	    }
	    
        return ids.get(0);
    }

	@Override
	public List<Map<String, Object>> findDoctorImgData(Integer type, Integer id) {
		ImageDataExample example = new ImageDataExample();
		example.createCriteria().andImageTypeEqualTo(type)
				.andRelationIdEqualTo(id);

		List<ImageData> list = mapper.selectByExample(example);
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String url = list.get(i).getImageUrl();
			if (StringUtils.isNotEmpty(url)) {
				map.put("id", list.get(i).getId());
                map.put("url", url);
                ret.add(map);
			}
		}
		return ret;
	}

	@Override
	public void deleteImgDataById(Integer id) {
		mapper.deleteByPrimaryKey(id);
	}

	@Override
	public void updateImgDataById(ImageData imageData) {
		mapper.updateByPrimaryKey(imageData);
	}
	@Override
	public List<Map<String, Object>> getOldDoctorCertImagesList(Integer doctorId) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		String serviceUrl = PropertiesUtil
				.getContextProperty("fileserver.upload");
		String methodUrl = serviceUrl + "upload/getCertPath";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", ReqUtil.instance.getToken());
		paramMap.put("userId", String.valueOf(doctorId));
		paramMap.put("certType","c1");
		String response = HttpHelper.get(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			JSONMessage json = JSONUtil
					.parseObject(JSONMessage.class, response);
			if (null != json.getData()) {
				JSONArray aray = (JSONArray) json.getData();
				if (aray.size() > 0) {
					for (int i = 0; i < aray.size(); i++) {
						if (null != aray.get(i)) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", "");
							map.put("url", aray.get(i).toString());
							ret.add(map);
						}
					}
				}
			}
		}
		return ret;
	}
	
	public String ListToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if(list!=null && list.size()>0) {
			for(int i = 0 ; i<list.size();i++) {
				sb.append(list.get(i));
				if((i+1) != list.size()) {
					sb.append(",");
				}
			}
		}else {
			return "";
		}
		return sb.toString();
	}
	
}
