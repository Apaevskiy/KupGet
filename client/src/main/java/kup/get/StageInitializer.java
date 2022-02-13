package kup.get;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {

    @Value("classpath:/fxml/main.fxml")
    private Resource chartResource;
    private final String applicationTitle;
    private final ApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            ApplicationContext applicationContext){
        this.applicationTitle = applicationTitle;
        this.applicationContext=applicationContext;
    }

    @Override
    public void onApplicationEvent(JavaFxApplication.StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(chartResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            stage.setTitle("Ведомость реализации ПП контролерами");
            stage.setScene(new Scene(parent));
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
