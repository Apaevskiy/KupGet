package kup.get.controller.FX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;

@FxmlLoader(path = "/fxml/settingPage.fxml")
public class SettingController extends MyAnchorPane {
    @FXML
    private Button saveButton;

    @FXML
    private TextField serverField;

    @FXML
    private Button resetButton;

    @FXML
    private Label versionInformationLabel;

    public SettingController() {

    }
}
