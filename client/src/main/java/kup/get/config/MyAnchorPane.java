package kup.get.config;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class MyAnchorPane extends AnchorPane {

    public MyAnchorPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource(this.getClass().getAnnotation(FxmlLoader.class).path());
            loader.setLocation(xmlUrl);
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            this.setId(this.getClass().getName());
            this.getStyleClass().add("hidden");
            AnchorPane.setTopAnchor(this, 0.0);
            AnchorPane.setBottomAnchor(this, 0.0);
            AnchorPane.setLeftAnchor(this, 0.0);
            AnchorPane.setRightAnchor(this, 0.0);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    protected void createAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    protected <S, t> TableColumn<S, t> column(String title, Function<S, t> property) {
        TableColumn<S, t> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(property.apply(cellData.getValue())));
        return col;
    }
    protected <S, t> TableColumn<S, t> invisibleColumn(String title, Function<S, t> property) {
        TableColumn<S, t> col = column(title, property);
        col.setVisible(false);
        return col;
    }
    protected <S, t> void onEditCommit(TableColumn<S, t> column, BiConsumer<S, t> consumer) {
        column.setOnEditCommit(e ->
                consumer.accept(e.getTableView().getItems().get(e.getTablePosition().getRow()), e.getNewValue()));
    }
}
