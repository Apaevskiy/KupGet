package kup.get.service;

import kup.get.config.RSocketClientBuilderImpl;
import kup.get.service.DAO.PersonDaoService;
import kup.get.service.DAO.TrafficDaoService;
import kup.get.service.socket.PersonSocketService;
import kup.get.service.socket.SocketService;
import kup.get.service.socket.TrafficSocketService;
import org.springframework.stereotype.Service;

@Service
public class Services extends SocketService {
    private final RSocketClientBuilderImpl config;
    private final TrafficDaoService trafficDaoService;
    private final TrafficSocketService trafficSocketService;
    private final PersonDaoService personDaoService;
    private final PersonSocketService personSocketService;

    public Services(RSocketClientBuilderImpl config, TrafficDaoService trafficDaoService, TrafficSocketService trafficSocketService, PersonDaoService personDaoService, PersonSocketService personSocketService) {
        super(config);
        this.config = config;
        this.trafficDaoService = trafficDaoService;
        this.trafficSocketService = trafficSocketService;
        this.personDaoService = personDaoService;
        this.personSocketService = personSocketService;
    }

    public MyTrafficService getTrafficService() {
        if(config.getRequester()==null)
            return trafficDaoService;
        return trafficSocketService;
    }

    public PersonService getPersonService() {
        if(config.getRequester()==null)
            return personDaoService;
        return personSocketService;
    }
}
