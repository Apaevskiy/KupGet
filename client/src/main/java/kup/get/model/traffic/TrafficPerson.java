package kup.get.model.traffic;

import kup.get.model.alfa.Person;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TrafficPerson {
    private Long id;
    private Long personnelNumber;
    private TrafficTeam team;
    private List<TrafficItem> items;

    public TrafficPerson(Person person) {
        personnelNumber = person.getId();
    }

    public void setPerson(TrafficPerson person){
        this.id=person.getId();
        this.personnelNumber=person.getPersonnelNumber();
//        this.team=person.getTeam();
        this.items=person.getItems();
    }
}
