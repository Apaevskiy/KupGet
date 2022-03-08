package kup.get.controller.FX.asu;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.config.RomanNumber;
import kup.get.controller.socket.SocketService;
import kup.get.model.alfa.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@FxmlLoader(path = "/fxml/asu/badges.fxml")
public class BadgeController extends MyAnchorPane {

    @FXML
    private Button generateExcelButton;
    @FXML
    private MyTable<Badge> peopleTable;
    @FXML
    private TextField searchField;

    private final SocketService socketService;
    private final ObservableList<Person> people = FXCollections.observableArrayList();

    public BadgeController(SocketService socketService) {
        this.socketService = socketService;
        peopleTable
                .column("", badge -> {
                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(badge.isActive());
                    checkBox.selectedProperty().addListener(
                            (ov, old_val, new_val) -> badge.setActive(new_val));
                    return checkBox;
                }).setMaxWidthColumn(30)
                .and()
                .column("Разряд", Badge::getRank)
                    .setEditable(Badge::setRank, TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                    .setMaxWidthColumn(100)
                .and()
                .column("Количество", Badge::getAmount)
                    .setEditable(Badge::setAmount, TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                    .setMaxWidthColumn(100)
                .and()
                .addColumn("Вид", badge -> {
                    TableCell<Badge, Type> c = new TableCell<>();
                    final ComboBox<Type> comboBox = new ComboBox<>(FXCollections.observableArrayList(Type.values()));
                    System.out.println(Arrays.toString(Type.values()));
                    c.itemProperty().addListener((observable, oldValue, newValue) -> {
                            comboBox.setValue(newValue);
                            badge.setType(newValue);
                    });
                    c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
                    return c;
                })
                .addColumn("Таб. №", badge -> badge.getPerson().getPersonnelNumber())
                .addColumn("Фамилия", badge -> badge.getPerson().getLastName())
                .addColumn("Имя", badge -> badge.getPerson().getFirstName())
                .addColumn("Отчество", badge -> badge.getPerson().getMiddleName())
                .addColumn("Должность", badge -> badge.getPerson().getPosition().getName())
                .addColumn("Подразделение", badge -> badge.getPerson().getDepartment().getName());

        generateExcelButton.setOnAction(event -> {
            /*HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Badges " + LocalDate.now());
            sheet.setMargin(Sheet.LeftMargin, 0.5);
            sheet.setMargin(Sheet.RightMargin, 0.5);
            sheet.setMargin(Sheet.TopMargin, 0.5);
            sheet.setMargin(Sheet.BottomMargin, 0.5);
            generateBadges(sheet);
            try {
                File file = new File("report.xls");
                FileOutputStream outFile = new FileOutputStream(file);
                workbook.write(outFile);
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        });
    }

    public void generateBadges(Sheet sheet) {
        File fi = new File("images/logo.png");
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(fi.toPath());
        } catch (IOException e) {
            createAlert("КУДА ДЕЛИ ЛОГОТИП?", "Верните логотип предприятия сюда:\n" + fi.getAbsolutePath());
            e.printStackTrace();
        }
        int rowNum = 0;
        sheet.setColumnWidth(0, 7762);
        sheet.setColumnWidth(1, 4265);
        sheet.setColumnWidth(2, 1100);
        sheet.setColumnWidth(3, 4171);
        sheet.setColumnWidth(4, 7054);
        for (Badge badge : peopleTable.getItems().stream().filter(Badge::isActive).collect(Collectors.toList())) {
            for (int i = 0; i < badge.getAmount(); i++) {
                Row firstRow = sheet.createRow(rowNum++);
                firstRow.setHeight((short) (43 * 20));
                generateCell(firstRow, 4, "КУП\n“ГОРЭЛЕКТРОТРАНСПОРТ”", HSSFColor.HSSFColorPredefined.BLUE, 10)
                        .getCellStyle().setVerticalAlignment(VerticalAlignment.BOTTOM);
                generateImages(badge.getPerson(), sheet, rowNum - 1, fileContent);
                Row row = sheet.createRow(rowNum++);
                row.setHeight((short) (25 * 20));
                generateCell(row, 0, badge.getPerson().getLastName(), HSSFColor.HSSFColorPredefined.BLUE, 16)
                        .getCellStyle().setVerticalAlignment(VerticalAlignment.BOTTOM);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 4, 4));
                row = sheet.createRow(rowNum++);
                row.setHeight((short) (25 * 20));
                generateCell(row, 0, badge.getPerson().getFirstName(), HSSFColor.HSSFColorPredefined.BLUE, 16);

                generateCell(row, 4,
                        badge.getRank()!=null
                                ? badge.getPerson().getPosition().getName() + "\n" + RomanNumber.toRoman(badge.getRank()) + " класса"
                                : badge.getPerson().getPosition().getName(),
                        HSSFColor.HSSFColorPredefined.RED, 10);
                row = sheet.createRow(rowNum++);
                row.setHeight((short) (25 * 20));
                generateCell(row, 0, badge.getPerson().getMiddleName(), HSSFColor.HSSFColorPredefined.BLUE, 16)
                        .getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
                generateCell(row, 4, badge.getPerson().getLastName() + "\n" + badge.getPerson().getFirstName() + "\n" + badge.getPerson().getMiddleName(), HSSFColor.HSSFColorPredefined.BLUE, 14);
                row = sheet.createRow(rowNum++);
                row.setHeight((short) (43 * 20));
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 4, 4));

                CellRangeAddress smallRangeAddress = new CellRangeAddress(rowNum - 5, rowNum - 2, 0, 1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, smallRangeAddress, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, smallRangeAddress, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, smallRangeAddress, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, smallRangeAddress, sheet);
                CellRangeAddress bigRangeAddress = new CellRangeAddress(rowNum - 5, rowNum - 1, 3, 4);
                RegionUtil.setBorderBottom(BorderStyle.DOUBLE, bigRangeAddress, sheet);
                RegionUtil.setBorderTop(BorderStyle.DOUBLE, bigRangeAddress, sheet);
                RegionUtil.setBorderLeft(BorderStyle.DOUBLE, bigRangeAddress, sheet);
                RegionUtil.setBorderRight(BorderStyle.DOUBLE, bigRangeAddress, sheet);
                RegionUtil.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
                RegionUtil.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
                RegionUtil.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
                RegionUtil.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
                rowNum++;
            }
        }
    }

    public void generateImages(Person person, Sheet sheet, int row, byte[] fileContent) {

        int photoIdx = sheet.getWorkbook().addPicture(socketService.getPhotoByPerson(person.getId()), Workbook.PICTURE_TYPE_PNG);
        int logoIdx = sheet.getWorkbook().addPicture(fileContent, Workbook.PICTURE_TYPE_PNG);

        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor photo1 = helper.createClientAnchor();
        photo1.setCol1(1);
        photo1.setRow1(row);
        photo1.setCol2(2);
        photo1.setRow2(row + 4);
        photo1.setDy1(30);
        drawing.createPicture(photo1, photoIdx)
                .resize(0.98, 0.99);

        ClientAnchor photo2 = helper.createClientAnchor();
        photo2.setCol1(3);
        photo2.setRow1(row);
        photo2.setCol2(4);
        photo2.setRow2(row + 5);
        photo2.setDx1(35);
        photo2.setDy1(130);
        drawing.createPicture(photo2, photoIdx)
                .resize(0.98, 0.89);

        ClientAnchor logo1 = helper.createClientAnchor();
        logo1.setCol1(0);
        logo1.setRow1(row);
        logo1.setCol2(1);
        logo1.setRow2(row + 1);
        logo1.setDx1(175);
        logo1.setDy1(20);
        drawing.createPicture(logo1, logoIdx)
                .resize(0.81, 0.90);

        ClientAnchor logo2 = helper.createClientAnchor();
        logo2.setCol1(4);
        logo2.setRow1(row);
        logo2.setCol2(5);
        logo2.setRow2(row + 1);
        logo2.setDx1(175);
        logo2.setDy1(20);
        drawing.createPicture(logo2, logoIdx)
                .resize(0.81, 0.90);
    }

    private org.apache.poi.ss.usermodel.Cell generateCell(Row row, int columnIndex, String value, HSSFColor.HSSFColorPredefined color, int size) {
        org.apache.poi.ss.usermodel.Font font = row.getSheet().getWorkbook().createFont();
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.setFont(font);
        Cell cell = row.createCell(columnIndex, CellType.STRING);
        cell.setCellValue(value);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setFontHeight((short) (size * 20));
        font.setColor(color.getIndex());
        font.setBold(true);
        font.setFontName("Times New Roman");
        style.setWrapText(true);
        cell.setCellStyle(style);
        return cell;
    }

    @Data
    private static class Badge {
        private Person person;
        private boolean active;
        private Integer rank;
        private Integer amount = 1;
        private Type type = Type.BIG;
        public Badge(Person person) {
            this.person = person;
        }
    }
    @AllArgsConstructor
    @Getter
    private enum Type{
        All("Б + М"),
        BIG("Большой"),
        SMALL("Маленький");
        private final String name;
    }
    @Override
    public void clearData() {
        people.clear();
        peopleTable.getItems().clear();
    }

    public void krsPeople(){
        people.addAll(socketService.getPeople().stream().filter(p -> p.getPosition().getId()==8388).collect(Collectors.toList()));
        for (Person person : people)
            peopleTable.getItems().add(new Badge(person));
    }
    public void trafficPeople(){
        people.addAll(socketService.getPeople().stream().filter(p -> p.getPosition().getId()==8347).collect(Collectors.toList()));
        for (Person person : people)
            peopleTable.getItems().add(new Badge(person));
    }
    public void allPeople(){
        people.addAll(socketService.getPeople());
        for (Person person : people)
            peopleTable.getItems().add(new Badge(person));
    }

    @Override
    public void fillData() {
        peopleTable.refresh();
    }
}
