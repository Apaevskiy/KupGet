package kup.get.service.update;

import kup.get.config.ZipConfig;
import kup.get.entity.postgres.security.User;
import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.repository.postgres.update.VersionRepository;
import lombok.AllArgsConstructor;
import org.aspectj.util.FileUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VersionService {
    private final VersionRepository repository;
    private final ZipConfig zipConfig;

    public Version save(String name, String info) {
        return repository.save(new Version(name, info));
    }

    public List<FileOfProgram> getNewFiles(Version version) {
        System.out.println("\n\tversion:\t"+version);
        System.out.println("\n\tlistNewFiles: ");
        for(FileOfProgram file: zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList())){
            System.out.println(file.getName() + "\t" +file.getComment() + "\tcontent: " + (file.getContent()!=null));
        }
        return zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList());
    }
    public List<FileOfProgram> whichFilesShouldBeSaved(){
        System.out.println("whichFilesShouldBeSaved");
        return zipConfig.getZipEntry().stream().map(f -> new FileOfProgram(f.getName(), f.getSize(), f.getTime())).collect(Collectors.toList());
    }

    public List<Version> getInformationAboutUpdate(Version version) {
        System.out.println("getInformationAboutUpdate\t"+version.getId());
        return repository.findAllByIdAfter(version.getId());
    }

    public Version getActualVersion() {
        System.out.println("getActualVersion");
        return repository.findFirstByOrderByIdDesc();
    }

    public Flux<HttpStatus> uploadFile(Flux<DataBuffer> bufferFlux) throws IOException {
        File file = new File("");
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return DataBufferUtils.write(bufferFlux, channel)
                .map(db -> HttpStatus.OK);
    }
}
