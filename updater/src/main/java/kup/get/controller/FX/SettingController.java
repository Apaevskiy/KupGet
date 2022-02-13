package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.RSocketClientConfig;
import kup.get.model.Version;
import kup.get.service.PropertyService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

@FxmlLoader(path = "/fxml/settingPage.fxml")
public class SettingController extends MyAnchorPane {
    @FXML
    private Button saveButton;
    @FXML
    private Button resetButton;

    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;

    @FXML
    private Label versionInformationLabel;

    public SettingController(PropertyService propertyService, UpdateController updateController, RSocketClientConfig config) {
        versionInformationLabel.setText("Если вы читаете это сообщение, то звоните в отдел АСУ\nВерсия программы " + propertyService.getVersion().getRelease());

        ipField.setText(propertyService.getIpServer());
        portField.setText(String.valueOf(propertyService.getPortServer()));

        saveButton.setOnAction(event -> {
            propertyService.saveServerConfig(ipField.getText(), portField.getText());
            String checkConnection = config.createRequester();
            if (checkConnection.isEmpty()) {
                this.getStyleClass().add("hidden");
                updateController.checkUpdates();
            } else {
                versionInformationLabel.setText("Что-то пошло не так!\nСохраните следующую информация для отдела АСУ:\n" + checkConnection);
            }
        });
        resetButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Сброс данных");
            alert.setHeaderText("Вы действительно хотите сбросить все данные?\n После сброса потребуется время для загрузки всех данных с сервера (автоматически).");
            alert.setContentText("Нажмите ОК для сброса и Cancel для отмены");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                try {
                    URL url = this.getClass().getResource("/program/program.jar");
                    if (url != null) {
                        Files.delete(new File(url.getFile()).toPath());
                        propertyService.saveVersion(Version.builder().id(0).release("0").build());
                    } else throw new NullPointerException("ResetButton in SettingController (url == null)");
                } catch (IOException | NullPointerException e) {
                    versionInformationLabel.setText("Что-то пошло не так!\nСохраните следующую информация для отдела АСУ:\n" + e.getMessage());

                }
            }
        });
    }

    public void information(String error) {
        versionInformationLabel.setText(versionInformationLabel.getText() + "\nОшибка: " + error);
    }
}
