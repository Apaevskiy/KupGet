package kup.get.controller.FX;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.RSocketClientBuilderImpl;
import kup.get.controller.socket.SocketService;

@FxmlLoader(path = "/fxml/main.fxml")
public class MainController extends MyAnchorPane {
    @FXML
    private TextArea updateInformationArea;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressText;

    @FXML
    private GridPane settingPane;

    @FXML
    private Button saveButton;
    @FXML
    private TextField ipField;
    @FXML
    private Button resetButton;
    @FXML
    private Label informationLabel;
    @FXML
    private TextField portField;

    private final RSocketClientBuilderImpl config;
    private final SocketService socketService;

    public MainController(RSocketClientBuilderImpl config, SocketService socketService) {
        this.config = config;
        this.socketService = socketService;
        this.config.createClientTransport()
                .doOnSuccess(duplexConnection -> {
                    this.config.createRequester();
                    checkUpdates();
                })
                .doOnError(throwable -> Platform.runLater(() -> {
                    informationLabel.setText("Ошибка:\n"+throwable.getLocalizedMessage());
                    settingPane.setDisable(true);
                }))
                .subscribe();



    }
    void checkUpdates(){
        socketService.getActualVersion().doOnSuccess(version -> {
            if (version.getId() != versionProgram.getId()) {
                updateProgram(versionProgram, version);
            }
        }).subscribe();
    }
}
