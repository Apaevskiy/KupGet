package kup.get.service;

import io.rsocket.DuplexConnection;
import kup.get.config.RSocketClientBuilderImpl;
import kup.get.service.DAO.PersonDaoService;
import kup.get.service.DAO.TrafficDaoService;
import kup.get.service.socket.PersonSocketService;
import kup.get.service.socket.SocketService;
import kup.get.service.socket.TrafficSocketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class Services {
    private final RSocketClientBuilderImpl config;
    private final TrafficDaoService trafficDaoService;
    private final TrafficSocketService trafficSocketService;
    private final PersonDaoService personDaoService;
    private final PersonSocketService personSocketService;

    public MyTrafficService getTrafficService() {
        if(config.getRequester()==null){
            return trafficDaoService;
        }
        return trafficSocketService;
    }

    public PersonService getPersonService() {
        if(config.getRequester()==null)
            return personDaoService;
        return personSocketService;
    }

    public Mono<DuplexConnection> createRequester() {
        return config.createRequester();
    }
}
