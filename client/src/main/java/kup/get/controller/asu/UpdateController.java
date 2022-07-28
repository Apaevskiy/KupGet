package kup.get.controller.asu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.entity.Version;
import kup.get.service.socket.AsuSocketService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML
    private ProgressIndicator uploadProgress;
    @FXML
    private AnchorPane menuPane;

    public UpdateController(AsuSocketService service) {
        chooseFileButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
            if (file != null) {
                pathToFileTextField.setText(file.getAbsolutePath());
            }
        });
        loadButton.setOnAction(event -> {
            uploadProgress.setVisible(true);
            menuPane.setVisible(false);

            Version version = service.addVersion(versionField.getText(), commentField.getText())
                    .onErrorResume(throwable -> {
                        createAlert("ERROR", throwable.getMessage());
                        return Mono.empty();
                    })
                    .block();
            if (version != null)
                uploadFile(service);
        });
    }

    private void uploadFile(AsuSocketService service) {
        try {
            Resource resource = new UrlResource("file:" + pathToFileTextField.getText());
            Flux<DataBuffer> flux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
            AtomicInteger i = new AtomicInteger(1);
            int count = Objects.requireNonNull(flux.collectList().block()).size();
            System.out.println(count);
            service.uploadFile(flux)
                    .doOnComplete(() -> {
                        createAlert("Уведомление", "Обновление упешно загружено");
                        Platform.runLater(() -> {
                            menuPane.setVisible(true);
                            uploadProgress.setVisible(false);
                        });
                    })
                    .subscribe(status -> {
//                        System.out.println(status);\
                        uploadProgress.setProgress(i.getAndIncrement() * 1.0 / count);
                    });
        } catch (MalformedURLException e) {
            createAlert("ERROR", e.getMessage());
        }

    }

    @Override
    public void fillData() {
    }

    @Override
    public void clearData() {

    }
}
