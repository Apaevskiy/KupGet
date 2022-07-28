package kup.get;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kup.get.controller.MainController;
import kup.get.service.PropertyService;
import kup.get.service.SocketService;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.*;
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
        createLink();
        this.socketService.createWebClient("setup", "1488325");
        if (socketService.checkConnection().block() != null) {
            Long actualVersionId = propertyService.getVersionId();
            Long latestVersionId = socketService.getActualVersion().block();
            if (!actualVersionId.equals(latestVersionId)) {
                Stage stage = event.getStage();
                stage.setTitle("Обновление программы");
                stage.setScene(new Scene(mainController));
                stage.getIcons().add(new Image("/images/logo.png"));
                stage.setOnCloseRequest(e -> System.exit(0));
                stage.setWidth(500);
                stage.setHeight(200);
                stage.show();
                mainController.initializeUpdate(actualVersionId, latestVersionId);
            } else {
                mainController.runProject(Paths.get("client.jar").toAbsolutePath().toString());
                System.exit(0);
            }
        } else {
            mainController.runProject(Paths.get("client.jar").toAbsolutePath().toString());
            System.exit(0);
        }
    }

    private void createLink() {
        Path batFile = Paths.get("client.bat");
        if (!Files.exists(batFile)) {
            try {
                Files.createFile(batFile);
                String contentOfBatFile = batFile.toAbsolutePath().getParent() + File.separator + "jre" + File.separator + "bin" + File.separator + "javaw.exe" +
                        " -jar " + batFile.toAbsolutePath().getParent() + File.separator + "client.jar";
                Files.write(batFile, contentOfBatFile.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
