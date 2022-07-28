package kup.get.service;

import kup.get.entity.FileOfProgram;
import kup.get.entity.Version;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SocketService {
    private WebClient client;
    private final String url;

    public SocketService(PropertyService propertyService) {
    this.url=propertyService.getServerAddress();
    }

    public Mono<Boolean> checkConnection(){
        return client.get().uri("/info").retrieve().bodyToMono(Boolean.class);
    }
    public void createWebClient(String username, String password) {
        this.client = WebClient.builder()
                .baseUrl(url)
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();
    }

    public Mono<Long> getActualVersion() {
        return client.get().uri("/update/getActualVersion")
                .retrieve().bodyToMono(Long.class);
    }

    public Flux<Version> getUpdateInformation(Long versionId) {
        return client.get().uri("/update/informationAboutUpdate/{id}",versionId)
                .retrieve().bodyToFlux(Version.class);
    }

    public Flux<FileOfProgram> getFilesOfProgram() {
        return client.get().uri("/update/getFilesOfProgram")
                .retrieve().bodyToFlux(FileOfProgram.class);
    }

    public Flux<DataBuffer> downloadFileOfProgram(FileOfProgram fileOfProgram) {
        return client.post().uri("/update/getContentOfFiles")
                .bodyValue(fileOfProgram.getName())
                .retrieve().bodyToFlux(DataBuffer.class);
    }
}
