package kup.get.controller.asu;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.config.FX.RomanNumber;
import kup.get.entity.alfa.Person;
import kup.get.service.Services;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@FxmlLoader(path = "/fxml/asu/badges.fxml")
public class BadgeController extends MyAnchorPane {

    @FXML
    private Button generateExcelButton;
    @FXML
    private MyTable<Badge> peopleTable;
    @FXML
    private TextField searchField;

    private final Services services;
    private byte[] logo;
    private final ObservableList<Badge> people = FXCollections.observableArrayList();

    public BadgeController(Services services) {
        this.services = services;
        try {
            this.logo = Files.readAllBytes(ResourceUtils.getFile("classpath:images/logo.png").toPath());
        } catch (IOException e) {
            this.logo = null;
            e.printStackTrace();
        }

        peopleTable
                .items(people)
                .searchBox(searchField.textProperty(), badge -> {
                    if (searchField.getText() == null || searchField.getText().isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = searchField.getText().toLowerCase();
                    if (badge.getPerson() == null)
                        return false;
                    return badge.getPerson().getPersonnelNumber().toLowerCase().contains(lowerCaseFilter)
                            || badge.getPerson().getLastName().toLowerCase().contains(lowerCaseFilter)
                            || badge.getPerson().getFirstName().toLowerCase().contains(lowerCaseFilter)
                            || badge.getPerson().getMiddleName().toLowerCase().contains(lowerCaseFilter);
                })
                .<Boolean>addColumn(col ->
                        col.header("")
                                .property(TableColumn::cellValueFactoryProperty, cellData -> {
                                    BooleanProperty property = new SimpleBooleanProperty(cellData.getValue().isActive());
                                    property.addListener((observable, oldValue, newValue) -> {
                                        cellData.getValue().setActive(newValue);
                                        if (cellData.getValue().getPerson().getPhoto() == null)
                                            services.getPersonService().getPhotoByPerson(cellData.getValue().getPerson().getId())
                                                    .doOnSuccess(cellData.getValue().getPerson()::setPhoto)
                                                    .doFinally((s) -> Platform.runLater(() -> peopleTable.refresh()))
                                                    .subscribe();
                                    });
                                    return property;
                                })
                                .widthColumn(30)
                                .property(TableColumn::cellFactoryProperty, CheckBoxTableCell.forTableColumn(col)))
                .<Integer>addColumn(col -> col
                        .header("Раз\nряд")
                        .cellValueFactory(badge -> badge.getPerson().getRank())
                        .editable((badge, v) -> badge.getPerson().setRank(v))
                        .cellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                        .widthColumn(50))
                .addColumn(col -> col
                        .header("Фото")
                        .cellValueFactory(badge -> {
                            MaterialDesignIconView photoIcon = null;
                            if (badge.getPerson().getPhoto() == null && badge.isActive()) {
                                photoIcon = new MaterialDesignIconView(MaterialDesignIcon.CLOSE_CIRCLE_OUTLINE);
                                photoIcon.setFill(Color.RED);
                                photoIcon.setSize("18");
                            } else if (badge.getPerson().getPhoto() != null && badge.isActive()) {
                                photoIcon = new MaterialDesignIconView(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
                                photoIcon.setFill(Color.GREEN);
                                photoIcon.setSize("18");
                            }
                            return photoIcon;
                        })
                        .widthColumn(50))
                .<Integer>addColumn(col -> col
                        .header("Коли\nчество")
                        .cellValueFactory(Badge::getAmount)
                        .editable(Badge::setAmount)
                        .cellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                        .widthColumn(50))
                .<Type>addColumn(col -> col
                        .header("Вид")
                        .cellValueFactory(Badge::getType)
                        .editable(Badge::setType)
                        .cellFactory(b -> new ComboBoxTableCell<>(Type.values()))
                        .widthColumn(80))
                .addColumn(col -> col
                        .header("Таб. №")
                        .cellValueFactory(badge -> badge.getPerson().getPersonnelNumber())
                        .widthColumn(80))
                .addColumn(col -> col.header("Фамилия").cellValueFactory(badge -> badge.getPerson().getLastName()))
                .addColumn(col -> col.header("Имя").cellValueFactory(badge -> badge.getPerson().getFirstName()))
                .addColumn(col -> col.header("Отчество").cellValueFactory(badge -> badge.getPerson().getMiddleName()))
                .addColumn(col -> col.header("Должность").cellValueFactory(badge -> badge.getPerson().getPosition().getName()))
                .addColumn(col -> col.header("Подразделение").cellValueFactory(badge -> badge.getPerson().getDepartment().getName()));

        generateExcelButton.setOnAction(event -> {
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet bigBadgesSheet = createSheet(workbook, "Большие бейджи");
            bigBadgesSheet.setColumnWidth(0, 4171);
            bigBadgesSheet.setColumnWidth(1, 7054);
            bigBadgesSheet.setColumnWidth(2, 1100);
            bigBadgesSheet.setColumnWidth(3, 4171);
            bigBadgesSheet.setColumnWidth(4, 7054);
            Sheet smallBadges = createSheet(workbook, "Узкие бейджи");
            smallBadges.setColumnWidth(0, 7762);
            smallBadges.setColumnWidth(1, 4265);
            smallBadges.setColumnWidth(2, 500);
            smallBadges.setColumnWidth(3, 7762);
            smallBadges.setColumnWidth(4, 4265);

            int row = 0, column = 1;
            for (Badge badge : people.stream()
                    .filter(badge -> badge.isActive() && !badge.getType().equals(Type.SMALL))
                    .collect(Collectors.toList())) {
                for (int i = 0; i < badge.getAmount(); i++) {
                    addBigBadge(bigBadgesSheet, badge, row, column % 2 != 0 ? 0 : 3);
                    addPhoto(bigBadgesSheet, badge, Type.BIG, row, column % 2 != 0 ? 0 : 3);
                    addLogo(bigBadgesSheet, row, column % 2 != 0 ? 1 : 4, 0.80);
                    if (column % 2 == 0) {
                        row += 5;
                        getRow(bigBadgesSheet, row++, 500);
                    }
                    column++;
                }
            }
            row = 0;
            column = 1;
            for (Badge badge : people.stream()
                    .filter(badge -> badge.isActive() && !badge.getType().equals(Type.BIG))
                    .collect(Collectors.toList())) {
                for (int i = 0; i < badge.getAmount(); i++) {
                    addSmallBadge(smallBadges, badge, row, column % 2 != 0 ? 0 : 3);
                    addPhoto(smallBadges, badge, Type.SMALL, row, column % 2 != 0 ? 1 : 4);
                    addLogo(smallBadges, row, column % 2 != 0 ? 0 : 3, 0.87);
                    if (column % 2 == 0) {
                        row += 4;
                        getRow(bigBadgesSheet, row++, 500);
                    }
                    column++;
                }
            }
            try {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                File file = new File("Бэйджи " + ft.format(new Date()) + ".xls");
                FileOutputStream outFile = new FileOutputStream(file);
                workbook.write(outFile);
                outFile.close();
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Sheet createSheet(Workbook workbook, String name) {
        Sheet sheet = workbook.createSheet(name);
        sheet.setMargin(Sheet.LeftMargin, 0.3);
        sheet.setMargin(Sheet.RightMargin, 0.3);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);
        return sheet;
    }

    private void addBigBadge(Sheet sheet, Badge badge, int rowNum, int colNum) {
        Row row = getRow(sheet, rowNum++, 43 * 20);
        generateCell(row, colNum + 1, "КУП\n“ГОРЭЛЕКТРОТРАНСПОРТ”", HSSFColor.HSSFColorPredefined.BLUE, 10)
                .getCellStyle().setVerticalAlignment(VerticalAlignment.BOTTOM);

        getRow(sheet, rowNum++, 25 * 20);

        row = getRow(sheet, rowNum++, 25 * 20);
        generateCell(row, colNum + 1,
                badge.getPerson().getRank()!=null
                        ? badge.getPerson().getPosition().getName() + "\n" + RomanNumber.toRoman(badge.getPerson().getRank()) + " класса"
                        : badge.getPerson().getPosition().getName(),
                HSSFColor.HSSFColorPredefined.RED, 10);

        row = getRow(sheet, rowNum++, 25 * 20);
        generateCell(row, colNum + 1, badge.getPerson().getLastName() + "\n" + badge.getPerson().getFirstName() + "\n" + badge.getPerson().getMiddleName(), HSSFColor.HSSFColorPredefined.BLUE, 14);

        getRow(sheet, rowNum, 43 * 20);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum, colNum + 1, colNum + 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 4, rowNum - 3, colNum + 1, colNum + 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 4, rowNum, colNum, colNum));

        CellRangeAddress bigRangeAddress = new CellRangeAddress(rowNum - 4, rowNum, colNum, colNum + 1);
        RegionUtil.setBorderBottom(BorderStyle.DOUBLE, bigRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.DOUBLE, bigRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.DOUBLE, bigRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.DOUBLE, bigRangeAddress, sheet);
        RegionUtil.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
        RegionUtil.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
        RegionUtil.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
        RegionUtil.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), bigRangeAddress, sheet);
    }

    private void addSmallBadge(Sheet sheet, Badge badge, int rowNum, int colNum) {
        getRow(sheet, rowNum++, 860);

        Row row = getRow(sheet, rowNum++, 500);
        generateCell(row, colNum, badge.getPerson().getLastName(), HSSFColor.HSSFColorPredefined.BLUE, 16)
                .getCellStyle().setVerticalAlignment(VerticalAlignment.BOTTOM);

        row = getRow(sheet, rowNum++, 625);
        generateCell(row, colNum, badge.getPerson().getFirstName(), HSSFColor.HSSFColorPredefined.BLUE, 16);

        row = getRow(sheet, rowNum++, 500);
        generateCell(row, colNum, badge.getPerson().getMiddleName(), HSSFColor.HSSFColorPredefined.BLUE, 16)
                .getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
        CellRangeAddress smallRangeAddress = new CellRangeAddress(rowNum - 4, rowNum - 1, colNum, colNum + 1);

        RegionUtil.setBorderBottom(BorderStyle.THIN, smallRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, smallRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, smallRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, smallRangeAddress, sheet);
    }

    private Row getRow(Sheet sheet, int rowNum, int height) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
            row.setHeight((short) height);
        }
        return row;
    }

    private void addPhoto(Sheet sheet, Badge badge, Type type, int rowNum, int colNum) {
        if (badge.getPerson().getPhoto() == null)
            services.getPersonService().getPhotoByPerson(badge.getPerson().getId())
                    .doOnSuccess(photo -> {
                        badge.getPerson().setPhoto(photo);
                        drawing(sheet, photo, colNum, rowNum, colNum + 1,
                                rowNum + type.getRowCount(), type.getDx(), type.getDy(),
                                type.getPercentHeight(), type.getPercentWidth());
                    })
                    .subscribe();
        else
            drawing(sheet, badge.getPerson().getPhoto(), colNum, rowNum, colNum + 1,
                    rowNum + type.getRowCount(), type.getDx(), type.getDy(),
                    type.getPercentHeight(), type.getPercentWidth());
    }

    private void addLogo(Sheet sheet, int rowNum, int colNum, double resizeY) {
        drawing(sheet, logo, colNum, rowNum, colNum + 1, rowNum + 1, 100, 30, 0.9, resizeY);
    }

    private void drawing(Sheet sheet, byte[] image, int colNum, int rowNum, int col2, int row2, int Dx1, int Dy1, double resizeX, double resizeY) {
        int logoIdx = sheet.getWorkbook().addPicture(image, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor logoAnchor = helper.createClientAnchor();
        logoAnchor.setCol1(colNum);
        logoAnchor.setRow1(rowNum);
        logoAnchor.setCol2(col2);
        logoAnchor.setRow2(row2);
        logoAnchor.setDx1(Dx1);
        logoAnchor.setDy1(Dy1);
        drawing.createPicture(logoAnchor, logoIdx)
                .resize(resizeX, resizeY);
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
        private boolean rank;
        private Integer amount = 1;
        private Type type = Type.All;

        public Badge(Person person) {
            this.person = person;
        }
    }

    @AllArgsConstructor
    @Getter
    private enum Type {
        All("Б + М", 0, 0, 0, 0, 0),
        BIG("Большой", 0.98, 0.89, 35, 130, 5),
        SMALL("Маленький", 0.98, 0.99, 0, 30, 4);
        private final String name;
        private final double percentHeight;
        private final double percentWidth;
        private final int dx;
        private final int dy;
        private final int rowCount;

        @Override
        public String toString() {
            return name;
        }
    }

    public void krsPeople() {
        services.getPersonService().getPeople()
                .doFinally(signalType -> peopleTable.refresh())
                .subscribe(p -> {
                    if(p.getDepartment() != null && p.getDepartment().getId() == 31)
                        people.add(new Badge(p));
                });
    }

    public void trafficPeople() {
        services.getPersonService().getPeople()
                .doFinally(signalType -> peopleTable.refresh())
                .subscribe(p -> {
                    if(p.getDepartment() != null && p.getDepartment().getId() == 151)
                        people.add(new Badge(p));
                });
    }

    public void allPeople() {
        services.getPersonService().getPeople()
                .doFinally(signalType -> peopleTable.refresh())
                .subscribe(person -> people.add(new Badge(person)));
    }

    @Override
    public void clearData() {
        people.clear();
    }

    @Override
    public void fillData() {
        peopleTable.refresh();
    }
}
