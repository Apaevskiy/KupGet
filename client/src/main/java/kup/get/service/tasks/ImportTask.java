package kup.get.service.tasks;

import javafx.concurrent.Task;
import kup.get.entity.alfa.Person;
import kup.get.entity.traffic.TrafficItemType;
import kup.get.entity.traffic.TrafficTeam;
import kup.get.entity.traffic.TrafficVehicle;
import kup.get.service.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportTask extends Task<String> {
    private final Services services;

    private final List<Person> people;
    private final List<TrafficTeam> teams;
    private final List<TrafficVehicle> vehicles;
    private final List<TrafficItemType> types;

    public ImportTask(Services services, List<Person> people, List<TrafficTeam> teams, List<TrafficVehicle> vehicles, List<TrafficItemType> types) {
        this.services = services;
        this.people = people;
        this.teams = teams;
        this.vehicles = vehicles;
        this.types = types;
    }

    @Override
    protected String call() throws Exception {
        int count = people.size() + teams.size() + vehicles.size() + types.size();
        AtomicInteger progress = new AtomicInteger(0);
        services.getPersonService().savePeople(people)
                .subscribe(p -> update(progress.getAndIncrement(), count));
        services.getTrafficService().saveTrafficTeams(teams)
                .subscribe(p -> update(progress.getAndIncrement(), count));
        services.getTrafficService().saveTrafficVehicles(vehicles)
                .subscribe(p -> update(progress.getAndIncrement(), count));
        services.getTrafficService().saveItemTypes(types)
                .subscribe(p -> update(progress.getAndIncrement(), count));

        return null;
    }

    private synchronized void update(int i, int count) {
        this.updateProgress(i, count);
    }

}
