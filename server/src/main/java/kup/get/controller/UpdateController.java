package kup.get.controller;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.service.update.VersionService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class UpdateController {
    private final VersionService service;

    @GetMapping("/update/getActualVersion")
    public Mono<Long> getActualVersion() {
        return Mono.just(service.getActualVersionId());
    }

    @GetMapping("/update/informationAboutUpdate/{id}")
    public Flux<Version> getInformationAboutUpdate(@PathVariable Long id) {
        return Flux.fromIterable(service.getInformationAboutUpdate(id));
    }

    @GetMapping("/update/getFilesOfProgram")
    public Flux<FileOfProgram> getFilesOfProgram() {
        return Flux.fromIterable(service.getFilesOfProgram());
    }

    @PostMapping("/update/getContentOfFiles")
    public Flux<DataBuffer> getContentOfFile(@RequestBody Mono<String> mono) {
        return mono.flatMapMany(service::getContentOfFile);
    }
    @PostMapping("/asu/uploadFile")
    Flux<HttpStatus> update(@RequestBody Flux<DataBuffer> content) throws IOException {
        return Flux.concat(service.uploadFile(content), Mono.just(HttpStatus.OK))
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn(HttpStatus.BAD_GATEWAY);
    }
    @PostMapping("/asu/version")
    Mono<Version> addVersion(@RequestBody Mono<Version> versionMono) {
        return versionMono.map(service::save);
    }
}
