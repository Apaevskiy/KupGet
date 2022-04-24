package kup.get.service.update;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.repository.postgres.update.VersionRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class VersionService {
    private final VersionRepository repository;
    private final ZipService zipService;

    public Version save(String name, String info) {
        return repository.save(new Version(name, info));
    }

    public List<Version> getInformationAboutUpdate(Version version) {
        return repository.findAllByIdAfter(version.getId());
    }

    public Version getActualVersion() {
        return repository.findFirstByOrderByIdDesc();
    }

    public List<FileOfProgram> getFilesOfProgram() {
        Path path = Paths.get("UpdateFile.jar");
        List<FileOfProgram> list = new ArrayList<>();
        if (Files.exists(path)) {
            list = zipService.parseJarFileToListFileOfPrograms(path);
        }
        return list;
    }


    @Synchronized
    public Flux<HttpStatus> uploadFile(Flux<DataBuffer> bufferFlux) throws IOException {
        File file = new File("UpdateFile.jar");
        if (file.exists())
            file.delete();
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return DataBufferUtils.write(bufferFlux, channel).map(db -> HttpStatus.CHECKPOINT).doOnComplete(() -> zipService.unzip(file.getAbsolutePath(), "program"));
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
