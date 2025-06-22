package com.ITK.kalenyuk.DDT;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelOperations {

    private static HSSFWorkbook workbook;
    private static HSSFSheet sheet;
    private static HSSFRow row;
    private static HSSFCell cell;

    public void setExcelFile(String excelFilePath,String sheetName)
            throws IOException {
        File file =    new File(excelFilePath);


        FileInputStream inputStream = new FileInputStream(file);


        workbook=new HSSFWorkbook(inputStream);

        sheet=workbook.getSheet(sheetName);
    }

    public String getCellData(int rowNumber,int columnNumber){
        cell =sheet.getRow(rowNumber).getCell(columnNumber);

        return cell.getStringCellValue();
    }

    public int getRowCountInSheet(){

        int rowcount = sheet.getLastRowNum()-sheet.getFirstRowNum();
        return rowcount;
    }

    public void setCellValue(int rowNum,int columnNum,String
            cellValue,String excelFilePath) throws IOException {

        sheet.getRow(rowNum).createCell(columnNum).setCellValue(cellValue);

        FileOutputStream outputStream = new FileOutputStream(excelFilePath);
        workbook.write(outputStream);
    }

}
