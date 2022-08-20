package kup.get;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kup.get.controller.MainController;
import kup.get.service.PropertyService;
import kup.get.service.SocketService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {

    private final MainController mainController;

    private final SocketService socketService;
    private final PropertyService propertyService;

    public StageInitializer(MainController mainController, SocketService socketService, PropertyService propertyService) {
        this.mainController = mainController;
        this.socketService = socketService;
        this.propertyService = propertyService;
    }


    @Override
    public void onApplicationEvent(@NonNull JavaFxApplication.StageReadyEvent event) {
        Long sizeProgramFile;
        if (( sizeProgramFile = socketService.checkConnection("setup", "1488325").block()) != null) {
            Long actualVersionId = propertyService.getVersionId();
            Long latestVersionId = socketService.getActualVersion().block();
            if (!actualVersionId.equals(latestVersionId) || !Files.exists(Paths.get("client.exe"))) {
                Stage stage = event.getStage();
                stage.setTitle("Обновление программы");
                stage.setScene(new Scene(mainController));
                stage.getIcons().add(new Image("/images/logo.png"));
                stage.setOnCloseRequest(e -> System.exit(0));
                stage.setWidth(500);
                stage.setHeight(200);
                stage.show();

                mainController.initializeUpdate(actualVersionId, latestVersionId, sizeProgramFile);
            } else {
                mainController.runProject(Paths.get("client.exe").toAbsolutePath().toString());
                System.exit(0);
            }
        } else {
            mainController.runProject(Paths.get("client.exe").toAbsolutePath().toString());
            System.exit(0);
        }
    }
}
