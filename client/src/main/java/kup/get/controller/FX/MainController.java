package kup.get.controller.FX;

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
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import kup.get.config.CustomMenuItem;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.controller.FX.asu.BadgeController;
import kup.get.controller.FX.traffic.ItemController;
import kup.get.controller.FX.traffic.TeamAndVehicleController;
import kup.get.controller.FX.traffic.TrafficItemTypeController;
import kup.get.controller.socket.SocketService;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

@FxmlLoader(path = "/fxml/main.fxml")
public class MainController extends MyAnchorPane {
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

    private boolean checkHiddenMenu = true;
    private final SocketService socketService;
    private final AtomicReference<CustomMenuItem> actualMenuItem = new AtomicReference<>();
    private MyAnchorPane actualPane;
    private final AtomicReference<SequentialTransition> transition;

    public MainController(TrafficItemTypeController typeController,
                          TeamAndVehicleController teamAndVehicleController,
                          ItemController itemController,
                          BadgeController badgeController,
                          SocketService service, AtomicReference<SequentialTransition> transition) {
        this.transition = transition;
        this.socketService = service;
        mainPane.getChildren().addAll(typeController, teamAndVehicleController, itemController,
                badgeController);
        this.setVisible(true);
        this.setOpacity(1);

        CustomMenuItem trafficMenu = CustomMenuItem.builder()
                .menuItem("Служба движения", new MaterialDesignIconView(MaterialDesignIcon.BUS))
                .setRoles("ROLE_TRAFFIC", "ROLE_SUPERADMIN")
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
                );
        CustomMenuItem krsMenu = CustomMenuItem.builder()
                .menuItem("КРС", new FontAwesomeIconView(FontAwesomeIcon.USER_SECRET))
                .setRoles("ROLE_KRS", "ROLE_SUPERADMIN")
                .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton)
                .addChildren(
                        CustomMenuItem.builder()
                                .menuItem("Бейджи", new MaterialDesignIconView(MaterialDesignIcon.TICKET_ACCOUNT))
                                .setEventSwitchPane(event -> {
                                    badgeController.krsPeople();
                                    hiddenPages(badgeController);
                                }));
        CustomMenuItem energyMenu = CustomMenuItem.builder()
                .menuItem("Энергослужба", new WeatherIconView(WeatherIcon.OWM_210))
                .setRoles("ROLE_ENERGY", "ROLE_SUPERADMIN")
                .setEventOpenMenu(vBoxMenuItems, actualMenuItem, returnButton);
        CustomMenuItem asuMenu = CustomMenuItem.builder()
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
                                .setEventSwitchPane(event -> hiddenPages(typeController)),
                        CustomMenuItem.builder()
                                .menuItem("Обновления", new FontAwesomeIconView(FontAwesomeIcon.CLOUD_UPLOAD))
                                .setEventSwitchPane(event -> hiddenPages(typeController)),
                        CustomMenuItem.builder()
                                .menuItem("Расписание", new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_CLOCK))
                                .setEventSwitchPane(event -> hiddenPages(teamAndVehicleController))
                );

        CustomMenuItem.addToPane(vBoxMenuItems, krsMenu, trafficMenu, energyMenu, asuMenu);
        actualMenuItem.set(CustomMenuItem.builder().addChildren(krsMenu, trafficMenu, energyMenu, asuMenu));

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
            System.out.println("l " + usernameField.getText() + " p " + passwordField.getText());
           /* service.authorize(usernameField.getText(), passwordField.getText())
                    .doOnError(throwable -> Platform.runLater(() -> infoLabel.setText(throwable.getMessage())))
                    .onErrorResume(throwable -> Mono.just(throwable.getMessage()))      //  LOG
                    .subscribe(System.out::println);
//            System.out.println(service.getItemsType().blockFirst());*/
        });
    }

    @PostConstruct
    public void test() {
        socketService.authorize("sanya", "1101")
                .onErrorResume(s -> Mono.just(s.getMessage()))      //  LOG
                .doOnComplete(socketService::updatePeople).subscribe(System.out::println);
    }

    private void hiddenPages(MyAnchorPane appearancePane) {
        appearancePane.fillData();
        if (actualPane != null) {
            transition.set(switchPaneTransition(actualPane, appearancePane));
            actualPane.clearData();
        } else {
            transition.set(appearancePaneTransition(appearancePane));
        }
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
