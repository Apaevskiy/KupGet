package kup.get.service;

import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import kup.get.entity.FileOfProgram;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.*;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class UpdateTask extends Task<File> {
    private final ZipService zipService;
    private final List<FileOfProgram> files;
    private final double sizeFiles;
    private final AtomicReference<Double> progress = new AtomicReference<>(0.0);
    private final SocketService socketService;
    private final ChannelTask channelTask;

    public UpdateTask(List<FileOfProgram> files, SocketService socketService, ZipService zipService) {
        this.zipService = zipService;
        this.socketService = socketService;
        this.files = files;
        this.sizeFiles = files.stream().mapToDouble(FileOfProgram::getSize).sum();
        this.channelTask = new ChannelTask(socketService, (value, unused) -> {
            progress.set(progress.get() + value);
            this.updateInformation(progress, sizeFiles);
        });
    }

    @Override
    protected File call() {
        this.updateMessage("Получение списка обновлений...");
//        File oldFile = createFileWithDirectory(Paths.get()).toFile();
//        File programFile = createFileWithDirectory(Paths.get()).toFile();

        try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(createFileWithDirectory(Paths.get("bin/client.jar")).toFile()));
             JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(createFileWithDirectory(Paths.get("bin/client1.jar")).toFile()), jarInputStream.getManifest())) {
//            jarOutputStream.setLevel(Deflater.BEST_COMPRESSION);

            System.out.println("start " + jarInputStream.getManifest());
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                FileOfProgram fileOfProgram = new FileOfProgram(entry);
                if (files.contains(fileOfProgram)) {

                    jarOutputStream.putNextEntry(entry);
                    if (fileOfProgram.getSize() != 0) {
                        for (int c = jarInputStream.read(); c != -1; c = jarInputStream.read()) {
                            jarOutputStream.write(c);
                        }
                    }
                    progress.set(progress.get() + fileOfProgram.getSize());
                    this.updateInformation(progress, sizeFiles);
                    jarOutputStream.closeEntry();
                    files.remove(fileOfProgram);
                }
                jarInputStream.closeEntry();
            }
            downloadOtherFiles(jarOutputStream);

//                                    socketService.downloadFileOfProgram(fileOfProgram)
            jarInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    synchronized void downloadOtherFiles(JarOutputStream zout) {
        for (FileOfProgram fileOfProgram : files) {
            try {

                JarEntry entry = new JarEntry(fileOfProgram.getName());
                entry.setTime(fileOfProgram.getTime());
//                entry.setMethod(JarEntry.STORED);
//                entry.setCompressedSize(fileOfProgram.getSize());
                entry.setSize(fileOfProgram.getSize());
                zout.putNextEntry(entry);


                CRC32 crc = new CRC32();
                ArrayDeque<DataBuffer> deque = new ArrayDeque<>();
                socketService.downloadFileOfProgram(fileOfProgram)
                        .doOnComplete(this::closeEntry)
                        .subscribe(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                zout.write(bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            progress.set(progress.get() + dataBuffer.capacity());
                            this.updateInformation(progress, sizeFiles);
                            /*deque.add(dataBuffer);
                            crc.update(dataBuffer.asByteBuffer());*/
                        });
                wait();

                /*entry.setCrc(crc.getValue());
                zout.putNextEntry(entry);

                while (!deque.isEmpty()) {
                    DataBuffer dataBuffer = deque.poll();
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    zout.write(bytes);
                    progress.set(progress.get() + dataBuffer.capacity());
                    this.updateInformation(progress, sizeFiles);
                }
*/

//                System.out.println("start");
//                Thread.sleep(5000);
                zout.closeEntry();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    synchronized void closeEntry() {
        this.notify();
    }

    void updateInformation(AtomicReference<Double> progress, double size) {
        this.updateMessage(String.format("Загрузка:\n%.2f / %.2f мб", progress.get() / 1024 / 1024, size / 1024 / 1024));
        this.updateProgress(progress.get(), size);
    }

    public static Path createFileWithDirectory(Path file) {
        try {
            if (!file.toFile().exists()) {
                if (file.getParent() != null) {
                    Files.createDirectories(file.getParent());
                }
                Files.createFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
