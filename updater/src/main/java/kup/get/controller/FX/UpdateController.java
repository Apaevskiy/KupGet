package kup.get.controller.FX;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.controller.socket.SocketController;
import kup.get.entity.Version;
import kup.get.service.*;

import java.io.File;
import java.util.List;

@FxmlLoader(path = "/fxml/updates.fxml")
public class UpdateController extends MyAnchorPane {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label processInformationField;
    @FXML
    private Label progressText;

    @FXML
    private AnchorPane updateInformationPane;
    @FXML
    private AnchorPane updatePane1;
    @FXML
    private AnchorPane updatePane2;

    private final ZipService zipService;
    private final PropertyService propertyService;
    private final InformationController informationController;
    private final SocketController socketController;

    public UpdateController(ZipService zipService, PropertyService propertyService, InformationController informationController, SocketController socketController) {
        this.zipService = zipService;
        this.propertyService = propertyService;
        this.informationController = informationController;
        this.socketController = socketController;
    }

    public void checkUpdates() {
        this.getStyleClass().removeAll("hidden");
        Version versionProgram = propertyService.getVersion();
        socketController.getActualVersion().doOnSuccess(version -> {
            if (version.getId() != versionProgram.getId()) {
                updateProgram(versionProgram, version);
            }
        }).subscribe();
    }

    private void updateProgram(Version version, Version actualVersion) {
        QueryTask queryTask = new QueryTask(processInformationField);
        Thread threadQueryWriter = new Thread(queryTask);

        UpdateTask task = new UpdateTask(socketController, zipService, queryTask, version, threadQueryWriter);
        Thread threadTask = new Thread(task);

        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t -> {
            File file = task.getValue();
            List<Version> updates = socketController.getUpdateInformation(version);
            System.out.println("END");
            if (updates != null) {
                propertyService.saveVersion(actualVersion);
                switchToNode(informationController);
                informationController.printInformation(updates);
                String[] run = {"java", "-jar", file.getAbsolutePath()};
                try {
                    Runtime.getRuntime().exec(run);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        threadTask.start();
    }
}
