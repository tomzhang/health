package com.dachen.health.pack.income.mapper;

import java.util.List;

import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.entity.vo.IncomeVONew;



public interface IncomeMapperNew {
    
    Integer getDoctorIncomeBalance(IncomeVONew incomeVONew);
    Integer getDoctorDivisionBalance(IncomeVONew incomeVONew);
    Integer getDoctorUnFinishBalance(IncomeVONew incomeVONew);
    
    List<IncomeDetailsVO> getDoctorFinishDetail(IncomeVONew incomeVONew);
    List<IncomeDetailsVO> getDoctorUnFinishDetail(IncomeVONew incomeVONew);
    
    Integer getDoctorFinishDetailCount(IncomeVONew incomeVONew);
    Integer getDoctorUnFinishDetailCount(IncomeVONew incomeVONew);
    
    
}