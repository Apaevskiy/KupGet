package kup.get.controller.socket;

import io.rsocket.metadata.WellKnownMimeType;
import javafx.collections.ObservableList;
import kup.get.model.traffic.*;
import kup.get.model.alfa.Person;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SocketService {
    private UsernamePasswordMetadata metadata;
    private final MimeType mimetype = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private final RSocketRequester requester;
    private final List<Person> people = new ArrayList<>();

    public SocketService(RSocketRequester requester) {
        this.requester = requester;
    }

    public Flux<String> authorize(String username, String password) {
        metadata = new UsernamePasswordMetadata(username, password);
        return requester
                .route("greetings")
                .metadata(this.metadata, this.mimetype)
                .data(Mono.empty())
                .retrieveFlux(String.class);
    }

    private RSocketRequester.RequestSpec route(String s) {
        return requester.route(s).metadata(this.metadata, this.mimetype);
    }

    public Flux<TrafficItem> getBriefing(LocalDate date) {
        return route("traffic.briefing")
                .data(date)
                .retrieveFlux(TrafficItem.class);
    }

    public void updatePeople() {
        route("traffic.drivers")
                .retrieveFlux(Person.class)
                .subscribe(people::add);
    }

    public List<Person> getPeople() {
        return people;
    }

    public Flux<TrafficItemType> getItemsType() {
        return route("traffic.trafficItemType")
                .retrieveFlux(TrafficItemType.class);
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return route("traffic.saveItemType")
                .data(type)
                .retrieveMono(TrafficItemType.class);
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return route("traffic.saveTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(TrafficTeam.class);
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return route("traffic.saveTrafficVehicle")
                .data(trafficVehicle)
                .retrieveMono(TrafficVehicle.class);
    }

    public Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle) {
        return route("traffic.deleteTrafficVehicle")
                .data(vehicle)
                .retrieveMono(Void.class);
    }

    public Flux<TrafficVehicle> getTrafficVehicle() {
        return route("traffic.getTrafficVehicle")
                .retrieveFlux(TrafficVehicle.class);
    }

    public Flux<TrafficTeam> getTrafficTeam() {
        return route("traffic.getTrafficTeam")
                .retrieveFlux(TrafficTeam.class);
    }

    public Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam) {
        return route("traffic.deleteTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(Void.class);
    }

    public Mono<TrafficPerson> saveTrafficPerson(TrafficPerson person) {
        return route("traffic.saveTrafficPerson")
                .data(person)
                .retrieveMono(TrafficPerson.class);
    }

    public Flux<TrafficPerson> getTrafficPeople() {
        return route("traffic.getTrafficPeople")
                .retrieveFlux(TrafficPerson.class);
    }

    public Flux<TrafficItem> getTrafficItem() {
        return route("traffic.getTrafficItems")
                .retrieveFlux(TrafficItem.class);
    }

    public Flux<TrafficPerson> getPeopleByTeam(TrafficTeam team) {
        return route("traffic.getPeopleByTeam")
                .data(team.getId())
                .retrieveFlux(TrafficPerson.class);
    }
}
