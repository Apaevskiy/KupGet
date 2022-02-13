package kup.get.controller.socket;

import kup.get.config.RSocketClientConfig;
import kup.get.model.FileOfProgram;
import kup.get.model.Version;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SocketController {
    private final RSocketClientConfig config;

    public SocketController(RSocketClientConfig config) {
        this.config = config;
    }

    public Mono<Version> getActualVersion() {
        return config.getRequester()
                .route("getActualVersion")
                .retrieveMono(Version.class);
    }

    public List<FileOfProgram> getUpdateFiles(Version version) {
        return config.getRequester()
                .route("getUpdates")
                .data(version)
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }

    public List<FileOfProgram> getSavedFiles() {
        return config.getRequester()
                .route("filesSaved")
                .retrieveFlux(FileOfProgram.class)
                .collect(Collectors.toList())
                .block();
    }

    public List<Version> getUpdateInformation(Version version) {
        return config.getRequester()
                .route("informationAboutUpdate")
                .data(version)
                .retrieveFlux(Version.class)
                .collect(Collectors.toList())
                .block();
    }
}
