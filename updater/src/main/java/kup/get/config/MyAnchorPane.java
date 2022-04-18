package kup.get.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public abstract class MyAnchorPane extends AnchorPane {

    public MyAnchorPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource(this.getClass().getAnnotation(FxmlLoader.class).path());
            loader.setLocation(xmlUrl);
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            this.setId(this.getClass().getName());
            AnchorPane.setTopAnchor(this, 0.0);
            AnchorPane.setBottomAnchor(this, 0.0);
            AnchorPane.setLeftAnchor(this, 0.0);
            AnchorPane.setRightAnchor(this, 0.0);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected void switchToNode(Node... nodes) {
        if (!this.getStyleClass().contains("hidden"))
            this.getStyleClass().add("hidden");
        for (Node node : nodes)
            node.getStyleClass().removeAll("hidden");
    }
}
