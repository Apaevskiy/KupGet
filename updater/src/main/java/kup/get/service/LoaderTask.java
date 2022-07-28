package kup.get.service;

import kup.get.entity.FileOfProgram;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/*
*
*       Сохранение загруженных файлов в временный каталог для последующей записи в jar-файл.
*       Временно отложил это, т.к. актуально только с файлами большого объёма.
*
* */

@Deprecated
public class LoaderTask extends Thread {

    private final SocketService socketService;
    private final UpdateTask updateTask;

    private final ArrayDeque<FileOfProgram> deque = new ArrayDeque<>();
    private final ArrayDeque<FileOfProgram> loadedFilesDeque = new ArrayDeque<>();
    private boolean checkStop = false;
    private Path tempDir;

    public LoaderTask(SocketService socketService, UpdateTask updateTask) {
        this.socketService = socketService;
        this.updateTask = updateTask;

        try {
            Path temp = Paths.get("Temp");
            if (!Files.exists(temp))
                Files.createDirectory(temp);
            tempDir = Files.createTempDirectory(temp, "update ");
            tempDir.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!checkStop || deque.size() != 0 || loadedFilesDeque.size() != 0) {
            downloadFile(getFileOfProgram());
        }
//        updateTask.stopDownload();
    }

    public synchronized FileOfProgram getFileOfProgram() {
        while (deque.size() == 0 || loadedFilesDeque.size() >= 1) {
            try {
                wait();
                if (checkStop && deque.size() == 0 && loadedFilesDeque.size() == 0) return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return deque.poll();
    }

    public synchronized void addFileOfProgram(FileOfProgram file) {
        deque.add(file);
        notify();
    }

    public synchronized void stopTask() {
        System.out.println("stopThreadDownload");
        checkStop = true;
        notify();
    }

    private synchronized void downloadFile(FileOfProgram fileOfProgram) {
        if (fileOfProgram == null) return;
//        System.out.println("download ("+channelsDeque.size()+"): " + fileOfProgram.getName());

        try {
            Path file = Paths.get(tempDir.toAbsolutePath() + File.separator + fileOfProgram.getName());
            if (file.getParent() != null && !Files.exists(file.getParent())) {
                file.getParent().toFile().mkdirs();
            }
            if (!Files.exists(file))
                Files.createFile(file);
            /*AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    file, StandardOpenOption.WRITE, StandardOpenOption.CREATE);*/
            loadedFilesDeque.add(fileOfProgram);
            /*DataBufferUtils.write(socketService.downloadFileOfProgram(fileOfProgram), channel)
                    .doOnComplete(() -> {
//                        System.out.println("success download: " + fileOfProgram.getName());
                        fileOfProgram.setDownloaded(true);
                        updateTask.addDownloadedFileOfProgram(fileOfProgram);
                        loadedFilesDeque.remove(fileOfProgram);
                    })
                    .doFinally(signalType -> myNotify())
                    .subscribe();*/
            socketService.downloadFileOfProgram(fileOfProgram)
                    .doOnComplete(() -> {
                        loadedFilesDeque.remove(fileOfProgram);
                        fileOfProgram.setDownloaded(true);
//                        updateTask.addDownloadedFileOfProgram(fileOfProgram);
                    })
                    .doFinally(signalType -> myNotify())
                    .subscribe(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        try {
//                            IOUtils.copy(new ByteArrayInputStream(bytes), outputStream);
                            Files.write(file, bytes, StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void myNotify() {
        this.notify();
    }

    public Path getTempDirectory() {
        return tempDir;
    }
}
