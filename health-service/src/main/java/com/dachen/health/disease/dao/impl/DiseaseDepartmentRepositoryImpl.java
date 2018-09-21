package com.dachen.health.disease.dao.impl;

import org.springframework.stereotype.Repository;

import com.dachen.health.commons.dao.mongo.BaseRepositoryImpl;
import com.dachen.health.disease.dao.DiseaseDepartmentRepository;
import com.dachen.health.disease.entity.DiseaseDepartment;

@Repository
public class DiseaseDepartmentRepositoryImpl extends BaseRepositoryImpl<DiseaseDepartment, String> implements DiseaseDepartmentRepository{

}
