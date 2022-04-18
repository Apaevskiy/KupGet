package kup.get.service;

import javafx.concurrent.Task;
import kup.get.entity.FileOfProgram;
import lombok.Data;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kup.get.service.UpdateTask.createFileWithDirectory;

public class ChannelTask extends Task<Void> {
    private final List<Channel> states = new ArrayList<>();
    private boolean checkStop=false;
    @Override
    protected Void call() {
        while (!checkStop && states.size()!=0){

        }
        return null;
    }

    public synchronized void stopTask(){
        checkStop=true;
    }
    public synchronized Channel getChannel() {
        List<Channel> channels = states.stream().filter(channel -> channel.fileChannel == null).collect(Collectors.toList());
        while (channels.size()>5 || channels.size()==0) {
            try {
                wait();
                channels = states.stream().filter(channel -> channel.fileChannel == null).collect(Collectors.toList());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Channel channel = channels.get(0);
            channel.setFileChannel(AsynchronousFileChannel.open(
                    createFileWithDirectory(channel.filePath),
                    StandardOpenOption.WRITE));
            System.out.println("start channel " + channel.fileOfProgram.getName());
            return channel;
        } catch (IOException e) {
            e.printStackTrace();
            return getChannel();
        }
    }

    public synchronized void putChannel(Path pathFile, FileOfProgram file) {
        System.out.println("put channel " + file.getName());
        states.add(new Channel(pathFile, file));
        notify();
    }

    public synchronized void closeChannel(Channel channel) {
        System.out.println("close channel " + channel.fileOfProgram.getName());
        states.remove(channel);
        notify();
    }

    @Data
    public static class Channel {
        private Path filePath;
        private FileOfProgram fileOfProgram;
        private AsynchronousFileChannel fileChannel;

        public Channel(Path fileChannel, FileOfProgram fileOfProgram) {
            this.filePath = fileChannel;
            this.fileOfProgram = fileOfProgram;
        }
    }
}
