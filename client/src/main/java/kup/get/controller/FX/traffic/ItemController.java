package kup.get.controller.FX.traffic;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.MyContextMenu;
import kup.get.config.MyTable;
import kup.get.controller.socket.SocketService;
import kup.get.model.alfa.Person;
import kup.get.model.traffic.*;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

@FxmlLoader(path = "/fxml/traffic/items.fxml")
public class ItemController extends MyAnchorPane {
    @FXML
    private Button excelButton;

    @FXML
    private MyTable<TrafficItem> itemTable;

    @FXML
    private ComboBox<Owner> ownerComboBox;

    @FXML
    private DatePicker finishDatePicker;
    @FXML
    private DatePicker startDatePricker;
    @FXML
    private ComboBox<TrafficItemType> filterItemTypeComboBox;

    @FXML
    private GridPane addItemPane;
    @FXML
    private GridPane itemsPane;

    @FXML
    private TextArea commentField;
    @FXML
    private ComboBox<TrafficItemType> itemTypeComboBox;
    @FXML
    private DatePicker dateStartField;
    @FXML
    private DatePicker dateFinishField;
    @FXML
    private Button addButton;
    @FXML
    private Button canselButton;
    @FXML
    private TextField searchPerson;
    @FXML
    private MyTable<Person> peopleTable;
    @FXML
    private MyTable<TrafficTeam> teamTable;
    @FXML
    private MyTable<TrafficVehicle> vehicleTable;

    private final SocketService socketService;
    private ObservableList<Person> people = FXCollections.observableArrayList();
    private final AtomicReference<SequentialTransition> transition;
    private final String nameItemColumn = "Предметы";

    public ItemController(SocketService socketService, AtomicReference<SequentialTransition> sequentialTransition) {
        this.socketService = socketService;
        this.transition = sequentialTransition;
        ownerComboBox.setItems(FXCollections.observableArrayList(Owner.values()));

        itemTable
                .headerColumn(nameItemColumn)
                    .column("id", TrafficItem::getId).setInvisible().build()
                    .column("Наименование", ti -> ti.getType().getName()).build()
                    .column("Описание", TrafficItem::getDescription).build()
                    .column("Начало периода", TrafficItem::getDateStart).build()
                    .column("Конец периода", TrafficItem::getDateFinish).build()
                .and()
                .headerColumn(Owner.PERSON.title).setInvisible()
                    .column("Таб. №", ti -> parsePerson(Person::getPersonnelNumber, ti.getPerson())).setInvisible().build()
                    .column("Фамилия", ti -> parsePerson(Person::getLastName, ti.getPerson())).setInvisible().build()
                    .column("Имя", ti -> parsePerson(Person::getFirstName, ti.getPerson())).setInvisible().build()
                    .column("Отчество", ti -> parsePerson(Person::getMiddleName, ti.getPerson())).setInvisible().build()
                .and()
                .headerColumn(Owner.TEAM.title).setInvisible()
                    .column("Номер экипажа", ti -> ti.getTeam().getNumber()).setInvisible().build()
                    .column("Режим работы", ti -> ti.getTeam().getWorkingMode()).setInvisible().build()
                .and()
                .headerColumn(Owner.VEHICLE.title).setInvisible()
                    .column("Номер ТС", ti -> ti.getVehicle().getNumber()).setInvisible().build()
                    .column("Модель ТС", ti -> ti.getVehicle().getModel()).setInvisible().build();

        itemTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                itemTable.setContextMenu(itemTableContextMenu());
            }
        });

        ownerComboBox.setOnAction(event -> {
            Owner owner = ownerComboBox.getSelectionModel().getSelectedItem();
            if (owner.equals(Owner.All)) {
                itemTable.getColumns().forEach(column -> column.setVisible(true));
            } else {
                itemTable.getColumns().forEach(column -> {
                    System.out.println("text: "+column.getText() + " check:" + (column.getText().equals(owner.title) || column.getText().equals(nameItemColumn)));
                    column.setVisible(column.getText().equals(owner.title) || column.getText().equals(nameItemColumn));
                });
            }
        });
        canselButton.setOnAction(event -> switchPane(addItemPane, itemsPane));
        peopleTable
                .headerColumn(Owner.PERSON.title)
                .column("Таб. №", Person::getPersonnelNumber).build()
                .column("Фамилия", Person::getLastName).build()
                .column("Имя", Person::getFirstName).build()
                .column("Отчество", Person::getMiddleName).build();
        teamTable.headerColumn(Owner.TEAM.title)
                .column("Номер экипажа", TrafficTeam::getNumber).build()
                .column("Режим работы", TrafficTeam::getWorkingMode).build()
                .column("Номер ТС", tt -> tt.getVehicle().getNumber()).build()
                .column("Модель ТС", tt -> tt.getVehicle().getModel()).build();
        vehicleTable
                .headerColumn(Owner.VEHICLE.title)
                .column("Номер ТС", TrafficVehicle::getNumber).build()
                .column("Модель ТС", TrafficVehicle::getModel).build()
                .column("Номер экипажа", tv -> tv.getTeam().getNumber()).build()
                .column("Режим работы", tv -> tv.getTeam().getWorkingMode()).build();
    }

    private ContextMenu itemTableContextMenu() {
        return MyContextMenu.builder()
                .menu("Добавить")
                .menuItem("По водителю", event -> {
                    peopleTable.setVisible(true);
                    teamTable.setVisible(false);
                    vehicleTable.setVisible(false);
                    switchPane(itemsPane, addItemPane);
                })
                .menuItem("По экипажу", event -> {
                    peopleTable.setVisible(false);
                    teamTable.setVisible(true);
                    vehicleTable.setVisible(false);
                    teamTable.getItems().clear();
                    socketService.getTrafficTeam()
                            .doOnComplete(() -> teamTable.refresh())
                            .subscribe(teamTable.getItems()::add);
                    switchPane(itemsPane, addItemPane);
                })
                .menuItem("По ТС", event -> {
                    peopleTable.setVisible(false);
                    teamTable.setVisible(false);
                    vehicleTable.setVisible(true);
                    vehicleTable.getItems().clear();
                    socketService.getTrafficVehicle()
                            .doOnComplete(() -> vehicleTable.refresh())
                            .subscribe(vehicleTable.getItems()::add);
                    switchPane(itemsPane, addItemPane);
                })
                .build()
                .item("", event -> {
                });
    }

    @AllArgsConstructor
    private enum Owner {
        All("Все"),
        PERSON("Водители"),
        TEAM("Экипажи"),
        VEHICLE("Траспортные средства");

        private final String title;

        @Override
        public String toString() {
            return title;
        }
    }

    private void switchPane(Pane disappearancePane, Pane appearancePane) {
        if (transition.get() == null || transition.get().getStatus().equals(Animation.Status.STOPPED)) {
            transition.set(createTransition(disappearancePane, appearancePane));
            transition.get().play();
        }
    }

    private String parsePerson(Function<Person, String> function, TrafficPerson p) {
        try {
            return people.stream()
                    .filter(person -> person.getId().equals(p.getPersonnelNumber()))
                    .findFirst()
                    .map(function)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void fillInTheTables() {
        itemTable.getItems().clear();
        itemTypeComboBox.getItems().clear();
        filterItemTypeComboBox.getItems().clear();

        socketService.getTrafficItem().subscribe(itemTable.getItems()::add);
        socketService.getItemsType().subscribe(type -> {
            itemTypeComboBox.getItems().add(type);
            filterItemTypeComboBox.getItems().add(type);
        });

        people = FXCollections.observableArrayList(socketService.getPeople());
        peopleTable.setItems(people);
    }

    @PostConstruct
    public void test() {
        socketService.authorize("sanya", "1101")
                .onErrorResume(s -> Mono.just(s.getMessage()))      //  LOG
                .doOnComplete(socketService::updatePeople).subscribe(System.out::println);
    }
}
