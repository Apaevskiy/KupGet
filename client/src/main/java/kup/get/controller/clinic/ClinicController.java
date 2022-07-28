package kup.get.controller.clinic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.alfa.ClinicEvent;
import kup.get.entity.alfa.Person;
import kup.get.service.Services;
import kup.get.service.other.SheetService;
import kup.get.service.socket.ClinicService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@FxmlLoader(path = "/fxml/clinic/report.fxml")
public class ClinicController extends MyAnchorPane {
    @FXML
    private MyTable<Event> itemTable;

    @FXML
    private ComboBox<Type> typeComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField searchItemField;

    @FXML
    private VBox menuVBox;
    @FXML
    private CheckBox vacationCheckBox;
    @FXML
    private CheckBox sickLeaveCheckBox;

    @FXML
    private Button excelButton;
    @FXML
    private Button loadDataButton;

    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final Services services;
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService, Services services) {
        this.services = services;
        this.clinicService = clinicService;

        typeComboBox.setItems(FXCollections.observableArrayList(Type.values()));
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        itemTable
                .items(events)
                .searchBox(vacationCheckBox.onActionProperty(), item -> {
                    if (vacationCheckBox.isSelected())
                        return true;
                    return item.clinicEvents.stream().noneMatch(clinicEvent -> clinicEvent.getPnp() == -1);
                })
                .searchBox(sickLeaveCheckBox.onActionProperty(), item -> {
                    if (sickLeaveCheckBox.isSelected())
                        return true;
                    return item.clinicEvents.stream().noneMatch(clinicEvent -> clinicEvent.getPnp() == -2);
                })
                .searchBox(typeComboBox.onActionProperty(), item -> {
                    Type type = typeComboBox.getSelectionModel().getSelectedItem();
                    if (type == null || type == Type.ALL)
                        return true;
                    else if (type == Type.SPECIALISTS) {
                        if ((item.person.getDepartment().getId() == 40 && item.person.getPosition().getId() == 48) ||
                                (item.person.getDepartment().getId() == 31 && item.person.getPosition().getId() == 59))
                            return true;
                        return item.person.getRank() != null && item.person.getRank() > 7 && item.person.getRank() < 30;
                    } else if (type == Type.WORKERS) {
                        return item.person.getRank() == null || item.person.getRank() <= 7;
                    }
                    return false;
                })
                .searchBox(searchItemField.textProperty(), item -> {
                    if (searchItemField.getText() == null || searchItemField.getText().isEmpty()) {
                        return true;
                    }
                    if (item == null)
                        return false;

                    String lowerCaseFilter = searchItemField.getText().toLowerCase();

                    if (item.person != null && item.person.getDepartment() != null && item.person.getDepartment().getName().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    if (item.person != null && item.person.getPosition() != null && item.person.getPosition().getName().toLowerCase().contains(lowerCaseFilter))
                        return true;

                    return (item.person != null &&
                            (item.person.getPersonnelNumber().toLowerCase().contains(lowerCaseFilter)
                                    || item.person.getLastName().toLowerCase().contains(lowerCaseFilter)
                                    || item.person.getFirstName().toLowerCase().contains(lowerCaseFilter)
                                    || item.person.getMiddleName().toLowerCase().contains(lowerCaseFilter)));
                })
                .addColumn(col -> col.header("Табельный\nномер").cellValueFactory(event -> event.getPerson().getPersonnelNumber()))
                .addColumn(col -> col.header("ФИО").cellValueFactory(event -> event.getPerson().getFullName()))
                .addColumn(col -> col.header("Должность").cellValueFactory(event -> event.getPerson().getPosition().getName()))
                .addColumn(col -> col.header("Подразделение").cellValueFactory(event -> event.getPerson().getDepartment().getName()))
                .<Set<Integer>>addColumn(col -> col.header("Результат осмотра").cellValueFactory(event -> event.getClinicEvents().stream().map(ClinicEvent::getPnp).collect(Collectors.toSet()))
                        .cellFactory(param ->
                                new TableCell<Event, Set<Integer>>() {
                                    @Override
                                    protected void updateItem(Set<Integer> item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (!empty && item != null) {
                                            setText(item.stream().map(v -> {
                                                if (v == 0) return "Допущен";
                                                else if (v == 1) return "Не допущен";
                                                else if (v == 2) return "Отмена";
                                                else if (v == -1) return "Больничный";
                                                else if (v == -2) return "Отпуск";
                                                else return String.valueOf(v);
                                            }).collect(Collectors.joining("/", "", "")));
                                        } else setText("");
                                    }
                                }))
                .addColumn(col -> col.header("Время\nпрохождения\nнарколога").cellValueFactory(event -> event.clinicEvents.stream().map(n -> n.getTime() != null ? String.valueOf(n.getTime()) : "").collect(Collectors.joining(", ", "", ""))))
                .addColumn(col -> col.header("Время прохождения проходной")
                        .<String>childColumn(c -> c.header("Вход").cellValueFactory(event -> event.checkpointEventsIn.stream().map(n -> ft.format(n.getTime())).collect(Collectors.joining(", ", "", ""))))
                        .<String>childColumn(c -> c.header("Выход").cellValueFactory(event -> event.checkpointEventsOut.stream().map(n -> ft.format(n.getTime())).collect(Collectors.joining(", ", "", "")))));

        loadDataButton.setOnAction(event -> {
            loadDataButton.setDisable(true);
            clearEvents();
            LocalDate date = datePicker.getValue();
            if (date == null) {
                createAlert("Ошибка", "Выберите дату!");
                return;
            }
            AtomicInteger i = new AtomicInteger(0);
            loadDataOfAlfa(date,i);
            loadDataOfCheckpoint(date,i).start();
        });
        excelButton.setOnAction(event -> {
            excelButton.setDisable(true);
            SheetService sheetService = new SheetService();
            sheetService.writeDataOfColumnsToSheet("Отчёт", itemTable);
            sheetService.createAndOpenFile("Отчёт посещений " + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
            excelButton.setDisable(false);
        });
    }

    @Override
    public void fillData() {
        loadDataButton.setDisable(true);
        datePicker.setValue(LocalDate.now());

        services.getPersonService().getPeople()
                .map(person -> {
                    Event event = new Event();
                    event.setPerson(person);
                    return event;
                })
                .doFinally((signalType) -> Platform.runLater(() -> {
                    itemTable.refresh();
                    loadDataButton.setDisable(false);
                }))
                .onErrorResume(throwable -> {
                    createAlert("Ошибка", throwable.getMessage());
                    return Mono.empty();
                })
                .subscribe(events::add);

    }

    @Data
    private static class Event {
        private Person person;
        private List<ClinicEvent> clinicEvents = new ArrayList<>();
        private List<Date> checkpointEventsIn = new ArrayList<>();
        private List<Date> checkpointEventsOut = new ArrayList<>();
    }

    private void clearEvents() {
        events.forEach(event -> {
            event.clinicEvents.clear();
            event.checkpointEventsIn.clear();
            event.checkpointEventsOut.clear();
        });
        menuVBox.setVisible(false);
    }

    @Override
    public void clearData() {
        events.clear();
    }

    @Getter
    @AllArgsConstructor
    private enum Type {
        ALL("Все"),
        WORKERS("Рабочие"),
        DRIVERS("Водители троллейбуса"),
        SPECIALISTS("ИТР");
        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private Thread loadDataOfCheckpoint(LocalDate date, AtomicInteger i) {
        return new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Connection connection = DriverManager.getConnection("jdbc:firebirdsql://192.168.0.250/C:/Program Files (x86)/ENT/Server/DB/cbase.fdb?sql_dialect=3&lc_ctype=WIN1251", "SYSDBA", "masterkey");
                    ResultSet rs = connection.prepareStatement("select e.dt, u.fio, e.evn from fb_evn e join fb_usr u on e.usr=u.id " +
                            "where cast(e.dt as date)='" + date + "' and (e.EVN=5 or e.EVN=3)").executeQuery();
                    while (rs.next()) {
                        String fio = rs.getString("fio");
                        Optional<Event> optional = events.stream().filter(e -> e.getPerson().getFullName().toLowerCase(Locale.ROOT).equals(fio.toLowerCase(Locale.ROOT))).findFirst();
                        if (optional.isPresent()) {
                            if (rs.getInt("evn") == 3)
                                optional.get().checkpointEventsOut.add(rs.getDate("dt"));
                            else if (rs.getInt("evn") == 5)
                                optional.get().checkpointEventsIn.add(rs.getDate("dt"));
                        }
                    }
                    connection.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> createAlert("Ошибка", e.getMessage()));
                }
                if (i.incrementAndGet() == 2)
                    loadDataButton.setDisable(false);
                itemTable.refresh();
            }
        };
    }

    private void loadDataOfAlfa(LocalDate date, AtomicInteger i) {
        clinicService.getItemsType(date)
                .onErrorResume(throwable -> {
                    createAlert("Ошибка", throwable.getMessage());
                    return Mono.empty();
                })
                .doFinally(signalType -> Platform.runLater(() -> {
                    if (i.incrementAndGet() == 2)
                        loadDataButton.setDisable(false);
                    itemTable.refresh();
                    menuVBox.setVisible(true);
                }))
                .subscribe(clinicEvent -> {
                    Optional<Event> optional = events.stream().filter(e -> e.getPerson().getId().equals(clinicEvent.getPersonId())).findFirst();
                    optional.ifPresent(value -> value.getClinicEvents().add(clinicEvent));
                });
    }
}
