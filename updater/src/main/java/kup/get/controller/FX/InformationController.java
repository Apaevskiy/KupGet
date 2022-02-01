package kup.get.controller.FX;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.model.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@FxmlLoader(path = "/fxml/information.fxml")
public class InformationController extends MyAnchorPane {

    @FXML
    private Label informationLabel1;
    @FXML
    private Label informationLabel2;
    @FXML
    private Label releaseLabel1;
    @FXML
    private Label releaseLabel2;

    @FXML
    private Label countPageLabel;
    @FXML
    private Label pageLabel;

    @FXML
    private Button switchToLeftButton;
    @FXML
    private Button switchToRightButton;

    @FXML
    private AnchorPane updatePane1;
    @FXML
    private AnchorPane updatePane2;
    @FXML
    private AnchorPane informationPane;

    private int numberPane = 1;
    private int count = 0;
    private List<Version> versions = new ArrayList<>();

    private SequentialTransition transition;

    public InformationController() {
        updatePane2.setScaleX(0.5);
        updatePane2.setScaleY(0.5);

        switchToLeftButton.setOnAction(event -> switchPane(Side.LEFT));
        switchToRightButton.setOnAction(event -> switchPane(Side.RIGHT));
    }

    private SequentialTransition createTransition(AnchorPane disappearancePane, AnchorPane appearancePane, Side side) {
        return new SequentialTransition(
                new Timeline(
                        new KeyFrame(Duration.millis(1),
                                new KeyValue(appearancePane.translateXProperty(),
                                        (informationPane.getWidth()) * side.value),
                                new KeyValue(appearancePane.visibleProperty(), true))
                ),
                new Timeline(
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(disappearancePane.scaleXProperty(), 0.5),
                                new KeyValue(disappearancePane.scaleYProperty(), 0.5)
                        )),
                new Timeline(
                        new KeyFrame(Duration.millis(500),
                                new KeyValue(disappearancePane.translateXProperty(),
                                        (informationPane.getWidth()) * side.value * -1),
                                new KeyValue(disappearancePane.opacityProperty(), 0.5),
                                new KeyValue(appearancePane.translateXProperty(), 0),
                                new KeyValue(appearancePane.opacityProperty(), 1.0))),
                new Timeline(
                        new KeyFrame(Duration.millis(500),
                                new KeyValue(appearancePane.scaleXProperty(), 1),
                                new KeyValue(appearancePane.scaleYProperty(), 1),
                                new KeyValue(disappearancePane.visibleProperty(), false)
                        )));
    }

    public void printInformation(List<Version> versions) {
        this.versions = versions;
        countPageLabel.setText("/ " + versions.size());
        pageLabel.setText("1");
        releaseLabel1.setText("Обновление: " + versions.get(0).getRelease());
        informationLabel1.setText(versions.get(0).getInformation());
        updatePane2.setVisible(false);
        switchToLeftButton.setVisible(false);

// switchPane(Side.LEFT);
    }

    private void switchPane(Side side) {
        if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
            count += side.value;
            switchToRightButton.setVisible(count != versions.size() - 1);
            switchToLeftButton.setVisible(count != 0);

            if (numberPane == 2) {
                informationLabel1.setText(versions.get(count).getInformation());
                releaseLabel1.setText("Обновление: " + versions.get(count).getRelease());

                numberPane = 1;

                transition = createTransition(updatePane2, updatePane1, side);
            } else {
                informationLabel2.setText(versions.get(count).getInformation());
                releaseLabel2.setText("Обновление: " + versions.get(count).getRelease());

                numberPane = 2;

                transition = createTransition(updatePane1, updatePane2, side);
            }
            pageLabel.setText(String.valueOf(count + 1));
            transition.play();

        }
    }

    @Getter
    @AllArgsConstructor
    private enum Side {
        LEFT(-1), RIGHT(1);
        private final int value;
    }
}

