package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.RSocketClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@FxmlLoader(path = "/fxml/main.fxml")
public class MainController extends MyAnchorPane {
    @FXML
    private AnchorPane workPlace;

    public MainController(SettingController settingController, UpdateController updateController, InformationController informationController, RSocketClientConfig config) {
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
