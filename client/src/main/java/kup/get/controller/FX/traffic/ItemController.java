package kup.get.controller.FX.traffic;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.controller.socket.SocketService;
import kup.get.model.alfa.Person;
import kup.get.model.traffic.TrafficItem;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@FxmlLoader(path = "/fxml/traffic/items.fxml")
public class ItemController extends MyAnchorPane {
    @FXML
    private Button excelButton;

    @FXML
    private MyTable<TrafficItem> ownerTable;
    @FXML
    private MyTable<TrafficItem> itemTable;

    @FXML
    private ComboBox<String> ownerComboBox;

    @FXML
    private DatePicker finishDatePicker;
    @FXML
    private DatePicker startDatePricker;

    private final SocketService socketService;

    public ItemController(SocketService socketService) {
        this.socketService = socketService;
        ownerComboBox.getItems().addAll("Все","ТС","Экипажи","Водители");

        ownerTable
                .headerColumn("items")
//                .invisibleColumn("id", TrafficItem::getId)
                .column("Наименование", ti -> ti.getType()!=null?ti.getType().getName():null)
                .column("Описание", TrafficItem::getDescription)
                .column("Начало периода", TrafficItem::getDateStart)
                .column("Конец периода", TrafficItem::getDateFinish);
        TableColumn<TrafficItem, String> personnelNumberColumn = new TableColumn<>("Таб. №");
        TableColumn<TrafficItem, String> lastNameColumn = new TableColumn<>("Фамилия");
        TableColumn<TrafficItem, String> firstNameColumn = new TableColumn<>("Имя");
        TableColumn<TrafficItem, String> middleNameColumn = new TableColumn<>("Отчество");
        TableColumn<TrafficItem, String> numberTeamColumn = new TableColumn<>("Номер экипажа");
        TableColumn<TrafficItem, String> workingModeTeamColumn = new TableColumn<>("Режим работы");
        TableColumn<TrafficItem, String> numberVehicleColumn = new TableColumn<>("Номер ТС");
        TableColumn<TrafficItem, String> modelVehicleColumn = new TableColumn<>("Модель ТС");

    }

    public void fillInTheTables() {

        ownerTable.getItems().clear();
        itemTable.getItems().clear();

        socketService.getTrafficItem().subscribe(itemTable.getItems()::add);
//        socketService.getTrafficTeam().subscribe(owner.teams::add);
//        socketService.getTrafficVehicle().subscribe(owner.vehicles::add);
    }

    @PostConstruct
    public void test() {
        socketService.authorize("sanya", "1101")
                .onErrorResume(s -> Mono.just(s.getMessage()))      //  LOG
                .doOnComplete(socketService::updatePeople)
                .subscribe(System.out::println);
    }
}
