package kup.get.controller.socket;

import io.rsocket.metadata.WellKnownMimeType;
import kup.get.model.Item;
import kup.get.model.Person;
import kup.get.model.TrafficItemType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SocketService {
    private UsernamePasswordMetadata metadata;
    private final MimeType mimetype = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private final RSocketRequester requester;

    public SocketService(RSocketRequester requester) {
        this.requester = requester;
    }
    public void authorize(String username, String password){
        metadata = new UsernamePasswordMetadata(username, password);
        requester
                .route("greetings")
                .metadata(this.metadata,this.mimetype)
                .data(Mono.empty())
                .retrieveFlux(String.class)
                .subscribe(gr -> System.out.println("secured response: " + gr));
    }
    public RSocketRequester.RequestSpec route(String s) {
        return requester.route(s).metadata(this.metadata,this.mimetype);
    }

    public Flux<Item> getBriefing(LocalDate date) {
        return route("briefing")
                .data(date)
                .retrieveFlux(Item.class);
    }

    public Flux<Person> getPeople() {
        return route("drivers")
                .retrieveFlux(Person.class);
    }

    public Flux<TrafficItemType> getItemsType() {
        return route("traffic.trafficItemType")
                .retrieveFlux(TrafficItemType.class);
    }

    public Flux<Boolean> saveItemsTypes(List<TrafficItemType> list) {
        return route("saveItemsTypes")
                .data(Flux.fromIterable(list))
                .retrieveFlux(Boolean.class);
    }
}
