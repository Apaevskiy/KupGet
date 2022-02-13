package kup.get.controller;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.service.update.VersionService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
public class UpdateController {
    private final VersionService service;

    @MessageMapping("getActualVersion")
    public Mono<Version> getActualVersion() {
        return Mono.just(service.getActualVersion());
    }

    @MessageMapping("getUpdates")
    public Flux<FileOfProgram> getUpdate(Mono<Version> message) {
        return message.flatMapMany(version ->
                Flux.fromIterable(service.getNewFiles(version)));
    }

    @MessageMapping("filesSaved")
    public Flux<FileOfProgram> getSavedFiles() {
        return Flux.fromIterable(service.whichFilesShouldBeSaved());
    }

    @MessageMapping("informationAboutUpdate")
    public Flux<Version> getInformationAboutUpdate(Mono<Version> message) {
        return message.flatMapMany(version ->
            Flux.fromIterable(service.getInformationAboutUpdate(version))
        );
    }
}
