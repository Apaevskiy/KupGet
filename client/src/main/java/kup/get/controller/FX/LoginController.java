package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.RSocketClientConfig;
import kup.get.controller.socket.SocketService;

@FxmlLoader(path = "/fxml/authentication.fxml")
public class LoginController extends MyAnchorPane {
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public LoginController(SocketService service) {
        loginButton.setOnAction(event -> {
            System.out.println("l " + usernameField.getText() + " p " + passwordField.getText());
            service.authorize(usernameField.getText(), passwordField.getText());
            System.out.println(service.getItemsType().blockFirst());
        });
    }
}
