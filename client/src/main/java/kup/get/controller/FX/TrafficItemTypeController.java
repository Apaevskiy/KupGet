package kup.get.controller.FX;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.MyAnchorPane;
import kup.get.controller.socket.SocketService;
import kup.get.model.TrafficItemType;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

//@FxmlLoader(path = "/fxml/itemType.fxml")
public class TrafficItemTypeController extends MyAnchorPane {

    @FXML
    private TableView<TrafficItemType> itemTypeTable;
    @FXML
    private TableColumn<TrafficItemType, Long> idColumn;
    @FXML
    private TableColumn<TrafficItemType, CheckBox> statusColumn;
    @FXML
    private TableColumn<TrafficItemType, String> nameColumn;
    @FXML
    private TableColumn<TrafficItemType, Integer> resetAfterColumn;

    @FXML
    private TextField searchItemTypeField;
    @FXML
    private Button saveButton;

    private final SocketService socketService;

    public TrafficItemTypeController(SocketService socketService) {
        this.socketService = socketService;
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        resetAfterColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDefaultDurationInMonth()));
        statusColumn.setCellValueFactory(cell -> {
            TrafficItemType type = cell.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(type.isStatus());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                type.setStatus(new_val);
                type.setChanged(true);
            });
            return new SimpleObjectProperty<>(checkBox);
        });
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        resetAfterColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        onEditCommit(nameColumn, (type, value) -> {
            type.setName(value);
            type.setChanged(true);
        });
        onEditCommit(resetAfterColumn, (type, value) -> {
            type.setDefaultDurationInMonth(value);
            type.setChanged(true);
        });

        itemTypeTable.setItems(FXCollections.observableArrayList());

        saveButton.setOnAction(event -> {
            List<TrafficItemType> list = itemTypeTable
                    .getItems()
                    .stream()
                    .filter(TrafficItemType::isChanged)
                    .collect(Collectors.toList());
            System.out.println("\\/ ________________________");
            System.out.println(itemTypeTable.getItems());
            System.out.println(list);
            saveTypes(Boolean.TRUE.equals(socketService.saveItemsTypes(list).blockFirst()));
            itemTypeTable.getSortOrder().add(idColumn);
        });

        ContextMenu cm = createContextMenu();
        itemTypeTable.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                itemTypeTable.setContextMenu(cm);
            }
        });
    }

    private void saveTypes(boolean e) {
        updateTable();
        if (e) createAlert(
                "Уведомление",
                "Данные упешно сохранены");
        else createAlert("Ошибка",
                "Не все данные были сохранены.\n" +
                        "Проверьте введённые данные");
    }

    private ContextMenu createContextMenu() {
        ContextMenu cm = new ContextMenu();

        MenuItem addMenuItem = new MenuItem("Добавить");
        addMenuItem.setOnAction(e -> {
            if (itemTypeTable.getItems().size() == 0 || itemTypeTable.getItems().get(itemTypeTable.getItems().size() - 1).isNotEmpty()) {
                itemTypeTable.getItems().add(new TrafficItemType());
                itemTypeTable.getSelectionModel().selectLast();
            } else {
                itemTypeTable.getSelectionModel().selectLast();
            }
        });

        cm.getItems().add(addMenuItem);
        return cm;
    }

    @PostConstruct
    public void updateTable() {
        saveButton.setDisable(true);
        itemTypeTable.getItems().clear();
        socketService.getItemsType()
                .doOnNext(item -> {
                    itemTypeTable.getItems().add(item);
                })
                .doOnComplete(() -> {
                    saveButton.setDisable(false);
                    itemTypeTable.refresh();
                })
                .subscribe();
    }
}
