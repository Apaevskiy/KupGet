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
import kup.get.controller.clinic.ClinicController;
import kup.get.controller.other.ImportExportController;
import kup.get.controller.other.PersonController;
import kup.get.controller.traffic.ItemController;
import kup.get.controller.traffic.TeamAndVehicleController;
import kup.get.controller.traffic.TrafficItemTypeController;
import kup.get.service.DAO.PersonDaoService;
import kup.get.service.DAO.TrafficDaoService;
import kup.get.service.Services;
import kup.get.service.other.PropertyService;
import kup.get.service.socket.AsuSocketService;
import kup.get.service.socket.ClinicService;
import kup.get.service.socket.PersonSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
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

    private TrafficItemTypeController typeController;
    private TeamAndVehicleController teamAndVehicleController;
    private ItemController itemController;
    private BadgeController badgeController;
    private ImportExportController importExportController;
    private PersonController personController;
    private UpdateController updateController;
    private UsersController usersController;
    private ScheduleController scheduleController;
    private ClinicController clinicController;
    private final PropertyService propertyService;

    public MainController(Services services,
                          PropertyService propertyService,
                          PersonSocketService personSocketService, PersonDaoService personDaoService,
                          TrafficDaoService trafficDaoService,
                          AsuSocketService asuSocketService,
                          ClinicService clinicService) {
        this.services = services;
        this.propertyService = propertyService;
        this.setVisible(true);
        this.setOpacity(1);

        usernameField.setText(propertyService.getUsername());

        Hooks.onErrorDropped(err -> Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Возникла ошибка!");
            alert.setContentText(err.getMessage());
            alert.showAndWait();
            log.error("Hooks.onErrorDropped");
           /* workPlace.setOpacity(0);
            workPlace.setVisible(false);
            errorPane.setOpacity(1);
            errorPane.setVisible(true);*/
        }));

        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Служба движения", new MaterialDesignIconView(MaterialDesignIcon.BUS))
                        .setRoles("ROLE_TRAFFIC", "ROLE_ASU", "AFK")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Отчёты", new MaterialDesignIconView(MaterialDesignIcon.CLIPBOARD_TEXT))
                                        .setEventSwitchPane(event -> {
                                            if (itemController == null) {
                                                itemController = new ItemController(services);
                                                mainPane.getChildren().add(itemController);
                                            }
                                            hiddenPages(itemController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Перечень пунктов", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS))
                                        .setEventSwitchPane(event -> {
                                            if (typeController == null) {
                                                typeController = new TrafficItemTypeController(services);
                                                mainPane.getChildren().add(typeController);
                                            }
                                            hiddenPages(typeController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Экипажи и ТС", new FontAwesomeIconView(FontAwesomeIcon.USERS))
                                        .setEventSwitchPane(event -> {
                                            if (teamAndVehicleController == null) {
                                                teamAndVehicleController = new TeamAndVehicleController(trafficDaoService,services);
                                                mainPane.getChildren().add(teamAndVehicleController);
                                            }
                                            hiddenPages(teamAndVehicleController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            if (badgeController == null) {
                                                badgeController = new BadgeController(services);
                                                mainPane.getChildren().add(badgeController);
                                            }
                                            badgeController.trafficPeople();
                                            hiddenPages(badgeController);
                                        })
                        ));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("КРС", new FontAwesomeIconView(FontAwesomeIcon.USER_SECRET))
                        .setRoles("ROLE_KRS", "ROLE_ASU")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            if (badgeController == null) {
                                                badgeController = new BadgeController(services);
                                                mainPane.getChildren().add(badgeController);
                                            }
                                            badgeController.krsPeople();
                                            hiddenPages(badgeController);
                                        }))
        );
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Здравпункт", new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE))
                        .setRoles("ROLE_CLINIC", "ROLE_ASU")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Медосмотры", new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_CHECK_ALT))
                                        .setEventSwitchPane(event -> {
                                            if (clinicController == null) {
                                                clinicController = new ClinicController(clinicService, services);
                                                mainPane.getChildren().add(clinicController);
                                            }
                                            hiddenPages(clinicController);
                                        }))
        );
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Энергослужба", new WeatherIconView(WeatherIcon.OWM_210))
                        .setRoles("ROLE_ENERGY", "ROLE_ASU")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("АСУ", new FontAwesomeIconView(FontAwesomeIcon.PIED_PIPER_ALT))
                        .setRoles("ROLE_ASU")
                        .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton, menuLabel)
                        .addChildren(
                                CustomMenuItem.builder()
                                        .menuItem("Пропуска", new OctIconView(OctIcon.CREDIT_CARD))
                                        .setEventSwitchPane(event -> {
                                            if (itemController == null) {
                                                itemController = new ItemController(services);
                                                mainPane.getChildren().add(itemController);
                                            }
                                            hiddenPages(itemController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                        .setEventSwitchPane(event -> {
                                            if (badgeController == null) {
                                                badgeController = new BadgeController(services);
                                                mainPane.getChildren().add(badgeController);
                                            }
                                            badgeController.allPeople();
                                            hiddenPages(badgeController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Пользователи", new FontAwesomeIconView(FontAwesomeIcon.USER))
                                        .setEventSwitchPane(event -> {
                                            if (usersController == null) {
                                                usersController = new UsersController(personSocketService);
                                                mainPane.getChildren().add(usersController);
                                            }
                                            hiddenPages(usersController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Обновления", new FontAwesomeIconView(FontAwesomeIcon.CLOUD_UPLOAD))
                                        .setEventSwitchPane(event -> {
                                            if (updateController == null) {
                                                updateController = new UpdateController(asuSocketService);
                                                mainPane.getChildren().add(updateController);
                                            }
                                            hiddenPages(updateController);
                                        }),
                                CustomMenuItem.builder()
                                        .menuItem("Расписание", new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_CLOCK))
                                        .setEventSwitchPane(event -> {
                                            if (scheduleController == null) {
                                                scheduleController = new ScheduleController();
                                                mainPane.getChildren().add(scheduleController);
                                            }
                                            hiddenPages(scheduleController);
                                        })
                        ));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Экспорт и импорт", new MaterialDesignIconView(MaterialDesignIcon.FILE_EXPORT))
                        .setRoles("ROLE_TRAFFIC", "ROLE_ASU", "AFK")
                        .setEventSwitchPane(event -> {
                            if (importExportController == null) {
                                importExportController = new ImportExportController(services);
                                mainPane.getChildren().add(importExportController);
                            }
                            hiddenPages(importExportController);
                        }));
        customMenuItemList.add(
                CustomMenuItem.builder()
                        .menuItem("Сотрудники", new FontAwesomeIconView(FontAwesomeIcon.USERS))
                        .setRoles("AFK")
                        .setEventSwitchPane(event -> {
                            if (personController == null) {
                                personController = new PersonController(personDaoService);
                                mainPane.getChildren().add(personController);
                            }
                            hiddenPages(personController);
                        }));

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
            loginButton.setDisable(true);
            infoLabel.setText("");
            propertyService.saveUsername(usernameField.getText());
            auth();
        });
        logoutButton.setOnAction(event -> {
            vBoxMenuItems.getChildren().clear();
            customMenuItemList.clear();

            if (actualPane != null) {
                actualPane.setOpacity(0);
                actualPane.setVisible(false);
            }
            passwordField.setText("");
            switchPaneTransition(workPlace, loginPane).play();
            services.disconnect();
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
        services.connectToServer()
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
                .subscribe(this::checkUpdates);
    }

    private void checkUpdates(Long latestVersionId) {
        Long actualVersionId = propertyService.getVersionId();
        if (!actualVersionId.equals(latestVersionId)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Обновление");
                alert.setHeaderText(null);
                alert.setContentText("Доступно обновление программы.\nНажмите \"Установить\" для установки обновления.");
                ButtonType setupButton = new ButtonType("Установить");
                ButtonType exitButton = new ButtonType("Выход");
                alert.getButtonTypes().clear();

                alert.getButtonTypes().addAll(setupButton, exitButton);

                Optional<ButtonType> option = alert.showAndWait();

                if (option.isPresent() && option.get() == setupButton) {
                    String[] run = {Paths.get("updater.exe").toAbsolutePath().toString()};
                    try {
                        Runtime.getRuntime().exec(run);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.exit(130);
            });
        }
    }

    void addCustomMenuItems(String role) {
        Set<CustomMenuItem> items = customMenuItemList.stream().filter(customMenuItem -> customMenuItem.getRoles().contains(role)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!items.isEmpty()) {
            CustomMenuItem.addToPane(vBoxMenuItems, items);
            actualMenuItem.get().addChildren(items);
        }
    }

    void auth() {
        services.getAuthorities(usernameField.getText(), passwordField.getText())
                .doFinally(signalType -> Platform.runLater(() -> loginButton.setDisable(false)))
                .doOnComplete(() -> switchPaneTransition(loginPane, workPlace).play())
                .onErrorResume(throwable -> {
                    Platform.runLater(() -> infoLabel.setText(throwable.getLocalizedMessage()));
                    return Mono.empty();
                })
                .switchOnFirst((s, t) -> {
                    Platform.runLater(() -> usernameLabel.setText(s.get()));
                    return t.skip(1);
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
