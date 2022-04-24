package kup.get.service.update;

import kup.get.entity.postgres.update.FileOfProgram;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ZipService {
    public void unzip(String pathToFile, String pathToDirectory) {
        try {
            Path path = Paths.get(pathToDirectory);
            if (!Files.exists(path))
                Files.createDirectory(path);

            ZipFile zipFile = new ZipFile(pathToFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path file = Paths.get(pathToDirectory + File.separator + entry.getName());
                if (Files.exists(file)) {
                    if (file.toFile().length() != entry.getSize() ||
                            file.toFile().lastModified() != entry.getLastModifiedTime().toMillis()) {
                        Files.delete(file);
                    } else continue;
                }
                if (!entry.isDirectory()) {
                    if (file.getParent() != null && !Files.exists(file.getParent())) {
                        Files.createDirectory(file.getParent());
                    }
                    Files.copy(zipFile.getInputStream(entry), file);
                } else {
                    Files.createDirectory(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<FileOfProgram> parseJarFileToListFileOfPrograms(Path pathToFile) {
        List<FileOfProgram> list = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(pathToFile.toString());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                list.add(new FileOfProgram(entries.nextElement()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
