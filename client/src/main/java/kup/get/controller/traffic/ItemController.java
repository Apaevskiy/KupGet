package kup.get.controller.traffic;

import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.alfa.Person;
import kup.get.entity.traffic.TrafficItem;
import kup.get.entity.traffic.TrafficItemType;
import kup.get.entity.traffic.TrafficTeam;
import kup.get.entity.traffic.TrafficVehicle;
import kup.get.service.Services;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

@FxmlLoader(path = "/fxml/traffic/items.fxml")
public class ItemController extends MyAnchorPane {
    @FXML
    private GridPane itemsPane;

    @FXML
    private Button excelButton;
    @FXML
    private MyTable<TrafficItem> itemTable;
    @FXML
    private TextField searchItemField;
    @FXML
    private ComboBox<Owner> ownerComboBox;
    @FXML
    private DatePicker startDatePricker;
    @FXML
    private DatePicker finishDatePicker;
    @FXML
    private ComboBox<TrafficItemType> filterItemTypeComboBox;

    @FXML
    private GridPane addItemPane;

    @FXML
    private TextArea commentField;
    @FXML
    private ComboBox<TrafficItemType> itemTypeComboBox;
    @FXML
    private DatePicker dateStartField;
    @FXML
    private DatePicker dateFinishField;
    @FXML
    private Label infoLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button canselButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private MyTable<Person> peopleTable;
    @FXML
    private TextField searchPeopleField;
    @FXML
    private MyTable<TrafficTeam> teamTable;
    @FXML
    private TextField searchTeamField;
    @FXML
    private MyTable<TrafficVehicle> vehicleTable;
    @FXML
    private TextField searchVehicleField;

    private final Services services;
    private final AtomicReference<SequentialTransition> transition;
    private final String nameItemColumn = "Предметы";
    private final ObservableList<Person> people = FXCollections.observableArrayList();
    private final ObservableList<TrafficItem> items = FXCollections.observableArrayList();
    private final ObservableList<TrafficTeam> teams = FXCollections.observableArrayList();
    private final ObservableList<TrafficVehicle> vehicles = FXCollections.observableArrayList();

    public ItemController(Services services, AtomicReference<SequentialTransition> sequentialTransition) {
        this.services = services;
        this.transition = sequentialTransition;
        ownerComboBox.setItems(FXCollections.observableArrayList(Owner.values()));
        itemTable
                .items(items)
                .searchBox(searchItemField, item -> {
                    if (searchItemField.getText() == null || searchItemField.getText().isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = searchItemField.getText().toLowerCase();
                    if (item == null)
                        return false;
                    if (item.getPerson() != null && (item.getPerson().getPersonnelNumber().toLowerCase().contains(lowerCaseFilter)
                            || item.getPerson().getLastName().toLowerCase().contains(lowerCaseFilter)
                            || item.getPerson().getFirstName().toLowerCase().contains(lowerCaseFilter)
                            || item.getPerson().getMiddleName().toLowerCase().contains(lowerCaseFilter)))
                        return true;

                    return item.getType().getName().toLowerCase().contains(lowerCaseFilter)
                            || item.getDescription().toLowerCase().contains(lowerCaseFilter)
                            || item.getDateStart().toString().toLowerCase().contains(lowerCaseFilter)
                            || item.getDateFinish().toString().toLowerCase().contains(lowerCaseFilter)
                            || item.getTeam().getNumber().toLowerCase().contains(lowerCaseFilter)
                            || item.getTeam().getWorkingMode().toLowerCase().contains(lowerCaseFilter)
                            || String.valueOf(item.getVehicle().getNumber()).toLowerCase().contains(lowerCaseFilter)
                            || item.getVehicle().getModel().toLowerCase().contains(lowerCaseFilter);
                })
                .contextMenu(cm -> cm.item("Добавить", event -> switchPane(itemsPane, addItemPane)))
                .addColumn(itemColumn -> itemColumn
                        .header(nameItemColumn)
                        .childColumn(col -> col.header("id").cellValueFactory(TrafficItem::getId).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Наименование").cellValueFactory(ti -> ti.getType().getName()))
                        .childColumn(col -> col.header("Описание").cellValueFactory(TrafficItem::getDescription))
                        .childColumn(col -> col.header("Начало периода").cellValueFactory(TrafficItem::getDateStart))
                        .childColumn(col -> col.header("Конец периода").cellValueFactory(TrafficItem::getDateFinish)))
                .addColumn(itemColumn -> itemColumn
                        .header(Owner.PERSON.title)
                        .property(TableColumnBase::visibleProperty, false)
                        .childColumn(col -> col.header("Таб. №").cellValueFactory(ti -> ti.getPerson().getPersonnelNumber()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Фамилия").cellValueFactory(ti -> ti.getPerson().getLastName()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Имя").cellValueFactory(ti -> ti.getPerson().getFirstName()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Отчество").cellValueFactory(ti -> ti.getPerson().getMiddleName()).property(TableColumnBase::visibleProperty, false)))
                .addColumn(itemColumn -> itemColumn
                        .header(Owner.TEAM.title)
                        .property(TableColumnBase::visibleProperty, false)
                        .childColumn(col -> col.header("id").cellValueFactory(ti -> ti.getTeam().getId()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Номер экипажа").cellValueFactory(ti -> ti.getTeam().getNumber()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Режим работы").cellValueFactory(ti -> ti.getTeam().getWorkingMode()).property(TableColumnBase::visibleProperty, false)))
                .addColumn(itemColumn -> itemColumn
                        .header(Owner.VEHICLE.title)
                        .property(TableColumnBase::visibleProperty, false)
                        .childColumn(col -> col.header("id").cellValueFactory(ti -> ti.getVehicle().getId()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Номер ТС").cellValueFactory(ti -> ti.getVehicle().getNumber()).property(TableColumnBase::visibleProperty, false))
                        .childColumn(col -> col.header("Модель ТС").cellValueFactory(ti -> ti.getVehicle().getModel()).property(TableColumnBase::visibleProperty, false)));

        ownerComboBox.setOnAction(event -> {
            Owner owner = ownerComboBox.getSelectionModel().getSelectedItem();
            if (owner.equals(Owner.All)) {
                itemTable.getColumns().forEach(column -> column.setVisible(true));
            } else {
                itemTable.getColumns().forEach(column -> column.setVisible(column.getText().equals(owner.title) || column.getText().equals(nameItemColumn)));
            }
        });

        canselButton.setOnAction(event -> switchPane(addItemPane, itemsPane));

        dateStartField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (itemTypeComboBox.getValue() != null && !newValue.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                dateFinishField.setValue(LocalDate.parse(newValue, formatter).plusMonths(itemTypeComboBox.getValue().getDefaultDurationInMonth()));
            }
        });

        peopleTable
                .items(people)
                .searchBox(searchPeopleField, person -> {
                    if (searchPeopleField.getText() == null || searchPeopleField.getText().isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = searchPeopleField.getText().toLowerCase();
                    if (person == null)
                        return false;
                    return person.getPersonnelNumber().toLowerCase().contains(lowerCaseFilter)
                            || person.getLastName().toLowerCase().contains(lowerCaseFilter)
                            || person.getFirstName().toLowerCase().contains(lowerCaseFilter)
                            || person.getMiddleName().toLowerCase().contains(lowerCaseFilter);
                })
                .addColumn(col -> col.header("Таб. №").cellValueFactory(Person::getPersonnelNumber))
                .addColumn(col -> col.header("Фамилия").cellValueFactory(Person::getLastName))
                .addColumn(col -> col.header("Имя").cellValueFactory(Person::getFirstName))
                .addColumn(col -> col.header("Отчество").cellValueFactory(Person::getMiddleName));
        teamTable
                .items(teams)
                .searchBox(searchTeamField, team -> {
                    if (searchTeamField.getText() == null || searchTeamField.getText().isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = searchTeamField.getText().toLowerCase();
                    if (team == null)
                        return false;
                    return team.getNumber().toLowerCase().contains(lowerCaseFilter) || team.getWorkingMode().toLowerCase().contains(lowerCaseFilter);
                })
                .addColumn(col -> col.header("id").cellValueFactory(TrafficTeam::getId).property(TableColumnBase::visibleProperty, false))
                .addColumn(col -> col.header("Номер экипажа").cellValueFactory(TrafficTeam::getNumber))
                .addColumn(col -> col.header("Режим работы").cellValueFactory(TrafficTeam::getWorkingMode));
        vehicleTable
                .items(vehicles)
                .searchBox(searchVehicleField, team -> {
                    if (searchVehicleField.getText() == null || searchVehicleField.getText().isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = searchVehicleField.getText().toLowerCase();
                    if (team == null)
                        return false;
                    return String.valueOf(team.getNumber()).toLowerCase().contains(lowerCaseFilter)
                            || team.getModel().toLowerCase().contains(lowerCaseFilter)
                            || team.getTeam().getNumber().toLowerCase().contains(lowerCaseFilter)
                            || team.getTeam().getWorkingMode().toLowerCase().contains(lowerCaseFilter);
                })
                .addColumn(col -> col.header("id").cellValueFactory(TrafficVehicle::getId).property(TableColumnBase::visibleProperty, false))
                .addColumn(col -> col.header("Номер ТС").cellValueFactory(TrafficVehicle::getNumber))
                .addColumn(col -> col.header("Модель ТС").cellValueFactory(TrafficVehicle::getModel))
                .addColumn(col -> col.header("Номер экипажа").cellValueFactory(tv -> tv.getTeam().getNumber()))
                .addColumn(col -> col.header("Режим работы").cellValueFactory(tv -> tv.getTeam().getWorkingMode()));

        addButton.setOnAction(event -> {
            SelectionModel<?> model = null;
            infoLabel.setStyle("-fx-text-fill: red");
            TrafficItem item = new TrafficItem();
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Водители")) {
                model = peopleTable.getSelectionModel();
                item.setPerson((Person) model.getSelectedItem());
            } else if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Экипажи")) {
                model = teamTable.getSelectionModel();
                item.setTeam((TrafficTeam) model.getSelectedItem());
            } else if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Транспортные средства")) {
                model = vehicleTable.getSelectionModel();
                item.setVehicle((TrafficVehicle) model.getSelectedItem());
            }
            if (model != null && model.getSelectedItem() != null) {
                if (!itemTypeComboBox.getSelectionModel().isEmpty()) {
                    if (!commentField.getText().isEmpty()) {
                        if (dateStartField.getValue() != null && dateFinishField.getValue() != null) {
                            if (dateFinishField.getValue().isAfter(dateStartField.getValue())) {
                                item.setType(itemTypeComboBox.getValue());
                                item.setDescription(commentField.getText());
                                item.setDateStart(dateStartField.getValue());
                                item.setDateFinish(dateFinishField.getValue());
                                services.getTrafficService().saveTrafficItem(item)
                                        .doOnSuccess(tt -> {
                                            item.setId(tt.getId());
                                            items.add(item);
                                            Platform.runLater(() -> {
                                                infoLabel.setStyle("-fx-text-fill: green");
                                                infoLabel.setText("Успешно");
                                                commentField.clear();
                                                dateStartField.setValue(null);
                                                dateFinishField.setValue(null);
                                                itemTypeComboBox.getSelectionModel().clearSelection();
                                            });
                                        })
                                        .doOnError(throwable -> Platform.runLater(() -> infoLabel.setText("Ошибка " + throwable.getMessage())))
                                        .subscribe();
                            } else infoLabel.setText("Дата корректность введённых данных");
                        } else infoLabel.setText("Выберите даты");
                    } else infoLabel.setText("Введите комментарий");
                } else infoLabel.setText("Выберите пункт выпадающего списка");
            } else infoLabel.setText("Выберите элемент из таблицы");
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
        transition.set(switchPaneTransition(disappearancePane, appearancePane));
        transition.get().play();
    }

    @Override
    public void clearData() {
        itemTable.getItems().clear();
        itemTypeComboBox.getItems().clear();
        filterItemTypeComboBox.getItems().clear();
        people.clear();
        teams.clear();
        vehicles.clear();
    }

    @Override
    public void fillData() {
        people.addAll(services.getPersonService().getPeople());
        services.getTrafficService().getTrafficItems()
                .map(ti -> {
                    if(!people.isEmpty() && ti.getTrafficPerson()!=null && ti.getPerson()==null){
                        System.out.println(ti);
                        Person person = people.stream().filter(p -> p.getId().equals(ti.getTrafficPerson().getPerson())).findFirst().orElse(null);
                        System.out.println(person);
                        ti.setPerson(person);
                    }
                    return ti;
                }).subscribe(items::add);
        System.out.println(people.stream().filter(p -> p.getId()==15L).findFirst());
        System.out.println(people.stream().filter(p -> p.getId()==8L).findFirst());
        services.getTrafficService().getItemsType().subscribe(type -> {
            itemTypeComboBox.getItems().add(type);
            filterItemTypeComboBox.getItems().add(type);
        });




        services.getTrafficService().getAllTrafficTeam()
                .doOnComplete(() -> teamTable.refresh())
                .subscribe(teams::add);
        services.getTrafficService().getTrafficVehicle()
                .doOnComplete(() -> vehicleTable.refresh())
                .subscribe(vehicles::add);
    }
}
