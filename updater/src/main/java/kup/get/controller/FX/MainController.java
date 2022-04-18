package kup.get.controller.FX;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.RSocketClientBuilderImpl;
import kup.get.controller.socket.SocketService;
import kup.get.entity.FileOfProgram;
import kup.get.entity.Version;
import kup.get.service.PropertyService;
import kup.get.service.UpdateTask;
import kup.get.service.ZipService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@FxmlLoader(path = "/fxml/main.fxml")
@Slf4j
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
    private RowConstraints settingRow;

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
    private final PropertyService propertyService;
    private final ZipService zipService;


    public MainController(RSocketClientBuilderImpl config, SocketService socketService, PropertyService propertyService, ZipService zipService) {
        this.config = config;
        this.socketService = socketService;
        this.propertyService = propertyService;
        this.zipService = zipService;
        connectToServer();
    }

    void connectToServer() {
        Timeline hideSettingPane = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(settingRow.maxHeightProperty(), 0),
                        new KeyValue(settingPane.opacityProperty(), 0),
                        new KeyValue(settingPane.visibleProperty(), false)));
        hideSettingPane.play();
        this.config.createClientTransport()
                .doOnSuccess(duplexConnection -> {
                    this.config.createRequester();
                    log.info("checkUpdates");
                    checkUpdates();
                })
                .doOnError(throwable -> Platform.runLater(() -> {
                    Timeline openSettingPane = new Timeline(
                            new KeyFrame(Duration.millis(1),
                                    new KeyValue(settingPane.visibleProperty(), true)),
                            new KeyFrame(Duration.millis(500),
                                    new KeyValue(settingRow.maxHeightProperty(), 200),
                                    new KeyValue(settingPane.opacityProperty(), 1)));
                    openSettingPane.play();
                    informationLabel.setText("Ошибка:\n" + throwable.getLocalizedMessage());

                }))
                .subscribe();
    }

    void checkUpdates() {
        Version versionProgram = propertyService.getVersion();
        socketService.getActualVersion().doOnSuccess(actualVersion -> {
            log.info("actualVersion " + actualVersion.getId() + ", versionProgram " + versionProgram.getId());

            if (actualVersion.getId() != versionProgram.getId()) {
                initializeUpdate(versionProgram, actualVersion);
            }
        }).subscribe();
    }

    void initializeUpdate(Version versionProgram, Version actualVersion) {
        List<FileOfProgram> files = new ArrayList<>();
        socketService.getFilesOfProgram()
                .doOnComplete(() -> Platform.runLater(() -> updateProgram(files, versionProgram, actualVersion)))
                .subscribe(files::add);
    }

    void updateProgram(List<FileOfProgram> files, Version versionProgram, Version actualVersion) {
        UpdateTask task = new UpdateTask(files, socketService, zipService);
        Thread threadTask = new Thread(task);
        updateInformationArea.setText("");

        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        progressText.textProperty().unbind();
        progressText.textProperty().bind(task.messageProperty());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t -> {
            File file = task.getValue();
            if (file != null) {
                System.out.println("END " + file.getAbsolutePath());
                socketService.getUpdateInformation(versionProgram)
                        .subscribe(version ->
                                updateInformationArea.setText(updateInformationArea.getText() + "\n\tОбновление " + version.getRelease() + ":\n" + version.getInformation()));
//                propertyService.saveVersion(actualVersion);

                /*String[] run = {"java", "-jar", file.getAbsolutePath()};
                try {
                    Runtime.getRuntime().exec(run);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            }
        });
        threadTask.start();
    }
}
