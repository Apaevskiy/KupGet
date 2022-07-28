package kup.get.controller;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import kup.get.service.PropertyService;
import kup.get.service.SocketService;
import kup.get.service.UpdateTask;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;


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

    private final SocketService socketService;
    private final PropertyService propertyService;

    public MainController(SocketService socketService, PropertyService propertyService) {
        this.socketService = socketService;
        this.propertyService = propertyService;
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

    public void initializeUpdate(Long actualVersionId, Long latestVersionId) {
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
                socketService.getUpdateInformation(actualVersionId)
                        .subscribe(version ->
                                updateInformationArea.setText(updateInformationArea.getText() + "⟳\tОбновление " + version.getRelease() + ":\n" + version.getInformation() + "\n\n"));
                propertyService.saveVersion(latestVersionId);
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

    public void runProject(String path) {
        String[] run = {"java", "-jar", path};
        try {
            Runtime.getRuntime().exec(run);
//            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}