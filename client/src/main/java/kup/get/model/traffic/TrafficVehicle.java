package kup.get.model.traffic;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class TrafficVehicle {
    private Long id;

    private int number;
    private String model;

    private List<TrafficItem> items;
    private TrafficTeam team;

    public void setVehicle(TrafficVehicle vehicle) {
        this.id = vehicle.getId();
        this.number = vehicle.getNumber();
        this.model = vehicle.getModel();
        this.items = vehicle.getItems();
        this.team = vehicle.getTeam();
    }
}
