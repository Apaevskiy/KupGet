package kup.get.service.update;

import kup.get.entity.postgres.update.FileOfProgram;
import kup.get.entity.postgres.update.Version;
import kup.get.repository.postgres.update.VersionRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

@Slf4j
@Service
public class VersionService {
    private final VersionRepository repository;
    private final ZipService zipService;
    private final File file;

    public VersionService(VersionRepository repository, ZipService zipService) {
        this.repository = repository;
        this.zipService = zipService;
        file = new File("UpdateFile.jar");
    }

    public Version save(Version version) {
        return repository.save(version);
    }

    public List<Version> getInformationAboutUpdate(Long versionId) {
        return repository.findAllByIdAfter(versionId);
    }

    public Long getActualVersionId() {
        return repository.findFirstByOrderByIdDesc().getId();
    }

    public List<FileOfProgram> getFilesOfProgram() {
        List<FileOfProgram> list = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream("update.json");
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List <FileOfProgram>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Synchronized
    public Flux<HttpStatus> uploadFile(Flux<DataBuffer> bufferFlux) throws IOException {
        if (file.exists())
            file.delete();
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return DataBufferUtils.write(bufferFlux, channel).map(db -> HttpStatus.CHECKPOINT).doOnComplete(() -> zipService.unzip(file.getAbsolutePath(), "program"));
    }

    /*public Flux<DataBuffer> getContentOfFile(String fileName) {
        try {
            ZipFile zipFile = new ZipFile(file);
            InputStreamResource inputStreamResource = new InputStreamResource(zipFile.getInputStream(zipFile.getEntry(fileName)));
            return DataBufferUtils.read(inputStreamResource, new DefaultDataBufferFactory(), 4096);
        } catch (IOException e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }*/

    public Flux<DataBuffer> getContentOfFile(String fileName) {
        Resource resource;
        try {
            resource = new UrlResource("file:" + "program" + File.separator + fileName);
            return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }
}
