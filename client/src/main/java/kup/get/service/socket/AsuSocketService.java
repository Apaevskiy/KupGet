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

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AsuSocketService {
    private final SocketService socketService;

    public Flux<HttpStatus> uploadFile(String version, String comment, Resource resource) {
        Flux<DataBuffer> readFlux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
        Map<String, Object> map = new HashMap<>();
        map.put("version", version);
        map.put("comment", comment);
        return socketService.route("asu.update", map)
                .data(readFlux)
                .retrieveFlux(HttpStatus.class);
    }
}
