package kup.get.controller;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.service.update.VersionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@AllArgsConstructor
@Slf4j
public class UpdateController {
    private final VersionService service;

    @GetMapping("/info")
    Mono<Long> getInfo(){
        try {
            return Mono.just(Files.size(Paths.get("program.exe")));
        } catch (IOException e) {
            log.error(e.getMessage());
            return Mono.empty();
        }
    }

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
    @GetMapping("/update/getProgram")
    public Flux<DataBuffer> getProgram() {
        Resource resource;
        try {
            resource = new UrlResource("file:program.exe");
            return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            return Flux.empty();
        }
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
