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
                             TeamAndVehicleController teamAndVehicleController) {
        workPlace.getChildren().addAll(typeController, briefingController, teamAndVehicleController);

        this.setVisible(true);
        this.setOpacity(1);


        Menu referenceMenu = new Menu("Справочники");
        MenuItem referenceBriefingMI = new MenuItem("Иснструктажи по ОТ");
        MenuItem referenceItemTypeMI = new MenuItem("Тип");
        MenuItem referenceTeamAndVehicleMI = new MenuItem("Экипажи и ТС");
        MenuItem referenceVehicleMI = new MenuItem("x Транспортные средства");
        referenceMenu.getItems().addAll(referenceBriefingMI, referenceItemTypeMI, referenceTeamAndVehicleMI);

        Menu reportMenu = new Menu("Отчёты");
        MenuItem reportPeopleMI = new MenuItem("x По водителям");
        MenuItem reportTeamMI = new MenuItem("x По экипажам");
        MenuItem reportVehicleMI = new MenuItem("x По ТС");
        MenuItem reportAllMI = new MenuItem("x По всем");
        reportMenu.getItems().addAll(reportPeopleMI,reportTeamMI, reportVehicleMI, reportAllMI);

        menuBar.getMenus().addAll(reportMenu, referenceMenu);

        referenceBriefingMI.setOnAction(event -> {
            createTransition(actualPane, briefingController).play();
            actualPane = briefingController;
        });
        referenceItemTypeMI.setOnAction(event -> {
            createTransition(actualPane, typeController).play();
            actualPane = typeController;
        });
        referenceTeamAndVehicleMI.setOnAction(event -> {
            teamAndVehicleController.fillInTheTables();
            createTransition(actualPane, teamAndVehicleController).play();
            actualPane = teamAndVehicleController;
        });
    }
}
