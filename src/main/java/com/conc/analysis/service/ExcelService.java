package com.conc.analysis.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class ExcelService {

    public double[] processExcel(MultipartFile file) throws IOException {
        List<Double> flowRate = new ArrayList<>();
        List<Double> sg = new ArrayList<>();
        List<Double> wt = new ArrayList<>();
        List<Double> ash = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 2; i < rowCount; i++) { 
                Row row = sheet.getRow(i);
                if(row.getCell(0).getCellType() == CellType.BLANK) break;
                flowRate.add(row.getCell(0).getNumericCellValue());
            }
        }
        // Error handling
        int n = wt.size();
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = flowRate.get(i);
        }

        return data;
    }

    public String createFile(double[][] data) {
        // Create Excel with new columns
        String fileName = "Washibility Characteristics.xlsx";
        Path outputPath = Paths.get("output/" + fileName);
        try{
            Files.createDirectories(outputPath.getParent());
        }
        catch (IOException e) {
            e.printStackTrace();        
        }
        Workbook newWorkbook = new XSSFWorkbook();
        Sheet newSheet = newWorkbook.createSheet("Processed");

        int n = data.length;

        // Create header
        Row headerRow1 = newSheet.createRow(0);
        headerRow1.createCell(0).setCellValue("SG");
        headerRow1.createCell(1).setCellValue("Direct");
        headerRow1.createCell(2).setCellValue("Direct");
        headerRow1.createCell(3).setCellValue("Ash Product");
        headerRow1.createCell(4).setCellValue("Cumulative Float");
        headerRow1.createCell(5).setCellValue("Cumulative Float");
        headerRow1.createCell(6).setCellValue("Cumulative Float");
        headerRow1.createCell(7).setCellValue("Cumulative Sink");
        headerRow1.createCell(8).setCellValue("Cumulative Sink");
        headerRow1.createCell(9).setCellValue("Cumulative Sink");

        Row headerRow2 = newSheet.createRow(1);
        headerRow2.createCell(0).setCellValue("SG");
        headerRow2.createCell(1).setCellValue("Wt %");
        headerRow2.createCell(2).setCellValue("Ash %");
        headerRow2.createCell(3).setCellValue("Ash Product");
        headerRow2.createCell(4).setCellValue("Wt %");
        headerRow2.createCell(5).setCellValue("Ash Product");
        headerRow2.createCell(6).setCellValue("Ash %");
        headerRow2.createCell(7).setCellValue("Wt %");
        headerRow2.createCell(8).setCellValue("Ash Product");
        headerRow2.createCell(9).setCellValue("Ash %");

        newSheet.addMergedRegion(new CellRangeAddress(
                0, 1, // fromRow, toRow (vertical merge)
                0, 0  // fromCol, toCol (1st column)
        ));

        newSheet.addMergedRegion(new CellRangeAddress(
                0, 1, // fromRow, toRow (vertical merge)
                3, 3  // fromCol, toCol (1st column)
        ));

        newSheet.addMergedRegion(new CellRangeAddress(
                0, 0, // fromRow, toRow (same row)
                1, 2  // fromCol, toCol (horizontal merge)
        ));

        newSheet.addMergedRegion(new CellRangeAddress(
                0, 0, // fromRow, toRow (same row)
                4, 6  // fromCol, toCol (horizontal merge)
        ));

        newSheet.addMergedRegion(new CellRangeAddress(
                0, 0, // fromRow, toRow (same row)
                7, 9  // fromCol, toCol (horizontal merge)
        ));

        // Create a bold font style
        Font boldFont = newWorkbook.createFont();
        boldFont.setBold(true);

        // Bold Cell Style (for first 2 rows)
        CellStyle boldStyle = newWorkbook.createCellStyle();
        boldStyle.setFont(boldFont);
        boldStyle.setBorderTop(BorderStyle.THIN);
        boldStyle.setBorderBottom(BorderStyle.THIN);
        boldStyle.setBorderLeft(BorderStyle.THIN);
        boldStyle.setBorderRight(BorderStyle.THIN);
        boldStyle.setAlignment(HorizontalAlignment.CENTER);
        boldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for(int i = 0; i < 2; i++) {
            Row row = newSheet.getRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.getCell(j);
                cell.setCellStyle(boldStyle);
            }
            newSheet.autoSizeColumn(i);
        }
        // Normal Cell Style (for remaining rows)
        CellStyle normalStyle = newWorkbook.createCellStyle();
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setAlignment(HorizontalAlignment.CENTER);
        normalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for(int i = 2; i < n+2; i++) {
            Row row = newSheet.createRow(i);
            for (int j = 0; j < data[i-2].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i-2][j]);
                cell.setCellStyle(normalStyle);
            }
            newSheet.autoSizeColumn(i);
        }
        newSheet.getRow(n+1).getCell(9).setCellValue("-");

        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            newWorkbook.write(fos);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }
}
