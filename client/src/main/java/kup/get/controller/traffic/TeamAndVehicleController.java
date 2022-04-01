package kup.get.controller.traffic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.FX.MyContextMenu;
import kup.get.config.MyTable;
import kup.get.entity.alfa.Person;
import kup.get.entity.traffic.TrafficPerson;
import kup.get.entity.traffic.TrafficTeam;
import kup.get.entity.traffic.TrafficVehicle;
import kup.get.service.Services;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@FxmlLoader(path = "/fxml/traffic/TeamAndVehicle.fxml")
public class TeamAndVehicleController extends MyAnchorPane {

    @FXML
    private AnchorPane briefingPane;

    @FXML
    private TextField searchBriefing;

    @FXML
    private MyTable<TrafficVehicle> vehicleTable;
    @FXML
    private MyTable<TrafficTeam> teamTable;
    @FXML
    private MyTable<TrafficPerson> trafficPeopleTable;
    @FXML
    private MyTable<Person> peopleTable;

    private final Services services;

    public TeamAndVehicleController(Services services) {
        this.services = services;
        peopleTable.setItems(peopleTable.getItems());
        trafficPeopleTable
                .setMyContextMenu(trafficPeopleCM())
                .headerColumn("Водители экипажа")
                .column("Таб.№", p -> parsePerson(Person::getPersonnelNumber, p)).build()
                .column("Фамилия", p -> parsePerson(Person::getLastName, p)).build()
                .column("Имя", p -> parsePerson(Person::getFirstName, p)).build()
                .column("Отчество", p -> parsePerson(Person::getMiddleName, p)).build();
        teamTable
                .setMyContextMenu(teamCM())
                .headerColumn("Экипажи")
                .column("id экипажа", TrafficTeam::getId).setInvisible().build()
                .column("№ экипажа", TrafficTeam::getNumber).setEditable((tt, value) -> {
                    tt.setNumber(value);
                    saveTrafficTeam(tt);
                }, TextFieldTableCell.forTableColumn()).build()
                .column("Режим работы", TrafficTeam::getWorkingMode).setEditable((tt, value) -> {
                    tt.setWorkingMode(value);
                    saveTrafficTeam(tt);
                }, TextFieldTableCell.forTableColumn()).build();

        vehicleTable
                .setMyContextMenu(vehicleCM())
                .headerColumn("Транспортные стредства")
                .column("Имя", TrafficVehicle::getId).setInvisible().build()
                .column("№ ТС", TrafficVehicle::getNumber)
                .setEditable((tv, value) -> {
                    tv.setNumber(value);
                    saveTrafficVehicle(tv);
                }, TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                .build()
                .column("Модель ТС", TrafficVehicle::getModel)
                .setEditable((tv, value) -> {
                    tv.setModel(value);
                    saveTrafficVehicle(tv);
                }, TextFieldTableCell.forTableColumn())
                .build()
                .column("id экипажа", tv -> tv.getTeam().getId()).setInvisible().build()
                .column("№ экипажа", tv -> tv.getTeam().getNumber()).build()
                .column("Режим работы", tv -> tv.getTeam().getWorkingMode()).build();

        peopleTable
                .setMyContextMenu(peopleCM())
                .headerColumn("Сотрудники")
                .column("id", Person::getId).setInvisible().build()
                .column("Таб.№", Person::getPersonnelNumber).build()
                .column("Фамилия", Person::getLastName).build()
                .column("Имя", Person::getFirstName).build()
                .column("Отчество", Person::getMiddleName).build()
                .column("Подразделение", p -> p.getDepartment().getName()).setInvisible().build()
                .column("Должность", p -> p.getPosition().getName()).setInvisible().build();

        vehicleTable.setRowFactory(tv -> {
            TableRow<TrafficVehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    trafficPeopleTable.getItems().clear();
                    if (row.getItem().getTeam() != null) {
                        services.getTrafficService()
                                .getPeopleByTeam(row.getItem().getTeam())
                                .doOnCancel(() -> trafficPeopleTable.refresh())
                                .subscribe(trafficPeopleTable.getItems()::add);
                    }
                }
            });
            return row;
        });
        teamTable.setRowFactory(tv -> {
            TableRow<TrafficTeam> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    trafficPeopleTable.getItems().clear();
                    services.getTrafficService()
                            .getPeopleByTeam(row.getItem())
                            .doOnCancel(() -> trafficPeopleTable.refresh())
                            .subscribe(trafficPeopleTable.getItems()::add);
                }
            });
            return row;
        });
    }

    private String parsePerson(Function<Person, String> function, TrafficPerson p) {
        try {
            return peopleTable.getItems().stream()
                    .filter(person -> person.getId().equals(p.getPersonId()))
                    .findFirst()
                    .map(function)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveTrafficTeam(TrafficTeam trafficTeam) {
        services.getTrafficService()
                .saveTrafficTeam(trafficTeam)
                .onErrorResume(e -> error())
                .doOnSuccess(tt -> trafficTeam.setId(tt.getId()))
                .subscribe();
    }

    private void saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        services.getTrafficService()
                .saveTrafficVehicle(trafficVehicle)
                .onErrorResume(e -> error())
                .doOnSuccess(p -> vehicleTable.refresh())
                .subscribe(tv -> trafficVehicle.setId(tv.getId()));
    }

    private ContextMenu vehicleCM() {
        return MyContextMenu.builder()
                .item("Добавить ТС", event -> {
                    vehicleTable.getItems().add(new TrafficVehicle());
                    vehicleTable.getSelectionModel().selectLast();
                })
                .item("Удалить ТС", event -> {
                    SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        services.getTrafficService().deleteTrafficVehicle(model.getSelectedItem())
                                .onErrorResume(e -> error())
                                .doOnSuccess(b -> vehicleTable.getItems().remove(model.getSelectedItem()))
                                .subscribe();
                    }
                })
                .item("Открепить экипаж", event -> {
                    SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        model.getSelectedItem().setTeam(null);
                        saveTrafficVehicle(model.getSelectedItem());
                    }
                });
    }

    private ContextMenu peopleCM() {
        return MyContextMenu
                .builder()
                .item("Закрепить сотрудника", event -> {
                    SelectionModel<TrafficTeam> teamModel = teamTable.getSelectionModel();
                    if (teamModel != null && teamModel.getSelectedItem() != null) {
                        SelectionModel<Person> personModel = peopleTable.getSelectionModel();
                        if (personModel != null && personModel.getSelectedItem() != null) {
                            TrafficPerson person = new TrafficPerson();
                            person.setPersonId(personModel.getSelectedItem().getId());
                            person.setTeam(teamModel.getSelectedItem());
                            services.getTrafficService()
                                    .saveTrafficPerson(person)
                                    .onErrorResume(e -> error())
                                    .doOnSuccess(tt -> {
                                        trafficPeopleTable.getItems().add(tt);
                                        trafficPeopleTable.refresh();
                                    })
                                    .subscribe();
                        }
                    }
                })
                .item("Обновить таблицу", event -> {
                    services.getPersonService().updatePeople();
//                    peopleTable.getItems().clear();
//                    peopleTable.setItems(FXCollections.observableArrayList(services.getPersonService().getPeople()));
                });
    }

    private ContextMenu trafficPeopleCM() {
        return MyContextMenu
                .builder()
                .item("Открепить сотрудника", event -> {
                    SelectionModel<TrafficPerson> personModel = trafficPeopleTable.getSelectionModel();
                    if (personModel != null && personModel.getSelectedItem() != null) {
                        personModel.getSelectedItem().setTeam(null);
                        services.getTrafficService()
                                .saveTrafficPerson(personModel.getSelectedItem())
                                .onErrorResume(e -> error())
                                .doOnSuccess(tp -> {
                                    trafficPeopleTable.getItems().remove(personModel.getSelectedItem());
                                    personModel.getSelectedItem().setTeam(tp.getTeam());
                                })
                                .subscribe();
                    }
                });
    }

    private ContextMenu teamCM() {
        return MyContextMenu.builder()
                .item("Добавить экипаж", event -> {
                    teamTable.getItems().add(new TrafficTeam());
                    teamTable.getSelectionModel().selectLast();
                })
                .item("Удалить экипаж", event -> {
                    SelectionModel<TrafficTeam> model = teamTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        services.getTrafficService().deleteTrafficTeam(model.getSelectedItem())
                                .onErrorResume(e -> error())
                                .doOnSuccess(b -> teamTable.getItems().remove(model.getSelectedItem()))
                                .subscribe();
                    }
                })
                .item("Закрепить экипаж", event -> {
                    SelectionModel<TrafficTeam> teamModel = teamTable.getSelectionModel();
                    if (teamModel != null && teamModel.getSelectedItem() != null) {
                        SelectionModel<TrafficVehicle> vehicleModel = vehicleTable.getSelectionModel();
                        if (vehicleModel != null && vehicleModel.getSelectedItem() != null) {
                            vehicleModel.getSelectedItem().setTeam(teamModel.getSelectedItem());
                            saveTrafficVehicle(vehicleModel.getSelectedItem());
                        }
                    }
                });
    }

    private <t> Mono<t> error() {
        Platform.runLater(() -> createAlert("Ошибка", "Не удалось удалить элемент\nПри необходимости обратитесь к администратору"));
        return Mono.empty();
    }

    @Override
    public void clearData() {
        trafficPeopleTable.getItems().clear();
        teamTable.getItems().clear();
        vehicleTable.getItems().clear();
        peopleTable.getItems().clear();
    }

    @Override
    public void fillData() {
        peopleTable.setItems(FXCollections.observableArrayList(services.getPersonService().getPeople()));
        services.getTrafficService().getTrafficVehicle().subscribe(vehicleTable.getItems()::add);
        services.getTrafficService().getAllTrafficTeam().subscribe(teamTable.getItems()::add);
    }
}
