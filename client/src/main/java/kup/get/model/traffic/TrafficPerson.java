package kup.get.model.traffic;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TrafficPerson {
    private Long id;
    private Long personnelNumber;
//    private TrafficTeam team;
    private List<TrafficItem> items;

    public void setPerson(TrafficPerson person){
        this.id=person.getId();
        this.personnelNumber=person.getPersonnelNumber();
//        this.team=person.getTeam();
        this.items=person.getItems();
    }
}
