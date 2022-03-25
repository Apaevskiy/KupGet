package kup.get.controller.asu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.service.socket.AsuSocketService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.MalformedURLException;

@FxmlLoader(path = "/fxml/asu/updates.fxml")
public class UpdateController extends MyAnchorPane {
    @FXML
    private TextArea commentField;
    @FXML
    private TextField pathToFileTextField;
    @FXML
    private TextField versionField;
    @FXML
    private Button chooseFileButton;
    @FXML
    private Button loadButton;

    public UpdateController(AsuSocketService service) {
        chooseFileButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
            if (file != null) {
                pathToFileTextField.setText(file.getAbsolutePath());
            }
        });
        loadButton.setOnAction(event -> {
            try {
                Resource resource =  new UrlResource("file:" + pathToFileTextField.getText());
                service.uploadFile(versionField.getText(), commentField.getText(), resource)
                        .doOnCancel(() -> Platform.runLater(() -> createAlert("1", "cansel")))
                        .subscribe(System.out::println);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void fillData() {

    }

    @Override
    public void clearData() {

    }
}
