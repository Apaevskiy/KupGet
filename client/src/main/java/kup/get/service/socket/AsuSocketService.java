package kup.get.service.socket;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AsuSocketService {
    private final SocketService socketService;

    public Flux<HttpStatus> uploadFile(String version, String comment, Flux<DataBuffer> dataBufferFlux) {
        /*return bufferFlux.map(db -> new BufferedReader(
                new InputStreamReader(db.asInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n")));*/
        return socketService.route("asu.update")
                .metadata(metadataSpec ->
                        metadataSpec
                                .metadata(version, MimeType.valueOf("message/version.information"))
                                .metadata(comment, MimeType.valueOf("message/version.comment")))
                .data( dataBufferFlux)
                .retrieveFlux(HttpStatus.class);
    }
}
