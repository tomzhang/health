package com.dachen.health.commons.dao.mongo;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.health.commons.dao.BankBinDataRepository;
import com.dachen.health.commons.entity.BankBinNoData;

@Repository
public class BankBinDataRepositoryImpl extends BaseRepositoryImpl<BankBinNoData,String> implements BankBinDataRepository{

}
