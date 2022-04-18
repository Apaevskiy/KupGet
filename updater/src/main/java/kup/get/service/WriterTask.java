package kup.get.service;

import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import kup.get.entity.FileOfProgram;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.function.BiConsumer;

import static kup.get.service.UpdateTask.createFileWithDirectory;

public class WriterTask extends Task<Void> {

    private final SocketService socketService;
    private final BiConsumer<Integer, Void> biConsumer;

    private final ArrayDeque<FileOfProgram> waitingFiles = new ArrayDeque<>();
    private final ArrayDeque<FileOfProgram> activeFiles = new ArrayDeque<>();
    private boolean checkStop = false;

    public WriterTask(SocketService socketService, BiConsumer<Integer, Void> biConsumer) {
        this.socketService = socketService;
        this.biConsumer = biConsumer;
    }

    @Override
    protected Void call() {
        startTask();
        System.out.println("stop ChannelTask");
        return null;
    }

    synchronized void startTask() {
        while (!checkStop || waitingFiles.size() != 0) {
            /*try {
                FileOfProgram file = getChannel();
                if (file != null) {
                    AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(
                            createFileWithDirectory(Paths.get(tempDirectory + File.separator + file.getName())),
                            StandardOpenOption.WRITE);
                    DataBufferUtils.write(socketService.downloadFileOfProgram(file), asynchronousFileChannel)
                            .doOnComplete(() -> closeChannel(file))
                            .subscribe(dataBuffer -> biConsumer.accept(dataBuffer.capacity(), null));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        while (activeFiles.size() != 0) {
            try {
                System.out.println("waitingChannels size: " + activeFiles.size());
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stopTask() {
        checkStop = true;
        notifyAll();
    }

    public synchronized FileOfProgram getChannel() {
        while (activeFiles.size() > 3 || waitingFiles.size() == 0) {
            try {
                wait();
                if (checkStop && waitingFiles.size() == 0) return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        FileOfProgram channel = waitingFiles.poll();
        activeFiles.add(channel);
        return channel;
    }

    public synchronized void putChannel(FileOfProgram file) {
//        System.out.println("put channel " + file.getName());
        waitingFiles.add(file);
        notifyAll();
    }

    public synchronized void closeChannel(FileOfProgram file) {
//        System.out.println("close channel " + channel.fileOfProgram.getName());
        activeFiles.remove(file);
        notifyAll();
    }

    /*public void setTempDirectory(Path tempDirectory) {
        this.tempDirectory = tempDirectory;
    }*/
}
