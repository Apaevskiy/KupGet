package kup.get.config;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;


public class MyTable<S> extends TableView<S> {
    private final MyContextMenu contextMenu = new MyContextMenu();

    public MyTable() {
        contextMenu.item("Обновить таблицу", e -> this.refresh());
        this.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                this.setContextMenu(contextMenu);
            }
        });
    }

    public <T> MyTable<S> addColumn(Function<MyTableColumn<T>, MyTableColumn<T>> column) {
        this.getColumns().add(column.apply(new MyTableColumn<>(null)));
        return this;
    }

    public MyTable<S> items(List<S> list) {
        this.setItems(FXCollections.observableArrayList(list));
        return this;
    }

    public MyTable<S> items(ObservableList<S> list) {
        this.setItems(list);
        return this;
    }

    public MyTable<S> contextMenu(Function<MyContextMenu, MyContextMenu> menuFunction) {
        menuFunction.apply(contextMenu);
        return this;
    }

    public MyTable<S> searchBox(StringProperty property, Function<S, Boolean> function) {
        FilteredList<S> filteredData = new FilteredList<>(this.getItems(), b -> true);
        property.addListener((observable, oldValue, newValue) -> filteredData.setPredicate(function::apply));
        SortedList<S> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedData);
        return this;
    }

    public MyTable<S> searchBox(ObjectProperty<EventHandler<ActionEvent>> property, Function<S, Boolean> function) {
        FilteredList<S> filteredData = new FilteredList<>(this.getItems(), b -> true);
        property.set(event -> filteredData.setPredicate(function::apply));
        SortedList<S> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedData);
        return this;
    }

    public class MyTableColumn<T> extends TableColumn<S, T> {

        public MyTableColumn(String text) {
            super(text);
        }

        public MyTableColumn<T> header(String header) {
            this.setText(header);
            return this;
        }

        public <t> MyTableColumn<T> property(Function<MyTableColumn<T>, Property<t>> function, t value) {
            function.apply(this).setValue(value);
            return this;
        }

        public <t> MyTableColumn<T> childColumn(Function<MyTableColumn<t>, MyTableColumn<t>> column) {
            this.getColumns().add(column.apply(new MyTableColumn<>(null)));
            return this;
        }

        public MyTableColumn<T> cellValueFactory(Function<S, T> property) {
            this.setCellValueFactory(cellData -> {
                try {
                    return new SimpleObjectProperty<>(property.apply(cellData.getValue()));
                } catch (Exception e) {
                    return null;
                }
            });
            return this;
        }

        public MyTableColumn<T> cellFactory(Callback<TableColumn<S, T>, TableCell<S, T>> columnCallback) {
            this.setCellFactory(columnCallback);
            return this;
        }

        public MyTableColumn<T> editable(BiConsumer<S, T> consumer) {
            this.setOnEditCommit(e -> consumer.accept(e.getTableView().getItems().get(e.getTablePosition().getRow()), e.getNewValue()));
            return this;
        }

        public MyTableColumn<T> widthColumn(double width) {
            this.setMaxWidth(width);
            this.setMinWidth(width);
            this.setPrefWidth(width);
            this.setResizable(false);
            return this;
        }
    }


    public class MyContextMenu extends ContextMenu {
        public MyContextMenu item(String name, EventHandler<ActionEvent> action) {
            MenuItem menuItem = new MenuItem(name);
            menuItem.setOnAction(action);
            contextMenu.getItems().add(menuItem);
            return this;
        }

        public MyContextMenu menu(String title, Function<MyContextMenu.MyMenu, MyContextMenu.MyMenu> function) {
            MyMenu menu = new MyMenu(title);
            contextMenu.getItems().add(function.apply(menu));
            return this;
        }

        class MyMenu extends Menu {
            public MyMenu(String title) {
                super(title);
            }

            public MyContextMenu.MyMenu item(String name, EventHandler<ActionEvent> action) {
                MenuItem menuItem = new MenuItem(name);
                menuItem.setOnAction(action);
                this.getItems().add(menuItem);
                return this;
            }
        }
    }
}
