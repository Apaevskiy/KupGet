package kup.get.controller.FX.traffic;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import kup.get.config.FxmlLoader;
import kup.get.config.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.controller.socket.SocketService;
import kup.get.model.alfa.Person;
import kup.get.model.traffic.TrafficItem;
import kup.get.model.traffic.TrafficPerson;
import kup.get.model.traffic.TrafficTeam;
import kup.get.model.traffic.TrafficVehicle;
import lombok.Data;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final List<Person> people = new ArrayList<>();

    public ItemController(SocketService socketService) {
        this.socketService = socketService;
        ownerComboBox.getItems().addAll("Все","ТС","Экипажи","Водители");
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
                .subscribe(System.out::println);
        socketService.getPeople().subscribe(people::add);
    }
}
