package kup.get.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kup.get.service.PropertyService;
import kup.get.service.SocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


@Component
@Slf4j
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
    private final AtomicReference<Long> progress = new AtomicReference<>(0L);

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
            createAlert(e.getMessage());
        }
    }

    public void initializeUpdate(Long actualVersionId, Long latestVersionId, Long sizeProgramFile) {
        progressText.setText("Получение списка обновлений...");
        progressBar.setProgress(0);
        Path file = Paths.get("client.exe");
        if (Files.exists(file)) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("В процессе работы программы возникла ошибка");
                alert.setContentText(e.getMessage());
                ButtonType returnButton = new ButtonType("Повторить");
                ButtonType exitButton = new ButtonType("Выход");
                alert.getButtonTypes().clear();

                alert.getButtonTypes().addAll(returnButton, exitButton);

                Optional<ButtonType> option = alert.showAndWait();

                if (option.isPresent() && option.get() == returnButton) {
                    initializeUpdate(actualVersionId, latestVersionId, sizeProgramFile);
                    return;
                }
            }
        }
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            DataBufferUtils.write(socketService.getProgram(), channel)
                    .doOnComplete(() -> {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveUpdate(actualVersionId, latestVersionId, file);
                    })
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .map(DataBuffer::capacity)
                    .subscribe(capacity -> {
                        Platform.runLater(() -> updateInformation(
                                progress.accumulateAndGet((long) capacity, Long::sum),
                                sizeProgramFile));
                    });
        } catch (IOException e) {
            createAlert(e.getMessage());
        }

    }

    public void runProject(String path) {
        String[] run = {/*"java", "-jar", */path};
        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            createAlert(ex.getMessage());
        }
    }

    private void updateInformation(Long progress, Long sizeOfFile) {
        progressText.setText(String.format("%.2f/%.2f", progress / 1048576.0, sizeOfFile / 1048576.0));
        progressBar.setProgress(progress * 1.0 / sizeOfFile);
    }

    private void saveUpdate(Long actualVersionId, Long latestVersionId, Path file) {
        socketService.getUpdateInformation(actualVersionId)
                .subscribe(version ->
                        updateInformationArea.setText(updateInformationArea.getText() + "⟳\tОбновление " + version.getRelease() + ":\n" + version.getInformation() + "\n\n"));
        propertyService.saveVersion(latestVersionId);
        this.getScene().getWindow().setHeight(400);
        progressPane.setVisible(false);
        updateInformationArea.setVisible(true);
        runProject(file.toString());
    }

    protected void createAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("В процессе работы программы возникла ошибка");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}