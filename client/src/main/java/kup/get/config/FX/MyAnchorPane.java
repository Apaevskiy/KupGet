package kup.get.config.FX;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

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

    public abstract void fillData();
    public abstract void clearData();

    protected SequentialTransition switchPaneTransition(Pane disappearancePane, Pane appearancePane) {
        return switchPaneTransition(disappearancePane, appearancePane, 800);
    }
    protected SequentialTransition appearancePaneTransition(Pane appearancePane) {
        return new SequentialTransition(
                new Timeline(
                        new KeyFrame(Duration.millis(1),
                                new KeyValue(appearancePane.visibleProperty(), true))),
                new Timeline(new KeyFrame(Duration.millis(500),
                        new KeyValue(appearancePane.opacityProperty(), 1))));
    }
    protected SequentialTransition switchPaneTransition(Pane disappearancePane, Pane appearancePane, long time) {
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
