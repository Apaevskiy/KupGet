package kup.get.config;

import io.rsocket.DuplexConnection;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import kup.get.controller.MainController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RSocketClientBuilderImpl {
    private RSocketRequester requester;
    private final RSocketRequester.Builder builder;
    private final RSocketStrategies rsocketStrategies;
    private final TcpClientTransport transport = TcpClientTransport.create("localhost", 9091);

    public RSocketClientBuilderImpl(RSocketRequester.Builder builder, RSocketStrategies rsocketStrategies) {
        this.builder = builder;
        this.rsocketStrategies = rsocketStrategies;
    }

    public Mono<DuplexConnection> createClientTransport() {
        return transport.connect();
    }
    public void createRequester(String username, String password){
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
        UsernamePasswordMetadata metadata = new UsernamePasswordMetadata(username, password);
        this.requester = builder
                .setupRoute("shell-client")
                .setupData(System.getenv().get("COMPUTERNAME"))
                .setupMetadata(metadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .rsocketStrategies(builder ->
                        builder.encoder(new SimpleAuthenticationEncoder()))
                .rsocketConnector(connector -> connector.acceptor(responder))
                .transport(transport);
    }
    public RSocketRequester getRequester() {
        return requester;
    }

    @Slf4j
    static class ClientHandler {
        @MessageMapping("client-status")
        public Flux<String> statusUpdate(String status) {
            log.info("Connection {}", status);
            return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
        }
    }
}
