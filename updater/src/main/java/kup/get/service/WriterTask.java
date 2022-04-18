package kup.get.service;

import javafx.concurrent.Task;
import kup.get.controller.socket.SocketService;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class WriterTask extends Task<Void> {

    private final AtomicInteger count;
    private final SocketService socketService;
    private final ChannelTask channelTask;
    private boolean checkStop;
    private final BiConsumer<Integer, Void> biConsumer;

    public WriterTask(AtomicInteger count, SocketService socketService, ChannelTask channelTask, BiConsumer<Integer, Void> biConsumer) {
        this.count = count;
        this.socketService = socketService;
        this.channelTask = channelTask;
        this.biConsumer = biConsumer;
    }

    @Override
    protected Void call() throws Exception {
        while (!checkStop){
            writeChannel();
        }
        return null;
    }

    public synchronized void stopTask(){
        checkStop=true;
    }

    synchronized void writeChannel(){
        ChannelTask.Channel channel = channelTask.getChannel();
        DataBufferUtils.write(socketService.downloadFileOfProgram(channel.getFileOfProgram()), channel.getFileChannel())
                .doOnComplete(() -> {
                    channelTask.closeChannel(channel);
                    count.incrementAndGet();
                })
                .subscribe(dataBuffer -> {
                    biConsumer.accept(dataBuffer.capacity(), null);
                });
    }
}
