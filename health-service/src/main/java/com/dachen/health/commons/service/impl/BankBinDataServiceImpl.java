package com.dachen.health.commons.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.poi.CheckBankCard;
import com.dachen.commons.poi.PoiUtil;
import com.dachen.health.commons.dao.BankBinDataRepository;
import com.dachen.health.commons.entity.BankBinNoData;
import com.dachen.health.commons.service.BankBinDataService;
import com.dachen.util.StringUtil;

/**
 * 全国银行名称数据字典
 * @author Administrator
 *
 */
@Service
public class BankBinDataServiceImpl implements BankBinDataService{

	@Resource
	BankBinDataRepository bankBinDataRepository;
	
	public BankBinNoData findBankName(String bankCard) {
		
		if(StringUtil.isBlank(bankCard)) {
			throw new ServiceException("请输入有效的银联卡号");
		}
		
		bankCard = bankCard.replaceAll(" ", "");
		
		//位数校验
		if (bankCard.length() == 16 || bankCard.length() == 19 || bankCard.length() == 15 || bankCard.length() == 18 || bankCard.length() == 17) {

		} else {
//			throw new ServiceException("卡号位数无效");
			throw new ServiceException("请输入正确的银行卡卡号");
		}
		
		if (CheckBankCard.checkBankCard(bankCard) == true) {

		} else {
//			throw new ServiceException("卡号校验失败");
			throw new ServiceException("请输入正确的银行卡卡号");
		}
		
		String careNo =bankCard.substring(0, 6);
		
		BankBinNoData bankBinNoData = bankBinDataRepository.createQuery().field("bankBinNo").equal(careNo).get();
		
		if(bankBinNoData == null) {
			careNo =bankCard.substring(0, 8);
			bankBinNoData = bankBinDataRepository.createQuery().field("bankBinNo").equal(careNo).get();
		}
		
		if(bankBinNoData == null) {
			careNo =bankCard.substring(0, 9);
			bankBinNoData = bankBinDataRepository.createQuery().field("bankBinNo").equal(careNo).get();
		}
		
		if(bankBinNoData == null) {
			return new BankBinNoData();
		}
		
		return bankBinNoData;
	}
	
	
	public void save() throws InvalidFormatException, IOException {
		List<BankBinNoData> list = reandExcelByRows("D:\\bankcard.xls");
		for(BankBinNoData bankBinNoData:list) {
			bankBinDataRepository.save(bankBinNoData);
		}
	}
	
	public static List<BankBinNoData>  reandExcelByRows(String fileName) throws InvalidFormatException, IOException {
		
		List<BankBinNoData> list = new ArrayList<BankBinNoData>();
		
		Workbook workbook = WorkbookFactory.create(new File(fileName));
		// 根据sheet的名字获取
		Sheet sheet = workbook.getSheet("Sheet1");
		// 处了上面testReadExcel的方式读取以外,还支持foreach的方式读取
		for (Row row : sheet) {
			
			BankBinNoData bankBinBoData = new BankBinNoData();
			System.out.print(PoiUtil.getCellValue(row.getCell(0)));
			System.out.print(PoiUtil.getCellValue(row.getCell(1)));
			System.out.print(PoiUtil.getCellValue(row.getCell(2)));
			System.out.print(PoiUtil.getCellValue(row.getCell(3)));
			System.out.print(PoiUtil.getCellValue(row.getCell(4)));
			System.out.println();
			
			
			bankBinBoData.setBankName(PoiUtil.getCellValue(row.getCell(0)).trim());
			bankBinBoData.setBankCode(PoiUtil.getCellValue(row.getCell(1)).trim());
			String bankName=PoiUtil.getCellValue(row.getCell(0)).trim();
			if(bankName.indexOf("邮政")>=0 || bankName.indexOf("邮储")>=0) {
				bankBinBoData.setBankIoc("/default/bank/yz.png");
			}else if(bankName.indexOf("工商")>=0  || bankName.indexOf("工行")>=0 || bankName.indexOf("工行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/gs.png");
			}else if(bankName.indexOf("农业")>=0) {
				bankBinBoData.setBankIoc("/default/bank/ny.png");
			}else if(bankName.indexOf("中国银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/zg.png");
			}else if(bankName.indexOf("建设银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/js.png");
			}else if(bankName.indexOf("交通银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/jt.png");
			}else if(bankName.indexOf("中信银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/zx.png");
				
			}else if(bankName.indexOf("光大银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/gd.png");
				
			}else if(bankName.indexOf("华夏银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/hx.png");
				
			}else if(bankName.indexOf("民生银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/ms.png");
			}else if(bankName.indexOf("平安")>=0) {
				bankBinBoData.setBankIoc("/default/bank/pa.png");
			}else if(bankName.indexOf("广发")>=0) {
				bankBinBoData.setBankIoc("/default/bank/gf.png");
			}else if(bankName.indexOf("招商银行")>=0) {
				bankBinBoData.setBankIoc("/default/bank/zs.png");
			}else if(bankName.indexOf("兴业银行")>=0) {
				bankBinBoData.setBankIoc("yz.png");
			}else if(bankName.indexOf("浦东发展")>=0) {
				bankBinBoData.setBankIoc("/default/bank/pf.png");
			}/*else if(rags[0].indexOf("恒丰银行")>0) {
				bankBinBoData.setBankIoc("yz.png");
				
			}*//*else if(rags[0].indexOf("上海银行")>0) {
				bankBinBoData.setBankIoc("defulat_bank.png");
				
			}*/else {
				bankBinBoData.setBankIoc("/default/bank/defulat_bank.png");
			}
			
			
			bankBinBoData.setBankCateName(PoiUtil.getCellValue(row.getCell(2)));
			bankBinBoData.setBankNoLength(Integer.valueOf(PoiUtil.getCellValue(row.getCell(3))));
			bankBinBoData.setBankBinNo(PoiUtil.getCellValue(row.getCell(4)));
			bankBinBoData.setBankNoType(PoiUtil.getCellValue(row.getCell(5)));
			list.add(bankBinBoData);
			continue;
		/*	for (Cell cell : row) {
				System.out.print("["+PoiUtil.getCellValue(row.getCell(0))+""+"]");
				//System.out.print("[" + PoiUtil.getCellValue(cell) + "]");
			}*/
			//System.out.println();
		}
		
		return list;
	}
	
}
