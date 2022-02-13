package kup.get.controller.FX;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import kup.get.config.DateEditingCell;
import kup.get.config.MyAnchorPane;
import kup.get.controller.socket.SocketService;
import kup.get.model.Item;
import kup.get.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

//@FxmlLoader(path = "/fxml/briefing.fxml")
public class BriefingController extends MyAnchorPane {

    @FXML
    private AnchorPane briefingPane;

    @FXML
    private TableView<Item> briefingTable;
    @FXML
    private TextField searchBriefing;
    @FXML
    private Button excelButton;

    @FXML
    private AnchorPane addBriefingPage;

    @FXML
    private TextArea commentField;
    @FXML
    private DatePicker dateStartField;
    @FXML
    private DatePicker dateFinishField;
    @FXML
    private Button addButton;
    @FXML
    private TextField searchPerson;
    @FXML
    private TableView<Person> peopleTable;


    private SequentialTransition transition;
    private final SocketService socketService;

    public BriefingController(SocketService socketService) {
        this.socketService = socketService;

        ObservableList<Item> briefingList = FXCollections.observableArrayList();
        briefingTable.setItems(briefingList);
        briefingTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<Person> personList = FXCollections.observableArrayList();
        peopleTable.setItems(personList);

        TableColumn<Item, LocalDate> dateStartColumn = column("Дата последнего\nинструктажа", Item::getDateStart);
        TableColumn<Item, LocalDate> dateFinishColumn = column("Дата необходимого\nинструктажа", Item::getDateFinish);

        dateStartColumn.setCellFactory(p -> new DateEditingCell<>());
        dateFinishColumn.setCellFactory(p -> new DateEditingCell<>());

        briefingTable.getColumns().addAll(
                column("Таб.№", item -> item.getPerson().getPersonnelNumber()),
                column("Фамилия", item -> item.getPerson().getLastName()),
                column("Имя", item -> item.getPerson().getFirstName()),
                column("Отчество", item -> item.getPerson().getMiddleName()),
                column("Комментарий", Item::getDescription),
                dateStartColumn,
                dateFinishColumn);


        peopleTable.getColumns().addAll(
                invisibleColumn("id", Person::getId),
                column("Таб.№", Person::getPersonnelNumber),
                column("Фамилия", Person::getLastName),
                column("Имя", Person::getFirstName),
                column("Отчество", Person::getMiddleName),
                invisibleColumn("Подразделение", person -> person.getDepartment().getName()),
                invisibleColumn("Должность", person -> person.getPosition().getName()));
        ContextMenu cm = createContextMenu();
        briefingTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                briefingTable.setContextMenu(cm);
            }
        });

        addButton.setOnAction(event -> {
            /*if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
                transition = createTransition(addBriefingPage, briefingPane);
                transition.play();
            }*/
            fillInTheTables();
        });
        dateStartField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            dateFinishField.setValue(LocalDate.parse(newValue, formatter).plusMonths(3));
        });
    }

    private ContextMenu createContextMenu() {
        ContextMenu cm = new ContextMenu();

        MenuItem closeKeyMenu = new MenuItem("Добавить инструктаж");
        closeKeyMenu.setOnAction(e -> {
            if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
                transition = createTransition(briefingPane, addBriefingPage);
                transition.play();
            }
        });

        cm.getItems().add(closeKeyMenu);
        return cm;
    }

    private SequentialTransition createTransition(AnchorPane disappearancePane, AnchorPane appearancePane) {
        return new SequentialTransition(
                new Timeline(
                        new KeyFrame(Duration.millis(500),
                                new KeyValue(disappearancePane.opacityProperty(), 0))),
                new Timeline(
                        new KeyFrame(Duration.millis(1),
                                new KeyValue(disappearancePane.visibleProperty(), false),
                                new KeyValue(appearancePane.visibleProperty(), true))),
                new Timeline(new KeyFrame(Duration.millis(800),
                        new KeyValue(appearancePane.opacityProperty(), 1))));
    }

    private void fillInTheTables() {
        socketService.getBriefing(LocalDate.now())
                .doOnNext(item -> briefingTable.getItems().add(item))
                .subscribe();
        socketService.getPeople().doOnNext(person -> peopleTable.getItems().add(person)).subscribe();
//        peopleTable.getItems().add(person)
    }
}
