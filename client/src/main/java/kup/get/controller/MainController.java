package kup.get.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kup.get.config.FX.CustomMenuItem;
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
import reactor.core.publisher.Hooks;

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
    private Label menuLabel;

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

    @FXML
    private GridPane errorPane;
    @FXML
    private Button exitButton;
    @FXML
    private Button reconnectButton;
    @FXML
    private Button offlineButton;

    private final AtomicReference<CustomMenuItem> actualMenuItem = new AtomicReference<>(CustomMenuItem.builder());
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
                          Services services) {
        this.services = services;
        this.setVisible(true);
        this.setOpacity(1);

        mainPane.getChildren().addAll(typeController, teamAndVehicleController, itemController,
                badgeController, importExportController, usersController, updateController,
                scheduleController, personController);

        Hooks.onErrorDropped(err -> Platform.runLater(() -> {
            workPlace.setOpacity(0);
            workPlace.setVisible(false);
            errorPane.setOpacity(1);
            errorPane.setVisible(true);
        }));


        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Служба движения", new MaterialDesignIconView(MaterialDesignIcon.BUS))
                        .setRoles("ROLE_TRAFFIC", "ROLE_ADMIN", "AFK")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Отчёты", new MaterialDesignIconView(MaterialDesignIcon.CLIPBOARD_TEXT))
                                        .setEventSwitchPane(event -> hiddenPages(itemController)),
                                CustomMenuItem.builder()
                                        .menuItem("Перечень пунктов", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS))
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
                        .setRoles("ROLE_KRS", "ROLE_ADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
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
                        .setRoles("ROLE_ENERGY", "ROLE_ADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("АСУ", new FontAwesomeIconView(FontAwesomeIcon.PIED_PIPER_ALT))
                        .setRoles("ROLE_SUPERADMIN")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
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
                        .setRoles("ROLE_TRAFFIC", "ROLE_ADMIN", "AFK")
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
                    if (parent.getParent() == null) {
                        returnButton.setVisible(false);
                        menuLabel.setText("Портал КУП ГЭТ");
                    }
                } else {
                    returnButton.setVisible(false);
                    menuLabel.setText("Портал КУП ГЭТ");
                }
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
            switchPaneTransition(workPlace, loginPane).play();
            actualPane.setOpacity(0);
            actualPane.setVisible(false);
            passwordField.setText("");
        });
        offlineButton.setOnAction(event -> {
            errorPane.setOpacity(0);
            errorPane.setVisible(false);

            this.addCustomMenuItems("AFK");
            switchPaneTransition(loginPane, workPlace).play();
        });
        reconnectButton.setOnAction(event -> {
            errorPane.setOpacity(0);
            errorPane.setVisible(false);
            connectToServer();
        });
        exitButton.setOnAction(event -> System.exit(0));
    }

    @PostConstruct
    void connectToServer() {
        services.createClientTransport()
                .doOnSuccess(dc -> {
                    loginPane.setOpacity(1);
                    loginPane.setVisible(true);
                })
                .doOnError(throwable -> {
                    if (throwable.getMessage().contains("Connection refused")) {
                        Platform.runLater(() -> {
                            loginPane.setOpacity(0);
                            loginPane.setVisible(false);
                            errorPane.setOpacity(1);
                            errorPane.setVisible(true);
                        });
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
                    switchPaneTransition(loginPane, workPlace).play();
                })
                .subscribe(s -> Platform.runLater(() -> this.addCustomMenuItems(s)));
    }

    private void hiddenPages(MyAnchorPane appearancePane) {
        if (actualPane != null) {
            switchPaneTransition(actualPane, appearancePane).play();
            actualPane.clearData();
        } else {
            appearancePaneTransition(appearancePane).play();
        }
        appearancePane.fillData();
        actualPane = appearancePane;
    }

    @Override
    public void fillData() {

    }

    @Override
    public void clearData() {

    }
}
