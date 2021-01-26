package odds.portal.util;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Formula {
   public static void main(String[] args)throws Exception {
      XSSFWorkbook workbook = new XSSFWorkbook(); 
      XSSFSheet spreadsheet = workbook.createSheet("formula");
      XSSFRow row = spreadsheet.createRow(1);
      XSSFCell cell = row.createCell(1);
      
      cell.setCellValue("A = ");
      cell = row.createCell(2);
      cell.setCellValue(2);
      row = spreadsheet.createRow(2);
      cell = row.createCell(1);
      cell.setCellValue("B = ");
      cell = row.createCell(2);
      cell.setCellValue(4);
      row = spreadsheet.createRow(3);
      cell = row.createCell(1);
      cell.setCellValue("Total = ");
      cell = row.createCell(2);
      
      // Create SUM formula
      cell.setCellType(CellType.FORMULA);
      cell.setCellFormula("SUM(C2:C3)");
      cell = row.createCell(3);
      cell.setCellValue("SUM(C2:C3)");
      row = spreadsheet.createRow(4);
      cell = row.createCell(1);
      cell.setCellValue("POWER =");
      cell=row.createCell(2);
      
      // Create POWER formula
      cell.setCellType(CellType.FORMULA);
      cell.setCellFormula("POWER(C2,C3)");
      cell = row.createCell(3);
      cell.setCellValue("POWER(C2,C3)");
      row = spreadsheet.createRow(5);
      cell = row.createCell(1);
      cell.setCellValue("MAX = ");
      cell = row.createCell(2);
      
      // Create MAX formula
      cell.setCellType(CellType.FORMULA);
      cell.setCellFormula("MAX(C2,C3)");
      cell = row.createCell(3);
      cell.setCellValue("MAX(C2,C3)");
      row = spreadsheet.createRow(6);
      cell = row.createCell(1);
      cell.setCellValue("FACT = ");
      cell = row.createCell(2);
      
      // Create FACT formula
      cell.setCellType(CellType.FORMULA);
      cell.setCellFormula("FACT(C3)");
      cell = row.createCell(3);
      cell.setCellValue("FACT(C3)");
      row = spreadsheet.createRow(7);
      cell = row.createCell(1);
      cell.setCellValue("SQRT = ");
      cell = row.createCell(2);
      
      // Create SQRT formula
      cell.setCellType(CellType.FORMULA);
      cell.setCellFormula("SQRT(C5)");
      cell = row.createCell(3);
      cell.setCellValue("SQRT(C5)");
      workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
      FileOutputStream out = new FileOutputStream(new File("formula.xlsx"));
      workbook.write(out);
      out.close();
      System.out.println("fromula.xlsx written successfully");
   }
}