package kup.get.controller.FX.traffic;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;

@FxmlLoader(path = "/fxml/traffic/traffic.fxml")
public class TrafficController extends MyAnchorPane {
    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane workPlace;

    private Pane actualPane = new Pane();

    public TrafficController(TrafficItemTypeController typeController,
                             BriefingController briefingController,
                             TeamAndVehicleController teamAndVehicleController,
                             ItemController itemController) {
        workPlace.getChildren().addAll(
                typeController,
                briefingController,
                teamAndVehicleController,
                itemController
        );

        this.setVisible(true);
        this.setOpacity(1);


        Menu referenceMenu = new Menu("Справочники");
        MenuItem referenceItemTypeMI = new MenuItem("Тип");
        MenuItem referenceTeamAndVehicleMI = new MenuItem("Экипажи и ТС");
        referenceMenu.getItems().addAll(referenceItemTypeMI, referenceTeamAndVehicleMI);

        Menu reportMenu = new Menu("Отчёты");
        MenuItem referenceBriefingMI = new MenuItem("Иснструктажи по ОТ");
        MenuItem reportPeopleMI = new MenuItem("Остальное");
        MenuItem reportTeamMI = new MenuItem("x По экипажам");
        MenuItem reportVehicleMI = new MenuItem("x По ТС");
        MenuItem reportAllMI = new MenuItem("x По всем");
        reportMenu.getItems().addAll(referenceBriefingMI,reportPeopleMI,reportTeamMI, reportVehicleMI, reportAllMI);

        menuBar.getMenus().addAll(reportMenu, referenceMenu);

        referenceBriefingMI.setOnAction(event -> {
            briefingController.fillInTheTables();
            createTransition(actualPane, briefingController).play();
            actualPane = briefingController;
        });
        referenceItemTypeMI.setOnAction(event -> {
            typeController.fillInTheTables();
            createTransition(actualPane, typeController).play();
            actualPane = typeController;
        });
        referenceTeamAndVehicleMI.setOnAction(event -> {
            teamAndVehicleController.fillInTheTables();
            createTransition(actualPane, teamAndVehicleController).play();
            actualPane = teamAndVehicleController;
        });
        reportPeopleMI.setOnAction(event -> {
            itemController.fillInTheTables();
            createTransition(actualPane, itemController).play();
            actualPane = itemController;
        });
    }
}
