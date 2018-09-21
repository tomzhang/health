package com.dachen.health.pack.patient.mapper;

import java.util.List;

import com.dachen.health.pack.patient.model.vo.ImageDataParam;
import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.ImageData;
import com.dachen.health.pack.patient.model.ImageDataExample;

public interface ImageDataMapper {
    int countByExample(ImageDataExample example);

    int deleteByExample(ImageDataExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ImageData record);

    int insertSelective(ImageData record);

    List<ImageData> selectByExample(ImageDataExample example);

    ImageData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ImageData record, @Param("example") ImageDataExample example);

    int updateByExample(@Param("record") ImageData record, @Param("example") ImageDataExample example);

    int updateByPrimaryKeySelective(ImageData record);

    int updateByPrimaryKey(ImageData record);
    
    int deleteImgData(@Param("type")Integer type,@Param("relationId") Integer relationId);

    List<ImageData> findByCureRecordIds(ImageDataParam imageDataParam);
    
}