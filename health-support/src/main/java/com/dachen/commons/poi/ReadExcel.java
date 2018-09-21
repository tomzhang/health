package com.dachen.commons.poi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class ReadExcel {
	
	
	public static void main(String...strings) throws InvalidFormatException, IOException {
		
		String path="D:\\bankBinData.xls";
		reandExcelByRows(path);
		
		
		
	}
	
	public static void  reandExcelByRows(String fileName) throws InvalidFormatException, IOException {
		
		Workbook workbook = WorkbookFactory.create(new File(fileName));
		// 根据sheet的名字获取
		Sheet sheet = workbook.getSheet("Sheet1");
		// 处了上面testReadExcel的方式读取以外,还支持foreach的方式读取
		for (Row row : sheet) {
			if(row.getRowNum()==0) {
				continue;
			}
			System.out.print("[" + PoiUtil.getCellValue(row.getCell(0)) + "]");
			for (Cell cell : row) {
				//System.out.print("["+PoiUtil.getCellValue(cell)+""+"]");
				//System.out.print("[" + PoiUtil.getCellValue(cell) + "]");
			}
			System.out.println();
		}
	}
	
	public static void readExcel(String fileName) throws InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(new File(fileName));
		// 获取第一个工作目录,下标从0开始
		Sheet sheet = workbook.getSheetAt(0);
		// 获取该工作目录最后一行的行数
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 0; i <= lastRowNum; i++) {
			// 获取下标为i的行
			Row row = sheet.getRow(i);
			if (row != null) {
				// 获取该行单元格个数
				int lastCellNum = row.getLastCellNum();
				for (int j = 0; j < lastCellNum; j++) {
					// 获取下标为j的单元格
					Cell cell = row.getCell(j);
					// 调用获取方法
					String cellValue = PoiUtil.getCellValue(cell);
					System.out.print("[" + cellValue + "]");
				}
			}
			System.out.println();
		}
	}
	

}
