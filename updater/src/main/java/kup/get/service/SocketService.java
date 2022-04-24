package kup.get.service;

import kup.get.config.RSocketClientBuilderImpl;
import kup.get.entity.FileOfProgram;
import kup.get.entity.Version;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Component
@AllArgsConstructor
public class SocketService {
    private final RSocketClientBuilderImpl config;

    public Mono<Version> getActualVersion() {
        return config.getRequester()
                .route("update.getActualVersion")
                .retrieveMono(Version.class);
    }

    public Flux<Version> getUpdateInformation(Version version) {
        return config.getRequester()
                .route("update.informationAboutUpdate")
                .data(version)
                .retrieveFlux(Version.class);
    }


    public Flux<FileOfProgram> getFilesOfProgram() {
        return config.getRequester()
                .route("update.getFilesOfProgram")
                .retrieveFlux(FileOfProgram.class);
    }

    public Flux<DataBuffer> downloadFileOfProgram(FileOfProgram fileOfProgram) {
        return config.getRequester()
                .route("update.getContentOfFiles")
                .data(fileOfProgram)
                .retrieveFlux(DataBuffer.class);
    }
}
