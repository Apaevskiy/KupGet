package kup.get.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kup.get.config.CustomMenuItem;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.controller.asu.BadgeController;
import kup.get.controller.asu.ScheduleController;
import kup.get.controller.asu.UpdateController;
import kup.get.controller.asu.UsersController;
import kup.get.controller.other.ImportExportController;
import kup.get.controller.other.PersonController;
import kup.get.controller.traffic.ItemController;
import kup.get.controller.traffic.TeamAndVehicleController;
import kup.get.controller.traffic.TrafficItemTypeController;
import kup.get.service.Services;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@FxmlLoader(path = "/fxml/main.fxml")
public class MainController extends MyAnchorPane {

    @FXML
    private GridPane workPlace;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private VBox vBoxMenuItems;
    @FXML
    private ColumnConstraints menuColumn;
    @FXML
    private Button logoutButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button hidePanelButton;
    @FXML
    private GridPane returnButton;

    @FXML
    private GridPane loginPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label infoLabel;
    @FXML
    private Button loginButton;

    private final AtomicReference<CustomMenuItem> actualMenuItem = new AtomicReference<>(CustomMenuItem.builder());
    private final AtomicReference<SequentialTransition> transition;
    private final List<CustomMenuItem> customMenuItemList = new ArrayList<>();
    private final Services services;
    private boolean checkHiddenMenu = true;
    private MyAnchorPane actualPane;

    public MainController(TrafficItemTypeController typeController,
                          TeamAndVehicleController teamAndVehicleController,
                          ItemController itemController,
                          BadgeController badgeController,
                          ImportExportController importExportController,
                          PersonController personController,
                          UpdateController updateController,
                          UsersController usersController,
                          ScheduleController scheduleController,
                          AtomicReference<SequentialTransition> transition, Services services) {
        this.transition = transition;
        this.services = services;
        this.setVisible(true);
        this.setOpacity(1);


        mainPane.getChildren().addAll(typeController, teamAndVehicleController, itemController,
                badgeController, importExportController, usersController, updateController,
                scheduleController, personController);

        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Служба движения", new MaterialDesignIconView(MaterialDesignIcon.BUS))
                        .setRoles("ROLE_TRAFFIC", "ROLE_SUPERADMIN", "AFK")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Отчёты", new MaterialDesignIconView(MaterialDesignIcon.CLIPBOARD_TEXT))
                                        .setEventSwitchPane(event -> hiddenPages(itemController)),
                                CustomMenuItem.builder()
                                        .menuItem("Перечень пунктов", new MaterialIconView(MaterialIcon.WIDGETS))
                                        .setEventSwitchPane(event -> hiddenPages(typeController)),
                                CustomMenuItem.builder()
                                        .menuItem("Экипажи и ТС", new FontAwesomeIconView(FontAwesomeIcon.USERS))
                                        .setEventSwitchPane(event -> hiddenPages(teamAndVehicleController)),
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            badgeController.trafficPeople();
                                            hiddenPages(badgeController);
                                        })
                        ));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("КРС", new FontAwesomeIconView(FontAwesomeIcon.USER_SECRET))
                        .setRoles("ROLE_KRS", "ROLE_SUPERADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            badgeController.krsPeople();
                                            hiddenPages(badgeController);
                                        }))
        );
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Энергослужба", new WeatherIconView(WeatherIcon.OWM_210))
                        .setRoles("ROLE_ENERGY", "ROLE_SUPERADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("АСУ", new FontAwesomeIconView(FontAwesomeIcon.PIED_PIPER_ALT))
                        .setRoles("ROLE_SUPERADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Пропуска", new OctIconView(OctIcon.CREDIT_CARD))
                                        .setEventSwitchPane(event -> hiddenPages(itemController)),
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            badgeController.allPeople();
                                            hiddenPages(badgeController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Пользователи", new FontAwesomeIconView(FontAwesomeIcon.USER))
                                        .setEventSwitchPane(event -> hiddenPages(usersController)),
                                CustomMenuItem.builder()
                                        .menuItem("Обновления", new FontAwesomeIconView(FontAwesomeIcon.CLOUD_UPLOAD))
                                        .setEventSwitchPane(event -> hiddenPages(updateController)),
                                CustomMenuItem.builder()
                                        .menuItem("Расписание", new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_CLOCK))
                                        .setEventSwitchPane(event -> hiddenPages(scheduleController))
                        ));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Экспорт и импорт", new MaterialDesignIconView(MaterialDesignIcon.FILE_EXPORT))
                        .setRoles("ROLE_TRAFFIC", "ROLE_SUPERADMIN", "AFK")
                        .setEventSwitchPane(event -> hiddenPages(importExportController)));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Сотрудники", new FontAwesomeIconView(FontAwesomeIcon.USERS))
                        .setRoles("AFK")
                        .setEventSwitchPane(event -> hiddenPages(personController)));

        returnButton.setOnMouseClicked(event -> {
            if (actualMenuItem.get() != null) {
                CustomMenuItem parent = actualMenuItem.get().getParent();
                if (parent != null) {
                    CustomMenuItem.addToPane(vBoxMenuItems, parent.getChildren());
                    if (parent.getParent() == null)
                        returnButton.setVisible(false);
                } else returnButton.setVisible(false);
            }
        });

        Timeline hideMenu = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(menuColumn.maxWidthProperty(), 70)));
        Timeline openMenu = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(menuColumn.maxWidthProperty(), 250)));
        hidePanelButton.setOnAction(event -> {
            if (checkHiddenMenu)
                hideMenu.play();
            else openMenu.play();
            checkHiddenMenu = !checkHiddenMenu;
        });
        loginButton.setOnAction(event -> {
            vBoxMenuItems.getChildren().clear();
            loginButton.setDisable(true);
            auth();
        });
        logoutButton.setOnAction(event -> {
            services.closeConnection();
            vBoxMenuItems.getChildren().clear();
            transition.set(switchPaneTransition(workPlace, loginPane));
            transition.get().play();
            actualPane.setOpacity(0);
            actualPane.setVisible(false);
            passwordField.setText("");
        });
    }

    @PostConstruct
    void connectToServer() {
        services.createClientTransport()
                .doOnError(throwable -> {
                    if (throwable.getMessage().contains("Connection refused")) {
                        Platform.runLater(this::afk);
                    } else {
                        Platform.runLater(() -> infoLabel.setText(throwable.getLocalizedMessage()));
                    }
                })
                .subscribe();
    }

    void addCustomMenuItems(String role) {
        Set<CustomMenuItem> items = customMenuItemList.stream().filter(customMenuItem -> customMenuItem.getRoles().contains(role)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!items.isEmpty()) {
            CustomMenuItem.addToPane(vBoxMenuItems, items);
            actualMenuItem.get().addChildren(items);
        }
    }

    void auth() {
        services.createRequester(usernameField.getText(), passwordField.getText())
                .doFinally(signalType -> Platform.runLater(() -> loginButton.setDisable(false)))
                .doOnError(throwable -> Platform.runLater(() -> infoLabel.setText(throwable.getLocalizedMessage())))
                .doOnComplete(() -> {
                    services.getPersonService().updatePeople();
                    transition.set(switchPaneTransition(loginPane, workPlace));
                    transition.get().play();
                })
                .subscribe(s -> Platform.runLater(() -> this.addCustomMenuItems(s)));
    }

    void afk() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Ошибка соединения");
        alert.setHeaderText("Не удалось подключиться к серверу!");
        alert.setContentText("Продолжить работу в автономном режиме?\n" +
                "Часть функционала программы будет недоступна.");
        ButtonType offlineButton = new ButtonType("Автономный\nрежим");
        ButtonType reconnectButton = new ButtonType("Подключиться\nснова");
        ButtonType exitButton = new ButtonType("Выход");
        alert.getButtonTypes().clear();

        alert.getButtonTypes().addAll(offlineButton, reconnectButton, exitButton);

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == offlineButton) {
            this.addCustomMenuItems("AFK");
            transition.set(switchPaneTransition(loginPane, workPlace));
            transition.get().play();
            loginButton.setDisable(false);
        } else if (option.isPresent() && option.get() == reconnectButton) {
            connectToServer();
        } else {
            System.exit(130);
        }
    }

    private void hiddenPages(MyAnchorPane appearancePane) {
        if (actualPane != null) {
            transition.set(switchPaneTransition(actualPane, appearancePane));
            actualPane.clearData();
        } else {
            transition.set(appearancePaneTransition(appearancePane));
        }
        appearancePane.fillData();
        transition.get().play();
        actualPane = appearancePane;
    }

    @Override
    public void fillData() {

    }

    @Override
    public void clearData() {

    }
}
