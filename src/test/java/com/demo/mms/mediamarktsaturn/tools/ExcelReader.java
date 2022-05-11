package com.demo.mms.mediamarktsaturn.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelReader {

    XSSFWorkbook workbook;
    Iterator<Row> rowIterator;

    public ExcelReader(String fileUrl) throws IOException {
        File file = new File(fileUrl);
        FileInputStream inputStream = new FileInputStream(file);
        workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        rowIterator = firstSheet.iterator();
        // this reads the header
        rowIterator.next();
    }

    public List<String> nextRow() throws IOException {
        if (!rowIterator.hasNext()){
            workbook.close();
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();

        Row nextRow = rowIterator.next();
        Iterator<Cell> cellIterator = nextRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell nextCell = cellIterator.next();
            if (nextCell.getCellType() == CellType.STRING) {
                result.add(nextCell.getStringCellValue());
            }else if (nextCell.getCellType() == CellType.NUMERIC) {
                result.add(String.valueOf(nextCell.getNumericCellValue()));
            } else {
                log.error("Unknown dataType in row {}", nextRow);
            }
        }
        return result;
    }

}
