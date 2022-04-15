package kup.get.controller.socket;

import kup.get.config.RSocketClientBuilderImpl;
import kup.get.entity.FileOfProgram;
import kup.get.entity.Version;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SocketService {
    private final RSocketClientBuilderImpl config;

    public SocketService(RSocketClientBuilderImpl config) {
        this.config = config;
    }

    public Mono<Version> getActualVersion() {
        return config.getRequester()
                .route("update.getActualVersion")
                .retrieveMono(Version.class);
    }

    /*public List<FileOfProgram> getUpdateFiles(Version version) {
        return config.getRequester()
                .route("update.getUpdates")
                .data(version)
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }

    public List<FileOfProgram> getSavedFiles() {
        return config.getRequester()
                .route("update.filesSaved")
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }

    public List<Version> getUpdateInformation(Version version) {
        return config.getRequester()
                .route("update.informationAboutUpdate")
                .data(version)
                .retrieveFlux(Version.class)
                .collect(Collectors.toList())
                .block();
    }*/


    public Flux<FileOfProgram> getFilesOfProgram() {
        return config.getRequester()
                .route("update.getFilesOfProgram")
                .retrieveFlux(FileOfProgram.class);
    }
}
