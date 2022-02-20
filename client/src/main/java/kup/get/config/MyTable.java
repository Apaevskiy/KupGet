package kup.get.config;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Function;


public class MyTable<S> extends TableView<S> {
    TableColumn<S, Void> column = new TableColumn<>();

    public MyTable() {
        super(FXCollections.observableArrayList());
        column.setSortable(false);
        column.setResizable(false);
    }

    public <t> MyTable<S> column(String title, Function<S, t> property) {
        TableColumn<S, t> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(property.apply(cellData.getValue())));
        column.getColumns().add(col);
        return this;
    }

    public <t> MyTable<S> invisibleColumn(String title, Function<S, t> property) {
        TableColumn<S, t> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(property.apply(cellData.getValue())));
        col.setVisible(false);
        column.getColumns().add(col);
        return this;
    }
    public <t> MyTable<S> editableColumn(String title, Function<S, t> property, BiConsumer<S, t> consumer,
                                         Callback<TableColumn<S,t>, TableCell<S,t>> columnCallback) {
        TableColumn<S, t> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(property.apply(cellData.getValue())));
        col.setCellFactory(columnCallback);
        onEditCommit(col, consumer);
        column.getColumns().add(col);
        return this;
    }

    public MyTable<S> headerColumn(String header){
        column.setText(header);
        this.getColumns().add(column);
        return this;
    }
    public <t> void onEditCommit(TableColumn<S, t> column, BiConsumer<S, t> consumer) {
        column.setOnEditCommit(e ->
                consumer.accept(e.getTableView().getItems().get(e.getTablePosition().getRow()), e.getNewValue()));
    }
}
