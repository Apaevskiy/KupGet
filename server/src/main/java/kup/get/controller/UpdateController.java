package kup.get.controller;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.service.update.VersionService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;

@Controller
@AllArgsConstructor
public class UpdateController {
    private final VersionService service;

    @MessageMapping("update.getActualVersion")
    public Mono<Version> getActualVersion() {
        return Mono.just(service.getActualVersion());
    }

    @MessageMapping("update.informationAboutUpdate")
    public Flux<Version> getInformationAboutUpdate(Mono<Version> message) {
        return message.flatMapMany(version -> Flux.fromIterable(service.getInformationAboutUpdate(version)));
    }

    @MessageMapping("update.getFilesOfProgram")
    public Flux<FileOfProgram> getFilesOfProgram() {
        return Flux.fromIterable(service.getFilesOfProgram());
    }

    @MessageMapping("update.getContentOfFiles")
    public Flux<DataBuffer> getContentOfFile(Mono<FileOfProgram> mono) {
        return mono.flatMapMany(service::getContentOfFile);
    }
    @MessageMapping("asu.update")
    Flux<HttpStatus> update(Flux<DataBuffer> content) throws IOException {
        return Flux.concat(service.uploadFile(content), Mono.just(HttpStatus.OK))
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn(HttpStatus.BAD_GATEWAY);
    }
}
