package kup.get.service;

import kup.get.entity.Version;
import kup.get.service.DAO.PersonDaoService;
import kup.get.service.DAO.TrafficDaoService;
import kup.get.service.other.PropertyService;
import kup.get.service.socket.PersonSocketService;
import kup.get.service.socket.SocketService;
import kup.get.service.socket.TrafficSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class Services {
    private final TrafficDaoService trafficDaoService;
    private final TrafficSocketService trafficSocketService;
    private final PersonDaoService personDaoService;
    private final PersonSocketService personSocketService;
    private final SocketService socketService;

    private final PropertyService propertyService;

    public MyTrafficService getTrafficService() {
        if (socketService.getClient() == null) {
            return trafficDaoService;
        }
        return trafficSocketService;
    }

    public PersonService getPersonService() {
        if (socketService.getClient() == null)
            return personDaoService;
        return personSocketService;
    }

    public Mono<Long> connectToServer(){
        return socketService.createWebClient("setup","1488325",propertyService.getServerAddress())
                .get().uri("/update/getActualVersion").retrieve().bodyToMono(Long.class);
    }
    public Flux<String> getAuthorities(String username, String password) {
        return socketService.createWebClient(username, password, propertyService.getServerAddress())
                .get().uri("/auth/getAuthorities")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public void disconnect() {
        socketService.disconnect();
    }
}
