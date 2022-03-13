package kup.get.controller.other;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.util.Duration;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;

import java.util.stream.Collectors;


@FxmlLoader(path = "/fxml/import.fxml")
public class ImportExportController extends MyAnchorPane {
    @FXML
    private Button switchButton;

    @FXML
    private ListView<CheckBox> exportListView;
    @FXML
    private ComboBox<String> saveToComboBox;
    @FXML
    private TextField pathToFileTextField;
    @FXML
    private Button chooseFileButton;
    @FXML
    private Button exportButton;

    @FXML
    private TreeView<CheckBox> treeView;

    private final Label textGraphicButton = new Label("Экспорт");


    public ImportExportController() {
        CheckBox choosePhotoCheckBox = new CheckBox("С фото?");
        choosePhotoCheckBox.setPadding(new Insets(10, 10, 10, 25));

        CheckBox peopleCheckBox = checkBox("Сотрудники");
        peopleCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            choosePhotoCheckBox.setVisible(newValue);
        });

        exportListView.getItems().addAll(
                peopleCheckBox,
                choosePhotoCheckBox,
                checkBox("Перечень пунктов для ОТ"),
                checkBox("Экипажи"),
                checkBox("ТС")
        );
        exportButton.setOnAction(event -> {
            System.out.println(exportListView.getItems().stream().map(cb -> cb.getText() + " " + cb.isSelected()).collect(Collectors.toList()));
        });

        switchButton.setGraphic(textGraphicButton);
        Transition importTransition = switchButtonTransition(65, "Импорт");
        Transition exportTransition = switchButtonTransition(5, "Экспорт");
        switchButton.setOnAction(event -> {
            if (switchButton.getLayoutX() == 65) {
                exportTransition.play();
            } else {
                importTransition.play();
            }
        });
    }

    private Transition switchButtonTransition(int layoutX, String text) {
        float timeTransition = 1000;

        return new ParallelTransition(
                new Timeline(
                        new KeyFrame(Duration.millis(timeTransition),
                                new KeyValue(switchButton.layoutXProperty(), layoutX))),
                new SequentialTransition(
                        new Timeline(
                                new KeyFrame(Duration.millis(timeTransition / 2),
                                        new KeyValue(textGraphicButton.opacityProperty(), 0))),
                        new Timeline(
                                new KeyFrame(Duration.millis(1),
                                        new KeyValue(textGraphicButton.textProperty(), text))),
                        new Timeline(
                                new KeyFrame(Duration.millis(timeTransition / 2),
                                        new KeyValue(textGraphicButton.opacityProperty(), 1))))
        );
    }

    private CheckBox checkBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setPadding(new Insets(10));
        return checkBox;
    }

    @Override
    public void fillData() {

    }

    @Override
    public void clearData() {

    }
}
