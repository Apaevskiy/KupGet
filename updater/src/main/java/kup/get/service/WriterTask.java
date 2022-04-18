package kup.get.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static kup.get.service.UpdateTask.createFileWithDirectory;

public class WriterTask extends Task<Void> {

    private final AtomicReference<Double> progress;
    private final AtomicInteger count;
    private final SocketService socketService;
    private final ChannelTask channelTask;
    private boolean checkStop;

    public WriterTask(AtomicReference<Double> progress, AtomicInteger count, SocketService socketService, ChannelTask channelTask) {
        this.progress = progress;
        this.count = count;

        this.socketService = socketService;
        this.channelTask = channelTask;
    }

    @Override
    protected Void call() throws Exception {
        while (!checkStop && states.size()!=0){
            wait();
        }
        return null;
    }
    synchronized void writeChannel(){

        try {
            channelTask.putChannel();

            ChannelTask.Channel channel = channelTask.getChannel();
            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(
                            createFileWithDirectory(channel.getFilePath()),
                            StandardOpenOption.WRITE);

            DataBufferUtils.write(socketService.downloadFileOfProgram(channel.getFileOfProgram()), asynchronousFileChannel)
                    .doOnComplete(() -> Platform.runLater(() -> {

                    }))
                    .subscribe(dataBuffer -> {
                        progress.set(progress.get() + dataBuffer.capacity());
                        updateInformation(progress, sizeFiles);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
