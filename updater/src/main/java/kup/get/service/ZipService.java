package kup.get.service;

import kup.get.entity.FileOfProgram;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
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
                } else list.add(new FileOfProgram(entry));
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public void writeZip(List<FileOfProgram> filesOfProgram, File file) {
        System.out.println("writeZip \t" + file.getAbsolutePath());
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file))) {
            for (FileOfProgram fileOfProgram : filesOfProgram) {
                ZipEntry entry = new ZipEntry(fileOfProgram.getName());
                    entry.setSize(fileOfProgram.getSize());
                    entry.setTime(fileOfProgram.getTime());
                zout.putNextEntry(entry);
                if (fileOfProgram.getSize()!=0)
                    IOUtils.copy(new ByteArrayInputStream(fileOfProgram.getContent()), zout);

                zout.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
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

    public void read(File file, Path tempDirectory) {
        try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file)))
        {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                FileOutputStream fout = new FileOutputStream(tempDirectory + entry.getName());
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    fout.write(c);
                }
                fout.flush();
                zipInputStream.closeEntry();
                fout.close();
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
