package kup.get.model.traffic;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TrafficTeam {
    private Long id;
    private String number;
    private String workingMode;

    private TrafficVehicle vehicle;
    private Set<TrafficPerson> trafficPeople = new HashSet<>();
    private Set<TrafficItem> trafficItems = new HashSet<>();

    public void setTeam(TrafficTeam tt){
        this.id = tt.getId();
       this.number = tt.getNumber();
       this.workingMode = tt.getWorkingMode();
       this.trafficPeople = tt.getTrafficPeople();
       this.vehicle = tt.getVehicle();
       this.trafficItems = tt.getTrafficItems();
    }
}
