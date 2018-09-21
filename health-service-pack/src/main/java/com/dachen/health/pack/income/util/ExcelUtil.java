package com.dachen.health.pack.income.util;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.dachen.commons.exception.ServiceException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * 导出Excel文档工具类
 * */
public class ExcelUtil {

    /**
     * 创建excel文档，
     * @param list 数据
     * @param keys list中map的key数组集合
     * @param columnNames excel的列名
     * */
    public static Workbook createWorkBook(List<Map<String, Object>> list,String []keys,String columnNames[]) {
        // 创建excel工作簿
        SXSSFWorkbook  wb = new SXSSFWorkbook();
        // 创建第一个sheet（页），并命名
        Sheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
        // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
        for(int i=0;i<keys.length;i++){
            sheet.setColumnWidth((short) i, (short) (35.7 * 150));
        }

        // 创建第一行
        Row row = sheet.createRow((short) 0);

        // 创建两种单元格格式
        CellStyle cs = wb.createCellStyle();
        CellStyle cs2 = wb.createCellStyle();

        // 创建两种字体
        Font f = wb.createFont();
        Font f2 = wb.createFont();

        // 创建第一种字体样式（用于列名）
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // 创建第二种字体样式（用于值）
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());

//        Font f3=wb.createFont();
//        f3.setFontHeightInPoints((short) 10);
//        f3.setColor(IndexedColors.RED.getIndex());

        // 设置第一种单元格的样式（用于列名）
        cs.setFont(f);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setAlignment(CellStyle.ALIGN_CENTER);

        // 设置第二种单元格的样式（用于值）
        cs2.setFont(f2);
        cs2.setBorderLeft(CellStyle.BORDER_THIN);
        cs2.setBorderRight(CellStyle.BORDER_THIN);
        cs2.setBorderTop(CellStyle.BORDER_THIN);
        cs2.setBorderBottom(CellStyle.BORDER_THIN);
        cs2.setAlignment(CellStyle.ALIGN_CENTER);
        //设置列名
        for(int i=0;i<columnNames.length;i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(cs);
        }
        //设置每行每列的值
        for (int i = 1; i < list.size(); i++) {
            // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
            // 创建一行，在页sheet上
            Row row1 = sheet.createRow(i);
            // 在row行上创建一个方格
            for(short j=0;j<keys.length;j++){
                Cell cell = row1.createCell(j);
                cell.setCellValue(list.get(i).get(keys[j]) == null?" ": list.get(i).get(keys[j]).toString());
                cell.setCellStyle(cs2);
            }
        }
        return wb;
    }

    /**
     * 创建excel文档，
     * @param list 数据
     * @param keys list中map的key数组集合
     * @param columnNames excel的列名
     * */
    public static Workbook createWorkBooks(List<String> sheetNames, List<List<Map<String, Object>>> list, List<String[]> keys, List<String[]> columnNames) {
        // 创建excel工作簿
        Workbook wb = new HSSFWorkbook();
        // 创建第一个sheet（页），并命名
        if (sheetNames != null) {
        	int size = sheetNames.size();
        	if (list == null || keys == null || columnNames == null
        			|| list.size() != size || keys.size() != size || columnNames.size() != size) {
				throw new ServiceException("创建excel时发生错误");
			}
        	
        	for (int i = 0; i < size; i++) {
				// 创建一个sheet（页），并命名
                Sheet sheet = wb.createSheet(sheetNames.get(i));
                List<Map<String, Object>> value = list.get(i);
                String[] key = keys.get(i);
                String[] columnName = columnNames.get(i);
                
                // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
                for(int j=0;j<key.length;j++){
                    sheet.setColumnWidth((short) j, (short) (35.7 * 150));
                }
                
                // 创建第一行
                Row row = sheet.createRow((short) 0);

                // 创建两种单元格格式
                CellStyle cs = wb.createCellStyle();
                CellStyle cs2 = wb.createCellStyle();

                // 创建两种字体
                Font f = wb.createFont();
                Font f2 = wb.createFont();

                // 创建第一种字体样式（用于列名）
                f.setFontHeightInPoints((short) 10);
                f.setColor(IndexedColors.BLACK.getIndex());
                f.setBoldweight(Font.BOLDWEIGHT_BOLD);

                // 创建第二种字体样式（用于值）
                f2.setFontHeightInPoints((short) 10);
                f2.setColor(IndexedColors.BLACK.getIndex());
                
                // 设置第一种单元格的样式（用于列名）
                cs.setFont(f);
                cs.setBorderLeft(CellStyle.BORDER_THIN);
                cs.setBorderRight(CellStyle.BORDER_THIN);
                cs.setBorderTop(CellStyle.BORDER_THIN);
                cs.setBorderBottom(CellStyle.BORDER_THIN);
                cs.setAlignment(CellStyle.ALIGN_CENTER);

                // 设置第二种单元格的样式（用于值）
                cs2.setFont(f2);
                cs2.setBorderLeft(CellStyle.BORDER_THIN);
                cs2.setBorderRight(CellStyle.BORDER_THIN);
                cs2.setBorderTop(CellStyle.BORDER_THIN);
                cs2.setBorderBottom(CellStyle.BORDER_THIN);
                cs2.setAlignment(CellStyle.ALIGN_CENTER);
                
                //设置列名
                for(int j=0;j<columnName.length;j++){
                    Cell cell = row.createCell(j);
                    cell.setCellValue(columnName[j]);
                    cell.setCellStyle(cs);
                }
                //设置每行每列的值
                for (short j = 0; j < value.size(); j++) {
                    // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
                    // 创建一行，在页sheet上
                    Row row1 = sheet.createRow((short) (j + 1));
                    // 在row行上创建一个方格
                    for(short k=0; k<key.length; k++){
                        Cell cell = row1.createCell(k);
                        cell.setCellValue(value.get(j).get(key[k]) == null?" ": value.get(j).get(key[k]).toString());
                        cell.setCellStyle(cs2);
                    }
                }
			}
		}
        
        return wb;
    }

    /**
     * 创建excel文档，
     * @param list 数据
     * @param keys list中map的key数组集合
     * @param columnNames excel的列名
     * */
    public static SXSSFWorkbook createSXSSFWorkBooks(List<String> sheetNames, List<List<Map<String, Object>>> list, List<String[]> keys, List<String[]> columnNames) {
        // 创建excel工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 创建第一个sheet（页），并命名
        if (sheetNames != null) {
            int size = sheetNames.size();
            if (list == null || keys == null || columnNames == null
                || list.size() != size || keys.size() != size || columnNames.size() != size) {
                return null;
            }

            for (int i = 0; i < size; i++) {
                // 创建一个sheet（页），并命名
                Sheet sheet = wb.createSheet(sheetNames.get(i));
                List<Map<String, Object>> value = list.get(i);
                String[] key = keys.get(i);
                String[] columnName = columnNames.get(i);

                // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
                for(int j=0;j<key.length;j++){
                    sheet.setColumnWidth((short) j, (short) (35.7 * 150));
                }

                // 创建第一行
                Row row = sheet.createRow((short) 0);

                // 创建两种单元格格式
                CellStyle cs = wb.createCellStyle();
                CellStyle cs2 = wb.createCellStyle();

                // 创建两种字体
                Font f = wb.createFont();
                Font f2 = wb.createFont();

                // 创建第一种字体样式（用于列名）
                f.setFontHeightInPoints((short) 10);
                f.setColor(IndexedColors.BLACK.getIndex());
                f.setBoldweight(Font.BOLDWEIGHT_BOLD);

                // 创建第二种字体样式（用于值）
                f2.setFontHeightInPoints((short) 10);
                f2.setColor(IndexedColors.BLACK.getIndex());

                // 设置第一种单元格的样式（用于列名）
                cs.setFont(f);
                cs.setBorderLeft(CellStyle.BORDER_THIN);
                cs.setBorderRight(CellStyle.BORDER_THIN);
                cs.setBorderTop(CellStyle.BORDER_THIN);
                cs.setBorderBottom(CellStyle.BORDER_THIN);
                cs.setAlignment(CellStyle.ALIGN_CENTER);

                // 设置第二种单元格的样式（用于值）
                cs2.setFont(f2);
                cs2.setBorderLeft(CellStyle.BORDER_THIN);
                cs2.setBorderRight(CellStyle.BORDER_THIN);
                cs2.setBorderTop(CellStyle.BORDER_THIN);
                cs2.setBorderBottom(CellStyle.BORDER_THIN);
                cs2.setAlignment(CellStyle.ALIGN_CENTER);

                //设置列名
                for(int j=0;j<columnName.length;j++){
                    Cell cell = row.createCell(j);
                    cell.setCellValue(columnName[j]);
                    cell.setCellStyle(cs);
                }
                //设置每行每列的值
                for (Integer j = 0; j < value.size(); j++) {
                    // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
                    // 创建一行，在页sheet上
                    Row row1 = sheet.createRow(j + 1);
                    // 在row行上创建一个方格
                    for(short k=0; k<key.length; k++){
                        Cell cell = row1.createCell(k);
                        cell.setCellValue(value.get(j).get(key[k]) == null?" ": value.get(j).get(key[k]).toString());
                        cell.setCellStyle(cs2);
                    }
                }
            }
        }

        return wb;
    }
    
}
