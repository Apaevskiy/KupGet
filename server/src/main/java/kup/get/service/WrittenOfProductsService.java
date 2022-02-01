package kup.get.service;

import kup.get.entity.energy.WrittenOfProducts;
import kup.get.repository.energy.WrittenOfProductsRepository;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class WrittenOfProductsService {
    private final WrittenOfProductsRepository repository;
    private final LogService logService;

    public WrittenOfProductsService(WrittenOfProductsRepository repository, LogService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    public void writeOf(WrittenOfProducts writtenOfProducts, double count) {
        repository.save(writtenOfProducts);
        logService.addLog("Списал товар " +
                writtenOfProducts.getProductsOnMaster().getProduct().getType().getName() +
                " в количесве " + count + " " +
                writtenOfProducts.getProductsOnMaster().getProduct().getType().getUnit());
    }

    public List<WrittenOfProducts> getProducts(LocalDate begin, LocalDate end) {
        return repository.findAll(
                WrittenOfProductsRepository.dataFilter(begin, end).and(WrittenOfProductsRepository.userFilter()),
                WrittenOfProductsRepository.getPageable()).getContent();
    }
    public List<WrittenOfProducts> getAllProducts(LocalDate begin, LocalDate end) {
        return repository.findAll(
                WrittenOfProductsRepository.dataFilter(begin, end),
                WrittenOfProductsRepository.getPageable()).getContent();
    }

    public File getExcelFile(List<WrittenOfProducts> list) {
        if (list.size() != 0) {

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("journal");
            int rowNum = 0;

            HSSFFont font = workbook.createFont();
            font.setFontHeight((short) (14 * 20));
            font.setBold(true);
            style = workbook.createCellStyle();
            style.setFont(font);
            style.setWrapText(false);

            Row row = sheet.createRow(rowNum++);
            int i = 0;

            generateCell(row, i++, "№ Товара");
            generateCell(row, i++, "Шифр учёта");
            generateCell(row, i++, "Наименование");
            generateCell(row, i++, "Ед. изм.");
            generateCell(row, i++, "Количество");
            i++;
            generateCell(row, i++, "Цена");
            generateCell(row, i++, "Сумма");
            generateCell(row, i++, "Статус");
            generateCell(row, i++, "Дата списания");
            generateCell(row, i++, "Мастер");
            generateCell(row, i++, "Номер накладной");


            row = sheet.createRow(rowNum++);
            generateCell(row, 4, "Затребовано");
            generateCell(row, 5, "Отпущено");

            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 8, 8));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 9, 9));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 10));
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 11, 11));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5));
            for (int j = 0; j < i; j++) {
                sheet.autoSizeColumn(j);
            }
            font.setFontHeight((short) (12 * 20));
            font.setBold(false);
            style = workbook.createCellStyle();
            style.setFont(font);
            style.setWrapText(true);
            for (WrittenOfProducts product : list) {
                row = sheet.createRow(rowNum++);
                i = 0;
                generateCell(row, i++, product.getId());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getType().getId());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getType().getName());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getType().getUnit());
                generateCell(row, i++, product.getNumberRequire());
                generateCell(row, i++, product.getRemaining());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getPrice());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getPrice() * product.getRemaining());
                generateCell(row, i++, product.getProductsOnMaster().getProduct().getStatus());
                generateCell(row, i++, product.getDateWrittenOf());
                generateCell(row, i++, product.getProductsOnMaster().getUser().getFIO());
                generateCell(row, i, product.getProductsOnMaster().getProduct().getWaybills().getId());
            }
            try {
                File file = new File("report.xls");
                FileOutputStream outFile = new FileOutputStream(file);
                workbook.write(outFile);
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private HSSFCellStyle style;

    private void generateCell(Row row, int i, Object name) {
        if (name == null) name = "";
        Cell cell = row.createCell(i, CellType.STRING);
        cell.setCellValue(name.toString());
        cell.setCellStyle(style);
    }
}
