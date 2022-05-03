package kup.get;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kup.get.controller.MainController;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {

    private final Scene scene;

    public StageInitializer(MainController mainController) {
        this.scene = new Scene(mainController);

    }

    @Override
    public void onApplicationEvent(JavaFxApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setTitle("Портал КУП Горэлектротранспорт");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.show();
        stage.setWidth(500);
        stage.setHeight(200);
    }

}
