package kup.get.config;

import io.rsocket.DuplexConnection;
import io.rsocket.transport.netty.client.TcpClientTransport;
import javafx.animation.SequentialTransition;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class RSocketClientBuilderImpl {
    private RSocketRequester requester;
    private final RSocketRequester.Builder builder;

    public RSocketClientBuilderImpl(RSocketRequester.Builder builder) {
        this.builder = builder;
    }

    public Mono<DuplexConnection> createRequester() {
        TcpClientTransport transport = TcpClientTransport.create("localhost", 9091);
        return transport.connect().doOnSuccess(dc -> requester = builder.transport(transport));
    }

    public RSocketRequester getRequester() {
        return requester;
    }
}
