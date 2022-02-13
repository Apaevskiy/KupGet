package kup.get.config;

import io.rsocket.transport.netty.client.TcpClientTransport;
import kup.get.service.PropertyService;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.ConnectException;

@Component
public class RSocketClientConfig {
    private final PropertyService propertyService;
    private RSocketRequester requester;
    private final RSocketRequester.Builder builder;

    public RSocketClientConfig(PropertyService propertyService, RSocketRequester.Builder builder) {
        this.propertyService = propertyService;
        this.builder = builder;
    }

    public String createRequester() {
        System.out.println("ip: "+propertyService.getIpServer() + " port: " + propertyService.getPortServer());
        TcpClientTransport transport = TcpClientTransport.create(propertyService.getIpServer(), propertyService.getPortServer());
        return transport.connect()
                .flatMap(dc -> Mono.just(""))
                .doOnSuccess(dc -> requester = builder.transport(transport))
                .onErrorReturn(ConnectException.class,"ConnectException")
                .onErrorReturn(Exception.class,"Exception")
                .block();
    }

    public RSocketRequester getRequester() {
        return requester;
    }
}
