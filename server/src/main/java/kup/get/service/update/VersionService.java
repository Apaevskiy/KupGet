package kup.get.service.update;

import kup.get.config.ZipConfig;
import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.repository.postgres.update.VersionRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class VersionService {
    private final VersionRepository repository;
    private final ZipConfig zipConfig;
    private final ZipService zipService;

    public Version save(String name, String info) {
        return repository.save(new Version(name, info));
    }

    public List<FileOfProgram> getNewFiles(Version version) {
        System.out.println("\n\tversion:\t" + version);
        System.out.println("\n\tlistNewFiles: ");
        for (FileOfProgram file : zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList())) {
            System.out.println(file.getName() + "\t" + file.getComment() + "\tcontent: " + (file.getContent() != null));
        }
        return zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList());
    }

    public List<FileOfProgram> whichFilesShouldBeSaved() {
        System.out.println("whichFilesShouldBeSaved");
        return zipConfig.getZipEntry().stream().map(f -> new FileOfProgram(f.getName(), f.getSize(), f.getTime())).collect(Collectors.toList());
    }

    public List<Version> getInformationAboutUpdate(Version version) {
        System.out.println("getInformationAboutUpdate\t" + version.getId());
        return repository.findAllByIdAfter(version.getId());
    }

    public Version getActualVersion() {
        System.out.println("getActualVersion");
        return repository.findFirstByOrderByIdDesc();
    }


    public List<FileOfProgram> getFilesOfProgram() {
        File folder = new File("program");
        try {
            if (!folder.exists()) {
                Files.createDirectory(folder.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<FileOfProgram> list = new ArrayList<>();
        zipService.listFilesForFolder(folder, list);
        list.forEach(fileOfProgram -> fileOfProgram.setName(fileOfProgram.getName().replaceAll("^" + folder.getName() + "\\\\", "")));
        return list;
    }


    @Synchronized
    public Flux<HttpStatus> uploadFile(Flux<DataBuffer> bufferFlux, String inf, String comment) throws IOException {
        File file = new File("bufferUpdateFile.jar");
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return DataBufferUtils.write(bufferFlux, channel)
                .map(db -> HttpStatus.CHECKPOINT)
                .doOnComplete(() -> {
                    zipService.update(save(inf, comment), file, zipConfig.getZipEntry());
                    log.info("check delete buffer file " + file.delete());
                });
    }

    public Flux<DataBuffer> getContentOfFile(FileOfProgram fileOfProgram) {
        Resource resource;
        try {
            resource = new UrlResource("file:" + "program" + File.separator + fileOfProgram.getName());
            return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }
}
