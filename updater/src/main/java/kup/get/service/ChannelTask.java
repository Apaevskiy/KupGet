package kup.get.service;

import javafx.concurrent.Task;
import kup.get.entity.FileOfProgram;
import lombok.Data;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChannelTask extends Task<Void> {
    @Override
    protected Void call() {

        return null;
    }

    private final List<Channel> states = new ArrayList<>();


    public Channel getChannel() {
        Optional<Channel> optional = states.stream().filter(channel -> channel.fileChannel == null).findFirst();
        while (!optional.isPresent() && states.size() < 5) {
            try {
                wait();
                optional = states.stream().filter(channel -> channel.fileChannel == null).findFirst();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return optional.get();
    }

    public void putChannel(Path pathFile, FileOfProgram file) {
        states.add(new Channel(pathFile, file));
    }

    public void closeChannel(Channel channel) {
        states.remove(channel);
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
