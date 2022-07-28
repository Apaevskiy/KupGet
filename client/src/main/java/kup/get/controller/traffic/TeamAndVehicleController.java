package kup.get.controller.traffic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.alfa.Person;
import kup.get.entity.traffic.TrafficPerson;
import kup.get.entity.traffic.TrafficTeam;
import kup.get.entity.traffic.TrafficVehicle;
import kup.get.service.Services;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@FxmlLoader(path = "/fxml/traffic/TeamAndVehicle.fxml")
public class TeamAndVehicleController extends MyAnchorPane {

    @FXML
    private AnchorPane briefingPane;

    @FXML
    private TextField searchField;

    @FXML
    private MyTable<TrafficVehicle> vehicleTable;
    @FXML
    private MyTable<TrafficTeam> teamTable;
    @FXML
    private MyTable<TrafficPerson> trafficPeopleTable;
    @FXML
    private MyTable<Person> peopleTable;

    private final Services services;
    private final ObservableList<Person> people = FXCollections.observableArrayList();

    public TeamAndVehicleController(Services services) {
        this.services = services;

        trafficPeopleTable
                .contextMenu(myContextMenu -> myContextMenu
                        .item("Открепить сотрудника", event -> {
                            SelectionModel<TrafficPerson> personModel = trafficPeopleTable.getSelectionModel();
                            if (personModel != null && personModel.getSelectedItem() != null) {
                                personModel.getSelectedItem().setTeam(null);
                                services.getTrafficService()
                                        .saveTrafficPerson(personModel.getSelectedItem())
                                        .onErrorResume(this::error)
                                        .doOnSuccess(tp -> {
                                            trafficPeopleTable.getItems().remove(personModel.getSelectedItem());
                                            personModel.getSelectedItem().setTeam(tp.getTeam());
                                        })
                                        .subscribe();
                            }
                        }))
                .addColumn(parentColumn ->
                        parentColumn.header("Водители экипажа")
                                .childColumn(col -> col
                                        .header("Таб.№")
                                        .cellValueFactory(p -> p.getTransientPerson().getPersonnelNumber()))
                                .childColumn(col -> col
                                        .header("Фамилия")
                                        .cellValueFactory(p -> p.getTransientPerson().getLastName()))
                                .childColumn(col -> col
                                        .header("Имя")
                                        .cellValueFactory(p -> p.getTransientPerson().getFirstName()))
                                .childColumn(col -> col
                                        .header("Отчество")
                                        .cellValueFactory(p -> p.getTransientPerson().getMiddleName())));
        teamTable
                .contextMenu(cm -> cm
                        .item("Добавить экипаж", event -> {
                            teamTable.getItems().add(new TrafficTeam());
                            teamTable.getSelectionModel().selectLast();
                        })
                        .item("Удалить экипаж", event -> {
                            SelectionModel<TrafficTeam> model = teamTable.getSelectionModel();
                            if (model != null && model.getSelectedItem() != null) {
                                services.getTrafficService().deleteTrafficTeam(model.getSelectedItem())
                                        .onErrorResume(this::error)
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
                                    saveTrafficVehicle(TrafficVehicle::setTeam).accept(vehicleModel.getSelectedItem(), teamModel.getSelectedItem());
                                }
                            }
                        }))
                .addColumn(parentColumn ->
                        parentColumn.header("Экипажи")
                                .childColumn(col -> col
                                        .header("id экипажа")
                                        .cellValueFactory(TrafficTeam::getId)
                                        .property(TableColumn::visibleProperty, false))
                                .<String>childColumn(col -> col
                                        .header("№ экипажа")
                                        .cellValueFactory(TrafficTeam::getNumber)
                                        .editable(saveTrafficTeam(TrafficTeam::setNumber))
                                        .cellFactory(TextFieldTableCell.forTableColumn()))
                                .<String>childColumn(col -> col
                                        .header("Режим работы")
                                        .cellValueFactory(TrafficTeam::getWorkingMode)
                                        .editable(saveTrafficTeam(TrafficTeam::setWorkingMode))
                                        .cellFactory(TextFieldTableCell.forTableColumn())));

        vehicleTable
                .contextMenu(cm -> cm
                        .item("Добавить ТС", event -> {
                            vehicleTable.getItems().add(new TrafficVehicle());
                            vehicleTable.getSelectionModel().selectLast();
                        })
                        .item("Удалить ТС", event -> {
                            SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                            if (model != null && model.getSelectedItem() != null) {
                                services.getTrafficService().deleteTrafficVehicle(model.getSelectedItem())
                                        .onErrorResume(this::error)
                                        .doOnSuccess(b -> vehicleTable.getItems().remove(model.getSelectedItem()))
                                        .subscribe();
                            }
                        })
                        .item("Открепить экипаж", event -> {
                            SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                            if (model != null && model.getSelectedItem() != null) {
                                saveTrafficVehicle(TrafficVehicle::setTeam).accept(model.getSelectedItem(), null);
                            }
                        }))
                .addColumn(parentColumn ->
                        parentColumn.header("Транспортные стредства")
                                .childColumn(col -> col
                                        .header("id")
                                        .cellValueFactory(TrafficVehicle::getId)
                                        .property(TableColumn::visibleProperty, false))
                                .<Integer>childColumn(col -> col
                                        .header("№ ТС")
                                        .cellValueFactory(TrafficVehicle::getNumber)
                                        .editable(saveTrafficVehicle(TrafficVehicle::setNumber))
                                        .cellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())))
                                .<String>childColumn(col -> col
                                        .header("Модель ТС")
                                        .cellValueFactory(TrafficVehicle::getModel)
                                        .editable(saveTrafficVehicle(TrafficVehicle::setModel))
                                        .cellFactory(TextFieldTableCell.forTableColumn()))
                                .childColumn(col -> col
                                        .header("id экипажа")
                                        .cellValueFactory(tv -> tv.getTeam().getId())
                                        .property(TableColumn::visibleProperty, false))
                                .<String>childColumn(col -> col
                                        .header("№ экипажа")
                                        .cellValueFactory(tv -> tv.getTeam().getNumber()))
                                .<String>childColumn(col -> col
                                        .header("Режим работы")
                                        .cellValueFactory(tv -> tv.getTeam().getWorkingMode())));

        peopleTable
                .items(people)
                .searchBox(searchField.textProperty(), person -> {
                    if (searchField.getText() == null || searchField.getText().isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = searchField.getText().toLowerCase();
                    if (person == null)
                        return false;
                    return person.getPersonnelNumber().toLowerCase().contains(lowerCaseFilter)
                            || person.getLastName().toLowerCase().contains(lowerCaseFilter)
                            || person.getFirstName().toLowerCase().contains(lowerCaseFilter)
                            || person.getMiddleName().toLowerCase().contains(lowerCaseFilter)
                            || person.getDepartment().getName().toLowerCase().contains(lowerCaseFilter)
                            || person.getPosition().getName().toLowerCase().contains(lowerCaseFilter);
                })
                .contextMenu(cm -> cm
                        .item("Закрепить сотрудника", event -> {
                            SelectionModel<TrafficTeam> teamModel = teamTable.getSelectionModel();
                            if (teamModel != null && teamModel.getSelectedItem() != null) {
                                SelectionModel<Person> personModel = peopleTable.getSelectionModel();
                                if (personModel != null && personModel.getSelectedItem() != null) {
                                    TrafficPerson person = new TrafficPerson();
                                    person.setTransientPerson(personModel.getSelectedItem());
                                    person.setTeam(teamModel.getSelectedItem());
                                    services.getTrafficService()
                                            .saveTrafficPerson(person)
                                            .onErrorResume(this::error)
                                            .doOnSuccess(tt -> {
                                                trafficPeopleTable.getItems().add(tt);
                                                trafficPeopleTable.refresh();
                                            })
                                            .subscribe();
                                }
                            }
                        }))
                .addColumn(parentColumn ->
                        parentColumn.header("Сотрудники")
                                .childColumn(col -> col.header("id").cellValueFactory(Person::getId)
                                        .property(TableColumn::visibleProperty, false))
                                .childColumn(col -> col.header("Таб.№").cellValueFactory(Person::getPersonnelNumber))
                                .childColumn(col -> col.header("Фамилия").cellValueFactory(Person::getLastName))
                                .childColumn(col -> col.header("Имя").cellValueFactory(Person::getFirstName))
                                .childColumn(col -> col.header("Отчество").cellValueFactory(Person::getMiddleName))
                                .childColumn(col -> col.header("Подразделение").cellValueFactory(p -> p.getDepartment().getName())
                                        .property(TableColumn::visibleProperty, false))
                                .childColumn(col -> col.header("Должность").cellValueFactory(p -> p.getPosition().getName())
                                        .property(TableColumn::visibleProperty, false)));

        vehicleTable.setRowFactory(tv -> {
            TableRow<TrafficVehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    trafficPeopleTable.getItems().clear();
                    if (row.getItem().getTeam() != null) {
                        services.getTrafficService()
                                .getPeopleByTeam(row.getItem().getTeam())
                                .doOnCancel(() -> trafficPeopleTable.refresh())
                                .map(ti -> {
                                    if (!people.isEmpty() && ti.getTransientPerson() == null) {
                                        Person person = people.stream().filter(p -> p.getId().equals(ti.getPerson())).findFirst().orElse(null);
                                        ti.setTransientPerson(person);
                                    }
                                    return ti;
                                })
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
                            .map(ti -> {
                                if (!people.isEmpty() && ti.getTransientPerson() == null) {
                                    Person person = people.stream().filter(p -> p.getId().equals(ti.getPerson())).findFirst().orElse(null);
                                    ti.setTransientPerson(person);
                                }
                                return ti;
                            })
                            .subscribe(trafficPeopleTable.getItems()::add);
                }
            });
            return row;
        });
    }


    private <T> BiConsumer<TrafficTeam, T> saveTrafficTeam(BiConsumer<TrafficTeam, T> consumer) {
        return consumer.andThen((trafficTeam, t) ->
                services.getTrafficService().saveTrafficTeam(trafficTeam)
                        .onErrorResume(this::error)
                        .doOnSuccess(tt -> {
                            trafficTeam.setId(tt.getId());
                            trafficTeam.setWorkingMode(tt.getWorkingMode());
                            trafficTeam.setNumber(tt.getNumber());
                            teamTable.refresh();
                        })
                        .subscribe());

    }

    private <T> BiConsumer<TrafficVehicle, T> saveTrafficVehicle(BiConsumer<TrafficVehicle, T> consumer) {
        return consumer.andThen((trafficVehicle, t) ->
                services.getTrafficService()
                        .saveTrafficVehicle(trafficVehicle)
                        .onErrorResume(this::error)
                        .doOnSuccess(tv -> {
                            trafficVehicle.setId(tv.getId());
                            trafficVehicle.setNumber(tv.getNumber());
                            trafficVehicle.setModel(tv.getModel());
                            trafficVehicle.setTeam(tv.getTeam());
                            vehicleTable.refresh();
                        })
                        .subscribe());
    }

    private <t> Mono<t> error(Throwable throwable) {
        Platform.runLater(() -> createAlert("Ошибка", "Не удалось удалить элемент\n" + throwable.getLocalizedMessage()));
        return Mono.empty();
    }

    @Override
    public void clearData() {
        trafficPeopleTable.getItems().clear();
        teamTable.getItems().clear();
        vehicleTable.getItems().clear();
        people.clear();
    }

    @Override
    public void fillData() {
        services.getPersonService().getPeople().subscribe(people::add);
        services.getTrafficService().getTrafficVehicle().subscribe(vehicleTable.getItems()::add);
        services.getTrafficService().getAllTrafficTeam().subscribe(teamTable.getItems()::add);
    }
}
