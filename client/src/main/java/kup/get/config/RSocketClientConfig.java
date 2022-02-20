package kup.get.config;

import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import kup.get.model.alfa.Person;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.List;

@Configuration
public class RSocketClientConfig {
    @Bean
    RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return strategies -> strategies.encoder(new SimpleAuthenticationEncoder());
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .setupMetadata(new UsernamePasswordMetadata("test", "test"),
                        MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .tcp("localhost", 9091);
    }
    /*public String createRequester() {
        TcpClientTransport transport = TcpClientTransport.create("localhost", 9091);
        return transport.connect()
                .flatMap(dc -> Mono.just(""))
                .doOnSuccess(dc -> requester = builder.transport(transport))
                .onErrorReturn(ConnectException.class, "ConnectException")
                .onErrorReturn(Exception.class, "Exception")
                .block();
    }*/

}
