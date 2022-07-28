package kup.get.service.other;

import javafx.scene.control.TableColumn;
import kup.get.config.MyTable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SheetService {
    private final CellStyle style;
    private final HSSFWorkbook workbook;

    public SheetService() {
        workbook = new HSSFWorkbook();
        Font font = workbook.createFont();
        style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setFontHeight((short) (12 * 20));
        font.setFontName("Times New Roman");
    }

    public void writeDataOfColumnsToSheet(String sheetName, MyTable<?> table) {
        AtomicInteger colNum = new AtomicInteger(0);
        for (TableColumn<?, ?> tableColumn : table.getColumns().stream().filter(col -> col.visibleProperty().get()).collect(Collectors.toList())) {
            getColumn(tableColumn, getSheet(workbook, sheetName), 0, colNum);
            colNum.getAndIncrement();
        }
    }

    public void createAndOpenFile(String name) {
        try {
            Path reportDirectory = Paths.get("Report");
            if(!Files.exists(reportDirectory))
                Files.createDirectory(reportDirectory);

            File file = new File(reportDirectory + File.separator + name + ".xls");
            file.deleteOnExit();
            Files.deleteIfExists(file.toPath());

            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);
            outFile.close();
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T, S> void getColumn(TableColumn<T, S> column, Sheet sheet, int rowNum, AtomicInteger colNum) {
        if (!column.getColumns().isEmpty()) {
            generateCell(getRow(sheet, rowNum), colNum.get(), String.valueOf(column.getText()));
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, colNum.get(), (int) (colNum.get() - 1 + column.getColumns().stream().filter(col -> col.visibleProperty().get()).count())));
            for (TableColumn<T, ?> tableColumn : column.getColumns().stream().filter(col -> col.visibleProperty().get()).collect(Collectors.toList())) {
                getColumn(tableColumn, sheet, rowNum + 1, colNum);
                colNum.getAndIncrement();
            }
        } else {
            generateCell(getRow(sheet, rowNum++), colNum.get(), String.valueOf(column.getText()));
            for (int i = 0; i < column.getTableView().getItems().size(); i++) {
                String value = "";
                if (column.getCellData(i) != null && column.getCellData(i) instanceof LocalDate) {
                    value = ((LocalDate) column.getCellData(i)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                } else if (column.getCellData(i) != null) {
                    value = String.valueOf(column.getCellData(i));
                }
                generateCell(getRow(sheet, rowNum++), colNum.get(), value);
            }
            sheet.autoSizeColumn(colNum.get());
        }
    }

    private void generateCell(Row row, int columnIndex, String value) {
        Cell cell = row.createCell(columnIndex, CellType.STRING);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private Row getRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        return row;
    }

    private Sheet getSheet(Workbook workbook, String name) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            sheet = workbook.createSheet(name);
        }
        return sheet;
    }
}
