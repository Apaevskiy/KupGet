package kup.get.controller.FX.traffic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.MyContextMenu;
import kup.get.config.MyTable;
import kup.get.controller.socket.SocketService;
import kup.get.model.alfa.Person;
import kup.get.model.traffic.TrafficPerson;
import kup.get.model.traffic.TrafficTeam;
import kup.get.model.traffic.TrafficVehicle;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

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


    private final SocketService socketService;
    private final ObservableList<Person> people = FXCollections.observableArrayList();

    public TeamAndVehicleController(SocketService socketService) {
        this.socketService = socketService;
        peopleTable.setItems(people);
        Function<Long, Optional<Person>> function = p -> people.stream().filter(person -> person.getId().equals(p)).findFirst();
        trafficPeopleTable
                .headerColumn("Водители экипажа")
                .column("Таб.№", p -> {
                    if (p != null) {
                        Optional<Person> optional = function.apply(p.getPersonnelNumber());
                        return optional.isPresent() ? optional.get().getPersonnelNumber() : "";
                    } else return "";
                })
                .column("Фамилия", p -> {
                    if (p != null) {
                        Optional<Person> optional = people.stream().filter(person -> person.getId().equals(p.getPersonnelNumber())).findFirst();
                        return optional.isPresent() ? optional.get().getLastName() : "";
                    } else return "";
                })
                .column("Имя", p -> {
                    if (p != null) {
                        Optional<Person> optional = people.stream().filter(person -> person.getId().equals(p.getPersonnelNumber())).findFirst();
                        return optional.isPresent() ? optional.get().getFirstName() : "";
                    } else return "";
                })
                .column("Отчество", p -> {
                    if (p != null) {
                        Optional<Person> optional = people.stream().filter(person -> person.getId().equals(p.getPersonnelNumber())).findFirst();
                        return optional.isPresent() ? optional.get().getMiddleName() : "";
                    } else return "";
                });

        teamTable
                .headerColumn("Экипажи")
                .invisibleColumn("id экипажа", TrafficTeam::getId)
                .editableColumn("№ экипажа", TrafficTeam::getNumber, (tt, value) -> saveTrafficTeam(tt, TrafficTeam::setNumber, value), TextFieldTableCell.forTableColumn())
                .editableColumn("Режим работы", TrafficTeam::getWorkingMode, (tt, value) -> saveTrafficTeam(tt, TrafficTeam::setWorkingMode, value), TextFieldTableCell.forTableColumn());

        vehicleTable
                .headerColumn("Транспортные стредства")
                .invisibleColumn("id ТС", TrafficVehicle::getId)
                .editableColumn("№ трол-са", TrafficVehicle::getNumber, (tv, value) -> saveTrafficVehicle(tv, TrafficVehicle::setNumber, value), TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                .editableColumn("Марка трол-са", TrafficVehicle::getModel, (tv, value) -> saveTrafficVehicle(tv, TrafficVehicle::setModel, value), TextFieldTableCell.forTableColumn())
                .invisibleColumn("id экипажа", tv -> tv.getTeam() != null ? tv.getTeam().getId() : null)
                .column("№ экипажа", tv -> tv.getTeam() != null ? tv.getTeam().getNumber() : null)
                .column("Режим работы", tv -> tv.getTeam() != null ? tv.getTeam().getWorkingMode() : null);


        peopleTable
                .headerColumn("Сотрудники")
                .invisibleColumn("id", Person::getId)
                .column("Таб.№", Person::getPersonnelNumber)
                .column("Фамилия", Person::getLastName)
                .column("Имя", Person::getFirstName)
                .column("Отчество", Person::getMiddleName)
                .invisibleColumn("Подразделение", person -> person.getDepartment().getName())
                .invisibleColumn("Должность", person -> person.getPosition().getName());


        vehicleTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                vehicleTable.setContextMenu(vehicleCM());
            }
        });

        peopleTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                peopleTable.setContextMenu(peopleCM());
            }
        });

        teamTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                teamTable.setContextMenu(teamCM());
            }
        });
        trafficPeopleTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                trafficPeopleTable.setContextMenu(trafficPeopleCM());
            }
        });

        vehicleTable.setRowFactory(tv -> {
            TableRow<TrafficVehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    if (row.getItem().getTeam() != null && row.getItem().getTeam().getTrafficPeople() != null) {
                        trafficPeopleTable.setItems(FXCollections.observableArrayList(row.getItem().getTeam().getTrafficPeople()));
                        trafficPeopleTable.refresh();
                    } else trafficPeopleTable.getItems().clear();
                }
            });
            return row;
        });
        teamTable.setRowFactory(tv -> {
            TableRow<TrafficTeam> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    if (row.getItem().getTrafficPeople() != null) {
                        trafficPeopleTable.setItems(FXCollections.observableArrayList(row.getItem().getTrafficPeople()));
                        trafficPeopleTable.refresh();
                    } else trafficPeopleTable.getItems().clear();
                }
            });
            return row;
        });
    }

    private <t> void saveTrafficTeam(TrafficTeam tt, BiConsumer<TrafficTeam, t> consumer, t value) {
        consumer.accept(tt, value);
        socketService
                .saveTrafficTeam(tt)
                .onErrorResume(e -> error())
                .doOnSuccess(tt::setTeam)
                .subscribe();
    }

    private <t> void saveTrafficVehicle(TrafficVehicle tv, BiConsumer<TrafficVehicle, t> consumer, t value) {
        consumer.accept(tv, value);
        socketService
                .saveTrafficVehicle(tv)
                .onErrorResume(e -> error())
                .doOnSuccess(p -> vehicleTable.refresh())
                .subscribe(tv::setVehicle);
    }

    /*private void saveTrafficPerson(TrafficPerson person) {
        socketService.saveTrafficPerson(person)
                .onErrorResume(e -> error())
                .doOnSuccess(p -> {
                    person.getTeam().getTrafficPeople().add(p);
                    trafficPeopleTable.setItems(FXCollections.observableArrayList(person.getTeam().getTrafficPeople()));
                    trafficPeopleTable.refresh();
                })
                .subscribe(person::setPerson);
    }*/

    private ContextMenu vehicleCM() {
        return MyContextMenu.builder()
                .item("Добавить ТС", event -> {
                    vehicleTable.getItems().add(TrafficVehicle.builder().build());
                    vehicleTable.getSelectionModel().selectLast();
                })
                .item("Удалить ТС", event -> {
                    SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        socketService.deleteTrafficVehicle(model.getSelectedItem())
                                .onErrorResume(e -> error())
                                .doOnSuccess(b -> vehicleTable.getItems().remove(model.getSelectedItem()))
                                .subscribe();
                    }
                })
                .item("Открепить экипаж", event -> {
                    SelectionModel<TrafficVehicle> model = vehicleTable.getSelectionModel();
                    if (model != null && model.getSelectedItem() != null) {
                        saveTrafficVehicle(model.getSelectedItem(), TrafficVehicle::setTeam, null);
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
                            person.setPersonnelNumber(personModel.getSelectedItem().getId());
                            teamModel.getSelectedItem().getTrafficPeople().add(person);
                            socketService
                                    .saveTrafficTeam(teamModel.getSelectedItem())
                                    .onErrorResume(e -> error())
                                    .doOnSuccess(tt -> {
                                        teamModel.getSelectedItem().setTeam(tt);
                                        trafficPeopleTable.setItems(FXCollections.observableArrayList(tt.getTrafficPeople()));
                                        trafficPeopleTable.refresh();
                                    })
                                    .subscribe();
                        }
                    }
                });
    }
    private ContextMenu trafficPeopleCM() {
        return MyContextMenu
                .builder()
                .item("Открепить сотрудника", event -> {
                        SelectionModel<TrafficPerson> personModel = trafficPeopleTable.getSelectionModel();
                        if (personModel != null && personModel.getSelectedItem() != null) {
                            TrafficTeam team = teamTable.getSelectionModel().getSelectedItem();
                            team.getTrafficPeople().remove(personModel.getSelectedItem());
                            socketService
                                    .saveTrafficTeam(team)
                                    .onErrorResume(e -> error())
                                    .doOnSuccess(tt -> {
                                        team.setTeam(tt);
                                        trafficPeopleTable.setItems(FXCollections.observableArrayList(tt.getTrafficPeople()));
                                        trafficPeopleTable.refresh();
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
                        socketService.deleteTrafficTeam(model.getSelectedItem())
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
                            saveTrafficVehicle(vehicleModel.getSelectedItem(), TrafficVehicle::setTeam, teamModel.getSelectedItem());
                        }
                    }
                });
    }

    private <t> Mono<t> error() {
        Platform.runLater(() -> createAlert("Ошибка", "Не удалось удалить элемент\nПри необходимости обратитесь к администратору"));
        return Mono.empty();
    }

    public void fillInTheTables() {
        trafficPeopleTable.getItems().clear();
        teamTable.getItems().clear();
        vehicleTable.getItems().clear();

        socketService.getTrafficVehicle().subscribe(vehicleTable.getItems()::add);
        socketService.getTrafficTeam().subscribe(teamTable.getItems()::add);
    }
}
