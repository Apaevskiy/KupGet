package kup.get.config;

import kup.get.service.PropertyService;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.UnknownHostException;

@Configuration
public class RSocketClientConfig {
    private final PropertyService propertyService;

    public RSocketClientConfig(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
    @Bean
    RSocketRequester requester(RSocketRequester.Builder builder){
        return builder.tcp(propertyService.getIpServer(), propertyService.getPortServer());
    }
}
