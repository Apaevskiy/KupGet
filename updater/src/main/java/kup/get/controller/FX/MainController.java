package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import kup.get.config.RSocketClientConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainController {
    @FXML
    private AnchorPane workPlace;

    private final SettingController settingController;
    private final UpdateController updateController;
    private final InformationController informationController;
    private final RSocketClientConfig config;

    public MainController(SettingController settingController, UpdateController updateController, InformationController informationController, RSocketClientConfig config) {
        this.settingController = settingController;
        this.updateController = updateController;
        this.informationController = informationController;
        this.config = config;
    }

    @FXML
    public void initialize() {
        workPlace.getChildren().addAll(settingController, updateController, informationController);
        hiddenPages(workPlace.getChildren(), updateController);
        String checkConnection = config.createRequester();
        if (checkConnection.isEmpty()) {
            updateController.checkUpdates();
        } else {
            settingController.information(checkConnection);
            hiddenPages(workPlace.getChildren(), settingController);
        }
    }

    private void hiddenPages(List<Node> list, Node pane) {
        for (Node node : list) {
            if (node.equals(pane)) {
                node.getStyleClass().removeAll("hidden");
            } else {
                if (!node.getStyleClass().contains("hidden")) node.getStyleClass().add("hidden");
            }
        }
    }
}
