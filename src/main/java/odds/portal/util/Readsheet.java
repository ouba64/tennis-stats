package odds.portal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Readsheet {
	static XSSFRow row;
	void loadExcel() {
		try {


			FileInputStream fis = new FileInputStream(new File("./matchs.xlsm"));
			// InputStream is =
			// getClass().getClassLoader().getResourceAsStream(cityGraphFilename);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet spreadsheet = workbook.getSheetAt(1);
			

			spreadsheet.getRow(2).getCell(2).setCellValue("Bonjour");
			
            FileOutputStream outFile =new FileOutputStream(new File("./matchs.xlsm"));
            workbook.write(outFile);
            outFile.close();
			
			Iterator<Row> rowIterator = spreadsheet.iterator();

			while (rowIterator.hasNext()) {
				row = (XSSFRow) rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					System.out.print(cell.getAddress()+"  ");
				}
				System.out.println();
			}
			fis.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Readsheet r = new Readsheet();
		r.loadExcel();
	}
}