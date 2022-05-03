package kup.get.controller;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import kup.get.config.RSocketClientBuilderImpl;
import kup.get.service.SocketService;
import kup.get.entity.Version;
import kup.get.service.PropertyService;
import kup.get.service.UpdateTask;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class MainController extends AnchorPane {
    @FXML
    private TextArea updateInformationArea;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressText;

    @FXML
    private VBox progressPane;

    private final RSocketClientBuilderImpl config;
    private final SocketService socketService;
    private final PropertyService propertyService;

    public MainController(RSocketClientBuilderImpl config, SocketService socketService, PropertyService propertyService) {
        this.config = config;
        this.socketService = socketService;
        this.propertyService = propertyService;
        connectToServer();
    }

    void openGUI() {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/fxml/main.fxml");
        loader.setLocation(xmlUrl);
        loader.setRoot(this);
        loader.setController(this);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void connectToServer() {
        this.config.createClientTransport()
                .doOnSuccess(duplexConnection -> {
                    this.config.createRequester();
                    checkUpdates();
                })
                .doOnError(throwable -> Platform.runLater(() -> runProject(Paths.get("bin/client.jar").toAbsolutePath().toString())))
                .subscribe();
    }

    void checkUpdates() {
        Version versionProgram = propertyService.getVersion();
        socketService.getActualVersion().doOnSuccess(actualVersion -> {
            if (actualVersion.getId() != versionProgram.getId()) {
                Platform.runLater(() -> {
                    this.openGUI();
                    this.initializeUpdate(versionProgram, actualVersion);
                });
            } else {
                runProject(Paths.get("bin/client.jar").toAbsolutePath().toString());
                System.exit(0);
            }

        }).subscribe();
    }

    void initializeUpdate(Version versionProgram, Version actualVersion) {
        UpdateTask task = new UpdateTask(socketService);
        Thread threadTask = new Thread(task);

        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        progressText.textProperty().unbind();
        progressText.textProperty().bind(task.messageProperty());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t -> {
            Path file = task.getValue();
            progressText.textProperty().unbind();
            if (file != null) {
                progressText.setText("Обновление успешно установлено");
                socketService.getUpdateInformation(versionProgram)
                        .subscribe(version ->
                                updateInformationArea.setText(updateInformationArea.getText() + "⟳\tОбновление " + version.getRelease() + ":\n" + version.getInformation() + "\n\n"));
                propertyService.saveVersion(actualVersion);
                this.getScene().getWindow().setHeight(400);
                progressPane.setVisible(false);
                updateInformationArea.setVisible(true);
                runProject(file.toAbsolutePath().toString());
            } else {
                progressText.setTextFill(Color.RED);
                progressText.setText("Произошёл сбой!!!");
            }

        });
        threadTask.start();

        socketService.getFilesOfProgram()
                .doOnComplete(task::stopTask)
                .subscribe(task::addFileOfProgram);
    }

    void runProject(String path) {
        String[] run = {"java", "-jar", path};
        try {
            Runtime.getRuntime().exec(run);
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}