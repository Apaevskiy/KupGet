package kup.get.controller.traffic;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.traffic.TrafficItemType;
import kup.get.service.Services;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;

@FxmlLoader(path = "/fxml/traffic/itemType.fxml")
public class TrafficItemTypeController extends MyAnchorPane {

    @FXML
    private MyTable<TrafficItemType> itemTypeTable;
    @FXML
    private TextField searchItemTypeField;

    private final Services services;

    public TrafficItemTypeController(Services services) {
        this.services = services;
        itemTypeTable
                .contextMenu(myContextMenu ->
                        myContextMenu.item("Добавить", e -> {
                            if (itemTypeTable.getItems().size() == 0 || itemTypeTable.getItems().get(itemTypeTable.getItems().size() - 1).getId() != null) {
                                itemTypeTable.getItems().add(new TrafficItemType());
                                itemTypeTable.getSelectionModel().selectLast();
                            } else {
                                itemTypeTable.getSelectionModel().selectLast();
                            }
                        }))
                .addColumn(parentColumn ->
                        parentColumn.header("Перечень пунктов для ОТ")
                                .childColumn(col -> col
                                        .header("id")
                                        .cellValueFactory(TrafficItemType::getId)
                                        .property(TableColumn::visibleProperty, false))
                                .<Boolean>childColumn(col -> col
                                        .header("Статус")
                                        .property(TableColumn::cellValueFactoryProperty, cellData -> {
                                            BooleanProperty property = new SimpleBooleanProperty(cellData.getValue().isStatus());
                                            property.addListener((observable, oldValue, newValue) -> saveTrafficType(TrafficItemType::setStatus).accept(cellData.getValue(), newValue));
                                            return property;
                                        })
                                        .cellFactory(CheckBoxTableCell.forTableColumn(col))
                                        .widthColumn(60))
                                .<String>childColumn(col -> col
                                        .header("Наименование")
                                        .cellValueFactory(TrafficItemType::getName)
                                        .editable(saveTrafficType(TrafficItemType::setName))
                                        .cellFactory(TextFieldTableCell.forTableColumn())
                                )
                                .<Integer>childColumn(col -> col
                                        .header("Повториять каждые\n(месяцев)")
                                        .cellValueFactory(TrafficItemType::getDefaultDurationInMonth)
                                        .editable(saveTrafficType(TrafficItemType::setDefaultDurationInMonth))
                                        .cellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())))
                );
    }

    private <T> BiConsumer<TrafficItemType, T> saveTrafficType(BiConsumer<TrafficItemType, T> consumer) {
        return consumer.andThen((type, t) -> {
            services.getTrafficService()
                    .saveItemType(type)
                    .onErrorResume(e -> {
                        Platform.runLater(() -> createAlert("Ошибка", "Не удалось удалить элемент\nПри необходимости обратитесь к администратору"));
                        return Mono.empty();
                    })
                    .doOnSuccess(it -> {
                        type.setId(it.getId());
                        type.setDefaultDurationInMonth(it.getDefaultDurationInMonth());
                        type.setStatus(it.isStatus());
                        type.setName(it.getName());
                    })
                    .subscribe();
        });
    }

    @Override
    public void clearData() {
        itemTypeTable.getItems().clear();
    }

    @Override
    public void fillData() {
        services.getTrafficService()
                .getItemsType()
                .doOnNext(item -> itemTypeTable.getItems().add(item))
                .doOnComplete(() -> itemTypeTable.refresh())
                .subscribe();
    }
}
