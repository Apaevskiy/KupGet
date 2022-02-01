package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

import java.net.UnknownHostException;
import java.util.List;

@Component
public class MainController {
    @FXML
    private AnchorPane workPlace;

    private final SettingController settingController;
    private final UpdateController updateController;
    private final InformationController informationController;

    public MainController(SettingController settingController, UpdateController updateController, InformationController informationController) {
        this.settingController = settingController;
        this.updateController = updateController;
        this.informationController = informationController;
    }

    @FXML
    public void initialize() {
        workPlace.getChildren().addAll(settingController, updateController, informationController);
        hiddenPages(workPlace.getChildren(), updateController);

        try {
            updateController.checkUpdates();
        } catch (UnknownHostException | NullPointerException e) {
            hiddenPages(workPlace.getChildren(), settingController);
        }
    }

    private void hiddenPages(List<Node> list, Node pane) {
        for (Node node : list) {
            if (node.equals(pane)) {
                node.getStyleClass().removeAll("hidden");
            } else {
                if (!node.getStyleClass().contains("hidden"))
                    node.getStyleClass().add("hidden");
            }
        }
    }
}
