package kup.get.config;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Duration;
import kup.get.model.traffic.TrafficVehicle;

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
            this.setVisible(false);
            this.setOpacity(0);
            this.setId(this.getClass().getName());
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



    protected SequentialTransition createTransition(Pane disappearancePane, Pane appearancePane) {
        return createTransition(disappearancePane, appearancePane, 1300);
    }

    protected SequentialTransition createTransition(Pane disappearancePane, Pane appearancePane, long time) {
        return new SequentialTransition(
                new Timeline(
                        new KeyFrame(Duration.millis(time*0.4),
                                new KeyValue(disappearancePane.opacityProperty(), 0))),
                new Timeline(
                        new KeyFrame(Duration.millis(1),
                                new KeyValue(disappearancePane.visibleProperty(), false),
                                new KeyValue(appearancePane.visibleProperty(), true))),
                new Timeline(new KeyFrame(Duration.millis(time*0.6),
                        new KeyValue(appearancePane.opacityProperty(), 1))));
    }
}
