package kup.get.service;

import javafx.concurrent.Task;
import kup.get.entity.FileOfProgram;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
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
        Path newPathJarFile = Paths.get("bin/bufferFile.jar");

        try {
            if (Files.exists(oldJarFile)) {
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(newPathJarFile.toFile()));
                ZipFile zipFile = new ZipFile(oldJarFile.toFile());
                uploadJarFile(zipFile, jarOutputStream);
                jarOutputStream.close();
                zipFile.close();
                return newPathJarFile;
            } else {
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(oldJarFile.toString()));
                uploadJarFile(jarOutputStream);
                jarOutputStream.close();
                return oldJarFile;
            }

        } catch (ZipException e) {
            e.printStackTrace();
            try {
                Files.deleteIfExists(oldJarFile);
                Files.deleteIfExists(newPathJarFile);
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
            if (fileOfProgram.getSize() != 0) {
                JarEntry entry = new JarEntry(fileOfProgram.getName());
                entry.setTime(fileOfProgram.getTime());
                entry.setSize(fileOfProgram.getSize());
                entry.setMethod(fileOfProgram.getMethod());
                entry.setCompressedSize(fileOfProgram.getCompressedSize());
                if (fileOfProgram.getMethod()==ZipEntry.STORED) {
                    CRC32 crc32 = new CRC32();
                    downloadFileOfProgram(fileOfProgram, (bytes, unused) -> crc32.update(bytes));
                    entry.setCrc(crc32.getValue());
                }
                jarOutputStream.putNextEntry(entry);
                downloadFileOfProgram(fileOfProgram, (bytes, capacity) -> {
                    try {
                        jarOutputStream.write(bytes);
                        this.updateInformation(capacity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                JarEntry entry = new JarEntry(fileOfProgram.getName().endsWith("/") ? fileOfProgram.getName() : fileOfProgram.getName() + "/");
                entry.setTime(fileOfProgram.getTime());
                jarOutputStream.putNextEntry(entry);
            }

            jarOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private synchronized void downloadFileOfProgram(FileOfProgram fileOfProgram, BiConsumer<byte[], Integer> consumer){
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
                    consumer.accept(bytes, dataBuffer.capacity());
                });
        while (!checkReleaseDataBuffer.get()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void updateInformation(long size) {
        progress.accumulateAndGet(size, Long::sum);
        this.updateMessage(String.format("Загрузка:\n%.2f / %.2f мб", progress.get() / 1024.0 / 1024, sizeFiles.get() / 1024.0 / 1024));
        this.updateProgress(progress.get(), sizeFiles.get());
    }

}
