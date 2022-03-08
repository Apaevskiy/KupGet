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
    public MyTable() {
        super(FXCollections.observableArrayList());
    }

    public <T> MyTableColumn<T> headerColumn(String title) {
        MyTableColumn<T> myTableColumn = new MyTableColumn<>(title);
        this.getColumns().add(myTableColumn);
        return myTableColumn;
    }
    public <t> MyTable<S> addColumn(String title, Function<S, t> property){
        column(title, property);
        return this;
    }
    public <t> MyTableColumn<t> column(String title, Function<S, t> property) {
        MyTableColumn<t> myTableColumn = new MyTableColumn<>(title);
        myTableColumn.setFactory(property);
        this.getColumns().add(myTableColumn);
        return myTableColumn;
    }

    private MyTable<S> getMyTable() {
        return this;
    }

    public class MyTableColumn<T> extends TableColumn<S, T> {
        private MyTableColumn<?> parentColumn;

        public MyTableColumn(String text) {
            super(text);
        }
        public MyTableColumn<T> setMaxWidthColumn(double width){
            this.setMaxWidth(width);
            this.setPrefWidth(width);
            this.setResizable(false);
            return this;
        }
        public <t> MyTableColumn(String text, MyTableColumn<t> parentColumn) {
            super(text);
            this.parentColumn = parentColumn;
        }

        public <t> MyTableColumn<t> column(String title) {
            MyTableColumn<t> myTableColumn = new MyTableColumn<>(title, this);
            this.getColumns().add(myTableColumn);
            return myTableColumn;
        }
        public <t> MyTableColumn<t> column(String title, Function<S, t> property) {
            MyTableColumn<t> myTableColumn = new MyTableColumn<>(title, this);
            myTableColumn.setFactory(property);
            this.getColumns().add(myTableColumn);
            return myTableColumn;
        }


        public void setFactory(Function<S, T> property) {
            this.setCellValueFactory(cellData -> {
                try {
                    return new SimpleObjectProperty<>(property.apply(cellData.getValue()));
                } catch (Exception e) {
                    return null;
                }
            });
        }

        public MyTableColumn<T> setEditable(BiConsumer<S, T> consumer, Callback<TableColumn<S, T>, TableCell<S, T>> columnCallback) {
            this.setCellFactory(columnCallback);
            this.setOnEditCommit(e -> consumer.accept(e.getTableView().getItems().get(e.getTablePosition().getRow()), e.getNewValue()));
            return this;
        }

        public MyTableColumn<T> setInvisible() {
            this.setVisible(false);
            return this;
        }

        public MyTableColumn<?> build() {
            return parentColumn;
        }

        public MyTable<S> and() {
            return getMyTable();
        }
    }
}
