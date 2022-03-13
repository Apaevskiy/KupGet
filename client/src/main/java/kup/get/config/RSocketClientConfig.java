package kup.get.config;

import io.rsocket.DuplexConnection;
import io.rsocket.transport.netty.client.TcpClientTransport;
import javafx.animation.SequentialTransition;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class RSocketClientConfig {
    @Bean
    RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return strategies -> strategies.encoder(new SimpleAuthenticationEncoder());
    }
    @Bean
    AtomicReference<SequentialTransition> getTransition(){
        return new AtomicReference<>(new SequentialTransition());
    }

}
