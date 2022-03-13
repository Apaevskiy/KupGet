package kup.get.config;

import io.rsocket.DuplexConnection;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import javafx.animation.SequentialTransition;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class RSocketClientBuilderImpl {
    private RSocketRequester requester;
    private final RSocketRequester.Builder builder;

    public RSocketClientBuilderImpl(RSocketRequester.Builder builder) {
        this.builder = builder;
    }

    public Mono<DuplexConnection> createRequester() {
        TcpClientTransport transport = TcpClientTransport.create("localhost", 9091);
        return transport.connect().doOnSuccess(dc -> {
            requester = builder
                    .setupMetadata(new UsernamePasswordMetadata("test", "test"),
                            MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                    .transport(transport);
        });
    }

    public RSocketRequester getRequester() {
        return requester;
    }
}
