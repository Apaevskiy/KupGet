package kup.get.service.socket;

import kup.get.entity.Version;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AsuSocketService {
    private final SocketService socketService;

    public Flux<HttpStatus> uploadFile( Flux<DataBuffer> dataBufferFlux) {
        return socketService.getClient().post().uri("/asu/uploadFile")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(dataBufferFlux, DataBuffer.class)
                .retrieve()
                .bodyToFlux(HttpStatus.class);
    }
    public Mono<Version> addVersion(String release, String information){
        Version version = new Version();
        version.setRelease(release);
        version.setInformation(information);
        return socketService.getClient().post().uri("/asu/version")
                .bodyValue(version)
                .retrieve()
                .bodyToMono(Version.class);
    }
}
