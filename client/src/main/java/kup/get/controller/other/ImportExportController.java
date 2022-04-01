package kup.get.controller.other;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.entity.traffic.TrafficItemType;
import kup.get.entity.traffic.TrafficTeam;
import kup.get.entity.traffic.TrafficVehicle;
import kup.get.service.Services;
import lombok.Data;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@FxmlLoader(path = "/fxml/import.fxml")
public class ImportExportController extends MyAnchorPane {
    @FXML
    private Button switchButton;

    @FXML
    private GridPane importPane;
    @FXML
    private Button chooseFileButton;
    @FXML
    private TextField pathToFileTextField;
    @FXML
    private Button importButton;
    @FXML
    private ProgressIndicator importProgressIndicator;


    @FXML
    private GridPane exportPane;
    @FXML
    private TreeView<CheckBox> treeView;
    @FXML
    private ComboBox<String> saveToComboBox;
    @FXML
    private HBox filePane;
    @FXML
    private TextField pathToDirectoryTextField;
    @FXML
    private Button chooseDirectoryButton;
    @FXML
    private Button exportButton;


    private final Label textGraphicButton = new Label("Экспорт");

    public ImportExportController(Services services) {
        TreeItem<CheckBox> peopleTreeItem = treeItemCheckBox("Сотрудники");
        TreeItem<CheckBox> photoTreeItem = treeItemCheckBox("С фото?");

        TreeItem<CheckBox> itemTypeTreeItem = treeItemCheckBox("Перечень пунктов для ОТ");
        TreeItem<CheckBox> teamTreeItem = treeItemCheckBox("Экипажи");
        TreeItem<CheckBox> vehicleTreeItem = treeItemCheckBox("ТС");
        peopleTreeItem.getChildren().add(photoTreeItem);
        TreeItem<CheckBox> rootTreeItem = treeItemCheckBox("Всё");
        treeView.setRoot(rootTreeItem);

        rootTreeItem.getChildren().addAll(peopleTreeItem, itemTypeTreeItem, teamTreeItem, vehicleTreeItem);
        rootTreeItem.getValue().selectedProperty().addListener((observable, oldValue, newValue) -> rootTreeItem.getChildren().forEach(ti -> ti.getValue().setSelected(newValue)));
        rootTreeItem.setExpanded(true);

        saveToComboBox.getItems().addAll("В файл", "В программу");
        saveToComboBox.setOnAction(event -> {
            SingleSelectionModel<String> model = saveToComboBox.getSelectionModel();
            if (model != null && model.getSelectedItem() != null) {
                if (model.getSelectedItem().equals("В файл")) {
                    filePane.setVisible(true);
                } else if (model.getSelectedItem().equals("В программу")) {
                    filePane.setVisible(false);
                }
            }
        });

        chooseFileButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
            if (file != null) {
                pathToFileTextField.setText(file.getAbsolutePath());
            }
        });
        chooseDirectoryButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedDirectory = directoryChooser.showDialog(pathToDirectoryTextField.getScene().getWindow());
            if (selectedDirectory != null) {
                pathToDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        switchButton.setGraphic(textGraphicButton);
        Transition importTransition = switchButtonTransition(65, "Импорт", importPane, exportPane);
        Transition exportTransition = switchButtonTransition(5, "Экспорт", exportPane, importPane);
        switchButton.setOnAction(event -> {
            if (importTransition.getStatus().equals(Animation.Status.STOPPED) && exportTransition.getStatus().equals(Animation.Status.STOPPED)) {
                if (switchButton.getLayoutX() == 65) {
                    exportTransition.play();
                } else {
                    importTransition.play();
                }
            }
        });
        exportButton.setOnAction(event -> {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            try {
                FileOutputStream fos = new FileOutputStream(pathToDirectoryTextField.getText() + "/export " + ft.format(new Date()) + ".txt");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                AtomicInteger count = new AtomicInteger();
                Items items = new Items();

                if (peopleTreeItem.getValue().isSelected()) {
                    items.setPeople(services.getPersonService().getPeople());
                        writeObject(oos, fos, items, count);
                }

                if (photoTreeItem.getValue().isSelected()) {
                    services.getPersonService().addPhotoToPeople()
                            .doOnComplete(() -> writeObject(oos, fos, items, count))
                            .subscribe(p -> {
                                items.getPhotos().add(p);
                                System.out.println(p.getId());
                            });
                }

                if (itemTypeTreeItem.getValue().isSelected()) {
                    services.getTrafficService().getItemsType()
                            .doOnComplete(() -> writeObject(oos, fos, items, count))
                            .subscribe(items.getTypes()::add);
                }
                if (teamTreeItem.getValue().isSelected())
                    services.getTrafficService().getAllTrafficTeam()
                            .doOnComplete(() -> writeObject(oos, fos, items, count))
                            .subscribe(items.getTeams()::add);
                if (vehicleTreeItem.getValue().isSelected())
                    services.getTrafficService().getTrafficVehicle()
                            .doOnComplete(() -> writeObject(oos, fos, items, count))
                            .subscribe(items.getVehicles()::add);

            } catch (IOException ex) {
                createAlert("Ошибка", ex.getMessage());
            }
        });
        importButton.setOnAction(event -> {
            try (FileInputStream fi = new FileInputStream(pathToFileTextField.getText()); ObjectInputStream oi = new ObjectInputStream(fi)) {
                Items items;
                items = (Items) oi.readObject();
                Task<Integer> task = new Task<Integer>() {
                    @Override
                    protected Integer call() {
                        int count = items.size();
                        AtomicInteger progress = new AtomicInteger(0);
                        services.getPersonService().savePeople(items.getPeople())
                                .subscribe(p -> {
                                    this.updateProgress(progress.getAndIncrement(), count);
                                    System.out.println(progress.get() + "/" + count);
                                });
                        services.getTrafficService().saveTrafficTeams(items.getTeams())
                                .subscribe(p -> {
                                    this.updateProgress(progress.getAndIncrement(), count);
                                    System.out.println(progress.get() + "/" + count);
                                });
                        services.getTrafficService().saveTrafficVehicles(items.getVehicles())
                                .subscribe(p -> {
                                    this.updateProgress(progress.getAndIncrement(), count);
                                    System.out.println(progress.get() + "/" + count);
                                });
                        services.getTrafficService().saveItemTypes(items.getTypes())
                                .subscribe(p -> {
                                    this.updateProgress(progress.getAndIncrement(), count);
                                    System.out.println(progress.get() + "/" + count);
                                });
                        services.getPersonService().savePhotos(items.getPhotos())
                                .subscribe(p -> {
                                    this.updateProgress(progress.getAndIncrement(), count);
                                    System.out.println(progress.get() + "/" + count);
                                });
                        return null;
                    }
                };
                importProgressIndicator.progressProperty().unbind();
                importProgressIndicator.progressProperty().bind(task.progressProperty());
                importProgressIndicator.setVisible(true);

                task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        t -> {
                            importProgressIndicator.setVisible(false);
                            createAlert("Иммпорт выполнен успешно",
                                    "Сотрудники: " + items.getPeople().size() + "\nЭкипажи: " + items.getTeams().size()
                                            + "\nФото: " + items.getPhotos().size()
                                            + "\nТС: " + items.getVehicles().size() + "\nПеречень для ОТ: " + items.getTypes().size());
                        });
                new Thread(task).start();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                createAlert("Ошибка", ex.getMessage());
            }
        });
    }

    private synchronized void writeObject(ObjectOutputStream oos, FileOutputStream fos, Items items, AtomicInteger count) {
        try {
            if (count.incrementAndGet() == 5) {
                oos.writeObject(items);
                oos.close();
                fos.close();
                Platform.runLater(() -> createAlert("Успешно", "Экспорт данных прошёл успешно"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> createAlert("Ошибка writeObject", e.getMessage()));
        }
    }

    private Transition switchButtonTransition(int layoutX, String text, Node appearancePane, Node disappearancePane) {
        float timeTransition = 700;

        return new ParallelTransition(new Timeline(new KeyFrame(Duration.millis(timeTransition), new KeyValue(switchButton.layoutXProperty(), layoutX))), new SequentialTransition(new Timeline(new KeyFrame(Duration.millis(timeTransition / 2), new KeyValue(textGraphicButton.opacityProperty(), 0), new KeyValue(disappearancePane.opacityProperty(), 0))), new Timeline(new KeyFrame(Duration.millis(1), new KeyValue(textGraphicButton.textProperty(), text), new KeyValue(disappearancePane.visibleProperty(), false), new KeyValue(appearancePane.visibleProperty(), true))), new Timeline(new KeyFrame(Duration.millis(timeTransition / 2), new KeyValue(textGraphicButton.opacityProperty(), 1), new KeyValue(appearancePane.opacityProperty(), 1)))));
    }

    private TreeItem<CheckBox> treeItemCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        return new TreeItem<>(checkBox);
    }

    @Data
    private static class Items implements Serializable {
        List<Person> people = new ArrayList<>();
        List<Photo> photos = new ArrayList<>();
        List<TrafficTeam> teams = new ArrayList<>();
        List<TrafficVehicle> vehicles = new ArrayList<>();
        List<TrafficItemType> types = new ArrayList<>();

        public int size() {
            return people.size()+photos.size()+teams.size()+vehicles.size()+types.size();
        }
    }

    @Override
    public void fillData() {

    }

    @Override
    public void clearData() {

    }
}
