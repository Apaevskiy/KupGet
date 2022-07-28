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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@Slf4j
public class UpdateTask extends Task<Path> {

    private final ArrayDeque<FileOfProgram> deque = new ArrayDeque<>();

    private boolean checkStop = false;
    private final AtomicInteger countFiles = new AtomicInteger(0);
    private final AtomicInteger leftFiles = new AtomicInteger(0);
    private final AtomicReference<Long> progress = new AtomicReference<>(0L);
    private final AtomicReference<Long> sizeFiles = new AtomicReference<>(0L);

    private final SocketService socketService;

    public UpdateTask(SocketService socketService) {
        this.socketService = socketService;
    }

    @Override
    protected Path call() {
        this.updateMessage("Получение списка обновлений...");
        Path oldJarFile = Paths.get("client.jar");
        Path newJarFile = Paths.get("client1.jar");
        try {
            if (oldJarFile.getParent() != null) {
                Files.createDirectories(oldJarFile.getParent());
            }

            if (Files.exists(oldJarFile)) { // Если это не первая загрузка, то идёт проверка на старые файлы, чтобы не качать их же с сервера
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(newJarFile.toFile()));
                ZipFile zipFile = new ZipFile(oldJarFile.toFile());
                uploadJarFile(zipFile, jarOutputStream);
                jarOutputStream.close();
                zipFile.close();
                Files.delete(oldJarFile);
                Files.move(newJarFile, newJarFile.resolveSibling("client.jar"));
            } else {
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(oldJarFile.toString()));
                uploadJarFile(jarOutputStream);
                jarOutputStream.close();
            }
            return oldJarFile;

        } catch (ZipException e) {
            log.error(e.getMessage());
            try {
                Files.deleteIfExists(oldJarFile);
                Files.deleteIfExists(newJarFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private void uploadJarFile(JarOutputStream jarOutputStream) throws IOException {
        while (!checkStop || deque.size() != 0) {
            FileOfProgram fileOfProgram = getFileOfProgram();
            if (fileOfProgram == null)
                continue;
            if (fileOfProgram.getSize() == 0) {
                JarEntry entry = new JarEntry(fileOfProgram.getName());
                entry.setTime(fileOfProgram.getTime());
                jarOutputStream.putNextEntry(entry);
                updateInformation(0);
                continue;
            }
            putEntry(fileOfProgram, jarOutputStream);
        }
    }

    synchronized void uploadJarFile(ZipFile zipFile, JarOutputStream jarOutputStream) {
        while (!checkStop || deque.size() != 0) {
            try {
                FileOfProgram fileOfProgram = getFileOfProgram();
                if (fileOfProgram == null)
                    continue;

                if (fileOfProgram.getSize() == 0) {
                    JarEntry entry = new JarEntry(fileOfProgram.getName());
                    entry.setTime(fileOfProgram.getTime());
                    jarOutputStream.putNextEntry(entry);
                    continue;
                }
                ZipEntry entry = zipFile.getEntry(fileOfProgram.getName().replaceAll("\\\\", "/"));
                if (entry != null) {
                    jarOutputStream.putNextEntry(entry);
                    if (fileOfProgram.getSize() > 0) {
                        IOUtils.copy(zipFile.getInputStream(entry), jarOutputStream);
                        this.updateInformation(fileOfProgram.getSize());
                    }
                    jarOutputStream.closeEntry();
                } else {
                    putEntry(fileOfProgram, jarOutputStream);
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
                log.error(e.getMessage());
            }
        }
        return deque.poll();
    }

    public synchronized void addFileOfProgram(FileOfProgram file) {
        deque.add(file);
        countFiles.incrementAndGet();
        sizeFiles.accumulateAndGet(file.getSize(), Long::sum);
        notify();
    }

    public synchronized void stopTask() {
        checkStop = true;
        notify();
    }

    private void putEntry(FileOfProgram fileOfProgram, JarOutputStream jarOutputStream) throws IOException {
        JarEntry entry = new JarEntry(fileOfProgram.getName());
        entry.setTime(fileOfProgram.getTime());
        entry.setSize(fileOfProgram.getSize());
        entry.setCompressedSize(fileOfProgram.getCompressedSize());
        entry.setMethod(fileOfProgram.getMethod());
        List<byte[]> contentOfFile = socketService.downloadFileOfProgram(fileOfProgram).collectList().map(dataBuffers ->
                dataBuffers.stream().map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).collect(Collectors.toList())
        ).block();

        if (contentOfFile != null) {
            if (fileOfProgram.getMethod() == ZipEntry.STORED) {
                CRC32 crc32 = new CRC32();
                contentOfFile.forEach(crc32::update);
                entry.setCrc(crc32.getValue());
            }
            jarOutputStream.putNextEntry(entry);
            contentOfFile.forEach(bytes -> {
                try {
                    jarOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else log.error("empty content of " + fileOfProgram.getName());
        this.updateInformation(fileOfProgram.getSize());
    }

    private void updateInformation(long size) {
        progress.accumulateAndGet(size, Long::sum);
        this.updateMessage(String.format("%-40s Загрузка %40s",
                String.format("%.2f / %.2f мб", progress.get() / 1048576.0, sizeFiles.get() / 1048576.0),
                String.format("%d / %d файлов", leftFiles.incrementAndGet(), countFiles.get())));
        this.updateProgress(progress.get(), sizeFiles.get());
    }
}
