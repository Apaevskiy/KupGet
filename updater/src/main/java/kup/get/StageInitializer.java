package kup.get;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kup.get.controller.FX.MainController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
