package kup.get.controller.FX;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.controller.FX.traffic.TrafficController;
import kup.get.controller.socket.SocketService;
import reactor.core.publisher.Mono;

@FxmlLoader(path = "/fxml/main.fxml")
public class MainController extends MyAnchorPane {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private TilePane rolePane;

    @FXML
    private GridPane loginPane;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label infoLabel;

    public MainController(TrafficController trafficController, SocketService service) {
        mainPane.getChildren().addAll(trafficController);
        this.setVisible(true);
        this.setOpacity(1);
//        hiddenPages(trafficController);


        /*service.authorize("sanya", "1101")
                .doOnError(throwable -> Platform.runLater(() -> infoLabel.setText(throwable.getMessage())))
                .onErrorResume(throwable -> Mono.just(throwable.getMessage()))      //  LOG
                .subscribe(System.out::println);*/

        loginButton.setOnAction(event -> {
            System.out.println("l " + usernameField.getText() + " p " + passwordField.getText());
           /* service.authorize(usernameField.getText(), passwordField.getText())
                    .doOnError(throwable -> Platform.runLater(() -> infoLabel.setText(throwable.getMessage())))
                    .onErrorResume(throwable -> Mono.just(throwable.getMessage()))      //  LOG
                    .subscribe(System.out::println);
//            System.out.println(service.getItemsType().blockFirst());*/
        });
    }

    private Transition transition;
    private boolean hidden = false;
    public void switchPane(KeyEvent event){
        if(event.getCode().equals(KeyCode.F1)){
            if (hidden) {
                appearancePane();
                hidden = false;
            } else {
                disappearancePane();
                hidden = true;
            }
        }
    }
    private void disappearancePane(){
        if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
            transition = createTransition(mainPane, rolePane);
            transition.play();
        }
    }
    private void appearancePane(){
        if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
            transition = createTransition(rolePane, mainPane);
            transition.play();
        }
    }

    private void hiddenPages(AnchorPane pane) {
        for (Node node : mainPane.getChildren()) {
            if (node.equals(pane)) {
                node.getStyleClass().removeAll("hidden");
            } else {
                if (!node.getStyleClass().contains("hidden")) node.getStyleClass().add("hidden");
            }
        }
    }
}
