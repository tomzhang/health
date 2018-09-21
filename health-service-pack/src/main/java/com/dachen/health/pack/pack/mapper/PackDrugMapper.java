package com.dachen.health.pack.pack.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.pack.entity.po.PackDrug;
import com.dachen.health.pack.pack.entity.po.PackDrugExample;

public interface PackDrugMapper {
    int countByExample(PackDrugExample example);

    int deleteByExample(PackDrugExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PackDrug record);

    int insertSelective(PackDrug record);

    List<PackDrug> selectByExample(PackDrugExample example);

    PackDrug selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PackDrug record, @Param("example") PackDrugExample example);

    int updateByExample(@Param("record") PackDrug record, @Param("example") PackDrugExample example);

    int updateByPrimaryKeySelective(PackDrug record);

    int updateByPrimaryKey(PackDrug record);
}