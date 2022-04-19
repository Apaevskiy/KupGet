package kup.get.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import kup.get.entity.FileOfProgram;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@Slf4j
public class UpdateTask extends Task<Path> {

    private final SocketService socketService;

    private final ArrayDeque<FileOfProgram> deque = new ArrayDeque<>();
    private boolean checkStop = false;
    private final AtomicReference<Long> progress = new AtomicReference<>(0L);
    private final AtomicReference<Long> sizeFiles = new AtomicReference<>(0L);

    public UpdateTask(SocketService socketService) {
        this.socketService = socketService;
    }

    @Override
    protected Path call() {
        this.updateMessage("Получение списка обновлений...");
        Path oldJarFile = Paths.get("bin/client.jar");
        Path newPathJarFile = Paths.get("bin/client1.jar");
        try {
            if (oldJarFile.getParent() != null) {
                Files.createDirectories(oldJarFile.getParent());
            }
            JarInputStream inputStream = new JarInputStream(new FileInputStream("update/4.jar"));
            Manifest manifest = inputStream.getManifest();
            System.out.println("manifest.getMainAttributes");
            for (Object o : manifest.getMainAttributes().keySet()){
                System.out.println("key: " + o + " value: " + manifest.getMainAttributes().get(o));
            }
            System.out.println("manifest.getEntries");
            for (String o : manifest.getEntries().keySet()){
                System.out.println("key: " + o + " value: " + manifest.getEntries().get(o));
            }

            if (Files.exists(oldJarFile)) {
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(newPathJarFile.toFile()));
                ZipFile zipFile = new ZipFile(oldJarFile.toFile());
                uploadJarFile(zipFile, jarOutputStream);
                jarOutputStream.close();
                zipFile.close();
                return newPathJarFile;
//                Files.delete(oldJarFile);
//                newPathJarFile.toFile().renameTo(oldJarFile.toFile());
            } else {
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(oldJarFile.toString()));
                uploadJarFile(jarOutputStream);
                jarOutputStream.close();
                return oldJarFile;
            }

        }catch (ZipException e){
            e.printStackTrace();
            try {
                Files.deleteIfExists(oldJarFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void uploadJarFile(JarOutputStream jarOutputStream) {
        while (!checkStop || deque.size() != 0) {
            FileOfProgram fileOfProgram = getFileOfProgram();
            if (fileOfProgram != null) {
                downloadOtherFiles(jarOutputStream, fileOfProgram);
            }
        }
    }

    synchronized void uploadJarFile(ZipFile zipFile, JarOutputStream jarOutputStream) {
        while (!checkStop || deque.size() != 0) {
            try {
                FileOfProgram fileOfProgram = getFileOfProgram();
                if (fileOfProgram != null) {
                    ZipEntry entry = zipFile.getEntry(fileOfProgram.getName().replaceAll("\\\\", "/"));
                    if (entry != null) {
                        jarOutputStream.putNextEntry(entry);
                        if (fileOfProgram.getSize() > 0) {
                            IOUtils.copy(zipFile.getInputStream(entry), jarOutputStream);
                            this.updateInformation(fileOfProgram.getSize());
                        }
                        jarOutputStream.closeEntry();
                    } else {
                        downloadOtherFiles(jarOutputStream, fileOfProgram);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized FileOfProgram getFileOfProgram() {
        while (deque.size() == 0) {
            try {
                wait();
                if (checkStop && deque.size() == 0) return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return deque.poll();
    }

    public synchronized void addFileOfProgram(FileOfProgram file) {
        deque.add(file);
        sizeFiles.accumulateAndGet(file.getSize(), Long::sum);
        notify();
    }

    public synchronized void stopTask() {
        checkStop = true;
        notifyAll();
    }
    synchronized void thisNotify() {
        notifyAll();
    }
    synchronized void downloadOtherFiles(JarOutputStream jarOutputStream, FileOfProgram fileOfProgram) {

        try {

            JarEntry entry = new JarEntry(fileOfProgram.getName());
            entry.setTime(fileOfProgram.getTime());
            entry.setSize(fileOfProgram.getSize());
            jarOutputStream.putNextEntry(entry);
            AtomicBoolean checkReleaseDataBuffer = new AtomicBoolean(false);
                socketService.downloadFileOfProgram(fileOfProgram)
                        .doOnComplete(() -> {
                            checkReleaseDataBuffer.set(true);
                            this.thisNotify();
                        })
                        .subscribe(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                jarOutputStream.write(bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            this.updateInformation(dataBuffer.capacity());
                        });
            while (!checkReleaseDataBuffer.get()){
                wait();
            }

            jarOutputStream.closeEntry();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }


    void updateInformation(long size) {
        progress.accumulateAndGet(size, Long::sum);
        this.updateMessage(String.format("Загрузка:\n%.2f / %.2f мб", progress.get() / 1024.0 / 1024, sizeFiles.get() / 1024.0 / 1024));
        this.updateProgress(progress.get(), sizeFiles.get());
    }

}
