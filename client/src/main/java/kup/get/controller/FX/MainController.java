package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem trafficItemTypeMenu;
    @FXML
    private AnchorPane workPlace;

    private final LoginController loginController;

    public MainController(LoginController loginController) {
        this.loginController = loginController;
    }

    /*private final BriefingController briefingController;
    private final TrafficItemTypeController trafficItemTypeController;

    public MainController(BriefingController briefingController, TrafficItemTypeController trafficItemTypeController) {
        this.briefingController = briefingController;
        this.trafficItemTypeController = trafficItemTypeController;
    }*/

    @FXML
    public void initialize() {
        workPlace.getChildren().addAll(loginController);
        hiddenPages(loginController);

        trafficItemTypeMenu.setOnAction(event -> {

        });
    }

    private void hiddenPages(Node pane) {
        for (Node node : workPlace.getChildren()) {
            if (node.equals(pane)) {
                node.getStyleClass().removeAll("hidden");
            } else {
                if (!node.getStyleClass().contains("hidden")) node.getStyleClass().add("hidden");
            }
        }
    }
}
