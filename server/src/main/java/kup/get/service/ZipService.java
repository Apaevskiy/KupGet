package kup.get.service;

import kup.get.entity.energy.FileOfProgram;
import kup.get.entity.energy.Version;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {
    private List<FileOfProgram> read(ZipInputStream zipInputStream) {
        List<FileOfProgram> list = new ArrayList<>();
        ZipEntry entry;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                        outStream.write(c);
                    }
                    list.add(new FileOfProgram(entry, outStream.toByteArray()));
                    zipInputStream.closeEntry();
                } else
                    list.add(new FileOfProgram(entry));
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public void writeZip(List<FileOfProgram> filesOfProgram, File file) {
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file))) {
            for (FileOfProgram fileOfProgram : filesOfProgram) {
                outputStream.putNextEntry(fileOfProgram.getFile());
                if (!fileOfProgram.getFile().isDirectory())
                    IOUtils.copy(new ByteArrayInputStream(fileOfProgram.getContent()), outputStream);
                outputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<FileOfProgram> readInputStream(InputStream stream) {
        return read(new ZipInputStream(stream));
    }

    public List<FileOfProgram> readFile(File file) {
        try {
            return read(new ZipInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(Version version, MultipartFile file, List<FileOfProgram> listOldFiles) {
        try {
            List<FileOfProgram> listNewFiles = readInputStream(file.getInputStream());
            List<FileOfProgram> listSavedFiles = new ArrayList<>(listOldFiles);

            listSavedFiles.retainAll(listNewFiles); // список файлов, которые не изменились
            listNewFiles.removeAll(listOldFiles);   //  список новых файлов
            listNewFiles.forEach(program -> program.setComment(version.getId()));

            listOldFiles.clear();
            listOldFiles.addAll(listNewFiles);
            listOldFiles.addAll(listSavedFiles);

            File programFile = new File("program.jar");
            Files.delete(programFile.toPath());

            writeZip(listOldFiles, new File(programFile.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
