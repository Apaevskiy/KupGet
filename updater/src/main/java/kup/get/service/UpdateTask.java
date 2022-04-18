package kup.get.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import kup.get.entity.FileOfProgram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class UpdateTask extends Task<File> {
    private final SocketService socketService;
    private final ZipService zipService;
    private final List<FileOfProgram> files;

    public UpdateTask(List<FileOfProgram> files, SocketService socketService, ZipService zipService) {
        this.socketService = socketService;
        this.zipService = zipService;
        this.files = files;
    }

    @Override
    protected File call() {
        try {
            this.updateMessage("Получение списка обновлений...");

            File programFile = createFileWithDirectory(Paths.get("bin/client.jar")).toFile();
            List<FileOfProgram> oldFiles = zipService.readFile(programFile);

            Path tempDirectory;
            Path directory = Paths.get("Temp");
            if (!Files.exists(directory)) Files.createDirectory(directory);
            tempDirectory = Files.createTempDirectory(directory, "Temp directory ");
            tempDirectory.toFile().deleteOnExit();

            double sizeFiles = files.stream().mapToDouble(FileOfProgram::getSize).sum();
            final AtomicReference<Double> progress = new AtomicReference<>(0.0);
            AtomicInteger iterationDownloadFiles = new AtomicInteger(0);

            for (FileOfProgram fileOfProgram : files) {
                Optional<FileOfProgram> optional = oldFiles.stream().filter(file -> file.equals(fileOfProgram)).findFirst();
                if (optional.isPresent()) {
                    writeFileToTempDirectory(tempDirectory, optional.get());
                    progress.set(progress.get() + fileOfProgram.getSize());
                    updateInformation(progress, sizeFiles);
                    //System.out.printf("w i: %d, s: %d, name: %s\n", iterationDownloadFiles.get(), files.size(), fileOfProgram.getName());
                    if (iterationDownloadFiles.incrementAndGet() == files.size())
                        writeFilesToJar(tempDirectory, programFile);
                } else {

                    AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                            createFileWithDirectory(Paths.get(tempDirectory.toAbsolutePath() + File.separator + fileOfProgram.getName())),
                            StandardOpenOption.WRITE);
                    DataBufferUtils.write(socketService.downloadFileOfProgram(fileOfProgram), channel)
                            .doOnComplete(() -> Platform.runLater(() -> {
                                //System.out.printf("d i: %d, s: %d, name: %s\n", iterationDownloadFiles.get(), files.size(), fileOfProgram.getName());
                                if (iterationDownloadFiles.incrementAndGet() == files.size())
                                    writeFilesToJar(tempDirectory, programFile);
                            }))
                            .subscribe(dataBuffer -> {
                                progress.set(progress.get() + dataBuffer.capacity());
                                updateInformation(progress, sizeFiles);
                            });
                }
            }
            if (iterationDownloadFiles.get()!=files.size()){
                log.info("start wait");
                this.wait();
                log.info("end wait");
            }

            /*if (threadQueryWriter.isAlive()) {
                try {
                    threadQueryWriter.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/

            return programFile;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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

    void writeFileToTempDirectory(Path tempDirectory, FileOfProgram fileOfProgram) {
        Path file = Paths.get(tempDirectory + File.separator + fileOfProgram.getName());
        try {
            createFileWithDirectory(file);
            if (fileOfProgram.getContent() != null)
                Files.write(file, fileOfProgram.getContent());
            Files.setLastModifiedTime(file, FileTime.fromMillis(fileOfProgram.getTime()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void writeFilesToJar(Path tempDirectory, File programFile) {
        this.updateMessage("Установка обновления...");
        List<FileOfProgram> list = new ArrayList<>();
        listFilesForFolder(tempDirectory.toFile(), list);
        System.out.println(tempDirectory);
        list.forEach(file -> {
            //System.out.println("name "+file.getName());
            file.setName(file.getName().replace(tempDirectory.toString(), ""));
        });
        try {
            Files.deleteIfExists(programFile.toPath());
            Files.createFile(programFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        zipService.writeZip(list, programFile);
        this.updateMessage("Обновления установены!");
        this.notify();
    }

    void listFilesForFolder(File folder, List<FileOfProgram> list) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, list);
            } else {
                FileOfProgram fileOfProgram = new FileOfProgram();
                fileOfProgram.setName(fileEntry.getPath());
                fileOfProgram.setSize(fileEntry.length());
                fileOfProgram.setTime(fileEntry.lastModified());
                try {
                    fileOfProgram.setContent(Files.readAllBytes(fileEntry.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(fileOfProgram);
            }
        }
    }
}
