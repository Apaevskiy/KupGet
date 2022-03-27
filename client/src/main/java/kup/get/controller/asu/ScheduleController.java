package kup.get.controller.asu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.FX.MyContextMenu;
import kup.get.config.MyTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

@FxmlLoader(path = "/fxml/asu/schedule.fxml")
public class ScheduleController extends MyAnchorPane {
    @FXML
    private MyTable<Direction> routeTable;
    @FXML
    private Button exportDataButton;
    @FXML
    private Button importDataButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextArea scheduleArea;
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    public ScheduleController() {
        routeTable
                .column("Номер\nмаршрута", d -> d.getRoute().getName())
                .setEditable((direction, s) -> direction.getRoute().setName(s), TextFieldTableCell.forTableColumn()).and()
                .column("Дни\nнедели", d -> d.getRoute().getWorkDays())
                .setEditable((direction, s) -> direction.getRoute().setWorkDays(s), TextFieldTableCell.forTableColumn()).and()
                .column("Отправление", Direction::getDeparturePoint)
                .setEditable(Direction::setDeparturePoint, TextFieldTableCell.forTableColumn()).and()
                .column("Стиль", d -> d.getRoute().getStyle())
                .setEditable((direction, s) -> direction.getRoute().setStyle(s), TextFieldTableCell.forTableColumn()).and()
                .addColumn("Начало\nдвижения", Direction::getStartWork)
                .addColumn("Окончание\nдвижения", Direction::getEndWork);

        routeTable.setRowFactory(tv -> {
            TableRow<Direction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    if (row.getItem().getSchedule().isEmpty())
                        scheduleArea.setText("");
                    else
                        scheduleArea.setText(
                                row.getItem().getSchedule()
                                        .replaceAll(", ", "\n")
                                        .replaceAll("-", "."));
                }
            });
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Direction draggedDirection = routeTable.getItems().remove(draggedIndex);

                    int dropIndex;

                    if (row.isEmpty()) {
                        dropIndex = routeTable.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    routeTable.getItems().add(dropIndex, draggedDirection);

                    event.setDropCompleted(true);
                    routeTable.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            return row;
        });
        saveButton.setOnAction(event -> {
            SelectionModel<Direction> model = routeTable.getSelectionModel();
            if (model != null && model.getSelectedItem() != null) {
                Pattern pattern = Pattern.compile("\\d{1,2}\\.\\d{1,2}(\n|$)");
                Matcher matcher = pattern.matcher(scheduleArea.getText());
                String start = "", end = "";
                while (matcher.find()) {
                    if (start.isEmpty())
                        start = matcher.group();
                    end = matcher.group();
                }
                String[] startList = start.replaceAll("\n", "").split("\\."),
                        endList = end.replaceAll("\n", "").split("\\.");
                if (startList.length == 2 && endList.length == 2) {
                    try {
                        model.getSelectedItem().setStartWork(String.format("%02d:%02d", Integer.parseInt(startList[0]), Integer.parseInt(startList[1])));
                        model.getSelectedItem().setEndWork(String.format("%02d:%02d", Integer.parseInt(endList[0]), Integer.parseInt(endList[1])));

                        model.getSelectedItem().setSchedule(
                                scheduleArea.getText()
                                        .replaceAll("\\d{1,2}\\.\\d{1,2}\\.1", " (в депо №1), ")
                                        .replaceAll("\\d{1,2}\\.\\d{1,2}\\.2", " (в депо №2), ")
                                        .replaceAll("\\d{1,2}\\.\\d{1,2}\\.3", " (до парка Фестивальный), ")
                                        .replaceAll("\n", ", ")
                                        .replaceAll("\\.", "-")
                        );
                    } catch (NumberFormatException e) {
                        createAlert("Ошибка", "Проверьте введённые данные\n"+e.getMessage());
                        e.printStackTrace();
                    }
                } else createAlert("Ошибка", "Проверьте введённые данные");


                routeTable.refresh();
            }
        });
        importDataButton.setOnAction(event -> {
            clearData();
            fillData();
        });
        exportDataButton.setOnAction(event -> {
            StringBuilder result = new StringBuilder(
                    "<div class=\"tabel_flex column_11 tabel\">\n" +
                            "\t<div class=\"table_flex_line main\">\n" +
                            "\t\t<span class=\"col_2\">Номер маршрута</span>\n" +
                            "\t\t<span>Дни недели</span>\n" +
                            "\t\t<span>Отправление</span>\n" +
                            "\t\t<span>Начало движения</span>\n" +
                            "\t\t<span>Окончание движения</span>\n" +
                            "\t\t<span style=\"text-align: center;\" class=\"col_5\">Расписание отправлений</span>\n" +
                            "\t</div>\n");
            Set<Route> set = new LinkedHashSet<>();
            routeTable.getItems().stream().map(Direction::getRoute).forEach(set::add);
            for (Route route : set) {
                result.append(createTableFlexLine(route));
            }
            result.append("</div>");

            StringSelection selection = new StringSelection(result.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            createAlert("Уведомление", "Текст скопирован в буфер обмена!");
        });

        routeTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                routeTable.setContextMenu(createContextMenu());
            }
        });
    }

    private ContextMenu createContextMenu() {
        return MyContextMenu.builder()
                .item("Добавить маршрут", e -> {
                    Direction direction = new Direction();
                    direction.setRoute(new Route());
                    routeTable.getItems().add(direction);
                    routeTable.getSelectionModel().select(direction);
                    routeTable.refresh();
                })
                .item("Добавить направление", e -> {
                    SelectionModel<Direction> model = routeTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        Direction direction = new Direction();
                        direction.setRoute(model.getSelectedItem().getRoute());
                        routeTable.getItems().add(model.getSelectedIndex(), direction);
                        routeTable.getSelectionModel().select(model.getSelectedIndex());
                        routeTable.refresh();
                    }
                })
                .item("Сменить направление", e -> {
                    SelectionModel<Direction> model = routeTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        Route route = model.getSelectedItem().getRoute();
                        if (route != null && route.getTo() != null && route.getOut() != null) {
                            String str = route.getTo().getDeparturePoint();
                            route.getTo().setDeparturePoint(route.getOut().getDeparturePoint());
                            route.getOut().setDeparturePoint(str);
                            routeTable.refresh();
                        }
                    }
                })
                .item("Переместить наверх", e -> {
                    SelectionModel<Direction> model = routeTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        Route route = model.getSelectedItem().getRoute();
                        if (route != null && route.getTo() != null) {
                            routeTable.getItems().remove(route.getTo());
                            routeTable.getItems().add(0, route.getTo());
                        }
                        if (route != null && route.getOut() != null) {
                            routeTable.getItems().remove(route.getOut());
                            routeTable.getItems().add(0, route.getOut());
                        }
                        routeTable.refresh();
                    }
                })
                .item("Переместить вниз", e -> {
                    SelectionModel<Direction> model = routeTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        Route route = model.getSelectedItem().getRoute();
                        if (route != null && route.getTo() != null) {
                            routeTable.getItems().remove(route.getTo());
                            routeTable.getItems().add(route.getTo());
                        } else if (route != null && route.getOut() != null) {
                            routeTable.getItems().remove(route.getOut());
                            routeTable.getItems().add(route.getOut());
                            routeTable.refresh();
                        }
                    }
                })
                .item("Удалить", e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Подтверждение");
                    alert.setHeaderText("Вы действительно хотите удалить маршрут?");
                    alert.setContentText("Нажмите ОК для удаление и Cancel для отмены");

                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        SelectionModel<Direction> model = routeTable.getSelectionModel();
                        if (model != null && model.getSelectedItem() != null) {
                            routeTable.getItems().remove(model.getSelectedItem());
                            routeTable.refresh();
                        }
                    }
                })
                .item("Обновить", e -> routeTable.refresh());
    }

    private String createTableFlexLine(Route route) {
        return "\t<div class=\"table_flex_line\">\n" +
                "\t\t<span class=\"col_2\"><b>" + route.getName() + "</b></span>\n" +
                "\t\t<span style=\"" + route.style + "\"><i>" + route.getWorkDays() + "</i></span>\n" +
                "\t\t<span>" + route.getTo().getDeparturePoint() + "</span>\n" +
                "\t\t<span>" + route.getTo().getStartWork() + "</span>\n" +
                "\t\t<span>" + route.getTo().getEndWork() + "</span>\n" +
                "\t\t<span class=\"col_5\">\n" +
                "\t\t\t" + route.getTo().getSchedule() + "\n" +
                "\t\t</span>\n" +
                "\t</div>\n" +
                "\t<div class=\"table_flex_line\">\n" +
                "\t\t<span class=\"col_3\"></span>\n" +
                "\t\t<span>" + route.getOut().getDeparturePoint() + "</span>\n" +
                "\t\t<span>" + route.getOut().getStartWork() + "</span>\n" +
                "\t\t<span>" + route.getOut().getEndWork() + "</span>\n" +
                "\t\t<span class=\"col_5\">\n" +
                "\t\t\t" + route.getOut().getSchedule() + "\n" +
                "\t\t</span>\n" +
                "\t</div>\n";
    }

    @Override
    public void fillData() {
        try {
            Document doc = Jsoup.connect("http://get.gomel.by/raspisanie/")
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .get();
            Elements elements = doc.select("div[class=table_flex_line]");

            Route route = null;

            for (Element tableLine : elements) {
                Elements listSpan = tableLine.select("span");
                if (listSpan.size() == 6) {
                    route = new Route();
                    route.setName(listSpan.get(0).text());
                    route.setWorkDays(listSpan.get(1).text());
                    route.setStyle(listSpan.get(1).attr("style"));

                    Direction direction = new Direction(
                            listSpan.get(2).text(),
                            listSpan.get(3).text(),
                            listSpan.get(4).text(),
                            listSpan.get(5).text(),
                            route);
                    route.setTo(direction);
                    routeTable.getItems().add(direction);
                } else if (listSpan.size() == 5) {
                    Direction direction = new Direction(
                            listSpan.get(1).text(),
                            listSpan.get(2).text(),
                            listSpan.get(3).text(),
                            listSpan.get(4).text(),
                            route);
                    if (route != null)
                        route.setOut(direction);
                    routeTable.getItems().add(direction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearData() {
        routeTable.getItems().clear();
    }

    @Data
    private static class Route {
        private String name = "";
        private String workDays = "";
        private Direction to;
        private Direction out;
        private String style = "";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Direction {
        private String departurePoint = "";
        private String startWork = "";
        private String endWork = "";
        private String schedule = "";
        private Route route;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Direction direction = (Direction) o;
            return Objects.equals(departurePoint, direction.departurePoint) && Objects.equals(startWork, direction.startWork) && Objects.equals(endWork, direction.endWork) && Objects.equals(schedule, direction.schedule);
        }

        @Override
        public int hashCode() {
            return Objects.hash(departurePoint, startWork, endWork, schedule);
        }
    }
}
