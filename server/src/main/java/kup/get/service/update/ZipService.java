package kup.get.service.update;

import kup.get.entity.postgres.update.FileOfProgram;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;
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
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            List<FileOfProgram> list = new ArrayList<>();
            ZipFile zipFile = new ZipFile(pathToFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            File fileOfEntries = new File("update.json");
            if(fileOfEntries.exists()) {
                fileOfEntries.delete();
            }
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(fileOfEntries));
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                list.add(new FileOfProgram(entry));
                Path file = Paths.get(pathToDirectory + File.separator + entry.getName());
                if (Files.exists(file)) {
                    if ((file.toFile().length() != entry.getSize() ||
                            file.toFile().lastModified() != entry.getLastModifiedTime().toMillis())&& !entry.isDirectory()) {
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
            fos.writeObject(list);
            fos.close();
            Files.deleteIfExists(Paths.get(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Deprecated
    public List<FileOfProgram> parseJarFileToListFileOfPrograms(File file) {
        List<FileOfProgram> list = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(file);
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
