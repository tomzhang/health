package com.dachen.health.pack.pack.mapper;

import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.entity.po.PackDoctorExample;
import com.dachen.health.pack.pack.entity.po.PackDoctorGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackDoctorMapper {
    int countByExample(PackDoctorExample example);

    int deleteByExample(PackDoctorExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PackDoctor record);

    int insertSelective(PackDoctor record);

    List<PackDoctor> selectByExample(PackDoctorExample example);

    PackDoctor selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PackDoctor record, @Param("example") PackDoctorExample example);

    int updateByExample(@Param("record") PackDoctor record, @Param("example") PackDoctorExample example);

    int updateByPrimaryKeySelective(PackDoctor record);

    int updateByPrimaryKey(PackDoctor record);
    
    List<PackDoctorGroup>  groupByExample(PackDoctorExample example);

    List<Integer> getDoctorByGoodsGroupId(List<String> goodsGoodIds);
    
}