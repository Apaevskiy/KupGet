package kup.get;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import kup.get.controller.FX.MainController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {

    private final Scene scene;

    public StageInitializer(MainController mainController) {
        this.scene=new Scene(mainController);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, mainController::switchPane);
    }

    @Override
    public void onApplicationEvent(JavaFxApplication.StageReadyEvent event) {
            Stage stage = event.getStage();
            stage.setTitle("Ведомость реализации ПП контролерами");
            stage.setScene(scene);
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.show();
    }
}
